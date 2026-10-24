package com.science.gtnl.common.yogsothoth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import baubles.common.container.InventoryBaubles;

// Why: other mods cast BaublesApi.getBaubles to InventoryBaubles (Witching Gadgets' Kama on equip).
public class YogBaublesView extends InventoryBaubles {

    public interface Entry {

        ItemStack get();

        void set(EntityPlayer player, ItemStack stack);
    }

    private static BiFunction<EntityPlayer, InventoryBaubles, List<? extends Entry>> clientEntries;

    private final InventoryBaubles delegate;

    private YogBaublesView(EntityPlayer player, InventoryBaubles delegate) {
        super(player);
        this.delegate = delegate;
        stackList = delegate.stackList;
    }

    public static void setClientEntries(BiFunction<EntityPlayer, InventoryBaubles, List<? extends Entry>> entries) {
        clientEntries = entries;
    }

    public static IInventory wrap(EntityPlayer player, IInventory original) {
        if (!(original instanceof InventoryBaubles baubles) || original instanceof YogBaublesView
            || player == null
            || player.isDead
            || player.getHealth() <= 0) return original;
        if (entries(player, baubles).isEmpty()) return original;
        // Why: not cached; a player-keyed cache pins the player, and a respawned player equals the dead one.
        return new YogBaublesView(player, baubles);
    }

    static List<? extends Entry> entries(EntityPlayer player, InventoryBaubles baubles) {
        if (!player.worldObj.isRemote) return YogTerminalService.carriedEntries(player, baubles);
        return clientEntries == null ? Collections.emptyList() : clientEntries.apply(player, baubles);
    }

    public InventoryBaubles delegate() {
        return delegate;
    }

    private List<? extends Entry> virtualEntries() {
        EntityPlayer owner = player.get();
        return owner == null ? Collections.emptyList() : entries(owner, delegate);
    }

    private Entry entry(int slot) {
        int virtual = slot - delegate.getSizeInventory();
        if (virtual < 0) return null;
        List<? extends Entry> entries = virtualEntries();
        return virtual < entries.size() ? entries.get(virtual) : null;
    }

    private EntityPlayer serverOwner() {
        EntityPlayer owner = player.get();
        return owner == null || owner.worldObj.isRemote ? null : owner;
    }

    @Override
    public int getSizeInventory() {
        return delegate.getSizeInventory() + virtualEntries().size();
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
        EntityPlayer owner = serverOwner();
        if (owner != null) entry.set(owner, null);
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
        EntityPlayer owner = serverOwner();
        if (owner == null) return;
        Entry entry = entry(slot);
        if (entry != null) entry.set(owner, stack);
        else YogTerminalService.acceptCarried(owner, delegate, stack);
    }

    @Override
    public void syncSlotToClients(int slot) {
        if (slot < delegate.getSizeInventory()) delegate.syncSlotToClients(slot);
    }

    @Override
    public Container getEventHandler() {
        return delegate.getEventHandler();
    }

    @Override
    public void setEventHandler(Container eventHandler) {
        delegate.setEventHandler(eventHandler);
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
    public boolean isUseableByPlayer(EntityPlayer user) {
        return delegate.isUseableByPlayer(user);
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
        EntityPlayer owner = serverOwner();
        return owner != null && YogTerminalService.acceptsCarried(owner, delegate, stack);
    }

    @Override
    public void saveNBT(EntityPlayer owner) {
        delegate.saveNBT(owner);
    }

    @Override
    public void saveNBT(NBTTagCompound tags) {
        delegate.saveNBT(tags);
    }

    @Override
    public void readNBT(EntityPlayer owner) {
        delegate.readNBT(owner);
    }

    @Override
    public void readNBT(NBTTagCompound tags) {
        delegate.readNBT(tags);
    }

    @Override
    public void dropItems(ArrayList<EntityItem> drops) {
        delegate.dropItems(drops);
    }

    @Override
    public void dropItemsAt(ArrayList<EntityItem> drops, Entity entity) {
        delegate.dropItemsAt(drops, entity);
    }
}
