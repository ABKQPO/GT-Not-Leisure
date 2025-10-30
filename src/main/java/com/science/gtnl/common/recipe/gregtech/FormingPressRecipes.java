package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.enums.Mods.*;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.config.MainConfig;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class FormingPressRecipes implements IRecipePool {

    public RecipeMap<?> FPR = RecipeMaps.formingPressRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Ingot.get(0), ItemList.Netherite_Nanoparticles.get(1))
            .fluidInputs(Materials.InfusedGold.getMolten(144))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Netherite, 1))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Plate.get(0), ItemList.Netherite_Nanoparticles.get(1))
            .fluidInputs(Materials.InfusedGold.getMolten(144))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Netherite, 1))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                ItemList.Shape_Mold_Plate.get(0),
                ItemList.Netherite_Nanoparticles.get(2))
            .fluidInputs(Materials.InfusedGold.getMolten(288))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Netherite, 1))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(9),
                ItemList.Shape_Mold_Plate.get(0),
                ItemList.Netherite_Nanoparticles.get(9))
            .fluidInputs(Materials.InfusedGold.getMolten(1296))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Netherite, 1))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Rod.get(0), ItemList.Netherite_Nanoparticles.get(1))
            .fluidInputs(Materials.InfusedGold.getMolten(144))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.stick, Materials.Netherite, 2))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Round.get(0), ItemList.Netherite_Nanoparticles.get(1))
            .fluidInputs(Materials.InfusedGold.getMolten(144))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.round, Materials.Netherite, 9))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Bolt.get(0), ItemList.Netherite_Nanoparticles.get(1))
            .fluidInputs(Materials.InfusedGold.getMolten(144))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.round, Materials.Netherite, 8))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Screw.get(0), ItemList.Netherite_Nanoparticles.get(1))
            .fluidInputs(Materials.InfusedGold.getMolten(144))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.screw, Materials.Netherite, 8))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Ring.get(0), ItemList.Netherite_Nanoparticles.get(1))
            .fluidInputs(Materials.InfusedGold.getMolten(144))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.ring, Materials.Netherite, 4))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                ItemList.Shape_Mold_Plate.get(0),
                ItemList.Netherite_Nanoparticles.get(1))
            .fluidInputs(Materials.InfusedGold.getMolten(144))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.foil, Materials.Netherite, 4))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Casing.get(0), ItemList.Netherite_Nanoparticles.get(1))
            .fluidInputs(Materials.InfusedGold.getMolten(144))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.itemCasing, Materials.Netherite, 2))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Gear_Small.get(0), ItemList.Netherite_Nanoparticles.get(1))
            .fluidInputs(Materials.InfusedGold.getMolten(144))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.Netherite, 1))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Gear_Small.get(0), ItemList.Netherite_Nanoparticles.get(4))
            .fluidInputs(Materials.InfusedGold.getMolten(576))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Netherite, 1))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Rotor.get(0), ItemList.Netherite_Nanoparticles.get(5))
            .fluidInputs(Materials.InfusedGold.getMolten(720))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Netherite, 1))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Rod_Long.get(0), ItemList.Netherite_Nanoparticles.get(1))
            .fluidInputs(Materials.InfusedGold.getMolten(144))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.Netherite, 1))
            .duration(300)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FPR);

        if (MainConfig.enableDeleteRecipe) loadDeleteRecipe();
    }

    public void loadDeleteRecipe() {
        GTValues.RA.stdBuilder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(
                ItemList.Circuit_Board_Multifiberglass_Elite.get(1),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Titanium, 1),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.YttriumBariumCuprate, 1),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.NickelZincFerrite, 1),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.NaquadahAlloy, 1),
                MaterialPool.Darmstadtium.get(OrePrefixes.bolt, 4))
            .itemOutputs(ItemList.Optical_Cpu_Containment_Housing.get(1))
            .duration(290)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(FPR);
    }
}
