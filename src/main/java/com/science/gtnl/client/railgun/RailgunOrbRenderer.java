package com.science.gtnl.client.railgun;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.science.gtnl.client.railgun.RailgunRenderHelper.Sprite;
import com.science.gtnl.client.railgun.shader.RailgunShaderEffect;
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
    private static final int FILAMENT_SEGMENTS = 8;
    private static final int SPHERE_LONGITUDES = 24;
    private static final int SPHERE_LATITUDES = 12;
    private static final double SPHERE_RIM = 1.35;

    private static final float[] COLOR = new float[3];

    private static final double ORB_MIN_RADIUS = 0.07;
    private static final double ORB_CHARGE_RADIUS = 0.12;
    private static final double ORB_TIER_RADIUS = 0.055;
    private static final double ORB_MUZZLE_GAP = 0.28;
    private static final double FLIGHT_SHRINK = 0.05;
    private static final float LAUNCHED_STRENGTH = 1F;

    private static final double[] SHELL_RADII = { 1, 0.68, 0.42 };
    private static final double[] SHELL_MIX = { 0.15, 0.55, 1 };
    private static final float[] SHELL_ALPHA = { 0.62F, 0.85F, 1F };

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
                drawBody(orbs, now + event.partialTicks);
                RailgunRenderHelper.renderGlow(Sprite.RADIAL, () -> { for (OrbViz orb : orbs) innerHalo(orb); });
            }
        }
        arrive(now);
    }

    private static void drawBody(List<OrbViz> orbs, double time) {
        if (MainConfig.client.enableRailgunShaders) {
            RailgunShaderEffect shader = RailgunShaderEffect.ORB;
            if (shader.begin()) {
                try {
                    shader.uniform1f("time", (float) (time * 0.05));
                    RailgunRenderHelper.render(() -> { for (OrbViz orb : orbs) shells(orb); });
                } finally {
                    shader.end();
                }
                return;
            }
        }
        RailgunRenderHelper.render(() -> { for (OrbViz orb : orbs) shells(orb); });
    }

    private static void shells(OrbViz orb) {
        for (int shell = 0; shell < SHELL_RADII.length; shell++) {
            float[] rgb = mix(orb.tier(), (float) SHELL_MIX[shell]);
            RailgunRenderHelper.sphere(
                orb.center(),
                orb.radius() * SHELL_RADII[shell],
                SPHERE_LONGITUDES,
                SPHERE_LATITUDES,
                rgb[0],
                rgb[1],
                rgb[2],
                SHELL_ALPHA[shell] * orb.strength(),
                SPHERE_RIM);
        }
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
        if (!RailgunCharge.charging(player)) return null;
        float progress = RailgunCharge.progress(player);
        RailgunVec direction = RailgunVec.look(player, partialTicks);
        RailgunVec muzzle = RailgunRenderHelper.muzzle(player.getEntityId(), RailgunVec.pos(player), partialTicks)
            .add(direction.scale(ORB_MUZZLE_GAP * (1 - progress)));
        double radius = ORB_MIN_RADIUS + ORB_CHARGE_RADIUS * progress * progress;
        return new OrbViz(
            muzzle,
            direction,
            null,
            radius,
            RailgunCharge.tier(player)
                .ordinal(),
            player.getEntityId(),
            now + partialTicks,
            0.35F + 0.65F * progress);
    }

    private static OrbViz launched(LaunchedOrb orb, long now, float partialTicks) {
        double raw = (now - orb.born + partialTicks) / orb.travelTicks;
        if (raw < 0) return null;
        double eased = 1 - (1 - Math.min(1, raw)) * (1 - Math.min(1, raw));
        RailgunVec from = RailgunRenderHelper.muzzle(orb.shooterId, orb.fallback, partialTicks);
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
        setColor(orb.tier(), 0F, 0.62F);
        RailgunRenderHelper.glow(orb.center(), orb.radius() * 3.6, (float) (orb.time() * 0.8 + orb.seed()));
    }

    private static void innerHalo(OrbViz orb) {
        double radius = orb.radius();
        float radians = (float) (orb.time() * 0.8 + orb.seed());
        setColor(orb.tier(), 0.5F, 0.9F);
        RailgunRenderHelper.glow(orb.center(), radius * 2.1, -radians);
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
            double width = orb.radius() * (0.02 + 1.6 * start * start);
            double nextWidth = orb.radius() * (0.02 + 1.6 * end * end);
            setColor(orb.tier(), (float) start, (float) (0.22 + 0.95 * start * start));
            RailgunRenderHelper.glowRibbon(tail.add(delta.scale(start)), tail.add(delta.scale(end)), width, nextWidth);
        }
    }

    private static void filaments(OrbViz orb) {
        RailgunVec direction = orb.direction();
        RailgunVec right = RailgunVec.rightOf(direction);
        RailgunVec up = right.cross(direction)
            .normalize();
        double reach = orb.radius() * 2.6, width = Math.max(0.02, orb.radius() * 0.26);
        float[] rgb = mix(orb.tier(), 0.7F);
        for (int filament = 0; filament < FILAMENTS; filament++) {
            double angle = orb.time() * 1.9 + filament * (Math.PI * 2 / FILAMENTS) + orb.seed();
            RailgunVec offset = right.scale(Math.cos(angle) * orb.radius())
                .add(up.scale(Math.sin(angle) * orb.radius()));
            RailgunRenderHelper.taperedRibbon(
                orb.center()
                    .subtract(direction.scale(reach))
                    .add(offset),
                orb.center()
                    .add(direction.scale(reach))
                    .subtract(offset),
                width,
                FILAMENT_SEGMENTS,
                rgb[0],
                rgb[1],
                rgb[2],
                0.8F);
        }
    }

    private static void setColor(int tier, float mix, float alpha) {
        float[] rgb = mix(tier, mix);
        Tessellator.instance.setColorRGBA_F(rgb[0], rgb[1], rgb[2], alpha);
    }

    private static float[] mix(int tier, float mix) {
        float[] inner = tier >= RailgunChargeTier.EHV3.ordinal() ? EXTREME_INNER : HIGH_INNER;
        float[] outer = tier >= RailgunChargeTier.EHV3.ordinal() ? EXTREME_OUTER : HIGH_OUTER;
        COLOR[0] = inner[0] + (outer[0] - inner[0]) * mix;
        COLOR[1] = inner[1] + (outer[1] - inner[1]) * mix;
        COLOR[2] = inner[2] + (outer[2] - inner[2]) * mix;
        return COLOR;
    }
}
