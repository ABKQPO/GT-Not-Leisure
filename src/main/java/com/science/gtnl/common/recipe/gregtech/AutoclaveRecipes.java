package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.enums.Mods.*;
import static gregtech.api.util.GTModHandler.getModItem;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.enums.ItemList;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;

public class AutoclaveRecipes implements IRecipePool {

    public RecipeMap<?> AR = RecipeMaps.autoclaveRecipes;
    static ItemStack missing = new ItemStack(Blocks.fire);
    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 16))
            .fluidInputs(MaterialPool.Polyetheretherketone.getMolten(9))
            .itemOutputs(GTModHandler.getModItem(IndustrialCraft2.ID, "itemPartCarbonFibre", 64))
            .outputChances(10000)
            .duration(60)
            .eut(TierEU.RECIPE_IV)
            .addTo(AR);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Hot_Netherite_Scrap.get(2))
            .fluidInputs(Materials.RichNetherWaste.getFluid(2_000))
            .itemOutputs(
                ItemList.Netherite_Scrap_Seed.get(1),
                getModItem(EtFuturumRequiem.ID, "netherite_scrap", 2, missing))
            .duration(1200)
            .eut(TierEU.RECIPE_IV)
            .addTo(AR);
    }
}
