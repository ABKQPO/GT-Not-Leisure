package com.science.gtnl.client.text.effect;

import net.minecraft.util.ResourceLocation;

import com.science.gtnl.client.text.TextEffectRegistry;
import com.science.gtnl.utils.ColorUtils;
import com.science.gtnl.utils.text.effect.TextEffectStyle;
import com.science.gtnl.utils.text.effect.TextEffects;

public class BuiltinTextEffects {

    private BuiltinTextEffects() {}

    public static void register() {
        register(
            TextEffects.INFERNUM_RED_RARITY,
            ColorUtils.rgb(ColorUtils.effectInfernumRed),
            ColorUtils.rgb(ColorUtils.effectInfernumRedShadow));
        register(
            TextEffects.GENESIS_COMPONENT_RARITY_SHADER,
            ColorUtils.rgb(ColorUtils.effectGenesisPurple),
            ColorUtils.rgb(ColorUtils.effectGenesisGold),
            ColorUtils.rgb(ColorUtils.effectGenesisPink));
        register(
            TextEffects.PULSE_CIRCLE,
            ColorUtils.rgb(ColorUtils.effectPulsePink),
            ColorUtils.rgb(ColorUtils.effectPulsePinkHighlight));
        register(TextEffects.NAMELESS_BOSS_BAR_SHADER, ColorUtils.rgb(ColorUtils.effectWhite));
        register(
            TextEffects.PULSE_UPWARDS,
            ColorUtils.rgb(ColorUtils.effectPulseGreen),
            ColorUtils.rgb(ColorUtils.effectPulseGreenHighlight));
        register(
            TextEffects.CALAMITY_RED,
            ColorUtils.rgb(ColorUtils.effectCalamityRed),
            ColorUtils.rgb(ColorUtils.effectCalamityRedShadow));
        register(
            TextEffects.EXOTIC_RAINBOW,
            ColorUtils.rgb(ColorUtils.effectExoticRed),
            ColorUtils.rgb(ColorUtils.effectExoticBlue),
            ColorUtils.rgb(ColorUtils.effectExoticGreen));
        registerRarity(
            TextEffects.SUPERBOSS_RARITY,
            1.5f,
            ColorUtils.rgb(ColorUtils.effectSuperbossBlue),
            ColorUtils.rgb(ColorUtils.effectSuperbossNavy));
        registerRarity(
            TextEffects.INFERNUM_SPARK_RARITY,
            1,
            ColorUtils.rgb(ColorUtils.effectCyan),
            ColorUtils.rgb(ColorUtils.effectSkyBlue),
            ColorUtils.rgb(ColorUtils.effectCyan),
            ColorUtils.rgb(ColorUtils.effectYellow));
        TextEffectRegistry.register(
            TextEffects.INFERNUM_CYAN_SPARK.rendererId(),
            new InfernumCyanSparkTextEffect(
                fragment(TextEffects.INFERNUM_CYAN_SPARK),
                ColorUtils.rgb(ColorUtils.effectSkyBlue),
                ColorUtils.rgb(ColorUtils.effectCyan),
                ColorUtils.rgb(ColorUtils.effectCyan),
                ColorUtils.rgb(ColorUtils.effectYellow)));
        TextEffectRegistry.register(
            TextEffects.BURNISHED_AURIC.rendererId(),
            new BurnishedAuricTextEffect(
                fragment(TextEffects.BURNISHED_AURIC),
                ColorUtils.rgb(ColorUtils.effectAuricBrown),
                ColorUtils.rgb(ColorUtils.effectAuricWine),
                ColorUtils.rgb(ColorUtils.effectAuricGold),
                ColorUtils.rgb(ColorUtils.effectAuricBlue),
                ColorUtils.rgb(ColorUtils.effectAuricCyan)));
        registerRarity(
            TextEffects.EVERCOLD_CYAN,
            1.5f,
            ColorUtils.rgb(ColorUtils.effectEvercoldBlue),
            ColorUtils.rgb(ColorUtils.effectEvercoldNavy));
        registerRarity(
            TextEffects.STARSILVER_RARITY,
            1,
            ColorUtils.rgb(ColorUtils.effectStarsilverWhite),
            ColorUtils.rgb(ColorUtils.effectStarsilverNavy),
            ColorUtils.rgb(ColorUtils.effectWhite));
        register(
            TextEffects.NEBULA_RIFT,
            ColorUtils.rgb(ColorUtils.effectNebulaPurple),
            ColorUtils.rgb(ColorUtils.effectNebulaCyan),
            ColorUtils.rgb(ColorUtils.effectNebulaPink),
            ColorUtils.rgb(ColorUtils.effectWhite));
        register(
            TextEffects.PRISMATIC_SCAN,
            ColorUtils.rgb(ColorUtils.effectPrismaticRed),
            ColorUtils.rgb(ColorUtils.effectPrismaticCyan),
            ColorUtils.rgb(ColorUtils.effectPrismaticYellow),
            ColorUtils.rgb(ColorUtils.effectWhite));
        register(
            TextEffects.QUANTUM_GLITCH,
            ColorUtils.rgb(ColorUtils.effectQuantumCyan),
            ColorUtils.rgb(ColorUtils.effectQuantumPink),
            ColorUtils.rgb(ColorUtils.effectQuantumYellow));
        register(
            TextEffects.MOLTEN_CORE,
            ColorUtils.rgb(ColorUtils.effectMoltenBrown),
            ColorUtils.rgb(ColorUtils.effectMoltenRed),
            ColorUtils.rgb(ColorUtils.effectMoltenGold),
            ColorUtils.rgb(ColorUtils.effectMoltenWhite));
        register(
            TextEffects.PIXEL_REASSEMBLY,
            ColorUtils.rgb(ColorUtils.effectPixelNavy),
            ColorUtils.rgb(ColorUtils.effectPixelTeal),
            ColorUtils.rgb(ColorUtils.effectPixelLime));
        register(
            TextEffects.REFRACTED_PRISM,
            ColorUtils.rgb(ColorUtils.effectRefractedNavy),
            ColorUtils.rgb(ColorUtils.effectRefractedCyan),
            ColorUtils.rgb(ColorUtils.effectRefractedPink),
            ColorUtils.rgb(ColorUtils.effectWhite));
        register(
            TextEffects.RUNE_ETCHING,
            ColorUtils.rgb(ColorUtils.effectRuneTeal),
            ColorUtils.rgb(ColorUtils.effectRuneGreen),
            ColorUtils.rgb(ColorUtils.effectRuneWhite));
        register(
            TextEffects.CIRCUIT_AWAKENING,
            ColorUtils.rgb(ColorUtils.effectCircuitTeal),
            ColorUtils.rgb(ColorUtils.effectCircuitGreen),
            ColorUtils.rgb(ColorUtils.effectCircuitGold));
        register(
            TextEffects.GHOST_ECHO,
            ColorUtils.rgb(ColorUtils.effectGhostBlue),
            ColorUtils.rgb(ColorUtils.effectGhostCyan),
            ColorUtils.rgb(ColorUtils.effectGhostPurple));
        register(
            TextEffects.PAPER_FOLD,
            ColorUtils.rgb(ColorUtils.effectPaperNavy),
            ColorUtils.rgb(ColorUtils.effectPaperSilver),
            ColorUtils.rgb(ColorUtils.effectPaperCream));
        register(
            TextEffects.FROST_CRYSTAL,
            ColorUtils.rgb(ColorUtils.effectFrostNavy),
            ColorUtils.rgb(ColorUtils.effectFrostBlue),
            ColorUtils.rgb(ColorUtils.effectFrostWhite));
    }

    private static void register(TextEffectStyle style, int... colors) {
        register(style, 1, false, colors);
    }

    private static void registerRarity(TextEffectStyle style, float paddingScale, int... colors) {
        register(style, paddingScale, true, colors);
    }

    private static void register(TextEffectStyle style, float paddingScale, boolean premultipliedAlpha, int... colors) {
        TextEffectRegistry.register(
            style.rendererId(),
            new ShaderTextEffect(fragment(style), paddingScale, premultipliedAlpha, colors));
    }

    private static ResourceLocation fragment(TextEffectStyle style) {
        ResourceLocation id = new ResourceLocation(style.rendererId());
        return new ResourceLocation(id.getResourceDomain(), "shaders/text/" + id.getResourcePath() + ".frag.glsl");
    }
}
