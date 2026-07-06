package com.science.gtnl.mixins.early.gregtech;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import gregtech.api.recipe.RecipeCategory;
import gregtech.api.recipe.metadata.IRecipeMetadataStorage;
import gregtech.api.util.GTRecipe;

@Mixin(value = GTRecipe.class, remap = false)
public interface AccessorGTRecipe {

    @Invoker("<init>")
    static GTRecipe create(ItemStack[] mInputs, ItemStack[] mOutputs, FluidStack[] mFluidInputs,
        FluidStack[] mFluidOutputs, int[] mInputChances, int[] mOutputChances, int[] mFluidInputChances,
        int[] mFluidOutputChances, int mEUt, int mSpecialValue, boolean mEnabled, boolean mHidden, boolean mFakeRecipe,
        boolean mCanBeBuffered, boolean mNeedsEmptyOutput, boolean nbtSensitive, String[] neiDesc,
        @Nullable IRecipeMetadataStorage metadataStorage, RecipeCategory recipeCategory, Object mSpecialItems,
        int mDuration) {
        throw new AssertionError();
    }
}
