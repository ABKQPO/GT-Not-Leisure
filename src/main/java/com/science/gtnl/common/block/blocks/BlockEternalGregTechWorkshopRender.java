package com.science.gtnl.common.block.blocks;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.tile.TileEntityEternalGregTechWorkshop;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEternalGregTechWorkshopRender extends Block {

    public BlockEternalGregTechWorkshopRender() {
        super(Material.iron);
        this.setResistance(20f);
        this.setHardness(-1.0f);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        this.setBlockName("EternalGregTechWorkshopRenderer");
        this.setLightLevel(100.0f);
        GameRegistry.registerBlock(this, ItemBlockEternalGregTechWorkshopRender.class, getUnlocalizedName());
        GTNLItemList.EternalGregTechWorkshopRender.set(new ItemStack(this, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + "air");
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean canRenderInPass(int a) {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityEternalGregTechWorkshop();
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {}

    @Override
    public void dropBlockAsItem(World world, int x, int y, int z, ItemStack itemStack) {}

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess worldIn, int x, int y, int z) {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }
}
