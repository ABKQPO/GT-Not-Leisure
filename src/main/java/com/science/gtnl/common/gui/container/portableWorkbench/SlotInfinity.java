package com.science.gtnl.common.gui.container.portableWorkbench;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import com.science.gtnl.api.IInfinitySlot;

public class SlotInfinity extends Slot implements IInfinitySlot {

    public SlotInfinity(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
        super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
    }

    @Override
    public int getSlotStackLimit() {
        return Integer.MAX_VALUE;
    }
}
