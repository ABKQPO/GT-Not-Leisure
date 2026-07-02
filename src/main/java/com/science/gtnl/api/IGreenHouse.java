package com.science.gtnl.api;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.cropsnh.api.IGrowthRequirement;
import com.gtnewhorizon.cropsnh.api.IMachineGrowthRequirement;
import com.gtnewhorizon.cropsnh.api.ISeedData;
import com.gtnewhorizon.cropsnh.blocks.BlockAdvancedHarvestingUnit;
import com.gtnewhorizon.cropsnh.blocks.BlockFertilizerUnit;
import com.gtnewhorizon.cropsnh.blocks.BlockGrowthAccelerationUnit;
import com.gtnewhorizon.cropsnh.blocks.BlockSeedBed;
import com.gtnewhorizon.cropsnh.farming.registries.HydrationRegistry;
import com.gtnewhorizon.cropsnh.farming.requirements.BlockUnderRequirement;
import com.gtnewhorizon.cropsnh.tileentity.TileEntityCropSticks;
import com.gtnewhorizon.cropsnh.utility.CropsNHUtils;
import com.gtnewhorizon.cropsnh.utility.IFDropTable;
import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.Column;
import com.gtnewhorizons.modularui.common.widget.CycleButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseMode;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseModes;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseStoredCrop;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseViewMode;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.IVoidable;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.ItemEjectionHelper;

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
        .ofFailure("GTNL.greenhouse.blockUnderMismatch.input");
    CheckRecipeResult BLOCK_UNDER_MISMATCH_FARM = SimpleCheckRecipeResult
        .ofFailure("GTNL.greenhouse.blockUnderMismatch.farm");
    CheckRecipeResult BLOCK_UNDER_NOT_FOUND = SimpleCheckRecipeResult.ofFailure("GTNL.greenhouse.blockUnderNotFound");
    CheckRecipeResult SEED_BED_TIER_TOO_LOW = SimpleCheckRecipeResult.ofFailure("GTNL.greenhouse.seedBedTierTooLow");
    CheckRecipeResult SEEDS_FULL = SimpleCheckRecipeResult.ofFailure("GTNL.greenhouse.seedsFull");
    CheckRecipeResult SEED_TYPES_FULL = SimpleCheckRecipeResult.ofFailure("GTNL.greenhouse.seedTypesFull");
    CheckRecipeResult SEED_OVERFLOW = SimpleCheckRecipeResult.ofFailure("GTNL.greenhouse.seedOverflow");
    CheckRecipeResult CANNOT_GROW = SimpleCheckRecipeResult.ofFailure("GTNL.greenhouse.cannotGrow");
    CheckRecipeResult NOT_ENOUGH_WATER = SimpleCheckRecipeResult.ofFailure("GTNL.greenhouse.notEnoughWater");
    CheckRecipeResult NOT_ENOUGH_FERTILIZER = SimpleCheckRecipeResult.ofFailure("GTNL.greenhouse.notEnoughFertilizer");

    IGregTechTileEntity getBaseMetaTileEntity();

    ArrayList<ItemStack> getStoredInputs();

    int getMaxProgressTime();

    void setMaxProgressTime(int time);

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

    default int getUsedBlockUnderCount() {
        return getStoredCrops().stream()
            .mapToInt(crop -> crop.hasBlockUnder() ? crop.getSeedCount() : 0)
            .sum();
    }

    default CheckRecipeResult processIndustrialFarmMode() {
        return switch (getMachineMode()) {
            case MODE_INPUT -> {
                getIndustrialFarmDropTracker().clear();
                yield checkProcessingInputMode();
            }
            case MODE_FARM -> {
                CheckRecipeResult result = checkProcessingFarmMode();
                if (!result.wasSuccessful()) {
                    getIndustrialFarmDropTracker().clear();
                }
                yield result;
            }
            case MODE_OUTPUT -> {
                getIndustrialFarmDropTracker().clear();
                yield checkProcessingOutputMode();
            }
            default -> CheckRecipeResultRegistry.NO_RECIPE;
        };
    }

    default CheckRecipeResult checkProcessingInputMode() {
        if (getMaxSeedCount() <= 0) return CheckRecipeResultRegistry.NO_RECIPE;
        for (ItemStack input : getStoredInputs()) {
            if (CropsNHUtils.isStackInvalid(input)) continue;
            CheckRecipeResult result = tryAddCropStack(input, false);
            if (result.wasSuccessful()) {
                setMaxProgressTime(5);
                setLEUt(0);
                updateSlots();
                return result;
            }
            if (result != CheckRecipeResultRegistry.NO_RECIPE) return result;
        }
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    default CheckRecipeResult tryAddCropStack(ItemStack input, boolean simulate) {
        ISeedData seedData = CropsNHUtils.getAnalyzedSeedData(input);
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
                if (!simulate) blockUnder.stackSize += consume;
            }
            if (!simulate) {
                input.stackSize -= consume;
                existing.getSeedStack().stackSize += consume;
            }
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        if (getStoredCrops().size() >= getMaxSeedTypes()) return SEED_TYPES_FULL;

        ItemStack blockUnder = findRequiredBlockUnder(seedData);
        if (blockUnder == null && needsBlockUnder(seedData)) return BLOCK_UNDER_NOT_FOUND;
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
            .anyMatch(BlockUnderRequirement.class::isInstance);
    }

    default ItemStack findRequiredBlockUnder(ISeedData seedData) {
        for (IGrowthRequirement requirement : seedData.getCrop()
            .getGrowthRequirements()) {
            if (!(requirement instanceof BlockUnderRequirement blockUnderRequirement)) continue;
            for (ItemStack input : getStoredInputs()) {
                if (blockUnderRequirement.isValidBlockUnder(input)) {
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
        List<ItemStack> simulated = new ArrayList<>();
        for (GreenHouseStoredCrop crop : getStoredCrops()) {
            if (CropsNHUtils.isStackValid(crop.getSeedStack())) {
                simulated.add(CropsNHUtils.copyStackWithSize(crop.getSeedStack(), 1));
            }
            if (CropsNHUtils.isStackValid(crop.getBlockUnderStack())) {
                simulated.add(CropsNHUtils.copyStackWithSize(crop.getBlockUnderStack(), 1));
            }
        }
        if (simulated.isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;

        int maxParallels = getStoredCrops().stream()
            .mapToInt(GreenHouseStoredCrop::getSeedCount)
            .sum();
        ItemEjectionHelper ejectionHelper = new ItemEjectionHelper(new ArrayList<>(getOutputBus()), true);
        maxParallels = ejectionHelper.ejectItems(simulated, maxParallels);
        if (maxParallels <= 0) return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;

        int remaining = maxParallels;
        for (Iterator<GreenHouseStoredCrop> iterator = getStoredCrops().iterator(); iterator.hasNext()
            && remaining > 0;) {
            GreenHouseStoredCrop crop = iterator.next();
            int removed = Math.min(remaining, crop.getSeedCount());
            ItemStack seed = crop.removeSeeds(removed);
            ItemStack block = crop.removeBlockUnders(removed);
            if (seed != null) addItemOutputsToGreenHouse(new ItemStack[] { seed });
            if (block != null) addItemOutputsToGreenHouse(new ItemStack[] { block });
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

    default CheckRecipeResult checkProcessingFarmMode() {
        if (getStoredCrops().isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;
        if (getTotalStoredCropCount() > getMaxSeedCount()) return SEED_OVERFLOW;

        List<Pair<FluidStack, Integer>> waterToConsume = new ArrayList<>();
        int waterMissing = getWaterUsage();
        for (FluidStack fluidStack : getStoredFluids()) {
            if (CropsNHUtils.isStackInvalid(fluidStack)) continue;
            int potency = HydrationRegistry.instance.getPotency(fluidStack.getFluid());
            int amount = getAmountToConsumeBasedOnPotency(waterMissing, potency, fluidStack.amount);
            if (amount > 0) {
                waterMissing -= amount * potency;
                waterToConsume.add(Pair.of(fluidStack, amount));
            }
            if (waterMissing <= 0) break;
        }
        if (waterMissing > 0) return NOT_ENOUGH_WATER;

        IFDropTable cycleDrops = new IFDropTable();
        for (GreenHouseStoredCrop storedCrop : getStoredCrops()) {
            ISeedData seedData = createRuntimeSeedData(storedCrop.getSeedStack());
            if (seedData == null) return CheckRecipeResultRegistry.NO_RECIPE;
            CheckRecipeResult canGrow = validateCanGrow(seedData, storedCrop);
            if (!canGrow.wasSuccessful()) return canGrow;
            IFDropTable drops = getDropsPerCycle(seedData);
            if (drops == null) return CANNOT_GROW;
            drops.addTo(cycleDrops, storedCrop.getSeedCount() * getGreenHouseOutputMultiplier());
        }

        cycleDrops.addTo(getIndustrialFarmDropTracker());
        if (getVoidingMode().protectItem) {
            ItemEjectionHelper helper = new ItemEjectionHelper(new ArrayList<>(getOutputBus()), true);
            ItemStack[] drops = getIndustrialFarmDropTracker().getDrops(true);
            if (drops.length != 0 && helper.ejectItems(Arrays.asList(drops), 1) <= 0) {
                cycleDrops.addTo(getIndustrialFarmDropTracker(), -1.0d);
                return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
            }
        }
        waterToConsume.forEach(pair -> pair.getLeft().amount -= pair.getRight());
        setIndustrialFarmGuiDropTracker(cycleDrops);
        setGreenHouseOutputItems(getIndustrialFarmDropTracker().getDrops(false));
        setLEUt(-getIndustrialFarmEUt());
        setMaxProgressTime(CYCLE_DURATION);
        updateSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    boolean addItemOutputsToGreenHouse(ItemStack[] outputs);

    void setGreenHouseOutputItems(ItemStack[] outputs);

    default ISeedData createRuntimeSeedData(ItemStack seedStack) {
        return CropsNHUtils.getAnalyzedSeedData(seedStack);
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
                if (requirement instanceof BlockUnderRequirement) return BLOCK_UNDER_MISMATCH_FARM;
                return CANNOT_GROW;
            }
        }
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    default int getAmountToConsumeBasedOnPotency(int missingPotency, int inputPotency, int inputAmount) {
        if (missingPotency <= 0 || inputPotency <= 0 || inputAmount <= 0) return 0;
        int maxConsume = missingPotency / inputPotency + (missingPotency % inputPotency > 0 ? 1 : 0);
        return Math.min(maxConsume, inputAmount);
    }

    default int getNutrientScore(ISeedData seedData) {
        if (seedData == null) return 0;
        if (!shouldUseCurrentBiome()) return TileEntityCropSticks.MAX_NUTRIENT_SCORE;
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null) return 0;
        BiomeGenBase biome = base.getBiome();
        Set<BiomeDictionary.Type> biomeTags = new HashSet<>(Arrays.asList(BiomeDictionary.getTypesForBiome(biome)));
        int likedBiomes = (int) seedData.getCrop()
            .getLikedBiomeTags()
            .stream()
            .filter(biomeTags::contains)
            .count();
        return TileEntityCropSticks.getNutrientsPerCycle(
            likedBiomes,
            biome.rainfall,
            SIMULATED_CAN_SEE_SKY,
            SIMULATED_WATER_STORAGE,
            SIMULATED_FERTILIZER_STORAGE_WHEN_FERTILIZER_PROVIDED);
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
        return growthPercentPerGrowthTick * ((double) CYCLE_DURATION / TileEntityCropSticks.TICK_RATE)
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
        avgDropCount *= getHarvestRoundMultiplier();

        IFDropTable drops = new IFDropTable();
        for (Map.Entry<ItemStack, Integer> entry : seedData.getCrop()
            .getDropTable()
            .entrySet()) {
            ItemStack stack = entry.getKey();
            double chance = entry.getValue() / 10_000d;
            double unscaled = (stack.stackSize + avgDropIncrease) * chance * avgDropCount;
            drops.addDrop(stack, unscaled * progressPerCycle);
        }
        return drops;
    }

    @Deprecated
    default ModularWindow createConfigurationWindow(final EntityPlayer player) {
        // TODO: Remove this MUI1 configuration window after greenhouse machines use the MUI2 panel.
        ModularWindow.Builder builder = ModularWindow.builder(200, 100);
        builder.setBackground(ModularUITextures.VANILLA_BACKGROUND);
        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.OVERLAY_BUTTON_CYCLIC)
                .setPos(5, 5)
                .setSize(16, 16))
            .widget(new TextWidget(StatCollector.translateToLocal("Info_EdenGarden_Configuration")).setPos(25, 9))
            .widget(
                ButtonWidget.closeWindowButton(true)
                    .setPos(185, 3))
            .widget(
                new Column().widget(
                    new CycleButtonWidget().setLength(3)
                        .setGetter(this::getSetupPhase)
                        .setSetter(val -> {
                            if (!(player instanceof EntityPlayerMP)) return;
                            tryChangeSetupPhase(player);
                        })
                        .addTooltip(
                            0,
                            new Text(StatCollector.translateToLocal("Info_EdenGarden_Operating"))
                                .color(Color.GREEN.dark(3)))
                        .addTooltip(
                            1,
                            new Text(StatCollector.translateToLocal("Info_EdenGarden_Input"))
                                .color(Color.YELLOW.dark(3)))
                        .addTooltip(
                            2,
                            new Text(StatCollector.translateToLocal("Info_EdenGarden_Output"))
                                .color(Color.YELLOW.dark(3)))
                        .setTextureGetter(
                            i -> i == 0
                                ? new Text(StatCollector.translateToLocal("Info_EdenGarden_Operating"))
                                    .color(Color.GREEN.dark(3))
                                    .withFixedSize(70 - 18, 18, 15, 0)
                                : i == 1
                                    ? new Text(StatCollector.translateToLocal("Info_EdenGarden_Input"))
                                        .color(Color.YELLOW.dark(3))
                                        .withFixedSize(70 - 18, 18, 15, 0)
                                    : new Text(StatCollector.translateToLocal("Info_EdenGarden_Output"))
                                        .color(Color.YELLOW.dark(3))
                                        .withFixedSize(70 - 18, 18, 15, 0))
                        .setBackground(
                            ModularUITextures.VANILLA_BACKGROUND,
                            GTUITextures.OVERLAY_BUTTON_CYCLIC.withFixedSize(18, 18))
                        .setSize(70, 18)
                        .addTooltip(StatCollector.translateToLocal("Info_EdenGarden_SetupMode")))
                    .widget(
                        new CycleButtonWidget().setLength(2)
                            .setGetter(
                                () -> this.getMode()
                                    .getUIIndex())
                            .setSetter(val -> {
                                if (!(player instanceof EntityPlayerMP)) return;
                                tryChangeMode(player);
                            })
                            .addTooltip(
                                0,
                                new Text(StatCollector.translateToLocal("Info_EdenGarden_Disabled"))
                                    .color(Color.RED.dark(3)))
                            .addTooltip(
                                1,
                                new Text(StatCollector.translateToLocal("Info_EdenGarden_Enabled"))
                                    .color(Color.GREEN.dark(3)))
                            .setTextureGetter(
                                i -> i == 0
                                    ? new Text(StatCollector.translateToLocal("Info_EdenGarden_Disabled"))
                                        .color(Color.RED.dark(3))
                                        .withFixedSize(70 - 18, 18, 15, 0)
                                    : new Text(StatCollector.translateToLocal("Info_EdenGarden_Enabled"))
                                        .color(Color.GREEN.dark(3))
                                        .withFixedSize(70 - 18, 18, 15, 0))
                            .setBackground(
                                ModularUITextures.VANILLA_BACKGROUND,
                                GTUITextures.OVERLAY_BUTTON_CYCLIC.withFixedSize(18, 18))
                            .setSize(70, 18)
                            .addTooltip(StatCollector.translateToLocal("Info_EdenGarden_IC2Mode")))
                    .widget(
                        new CycleButtonWidget().setLength(2)
                            .setGetter(() -> isUseNoHumidity() ? 1 : 0)
                            .setSetter(val -> {
                                if (!(player instanceof EntityPlayerMP)) return;
                                this.tryChangeHumidityMode(player);
                            })
                            .addTooltip(
                                0,
                                new Text(StatCollector.translateToLocal("Info_EdenGarden_Disabled"))
                                    .color(Color.RED.dark(3)))
                            .addTooltip(
                                1,
                                new Text(StatCollector.translateToLocal("Info_EdenGarden_Enabled"))
                                    .color(Color.GREEN.dark(3)))
                            .setTextureGetter(
                                i -> i == 0
                                    ? new Text(StatCollector.translateToLocal("Info_EdenGarden_Disabled"))
                                        .color(Color.RED.dark(3))
                                        .withFixedSize(70 - 18, 18, 15, 0)
                                    : new Text(StatCollector.translateToLocal("Info_EdenGarden_Enabled"))
                                        .color(Color.GREEN.dark(3))
                                        .withFixedSize(70 - 18, 18, 15, 0))
                            .setBackground(
                                ModularUITextures.VANILLA_BACKGROUND,
                                GTUITextures.OVERLAY_BUTTON_CYCLIC.withFixedSize(18, 18))
                            .setSize(70, 18)
                            .addTooltip(StatCollector.translateToLocal("Info_EdenGarden_NoHumidityMode")))
                    .setEnabled(widget -> !getBaseMetaTileEntity().isActive())
                    .setPos(10, 30))
            .widget(
                new Column()
                    .widget(
                        new TextWidget(StatCollector.translateToLocal("Info_EdenGarden_SetupMode")).setSize(100, 18))
                    .widget(new TextWidget(StatCollector.translateToLocal("Info_EdenGarden_IC2Mode")).setSize(100, 18))
                    .widget(
                        new TextWidget(StatCollector.translateToLocal("Info_EdenGarden_NoHumidityMode"))
                            .setSize(100, 18))
                    .setEnabled(widget -> !getBaseMetaTileEntity().isActive())
                    .setPos(80, 30))
            .widget(
                new DrawableWidget().setDrawable(GTUITextures.OVERLAY_BUTTON_CROSS)
                    .setSize(18, 18)
                    .setPos(10, 30)
                    .addTooltip(new Text("Can't change configuration when running !").color(Color.RED.dark(3)))
                    .setEnabled(widget -> getBaseMetaTileEntity().isActive()));
        return builder.build();
    }

    @Deprecated
    default void tryChangeMode(EntityPlayer aPlayer) {
        // TODO: Remove this legacy greenhouse mode toggle after the MUI2 machine mode path replaces it.
        if (this.getMaxProgressTime() > 0) {
            GTUtility.sendChatTrans(aPlayer, "Info_EdenGarden_Mode_Working");
            return;
        }
        if (!this.getStoredCrops()
            .isEmpty()) {
            GTUtility.sendChatTrans(aPlayer, "Info_EdenGarden_Mode_HasSeeds");
            return;
        }
        this.setMode(GreenHouseModes.getNextMode(this.getMode()));
        GTUtility.sendChatTrans(
            aPlayer,
            "Info_EdenGarden_Mode_Change",
            this.getMode()
                .getName());
    }

    @Deprecated
    default void tryChangeSetupPhase(EntityPlayer aPlayer) {
        // TODO: Remove this legacy setup phase toggle after greenhouse setup is fully owned by MUI2 machine modes.
        if (this.getMaxProgressTime() > 0) {
            GTUtility.sendChatTrans(aPlayer, "Info_EdenGarden_SetupPhase_Working");
            return;
        }
        this.setSetupPhase(this.getSetupPhase() + 1);
        if (this.getSetupPhase() == 3) this.setSetupPhase(0);

        String phaseKey = switch (this.getSetupPhase()) {
            case 0 -> "Info_EdenGarden_Operating";
            case 1 -> "Info_EdenGarden_Input";
            case 2 -> "Info_EdenGarden_Output";
            default -> "Info_EdenGarden_SetupPhase_Invalid";
        };

        GTUtility
            .sendChatTrans(aPlayer, "Info_EdenGarden_SetupPhase_Change_Format", new ChatComponentTranslation(phaseKey));
    }

    @Deprecated
    default void tryChangeHumidityMode(EntityPlayer aPlayer) {
        // TODO: Remove this legacy humidity toggle after greenhouse biome handling is fully CropsNH based.
        this.setUseNoHumidity(!this.isUseNoHumidity());
        if (this.isUseNoHumidity()) {
            GTUtility.sendChatTrans(aPlayer, "Info_EdenGarden_NoHumidityMode_Enabled");
        } else {
            GTUtility.sendChatTrans(aPlayer, "Info_EdenGarden_NoHumidityMode_Disabled");
        }
    }

}
