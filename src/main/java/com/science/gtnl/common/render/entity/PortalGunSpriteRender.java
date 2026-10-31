package com.science.gtnl.common.render.entity;

import org.lwjgl.opengl.GL11;

import com.gtnllib.client.fx.FxEasing;
import com.science.gtnl.client.portalgun.PortalGunPortalFx.Pose;
import com.science.gtnl.common.entity.EntityPortalGunPortal;
import com.science.gtnl.utils.enums.PortalGunPortalEffect;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PortalGunSpriteRender {

    private static final float GLOW_ALPHA = 0.3F;

    public static float widthScale(float open, float close) {
        return FxEasing.easeOutBack(open) * PortalGunPortalRender.shrink(close);
    }

    public static float heightScale(float open, float close) {
        return FxEasing.easeOutCubic(open) * PortalGunPortalRender.shrink(close);
    }

    public static void render(EntityPortalGunPortal portal, PortalGunPortalEffect effect, double x, double y, double z,
        float partialTicks, float close, Pose pose) {
        float open = portal.openProgress(partialTicks);
        float width = widthScale(open, close), height = heightScale(open, close);
        if (width <= 0.01F || height <= 0.01F) return;
        int frame = PortalGunPortalRender.sheetFrame(effect, portal.ticksExisted + partialTicks);
        double top = frame / (double) effect.frames, bottom = (frame + 1) / (double) effect.frames;
        PortalGunPortalRender.begin(portal, x, y, z, pose);
        try {
            GL11.glScalef(width, height, 1);
            PortalGunPortalRender.bind(effect);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            PortalGunPortalRender.quad(effect.halfWidth(), effect.halfHeight(), 0, 1, top, bottom, 1F);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            PortalGunPortalRender.quad(effect.halfWidth(), effect.halfHeight(), 0, 1, top, bottom, GLOW_ALPHA);
        } finally {
            PortalGunPortalRender.end();
        }
    }
}
