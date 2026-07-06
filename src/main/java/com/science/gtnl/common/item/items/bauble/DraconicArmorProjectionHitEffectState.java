package com.science.gtnl.common.item.items.bauble;

import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;

public class DraconicArmorProjectionHitEffectState {

    private static final int DEFAULT_DURATION_TICKS = 6;
    private static final Map<UUID, HitEffectState> STATES = new Object2ObjectOpenHashMap<>();

    private DraconicArmorProjectionHitEffectState() {}

    public static void trigger(EntityPlayer player, float shieldPower) {
        if (player == null) {
            return;
        }
        STATES.put(player.getUniqueID(), new HitEffectState(Math.max(0.0F, shieldPower), DEFAULT_DURATION_TICKS));
    }

    public static boolean isActive(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        HitEffectState state = STATES.get(player.getUniqueID());
        return state != null && state.remainingTicks > 0;
    }

    public static float getShieldPower(EntityPlayer player) {
        HitEffectState state = get(player);
        return state == null ? 0.0F : state.getShieldPower();
    }

    public static int getRemainingTicks(EntityPlayer player) {
        HitEffectState state = get(player);
        return state == null ? 0 : state.getRemainingTicks();
    }

    public static HitEffectState get(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        return STATES.get(player.getUniqueID());
    }

    public static void tick() {
        if (STATES.isEmpty()) {
            return;
        }
        STATES.values()
            .removeIf(state -> --state.remainingTicks <= 0);
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

    public static void clearAll() {
        STATES.clear();
    }

    @Getter
    public static class HitEffectState {

        private final float shieldPower;
        private int remainingTicks;

        public HitEffectState(float shieldPower, int remainingTicks) {
            this.shieldPower = shieldPower;
            this.remainingTicks = remainingTicks;
        }

    }
}
