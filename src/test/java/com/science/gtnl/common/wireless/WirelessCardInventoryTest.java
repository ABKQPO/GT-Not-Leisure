package com.science.gtnl.common.wireless;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/** Checks actual stack identity across the inventory view used by delayed placement work. */
public final class WirelessCardInventoryTest {

    public static void main(String[] args) {
        Item item = new Item();
        ItemStack card = new ItemStack(item);
        ItemStack[] main = new ItemStack[36];
        InventoryBasic worn = new InventoryBasic("test", true, 12);
        InventoryBasic inventory = new InventoryBasic("main", true, 36);
        var heldSlot = WirelessCardInventory.resolve(inventory, worn, 8, 0);
        check(heldSlot.inventory() == inventory && heldSlot.index() == 8, "Air-use keeps the selected hotbar slot");
        var backpackSlot = WirelessCardInventory.resolve(inventory, worn, 0, 36);
        check(
            backpackSlot.inventory() == inventory && backpackSlot.index() == 35,
            "The shortcut can address the last backpack slot without changing the selected item");
        var wornSlot = WirelessCardInventory.resolve(inventory, worn, 0, -12);
        check(wornSlot.inventory() == worn && wornSlot.index() == 11, "Expanded Baubles slots can host the GUI card");
        check(
            WirelessCardInventory.resolve(inventory, null, 0, -1) == null,
            "Missing Baubles inventory rejects a worn-card address");
        for (int invalid : new int[] { 37, -13, Integer.MIN_VALUE, Integer.MAX_VALUE }) {
            check(
                WirelessCardInventory.resolve(inventory, worn, 0, invalid) == null,
                "Out-of-range GUI addresses are rejected without overflow");
        }
        worn.setInventorySlotContents(11, card);
        check(
            WirelessCardInventory.collect(main, worn)
                .get(0) == card,
            "Expanded slots retain the actual worn stack");
        check(
            WirelessCardInventory.collect(main, null)
                .isEmpty(),
            "An unavailable Baubles inventory is harmless");
        main[5] = card;
        ItemStack hand = new ItemStack(item);
        var displayCards = WirelessCardInventory.collect(new ItemStack[] { hand }, worn);
        check(
            displayCards.size() == 2 && displayCards.get(0) == hand && displayCards.get(1) == card,
            "Visualisation candidates include the actual hand and worn stacks");
        check(
            WirelessCardInventory.collect(new ItemStack[] { null }, null)
                .isEmpty(),
            "An empty hand without Baubles yields no visualisation candidates");
        check(
            WirelessCardInventory.collect(main, worn)
                .size() == 1,
            "One stack reference cannot create false ambiguity");
        worn.setInventorySlotContents(11, null);
        check(
            WirelessCardInventory.collect(main, worn)
                .get(0) == card,
            "The same stack remains carried after moving");
        ItemStack other = card.copy();
        worn.setInventorySlotContents(2, other);
        check(
            WirelessCardInventory.collect(main, worn)
                .size() == 2,
            "Equal separate cards still create two candidates");
        main[5] = null;
        check(
            WirelessCardInventory.collect(main, worn)
                .stream()
                .noneMatch(stack -> stack == card),
            "A replacement card cannot keep the removed card's delayed work alive");
        other.stackSize = 0;
        check(
            WirelessCardInventory.collect(main, worn)
                .isEmpty(),
            "Empty stacks cannot authorize placement");
        ItemStack vanillaCopy = ItemStack.copyItemStack(card);
        check(
            vanillaCopy != card && WirelessCardContainer.matchesHeldCard(3, 3, card, vanillaCopy),
            "Vanilla's post-right-click copy must keep the GUI open");
        check(!WirelessCardContainer.matchesHeldCard(3, 4, card, vanillaCopy), "Changing hotbar slots closes the GUI");
        check(!WirelessCardContainer.matchesHeldCard(3, 3, card, null), "Removing the card closes the GUI");
        vanillaCopy.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
        vanillaCopy.getTagCompound()
            .setBoolean("different", true);
        check(
            !WirelessCardContainer.matchesHeldCard(3, 3, card, vanillaCopy),
            "A differently configured card cannot reuse the GUI session");
        System.out.println("WirelessCardInventoryTest: carried stacks and vanilla GUI copy regression passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
