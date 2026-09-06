package com.science.gtnl.common.part;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.science.gtnl.loader.BlockLoader;

public class PartSuperDenseEnergyCell extends PartEnergyCellBase {

    public PartSuperDenseEnergyCell(ItemStack stack) {
        super(stack);
    }

    @Override
    protected double getBaseCapacity() {
        return SUPER_DENSE_ENERGY_CELL_CAPACITY;
    }

    @Override
    protected Block getEnergyCellBlock() {
        return BlockLoader.superDenseEnergyCell;
    }
}
