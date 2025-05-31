package com.science.gtnl.common.recipe.GregTech;

import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.item.ModItems;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.material.nuclear.MaterialsFluorides;
import gtPlusPlus.core.util.minecraft.ItemUtils;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public class ElectrolyzerRecipes implements IRecipePool {

    final RecipeMap<?> ENCR = GTPPRecipeMaps.electrolyzerNonCellRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs()
            .fluidInputs(MaterialPool.FluoroboricAcide.getFluidOrGas(1000))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 2009))
            .fluidOutputs(FluidRegistry.getFluidStack("hydrogen", 1000), FluidRegistry.getFluidStack("fluorine", 4000))
            .specialValue(0)
            .duration(840)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.NitronsoniumTetrafluoroborate.get(OrePrefixes.dust, 7))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 2009))
            .fluidOutputs(
                FluidRegistry.getFluidStack("nitrogen", 1000),
                FluidRegistry.getFluidStack("oxygen", 1000),
                FluidRegistry.getFluidStack("fluorine", 4000))
            .specialValue(0)
            .duration(840)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs()
            .fluidInputs(MaterialPool.BoronFluoride.getFluidOrGas(1000))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 2009))
            .fluidOutputs(FluidRegistry.getFluidStack("fluorine", 3000))
            .specialValue(0)
            .duration(640)
            .eut(30)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs()
            .fluidInputs(MaterialPool.Benzaldehyde.getFluidOrGas(1000))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 7, 2010))
            .fluidOutputs(FluidRegistry.getFluidStack("hydrogen", 6000), FluidRegistry.getFluidStack("oxygen", 1000))
            .specialValue(0)
            .duration(112)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs()
            .fluidInputs(MaterialPool.HydrobromicAcid.getFluidOrGas(1000))
            .itemOutputs()
            .fluidOutputs(MaterialsElements.getInstance().BROMINE.getFluidStack(1000), Materials.Hydrogen.getGas(1000))
            .specialValue(0)
            .duration(72)
            .eut(30)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.PotassiumSulfate.get(OrePrefixes.dust, 7))
            .fluidInputs()
            .itemOutputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 2, 2025),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 2022))
            .fluidOutputs(Materials.Oxygen.getGas(4000))
            .specialValue(0)
            .duration(168)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.AmmoniumChloride.get(OrePrefixes.dust, 6))
            .itemOutputs()
            .fluidOutputs(
                Materials.Nitrogen.getGas(1000),
                Materials.Hydrogen.getGas(4000),
                Materials.Chlorine.getGas(1000))
            .specialValue(0)
            .duration(48)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.PraseodymiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Praseodymium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .specialValue(0)
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(WerkstoffMaterialPool.LanthanumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lanthanum, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .specialValue(0)
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.ScandiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Scandium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .specialValue(0)
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(WerkstoffMaterialPool.EuropiumIIIOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .specialValue(0)
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.GadoliniumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gadolinium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .specialValue(0)
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.TerbiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Terbium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .specialValue(0)
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.DysprosiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Dysprosium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .specialValue(0)
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.HolmiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Holmium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .specialValue(0)
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.ErbiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Erbium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .specialValue(0)
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.ThuliumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thulium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .specialValue(0)
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.YtterbiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ytterbium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .specialValue(0)
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.LutetiumOxide.get(OrePrefixes.dust, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 2))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .specialValue(0)
            .duration(70)
            .eut(60)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 6))
            .fluidInputs(MaterialPool.FluoroBenzene.getFluidOrGas(1000))
            .fluidOutputs(Materials.Hydrogen.getGas(5000), Materials.Fluorine.getGas(1000))
            .specialValue(0)
            .duration(96)
            .eut(TierEU.RECIPE_MV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1))
            .fluidInputs(MaterialsFluorides.URANIUM_HEXAFLUORIDE.getFluidStack(1000))
            .fluidOutputs(Materials.Fluorine.getGas(6000))
            .specialValue(0)
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemOutputs(MaterialsElements.getInstance().NEPTUNIUM.getDust(1))
            .fluidInputs(MaterialsFluorides.NEPTUNIUM_HEXAFLUORIDE.getFluidStack(1008))
            .fluidOutputs(Materials.Fluorine.getGas(6000))
            .specialValue(0)
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemOutputs(MaterialsElements.getInstance().SELENIUM.getDust(1))
            .fluidInputs(MaterialsFluorides.SELENIUM_HEXAFLUORIDE.getFluidStack(1008))
            .fluidOutputs(Materials.Fluorine.getGas(6000))
            .specialValue(0)
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemOutputs(MaterialPool.Technetium.get(OrePrefixes.dust, 1))
            .fluidInputs(MaterialsFluorides.TECHNETIUM_HEXAFLUORIDE.getFluidStack(1008))
            .fluidOutputs(Materials.Fluorine.getGas(6000))
            .specialValue(0)
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.RadiumChloride.get(OrePrefixes.dust, 3))
            .itemOutputs(MaterialsElements.getInstance().RADIUM.getDust(1))
            .fluidOutputs(Materials.Chlorine.getGas(2000))
            .specialValue(0)
            .duration(600)
            .eut(TierEU.RECIPE_IV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.BariumChloride.get(OrePrefixes.dust, 3))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Barium, 1))
            .fluidOutputs(Materials.Chlorine.getGas(2000))
            .specialValue(0)
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(ENCR);

        RecipeBuilder.builder()
            .itemInputs(ItemUtils.getSimpleStack(ModItems.dustCalciumCarbonate, 5))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Calcium, 1))
            .fluidOutputs(Materials.Oxygen.getGas(3000))
            .specialValue(0)
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(ENCR);
    }
}
