package com.science.gtnl.common.item.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class InventoryInfinityChest implements IInventory {

    private final int maxStackSize;
    public final ItemStack[] contents = new ItemStack[243];

    public InventoryInfinityChest(final int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    @Override
    public int getSizeInventory() {
        return contents.length;
    }

    @Override
    public ItemStack getStackInSlot(final int slot) {
        return slot >= 0 && slot < contents.length ? contents[slot] : null;
    }

    @Override
    public ItemStack decrStackSize(final int slot, final int amount) {
        ItemStack stack = getStackInSlot(slot);
        if (stack == null) return null;

        int takeAmount = MathHelper.clamp_int(amount, 1, stack.stackSize);
        ItemStack result = stack.copy();
        result.stackSize = takeAmount;

        stack.stackSize -= takeAmount;
        if (stack.stackSize <= 0) contents[slot] = null;

        markDirty();
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(final int slot) {
        return getStackInSlot(slot);
    }

    @Override
    public void setInventorySlotContents(final int slot, final ItemStack itemStack) {
        contents[slot] = itemStack;
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "Infinity Chest";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return maxStackSize;
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUseableByPlayer(final EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(final int slot, final ItemStack stack) {
        return true;
    }
}
