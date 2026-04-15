package com.science.gtnl.utils.recipes.metadata;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NaquadahReactorMetadata extends RecipeMetadataKey<Pair<Integer, Long>> {

    public static final NaquadahReactorMetadata INSTANCE = new NaquadahReactorMetadata();

    @SuppressWarnings("unchecked")
    private NaquadahReactorMetadata() {
        super((Class<Pair<Integer, Long>>) (Class<?>) Pair.class, "naquadah_reactor_data");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        Pair<Integer, Long> data = cast(value, Pair.of(0, 0L));
        int tier = data.getKey();

        if (tier == 1) {
            recipeInfo.drawText(StatCollector.translateToLocal("NaquadahReactorMetadata.0"));
        } else if (tier == 2) {
            recipeInfo.drawText(StatCollector.translateToLocal("NaquadahReactorMetadata.1"));
        }
    }
}
