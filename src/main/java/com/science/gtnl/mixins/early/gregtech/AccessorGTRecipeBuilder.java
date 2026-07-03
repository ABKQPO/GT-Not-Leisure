package com.science.gtnl.mixins.early.gregtech;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import gregtech.api.recipe.RecipeCategory;
import gregtech.api.recipe.metadata.IRecipeMetadataStorage;
import gregtech.api.util.GTRecipeBuilder;

@Mixin(value = GTRecipeBuilder.class, remap = false)
public interface AccessorGTRecipeBuilder {

    @Accessor("inputsBasic")
    ItemStack[] getInputsBasic();

    @Accessor("inputsBasic")
    void setInputsBasic(ItemStack[] value);

    @Accessor("inputsOreDict")
    Object[] getInputsOreDict();

    @Accessor("inputsOreDict")
    void setInputsOreDict(Object[] value);

    @Accessor("outputs")
    ItemStack[] getOutputs();

    @Accessor("outputs")
    void setOutputs(ItemStack[] value);

    @Accessor("alts")
    ItemStack[][] getAlts();

    @Accessor("alts")
    void setAlts(ItemStack[][] value);

    @Accessor("fluidInputs")
    FluidStack[] getFluidInputs();

    @Accessor("fluidInputs")
    void setFluidInputs(FluidStack[] value);

    @Accessor("fluidOutputs")
    FluidStack[] getFluidOutputs();

    @Accessor("fluidOutputs")
    void setFluidOutputs(FluidStack[] value);

    @Accessor("outputChances")
    int[] getChances();

    @Accessor("outputChances")
    void setChances(int[] value);

    @Accessor("special")
    Object getSpecial();

    @Accessor("special")
    void setSpecial(Object value);

    @Accessor("duration")
    int getDuration();

    @Accessor("duration")
    void setDuration(int value);

    @Accessor("eut")
    int getEut();

    @Accessor("eut")
    void setEut(int value);

    @Accessor("specialValue")
    int getSpecialValue();

    @Accessor("specialValue")
    void setSpecialValue(int value);

    @Accessor("enabled")
    boolean isEnabled();

    @Accessor("enabled")
    void setEnabled(boolean value);

    @Accessor("hidden")
    boolean isHidden();

    @Accessor("hidden")
    void setHidden(boolean value);

    @Accessor("fakeRecipe")
    boolean isFakeRecipe();

    @Accessor("fakeRecipe")
    void setFakeRecipe(boolean value);

    @Accessor("mCanBeBuffered")
    boolean canBeBuffered();

    @Accessor("mCanBeBuffered")
    void setCanBeBuffered(boolean value);

    @Accessor("mNeedsEmptyOutput")
    boolean needsEmptyOutput();

    @Accessor("mNeedsEmptyOutput")
    void setNeedsEmptyOutput(boolean value);

    @Accessor("nbtSensitive")
    boolean isNbtSensitive();

    @Accessor("nbtSensitive")
    void setNbtSensitive(boolean value);

    @Accessor("neiDesc")
    String[] getNeiDesc();

    @Accessor("neiDesc")
    void setNeiDesc(String[] value);

    @Accessor("recipeCategory")
    RecipeCategory getRecipeCategory();

    @Accessor("recipeCategory")
    void setRecipeCategory(RecipeCategory value);

    @Accessor("metadataStorage")
    IRecipeMetadataStorage getMetadataStorage();

    @Accessor("metadataStorage")
    void setMetadataStorage(IRecipeMetadataStorage value);

    @Accessor("checkForCollision")
    boolean getCheckForCollision();

    @Accessor("checkForCollision")
    void setCheckForCollision(boolean value);

    @Accessor("skip")
    boolean isSkip();

    @Accessor("skip")
    void setSkip(boolean value);

    @Accessor("valid")
    boolean isValid();

    @Accessor("valid")
    void setValid(boolean value);
}
