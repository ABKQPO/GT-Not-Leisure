package com.science.gtnl.common.greenhouse;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.science.gtnl.api.IGreenHouse;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.GTLoggers;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.util.MultiblockTooltipBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public abstract class GreenHouseMode {

    public static int EIG_BALANCE_IC2_ACCELERATOR_TIER = VoltageIndex.EV;
    public static int EIG_BALANCE_REGULAR_MODE_MIN_TIER = VoltageIndex.EV;
    public static int CONFIGURATION_WINDOW_ID = 10;

    public abstract int getUIIndex();

    public abstract String getName();

    public abstract int getMinVoltageTier();

    public abstract int getMinGlassTier();

    public abstract int getStartingSlotCount();

    public abstract int getSlotPerTierMultiplier();

    public abstract int getSlotCount(int machineTier);

    public abstract int getSeedCapacityPerSlot();

    public abstract int getWeedEXMultiplier();

    public abstract int getMaxFertilizerUsagePerSeed();

    public abstract double getFertilizerBoost();

    public abstract MultiblockTooltipBuilder addTooltipInfo(MultiblockTooltipBuilder builder);

    /**
     * Used to resolve factory type to an identifier.
     */
    private final Object2ObjectOpenHashMap<String, IGreenHouseBucketFactory> factories;
    /**
     * A way to have other mods submit custom buckets that can be prioritized over our default buckets
     */
    private final ObjectArrayList<IGreenHouseBucketFactory> orderedFactories;

    public GreenHouseMode() {
        this.factories = new Object2ObjectOpenHashMap<>();
        this.orderedFactories = new ObjectArrayList<>();
    }

    /**
     * Adds a bucket factory to the EIG mode and gives it a low priority. Factories with using existing IDs will
     * overwrite each other.
     *
     * @param factory The bucket factory to add.
     */
    public void addLowPriorityFactory(IGreenHouseBucketFactory factory) {
        String factoryId = factory.getNBTIdentifier();
        dealWithDuplicateFactoryId(factoryId);
        // add factory as lowest priority
        this.factories.put(factoryId, factory);
        this.orderedFactories.add(factory);
    }

    /**
     * Adds a bucket factory to the EIG mode and gives it a high priority. Factories with using existing IDs will
     * overwrite each other.
     *
     * @param factory The bucket factory to add.
     */
    public void addHighPriorityFactory(IGreenHouseBucketFactory factory) {
        String factoryId = factory.getNBTIdentifier();
        dealWithDuplicateFactoryId(factoryId);
        // add factory as lowest priority
        this.factories.put(factoryId, factory);
        this.orderedFactories.add(0, factory);
    }

    /**
     * A standardized way to deal with duplicate factory type identifiers.
     *
     * @param factoryId The ID of the factory
     */
    private void dealWithDuplicateFactoryId(String factoryId) {
        if (this.factories.containsKey(factoryId)) {
            // TODO: Check with devs to see if they want a throw instead.
            GTLoggers.GT_FML_LOGGER.error("Duplicate EIG bucket index detected: {}", factoryId);
            // remove duplicate from ordered list
            this.orderedFactories.remove(this.factories.get(factoryId));
        }
    }

    /**
     * Attempts to create a new bucket from a given item. Returns if the item cannot be inserted into the EIG.
     *
     * @see IGreenHouseBucketFactory#tryCreateBucket(IGreenHouse, ItemStack)
     * @param greenhouse The {@link IGreenHouse} that will contain the seed.
     * @param input      The {@link ItemStack} for the input item.
     * @param maxConsume The maximum amount of items to consume.
     * @param simulate   Whether to actually consume the seed.
     * @return Null if no bucket could be created from the item.
     */
    public GreenHouseBucket tryCreateNewBucket(IGreenHouse greenhouse, ItemStack input, int maxConsume,
        boolean simulate) {
        // Validate inputs
        if (input == null) return null;
        maxConsume = Math.min(input.stackSize, maxConsume);
        if (maxConsume <= 0) return null;
        for (IGreenHouseBucketFactory factory : this.orderedFactories) {
            GreenHouseBucket bucket = factory.tryCreateBucket(greenhouse, input);
            if (bucket == null || !bucket.isValid()) continue;
            if (!simulate) input.stackSize--;
            maxConsume--;
            bucket.tryAddSeed(greenhouse, input, maxConsume, simulate);
            return bucket;
        }
        return null;
    }

    /**
     * Restores the buckets of an EIG for the given mode.
     *
     * @see IGreenHouseBucketFactory#restore(NBTTagCompound)
     * @param bucketNBTList The
     */
    public void restoreBuckets(NBTTagList bucketNBTList, List<GreenHouseBucket> loadTo) {
        for (int i = 0; i < bucketNBTList.tagCount(); i++) {
            // validate nbt
            NBTTagCompound bucketNBT = bucketNBTList.getCompoundTagAt(i);
            if (bucketNBT.hasNoTags()) {
                GTLoggers.GT_FML_LOGGER.error("Empty NBT bucket found in EIG NBT.");
                continue;
            }
            if (!bucketNBT.hasKey("type", 8)) {
                GTLoggers.GT_FML_LOGGER.error("Failed to identify bucket type in EIG NBT.");
                continue;
            }
            // identify bucket type
            String bucketType = bucketNBT.getString("type");
            IGreenHouseBucketFactory factory = factories.getOrDefault(bucketType, null);
            if (factory == null) {
                GTLoggers.GT_FML_LOGGER.error("Failed to find EIG bucket factory for type: {}", bucketType);
                continue;
            }
            // restore bucket
            loadTo.add(factory.restore(bucketNBT));
        }
    }

    public static class EIGMigrationHolder {

        public final ItemStack seed;
        public final ItemStack supportBlock;
        public final boolean useNoHumidity;
        public int count;
        public boolean isValid;

        public EIGMigrationHolder(NBTTagCompound nbt) {
            this.seed = ItemUtils.readItemStackFromNBT(nbt.getCompoundTag("input"));
            if (this.seed != null) {
                this.count = this.seed.stackSize;
                this.seed.stackSize = 1;
            }
            this.supportBlock = nbt.hasKey("undercrop", 10)
                ? ItemUtils.readItemStackFromNBT(nbt.getCompoundTag("undercrop"))
                : null;
            this.useNoHumidity = nbt.getBoolean("noHumidity");
            this.isValid = true;
        }

        public String getKey() {
            if (this.supportBlock == null) return seed.toString();
            return "(" + this.seed.toString() + "," + this.supportBlock + ")";
        }
    }

}
