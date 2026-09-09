package com.science.gtnl.utils.recipes.metadata;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.mixins.early.gregtech.AccessorRecipeDisplayInfo;
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
        recipeInfo.drawText(
            StatCollector.translateToLocalFormatted("gtnl.machine.tree_diagram.metadata.eu_discount", data.speedBoost),
            28,
            10);
        recipeInfo.drawText(
            StatCollector.translateToLocalFormatted("gtnl.machine.tree_diagram.metadata.speed_bonus", data.euModifier),
            28,
            10);
        recipeInfo.drawText(
            StatCollector.translateToLocalFormatted(
                "gtnl.machine.tree_diagram.metadata.failure_bonus",
                data.failedChance * 100 + "%"),
            28,
            10);
        recipeInfo.drawText(
            StatCollector.translateToLocalFormatted(
                "gtnl.machine.tree_diagram.metadata.output_coefficient",
                data.outputMultiplier * 100 + "%"),
            28,
            10);
        recipeInfo.drawText(
            StatCollector.translateToLocalFormatted("gtnl.machine.tree_diagram.metadata.parallel", data.parallelCount),
            28,
            10);
        recipeInfo.drawText(
            StatCollector
                .translateToLocalFormatted("gtnl.machine.tree_diagram.metadata.nanites_tier", data.nantiesTier),
            28,
            10);
    }

}
