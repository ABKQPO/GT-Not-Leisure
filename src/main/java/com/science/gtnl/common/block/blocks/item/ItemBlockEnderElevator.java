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
        tooltip.add(StatCollector.translateToLocal("gtnl.block.ender_elevator.tooltip.0"));
        tooltip.add(StatCollector.translateToLocal("gtnl.block.ender_elevator.tooltip.1"));
        tooltip.add(StatCollector.translateToLocal("gtnl.block.ender_elevator.tooltip.2"));
        tooltip.add(StatCollector.translateToLocal("gtnl.block.ender_elevator.tooltip.3"));
        tooltip.add(StatCollector.translateToLocal("gtnl.block.ender_elevator.tooltip.4"));
    }

}
