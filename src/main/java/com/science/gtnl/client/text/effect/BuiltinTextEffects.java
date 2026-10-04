package com.science.gtnl.client.text.effect;

import net.minecraft.util.ResourceLocation;

import com.science.gtnl.client.text.TextEffectRegistry;
import com.science.gtnl.utils.text.effect.TextEffectStyle;
import com.science.gtnl.utils.text.effect.TextEffects;

public class BuiltinTextEffects {

    private BuiltinTextEffects() {}

    public static void register() {
        register(TextEffects.INFERNUM_RED_RARITY, 0xFF4500, 0xC80000);
        register(TextEffects.GENESIS_COMPONENT_RARITY_SHADER, 0x7F51FF, 0xFFEC47, 0xF06DE4);
        register(TextEffects.PULSE_CIRCLE, 0xFF309A, 0xFFA9F0);
        register(TextEffects.NAMELESS_BOSS_BAR_SHADER, 0xFFFFFF);
        register(TextEffects.PULSE_UPWARDS, 0x1CDE98, 0xA8F5E4);
        register(TextEffects.CALAMITY_RED, 0xF21B1B, 0xB4144B);
        register(TextEffects.EXOTIC_RAINBOW, 0xFF6B6B, 0x7DC4E1, 0xD3EB6C);
        registerRarity(TextEffects.SUPERBOSS_RARITY, 1.5f, 0x5F6F96, 0x00213C);
        registerRarity(TextEffects.INFERNUM_SPARK_RARITY, 1, 0x00FFFF, 0x87CEEB, 0x00FFFF, 0xFFFF00);
        TextEffectRegistry.register(
            TextEffects.INFERNUM_CYAN_SPARK.rendererId(),
            new InfernumCyanSparkTextEffect(
                fragment(TextEffects.INFERNUM_CYAN_SPARK),
                0x87CEEB,
                0x00FFFF,
                0x00FFFF,
                0xFFFF00));
        TextEffectRegistry.register(
            TextEffects.BURNISHED_AURIC.rendererId(),
            new BurnishedAuricTextEffect(
                fragment(TextEffects.BURNISHED_AURIC),
                0x9D6E0B,
                0x4D0021,
                0xFEE775,
                0x00B7F1,
                0x5ACFFF));
        registerRarity(TextEffects.EVERCOLD_CYAN, 1.5f, 0x44678B, 0x26435F);
        registerRarity(TextEffects.STARSILVER_RARITY, 1, 0xDEE6F4, 0x282C5A, 0xFFFFFF);
        register(TextEffects.NEBULA_RIFT, 0x7455A9, 0x18D2F0, 0xD95FFF, 0xFFFFFF);
        register(TextEffects.PRISMATIC_SCAN, 0xFF3B6B, 0x35E5FF, 0xF8F55A, 0xFFFFFF);
        register(TextEffects.QUANTUM_GLITCH, 0x00F0FF, 0xFF2C8A, 0xF6FF65);
        register(TextEffects.MOLTEN_CORE, 0xBA3A16, 0xFF3B12, 0xFFB11A, 0xFFF4A3);
        register(TextEffects.PIXEL_REASSEMBLY, 0x192C39, 0x35E6D0, 0xE8FF96);
        register(TextEffects.REFRACTED_PRISM, 0x23384C, 0x67D6E6, 0xF6C6ED, 0xFFFFFF);
        register(TextEffects.RUNE_ETCHING, 0x315A54, 0x68CFA7, 0xE8F7D4);
        register(TextEffects.CIRCUIT_AWAKENING, 0x4B777B, 0x31B7AD, 0xF4DD83);
        register(TextEffects.GHOST_ECHO, 0xA8DAEF, 0x51B4D2, 0xDABDF7);
        register(TextEffects.PAPER_FOLD, 0x30323B, 0xBBC1C9, 0xF5E1C0);
        register(TextEffects.FROST_CRYSTAL, 0x24455B, 0x73CDE0, 0xF3FBFF);
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
