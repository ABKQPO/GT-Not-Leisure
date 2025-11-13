package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.*;
import static net.minecraftforge.common.util.ForgeDirection.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockSearedLadder;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.blocks.abstracts.MultiServantLogic;

public class BlockSearedLadder extends Block {

    public IIcon ladderIcon;

    public BlockSearedLadder() {
        super(Material.rock);
        setHardness(3F);
        setResistance(20F);
        setStepSound(soundTypeMetal);
        this.setBlockName("SearedLadder");
        this.setBlockTextureName("tinker:searedbricksquare");
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockSearedLadder.class, getUnlocalizedName());
        GTNLItemList.SearedLadder.set(new ItemStack(this, 1));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return side == meta ? ladderIcon : blockIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        super.registerBlockIcons(reg);
        this.ladderIcon = reg.registerIcon(RESOURCE_ROOT_ID + ":" + "Searedbricksquare");
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new MultiServantLogic();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.updateLadderBounds(world.getBlockMetadata(x, y, z));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    public void updateLadderBounds(int metadata) {
        float thickness = 0.6F;

        switch (metadata) {
            case 2: // North face
                this.setBlockBounds(0.0F, 0.0F, 1.0F - thickness, 1.0F, 1.0F, 1.0F);
                break;
            case 3: // South face
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, thickness);
                break;
            case 4: // West face
                this.setBlockBounds(1.0F - thickness, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 5: // East face
                this.setBlockBounds(0.0F, 0.0F, 0.0F, thickness, 1.0F, 1.0F);
                break;
            default:
                break;
        }
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
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemIn) {
        int direction = 0;
        if (player != null) {
            direction = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        }
        int metadata = switch (direction) {
            case 0 -> 2; // 南
            case 1 -> 5; // 西
            case 2 -> 3; // 北
            case 3 -> 4; // 东
            default -> 2;
        };

        world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        return true;
    }
}
