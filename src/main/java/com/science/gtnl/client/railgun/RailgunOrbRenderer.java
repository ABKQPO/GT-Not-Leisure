package com.science.gtnl.client.railgun;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.science.gtnl.client.railgun.RailgunGlowSprites.Sprite;
import com.science.gtnl.client.railgun.shader.RailgunShaderEffect;
import com.science.gtnl.client.railgun.shader.RailgunShaders;
import com.science.gtnl.common.railgun.RailgunChargeTier;
import com.science.gtnl.common.railgun.RailgunVec;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RailgunOrbRenderer {

    private static final int MIN_TRAVEL_TICKS = 3;
    private static final int MAX_TRAVEL_TICKS = 8;
    private static final double BLOCKS_PER_TICK = 22;
    private static final int MAX_ORBS = 32;
    private static final int TRAIL_SEGMENTS = 7;
    private static final int FILAMENTS = 3;

    private static final double ORB_MIN_RADIUS = 0.07;
    private static final double ORB_CHARGE_RADIUS = 0.12;
    private static final double ORB_TIER_RADIUS = 0.055;
    private static final double ORB_MUZZLE_GAP = 0.28;
    private static final double FLIGHT_SHRINK = 0.05;
    private static final float LAUNCHED_STRENGTH = 1F;

    private static final float[] HIGH_INNER = { 0.35F, 0.86F, 1F };
    private static final float[] HIGH_OUTER = { 0.94F, 1F, 1F };
    private static final float[] EXTREME_INNER = { 1F, 0.42F, 0.68F };
    private static final float[] EXTREME_OUTER = { 1F, 0.94F, 1F };

    private record LaunchedOrb(int shooterId, RailgunVec fallback, RailgunVec target, int tier, long born,
        int travelTicks, Runnable onArrive) {}

    private record OrbViz(RailgunVec center, RailgunVec direction, RailgunVec tail, double radius, int tier, int seed,
        double time, float strength) {}

    private static final List<LaunchedOrb> ORBS = new ArrayList<>();

    public static void launch(int shooterId, RailgunVec fallbackFrom, RailgunVec target, int tier, Runnable onArrive) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        if (fallbackFrom == null || !fallbackFrom.finite() || target == null || !target.finite()) return;
        if (!MainConfig.client.enableRailgunBloom) {
            if (onArrive != null) onArrive.run();
            return;
        }
        if (ORBS.size() >= MAX_ORBS) ORBS.remove(0);
        ORBS.add(
            new LaunchedOrb(
                shooterId,
                fallbackFrom,
                target,
                tier,
                world.getTotalWorldTime(),
                travelTicks(fallbackFrom, target),
                onArrive));
    }

    public static int travelTicks(RailgunVec from, RailgunVec to) {
        double squared = from.distanceToSqr(to);
        if (squared <= 0) return MIN_TRAVEL_TICKS;
        double ticks = Math.sqrt(squared) / BLOCKS_PER_TICK;
        return Math.max(MIN_TRAVEL_TICKS, Math.min(MAX_TRAVEL_TICKS, (int) Math.round(ticks)));
    }

    public static void clear() {
        ORBS.clear();
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        long now = world.getTotalWorldTime();
        if (MainConfig.client.enableRailgunBloom) {
            List<OrbViz> orbs = collect(now, event.partialTicks);
            if (!orbs.isEmpty()) {
                RailgunRenderHelper.renderGlow(Sprite.STREAK, () -> {
                    for (OrbViz orb : orbs) trail(orb);
                    for (OrbViz orb : orbs) filaments(orb);
                });
                RailgunRenderHelper.renderGlow(Sprite.RADIAL, () -> { for (OrbViz orb : orbs) outerHalo(orb); });
                if (!drawBody(orbs, now + event.partialTicks))
                    RailgunRenderHelper.renderGlow(Sprite.RADIAL, () -> { for (OrbViz orb : orbs) innerHalo(orb); });
            }
        }
        arrive(now);
    }

    private static boolean drawBody(List<OrbViz> orbs, double time) {
        if (!MainConfig.client.enableRailgunShaders) return false;
        RailgunShaderEffect shader = RailgunShaders.ORB;
        if (!shader.begin()) return false;
        try {
            shader.uniform1f("time", (float) (time * 0.05));
            RailgunRenderHelper.render(() -> { for (OrbViz orb : orbs) body(orb); });
            return true;
        } finally {
            shader.end();
        }
    }

    private static void body(OrbViz orb) {
        setColor(orb.tier(), 0.2F, orb.strength());
        RailgunRenderHelper.glow(orb.center(), orb.radius() * 1.35, (float) (orb.time() * 0.8 + orb.seed()));
    }

    private static void arrive(long now) {
        for (int index = ORBS.size() - 1; index >= 0; index--) {
            LaunchedOrb orb = ORBS.get(index);
            if (now < orb.born + orb.travelTicks) continue;
            ORBS.remove(index);
            if (orb.onArrive != null) orb.onArrive.run();
        }
    }

    private static List<OrbViz> collect(long now, float partialTicks) {
        List<OrbViz> orbs = new ArrayList<>(ORBS.size() + 1);
        OrbViz charging = charging(now, partialTicks);
        if (charging != null) orbs.add(charging);
        for (LaunchedOrb orb : ORBS) {
            OrbViz launched = launched(orb, now, partialTicks);
            if (launched != null) orbs.add(launched);
        }
        return orbs;
    }

    private static OrbViz charging(long now, float partialTicks) {
        var player = Minecraft.getMinecraft().thePlayer;
        if (!RailgunChargeTracker.charging(player)) return null;
        float progress = RailgunChargeTracker.progress(player);
        RailgunVec direction = RailgunVec.look(player);
        RailgunVec muzzle = RailgunVisuals.muzzle(player.getEntityId(), RailgunVec.pos(player), partialTicks)
            .add(direction.scale(ORB_MUZZLE_GAP * (1 - progress)));
        double radius = ORB_MIN_RADIUS + ORB_CHARGE_RADIUS * progress * progress;
        return new OrbViz(
            muzzle,
            direction,
            null,
            radius,
            RailgunChargeTracker.tier(player)
                .ordinal(),
            player.getEntityId(),
            now + partialTicks,
            0.35F + 0.65F * progress);
    }

    private static OrbViz launched(LaunchedOrb orb, long now, float partialTicks) {
        double raw = (now - orb.born + partialTicks) / orb.travelTicks;
        if (raw < 0) return null;
        double eased = 1 - (1 - Math.min(1, raw)) * (1 - Math.min(1, raw));
        RailgunVec from = RailgunVisuals.muzzle(orb.shooterId, orb.fallback, partialTicks);
        RailgunVec delta = orb.target.subtract(from);
        if (delta.length() < 1.0E-4) return null;
        RailgunVec head = from.add(delta.scale(eased));
        double radius = ORB_MIN_RADIUS + ORB_TIER_RADIUS * orb.tier - FLIGHT_SHRINK * eased;
        return new OrbViz(
            head,
            delta.normalize(),
            from,
            radius,
            orb.tier,
            orb.shooterId,
            now + partialTicks,
            LAUNCHED_STRENGTH);
    }

    private static void outerHalo(OrbViz orb) {
        setColor(orb.tier(), 0F, 0.32F);
        RailgunRenderHelper.glow(orb.center(), orb.radius() * 2.8, (float) (orb.time() * 0.8 + orb.seed()));
    }

    private static void innerHalo(OrbViz orb) {
        double radius = orb.radius();
        float radians = (float) (orb.time() * 0.8 + orb.seed());
        setColor(orb.tier(), 0.5F, 0.6F);
        RailgunRenderHelper.glow(orb.center(), radius * 1.6, -radians);
        setColor(orb.tier(), 1F, 1F);
        RailgunRenderHelper.glow(orb.center(), radius, radians);
    }

    private static void trail(OrbViz orb) {
        RailgunVec tail = orb.tail();
        if (tail == null) return;
        RailgunVec delta = orb.center()
            .subtract(tail);
        for (int segment = 0; segment < TRAIL_SEGMENTS; segment++) {
            double start = segment / (double) TRAIL_SEGMENTS, end = (segment + 1) / (double) TRAIL_SEGMENTS;
            double width = orb.radius() * (0.15 + 1.5 * start * start);
            double nextWidth = orb.radius() * (0.15 + 1.5 * end * end);
            setColor(orb.tier(), (float) start, (float) (0.12 + 0.85 * start * start));
            RailgunRenderHelper.glowRibbon(tail.add(delta.scale(start)), tail.add(delta.scale(end)), width, nextWidth);
        }
    }

    private static void filaments(OrbViz orb) {
        RailgunVec direction = orb.direction();
        RailgunVec reference = Math.abs(direction.y()) > 0.95 ? new RailgunVec(1, 0, 0) : new RailgunVec(0, 1, 0);
        RailgunVec right = direction.cross(reference)
            .normalize();
        RailgunVec up = direction.cross(right)
            .normalize();
        double reach = orb.radius() * 2.6, width = Math.max(0.015, orb.radius() * 0.12);
        setColor(orb.tier(), 0.7F, 0.55F);
        for (int filament = 0; filament < FILAMENTS; filament++) {
            double angle = orb.time() * 1.9 + filament * (Math.PI * 2 / FILAMENTS) + orb.seed();
            RailgunVec offset = right.scale(Math.cos(angle) * orb.radius())
                .add(up.scale(Math.sin(angle) * orb.radius()));
            RailgunRenderHelper.glowRibbon(
                orb.center()
                    .subtract(direction.scale(reach))
                    .add(offset),
                orb.center()
                    .add(direction.scale(reach))
                    .subtract(offset),
                width,
                width);
        }
    }

    private static void setColor(int tier, float mix, float alpha) {
        float[] inner = tier >= RailgunChargeTier.EHV3.ordinal() ? EXTREME_INNER : HIGH_INNER;
        float[] outer = tier >= RailgunChargeTier.EHV3.ordinal() ? EXTREME_OUTER : HIGH_OUTER;
        Tessellator.instance.setColorRGBA_F(
            inner[0] + (outer[0] - inner[0]) * mix,
            inner[1] + (outer[1] - inner[1]) * mix,
            inner[2] + (outer[2] - inner[2]) * mix,
            alpha);
    }
}
