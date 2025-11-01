package com.science.gtnl.utils.recipes;

import javax.annotation.Nullable;

import com.science.gtnl.mixins.late.Gregtech.AccessorRecipeDisplayInfo;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.nei.RecipeDisplayInfo;

public class CircuitNanitesDataSpecialValue extends RecipeMetadataKey<CircuitNanitesRecipeData> {

    public static final CircuitNanitesDataSpecialValue INSTANCE = new CircuitNanitesDataSpecialValue();

    public CircuitNanitesDataSpecialValue() {
        super(CircuitNanitesRecipeData.class, "circuit_nanites_data");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        CircuitNanitesRecipeData data = cast(value, new CircuitNanitesRecipeData());
        AccessorRecipeDisplayInfo displayInfo = (AccessorRecipeDisplayInfo) recipeInfo;
        displayInfo.setYPos(displayInfo.getYPos() - 80);
        recipeInfo.drawText(String.format("Speed Boost: %.2f", data.speedBoost), 28, 10);
        recipeInfo.drawText(String.format("EU Modifier: %.2f", data.euModifier), 28, 10);
        recipeInfo.drawText(String.format("Success Chance: %.2f%%", data.successChance * 100), 28, 10);
        recipeInfo.drawText(String.format("Failed Chance: %.2f%%", data.failedChance * 100), 28, 10);
        recipeInfo.drawText(String.format("Output Multiplier: %.2f%%", data.outputMultiplier * 100), 28, 10);
        recipeInfo.drawText(String.format("Parallel Count: %d", data.parallelCount), 28, 10);
        recipeInfo.drawText(String.format("Max Tier Skips: %d", data.maxTierSkips), 28, 10);
    }
}
