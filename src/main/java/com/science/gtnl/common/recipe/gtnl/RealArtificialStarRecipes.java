package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.util.StatCollector;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.recipe.RecipeMap;

public class RealArtificialStarRecipes implements IRecipePool {

    public RecipeMap<?> RAS = GTNLRecipeMaps.RealArtificialStarRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.EnhancementCore.get(1))
            .specialValue(MainConfig.machine.artificial_star.euEveryEnhancementCore)
            .eut(0)
            .duration(0)
            .fake()
            .addTo(RAS);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.DepletedExcitedNaquadahFuelRod.get(1))
            .specialValue(MainConfig.machine.artificial_star.euEveryDepletedExcitedNaquadahFuelRod)
            .eut(0)
            .duration(0)
            .fake()
            .addTo(RAS);

        if (ModList.TwistSpaceTechnology.isModLoaded()) {

            RecipeBuilder.builder()
                .itemInputs(ModsItemlist.TwistSpaceAntimatter.get(1))
                .specialValue(3)
                .eut(0)
                .duration(0)
                .fake()
                .addTo(RAS);

            RecipeBuilder.builder()
                .itemInputs(ModsItemlist.TwistSpaceAntimatterFuelRod.get(1))
                .itemOutputs(
                    ModsItemlist.TwistSpaceStellarStructuralFrameMaterial.get(1)
                        .setStackDisplayName(
                            StatCollector.translateToLocal("gtnl.nei.real_antimatter_fuel_rod_generating_recipe.0")))
                .specialValue(1024)
                .eut(0)
                .duration(0)
                .fake()
                .addTo(RAS);

            RecipeBuilder.builder()
                .itemInputs(ModsItemlist.TwistSpaceStrangeAnnihilationFuelRod.get(1))
                .itemOutputs(
                    ModsItemlist.TwistSpaceStellarStructuralFrameMaterial.get(1)
                        .setStackDisplayName(
                            StatCollector.translateToLocal("gtnl.nei.real_antimatter_fuel_rod_generating_recipe.0")))
                .specialValue(32768)
                .eut(0)
                .duration(0)
                .fake()
                .addTo(RAS);
        }
    }
}
