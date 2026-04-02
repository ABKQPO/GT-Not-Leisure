package com.science.gtnl.common.recipe.gtnl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Mods;
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
            List<ItemStack> inputs = new ArrayList<>();
            ItemStack output = recipe.getOutput();
            FluidStack mana = GTNLMaterials.FluidMana.getFluidOrGas(recipe.getManaToConsume());

            if (isAlchemy) {
                inputs.add(GTModHandler.getModItem(Mods.Botania.ID, "alchemyCatalyst", 0));
            } else if (isConjuration) {
                inputs.add(GTModHandler.getModItem(Mods.Botania.ID, "conjurationCatalyst", 0));
            } else {
                inputs.add(GTUtility.getIntegratedCircuit(1));
            }

            if (input instanceof ItemStack itemStack) {
                ItemStack inputCopy = itemStack.copy();
                if (inputCopy.getItemDamage() == Short.MAX_VALUE) {
                    inputCopy.setItemDamage(0);
                }
                inputs.add(inputCopy);
                RecipeBuilder.builder()
                    .itemInputs(inputs)
                    .itemOutputs(output)
                    .fluidInputs(mana)
                    .duration(20)
                    .eut(2048)
                    .addTo(MIR);
            } else if (input instanceof String string) {
                List<ItemStack> validStacks = OreDictionary.getOres(string);
                for (ItemStack ostack : validStacks) {
                    ItemStack cstack = ostack.copy();
                    if (cstack.getItemDamage() == Short.MAX_VALUE) {
                        cstack.setItemDamage(0);
                    }
                    List<ItemStack> inputsCopy = new ArrayList<>(inputs);
                    inputsCopy.add(cstack);
                    RecipeBuilder.builder()
                        .itemInputs(inputsCopy)
                        .itemOutputs(output)
                        .fluidInputs(mana)
                        .duration(20)
                        .eut(2048)
                        .addTo(MIR);
                }
            }
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
