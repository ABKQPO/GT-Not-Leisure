package com.science.gtnl.utils.recipes.metadata;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.mixins.early.Gregtech.AccessorRecipeDisplayInfo;
import com.science.gtnl.utils.recipes.data.CircuitNanitesRecipeData;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CircuitNanitesDataMetadata extends RecipeMetadataKey<CircuitNanitesRecipeData> {

    public static final CircuitNanitesDataMetadata INSTANCE = new CircuitNanitesDataMetadata();

    public CircuitNanitesDataMetadata() {
        super(CircuitNanitesRecipeData.class, "circuit_nanites_data");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        CircuitNanitesRecipeData data = cast(value, new CircuitNanitesRecipeData());
        AccessorRecipeDisplayInfo displayInfo = (AccessorRecipeDisplayInfo) recipeInfo;
        displayInfo.setYPos(displayInfo.getYPos() - 80);
        recipeInfo.drawText(StatCollector.translateToLocal("CircuitNanitesDataMetadata.0") + data.speedBoost, 28, 10);
        recipeInfo.drawText(StatCollector.translateToLocal("CircuitNanitesDataMetadata.1") + data.euModifier, 28, 10);
        recipeInfo.drawText(
            StatCollector.translateToLocal("CircuitNanitesDataMetadata.2") + (data.failedChance * 100) + "%",
            28,
            10);
        recipeInfo.drawText(
            StatCollector.translateToLocal("CircuitNanitesDataMetadata.3") + (data.outputMultiplier * 100) + "%",
            28,
            10);
        recipeInfo
            .drawText(StatCollector.translateToLocal("CircuitNanitesDataMetadata.4") + data.parallelCount, 28, 10);
        recipeInfo.drawText(StatCollector.translateToLocal("CircuitNanitesDataMetadata.5") + data.maxTierSkips, 28, 10);
    }

}
