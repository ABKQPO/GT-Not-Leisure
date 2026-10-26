package com.science.gtnl.common.render.entity;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.gtnllib.client.fx.FxEasing;
import com.gtnllib.client.fx.world.FxCamera;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.client.portalgun.PortalGunPortalFx;
import com.science.gtnl.client.portalgun.PortalGunPortalFx.Pose;
import com.science.gtnl.common.entity.EntityPortalGunPortal;
import com.science.gtnl.common.portalgun.PortalGunGeometry;
import com.science.gtnl.config.ClientConfig;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.PortalGunPortalEffect;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PortalGunPortalRender extends Render {

    public static final float CLOSE_HOLD = 0.2F;
    // Why: the glow and arcs reach past the portal's box, so the vanilla frustum check is off.
    public static final double DRAWN_REACH = 3;
    private static final FloatBuffer MATRIX = BufferUtils.createFloatBuffer(16);
    private static final Map<PortalGunPortalEffect, ResourceLocation> TEXTURES = new EnumMap<>(
        PortalGunPortalEffect.class);

    public static double lift(PortalGunPortalEffect effect, boolean roomAbove) {
        return roomAbove ? Math.max(0, effect.standHalfHeightBlocks() - PortalGunGeometry.HEIGHT / 2) : 0;
    }

    public static float shrink(float close) {
        return 1 - FxEasing.smoothStep(CLOSE_HOLD, 1, close);
    }

    public static int sheetFrame(PortalGunPortalEffect effect, float ticks) {
        return (int) (ticks / effect.frameTicks) % effect.frames;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (!(entity instanceof EntityPortalGunPortal portal)) return;
        ClientConfig settings = MainConfig.client;
        PortalGunPortalEffect effect = settings.portalGunPortalEffect;
        double lift = portal.up() == ForgeDirection.UP ? lift(effect, roomAbove(portal)) : 0;
        PortalGunPortalFx.throwDots(portal, effect, x, y + lift, z, partialTicks);
        FxCamera.capture();
        if (!FxCamera.inView(
            new Vec3d(x + RenderManager.renderPosX, y + lift + RenderManager.renderPosY, z + RenderManager.renderPosZ),
            DRAWN_REACH)) return;
        float close = portal.closeProgress(partialTicks, PortalGunPortalFx.closeTicks(effect, settings));
        Pose pose = PortalGunPortalFx.pose(effect, portal.sincePass(partialTicks), close, settings);
        switch (effect.kind) {
            case FRAMED -> PortalGunDoorRender.render(portal, effect, x, y + lift, z, partialTicks, close, pose);
            case VORTEX -> PortalGunVortexRender.render(portal, effect, x, y + lift, z, partialTicks, close, pose);
            default -> PortalGunSpriteRender.render(portal, effect, x, y + lift, z, partialTicks, close, pose);
        }
        PortalGunPortalFx.render(portal, effect, x, y + lift, z, partialTicks, close, pose);
    }

    public static void begin(EntityPortalGunPortal portal, double x, double y, double z, Pose pose) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_LIGHTING_BIT
                | GL11.GL_CURRENT_BIT);
        GL11.glTranslated(x, y, z);
        orient(portal.facing(), portal.up());
        GL11.glTranslated(0, 0, pose.push());
        GL11.glScaled(pose.sx(), pose.sy(), 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
    }

    public static void end() {
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public static void bind(PortalGunPortalEffect effect) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(texture(effect));
    }

    public static void quad(double halfWidth, double halfHeight, double left, double right, double top, double bottom,
        float alpha) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(1F, 1F, 1F, alpha);
        tessellator.addVertexWithUV(-halfWidth, -halfHeight, 0, left, bottom);
        tessellator.addVertexWithUV(halfWidth, -halfHeight, 0, right, bottom);
        tessellator.addVertexWithUV(halfWidth, halfHeight, 0, right, top);
        tessellator.addVertexWithUV(-halfWidth, halfHeight, 0, left, top);
        tessellator.draw();
    }

    private static boolean roomAbove(EntityPortalGunPortal portal) {
        World world = portal.worldObj;
        int x = MathHelper.floor_double(portal.posX), z = MathHelper.floor_double(portal.posZ),
            y = MathHelper.floor_double(portal.posY + PortalGunGeometry.HEIGHT / 2);
        return world.getBlock(x, y, z)
            .getCollisionBoundingBoxFromPool(world, x, y, z) == null;
    }

    private static void orient(ForgeDirection facing, ForgeDirection up) {
        int wx = up.offsetY * facing.offsetZ - up.offsetZ * facing.offsetY,
            wy = up.offsetZ * facing.offsetX - up.offsetX * facing.offsetZ,
            wz = up.offsetX * facing.offsetY - up.offsetY * facing.offsetX;
        MATRIX.clear();
        MATRIX.put(
            new float[] { wx, wy, wz, 0, up.offsetX, up.offsetY, up.offsetZ, 0, facing.offsetX, facing.offsetY,
                facing.offsetZ, 0, 0, 0, 0, 1 });
        MATRIX.flip();
        GL11.glMultMatrix(MATRIX);
    }

    private static ResourceLocation texture(PortalGunPortalEffect effect) {
        return TEXTURES.computeIfAbsent(effect, key -> new ResourceLocation(RESOURCE_ROOT_ID, key.texturePath()));
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture(MainConfig.client.portalGunPortalEffect);
    }
}
