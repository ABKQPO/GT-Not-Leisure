package com.science.gtnl.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Lets an item be stored in the Yog-Sothoth terminal without implementing AE's {@code IWirelessTermHandler}, and
 * lets any stored terminal open its screen while it stays inside.
 */
public interface IYogTerminal {

    default boolean canStoreInYog(ItemStack stack) {
        return true;
    }

    /**
     * Opens this terminal while it stays inside a Yog-Sothoth terminal, server side.
     *
     * @param slot AE player slot index of the stack: {@code Platform.baublesSlotsOffset} plus its virtual bauble
     *             slot, resolvable through {@code BaublesApi.getBaubles(player)}
     * @return false to fall back to {@code onItemRightClick} with AE's GUI opening redirected to that slot
     */
    default boolean openFromSlot(ItemStack stack, EntityPlayer player, int slot) {
        return false;
    }
}
