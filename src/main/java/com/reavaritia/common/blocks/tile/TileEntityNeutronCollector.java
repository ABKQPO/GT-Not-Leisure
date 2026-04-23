package com.reavaritia.common.blocks.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import gregtech.api.util.GTUtility;

public class TileEntityNeutronCollector extends TileEntity implements IInventory {

    public final ItemStack target;
    public ItemStack output;
    public int facing = 2;
    public int progress;
    public int time;
    public int meta;
    public String machineType;

    public TileEntityNeutronCollector() {
        super();
        this.target = null;
    }

    public TileEntityNeutronCollector(int time, ItemStack target, String machineType) {
        super();
        this.time = time;
        this.target = target;
        this.machineType = machineType;
    }

    @Override
    public void updateEntity() {
        if (target == null) return;
        if (++progress >= time) {
            if (this.worldObj != null && !this.worldObj.isRemote) {
                if (output == null) {
                    createNeutronItemStack();
                } else if (GTUtility.areStacksEqual(output, target) && output.stackSize < 64) {
                    output.stackSize++;
                }
            }
            progress = 0;
            markDirty();
        }
    }

    public void createNeutronItemStack() {
        if (output == null && target != null) {
            output = target.copy();
        }
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
    public void readFromNBT(NBTTagCompound tag) {
        this.output = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("neutrons"));
        this.progress = tag.getInteger("progress");
        this.time = tag.getInteger("time");
        this.facing = tag.getInteger("facing");
        this.meta = tag.getInteger("meta");
        this.machineType = tag.getString("machineType");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("progress", this.progress);
        tag.setInteger("time", this.time);
        tag.setInteger("facing", this.facing);
        tag.setInteger("meta", this.meta);
        if (output != null) {
            NBTTagCompound produce = new NBTTagCompound();
            output.writeToNBT(produce);
            tag.setTag("neutrons", produce);
        }
        tag.setString("machineType", machineType);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, -999, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        readFromNBT(packet.func_148857_g());
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return output;
    }

    @Override
    public ItemStack decrStackSize(int slot, int decrement) {
        if (output == null) return null;
        else {
            ItemStack take;
            if (decrement < output.stackSize) {
                take = output.splitStack(decrement);
                if (output.stackSize <= 0) output = null;
            } else {
                take = output;
                output = null;
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
        output = stack;
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
