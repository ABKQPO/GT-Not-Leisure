package com.science.gtnl.common.block.blocks;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaTube;
import com.science.gtnl.loader.RenderLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.common.blocks.BlockTube;

public class BlockMultiEssentiaTube extends BlockTube {

    public static final int BUFFER_METADATA = 4;

    public BlockMultiEssentiaTube() {
        super();
    }

    @Override
    public String getUnlocalizedName() {
        return "gtnl.block.multi_essentia_tube";
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getSubBlocks(Item item, CreativeTabs creativeTab, List blocks) {
        blocks.add(new ItemStack(item, 1, BUFFER_METADATA));
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
        int metadata) {
        return BUFFER_METADATA;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        world.setBlockMetadataWithNotify(x, y, z, BUFFER_METADATA, 2);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityMultiEssentiaTube();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityMultiEssentiaTube();
    }

    @Override
    public int damageDropped(int metadata) {
        return BUFFER_METADATA;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return RenderLoader.MULTI_ESSENTIA_TUBE_RENDER_ID;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityMultiEssentiaTube tube)) return 0;

        float fillRatio = (float) tube.getTotalAmount() / TileEntityMultiEssentiaTube.MAX_CAPACITY;
        return MathHelper.floor_float(fillRatio * 14.0F) + (tube.getTotalAmount() > 0 ? 1 : 0);
    }
}
