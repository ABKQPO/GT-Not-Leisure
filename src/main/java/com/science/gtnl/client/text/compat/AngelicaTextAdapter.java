package com.science.gtnl.client.text.compat;

import java.util.Objects;

import net.minecraft.client.gui.FontRenderer;

import com.gtnewhorizons.angelica.client.font.FontProvider;
import com.gtnewhorizons.angelica.client.font.FontStrategist;
import com.gtnewhorizons.angelica.config.FontConfig;
import com.gtnewhorizons.angelica.glsm.GLStateManager;
import com.gtnewhorizons.angelica.mixins.interfaces.FontRendererAccessor;
import com.gtnewhorizons.angelica.rendering.tesr.ModelPartBatcher;
import com.gtnewhorizons.angelica.rendering.tesr.TesrBatchRenderer;
import com.science.gtnl.client.text.GlyphTextureMetrics;
import com.science.gtnl.utils.enums.ModList;

import cpw.mods.fml.common.Optional;

/** Optional references live only inside methods removed by Forge when Angelica is absent. */
public class AngelicaTextAdapter {

    private static FontSettings currentSettings;

    /** Reuses an immutable cache discriminator until native font configuration changes. */
    public static FontSettings fontSettings() {
        return ModList.Angelica.isModLoaded() ? angelicaFontSettings() : null;
    }

    @Optional.Method(modid = "angelica")
    private static FontSettings angelicaFontSettings() {
        FontSettings state = currentSettings;
        if (state == null || state.custom() != FontConfig.enableCustomFont
            || !Objects.equals(state.primary(), FontConfig.customFontNamePrimary)
            || !Objects.equals(state.fallback(), FontConfig.customFontNameFallback)
            || state.quality() != FontConfig.customFontQuality
            || state.scale() != FontConfig.customFontScale
            || state.boldCopies() != FontConfig.boldCopies
            || state.shadowCopies() != FontConfig.shadowCopies
            || state.aaMode() != FontConfig.fontAAMode
            || state.aaStrength() != FontConfig.fontAAStrength
            || state.replacements() != FontConfig.enableGlyphReplacements
            || state.unicodeShadowOffset() != FontConfig.fontShadowOffsetUC) {
            currentSettings = new FontSettings(
                FontConfig.enableCustomFont,
                FontConfig.customFontNamePrimary,
                FontConfig.customFontNameFallback,
                FontConfig.customFontQuality,
                FontConfig.customFontScale,
                FontConfig.boldCopies,
                FontConfig.shadowCopies,
                FontConfig.fontAAMode,
                FontConfig.fontAAStrength,
                FontConfig.enableGlyphReplacements,
                FontConfig.fontShadowOffsetUC);
        }
        return currentSettings;
    }

    public record FontSettings(boolean custom, String primary, String fallback, int quality, float scale,
        int boldCopies, int shadowCopies, int aaMode, int aaStrength, boolean replacements,
        float unicodeShadowOffset) {}

    public static boolean usesCustomFont(FontRenderer font) {
        return ModList.Angelica.isModLoaded() && angelicaUsesCustomFont(font);
    }

    @Optional.Method(modid = "angelica")
    private static boolean angelicaUsesCustomFont(FontRenderer font) {
        return FontConfig.enableCustomFont && font instanceof FontRendererAccessor accessor
            && accessor.angelica$getBatcher() != null
            && !accessor.angelica$getBatcher()
                .forceDefaults();
    }

    public static boolean shouldDeferEffects() {
        return ModList.Angelica.isModLoaded() && hasPendingAngelicaGeometry();
    }

    @Optional.Method(modid = "angelica")
    private static boolean hasPendingAngelicaGeometry() {
        return GLStateManager.isMainThread()
            && (TesrBatchRenderer.INSTANCE.hasPendingGeometry() || ModelPartBatcher.INSTANCE.isActive());
    }

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
