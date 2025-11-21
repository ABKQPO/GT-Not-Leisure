package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityDirePatternEncoder extends TileEntity implements IInventory, ISidedInventory {

    public ItemStack result; // slot 0
    public ItemStack inputPattern; // slot 1
    public ItemStack outputPattern; // slot 2
    public ItemStack[] matrix = new ItemStack[81]; // slot 3~83

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound aNBT) {
        super.readFromNBT(aNBT);
        this.result = ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag("result"));
        this.inputPattern = ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag("inputPattern"));
        this.outputPattern = ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag("outputPattern"));
        for (int x = 0; x < matrix.length; x++) {
            matrix[x] = ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag("craft" + x));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound aNBT) {
        super.writeToNBT(aNBT);
        if (result != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            result.writeToNBT(nbtTagCompound);
            aNBT.setTag("result", nbtTagCompound);
        } else aNBT.removeTag("result");

        if (inputPattern != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            inputPattern.writeToNBT(nbtTagCompound);
            aNBT.setTag("inputPattern", nbtTagCompound);
        } else aNBT.removeTag("inputPattern");

        if (outputPattern != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            outputPattern.writeToNBT(nbtTagCompound);
            aNBT.setTag("outputPattern", nbtTagCompound);
        } else aNBT.removeTag("outputPattern");

        for (int x = 0; x < matrix.length; x++) {
            if (matrix[x] != null) {
                NBTTagCompound craft = new NBTTagCompound();
                matrix[x].writeToNBT(craft);
                aNBT.setTag("craft" + x, craft);
            } else aNBT.removeTag("craft" + x);
        }
    }

    @Override
    public int getSizeInventory() {
        return 82;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot == 0) return result;
        else if (slot <= matrix.length) return matrix[slot - 1];
        else return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int decrement) {

        if (slot == 0) {

            if (result == null) return null;

            if (inputPattern == null || inputPattern.stackSize <= 0) return null;

            inputPattern.stackSize--;
            if (inputPattern.stackSize <= 0) inputPattern = null;

            outputPattern = result.copy();
            outputPattern.stackSize = 1;

            markDirty();
            return outputPattern;
        }

        if (slot == 1 && inputPattern != null) {
            if (inputPattern.stackSize <= decrement) {
                ItemStack tmp = inputPattern;
                inputPattern = null;
                markDirty();
                return tmp;
            }
            ItemStack split = inputPattern.splitStack(decrement);
            if (inputPattern.stackSize <= 0) inputPattern = null;
            markDirty();
            return split;
        }

        if (slot == 2 && outputPattern != null) {
            ItemStack tmp = outputPattern;
            outputPattern = null;
            markDirty();
            return tmp;
        }

        return null;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player
            .getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D)
            <= 64.0D;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {

        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot == 0) {
            result = stack;
            markDirty();
        } else if (slot <= matrix.length) {
            matrix[slot - 1] = stack;
            markDirty();
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    /**
     * Returns the name of the inventory
     */
    @Override
    public String getInventoryName() {
        return "container.DirePatternEncoder";
    }

    /**
     * Returns if the inventory is named
     */
    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] {};
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack item, int side) {
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack item, int side) {
        return false;
    }

}
