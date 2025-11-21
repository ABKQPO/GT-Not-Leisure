package com.science.gtnl.utils.recipes.metadata;

import javax.annotation.Nullable;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import com.science.gtnl.utils.recipes.data.NanitesIntegratedProcessingRecipesData;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.nei.RecipeDisplayInfo;

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
        if (data.bioengineeringModule)
            recipeInfo.drawText(StatCollector.translateToLocal("NanitesIntegratedProcessingMetadata.0"));
        if (data.oreExtractionModule)
            recipeInfo.drawText(StatCollector.translateToLocal("NanitesIntegratedProcessingMetadata.1"));
        if (data.polymerTwistingModule)
            recipeInfo.drawText(StatCollector.translateToLocal("NanitesIntegratedProcessingMetadata.2"));
    }
}
