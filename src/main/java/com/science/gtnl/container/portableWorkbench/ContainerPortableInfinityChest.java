package com.science.gtnl.container.portableWorkbench;

import static com.science.gtnl.ScienceNotLeisure.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

import org.jetbrains.annotations.NotNull;

import com.science.gtnl.common.item.items.PortableItem;
import com.science.gtnl.common.packet.PortableInfinityChestSyncPacket;

import cpw.mods.fml.common.FMLCommonHandler;

public class ContainerPortableInfinityChest extends ContainerPortableAvaritiaddonsChest {

    public ContainerPortableInfinityChest(@NotNull ItemStack stack, InventoryPlayer playerInv) {
        super(stack, playerInv, true);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        ItemStack held = player.getHeldItem();
        return PortableItem.matchesPortableID(held, portableID);
    }

    @Override
    public boolean mergeItemStack(final ItemStack itemStack, final int start, final int end, final boolean backwards) {
        if (FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient()) return false;
        int currentSlot = !backwards ? start : end - 1;
        boolean someThingChanged = false;

        while (itemStack.stackSize > 0 && (!backwards && currentSlot < end || backwards && currentSlot >= start)) {
            final Slot slot = inventorySlots.get(currentSlot);
            final ItemStack slotStack = slot.getStack();
            boolean changed = false;

            if (!backwards) {
                if (slotStack == null) {
                    slot.putStack(itemStack.copy());
                    itemStack.stackSize = 0;
                    changed = true;
                } else if (slotStack.getItem() == itemStack.getItem()
                    && itemStack.getItemDamage() == slotStack.getItemDamage()
                    && ItemStack.areItemStackTagsEqual(itemStack, slotStack)) {
                        if ((long) slotStack.stackSize + (long) itemStack.stackSize <= (long) Integer.MAX_VALUE) {
                            slotStack.stackSize += itemStack.stackSize;
                            itemStack.stackSize = 0;
                            changed = true;
                        }
                    }
                ++currentSlot;
            } else {
                if (slotStack == null) {
                    final int dif = MathHelper.clamp_int(itemStack.getMaxStackSize(), 1, itemStack.stackSize);
                    final ItemStack newItemStack = itemStack.copy();
                    newItemStack.stackSize = dif;
                    itemStack.stackSize -= dif;
                    slot.putStack(newItemStack);
                    changed = true;
                } else
                    if (slotStack.getItem() == itemStack.getItem() && slotStack.stackSize < slotStack.getMaxStackSize()
                        && itemStack.getItemDamage() == slotStack.getItemDamage()
                        && ItemStack.areItemStackTagsEqual(itemStack, slotStack)) {
                            final int dif = MathHelper
                                .clamp_int(itemStack.stackSize, 1, slotStack.getMaxStackSize() - slotStack.stackSize);
                            slotStack.stackSize += dif;
                            itemStack.stackSize -= dif;
                            changed = true;
                        }
                --currentSlot;
            }
            if (changed) {
                slot.onSlotChanged();
                someThingChanged = true;
            }
        }
        return someThingChanged;
    }

    @Override
    public boolean canDragIntoSlot(final Slot slot) {
        return slot.slotNumber > 242;
    }

    @Override
    public ItemStack slotClick(int slot, final int mouseButton, final int modifier, final EntityPlayer player) {
        if (slot >= 0 && slot < 243 && modifier == 0) {
            Slot actualSlot = inventorySlots.get(slot);
            final ItemStack slotStack = actualSlot.getStack();
            final ItemStack playerStack = player.inventory.getItemStack();
            if (mouseButton == 0) {
                if (slotStack == null && playerStack != null) {
                    actualSlot.putStack(playerStack.copy());
                    player.inventory.setItemStack(null);
                } else if (slotStack != null && playerStack == null) {
                    final ItemStack newItemStack = slotStack.copy();
                    int took = MathHelper.clamp_int(slotStack.stackSize, 1, newItemStack.getMaxStackSize());
                    newItemStack.stackSize = took;
                    slotStack.stackSize -= took;
                    player.inventory.setItemStack(newItemStack);
                    if (slotStack.stackSize == 0) actualSlot.putStack(null);
                    else actualSlot.onSlotChanged();
                } else if (slotStack != null && slotStack.getItem() == playerStack.getItem()
                    && playerStack.getItemDamage() == slotStack.getItemDamage()
                    && ItemStack.areItemStackTagsEqual(playerStack, slotStack)) {
                        if ((long) slotStack.stackSize + (long) playerStack.stackSize <= (long) Integer.MAX_VALUE) {
                            slotStack.stackSize += playerStack.stackSize;
                            player.inventory.setItemStack(null);
                        }
                    }
            } else if (mouseButton == 1 && playerStack != null) {
                if (slotStack == null) {
                    playerStack.stackSize--;
                    final ItemStack newItemStack = playerStack.copy();
                    newItemStack.stackSize = 1;
                    actualSlot.putStack(newItemStack);
                    if (playerStack.stackSize == 0) player.inventory.setItemStack(null);
                } else if (slotStack.getItem() == playerStack.getItem()
                    && playerStack.getItemDamage() == slotStack.getItemDamage()
                    && ItemStack.areItemStackTagsEqual(playerStack, slotStack)) {
                        playerStack.stackSize--;
                        slotStack.stackSize++;
                        actualSlot.onSlotChanged();
                        if (playerStack.stackSize == 0) player.inventory.setItemStack(null);
                    }
            } else if (mouseButton == 1 && slotStack != null) {
                int took = slotStack.stackSize > slotStack.getMaxStackSize() ? slotStack.getMaxStackSize() / 2
                    : slotStack.stackSize / 2;
                if (took == 0) took = 1;
                final ItemStack newItemStack = slotStack.copy();
                newItemStack.stackSize = took;
                slotStack.stackSize -= took;
                player.inventory.setItemStack(newItemStack);
                if (slotStack.stackSize == 0) actualSlot.putStack(null);
                else actualSlot.onSlotChanged();
            }
            ItemStack held = player.getHeldItem();
            if (PortableItem.matchesPortableID(held, portableID)) {
                type.saveInventory(held, this.chestInventory);
                itemStack = held;
            }
            return actualSlot.getHasStack() ? actualSlot.getStack()
                .copy() : null;
        } else if (slot >= 0 && slot < 243 && modifier == 2) {
            ItemStack held = player.getHeldItem();
            if (PortableItem.matchesPortableID(held, portableID)) {
                type.saveInventory(held, this.chestInventory);
                itemStack = held;
            }
            return null;
        } else {
            ItemStack held = player.getHeldItem();
            if (PortableItem.matchesPortableID(held, portableID)) {
                type.saveInventory(held, this.chestInventory);
                itemStack = held;
            }
            return super.slotClick(slot, mouseButton, modifier, player);
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        ItemStack held = player.getHeldItem();
        if (PortableItem.matchesPortableID(held, portableID)) {
            type.saveInventory(held, this.chestInventory);
        }
    }

    @Override
    public void detectAndSendChanges() {
        if (crafters.isEmpty()) return;
        ICrafting bCrafting;
        for (int i = 0; i < inventorySlots.size(); ++i) {
            ItemStack itemstack = inventorySlots.get(i)
                .getStack();
            ItemStack itemstack1 = inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
                itemstack1 = itemstack == null ? null : itemstack.copy();
                inventoryItemStacks.set(i, itemstack1);

                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                nbtTagCompound.setShort("Slot", (short) i);
                if (itemstack1 != null) nbtTagCompound.setInteger("intCount", itemstack1.stackSize);
                for (ICrafting crafter : crafters) {
                    if ((bCrafting = crafter) instanceof EntityPlayerMP) {
                        network.sendTo(new PortableInfinityChestSyncPacket(itemstack1, i), (EntityPlayerMP) bCrafting);
                    }
                }
            }
        }
    }

    public void syncData(final ItemStack itemStack, final int slot, final int stackSize) {
        if (slot < 0 || slot >= inventorySlots.size()) return;
        if (itemStack != null) {
            itemStack.stackSize = stackSize;
            inventorySlots.get(slot)
                .putStack(itemStack);
        } else inventorySlots.get(slot)
            .putStack(null);
    }
}
