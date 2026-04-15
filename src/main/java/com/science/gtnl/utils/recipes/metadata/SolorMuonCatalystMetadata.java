package com.science.gtnl.utils.recipes.metadata;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.Nullable;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SolorMuonCatalystMetadata extends RecipeMetadataKey<Boolean> {

    public static final SolorMuonCatalystMetadata INSTANCE = new SolorMuonCatalystMetadata();

    private SolorMuonCatalystMetadata() {
        super(Boolean.class, "solor_muon");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        boolean needRing = cast(value, false);
        if (needRing) recipeInfo.drawText(StatCollector.translateToLocal("SolorMuonCatalystMetadata.0"));
    }
}
