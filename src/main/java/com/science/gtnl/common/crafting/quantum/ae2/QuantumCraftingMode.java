package com.science.gtnl.common.crafting.quantum.ae2;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayer;

import org.jetbrains.annotations.Nullable;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.PlayerSource;

public class QuantumCraftingMode {

    private static final Set<UUID> ENABLED_PLAYERS = ConcurrentHashMap.newKeySet();

    public static void setEnabled(EntityPlayer player, boolean enabled) {
        if (enabled) {
            ENABLED_PLAYERS.add(player.getUniqueID());
        } else {
            ENABLED_PLAYERS.remove(player.getUniqueID());
        }
    }

    public static boolean isEnabled(@Nullable BaseActionSource source) {
        return source instanceof PlayerSource playerSource && playerSource.player != null
            && ENABLED_PLAYERS.contains(playerSource.player.getUniqueID());
    }

    public static boolean shouldUse(@Nullable IGrid grid, @Nullable BaseActionSource source) {
        return isEnabled(source) && isAvailable(grid);
    }

    public static boolean isAvailable(@Nullable IGrid grid) {
        if (grid == null) {
            return false;
        }
        final ICraftingGrid craftingGrid = grid.getCache(ICraftingGrid.class);
        if (craftingGrid == null) {
            return false;
        }
        for (ICraftingCPU cpu : craftingGrid.getCpus()) {
            if (QuantumCraftingJob.isQuantumCPU(cpu)) {
                return true;
            }
        }
        return false;
    }

    public static void onPlayerLoggedOut(EntityPlayer player) {
        ENABLED_PLAYERS.remove(player.getUniqueID());
    }

    public static void clear() {
        ENABLED_PLAYERS.clear();
    }
}
