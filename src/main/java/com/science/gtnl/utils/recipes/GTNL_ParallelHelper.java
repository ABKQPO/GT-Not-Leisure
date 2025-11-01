package com.science.gtnl.utils.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.function.Function;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.ModList;

import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.tileentity.IRecipeLockable;
import gregtech.api.interfaces.tileentity.IVoidable;
import gregtech.api.objects.XSTR;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SingleRecipeCheck;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.ParallelHelper;
import gregtech.api.util.VoidProtectionHelper;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
public class GTNL_ParallelHelper extends ParallelHelper {

    public static final double MAX_BATCH_MODE_TICK_TIME = 128;
    /**
     * Machine used for calculation
     */
    public IVoidable machine;
    /**
     * Machine used for single recipe locking calculation
     */
    public IRecipeLockable singleRecipeMachine;
    /**
     * Is locked to a single recipe?
     */
    public boolean isRecipeLocked;
    /**
     * Recipe used when trying to calculate parallels
     */
    public GTRecipe recipe;
    /**
     * EUt available to the multiblock (This should be the total eut available)
     */
    public long availableEUt;
    /**
     * The current parallel possible for the multiblock
     */
    public int currentParallel = 0;
    /**
     * The maximum possible parallel possible for the multiblock
     */
    public int maxParallel = 1;
    /**
     * The Batch Modifier applied when batch mode is enabled. 1 does nothing. 2 doubles max possible
     * parallel, but also duration
     */
    public int batchModifier = 1;
    /**
     * The inputs of the multiblock for the current recipe check
     */
    public ItemStack[] itemInputs;
    /**
     * The outputs of the recipe with the applied parallel
     */
    public ItemStack[] itemOutputs;
    /**
     * The inputs of the multiblock for the current recipe check
     */
    public FluidStack[] fluidInputs;
    /**
     * The outputs of the recipe with the applied parallel
     */
    public FluidStack[] fluidOutputs;
    /**
     * Does the multi have void protection enabled for items
     */
    public boolean protectExcessItem;
    /**
     * Does the multi have void protection enabled for fluids
     */
    public boolean protectExcessFluid;
    /**
     * Should the Parallel Helper automatically consume for the multi
     */
    public boolean consume;
    /**
     * Is batch mode turned on?
     */
    public boolean batchMode;
    /**
     * Should the Parallel Helper automatically calculate the outputs of the recipe with current parallel?
     */
    public boolean calculateOutputs;
    /**
     * Has the Parallel Helper been built?
     */
    public boolean built;
    /**
     * What is the duration multiplier with batch mode enabled
     */
    public double durationMultiplier;
    /**
     * Modifier which is applied on the recipe eut. Useful for GT++ machines
     */
    public double eutModifier = 1;
    /**
     * Multiplier that is applied on the output chances
     */
    public double chanceMultiplier = 1;
    /**
     * Method for calculating max parallel from given inputs.
     */
    public ParallelHelper.MaxParallelCalculator maxParallelCalculator = GTRecipe::maxParallelCalculatedByInputs;
    /**
     * Method for consuming inputs after determining how many parallels it can execute.
     */
    public ParallelHelper.InputConsumer inputConsumer = GTRecipe::consumeInput;

    /**
     * Calculator to use for overclocking
     */
    public GTNL_OverclockCalculator calculator;
    @Nonnull
    public CheckRecipeResult result = CheckRecipeResultRegistry.NONE;

    public Function<Integer, ItemStack[]> customItemOutputCalculation;

    public Function<Integer, FluidStack[]> customFluidOutputCalculation;

    public GTNL_ParallelHelper() {}

    /**
     * Sets machine, with current configuration for void protection mode.
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper setMachine(IVoidable machine) {
        return setMachine(machine, machine.protectsExcessItem(), machine.protectsExcessFluid());
    }

    /**
     * Sets machine, with void protection mode forcibly.
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper setMachine(IVoidable machine, boolean protectExcessItem, boolean protectExcessFluid) {
        this.protectExcessItem = protectExcessItem;
        this.protectExcessFluid = protectExcessFluid;
        this.machine = machine;
        return this;
    }

    /**
     * Sets the recipe, which will be used for the parallel calculation
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper setRecipe(@Nonnull GTRecipe aRecipe) {
        recipe = Objects.requireNonNull(aRecipe);
        return this;
    }

    @Nonnull
    @Override
    public GTNL_ParallelHelper setRecipeLocked(IRecipeLockable singleRecipeMachine, boolean isRecipeLocked) {
        this.singleRecipeMachine = singleRecipeMachine;
        this.isRecipeLocked = isRecipeLocked;
        return this;
    }

    /**
     * Sets the items available for the recipe check
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper setItemInputs(ItemStack... aItemInputs) {
        this.itemInputs = aItemInputs;
        return this;
    }

    /**
     * Sets the fluid inputs available for the recipe check
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper setFluidInputs(FluidStack... aFluidInputs) {
        this.fluidInputs = aFluidInputs;
        return this;
    }

    /**
     * Sets the available eut when trying for more parallels
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper setAvailableEUt(long aAvailableEUt) {
        this.availableEUt = aAvailableEUt;
        return this;
    }

    /**
     * Sets the modifier for recipe eut. 1 does nothing 0.9 is 10% less. 1.1 is 10% more
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper setEUtModifier(double aEUtModifier) {
        this.eutModifier = aEUtModifier;
        return this;
    }

    /**
     * Sets the multiplier that is applied on output chances. 1 does nothing. 0.9 is 10% less. 1.1 is 10% more.
     * Only useful for item outputs for sure.
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper setChanceMultiplier(double chanceMultiplier) {
        this.chanceMultiplier = chanceMultiplier;
        return this;
    }

    @Nonnull
    public GTNL_ParallelHelper setCalculator(GTNL_OverclockCalculator calculator) {
        this.calculator = calculator;
        return this;
    }

    /**
     * Set if we should consume inputs or not when trying for parallels
     *
     * @param consume Should we consume inputs
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper setConsumption(boolean consume) {
        this.consume = consume;
        return this;
    }

    /**
     * Sets the MaxParallel a multi can handle
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper setMaxParallel(int maxParallel) {
        this.maxParallel = maxParallel;
        return this;
    }

    /**
     * Enables Batch mode. Can do up to an additional processed recipes of mCurrentParallel * mBatchModifier A batch
     * modifier of 1 does nothing
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper enableBatchMode(int batchModifier) {
        this.batchMode = batchModifier > 1;
        this.batchModifier = batchModifier;
        return this;
    }

    /**
     * Sets if we should calculate outputs with the parallels we found or not
     *
     * @param calculateOutputs Should we calculate outputs with the helper or not
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper setOutputCalculation(boolean calculateOutputs) {
        this.calculateOutputs = calculateOutputs;
        return this;
    }

    /**
     * Set a custom way to calculate item outputs. You are given the amount of parallels and must return an ItemStack
     * array
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper setCustomItemOutputCalculation(Function<Integer, ItemStack[]> custom) {
        customItemOutputCalculation = custom;
        return this;
    }

    /**
     * Set a custom way to calculate item outputs. You are given the amount of parallels and must return a FluidStack
     * array
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper setCustomFluidOutputCalculation(Function<Integer, FluidStack[]> custom) {
        customFluidOutputCalculation = custom;
        return this;
    }

    /**
     * Sets method for calculating max parallel from given inputs.
     */
    public GTNL_ParallelHelper setMaxParallelCalculator(ParallelHelper.MaxParallelCalculator maxParallelCalculator) {
        this.maxParallelCalculator = maxParallelCalculator;
        return this;
    }

    /**
     * Sets method for consuming inputs after determining how many parallels it can execute.
     */
    public GTNL_ParallelHelper setInputConsumer(ParallelHelper.InputConsumer inputConsumer) {
        this.inputConsumer = inputConsumer;
        return this;
    }

    /**
     * Finishes the GT_ParallelHelper. Anything changed after this will not effect anything
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper build() {
        if (built) {
            throw new IllegalStateException("Tried to build twice");
        }
        if (recipe == null) {
            throw new IllegalStateException("Recipe is not set");
        }
        built = true;
        determineParallel();
        return this;
    }

    /**
     * @return The current parallels possible by the multiblock
     */
    @Override
    public int getCurrentParallel() {
        if (!built) {
            throw new IllegalStateException("Tried to get parallels before building");
        }
        return currentParallel;
    }

    /**
     * @return The duration multiplier if batch mode was enabled for the multiblock
     */
    @Override
    public double getDurationMultiplierDouble() {
        if (!built) {
            throw new IllegalStateException("Tried to get duration multiplier before building");
        }
        if (batchMode && durationMultiplier > 0) {
            return durationMultiplier;
        }
        return 1;
    }

    /**
     * @return The ItemOutputs from the recipe
     */
    @Nonnull
    @Override
    public ItemStack[] getItemOutputs() {
        if (!built || !calculateOutputs) {
            throw new IllegalStateException(
                "Tried to get item outputs before building or without enabling calculation of outputs");
        }
        return itemOutputs;
    }

    /**
     * @return The FluidOutputs from the recipe
     */
    @Nonnull
    @Override
    public FluidStack[] getFluidOutputs() {
        if (!built || !calculateOutputs) {
            throw new IllegalStateException(
                "Tried to get fluid outputs before building or without enabling calculation of outputs");
        }
        return fluidOutputs;
    }

    /**
     * @return The result of why a recipe could've failed or succeeded
     */
    @Nonnull
    @Override
    public CheckRecipeResult getResult() {
        if (!built) {
            throw new IllegalStateException("Tried to get recipe result before building");
        }
        return result;
    }

    /**
     * Called by build(). Determines the parallels and everything else that needs to be done at build time
     */
    @Override
    public void determineParallel() {
        if (!ModList.Overpowered.isModLoaded() && MainConfig.enableRecipeOutputChance) {
            // Compute optional bonus based on machine and current EU/t
            OptionalDouble bonusOptional = ChanceBonusManager
                .getChanceBonusOptional(machine, GTUtility.getTier(recipe.mEUt), chanceMultiplier, recipe);
            // If present, apply bonus by copying the recipe with increased chances
            if (bonusOptional.isPresent()) {
                recipe = ChanceBonusManager.copyAndBonusChance(recipe, bonusOptional.getAsDouble());
            }
        }

        if (maxParallel <= 0) {
            return;
        }
        if (itemInputs == null) {
            itemInputs = GTValues.emptyItemStackArray;
        }
        if (fluidInputs == null) {
            fluidInputs = GTValues.emptyFluidStackArray;
        }

        if (!consume) {
            copyInputs();
        }

        if (calculator == null) {
            calculator = new GTNL_OverclockCalculator().setEUt(availableEUt)
                .setRecipeEUt(recipe.mEUt)
                .setDuration(recipe.mDuration)
                .setEUtDiscount(eutModifier);
        }

        double heatDiscountMultiplier = calculator.calculateHeatDiscountMultiplier();

        final int tRecipeEUt = (int) Math.ceil(recipe.mEUt * eutModifier * heatDiscountMultiplier);
        if (availableEUt < tRecipeEUt) {
            result = CheckRecipeResultRegistry.insufficientPower(tRecipeEUt);
            return;
        }
        if (!calculator.getAllowedTierSkip()) {
            result = CheckRecipeResultRegistry.insufficientVoltage(tRecipeEUt);
            return;
        }

        // Save the original max parallel before calculating our overclocking under 1 tick
        int originalMaxParallel = maxParallel;
        calculator.setParallel(originalMaxParallel);

        // If the machine has custom supplier, use old method for giving parallels for overclocking too much, otherwise
        // use multiplier
        if (calculator.hasDurationUnderOneTickSupplier()) {
            if (calculator.getDurationUnderOneTickSupplier() < 1) {
                maxParallel = GTUtility.safeInt((long) (maxParallel / calculator.getDurationUnderOneTickSupplier()), 0);
            }
        } else {
            maxParallel = GTUtility.safeInt((long) (maxParallel * calculator.calculateMultiplierUnderOneTick()), 0);
        }
        int maxParallelBeforeBatchMode = maxParallel;
        if (batchMode) {
            maxParallel = GTUtility.safeInt((long) maxParallel * batchModifier, 0);
        }

        final ItemStack[] truncatedItemOutputs = recipe.mOutputs != null
            ? Arrays.copyOfRange(recipe.mOutputs, 0, Math.min(machine.getItemOutputLimit(), recipe.mOutputs.length))
            : GTValues.emptyItemStackArray;
        final FluidStack[] truncatedFluidOutputs = recipe.mFluidOutputs != null ? Arrays
            .copyOfRange(recipe.mFluidOutputs, 0, Math.min(machine.getFluidOutputLimit(), recipe.mFluidOutputs.length))
            : GTValues.emptyFluidStackArray;

        SingleRecipeCheck recipeCheck = null;
        SingleRecipeCheck.Builder tSingleRecipeCheckBuilder = null;
        if (isRecipeLocked && singleRecipeMachine != null) {
            recipeCheck = singleRecipeMachine.getSingleRecipeCheck();
            if (recipeCheck == null) {
                // Machine is configured to lock to a single recipe, but haven't built the recipe checker yet.
                // Build the checker on next successful recipe.
                RecipeMap<?> recipeMap = singleRecipeMachine.getRecipeMap();
                if (recipeMap != null) {
                    tSingleRecipeCheckBuilder = SingleRecipeCheck.builder(recipeMap)
                        .setBefore(itemInputs, fluidInputs);
                }
            }
        }

        // Let's look at how many parallels we can get with void protection
        if (protectExcessItem || protectExcessFluid) {
            if (machine == null) {
                throw new IllegalStateException("Tried to calculate void protection, but machine is not set");
            }
            VoidProtectionHelper voidProtectionHelper = new VoidProtectionHelper();
            voidProtectionHelper.setMachine(machine)
                .setItemOutputs(truncatedItemOutputs)
                .setFluidOutputs(truncatedFluidOutputs)
                .setChangeGetter(recipe::getOutputChance)
                .setChanceMultiplier(chanceMultiplier)
                .setMaxParallel(maxParallel)
                .build();
            maxParallel = Math.min(voidProtectionHelper.getMaxParallel(), maxParallel);
            if (voidProtectionHelper.isItemFull()) {
                result = CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
                return;
            }
            if (voidProtectionHelper.isFluidFull()) {
                result = CheckRecipeResultRegistry.FLUID_OUTPUT_FULL;
                return;
            }
        }

        maxParallelBeforeBatchMode = Math.min(maxParallel, maxParallelBeforeBatchMode);

        // determine normal parallel
        int actualMaxParallel = tRecipeEUt > 0 ? (int) Math.min(maxParallelBeforeBatchMode, availableEUt / tRecipeEUt)
            : maxParallelBeforeBatchMode;
        if (recipeCheck != null) {
            currentParallel = recipeCheck.checkRecipeInputs(true, actualMaxParallel, itemInputs, fluidInputs);
        } else {
            currentParallel = (int) maxParallelCalculator.calculate(recipe, actualMaxParallel, fluidInputs, itemInputs);
            if (currentParallel > 0) {
                if (tSingleRecipeCheckBuilder != null) {
                    // If recipe checker is not built yet, build and set it
                    inputConsumer.consume(recipe, 1, fluidInputs, itemInputs);
                    SingleRecipeCheck builtCheck = tSingleRecipeCheckBuilder.setAfter(itemInputs, fluidInputs)
                        .setRecipe(recipe)
                        .build();
                    singleRecipeMachine.setSingleRecipeCheck(builtCheck);
                    inputConsumer.consume(recipe, currentParallel - 1, fluidInputs, itemInputs);
                } else {
                    inputConsumer.consume(recipe, currentParallel, fluidInputs, itemInputs);
                }
            }
        }

        if (currentParallel <= 0) {
            result = CheckRecipeResultRegistry.INTERNAL_ERROR;
            return;
        }

        calculator.setCurrentParallel(currentParallel)
            .calculate();
        // If Batch Mode is enabled determine how many extra parallels we can get
        if (batchMode && currentParallel > 0 && calculator.getDuration() < MAX_BATCH_MODE_TICK_TIME) {
            int tExtraParallels;
            double batchMultiplierMax = MAX_BATCH_MODE_TICK_TIME / calculator.getDuration();
            final int maxExtraParallels = (int) Math.floor(
                Math.min(
                    currentParallel * Math.min(batchMultiplierMax - 1, batchModifier - 1),
                    maxParallel - currentParallel));
            if (recipeCheck != null) {
                tExtraParallels = recipeCheck.checkRecipeInputs(true, maxExtraParallels, itemInputs, fluidInputs);
            } else {
                tExtraParallels = (int) maxParallelCalculator
                    .calculate(recipe, maxExtraParallels, fluidInputs, itemInputs);
                inputConsumer.consume(recipe, tExtraParallels, fluidInputs, itemInputs);
            }
            durationMultiplier = 1.0f + (float) tExtraParallels / currentParallel;
            currentParallel += tExtraParallels;
        }

        // If we want to calculate outputs we do it here
        if (calculateOutputs && currentParallel > 0) {
            calculateItemOutputs(truncatedItemOutputs);
            calculateFluidOutputs(truncatedFluidOutputs);
        }
        result = CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public void copyInputs() {
        ItemStack[] itemInputsToUse;
        FluidStack[] fluidInputsToUse;
        itemInputsToUse = new ItemStack[itemInputs.length];
        for (int i = 0; i < itemInputs.length; i++) {
            itemInputsToUse[i] = itemInputs[i].copy();
        }
        fluidInputsToUse = new FluidStack[fluidInputs.length];
        for (int i = 0; i < fluidInputs.length; i++) {
            fluidInputsToUse[i] = fluidInputs[i].copy();
        }
        itemInputs = itemInputsToUse;
        fluidInputs = fluidInputsToUse;
    }

    @Override
    public void calculateItemOutputs(ItemStack[] truncatedItemOutputs) {
        if (customItemOutputCalculation != null) {
            itemOutputs = customItemOutputCalculation.apply(currentParallel);
            return;
        }
        if (truncatedItemOutputs.length == 0) return;
        ArrayList<ItemStack> itemOutputsList = new ArrayList<>();
        for (int i = 0; i < truncatedItemOutputs.length; i++) {
            if (recipe.getOutput(i) == null) continue;
            ItemStack origin = recipe.getOutput(i)
                .copy();
            final long itemStackSize = origin.stackSize;
            long chancedOutputMultiplier = calculateIntegralChancedOutputMultiplier(
                (int) (recipe.getOutputChance(i) * chanceMultiplier),
                currentParallel);
            long items = itemStackSize * chancedOutputMultiplier;
            addItemsLong(itemOutputsList, origin, items);
        }
        itemOutputs = itemOutputsList.toArray(new ItemStack[0]);
    }

    @Override
    public void calculateFluidOutputs(FluidStack[] truncatedFluidOutputs) {
        if (customFluidOutputCalculation != null) {
            fluidOutputs = customFluidOutputCalculation.apply(currentParallel);
            return;
        }
        if (truncatedFluidOutputs.length == 0) return;
        ArrayList<FluidStack> fluidOutputsList = new ArrayList<>();
        for (int i = 0; i < truncatedFluidOutputs.length; i++) {
            if (recipe.getFluidOutput(i) == null) continue;
            FluidStack origin = recipe.getFluidOutput(i)
                .copy();
            long fluids = (long) origin.amount * currentParallel;

            addFluidsLong(fluidOutputsList, origin, fluids);
        }
        fluidOutputs = fluidOutputsList.toArray(new FluidStack[0]);
    }

    public static double calculateChancedOutputMultiplier(int chanceInt, int parallel) {
        // Multiply the integer part of the chance directly with parallel
        double multiplier = Math.floorDiv(chanceInt, 10000) * parallel;
        int transformedChanceInt = chanceInt % 10000;
        if (transformedChanceInt == 0) return multiplier;
        // Calculation of the Decimal Part of chance
        double chance = transformedChanceInt / 10000.0;
        double mean = parallel * chance;
        double stdDev = Math.sqrt(parallel * chance * (1 - chance));
        // Check if everything within 3 standard deviations of mean is within the range
        // of possible values (0 ~ currentParallel)
        boolean isSuitableForFittingWithNormalDistribution = mean - 3 * stdDev >= 0 && mean + 3 * stdDev <= parallel;
        if (isSuitableForFittingWithNormalDistribution) {
            // Use Normal Distribution to fit Binomial Distribution
            double tMultiplier = stdDev * XSTR.XSTR_INSTANCE.nextGaussian() + mean;
            multiplier += Math.max(Math.min(tMultiplier, parallel), 0);
        } else {
            // Do Binomial Distribution by loop
            for (int roll = 0; roll < parallel; roll++) {
                if (transformedChanceInt > XSTR.XSTR_INSTANCE.nextInt(10000)) {
                    multiplier += 1;
                }
            }
        }
        return multiplier;
    }

    public static long calculateIntegralChancedOutputMultiplier(int chanceInt, int parallel) {
        double multiplier = calculateChancedOutputMultiplier(chanceInt, parallel);
        if (multiplier != Math.floor(multiplier)
            && multiplier - Math.floor(multiplier) > XSTR.XSTR_INSTANCE.nextDouble()) {
            return (long) multiplier + 1;
        }
        return (long) multiplier;
    }

    public static void addItemsLong(ArrayList<ItemStack> itemList, ItemStack origin, long amount) {
        if (amount > 0) {
            while (amount > Integer.MAX_VALUE) {
                itemList.add(GTUtility.copyAmountUnsafe(Integer.MAX_VALUE, origin));
                amount -= Integer.MAX_VALUE;
            }
            itemList.add(GTUtility.copyAmountUnsafe((int) amount, origin));
        }
    }

    public static void addFluidsLong(ArrayList<FluidStack> fluidList, FluidStack origin, long amount) {
        if (amount > 0) {
            while (amount > Integer.MAX_VALUE) {
                fluidList.add(GTUtility.copyAmount(Integer.MAX_VALUE, origin));
                amount -= Integer.MAX_VALUE;
            }
            fluidList.add(GTUtility.copyAmount((int) amount, origin));
        }
    }
}
