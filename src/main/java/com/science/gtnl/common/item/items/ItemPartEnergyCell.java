package com.science.gtnl.common.item.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.part.PartEnergyCell;
import com.science.gtnl.common.part.PartEnergyCellBase;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.AEApi;

public class ItemPartEnergyCell extends ItemPartEnergyCellBase {

    public ItemPartEnergyCell() {
        super("part_energy_cell", "gtnl.part_energy_cell");
        GTNLItemList.PartEnergyCell.set(new ItemStack(this));
    }

    @Override
    public PartEnergyCell createPartFromItemStack(ItemStack stack) {
        return new PartEnergyCell(stack);
    }

    @Override
    protected double getBaseCapacity() {
        return PartEnergyCellBase.ENERGY_CELL_CAPACITY;
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
