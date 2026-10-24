package com.science.gtnl.common.render.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;

import com.gtnllib.client.fx.FxEasing;
import com.gtnllib.client.fx.world.FxRenderHelper;
import com.gtnllib.client.fx.world.FxRenderHelper.Sprite;
import com.gtnllib.utils.ColorMath;
import com.gtnllib.utils.MathUtils;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.client.portalgun.PortalGunPortalFx;
import com.science.gtnl.client.portalgun.PortalGunPortalFx.Pose;
import com.science.gtnl.common.entity.EntityPortalGunPortal;
import com.science.gtnl.utils.enums.PortalGunPortalEffect;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PortalGunDoorRender {

    public static final double DOOR_HALF_WIDTH = 0.5625, DOOR_HALF_HEIGHT = 1.0, DOOR_INSET = 0.125, BEAM_WIDTH = 0.09,
        FRAME_HAZE = 0.11, ARC_LIFT = 0.012, ARC_REACH = 0.06, RUN_MIN = 0.05, RUN_MAX = 0.085, MIN_EDGE = 0.005;
    public static final float DOOR_SLIT = 0.3F, ARC_HOP_TICKS = 1.5F;
    public static final int DOOR_OPEN_TICKS = 10, ARC_POINTS = 13, MIN_HOPS = 2, MAX_HOPS = 4;
    private static final double DOOR_RIM = 0.03125, FRONT = 0.004, FRAME_GLOW = 0.04, FRAME_CORE = 0.014,
        RAY_LENGTH = 0.2, GLINT_WIDTH = 0.045, GLINT_MIN = 0.12, GLINT_MAX = 0.45, CORNER_GLOW = 0.11,
        RUNS_PER_HOP = 1.05, INWARD_CHANCE = 0.2, INWARD = 0.4, ARC_HAZE = 0.045, ARC_HUG = 0.02, ARC_CORE = 0.007,
        ARC_FOOT = 0.04, SURGE_GLOW = 0.06, SURGE_CORE = 0.02;
    private static final float GLINT_SPEED = 0.35F, FRAME_HAZE_ALPHA = 0.45F, FRAME_GLOW_ALPHA = 0.9F, RAY_ALPHA = 0.9F,
        ARC_HAZE_ALPHA = 0.35F, ARC_HUG_ALPHA = 0.85F, ARC_FOOT_ALPHA = 0.4F, SURGE_GLOW_ALPHA = 0.55F,
        SURGE_CORE_ALPHA = 0.6F;
    private static final int CYAN = 0x80E8FF, WHITE = 0xFFFFFF, CORNER = 0xD9F7FF, RUN_SALT = 31, SHAPE_SALT = 32,
        DIVE_SALT = 33, FLASH_SALT = 34;

    public record FrameArc(List<Vec3d> path, List<Vec3d> surge, float flash) {}

    private PortalGunDoorRender() {}

    public static double cornerGlowRadius(float glint) {
        return CORNER_GLOW * (0.6 + 0.6 * glint);
    }

    public static float doorWidth(float open, float close) {
        return FxEasing.smoothStep(open / DOOR_SLIT) * (1 - FxEasing.smoothStep(1 - DOOR_SLIT, 1, close));
    }

    public static float doorHeight(float open, float close) {
        return FxEasing.smoothStep(DOOR_SLIT, 1, open) * (1 - FxEasing.smoothStep(close / (1 - DOOR_SLIT)));
    }

    /**
     * The arcs crackling on the frame at this moment, in the door plane (x right, y up, frame centred on 0). Each run
     * appears at a random spot and crawls 2 to 4 hops along the frame, round corners too, hugging its outer edge; its
     * surge is the stretch of frame it passes over. A flat slit has none.
     */
    public static List<FrameArc> frameArcs(double halfWidth, double halfHeight, float ticks) {
        List<FrameArc> arcs = new ArrayList<>();
        if (halfWidth <= MIN_EDGE || halfHeight <= MIN_EDGE) return arcs;
        long now = (long) Math.floor(ticks / ARC_HOP_TICKS);
        for (long slot = now - MAX_HOPS + 1; slot <= now; slot++) {
            int count = (int) (RUNS_PER_HOP + MathUtils.hashUnit(slot, 0, 0, RUN_SALT)), step = (int) (now - slot);
            for (int run = 0; run < count; run++) {
                int hops = MIN_HOPS + (int) (MathUtils.hashUnit(slot, run, 1, RUN_SALT) * (MAX_HOPS - MIN_HOPS + 1));
                if (step >= hops) continue;
                double length = (RUN_MIN + (RUN_MAX - RUN_MIN) * MathUtils.hashUnit(slot, run, 2, RUN_SALT))
                    * (MathUtils.hashUnit(slot, run, 3, RUN_SALT) < 0.5 ? 1 : -1),
                    start = MathUtils.hashUnit(slot, run, 4, RUN_SALT) + length * step;
                arcs.add(arc(halfWidth, halfHeight, start, start + length, slot * MAX_HOPS * 8 + run * 8 + step));
            }
        }
        return arcs;
    }

    /**
     * A point on the frame, {@code along} going round it once from the top-left corner clockwise, pushed
     * {@code outward} away from the door.
     */
    public static Vec3d framePoint(double halfWidth, double halfHeight, double along, double outward) {
        double top = 2 * halfWidth, side = 2 * halfHeight, walked = (along - Math.floor(along)) * 2 * (top + side);
        if (walked < top) return new Vec3d(-halfWidth + walked, halfHeight + outward, 0);
        walked -= top;
        if (walked < side) return new Vec3d(halfWidth + outward, halfHeight - walked, 0);
        walked -= side;
        if (walked < top) return new Vec3d(halfWidth - walked, -halfHeight - outward, 0);
        return new Vec3d(-halfWidth - outward, -halfHeight + walked - top, 0);
    }

    private static FrameArc arc(double halfWidth, double halfHeight, double start, double end, long seed) {
        List<Vec3d> path = new ArrayList<>(ARC_POINTS), surge = new ArrayList<>(ARC_POINTS);
        for (int i = 0; i < ARC_POINTS; i++) {
            double t = i / (double) (ARC_POINTS - 1), along = start + (end - start) * t;
            double outward = (ARC_LIFT + ARC_REACH * MathUtils.hashUnit(seed, i, 0, SHAPE_SALT))
                * Math.pow(Math.sin(Math.PI * t), 0.6);
            if (MathUtils.hashUnit(seed, i, 0, DIVE_SALT) < INWARD_CHANCE) outward *= -INWARD;
            path.add(framePoint(halfWidth, halfHeight, along, outward));
            surge.add(framePoint(halfWidth, halfHeight, along, 0));
        }
        return new FrameArc(path, surge, (float) (0.75 + 0.25 * MathUtils.hashUnit(seed, 0, 0, FLASH_SALT)));
    }

    public static void render(EntityPortalGunPortal portal, PortalGunPortalEffect effect, double x, double y, double z,
        float partialTicks, float close, Pose pose) {
        float ticks = portal.ticksExisted + partialTicks;
        float open = MathUtils.clampUnit(ticks / DOOR_OPEN_TICKS);
        double halfWidth = DOOR_HALF_WIDTH * doorWidth(open, close),
            halfHeight = DOOR_HALF_HEIGHT * doorHeight(open, close);
        if (halfWidth < MIN_EDGE) return;
        double innerWidth = halfWidth - DOOR_INSET, innerHeight = halfHeight - DOOR_INSET;
        PortalGunPortalRender.begin(portal, x, y, z, pose);
        try {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (innerWidth > 0 && innerHeight > 0) {
                int frame = PortalGunPortalRender.sheetFrame(effect, ticks);
                double u = Math.min(1, innerWidth / effect.halfWidth()) / 2,
                    v = Math.min(1, innerHeight / effect.halfHeight()) / 2;
                PortalGunPortalRender.bind(effect);
                PortalGunPortalRender.quad(
                    innerWidth,
                    innerHeight,
                    0.5 - u,
                    0.5 + u,
                    (frame + 0.5 - v) / effect.frames,
                    (frame + 0.5 + v) / effect.frames,
                    1F);
            }
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            band(
                innerWidth + DOOR_RIM,
                innerHeight + DOOR_RIM,
                halfWidth,
                halfHeight,
                0.04F,
                0.02F,
                0.06F,
                0.04F,
                0.02F,
                0.06F,
                0.95F);
            band(
                innerWidth,
                innerHeight,
                innerWidth + DOOR_RIM,
                innerHeight + DOOR_RIM,
                1F,
                0.66F,
                0.2F,
                0.93F,
                0.33F,
                0.19F,
                1F);
            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        } finally {
            PortalGunPortalRender.end();
        }
        Vec3d up = Vec3d.of(portal.up()), facing = Vec3d.of(portal.facing()), right = up.cross(facing);
        Vec3d center = new Vec3d(
            x + RenderManager.renderPosX,
            y + RenderManager.renderPosY,
            z + RenderManager.renderPosZ).add(facing.scale(FRONT + pose.push()));
        frame(center, right, up, halfWidth * pose.sx(), halfHeight * pose.sy(), ticks);
    }

    private static void frame(Vec3d center, Vec3d right, Vec3d up, double halfWidth, double halfHeight, float ticks) {
        double[] sx = { -1, 1, 1, -1 }, sy = { 1, 1, -1, -1 };
        Vec3d[] corners = new Vec3d[4];
        float[] glints = new float[4];
        for (int i = 0; i < 4; i++) {
            corners[i] = world(center, right, up, sx[i] * halfWidth, sy[i] * halfHeight);
            glints[i] = (float) (0.5 + 0.5 * Math.sin(ticks * GLINT_SPEED + i * Math.PI / 2));
        }
        // Why: one joined loop, so the corners meet cleanly instead of four separate beams overlapping there.
        List<Vec3d> outline = halfHeight > MIN_EDGE
            ? List.of(
                world(center, right, up, 0, halfHeight),
                corners[1],
                corners[2],
                corners[3],
                corners[0],
                world(center, right, up, 0, halfHeight))
            : List.of(world(center, right, up, -halfWidth, 0), world(center, right, up, halfWidth, 0));
        List<FrameArc> arcs = new ArrayList<>();
        for (FrameArc arc : frameArcs(halfWidth, halfHeight, ticks)) arcs.add(
            new FrameArc(world(center, right, up, arc.path()), world(center, right, up, arc.surge()), arc.flash()));
        int glow = ColorMath.lerp(CYAN, WHITE, 0.5F), surge = ColorMath.lerp(CYAN, WHITE, 0.6F);
        FxRenderHelper.renderGlow(Sprite.STREAK, () -> {
            steady(outline, FRAME_HAZE, CYAN, FRAME_HAZE_ALPHA);
            steady(outline, FRAME_GLOW, glow, FRAME_GLOW_ALPHA);
            steady(outline, FRAME_CORE, WHITE, 1F);
            ColorMath.tessellate(Tessellator.instance, CYAN, RAY_ALPHA);
            for (int i = 0; i < 4; i++) {
                FxRenderHelper.glowRibbon(corners[i], corners[i].add(right.scale(sx[i] * RAY_LENGTH)), BEAM_WIDTH, 0);
                FxRenderHelper.glowRibbon(corners[i], corners[i].add(up.scale(sy[i] * RAY_LENGTH)), BEAM_WIDTH, 0);
                double glint = GLINT_MIN + (GLINT_MAX - GLINT_MIN) * glints[i];
                for (Vec3d arm : new Vec3d[] { right, right.scale(-1), up, up.scale(-1) })
                    FxRenderHelper.glowRibbon(corners[i], corners[i].add(arm.scale(glint)), GLINT_WIDTH, 0);
            }
            for (FrameArc arc : arcs) {
                surge(arc.surge(), SURGE_GLOW, surge, SURGE_GLOW_ALPHA * arc.flash());
                surge(arc.surge(), SURGE_CORE, WHITE, SURGE_CORE_ALPHA * arc.flash());
                PortalGunPortalFx.arcLayer(arc.path(), ARC_HAZE, CYAN, ARC_HAZE_ALPHA * arc.flash());
                PortalGunPortalFx.arcLayer(arc.path(), ARC_HUG, glow, ARC_HUG_ALPHA * arc.flash());
                PortalGunPortalFx.arcLayer(arc.path(), ARC_CORE, WHITE, arc.flash());
            }
        });
        FxRenderHelper.renderGlow(Sprite.RADIAL, () -> {
            for (int i = 0; i < 4; i++) {
                ColorMath.tessellate(Tessellator.instance, CORNER, 0.5F + 0.5F * glints[i]);
                FxRenderHelper.glow(corners[i], cornerGlowRadius(glints[i]), ticks * 0.05F + i);
            }
            for (FrameArc arc : arcs) {
                ColorMath.tessellate(Tessellator.instance, glow, ARC_FOOT_ALPHA * arc.flash());
                FxRenderHelper.glow(
                    arc.path()
                        .get(0),
                    ARC_FOOT,
                    0);
                FxRenderHelper.glow(
                    arc.path()
                        .get(ARC_POINTS - 1),
                    ARC_FOOT,
                    0);
            }
        });
    }

    private static void steady(List<Vec3d> path, double width, int rgb, float alpha) {
        FxRenderHelper.path(path, index -> width, index -> ColorMath.tessellate(Tessellator.instance, rgb, alpha));
    }

    private static void surge(List<Vec3d> path, double width, int rgb, float alpha) {
        int last = path.size() - 1;
        FxRenderHelper.path(
            path,
            index -> width * (0.5 + 0.5 * Math.sin(Math.PI * index / last)),
            index -> ColorMath.tessellate(Tessellator.instance, rgb, alpha * (float) Math.sin(Math.PI * index / last)));
    }

    private static Vec3d world(Vec3d center, Vec3d right, Vec3d up, double x, double y) {
        return center.add(right.scale(x))
            .add(up.scale(y));
    }

    private static List<Vec3d> world(Vec3d center, Vec3d right, Vec3d up, List<Vec3d> local) {
        List<Vec3d> points = new ArrayList<>(local.size());
        for (Vec3d point : local) points.add(world(center, right, up, point.x(), point.y()));
        return points;
    }

    private static void band(double innerWidth, double innerHeight, double outerWidth, double outerHeight, float topRed,
        float topGreen, float topBlue, float bottomRed, float bottomGreen, float bottomBlue, float alpha) {
        double width = Math.max(0, innerWidth), height = Math.max(0, innerHeight);
        float[] top = { topRed, topGreen, topBlue, alpha }, bottom = { bottomRed, bottomGreen, bottomBlue, alpha };
        rect(-outerWidth, height, outerWidth, outerHeight, top, top);
        rect(-outerWidth, -outerHeight, outerWidth, -height, bottom, bottom);
        rect(-outerWidth, -height, -width, height, bottom, top);
        rect(width, -height, outerWidth, height, bottom, top);
    }

    private static void rect(double x0, double y0, double x1, double y1, float[] lower, float[] upper) {
        if (x1 <= x0 || y1 <= y0) return;
        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorRGBA_F(lower[0], lower[1], lower[2], lower[3]);
        tessellator.addVertex(x0, y0, 0);
        tessellator.addVertex(x1, y0, 0);
        tessellator.setColorRGBA_F(upper[0], upper[1], upper[2], upper[3]);
        tessellator.addVertex(x1, y1, 0);
        tessellator.addVertex(x0, y1, 0);
    }
}
