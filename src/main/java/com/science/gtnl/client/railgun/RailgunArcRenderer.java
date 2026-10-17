package com.science.gtnl.client.railgun;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.science.gtnl.client.railgun.RailgunRenderHelper.Sprite;
import com.science.gtnl.common.railgun.RailgunVec;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RailgunArcRenderer {

    private static final int MAX_ACTIVE_ARCS = 256;
    private static final int MAX_SEGMENTS = 32;
    private static final double MAX_ARC_SPAN = 512;
    private static final double SWAY_SPEED = 0.18;
    private static final double SWAY_DISTANCE = 0.055;

    private record Arc(List<RailgunVec> points, RailgunVec side, RailgunVec up, int lifetime, long born, boolean ehv,
        long seed) {}

    private static final List<Arc> ACTIVE = new ArrayList<>();

    public static void spawnChain(RailgunVec from, RailgunVec to, int lifetime) {
        spawnChain(from, to, lifetime, true);
    }

    public static void spawnChain(RailgunVec from, RailgunVec to, int lifetime, boolean ehv) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null || from == null || to == null || !from.finite() || !to.finite() || lifetime <= 0) return;
        RailgunVec delta = to.subtract(from);
        double length = delta.length();
        if (length < 0.001 || length > MAX_ARC_SPAN) return;
        int segments = Math.min(MAX_SEGMENTS, Math.max(6, (int) Math.round(length * 1.5)));
        double spread = Math.min(0.55, 0.08 + length * 0.018);
        RailgunVec dir = delta.normalize();
        RailgunVec reference = Math.abs(dir.y()) > 0.95 ? new RailgunVec(1, 0, 0) : new RailgunVec(0, 1, 0);
        RailgunVec side = dir.cross(reference)
            .normalize(),
            up = dir.cross(side)
                .normalize();
        long seed = world.rand.nextLong();
        List<RailgunVec> points = new ArrayList<>(segments + 1);
        points.add(from);
        for (int i = 1; i < segments; i++) {
            double t = i / (double) segments;
            double attenuation = 4 * t * (1 - t);
            double phase = seed * 0.000000001 + i * 1.61803398875;
            double x = (Math.sin(phase * 2.37) * 0.62 + Math.sin(phase * 0.83 + 1.7) * 0.38) * spread * attenuation;
            double y = (Math.cos(phase * 1.91 + 0.4) * 0.58 + Math.sin(phase * 0.71 + 2.1) * 0.42) * spread
                * attenuation;
            points.add(
                from.add(delta.scale(t))
                    .add(side.scale(x))
                    .add(up.scale(y)));
        }
        points.add(to);
        if (ACTIVE.size() >= MAX_ACTIVE_ARCS) ACTIVE.remove(0);
        ACTIVE.add(new Arc(points, side, up, Math.min(40, lifetime), world.getTotalWorldTime(), ehv, seed));
    }

    public static void clear() {
        ACTIVE.clear();
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        ACTIVE.removeIf(arc -> world.getTotalWorldTime() - arc.born >= arc.lifetime);
        if (ACTIVE.isEmpty()) return;
        double time = world.getTotalWorldTime() + event.partialTicks;
        RailgunRenderHelper.render(() -> {
            for (Arc arc : ACTIVE) {
                double age = time - arc.born;
                float alpha = fade(age, arc.lifetime);
                if (alpha <= 0) continue;
                double swayPhase = time * SWAY_SPEED + arc.seed * 0.000000001;
                for (int i = 1; i < arc.points.size(); i++) {
                    RailgunVec from = animated(arc, i - 1, swayPhase), to = animated(arc, i, swayPhase);
                    float pulse = (float) (0.86 + 0.14 * Math.sin(swayPhase * 1.7 + i * 0.73));
                    float segmentAlpha = alpha * pulse;
                    RailgunRenderHelper.ribbon(
                        from,
                        to,
                        0.18,
                        arc.ehv ? 1 : 0.25F,
                        arc.ehv ? 0.3F : 0.75F,
                        arc.ehv ? 0.6F : 1,
                        segmentAlpha * 0.25F);
                    RailgunRenderHelper.ribbon(
                        from,
                        to,
                        0.095,
                        arc.ehv ? 1 : 0.55F,
                        arc.ehv ? 0.55F : 0.88F,
                        arc.ehv ? 0.75F : 1,
                        segmentAlpha * 0.62F);
                    RailgunRenderHelper.ribbon(
                        from,
                        to,
                        0.035,
                        arc.ehv ? 1 : 0.85F,
                        arc.ehv ? 0.8F : 0.97F,
                        arc.ehv ? 0.9F : 1,
                        segmentAlpha);
                }
            }
        });
        if (MainConfig.client.enableRailgunBloom) {
            RailgunRenderHelper.renderGlow(Sprite.RADIAL, () -> {
                for (Arc arc : ACTIVE) {
                    double age = time - arc.born;
                    float alpha = fade(age, arc.lifetime);
                    if (alpha <= 0) continue;
                    int step = Math.max(2, arc.points.size() / 6);
                    double phase = time * SWAY_SPEED + arc.seed * 0.000000001;
                    for (int index = 1; index < arc.points.size() - 1; index += step) {
                        RailgunVec node = animated(arc, index, phase);
                        float pulse = (float) (0.8 + 0.2 * Math.sin(phase * 1.7 + index * 0.73));
                        Tessellator.instance.setColorRGBA_F(
                            arc.ehv ? 1F : 0.4F,
                            arc.ehv ? 0.38F : 0.82F,
                            arc.ehv ? 0.68F : 1F,
                            alpha * pulse * 0.36F);
                        RailgunRenderHelper.glow(node, 0.11, (float) (phase + index));
                        Tessellator.instance.setColorRGBA_F(
                            arc.ehv ? 1F : 0.7F,
                            arc.ehv ? 0.75F : 0.94F,
                            arc.ehv ? 0.88F : 1F,
                            alpha * pulse * 0.58F);
                        RailgunRenderHelper.glow(node, 0.045, (float) (-phase - index));
                    }
                }
            });
        }
    }

    private static float fade(double age, int lifetime) {
        double progress = Math.max(0, Math.min(1, age / lifetime));
        double fadeIn = Math.min(1, progress * 5);
        return (float) (fadeIn * (1 - progress) * (0.9 + 0.1 * Math.sin(progress * Math.PI)));
    }

    private static RailgunVec animated(Arc arc, int index, double phase) {
        if (index == 0 || index == arc.points.size() - 1) return arc.points.get(index);
        double t = index / (double) (arc.points.size() - 1);
        double attenuation = 4 * t * (1 - t);
        double pointPhase = phase + index * 0.91 + arc.seed * 0.00000000037;
        double sideOffset = Math.sin(pointPhase) * SWAY_DISTANCE * attenuation;
        double upOffset = Math.cos(pointPhase * 0.83 + 1.4) * SWAY_DISTANCE * 0.72 * attenuation;
        return arc.points.get(index)
            .add(arc.side.scale(sideOffset))
            .add(arc.up.scale(upOffset));
    }
}
