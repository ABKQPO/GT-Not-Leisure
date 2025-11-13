package com.science.gtnl.common.block.blocks.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemBlockSearedLadder extends ItemBlock {

    public ItemBlockSearedLadder(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean f3_h) {
        tooltip.add(StatCollector.translateToLocal("smeltery.brick.tooltip1"));
        tooltip.add(StatCollector.translateToLocal("smeltery.brick.tooltip2"));
        tooltip.add(StatCollector.translateToLocal("Tooltip_SearedLadder_00"));
        tooltip.add(StatCollector.translateToLocal("Tooltip_SearedLadder_01"));
    }
}
