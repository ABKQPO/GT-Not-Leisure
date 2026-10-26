package com.gtnllib.client.text.effect;

import net.minecraft.util.ResourceLocation;

import com.gtnllib.client.text.TextEffectRegistry;
import com.gtnllib.text.TextEffectColors;
import com.gtnllib.text.TextEffectStyle;
import com.gtnllib.text.TextEffects;

public class BuiltinTextEffects {

    public static void register() {
        register(
            TextEffects.INFERNUM_RED_RARITY,
            TextEffectColors.rgb(TextEffectColors.effectInfernumRed),
            TextEffectColors.rgb(TextEffectColors.effectInfernumRedShadow));
        register(
            TextEffects.GENESIS_COMPONENT_RARITY_SHADER,
            TextEffectColors.rgb(TextEffectColors.effectGenesisPurple),
            TextEffectColors.rgb(TextEffectColors.effectGenesisGold),
            TextEffectColors.rgb(TextEffectColors.effectGenesisPink));
        register(
            TextEffects.PULSE_CIRCLE,
            TextEffectColors.rgb(TextEffectColors.effectPulsePink),
            TextEffectColors.rgb(TextEffectColors.effectPulsePinkHighlight));
        register(TextEffects.NAMELESS_BOSS_BAR_SHADER, TextEffectColors.rgb(TextEffectColors.effectWhite));
        register(
            TextEffects.PULSE_UPWARDS,
            TextEffectColors.rgb(TextEffectColors.effectPulseGreen),
            TextEffectColors.rgb(TextEffectColors.effectPulseGreenHighlight));
        register(
            TextEffects.CALAMITY_RED,
            TextEffectColors.rgb(TextEffectColors.effectCalamityRed),
            TextEffectColors.rgb(TextEffectColors.effectCalamityRedShadow));
        register(
            TextEffects.EXOTIC_RAINBOW,
            TextEffectColors.rgb(TextEffectColors.effectExoticRed),
            TextEffectColors.rgb(TextEffectColors.effectExoticBlue),
            TextEffectColors.rgb(TextEffectColors.effectExoticGreen));
        registerRarity(
            TextEffects.SUPERBOSS_RARITY,
            1.5f,
            TextEffectColors.rgb(TextEffectColors.effectSuperbossBlue),
            TextEffectColors.rgb(TextEffectColors.effectSuperbossNavy));
        registerRarity(
            TextEffects.INFERNUM_SPARK_RARITY,
            1,
            TextEffectColors.rgb(TextEffectColors.effectCyan),
            TextEffectColors.rgb(TextEffectColors.effectSkyBlue),
            TextEffectColors.rgb(TextEffectColors.effectCyan),
            TextEffectColors.rgb(TextEffectColors.effectYellow));
        TextEffectRegistry.register(
            TextEffects.INFERNUM_CYAN_SPARK.rendererId(),
            new InfernumCyanSparkTextEffect(
                fragment(TextEffects.INFERNUM_CYAN_SPARK),
                TextEffectColors.rgb(TextEffectColors.effectSkyBlue),
                TextEffectColors.rgb(TextEffectColors.effectCyan),
                TextEffectColors.rgb(TextEffectColors.effectCyan),
                TextEffectColors.rgb(TextEffectColors.effectYellow)));
        TextEffectRegistry.register(
            TextEffects.BURNISHED_AURIC.rendererId(),
            new BurnishedAuricTextEffect(
                fragment(TextEffects.BURNISHED_AURIC),
                TextEffectColors.rgb(TextEffectColors.effectAuricBrown),
                TextEffectColors.rgb(TextEffectColors.effectAuricWine),
                TextEffectColors.rgb(TextEffectColors.effectAuricGold),
                TextEffectColors.rgb(TextEffectColors.effectAuricBlue),
                TextEffectColors.rgb(TextEffectColors.effectAuricCyan)));
        registerRarity(
            TextEffects.EVERCOLD_CYAN,
            1.5f,
            TextEffectColors.rgb(TextEffectColors.effectEvercoldBlue),
            TextEffectColors.rgb(TextEffectColors.effectEvercoldNavy));
        registerRarity(
            TextEffects.STARSILVER_RARITY,
            1,
            TextEffectColors.rgb(TextEffectColors.effectStarsilverWhite),
            TextEffectColors.rgb(TextEffectColors.effectStarsilverNavy),
            TextEffectColors.rgb(TextEffectColors.effectWhite));
        register(
            TextEffects.NEBULA_RIFT,
            TextEffectColors.rgb(TextEffectColors.effectNebulaPurple),
            TextEffectColors.rgb(TextEffectColors.effectNebulaCyan),
            TextEffectColors.rgb(TextEffectColors.effectNebulaPink),
            TextEffectColors.rgb(TextEffectColors.effectWhite));
        register(
            TextEffects.PRISMATIC_SCAN,
            TextEffectColors.rgb(TextEffectColors.effectPrismaticRed),
            TextEffectColors.rgb(TextEffectColors.effectPrismaticCyan),
            TextEffectColors.rgb(TextEffectColors.effectPrismaticYellow),
            TextEffectColors.rgb(TextEffectColors.effectWhite));
        register(
            TextEffects.QUANTUM_GLITCH,
            TextEffectColors.rgb(TextEffectColors.effectQuantumCyan),
            TextEffectColors.rgb(TextEffectColors.effectQuantumPink),
            TextEffectColors.rgb(TextEffectColors.effectQuantumYellow));
        register(
            TextEffects.MOLTEN_CORE,
            TextEffectColors.rgb(TextEffectColors.effectMoltenBrown),
            TextEffectColors.rgb(TextEffectColors.effectMoltenRed),
            TextEffectColors.rgb(TextEffectColors.effectMoltenGold),
            TextEffectColors.rgb(TextEffectColors.effectMoltenWhite));
        register(
            TextEffects.PIXEL_REASSEMBLY,
            TextEffectColors.rgb(TextEffectColors.effectPixelNavy),
            TextEffectColors.rgb(TextEffectColors.effectPixelTeal),
            TextEffectColors.rgb(TextEffectColors.effectPixelLime));
        register(
            TextEffects.REFRACTED_PRISM,
            TextEffectColors.rgb(TextEffectColors.effectRefractedNavy),
            TextEffectColors.rgb(TextEffectColors.effectRefractedCyan),
            TextEffectColors.rgb(TextEffectColors.effectRefractedPink),
            TextEffectColors.rgb(TextEffectColors.effectWhite));
        register(
            TextEffects.RUNE_ETCHING,
            TextEffectColors.rgb(TextEffectColors.effectRuneTeal),
            TextEffectColors.rgb(TextEffectColors.effectRuneGreen),
            TextEffectColors.rgb(TextEffectColors.effectRuneWhite));
        register(
            TextEffects.CIRCUIT_AWAKENING,
            TextEffectColors.rgb(TextEffectColors.effectCircuitTeal),
            TextEffectColors.rgb(TextEffectColors.effectCircuitGreen),
            TextEffectColors.rgb(TextEffectColors.effectCircuitGold));
        register(
            TextEffects.GHOST_ECHO,
            TextEffectColors.rgb(TextEffectColors.effectGhostBlue),
            TextEffectColors.rgb(TextEffectColors.effectGhostCyan),
            TextEffectColors.rgb(TextEffectColors.effectGhostPurple));
        register(
            TextEffects.PAPER_FOLD,
            TextEffectColors.rgb(TextEffectColors.effectPaperNavy),
            TextEffectColors.rgb(TextEffectColors.effectPaperSilver),
            TextEffectColors.rgb(TextEffectColors.effectPaperCream));
        register(
            TextEffects.FROST_CRYSTAL,
            TextEffectColors.rgb(TextEffectColors.effectFrostNavy),
            TextEffectColors.rgb(TextEffectColors.effectFrostBlue),
            TextEffectColors.rgb(TextEffectColors.effectFrostWhite));
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
