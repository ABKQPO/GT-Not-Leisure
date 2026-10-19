package com.science.gtnl.container.portableWorkbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.item.items.PortableItem;

public interface PortableStorageHost {

    /**
     * 宿主是否仍然有效；无效时容器会被关闭。
     */
    boolean valid(EntityPlayer player);

    /**
     * 把容器当前内容写回宿主。
     */
    void save(EntityPlayer player, IInventory inventory);

    /**
     * 容器是否由背包模块打开。
     */
    default boolean fromBackpack() {
        return false;
    }

    static PortableStorageHost held(ItemStack stack) {
        String id = PortableItem.ensurePortableID(stack);
        PortableItem.PortableType type = PortableItem.getPortableType(stack);
        return new PortableStorageHost() {

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
