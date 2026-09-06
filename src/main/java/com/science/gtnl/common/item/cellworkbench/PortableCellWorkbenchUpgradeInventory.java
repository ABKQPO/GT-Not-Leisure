package com.science.gtnl.common.item.cellworkbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class PortableCellWorkbenchUpgradeInventory implements IInventory {

    private final PortableCellWorkbenchHost host;
    private final IInventory delegate;

    public PortableCellWorkbenchUpgradeInventory(PortableCellWorkbenchHost host, IInventory delegate) {
        this.host = host;
        this.delegate = delegate;
    }

    @Override
    public int getSizeInventory() {
        return delegate.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return delegate.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack result = delegate.decrStackSize(slot, amount);
        if (result != null) {
            saveChanges();
        }
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack result = delegate.getStackInSlotOnClosing(slot);
        if (result != null) {
            saveChanges();
        }
        return result;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        delegate.setInventorySlotContents(slot, stack);
        saveChanges();
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
        saveChanges();
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
        return delegate.isItemValidForSlot(slot, stack);
    }

    private void saveChanges() {
        host.onUpgradeInventoryChanged();
    }
}
