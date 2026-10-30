package com.gtnllib.utils;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayerMP;

/**
 * The creative inventory screen keeps the cursor stack on the client only; the server copy stays empty. Sending it
 * would wipe whatever the player holds there. Other screens over the player's container keep a server cursor.
 */
public class CreativeCursor {

    private static final Set<UUID> CREATIVE_SCREENS = ConcurrentHashMap.newKeySet();

    public static void setCreativeScreen(EntityPlayerMP player, boolean open) {
        if (open) CREATIVE_SCREENS.add(player.getUniqueID());
        else CREATIVE_SCREENS.remove(player.getUniqueID());
    }

    public static void clear(UUID player) {
        CREATIVE_SCREENS.remove(player);
    }

    public static boolean clientOwned(EntityPlayerMP player) {
        return player.theItemInWorldManager.isCreative() && player.openContainer == player.inventoryContainer
            && CREATIVE_SCREENS.contains(player.getUniqueID());
    }

    public static void sync(EntityPlayerMP player) {
        if (!clientOwned(player)) player.updateHeldItem();
    }

    public static void handBack(EntityPlayerMP player) {
        player.updateHeldItem();
        player.inventory.setItemStack(null);
    }

    /** Sends the cursor produced by an action that adopted the client's cursor, then hands it back to the client. */
    public static void returnToClient(EntityPlayerMP player) {
        player.updateHeldItem();
        if (clientOwned(player)) player.inventory.setItemStack(null);
    }
}
