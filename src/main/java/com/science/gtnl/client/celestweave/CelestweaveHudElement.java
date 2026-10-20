package com.science.gtnl.client.celestweave;

import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.science.gtnl.client.hud.HudElement;
import com.science.gtnl.client.hud.HudElement.Offset;
import com.science.gtnl.client.hud.HudRegistry;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum CelestweaveHudElement {

    CORE(0.5F, 1F, -92, -12, 10, 10, scale -> -Gap.CORE_HOTBAR - Gap.CORE_RADIUS * scale,
        scale -> -Gap.CORE_BOTTOM - Gap.CORE_RADIUS * scale),
    SHIELD_RING(0.5F, 0.5F, -25, -25, 25, 25, Offset.NONE, Offset.NONE),
    DASH_ARC(0.5F, 0.5F, -18, 12, 18, 27, Offset.NONE, Offset.NONE),
    ALTIMETER(0.5F, 0.5F, -42, -47, 24, 49, scale -> Gap.ALTIMETER * scale, Offset.NONE);

    public static final String LANG_PREFIX = "item.gtnl.celestweave.hud.element.";

    public final HudElement hud;

    CelestweaveHudElement(float anchorX, float anchorY, float minX, float minY, float maxX, float maxY, Offset defaultX,
        Offset defaultY) {
        String id = name().toLowerCase(Locale.ROOT);
        this.hud = HudElement.builder(id)
            .anchor(anchorX, anchorY)
            .bounds(minX, minY, maxX, maxY)
            .offset(defaultX, defaultY)
            .lang(LANG_PREFIX + id)
            .visibleWhen(CelestweaveHudElement::enabled)
            .baseScale(CelestweaveHudElement::baseScale)
            .preview((place, partialTicks) -> CelestweaveHud.preview(this, place, partialTicks))
            .build();
    }

    private static boolean enabled() {
        return MainConfig.client.enableCelestweaveHud;
    }

    private static double baseScale() {
        return MainConfig.client.celestweaveHudScale;
    }

    static void register() {
        for (CelestweaveHudElement element : values()) HudRegistry.register(element.hud);
        HudRegistry.reserveCrosshair(() -> {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            return player != null && CelestweaveData.wearsAny(player);
        });
    }

    private static class Gap {

        static final float CORE_RADIUS = 8F, CORE_HOTBAR = 97F, CORE_BOTTOM = 3F, ALTIMETER = 62F;
    }
}
