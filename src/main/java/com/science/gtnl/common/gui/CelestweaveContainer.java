package com.science.gtnl.common.gui;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.PlayerSlotGroup;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.item.items.CelestweaveArmor;

public class CelestweaveContainer extends ModularContainer {

    private final int boundSlot;
    private final CelestweaveGui gui;

    public CelestweaveContainer(int boundSlot, CelestweaveGui gui) {
        this.boundSlot = boundSlot;
        this.gui = gui;
    }

    @Override
    public void detectAndSendChanges() {
        gui.syncView(true);
        try {
            super.detectAndSendChanges();
        } finally {
            gui.syncView(false);
        }
    }

    @Override
    public List<ItemStack> getInventory() {
        gui.syncView(true);
        try {
            return super.getInventory();
        } finally {
            gui.syncView(false);
        }
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
    public void onModularContainerClosed() {
        super.onModularContainerClosed();
        if (isClient()) return;
        ItemStack stack = getPlayer().inventory.getStackInSlot(boundSlot);
        if (stack != null && stack.getItem() instanceof CelestweaveArmor) CelestweaveEnergy.clamp(stack);
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
