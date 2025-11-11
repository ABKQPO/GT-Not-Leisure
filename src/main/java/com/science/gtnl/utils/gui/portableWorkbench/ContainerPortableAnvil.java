package com.science.gtnl.utils.gui.portableWorkbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.item.items.PortableItem;
import com.science.gtnl.mixins.early.Minecraft.AccessorContainerRepair;

public class ContainerPortableAnvil extends ContainerRepair {

    public ContainerPortableAnvil(InventoryPlayer playerInv, EntityPlayer player) {
        super(playerInv, player.worldObj, 0, 0, 0, player);

        AccessorContainerRepair accessor = (AccessorContainerRepair) this;

        this.inventorySlots.set(0, new Slot(accessor.getInputSlots(), 0, 27, 47) {

            @Override
            public boolean isItemValid(ItemStack stack) {
                return !(stack != null && stack.getItem() instanceof PortableItem && stack.getItemDamage() == 3);
            }
        });

        this.inventorySlots.set(1, new Slot(accessor.getInputSlots(), 1, 76, 47) {

            @Override
            public boolean isItemValid(ItemStack stack) {
                return !(stack != null && stack.getItem() instanceof PortableItem && stack.getItemDamage() == 3);
            }
        });

        this.inventorySlots.set(2, new Slot(accessor.getOutputSlots(), 2, 134, 47) {

            /**
             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
             */
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            /**
             * Return whether this slot's stack can be taken from this slot.
             */
            @Override
            public boolean canTakeStack(EntityPlayer entityPlayer) {
                return (entityPlayer.capabilities.isCreativeMode || entityPlayer.experienceLevel >= maximumCost)
                    && maximumCost > 0
                    && this.getHasStack();
            }

            @Override
            public void onPickupFromSlot(EntityPlayer entityPlayer, ItemStack itemStack) {
                if (!entityPlayer.capabilities.isCreativeMode) {
                    entityPlayer.addExperienceLevel(-maximumCost);
                }

                accessor.getInputSlots()
                    .setInventorySlotContents(0, null);

                if (stackSizeToBeUsedInRepair > 0) {
                    ItemStack input = accessor.getInputSlots()
                        .getStackInSlot(1);

                    if (input != null && input.stackSize > stackSizeToBeUsedInRepair) {
                        input.stackSize -= stackSizeToBeUsedInRepair;
                        accessor.getInputSlots()
                            .setInventorySlotContents(1, input);
                    } else {
                        accessor.getInputSlots()
                            .setInventorySlotContents(1, null);
                    }
                } else {
                    accessor.getInputSlots()
                        .setInventorySlotContents(1, null);
                }

                maximumCost = 0;
            }
        });
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        ItemStack held = player.getHeldItem();
        return held != null && held.getItem() instanceof PortableItem && held.getItemDamage() == 3;
    }
}
