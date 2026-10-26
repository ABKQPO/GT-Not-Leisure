package com.science.gtnl.common.recipe.gtnl;

import java.util.List;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.machine.multiblock.ElementCopying;

import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class ElementCopyingRecipes implements IRecipePool {

    public static List<ElementCopying.ElementCopyingEntry> ENTRIES = new ObjectArrayList<>();

    @Override
    public void loadRecipes() {
        for (GTRecipe recipe : RecipeMaps.replicatorRecipes.getAllRecipes()) {
            if (recipe.mOutputs != null && recipe.mOutputs.length > 0
                && recipe.mFluidInputs != null
                && recipe.mFluidInputs.length > 0) {
                ENTRIES.add(
                    new ElementCopying.ItemCopyingEntry(
                        ItemId.createWithoutNBT(recipe.mOutputs[0]),
                        Math.max(1, recipe.mFluidInputs[0].amount / 10),
                        (long) recipe.mEUt * recipe.mDuration));

            } else if (recipe.mFluidOutputs != null && recipe.mFluidOutputs.length > 0
                && recipe.mFluidInputs != null
                && recipe.mFluidInputs.length > 0) {
                    ENTRIES.add(
                        new ElementCopying.FluidCopyingEntry(
                            GTUtility.FluidId.create(recipe.mFluidOutputs[0]),
                            Math.max(1, recipe.mFluidInputs[0].amount / 10),
                            (long) recipe.mEUt * recipe.mDuration));
                }
        }
    }
}
