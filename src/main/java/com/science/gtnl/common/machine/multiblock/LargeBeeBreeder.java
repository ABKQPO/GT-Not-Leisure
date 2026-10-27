package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings1;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.gui.modularui.LargeBeeBreederGui;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.machine.bee.BeeBreedingHelper;
import com.science.gtnl.utils.machine.bee.BeeBreedingPlanner;
import com.science.gtnl.utils.machine.bee.BeeSpeciesArchive;
import com.science.gtnl.utils.structure.GTNLMultiblockTooltipBuilder;

import forestry.api.apiculture.IAlleleBeeSpecies;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import lombok.Getter;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class LargeBeeBreeder extends MultiMachineBase<LargeBeeBreeder> {

    private static final long BASE_EUT_PER_BREEDING = 400L;

    private static final long EUT_PER_GLASS_TIER = 400L;

    private static final int TICKS_PER_BREEDING = 64;
    private static final int INPUT_SCAN_INTERVAL = 100;
    private static final int BASE_PARALLEL_STEPS = 1;

    private static final byte OPERATION_NONE = 0;
    private static final byte OPERATION_BREEDING = 1;
    private static final byte OPERATION_OUTPUT = 2;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":multiblock/large_bee_breeder";
    private static final int HORIZONTAL_OFFSET = 7;
    private static final int VERTICAL_OFFSET = 8;
    private static final int DEPTH_OFFSET = 0;

    private static final String[][] SHAPE = StructureUtils.readStructureFromFile(STRUCTURE_FILE_PATH);

    private static int getParallelForVoltageTier(int voltageTier) {
        int upgrades = Math.max(0, Math.min(voltageTier, VoltageIndex.MAX) - VoltageIndex.LV);
        return BASE_PARALLEL_STEPS << upgrades;
    }

    private boolean hasStainlessSteelGear;
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
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide() && aTick % INPUT_SCAN_INTERVAL == 0) {
            boolean gearBefore = hasStainlessSteelGear;
            hasStainlessSteelGear = checkStainlessSteelGear(getControllerSlot());
            if (gearBefore != hasStainlessSteelGear) markBreedingChanged();

            if (mMachine) scanInputBuses();
            refreshBreedingPlan();
        }
    }

    @Override
    public IStructureDefinition<LargeBeeBreeder> getStructureDefinition() {
        if (structureDefinition == null) {
            structureDefinition = StructureDefinition.<LargeBeeBreeder>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(SHAPE))
                .addElement('A', chainAllGlasses(-1, (te, t) -> te.mGlassTier = t, te -> te.mGlassTier))
                .addElement('B', ofChain(ofBlockAnyMeta(Blocks.dirt, 0), ofBlock(Blocks.grass, 0)))
                .addElement(
                    'G',
                    ofChain(
                        buildHatchAdder(LargeBeeBreeder.class)
                            .atLeast(InputBus, OutputBus, Energy.or(ExoticEnergy), Maintenance)
                            .casingIndex(getCasingTextureID())
                            .hint(1)
                            .buildAndChain(),
                        onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings1, 10))))
                .addElement('H', ofBlock(Blocks.planks, 0))
                .addElement('I', ofBlock(Blocks.wooden_slab, 0))
                .addElement('J', ofBlock(GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('K', ofBlock(GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('L', ofBlock(GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('N', ofBlock(GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('O', ofBlock(GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('P', ofBlock(GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .build();
        }
        return structureDefinition;
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
        hasStainlessSteelGear = checkStainlessSteelGear(getControllerSlot());

        markBreedingChanged();
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        scanInputBuses();
        activeOperation = OPERATION_NONE;
        activeBreedingBatch.clear();

        if (targetBeeSpecies == null || targetBeeSpecies.isEmpty()) {
            refreshBreedingPlan();
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        refreshBreedingPlan();
        if (pendingPrincessOutputs <= 0) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (speciesArchive.hasSpecies(targetBeeSpecies)) {
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
            prepareBreedingCycle(0L);
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        List<BeeBreedingHelper.BreedingStep> readySteps = breedingPlan
            .getReadySteps(speciesArchive.getSpecies(), getMaxParallelBreedingSteps());
        if (readySteps.isEmpty()) {
            allTasksBlocked = true;
            missingDroneSpecies = breedingPlan.getFirstMissingSpecies();
            if (missingDroneSpecies.isEmpty()) missingDroneSpecies = targetBeeSpecies;
            markBreedingChanged();
            return SimpleCheckRecipeResult.ofFailure("bee_breeder_missing_drone");
        }

        int poweredParallels = (int) Math
            .min(readySteps.size(), Math.max(0L, getMaxInputPower()) / getEuPerBreedingAttempt());
        if (poweredParallels == 0) {
            return CheckRecipeResultRegistry.insufficientPower(getEuPerBreedingAttempt());
        }
        readySteps = readySteps.subList(0, poweredParallels);

        double chanceBonus = (hasStainlessSteelGear ? 2.0D : 0.0D) + Math.max(0, mGlassTier);
        List<BreedingAttempt> batch = new ArrayList<>(readySteps.size());
        for (BeeBreedingHelper.BreedingStep step : readySteps) {
            batch.add(new BreedingAttempt(step, step.chance + chanceBonus));
        }

        activeOperation = OPERATION_BREEDING;
        activeBreedingBatch = batch;
        allTasksBlocked = false;
        missingDroneSpecies = "";
        prepareBreedingCycle(getEnergyCostPerTick(batch.size()));
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    private void prepareBreedingCycle(long eut) {
        mMaxProgresstime = TICKS_PER_BREEDING;
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        lEUt = -eut;
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
    }

    private boolean checkStainlessSteelGear(ItemStack stack) {
        if (stack == null) return false;
        return stack.isItemEqual(GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.StainlessSteel, 1L));
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
        return 10;
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
        values.put("glassEu", EUT_PER_GLASS_TIER);
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
                    attempt.effectiveChance));
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

    public int getMaxParallelBreedingSteps() {
        return getParallelForVoltageTier(mEnergyHatchTier);
    }

    public long getEuPerBreedingAttempt() {
        return getEnergyCostPerTick(1);
    }

    private long getEnergyCostPerTick(int parallelAttempts) {
        long eutPerAttempt = BASE_EUT_PER_BREEDING + (long) Math.max(0, mGlassTier) * EUT_PER_GLASS_TIER;
        return Math.max(0L, eutPerAttempt * Math.max(0, parallelAttempts));
    }

    public int getMutationChanceBonus() {
        return (hasStainlessSteelGear ? 2 : 0) + Math.max(0, mGlassTier);
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
            BeeBreedingHelper.BreedingStep step = new BeeBreedingHelper.BreedingStep(
                parent1,
                parent2,
                result,
                tag.getDouble("baseChance"));
            return new BreedingAttempt(step, tag.getDouble("effectiveChance"));
        }
    }

}
