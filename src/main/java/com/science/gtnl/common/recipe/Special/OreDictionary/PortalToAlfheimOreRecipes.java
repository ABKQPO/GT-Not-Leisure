package com.science.gtnl.common.recipe.Special.OreDictionary;

import static com.science.gtnl.loader.IScriptLoader.missing;
import static gregtech.api.enums.Mods.Botania;

import net.minecraft.item.ItemStack;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.RecipeRegister;

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

    final RecipeMap<?> PTAR = RecipeRegister.PortalToAlfheimRecipes;

    @Override
    public void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName,
        ItemStack aStack) {
        if (aOreDictName.equals("blockGlassEV")) {
            RecipeBuilder.builder()
                .itemInputs(GTUtility.copyAmount(1, aStack))
                .itemOutputs(GTModHandler.getModItem(Botania.ID, "elfGlass", 1, 0, missing))
                .duration(20)
                .eut(2048)
                .addTo(PTAR);
        }
    }

    public static void addManaInfusionOreRecipes(ItemStack aStack) {

        final RecipeMap<?> MIR = RecipeRegister.ManaInfusionRecipes;

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTUtility.copyAmount(1, aStack))
            .itemOutputs(GTModHandler.getModItem("Botania", "stone", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);
    }
}
