package com.science.gtnl.mixins.early.gregtech;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import gregtech.api.interfaces.tileentity.IRecipeLockable;
import gregtech.api.interfaces.tileentity.IVoidable;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipe;

@Mixin(value = ProcessingLogic.class, remap = false)
public interface AccessorProcessingLogic {

    @Accessor("machine")
    IVoidable getMachine();

    @Accessor("machine")
    void setMachine(IVoidable value);

    @Accessor("recipeLockableMachine")
    IRecipeLockable getRecipeLockableMachine();

    @Accessor("recipeLockableMachine")
    void setRecipeLockableMachine(IRecipeLockable value);

    @Accessor("isRecipeLocked")
    boolean getIsRecipeLocked();

    @Accessor("isRecipeLocked")
    void setIsRecipeLocked(boolean value);

    @Accessor("inputItems")
    ItemStack[] getInputItems();

    @Accessor("inputItems")
    void setInputItems(ItemStack[] value);

    @Accessor("inputFluids")
    FluidStack[] getInputFluids();

    @Accessor("inputFluids")
    void setInputFluids(FluidStack[] value);

    @Accessor("specialSlotItem")
    ItemStack getSpecialSlotItem();

    @Accessor("specialSlotItem")
    void setSpecialSlotItem(ItemStack value);

    @Accessor("maxParallel")
    int getMaxParallel();

    @Accessor("maxParallel")
    void setMaxParallel(int value);

    @Accessor("maxParallelSupplier")
    Supplier<Integer> getMaxParallelSupplier();

    @Accessor("maxParallelSupplier")
    void setMaxParallelSupplier(Supplier<Integer> value);

    @Accessor("batchSize")
    int getBatchSize();

    @Accessor("batchSize")
    void setBatchSize(int value);

    @Accessor("recipeMapSupplier")
    Supplier<RecipeMap<?>> getRecipeMapSupplier();

    @Accessor("recipeMapSupplier")
    void setRecipeMapSupplier(Supplier<RecipeMap<?>> value);

    @Accessor("euModifier")
    double getEuModifier();

    @Accessor("euModifier")
    void setEuModifier(double value);

    @Accessor("speedBoost")
    double getSpeedBoost();

    @Accessor("speedBoost")
    void setSpeedBoost(double value);

    @Accessor("availableVoltage")
    long getAvailableVoltage();

    @Accessor("availableVoltage")
    void setAvailableVoltage(long value);

    @Accessor("availableAmperage")
    long getAvailableAmperage();

    @Accessor("availableAmperage")
    void setAvailableAmperage(long value);

    @Accessor("maxTierSkips")
    int getMaxTierSkips();

    @Accessor("maxTierSkips")
    void setMaxTierSkips(int value);

    @Accessor("protectItems")
    boolean getProtectItems();

    @Accessor("protectItems")
    void setProtectItems(boolean value);

    @Accessor("protectFluids")
    boolean getProtectFluids();

    @Accessor("protectFluids")
    void setProtectFluids(boolean value);

    @Accessor("overClockTimeReduction")
    double getOverClockTimeReduction();

    @Accessor("overClockTimeReduction")
    void setOverClockTimeReduction(double value);

    @Accessor("overClockPowerIncrease")
    double getOverClockPowerIncrease();

    @Accessor("overClockPowerIncrease")
    void setOverClockPowerIncrease(double value);

    @Accessor("amperageOC")
    boolean getAmperageOC();

    @Accessor("amperageOC")
    void setAmperageOC(boolean value);

    @Accessor("recipeCaching")
    boolean getRecipeCaching();

    @Accessor("recipeCaching")
    void setRecipeCaching(boolean value);

    // Calculated results
    @Accessor("outputItems")
    ItemStack[] getOutputItems();

    @Accessor("outputItems")
    void setOutputItems(ItemStack[] value);

    @Accessor("outputFluids")
    FluidStack[] getOutputFluids();

    @Accessor("outputFluids")
    void setOutputFluids(FluidStack[] value);

    @Accessor("calculatedEut")
    long getCalculatedEut();

    @Accessor("calculatedEut")
    void setCalculatedEut(long value);

    @Accessor("duration")
    int getDuration();

    @Accessor("duration")
    void setDuration(int value);

    @Accessor("calculatedParallels")
    int getCalculatedParallels();

    @Accessor("calculatedParallels")
    void setCalculatedParallels(int value);

    // Cache
    @Accessor("lastRecipeMap")
    RecipeMap<?> getLastRecipeMap();

    @Accessor("lastRecipeMap")
    void setLastRecipeMap(RecipeMap<?> value);

    @Accessor("lastRecipe")
    GTRecipe getLastRecipe();

    @Accessor("lastRecipe")
    void setLastRecipe(GTRecipe value);
}
