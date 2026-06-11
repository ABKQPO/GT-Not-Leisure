package com.science.gtnl.common.gui.recipe;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBackendPropertiesBuilder;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RocketAssemblerBackend extends RecipeMapBackend {

    public static GTRecipe notFoundRecipe = new GTRecipe(
        false,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        0,
        0,
        0);

    static {
        notFoundRecipe.mCanBeBuffered = false;
    }

    public RocketAssemblerBackend(RecipeMapBackendPropertiesBuilder propertiesBuilder) {
        super(propertiesBuilder);
    }

    @Override
    public @Nullable GTRecipe modifyFoundRecipe(GTRecipe recipe, ItemStack[] items, FluidStack[] fluids,
        @Nullable ItemStack specialSlot) {
        if (recipe.mSpecialItems == null) return super.modifyFoundRecipe(recipe, items, fluids, specialSlot);
        for (ItemStack item : items) {
            if (GTUtility.areStacksEqual((ItemStack) recipe.mSpecialItems, item, true))
                return super.modifyFoundRecipe(recipe, items, fluids, specialSlot);
        }
        return notFoundRecipe;
    }
}
