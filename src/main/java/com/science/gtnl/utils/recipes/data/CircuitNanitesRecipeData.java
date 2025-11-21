package com.science.gtnl.utils.recipes.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CircuitNanitesRecipeData implements Comparable<CircuitNanitesRecipeData> {

    public static Map<ItemStack, CircuitNanitesRecipeData> recipeDataMap = new HashMap<>();

    public ItemStack stack;
    public double speedBoost = 1.0, euModifier = 1.0, successChance = 1.0, failedChance = 0, outputMultiplier = 1.0;
    public int parallelCount = 1, maxTierSkips = 1;
    public double speedBoostMin = 0, speedBoostMax = 1;
    public double euModifierMin = 0, euModifierMax = 1;
    public int maxTierSkipsMin = 0, maxTierSkipsMax = 1;
    public double successChanceMin = 0, successChanceMax = 1;
    public double failedChanceMin = 0, failedChanceMax = 1;
    public int parallelCountMin = 0, parallelCountMax = 1;
    public double outputMultiplierMin = 0, outputMultiplierMax = 1;
    public long worldSeed;

    public CircuitNanitesRecipeData() {}

    public CircuitNanitesRecipeData(ItemStack stack, long worldSeed, double speedBoostMin, double speedBoostMax,
        double euModifierMin, double euModifierMax, int maxTierSkipsMin, int maxTierSkipsMax, double successChanceMin,
        double successChanceMax, double failedChanceMin, double failedChanceMax, int parallelCountMin,
        int parallelCountMax, double outputMultiplierMin, double outputMultiplierMax) {
        this.stack = stack;
        this.worldSeed = worldSeed;
        setRangeParams(
            speedBoostMin,
            speedBoostMax,
            euModifierMin,
            euModifierMax,
            maxTierSkipsMin,
            maxTierSkipsMax,
            successChanceMin,
            successChanceMax,
            failedChanceMin,
            failedChanceMax,
            parallelCountMin,
            parallelCountMax,
            outputMultiplierMin,
            outputMultiplierMax);
        setDirectParams(worldSeed);
        recipeDataMap.put(stack, this);
    }

    public static CircuitNanitesRecipeData getOrCreate(ItemStack stack, long worldSeed, double speedBoostMin,
        double speedBoostMax, double euModifierMin, double euModifierMax, int maxTierSkipsMin, int maxTierSkipsMax,
        double successChanceMin, double successChanceMax, double failedChanceMin, double failedChanceMax,
        int parallelCountMin, int parallelCountMax, double outputMultiplierMin, double outputMultiplierMax) {

        CircuitNanitesRecipeData existing = recipeDataMap.get(stack);
        if (existing != null) {
            return existing;
        }

        CircuitNanitesRecipeData data = new CircuitNanitesRecipeData();
        data.stack = stack;
        data.worldSeed = worldSeed;
        data.setRangeParams(
            speedBoostMin,
            speedBoostMax,
            euModifierMin,
            euModifierMax,
            maxTierSkipsMin,
            maxTierSkipsMax,
            successChanceMin,
            successChanceMax,
            failedChanceMin,
            failedChanceMax,
            parallelCountMin,
            parallelCountMax,
            outputMultiplierMin,
            outputMultiplierMax);
        data.setDirectParams(worldSeed);
        recipeDataMap.put(stack, data);
        return data;
    }

    public void setDirectParams(long worldSeed) {
        Random random = new Random(worldSeed);
        this.speedBoost = randomDoubleInRange(random, speedBoostMin, speedBoostMax);
        this.euModifier = randomDoubleInRange(random, euModifierMin, euModifierMax);
        this.maxTierSkips = randomIntInRange(random, maxTierSkipsMin, maxTierSkipsMax);
        this.successChance = randomDoubleInRange(random, successChanceMin, successChanceMax);
        this.failedChance = randomDoubleInRange(random, failedChanceMin, failedChanceMax);
        this.parallelCount = randomIntInRange(random, parallelCountMin, parallelCountMax);
        this.outputMultiplier = randomDoubleInRange(random, outputMultiplierMin, outputMultiplierMax);
    }

    public void setRangeParams(double speedBoostMin, double speedBoostMax, double euModifierMin, double euModifierMax,
        int maxTierSkipsMin, int maxTierSkipsMax, double successChanceMin, double successChanceMax,
        double failedChanceMin, double failedChanceMax, int parallelCountMin, int parallelCountMax,
        double outputMultiplierMin, double outputMultiplierMax) {
        if (speedBoostMin >= 0) this.speedBoostMin = speedBoostMin;
        if (speedBoostMax >= 0) this.speedBoostMax = speedBoostMax;

        if (euModifierMin >= 0) this.euModifierMin = euModifierMin;
        if (euModifierMax >= 0) this.euModifierMax = euModifierMax;

        if (maxTierSkipsMin >= 0) this.maxTierSkipsMin = maxTierSkipsMin;
        if (maxTierSkipsMax >= 0) this.maxTierSkipsMax = maxTierSkipsMax;

        if (successChanceMin >= 0) this.successChanceMin = successChanceMin;
        if (successChanceMax >= 0) this.successChanceMax = successChanceMax;

        if (failedChanceMin >= 0) this.failedChanceMin = failedChanceMin;
        if (failedChanceMax >= 0) this.failedChanceMax = failedChanceMax;
        if (parallelCountMin >= 0) this.parallelCountMin = parallelCountMin;
        if (parallelCountMax >= 0) this.parallelCountMax = parallelCountMax;
        if (outputMultiplierMin >= 0) this.outputMultiplierMin = outputMultiplierMin;
        if (outputMultiplierMax >= 0) this.outputMultiplierMax = outputMultiplierMax;
    }

    @Override
    public int compareTo(CircuitNanitesRecipeData other) {
        return Integer.compare(this.parallelCount, other.parallelCount);
    }

    public static double randomDoubleInRange(Random random, double min, double max) {
        if (max <= min) return min;
        double value = min + (max - min) * random.nextDouble();
        return Math.round(value * 1000.0) / 1000.0;
    }

    public static int randomIntInRange(Random random, int min, int max) {
        if (max <= min) return min;
        return random.nextInt(max - min + 1) + min;
    }

}
