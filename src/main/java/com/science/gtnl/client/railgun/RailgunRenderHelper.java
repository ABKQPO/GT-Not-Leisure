package com.science.gtnl.client.railgun;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.railgun.RailgunVec;

public class RailgunRenderHelper {

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

    private static final RailgunVec UP = new RailgunVec(0, 1, 0);
    private static final double FIRST_PERSON_FORWARD = 0.9;
    private static final double FIRST_PERSON_RIGHT = 0.32;
    private static final double FIRST_PERSON_UP = -0.22;
    private static final double THIRD_PERSON_DROP = 0.45;
    private static final double THIRD_PERSON_FORWARD = 0.75;
    private static final double THIRD_PERSON_RIGHT = 0.3;
    private static final double THIRD_PERSON_UP = -0.12;
    private static final double DEGENERATE = 1.0E-6;
    private static final double MAX_ANCHOR_DRIFT = 1.2;

    private RailgunRenderHelper() {}

    public static void render(Runnable draw) {
        batch(null, draw);
    }

    public static void renderGlow(Sprite sprite, Runnable draw) {
        batch(sprite, draw);
    }

    public static void clearSprites() {
        for (Integer handle : TEXTURES.values()) GL11.glDeleteTextures(handle);
        TEXTURES.clear();
    }

    public static void vertex(RailgunVec vector) {
        Tessellator.instance.addVertex(vector.x(), vector.y(), vector.z());
    }

    public static void ribbon(RailgunVec from, RailgunVec to, double width, float red, float green, float blue,
        float alpha) {
        RailgunVec side = facingSide(from, to, width);
        Tessellator.instance.setColorRGBA_F(red, green, blue, alpha);
        vertex(from.subtract(side));
        vertex(from.add(side));
        vertex(to.add(side));
        vertex(to.subtract(side));
    }

    public static void glow(RailgunVec center, double radius, float radians) {
        RailgunVec direction = RailgunCamera.position()
            .subtract(center)
            .normalize();
        RailgunVec right = RailgunVec.rightOf(direction);
        RailgunVec up = right.cross(direction)
            .normalize();
        if (up.length() < DEGENERATE) up = new RailgunVec(0, 1, 0);
        float cosine = (float) Math.cos(radians), sine = (float) Math.sin(radians);
        RailgunVec sideways = right.scale(cosine)
            .add(up.scale(sine))
            .scale(radius);
        RailgunVec upward = up.scale(cosine)
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

    public static void sphere(RailgunVec center, double radius, int longitudes, int latitudes, float red, float green,
        float blue, float alpha, double rim) {
        int rings = Math.max(3, latitudes), segments = Math.max(4, longitudes);
        RailgunVec view = RailgunCamera.position()
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

    private static void shade(RailgunVec center, double normalX, double normalY, double normalZ, RailgunVec view,
        double rim, float red, float green, float blue, float alpha) {
        double facing = (normalX * view.x() + normalY * view.y() + normalZ * view.z());
        double factor = facing <= 0 ? 0 : Math.pow(facing, rim);
        Tessellator.instance.setColorRGBA_F(red, green, blue, (float) (alpha * factor));
    }

    public static void glowRibbon(RailgunVec from, RailgunVec to, double fromWidth, double toWidth) {
        uvRibbon(from, to, fromWidth, toWidth, 0, 1);
    }

    public static void taperedRibbon(RailgunVec from, RailgunVec to, double width, int segments, float red, float green,
        float blue, float alpha) {
        int steps = Math.max(2, segments);
        RailgunVec delta = to.subtract(from);
        for (int segment = 0; segment < steps; segment++) {
            double start = segment / (double) steps, end = (segment + 1) / (double) steps;
            double startShade = taper(start), endShade = taper(end);
            if (startShade <= 0 && endShade <= 0) continue;
            RailgunVec startPoint = from.add(delta.scale(start)), endPoint = from.add(delta.scale(end));
            RailgunVec axis = ribbonAxis(startPoint, endPoint);
            RailgunVec startSide = axis.scale(width * startShade);
            RailgunVec endSide = axis.scale(width * endShade);
            Tessellator.instance.setColorRGBA_F(red, green, blue, (float) (alpha * startShade));
            uvVertex(startPoint.subtract(startSide), 0, 0);
            uvVertex(startPoint.add(startSide), 1, 0);
            Tessellator.instance.setColorRGBA_F(red, green, blue, (float) (alpha * endShade));
            uvVertex(endPoint.add(endSide), 1, 1);
            uvVertex(endPoint.subtract(endSide), 0, 1);
        }
    }

    private static double taper(double position) {
        return Math.sin(Math.PI * Math.max(0, Math.min(1, position)));
    }

    public static void uvRibbon(RailgunVec from, RailgunVec to, double fromWidth, double toWidth, double startV,
        double endV) {
        RailgunVec axis = ribbonAxis(from, to);
        RailgunVec fromSide = axis.scale(fromWidth), toSide = axis.scale(toWidth);
        uvVertex(from.subtract(fromSide), 0, startV);
        uvVertex(from.add(fromSide), 1, startV);
        uvVertex(to.add(toSide), 1, endV);
        uvVertex(to.subtract(toSide), 0, endV);
    }

    private static RailgunVec ribbonAxis(RailgunVec from, RailgunVec to) {
        RailgunVec axis = to.subtract(from)
            .cross(
                RailgunCamera.position()
                    .subtract(
                        from.add(to)
                            .scale(0.5)))
            .normalize();
        return axis.length() < DEGENERATE ? RailgunVec.rightOf(
            to.subtract(from)
                .normalize())
            : axis;
    }

    public static void uvPlane(RailgunVec center, double half) {
        uvVertex(center.add(-half, 0, -half), 0, 0);
        uvVertex(center.add(half, 0, -half), 1, 0);
        uvVertex(center.add(half, 0, half), 1, 1);
        uvVertex(center.add(-half, 0, half), 0, 1);
    }

    public static RailgunVec muzzle(int shooterId, float partialTicks) {
        Minecraft minecraft = Minecraft.getMinecraft();
        Entity entity = minecraft.theWorld == null ? null : minecraft.theWorld.getEntityByID(shooterId);
        if (entity == null) return null;
        if (entity == minecraft.thePlayer && minecraft.gameSettings.thirdPersonView == 0)
            return firstPersonMuzzle(minecraft.thePlayer, partialTicks);
        RailgunVec analytic = thirdPersonMuzzle(entity, partialTicks);
        RailgunVec anchored = RailgunMuzzleAnchor.muzzle(shooterId, RailgunVec.look(entity, partialTicks));
        if (anchored == null || anchored.distanceToSqr(analytic) > MAX_ANCHOR_DRIFT * MAX_ANCHOR_DRIFT) return analytic;
        return anchored;
    }

    public static RailgunVec muzzle(int shooterId, RailgunVec fallback, float partialTicks) {
        RailgunVec muzzle = muzzle(shooterId, partialTicks);
        return muzzle == null ? fallback : muzzle;
    }

    public static RailgunVec analyticMuzzle(int shooterId, RailgunVec fallback, float partialTicks) {
        Minecraft minecraft = Minecraft.getMinecraft();
        Entity entity = minecraft.theWorld == null ? null : minecraft.theWorld.getEntityByID(shooterId);
        if (entity == null) return fallback;
        if (entity == minecraft.thePlayer && minecraft.gameSettings.thirdPersonView == 0)
            return firstPersonMuzzle(minecraft.thePlayer, partialTicks);
        return thirdPersonMuzzle(entity, partialTicks);
    }

    public static RailgunVec firstPersonMuzzle(EntityPlayer player, float partialTicks) {
        if (player == null) return null;
        RailgunVec look = RailgunVec.look(player, partialTicks);
        RailgunVec right = RailgunVec.rightOf(look);
        RailgunVec up = right.cross(look)
            .normalize();
        return RailgunVec.interpolated(player, partialTicks)
            .add(look.scale(FIRST_PERSON_FORWARD))
            .add(right.scale(FIRST_PERSON_RIGHT))
            .add(up.scale(FIRST_PERSON_UP));
    }

    public static RailgunVec thirdPersonMuzzle(Entity entity, float partialTicks) {
        RailgunVec look = RailgunVec.look(entity, partialTicks);
        RailgunVec right = RailgunVec.rightOf(look);
        RailgunVec up = right.cross(look)
            .normalize();
        return RailgunVec.interpolated(entity, partialTicks)
            .add(0, entity.getEyeHeight() - THIRD_PERSON_DROP, 0)
            .add(right.scale(THIRD_PERSON_RIGHT))
            .add(up.scale(THIRD_PERSON_UP))
            .add(look.scale(THIRD_PERSON_FORWARD));
    }

    private static void uvVertex(RailgunVec vector, double u, double v) {
        Tessellator.instance.addVertexWithUV(vector.x(), vector.y(), vector.z(), u, v);
    }

    private static RailgunVec facingSide(RailgunVec from, RailgunVec to, double width) {
        RailgunVec side = to.subtract(from)
            .cross(
                RailgunCamera.position()
                    .subtract(
                        from.add(to)
                            .scale(0.5)))
            .normalize()
            .scale(width);
        return side.length() < DEGENERATE ? UP.cross(to.subtract(from))
            .normalize()
            .scale(width) : side;
    }

    private static void batch(Sprite sprite, Runnable draw) {
        float lightX = OpenGlHelper.lastBrightnessX, lightY = OpenGlHelper.lastBrightnessY;
        RailgunCamera.capture();
        GL11.glPushMatrix();
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        try {
            GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
            if (sprite == null) GL11.glDisable(GL11.GL_TEXTURE_2D);
            else {
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                bind(sprite);
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
