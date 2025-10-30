package com.science.gtnl.utils.recipes;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.recipe.metadata.IRecipeMetadataStorage;
import gregtech.api.recipe.metadata.RecipeMetadataStorage;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.extensions.ArrayExt;

public class RecipeBuilder {

    public static RecipeBuilder builder() {
        return new RecipeBuilder();
    }

    public ItemStack[] inputsBasic = new ItemStack[0];
    public Object[] inputsOreDict;
    public Object special;
    public ItemStack[][] alts;
    public ItemStack[] inputItems = new ItemStack[0];
    public ItemStack[] outputItems = new ItemStack[0];
    public FluidStack[] inputFluids = new FluidStack[0];
    public FluidStack[] outputFluids = new FluidStack[0];
    public int[] outputChance;
    public int eut = 0;
    public int duration = 0;
    public int specialValue = 0;
    @Nullable
    public IRecipeMetadataStorage metadataStorage;
    public boolean skip = false;
    public String[] neiDesc;

    public RecipeBuilder() {}

    public RecipeBuilder itemInputs(ItemStack... inputItems) {
        if (inputItems != null && inputItems.length > 0) {
            this.inputItems = inputItems;
        }
        return this;
    }

    public RecipeBuilder itemOutputs(ItemStack... outputItems) {
        if (outputItems != null && outputItems.length > 0) {
            this.outputItems = outputItems;
        }
        return this;
    }

    public RecipeBuilder fluidInputs(FluidStack... inputFluids) {
        if (inputFluids != null && inputFluids.length > 0) {
            this.inputFluids = inputFluids;
        }
        return this;
    }

    public RecipeBuilder fluidOutputs(FluidStack... outputFluids) {
        if (outputFluids != null && outputFluids.length > 0) {
            this.outputFluids = outputFluids;
        }
        return this;
    }

    public RecipeBuilder outputChances(int... outputChance) {
        this.outputChance = outputChance;
        return this;
    }

    public RecipeBuilder special(Object special) {
        this.special = special;
        return this;
    }

    public RecipeBuilder eut(int eut) {
        this.eut = eut;
        return this;
    }

    public RecipeBuilder eut(long eut) {
        this.eut = (int) eut;
        return this;
    }

    public RecipeBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public RecipeBuilder specialValue(int specialValue) {
        this.specialValue = specialValue;
        return this;
    }

    public RecipeBuilder setNEIDesc(String... neiDesc) {
        this.neiDesc = neiDesc;
        return this;
    }

    public RecipeBuilder itemInputsAllowNulls(ItemStack... inputs) {
        if (skip) return this;
        inputsBasic = fix(inputs, false);
        inputsOreDict = null;
        alts = null;
        inputItems = inputsBasic;
        return this;
    }

    public static ItemStack[] fix(ItemStack[] inputs, boolean aUnsafe) {
        return GTOreDictUnificator
            .setStackArray(true, aUnsafe, ArrayExt.withoutTrailingNulls(inputs, ItemStack[]::new));
    }

    public RecipeBuilder addTo(RecipeMap<?> recipeMap) {

        GTRecipe tempRecipe = new GTRecipe(
            false,
            inputItems,
            outputItems,
            special,
            outputChance,
            inputFluids,
            outputFluids,
            duration,
            eut,
            specialValue);

        tempRecipe.mInputs = inputItems.clone();
        tempRecipe.mOutputs = outputItems.clone();
        tempRecipe.setNeiDesc(neiDesc);

        recipeMap.add(tempRecipe);
        return this;
    }

    public <T> RecipeBuilder metadata(RecipeMetadataKey<T> key, T value) {
        if (skip) return this;
        if (metadataStorage == null) {
            metadataStorage = new RecipeMetadataStorage();
        }
        metadataStorage.store(key, value);
        return this;
    }
}
