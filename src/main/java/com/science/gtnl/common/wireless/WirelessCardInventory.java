package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.compat.BaublesCompat;

import baubles.common.container.InventoryBaubles;

/** The same carried-item view is used for selection and delayed placement validation. */
public final class WirelessCardInventory {

    private WirelessCardInventory() {}

    public record CardSlot(IInventory inventory, int index) {}

    // GUI address: 0 = held, positive = main slot + 1, negative = -(Baubles slot + 1).
    public static CardSlot resolve(EntityPlayer player, int location) {
        return resolve(player.inventory, baubles(player), player.inventory.currentItem, location);
    }

    static CardSlot resolve(IInventory main, IInventory worn, int selected, int location) {
        IInventory inventory = location < 0 ? worn : main;
        long index = location == 0 ? selected : location > 0 ? (long) location - 1 : -(long) location - 1;
        return inventory != null && index >= 0 && index < inventory.getSizeInventory()
            ? new CardSlot(inventory, (int) index)
            : null;
    }

    public static int location(EntityPlayer player, ItemStack card) {
        if (card == player.getCurrentEquippedItem()) return 0;
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == card) return slot + 1;
        }
        IInventory worn = baubles(player);
        if (worn != null) {
            for (int slot = 0; slot < worn.getSizeInventory(); slot++) {
                if (worn.getStackInSlot(slot) == card) return -slot - 1;
            }
        }
        return Integer.MIN_VALUE;
    }

    public static List<ItemStack> carried(EntityPlayer player) {
        return collect(player.inventory.mainInventory, baubles(player));
    }

    static List<ItemStack> collect(ItemStack[] main, IInventory worn) {
        List<ItemStack> stacks = new ArrayList<>();
        for (ItemStack stack : main) add(stacks, stack);
        if (worn != null) {
            for (int slot = 0; slot < worn.getSizeInventory(); slot++) add(stacks, worn.getStackInSlot(slot));
        }
        return stacks;
    }

    private static void add(List<ItemStack> stacks, ItemStack stack) {
        if (stack != null && stack.stackSize > 0
            && stacks.stream()
                .noneMatch(existing -> existing == stack)) {
            stacks.add(stack);
        }
    }

    public static void sync(EntityPlayer player, ItemStack card) {
        IInventory worn = baubles(player);
        if (worn != null) {
            for (int slot = 0; slot < worn.getSizeInventory(); slot++) {
                if (worn.getStackInSlot(slot) != card) continue;
                worn.markDirty();
                // markDirty only marks the main inventory; worn NBT needs Baubles' own slot packet.
                if (worn instanceof InventoryBaubles inventory) inventory.syncSlotToClients(slot);
            }
        }
        player.inventory.markDirty();
        player.inventoryContainer.detectAndSendChanges();
        if (player.openContainer != player.inventoryContainer) player.openContainer.detectAndSendChanges();
    }

    private static IInventory baubles(EntityPlayer player) {
        return BaublesCompat.getBaubles(player);
    }
}
