package com.science.gtnl.utils.recipes.metadata;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FuelRefiningMetadata extends RecipeMetadataKey<Integer> {

    public static final FuelRefiningMetadata INSTANCE = new FuelRefiningMetadata();

    private FuelRefiningMetadata() {
        super(Integer.class, "fuel_refining_tier");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        int tier = cast(value, 0);
        recipeInfo.drawText(StatCollector.translateToLocalFormatted("FuelRefiningMetadata", tier));
    }
}
