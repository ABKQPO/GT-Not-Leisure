package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.enums.Mods.Botania;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;

public class NatureSpiritArrayRecipes implements IRecipePool {

    public RecipeMap<?> NSAR = RecipePool.NatureSpiritArrayRecipes;

    @Override
    public void loadRecipes() {
        ItemStack asgardandelion = ItemUtils.getSpecialFlower("asgardandelion");

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, asgardandelion))
            .fluidOutputs(MaterialPool.FluidMana.getFluidOrGas(2000000))
            .duration(20)
            .eut(491520)
            .addTo(NSAR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem("Botania", "manaResource", 1, 0))
            .fluidOutputs(MaterialPool.FluidMana.getFluidOrGas(3300))
            .duration(20)
            .eut(2048)
            .addTo(NSAR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem("Botania", "manaResource", 1, 1))
            .fluidOutputs(MaterialPool.FluidMana.getFluidOrGas(6500))
            .duration(20)
            .eut(2048)
            .addTo(NSAR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem("Botania", "manaResource", 1, 2))
            .fluidOutputs(MaterialPool.FluidMana.getFluidOrGas(44000))
            .duration(20)
            .eut(2048)
            .addTo(NSAR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "pool", 1, 1)))
            .fluidOutputs(MaterialPool.FluidMana.getFluidOrGas(2147483647))
            .duration(20)
            .eut(7864320)
            .addTo(NSAR);
    }
}
