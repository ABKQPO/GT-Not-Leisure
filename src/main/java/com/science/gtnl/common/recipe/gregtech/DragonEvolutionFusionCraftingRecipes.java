package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.enums.Mods.DraconicEvolution;
import static gregtech.api.enums.Mods.GalacticraftAmunRa;
import static gregtech.api.util.GTRecipeConstants.DEFC_CASING_TIER;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsElements;
import kubatech.loaders.DEFCRecipes;

public class DragonEvolutionFusionCraftingRecipes implements IRecipePool {

    public RecipeMap<?> DEFCR = DEFCRecipes.fusionCraftingRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Field_Generator_UEV.get(0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Infinity, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.TengamPurified, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ichorium, 32),
                MaterialsElements.STANDALONE.DRAGON_METAL.getDust(16),
                ItemList.Tesseract.get(2L),
                ItemList.Gravistar.get(16L),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 4),
                ItemList.Circuit_Parts_Chip_Bioware.get(64L))
            .fluidInputs(MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(10000))
            .fluidOutputs(GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(10000))
            .eut(TierEU.RECIPE_UEV)
            .duration(1200)
            .metadata(DEFC_CASING_TIER, 3)
            .addTo(DEFCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(DraconicEvolution.ID, "draconicCore", 0),
                GTModHandler.getModItem(GalacticraftAmunRa.ID, "item.baseItem", 4, 26),
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.InfusedEntropy, 512),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Draconium, 16),
                GTModHandler.getModItem(DraconicEvolution.ID, "awakenedCore", 8))
            .itemOutputs(GTModHandler.getModItem(DraconicEvolution.ID, "chaosShard", 2))
            .fluidInputs(Materials.DraconiumAwakened.getMolten(576))
            .eut(TierEU.RECIPE_UHV)
            .duration(300)
            .metadata(DEFC_CASING_TIER, 3)
            .addTo(DEFCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(DraconicEvolution.ID, "chaosFragment", 1, 1),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Draconium, 1),
                ItemList.NuclearStar.get(1))
            .fluidInputs(Materials.Void.getMolten(1440))
            .fluidOutputs(MaterialsElements.STANDALONE.DRAGON_METAL.getFluidStack(1440))
            .eut(TierEU.RECIPE_UEV)
            .duration(300)
            .metadata(DEFC_CASING_TIER, 4)
            .addTo(DEFCR);

    }
}
