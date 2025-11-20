package com.science.gtnl.common.recipe.gtnl;

import static com.science.gtnl.config.MainConfig.*;
import static com.science.gtnl.utils.enums.GTNLItemList.*;
import static com.science.gtnl.utils.enums.ModList.TwistSpaceTechnology;

import net.minecraft.util.StatCollector;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;

public class RealArtificialStarRecipes implements IRecipePool {

    public RecipeMap<?> RAS = RecipePool.RealArtificialStarRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(EnhancementCore.get(1))
            .specialValue(euEveryEnhancementCore)
            .eut(0)
            .duration(0)
            .fake()
            .addTo(RAS);

        GTValues.RA.stdBuilder()
            .itemInputs(DepletedExcitedNaquadahFuelRod.get(1))
            .specialValue(euEveryDepletedExcitedNaquadahFuelRod)
            .eut(0)
            .duration(0)
            .fake()
            .addTo(RAS);

        if (TwistSpaceTechnology.isModLoaded()) {

            GTValues.RA.stdBuilder()
                .itemInputs(GTModHandler.getModItem(TwistSpaceTechnology.ID, "MetaItem01", 1, 14))
                .specialValue(3)
                .eut(0)
                .duration(0)
                .fake()
                .addTo(RAS);

            GTValues.RA.stdBuilder()
                .itemInputs(GTModHandler.getModItem(TwistSpaceTechnology.ID, "MetaItem01", 1, 16))
                .itemOutputs(
                    GTModHandler.getModItem(TwistSpaceTechnology.ID, "MetaItem01", 1, 17)
                        .setStackDisplayName(
                            StatCollector.translateToLocal("NEI.RealAntimatterFuelRodGeneratingRecipe.01")))
                .specialValue(1024)
                .eut(0)
                .duration(0)
                .fake()
                .addTo(RAS);

            GTValues.RA.stdBuilder()
                .itemInputs(GTModHandler.getModItem(TwistSpaceTechnology.ID, "MetaItem01", 1, 29))
                .itemOutputs(
                    GTModHandler.getModItem(TwistSpaceTechnology.ID, "MetaItem01", 1, 17)
                        .setStackDisplayName(
                            StatCollector.translateToLocal("NEI.RealAntimatterFuelRodGeneratingRecipe.01")))
                .specialValue(32768)
                .eut(0)
                .duration(0)
                .fake()
                .addTo(RAS);
        }
    }
}
