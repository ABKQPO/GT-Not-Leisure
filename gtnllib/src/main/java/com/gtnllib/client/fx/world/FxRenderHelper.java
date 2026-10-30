package com.gtnllib.client.fx.world;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntToDoubleFunction;
import java.util.function.ObjDoubleConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.gtnllib.utils.Vec3d;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;

public class FxRenderHelper {

    public enum Sprite {

        RADIAL(128, false),
        STREAK(128, false),
        PIXEL_DOT(8, true),
        PIXEL_PUFF(14, true),
        PIXEL_RING(32, true),
        SOFT_RING(64, false),
        PIXEL_SPARK(5, true);

        private final int size;
        private final boolean pixel;

        Sprite(int size, boolean pixel) {
            this.size = size;
            this.pixel = pixel;
        }
    }

    public record SegmentSides(Vec3d start, Vec3d end) {}

    private static final Reference2IntOpenHashMap<Sprite> TEXTURES = new Reference2IntOpenHashMap<>();

    private static final double DEGENERATE = 1.0E-6, MITER_LIMIT = 0.5;
    private static final FloatBuffer MODELVIEW = BufferUtils.createFloatBuffer(16);
    private static Vec3d localFacing;

    public static void render(Runnable draw) {
        batch(null, draw, true);
    }

    public static void renderGlow(Sprite sprite, Runnable draw) {
        batch(() -> bind(sprite), draw, true);
    }

    public static void renderLocal(Runnable draw) {
        batch(null, draw, false);
    }

    public static void renderLocalGlow(Sprite sprite, Runnable draw) {
        batch(() -> bind(sprite), draw, false);
    }

    public static void renderTextured(ResourceLocation texture, Runnable draw) {
        batch(() -> {
            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(texture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        }, draw, true);
    }

    public static void clearSprites() {
        for (IntIterator it = TEXTURES.values()
            .iterator(); it.hasNext();) GL11.glDeleteTextures(it.nextInt());
        TEXTURES.clear();
    }

    public static void vertex(Vec3d vector) {
        Tessellator.instance.addVertex(vector.x(), vector.y(), vector.z());
    }

    public static void ribbon(Vec3d from, Vec3d to, double width, float red, float green, float blue, float alpha) {
        Vec3d side = ribbonAxis(from, to).scale(width);
        Tessellator.instance.setColorRGBA_F(red, green, blue, alpha);
        vertex(from.subtract(side));
        vertex(from.add(side));
        vertex(to.add(side));
        vertex(to.subtract(side));
    }

    public static void path(List<Vec3d> points, double width) {
        path(points, index -> width, index -> {});
    }

    public static void path(List<Vec3d> points, IntToDoubleFunction width, IntConsumer shade) {
        int count = points.size();
        if (count < 2) return;
        List<Vec3d> axes = new ObjectArrayList<>(count - 1);
        for (int i = 1; i < count; i++) axes.add(ribbonAxis(points.get(i - 1), points.get(i)));
        List<SegmentSides> sides = pathSides(axes);
        for (int i = 1; i < count; i++) {
            Vec3d from = points.get(i - 1), to = points.get(i);
            Vec3d fromSide = sides.get(i - 1)
                .start()
                .scale(width.applyAsDouble(i - 1)),
                toSide = sides.get(i - 1)
                    .end()
                    .scale(width.applyAsDouble(i));
            double startV = (i - 1) / (double) (count - 1), endV = i / (double) (count - 1);
            shade.accept(i - 1);
            uvVertex(from.subtract(fromSide), 0, startV);
            uvVertex(from.add(fromSide), 1, startV);
            shade.accept(i);
            uvVertex(to.add(toSide), 1, endV);
            uvVertex(to.subtract(toSide), 0, endV);
        }
    }

    public static List<SegmentSides> pathSides(List<Vec3d> axes) {
        List<SegmentSides> sides = new ObjectArrayList<>(axes.size());
        for (int i = 0; i < axes.size(); i++) sides.add(new SegmentSides(join(axes, i, i - 1), join(axes, i, i + 1)));
        return sides;
    }

    public static void along(List<Vec3d> points, double spacing, ObjDoubleConsumer<Vec3d> each) {
        double total = 0;
        for (int i = 1; i < points.size(); i++) total += points.get(i)
            .subtract(points.get(i - 1))
            .length();
        if (spacing <= 0 || total < DEGENERATE) return;
        double walked = 0, carry = 0;
        for (int i = 1; i < points.size(); i++) {
            Vec3d from = points.get(i - 1), step = points.get(i)
                .subtract(from);
            double length = step.length(), t = carry;
            if (length < DEGENERATE) continue;
            for (; t <= length; t += spacing) each.accept(from.add(step.scale(t / length)), (walked + t) / total);
            carry = t - length;
            walked += length;
        }
    }

    private static Vec3d join(List<Vec3d> axes, int own, int other) {
        Vec3d axis = axes.get(own);
        if (other < 0 || other >= axes.size()) return axis;
        Vec3d sum = axis.add(axes.get(other));
        if (sum.length() < DEGENERATE) return axis;
        Vec3d join = sum.normalize();
        double cosine = join.dot(axis);
        return cosine < MITER_LIMIT ? axis : join.scale(1 / cosine);
    }

    public static void glow(Vec3d center, double radius, float radians) {
        Vec3d direction = FxCamera.position()
            .subtract(center)
            .normalize();
        Vec3d right = Vec3d.rightOf(direction);
        Vec3d up = right.cross(direction)
            .normalize();
        if (up.length() < DEGENERATE) up = new Vec3d(0, 1, 0);
        float cosine = (float) Math.cos(radians), sine = (float) Math.sin(radians);
        Vec3d sideways = right.scale(cosine)
            .add(up.scale(sine))
            .scale(radius);
        Vec3d upward = up.scale(cosine)
            .subtract(right.scale(sine))
            .scale(radius);
        quad(center, sideways, upward);
    }

    public static void localGlow(Vec3d center, double radius) {
        quad(center, viewAxis(0).scale(radius), viewAxis(1).scale(radius));
    }

    public static void planeGlow(Vec3d center, Vec3d normal, double radius) {
        Vec3d along = Vec3d.rightOf(normal.normalize())
            .scale(radius);
        quad(center, along, along.cross(normal.normalize()));
    }

    private static void quad(Vec3d center, Vec3d sideways, Vec3d upward) {
        uvVertex(
            center.subtract(sideways)
                .subtract(upward),
            0,
            0);
        uvVertex(
            center.add(sideways)
                .subtract(upward),
            1,
            0);
        uvVertex(
            center.add(sideways)
                .add(upward),
            1,
            1);
        uvVertex(
            center.subtract(sideways)
                .add(upward),
            0,
            1);
    }

    public static void sphere(Vec3d center, double radius, int longitudes, int latitudes, float red, float green,
        float blue, float alpha, double rim) {
        int rings = Math.max(3, latitudes), segments = Math.max(4, longitudes);
        Vec3d view = FxCamera.position()
            .subtract(center)
            .normalize();
        for (int ring = 0; ring < rings; ring++) {
            double startV = ring / (double) rings, endV = (ring + 1) / (double) rings;
            double startLatitude = Math.PI * (startV - 0.5), endLatitude = Math.PI * (endV - 0.5);
            double startRadius = Math.cos(startLatitude), endRadius = Math.cos(endLatitude);
            double startHeight = Math.sin(startLatitude), endHeight = Math.sin(endLatitude);
            for (int segment = 0; segment < segments; segment++) {
                double startU = segment / (double) segments, endU = (segment + 1) / (double) segments;
                double startAngle = startU * Math.PI * 2, endAngle = endU * Math.PI * 2;
                double startCos = Math.cos(startAngle), startSin = Math.sin(startAngle);
                double endCos = Math.cos(endAngle), endSin = Math.sin(endAngle);
                shade(
                    center,
                    startCos * startRadius,
                    startHeight,
                    startSin * startRadius,
                    view,
                    rim,
                    red,
                    green,
                    blue,
                    alpha);
                uvVertex(
                    center.add(startCos * startRadius * radius, startHeight * radius, startSin * startRadius * radius),
                    startU,
                    startV);
                shade(
                    center,
                    endCos * startRadius,
                    startHeight,
                    endSin * startRadius,
                    view,
                    rim,
                    red,
                    green,
                    blue,
                    alpha);
                uvVertex(
                    center.add(endCos * startRadius * radius, startHeight * radius, endSin * startRadius * radius),
                    endU,
                    startV);
                shade(center, endCos * endRadius, endHeight, endSin * endRadius, view, rim, red, green, blue, alpha);
                uvVertex(
                    center.add(endCos * endRadius * radius, endHeight * radius, endSin * endRadius * radius),
                    endU,
                    endV);
                shade(
                    center,
                    startCos * endRadius,
                    endHeight,
                    startSin * endRadius,
                    view,
                    rim,
                    red,
                    green,
                    blue,
                    alpha);
                uvVertex(
                    center.add(startCos * endRadius * radius, endHeight * radius, startSin * endRadius * radius),
                    startU,
                    endV);
            }
        }
    }

    private static void shade(Vec3d center, double normalX, double normalY, double normalZ, Vec3d view, double rim,
        float red, float green, float blue, float alpha) {
        double facing = (normalX * view.x() + normalY * view.y() + normalZ * view.z());
        double factor = facing <= 0 ? 0 : Math.pow(facing, rim);
        Tessellator.instance.setColorRGBA_F(red, green, blue, (float) (alpha * factor));
    }

    public static void glowRibbon(Vec3d from, Vec3d to, double fromWidth, double toWidth) {
        uvRibbon(from, to, fromWidth, toWidth, 0, 1);
    }

    public static void uvRibbon(Vec3d from, Vec3d to, double fromWidth, double toWidth, double startV, double endV) {
        Vec3d axis = ribbonAxis(from, to);
        Vec3d fromSide = axis.scale(fromWidth), toSide = axis.scale(toWidth);
        uvVertex(from.subtract(fromSide), 0, startV);
        uvVertex(from.add(fromSide), 1, startV);
        uvVertex(to.add(toSide), 1, endV);
        uvVertex(to.subtract(toSide), 0, endV);
    }

    private static Vec3d ribbonAxis(Vec3d from, Vec3d to) {
        Vec3d toCamera = localFacing != null ? localFacing
            : FxCamera.position()
                .subtract(
                    from.add(to)
                        .scale(0.5));
        Vec3d axis = to.subtract(from)
            .cross(toCamera)
            .normalize();
        return axis.length() < DEGENERATE ? Vec3d.rightOf(
            to.subtract(from)
                .normalize())
            : axis;
    }

    private static Vec3d viewAxis(int row) {
        MODELVIEW.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW);
        return new Vec3d(MODELVIEW.get(row), MODELVIEW.get(4 + row), MODELVIEW.get(8 + row)).normalize();
    }

    private static void uvVertex(Vec3d vector, double u, double v) {
        Tessellator.instance.addVertexWithUV(vector.x(), vector.y(), vector.z(), u, v);
    }

    private static void batch(Runnable bind, Runnable draw, boolean world) {
        float lightX = OpenGlHelper.lastBrightnessX, lightY = OpenGlHelper.lastBrightnessY;
        if (world) FxCamera.capture();
        GL11.glPushMatrix();
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        try {
            if (world)
                GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
            else localFacing = viewAxis(2);
            if (bind == null) GL11.glDisable(GL11.GL_TEXTURE_2D);
            else {
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                bind.run();
            }
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GL11.glDepthMask(false);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            Tessellator.instance.startDrawingQuads();
            draw.run();
            Tessellator.instance.draw();
        } finally {
            localFacing = null;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    private static void bind(Sprite sprite) {
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture(sprite));
    }

    private static int texture(Sprite sprite) {
        int cached = TEXTURES.getInt(sprite);
        if (cached != 0) return cached;
        int handle = GL11.glGenTextures();
        TextureUtil.uploadTextureImage(handle, build(sprite));
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, handle);
        int filter = sprite.pixel ? GL11.GL_NEAREST : GL11.GL_LINEAR;
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        TEXTURES.put(sprite, handle);
        return handle;
    }

    private static BufferedImage build(Sprite sprite) {
        int size = sprite.size;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < size; y++)
            for (int x = 0; x < size; x++) image.setRGB(x, y, Math.round(alpha(sprite, x, y) * 255F) << 24 | 0xFFFFFF);
        return image;
    }

    public static float alpha(Sprite sprite, int x, int y) {
        float center = (sprite.size - 1) / 2F;
        float offsetX = (x - center) / center, offsetY = (y - center) / center;
        double pixel = Math.hypot(x + 0.5 - sprite.size / 2.0, y + 0.5 - sprite.size / 2.0);
        return switch (sprite) {
            case RADIAL -> (float) Math.pow(1F - Math.min(1F, Math.hypot(offsetX, offsetY)), 1.55F);
            case STREAK -> (float) Math.pow(1F - Math.min(1F, Math.abs(offsetX)), 1.05F);
            case PIXEL_DOT -> pixel <= 3.9 ? 1F : 0F;
            case PIXEL_PUFF -> pixel <= 6.6 ? 1F : 0F;
            case PIXEL_RING -> pixel >= 13.4 && pixel <= 15.6 ? 1F : 0F;
            case SOFT_RING -> (float) Math.exp(-Math.pow((pixel / (sprite.size / 2.0) - 0.88) / 0.1, 2));
            case PIXEL_SPARK -> {
                int arm = Math.abs(x - 2) + Math.abs(y - 2);
                yield x != 2 && y != 2 ? 0F : arm == 0 ? 1F : arm == 1 ? 0.9F : 0.45F;
            }
        };
    }
}
