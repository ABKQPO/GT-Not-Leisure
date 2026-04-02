package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.enums.Mods.Botania;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;

public class FluidCannerRecipes implements IRecipePool {

    public RecipeMap<?> FCR = RecipeMaps.fluidCannerRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.glass_bottle, 1))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 15))
            .fluidInputs(GTNLMaterials.EnderAir.getFluidOrGas(599))
            .duration(1)
            .eut(8)
            .addTo(FCR);
    }
}
