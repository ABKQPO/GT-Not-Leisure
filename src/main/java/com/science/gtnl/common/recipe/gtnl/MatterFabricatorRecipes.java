package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.enums.Mods.AppliedEnergistics2;
import static gregtech.api.util.GTModHandler.getModItem;

import net.minecraft.util.StatCollector;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class MatterFabricatorRecipes implements IRecipePool {

    public RecipeMap<?> MFR = RecipePool.MatterFabricatorRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 1L)
                    .setStackDisplayName(StatCollector.translateToLocal("NEI.MatterFabricatorRecipes.01")))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(640, getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 6)))
            .eut(120)
            .duration(200)
            .fake()
            .addTo(MFR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 1L)
                    .setStackDisplayName(StatCollector.translateToLocal("NEI.MatterFabricatorRecipes.01")))
            .fluidOutputs(Materials.UUAmplifier.getFluid(100000))
            .eut(120)
            .duration(200)
            .fake()
            .addTo(MFR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.Emerald, 1L)
                    .setStackDisplayName(StatCollector.translateToLocal("NEI.MatterFabricatorRecipes.02")))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(640, getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 6)))
            .eut(120)
            .duration(200)
            .fake()
            .addTo(MFR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.Emerald, 1L)
                    .setStackDisplayName(StatCollector.translateToLocal("NEI.MatterFabricatorRecipes.02")))
            .fluidOutputs(Materials.UUAmplifier.getFluid(100000))
            .eut(120)
            .duration(200)
            .fake()
            .addTo(MFR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.Iron, 1L)
                    .setStackDisplayName(StatCollector.translateToLocal("NEI.MatterFabricatorRecipes.03")))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(640 * 9, getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 6)))
            .eut(120)
            .duration(200)
            .fake()
            .addTo(MFR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.Iron, 1L)
                    .setStackDisplayName(StatCollector.translateToLocal("NEI.MatterFabricatorRecipes.03")))
            .fluidOutputs(Materials.UUAmplifier.getFluid(900000))
            .eut(120)
            .duration(200)
            .fake()
            .addTo(MFR);
    }
}
