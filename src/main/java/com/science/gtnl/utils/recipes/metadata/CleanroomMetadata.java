package com.science.gtnl.utils.recipes.metadata;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CleanroomMetadata extends RecipeMetadataKey<Integer> {

    public static final CleanroomMetadata INSTANCE = new CleanroomMetadata();

    public CleanroomMetadata() {
        super(Integer.class, "cleanroom_tier");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        int tier = cast(value, 1);
        switch (tier) {
            case 1 -> recipeInfo.drawText(StatCollector.translateToLocal("CleanroomMetadata.0"));
            case 2 -> recipeInfo.drawText(StatCollector.translateToLocal("CleanroomMetadata.1"));
            case 3 -> recipeInfo.drawText(StatCollector.translateToLocal("CleanroomMetadata.2"));
        }
    }
}
