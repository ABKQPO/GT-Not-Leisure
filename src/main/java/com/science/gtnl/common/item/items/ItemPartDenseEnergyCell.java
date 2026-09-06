package com.science.gtnl.common.item.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.part.PartDenseEnergyCell;
import com.science.gtnl.common.part.PartEnergyCellBase;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.AEApi;

public class ItemPartDenseEnergyCell extends ItemPartEnergyCellBase {

    public ItemPartDenseEnergyCell() {
        super("PartDenseEnergyCell");
        GTNLItemList.PartDenseEnergyCell.set(new ItemStack(this));
    }

    @Override
    public PartDenseEnergyCell createPartFromItemStack(ItemStack stack) {
        return new PartDenseEnergyCell(stack);
    }

    @Override
    protected double getBaseCapacity() {
        return PartEnergyCellBase.DENSE_ENERGY_CELL_CAPACITY;
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
