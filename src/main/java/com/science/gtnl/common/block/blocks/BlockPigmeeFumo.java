// Pigmee Fumo port from AE2 Lightning Tech Reborn
// (https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn).
// LGPL-3.0, model by TedXenon. Adapted for GT-Not-Leisure, Forge 1.7.10.
package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.api.IBlockModelProvider;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.properties.OrientationBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;
import com.gtnewhorizon.gtnhlib.client.model.BakedModelQuadContext;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.geometry.Orientation;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockPigmeeFumo;
import com.science.gtnl.common.block.blocks.tile.TileEntityPigmeeFumo;
import com.science.gtnl.common.render.model.pigmeeModel.PigmeeFumoModel;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPigmeeFumo extends BlockContainer implements IBlockModelProvider {

    public static final double[][] BOUNDS = {
        // metadata 2 and 3, snout along Z
        { 4.0, 0, 1.0, 12.0, 10.0, 15.0 }, { 4.0, 0, 1.0, 12.0, 10.0, 15.0 },
        // metadata 4 and 5, snout along X
        { 1.0, 0, 4.0, 15.0, 10.0, 12.0 }, { 1.0, 0, 4.0, 15.0, 10.0, 12.0 } };

    @SideOnly(Side.CLIENT)
    public IIcon particleIcon;

    public static final OrientationBlockProperty FACING_PROPERTY = new OrientationBlockProperty() {

        @Override
        public boolean hasTrait(BlockPropertyTrait trait) {
            return trait == BlockPropertyTrait.SupportsWorld || trait == BlockPropertyTrait.SupportsStacks;
        }

        @Override
        public Orientation getValue(IBlockAccess world, int x, int y, int z) {
            return orientationOf(world.getBlockMetadata(x, y, z));
        }

        @Override
        public Orientation getValue(ItemStack stack) {
            return PigmeeFumoModel.DEFAULT_ORIENTATION;
        }
    };

    public BlockPigmeeFumo() {
        super(Material.cloth);
        setHardness(0.5F);
        setStepSound(Block.soundTypeCloth);
        setLightOpacity(0);
        setBlockName("gtnl.pigmee_fumo");
        setBlockTextureName(RESOURCE_ROOT_ID + ":blocks/pigmee_fumo");
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockPigmeeFumo.class, "pigmee_fumo");
        BlockPropertyRegistry.registerBlockItemProperty(this, FACING_PROPERTY);
        GameRegistry.registerTileEntity(TileEntityPigmeeFumo.class, "pigmee_fumo_tile_entity");
        GTNLItemList.PigmeeFumo.set(new ItemStack(this, 1));
    }

    @Override
    public String getUnlocalizedName() {
        return "gtnl.block.pigmee_fumo";
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        super.registerBlockIcons(register);
        particleIcon = register.registerIcon(RESOURCE_ROOT_ID + ":blocks/pigmee_fumo_particle");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return particleIcon != null ? particleIcon : blockIcon;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityPigmeeFumo();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BakedModel getModel(BakedModelQuadContext context) {
        Orientation orientation = context.getBlockState()
            .getPropertyValue(FACING_PROPERTY);
        return PigmeeFumoModel.INSTANCE.get(orientation);
    }

    public static Orientation orientationOf(int metadata) {
        return switch (metadata) {
            case 3 -> Orientation.SOUTH_UP;
            case 4 -> Orientation.WEST_UP;
            case 5 -> Orientation.EAST_UP;
            default -> Orientation.NORTH_UP;
        };
    }

    public static double[] boundsFor(int metadata) {
        int index = switch (metadata) {
            case 3 -> 1;
            case 4 -> 2;
            case 5 -> 3;
            default -> 0;
        };
        return BOUNDS[index].clone();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, placer, stack);
        int metadata = 2;
        if (placer != null) {
            int quad = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
            metadata = switch (quad) {
                case 0 -> 2;
                case 1 -> 5;
                case 2 -> 3;
                default -> 4;
            };
        }
        world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityPigmeeFumo fumo)) {
            return false;
        }
        if (!world.isRemote) {
            fumo.toggleSpinningServer();
        }
        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        double[] b = boundsFor(world.getBlockMetadata(x, y, z));
        setBlockBounds(
            (float) b[0] / 16.0F,
            (float) b[1] / 16.0F,
            (float) b[2] / 16.0F,
            (float) b[3] / 16.0F,
            (float) b[4] / 16.0F,
            (float) b[5] / 16.0F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        double[] b = boundsFor(world.getBlockMetadata(x, y, z));
        return AxisAlignedBB.getBoundingBox(
            x + b[0] / 16.0D,
            y + b[1] / 16.0D,
            z + b[2] / 16.0D,
            x + b[3] / 16.0D,
            y + b[4] / 16.0D,
            z + b[5] / 16.0D);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return getCollisionBoundingBoxFromPool(world, x, y, z);
    }
}
