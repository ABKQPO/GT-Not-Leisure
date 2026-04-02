package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class InfernalCokeRecipes implements IRecipePool {

    public RecipeMap<?> ICR = GTNLRecipeMaps.InfernalCockRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.cactus, 1))
            .itemOutputs(GregtechItemList.CactusCharcoal.get(1))
            .fluidOutputs(Materials.Creosote.getFluid(60))
            .duration(20 * SECONDS)
            .eut(0)
            .addTo(ICR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.CactusCharcoal.get(1))
            .itemOutputs(GregtechItemList.CactusCoke.get(1))
            .fluidOutputs(Materials.Creosote.getFluid(60))
            .duration(20 * SECONDS)
            .eut(0)
            .addTo(ICR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.reeds, 1))
            .itemOutputs(GregtechItemList.SugarCharcoal.get(1))
            .fluidOutputs(Materials.Creosote.getFluid(60))
            .duration(20 * SECONDS)
            .eut(0)
            .addTo(ICR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.SugarCharcoal.get(1))
            .itemOutputs(GregtechItemList.SugarCoke.get(1))
            .fluidOutputs(Materials.Creosote.getFluid(60))
            .duration(20 * SECONDS)
            .eut(0)
            .addTo(ICR);

        ArrayList<ItemStack> aLogData = OreDictionary.getOres("logWood");
        for (ItemStack stack : aLogData) {
            RecipeBuilder.builder()
                .itemInputs(GTUtility.copyAmount(1, stack))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.gem, Materials.Charcoal, 1))
                .fluidOutputs(Materials.Creosote.getFluid(200))
                .duration(20 * SECONDS)
                .eut(0)
                .addTo(ICR);
        }

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.coal, 1))
            .itemOutputs(GTOreDictUnificator.get("fuelCoke", 1))
            .fluidOutputs(Materials.Creosote.getFluid(60))
            .duration(20 * SECONDS)
            .eut(0)
            .addTo(ICR);

    }
}
