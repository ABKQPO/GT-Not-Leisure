package com.science.gtnl.client.railgun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.science.gtnl.common.railgun.RailgunVec;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RailgunShockwaveRenderer {

    private static final int MAX_ACTIVE = 64;
    private static final int RING_SEGMENTS = 64;
    private static final double MAX_RADIUS = 32;
    private static final double PLANE_OFFSET = 0.05;
    private static final double OCCLUSION_MARGIN = 0.05;
    private static final float RADIUS_JITTER = 0.095F;
    private static final float RING_WIDTH = 0.18F;
    private static final float RING_ALPHA = 0.85F;
    private static final float RIM_ALPHA = 0.15F;
    private static final float RED = 1F;
    private static final float GREEN = 0.5F;
    private static final float BLUE = 0.75F;

    private static final class Wave {

        final RailgunVec center;
        final double maxRadius;
        final int lifetime;
        final long born;
        final float red, green, blue;
        final boolean[] occluded = new boolean[RING_SEGMENTS];
        long occlusionTick = Long.MIN_VALUE;

        Wave(RailgunVec center, double maxRadius, int lifetime, long born, float red, float green, float blue) {
            this.center = center;
            this.maxRadius = maxRadius;
            this.lifetime = lifetime;
            this.born = born;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }

    private static final List<Wave> ACTIVE = new ArrayList<>();

    public static void spawn(RailgunVec center, double radius, int lifetime) {
        spawn(center, radius, lifetime, RED, GREEN, BLUE);
    }

    public static void spawn(RailgunVec center, double radius, int lifetime, int rgb) {
        spawn(center, radius, lifetime, (rgb >> 16 & 255) / 255F, (rgb >> 8 & 255) / 255F, (rgb & 255) / 255F);
    }

    private static void spawn(RailgunVec center, double radius, int lifetime, float red, float green, float blue) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null || center == null
            || !center.finite()
            || !Double.isFinite(radius)
            || radius <= 0
            || radius > MAX_RADIUS
            || lifetime <= 0) return;
        if (ACTIVE.size() >= MAX_ACTIVE) ACTIVE.remove(0);
        ACTIVE.add(new Wave(center, radius, lifetime, world.getTotalWorldTime(), red, green, blue));
    }

    public static void clear() {
        ACTIVE.clear();
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        long now = world.getTotalWorldTime();
        ACTIVE.removeIf(wave -> now - wave.born >= wave.lifetime);
        if (ACTIVE.isEmpty()) return;
        for (Wave wave : ACTIVE) {
            float progress = progress(wave, now, event.partialTicks);
            if (now != wave.occlusionTick)
                refreshOcclusion(world, wave, radius(wave, progress), width(wave, progress), progress);
            wave.occlusionTick = now;
        }
        RailgunRenderHelper.render(() -> {
            for (Wave wave : ACTIVE) {
                float progress = progress(wave, now, event.partialTicks);
                ring(wave, radius(wave, progress), width(wave, progress), progress, RING_ALPHA * (1 - progress));
            }
        });
    }

    private static float progress(Wave wave, long now, float partialTicks) {
        return Math.min(1F, (now - wave.born + partialTicks) / wave.lifetime);
    }

    private static double radius(Wave wave, float progress) {
        return wave.maxRadius * (1 - (1 - progress) * (1 - progress));
    }

    private static double width(Wave wave, float progress) {
        return wave.maxRadius * RING_WIDTH * (1 - progress * 0.5);
    }

    private static void refreshOcclusion(World world, Wave wave, double radius, double width, float progress) {
        if (radius <= 0 || width <= 0) {
            Arrays.fill(wave.occluded, false);
            return;
        }
        RailgunVec origin = wave.center.add(0, PLANE_OFFSET, 0);
        for (int i = 0; i < RING_SEGMENTS; i++) {
            double middle = (i + 0.5) * Math.PI * 2 / RING_SEGMENTS;
            double reach = Math.max(
                jittered(wave, radius, i, progress) + width * 0.5,
                jittered(wave, radius, i + 1, progress) + width * 0.5) + OCCLUSION_MARGIN;
            RailgunVec edge = origin.add(Math.cos(middle) * reach, 0, Math.sin(middle) * reach);
            MovingObjectPosition hit = world.func_147447_a(origin.toMc(), edge.toMc(), false, true, false);
            wave.occluded[i] = hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK;
        }
    }

    private static void ring(Wave wave, double radius, double width, float progress, float alpha) {
        if (radius <= 0 || width <= 0 || alpha <= 0) return;
        Tessellator tessellator = Tessellator.instance;
        double y = wave.center.y() + PLANE_OFFSET;
        for (int i = 0; i < RING_SEGMENTS; i++) {
            if (wave.occluded[i]) continue;
            double start = i * Math.PI * 2 / RING_SEGMENTS, end = (i + 1) * Math.PI * 2 / RING_SEGMENTS;
            double radius0 = jittered(wave, radius, i, progress), radius1 = jittered(wave, radius, i + 1, progress);
            double inner0 = Math.max(0, radius0 - width * 0.5), inner1 = Math.max(0, radius1 - width * 0.5);
            double outer0 = radius0 + width * 0.5, outer1 = radius1 + width * 0.5;
            double cos0 = Math.cos(start), sin0 = Math.sin(start), cos1 = Math.cos(end), sin1 = Math.sin(end);
            tessellator.setColorRGBA_F(wave.red, wave.green, wave.blue, alpha);
            tessellator.addVertex(wave.center.x() + cos0 * inner0, y, wave.center.z() + sin0 * inner0);
            tessellator.addVertex(wave.center.x() + cos1 * inner1, y, wave.center.z() + sin1 * inner1);
            tessellator.setColorRGBA_F(wave.red, wave.green, wave.blue, alpha * RIM_ALPHA);
            tessellator.addVertex(wave.center.x() + cos1 * outer1, y, wave.center.z() + sin1 * outer1);
            tessellator.addVertex(wave.center.x() + cos0 * outer0, y, wave.center.z() + sin0 * outer0);
        }
    }

    private static double jittered(Wave wave, double radius, int segment, float progress) {
        if (radius <= 0) return radius;
        RailgunVec center = wave.center;
        double phase = center.x() * 0.17 + center.y() * 0.11 + center.z() * 0.13;
        double slow = Math.sin(segment * 0.59 + phase + progress * 2.4);
        double fast = Math.sin(segment * 1.37 + phase * 0.43 - progress * 3.1);
        return radius * (1 + (slow * 0.68 + fast * 0.32) * RADIUS_JITTER);
    }
}
