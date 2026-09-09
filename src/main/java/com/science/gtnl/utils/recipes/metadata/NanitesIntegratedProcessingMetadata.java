package com.science.gtnl.utils.recipes.metadata;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.science.gtnl.utils.recipes.data.NanitesIntegratedProcessingRecipesData;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NanitesIntegratedProcessingMetadata extends RecipeMetadataKey<NanitesIntegratedProcessingRecipesData> {

    public static final NanitesIntegratedProcessingMetadata INSTANCE = new NanitesIntegratedProcessingMetadata();

    public NanitesIntegratedProcessingMetadata() {
        super(NanitesIntegratedProcessingRecipesData.class, "nanites_integrated_processing_data");
    }

    @Override
    public void drawInfo(@NotNull RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        NanitesIntegratedProcessingRecipesData data = cast(
            value,
            new NanitesIntegratedProcessingRecipesData(false, false, false));
        if (data.bioengineeringModule) recipeInfo.drawText(
            StatCollector.translateToLocal(
                "gtnl.machine.nanites_integrated_processing_center.metadata.requires_bioengineering_module"));
        if (data.oreExtractionModule) recipeInfo.drawText(
            StatCollector.translateToLocal(
                "gtnl.machine.nanites_integrated_processing_center.metadata.requires_ore_extraction_module"));
        if (data.polymerTwistingModule) recipeInfo.drawText(
            StatCollector.translateToLocal(
                "gtnl.machine.nanites_integrated_processing_center.metadata.requires_polymer_twisting_module"));
    }
}
