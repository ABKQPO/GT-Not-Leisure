package com.science.gtnl.common.part;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import appeng.api.AEApi;

public class PartDenseEnergyCell extends PartEnergyCellBase {

    public PartDenseEnergyCell(ItemStack stack) {
        super(stack);
    }

    @Override
    protected double getBaseCapacity() {
        return DENSE_ENERGY_CELL_CAPACITY;
    }

    @Override
    protected Block getEnergyCellBlock() {
        return AEApi.instance()
            .definitions()
            .blocks()
            .energyCellDense()
            .maybeBlock()
            .orNull();
    }
}
