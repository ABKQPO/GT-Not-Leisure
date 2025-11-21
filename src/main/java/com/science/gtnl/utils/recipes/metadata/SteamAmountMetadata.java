package com.science.gtnl.utils.recipes.metadata;

import static kubatech.api.Variables.numberFormat;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SteamAmountMetadata extends RecipeMetadataKey<Long> {

    public static final SteamAmountMetadata INSTANCE = new SteamAmountMetadata();

    private SteamAmountMetadata() {
        super(Long.class, "offer_value");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        long offer = cast(value, 0L);
        recipeInfo.drawText(StatCollector.translateToLocal("CactusWonderMetadata") + numberFormat.format(offer));
    }
}
