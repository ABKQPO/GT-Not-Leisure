package com.science.gtnl.container.portableWorkbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerPortableCompressedChest extends ContainerPortableAvaritiaddonsChest {

    public ContainerPortableCompressedChest(ItemStack stack, InventoryPlayer playerInv) {
        super(stack, playerInv, false);
    }

    public ContainerPortableCompressedChest(ItemStack stack, InventoryPlayer playerInv, PortableStorageHost host) {
        super(stack, playerInv, false, host);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return host.valid(player);
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        host.save(player, chestInventory);
    }
}
