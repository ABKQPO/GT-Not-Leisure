package com.reavaritia.common.block.neutronCollector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import fox.spiteful.avaritia.items.LudicrousItems;
import fox.spiteful.avaritia.tile.TileLudicrous;
import lombok.Getter;

public class TileEntityNeutronCollector extends TileLudicrous implements IInventory {

    public ItemStack neutrons;
    @Getter
    public int facing = 2;
    public int progress;
    public int time;
    public int meta;
    @Getter
    public String machineType;

    public TileEntityNeutronCollector() {
        super();
    }

    public TileEntityNeutronCollector(int time, int meta, String machineType) {
        super();
        this.time = time;
        this.meta = meta;
        this.machineType = machineType;
    }

    @Override
    public void updateEntity() {
        if (++progress >= time) {
            if (neutrons == null) {
                neutrons = createNeutronItemStack();
            } else if (isNeutronItem(neutrons) && neutrons.stackSize < getMaxStackSize()) {
                neutrons.stackSize++;
            }
            progress = 0;
            markDirty();
        }
    }

    private ItemStack createNeutronItemStack() {
        return new ItemStack(LudicrousItems.resource, 1, getNeutronItemDamage());
    }

    private boolean isNeutronItem(ItemStack stack) {
        return stack.getItem() == LudicrousItems.resource && stack.getItemDamage() == getNeutronItemDamage();
    }

    private int getNeutronItemDamage() {
        return this.meta;
    }

    private int getMaxStackSize() {
        return 64;
    }

    public void setFacing(int facing) {
        this.facing = facing;
        this.markDirty();
    }

    public int getProgressScaled(int scale) {
        return time != 0 ? (progress * scale / time) : 0;
    }

    public float getProgressPercentage() {
        return time != 0 ? (progress * 100.0f / time) : 0.0f;
    }

    @Override
    public void readCustomNBT(NBTTagCompound tag) {
        this.neutrons = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Neutrons"));
        this.progress = tag.getInteger("Progress");
        this.facing = tag.getShort("Facing");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound tag) {
        tag.setInteger("Progress", this.progress);
        tag.setShort("Facing", (short) this.facing);
        if (neutrons != null) {
            NBTTagCompound produce = new NBTTagCompound();
            neutrons.writeToNBT(produce);
            tag.setTag("Neutrons", produce);
        } else tag.removeTag("Neutrons");
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return neutrons;
    }

    @Override
    public ItemStack decrStackSize(int slot, int decrement) {
        if (neutrons == null) return null;
        else {
            ItemStack take;
            if (decrement < neutrons.stackSize) {
                take = neutrons.splitStack(decrement);
                if (neutrons.stackSize <= 0) neutrons = null;
            } else {
                take = neutrons;
                neutrons = null;
            }
            markDirty();
            return take;
        }
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
        neutrons = stack;
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
        return "container.neutron";
    }

    /**
     * Returns if the inventory is named
     */
    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

}
