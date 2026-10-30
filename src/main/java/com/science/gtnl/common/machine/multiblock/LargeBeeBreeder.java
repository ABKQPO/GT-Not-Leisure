package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.error;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_GLOW;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.api.casing.GTNLCasings;
import com.science.gtnl.common.gui.modularui.LargeBeeBreederGui;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.machine.bee.BeeBreedingHelper;
import com.science.gtnl.utils.machine.bee.BeeBreedingHelper.FoundationRequirement;
import com.science.gtnl.utils.machine.bee.BeeBreedingPlanner;
import com.science.gtnl.utils.machine.bee.BeeBreedingTiers;
import com.science.gtnl.utils.machine.bee.BeeSpeciesArchive;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.structure.GTNLMultiblockTooltipBuilder;

import forestry.api.apiculture.IAlleleBeeSpecies;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import lombok.Getter;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class LargeBeeBreeder extends MultiMachineBase<LargeBeeBreeder> {

    private static final long BASE_EUT_PER_BREEDING = 8192L;
    private static final int BASE_VOLTAGE_TIER = VoltageIndex.IV;
    private static final int BASE_GLASS_TIER = VoltageIndex.HV;
    private static final int CHANCE_PER_GLASS_TIER = 2;

    private static final int TICKS_PER_BREEDING = 32 * 20;
    private static final int INPUT_SCAN_INTERVAL = 100;
    private static final int FOUNDATION_SCAN_INTERVAL = 200;
    private static final int BASE_PARALLEL_STEPS = 1;

    private static final byte OPERATION_NONE = 0;
    private static final byte OPERATION_BREEDING = 1;
    private static final byte OPERATION_OUTPUT = 2;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":multiblock/large_bee_breeder";
    private static final int HORIZONTAL_OFFSET = 5;
    private static final int VERTICAL_OFFSET = 9;
    private static final int DEPTH_OFFSET = 3;

    private static final String[][] SHAPE = StructureUtils.readStructureFromFile(STRUCTURE_FILE_PATH);

    private static int getParallelForVoltageTier(int voltageTier) {
        int upgrades = Math.max(0, Math.min(voltageTier, VoltageIndex.MAX) - BASE_VOLTAGE_TIER);
        return BASE_PARALLEL_STEPS << upgrades;
    }

    public static int getSpeciesVoltageTier(String species) {
        return BeeBreedingTiers.getVoltageTier(species);
    }

    private IStructureDefinition<LargeBeeBreeder> structureDefinition;

    @Getter
    private String targetBeeSpecies = "";

    @Getter
    private BeeSpeciesArchive speciesArchive = new BeeSpeciesArchive();

    private BeeBreedingPlanner.Plan breedingPlan = BeeBreedingPlanner.Plan.empty("");
    private List<BeeBreedingHelper.BreedingStep> breedingChain = new ArrayList<>();
    private boolean breedingPlanDirty = true;

    private byte activeOperation = OPERATION_NONE;
    private List<BreedingAttempt> activeBreedingBatch = new ArrayList<>();
    private final List<ItemStack> foundationInputs = new ArrayList<>();
    private final Map<String, ItemStack> foundationMatches = new HashMap<>();
    private long lastFoundationScanTick = Long.MIN_VALUE;
    private List<FoundationRequirement> missingFoundations = Collections.emptyList();
    private boolean activeFoundationsChecked;
    private int activeRequiredVoltageTier = BASE_VOLTAGE_TIER;

    @Getter
    private int pendingPrincessOutputs = 0;

    @Getter
    private String missingDroneSpecies = "";

    @Getter
    private boolean allTasksBlocked = false;

    @Getter
    private int chainTotalSteps = 0;

    @Getter
    private int chainCompletedSteps = 0;

    @Getter
    private boolean targetInputValid = true;

    @Getter
    private int breedingRevision;

    public LargeBeeBreeder(String aName) {
        super(aName);
    }

    public LargeBeeBreeder(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeBeeBreeder(this.mName);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide() && mMachine && isFoundationScanDue()) {
            startRecipeProcessing();
            try {
                refreshFoundationInputs(getStoredInputs());
            } finally {
                endRecipeProcessing();
            }
            refreshBreedingPlan();
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide() && aTick % INPUT_SCAN_INTERVAL == 0) {
            if (mMachine) {
                startRecipeProcessing();
                try {
                    scanInputBuses();
                } finally {
                    endRecipeProcessing();
                }
            }
            refreshBreedingPlan();
        }
    }

    @Override
    public IStructureDefinition<LargeBeeBreeder> getStructureDefinition() {
        if (structureDefinition == null) {
            structureDefinition = StructureDefinition.<LargeBeeBreeder>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(SHAPE))
                .addElement('A', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
                .addElement('B', ofStructureBlock(ModsItemlist.ChiselYellowHempcrete))
                .addElement('C', ofStructureBlock(ModsItemlist.ChiselBrownHempcrete))
                .addElement('D', ofStructureBlock(ModsItemlist.ChiselBlackHempcrete))
                .addElement('E', ofStructureBlock(ModsItemlist.ChiselOrangeWoolenClay))
                .addElement('F', ofStructureBlock(ModsItemlist.ChiselBrownWoolenClay))
                .addElement('G', ofStructureBlock(ModsItemlist.EtFuturumRequiemOrangeConcrete))
                .addElement('H', ofStructureBlock(ModsItemlist.EtFuturumRequiemBrownConcrete))
                .addElement('I', ofStructureBlock(ModsItemlist.ExtraUtilitiesLightBlueLapisCaelestis))
                .addElement(
                    'J',
                    ofChain(
                        buildHatchAdder(LargeBeeBreeder.class)
                            .atLeast(InputBus, OutputBus, Energy.or(ExoticEnergy), Maintenance)
                            .casingIndex(getCasingTextureID())
                            .hint(1)
                            .buildAndChain(),
                        onElementPass(
                            x -> ++x.mCountCasing,
                            ofStructureBlock(ModsItemlist.VillageNamesYellowConcrete))))
                .build();
        }
        return structureDefinition;
    }

    private IStructureElement<LargeBeeBreeder> ofStructureBlock(ModsItemlist material) {
        ItemStack stack = material.get(1);
        if (stack == null || !(stack.getItem() instanceof ItemBlock)) return error();
        return ofBlock(Block.getBlockFromItem(stack.getItem()), stack.getItemDamage());
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFFSET,
            VERTICAL_OFFSET,
            DEPTH_OFFSET,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        mCountCasing = 0;
        mGlassTier = -1;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 10);
        checkHatchMin(errors, InputBus, 1);
        checkHatchMin(errors, OutputBus, 1);
        checkHatchMin(errors, Energy.or(ExoticEnergy), 1);
        if (shouldCheckMaintenance()) checkHatchMin(errors, Maintenance, 1);

        markBreedingChanged();
    }

    @Override
    public int checkEnergyHatchTier() {
        int tier = VoltageIndex.ULV;
        for (MTEHatch hatch : GTUtility.validMTEList(mEnergyHatches)) {
            tier = Math.max(tier, (int) hatch.getInputTier());
        }
        for (MTEHatch hatch : GTUtility.validMTEList(mExoticEnergyHatches)) {
            tier = Math.max(tier, (int) hatch.getInputTier());
        }
        return tier;
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        scanInputBuses();
        activeOperation = OPERATION_NONE;
        activeBreedingBatch.clear();
        activeFoundationsChecked = false;

        if (targetBeeSpecies == null || targetBeeSpecies.isEmpty()) {
            refreshBreedingPlan();
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        refreshBreedingPlan();
        if (pendingPrincessOutputs <= 0) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (getBreedingVoltageTier() < BASE_VOLTAGE_TIER) return insufficientBreedingVoltage(BASE_VOLTAGE_TIER);
        if (speciesArchive.hasSpecies(targetBeeSpecies)) {
            int requiredTier = getSpeciesVoltageTier(targetBeeSpecies);
            if (getBreedingVoltageTier() < requiredTier) return insufficientBreedingVoltage(requiredTier);
        }

        long eutPerAttempt = getEuPerBreedingAttempt();
        if (speciesArchive.hasSpecies(targetBeeSpecies)) {
            if (getMaxInputPower() < eutPerAttempt) return CheckRecipeResultRegistry.insufficientPower(eutPerAttempt);
            ItemStack princess = BeeBreedingHelper.createPrincess(targetBeeSpecies);
            if (princess == null) {
                allTasksBlocked = true;
                missingDroneSpecies = targetBeeSpecies;
                markBreedingChanged();
                return SimpleCheckRecipeResult.ofFailure("bee_breeder_unreachable_target");
            }
            ItemStack drone = BeeBreedingHelper.createDrone(targetBeeSpecies);
            mOutputItems = drone == null ? new ItemStack[] { princess } : new ItemStack[] { princess, drone };
            activeOperation = OPERATION_OUTPUT;
            prepareBreedingCycle(eutPerAttempt);
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        List<BeeBreedingHelper.BreedingStep> readySteps = breedingPlan
            .getReadySteps(speciesArchive.getSpecies(), Integer.MAX_VALUE);
        if (readySteps.isEmpty()) {
            allTasksBlocked = true;
            missingDroneSpecies = breedingPlan.getFirstMissingSpecies();
            if (missingDroneSpecies.isEmpty()) missingDroneSpecies = targetBeeSpecies;
            markBreedingChanged();
            return SimpleCheckRecipeResult.ofFailure("bee_breeder_missing_drone");
        }

        int requiredTier = readySteps.stream()
            .mapToInt(step -> getSpeciesVoltageTier(step.result))
            .min()
            .orElse(BASE_VOLTAGE_TIER);
        readySteps.removeIf(step -> getBreedingVoltageTier() < getSpeciesVoltageTier(step.result));
        if (readySteps.isEmpty()) return insufficientBreedingVoltage(requiredTier);
        if (getMaxInputPower() < eutPerAttempt) return CheckRecipeResultRegistry.insufficientPower(eutPerAttempt);

        int poweredParallels = (int) Math.min(
            Math.min(readySteps.size(), getMaxParallelBreedingSteps()),
            Math.max(0L, getMaxInputPower()) / eutPerAttempt);
        readySteps = readySteps.subList(0, poweredParallels);
        updateMissingFoundations(readySteps);
        if (!missingFoundations.isEmpty()) {
            allTasksBlocked = true;
            return SimpleCheckRecipeResult.ofFailure("bee_breeder_missing_foundation");
        }

        double chanceBonus = getMutationChanceBonus();
        List<BreedingAttempt> batch = new ArrayList<>(readySteps.size());
        for (BeeBreedingHelper.BreedingStep step : readySteps) {
            batch.add(new BreedingAttempt(step, step.chance + chanceBonus));
        }

        activeOperation = OPERATION_BREEDING;
        activeBreedingBatch = batch;
        activeFoundationsChecked = true;
        allTasksBlocked = false;
        missingDroneSpecies = "";
        prepareBreedingCycle(getEnergyCostPerTick(batch.size()));
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    private CheckRecipeResult insufficientBreedingVoltage(int requiredTier) {
        allTasksBlocked = true;
        missingDroneSpecies = "";
        return SimpleCheckRecipeResult.ofFailure(switch (requiredTier) {
            case VoltageIndex.ZPM -> "bee_breeder_voltage_zpm";
            case VoltageIndex.UHV -> "bee_breeder_voltage_uhv";
            default -> "bee_breeder_voltage_iv";
        });
    }

    private void prepareBreedingCycle(long eut) {
        updateActiveVoltageTier();
        mMaxProgresstime = createBreedingOverclockCalculator().getDuration();
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        lEUt = -eut;
    }

    private void updateActiveVoltageTier() {
        activeRequiredVoltageTier = BASE_VOLTAGE_TIER;
        if (activeOperation == OPERATION_OUTPUT) {
            String outputSpecies = mOutputItems != null && mOutputItems.length > 0
                ? BeeBreedingHelper.getBeeUID(mOutputItems[0])
                : targetBeeSpecies;
            activeRequiredVoltageTier = getSpeciesVoltageTier(outputSpecies);
        }
        for (BreedingAttempt attempt : activeBreedingBatch) {
            activeRequiredVoltageTier = Math.max(activeRequiredVoltageTier, getSpeciesVoltageTier(attempt.step.result));
        }
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        if (getBreedingVoltageTier() < activeRequiredVoltageTier) {
            checkRecipeResult = insufficientBreedingVoltage(activeRequiredVoltageTier);
            return false;
        }
        if (activeOperation == OPERATION_BREEDING) {
            if (!activeFoundationsChecked) {
                List<BeeBreedingHelper.BreedingStep> active = new ArrayList<>(activeBreedingBatch.size());
                for (BreedingAttempt attempt : activeBreedingBatch) active.add(attempt.step);
                updateMissingFoundations(active);
                activeFoundationsChecked = true;
            }
            if (!missingFoundations.isEmpty()) {
                checkRecipeResult = SimpleCheckRecipeResult.ofFailure("bee_breeder_missing_foundation");
                return false;
            }
        }
        checkRecipeResult = CheckRecipeResultRegistry.SUCCESSFUL;
        allTasksBlocked = false;
        return super.onRunningTick(stack);
    }

    @Override
    protected void outputAfterRecipe() {
        super.outputAfterRecipe();
        settleActiveOperation();
        markBaseTileDirty();
    }

    private void settleActiveOperation() {
        if (activeOperation == OPERATION_NONE) return;

        boolean archiveChanged = false;
        if (activeOperation == OPERATION_OUTPUT) {
            if (pendingPrincessOutputs > 0) {
                pendingPrincessOutputs--;
            }
        } else if (activeOperation == OPERATION_BREEDING) {
            for (BreedingAttempt attempt : activeBreedingBatch) {
                if (speciesArchive.hasSpecies(attempt.step.result)) continue;
                if (BeeBreedingHelper.tryMutation(attempt.effectiveChance)) {
                    archiveChanged |= speciesArchive.unlockSpecies(attempt.step.result);
                }
            }
        }

        activeOperation = OPERATION_NONE;
        activeBreedingBatch.clear();
        if (archiveChanged) invalidateBreedingPlan();
        markBreedingChanged();
    }

    public void setTargetBeeSpecies(String species) {

        String candidate = species == null ? "" : species.trim();
        if (!candidate.isEmpty()) {
            IAlleleBeeSpecies resolved = BeeBreedingHelper.getSpeciesByUID(candidate);
            if (resolved == null) {
                resolved = BeeBreedingHelper.getSpeciesByName(candidate);
            }
            targetInputValid = resolved != null;
            if (resolved != null) {

                String resolvedUID = resolved.getUID();
                if (!resolvedUID.equals(targetBeeSpecies)) {
                    this.targetBeeSpecies = resolvedUID;
                    invalidateBreedingPlan();
                    markBaseTileDirty();
                }
                return;
            }
            markBreedingChanged();
            return;
        }
        targetInputValid = true;
        if (!targetBeeSpecies.isEmpty()) {
            this.targetBeeSpecies = "";
            invalidateBreedingPlan();
            markBaseTileDirty();
        }
    }

    private void refreshBreedingPlan() {
        boolean previousBlocked = allTasksBlocked;
        String previousMissing = missingDroneSpecies;
        int previousCompleted = chainCompletedSteps;

        if (targetBeeSpecies == null || targetBeeSpecies.isEmpty()) {
            if (breedingPlanDirty || !breedingChain.isEmpty()) markBreedingChanged();
            breedingPlan = BeeBreedingPlanner.Plan.empty("");
            breedingChain = new ArrayList<>();
            breedingPlanDirty = false;
            allTasksBlocked = false;
            missingDroneSpecies = "";
        } else {
            if (breedingPlanDirty) {
                breedingPlan = BeeBreedingPlanner.plan(targetBeeSpecies, speciesArchive.getSpecies());
                breedingChain = new ArrayList<>(breedingPlan.getSteps());
                breedingPlanDirty = false;
                markBreedingChanged();
            }

            boolean targetUnlocked = speciesArchive.hasSpecies(targetBeeSpecies);
            boolean hasReadyStep = !breedingPlan.getReadySteps(speciesArchive.getSpecies(), 1)
                .isEmpty();
            allTasksBlocked = pendingPrincessOutputs > 0 && !targetUnlocked && !hasReadyStep;
            missingDroneSpecies = allTasksBlocked ? breedingPlan.getFirstMissingSpecies() : "";
            if (allTasksBlocked && missingDroneSpecies.isEmpty()) {
                missingDroneSpecies = targetBeeSpecies;
            }
        }

        if (activeOperation == OPERATION_BREEDING) {
            updateMissingFoundations(getActiveBreedingSteps());
        } else if (pendingPrincessOutputs > 0 && !speciesArchive.hasSpecies(targetBeeSpecies)) {
            updateMissingFoundations(
                breedingPlan.getReadySteps(speciesArchive.getSpecies(), getMaxParallelBreedingSteps()));
        } else {
            updateMissingFoundations(Collections.emptyList());
        }
        allTasksBlocked |= !missingFoundations.isEmpty();
        updateChainDisplayInfo();
        if (previousBlocked != allTasksBlocked || !Objects.equals(previousMissing, missingDroneSpecies)
            || previousCompleted != chainCompletedSteps) {
            markBreedingChanged();
        }
    }

    private void invalidateBreedingPlan() {
        breedingPlanDirty = true;
        markBreedingChanged();
    }

    private void scanInputBuses() {
        ArrayList<ItemStack> inputs = getStoredInputs();
        List<ItemStack> toRemove = new ArrayList<>();

        for (ItemStack stack : inputs) {
            if (stack == null || stack.stackSize <= 0) continue;

            if (BeeBreedingHelper.isDrone(stack)) {
                speciesArchive.unlockSpecies(BeeBreedingHelper.getBeeUID(stack));
                toRemove.add(stack);
            } else if (BeeBreedingHelper.isPrincess(stack)) {

                String uid = BeeBreedingHelper.getBeeUID(stack);
                if (uid != null) {
                    speciesArchive.unlockSpecies(uid);
                    long newPending = (long) pendingPrincessOutputs + Math.max(1, stack.stackSize);
                    pendingPrincessOutputs = (int) Math.min(Integer.MAX_VALUE, newPending);
                }
                toRemove.add(stack);
            }
        }

        for (ItemStack stack : toRemove) {
            stack.stackSize = 0;
        }
        if (!toRemove.isEmpty()) {
            updateSlots();
            markBaseTileDirty();
            invalidateBreedingPlan();
            markBreedingChanged();
        }
        if (isFoundationScanDue()) refreshFoundationInputs(inputs);
    }

    private boolean isFoundationScanDue() {
        return lastFoundationScanTick == Long.MIN_VALUE
            || mTotalRunTime - lastFoundationScanTick >= FOUNDATION_SCAN_INTERVAL;
    }

    private void refreshFoundationInputs(List<ItemStack> inputs) {
        foundationInputs.clear();
        for (ItemStack input : inputs) {
            if (input == null || input.stackSize <= 0) continue;
            ItemStack copy = input.copy();
            copy.stackSize = 1;
            foundationInputs.add(copy);
        }
        foundationMatches.clear();
        lastFoundationScanTick = mTotalRunTime;
        activeFoundationsChecked = false;
        markBreedingChanged();
    }

    public ItemStack findFoundation(FoundationRequirement requirement) {
        String key = requirement.key();
        if (!foundationMatches.containsKey(key)) {
            ItemStack match = null;
            for (ItemStack input : foundationInputs) {
                if (requirement.matches(input)) {
                    match = input;
                    break;
                }
            }
            foundationMatches.put(key, match);
        }
        return foundationMatches.get(key);
    }

    private void updateMissingFoundations(List<BeeBreedingHelper.BreedingStep> steps) {
        Map<String, FoundationRequirement> missing = new LinkedHashMap<>();
        for (BeeBreedingHelper.BreedingStep step : steps) {
            for (FoundationRequirement requirement : step.foundations) {
                if (findFoundation(requirement) == null) missing.putIfAbsent(requirement.key(), requirement);
            }
        }
        List<FoundationRequirement> next = new ArrayList<>(missing.values());
        if (!missingFoundations.equals(next)) {
            missingFoundations = next;
            markBreedingChanged();
        }
    }

    public List<FoundationRequirement> getMissingFoundations() {
        return Collections.unmodifiableList(missingFoundations);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return GTNLCasings.YellowConcrete.getTextureId();
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        return new GTNLMultiblockTooltipBuilder().addAutoTooltip(this)
            .toolTipFinisher();
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.large_bee_breeder.name";
    }

    @Override
    public TooltipStats getTooltipStats() {
        return new TooltipStats(BASE_PARALLEL_STEPS, Double.NaN, Double.NaN, false, false);
    }

    @Override
    public TooltipStats getTooltipStatsAtTier(TieredPart part, int tier) {
        if (part == TieredPart.VOLTAGE) {
            return new TooltipStats(getParallelForVoltageTier(tier), Double.NaN, Double.NaN, false, false);
        }
        return getTooltipStats();
    }

    @Override
    public Map<String, Object> getTooltipVars() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("attemptSeconds", TICKS_PER_BREEDING / 20D);
        values.put("baseEu", BASE_EUT_PER_BREEDING);
        values.put("glassChance", CHANCE_PER_GLASS_TIER);
        return values;
    }

    @Override
    public String[] getInfoData() {
        String[] superInfo = super.getInfoData();
        String[] info = new String[superInfo.length + 5];
        System.arraycopy(superInfo, 0, info, 0, superInfo.length);

        info[superInfo.length] = StatCollector.translateToLocalFormatted(
            "gtnl.bee_breeder.current_target",
            targetBeeSpecies.isEmpty() ? StatCollector.translateToLocal("gtnl.bee_breeder.unset") : targetBeeSpecies);
        info[superInfo.length + 1] = StatCollector.translateToLocalFormatted(
            "gtnl.bee_breeder.archive_size",
            speciesArchive.getSpecies()
                .size());
        info[superInfo.length + 2] = StatCollector
            .translateToLocalFormatted("gtnl.bee_breeder.chain_progress", countCompletedSteps(), breedingChain.size());
        info[superInfo.length + 3] = StatCollector
            .translateToLocalFormatted("gtnl.bee_breeder.pending", pendingPrincessOutputs);
        info[superInfo.length + 4] = StatCollector.translateToLocalFormatted(
            "gtnl.bee_breeder.energy",
            NumberFormatUtil.formatNumber(getEnergyCostPerTick(1)),
            getMutationChanceBonus());

        return info;
    }

    public List<BeeBreedingHelper.BreedingStep> getBreedingSteps() {
        return Collections.unmodifiableList(breedingChain);
    }

    public List<BeeBreedingHelper.BreedingStep> getActiveBreedingSteps() {
        if (activeOperation != OPERATION_BREEDING) return Collections.emptyList();
        List<BeeBreedingHelper.BreedingStep> steps = new ArrayList<>(activeBreedingBatch.size());
        for (BreedingAttempt attempt : activeBreedingBatch) {
            steps.add(
                new BeeBreedingHelper.BreedingStep(
                    attempt.step.parent1,
                    attempt.step.parent2,
                    attempt.step.result,
                    attempt.effectiveChance,
                    attempt.step.foundations));
        }
        return steps;
    }

    private void updateChainDisplayInfo() {
        chainTotalSteps = breedingChain.size();
        chainCompletedSteps = countCompletedSteps();
    }

    private int countCompletedSteps() {
        int count = 0;
        for (BeeBreedingHelper.BreedingStep step : breedingChain) {
            if (speciesArchive.hasSpecies(step.result)) count++;
        }
        return count;
    }

    @Override
    @Nonnull
    protected MTEMultiBlockBaseGui<LargeBeeBreeder> getGui() {
        return new LargeBeeBreederGui(this);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

        aNBT.setString("targetBeeSpecies", targetBeeSpecies != null ? targetBeeSpecies : "");
        aNBT.setInteger("pendingPrincessOutputs", pendingPrincessOutputs);
        aNBT.setTag("dronePool", speciesArchive.toNBT());
        aNBT.setByte("beeBreederActiveOperation", activeOperation);
        NBTTagList attempts = new NBTTagList();
        for (BreedingAttempt attempt : activeBreedingBatch) {
            attempts.appendTag(attempt.toNBT());
        }
        aNBT.setTag("beeBreederActiveBatch", attempts);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

        targetBeeSpecies = aNBT.getString("targetBeeSpecies");
        pendingPrincessOutputs = aNBT.getInteger("pendingPrincessOutputs");
        foundationInputs.clear();
        foundationMatches.clear();
        lastFoundationScanTick = Long.MIN_VALUE;
        missingFoundations = Collections.emptyList();
        activeFoundationsChecked = false;

        if (aNBT.hasKey("dronePool")) {
            speciesArchive = BeeSpeciesArchive.fromNBT(aNBT.getCompoundTag("dronePool"));
        } else {
            speciesArchive = new BeeSpeciesArchive();
        }

        activeOperation = aNBT.getByte("beeBreederActiveOperation");
        activeBreedingBatch = new ArrayList<>();
        NBTTagList attempts = aNBT.getTagList("beeBreederActiveBatch", 10);
        for (int i = 0; i < attempts.tagCount(); i++) {
            BreedingAttempt attempt = BreedingAttempt.fromNBT(attempts.getCompoundTagAt(i));
            if (attempt != null) activeBreedingBatch.add(attempt);
        }
        if (activeOperation != OPERATION_BREEDING) activeBreedingBatch.clear();
        updateActiveVoltageTier();

        breedingPlan = BeeBreedingPlanner.Plan.empty(targetBeeSpecies);
        breedingPlanDirty = true;
        breedingChain.clear();
        markBreedingChanged();
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        aNBT.setString("targetBeeSpecies", targetBeeSpecies != null ? targetBeeSpecies : "");
        aNBT.setInteger("pendingPrincessOutputs", pendingPrincessOutputs);
        aNBT.setTag("dronePool", speciesArchive.toNBT());
        super.setItemNBT(aNBT);
    }

    @Override
    public int getMaxParallelRecipes() {
        return getMaxParallelBreedingSteps();
    }

    public int getBreedingVoltageTier() {
        return checkEnergyHatchTier();
    }

    public int getMaxParallelBreedingSteps() {
        return getParallelForVoltageTier(getBreedingVoltageTier());
    }

    public long getEuPerBreedingAttempt() {
        return getEnergyCostPerTick(1);
    }

    private long getEnergyCostPerTick(int parallelAttempts) {
        long eutPerAttempt = createBreedingOverclockCalculator().getConsumption() << getGlassUpgrades();
        return eutPerAttempt * Math.max(0, parallelAttempts);
    }

    private GTNLOverclockCalculator createBreedingOverclockCalculator() {
        int voltageUpgrades = Math.max(0, Math.min(getBreedingVoltageTier(), VoltageIndex.MAX) - BASE_VOLTAGE_TIER);
        return new GTNLOverclockCalculator().setRecipeEUt(BASE_EUT_PER_BREEDING)
            .setEUt(BASE_EUT_PER_BREEDING << (2 * voltageUpgrades))
            .setDuration(TICKS_PER_BREEDING)
            .setEUtIncreasePerOC(4)
            .setDurationDecreasePerOC(2)
            .calculate();
    }

    private int getGlassUpgrades() {
        return Math.max(0, Math.min(mGlassTier, VoltageIndex.MAX) - BASE_GLASS_TIER);
    }

    public int getMutationChanceBonus() {
        return getGlassUpgrades() * CHANCE_PER_GLASS_TIER;
    }

    @Override
    public boolean doesBindPlayerInventory() {
        return true;
    }

    @Override
    public boolean supportsVoidProtection() {
        return false;
    }

    @Override
    public boolean supportsInputSeparation() {
        return false;
    }

    @Override
    public boolean supportsBatchMode() {
        return false;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
    }

    private void markBaseTileDirty() {
        IGregTechTileEntity baseTile = getBaseMetaTileEntity();
        if (baseTile != null) baseTile.markDirty();
    }

    private void markBreedingChanged() {
        breedingRevision++;
    }

    private static final class BreedingAttempt {

        private final BeeBreedingHelper.BreedingStep step;
        private final double effectiveChance;

        private BreedingAttempt(BeeBreedingHelper.BreedingStep step, double effectiveChance) {
            this.step = step;
            this.effectiveChance = effectiveChance;
        }

        private NBTTagCompound toNBT() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("parent1", step.parent1);
            tag.setString("parent2", step.parent2);
            tag.setString("result", step.result);
            tag.setDouble("baseChance", step.chance);
            tag.setDouble("effectiveChance", effectiveChance);
            return tag;
        }

        private static BreedingAttempt fromNBT(NBTTagCompound tag) {
            if (tag == null) return null;
            String parent1 = tag.getString("parent1");
            String parent2 = tag.getString("parent2");
            String result = tag.getString("result");
            if (parent1.isEmpty() || parent2.isEmpty() || result.isEmpty()) return null;
            BeeBreedingHelper.BreedingStep step = BeeBreedingHelper
                .findRegisteredStep(parent1, parent2, result, tag.getDouble("baseChance"));
            if (step == null) return null;
            return new BreedingAttempt(step, tag.getDouble("effectiveChance"));
        }
    }

}
