package com.science.gtnl.common.yogsothoth;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiFunction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import baubles.common.container.InventoryBaubles;

public final class YogBaublesView implements IInventory {

    public interface Entry {

        ItemStack get();

        void set(EntityPlayer player, ItemStack stack);
    }

    private static final Map<EntityPlayer, YogBaublesView> VIEWS = Collections.synchronizedMap(new WeakHashMap<>());
    private static BiFunction<EntityPlayer, InventoryBaubles, List<? extends Entry>> clientEntries;

    private final EntityPlayer player;
    private final InventoryBaubles delegate;

    private YogBaublesView(EntityPlayer player, InventoryBaubles delegate) {
        this.player = player;
        this.delegate = delegate;
    }

    public static void setClientEntries(BiFunction<EntityPlayer, InventoryBaubles, List<? extends Entry>> entries) {
        clientEntries = entries;
    }

    public static IInventory wrap(EntityPlayer player, IInventory original) {
        if (!(original instanceof InventoryBaubles baubles) || player == null
            || player.isDead
            || player.getHealth() <= 0) return original;
        if (entries(player, baubles).isEmpty()) return original;
        YogBaublesView view = VIEWS.get(player);
        if (view == null || view.delegate != baubles) {
            view = new YogBaublesView(player, baubles);
            VIEWS.put(player, view);
        }
        return view;
    }

    static List<? extends Entry> entries(EntityPlayer player, InventoryBaubles baubles) {
        if (!player.worldObj.isRemote) return YogTerminalService.carriedEntries(player, baubles);
        return clientEntries == null ? Collections.emptyList() : clientEntries.apply(player, baubles);
    }

    public InventoryBaubles delegate() {
        return delegate;
    }

    private Entry entry(int slot) {
        int virtual = slot - delegate.getSizeInventory();
        if (virtual < 0) return null;
        List<? extends Entry> entries = entries(player, delegate);
        return virtual < entries.size() ? entries.get(virtual) : null;
    }

    @Override
    public int getSizeInventory() {
        return delegate.getSizeInventory() + entries(player, delegate).size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < delegate.getSizeInventory()) return slot < 0 ? null : delegate.getStackInSlot(slot);
        Entry entry = entry(slot);
        return entry == null ? null : entry.get();
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot < delegate.getSizeInventory()) return delegate.decrStackSize(slot, amount);
        Entry entry = entry(slot);
        ItemStack stack = entry == null ? null : entry.get();
        if (stack == null || amount <= 0) return null;
        if (!player.worldObj.isRemote) entry.set(player, null);
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return slot < delegate.getSizeInventory() ? delegate.getStackInSlotOnClosing(slot) : null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot < delegate.getSizeInventory()) {
            delegate.setInventorySlotContents(slot, stack);
            return;
        }
        if (player.worldObj.isRemote) return;
        Entry entry = entry(slot);
        if (entry != null) entry.set(player, stack);
        else YogTerminalService.acceptCarried(player, delegate, stack);
    }

    @Override
    public String getInventoryName() {
        return delegate.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return delegate.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return delegate.getInventoryStackLimit();
    }

    @Override
    public void markDirty() {
        delegate.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return delegate.isUseableByPlayer(player);
    }

    @Override
    public void openInventory() {
        delegate.openInventory();
    }

    @Override
    public void closeInventory() {
        delegate.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot < delegate.getSizeInventory()) return delegate.isItemValidForSlot(slot, stack);
        if (entry(slot) != null) return YogToken.isStorable(stack);
        return !player.worldObj.isRemote && YogTerminalService.acceptsCarried(player, delegate, stack);
    }
}
