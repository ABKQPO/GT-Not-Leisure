package com.science.gtnl.client.railgun;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.science.gtnl.common.railgun.RailgunVec;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RailgunArcRenderer {

    private static final int MAX_ACTIVE_ARCS = 512;
    private static final int MAX_SEGMENTS = 384;
    private static final int MAX_LIFETIME = 80;
    private static final double MAX_ARC_SPAN = 512;
    private static final float GLOW_ALPHA = 0.45F;
    private static final float MID_ALPHA = 0.65F;
    private static final float MID_WIDTH = 0.55F;
    private static final float CORE_ALPHA = 0.95F;

    private record Style(float coreRed, float coreGreen, float coreBlue, float glowRed, float glowGreen, float glowBlue,
        double coreWidth, double glowWidth) {}

    private static final Style PLASMA = new Style(1F, 0.8F, 0.9F, 1F, 0.3F, 0.6F, 0.05, 0.16);
    private static final Style HIGH_VOLTAGE = new Style(0.95F, 1F, 1F, 0.35F, 0.78F, 1F, 0.05, 0.16);
    private static final Style IMPACT_SPARK = new Style(1F, 0.82F, 0.88F, 0.9F, 0.25F, 0.55F, 0.04, 0.13);

    private record Arc(List<RailgunVec> points, int lifetime, long born, Style style) {}

    private static final List<Arc> ACTIVE = new ArrayList<>();

    public static void spawnChain(RailgunVec from, RailgunVec to, int lifetime) {
        spawnChain(from, to, lifetime, true);
    }

    public static void spawnChain(RailgunVec from, RailgunVec to, int lifetime, boolean ehv) {
        if (!from.finite() || !to.finite()) return;
        double length = to.subtract(from)
            .length();
        int segments = Math.max(6, Math.min(28, (int) Math.round(length * 1.5)));
        double spread = Math.min(0.55, 0.1 + length * 0.02);
        spawn(from, to, segments, spread, lifetime, ehv ? PLASMA : HIGH_VOLTAGE);
    }

    public static void spawnPlasma(RailgunVec from, RailgunVec to, int segments, double spread, int lifetime) {
        spawn(from, to, segments, spread, lifetime, PLASMA);
    }

    public static void spawnImpactSpark(RailgunVec from, RailgunVec to, int lifetime) {
        if (!from.finite() || !to.finite()) return;
        double length = to.subtract(from)
            .length();
        int segments = Math.max(4, Math.min(14, (int) Math.round(length * 2.5)));
        double spread = Math.min(0.35, 0.06 + length * 0.04);
        spawn(from, to, segments, spread, lifetime, IMPACT_SPARK);
    }

    private static void spawn(RailgunVec from, RailgunVec to, int segments, double spread, int lifetime, Style style) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null || !from.finite() || !to.finite() || !Double.isFinite(spread) || spread < 0 || lifetime <= 0)
            return;
        RailgunVec delta = to.subtract(from);
        double length = delta.length();
        if (length < 0.001 || length > MAX_ARC_SPAN) return;
        segments = Math.max(2, Math.min(MAX_SEGMENTS, segments));
        RailgunVec dir = delta.normalize();
        RailgunVec reference = Math.abs(dir.y()) > 0.95 ? new RailgunVec(1, 0, 0) : new RailgunVec(0, 1, 0);
        RailgunVec side = dir.cross(reference)
            .normalize(),
            up = dir.cross(side)
                .normalize();
        List<RailgunVec> points = new ArrayList<>(segments + 1);
        points.add(from);
        for (int i = 1; i < segments; i++) {
            double t = i / (double) segments;
            double attenuation = 4 * t * (1 - t);
            double x = (world.rand.nextDouble() - 0.5) * 2 * spread * attenuation;
            double y = (world.rand.nextDouble() - 0.5) * 2 * spread * attenuation;
            points.add(
                from.add(delta.scale(t))
                    .add(side.scale(x))
                    .add(up.scale(y)));
        }
        points.add(to);
        if (ACTIVE.size() >= MAX_ACTIVE_ARCS) ACTIVE.remove(0);
        ACTIVE.add(new Arc(points, Math.min(MAX_LIFETIME, lifetime), world.getTotalWorldTime(), style));
    }

    public static void clear() {
        ACTIVE.clear();
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        long now = world.getTotalWorldTime();
        ACTIVE.removeIf(arc -> now - arc.born >= arc.lifetime);
        if (ACTIVE.isEmpty()) return;
        RailgunRenderHelper.render(() -> {
            for (Arc arc : ACTIVE) {
                float life = 1 - (now - arc.born + event.partialTicks) / arc.lifetime;
                float alpha = (float) Math.sqrt(Math.max(0, life));
                Style style = arc.style;
                for (int i = 1; i < arc.points.size(); i++) {
                    RailgunVec from = arc.points.get(i - 1), to = arc.points.get(i);
                    RailgunRenderHelper.ribbon(
                        from,
                        to,
                        style.glowWidth,
                        style.glowRed,
                        style.glowGreen,
                        style.glowBlue,
                        GLOW_ALPHA * alpha);
                    RailgunRenderHelper.ribbon(
                        from,
                        to,
                        style.glowWidth * MID_WIDTH,
                        (style.glowRed + style.coreRed) * 0.5F,
                        (style.glowGreen + style.coreGreen) * 0.5F,
                        (style.glowBlue + style.coreBlue) * 0.5F,
                        MID_ALPHA * alpha);
                    RailgunRenderHelper.ribbon(
                        from,
                        to,
                        style.coreWidth,
                        style.coreRed,
                        style.coreGreen,
                        style.coreBlue,
                        CORE_ALPHA * alpha);
                }
            }
        });
    }
}
