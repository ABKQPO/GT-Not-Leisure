package com.science.gtnl.common.block.blocks.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockEssentiaHatch extends ItemBlock {

    public ItemBlockEssentiaHatch(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if (stack == null) return;
        tooltip.add(StatCollector.translateToLocal("gtnl.tooltip.no_mobs_spawn"));
        tooltip.add(StatCollector.translateToLocal("gtnl.block.essentia_hatch.tooltip.0"));
        tooltip.add(StatCollector.translateToLocal("gtnl.block.essentia_hatch.tooltip.1"));
    }
}
