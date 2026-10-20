package com.science.gtnl.client.fx.world;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.equipment.EquipmentVec;

public class FxRenderHelper {

    public enum Sprite {

        RADIAL(128, 1.55F, true),
        STREAK(128, 1.05F, false);

        private final int size;
        private final float falloff;
        private final boolean circular;

        Sprite(int size, float falloff, boolean circular) {
            this.size = size;
            this.falloff = falloff;
            this.circular = circular;
        }
    }

    private static final Map<Sprite, Integer> TEXTURES = new EnumMap<>(Sprite.class);

    private static final double DEGENERATE = 1.0E-6;

    private FxRenderHelper() {}

    public static void render(Runnable draw) {
        batch(null, draw);
    }

    public static void renderGlow(Sprite sprite, Runnable draw) {
        batch(() -> bind(sprite), draw);
    }

    public static void renderTextured(ResourceLocation texture, Runnable draw) {
        batch(() -> {
            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(texture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        }, draw);
    }

    public static void clearSprites() {
        for (Integer handle : TEXTURES.values()) GL11.glDeleteTextures(handle);
        TEXTURES.clear();
    }

    public static void vertex(EquipmentVec vector) {
        Tessellator.instance.addVertex(vector.x(), vector.y(), vector.z());
    }

    public static void ribbon(EquipmentVec from, EquipmentVec to, double width, float red, float green, float blue,
        float alpha) {
        EquipmentVec side = ribbonAxis(from, to).scale(width);
        Tessellator.instance.setColorRGBA_F(red, green, blue, alpha);
        vertex(from.subtract(side));
        vertex(from.add(side));
        vertex(to.add(side));
        vertex(to.subtract(side));
    }

    public static void glow(EquipmentVec center, double radius, float radians) {
        EquipmentVec direction = FxCamera.position()
            .subtract(center)
            .normalize();
        EquipmentVec right = EquipmentVec.rightOf(direction);
        EquipmentVec up = right.cross(direction)
            .normalize();
        if (up.length() < DEGENERATE) up = new EquipmentVec(0, 1, 0);
        float cosine = (float) Math.cos(radians), sine = (float) Math.sin(radians);
        EquipmentVec sideways = right.scale(cosine)
            .add(up.scale(sine))
            .scale(radius);
        EquipmentVec upward = up.scale(cosine)
            .subtract(right.scale(sine))
            .scale(radius);
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

    public static void sphere(EquipmentVec center, double radius, int longitudes, int latitudes, float red, float green,
        float blue, float alpha, double rim) {
        int rings = Math.max(3, latitudes), segments = Math.max(4, longitudes);
        EquipmentVec view = FxCamera.position()
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

    private static void shade(EquipmentVec center, double normalX, double normalY, double normalZ, EquipmentVec view,
        double rim, float red, float green, float blue, float alpha) {
        double facing = (normalX * view.x() + normalY * view.y() + normalZ * view.z());
        double factor = facing <= 0 ? 0 : Math.pow(facing, rim);
        Tessellator.instance.setColorRGBA_F(red, green, blue, (float) (alpha * factor));
    }

    public static void glowRibbon(EquipmentVec from, EquipmentVec to, double fromWidth, double toWidth) {
        uvRibbon(from, to, fromWidth, toWidth, 0, 1);
    }

    public static void uvRibbon(EquipmentVec from, EquipmentVec to, double fromWidth, double toWidth, double startV,
        double endV) {
        EquipmentVec axis = ribbonAxis(from, to);
        EquipmentVec fromSide = axis.scale(fromWidth), toSide = axis.scale(toWidth);
        uvVertex(from.subtract(fromSide), 0, startV);
        uvVertex(from.add(fromSide), 1, startV);
        uvVertex(to.add(toSide), 1, endV);
        uvVertex(to.subtract(toSide), 0, endV);
    }

    private static EquipmentVec ribbonAxis(EquipmentVec from, EquipmentVec to) {
        EquipmentVec axis = to.subtract(from)
            .cross(
                FxCamera.position()
                    .subtract(
                        from.add(to)
                            .scale(0.5)))
            .normalize();
        return axis.length() < DEGENERATE ? EquipmentVec.rightOf(
            to.subtract(from)
                .normalize())
            : axis;
    }

    private static void uvVertex(EquipmentVec vector, double u, double v) {
        Tessellator.instance.addVertexWithUV(vector.x(), vector.y(), vector.z(), u, v);
    }

    private static void batch(Runnable bind, Runnable draw) {
        float lightX = OpenGlHelper.lastBrightnessX, lightY = OpenGlHelper.lastBrightnessY;
        FxCamera.capture();
        GL11.glPushMatrix();
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        try {
            GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
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
        Integer cached = TEXTURES.get(sprite);
        if (cached != null) return cached;
        int handle = GL11.glGenTextures();
        TextureUtil.uploadTextureImage(handle, build(sprite));
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, handle);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        TEXTURES.put(sprite, handle);
        return handle;
    }

    private static BufferedImage build(Sprite sprite) {
        int size = sprite.size;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        float center = (size - 1) / 2F;
        for (int y = 0; y < size; y++) {
            float offsetY = sprite.circular ? (y - center) / center : 0F;
            for (int x = 0; x < size; x++) {
                float offsetX = (x - center) / center;
                float distance = Math.min(1F, (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY));
                float alpha = (float) Math.pow(1F - distance, sprite.falloff);
                image.setRGB(x, y, Math.round(alpha * 255F) << 24 | 0xFFFFFF);
            }
        }
        return image;
    }
}
