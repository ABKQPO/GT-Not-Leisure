package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.enums.Mods.Forestry;
import static gregtech.api.util.GTRecipeBuilder.INGOTS;
import static gregtech.api.util.GTRecipeBuilder.NUGGETS;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.utils.enums.GTNLItemList;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gregtech.common.items.CombType;
import gregtech.loaders.misc.GTBees;
import gtPlusPlus.core.item.ModItems;

public class BlastFurnaceRecipes implements IRecipePool {

    public RecipeMap<?> BFR = RecipeMaps.blastFurnaceRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTOreDictUnificator.get(OrePrefixes.block, Materials.SiliconSG, 96),
                ItemList.GalliumArsenideCrystal.get(8),
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Neutronium, 4),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(GTNLItemList.NeutroniumBoule.get(1))
            .fluidInputs(Materials.Radon.getGas(32000))
            .duration(18000)
            .eut(TierEU.RECIPE_LuV)
            .metadata(COIL_HEAT, 10000)
            .addTo(BFR);

        GTValues.RA.stdBuilder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1),
                GTUtility.getIntegratedCircuit(11))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Europium, 1))
            .fluidInputs(Materials.Helium.getGas(1000))
            .duration(120)
            .eut(TierEU.RECIPE_IV)
            .metadata(COIL_HEAT, 8300)
            .addTo(BFR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                MaterialPool.PitchblendeSlag.get(OrePrefixes.dust, 12),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumCarbonate, 6))
            .itemOutputs(
                MaterialPool.UraniumSlag.get(OrePrefixes.dust, 12),
                new ItemStack(ModItems.dustCalciumCarbonate, 1, 5),
                GTUtility.copyAmountUnsafe(7, WerkstoffLoader.Sodiumsulfate.get(OrePrefixes.dust, 1)))
            .duration(100)
            .eut(TierEU.RECIPE_HV)
            .metadata(COIL_HEAT, 3650)
            .addTo(BFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1), ItemList.Brittle_Netherite_Scrap.get(1))
            .fluidInputs(Materials.HellishMetal.getMolten(1 * INGOTS))
            .itemOutputs(ItemList.Intensely_Bonded_Netherite_Nanoparticles.get(1))
            .fluidOutputs(Materials.Thaumium.getMolten(2 * NUGGETS))
            .duration(15 * SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .metadata(COIL_HEAT, 7500)
            .addTo(BFR);

        if (Forestry.isModLoaded()) {
            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(2),
                    ItemList.Brittle_Netherite_Scrap.get(1),
                    GTBees.combs.getStackForType(CombType.NETHERITE, 32))
                .fluidInputs(Materials.HellishMetal.getMolten(1 * INGOTS))
                .itemOutputs(ItemList.Intensely_Bonded_Netherite_Nanoparticles.get(2))
                .fluidOutputs(Materials.Thaumium.getMolten(2 * NUGGETS))
                .duration(15 * SECONDS)
                .eut(TierEU.RECIPE_ZPM)
                .metadata(COIL_HEAT, 7500)
                .addTo(BFR);
        }
    }
}
