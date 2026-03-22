package com.science.gtnl.common.block.blocks.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemBlockEnderElevator extends ItemBlock {

    public ItemBlockEnderElevator(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean f3_h) {
        tooltip.add(StatCollector.translateToLocal("Tooltip_EnderElevator_00"));
        tooltip.add(StatCollector.translateToLocal("Tooltip_EnderElevator_01"));
        tooltip.add(StatCollector.translateToLocal("Tooltip_EnderElevator_02"));
        tooltip.add(StatCollector.translateToLocal("Tooltip_EnderElevator_03"));
        tooltip.add(StatCollector.translateToLocal("Tooltip_EnderElevator_04"));
    }

}
