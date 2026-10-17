package com.science.gtnl.client.railgun;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.science.gtnl.client.railgun.shader.RailgunShaderEffect;
import com.science.gtnl.common.railgun.RailgunVec;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RailgunShockwaveRenderer {

    private static final int LIFETIME = 12;
    private static final int MAX_ACTIVE = 64;
    private static final int FALLBACK_SEGMENTS = 64;
    private static final double PLANE_OFFSET = 0.03;
    private static final float RED = 1F;
    private static final float GREEN = 0.65F;
    private static final float BLUE = 0.85F;

    private record Wave(RailgunVec center, double radius, long born) {}

    private static final List<Wave> ACTIVE = new ArrayList<>();

    public static void spawn(RailgunVec center, double radius) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null || center == null || radius <= 0 || radius > 32 || !center.finite()) return;
        if (ACTIVE.size() >= MAX_ACTIVE) ACTIVE.remove(0);
        ACTIVE.add(new Wave(center, radius, world.getTotalWorldTime()));
    }

    public static void clear() {
        ACTIVE.clear();
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        long now = world.getTotalWorldTime();
        ACTIVE.removeIf(wave -> now - wave.born() >= LIFETIME);
        if (ACTIVE.isEmpty()) return;
        if (!drawShader(now, event.partialTicks)) RailgunRenderHelper
            .render(() -> { for (Wave wave : ACTIVE) fallback(wave, progress(wave, now, event.partialTicks)); });
    }

    private static boolean drawShader(long now, float partialTicks) {
        if (!MainConfig.client.enableRailgunShaders) return false;
        RailgunShaderEffect shader = RailgunShaderEffect.IMPACT;
        if (!shader.begin()) return false;
        try {
            shader.uniform1f("time", (float) ((now + partialTicks) * 0.05));
            for (Wave wave : ACTIVE) {
                shader.uniform1f("progress", (float) progress(wave, now, partialTicks));
                RailgunRenderHelper.render(() -> {
                    Tessellator.instance.setColorRGBA_F(RED, GREEN, BLUE, 1F);
                    RailgunRenderHelper.uvPlane(
                        wave.center()
                            .add(0, PLANE_OFFSET, 0),
                        wave.radius());
                });
            }
            return true;
        } finally {
            shader.end();
        }
    }

    private static void fallback(Wave wave, double progress) {
        double radius = wave.radius() * progress;
        double inner = Math.max(0, radius - 0.15 - 0.25 * progress);
        Tessellator.instance.setColorRGBA_F(RED, GREEN, BLUE, (float) (1 - progress) * 1F);
        for (int index = 0; index < FALLBACK_SEGMENTS; index++) {
            double start = index * Math.PI * 2 / FALLBACK_SEGMENTS, end = (index + 1) * Math.PI * 2 / FALLBACK_SEGMENTS;
            RailgunRenderHelper.vertex(
                wave.center()
                    .add(Math.cos(start) * inner, PLANE_OFFSET, Math.sin(start) * inner));
            RailgunRenderHelper.vertex(
                wave.center()
                    .add(Math.cos(start) * radius, PLANE_OFFSET, Math.sin(start) * radius));
            RailgunRenderHelper.vertex(
                wave.center()
                    .add(Math.cos(end) * radius, PLANE_OFFSET, Math.sin(end) * radius));
            RailgunRenderHelper.vertex(
                wave.center()
                    .add(Math.cos(end) * inner, PLANE_OFFSET, Math.sin(end) * inner));
        }
    }

    private static double progress(Wave wave, long now, float partialTicks) {
        return Math.min(1, (now - wave.born() + partialTicks) / LIFETIME);
    }
}
