package com.science.gtnl.common.item.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.part.PartEnergyCellBase;
import com.science.gtnl.common.part.PartSuperDenseEnergyCell;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.enums.GTNLItemList;

public class ItemPartSuperDenseEnergyCell extends ItemPartEnergyCellBase {

    public ItemPartSuperDenseEnergyCell() {
        super("PartSuperDenseEnergyCell", "gtnl.item.part_super_dense_energy_cell");
        GTNLItemList.PartSuperDenseEnergyCell.set(new ItemStack(this));
    }

    @Override
    public PartSuperDenseEnergyCell createPartFromItemStack(ItemStack stack) {
        return new PartSuperDenseEnergyCell(stack);
    }

    @Override
    protected double getBaseCapacity() {
        return PartEnergyCellBase.SUPER_DENSE_ENERGY_CELL_CAPACITY;
    }

    @Override
    protected Block getEnergyCellBlock() {
        return BlockLoader.superDenseEnergyCell;
    }
}
