package com.science.gtnl.common.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.glodblock.github.common.item.ItemFluidPacket;
import com.glodblock.github.inventory.AEFluidInventory;

import appeng.api.storage.data.IAEFluidStack;

public class FluidInventoryAdapter implements IInventory {

    private final AEFluidInventory inventory;

    public FluidInventoryAdapter(AEFluidInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        IAEFluidStack stack = inventory.getFluidInSlot(slot);
        return stack == null ? null : ItemFluidPacket.newStack(stack.getFluidStack());
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack stack = getStackInSlot(slot);
        if (stack == null) {
            return null;
        }
        inventory.setFluidInSlot(slot, null);
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return decrStackSize(slot, getInventoryStackLimit());
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {}

    @Override
    public String getInventoryName() {
        return "superDualInterfaceFluid";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return false;
    }
}
