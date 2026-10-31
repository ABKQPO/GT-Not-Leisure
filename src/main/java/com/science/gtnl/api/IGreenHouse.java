package com.science.gtnl.api;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.cropsnh.api.IGrowthRequirement;
import com.gtnewhorizon.cropsnh.api.IMachineGrowthRequirement;
import com.gtnewhorizon.cropsnh.api.IPotencyData;
import com.gtnewhorizon.cropsnh.api.ISeedData;
import com.gtnewhorizon.cropsnh.blocks.BlockAdvancedHarvestingUnit;
import com.gtnewhorizon.cropsnh.blocks.BlockFertilizerUnit;
import com.gtnewhorizon.cropsnh.blocks.BlockGrowthAccelerationUnit;
import com.gtnewhorizon.cropsnh.blocks.BlockSeedBed;
import com.gtnewhorizon.cropsnh.farming.registries.HydrationRegistry;
import com.gtnewhorizon.cropsnh.farming.requirements.SubSoilRequirement;
import com.gtnewhorizon.cropsnh.tileentity.TileEntityCropSticks;
import com.gtnewhorizon.cropsnh.utility.CropsNHUtils;
import com.gtnewhorizon.cropsnh.utility.IFDropTable;
import com.science.gtnl.common.greenhouse.GreenHouseModes;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseMode;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseStoredCrop;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseViewMode;

import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.IVoidable;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.ItemEjectionHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public interface IGreenHouse extends IVoidable {

    int CYCLE_DURATION = 5 * SECONDS;
    int SIMULATED_WATER_STORAGE = 200;
    int SIMULATED_FERTILIZER_STORAGE_WHEN_FERTILIZER_NOT_PROVIDED = 0;
    int SIMULATED_FERTILIZER_STORAGE_WHEN_FERTILIZER_PROVIDED = 200;
    boolean SIMULATED_CAN_SEE_SKY = true;

    int MODE_INPUT = 0;
    int MODE_FARM = 1;
    int MODE_OUTPUT = 2;

    CheckRecipeResult BLOCK_UNDER_MISMATCH_INPUT = SimpleCheckRecipeResult
        .ofFailure("gtnl.greenhouse.block_under_mismatch.input");
    CheckRecipeResult BLOCK_UNDER_MISMATCH_FARM = SimpleCheckRecipeResult
        .ofFailure("gtnl.greenhouse.block_under_mismatch.farm");
    CheckRecipeResult BLOCK_UNDER_NOT_FOUND = SimpleCheckRecipeResult
        .ofFailure("gtnl.greenhouse.block_under_not_found");
    CheckRecipeResult SEED_BED_TIER_TOO_LOW = SimpleCheckRecipeResult
        .ofFailure("gtnl.greenhouse.seed_bed_tier_too_low");
    CheckRecipeResult SEEDS_FULL = SimpleCheckRecipeResult.ofFailure("gtnl.greenhouse.seeds_full");
    CheckRecipeResult SEED_TYPES_FULL = SimpleCheckRecipeResult.ofFailure("gtnl.greenhouse.seed_types_full");
    CheckRecipeResult SEED_OVERFLOW = SimpleCheckRecipeResult.ofFailure("gtnl.greenhouse.seed_overflow");
    CheckRecipeResult CANNOT_GROW = SimpleCheckRecipeResult.ofFailure("gtnl.greenhouse.cannot_grow");
    CheckRecipeResult NOT_ENOUGH_WATER = SimpleCheckRecipeResult.ofFailure("gtnl.greenhouse.not_enough_water");
    CheckRecipeResult NOT_ENOUGH_FERTILIZER = SimpleCheckRecipeResult
        .ofFailure("gtnl.greenhouse.not_enough_fertilizer");

    IGregTechTileEntity getBaseMetaTileEntity();

    ArrayList<ItemStack> getStoredInputs();

    int getMaxProgressTime();

    void setMaxProgressTime(int time);

    /**
     * Length of one farm cycle. Crops grow for this long each cycle, so a longer cycle also yields more per cycle.
     *
     * @return the cycle length in ticks
     */
    default int getCycleDuration() {
        return CYCLE_DURATION;
    }

    void setLEUt(long lEUt);

    @Deprecated
    boolean isUseNoHumidity();

    @Deprecated
    void setUseNoHumidity(boolean useNoHumidity);

    @Deprecated
    int getSetupPhase();

    int getMaxSeedTypes();

    int getMaxSeedCount();

    @Deprecated
    void setSetupPhase(int setupPhase);

    @Deprecated
    GreenHouseMode getMode();

    @Deprecated
    void setMode(GreenHouseMode mode);

    ArrayList<? extends IOutputBus> getOutputBus();

    ArrayList<FluidStack> getStoredFluids();

    void updateSlots();

    int getWaterUsage();

    List<GreenHouseStoredCrop> getStoredCrops();

    IFDropTable getIndustrialFarmDropTracker();

    void setIndustrialFarmDropTracker(IFDropTable dropTracker);

    IFDropTable getIndustrialFarmGuiDropTracker();

    void setIndustrialFarmGuiDropTracker(IFDropTable dropTracker);

    int getMachineMode();

    void setMachineMode(int machineMode);

    int getIndustrialFarmTier();

    long getIndustrialFarmEUt();

    boolean shouldUseCurrentBiome();

    boolean forcesBestSeedStats();

    double getGreenHouseOutputMultiplier();

    default int getSimulatedWaterStorage(ISeedData seedData) {
        return SIMULATED_WATER_STORAGE;
    }

    default int getSimulatedFertilizerStorage(ISeedData seedData) {
        return SIMULATED_FERTILIZER_STORAGE_WHEN_FERTILIZER_PROVIDED;
    }

    default boolean canSimulateSkyAccess(ISeedData seedData) {
        return SIMULATED_CAN_SEE_SKY;
    }

    default double getCropDropChanceMultiplier(ISeedData seedData) {
        return 1.0d;
    }

    default double getDropTableChance(ISeedData seedData, ItemStack stack, int baseChance) {
        return baseChance / 10_000d;
    }

    default boolean supportsGreenHouseConfigurationPanel() {
        return false;
    }

    GreenHouseViewMode getGreenHouseViewMode();

    void setGreenHouseViewMode(GreenHouseViewMode mode);

    default boolean supportsBlockUnderView() {
        return true;
    }

    default boolean isGreenHouseStorageEditable() {
        return getMaxProgressTime() == 0 && !getBaseMetaTileEntity().isAllowedToWork();
    }

    default int getTotalStoredCropCount() {
        return getStoredCrops().stream()
            .mapToInt(GreenHouseStoredCrop::getSeedCount)
            .sum();
    }

    default int getEffectiveStoredCropCount() {
        int count = 0;
        for (GreenHouseStoredCrop crop : getStoredCrops()) {
            ISeedData seedData = CropsNHUtils.getSeedData(crop.getSeedStack(), false, true);
            count += getEffectiveStoredCropCount(crop, seedData);
        }
        return count;
    }

    default int getEffectiveStoredCropCount(GreenHouseStoredCrop crop, ISeedData seedData) {
        if (seedData == null) return 0;
        int seedCount = crop.getSeedCount();
        if (seedCount <= 0) return 0;
        if (!needsBlockUnder(seedData)) return seedCount;

        ItemStack blockUnder = crop.getBlockUnderStack();
        if (CropsNHUtils.isStackInvalid(blockUnder)) return 0;
        return Math.min(seedCount, blockUnder.stackSize);
    }

    default int getUsedBlockUnderCount() {
        return getStoredCrops().stream()
            .mapToInt(
                crop -> CropsNHUtils.isStackValid(crop.getBlockUnderStack()) ? crop.getBlockUnderStack().stackSize : 0)
            .sum();
    }

    default int getMissingBlockUnderCount() {
        int missing = 0;
        for (GreenHouseStoredCrop crop : getStoredCrops()) {
            ISeedData seedData = CropsNHUtils.getSeedData(crop.getSeedStack(), false, true);
            if (seedData == null || !needsBlockUnder(seedData)) continue;
            int blockCount = CropsNHUtils.isStackValid(crop.getBlockUnderStack()) ? crop.getBlockUnderStack().stackSize
                : 0;
            missing += Math.max(0, crop.getSeedCount() - blockCount);
        }
        return missing;
    }

    default int machineModeFromSetupPhase(int setupPhase) {
        return switch (setupPhase) {
            case 0 -> MODE_FARM;
            case 2 -> MODE_OUTPUT;
            default -> MODE_INPUT;
        };
    }

    default int setupPhaseFromMachineMode(int machineMode) {
        return switch (machineMode) {
            case MODE_FARM -> 0;
            case MODE_OUTPUT -> 2;
            default -> 1;
        };
    }

    default CheckRecipeResult checkProcessingInputMode() {
        if (getMaxSeedCount() <= 0) return CheckRecipeResultRegistry.NO_RECIPE;
        CheckRecipeResult firstFailure = CheckRecipeResultRegistry.NO_RECIPE;
        for (ItemStack input : getStoredInputs()) {
            if (CropsNHUtils.isStackInvalid(input)) continue;
            CheckRecipeResult result = tryAddCropStack(input, false);
            if (result.wasSuccessful()) {
                setMaxProgressTime(5);
                setLEUt(0);
                updateSlots();
                return result;
            }
            if (result != CheckRecipeResultRegistry.NO_RECIPE && firstFailure == CheckRecipeResultRegistry.NO_RECIPE) {
                firstFailure = result;
            }

            result = tryAddBlockUnderStack(input, false);
            if (result.wasSuccessful()) {
                setMaxProgressTime(5);
                setLEUt(0);
                updateSlots();
                return result;
            }
            if (result != CheckRecipeResultRegistry.NO_RECIPE && firstFailure == CheckRecipeResultRegistry.NO_RECIPE) {
                firstFailure = result;
            }
        }
        return firstFailure;
    }

    default CheckRecipeResult tryAddCropStack(ItemStack input, boolean simulate) {
        ISeedData seedData = CropsNHUtils.getSeedData(input, false, true);
        if (seedData == null) return CheckRecipeResultRegistry.NO_RECIPE;
        if (seedData.getCrop()
            .getMinSeedBedTier() > getIndustrialFarmTier()) return SEED_BED_TIER_TOO_LOW;
        if (getTotalStoredCropCount() >= getMaxSeedCount()) return SEEDS_FULL;

        GreenHouseStoredCrop existing = findStoredCrop(input);
        if (existing != null) {
            int consume = Math.min(input.stackSize, getMaxSeedCount() - getTotalStoredCropCount());
            if (consume <= 0) return SEEDS_FULL;
            ItemStack blockUnder = existing.getBlockUnderStack();
            if (CropsNHUtils.isStackValid(blockUnder)) {
                int availableBlockUnders = blockUnder.stackSize + countMatchingStacks(blockUnder, getStoredInputs());
                consume = Math.min(consume, availableBlockUnders - existing.getSeedCount());
                if (consume <= 0) return BLOCK_UNDER_NOT_FOUND;
                consumeMatchingStacks(blockUnder, getStoredInputs(), consume, simulate);
            }
            if (!simulate) {
                input.stackSize -= consume;
                existing.getSeedStack().stackSize += consume;
            }
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        if (getStoredCrops().size() >= getMaxSeedTypes()) return SEED_TYPES_FULL;

        ItemStack blockUnder = findRequiredBlockUnder(seedData);
        int consume = Math.min(input.stackSize, getMaxSeedCount() - getTotalStoredCropCount());
        if (blockUnder != null) {
            int availableBlockUnders = countMatchingStacks(blockUnder, getStoredInputs());
            consume = Math.min(consume, availableBlockUnders);
            if (consume <= 0) return BLOCK_UNDER_NOT_FOUND;
        }
        if (consume <= 0) return SEEDS_FULL;

        if (!simulate) {
            ItemStack storedSeed = CropsNHUtils.copyStackWithSize(input, consume);
            input.stackSize -= consume;
            ItemStack storedBlock = null;
            if (blockUnder != null) {
                storedBlock = CropsNHUtils.copyStackWithSize(blockUnder, 0);
                consumeMatchingStacks(storedBlock, getStoredInputs(), consume, false);
            }
            getStoredCrops().add(new GreenHouseStoredCrop(storedSeed, storedBlock));
        }
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    default CheckRecipeResult tryAddBlockUnderStack(ItemStack input, boolean simulate) {
        if (CropsNHUtils.isStackInvalid(input)) return CheckRecipeResultRegistry.NO_RECIPE;

        int remaining = input.stackSize;
        boolean inserted = false;
        for (GreenHouseStoredCrop crop : getStoredCrops()) {
            if (remaining <= 0) break;
            ISeedData seedData = CropsNHUtils.getSeedData(crop.getSeedStack(), false, true);
            if (seedData == null || !needsBlockUnder(seedData)) continue;
            int missing = getMissingBlockUnderCount(crop);
            if (missing <= 0) continue;

            ItemStack blockUnder = crop.getBlockUnderStack();
            if (CropsNHUtils.isStackValid(blockUnder)) {
                if (!GTUtility.areStacksEqual(blockUnder, input, false)) continue;
            } else {
                if (seedData == null || !isValidBlockUnder(seedData, input)) continue;
                if (!simulate) {
                    blockUnder = CropsNHUtils.copyStackWithSize(input, 0);
                    crop.setBlockUnderStack(blockUnder);
                }
            }

            int toInsert = Math.min(remaining, missing);
            remaining -= toInsert;
            inserted = true;
            if (!simulate) {
                input.stackSize -= toInsert;
                blockUnder.stackSize += toInsert;
            }
        }

        return inserted ? CheckRecipeResultRegistry.SUCCESSFUL : BLOCK_UNDER_NOT_FOUND;
    }

    default GreenHouseStoredCrop findStoredCrop(ItemStack input) {
        for (GreenHouseStoredCrop crop : getStoredCrops()) {
            if (crop.canStackSeeds(input)) return crop;
        }
        return null;
    }

    default boolean needsBlockUnder(ISeedData seedData) {
        return seedData.getCrop()
            .getGrowthRequirements()
            .stream()
            .anyMatch(SubSoilRequirement.class::isInstance);
    }

    default int getMissingBlockUnderCount(GreenHouseStoredCrop crop) {
        ItemStack blockUnder = crop.getBlockUnderStack();
        int blockCount = CropsNHUtils.isStackValid(blockUnder) ? blockUnder.stackSize : 0;
        return Math.max(0, crop.getSeedCount() - blockCount);
    }

    default boolean isValidBlockUnder(ISeedData seedData, ItemStack blockUnder) {
        if (CropsNHUtils.isStackInvalid(blockUnder)) return false;
        for (IGrowthRequirement requirement : seedData.getCrop()
            .getGrowthRequirements()) {
            if (requirement instanceof SubSoilRequirement subSoilRequirement
                && subSoilRequirement.isValidSubSoil(blockUnder)) {
                return true;
            }
        }
        return false;
    }

    default ItemStack findRequiredBlockUnder(ISeedData seedData) {
        for (IGrowthRequirement requirement : seedData.getCrop()
            .getGrowthRequirements()) {
            if (!(requirement instanceof SubSoilRequirement subSoilRequirement)) continue;
            for (ItemStack input : getStoredInputs()) {
                if (subSoilRequirement.isValidSubSoil(input)) {
                    ItemStack result = input.copy();
                    result.stackSize = 0;
                    return result;
                }
            }
            return null;
        }
        return null;
    }

    default int countMatchingStacks(ItemStack target, List<ItemStack> provider) {
        int count = 0;
        for (ItemStack stack : provider) {
            if (CropsNHUtils.isStackValid(stack) && GTUtility.areStacksEqual(stack, target, false)) {
                count += stack.stackSize;
            }
        }
        return count;
    }

    default int consumeMatchingStacks(ItemStack target, List<ItemStack> provider, int amount, boolean simulate) {
        if (CropsNHUtils.isStackInvalid(target) || amount <= 0) return 0;
        int consumed = 0;
        for (ItemStack stack : provider) {
            if (consumed >= amount) break;
            if (CropsNHUtils.isStackInvalid(stack) || !GTUtility.areStacksEqual(stack, target, false)) continue;
            int toConsume = Math.min(amount - consumed, stack.stackSize);
            consumed += toConsume;
            if (!simulate) {
                stack.stackSize -= toConsume;
            }
        }
        if (!simulate) {
            target.stackSize += consumed;
        }
        return consumed;
    }

    default CheckRecipeResult checkProcessingOutputMode() {
        int maxRemovable = getTotalStoredCropCount();
        if (maxRemovable <= 0) return CheckRecipeResultRegistry.NO_RECIPE;

        int removable = getMaxOutputtableStoredCropCount(maxRemovable);
        if (removable <= 0) return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;

        List<ItemStack> outputs = createStoredCropOutputs(removable);
        ItemEjectionHelper ejectionHelper = new ItemEjectionHelper(new ObjectArrayList<>(getOutputBus()), true);
        if (ejectionHelper.ejectItems(outputs, 1) <= 0) return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
        ejectionHelper.commit();

        int remaining = removable;
        for (Iterator<GreenHouseStoredCrop> iterator = getStoredCrops().iterator(); iterator.hasNext()
            && remaining > 0;) {
            GreenHouseStoredCrop crop = iterator.next();
            int removed = Math.min(remaining, crop.getSeedCount());
            crop.removeSeeds(removed);
            crop.removeBlockUnders(removed);
            remaining -= removed;
            crop.clearIfEmpty();
            if (crop.getSeedCount() <= 0) {
                iterator.remove();
            }
        }
        setMaxProgressTime(5);
        setLEUt(0);
        updateSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    default int getMaxOutputtableStoredCropCount(int maxRemovable) {
        int low = 0;
        int high = maxRemovable;
        while (low < high) {
            int middle = low + (high - low + 1) / 2;
            if (canOutputStoredCrops(middle)) {
                low = middle;
            } else {
                high = middle - 1;
            }
        }
        return low;
    }

    default boolean canOutputStoredCrops(int amount) {
        if (amount <= 0) return false;
        List<ItemStack> outputs = createStoredCropOutputs(amount);
        if (outputs.isEmpty()) return false;
        ItemEjectionHelper ejectionHelper = new ItemEjectionHelper(new ObjectArrayList<>(getOutputBus()), true);
        return ejectionHelper.ejectItems(outputs, 1) > 0;
    }

    default List<ItemStack> createStoredCropOutputs(int amount) {
        List<ItemStack> outputs = new ObjectArrayList<>();
        int remaining = amount;
        for (GreenHouseStoredCrop crop : getStoredCrops()) {
            if (remaining <= 0) break;
            int seedCount = Math.min(remaining, crop.getSeedCount());
            if (seedCount <= 0) continue;

            ItemStack seed = crop.getSeedStack();
            if (CropsNHUtils.isStackValid(seed)) {
                outputs.add(CropsNHUtils.copyStackWithSize(seed, seedCount));
            }

            ItemStack blockUnder = crop.getBlockUnderStack();
            if (CropsNHUtils.isStackValid(blockUnder)) {
                int blockCount = Math.min(seedCount, blockUnder.stackSize);
                if (blockCount > 0) {
                    outputs.add(CropsNHUtils.copyStackWithSize(blockUnder, blockCount));
                }
            }
            remaining -= seedCount;
        }
        return outputs;
    }

    default CheckRecipeResult checkProcessingFarmMode() {
        if (getStoredCrops().isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;
        if (getTotalStoredCropCount() > getMaxSeedCount()) return SEED_OVERFLOW;

        IFDropTable cycleDrops = new IFDropTable();
        int activeSeedCount = 0;
        boolean skippedBlockUnderCrop = false;
        for (GreenHouseStoredCrop storedCrop : getStoredCrops()) {
            ISeedData seedData = createRuntimeSeedData(storedCrop.getSeedStack());
            if (seedData == null) continue;

            int effectiveSeedCount = getEffectiveStoredCropCount(storedCrop, seedData);
            if (effectiveSeedCount <= 0) {
                if (needsBlockUnder(seedData)) {
                    skippedBlockUnderCrop = true;
                }
                continue;
            }

            CheckRecipeResult canGrow = validateCanGrow(seedData, storedCrop);
            if (!canGrow.wasSuccessful()) {
                if (canGrow == BLOCK_UNDER_MISMATCH_FARM) {
                    skippedBlockUnderCrop = true;
                    continue;
                }
                return canGrow;
            }

            IFDropTable drops = getDropsPerCycle(seedData);
            if (drops == null) return CANNOT_GROW;
            drops.addTo(cycleDrops, effectiveSeedCount * getGreenHouseOutputMultiplier());
            activeSeedCount += effectiveSeedCount;
        }
        if (activeSeedCount <= 0) {
            return skippedBlockUnderCrop ? BLOCK_UNDER_MISMATCH_FARM : CheckRecipeResultRegistry.NO_RECIPE;
        }

        List<ObjectIntPair<FluidStack>> waterToConsume = new ObjectArrayList<>();
        int waterMissing = getWaterUsage();
        for (FluidStack fluidStack : getStoredFluids()) {
            if (CropsNHUtils.isStackInvalid(fluidStack)) continue;
            IPotencyData potency = HydrationRegistry.instance.getPotency(fluidStack.getFluid());
            if (potency == null) continue;
            int applications = getApplicationCountBasedOnPotency(waterMissing, potency, fluidStack.amount);
            if (applications > 0) {
                waterMissing -= applications * potency.getPotency();
                waterToConsume
                    .add(ObjectIntPair.of(fluidStack, applications * potency.getUnitsConsumedPerApplication()));
            }
            if (waterMissing <= 0) break;
        }
        if (waterMissing > 0) return NOT_ENOUGH_WATER;

        cycleDrops.addTo(getIndustrialFarmDropTracker());
        if (getVoidingMode().protectItem) {
            ItemEjectionHelper helper = new ItemEjectionHelper(new ObjectArrayList<>(getOutputBus()), true);
            ItemStack[] drops = getIndustrialFarmDropTracker().getDrops(true);
            if (drops.length != 0 && helper.ejectItems(Arrays.asList(drops), 1) <= 0) {
                cycleDrops.addTo(getIndustrialFarmDropTracker(), -1.0d);
                return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
            }
        }
        for (ObjectIntPair<FluidStack> pair : waterToConsume) pair.left().amount -= pair.rightInt();
        setIndustrialFarmGuiDropTracker(cycleDrops);
        setGreenHouseOutputItems(getIndustrialFarmDropTracker().getDrops(false));
        setLEUt(-getIndustrialFarmEUt());
        setMaxProgressTime(getCycleDuration());
        updateSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    boolean addItemOutputsToGreenHouse(ItemStack[] outputs);

    void setGreenHouseOutputItems(ItemStack[] outputs);

    default ISeedData createRuntimeSeedData(ItemStack seedStack) {
        return CropsNHUtils.getSeedData(seedStack, false, true);
    }

    default CheckRecipeResult validateCanGrow(ISeedData seedData, GreenHouseStoredCrop crop) {
        if (seedData.getStack().stackSize > getMaxSeedCount()) return SEED_OVERFLOW;
        if (seedData.getCrop()
            .getMinSeedBedTier() > getIndustrialFarmTier()) return SEED_BED_TIER_TOO_LOW;

        ItemStack[] catalysts = CropsNHUtils.isStackValid(crop.getBlockUnderStack())
            ? new ItemStack[] { crop.getBlockUnderStack() }
            : new ItemStack[0];
        for (IGrowthRequirement requirement : seedData.getCrop()
            .getGrowthRequirements()) {
            if (requirement instanceof IMachineGrowthRequirement machineGrowthRequirement
                && !machineGrowthRequirement.canGrow(seedData, getBaseMetaTileEntity(), catalysts)) {
                if (requirement instanceof SubSoilRequirement) return BLOCK_UNDER_MISMATCH_FARM;
                return CANNOT_GROW;
            }
        }
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    default int getApplicationCountBasedOnPotency(int missingPotency, IPotencyData potency, int inputAmount) {
        int perApplication = potency.getPotency();
        int unitsPerApplication = potency.getUnitsConsumedPerApplication();
        if (missingPotency <= 0 || perApplication <= 0 || inputAmount < unitsPerApplication) return 0;
        int maxApplications = missingPotency / perApplication + (missingPotency % perApplication > 0 ? 1 : 0);
        return Math.min(maxApplications, inputAmount / unitsPerApplication);
    }

    default int getNutrientScore(ISeedData seedData) {
        if (seedData == null) return 0;
        if (!shouldUseCurrentBiome()) return TileEntityCropSticks.MAX_NUTRIENT_SCORE;
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null) return 0;
        BiomeGenBase biome = base.getBiome();
        Set<BiomeDictionary.Type> biomeTags = new ObjectOpenHashSet<>(
            Arrays.asList(BiomeDictionary.getTypesForBiome(biome)));
        int likedBiomes = (int) seedData.getCrop()
            .getLikedBiomeTags()
            .stream()
            .filter(biomeTags::contains)
            .count();
        return TileEntityCropSticks.getNutrientsPerCycle(
            likedBiomes,
            biome.rainfall,
            canSimulateSkyAccess(seedData),
            getSimulatedWaterStorage(seedData),
            getSimulatedFertilizerStorage(seedData));
    }

    default int getGrowthSpeedUnscaled(ISeedData seedData) {
        return TileEntityCropSticks.getGrowthRate(
            getNutrientScore(seedData),
            seedData.getCrop()
                .getTier(),
            seedData.getStats()
                .getGrowth());
    }

    default double getGrowthSpeedMultiplier() {
        return 1.0d + BlockGrowthAccelerationUnit.GROWTH_SPEED_BONUS;
    }

    default double getGrowthProgressPerCycle(ISeedData seedData) {
        int growthSpeed = getGrowthSpeedUnscaled(seedData);
        if (growthSpeed <= 0) return -1;
        int duration = seedData.getCrop()
            .getGrowthDuration();
        int growthTicksPerHarvest = duration / growthSpeed + (duration % growthSpeed == 0 ? 0 : 1);
        double growthPercentPerGrowthTick = 1.0d / growthTicksPerHarvest;
        return growthPercentPerGrowthTick * ((double) getCycleDuration() / TileEntityCropSticks.TICK_RATE)
            * getGrowthSpeedMultiplier();
    }

    default double getHarvestRoundMultiplier() {
        double multiplier = 1.0d;
        multiplier += BlockSeedBed.getHarvestRoundBonus(getIndustrialFarmTier());
        multiplier += BlockFertilizerUnit.HARVEST_ROUND_BONUS;
        multiplier *= 1.0d + BlockAdvancedHarvestingUnit.HARVEST_ROUND_MULTIPLIER;
        return multiplier;
    }

    default @Nullable IFDropTable getDropsPerCycle(ISeedData seedData) {
        double progressPerCycle = getGrowthProgressPerCycle(seedData);
        if (progressPerCycle <= 0) return null;
        double avgDropIncrease = TileEntityCropSticks.getAvgDropCountIncrease(
            seedData.getStats()
                .getGain());
        double avgDropCount = TileEntityCropSticks.getAvgDropRounds(
            seedData.getCrop(),
            seedData.getStats()
                .getGain());
        avgDropCount *= getCropDropChanceMultiplier(seedData);
        avgDropCount *= getHarvestRoundMultiplier();

        IFDropTable drops = new IFDropTable();
        for (Map.Entry<ItemStack, Integer> entry : seedData.getCrop()
            .getDropTable()
            .entrySet()) {
            ItemStack stack = entry.getKey();
            double chance = getDropTableChance(seedData, stack, entry.getValue());
            double unscaled = (stack.stackSize + avgDropIncrease) * chance * avgDropCount;
            drops.addDrop(stack, unscaled * progressPerCycle);
        }
        return drops;
    }

    default void tryChangeMode(EntityPlayer aPlayer) {
        if (this.getMaxProgressTime() > 0) {
            GTUtility.sendChatTrans(aPlayer, "gtnl.machine.eden_garden.message.mode_running");
            return;
        }
        if (!this.getStoredCrops()
            .isEmpty()) {
            GTUtility.sendChatTrans(aPlayer, "gtnl.machine.eden_garden.message.mode_has_seeds");
            return;
        }
        this.setMode(GreenHouseModes.getNextMode(this.getMode()));
        GTUtility.sendChatTrans(
            aPlayer,
            "gtnl.machine.eden_garden.message.mode_changed",
            this.getMode()
                .getName());
    }

    default void tryChangeSetupPhase(EntityPlayer aPlayer) {
        if (this.getMaxProgressTime() > 0) {
            GTUtility.sendChatTrans(aPlayer, "gtnl.machine.eden_garden.message.setup_mode_running");
            return;
        }
        int nextSetupPhase = this.getSetupPhase() + 1;
        if (nextSetupPhase == 3) nextSetupPhase = 0;
        this.setSetupPhase(nextSetupPhase);
        this.setMachineMode(machineModeFromSetupPhase(nextSetupPhase));

        String phaseKey = switch (nextSetupPhase) {
            case 0 -> "gtnl.machine.eden_garden.mode.operating";
            case 1 -> "gtnl.machine.eden_garden.mode.input";
            case 2 -> "gtnl.machine.eden_garden.mode.output";
            default -> "gtnl.machine.eden_garden.message.invalid_setup_phase";
        };

        GTUtility.sendChatTrans(
            aPlayer,
            "gtnl.machine.eden_garden.message.setup_phase_changed.format",
            new ChatComponentTranslation(phaseKey));
    }

    @Deprecated
    default void tryChangeHumidityMode(EntityPlayer aPlayer) {
        // TODO: Remove this legacy humidity toggle after greenhouse biome handling is fully CropsNH based.
        this.setUseNoHumidity(!this.isUseNoHumidity());
        if (this.isUseNoHumidity()) {
            GTUtility.sendChatTrans(aPlayer, "gtnl.machine.eden_garden.message.no_humidity_enabled");
        } else {
            GTUtility.sendChatTrans(aPlayer, "gtnl.machine.eden_garden.message.no_humidity_disabled");
        }
    }

}
