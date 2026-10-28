package com.science.gtnl.common.wireless;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.gtnhlib.compat.BaublesCompat;
import com.science.gtnl.common.item.items.WirelessFrequencyCard;
import com.science.gtnl.common.wireless.WirelessCardSelection.Candidate;

import baubles.common.container.InventoryBaubles;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class WirelessCardInventory {

    public record CardSlot(IInventory inventory, int index) {}

    static final class TrackedCard {

        private ItemStack card;
        private final UUID identity;
        private final WirelessCardBinding binding;

        TrackedCard(ItemStack card, WirelessCardBinding binding) {
            this.card = card;
            this.identity = WirelessCardBinding.identity(card.getTagCompound());
            this.binding = binding;
        }

        boolean present(EntityPlayer player) {
            return present(WirelessCardInventory.location(player, card) != Integer.MIN_VALUE, () -> carried(player));
        }

        boolean present(List<ItemStack> carried) {
            return present(
                carried.stream()
                    .anyMatch(stack -> stack == card),
                () -> carried);
        }

        private boolean present(boolean originalPresent, Supplier<List<ItemStack>> carried) {
            if (identity == null) return false;
            if (card.stackSize <= 0 || !originalPresent) {
                // Vanilla inventory moves can replace the stack object; follow only its unique persisted identity.
                ItemStack current = null;
                for (ItemStack candidate : carried.get()) {
                    if (candidate.stackSize <= 0 || !candidate.isItemEqual(card)
                        || !identity.equals(WirelessCardBinding.identity(candidate.getTagCompound()))) continue;
                    if (current != null) return false;
                    current = candidate;
                }
                if (current == null) return false;
                card = current;
            }
            return identity.equals(WirelessCardBinding.identity(card.getTagCompound()))
                && binding.equals(WirelessCardBinding.read(card.getTagCompound()))
                && WirelessCardBinding.automatic(card.getTagCompound());
        }
    }

    // GUI address: 0 = held, positive = main slot + 1, negative = -(Baubles slot + 1).
    public static CardSlot resolve(EntityPlayer player, int location) {
        return resolve(player.inventory, BaublesCompat.getBaubles(player), player.inventory.currentItem, location);
    }

    private static CardSlot resolve(IInventory main, IInventory worn, int selected, int location) {
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
        IInventory worn = BaublesCompat.getBaubles(player);
        if (worn != null) {
            for (int slot = 0; slot < worn.getSizeInventory(); slot++) {
                if (worn.getStackInSlot(slot) == card) return -slot - 1;
            }
        }
        return Integer.MIN_VALUE;
    }

    public static List<ItemStack> carried(EntityPlayer player) {
        return collect(player.inventory.mainInventory, BaublesCompat.getBaubles(player));
    }

    static boolean hasBoundCard(EntityPlayer player, WirelessFrequency source) {
        return hasBoundCard(player.getUniqueID(), source, carried(player));
    }

    static boolean hasBoundCard(UUID owner, WirelessFrequency source, Iterable<ItemStack> cards) {
        for (ItemStack card : cards) {
            if (card == null || card.stackSize <= 0 || !(card.getItem() instanceof WirelessFrequencyCard)) continue;
            WirelessCardBinding binding = WirelessCardBinding.read(card.getTagCompound());
            if (binding != null && binding.belongsTo(owner) && WirelessFrequencies.same(binding.frequency(), source))
                return true;
        }
        return false;
    }

    static WirelessCardSelection.Selection<ItemStack> select(EntityPlayer player, boolean connecting) {
        return WirelessCardSelection
            .select(candidates(player, carried(player), WirelessCardBinding::automatic), connecting);
    }

    static List<Candidate<ItemStack>> candidates(EntityPlayer player, List<ItemStack> stacks,
        Predicate<NBTTagCompound> enabled) {
        List<Candidate<ItemStack>> candidates = new ObjectArrayList<>();
        for (ItemStack stack : stacks) {
            if (!(stack.getItem() instanceof WirelessFrequencyCard)) continue;
            NBTTagCompound tag = stack.getTagCompound();
            WirelessCardBinding binding = WirelessCardBinding.read(tag);
            candidates.add(
                new Candidate<>(
                    stack,
                    stack == player.getCurrentEquippedItem(),
                    binding == null || binding.belongsTo(player.getUniqueID()),
                    binding != null,
                    enabled.test(tag)));
        }
        return candidates;
    }

    static List<ItemStack> collect(ItemStack[] main, IInventory worn) {
        List<ItemStack> stacks = new ObjectArrayList<>();
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
        IInventory worn = BaublesCompat.getBaubles(player);
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

}
