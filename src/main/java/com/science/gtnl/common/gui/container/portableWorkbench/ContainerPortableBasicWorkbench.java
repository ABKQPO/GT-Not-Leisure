package com.science.gtnl.common.gui.container.portableWorkbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.science.gtnl.common.item.items.PortableItem;

public class ContainerPortableBasicWorkbench extends ContainerWorkbench {

    private final String portableID;

    public ContainerPortableBasicWorkbench(EntityPlayer player, World world, ItemStack stack) {
        super(player.inventory, world, 0, 0, 0);
        this.portableID = PortableItem.ensurePortableID(stack);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        ItemStack held = player.getHeldItem();
        return PortableItem.matchesPortableID(held, portableID);
    }
}
