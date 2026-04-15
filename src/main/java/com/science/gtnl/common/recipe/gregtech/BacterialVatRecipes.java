package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeConstants.SIEVERT;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import com.dreammaster.item.NHItemList;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.API.recipe.BartWorksRecipeMaps;
import bartworks.util.BWUtil;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.recipe.Sievert;

public class BacterialVatRecipes implements IRecipePool {

    public RecipeMap<?> BVR = BartWorksRecipeMaps.bacterialVatRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Circuit_Chip_Stemcell.get(64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahEnriched, 1L))
            .itemOutputs(ItemList.Circuit_Chip_Biocell.get(64))
            .fluidInputs(Materials.BioMediumSterilized.getFluid(1))
            .fluidOutputs(FluidRegistry.getFluidStack("mutagen", 1))
            .eut(TierEU.RECIPE_ZPM)
            .metadata(SIEVERT, new Sievert(BWUtil.calculateSv(Materials.NaquadahEnriched), false))
            .duration(800)
            .addTo(BVR);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Circuit_Chip_Stemcell.get(64),
                Mods.NewHorizonsCoreMod.isModLoaded() ? getTCetiESeaweedExtract() : new ItemStack(Items.sugar, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1L))
            .fluidInputs(Materials.GrowthMediumRaw.getFluid(10))
            .fluidOutputs(Materials.BioMediumRaw.getFluid(10))
            .eut(TierEU.RECIPE_EV)
            .metadata(SIEVERT, new Sievert(BWUtil.calculateSv(Materials.NaquadahEnriched), false))
            .duration(1200)
            .addTo(BVR);

    }

    @Optional.Method(modid = "dreamcraft")
    public ItemStack getTCetiESeaweedExtract() {
        return NHItemList.TCetiESeaweedExtract.getIS(16);
    }
}
