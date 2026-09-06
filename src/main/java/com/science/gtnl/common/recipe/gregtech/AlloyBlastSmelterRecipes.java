package com.science.gtnl.common.recipe.gregtech;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialsElements;

public class AlloyBlastSmelterRecipes implements IRecipePool {

    public RecipeMap<?> aBS = RecipeMaps.alloyBlastSmelterRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                GTOreDictUnificator.get(MaterialsElements.getInstance().GERMANIUM.getDust(3)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 3))
            .fluidInputs(Materials.Nitrogen.getGas(10000))
            .fluidOutputs(GTNLMaterials.Germaniumtungstennitride.getMolten(2304))
            .duration(9600)
            .eut(30720)
            .addTo(aBS);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Invar, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1))
            .fluidOutputs(GTNLMaterials.HSLASteel.getMolten(720))
            .duration(3750)
            .eut(480)
            .addTo(aBS);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 2))
            .fluidOutputs(GTNLMaterials.MolybdenumDisilicide.getMolten(432))
            .duration(1800)
            .eut(1920)
            .addTo(aBS);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.stone, 2))
            .fluidOutputs(Materials.Lava.getFluid(1000))
            .duration(200)
            .eut(TierEU.RECIPE_LV)
            .addTo(aBS);

        if (MainConfig.recipe.enableDeleteRecipe) loadDeleteRecipe();
    }

    public void loadDeleteRecipe() {
        RecipeBuilder.builder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1))
            .fluidOutputs(Materials.Europium.getMolten(144))
            .duration(120)
            .eut(TierEU.RECIPE_LuV)
            .addTo(aBS);
    }
}
