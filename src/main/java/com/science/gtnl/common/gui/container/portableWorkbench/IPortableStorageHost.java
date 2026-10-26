package com.science.gtnl.common.gui.container.portableWorkbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.item.items.PortableItem;

public interface IPortableStorageHost {

    /**
     * Whether the host is still valid; the container closes once it is not.
     */
    boolean valid(EntityPlayer player);

    /**
     * Writes the container's current contents back to the host.
     */
    void save(EntityPlayer player, IInventory inventory);

    /**
     * Whether the container was opened from the backpack module.
     */
    default boolean fromBackpack() {
        return false;
    }

    static IPortableStorageHost held(ItemStack stack) {
        String id = PortableItem.ensurePortableID(stack);
        PortableItem.PortableType type = PortableItem.getPortableType(stack);
        return new IPortableStorageHost() {

            @Override
            public boolean valid(EntityPlayer player) {
                return PortableItem.matchesPortableID(player.getHeldItem(), id);
            }

            @Override
            public void save(EntityPlayer player, IInventory inventory) {
                ItemStack held = player.getHeldItem();
                if (type != null && PortableItem.matchesPortableID(held, id)) type.saveInventory(held, inventory);
            }
        };
    }
}
