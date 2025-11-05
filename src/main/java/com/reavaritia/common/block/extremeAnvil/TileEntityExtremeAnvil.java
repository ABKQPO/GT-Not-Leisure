package com.reavaritia.common.block.extremeAnvil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityExtremeAnvil extends TileEntity implements IInventory {

    public ItemStack[] inventory = new ItemStack[3];

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        NBTTagList itemList = tag.getTagList("Items", 10);
        for (int i = 0; i < itemList.tagCount(); ++i) {
            NBTTagCompound slotTag = itemList.getCompoundTagAt(i);
            int slot = slotTag.getByte("Slot") & 255;

            if (slot < this.inventory.length) {
                this.inventory[slot] = ItemStack.loadItemStackFromNBT(slotTag);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] != null) {
                NBTTagCompound slotTag = new NBTTagCompound();
                slotTag.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(slotTag);
                itemList.appendTag(slotTag);
            }
        }
        tag.setTag("Items", itemList);
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= this.inventory.length) return null;
        return this.inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot < 0 || slot >= this.inventory.length || this.inventory[slot] == null) return null;

        ItemStack stack;
        if (this.inventory[slot].stackSize <= amount) {
            stack = this.inventory[slot];
            this.inventory[slot] = null;
        } else {
            stack = this.inventory[slot].splitStack(amount);
            if (this.inventory[slot].stackSize == 0) {
                this.inventory[slot] = null;
            }
        }
        this.markDirty();
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (slot < 0 || slot >= this.inventory.length || this.inventory[slot] == null) return null;

        ItemStack stack = this.inventory[slot];
        this.inventory[slot] = null;
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot < 0 || slot >= this.inventory.length) return;

        this.inventory[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return "container.ExtremeAnvil";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
            && player.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }
}
