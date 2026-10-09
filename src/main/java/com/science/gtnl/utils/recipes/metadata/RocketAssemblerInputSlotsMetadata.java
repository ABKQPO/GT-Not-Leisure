package com.science.gtnl.utils.recipes.metadata;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
public class RocketAssemblerInputSlotsMetadata extends RecipeMetadataKey<int[]> {

    public static final RocketAssemblerInputSlotsMetadata INSTANCE = new RocketAssemblerInputSlotsMetadata();

    private RocketAssemblerInputSlotsMetadata() {
        super(int[].class, "gtnl_rocket_assembler_input_slots");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {}
}
