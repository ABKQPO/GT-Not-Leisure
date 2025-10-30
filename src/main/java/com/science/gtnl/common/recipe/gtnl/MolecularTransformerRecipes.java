package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.enums.Mods.AppliedEnergistics2;
import static gregtech.api.enums.Mods.IndustrialCraft2;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.enums.GTNLItemList;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class MolecularTransformerRecipes implements IRecipePool {

    public RecipeMap<?> MTR = RecipePool.MolecularTransformerRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Sapphire, 1L))
            .itemOutputs(GTModHandler.getModItem(IndustrialCraft2.ID, "itemBatLamaCrystal", 1, 26))
            .duration(800)
            .eut(524288)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Ruby, 1L))
            .itemOutputs(GTModHandler.getModItem(IndustrialCraft2.ID, "itemBatCrystal", 2, 26))
            .duration(200)
            .eut(524288)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTNLItemList.ClayedGlowstone.get(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Sunnarium, 1L))
            .duration(200)
            .eut(524288)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.WroughtIron, 1L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1L))
            .duration(80)
            .eut(480)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Graphene, 1L))
            .duration(100)
            .eut(TierEU.RECIPE_IV)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 2L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1L))
            .duration(200)
            .eut(120)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.coal, 4))
            .itemOutputs(new ItemStack(Items.diamond, 1))
            .duration(120)
            .eut(7680)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Calcium, 2L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Bone, 1L))
            .duration(20)
            .eut(30)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.dye, 6, 15))
            .itemOutputs(new ItemStack(Items.bone, 1))
            .duration(40)
            .eut(30)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.WroughtIron, 1L))
            .duration(20)
            .eut(120)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.quartz, 1))
            .itemOutputs(GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 1))
            .duration(160)
            .eut(120)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.coal, 1, 1))
            .itemOutputs(new ItemStack(Items.coal, 1))
            .duration(200)
            .eut(120)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.ActivatedCarbon, 1L))
            .duration(20)
            .eut(122880)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.DamascusSteel, 1L))
            .duration(20)
            .eut(480)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.skull, 2, 1))
            .itemOutputs(new ItemStack(Items.nether_star, 1))
            .duration(800)
            .eut(1920)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Platinum, 1L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Iridium, 1L))
            .duration(400)
            .eut(480)
            .addTo(MTR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.gem, Materials.Quartzite, 1L))
            .itemOutputs(GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 1))
            .duration(160)
            .eut(120)
            .addTo(MTR);

    }
}
