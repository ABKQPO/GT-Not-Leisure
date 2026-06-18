package com.science.gtnl.common.recipe.gtnl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Mods;
import gregtech.api.objects.OreDictItemStack;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeManaInfusion;

public class ManaInfusionRecipes implements IRecipePool {

    public RecipeMap<?> MIR = GTNLRecipeMaps.ManaInfusionRecipes;

    @Override
    public void loadRecipes() {
        for (RecipeManaInfusion recipe : BotaniaAPI.manaInfusionRecipes) {

            boolean isAlchemy = recipe.isAlchemy();
            boolean isConjuration = recipe.isConjuration();

            Object input = recipe.getInput();
            ItemStack output = recipe.getOutput();
            FluidStack mana = GTNLMaterials.FluidMana.getFluidOrGas(recipe.getManaToConsume());

            boolean hasOreDict = false;

            List<Object> finalInputs = new ArrayList<>(2);

            if (isAlchemy) {
                finalInputs.add(GTModHandler.getModItem(Mods.Botania.ID, "alchemyCatalyst", 0));
            } else if (isConjuration) {
                finalInputs.add(GTModHandler.getModItem(Mods.Botania.ID, "conjurationCatalyst", 0));
            } else {
                finalInputs.add(GTUtility.getIntegratedCircuit(1));
            }

            if (input instanceof ItemStack itemStack) {
                ItemStack inputCopy = itemStack.copy();

                if (inputCopy.getItemDamage() == Short.MAX_VALUE) {
                    inputCopy.setItemDamage(0);
                }

                finalInputs.add(inputCopy);
            } else if (input instanceof String string) {
                hasOreDict = true;
                finalInputs.add(new OreDictItemStack(string, 1));
            }

            RecipeBuilder builder = RecipeBuilder.builder()
                .itemOutputs(output)
                .fluidInputs(mana)
                .duration(20)
                .eut(2048);

            if (hasOreDict) {
                builder.itemInputs(finalInputs.toArray(new Object[0]));
            } else {
                builder.itemInputs(finalInputs.toArray(new ItemStack[0]));
            }
            builder.addTo(MIR);
        }

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.Botania.ID, "terraPlate", 0),
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 1, 1),
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 1, 2))
            .itemOutputs(GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 1, 4))
            .fluidInputs(GTNLMaterials.FluidMana.getFluidOrGas(500000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);
    }
}
