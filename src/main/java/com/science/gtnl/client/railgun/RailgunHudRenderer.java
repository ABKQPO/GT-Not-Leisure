package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.railgun.shader.RailgunShaderEffect;
import com.science.gtnl.common.railgun.RailgunChargeTier;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RailgunHudRenderer {

    private static final ResourceLocation BOLT_EMPTY = new ResourceLocation(
        ScienceNotLeisure.RESOURCE_ROOT_ID,
        "textures/gui/railgun/charge_bolt_empty.png");
    private static final ResourceLocation BOLT_FILL = new ResourceLocation(
        ScienceNotLeisure.RESOURCE_ROOT_ID,
        "textures/gui/railgun/charge_bolt_fill.png");
    private static final ResourceLocation BOLT_HIGHLIGHT = new ResourceLocation(
        ScienceNotLeisure.RESOURCE_ROOT_ID,
        "textures/gui/railgun/charge_bolt_highlight.png");
    private static final int ICON_WIDTH = 13;
    private static final int ICON_HEIGHT = 19;
    private static final int CROSSHAIR_OFFSET_X = 10;
    private static final float OVERLAY_INTENSITY = 0.75F;
    private static final int[] COLORS = { 0x3C6E8F, 0x50CFFF, 0x8798FF, 0xFF509A };
    private static final float[] COLOR_STOPS = { 0, RailgunChargeTier.EHV1.chargeTicks,
        RailgunChargeTier.EHV2.chargeTicks, RailgunChargeTier.EHV3.chargeTicks };

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS) return;
        var mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (!RailgunCharge.charging(player)) return;
        int width = event.resolution.getScaledWidth(), height = event.resolution.getScaledHeight();
        float units = RailgunCharge.units(player, event.partialTicks);
        float progress = Math.min(1F, units / RailgunData.FULL_CHARGE_UNITS);
        int color = color(units);
        if (mc.gameSettings.thirdPersonView == 0) drawOverlay(width, height, progress, color);
        drawBolt(mc, width, height, progress, color);
    }

    private static void drawOverlay(int width, int height, float progress, int color) {
        if (!MainConfig.client.enableRailgunBloom || !MainConfig.client.enableRailgunShaders) return;
        RailgunShaderEffect shader = RailgunShaderEffect.CHARGE;
        if (!shader.begin()) return;
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        try {
            shader.uniform1f("time", (float) ((System.currentTimeMillis() % 3600000L) / 1000.0));
            shader.uniform1f("progress", progress * progress * OVERLAY_INTENSITY);
            shader.uniform2f("screen", width, height);
            setColor(color, 1F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0F, 0F);
            GL11.glVertex2f(0F, 0F);
            GL11.glTexCoord2f(1F, 0F);
            GL11.glVertex2f(width, 0F);
            GL11.glTexCoord2f(1F, 1F);
            GL11.glVertex2f(width, height);
            GL11.glTexCoord2f(0F, 1F);
            GL11.glVertex2f(0F, height);
            GL11.glEnd();
        } finally {
            shader.end();
            GL11.glPopAttrib();
        }
    }

    private static void drawBolt(Minecraft mc, int width, int height, float progress, int color) {
        int x = width / 2 + CROSSHAIR_OFFSET_X;
        int y = (height - ICON_HEIGHT) / 2;
        int filled = Math.round(ICON_HEIGHT * progress);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        try {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            mc.getTextureManager()
                .bindTexture(BOLT_EMPTY);
            Gui.func_146110_a(x, y, 0, 0, ICON_WIDTH, ICON_HEIGHT, ICON_WIDTH, ICON_HEIGHT);
            if (filled <= 0) return;
            int top = ICON_HEIGHT - filled;
            setColor(color, 1F);
            mc.getTextureManager()
                .bindTexture(BOLT_FILL);
            Gui.func_146110_a(x, y + top, 0, top, ICON_WIDTH, filled, ICON_WIDTH, ICON_HEIGHT);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            mc.getTextureManager()
                .bindTexture(BOLT_HIGHLIGHT);
            Gui.func_146110_a(x, y + top, 0, top, ICON_WIDTH, filled, ICON_WIDTH, ICON_HEIGHT);
        } finally {
            GL11.glPopAttrib();
        }
    }

    private static void setColor(int rgb, float alpha) {
        GL11.glColor4f((rgb >> 16 & 255) / 255F, (rgb >> 8 & 255) / 255F, (rgb & 255) / 255F, alpha);
    }

    private static int color(float units) {
        if (units <= COLOR_STOPS[0]) return COLORS[0];
        for (int i = 1; i < COLOR_STOPS.length; i++) {
            if (units >= COLOR_STOPS[i]) continue;
            float t = (units - COLOR_STOPS[i - 1]) / (COLOR_STOPS[i] - COLOR_STOPS[i - 1]);
            return lerp(COLORS[i - 1], COLORS[i], t);
        }
        return COLORS[COLORS.length - 1];
    }

    private static int lerp(int from, int to, float t) {
        int red = Math.round((from >> 16 & 255) + ((to >> 16 & 255) - (from >> 16 & 255)) * t);
        int green = Math.round((from >> 8 & 255) + ((to >> 8 & 255) - (from >> 8 & 255)) * t);
        int blue = Math.round((from & 255) + ((to & 255) - (from & 255)) * t);
        return red << 16 | green << 8 | blue;
    }
}
