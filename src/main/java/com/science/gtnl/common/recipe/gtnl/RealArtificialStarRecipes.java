package com.science.gtnl.common.recipe.gtnl;

import static com.science.gtnl.utils.enums.GTNLItemList.DepletedExcitedNaquadahFuelRod;
import static com.science.gtnl.utils.enums.GTNLItemList.EnhancementCore;
import static com.science.gtnl.utils.enums.ModList.TwistSpaceTechnology;

import net.minecraft.util.StatCollector;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;

public class RealArtificialStarRecipes implements IRecipePool {

    public RecipeMap<?> RAS = GTNLRecipeMaps.RealArtificialStarRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(EnhancementCore.get(1))
            .specialValue(MainConfig.machine.artificial_star.euEveryEnhancementCore)
            .eut(0)
            .duration(0)
            .fake()
            .addTo(RAS);

        RecipeBuilder.builder()
            .itemInputs(DepletedExcitedNaquadahFuelRod.get(1))
            .specialValue(MainConfig.machine.artificial_star.euEveryDepletedExcitedNaquadahFuelRod)
            .eut(0)
            .duration(0)
            .fake()
            .addTo(RAS);

        if (TwistSpaceTechnology.isModLoaded()) {

            RecipeBuilder.builder()
                .itemInputs(GTModHandler.getModItem(TwistSpaceTechnology.ID, "MetaItem01", 1, 14))
                .specialValue(3)
                .eut(0)
                .duration(0)
                .fake()
                .addTo(RAS);

            RecipeBuilder.builder()
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

            RecipeBuilder.builder()
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
