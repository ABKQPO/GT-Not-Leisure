package com.science.gtnl.utils.recipes.metadata;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IsaMillMetadata extends RecipeMetadataKey<Integer> {

    public static final IsaMillMetadata INSTANCE = new IsaMillMetadata();

    private IsaMillMetadata() {
        super(Integer.class, "isamill_tier");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        int tier = cast(value, 1);
        switch (tier) {
            case 1 -> recipeInfo.drawText(StatCollector.translateToLocal("IsaMillMetadata.0"));
            case 2 -> recipeInfo.drawText(StatCollector.translateToLocal("IsaMillMetadata.1"));
        }
    }
}
