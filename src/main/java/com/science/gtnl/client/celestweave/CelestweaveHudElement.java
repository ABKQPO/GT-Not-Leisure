package com.science.gtnl.client.celestweave;

import java.util.Locale;
import java.util.function.UnaryOperator;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.gtnllib.client.hud.HudColor;
import com.gtnllib.client.hud.HudElement;
import com.gtnllib.client.hud.HudElement.Offset;
import com.gtnllib.client.hud.HudOption;
import com.gtnllib.client.hud.HudRegistry;
import com.gtnllib.client.hud.HudTiming;
import com.science.gtnl.common.celestweave.CelestweaveData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum CelestweaveHudElement {

    CORE(0.5F, 1F, -92, -12, 10, 10, scale -> -Spec.CORE_HOTBAR - Spec.CORE_RADIUS * scale,
        scale -> -Spec.CORE_BOTTOM - Spec.CORE_RADIUS * scale, builder -> builder.timing(HudTiming.CHANGED)
            .activeWhen(CelestweaveHud::coreActive)
            .changeWhen(CelestweaveHud::coreSignature)
            .idle(Spec.IDLE_SECONDS, Spec.IDLE_ALPHA)
            .option(HudOption.toggle(Spec.READOUT, Spec.READOUT_LANG, true))),
    SHIELD_RING(0.5F, 0.5F, -25, -25, 25, 25, Offset.NONE, Offset.NONE, builder -> builder.timing(HudTiming.ACTIVE)
        .activeWhen(CelestweaveHud::shieldActive)),
    DASH_ARC(0.5F, 0.5F, -18, 12, 18, 27, Offset.NONE, Offset.NONE, builder -> builder.timing(HudTiming.ACTIVE)
        .activeWhen(CelestweaveHud::dashActive)),
    ALTIMETER(0.5F, 0.5F, -42, -47, 24, 49, scale -> Spec.ALTIMETER * scale, Offset.NONE,
        builder -> builder.timing(HudTiming.ACTIVE)
            .activeWhen(CelestweaveHud::altimeterActive));

    public static final String LANG_PREFIX = "item.gtnl.celestweave.hud.element.";
    public static final String READOUT = Spec.READOUT;

    public final HudElement hud;

    CelestweaveHudElement(float anchorX, float anchorY, float minX, float minY, float maxX, float maxY, Offset defaultX,
        Offset defaultY, UnaryOperator<HudElement.Builder> extras) {
        String id = name().toLowerCase(Locale.ROOT);
        this.hud = extras.apply(
            HudElement.builder(id)
                .anchor(anchorX, anchorY)
                .bounds(minX, minY, maxX, maxY)
                .offset(defaultX, defaultY)
                .lang(LANG_PREFIX + id)
                .style(HudElement.STANDARD_STYLE)
                .color(HudColor.ACCENT)
                .inUseWhen(CelestweaveHudElement::worn)
                .preview((place, partialTicks) -> CelestweaveHud.preview(this, place, partialTicks)))
            .build();
    }

    private static boolean worn() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        return player != null && CelestweaveData.wearsAny(player);
    }

    static void register() {
        for (CelestweaveHudElement element : values()) HudRegistry.register(element.hud);
        HudRegistry.reserveCrosshair(CelestweaveHudElement::worn);
    }

    private static class Spec {

        static final float CORE_RADIUS = 8F, CORE_HOTBAR = 97F, CORE_BOTTOM = 3F, ALTIMETER = 62F, IDLE_SECONDS = 3F,
            IDLE_ALPHA = 0.35F;
        static final String READOUT = "readout", READOUT_LANG = "item.gtnl.celestweave.hud.option.readout";
    }
}
