package com.science.gtnl.client.railgun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.science.gtnl.common.packet.RailgunBeamUpdatePacket;
import com.science.gtnl.common.railgun.RailgunVec;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class RailgunBeamRenderer {

    private record Shot(int shooterId, RailgunVec from, RailgunVec to, boolean ehv, long until) {}

    private static final List<Shot> SHOTS = new ArrayList<>();

    private record BeamState(RailgunVec from, RailgunVec to, boolean ehv, long lastUpdateTick) {}

    private static final Map<Integer, BeamState> ACTIVE = new HashMap<>();
    private static final Map<Integer, Long> LAST_ARC = new HashMap<>();
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
        LAST_ARC.clear();
        localRequested = false;
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        SHOTS.removeIf(shot -> shot.until <= world.getTotalWorldTime());
        ACTIVE.entrySet()
            .removeIf(entry -> world.getTotalWorldTime() - entry.getValue().lastUpdateTick > 6);
        LAST_ARC.keySet()
            .retainAll(ACTIVE.keySet());
        ACTIVE.forEach((id, state) -> {
            if (world.getTotalWorldTime() - LAST_ARC.getOrDefault(id, -100L) < 3) return;
            LAST_ARC.put(id, world.getTotalWorldTime());
            RailgunVec point = state.from.add(
                state.to.subtract(state.from)
                    .scale(world.rand.nextDouble()));
            RailgunVec end = point.add(
                (world.rand.nextDouble() - 0.5) * 2,
                (world.rand.nextDouble() - 0.5) * 2,
                (world.rand.nextDouble() - 0.5) * 2);
            RailgunArcRenderer.spawnChain(point, end, 4, state.ehv);
        });
        if (SHOTS.isEmpty() && ACTIVE.isEmpty()) return;
        RailgunRenderHelper.render(() -> {
            for (Shot shot : SHOTS) {
                RailgunVec from = RailgunVisuals.muzzle(shot.shooterId, shot.from, event.partialTicks);
                beam(from, shot.to, shot.ehv, world.getTotalWorldTime() + event.partialTicks);
            }
            ACTIVE.forEach(
                (id, beam) -> beam(
                    RailgunVisuals.muzzle(id, beam.from, event.partialTicks),
                    beam.to,
                    beam.ehv,
                    world.getTotalWorldTime() + event.partialTicks));
        });
    }

    private static void beam(RailgunVec from, RailgunVec to, boolean ehv, double time) {
        RailgunVec delta = to.subtract(from), direction = delta.normalize();
        if (delta.length() < 0.001 || delta.length() > 512) return;
        RailgunVec reference = Math.abs(direction.y()) > 0.95 ? new RailgunVec(1, 0, 0) : new RailgunVec(0, 1, 0);
        RailgunVec right = direction.cross(reference)
            .normalize(),
            up = direction.cross(right)
                .normalize();
        double breathing = 1 + Math.sin(time * 0.30) * 0.08;
        double[] radii = { 0.20, 0.11, 0.038 }, speeds = { 0.18, -0.32, 0.55 };
        float red = ehv ? 1 : 0.25F, green = ehv ? 0.3F : 0.75F, blue = ehv ? 0.6F : 1;
        for (int layer = 0; layer < 3; layer++) {
            float mix = layer / 2.0F;
            float r = red + ((ehv ? 1 : 0.85F) - red) * mix;
            float g = green + ((ehv ? 0.8F : 0.97F) - green) * mix;
            float b = blue + ((ehv ? 0.9F : 1) - blue) * mix;
            for (int segment = 0; segment < 12; segment++) {
                double a = segment / 12.0, end = (segment + 1) / 12.0;
                RailgunVec startPoint = from.add(delta.scale(a)), endPoint = from.add(delta.scale(end));
                double rotation = time * speeds[layer] + a * Math.PI * 2;
                double nextRotation = time * speeds[layer] + end * Math.PI * 2;
                double radius = radii[layer] * breathing;
                float flow = (float) (0.75 + 0.25 * Math.sin(time * 0.22 - segment * 0.8));
                Tessellator.instance.setColorRGBA_F(r, g, b, (0.3F + layer * 0.3F) * flow);
                for (int face = 0; face < 4; face++) {
                    double angle = face * Math.PI / 2, next = (face + 1) * Math.PI / 2;
                    RailgunRenderHelper.vertex(corner(startPoint, right, up, rotation + angle, radius));
                    RailgunRenderHelper.vertex(corner(startPoint, right, up, rotation + next, radius));
                    RailgunRenderHelper.vertex(corner(endPoint, right, up, nextRotation + next, radius));
                    RailgunRenderHelper.vertex(corner(endPoint, right, up, nextRotation + angle, radius));
                }
            }
        }
        RailgunRenderHelper.flare(to, 0.35 * breathing, red, green, blue, 0.45F);
        RailgunRenderHelper.flare(to, 0.12 * breathing, ehv ? 1 : 0.85F, ehv ? 0.8F : 0.97F, ehv ? 0.9F : 1, 0.9F);
    }

    private static RailgunVec corner(RailgunVec center, RailgunVec right, RailgunVec up, double angle, double radius) {
        return center.add(right.scale(Math.cos(angle) * radius))
            .add(up.scale(Math.sin(angle) * radius));
    }
}
