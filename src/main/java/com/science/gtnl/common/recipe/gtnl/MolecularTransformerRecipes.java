package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import appeng.api.AEApi;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class MolecularTransformerRecipes implements IRecipePool {

    public RecipeMap<?> MTR = GTNLRecipeMaps.MolecularTransformerRecipes;

    @Override
    public void loadRecipes() {
        var aeMaterials = AEApi.instance()
            .definitions()
            .materials();

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Sapphire, 1L))
            .itemOutputs(GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "itemBatLamaCrystal", 1, 26))
            .duration(800)
            .eut(524288)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Ruby, 1L))
            .itemOutputs(GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "itemBatCrystal", 2, 26))
            .duration(200)
            .eut(524288)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.ClayedGlowstone.get(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Sunnarium, 1L))
            .duration(200)
            .eut(524288)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SiliconSG, 1L))
            .duration(40)
            .eut(TierEU.RECIPE_HV)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.WroughtIron, 1L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1L))
            .duration(80)
            .eut(480)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Graphene, 1L))
            .duration(100)
            .eut(TierEU.RECIPE_IV)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 2L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1L))
            .duration(200)
            .eut(120)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.coal, 4))
            .itemOutputs(new ItemStack(Items.diamond, 1))
            .duration(120)
            .eut(7680)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Calcium, 2L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Bone, 1L))
            .duration(20)
            .eut(30)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.dye, 6, 15))
            .itemOutputs(new ItemStack(Items.bone, 1))
            .duration(40)
            .eut(30)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.WroughtIron, 1L))
            .duration(20)
            .eut(120)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.quartz, 1))
            .itemOutputs(
                aeMaterials.certusQuartzCrystalCharged()
                    .maybeStack(1)
                    .orNull())
            .duration(160)
            .eut(120)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.coal, 1, 1))
            .itemOutputs(new ItemStack(Items.coal, 1))
            .duration(200)
            .eut(120)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.ActivatedCarbon, 1L))
            .duration(20)
            .eut(122880)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.DamascusSteel, 1L))
            .duration(20)
            .eut(480)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.skull, 2, 1))
            .itemOutputs(new ItemStack(Items.nether_star, 1))
            .duration(800)
            .eut(1920)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Platinum, 1L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Iridium, 1L))
            .duration(400)
            .eut(480)
            .addTo(MTR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.gem, Materials.Quartzite, 1L))
            .itemOutputs(
                aeMaterials.certusQuartzCrystalCharged()
                    .maybeStack(1)
                    .orNull())
            .duration(160)
            .eut(120)
            .addTo(MTR);

    }
}
