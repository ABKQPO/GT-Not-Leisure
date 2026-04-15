package com.science.gtnl.utils.recipes;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import gregtech.api.util.GTRecipe;
import gregtech.api.util.OverclockCalculator;

public class GTNLOverclockCalculator extends OverclockCalculator {

    // Basic properties
    /** EUt the recipe originally runs at */
    public long recipeEUt = 0;
    /** Voltage of the machine */
    public long machineVoltage = 0;
    /** Amperage of the machine */
    public long machineAmperage = 1;
    /** Duration of the recipe */
    public int duration = 0;
    /** Min duration of the recipe */
    public int minDuration = 1;
    /** A supplier used for machines which have a custom way of calculating base duration, like Neutron Activator */
    public Supplier<Double> durationUnderOneTickSupplier;
    /** The parallel the machine has when trying to overclock */
    public int parallel = 1;
    /** The max amount of tiers above the machine voltage a recipe is valid */
    public int maxTierSkip = Integer.MAX_VALUE;

    // Modifiers
    /** Energy modifier that is applied at the start of calculating overclocks, like GT++ machines */
    public double eutModifier = 1.00;
    /** Duration modifier that is applied at the start of calculating overclocks, like GT++ machines */
    public double durationModifier = 1.00;
    /** Extra Duration modifier that is applied at the start of calculating overclocks, like GTM machines */
    public double extraDurationModifier = 1.00;

    // Overclock parameters
    /** How much the energy would be multiplied by per overclock available */
    public double eutIncreasePerOC = 4;
    /** How much the duration would be divided by per overclock made that isn't an overclock from HEAT */
    public double durationDecreasePerOC = 2;
    /** Whether the multi should use laser overclocks. */
    public boolean laserOC;
    /** Whether the multi should use amperage to overclock normally. */
    public boolean amperageOC;
    /** Maximum number of overclocks to perform. Defaults to no limit. */
    public int maxOverclocks = Integer.MAX_VALUE;
    /** Maximum number of regular overclocks to perform before exotic (e.g. laser) overclocks. Defaults to no limit. */
    public int maxRegularOverclocks = Integer.MAX_VALUE;
    /** How many overclocks have been performed */
    public int overclocks = 0;
    /** Should we actually try to calculate overclocking */
    public boolean noOverclock;
    /** The parallel the machine actually used. */
    public int currentParallel;

    // Heat parameters
    /** The min heat required for the recipe */
    public int recipeHeat = 0;
    /** The heat the machine has when starting the recipe */
    public int machineHeat = 0;
    /** How much the duration should be divided by for each 1800K above recipe heat */
    public final double durationDecreasePerHeatOC = 4;
    /** Whether to enable overclocking with heat like the EBF every 1800 heat difference */
    public boolean heatOC;
    /** Whether to enable heat discounts every 900 heat difference */
    public boolean heatDiscount;
    /** The value used for discount final eut per 900 heat */
    public double heatDiscountExponent = 0.95;

    // Results
    /** variable to check whether the overclocks have been calculated */
    public boolean calculated;
    /** The calculated duration result. */
    public int calculatedDuration;
    /** The calculated energy consumption result. */
    public long calculatedConsumption;

    // Constants
    public static final int HEAT_DISCOUNT_THRESHOLD = 900;
    public static final int HEAT_OVERCLOCK_THRESHOLD = 1800;
    public static final double LOG4 = Math.log(4);

    /** Creates calculator that doesn't do OC at all. Will use recipe duration. */
    public static GTNLOverclockCalculator ofNoOverclock(@NotNull GTRecipe recipe) {
        return ofNoOverclock(recipe.mEUt, recipe.mDuration);
    }

    /** Creates calculator that doesn't do OC at all, with set duration. */
    public static GTNLOverclockCalculator ofNoOverclock(long eut, int duration) {
        return new GTNLOverclockCalculator().setRecipeEUt(eut)
            .setDuration(duration)
            .setEUt(eut)
            .setNoOverclock(true);
    }

    /** An Overclock helper for calculating overclocks in many different situations */
    public GTNLOverclockCalculator() {}

    // region setters
    /** @param recipeEUt Sets the Recipe's starting voltage */
    @NotNull
    @Override
    public GTNLOverclockCalculator setRecipeEUt(long recipeEUt) {
        this.recipeEUt = recipeEUt;
        return this;
    }

    /** @param machineVoltage Sets the EUt that the machine can use. This is the voltage of the machine */
    @NotNull
    @Override
    public GTNLOverclockCalculator setEUt(long machineVoltage) {
        this.machineVoltage = machineVoltage;
        return this;
    }

    /** @param duration Sets the duration of the recipe */
    @NotNull
    @Override
    public GTNLOverclockCalculator setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    /** @param minDuration Sets the min duration of the recipe */
    @NotNull
    public GTNLOverclockCalculator setMinDuration(int minDuration) {
        this.minDuration = minDuration;
        return this;
    }

    /** @param machineAmperage Sets the Amperage that the machine can support */
    @NotNull
    @Override
    public GTNLOverclockCalculator setAmperage(long machineAmperage) {
        this.machineAmperage = machineAmperage;
        return this;
    }

    /** Enables Perfect OC in calculation */
    @NotNull
    @Override
    public GTNLOverclockCalculator enablePerfectOC() {
        this.durationDecreasePerOC = 4;
        return this;
    }

    /** Enables Perfect OC in calculation */
    @NotNull
    public GTNLOverclockCalculator setPerfectOC(boolean enable) {
        this.durationDecreasePerOC = enable ? 4 : 2;
        return this;
    }

    /** Set if we should be calculating overclocking using EBF's perfectOC */
    @NotNull
    @Override
    public GTNLOverclockCalculator setHeatOC(boolean heatOC) {
        this.heatOC = heatOC;
        return this;
    }

    /** Sets if we should add a heat discount at the start of calculating an overclock, just like the EBF */
    @NotNull
    @Override
    public GTNLOverclockCalculator setHeatDiscount(boolean heatDiscount) {
        this.heatDiscount = heatDiscount;
        return this;
    }

    /** Sets the starting heat of the recipe */
    @NotNull
    @Override
    public GTNLOverclockCalculator setRecipeHeat(int recipeHeat) {
        this.recipeHeat = recipeHeat;
        return this;
    }

    /** Sets the heat of the coils on the machine */
    @NotNull
    @Override
    public GTNLOverclockCalculator setMachineHeat(int machineHeat) {
        this.machineHeat = machineHeat;
        return this;
    }

    /** Sets an EUtDiscount. 0.9 is 10% less energy. 1.1 is 10% more energy */
    @NotNull
    @Override
    public GTNLOverclockCalculator setEUtDiscount(double aEUtDiscount) {
        this.eutModifier = aEUtDiscount;
        return this;
    }

    /** Sets a Speed Boost for the multiblock. 0.9 is 10% faster. 1.1 is 10% slower */
    @NotNull
    @Override
    public GTNLOverclockCalculator setDurationModifier(double aSpeedBoost) {
        this.durationModifier = aSpeedBoost;
        return this;
    }

    /** Sets a Extra Speed Boost for the multiblock. 0.9 is 10% faster. 1.1 is 10% slower */
    @NotNull
    public GTNLOverclockCalculator setExtraDurationModifier(double aSpeedBoost) {
        this.extraDurationModifier = aSpeedBoost;
        return this;
    }

    /** Sets the parallel that the multiblock uses */
    @NotNull
    @Override
    public GTNLOverclockCalculator setParallel(int aParallel) {
        this.parallel = aParallel;
        return this;
    }

    /** Sets the max tiers above the machine's voltage a valid recipe can be */
    @NotNull
    @Override
    public GTNLOverclockCalculator setMaxTierSkips(int aMaxTierSkips) {
        this.maxTierSkip = aMaxTierSkips;
        return this;
    }

    @NotNull
    @Override
    public GTNLOverclockCalculator setUnlimitedTierSkips() {
        this.maxTierSkip = Integer.MAX_VALUE;
        return this;
    }

    /**
     * Sets the heat discount during OC calculation if HeatOC is used. Default: 0.95 = 5% discount Used like a EU/t
     * Discount
     */
    @NotNull
    @Override
    public GTNLOverclockCalculator setHeatDiscountMultiplier(double heatDiscountExponent) {
        this.heatDiscountExponent = heatDiscountExponent;
        return this;
    }

    /**
     * Sets the amount that the eut would be multiplied by per overclock. Do not set as 1(ONE) if the duration decrease
     * is also 1(ONE)!
     */
    @NotNull
    @Override
    public GTNLOverclockCalculator setEUtIncreasePerOC(double eutIncreasePerOC) {
        if (eutIncreasePerOC <= 0)
            throw new IllegalArgumentException("EUt increase can't be a negative number or zero");
        this.eutIncreasePerOC = eutIncreasePerOC;
        return this;
    }

    /**
     * Sets the amount that the duration would be divided by per overclock. Do not set as 1(ONE) if the eut increase is
     * also 1(ONE)!
     */
    @NotNull
    @Override
    public GTNLOverclockCalculator setDurationDecreasePerOC(double durationDecreasePerOC) {
        if (durationDecreasePerOC <= 0)
            throw new IllegalArgumentException("Duration decrease can't be a negative number or zero");
        this.durationDecreasePerOC = durationDecreasePerOC;
        return this;
    }

    /**
     * Sets the maximum number of overclocks that can be performed, regardless of how much power is available.
     * Negative values are rounded up to 0.
     */
    @NotNull
    @Override
    public GTNLOverclockCalculator setMaxOverclocks(int maxOverclocks) {
        this.maxOverclocks = Math.max(maxOverclocks, 0);
        return this;
    }

    /**
     * Sets the maximum number of regular overclocks that can be performed before exotic (e.g. laser) overclocks,
     * regardless of how much power is available. Negative values are rounded up to 0.
     */
    @NotNull
    @Override
    public GTNLOverclockCalculator setMaxRegularOverclocks(int maxRegularOverclocks) {
        this.maxRegularOverclocks = Math.max(maxRegularOverclocks, 0);
        return this;
    }

    @NotNull
    @Override
    public GTNLOverclockCalculator setLaserOC(boolean laserOC) {
        this.laserOC = laserOC;
        return this;
    }

    @NotNull
    @Override
    public GTNLOverclockCalculator setAmperageOC(boolean amperageOC) {
        this.amperageOC = amperageOC;
        return this;
    }

    /** Set a supplier for calculating custom duration for when its needed under one tick */
    @NotNull
    @Override
    public GTNLOverclockCalculator setDurationUnderOneTickSupplier(Supplier<Double> supplier) {
        this.durationUnderOneTickSupplier = supplier;
        return this;
    }

    /** Sets if we should do overclocking or not */
    @NotNull
    @Override
    public GTNLOverclockCalculator setNoOverclock(boolean noOverclock) {
        this.noOverclock = noOverclock;
        return this;
    }

    /** Set actually performed parallel */
    @Override
    public GTNLOverclockCalculator setCurrentParallel(int currentParallel) {
        this.currentParallel = currentParallel;
        // Sets parallel to the actually performed one if machine's parallel is underused.
        this.parallel = Math.min(parallel, currentParallel);
        return this;
    }

    /** @return The consumption after overclock has been calculated */
    @Override
    public long getConsumption() {
        if (!calculated) {
            throw new IllegalStateException("Tried to get consumption before calculating");
        }
        return calculatedConsumption;
    }

    /** @return The duration of the recipe after overclock has been calculated */
    @Override
    public int getDuration() {
        if (!calculated) {
            throw new IllegalStateException("Tried to get duration before calculating");
        }
        return calculatedDuration;
    }

    /** @return Number of performed overclocks */
    @Override
    public int getPerformedOverclocks() {
        if (!calculated) {
            throw new IllegalStateException("Tried to get performed overclocks before calculating");
        }
        return overclocks;
    }

    @Override
    public boolean getAllowedTierSkip() {
        if (this.maxTierSkip == Integer.MAX_VALUE) return true;
        return recipeEUt <= machineVoltage * (1L << (2 * maxTierSkip));
    }

    @Override
    public boolean hasDurationUnderOneTickSupplier() {
        return durationUnderOneTickSupplier != null;
    }

    @Override
    public double getDurationUnderOneTickSupplier() {
        return durationUnderOneTickSupplier.get();
    }

    /** Call this when all values have been put it. */
    @NotNull
    @Override
    public GTNLOverclockCalculator calculate() {
        if (calculated) {
            throw new IllegalStateException("Tried to calculate overclocks twice");
        }
        calculateOverclock();
        calculated = true;
        return this;
    }

    public double calculateHeatDiscountMultiplier() {
        int heatDiscounts = heatDiscount ? (machineHeat - recipeHeat) / HEAT_DISCOUNT_THRESHOLD : 0;
        return Math.pow(heatDiscountExponent, heatDiscounts);
    }

    @Override
    public void calculateOverclock() {
        // Determine the base duration, using the custom supplier if available.
        double duration = durationUnderOneTickSupplier != null ? durationUnderOneTickSupplier.get()
            : this.duration * durationModifier * extraDurationModifier;

        // If currentParallel isn't set, assume full parallel usage.
        currentParallel = Math.max(currentParallel, parallel);

        // Treat ULV (tier 0) as LV (tier 1) for overclocking calculations.
        double recipePower = recipeEUt * parallel * eutModifier * calculateHeatDiscountMultiplier();
        double recipePowerTier = Math.max(Math.log(recipePower / 8) / LOG4, 1);
        double machinePower = machineVoltage * (amperageOC ? machineAmperage : Math.min(machineAmperage, parallel));
        double machinePowerTier = Math.max(Math.log(machinePower / 8) / LOG4, 1);

        // If overclocking is disabled, use the base values and return.
        if (noOverclock) {
            calculatedConsumption = (long) Math.ceil(recipePower);
            calculatedDuration = (int) Math.ceil(duration);
            return;
        }

        // Special handling for laser overclocking.
        if (laserOC) {
            double eutOverclock = recipePower;
            double currentDuration = duration;

            // Keep increasing power until normal overclocks are used.
            int regularOverclocks = 0;
            while (eutOverclock * 4.0 < machinePower && regularOverclocks < maxRegularOverclocks) {
                if (currentDuration / durationDecreasePerOC < minDuration) break;

                eutOverclock *= 4.0;
                currentDuration /= durationDecreasePerOC;
                regularOverclocks++;
            }

            // Keep increasing power until it hits the machine's limit.
            int laserOverclocks = 0;
            while (eutOverclock * (4.0 + 0.3 * (laserOverclocks + 1)) < machinePower) {
                if (currentDuration / durationDecreasePerOC < minDuration) break;

                eutOverclock *= 4.0 + 0.3 * (laserOverclocks + 1);
                currentDuration /= durationDecreasePerOC;
                laserOverclocks++;
            }

            overclocks = regularOverclocks + laserOverclocks;
            calculatedConsumption = (long) Math.ceil(eutOverclock);
            calculatedDuration = (int) Math.max(currentDuration, 1);
            return;
        }

        // Limit overclocks allowed by power tier.
        overclocks = Math.min(maxOverclocks, (int) (machinePowerTier - recipePowerTier));

        // If amperage overclocks are disabled, limit overclocks by voltage tier.
        if (!amperageOC) {
            int voltageTierMachine = (int) Math.max(Math.ceil(Math.log((double) machineVoltage / 8) / LOG4), 1);
            int voltageTierRecipe = (int) Math.max(Math.ceil(Math.log((double) recipeEUt / 8) / LOG4), 1);
            overclocks = Math.min(overclocks, voltageTierMachine - voltageTierRecipe);
        }

        // Make sure overclocks don't go negative. This allows recipes needing >1A to run on a single hatch.
        overclocks = Math.max(overclocks, 0);

        if (duration > minDuration) {
            int durationLimitedOC = (int) (Math.log(duration / minDuration) / Math.log(durationDecreasePerOC));
            overclocks = Math.min(overclocks, Math.max(durationLimitedOC, 0));
        } else {
            overclocks = 0;
        }

        // Split overclocks into heat-based and regular overclocks.
        int heatOverclocks = Math.min(heatOC ? (machineHeat - recipeHeat) / HEAT_OVERCLOCK_THRESHOLD : 0, overclocks);
        long regularOverclocks = overclocks - heatOverclocks;

        // Adjust power consumption and processing time based on overclocks.
        calculatedConsumption = (long) Math.ceil(recipePower * Math.pow(eutIncreasePerOC, overclocks));
        duration /= Math.pow(durationDecreasePerHeatOC, heatOverclocks);
        duration /= Math.pow(durationDecreasePerOC, regularOverclocks);
        calculatedDuration = (int) Math.max(duration, 1);

    }

    /**
     * Returns a multiplier to parallel based on much it is overclock too much. This doesn't count as calculating
     */
    @Override
    public double calculateMultiplierUnderOneTick() {
        int neededOverclocks = 0;

        // If overclocking is disabled, get no multiplier.
        if (noOverclock) return 1;

        // Determine the base duration, using the custom supplier if available.
        double duration = durationUnderOneTickSupplier != null ? durationUnderOneTickSupplier.get()
            : this.duration * durationModifier * extraDurationModifier;

        // Treat ULV (tier 0) as LV (tier 1) for overclocking calculations.
        double recipePower = recipeEUt * parallel * eutModifier * calculateHeatDiscountMultiplier();
        double recipePowerTier = Math.max(Math.log(recipePower / 8) / LOG4, 1);
        double machinePower = machineVoltage * (amperageOC ? machineAmperage : Math.min(machineAmperage, parallel));
        double machinePowerTier = Math.max(Math.log(machinePower / 8) / LOG4, 1);

        // Special handling for laser overclocking.
        if (laserOC) {
            double eutOverclock = recipePower;

            // Keep increasing power until normal overclocks are used.
            int regularOverclocks = 0;
            while (eutOverclock * 4.0 < machinePower && regularOverclocks < maxRegularOverclocks) {
                eutOverclock *= 4.0;
                regularOverclocks++;
                if (duration / Math.pow(durationDecreasePerOC, overclocks) < 2 && neededOverclocks == 0) {
                    neededOverclocks = regularOverclocks;
                }
            }

            // Keep increasing power until it hits the machine's limit.
            int laserOverclocks = 0;
            while (eutOverclock * (4.0 + 0.3 * (laserOverclocks + 1)) < machinePower) {
                eutOverclock *= (4.0 + 0.3 * (laserOverclocks + 1));
                laserOverclocks++;
                if (duration / Math.pow(durationDecreasePerOC, overclocks) < 2 && neededOverclocks == 0) {
                    neededOverclocks = overclocks + laserOverclocks;
                }
            }

            int overclocks = regularOverclocks + laserOverclocks;
            return Math.pow(durationDecreasePerOC, Math.max(neededOverclocks - overclocks, 0));
        }

        // Limit overclocks allowed by power tier.
        int overclocks = Math.min(maxOverclocks, (int) (machinePowerTier - recipePowerTier));

        // If amperage overclocks are disabled, limit overclocks by voltage tier.
        if (!amperageOC) {
            int voltageTierMachine = (int) Math.max(Math.ceil(Math.log((double) machineVoltage / 8) / LOG4), 1);
            int voltageTierRecipe = (int) Math.max(Math.ceil(Math.log((double) recipeEUt / 8) / LOG4), 1);
            overclocks = Math.min(overclocks, voltageTierMachine - voltageTierRecipe);
        }

        // Make sure overclocks don't go negative. This allows recipes needing >1A to run on a single hatch.
        overclocks = Math.max(overclocks, 0);

        // Split overclocks into heat-based and regular overclocks.
        int heatOverclocks = Math.min(heatOC ? (machineHeat - recipeHeat) / HEAT_OVERCLOCK_THRESHOLD : 0, overclocks);
        int regularOverclocks = overclocks - heatOverclocks;

        double originalDuration = duration;
        int neededHeatOverclocks = (int) Math.ceil((Math.log(duration) / Math.log(durationDecreasePerHeatOC)));
        duration /= Math.pow(durationDecreasePerHeatOC, heatOverclocks);
        neededOverclocks = (int) Math.ceil((Math.log(duration) / Math.log(durationDecreasePerOC)));

        double heatMultiplier = Math.pow(durationDecreasePerHeatOC, Math.max(heatOverclocks - neededHeatOverclocks, 0));
        double regularMultiplier = Math.pow(durationDecreasePerOC, Math.max(regularOverclocks - neededOverclocks, 0));

        // Produces a fractional multiplier that corrects for inaccuracies resulting from discrete parallels and tick
        // durations. It is 1 / (duration of first OC to go below 1 tick)
        double correctionMultiplier = 1.0;
        if (heatOverclocks >= neededHeatOverclocks) {
            double criticalDuration = originalDuration / Math.pow(durationDecreasePerHeatOC, neededHeatOverclocks);
            correctionMultiplier = 1 / criticalDuration;
        } else if (regularOverclocks >= neededOverclocks) {
            double criticalDuration = originalDuration / Math.pow(durationDecreasePerOC, neededOverclocks);
            correctionMultiplier = 1 / criticalDuration;
        }

        return Math.ceil(heatMultiplier * correctionMultiplier * regularMultiplier);
    }

    public GTNLOverclockCalculator reset() {

        // Basic properties
        this.recipeEUt = 0;
        this.machineVoltage = 0;
        this.machineAmperage = 1;
        this.duration = 0;
        // DO NOT reset durationUnderOneTickSupplier, it's user defined
        this.parallel = 1;
        this.maxTierSkip = Integer.MAX_VALUE;

        // Modifiers
        this.eutModifier = 1.0;
        this.durationModifier = 1.0;
        this.extraDurationModifier = 1.0;

        // Overclock parameters
        this.eutIncreasePerOC = 4;
        this.durationDecreasePerOC = 2;
        this.laserOC = false;
        this.amperageOC = false;
        this.maxOverclocks = Integer.MAX_VALUE;
        this.maxRegularOverclocks = Integer.MAX_VALUE;
        this.overclocks = 0;
        this.noOverclock = false;
        this.currentParallel = 0;

        // Heat parameters
        this.recipeHeat = 0;
        this.machineHeat = 0;
        // durationDecreasePerHeatOC is final, leave it untouched
        this.heatOC = false;
        this.heatDiscount = false;
        this.heatDiscountExponent = 0.95;

        // Results
        this.calculated = false;
        this.calculatedDuration = 0;
        this.calculatedConsumption = 0;

        return this;
    }
}
