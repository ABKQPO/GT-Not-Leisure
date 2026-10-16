package com.science.gtnl.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.PlayerSlotGroup;

public class RailgunContainer extends ModularContainer {

    private final int boundSlot;

    public RailgunContainer(int boundSlot) {
        this.boundSlot = boundSlot;
    }

    private boolean bound(int index) {
        if (index < 0 || index >= inventorySlots.size()) return false;
        var slot = getSlot(index);
        return slot instanceof ModularSlot modular && modular.getSlotGroup() != null
            && PlayerSlotGroup.NAME.equals(
                modular.getSlotGroup()
                    .getName())
            && slot.getSlotIndex() == boundSlot;
    }

    @Override
    public void onModularContainerOpened() {
        super.onModularContainerOpened();
        for (int i = 0; i < inventorySlots.size(); i++)
            if (bound(i)) ((ModularSlot) getSlot(i)).accessibility(false, false);
    }

    @Override
    public ItemStack slotClick(int slot, int button, int mode, EntityPlayer player) {
        if (bound(slot) || mode == 2 && button == boundSlot) return null;
        return super.slotClick(slot, button, mode, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        return bound(index) ? null : super.transferStackInSlot(player, index);
    }
}
