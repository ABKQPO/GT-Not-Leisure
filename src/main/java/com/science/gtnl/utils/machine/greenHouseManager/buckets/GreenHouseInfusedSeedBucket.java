package com.science.gtnl.utils.machine.greenHouseManager.buckets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.api.IGreenHouse;
import com.science.gtnl.mixins.late.ThaumicTinkerer.AccessorAspectCropLootManager;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseBucket;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseDropTable;
import com.science.gtnl.utils.machine.greenHouseManager.IGreenHouseBucketFactory;

import thaumcraft.api.aspects.Aspect;
import thaumic.tinkerer.common.item.ItemInfusedSeeds;

public class GreenHouseInfusedSeedBucket extends GreenHouseBucket {

    public static IGreenHouseBucketFactory factory = new GreenHouseInfusedSeedBucket.Factory();
    public static String NBT_IDENTIFIER = "TTINKER:INFUSEDSEED";

    public static class Factory implements IGreenHouseBucketFactory {

        @Override
        public String getNBTIdentifier() {
            return NBT_IDENTIFIER;
        }

        @Override
        public GreenHouseBucket tryCreateBucket(IGreenHouse greenhouse, ItemStack input) {
            if (!(input.getItem() instanceof ItemInfusedSeeds)) return null;
            return new GreenHouseInfusedSeedBucket(input, 1);
        }

        @Override
        public GreenHouseBucket restore(NBTTagCompound nbt) {
            return new GreenHouseInfusedSeedBucket(nbt);
        }

    }

    public Random random = new Random();

    public GreenHouseInfusedSeedBucket(ItemStack seed, int seedCount) {
        super(seed, seedCount, null);
    }

    public GreenHouseInfusedSeedBucket(NBTTagCompound nbt) {
        super(nbt);
    }

    @Override
    public boolean revalidate(IGreenHouse greenhouse) {
        return this.isValid();
    }

    @Override
    public String getNBTIdentifier() {
        return NBT_IDENTIFIER;
    }

    @Override
    public void addProgress(double multiplier, GreenHouseDropTable tracker) {
        if (!this.isValid()) return;

        Aspect aspect = ItemInfusedSeeds.getAspect(this.seed);
        if (aspect == null) return;

        HashMap<ItemStack, Integer> aspectDrops = AccessorAspectCropLootManager.getLootMap()
            .get(aspect);
        if (aspectDrops == null || aspectDrops.size() == 0) return; // no drops

        int totalDrop = this.seedCount;
        int ordoTendency = ItemInfusedSeeds.getAspectTendencies(this.seed)
            .getAmount(Aspect.ORDER);
        if (ordoTendency > 0) {
            double addNewDropChance = (double) ordoTendency / 75;
            double stopNewDropChance = 1.0 - addNewDropChance;

            double expectedAdditionalPerSeed = (stopNewDropChance > 0) ? addNewDropChance / stopNewDropChance : 0;
            double variancePerSeed = (stopNewDropChance > 0)
                ? addNewDropChance / (stopNewDropChance * stopNewDropChance)
                : 0;

            double averageAdditionalDrops = this.seedCount * expectedAdditionalPerSeed;
            double standardDeviationForAdditionalDrops = Math.sqrt(this.seedCount * variancePerSeed);
            int additionalDrops = Math.max(
                0,
                (int) Math.round(averageAdditionalDrops + standardDeviationForAdditionalDrops * random.nextGaussian()));
            totalDrop += additionalDrops;
        }

        if (aspectDrops.size() == 1) {
            // fast path, get first ItemStack of HashMap (as it's the only one)
            ItemStack item = aspectDrops.keySet()
                .iterator()
                .next();
            tracker.addDrop(item.copy(), multiplier * totalDrop * item.stackSize);
            return;
        }

        // 2+ drops from aspect, uses weights

        int lowestWeight = Integer.MAX_VALUE;
        int totalWeight = 0;
        for (Integer weight : aspectDrops.values()) {
            lowestWeight = Math.min(lowestWeight, weight);
            totalWeight += weight;
        }

        double chanceOfTheRarest = (double) lowestWeight / totalWeight;
        // > than 1 of the rarest per average AND calculation is not too expensive
        // OR too cheap for approximation
        if ((chanceOfTheRarest * totalDrop > 1 && totalDrop <= 128) || totalDrop < Math.max(16, aspectDrops.size())) {
            // slow approach, but if `totalDrop` is small enough
            // then it's better to simulate this way for accuracy

            HashMap<ItemStack, Integer> accumulated = new HashMap<>();
            for (int i = 0; i < totalDrop; i++) {
                int roll = random.nextInt(totalWeight);
                for (Map.Entry<ItemStack, Integer> kv : aspectDrops.entrySet()) {
                    int weight = kv.getValue();
                    roll -= weight;
                    if (roll > 0) continue;

                    ItemStack item = kv.getKey();
                    accumulated.merge(item, item.stackSize, Integer::sum);
                    break;
                }
            }

            for (Map.Entry<ItemStack, Integer> kv : accumulated.entrySet()) {
                tracker.addDrop(kv.getKey(), multiplier * kv.getValue());
            }
            return;
        }

        int remainingDrops = totalDrop;
        int remainingWeight = totalWeight;
        Iterator<Map.Entry<ItemStack, Integer>> it = aspectDrops.entrySet()
            .iterator();
        while (it.hasNext()) {
            Map.Entry<ItemStack, Integer> entry = it.next();
            ItemStack item = entry.getKey();
            int weight = entry.getValue();
            if (weight == 0) continue;
            if (!it.hasNext()) { // last one
                if (remainingDrops > 0) {
                    tracker.addDrop(item, multiplier * remainingDrops * item.stackSize);
                }
                break;
            }
            double relativeChance = (double) weight / remainingWeight;
            double averageDrops = remainingDrops * relativeChance;
            double standardDeviation = Math.sqrt(remainingDrops * relativeChance * (1 - relativeChance));
            int drops = (int) Math.round(averageDrops + standardDeviation * random.nextGaussian());
            // Math.clamp is introduced in java21 (idk if JABEL fixes it)
            drops = Math.max(0, Math.min(drops, remainingDrops));
            if (drops > 0) {
                tracker.addDrop(item, multiplier * drops * item.stackSize);
            }
            remainingDrops -= drops;
            remainingWeight -= weight;
            if (remainingDrops <= 0) break;
        }
    }

}
