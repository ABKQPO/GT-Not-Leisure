package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockBeamFormer;
import com.science.gtnl.common.block.blocks.tile.TileEntityBeamFormer;
import com.science.gtnl.common.render.tile.RenderBlockBeamFormer;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.block.AEBaseTileBlock;
import appeng.core.features.AEFeature;
import appeng.helpers.ICustomCollision;
import appeng.util.Platform;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBeamFormer extends AEBaseTileBlock implements ICustomCollision {

    @SideOnly(Side.CLIENT)
    public static IIcon iconBase;
    @SideOnly(Side.CLIENT)
    public static IIcon iconStatusOff;
    @SideOnly(Side.CLIENT)
    public static IIcon iconStatusOn;
    @SideOnly(Side.CLIENT)
    public static IIcon iconStatusBeaming;
    @SideOnly(Side.CLIENT)
    public static IIcon iconPrism;

    public static final double MIN = 5.0 / 16.0;
    public static final double MAX = 11.0 / 16.0;
    public static final double LEN = 5.0 / 16.0;

    public BlockBeamFormer() {
        super(Material.iron);
        setHardness(8.0F);
        setResistance(40.0F);
        setBlockName("BeamFormer");
        setHarvestLevel("pickaxe", 3);
        setBlockTextureName(RESOURCE_ROOT_ID + ":BeamFormer");
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        setLightOpacity(0);
        GameRegistry.registerBlock(this, ItemBlockBeamFormer.class, getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntityBeamFormer.class, "BeamFormerTileEntity");
        GTNLItemList.BlockBeamFormer.set(new ItemStack(this, 1));
        this.setTileEntity(TileEntityBeamFormer.class);
        this.setFeature(EnumSet.of(AEFeature.Core));
        this.isOpaque = false;
        this.isFullSize = false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBeamFormer getRenderer() {
        return new RenderBlockBeamFormer();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        iconBase = register.registerIcon(RESOURCE_ROOT_ID + ":part/beam_former_base");
        iconStatusOff = register.registerIcon(RESOURCE_ROOT_ID + ":part/beam_former_status_off");
        iconStatusOn = register.registerIcon(RESOURCE_ROOT_ID + ":part/beam_former_status_on");
        iconStatusBeaming = register.registerIcon(RESOURCE_ROOT_ID + ":part/beam_former_status_beaming");
        iconPrism = register.registerIcon(RESOURCE_ROOT_ID + ":part/beam_former_prism");
        super.registerBlockIcons(register);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        var te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityBeamFormer beamFormer) {
            return beamFormer.isActive() && beamFormer.getBeamLength() > 0 ? 15 : 0;
        }
        return 0;
    }

    @Override
    public boolean onActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY,
        float hitZ) {
        if (player.isSneaking()) {
            return false;
        }
        var heldItem = player.getHeldItem();
        if (Platform.isWrench(player, heldItem, x, y, z)) {
            if (world.getTileEntity(x, y, z) instanceof TileEntityBeamFormer te) {
                return te.onActivate(player, Vec3.createVectorHelper(x, y, z));
            }
        }
        return super.onActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, net.minecraft.block.Block block, int meta) {
        if (world.getTileEntity(x, y, z) instanceof TileEntityBeamFormer te) {
            te.unregisterListener();
            te.disconnect(null);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    public AxisAlignedBB getVoxelBox(ForgeDirection forward) {
        return switch (forward) {
            case DOWN -> AxisAlignedBB.getBoundingBox(MIN, 1.0 - LEN, MIN, MAX, 1.0, MAX);
            case UP -> AxisAlignedBB.getBoundingBox(MIN, 0.0, MIN, MAX, LEN, MAX);
            case NORTH -> AxisAlignedBB.getBoundingBox(MIN, MIN, 1.0 - LEN, MAX, MAX, 1.0);
            case SOUTH -> AxisAlignedBB.getBoundingBox(MIN, MIN, 0.0, MAX, MAX, LEN);
            case WEST -> AxisAlignedBB.getBoundingBox(1.0 - LEN, MIN, MIN, 1.0, MAX, MAX);
            case EAST -> AxisAlignedBB.getBoundingBox(0.0, MIN, MIN, LEN, MAX, MAX);
            default -> AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        };
    }

    @Override
    public Iterable<AxisAlignedBB> getSelectedBoundingBoxesFromPool(World w, int x, int y, int z, Entity e,
        boolean isVisual) {
        TileEntityBeamFormer tile = (TileEntityBeamFormer) w.getTileEntity(x, y, z);
        if (tile != null) {
            return Collections.singletonList(getVoxelBox(tile.getForward()));
        }
        return Collections.singletonList(AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1));
    }

    @Override
    public void addCollidingBlockToList(World w, int x, int y, int z, AxisAlignedBB bb, List<AxisAlignedBB> out,
        Entity e) {
        TileEntityBeamFormer tile = (TileEntityBeamFormer) w.getTileEntity(x, y, z);
        AxisAlignedBB box = (tile != null) ? getVoxelBox(tile.getForward())
            : AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);
        AxisAlignedBB offsetBox = box.getOffsetBoundingBox(x, y, z);
        if (offsetBox.intersectsWith(bb)) {
            out.add(offsetBox);
        }
    }
}
