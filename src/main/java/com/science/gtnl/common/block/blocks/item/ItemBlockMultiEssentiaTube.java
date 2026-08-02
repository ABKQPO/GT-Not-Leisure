package com.science.gtnl.common.block.blocks.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaTube;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockMultiEssentiaTube extends ItemBlock {

    private static final int BUFFER_METADATA = 4;

    public ItemBlockMultiEssentiaTube(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int metadata) {
        return BUFFER_METADATA;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(
            StatCollector.translateToLocalFormatted(
                "Tooltip_MultiEssentiaTube_Capacity",
                TileEntityMultiEssentiaTube.MAX_CAPACITY));
        tooltip.add(StatCollector.translateToLocal("Tooltip_MultiEssentiaTube_Mixed"));
        tooltip.add(StatCollector.translateToLocal("Tooltip_MultiEssentiaTube_Wand"));
    }
}
