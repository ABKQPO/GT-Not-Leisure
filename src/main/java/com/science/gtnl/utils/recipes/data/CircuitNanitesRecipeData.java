package com.science.gtnl.utils.recipes.data;

import java.util.Random;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CircuitNanitesRecipeData implements Comparable<CircuitNanitesRecipeData> {

    public static Object2ObjectMap<ItemId, CircuitNanitesRecipeData> recipeDataMap = new Object2ObjectOpenHashMap<>();

    public ItemId stack;
    public double speedBoost = 1.0, euModifier = 1.0, failedChance = 0, outputMultiplier = 1.0;
    public int parallelCount = 1, nantiesTier;
    public double speedBoostMin = 0, speedBoostMax = 1;
    public double euModifierMin = 0, euModifierMax = 1;
    public double failedChanceMin = 0, failedChanceMax = 1;
    public int parallelCountMin = 0, parallelCountMax = 1;
    public double outputMultiplierMin = 0, outputMultiplierMax = 1;
    public long worldSeed;

    public CircuitNanitesRecipeData() {}

    public CircuitNanitesRecipeData(ItemStack stack, long worldSeed, int nantiesTier, double speedBoostMin,
        double speedBoostMax, double euModifierMin, double euModifierMax, double failedChanceMin,
        double failedChanceMax, int parallelCountMin, int parallelCountMax, double outputMultiplierMin,
        double outputMultiplierMax) {
        this.stack = ItemId.createWithoutNBT(stack);
        this.worldSeed = worldSeed;
        this.nantiesTier = nantiesTier;
        setRangeParams(
            speedBoostMin,
            speedBoostMax,
            euModifierMin,
            euModifierMax,
            failedChanceMin,
            failedChanceMax,
            parallelCountMin,
            parallelCountMax,
            outputMultiplierMin,
            outputMultiplierMax);
        setDirectParams(worldSeed);
        recipeDataMap.put(this.stack, this);
    }

    public static CircuitNanitesRecipeData getOrCreate(ItemStack stack, long worldSeed, int nantiesTier,
        double speedBoostMin, double speedBoostMax, double euModifierMin, double euModifierMax, double failedChanceMin,
        double failedChanceMax, int parallelCountMin, int parallelCountMax, double outputMultiplierMin,
        double outputMultiplierMax) {

        CircuitNanitesRecipeData existing = recipeDataMap.get(ItemId.createWithoutNBT(stack));
        if (existing != null) {
            return existing;
        }

        CircuitNanitesRecipeData data = new CircuitNanitesRecipeData();
        data.stack = ItemId.createWithoutNBT(stack);
        data.worldSeed = worldSeed;
        data.nantiesTier = nantiesTier;
        data.setRangeParams(
            speedBoostMin,
            speedBoostMax,
            euModifierMin,
            euModifierMax,
            failedChanceMin,
            failedChanceMax,
            parallelCountMin,
            parallelCountMax,
            outputMultiplierMin,
            outputMultiplierMax);
        data.setDirectParams(worldSeed);
        recipeDataMap.put(data.stack, data);
        return data;
    }

    public void setDirectParams(long worldSeed) {
        Random random = new Random(worldSeed);
        this.speedBoost = randomDoubleInRange(random, speedBoostMin, speedBoostMax);
        this.euModifier = randomDoubleInRange(random, euModifierMin, euModifierMax);
        this.failedChance = randomDoubleInRange(random, failedChanceMin, failedChanceMax);
        this.parallelCount = randomIntInRange(random, parallelCountMin, parallelCountMax);
        this.outputMultiplier = randomDoubleInRange(random, outputMultiplierMin, outputMultiplierMax);
    }

    public void setRangeParams(double speedBoostMin, double speedBoostMax, double euModifierMin, double euModifierMax,
        double failedChanceMin, double failedChanceMax, int parallelCountMin, int parallelCountMax,
        double outputMultiplierMin, double outputMultiplierMax) {

        this.speedBoostMin = speedBoostMin;
        this.speedBoostMax = speedBoostMax;

        this.euModifierMin = euModifierMin;
        this.euModifierMax = euModifierMax;

        this.failedChanceMin = failedChanceMin;
        this.failedChanceMax = failedChanceMax;

        this.parallelCountMin = parallelCountMin;
        this.parallelCountMax = parallelCountMax;

        this.outputMultiplierMin = outputMultiplierMin;
        this.outputMultiplierMax = outputMultiplierMax;
    }

    @Override
    public int compareTo(CircuitNanitesRecipeData other) {
        return Integer.compare(this.parallelCount, other.parallelCount);
    }

    public static double randomDoubleInRange(Random random, double min, double max) {
        if (max <= min) return min;
        double value = min + (max - min) * random.nextDouble();
        return Math.round(value * 100.0) / 100.0;
    }

    public static int randomIntInRange(Random random, int min, int max) {
        if (max <= min) return min;
        return random.nextInt(max - min + 1) + min;
    }

}
