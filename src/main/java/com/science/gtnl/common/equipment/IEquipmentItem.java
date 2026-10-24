package com.science.gtnl.common.equipment;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public interface IEquipmentItem {

    /**
     * Runs on the server right before the settings GUI of the held equipment opens, for example to stop an
     * ongoing action of the item. Does nothing by default.
     */
    default void beforeSettingsOpen(EntityPlayerMP player, ItemStack stack) {}
}
