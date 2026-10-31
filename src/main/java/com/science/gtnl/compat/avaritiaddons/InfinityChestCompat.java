package com.science.gtnl.compat.avaritiaddons;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import wanion.avaritiaddons.Avaritiaddons;
import wanion.avaritiaddons.network.InfinityChestClick;

public class InfinityChestCompat {

    public static void sendSlotClick(GuiContainer window, int slotIndex, int button, int modifiers) {
        short nextTransactionID = window.mc.thePlayer.openContainer.getNextTransactionID(window.mc.thePlayer.inventory);
        ItemStack itemStack = window.mc.thePlayer.openContainer
            .slotClick(slotIndex, button, modifiers, window.mc.thePlayer);
        Avaritiaddons.networkWrapper.sendToServer(
            new InfinityChestClick(
                window.inventorySlots.windowId,
                slotIndex,
                button,
                modifiers,
                itemStack,
                nextTransactionID));
    }
}
