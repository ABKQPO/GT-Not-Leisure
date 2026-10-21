package com.gtnllib.client.fx.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.gtnllib.client.fx.world.FxRenderHelper.Sprite;
import com.gtnllib.utils.ColorMath;
import com.gtnllib.utils.MathUtils;
import com.gtnllib.utils.Vec3d;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class FxArcRenderer {

    private static final int MAX_ACTIVE_ARCS = 256;
    private static final int MAX_SEGMENTS = 32;
    private static final int MAX_LIFETIME = 40;
    private static final double MAX_ARC_SPAN = 512;
    private static final double SWAY_SPEED = 0.18;
    private static final double SWAY_DISTANCE = 0.055;
    private static final double GLOW_WIDTH = 0.18;
    private static final double MID_WIDTH = 0.095;
    private static final double CORE_WIDTH = 0.035;
    private static final float GLOW_ALPHA = 0.25F;
    private static final float MID_ALPHA = 0.62F;

    public record Color(float red, float green, float blue) {}

    public record Style(Color glow, Color mid, Color core, Color haloOuter, Color haloInner, double widthScale) {}

    private record Arc(List<Vec3d> points, Vec3d side, Vec3d up, int lifetime, long born, Style style, long seed,
        boolean halo) {}

    private static final List<Arc> ACTIVE = new ArrayList<>();

    public static void spawnColored(Vec3d from, Vec3d to, int segments, double spread, int lifetime, int rgb) {
        spawnColored(from, to, segments, spread, lifetime, rgb, 1);
    }

    public static void spawnColored(Vec3d from, Vec3d to, int segments, double spread, int lifetime, int rgb,
        double width) {
        Color glow = new Color(ColorMath.red(rgb), ColorMath.green(rgb), ColorMath.blue(rgb));
        spawn(
            from,
            to,
            segments,
            spread,
            lifetime,
            new Style(
                glow,
                mixWhite(glow, 0.3F),
                mixWhite(glow, 0.7F),
                mixWhite(glow, 0.1F),
                mixWhite(glow, 0.6F),
                width),
            true);
    }

    private static Color mixWhite(Color color, float k) {
        return new Color(
            color.red() + (1 - color.red()) * k,
            color.green() + (1 - color.green()) * k,
            color.blue() + (1 - color.blue()) * k);
    }

    public static void spawn(Vec3d from, Vec3d to, int segments, double spread, int lifetime, Style style,
        boolean halo) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null || from == null
            || to == null
            || !from.finite()
            || !to.finite()
            || !Double.isFinite(spread)
            || spread < 0
            || lifetime <= 0) return;
        Vec3d delta = to.subtract(from);
        double length = delta.length();
        if (length < 0.001 || length > MAX_ARC_SPAN) return;
        segments = Math.max(2, Math.min(MAX_SEGMENTS, segments));
        Vec3d direction = delta.normalize();
        Vec3d reference = Math.abs(direction.y()) > 0.95 ? new Vec3d(1, 0, 0) : new Vec3d(0, 1, 0);
        Vec3d side = direction.cross(reference)
            .normalize(),
            up = direction.cross(side)
                .normalize();
        long seed = world.rand.nextLong();
        List<Vec3d> points = new ArrayList<>(segments + 1);
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
        ACTIVE.add(
            new Arc(points, side, up, Math.min(MAX_LIFETIME, lifetime), world.getTotalWorldTime(), style, seed, halo));
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
        FxRenderHelper.render(() -> {
            for (Arc arc : ACTIVE) {
                double age = time - arc.born;
                float alpha = fade(age, arc.lifetime);
                if (alpha <= 0) continue;
                Style style = arc.style;
                double swayPhase = time * SWAY_SPEED + arc.seed * 0.000000001;
                for (int i = 1; i < arc.points.size(); i++) {
                    Vec3d from = animated(arc, i - 1, swayPhase), to = animated(arc, i, swayPhase);
                    float pulse = (float) (0.86 + 0.14 * Math.sin(swayPhase * 1.7 + i * 0.73));
                    float segmentAlpha = alpha * pulse;
                    ribbon(from, to, GLOW_WIDTH * style.widthScale, style.glow, segmentAlpha * GLOW_ALPHA);
                    ribbon(from, to, MID_WIDTH * style.widthScale, style.mid, segmentAlpha * MID_ALPHA);
                    ribbon(from, to, CORE_WIDTH * style.widthScale, style.core, segmentAlpha);
                }
            }
        });
        if (ACTIVE.stream()
            .anyMatch(Arc::halo)) {
            FxRenderHelper.renderGlow(Sprite.RADIAL, () -> {
                for (Arc arc : ACTIVE) {
                    if (!arc.halo) continue;
                    double age = time - arc.born;
                    float alpha = fade(age, arc.lifetime);
                    if (alpha <= 0) continue;
                    Style style = arc.style;
                    int step = Math.max(2, arc.points.size() / 6);
                    double phase = time * SWAY_SPEED + arc.seed * 0.000000001;
                    for (int index = 1; index < arc.points.size() - 1; index += step) {
                        Vec3d node = animated(arc, index, phase);
                        float pulse = (float) (0.8 + 0.2 * Math.sin(phase * 1.7 + index * 0.73));
                        setColor(style.haloOuter, alpha * pulse * 0.36F);
                        FxRenderHelper.glow(node, 0.11 * style.widthScale, (float) (phase + index));
                        setColor(style.haloInner, alpha * pulse * 0.58F);
                        FxRenderHelper.glow(node, 0.045 * style.widthScale, (float) (-phase - index));
                    }
                }
            });
        }
    }

    private static void ribbon(Vec3d from, Vec3d to, double width, Color color, float alpha) {
        FxRenderHelper.ribbon(from, to, width, color.red, color.green, color.blue, alpha);
    }

    private static void setColor(Color color, float alpha) {
        Tessellator.instance.setColorRGBA_F(color.red, color.green, color.blue, alpha);
    }

    private static float fade(double age, int lifetime) {
        double progress = MathUtils.clampUnit(age / lifetime);
        double fadeIn = Math.min(1, progress * 5);
        return (float) (fadeIn * (1 - progress) * (0.9 + 0.1 * Math.sin(progress * Math.PI)));
    }

    private static Vec3d animated(Arc arc, int index, double phase) {
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
