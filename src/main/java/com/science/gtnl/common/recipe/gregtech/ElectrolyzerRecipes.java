package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.item.ModItems;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.material.nuclear.MaterialsFluorides;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public class ElectrolyzerRecipes implements IRecipePool {

    public RecipeMap<?> ENCR = GTPPRecipeMaps.electrolyzerNonCellRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.FluoroboricAcide.getFluidOrGas(1000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Boron, 1))
            .fluidOutputs(Materials.Hydrogen.getGas(1000), Materials.Fluorine.getGas(4000))
            .duration(840)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.NitronsoniumTetrafluoroborate.get(OrePrefixes.dust, 7))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Boron, 1))
            .fluidOutputs(
                Materials.Nitrogen.getGas(1000),
                Materials.Oxygen.getGas(1000),
                Materials.Fluorine.getGas(4000))
            .duration(840)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.BoronFluoride.getFluidOrGas(1000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Boron, 1))
            .fluidOutputs(Materials.Fluorine.getGas(3000))
            .duration(640)
            .eut(30)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.Benzaldehyde.getFluidOrGas(1000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 7))
            .fluidOutputs(Materials.Hydrogen.getGas(6000), Materials.Oxygen.getGas(1000))
            .duration(112)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.HydrobromicAcid.getFluidOrGas(1000))
            .itemOutputs()
            .fluidOutputs(MaterialsElements.getInstance().BROMINE.getFluidStack(1000), Materials.Hydrogen.getGas(1000))
            .duration(72)
            .eut(30)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.PotassiumSulfate.get(OrePrefixes.dust, 7))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Potassium, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1))
            .fluidOutputs(Materials.Oxygen.getGas(4000))
            .duration(168)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.AmmoniumChloride.get(OrePrefixes.dust, 6))
            .fluidOutputs(
                Materials.Nitrogen.getGas(1000),
                Materials.Hydrogen.getGas(4000),
                Materials.Chlorine.getGas(1000))
            .duration(48)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.PraseodymiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Praseodymium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(WerkstoffMaterialPool.LanthanumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lanthanum, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.ScandiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Scandium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(WerkstoffMaterialPool.EuropiumIIIOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.GadoliniumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gadolinium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.TerbiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Terbium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.DysprosiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Dysprosium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.HolmiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Holmium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.ErbiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Erbium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.ThuliumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thulium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.YtterbiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ytterbium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.LutetiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 6))
            .fluidInputs(GTNLMaterials.FluoroBenzene.getFluidOrGas(1000))
            .fluidOutputs(Materials.Hydrogen.getGas(5000), Materials.Fluorine.getGas(1000))
            .duration(96)
            .eut(TierEU.RECIPE_MV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1))
            .fluidInputs(MaterialsFluorides.URANIUM_HEXAFLUORIDE.getFluidStack(1000))
            .fluidOutputs(Materials.Fluorine.getGas(6000))
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemOutputs(MaterialsElements.getInstance().NEPTUNIUM.getDust(1))
            .fluidInputs(MaterialsFluorides.NEPTUNIUM_HEXAFLUORIDE.getFluidStack(1008))
            .fluidOutputs(Materials.Fluorine.getGas(6000))
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemOutputs(MaterialsElements.getInstance().SELENIUM.getDust(1))
            .fluidInputs(MaterialsFluorides.SELENIUM_HEXAFLUORIDE.getFluidStack(1008))
            .fluidOutputs(Materials.Fluorine.getGas(6000))
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemOutputs(GTNLMaterials.Technetium.get(OrePrefixes.dust, 1))
            .fluidInputs(MaterialsFluorides.TECHNETIUM_HEXAFLUORIDE.getFluidStack(1008))
            .fluidOutputs(Materials.Fluorine.getGas(6000))
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.RadiumChloride.get(OrePrefixes.dust, 3))
            .itemOutputs(MaterialsElements.getInstance().RADIUM.getDust(1))
            .fluidOutputs(Materials.Chlorine.getGas(2000))
            .duration(600)
            .eut(TierEU.RECIPE_IV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.BariumChloride.get(OrePrefixes.dust, 3))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Barium, 1))
            .fluidOutputs(Materials.Chlorine.getGas(2000))
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(ModItems.dustCalciumCarbonate, 1, 5))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Calcium, 1))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 2))
            .fluidInputs(Materials.PolyAluminiumChloride.getFluid(1000))
            .fluidOutputs(
                Materials.Hydrogen.getGas(3000),
                Materials.Oxygen.getGas(3000),
                Materials.Chlorine.getGas(3000))
            .duration(40)
            .eut(TierEU.RECIPE_HV)
            .addTo(ENCR);

        GTValues.RA.stdBuilder()
            .fluidInputs(GTNLMaterials.AmmoniumBisulfate.getFluidOrGas(2000))
            .fluidOutputs(GTNLMaterials.AmmoniumPersulfate.getFluidOrGas(1000), Materials.Hydrogen.getGas(1000))
            .duration(80 * SECONDS)
            .eut(TierEU.EV)
            .addTo(ENCR);
    }
}
