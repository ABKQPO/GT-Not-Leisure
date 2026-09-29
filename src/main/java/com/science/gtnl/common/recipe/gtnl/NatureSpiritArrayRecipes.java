package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.recipe.RecipeMap;

public class NatureSpiritArrayRecipes implements IRecipePool {

    public RecipeMap<?> NSAR = GTNLRecipeMaps.NatureSpiritArrayRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(ItemUtils.getSpecialFlower("asgardandelion", 0))
            .fluidOutputs(GTNLMaterials.FluidMana.getFluidOrGas(2000000))
            .duration(20)
            .eut(491520)
            .addTo(NSAR);

        RecipeBuilder.builder()
            .itemInputs(ModsItemlist.BotaniaManasteelIngot.get(1))
            .fluidOutputs(GTNLMaterials.FluidMana.getFluidOrGas(3300))
            .duration(20)
            .eut(2048)
            .addTo(NSAR);

        RecipeBuilder.builder()
            .itemInputs(ModsItemlist.BotaniaManaPearl.get(1))
            .fluidOutputs(GTNLMaterials.FluidMana.getFluidOrGas(6500))
            .duration(20)
            .eut(2048)
            .addTo(NSAR);

        RecipeBuilder.builder()
            .itemInputs(ModsItemlist.BotaniaManaDiamond.get(1))
            .fluidOutputs(GTNLMaterials.FluidMana.getFluidOrGas(44000))
            .duration(20)
            .eut(2048)
            .addTo(NSAR);

        RecipeBuilder.builder()
            .itemInputs(ModsItemlist.BotaniaEverlastingManaPool.get(0))
            .fluidOutputs(GTNLMaterials.FluidMana.getFluidOrGas(2147483647))
            .duration(20)
            .eut(7864320)
            .addTo(NSAR);
    }
}
