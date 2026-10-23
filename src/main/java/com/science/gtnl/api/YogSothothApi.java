package com.science.gtnl.api;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.yogsothoth.YogTerminals;
import com.science.gtnl.common.yogsothoth.YogToken;

/**
 * Stored terminals are also exposed as virtual slots of {@code BaublesApi.getBaubles(player)}, so mods that look for
 * terminals there need nothing else; mods that only scan the main inventory can use these.
 */
public final class YogSothothApi {

    /** An AE wireless terminal handler or an {@link IYogTerminal}. */
    public static boolean isTerminal(ItemStack stack) {
        return YogToken.isStorable(stack);
    }

    /** Terminals stored in every Yog-Sothoth terminal the player carries or wears, on the calling side. */
    public static List<ItemStack> storedTerminals(EntityPlayer player) {
        return YogTerminals.carried(player);
    }

    /** AE player slot index of a stored terminal (bauble offset based), or -1. */
    public static int slotOf(EntityPlayer player, ItemStack terminal) {
        return YogTerminals.slotOf(player, terminal);
    }

    /** The stored terminal whose screen the player opened from a Yog-Sothoth terminal, or null. */
    public static ItemStack openedTerminal(EntityPlayer player) {
        return YogTerminals.opened(player);
    }
}
