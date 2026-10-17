package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.client.railgun.shader.RailgunShaderEffect;
import com.science.gtnl.client.railgun.shader.RailgunShaders;
import com.science.gtnl.common.railgun.RailgunChargeTier;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RailgunHudRenderer {

    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 4;
    private static final int OFFSET_Y = 15;
    private static final int FRAME_BACK = 0xAA101020;
    private static final float OVERLAY_INTENSITY = 0.55F;

    private static final int[] THRESHOLDS = { RailgunChargeTier.EHV1.chargeTicks, RailgunChargeTier.EHV2.chargeTicks,
        RailgunChargeTier.EHV3.chargeTicks };

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS) return;
        var mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (!RailgunChargeTracker.charging(player)) return;
        int width = event.resolution.getScaledWidth(), height = event.resolution.getScaledHeight();
        float progress = RailgunChargeTracker.progress(player);
        int tier = RailgunChargeTracker.tier(player)
            .ordinal();
        if (mc.gameSettings.thirdPersonView == 0) drawOverlay(width, height, progress, tier);
        drawMeter(width, height, progress, tier);
    }

    private static void drawOverlay(int width, int height, float progress, int tier) {
        if (!MainConfig.client.enableRailgunBloom || !MainConfig.client.enableRailgunShaders) return;
        RailgunShaderEffect shader = RailgunShaders.CHARGE;
        if (!shader.begin()) return;
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        try {
            shader.uniform1f("time", (float) ((System.currentTimeMillis() % 3600000L) / 1000.0));
            shader.uniform1f("progress", progress * progress * OVERLAY_INTENSITY);
            shader.uniform2f("screen", width, height);
            setColor(tier, 1F);
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

    private static void drawMeter(int width, int height, float progress, int tier) {
        int filled = Math.round(progress * BAR_WIDTH);
        int x = width / 2 - BAR_WIDTH / 2;
        int y = height / 2 + OFFSET_Y;
        Gui.drawRect(x - 2, y - 2, x + BAR_WIDTH + 2, y + BAR_HEIGHT + 2, glow(tier));
        Gui.drawRect(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, FRAME_BACK);
        if (filled > 0) Gui.drawRect(x, y, x + filled, y + BAR_HEIGHT, color(tier));
        for (int threshold : THRESHOLDS) {
            int mark = x + threshold * BAR_WIDTH / RailgunChargeTracker.FULL_CHARGE_UNITS;
            Gui.drawRect(mark - 1, y - 2, mark, y + BAR_HEIGHT + 2, 0xFFFFFFFF);
        }
    }

    private static void setColor(int tier, float alpha) {
        int rgb = color(tier);
        GL11.glColor4f((rgb >> 16 & 255) / 255F, (rgb >> 8 & 255) / 255F, (rgb & 255) / 255F, alpha);
    }

    private static int color(int tier) {
        return switch (tier) {
            case 1 -> 0xFF50CFFF;
            case 2 -> 0xFF8798FF;
            case 3 -> 0xFFFF509A;
            default -> 0xFF3C6E8F;
        };
    }

    private static int glow(int tier) {
        return color(tier) & 0x00FFFFFF | (tier == 0 ? 0x20000000 : 0x40000000);
    }
}
