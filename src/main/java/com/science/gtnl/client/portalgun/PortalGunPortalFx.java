package com.science.gtnl.client.portalgun;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;

import com.google.common.collect.MapMaker;
import com.gtnllib.client.fx.FxEasing;
import com.gtnllib.client.fx.world.FxMoteRenderer;
import com.gtnllib.client.fx.world.FxMoteRenderer.Mote;
import com.gtnllib.client.fx.world.FxRenderHelper;
import com.gtnllib.client.fx.world.FxRenderHelper.Sprite;
import com.gtnllib.utils.ColorMath;
import com.gtnllib.utils.MathUtils;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.common.entity.EntityPortalGunPortal;
import com.science.gtnl.common.render.entity.PortalGunDoorRender;
import com.science.gtnl.common.render.entity.PortalGunSpriteRender;
import com.science.gtnl.common.render.entity.PortalGunVortexRender;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.PortalGunPortalEffect;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SideOnly(Side.CLIENT)
public class PortalGunPortalFx {

    public static final double BALL = 0.32, BURST = 2.5, RAMP = 1.5, ARC_FADE = 2, SWIRL_HALF_WIDTH = 0.625,
        SWIRL_HALF_HEIGHT = 1.0, VORTEX_EDGE = 0.95, FACE_REACH = 0.95, CENTER_REACH = 0.25;
    public static final double FLOAT_LIFETIME = 30;
    public static final double JELLY_PERIOD = 5, JELLY_TICKS = 40, JELLY_ROCK = 0.8, JELLY_LAG = 0.6, POP_SPAN = 0.5,
        PASS_REACH = 0.8, PASS_SPREAD = 1.5, PASS_FRESH = 10;
    private static final double CONE = Math.tan(Math.toRadians(60)), HAZE_PEAK = 2, HAZE_FADE = 16;
    private static final double DOOR_RING_INSET = 0.16, SWIRL_RING = 0.84, VORTEX_RING = 0.86;
    private static final double ARC_HAZE = 0.04, ARC_HUG = 3.5, ARC_FOOT = 0.035, ARC_NODE = 0.022, UNEVEN = 0.22,
        END_FADE = Math.sin(Math.PI * 0.2);
    private static final double[] BEND_LEVELS = { 1, 0.69, 0.125 }, RISE_LEVELS = { 0.83, 0.67, 0.13 };
    private static final float ARC_HAZE_ALPHA = 0.12F, ARC_HUG_ALPHA = 0.85F, ARC_CORE_ALPHA = 0.95F,
        ARC_FOOT_ALPHA = 0.35F, ARC_NODE_ALPHA = 0.55F;
    private static final int WHITE = 0xFFFFFF, LEVELS = 3;
    private static final Map<EntityPortalGunPortal, Boolean> BURST_SPAWNED = new MapMaker().weakKeys()
        .makeMap();
    private static final Map<EntityPortalGunPortal, Integer> PASS_SEEN = new MapMaker().weakKeys()
        .makeMap();

    public record Shape(boolean rect, double halfWidth, double halfHeight) {

        public double size() {
            return Math.max(halfWidth, halfHeight);
        }

        public double perimeter() {
            if (rect) return 4 * (halfWidth + halfHeight);
            double a = halfWidth, b = halfHeight;
            return Math.PI * (3 * (a + b) - Math.sqrt((3 * a + b) * (a + 3 * b)));
        }

        public Edge edge(double u) {
            double t = u - Math.floor(u);
            if (rect) {
                double top = 2 * halfWidth, side = 2 * halfHeight, d = t * (2 * top + 2 * side);
                if (d < top) return new Edge(-halfWidth + d, halfHeight, 0, 1);
                if (d < top + side) return new Edge(halfWidth, halfHeight - (d - top), 1, 0);
                if (d < 2 * top + side) return new Edge(halfWidth - (d - top - side), -halfHeight, 0, -1);
                return new Edge(-halfWidth, -halfHeight + (d - 2 * top - side), -1, 0);
            }
            double angle = t * 2 * Math.PI, nx = Math.cos(angle) * Math.max(halfHeight, 1.0E-3),
                ny = Math.sin(angle) * Math.max(halfWidth, 1.0E-3), length = Math.hypot(nx, ny);
            return new Edge(Math.cos(angle) * halfWidth, Math.sin(angle) * halfHeight, nx / length, ny / length);
        }
    }

    public record Edge(double x, double y, double outX, double outY) {}

    public record Pose(double sx, double sy, double push) {

        public static final Pose REST = new Pose(1, 1, 0);
    }

    public record Arc(List<Vec3d> points, boolean straightOut, boolean fork, float alpha) {}

    public record Dot(double born, double hover, Vec3d origin, Vec3d velocity, double size, boolean twinkle,
        float phase) {}

    private PortalGunPortalFx() {}

    public static Shape shape(PortalGunPortalEffect effect, float ticks, float close) {
        if (ticks < 0) return new Shape(false, 0, 0);
        if (effect.kind == PortalGunPortalEffect.Kind.FRAMED) {
            float open = MathUtils.clampUnit(ticks / PortalGunDoorRender.DOOR_OPEN_TICKS);
            return new Shape(
                true,
                PortalGunDoorRender.DOOR_HALF_WIDTH * PortalGunDoorRender.doorWidth(open, close),
                PortalGunDoorRender.DOOR_HALF_HEIGHT * PortalGunDoorRender.doorHeight(open, close));
        }
        if (effect.kind == PortalGunPortalEffect.Kind.VORTEX) {
            float size = PortalGunVortexRender
                .size(MathUtils.clampUnit(ticks / PortalGunVortexRender.OPEN_TICKS), close);
            return new Shape(
                false,
                PortalGunVortexRender.RX * VORTEX_EDGE * size,
                PortalGunVortexRender.RY * VORTEX_EDGE * size);
        }
        float open = MathUtils.clampUnit(ticks / EntityPortalGunPortal.OPEN_TICKS);
        return new Shape(
            false,
            SWIRL_HALF_WIDTH * PortalGunSpriteRender.widthScale(open, close),
            SWIRL_HALF_HEIGHT * PortalGunSpriteRender.heightScale(open, close));
    }

    public static int closeTicks(PortalGunPortalEffect effect, MainConfig.Client settings) {
        return effect.kind == PortalGunPortalEffect.Kind.FRAMED ? PortalGunDoorRender.DOOR_OPEN_TICKS
            : settings.portalGunCloseTicks;
    }

    public static Pose pose(PortalGunPortalEffect effect, double sincePass, float close, MainConfig.Client settings) {
        double sx = 1, sy = 1, push = 0, jelly = settings.portalGunPassJelly;
        if (close <= 0 && jelly > 0 && sincePass >= 0 && sincePass < JELLY_TICKS) {
            double decay = Math.exp(-sincePass / JELLY_PERIOD), phase = 2 * Math.PI * sincePass / JELLY_PERIOD;
            sx += jelly * Math.sin(phase) * decay;
            sy -= jelly * Math.sin(phase) * decay;
            push = -JELLY_ROCK * jelly * Math.sin(phase + JELLY_LAG) * decay;
        }
        if (close > 0 && effect.kind != PortalGunPortalEffect.Kind.FRAMED) {
            double bump = settings.portalGunClosePop * Math.sin(Math.PI * Math.min(1, close / POP_SPAN));
            sx *= 1 + bump / 2;
            sy *= 1 + bump;
        }
        return new Pose(sx, sy, push);
    }

    public static int openedAt(PortalGunPortalEffect effect) {
        return switch (effect.kind) {
            case FRAMED -> PortalGunDoorRender.DOOR_OPEN_TICKS;
            case VORTEX -> PortalGunVortexRender.OPEN_TICKS;
            default -> EntityPortalGunPortal.OPEN_TICKS;
        };
    }

    public static Shape innerRing(PortalGunPortalEffect effect, Shape shape) {
        if (shape.rect()) return new Shape(
            true,
            Math.max(0.05, shape.halfWidth() - DOOR_RING_INSET),
            Math.max(0.05, shape.halfHeight() - DOOR_RING_INSET));
        double k = effect.kind == PortalGunPortalEffect.Kind.VORTEX ? VORTEX_RING : SWIRL_RING;
        return new Shape(false, shape.halfWidth() * k, shape.halfHeight() * k);
    }

    public static List<Dot> dots(PortalGunPortalEffect effect, long seed, MainConfig.Client settings) {
        Random r = new Random(MathUtils.hash(seed, 0x5EED, 0, 0));
        Shape full = shape(effect, Float.MAX_VALUE / 2, 0);
        int spots = Math.max(1, (int) Math.round(full.perimeter() * settings.portalGunOpenDensity));
        double limit = Math.max(1.0E-4, settings.portalGunOpenSpread * 2.5);
        List<Dot> dots = new ObjectArrayList<>();
        for (int i = 0; i < spots; i++) {
            double born = BURST * Math.pow(r.nextDouble(), 1.6);
            Shape now = shape(effect, (float) born, 0);
            Edge edge = now.edge((i + r.nextDouble()) / spots);
            if (now.size() < BALL) {
                double angle = r.nextDouble() * 2 * Math.PI, k = Math.sqrt(r.nextDouble()) * BALL;
                edge = new Edge(Math.cos(angle) * k, Math.sin(angle) * k, Math.cos(angle), Math.sin(angle));
            } else if (settings.enablePortalGunOpenWholeFace) {
                double k = Math.sqrt(r.nextDouble()) * FACE_REACH;
                edge = new Edge(edge.x() * k, edge.y() * k, edge.outX(), edge.outY());
            }
            for (int k = 0; k < settings.portalGunOpenCount; k++) {
                double lateral = Math.abs(PortalGunOrbTrail.gauss(r, settings.portalGunOpenSpread, limit)),
                    depth = Math.abs(PortalGunOrbTrail.gauss(r, settings.portalGunOpenSpread, limit));
                Vec3d origin = new Vec3d(
                    edge.x() + edge.outX() * lateral,
                    edge.y() + edge.outY() * lateral,
                    0.04 + depth);
                dots.add(thrown(r, settings, born, origin, edge.outX(), edge.outY()));
            }
        }
        return dots;
    }

    public static List<Dot> passDots(PortalGunPortalEffect effect, long seed, MainConfig.Client settings) {
        Random r = new Random(MathUtils.hash(seed, 0x5A55, 0, 0));
        Shape full = shape(effect, Float.MAX_VALUE / 2, 0);
        List<Dot> dots = new ObjectArrayList<>(settings.portalGunPassDotCount);
        for (int i = 0; i < settings.portalGunPassDotCount; i++) {
            double angle = r.nextDouble() * 2 * Math.PI, k = Math.sqrt(r.nextDouble()) * PASS_REACH;
            Vec3d origin = new Vec3d(
                Math.cos(angle) * full.halfWidth() * k,
                Math.sin(angle) * full.halfHeight() * k,
                0.05);
            dots.add(thrown(r, settings, PASS_SPREAD * r.nextDouble(), origin, Math.cos(angle), Math.sin(angle)));
        }
        return dots;
    }

    private static Dot thrown(Random r, MainConfig.Client settings, double born, Vec3d origin, double outX,
        double outY) {
        double tilt = r.nextDouble() * 2 * Math.PI, cone = CONE * Math.sqrt(r.nextDouble());
        Vec3d aim = new Vec3d(Math.cos(tilt) * cone, Math.sin(tilt) * cone, 1).normalize();
        Vec3d velocity = new Vec3d(outX, outY, 0).scale(settings.portalGunOpenSpray * range(r, 0.5, 1.5))
            .add(aim.scale(settings.portalGunOpenJet * range(r, 0.3, 1.3)));
        return new Dot(
            born,
            settings.portalGunOpenHover * range(r, 0.5, 1.5),
            origin,
            velocity,
            settings.portalGunOpenDotSize * range(r, 0.6, 1.4),
            r.nextDouble() < PortalGunOrbTrail.TWINKLE_CHANCE,
            r.nextFloat() * (float) Math.PI * 2);
    }

    public static List<Arc> openingArcs(PortalGunPortalEffect effect, double age, long seed,
        MainConfig.Client settings) {
        List<Arc> arcs = new ObjectArrayList<>();
        if (age < 0 || age >= settings.portalGunOpenArcTicks) return arcs;
        double hop = settings.portalGunArcHop;
        Random r = new Random(MathUtils.hash(seed, (long) Math.floor(age / hop), 0, 0));
        double expected = settings.portalGunOpenArcRate * hop * FxEasing.smoothStep((float) ((age + hop) / RAMP));
        int count = (int) Math.floor(expected * range(r, 0.4, 1.6) + r.nextDouble());
        Shape shape = shape(effect, (float) age, 0);
        float flash = 0.7F + 0.3F * r.nextFloat();
        for (int k = 0; k < count; k++) {
            if (shape.size() < 0.3) {
                Vec3d a = new Vec3d(range(r, -1, 1) * BALL, range(r, -1, 1) * BALL, 0.15 + range(r, -0.3, 1) * BALL);
                Vec3d b = new Vec3d(range(r, -1, 1) * BALL, range(r, -1, 1) * BALL, 0.15 + range(r, -0.3, 1) * BALL);
                bolt(arcs, r, a, b, 0, 0.35, settings.portalGunArcForkChance, false, flash);
            } else if (r.nextDouble() < 0.2) {
                double[] at = facePoint(r, shape, 0.35);
                sharpBolt(
                    arcs,
                    r,
                    new Vec3d(at[0], at[1], 0.03),
                    new Vec3d(at[0] + range(r, -0.05, 0.05), at[1] + range(r, -0.05, 0.05), range(r, 0.35, 0.55)),
                    0,
                    settings.portalGunOpenArcBend,
                    settings.portalGunOpenArcRise,
                    settings.portalGunArcForkChance,
                    true,
                    flash);
            } else {
                double[] from = facePoint(r, shape, 0.7);
                double angle = r.nextDouble() * 2 * Math.PI,
                    span = range(r, 0.4, 0.65) * Math.max(shape.halfWidth(), 0.3);
                double toX = clamp(from[0] + Math.cos(angle) * span, 0.8 * shape.halfWidth()),
                    toY = clamp(from[1] + Math.sin(angle) * span, 0.8 * shape.halfHeight());
                sharpBolt(
                    arcs,
                    r,
                    new Vec3d(from[0], from[1], 0.03),
                    new Vec3d(toX, toY, 0.03),
                    settings.portalGunOpenArcRise * Math.hypot(toX - from[0], toY - from[1]) * range(r, 0.6, 1.4),
                    settings.portalGunOpenArcBend,
                    settings.portalGunOpenArcRise,
                    settings.portalGunArcForkChance,
                    false,
                    flash);
            }
        }
        return arcs;
    }

    public static List<Arc> idleArcs(PortalGunPortalEffect effect, double age, long seed, MainConfig.Client settings) {
        List<Arc> arcs = new ObjectArrayList<>();
        int opened = openedAt(effect);
        if (age < opened) return arcs;
        double hop = settings.portalGunArcHop;
        long now = (long) Math.floor(age / hop);
        Shape shape = shape(effect, (float) age, 0), ring = innerRing(effect, shape);
        double[] rates = { settings.portalGunRingArcRate, settings.portalGunCenterArcRate };
        for (int kind = 0; kind < 2; kind++) {
            if (rates[kind] <= 0) continue;
            for (long slot = now - 4; slot <= now; slot++) {
                if (slot * hop < opened) continue;
                Random plan = new Random(MathUtils.hash(seed, slot * 2 + kind + 7, 0, 0));
                if (plan.nextDouble() >= rates[kind] * hop / 20) continue;
                int hops = 2 + plan.nextInt(3), step = (int) (now - slot);
                if (step >= hops) continue;
                Random strike = new Random(MathUtils.hash(seed, now * 4 + kind, 1, 0));
                float flash = 0.65F + 0.35F * strike.nextFloat();
                if (kind == 0) {
                    double u0 = plan.nextDouble(),
                        length = (ring.rect() ? range(plan, 0.04, 0.07) : range(plan, 0.05, 0.09))
                            * (plan.nextBoolean() ? 1 : -1);
                    Edge a = ring.edge(u0 + length * step), b = ring.edge(u0 + length * (step + 1));
                    bolt(
                        arcs,
                        strike,
                        new Vec3d(a.x(), a.y(), 0.02),
                        new Vec3d(b.x(), b.y(), 0.02),
                        range(strike, 0.01, 0.05),
                        0.32,
                        settings.portalGunArcForkChance,
                        false,
                        flash);
                } else {
                    double[] a = facePoint(strike, shape, CENTER_REACH);
                    if (strike.nextDouble() < 0.3) bolt(
                        arcs,
                        strike,
                        new Vec3d(a[0], a[1], 0.02),
                        new Vec3d(
                            a[0] + range(strike, -0.04, 0.04),
                            a[1] + range(strike, -0.04, 0.04),
                            range(strike, 0.18, 0.32)),
                        0,
                        0.32,
                        settings.portalGunArcForkChance,
                        true,
                        flash);
                    else {
                        double[] b = facePoint(strike, shape, CENTER_REACH);
                        bolt(
                            arcs,
                            strike,
                            new Vec3d(a[0], a[1], 0.02),
                            new Vec3d(b[0], b[1], 0.02),
                            range(strike, 0.02, 0.08),
                            0.32,
                            settings.portalGunArcForkChance,
                            false,
                            flash);
                    }
                }
            }
        }
        return arcs;
    }

    public static void bolt(List<Arc> arcs, Random r, Vec3d a, Vec3d b, double lift, double rough, double forkChance,
        boolean straightOut, float alpha) {
        List<Vec3d> points = new ObjectArrayList<>(List.of(a, b));
        for (int level = 0; level < LEVELS; level++) {
            List<Vec3d> next = new ObjectArrayList<>(points.size() * 2);
            next.add(points.get(0));
            for (int i = 1; i < points.size(); i++) {
                Vec3d p0 = points.get(i - 1), p1 = points.get(i), delta = p1.subtract(p0);
                double flat = Math.hypot(delta.x(), delta.y()), length = Math.max(1.0E-3, delta.length());
                double sideX = flat > 1.0E-6 ? -delta.y() / flat : 1, sideY = flat > 1.0E-6 ? delta.x() / flat : 0;
                double kick = range(r, -1, 1) * rough * length;
                next.add(
                    new Vec3d(
                        (p0.x() + p1.x()) / 2 + sideX * kick,
                        (p0.y() + p1.y()) / 2 + sideY * kick,
                        Math.max(0.01, (p0.z() + p1.z()) / 2 + range(r, -1, 1) * rough * 0.5 * length)));
                next.add(p1);
            }
            points = next;
        }
        int last = points.size() - 1;
        List<Vec3d> lifted = new ObjectArrayList<>(points.size());
        for (int i = 0; i <= last; i++) lifted.add(
            points.get(i)
                .add(0, 0, lift * Math.sin(Math.PI * i / last)));
        branch(arcs, r, a, b, lifted, forkChance, straightOut, alpha);
    }

    public static void sharpBolt(List<Arc> arcs, Random r, Vec3d a, Vec3d b, double peak, double bend, double rise,
        double forkChance, boolean straightOut, float alpha) {
        List<Vec3d> points = new ObjectArrayList<>(List.of(a, b));
        for (int level = 0; level < LEVELS; level++) {
            List<Vec3d> next = new ObjectArrayList<>(points.size() * 2);
            next.add(points.get(0));
            for (int i = 1; i < points.size(); i++) {
                Vec3d p0 = points.get(i - 1), p1 = points.get(i), delta = p1.subtract(p0);
                double flat = Math.hypot(delta.x(), delta.y()), length = Math.max(1.0E-3, delta.length());
                double sideX = flat > 1.0E-6 ? -delta.y() / flat : 1, sideY = flat > 1.0E-6 ? delta.x() / flat : 0;
                double kick = range(r, -1, 1) * bend * BEND_LEVELS[level] * length;
                Vec3d base = p0.add(delta.scale(0.5 + range(r, -1, 1) * UNEVEN));
                double push = level == 0 ? peak : level == 1 ? range(r, 0, 0.35) * peak : 0;
                next.add(
                    new Vec3d(
                        base.x() + sideX * kick,
                        base.y() + sideY * kick,
                        Math.max(0.01, base.z() + push + range(r, -1, 1) * rise * RISE_LEVELS[level] * length)));
                next.add(p1);
            }
            points = next;
        }
        branch(arcs, r, a, b, points, forkChance, straightOut, alpha);
    }

    private static void branch(List<Arc> arcs, Random r, Vec3d a, Vec3d b, List<Vec3d> lifted, double forkChance,
        boolean straightOut, float alpha) {
        int last = lifted.size() - 1;
        arcs.add(new Arc(lifted, straightOut, false, alpha));
        if (r.nextDouble() >= forkChance) return;
        Vec3d from = lifted.get(2 + r.nextInt(last - 3));
        double direction = r.nextDouble() * 2 * Math.PI,
            reach = Math.hypot(b.x() - a.x(), b.y() - a.y()) * range(r, 0.25, 0.45);
        Vec3d end = from.add(Math.cos(direction) * reach, Math.sin(direction) * reach, range(r, 0, 0.04));
        Vec3d middle = new Vec3d(
            (from.x() + end.x()) / 2 + range(r, -1, 1) * 0.3 * reach,
            (from.y() + end.y()) / 2 + range(r, -1, 1) * 0.3 * reach,
            (from.z() + end.z()) / 2);
        arcs.add(new Arc(List.of(from, middle, end), false, true, alpha));
    }

    public static void throwDots(EntityPortalGunPortal portal, PortalGunPortalEffect effect, double x, double y,
        double z, float partialTicks) {
        MainConfig.Client settings = MainConfig.client;
        double age = portal.ticksExisted + partialTicks;
        long seed = portal.getEntityId();
        Vec3d up = Vec3d.of(portal.up()), normal = Vec3d.of(portal.facing()), right = up.cross(normal);
        Vec3d center = new Vec3d(
            x + RenderManager.renderPosX,
            y + RenderManager.renderPosY,
            z + RenderManager.renderPosZ);
        int glow = effect.glowColor & 0xFFFFFF, core = effect.coreColor & 0xFFFFFF;
        if (BURST_SPAWNED.putIfAbsent(portal, Boolean.TRUE) == null && age < BURST)
            spawnDots(dots(effect, seed, settings), age, settings, center, right, up, normal, glow, core, partialTicks);
        int passed = portal.lastPass();
        if (passed != EntityPortalGunPortal.NEVER_PASSED && !Objects.equals(PASS_SEEN.put(portal, passed), passed)
            && portal.sincePass(partialTicks) < PASS_FRESH)
            spawnDots(
                passDots(effect, MathUtils.hash(seed, passed, 0, 0), settings),
                0,
                settings,
                center,
                right,
                up,
                normal,
                glow,
                core,
                partialTicks);
    }

    public static void render(EntityPortalGunPortal portal, PortalGunPortalEffect effect, double x, double y, double z,
        float partialTicks, float close, Pose pose) {
        MainConfig.Client settings = MainConfig.client;
        double age = portal.ticksExisted + partialTicks;
        long seed = portal.getEntityId();
        Vec3d up = Vec3d.of(portal.up()), normal = Vec3d.of(portal.facing()), right = up.cross(normal);
        Vec3d center = new Vec3d(
            x + RenderManager.renderPosX,
            y + RenderManager.renderPosY,
            z + RenderManager.renderPosZ);
        int glow = effect.glowColor & 0xFFFFFF, core = effect.coreColor & 0xFFFFFF;
        Vec3d posedCenter = center.add(normal.scale(pose.push())), posedRight = right.scale(pose.sx()),
            posedUp = up.scale(pose.sy());
        double haze = FxEasing.smoothStep((float) (age / HAZE_PEAK))
            * (1 - FxEasing.smoothStep((float) HAZE_PEAK, (float) (HAZE_PEAK + HAZE_FADE), (float) age));
        Shape shape = shape(effect, (float) age, close);
        if (haze > 0.01) FxRenderHelper.renderGlow(Sprite.RADIAL, () -> {
            ColorMath.tessellate(Tessellator.instance, ColorMath.lerp(glow, core, 0.3F), (float) (0.6 * haze));
            FxRenderHelper.glow(posedCenter.add(normal.scale(0.05)), 1.8 * Math.max(0.3, shape.halfHeight()), 0);
        });
        List<Arc> arcs = new ObjectArrayList<>();
        float fade = 1 - FxEasing.smoothStep(
            (float) (settings.portalGunOpenArcTicks - ARC_FADE),
            (float) settings.portalGunOpenArcTicks,
            (float) age);
        for (Arc arc : openingArcs(effect, age, seed, settings))
            arcs.add(new Arc(arc.points(), arc.straightOut(), arc.fork(), arc.alpha() * fade));
        if (!portal.closing()) arcs.addAll(idleArcs(effect, age, seed, settings));
        if (arcs.isEmpty()) return;
        List<List<Vec3d>> paths = new ObjectArrayList<>(arcs.size());
        for (Arc arc : arcs) {
            List<Vec3d> points = new ObjectArrayList<>(
                arc.points()
                    .size());
            for (Vec3d local : arc.points()) points.add(world(local, posedCenter, posedRight, posedUp, normal));
            paths.add(points);
        }
        double width = settings.portalGunArcWidth;
        int hug = ColorMath.lerp(glow, core, 0.5F), node = ColorMath.lerp(core, WHITE, 0.5F),
            foot = ColorMath.lerp(glow, WHITE, 0.5F);
        FxRenderHelper.renderGlow(Sprite.RADIAL, () -> {
            for (int i = 0; i < arcs.size(); i++) {
                Arc arc = arcs.get(i);
                List<Vec3d> path = paths.get(i);
                float alpha = arcAlpha(arc);
                double radius = arc.fork() ? ARC_HAZE * 0.7 : ARC_HAZE;
                FxRenderHelper.along(path, radius / 5, (point, along) -> {
                    float ends = (float) (0.25 + 0.75 * Math.sqrt(Math.sin(Math.PI * along)));
                    ColorMath.tessellate(Tessellator.instance, glow, ARC_HAZE_ALPHA * alpha * ends);
                    FxRenderHelper.glow(point, radius, 0);
                });
                if (arc.fork()) continue;
                int last = path.size() - 1;
                ColorMath.tessellate(Tessellator.instance, foot, ARC_FOOT_ALPHA * alpha);
                FxRenderHelper.glow(path.get(0), ARC_FOOT, 0);
                if (!arc.straightOut()) FxRenderHelper.glow(path.get(last), ARC_FOOT, 0);
                ColorMath.tessellate(Tessellator.instance, node, ARC_NODE_ALPHA * alpha);
                FxRenderHelper.glow(path.get(last / 2), ARC_NODE, 0);
                if (arc.alpha() > 0.85F) FxRenderHelper.glow(path.get(last / 4), ARC_NODE, 0);
            }
        });
        FxRenderHelper.renderGlow(Sprite.STREAK, () -> {
            for (int i = 0; i < arcs.size(); i++) arcLayer(
                paths.get(i),
                arcWidth(arcs.get(i), width) * ARC_HUG,
                hug,
                ARC_HUG_ALPHA * arcAlpha(arcs.get(i)));
        });
        FxRenderHelper.render(() -> {
            for (int i = 0; i < arcs.size(); i++)
                arcLayer(paths.get(i), arcWidth(arcs.get(i), width), WHITE, ARC_CORE_ALPHA * arcAlpha(arcs.get(i)));
        });
    }

    public static void arcLayer(List<Vec3d> path, double width, int rgb, float alpha) {
        int last = path.size() - 1;
        FxRenderHelper.path(
            path,
            index -> width * (0.45 + 0.55 * Math.sin(Math.PI * index / last)),
            index -> ColorMath.tessellate(Tessellator.instance, rgb, alpha * endFade(index, last)));
    }

    private static float endFade(int index, int last) {
        return (float) (0.35 + 0.65 * Math.min(1, Math.sin(Math.PI * index / last) / END_FADE));
    }

    private static double arcWidth(Arc arc, double width) {
        return arc.fork() ? width * 0.7 : width;
    }

    private static float arcAlpha(Arc arc) {
        return arc.fork() ? arc.alpha() * 0.75F : arc.alpha();
    }

    private static void spawnDots(List<Dot> dots, double age, MainConfig.Client settings, Vec3d center, Vec3d right,
        Vec3d up, Vec3d normal, int glow, int core, float partialTicks) {
        boolean falls = settings.portalGunOpenGravity > 0;
        for (Dot dot : dots) {
            if (dot.born() < age) continue;
            Mote mote = PortalGunOrbTrail
                .dot(
                    world(dot.origin(), center, right, up, normal),
                    dot.size(),
                    falls ? PortalGunOrbTrail.DOT_LIFETIME : FLOAT_LIFETIME,
                    glow,
                    core,
                    world(dot.velocity(), Vec3d.ZERO, right, up, normal),
                    settings.portalGunOpenDrag,
                    settings.portalGunOpenGravity)
                .hover(dot.hover())
                .delay(dot.born() - age);
            if (dot.twinkle()) mote.twinkle(dot.phase());
            FxMoteRenderer.spawn(mote, partialTicks);
        }
    }

    private static Vec3d world(Vec3d local, Vec3d center, Vec3d right, Vec3d up, Vec3d normal) {
        return center.add(right.scale(local.x()))
            .add(up.scale(local.y()))
            .add(normal.scale(local.z()));
    }

    private static double[] facePoint(Random r, Shape shape, double reach) {
        double angle = r.nextDouble() * 2 * Math.PI, k = Math.sqrt(r.nextDouble()) * reach;
        return new double[] { Math.cos(angle) * shape.halfWidth() * k, Math.sin(angle) * shape.halfHeight() * k };
    }

    private static double clamp(double value, double limit) {
        return Math.max(-limit, Math.min(limit, value));
    }

    private static double range(Random r, double low, double high) {
        return low + (high - low) * r.nextDouble();
    }
}
