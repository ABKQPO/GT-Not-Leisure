package com.science.gtnl.common.item.items.bauble;

import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class DraconicArmorProjectionState {

    private static final Map<UUID, DraconicArmorProjectionType> STATES = new Object2ObjectOpenHashMap<>();

    public static void set(EntityPlayer player, DraconicArmorProjectionType type) {
        if (player == null || type == null) {
            return;
        }
        STATES.put(player.getUniqueID(), type);
    }

    public static DraconicArmorProjectionType get(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        return STATES.get(player.getUniqueID());
    }

    public static void clear(EntityPlayer player) {
        if (player == null) {
            return;
        }
        STATES.remove(player.getUniqueID());
    }

    public static void clear(UUID playerId) {
        if (playerId == null) {
            return;
        }
        STATES.remove(playerId);
    }
}
