package com.science.gtnl.utils.recipes.metadata;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;

/**
 * Whether recipe requires compact or not
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SteamFusionMetadata extends RecipeMetadataKey<Integer> {

    public static final SteamFusionMetadata INSTANCE = new SteamFusionMetadata();

    private SteamFusionMetadata() {
        super(Integer.class, "steam_fusion_key");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        int tier = cast(value, 0);
        if (tier != 0) recipeInfo.drawText(StatCollector.translateToLocal("SteamFusionMetadata.0"));
    }
}
