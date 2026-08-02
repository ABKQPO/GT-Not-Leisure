package com.science.gtnl.common.block.blocks;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.block.blocks.item.ItemBlockMultiEssentiaTube;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaTube;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.common.blocks.BlockTube;

public class BlockMultiEssentiaTube extends BlockTube {

    private static final int BUFFER_METADATA = 4;

    public BlockMultiEssentiaTube() {
        super();
        setBlockName("MultiEssentiaTube");
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        GameRegistry.registerBlock(this, ItemBlockMultiEssentiaTube.class, getUnlocalizedName());
        GameRegistry.registerTileEntity(TileEntityMultiEssentiaTube.class, "MultiEssentiaTubeTileEntity");
        GTNLItemList.MultiEssentiaTube.set(new ItemStack(this, 1, BUFFER_METADATA));
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

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityMultiEssentiaTube tube)) return 0;

        float fillRatio = (float) tube.getTotalAmount() / TileEntityMultiEssentiaTube.MAX_CAPACITY;
        return MathHelper.floor_float(fillRatio * 14.0F) + (tube.getTotalAmount() > 0 ? 1 : 0);
    }
}
