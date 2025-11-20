package com.science.gtnl.common.recipe.oreDictionary;

import static com.dreammaster.scripts.IScriptLoader.missing;
import static gregtech.api.enums.Mods.Botania;

import net.minecraft.item.ItemStack;

import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.common.material.RecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IOreRecipeRegistrator;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;

public class PortalToAlfheimOreRecipes implements IOreRecipeRegistrator {

    public PortalToAlfheimOreRecipes() {
        OrePrefixes.blockGlass.add(this);
    }

    public RecipeMap<?> PTAR = RecipePool.PortalToAlfheimRecipes;

    @Override
    public void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName,
        ItemStack aStack) {
        if (aOreDictName.equals("blockGlassEV")) {
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.copyAmount(1, aStack))
                .itemOutputs(GTModHandler.getModItem(Botania.ID, "elfGlass", 1, 0, missing))
                .duration(20)
                .eut(2048)
                .addTo(PTAR);
        }
    }

    public static void addManaInfusionOreRecipes(ItemStack aStack) {
        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 0), GTUtility.copyAmount(1, aStack))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "stone", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(RecipePool.ManaInfusionRecipes);
    }
}
