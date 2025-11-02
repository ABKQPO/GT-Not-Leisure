package com.science.gtnl.utils.recipes;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FuelRefiningTierKey extends RecipeMetadataKey<Integer> {

    public static final FuelRefiningTierKey INSTANCE = new FuelRefiningTierKey();

    private FuelRefiningTierKey() {
        super(Integer.class, "fuel_refining_tier");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        int tier = cast(value, 0);
        recipeInfo.drawText(StatCollector.translateToLocalFormatted("FuelRefiningTierKey", tier));
    }
}
