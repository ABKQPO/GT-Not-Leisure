package com.science.gtnl.client.celestweave;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.client.railgun.RailgunArcRenderer;
import com.science.gtnl.client.railgun.RailgunCamera;
import com.science.gtnl.client.railgun.RailgunRenderHelper;
import com.science.gtnl.common.railgun.RailgunVec;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class CwShieldFlash {

    private static final int MAX_ACTIVE = 32, LONGITUDES = 24, LATITUDES = 16, U_REPEAT = 10, V_REPEAT = 4;
    private static final int HIT_LIFE = 420, SAVE_LIFE = 800;
    private static final int HEX_WIDTH = 64, HEX_HEIGHT = 111;
    private static final List<Flash> ACTIVE = new ArrayList<>();
    private static ResourceLocation hex;

    private record Flash(int entityId, RailgunVec dir, long born, int life, int rgb, boolean save) {}

    private record Shell(Flash flash, RailgunVec center, double horizontal, double vertical, float progress,
        float strength) {}

    static void spawn(EntityLivingBase entity, float x, float y, float z, int rgb, boolean save, boolean omni) {
        RailgunVec center = center(entity, 1F);
        RailgunVec offset = new RailgunVec(x, y, z).subtract(center);
        RailgunVec dir = omni || offset.length() < 0.4 ? null : offset.normalize();
        if (ACTIVE.size() >= MAX_ACTIVE) ACTIVE.remove(0);
        ACTIVE.add(
            new Flash(entity.getEntityId(), dir, Minecraft.getSystemTime(), save ? SAVE_LIFE : HIT_LIFE, rgb, save));
        if (!firstPerson(entity)) sparks(entity, center, dir, rgb, save);
    }

    static void clear() {
        ACTIVE.clear();
    }

    private static boolean firstPerson(Entity entity) {
        Minecraft minecraft = Minecraft.getMinecraft();
        return entity == minecraft.renderViewEntity && minecraft.gameSettings.thirdPersonView == 0;
    }

    private static double horizontal(Entity entity) {
        return entity.width * 0.9 + 0.35;
    }

    private static double vertical(Entity entity) {
        return entity.height * 0.62;
    }

    private static RailgunVec center(Entity entity, float partialTicks) {
        double back = 1 - partialTicks;
        return new RailgunVec(
            (entity.boundingBox.minX + entity.boundingBox.maxX) / 2 - (entity.posX - entity.lastTickPosX) * back,
            (entity.boundingBox.minY + entity.boundingBox.maxY) / 2 - (entity.posY - entity.lastTickPosY) * back,
            (entity.boundingBox.minZ + entity.boundingBox.maxZ) / 2 - (entity.posZ - entity.lastTickPosZ) * back);
    }

    private static RailgunVec surface(RailgunVec center, RailgunVec normal, double horizontal, double vertical) {
        return center.add(normal.x() * horizontal, normal.y() * vertical, normal.z() * horizontal);
    }

    private static RailgunVec randomUnit() {
        RailgunVec vector = new RailgunVec(
            CwArcs.RNG.nextGaussian(),
            CwArcs.RNG.nextGaussian(),
            CwArcs.RNG.nextGaussian());
        return vector.length() < 1.0E-3 ? new RailgunVec(0, 1, 0) : vector.normalize();
    }

    private static void sparks(EntityLivingBase entity, RailgunVec center, RailgunVec dir, int rgb, boolean save) {
        double horizontal = horizontal(entity), vertical = vertical(entity);
        int color = CwLook.mixWhite(rgb, 0.45F);
        int count = save ? 10 : 6;
        for (int i = 0; i < count; i++) {
            RailgunVec from = dir != null ? dir : randomUnit();
            RailgunVec axis = from.cross(randomUnit());
            if (axis.length() < 1.0E-3) continue;
            axis = axis.normalize();
            double angle = CwArcs.rand(0.35F, 0.8F);
            RailgunVec to = from.scale(Math.cos(angle))
                .add(
                    axis.cross(from)
                        .scale(Math.sin(angle)));
            RailgunArcRenderer.spawnTinted(
                surface(center, from, horizontal, vertical),
                surface(center, to, horizontal, vertical),
                6,
                0.05,
                3 + CwArcs.RNG.nextInt(2),
                color,
                0.6);
        }
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft.theWorld;
        long now = Minecraft.getSystemTime();
        ACTIVE.removeIf(flash -> now - flash.born >= flash.life);
        if (world == null || ACTIVE.isEmpty()) return;
        List<Shell> shells = new ArrayList<>();
        for (Flash flash : ACTIVE) {
            Entity entity = world.getEntityByID(flash.entityId);
            if (!(entity instanceof EntityLivingBase) || entity.isDead || firstPerson(entity)) continue;
            float progress = (now - flash.born) / (float) flash.life;
            float grow = 1 + (flash.save ? 0.45F : 0.1F) * progress;
            float strength = Math.min(1F, progress * 10F) * (float) Math.pow(1 - progress, 1.5)
                * (flash.save ? 1.4F : 1F);
            shells.add(
                new Shell(
                    flash,
                    center(entity, event.partialTicks),
                    horizontal(entity) * grow,
                    vertical(entity) * grow,
                    progress,
                    strength));
        }
        if (shells.isEmpty()) return;
        RailgunRenderHelper.render(() -> { for (Shell shell : shells) emit(shell, RailgunCamera.position(), false); });
        textured(() -> { for (Shell shell : shells) emit(shell, RailgunCamera.position(), true); });
        RailgunRenderHelper
            .renderGlow(RailgunRenderHelper.Sprite.RADIAL, () -> { for (Shell shell : shells) impact(shell); });
    }

    private static void emit(Shell shell, RailgunVec camera, boolean hexPass) {
        for (int ring = 0; ring < LATITUDES; ring++) {
            for (int segment = 0; segment < LONGITUDES; segment++) {
                vertex(shell, ring, segment, camera, hexPass);
                vertex(shell, ring, segment + 1, camera, hexPass);
                vertex(shell, ring + 1, segment + 1, camera, hexPass);
                vertex(shell, ring + 1, segment, camera, hexPass);
            }
        }
    }

    private static void vertex(Shell shell, int ring, int segment, RailgunVec camera, boolean hexPass) {
        Flash flash = shell.flash;
        float progress = shell.progress;
        double latitude = Math.PI * (ring / (double) LATITUDES - 0.5);
        double longitude = Math.PI * 2 * segment / LONGITUDES;
        double nx = Math.cos(latitude) * Math.cos(longitude), ny = Math.sin(latitude),
            nz = Math.cos(latitude) * Math.sin(longitude);
        RailgunVec point = shell.center.add(nx * shell.horizontal, ny * shell.vertical, nz * shell.horizontal);
        RailgunVec view = camera.subtract(point)
            .normalize();
        double facing = Math.abs(nx * view.x() + ny * view.y() + nz * view.z());
        double rim = Math.pow(1 - facing, 3) * (flash.save ? 0.6 : 0.35);
        double spot, wave;
        if (flash.dir == null) {
            spot = 0;
            double front = ny * 0.5 + 0.5 - progress;
            wave = Math.exp(-front * front / 0.02) * 0.7 + 0.12;
        } else {
            double cosine = nx * flash.dir.x() + ny * flash.dir.y() + nz * flash.dir.z();
            spot = Math.pow(Math.max(0, cosine), 8);
            double front = (Math.acos(Math.max(-1, Math.min(1, cosine))) - progress * 2.4) / 0.22;
            wave = Math.exp(-front * front) * 0.75 + 0.06;
        }
        double alpha;
        if (hexPass) alpha = (spot * 1.3 + wave * 1.1 + rim * 0.35 + 0.05) * shell.strength;
        else {
            double shimmer = 0.85 + 0.15 * Math.sin(longitude * 7 + latitude * 5 + progress * 20);
            alpha = (rim * 0.8 + spot * 0.55 + wave * 0.35) * shimmer * shell.strength;
        }
        float white = (float) Math.min(1, spot * 0.7 + (flash.save ? 0.25 : 0));
        float red = (flash.rgb >> 16 & 255) / 255F, green = (flash.rgb >> 8 & 255) / 255F,
            blue = (flash.rgb & 255) / 255F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorRGBA_F(
            red + (1 - red) * white,
            green + (1 - green) * white,
            blue + (1 - blue) * white,
            (float) Math.min(1, alpha));
        if (hexPass) tessellator.addVertexWithUV(
            point.x(),
            point.y(),
            point.z(),
            segment / (double) LONGITUDES * U_REPEAT,
            ring / (double) LATITUDES * V_REPEAT);
        else RailgunRenderHelper.vertex(point);
    }

    private static void impact(Shell shell) {
        Flash flash = shell.flash;
        float k = Math.max(0F, 1F - shell.progress / 0.35F);
        if (flash.dir == null || k <= 0F) return;
        RailgunVec point = surface(shell.center, flash.dir, shell.horizontal, shell.vertical);
        int light = CwLook.mixWhite(flash.rgb, 0.3F);
        Tessellator tessellator = Tessellator.instance;
        tessellator
            .setColorRGBA_F((light >> 16 & 255) / 255F, (light >> 8 & 255) / 255F, (light & 255) / 255F, 0.55F * k);
        RailgunRenderHelper.glow(point, 0.35 + 0.35 * (1 - k), shell.progress * 6F);
        tessellator.setColorRGBA_F(1F, 1F, 1F, 0.85F * k * k);
        RailgunRenderHelper.glow(point, 0.18, -shell.progress * 9F);
    }

    private static void textured(Runnable draw) {
        Minecraft minecraft = Minecraft.getMinecraft();
        float lightX = OpenGlHelper.lastBrightnessX, lightY = OpenGlHelper.lastBrightnessY;
        GL11.glPushMatrix();
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_CURRENT_BIT
                | GL11.GL_TEXTURE_BIT);
        try {
            GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            minecraft.getTextureManager()
                .bindTexture(hexTexture());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
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

    private static ResourceLocation hexTexture() {
        if (hex == null) hex = Minecraft.getMinecraft()
            .getTextureManager()
            .getDynamicTextureLocation("gtnl_celestweave_hex", new DynamicTexture(buildHex()));
        return hex;
    }

    private static BufferedImage buildHex() {
        double radius = HEX_WIDTH / Math.sqrt(3), rowStep = radius * 1.5;
        BufferedImage image = new BufferedImage(HEX_WIDTH, HEX_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        for (int py = 0; py < HEX_HEIGHT; py++) {
            for (int px = 0; px < HEX_WIDTH; px++) {
                double x = px + 0.5, y = py + 0.5;
                double first = Double.MAX_VALUE, second = Double.MAX_VALUE, third = Double.MAX_VALUE;
                for (int row = -1; row <= 3; row++) {
                    double cy = row * rowStep, shift = (row & 1) == 0 ? 0 : HEX_WIDTH / 2.0;
                    for (int column = -1; column <= 1; column++) {
                        double cx = column * HEX_WIDTH + shift;
                        double distance = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                        if (distance < first) {
                            third = second;
                            second = first;
                            first = distance;
                        } else if (distance < second) {
                            third = second;
                            second = distance;
                        } else if (distance < third) third = distance;
                    }
                }
                double edge = second - first, corner = third - first;
                double alpha = clamp(1 - edge / 3.0) + 0.22 * clamp(1 - edge / 14.0) + 0.5 * clamp(1 - corner / 6.0);
                image.setRGB(px, py, (int) (clamp(alpha) * 255) << 24 | 0xFFFFFF);
            }
        }
        return image;
    }

    private static double clamp(double value) {
        return Math.max(0, Math.min(1, value));
    }
}
