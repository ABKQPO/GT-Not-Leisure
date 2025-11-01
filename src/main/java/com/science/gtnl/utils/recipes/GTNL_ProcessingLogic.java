package com.science.gtnl.utils.recipes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.IConfigurationMaintenance;
import com.science.gtnl.mixins.late.Gregtech.AccessorProcessingLogic;

import gregtech.api.interfaces.tileentity.IRecipeLockable;
import gregtech.api.interfaces.tileentity.IVoidable;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchMaintenance;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.objects.GTDualInputPattern;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SingleRecipeCheck;
import gregtech.api.util.GTRecipe;
import gregtech.common.tileentities.machines.IDualInputInventoryWithPattern;

/**
 * Logic class to calculate result of recipe check from inputs, based on recipemap.
 */
@SuppressWarnings({ "unused", "UnusedReturnValue" })
public class GTNL_ProcessingLogic extends ProcessingLogic {

    // Traits
    public IVoidable machine;
    public IRecipeLockable recipeLockableMachine;
    public boolean isRecipeLocked;
    public ItemStack[] inputItems;
    public FluidStack[] inputFluids;
    public ItemStack specialSlotItem;
    public int maxParallel = 1;
    public Supplier<Integer> maxParallelSupplier;
    public int batchSize = 1;
    public Supplier<RecipeMap<?>> recipeMapSupplier;
    public double euModifier = 1.0;
    public double speedBoost = 1.0;
    public double extraSpeedBoost = 1.0;
    public long availableVoltage;
    public long availableAmperage;
    public int maxOverclocks = Integer.MAX_VALUE;
    public int maxTierSkips = Integer.MAX_VALUE;
    public boolean protectItems;
    public boolean protectFluids;
    public double overClockTimeReduction = 2.0;
    public double overClockPowerIncrease = 4.0;
    public boolean amperageOC = true;

    // Calculated results
    public ItemStack[] outputItems;
    public FluidStack[] outputFluids;
    public long calculatedEut;
    public int duration;
    public int calculatedParallels = 0;

    // Cache
    public RecipeMap<?> lastRecipeMap;
    public GTRecipe lastRecipe;

    /**
     * The {@link IDualInputInventoryWithPattern} that has any possible recipe found in the last call of
     * {@link #tryCachePossibleRecipesFromPattern(IDualInputInventoryWithPattern)}.
     */
    public IDualInputInventoryWithPattern activeDualInv;
    /**
     * The cache keyed by the {@link IDualInputInventoryWithPattern}, storing the possible recipes of the inv.
     * <p>
     * The entries can be removed by {@link #removeInventoryRecipeCache(IDualInputInventoryWithPattern)}, and it
     * requires the hatches to actively call it when the inventory is changed (like the pattern is changed).
     * <p>
     * It will also be fully cleared when the {@link #getCurrentRecipeMap()} is not same to the last.
     */
    public Map<IDualInputInventoryWithPattern, Set<GTRecipe>> dualInvWithPatternToRecipeCache = new HashMap<>();

    public GTNL_ProcessingLogic() {}

    public GTNL_ProcessingLogic(ProcessingLogic logic) {
        AccessorProcessingLogic processingLogic = (AccessorProcessingLogic) logic;

        this.machine = processingLogic.getMachine();
        this.recipeLockableMachine = processingLogic.getRecipeLockableMachine();
        this.isRecipeLocked = processingLogic.getIsRecipeLocked();
        this.inputItems = processingLogic.getInputItems();
        this.inputFluids = processingLogic.getInputFluids();
        this.specialSlotItem = processingLogic.getSpecialSlotItem();
        this.maxParallel = processingLogic.getMaxParallel();
        this.maxParallelSupplier = processingLogic.getMaxParallelSupplier();
        this.batchSize = processingLogic.getBatchSize();
        this.recipeMapSupplier = processingLogic.getRecipeMapSupplier();
        this.euModifier = processingLogic.getEuModifier();
        this.speedBoost = processingLogic.getSpeedBoost();
        this.availableVoltage = processingLogic.getAvailableVoltage();
        this.availableAmperage = processingLogic.getAvailableAmperage();
        this.maxTierSkips = processingLogic.getMaxTierSkips();
        this.protectItems = processingLogic.getProtectItems();
        this.protectFluids = processingLogic.getProtectFluids();
        this.overClockTimeReduction = processingLogic.getOverClockTimeReduction();
        this.overClockPowerIncrease = processingLogic.getOverClockPowerIncrease();
        this.amperageOC = processingLogic.getAmperageOC();

        this.outputItems = processingLogic.getOutputItems();
        this.outputFluids = processingLogic.getOutputFluids();
        this.calculatedEut = processingLogic.getCalculatedEut();
        this.duration = processingLogic.getDuration();
        this.calculatedParallels = processingLogic.getCalculatedParallels();

        this.lastRecipeMap = processingLogic.getLastRecipeMap();
        this.lastRecipe = processingLogic.getLastRecipe();
    }

    // region Setters

    /**
     * Sets machine used for void protection logic.
     */
    @Override
    public GTNL_ProcessingLogic setMachine(IVoidable machine) {
        this.machine = machine;
        return this;
    }

    /**
     * Enables single recipe locking mode.
     */
    @Override
    public GTNL_ProcessingLogic setRecipeLocking(IRecipeLockable recipeLockableMachine, boolean isRecipeLocked) {
        this.recipeLockableMachine = recipeLockableMachine;
        this.isRecipeLocked = isRecipeLocked;
        return this;
    }

    @Nonnull
    @Override
    public GTNL_ProcessingLogic setInputItems(ItemStack... itemInputs) {
        this.inputItems = itemInputs;
        return this;
    }

    @Nonnull
    @Override
    public GTNL_ProcessingLogic setInputItems(List<ItemStack> itemInputs) {
        this.inputItems = itemInputs.toArray(new ItemStack[0]);
        return this;
    }

    @Nonnull
    @Override
    public GTNL_ProcessingLogic setInputFluids(FluidStack... fluidInputs) {
        this.inputFluids = fluidInputs;
        return this;
    }

    @Nonnull
    @Override
    public GTNL_ProcessingLogic setInputFluids(List<FluidStack> fluidInputs) {
        this.inputFluids = fluidInputs.toArray(new FluidStack[0]);
        return this;
    }

    @Override
    public GTNL_ProcessingLogic setSpecialSlotItem(ItemStack specialSlotItem) {
        this.specialSlotItem = specialSlotItem;
        return this;
    }

    /**
     * Try to cache the possible recipes from the pattern.
     * <p>
     * If the inventory can be cached, and any possible recipe is found, {@link #activeDualInv the active inv} will be
     * set to the given inventory.
     *
     * @return {@code true} if the inv shouldn't be cached, or there is already a cached recipe sets, or the recipes
     *         are cached successfully. {@code false} if there is no recipe found.
     */
    @Override
    public boolean tryCachePossibleRecipesFromPattern(IDualInputInventoryWithPattern inv) {
        if (!inv.shouldBeCached()) {
            return true;
        }

        // check existing caches
        if (dualInvWithPatternToRecipeCache.containsKey(inv)) {
            activeDualInv = inv;
            return true;
        }

        // get recipes from the pattern
        GTDualInputPattern inputs = inv.getPatternInputs();
        setInputItems(inputs.inputItems);
        setInputFluids(inputs.inputFluid);
        Set<GTRecipe> recipes = findRecipeMatches(getCurrentRecipeMap()).collect(Collectors.toSet());

        // reset the status
        setInputItems();
        setInputFluids();

        if (!recipes.isEmpty()) {
            dualInvWithPatternToRecipeCache.put(inv, recipes);
            activeDualInv = inv;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void removeInventoryRecipeCache(IDualInputInventoryWithPattern inv) {
        dualInvWithPatternToRecipeCache.remove(inv);
    }

    /**
     * Sets max amount of parallel.
     */
    @Override
    public GTNL_ProcessingLogic setMaxParallel(int maxParallel) {
        this.maxParallel = maxParallel;
        return this;
    }

    /**
     * Sets method to get max amount of parallel.
     */
    @Override
    public GTNL_ProcessingLogic setMaxParallelSupplier(Supplier<Integer> supplier) {
        this.maxParallelSupplier = supplier;
        return this;
    }

    /**
     * Sets batch size for batch mode.
     */
    @Override
    public GTNL_ProcessingLogic setBatchSize(int size) {
        this.batchSize = size;
        return this;
    }

    @Override
    public GTNL_ProcessingLogic setRecipeMap(RecipeMap<?> recipeMap) {
        return setRecipeMapSupplier(() -> recipeMap);
    }

    @Override
    public GTNL_ProcessingLogic setRecipeMapSupplier(Supplier<RecipeMap<?>> supplier) {
        this.recipeMapSupplier = supplier;
        return this;
    }

    @Override
    public GTNL_ProcessingLogic setEuModifier(double modifier) {
        this.euModifier = modifier;
        return this;
    }

    @Override
    public GTNL_ProcessingLogic setSpeedBonus(double speedModifier) {
        this.speedBoost = speedModifier;
        return this;
    }

    public GTNL_ProcessingLogic setExtraSpeedBonus(double speedModifier) {
        this.extraSpeedBoost = speedModifier;
        return this;
    }

    /**
     * Sets voltage of the machine. It doesn't need to be actual voltage (excluding amperage) of the machine;
     * For example, most of the multiblock machines set maximum possible input power (including amperage) as voltage
     * and 1 as amperage. That way recipemap search will be executed with overclocked voltage.
     */
    @Override
    public GTNL_ProcessingLogic setAvailableVoltage(long voltage) {
        this.availableVoltage = voltage;
        return this;
    }

    /**
     * Sets amperage of the machine. This amperage doesn't involve in EU/t when searching recipemap.
     * Useful for preventing tier skip but still considering amperage for parallel.
     */
    @Override
    public GTNL_ProcessingLogic setAvailableAmperage(long amperage) {
        this.availableAmperage = amperage;
        return this;
    }

    public GTNL_ProcessingLogic setMaxOverclocks(int maxOverclocks) {
        this.maxOverclocks = maxOverclocks;
        return this;
    }

    public GTNL_ProcessingLogic setUnlimitedMaxOverclocks() {
        this.maxOverclocks = Integer.MAX_VALUE;
        return this;
    }

    /**
     * Sets the max amount of tier skips, which is how many voltage tiers above the input voltage
     * a recipe is valid. For unlimited tier skips, use {@link #setUnlimitedTierSkips()}
     */
    @Override
    public GTNL_ProcessingLogic setMaxTierSkips(int tierSkips) {
        this.maxTierSkips = tierSkips;
        return this;
    }

    @Override
    public GTNL_ProcessingLogic setUnlimitedTierSkips() {
        this.maxTierSkips = Integer.MAX_VALUE;
        return this;
    }

    @Override
    public GTNL_ProcessingLogic setVoidProtection(boolean protectItems, boolean protectFluids) {
        this.protectItems = protectItems;
        this.protectFluids = protectFluids;
        return this;
    }

    @Override
    public GTNL_ProcessingLogic setOverclock(double timeReduction, double powerIncrease) {
        this.overClockTimeReduction = timeReduction;
        this.overClockPowerIncrease = powerIncrease;
        return this;
    }

    /**
     * Sets overclock ratio to 4/4.
     */
    @Override
    public GTNL_ProcessingLogic enablePerfectOverclock() {
        return this.setOverclock(4.0, 4.0);
    }

    /**
     * Sets whether the multi should use amperage to OC or not.
     */
    @Override
    public GTNL_ProcessingLogic setAmperageOC(boolean amperageOC) {
        this.amperageOC = amperageOC;
        return this;
    }

    // endregion

    // region Overwrite calculated result

    /**
     * Overwrites calculated item output.
     */
    @Override
    public GTNL_ProcessingLogic overwriteOutputItems(ItemStack... itemOutputs) {
        this.outputItems = itemOutputs;
        return this;
    }

    /**
     * Overwrites calculated fluid output.
     */
    @Override
    public GTNL_ProcessingLogic overwriteOutputFluids(FluidStack... fluidOutputs) {
        this.outputFluids = fluidOutputs;
        return this;
    }

    /**
     * Overwrites calculated EU/t.
     */
    @Override
    public GTNL_ProcessingLogic overwriteCalculatedEut(long calculatedEut) {
        this.calculatedEut = calculatedEut;
        return this;
    }

    /**
     * Overwrites calculated duration.
     */
    @Override
    public GTNL_ProcessingLogic overwriteCalculatedDuration(int duration) {
        this.duration = duration;
        return this;
    }

    // endregion

    /**
     * Clears calculated results and provided machine inputs to prepare for the next machine operation.
     */
    @Override
    public GTNL_ProcessingLogic clear() {
        this.inputItems = null;
        this.inputFluids = null;
        this.specialSlotItem = null;
        this.outputItems = null;
        this.outputFluids = null;
        this.calculatedEut = 0;
        this.duration = 0;
        this.calculatedParallels = 0;
        this.activeDualInv = null;
        return this;
    }

    // region Logic

    /**
     * Refreshes recipemap to use. Remember to call this before {@link #process} to make sure correct recipemap is used.
     *
     * @return Recipemap to use now
     */
    @Override
    public RecipeMap<?> getCurrentRecipeMap() {
        RecipeMap<?> recipeMap;
        if (recipeMapSupplier == null) {
            recipeMap = null;
        } else {
            recipeMap = recipeMapSupplier.get();
        }
        if (lastRecipeMap != recipeMap) {
            if (lastRecipeMap != null) {
                dualInvWithPatternToRecipeCache.clear();
            }
            lastRecipe = null;
            lastRecipeMap = recipeMap;
        }
        return recipeMap;
    }

    /**
     * Executes the recipe check: Find recipe from recipemap, Calculate parallel, overclock and outputs.
     */
    @Nonnull
    @Override
    public CheckRecipeResult process() {
        RecipeMap<?> recipeMap = getCurrentRecipeMap();

        if (maxParallelSupplier != null) {
            maxParallel = maxParallelSupplier.get();
        }

        if (inputItems == null) {
            inputItems = new ItemStack[0];
        }
        if (inputFluids == null) {
            inputFluids = new FluidStack[0];
        }

        if (activeDualInv != null) {
            Set<GTRecipe> matchedRecipes = dualInvWithPatternToRecipeCache.get(activeDualInv);
            for (GTRecipe matchedRecipe : matchedRecipes) {
                if (matchedRecipe.maxParallelCalculatedByInputs(1, inputFluids, inputItems) == 1) {
                    CalculationResult foundResult = validateAndCalculateRecipe(matchedRecipe);
                    return foundResult.checkRecipeResult;
                }
            }
            activeDualInv = null;
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (isRecipeLocked && recipeLockableMachine != null && recipeLockableMachine.getSingleRecipeCheck() != null) {
            // Recipe checker is already built, we'll use it
            SingleRecipeCheck singleRecipeCheck = recipeLockableMachine.getSingleRecipeCheck();
            // Validate recipe here, otherwise machine will show "not enough output space"
            // even if recipe cannot be found
            if (singleRecipeCheck.checkRecipeInputs(false, 1, inputItems, inputFluids) == 0) {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }

            return validateAndCalculateRecipe(
                recipeLockableMachine.getSingleRecipeCheck()
                    .getRecipe()).checkRecipeResult;
        }
        Stream<GTRecipe> matchedRecipes = findRecipeMatches(recipeMap);
        Iterable<GTRecipe> recipeIterable = matchedRecipes::iterator;
        CheckRecipeResult checkRecipeResult = CheckRecipeResultRegistry.NO_RECIPE;
        for (GTRecipe matchedRecipe : recipeIterable) {
            CalculationResult foundResult = validateAndCalculateRecipe(matchedRecipe);
            if (foundResult.successfullyConsumedInputs) {
                // Successfully found and set recipe, so return it
                return foundResult.checkRecipeResult;
            }
            if (foundResult.checkRecipeResult != CheckRecipeResultRegistry.NO_RECIPE) {
                // Recipe failed in interesting way, so remember that and continue searching
                checkRecipeResult = foundResult.checkRecipeResult;
            }
        }
        return checkRecipeResult;
    }

    /**
     * Checks if supplied recipe is valid for process. This involves voltage check, output full check. If successful,
     * additionally performs input consumption, output calculation with parallel, and overclock calculation.
     *
     * @param recipe The recipe which will be checked and processed
     */
    @Nonnull
    public CalculationResult validateAndCalculateRecipe(@Nonnull GTRecipe recipe) {
        CheckRecipeResult result = validateRecipe(recipe);
        if (!result.wasSuccessful()) {
            return CalculationResult.ofFailure(result);
        }

        GTNL_ParallelHelper helper = createParallelHelper(recipe);
        GTNL_OverclockCalculator calculator = createOverclockCalculator(recipe);
        helper.setCalculator(calculator);
        helper.build();

        if (!helper.getResult()
            .wasSuccessful()) {
            return CalculationResult.ofFailure(helper.getResult());
        }

        return CalculationResult.ofSuccess(applyRecipe(recipe, helper, calculator, result));
    }

    /**
     * Check has been succeeded, so it applies the recipe and calculated parameters.
     * At this point, inputs have been already consumed.
     */
    @Nonnull
    public CheckRecipeResult applyRecipe(@Nonnull GTRecipe recipe, @Nonnull GTNL_ParallelHelper helper,
        @Nonnull GTNL_OverclockCalculator calculator, @Nonnull CheckRecipeResult result) {
        if (recipe.mCanBeBuffered) {
            lastRecipe = recipe;
        } else {
            lastRecipe = null;
        }
        calculatedParallels = helper.getCurrentParallel();

        if (calculator.getConsumption() == Long.MAX_VALUE) {
            return CheckRecipeResultRegistry.POWER_OVERFLOW;
        }
        if (calculator.getDuration() == Integer.MAX_VALUE) {
            return CheckRecipeResultRegistry.DURATION_OVERFLOW;
        }

        calculatedEut = calculator.getConsumption();

        double finalDuration = calculateDuration(recipe, helper, calculator);
        if (finalDuration >= Integer.MAX_VALUE) {
            return CheckRecipeResultRegistry.DURATION_OVERFLOW;
        }
        duration = (int) finalDuration;

        if (machine instanceof MTEMultiBlockBase mte) {
            try {
                for (MTEHatchMaintenance maintenance : mte.mMaintenanceHatches) {
                    if (maintenance instanceof IConfigurationMaintenance customMaintenance
                        && customMaintenance.isConfiguration()) {
                        duration = (int) Math.max(1, duration * customMaintenance.getConfigTime() / 100.0);
                        break;
                    }
                }
            } catch (Exception e) {
                ScienceNotLeisure.LOG.warn("Error reading IConfigurationMaintenance from MTE: {}", mte, e);
            }
        }

        CheckRecipeResult hookResult = onRecipeStart(recipe);
        if (!hookResult.wasSuccessful()) {
            return hookResult;
        }

        outputItems = helper.getItemOutputs();
        outputFluids = helper.getFluidOutputs();

        return result;
    }

    /**
     * Override to tweak final duration that will be set as a result of this logic class.
     */
    public double calculateDuration(@Nonnull GTRecipe recipe, @Nonnull GTNL_ParallelHelper helper,
        @Nonnull GTNL_OverclockCalculator calculator) {
        return calculator.getDuration() * helper.getDurationMultiplierDouble();
    }

    /**
     * Finds a list of matched recipes. At this point no additional check to the matched recipe has been done.
     * <p>
     * Override {@link #validateRecipe} to have custom check.
     * <p>
     * Override this method if it doesn't work with normal recipemaps.
     */
    @Nonnull
    @Override
    public Stream<GTRecipe> findRecipeMatches(@Nullable RecipeMap<?> map) {
        if (map == null) {
            return Stream.empty();
        }
        return map.findRecipeQuery()
            .items(inputItems)
            .fluids(inputFluids)
            .specialSlot(specialSlotItem)
            .cachedRecipe(lastRecipe)
            .findAll();
    }

    /**
     * Override to do additional check for found recipe if needed.
     */
    @Nonnull
    @Override
    public CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    /**
     * Override to tweak parallel logic if needed.
     */
    @Nonnull
    @Override
    public GTNL_ParallelHelper createParallelHelper(@Nonnull GTRecipe recipe) {
        return new GTNL_ParallelHelper().setRecipe(recipe)
            .setItemInputs(inputItems)
            .setFluidInputs(inputFluids)
            .setAvailableEUt(availableVoltage * availableAmperage)
            .setMachine(machine, protectItems, protectFluids)
            .setRecipeLocked(recipeLockableMachine, isRecipeLocked)
            .setMaxParallel(maxParallel)
            .setEUtModifier(euModifier)
            .enableBatchMode(batchSize)
            .setConsumption(true)
            .setOutputCalculation(true);
    }

    /**
     * Override to tweak overclock logic if needed.
     */
    @Nonnull
    @Override
    public GTNL_OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
        return new GTNL_OverclockCalculator().setRecipeEUt(recipe.mEUt)
            .setAmperage(availableAmperage)
            .setEUt(availableVoltage)
            .setMaxTierSkips(maxTierSkips)
            .setMaxOverclocks(maxOverclocks)
            .setDuration(recipe.mDuration)
            .setExtraDurationModifier(extraSpeedBoost)
            .setDurationModifier(speedBoost)
            .setEUtDiscount(euModifier)
            .setAmperageOC(amperageOC)
            .setDurationDecreasePerOC(overClockTimeReduction)
            .setEUtIncreasePerOC(overClockPowerIncrease);
    }

    /**
     * Override to perform additional logic when recipe starts.
     * <p>
     * This is called when the recipe processing logic has finished all
     * checks, consumed all inputs, but has not yet set the outputs to
     * be produced. Returning a result other than SUCCESSFUL will void
     * all inputs!
     */
    @Nonnull
    @Override
    public CheckRecipeResult onRecipeStart(@Nonnull GTRecipe recipe) {
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    // endregion

    // region Getters

    @Override
    public ItemStack[] getOutputItems() {
        return outputItems;
    }

    @Override
    public FluidStack[] getOutputFluids() {
        return outputFluids;
    }

    public ItemStack[] getInputItems() {
        return inputItems;
    }

    public FluidStack[] getInputFluids() {
        return inputFluids;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public long getCalculatedEut() {
        return calculatedEut;
    }

    @Override
    public int getCurrentParallels() {
        return calculatedParallels;
    }

    // endregion

    /**
     * Represents the status of check recipe calculation. {@link #successfullyConsumedInputs} does not necessarily mean
     * {@link #checkRecipeResult} being successful, when duration or power is overflowed. Being failure means
     * recipe cannot meet requirements and recipe search should be continued if possible.
     */
    public static class CalculationResult {

        public final boolean successfullyConsumedInputs;
        public final CheckRecipeResult checkRecipeResult;

        public static CalculationResult ofSuccess(CheckRecipeResult checkRecipeResult) {
            return new CalculationResult(true, checkRecipeResult);
        }

        public static CalculationResult ofFailure(CheckRecipeResult checkRecipeResult) {
            return new CalculationResult(false, checkRecipeResult);
        }

        private CalculationResult(boolean successfullyConsumedInputs, CheckRecipeResult checkRecipeResult) {
            this.successfullyConsumedInputs = successfullyConsumedInputs;
            this.checkRecipeResult = checkRecipeResult;
        }
    }
}
