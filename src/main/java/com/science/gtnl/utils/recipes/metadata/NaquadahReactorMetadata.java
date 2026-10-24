package com.science.gtnl.utils.recipes.metadata;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.Nullable;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;
import it.unimi.dsi.fastutil.ints.IntLongPair;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NaquadahReactorMetadata extends RecipeMetadataKey<IntLongPair> {

    public static final NaquadahReactorMetadata INSTANCE = new NaquadahReactorMetadata();
    public static final IntLongPair NONE = IntLongPair.of(0, 0L);

    private NaquadahReactorMetadata() {
        super(IntLongPair.class, "naquadah_reactor_data");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        int tier = cast(value, NONE).leftInt();

        if (tier == 1) {
            recipeInfo.drawText(StatCollector.translateToLocal("gtnl.recipe.metadata.naquadah_reactor.0"));
        } else if (tier == 2) {
            recipeInfo.drawText(StatCollector.translateToLocal("gtnl.recipe.metadata.naquadah_reactor.1"));
        }
    }
}
