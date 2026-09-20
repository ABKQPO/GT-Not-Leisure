package com.science.gtnl.client.text.compat;

import net.minecraft.client.gui.FontRenderer;

import com.gtnewhorizons.angelica.client.font.FontProvider;
import com.gtnewhorizons.angelica.client.font.FontStrategist;
import com.gtnewhorizons.angelica.config.FontConfig;
import com.gtnewhorizons.angelica.glsm.GLStateManager;
import com.gtnewhorizons.angelica.mixins.interfaces.FontRendererAccessor;
import com.science.gtnl.client.text.GlyphTextureMetrics;
import com.science.gtnl.utils.enums.ModList;

import cpw.mods.fml.common.Optional;

/** Optional references live only inside methods removed by Forge when Angelica is absent. */
public class AngelicaTextAdapter {

    public static void beginForeignDraw() {
        if (ModList.Angelica.isModLoaded()) beginAngelicaDraw();
    }

    public static void endForeignDraw() {
        if (ModList.Angelica.isModLoaded()) endAngelicaDraw();
    }

    @Optional.Method(modid = "angelica")
    private static void beginAngelicaDraw() {
        GLStateManager.beginForeignDraw();
    }

    @Optional.Method(modid = "angelica")
    private static void endAngelicaDraw() {
        GLStateManager.endForeignDraw();
    }

    public static FontBatchBridge bridge(FontRenderer font) {
        return ModList.Angelica.isModLoaded() ? angelicaBridge(font) : null;
    }

    public static boolean supportsEffects(FontRenderer font) {
        return !ModList.Angelica.isModLoaded() || bridge(font) != null;
    }

    public static GlyphTextureMetrics glyphTextureMetrics(FontRenderer font, char character) {
        return ModList.Angelica.isModLoaded() ? angelicaGlyphTextureMetrics(font, character) : null;
    }

    @Optional.Method(modid = "angelica")
    private static GlyphTextureMetrics angelicaGlyphTextureMetrics(FontRenderer font, char character) {
        if (!(font instanceof FontRendererAccessor accessor) || accessor.angelica$getBatcher() == null) return null;
        FontProvider provider = FontStrategist.getFontProvider(
            accessor.angelica$getBatcher(),
            character,
            FontConfig.enableCustomFont,
            font.getUnicodeFlag());
        return new GlyphTextureMetrics(provider.getVStart(character), provider.getVSize(character));
    }

    @Optional.Method(modid = "angelica")
    private static FontBatchBridge angelicaBridge(FontRenderer font) {
        if (font instanceof FontRendererAccessor accessor
            && accessor.angelica$getBatcher() instanceof FontBatchBridge bridge) return bridge;
        return null;
    }
}
