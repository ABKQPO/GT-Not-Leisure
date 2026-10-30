package com.gtnllib.gui.widget;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;

public class ListeningItemHandler implements IItemHandlerModifiable {

    private final IItemHandlerModifiable delegate;
    private final Runnable changeListener;

    public ListeningItemHandler(IItemHandlerModifiable delegate, Runnable changeListener) {
        this.delegate = delegate;
        this.changeListener = changeListener;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        delegate.setStackInSlot(slot, stack);
        changeListener.run();
    }

    @Override
    public int getSlots() {
        return delegate.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return delegate.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        ItemStack remainder = delegate.insertItem(slot, stack, simulate);
        if (!simulate && stack != null && (remainder == null || remainder.stackSize != stack.stackSize))
            changeListener.run();
        return remainder;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack extracted = delegate.extractItem(slot, amount, simulate);
        if (!simulate && extracted != null) changeListener.run();
        return extracted;
    }

    @Override
    public int getSlotLimit(int slot) {
        return delegate.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return delegate.isItemValid(slot, stack);
    }
}
