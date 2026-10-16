package com.science.gtnl.client.railgun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.science.gtnl.common.packet.RailgunBeamUpdatePacket;
import com.science.gtnl.common.railgun.RailgunVec;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class RailgunBeamRenderer {

    private record Shot(int shooterId, RailgunVec from, RailgunVec to, boolean ehv, long until) {}

    private static final List<Shot> SHOTS = new ArrayList<>();

    private record BeamState(RailgunVec from, RailgunVec to, boolean ehv, long lastUpdateTick) {}

    private static final Map<Integer, BeamState> ACTIVE = new HashMap<>();
    private static boolean localRequested;

    public static void setLocalRequested(boolean requested) {
        localRequested = requested;
        var player = Minecraft.getMinecraft().thePlayer;
        if (!requested && player != null) ACTIVE.remove(player.getEntityId());
    }

    public static void update(RailgunBeamUpdatePacket packet) {
        var mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return;
        if (!packet.active
            || mc.thePlayer != null && packet.shooterId == mc.thePlayer.getEntityId() && !localRequested) {
            ACTIVE.remove(packet.shooterId);
            return;
        }
        if (ACTIVE.size() < 64 || ACTIVE.containsKey(packet.shooterId)) ACTIVE
            .put(packet.shooterId, new BeamState(packet.from, packet.to, packet.ehv, mc.theWorld.getTotalWorldTime()));
    }

    public static boolean active(int shooterId) {
        var world = Minecraft.getMinecraft().theWorld;
        BeamState state = ACTIVE.get(shooterId);
        return world != null && state != null && world.getTotalWorldTime() - state.lastUpdateTick <= 6;
    }

    public static Set<Integer> shooters() {
        return new HashSet<>(ACTIVE.keySet());
    }

    public static void shot(int shooterId, RailgunVec from, RailgunVec to, boolean ehv) {
        if (SHOTS.size() >= 64) SHOTS.remove(0);
        SHOTS.add(new Shot(shooterId, from, to, ehv, Minecraft.getMinecraft().theWorld.getTotalWorldTime() + 6));
    }

    public static void clear() {
        SHOTS.clear();
        ACTIVE.clear();
        localRequested = false;
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        SHOTS.removeIf(shot -> shot.until <= world.getTotalWorldTime());
        ACTIVE.entrySet()
            .removeIf(entry -> world.getTotalWorldTime() - entry.getValue().lastUpdateTick > 6);
        if (SHOTS.isEmpty() && ACTIVE.isEmpty()) return;
        RailgunRenderHelper.render(() -> {
            for (Shot shot : SHOTS) {
                RailgunVec from = RailgunVisuals.muzzle(shot.shooterId, shot.from, event.partialTicks);
                beam(from, shot.to, shot.ehv);
            }
            ACTIVE.forEach(
                (id, beam) -> beam(RailgunVisuals.muzzle(id, beam.from, event.partialTicks), beam.to, beam.ehv));
        });
    }

    private static void beam(RailgunVec from, RailgunVec to, boolean ehv) {
        RailgunRenderHelper.ribbon(from, to, 0.2, ehv ? 1 : 0.25F, ehv ? 0.3F : 0.75F, ehv ? 0.6F : 1, 0.5F);
        RailgunRenderHelper.ribbon(from, to, 0.04, ehv ? 1 : 0.85F, ehv ? 0.8F : 0.97F, ehv ? 0.9F : 1, 1);
    }
}
