package com.science.gtnl.common.block.blocks.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaInputHatch;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockMultiEssentiaInputHatch extends ItemBlock {

    public ItemBlockMultiEssentiaInputHatch(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(StatCollector.translateToLocal("gtnl.tooltip.no_mobs_spawn"));
        tooltip.add(
            StatCollector.translateToLocalFormatted(
                "gtnl.block.multi_essentia_input_hatch.tooltip.0",
                TileEntityMultiEssentiaInputHatch.MAX_CAPACITY));
        tooltip.add(StatCollector.translateToLocal("gtnl.block.multi_essentia_input_hatch.tooltip.1"));
        tooltip.add(StatCollector.translateToLocal("gtnl.block.multi_essentia_input_hatch.tooltip.2"));
    }
}
