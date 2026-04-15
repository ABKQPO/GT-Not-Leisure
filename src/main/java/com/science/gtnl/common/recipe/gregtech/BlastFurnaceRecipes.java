package com.science.gtnl.common.recipe.gregtech;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gregtech.common.items.CombType;
import gregtech.loaders.misc.GTBees;
import gtPlusPlus.core.item.ModItems;

public class BlastFurnaceRecipes implements IRecipePool {

    public RecipeMap<?> BFR = RecipeMaps.blastFurnaceRecipes;
    public IRecipeMap BFWG = GTRecipeConstants.BlastFurnaceWithGas;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.block, Materials.SiliconSG, 96),
                ItemList.GalliumArsenideCrystal.get(8),
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Neutronium, 4),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(GTNLItemList.NeutroniumBoule.get(1))
            .duration(18000)
            .eut(TierEU.RECIPE_LuV)
            .metadata(GTRecipeConstants.COIL_HEAT, 10000)
            .metadata(GTRecipeConstants.ADDITIVE_AMOUNT, 32000)
            .metadata(GTRecipeConstants.NO_GAS, false)
            .addTo(BFWG);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1),
                GTUtility.getIntegratedCircuit(11))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Europium, 1))
            .duration(400)
            .eut(TierEU.RECIPE_IV)
            .metadata(GTRecipeConstants.COIL_HEAT, 8300)
            .metadata(GTRecipeConstants.ADDITIVE_AMOUNT, 1000)
            .metadata(GTRecipeConstants.NO_GAS, true)
            .metadata(GTRecipeConstants.NO_GAS_CIRCUIT_CONFIG, 1)
            .addTo(BFWG);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLMaterials.PitchblendeSlag.get(OrePrefixes.dust, 12),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumCarbonate, 6))
            .itemOutputs(
                GTNLMaterials.UraniumSlag.get(OrePrefixes.dust, 12),
                new ItemStack(ModItems.dustCalciumCarbonate, 1, 5),
                GTUtility.copyAmountUnsafe(7, WerkstoffLoader.Sodiumsulfate.get(OrePrefixes.dust, 1)))
            .duration(100)
            .eut(TierEU.RECIPE_HV)
            .metadata(GTRecipeConstants.COIL_HEAT, 3650)
            .addTo(BFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1), ItemList.Brittle_Netherite_Scrap.get(1))
            .fluidInputs(Materials.HellishMetal.getMolten(1 * GTRecipeBuilder.INGOTS))
            .itemOutputs(ItemList.Intensely_Bonded_Netherite_Nanoparticles.get(1))
            .fluidOutputs(Materials.Thaumium.getMolten(2 * GTRecipeBuilder.NUGGETS))
            .duration(15 * GTRecipeBuilder.SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .metadata(GTRecipeConstants.COIL_HEAT, 7500)
            .addTo(BFR);

        if (Mods.Forestry.isModLoaded()) {
            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(2),
                    ItemList.Brittle_Netherite_Scrap.get(1),
                    GTBees.combs.getStackForType(CombType.NETHERITE, 32))
                .fluidInputs(Materials.HellishMetal.getMolten(1 * GTRecipeBuilder.INGOTS))
                .itemOutputs(ItemList.Intensely_Bonded_Netherite_Nanoparticles.get(2))
                .fluidOutputs(Materials.Thaumium.getMolten(2 * GTRecipeBuilder.NUGGETS))
                .duration(15 * GTRecipeBuilder.SECONDS)
                .eut(TierEU.RECIPE_ZPM)
                .metadata(GTRecipeConstants.COIL_HEAT, 7500)
                .addTo(BFR);
        }
    }
}
