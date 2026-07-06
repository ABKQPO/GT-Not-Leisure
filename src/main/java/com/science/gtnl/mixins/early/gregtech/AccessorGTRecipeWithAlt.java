package com.science.gtnl.mixins.early.gregtech;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import gregtech.api.recipe.RecipeCategory;
import gregtech.api.recipe.metadata.IRecipeMetadataStorage;
import gregtech.api.util.GTRecipe;

@Mixin(value = GTRecipe.GTRecipe_WithAlt.class, remap = false)
public interface AccessorGTRecipeWithAlt {

    @Invoker("<init>")
    static GTRecipe.GTRecipe_WithAlt create(ItemStack[] mInputs, ItemStack[] mOutputs, FluidStack[] mFluidInputs,
        FluidStack[] mFluidOutputs, int[] mInputChances, int[] mOutputChances, int[] mFluidInputChances,
        int[] mFluidOutputChances, Object mSpecialItems, int mDuration, int mEUt, int mSpecialValue, boolean mEnabled,
        boolean mHidden, boolean mFakeRecipe, boolean mCanBeBuffered, boolean mNeedsEmptyOutput, boolean nbtSensitive,
        String[] neiDesc, @Nullable IRecipeMetadataStorage metadataStorage, RecipeCategory recipeCategory,
        ItemStack[][] mOreDictAlt, int[] mOreDictIds, FluidStack[][] mAltFluidInputs) {
        throw new AssertionError();
    }
}
