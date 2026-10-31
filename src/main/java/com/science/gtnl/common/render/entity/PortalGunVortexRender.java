package com.science.gtnl.common.render.entity;

import java.util.List;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.gtnllib.client.fx.FxEasing;
import com.gtnllib.utils.MathUtils;
import com.science.gtnl.client.portalgun.PortalGunPortalFx.Pose;
import com.science.gtnl.common.entity.EntityPortalGunPortal;
import com.science.gtnl.utils.enums.PortalGunPortalEffect;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SideOnly(Side.CLIENT)
public class PortalGunVortexRender {

    public static final double RX = 0.62, RY = 1.0, FLAME_BASE = 0.9, FLAME_DEPTH = 0.22, RIM_INNER = 0.93,
        INTERIOR = 0.9, LIQUID_RADIUS = 1.08, OUTSIDE = 1.05, DROP_HEAD = 0.6, CLAMP_SOFTNESS = 0.12;
    public static final float GROW_START = 0.19F, GROW_SPAN = 0.5F;
    public static final int OPEN_TICKS = 16, FLAMES = 11, STREAMS = 2, STREAM_POINTS = 15, CAP_POINTS = 8,
        DROP_SLOTS = 8, COMPANIONS = 2, COMPANION_POINTS = 24;
    private static final int SEGMENTS = 128, FLAME_SALT = 10, DROP_SALT = 9, EDGE_SALT = 11, SPAWN_SALT = 12,
        LUMP_SALT = 13, COMPANION_SALT = 14, DISC_SIDES = 14, DROP_ARC = 19, DROP_TAIL = 6, WOBBLE_TICKS = 2;
    private static final double SPIRAL_SPIN = 2.5 * Math.PI, SPIRAL_SPAN = Math.PI, RIM_SPAN = 2.15,
        SPIRAL_WIDTH = 0.065, RIM_WIDTH = 0.055, TAIL_TAPER = 0.35, LIQUID_SPEED = 0.12, FLAME_GLOW = 0.16,
        OUTLINE = 0.01, RIM_CORE = 0.965, RIM_GLOW = 1.04, RIM_HAZE = 1.12, RIM_WAVE = 0.02, HALO_SPREAD = 2.0,
        SHINE_START = 0.68, SHINE_LENGTH = 0.3, TAILED_CHANCE = 0.25, SHINY_CHANCE = 0.6, DROP_DRAG = 0.45,
        GOLDEN = 0.618;
    private static final float[] ROSE = { 0.94F, 0.63F, 0.89F, 0.8F }, PINK = { 1F, 0.67F, 0.92F, 0.32F },
        CLEAR_PINK = { 1F, 0.67F, 0.92F, 0F }, WHITE = { 1F, 0.96F, 1F, 0.95F }, RIM_PINK = { 1F, 0.7F, 0.95F, 0.75F },
        HAZE_PINK = { 1F, 0.6F, 0.9F, 0.25F }, LAVENDER = { 0.86F, 0.71F, 0.98F, 0.95F },
        LAVENDER_GLOW = { 0.86F, 0.71F, 0.98F, 0.75F }, BODY = { 0.59F, 0.88F, 0.98F, 0.94F },
        DROP = { 0.69F, 0.93F, 0.99F, 0.94F }, COMPANION = { 0.75F, 0.94F, 0.99F, 0.88F },
        SHINE = { 1F, 1F, 1F, 0.94F }, HALO = { 0.59F, 0.88F, 0.98F, 0.26F }, DROP_HALO = { 0.59F, 0.88F, 0.98F, 0.2F },
        CLEAR_HALO = { 0.59F, 0.88F, 0.98F, 0F };

    public record Flow(List<double[]> points, float open) {}

    public record Outline(double[][] points, double[][] normals, double[] widths, double[] outer, double[] inner,
        double[] lumps) {

        private double[] at(int index, double side) {
            double scale = widths[index] * side * (side > 0 ? outer[index] : inner[index]);
            return new double[] { points[index][0] + normals[index][0] * scale,
                points[index][1] + normals[index][1] * scale };
        }
    }

    public record Drop(double x, double y, double size, double alpha, double alongX, double alongY, double stretch,
        double curve, double wobble, boolean tailed, boolean shiny) {}

    public static float size(float open, float close) {
        float grow = MathUtils.clampUnit((open - GROW_START) / GROW_SPAN), overshoot = 0.05F
            * (float) Math.sin(Math.PI * MathUtils.clampUnit((open - GROW_START - GROW_SPAN) / 0.25F));
        return (0.5F * grow + 0.5F * FxEasing.smoothStep(grow) + overshoot) * PortalGunPortalRender.shrink(close);
    }

    public static float fluid(float open, float close) {
        return FxEasing.smoothStep(open * 8) * PortalGunPortalRender.shrink(close);
    }

    public static float settle(float open) {
        return FxEasing.smoothStep(0.55F, 1, open);
    }

    public static float bloom(float open) {
        return FxEasing.smoothStep(0.55F, 0.9F, open);
    }

    public static double flameRadius(double angle, float ticks) {
        double reach = 0;
        for (int i = 0; i < FLAMES; i++) {
            double center = 2 * Math.PI * (i + 0.8 * flame(i, 0)) / FLAMES
                + 0.04 * Math.sin(ticks * 0.05 + 2 * Math.PI * flame(i, 1)), width = 0.06 + 0.2 * flame(i, 2),
                length = 0.3 + 0.7 * flame(i, 3),
                breathe = 0.78 + 0.22 * Math.sin(ticks * (0.1 + 0.12 * flame(i, 4)) + 2 * Math.PI * flame(i, 5)),
                lean = 0.7 * (flame(i, 6) - 0.5), offset = wrap(angle - center) / width;
            offset *= offset < 0 ? 1 + lean : 1 - lean;
            reach = Math.max(reach, length * breathe * Math.exp(-Math.pow(Math.abs(offset), 1.2 + 2.5 * flame(i, 7))));
        }
        double ripple = 0.05 * (1 + Math.sin(angle * 23 + ticks * 0.21))
            + 0.035 * (1 + Math.sin(angle * 41 - ticks * 0.17));
        return FLAME_BASE - FLAME_DEPTH * Math.min(1, reach + ripple * (1 - 0.5 * reach));
    }

    public static Flow flow(int stream, float open, float size, float ticks) {
        List<double[]> points = new ObjectArrayList<>(STREAM_POINTS);
        for (int i = 0; i < STREAM_POINTS; i++)
            points.add(spine(stream, open, size, ticks, i / (double) (STREAM_POINTS - 1)));
        return new Flow(points, open);
    }

    public static double thickness(float open, double along) {
        float settle = settle(open);
        return (SPIRAL_WIDTH * (1 - settle) + RIM_WIDTH * settle) * Math.min(1, 0.4 + open * 3)
            * (0.25 + 0.2 * settle + (0.9 - 0.4 * settle) * FxEasing.smoothStep((float) (along / 0.9)))
            * Math.pow(Math.min(1, along / TAIL_TAPER), 0.9);
    }

    public static Outline outline(Flow flow, int stream, float ticks) {
        List<double[]> spine = flow.points();
        int count = spine.size(), total = count + CAP_POINTS, step = (int) (ticks / WOBBLE_TICKS);
        double[][] points = new double[total][], normals = new double[total][];
        double[] widths = new double[total], outer = new double[total], inner = new double[total],
            lumps = new double[count];
        for (int i = 0; i < count; i++) {
            points[i] = spine.get(i);
            normals[i] = normal(spine, i);
            lumps[i] = lump(stream, step, i);
            widths[i] = thickness(flow.open(), i / (double) (count - 1)) * lumps[i];
        }
        double[] end = spine.get(count - 1), before = spine.get(count - 2);
        double tx = end[0] - before[0], ty = end[1] - before[1], length = Math.max(1.0E-9, Math.hypot(tx, ty)),
            cap = widths[count - 1];
        for (int k = 1; k <= CAP_POINTS; k++) {
            double t = k / (double) CAP_POINTS;
            points[count - 1 + k] = new double[] { end[0] + tx / length * cap * t, end[1] + ty / length * cap * t };
            normals[count - 1 + k] = normals[count - 1];
            widths[count - 1 + k] = cap * Math.sqrt(Math.max(0, 1 - t * t));
        }
        for (int i = 0; i < total; i++) {
            outer[i] = 1 + 0.28 * wave(stream, step, i, 0);
            inner[i] = 1 + 0.22 * wave(stream, step, i, 1);
        }
        return new Outline(points, normals, widths, outer, inner, lumps);
    }

    public static float companionShow(int stream, int index, float open) {
        float start = 0.04F + 0.16F * (float) companion(stream, index, 0);
        return FxEasing.smoothStep(start, start + 0.12F, open) * (1 - FxEasing.smoothStep(0.45F, 0.75F, open));
    }

    public static Outline companion(int stream, int index, float open, float size, float ticks) {
        double offset = (0.1 + 0.12 * companion(stream, index, 1)) * (index == 0 ? 1 : -0.7),
            lag = 0.15 + 0.4 * companion(stream, index, 2), span = 0.9 + 0.5 * companion(stream, index, 3),
            thick = 0.012 + 0.008 * companion(stream, index, 4);
        List<double[]> spine = new ObjectArrayList<>(COMPANION_POINTS);
        for (int i = 0; i < COMPANION_POINTS; i++)
            spine.add(spine(stream, open, size, ticks, i / (double) (COMPANION_POINTS - 1), lag, span, offset));
        double[][] points = spine.toArray(new double[0][]), normals = new double[COMPANION_POINTS][];
        double[] widths = new double[COMPANION_POINTS], ones = new double[COMPANION_POINTS];
        for (int i = 0; i < COMPANION_POINTS; i++) {
            double s = i / (double) (COMPANION_POINTS - 1);
            normals[i] = normal(spine, i);
            widths[i] = thick * Math.pow(Math.sin(Math.PI * Math.pow(s, 0.75)), 0.8);
            ones[i] = 1;
        }
        return new Outline(points, normals, widths, ones, ones, new double[0]);
    }

    public static float shine(double index) {
        return FxEasing
            .smoothStep((float) ((index - SHINE_START * (STREAM_POINTS - 1)) / (SHINE_LENGTH * (STREAM_POINTS - 1))));
    }

    public static List<Drop> droplets(int stream, float ticks, float size) {
        List<Drop> drops = new ObjectArrayList<>(DROP_SLOTS);
        for (int k = 0; k < DROP_SLOTS; k++) {
            double life = 6 + 8 * drop(stream, k), clock = ticks + life * ((k * GOLDEN + 0.5 * stream) % 1);
            int cycle = (int) Math.floor(clock / life), seed = k * 4096 + cycle;
            double age = clock - cycle * life, born = ticks - age;
            if (born < 0) continue;
            float bornOpen = openAt(born), bornSettle = settle(bornOpen);
            if (spawn(stream, seed, 2) > 1 - 0.75 * bornSettle) continue;
            double along = 0.2 + 0.8 * ((k * 5 % DROP_SLOTS) + spawn(stream, seed, 1)) / DROP_SLOTS;
            double[] from = spineAt(stream, born, along), next = spineAt(stream, born + 0.5, along),
                behind = spineAt(stream, born, Math.max(0, along - 0.03)),
                ahead = spineAt(stream, born, Math.min(1, along + 0.03));
            double gx = ahead[0] - behind[0], gy = ahead[1] - behind[1], gl = Math.max(1.0E-9, Math.hypot(gx, gy)),
                nx = -gy / gl, ny = gx / gl;
            if (nx * from[0] + ny * from[1] < 0) {
                nx = -nx;
                ny = -ny;
            }
            int side = spawn(stream, seed, 6) < 0.85 + 0.15 * bornSettle ? 1 : -1;
            double keep = (0.3 + 0.4 * spawn(stream, seed, 10)) * (1 - 0.7 * bornSettle),
                kick = (0.006 + 0.02 * spawn(stream, seed, 5)) * side, vx = (next[0] - from[0]) * 2 * keep + nx * kick,
                vy = (next[1] - from[1]) * 2 * keep + ny * kick, travel = (1 - Math.exp(-DROP_DRAG * age)) / DROP_DRAG,
                edge = side * thickness(bornOpen, along) * (0.6 + 0.6 * spawn(stream, seed, 4)),
                x = from[0] + nx * edge + vx * travel, y = from[1] + ny * edge + vy * travel,
                speed = Math.hypot(vx, vy) * Math.exp(-DROP_DRAG * age), t = age / life,
                inside = Math.hypot(x / RX, y / RY) / Math.max(size, 1.0E-3),
                velocity = Math.max(1.0E-9, Math.hypot(vx, vy)), ax = vx / velocity, ay = vy / velocity,
                inward = -ay * x + ax * y > 0 ? -1 : 1;
            boolean tailed = spawn(stream, seed, 13) < TAILED_CHANCE;
            drops.add(
                new Drop(
                    x,
                    y,
                    (tailed ? 0.02 + 0.04 * spawn(stream, seed, 3) : 0.014 + 0.022 * spawn(stream, seed, 3))
                        * (1 - 0.5 * t),
                    (1 - t * t * t) * FxEasing.smoothStep(0.9F, 1.05F, (float) inside),
                    ax,
                    ay,
                    Math.min(6, 3 + 2 * spawn(stream, seed, 7) + speed * 30),
                    (0.12 + 0.22 * spawn(stream, seed, 14)) * (spawn(stream, seed, 15) < 0.75 ? inward : -inward),
                    2 * Math.PI * spawn(stream, seed, 12),
                    tailed,
                    spawn(stream, seed, 8) < SHINY_CHANCE));
        }
        return drops;
    }

    public static void render(EntityPortalGunPortal portal, PortalGunPortalEffect effect, double x, double y, double z,
        float partialTicks, float close, Pose pose) {
        float ticks = portal.ticksExisted + partialTicks;
        float open = MathUtils.clampUnit(ticks / OPEN_TICKS), size = size(open, close);
        PortalGunPortalRender.begin(portal, x, y, z, pose);
        try {
            if (size > 0.01F) {
                GL11.glPushMatrix();
                GL11.glScalef(size, size, 1);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                int frame = PortalGunPortalRender.sheetFrame(effect, ticks);
                PortalGunPortalRender.bind(effect);
                PortalGunPortalRender.quad(
                    INTERIOR * RX,
                    INTERIOR * RY,
                    0,
                    1,
                    frame / (double) effect.frames,
                    (frame + 1) / (double) effect.frames,
                    1F);
                drawRim(ticks, open);
                GL11.glPopMatrix();
            }
            drawFluid(ticks, open, close, size);
        } finally {
            PortalGunPortalRender.end();
        }
    }

    private static void drawRim(float ticks, float open) {
        float bloom = bloom(open);
        double[] flames = new double[SEGMENTS + 1], wavy = new double[SEGMENTS + 1];
        for (int k = 0; k <= SEGMENTS; k++) {
            double angle = 2 * Math.PI * k / SEGMENTS;
            wavy[k] = RIM_WAVE * (1 - bloom) * Math.sin(9 * angle + ticks * 0.6);
            flames[k] = FLAME_BASE + (flameRadius(angle, ticks) - FLAME_BASE) * bloom + wavy[k];
        }
        float[] fill = mix(LAVENDER, ROSE, bloom), rim = mix(LAVENDER_GLOW, RIM_PINK, bloom),
            clearRim = { rim[0], rim[1], rim[2], 0F };
        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        tessellator.startDrawingQuads();
        band(flames, 0, ring(RIM_INNER, wavy), 0, fill, fill, 1F);
        tessellator.draw();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        tessellator.startDrawingQuads();
        band(flames, -FLAME_GLOW, flames, 0, CLEAR_PINK, PINK, bloom);
        band(flames, -OUTLINE, flames, OUTLINE, WHITE, WHITE, bloom);
        band(ring(RIM_INNER, wavy), 0, ring(RIM_CORE, wavy), 0, WHITE, WHITE, 1F);
        band(ring(RIM_CORE, wavy), 0, ring(RIM_GLOW, wavy), 0, rim, clearRim, 1F);
        band(ring(RIM_GLOW, wavy), 0, ring(RIM_HAZE, wavy), 0, HAZE_PINK, CLEAR_PINK, 1F);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private static void drawFluid(float ticks, float open, float close, float size) {
        float alpha = fluid(open, close);
        if (alpha <= 0.01F) return;
        List<Outline> outlines = new ObjectArrayList<>(STREAMS), companions = new ObjectArrayList<>();
        FloatArrayList companionAlphas = new FloatArrayList();
        List<Drop> drops = new ObjectArrayList<>();
        for (int stream = 0; stream < STREAMS; stream++) {
            outlines.add(outline(flow(stream, open, size, ticks), stream, ticks));
            drops.addAll(droplets(stream, ticks, size));
            for (int index = 0; index < COMPANIONS; index++) {
                float show = companionShow(stream, index, open) * alpha;
                if (show <= 0.01F) continue;
                companions.add(companion(stream, index, open, size, ticks));
                companionAlphas.add(show);
            }
        }
        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        tessellator.startDrawingQuads();
        for (int i = 0; i < companions.size(); i++) halo(companions.get(i), companionAlphas.getFloat(i) * 0.8F);
        for (Outline outline : outlines) halo(outline, alpha);
        for (Drop drop : drops) dropHalo(drop, alpha * (float) drop.alpha());
        tessellator.draw();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        tessellator.startDrawingQuads();
        for (int i = 0; i < companions.size(); i++) fill(companions.get(i), COMPANION, companionAlphas.getFloat(i));
        for (Outline outline : outlines) body(outline, alpha);
        for (Drop drop : drops) blob(drop, alpha * (float) drop.alpha());
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private static double[] spine(int stream, float open, float size, float ticks, double along) {
        return spine(stream, open, size, ticks, along, 0, 1, 0);
    }

    private static double[] spine(int stream, float open, float size, float ticks, double along, double lag,
        double span, double offset) {
        float settle = settle(open);
        double reach = (0.3 + 1.2 * FxEasing.smoothStep(open / 0.7F)) * (1 - settle) + LIQUID_RADIUS * size * settle,
            length = (SPIRAL_SPAN * (1 - settle) + RIM_SPAN * settle) * span,
            angle = SPIRAL_SPIN * (1 - (1 - open) * (1 - open)) + ticks * LIQUID_SPEED * settle
                + Math.PI * stream
                - lag
                - length * (1 - along),
            radius = reach * (1 - (1 - settle) * 0.45 * (1 - along)) * (1 + offset)
                + settle * 0.02 * size * Math.sin(3 * angle + ticks * 0.3);
        return ellipse(softMax(radius, OUTSIDE * size), angle);
    }

    private static double[] spineAt(int stream, double ticks, double along) {
        float open = openAt(ticks);
        return spine(stream, open, size(open, 0), (float) ticks, along);
    }

    private static float openAt(double ticks) {
        return MathUtils.clampUnit((float) ticks / OPEN_TICKS);
    }

    private static double softMax(double value, double floor) {
        return (value + floor + Math.sqrt((value - floor) * (value - floor) + CLAMP_SOFTNESS * CLAMP_SOFTNESS)) / 2;
    }

    private static double[] normal(List<double[]> spine, int index) {
        double[] before = spine.get(Math.max(0, index - 1)), after = spine.get(Math.min(spine.size() - 1, index + 1)),
            here = spine.get(index);
        double tx = after[0] - before[0], ty = after[1] - before[1], length = Math.max(1.0E-9, Math.hypot(tx, ty)),
            nx = -ty / length, ny = tx / length;
        return nx * here[0] + ny * here[1] < 0 ? new double[] { -nx, -ny } : new double[] { nx, ny };
    }

    private static void halo(Outline outline, float alpha) {
        for (int i = 1; i < outline.points().length; i++) {
            for (double side : new double[] { HALO_SPREAD, -HALO_SPREAD }) {
                color(HALO, alpha);
                vertex(outline.at(i - 1, 0));
                vertex(outline.at(i, 0));
                color(CLEAR_HALO, alpha);
                vertex(outline.at(i, side));
                vertex(outline.at(i - 1, side));
            }
        }
    }

    private static void fill(Outline outline, float[] rgba, float alpha) {
        color(rgba, alpha);
        for (int i = 1; i < outline.points().length; i++) {
            vertex(outline.at(i - 1, -1));
            vertex(outline.at(i - 1, 1));
            vertex(outline.at(i, 1));
            vertex(outline.at(i, -1));
        }
    }

    private static void body(Outline outline, float alpha) {
        fill(outline, BODY, alpha);
        for (int i = 1; i < outline.points().length; i++) {
            float from = shine(i - 1), to = shine(i);
            if (from <= 0.01F && to <= 0.01F) continue;
            shade(from, alpha);
            vertex(outline.at(i - 1, -spread(from)));
            vertex(outline.at(i - 1, spread(from) + 0.1));
            shade(to, alpha);
            vertex(outline.at(i, spread(to) + 0.1));
            vertex(outline.at(i, -spread(to)));
        }
        double[] lumps = outline.lumps();
        for (int i = 2; i < lumps.length - 3; i++) {
            if (lumps[i] < 1.08 || lumps[i] < lumps[i - 1] || lumps[i] < lumps[i + 1]) continue;
            double[] spot = outline.at(i, 0.4);
            disc(spot[0], spot[1], outline.widths()[i] * 0.3, SHINE, alpha);
        }
    }

    private static double spread(float shine) {
        return 0.25 + 0.6 * shine;
    }

    private static void shade(float shine, float alpha) {
        Tessellator.instance.setColorRGBA_F(
            BODY[0] + (SHINE[0] - BODY[0]) * shine,
            BODY[1] + (SHINE[1] - BODY[1]) * shine,
            BODY[2] + (SHINE[2] - BODY[2]) * shine,
            BODY[3] * alpha);
    }

    private static void blob(Drop drop, float alpha) {
        if (!drop.tailed()) {
            disc(drop.x(), drop.y(), drop.size(), DROP, alpha);
            if (drop.size() > 0.02)
                disc(drop.x() - drop.size() * 0.3, drop.y() + drop.size() * 0.3, drop.size() * 0.35, SHINE, alpha);
            return;
        }
        double head = drop.size() * DROP_HEAD, length = drop.size() * drop.stretch(), spread = Math.acos(head / length);
        double[][] arc = new double[DROP_ARC][], upper = new double[DROP_TAIL + 1][],
            lower = new double[DROP_TAIL + 1][];
        for (int v = 0; v < DROP_ARC; v++) {
            double angle = (Math.PI - spread) * (1 - 2.0 * v / (DROP_ARC - 1)),
                wobble = 1 + 0.05 * Math.sin(3 * v + drop.wobble());
            arc[v] = local(drop, head * Math.cos(angle) * wobble, head * Math.sin(angle) * wobble);
        }
        double[] top = { head * Math.cos(Math.PI - spread), head * Math.sin(Math.PI - spread) };
        for (int v = 0; v <= DROP_TAIL; v++) {
            double f = v / (double) DROP_TAIL, u = top[0] + (-length - top[0]) * f,
                w = top[1] * (1 - f) * (1 - 0.2 * Math.sin(Math.PI * f));
            upper[v] = local(drop, u, w);
            lower[v] = local(drop, u, -w);
        }
        upper[0] = arc[0];
        lower[0] = arc[DROP_ARC - 1];
        color(DROP, alpha);
        for (int v = 0; v < DROP_ARC; v++) {
            double[] from = arc[v], to = arc[(v + 1) % DROP_ARC];
            Tessellator.instance.addVertex(drop.x(), drop.y(), 0);
            vertex(from);
            vertex(to);
            Tessellator.instance.addVertex(drop.x(), drop.y(), 0);
        }
        for (int v = 1; v <= DROP_TAIL; v++) {
            vertex(upper[v - 1]);
            vertex(upper[v]);
            vertex(lower[v]);
            vertex(lower[v - 1]);
        }
        if (drop.shiny() && drop.size() > 0.022) disc(
            drop.x() + drop.alongX() * head * 0.15 - drop.alongY() * head * 0.3,
            drop.y() + drop.alongY() * head * 0.15 + drop.alongX() * head * 0.3,
            head * 0.4,
            SHINE,
            alpha);
    }

    private static double[] local(Drop drop, double u, double w) {
        double length = drop.size() * drop.stretch(),
            bent = w + drop.curve() * length * Math.pow(Math.min(0, u) / length, 2);
        return new double[] { drop.x() + drop.alongX() * u - drop.alongY() * bent,
            drop.y() + drop.alongY() * u + drop.alongX() * bent };
    }

    private static void dropHalo(Drop drop, float alpha) {
        double radius = drop.size() * (drop.tailed() ? DROP_HEAD * 3 : 2);
        for (int k = 0; k < DISC_SIDES; k++) {
            double a0 = 2 * Math.PI * k / DISC_SIDES, a1 = 2 * Math.PI * (k + 1) / DISC_SIDES;
            color(DROP_HALO, alpha);
            Tessellator.instance.addVertex(drop.x(), drop.y(), 0);
            Tessellator.instance.addVertex(drop.x(), drop.y(), 0);
            color(CLEAR_HALO, alpha);
            Tessellator.instance.addVertex(drop.x() + radius * Math.cos(a1), drop.y() + radius * Math.sin(a1), 0);
            Tessellator.instance.addVertex(drop.x() + radius * Math.cos(a0), drop.y() + radius * Math.sin(a0), 0);
        }
    }

    private static void disc(double x, double y, double radius, float[] rgba, float alpha) {
        color(rgba, alpha);
        for (int k = 0; k < DISC_SIDES; k++) {
            double a0 = 2 * Math.PI * k / DISC_SIDES, a1 = 2 * Math.PI * (k + 1) / DISC_SIDES;
            Tessellator.instance.addVertex(x, y, 0);
            Tessellator.instance.addVertex(x + radius * Math.cos(a0), y + radius * Math.sin(a0), 0);
            Tessellator.instance.addVertex(x + radius * Math.cos(a1), y + radius * Math.sin(a1), 0);
            Tessellator.instance.addVertex(x, y, 0);
        }
    }

    private static void band(double[] inner, double innerOffset, double[] outer, double outerOffset, float[] innerColor,
        float[] outerColor, float alpha) {
        if (alpha <= 0.01F) return;
        for (int k = 0; k < SEGMENTS; k++) {
            double a0 = 2 * Math.PI * k / SEGMENTS, a1 = 2 * Math.PI * (k + 1) / SEGMENTS;
            color(innerColor, alpha);
            vertex(ellipse(inner[k] + innerOffset, a0));
            color(outerColor, alpha);
            vertex(ellipse(outer[k] + outerOffset, a0));
            vertex(ellipse(outer[k + 1] + outerOffset, a1));
            color(innerColor, alpha);
            vertex(ellipse(inner[k + 1] + innerOffset, a1));
        }
    }

    private static double[] ring(double radius, double[] wavy) {
        double[] radii = new double[SEGMENTS + 1];
        for (int k = 0; k <= SEGMENTS; k++) radii[k] = radius + wavy[k];
        return radii;
    }

    private static float[] mix(float[] from, float[] to, float blend) {
        float[] out = new float[4];
        for (int i = 0; i < 4; i++) out[i] = from[i] + (to[i] - from[i]) * blend;
        return out;
    }

    private static double lump(int stream, int step, int index) {
        double x = index * 0.5;
        int cell = (int) Math.floor(x);
        double blend = FxEasing.smoothStep((float) (x - cell)),
            from = MathUtils.hashUnit(LUMP_SALT, stream, step * 64 + cell, 0),
            to = MathUtils.hashUnit(LUMP_SALT, stream, step * 64 + cell + 1, 0);
        return 0.6 + 0.7 * (from + (to - from) * blend);
    }

    private static double[] ellipse(double radius, double angle) {
        return new double[] { radius * RX * Math.cos(angle), radius * RY * Math.sin(angle) };
    }

    private static void vertex(double[] point) {
        Tessellator.instance.addVertex(point[0], point[1], 0);
    }

    private static void color(float[] rgba, float alpha) {
        Tessellator.instance.setColorRGBA_F(rgba[0], rgba[1], rgba[2], rgba[3] * alpha);
    }

    private static double wave(int stream, int step, int index, int salt) {
        double x = index * 0.35;
        int cell = (int) Math.floor(x);
        double blend = FxEasing.smoothStep((float) (x - cell)),
            from = MathUtils.hashUnit(EDGE_SALT, stream, step * 64 + cell, salt),
            to = MathUtils.hashUnit(EDGE_SALT, stream, step * 64 + cell + 1, salt);
        return 2 * (from + (to - from) * blend) - 1;
    }

    private static double wrap(double angle) {
        return angle - 2 * Math.PI * Math.floor((angle + Math.PI) / (2 * Math.PI));
    }

    private static double flame(int flame, int salt) {
        return MathUtils.hashUnit(FLAME_SALT, flame, 0, salt);
    }

    private static double companion(int stream, int index, int salt) {
        return MathUtils.hashUnit(COMPANION_SALT, stream, index, salt);
    }

    private static double drop(int stream, int drop) {
        return MathUtils.hashUnit(DROP_SALT, stream, drop, DROP_SALT);
    }

    private static double spawn(int stream, int seed, int salt) {
        return MathUtils.hashUnit(SPAWN_SALT, stream, seed, salt);
    }
}
