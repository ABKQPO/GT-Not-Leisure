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
    private static final double MAX_ARC_SPAN = 512;

    private record Arc(List<RailgunVec> points, int lifetime, long born, boolean ehv) {}

    private static final List<Arc> ACTIVE = new ArrayList<>();

    public static void spawnChain(RailgunVec from, RailgunVec to, int lifetime) {
        spawnChain(from, to, lifetime, true);
    }

    public static void spawnChain(RailgunVec from, RailgunVec to, int lifetime, boolean ehv) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null || !from.finite() || !to.finite() || lifetime <= 0) return;
        RailgunVec delta = to.subtract(from);
        double length = delta.length();
        if (length < 0.001 || length > MAX_ARC_SPAN) return;
        int segments = Math.min(MAX_SEGMENTS, Math.max(6, Math.min(28, (int) Math.round(length * 1.5))));
        double spread = Math.min(0.55, 0.1 + length * 0.02);
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
        ACTIVE.add(new Arc(points, Math.min(40, lifetime), world.getTotalWorldTime(), ehv));
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
        RailgunRenderHelper.render(() -> {
            for (Arc arc : ACTIVE) {
                float alpha = Math
                    .max(0, 1 - (world.getTotalWorldTime() - arc.born + event.partialTicks) / arc.lifetime);
                for (int i = 1; i < arc.points.size(); i++) {
                    RailgunVec from = arc.points.get(i - 1), to = arc.points.get(i);
                    RailgunRenderHelper.ribbon(
                        from,
                        to,
                        0.24,
                        arc.ehv ? 1 : 0.25F,
                        arc.ehv ? 0.3F : 0.75F,
                        arc.ehv ? 0.6F : 1,
                        alpha * 0.5F);
                    RailgunRenderHelper.ribbon(
                        from,
                        to,
                        0.13,
                        arc.ehv ? 1 : 0.55F,
                        arc.ehv ? 0.55F : 0.88F,
                        arc.ehv ? 0.75F : 1,
                        alpha * 0.85F);
                    RailgunRenderHelper
                        .ribbon(from, to, 0.05, arc.ehv ? 1 : 0.85F, arc.ehv ? 0.8F : 0.97F, arc.ehv ? 0.9F : 1, alpha);
                }
            }
        });
    }
}
