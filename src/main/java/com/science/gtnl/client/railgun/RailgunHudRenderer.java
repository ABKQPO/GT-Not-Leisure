package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.fx.FxColor;
import com.science.gtnl.client.hud.HudEditor;
import com.science.gtnl.client.hud.HudElement;
import com.science.gtnl.client.hud.HudLayout.Placement;
import com.science.gtnl.client.hud.HudRegistry;
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
    private static final float OVERLAY_INTENSITY = 0.75F, PREVIEW_PROGRESS = 0.6F;
    private static final int[] COLORS = { 0x3C6E8F, 0x50CFFF, 0x8798FF, 0xFF509A };
    private static final float[] COLOR_STOPS = { 0, RailgunChargeTier.EHV1.chargeTicks,
        RailgunChargeTier.EHV2.chargeTicks, RailgunChargeTier.EHV3.chargeTicks };
    private static final float BOLT_HALF_WIDTH = 6.5F, BOLT_OFFSET = 10F, BOLT_CLEAR_OFFSET = 26F;

    public static final HudElement CHARGE = HudElement.builder("railgun_charge")
        .bounds(-7, -10, 7, 10)
        .offset(
            scale -> (HudRegistry.crosshairReserved() ? BOLT_CLEAR_OFFSET : BOLT_OFFSET) + BOLT_HALF_WIDTH * scale,
            HudElement.Offset.NONE)
        .lang("item.gtnl.celestweave.hud.element.railgun_charge")
        .visibleWhen(
            () -> MainConfig.client.enableCelestweaveHud && RailgunCharge.charging(Minecraft.getMinecraft().thePlayer))
        .baseScale(() -> MainConfig.client.celestweaveHudScale)
        .preview((place, partialTicks) -> preview(place))
        .render(RailgunHudRenderer::render)
        .build();

    @SubscribeEvent
    public void overlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS) return;
        var minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;
        if (!RailgunCharge.charging(player) || HudEditor.isOpen() || minecraft.gameSettings.thirdPersonView != 0)
            return;
        int width = event.resolution.getScaledWidth(), height = event.resolution.getScaledHeight();
        float units = RailgunCharge.units(player, event.partialTicks);
        drawOverlay(width, height, Math.min(1F, units / RailgunData.FULL_CHARGE_UNITS), chargeRgb(units));
    }

    private static void render(Placement place, float partialTicks) {
        float units = RailgunCharge.units(Minecraft.getMinecraft().thePlayer, partialTicks);
        drawBolt(
            Minecraft.getMinecraft(),
            place,
            Math.min(1F, units / RailgunData.FULL_CHARGE_UNITS),
            chargeRgb(units));
    }

    private static void preview(Placement place) {
        drawBolt(
            Minecraft.getMinecraft(),
            place,
            PREVIEW_PROGRESS,
            chargeRgb(PREVIEW_PROGRESS * RailgunData.FULL_CHARGE_UNITS));
    }

    private static void drawOverlay(int width, int height, float progress, int rgb) {
        if (!MainConfig.client.enableRailgunBloom || !MainConfig.client.enableRailgunShaders) return;
        RailgunShaderEffect shader = RailgunShaderEffect.CHARGE;
        if (!shader.begin()) return;
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        try {
            shader.uniform1f("time", (float) ((System.currentTimeMillis() % 3600000L) / 1000.0));
            shader.uniform1f("progress", progress * progress * OVERLAY_INTENSITY);
            shader.uniform2f("screen", width, height);
            FxColor.gl(rgb, 1F);
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

    private static void drawBolt(Minecraft minecraft, Placement place, float progress, int rgb) {
        int filled = Math.round(ICON_HEIGHT * progress);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        GL11.glPushMatrix();
        try {
            GL11.glTranslatef(
                (float) Math.floor(place.x() - ICON_WIDTH / 2F * place.scale()),
                (float) Math.floor(place.y() - ICON_HEIGHT / 2F * place.scale()),
                0);
            GL11.glScalef(place.scale(), place.scale(), 1);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            minecraft.getTextureManager()
                .bindTexture(BOLT_EMPTY);
            Gui.func_146110_a(0, 0, 0, 0, ICON_WIDTH, ICON_HEIGHT, ICON_WIDTH, ICON_HEIGHT);
            if (filled <= 0) return;
            int top = ICON_HEIGHT - filled;
            FxColor.gl(rgb, 1F);
            minecraft.getTextureManager()
                .bindTexture(BOLT_FILL);
            Gui.func_146110_a(0, top, 0, top, ICON_WIDTH, filled, ICON_WIDTH, ICON_HEIGHT);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            minecraft.getTextureManager()
                .bindTexture(BOLT_HIGHLIGHT);
            Gui.func_146110_a(0, top, 0, top, ICON_WIDTH, filled, ICON_WIDTH, ICON_HEIGHT);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private static int chargeRgb(float units) {
        if (units <= COLOR_STOPS[0]) return COLORS[0];
        for (int i = 1; i < COLOR_STOPS.length; i++) {
            if (units >= COLOR_STOPS[i]) continue;
            float t = (units - COLOR_STOPS[i - 1]) / (COLOR_STOPS[i] - COLOR_STOPS[i - 1]);
            return FxColor.lerp(COLORS[i - 1], COLORS[i], t);
        }
        return COLORS[COLORS.length - 1];
    }
}
