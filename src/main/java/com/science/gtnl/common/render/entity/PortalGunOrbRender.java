package com.science.gtnl.common.render.entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.gtnllib.client.fx.world.FxRenderHelper;
import com.gtnllib.client.fx.world.FxRenderHelper.Sprite;
import com.gtnllib.utils.ColorMath;
import com.gtnllib.utils.Vec3d;
import com.science.gtnl.client.portalgun.PortalGunOrbTrail;
import com.science.gtnl.common.entity.EntityPortalGunOrb;
import com.science.gtnl.config.ClientConfig;
import com.science.gtnl.utils.enums.PortalGunPortalEffect;
import com.science.gtnl.utils.enums.PortalGunPortalEffect.OrbStyle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PortalGunOrbRender extends Render {

    private static final double JET_LENGTH = 1.4, JET_WIDTH = 0.11, JET_CORE = 0.03;
    private static final int JET_POINTS = 8;

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (!(entity instanceof EntityPortalGunOrb orb)) return;
        float time = entity.ticksExisted + partialTicks;
        PortalGunPortalEffect effect = ClientConfig.portalGunPortalEffect;
        Vec3d center = PortalGunOrbTrail.follow(
            orb,
            Vec3d.interpolated(entity, partialTicks)
                .add(0, entity.height / 2, 0),
            effect,
            partialTicks);
        if (effect.orbStyle == OrbStyle.JET) {
            renderJet(center, PortalGunOrbTrail.heading(entity), time, effect);
            return;
        }
        float pulse = 1 + 0.15F * (float) Math.sin(time * 1.3);
        FxRenderHelper.renderGlow(Sprite.RADIAL, () -> {
            ColorMath.tessellateArgb(Tessellator.instance, effect.glowColor);
            FxRenderHelper.glow(center, 0.45 * pulse, time * 0.2F);
            ColorMath.tessellateArgb(Tessellator.instance, effect.coreColor);
            FxRenderHelper.glow(center, 0.18, -time * 0.3F);
        });
    }

    private static void renderJet(Vec3d head, Vec3d heading, float time, PortalGunPortalEffect effect) {
        Vec3d[] points = new Vec3d[JET_POINTS];
        Vec3d side = Vec3d.rightOf(heading);
        for (int i = 0; i < JET_POINTS; i++) {
            double t = i / (double) (JET_POINTS - 1);
            points[i] = head.subtract(heading.scale(JET_LENGTH * t))
                .add(side.scale(0.04 * t * Math.sin(time * 1.7 + i * 1.3)));
        }
        FxRenderHelper.renderGlow(Sprite.STREAK, () -> {
            ColorMath.tessellateArgb(Tessellator.instance, effect.glowColor);
            for (int i = 1; i < JET_POINTS; i++)
                FxRenderHelper.glowRibbon(points[i - 1], points[i], jetWidth(i - 1, JET_WIDTH), jetWidth(i, JET_WIDTH));
        });
        FxRenderHelper.render(() -> {
            for (int i = 1; i < JET_POINTS; i++)
                FxRenderHelper.ribbon(points[i - 1], points[i], jetWidth(i - 1, JET_CORE), 1F, 1F, 1F, 0.95F);
        });
        FxRenderHelper.renderGlow(Sprite.RADIAL, () -> {
            ColorMath.tessellateArgb(Tessellator.instance, effect.coreColor);
            FxRenderHelper.glow(head, 0.16, time * 0.4F);
        });
    }

    private static double jetWidth(int index, double width) {
        return width * (1 - index / (double) (JET_POINTS - 1) * 0.85);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
