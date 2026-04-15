package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gtPlusPlus.api.recipe.GTPPRecipeMaps.quantumForceTransformerRecipes;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public class QuantumForceTransformerRecipes implements IRecipePool {

    public RecipeMap<?> QFT = GTPPRecipeMaps.quantumForceTransformerRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(Materials.RareEarth.getDust(16))
            .itemOutputs(
                Materials.Cerium.getDust(64),
                Materials.Gadolinium.getDust(64),
                Materials.Samarium.getDust(64),
                WerkstoffMaterialPool.Hafnia.get(OrePrefixes.dust, 64),
                MaterialsElements.getInstance().ZIRCONIUM.getDust(64),
                ItemList.SuperconductorComposite.get(1))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_UHV)
            .metadata(GTRecipeConstants.QFT_CATALYST, GregtechItemList.RareEarthGroupCatalyst.get(0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 2)
            .addTo(QFT);

        RecipeBuilder.builder()
            .itemInputs(
                GGMaterial.naquadahEarth.get(OrePrefixes.dust, 32),
                Materials.Sodium.getDust(64),
                Materials.Carbon.getDust(1))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(96, Materials.Naquadah.getDust(1)),
                Materials.NaquadahEnriched.getDust(28),
                Materials.Naquadria.getDust(24),
                Materials.Titanium.getDust(48),
                Materials.Adamantium.getDust(48),
                Materials.Gallium.getDust(48),
                Materials.Trinium.getDust(48))
            .fluidInputs(
                Materials.Hydrogen.getGas(64000),
                Materials.Fluorine.getGas(64000),
                Materials.Oxygen.getPlasma(100))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .metadata(GTRecipeConstants.QFT_CATALYST, GregtechItemList.SimpleNaquadahCatalyst.get(0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 2)
            .addTo(QFT);

        RecipeBuilder.builder()
            .itemInputs(
                GGMaterial.enrichedNaquadahEarth.get(OrePrefixes.dust, 32),
                Materials.Zinc.getDust(64),
                Materials.Carbon.getDust(1))
            .itemOutputs(
                Materials.Naquadah.getDust(64),
                GTUtility.copyAmountUnsafe(96, Materials.NaquadahEnriched.getDust(1)),
                Materials.Naquadria.getDust(64),
                Materials.Chrome.getDust(64),
                Materials.Trinium.getDust(64))
            .fluidInputs(Materials.SulfuricAcid.getFluid(16000), Materials.Oxygen.getPlasma(100))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_UEV)
            .metadata(GTRecipeConstants.QFT_CATALYST, GregtechItemList.SimpleNaquadahCatalyst.get(0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 2)
            .addTo(QFT);

        GTValues.RA.stdBuilder()
            .itemInputs(GGMaterial.naquadriaEarth.get(OrePrefixes.dust, 32), Materials.Magnesium.getDust(64))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(96, Materials.Naquadria.getDust(1)),
                Materials.Barium.getDust(64),
                Materials.Indium.getDust(64),
                ItemList.NaquadriaSupersolid.get(1))
            .fluidInputs(
                Materials.PhosphoricAcid.getFluid(16_000),
                Materials.SulfuricAcid.getFluid(16_000),
                Materials.Oxygen.getPlasma(100))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_UMV)
            .metadata(GTRecipeConstants.QFT_CATALYST, GregtechItemList.AdvancedNaquadahCatalyst.get(0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 3)
            .addTo(quantumForceTransformerRecipes);

        RecipeBuilder.builder()
            .itemInputs(
                Materials.Lead.getDust(16),
                Materials.Bauxite.getDust(32),
                WerkstoffLoader.Ferberite.get(OrePrefixes.dust, 16))
            .itemOutputs(
                Materials.Titanium.getDust(64),
                Materials.TungstenSteel.getDust(64),
                Materials.TungstenCarbide.getDust(64),
                Materials.Indium.getDust(64))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_UV)
            .metadata(GTRecipeConstants.QFT_CATALYST, GregtechItemList.TitaTungstenIndiumCatalyst.get(0))
            .metadata(GTRecipeConstants.QFT_FOCUS_TIER, 1)
            .addTo(quantumForceTransformerRecipes);
    }
}
