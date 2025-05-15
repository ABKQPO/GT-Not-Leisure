package com.science.gtnl.common.block.blocks.laserBeacon;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemBlockLaserBeacon extends ItemBlock {

    public ItemBlockLaserBeacon(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean f3_h) {
        tooltip.add(StatCollector.translateToLocal("Tooltip_LaserBeacon"));
    }
}
