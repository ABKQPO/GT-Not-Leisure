package com.science.gtnl.container.portableWorkbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.celestweave.CelestweaveBackpack;
import com.science.gtnl.common.item.items.PortableItem;

public class ContainerPortableEnderChest extends ContainerChest {

    public final PortableStorageHost host;

    public ContainerPortableEnderChest(InventoryPlayer playerInv, IInventory enderInv) {
        this(playerInv, enderInv, null);
    }

    public ContainerPortableEnderChest(InventoryPlayer playerInv, IInventory enderInv, PortableStorageHost host) {
        super(playerInv, enderInv);
        this.host = host;

        for (int i = 0; i < this.inventorySlots.size(); i++) {
            final Slot oldSlot = this.inventorySlots.get(i);
            Slot newSlot = new Slot(
                oldSlot.inventory,
                oldSlot.getSlotIndex(),
                oldSlot.xDisplayPosition,
                oldSlot.yDisplayPosition) {

                @Override
                public boolean isItemValid(ItemStack stack) {
                    if (stack != null && stack.getItem() instanceof PortableItem && stack.getItemDamage() == 4) {
                        return false;
                    }
                    if (CelestweaveBackpack.blockedInStorage(stack)) return false;
                    return super.isItemValid(stack);
                }
            };
            newSlot.slotNumber = oldSlot.slotNumber;
            this.inventorySlots.set(i, newSlot);
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        if (index >= getLowerChestInventory().getSizeInventory() && index < inventorySlots.size()) {
            Slot slot = inventorySlots.get(index);
            if (slot != null && slot.getHasStack()
                && !inventorySlots.get(0)
                    .isItemValid(slot.getStack()))
                return null;
        }
        return super.transferStackInSlot(player, index);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        if (host != null) return host.valid(player);
        ItemStack held = player.getHeldItem();
        return held != null && held.getItemDamage() == 4 && held.getItem() instanceof PortableItem;
    }
}
