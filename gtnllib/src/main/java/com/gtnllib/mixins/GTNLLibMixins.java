package com.gtnllib.mixins;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;
import com.gtnllib.mod.ModList;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GTNLLibMixins implements IMixins {

    TEXT_EFFECTS_COMMON(new MixinBuilder("Text effect formatting codes")
        .addClientMixins("texteffect.MixinEnumChatFormattingTextEffects")
        .setPhase(Phase.EARLY)),

    TEXT_EFFECTS_CLIENT(new MixinBuilder("Text effect rendering")
        .addClientMixins(
            "texteffect.MixinFontRendererTextEffects",
            "texteffect.MixinGuiNewChatTextEffects",
            "texteffect.MixinGuiTextFieldTextEffects")
        .setPhase(Phase.EARLY)),

    TEXT_EFFECTS_ANGELICA(new MixinBuilder("Optional Angelica text effect batching")
        .addClientMixins("texteffect.angelica.MixinFontBatchTextEffects")
        .setPhase(Phase.EARLY)
        .addRequiredMod(ModList.Angelica)),

    TEXT_EFFECTS_NEI(
        new MixinBuilder("Optional NEI text effect input").addClientMixins("texteffect.MixinNEIFormattedTextField")
            .setPhase(Phase.LATE)
            .addRequiredMod(ModList.NotEnoughItems));

    private final MixinBuilder builder;
}
