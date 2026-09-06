package com.science.gtnl.common.part;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import appeng.api.AEApi;

public class PartEnergyCell extends PartEnergyCellBase {

    public PartEnergyCell(ItemStack stack) {
        super(stack);
    }

    @Override
    protected double getBaseCapacity() {
        return ENERGY_CELL_CAPACITY;
    }

    @Override
    protected Block getEnergyCellBlock() {
        return AEApi.instance()
            .definitions()
            .blocks()
            .energyCell()
            .maybeBlock()
            .orNull();
    }
}
