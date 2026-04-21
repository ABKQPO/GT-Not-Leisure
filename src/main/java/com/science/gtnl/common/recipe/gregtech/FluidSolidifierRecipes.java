package com.science.gtnl.common.recipe.gregtech;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class FluidSolidifierRecipes implements IRecipePool {

    public RecipeMap<?> FSR = RecipeMaps.fluidSolidifierRecipes;

    @Override

    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.block, Materials.BorosilicateGlass, 1))
            .fluidInputs(MaterialsUEVplus.QuarkGluonPlasma.getFluid(1152))
            .itemOutputs(GTNLItemList.QuarkGluonPlasmaReinforcedBoronSilicateGlass.get(1))
            .duration(800)
            .eut(TierEU.RECIPE_UXV)
            .addTo(FSR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.block, Materials.BorosilicateGlass, 1))
            .fluidInputs(GGMaterial.shirabon.getMolten(1152))
            .itemOutputs(GTNLItemList.ShirabonReinforcedBoronSilicateGlass.get(1))
            .duration(800)
            .eut(TierEU.RECIPE_UMV)
            .addTo(FSR);

        RecipeBuilder.builder()
            .itemInputs(ItemList.Shape_Mold_Block.get(0))
            .fluidInputs(MaterialsUEVplus.SixPhasedCopper.getMolten(1296))
            .itemOutputs(MaterialsUEVplus.SixPhasedCopper.getBlocks(1))
            .duration(204)
            .eut(TierEU.RECIPE_UEV)
            .addTo(FSR);

        RecipeBuilder.builder()
            .itemInputs(ItemList.Shape_Mold_Block.get(0))
            .fluidInputs(MaterialsUEVplus.WhiteDwarfMatter.getMolten(1296))
            .itemOutputs(MaterialsUEVplus.WhiteDwarfMatter.getBlocks(1))
            .duration(204)
            .eut(TierEU.RECIPE_UEV)
            .addTo(FSR);

        RecipeBuilder.builder()
            .itemInputs(ItemList.Shape_Mold_Block.get(0))
            .fluidInputs(MaterialsUEVplus.BlackDwarfMatter.getMolten(1296))
            .itemOutputs(MaterialsUEVplus.BlackDwarfMatter.getBlocks(1))
            .duration(204)
            .eut(TierEU.RECIPE_UEV)
            .addTo(FSR);

        RecipeBuilder.builder()
            .itemInputs(ItemList.Shape_Mold_Block.get(0))
            .fluidInputs(MaterialsUEVplus.Universium.getMolten(1296))
            .itemOutputs(MaterialsUEVplus.Universium.getBlocks(1))
            .duration(204)
            .eut(TierEU.RECIPE_UMV)
            .addTo(FSR);

        RecipeBuilder.builder()
            .itemInputs(ItemList.Shape_Mold_Block.get(0))
            .fluidInputs(MaterialsUEVplus.MagMatter.getMolten(1296))
            .itemOutputs(MaterialsUEVplus.MagMatter.getBlocks(1))
            .duration(204)
            .eut(TierEU.RECIPE_UMV)
            .addTo(FSR);

        RecipeBuilder.builder()
            .itemInputs(ItemList.Shape_Mold_Block.get(0))
            .fluidInputs(MaterialsUEVplus.Eternity.getMolten(1296))
            .itemOutputs(MaterialsUEVplus.Eternity.getBlocks(1))
            .duration(204)
            .eut(TierEU.RECIPE_UMV)
            .addTo(FSR);
    }
}
