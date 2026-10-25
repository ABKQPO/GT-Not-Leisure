package com.science.gtnl.common.machine.multiMachineBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.MainAxisAlignment;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.IWidgetBuilder;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.Column;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedRow;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.IControllerInfo;
import com.science.gtnl.api.IMultiblockTooltipRules;
import com.science.gtnl.api.IWirelessEnergy;
import com.science.gtnl.common.gui.modularui.GTNLMultiBlockBaseGui;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputHatchME;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;
import com.science.gtnl.utils.structure.GTNLMultiblockTooltipBuilder;
import com.science.gtnl.utils.structure.GTNLStructureErrors;
import com.science.gtnl.utils.structure.InfoSummary.BonusStat;
import com.science.gtnl.utils.structure.InfoSummary.TierBonus;
import com.science.gtnl.utils.structure.MultiblockTooltipReader;
import com.science.gtnl.utils.structure.StructureTooltipHooks;

import bartworks.util.BWUtil;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.structure.error.ErrorType;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.structure.error.TranslatableText;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.api.util.tooltip.TooltipHelper;
import gregtech.common.data.GTCoilTracker;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.IDualInputInventoryWithPattern;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gtPlusPlus.api.objects.minecraft.BlockPos;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import lombok.Getter;
import lombok.Setter;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;

public abstract class MultiMachineBase<T extends MultiMachineBase<T>> extends MTEExtendedPowerMultiBlockBase<T>
    implements IConstructable, ISurvivalConstructable, IControllerInfo, IMultiblockTooltipRules {

    public static final Optional<Byte>[] HATCH_COLOR_OPTIONS = createHatchColorOptions();
    private static final String PARALLEL_CONTROLLER_TOOLTIP_KEY = "gtnl.structure.tooltip.parallel_controller";
    protected static final String WIRELESS_UPGRADE_TOOLTIP_KEY = "gtnl.structure.tooltip.wireless_upgrade";
    // Why: GT's addGlassEnergyLimitInfo(int) builds this sentence but is deprecated.
    private static final String GLASS_LIMIT_TOOLTIP_KEY = "GT5U.MBTT.Structure.GlassEnergyLimit";
    private static final String GLASS_LIMIT_TIER_TOOLTIP_KEY = "GT5U.MBTT.Structure.GlassEnergyLimitTier";
    // Why: GT joins the two halves with a fixed English comma, which reads wrong in other languages.
    private static final String GLASS_LIMIT_LINE_KEY = "gtnl.structure.tooltip.glass_energy_limit";
    private static final String CASING_LIMIT_TOOLTIP_KEY = "gtnl.structure.tooltip.casing_energy_limit";
    private static final String CASING_LIMIT_UNLOCK_TOOLTIP_KEY = CASING_LIMIT_TOOLTIP_KEY + ".unlock";
    public static final int ONE_TIER_SKIP = 1;
    private static final int BASE_TOOLTIP_TIER = 0;
    private static final int NO_TOOLTIP_TIER = -1;
    private static final int LAST_PARALLEL_CONTROLLER_PROBE_TIER = 5;
    private static final int HEAT_PROBE_DURATION = 20, HEAT_PROBE_EUT = 30, HEAT_PROBE_RECIPE_HEAT = 1;
    private static final int VOLTAGE_HEAT_PER_TIER = 100, VOLTAGE_HEAT_FROM_TIER = VoltageIndex.MV;

    @SuppressWarnings("unchecked")
    public static Optional<Byte>[] createHatchColorOptions() {
        Optional<Byte>[] colorOptions = new Optional[16];
        for (byte color = 0; color < colorOptions.length; color++) {
            colorOptions[color] = Optional.of(color);
        }
        return colorOptions;
    }

    public ArrayList<MTEHatch> mExoticDynamoHatches = new ArrayList<>();
    public ArrayList<ParallelControllerHatch> mParallelControllerHatches = new ArrayList<>();

    public GTCoilTracker.MultiCoilLease coilLease = null;
    public final ArrayList<ItemStack> recipeSearchItemInputs = new ArrayList<>();
    public final ArrayList<FluidStack> recipeSearchFluidInputs = new ArrayList<>();
    public List<SlotWidget> slotWidgets = new ArrayList<>(1);
    public int mCountCasing = -1;
    public int mGlassTier = -1;
    public int mCasingTier = -1;
    public int mParallelTier = 0;
    public int mEnergyHatchTier = 0;
    public double mConfigSpeedBoost = 1;

    @Getter
    @Setter
    public HeatingCoilLevel mCoilLevel = HeatingCoilLevel.None;
    public int mHeatingCapacity = 0;
    private int tooltipVoltageTier = NO_TOOLTIP_TIER;
    private int tooltipParallelTier = NO_TOOLTIP_TIER;

    public MultiMachineBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public MultiMachineBase(String aName) {
        super(aName);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            runServerTick(aBaseMetaTileEntity, aTick);
            return;
        }
        playActivitySoundLoop();
    }

    @Override
    public void runMachine(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (mMaxProgresstime > 0 && doRandomMaintenanceDamage()) {
            runActiveMachineTick(aBaseMetaTileEntity);
        } else {
            runIdleMachineTick(aBaseMetaTileEntity, aTick);
        }
    }

    public void runServerTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        incrementTotalRunTime();
        if (mEfficiency < 0) {
            mEfficiency = 0;
        }

        refreshRuntimeUpdateState();
        if (shouldCheckStructureNow()) {
            checkStructure(true, aBaseMetaTileEntity);
        }

        if (mStartUpCheck < 0) {
            runMachineLifecycle(aBaseMetaTileEntity, aTick);
        }

        updateRuntimeIndicators(aBaseMetaTileEntity);
        updateCoilLeaseState();
    }

    public void runMachineLifecycle(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (!mMachine) {
            if (aBaseMetaTileEntity.isAllowedToWork()) {
                stopMachine(ShutDownReasonRegistry.STRUCTURE_INCOMPLETE);
            }
            return;
        }

        checkMaintenance();
        if (getRepairStatus() <= 0) {
            if (aBaseMetaTileEntity.isAllowedToWork()) {
                stopMachine(ShutDownReasonRegistry.NO_REPAIR);
            }
            return;
        }

        runMachine(aBaseMetaTileEntity, aTick);
    }

    public void runActiveMachineTick(IGregTechTileEntity aBaseMetaTileEntity) {
        ItemStack controllerSlot = getControllerSlot();
        if (!onRunningTick(mInventory[1])) {
            return;
        }

        markDirty();
        if (!polluteEnvironment(getPollutionPerTick(mInventory[1]))) {
            stopMachine(ShutDownReasonRegistry.POLLUTION_FAIL);
            return;
        }

        if (mMaxProgresstime > 0 && ++mProgresstime >= mMaxProgresstime) {
            flushRecipeOutputs();
            triggerOutputAfterRecipe();
            finishRecipeProgress(controllerSlot);
            if (aBaseMetaTileEntity.isAllowedToWork()) {
                tryCheckRecipe();
            }
        }
    }

    public void runIdleMachineTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isAllowedToWork() && shouldCheckRecipeThisTick(aTick, aBaseMetaTileEntity)
            && tryCheckRecipe()) {
            markDirty();
        }

        if (mMaxProgresstime <= 0) {
            mEfficiency = Math.max(0, mEfficiency - 1000);
        }
    }

    public void updateCoilLeaseState() {
        boolean active = mMaxProgresstime > 0;
        if (!mMachine || !active) {
            deactivateCoilLease();
            return;
        }

        if (!mCoils.isEmpty() && coilLease == null) {
            coilLease = GTCoilTracker.activate(this, mCoils);
        }
    }

    public void refreshRuntimeUpdateState() {
        if (mUpdated) {
            if (mUpdate <= 0) {
                mUpdate = 50;
            }
            mUpdated = false;
        }
    }

    public boolean shouldCheckStructureNow() {
        return --mUpdate == 0 || --mStartUpCheck == 0;
    }

    public boolean shouldCheckRecipeThisTick(long aTick) {
        return shouldCheckRecipeThisTick(aTick, getBaseMetaTileEntity());
    }

    public boolean shouldCheckRecipeThisTick(long aTick, IGregTechTileEntity aBaseMetaTileEntity) {
        if (aBaseMetaTileEntity != null
            && (aBaseMetaTileEntity.hasWorkJustBeenEnabled() || aBaseMetaTileEntity.hasInventoryBeenModified())) {
            return true;
        }
        return super.shouldCheckRecipeThisTick(aTick);
    }

    public boolean clearRecipeMapForAllInputHatches() {
        return resetRecipeMapForAllInputHatches(null);
    }

    public boolean resetRecipeMapForAllInputHatches() {
        return resetRecipeMapForAllInputHatches(this.getRecipeMap());
    }

    public boolean resetRecipeMapForAllInputHatches(RecipeMap<?> aMap) {
        int cleared = 0;
        for (MTEHatchInput g : this.mInputHatches) {
            if (resetRecipeMapForHatch(g, aMap)) {
                cleared++;
            }
        }
        for (MTEHatchInputBus g : this.mInputBusses) {
            if (resetRecipeMapForHatch(g, aMap)) {
                cleared++;
            }
        }
        return cleared > 0;
    }

    @Override
    public void onRemoval() {
        deactivateCoilLease();
        super.onRemoval();
    }

    @Override
    public void onUnload() {
        deactivateCoilLease();
        super.onUnload();
    }

    public void deactivateCoilLease() {
        if (coilLease != null) {
            GTCoilTracker.deactivate(coilLease);
            coilLease = null;
        }
    }

    public void updateRuntimeIndicators(IGregTechTileEntity aBaseMetaTileEntity) {
        setErrorDisplayID(
            (getErrorDisplayID() & ~127) | (mWrench ? 0 : 1)
                | (mScrewdriver ? 0 : 2)
                | (mSoftMallet ? 0 : 4)
                | (mHardHammer ? 0 : 8)
                | (mSolderingTool ? 0 : 16)
                | (mCrowbar ? 0 : 32)
                | (mMachine ? 0 : 64));
        aBaseMetaTileEntity.setActive(mMaxProgresstime > 0);
        setMufflers(aBaseMetaTileEntity.isActive() && mPollution > 0);
    }

    public void playActivitySoundLoop() {
        doActivitySound(getActivitySoundLoop());
    }

    public void incrementTotalRunTime() {
        mTotalRunTime++;
    }

    public void flushRecipeOutputs() {
        if (mOutputItems != null) {
            addItemOutputs(mOutputItems);
            mOutputItems = null;
        }
        if (mOutputFluids != null) {
            addFluidOutputs(mOutputFluids);
            mOutputFluids = null;
        }
    }

    public void finishRecipeProgress(ItemStack controllerSlot) {
        mEfficiency = Math.max(
            0,
            Math.min(
                mEfficiency + mEfficiencyIncrease,
                getMaxEfficiency(controllerSlot) - ((getIdealStatus() - getRepairStatus()) * 1000)));
        mProgresstime = 0;
        mMaxProgresstime = 0;
        mEfficiencyIncrease = 0;
        mLastWorkingTick = mTotalRunTime;
    }

    public void triggerOutputAfterRecipe() {
        outputAfterRecipe();
    }

    public boolean tryCheckRecipe() {
        return checkRecipe();
    }

    public void checkHatch(List<StructureError> errors) {
        checkHatchMax(errors, HatchElement.Maintenance, 1);
        if (getPollutionPerSecond(null) > 0) {
            checkHasMufflerHatch(errors);
        }
        checkParallelControllerHatchMax(errors, 1);
        checkCoilStructureRequirement(errors);
        checkGlassEnergyHatchRequirement(errors);
    }

    protected void checkEnergyHatch(List<StructureError> errors) {
        if (MainConfig.machine.enableLaserHatch) {
            return;
        }
        boolean hasEnergyTunnel = false;
        for (MTEHatch hatch : getExoticEnergyHatches()) {
            if (hatch instanceof MTEHatchEnergyTunnel) {
                hasEnergyTunnel = true;
                break;
            }
        }
        if (hasEnergyTunnel) {
            errors.add(GTNLStructureErrors.laserEnergyTunnelDisabled());
            return;
        }
        if (getRealMaxInputAmps() > 64) {
            errors.add(GTNLStructureErrors.energyInputAmperageTooHigh());
        }
    }

    protected void checkCoilStructureRequirement(List<StructureError> errors) {
        if (requiresCoilStructureCheck() && mCoilLevel == HeatingCoilLevel.None) {
            errors.add(StructureErrorRegistry.COIL_LEVEL_NOT_ENOUGH);
        }
    }

    protected boolean requiresCoilStructureCheck() {
        return false;
    }

    protected void checkGlassEnergyHatchRequirement(List<StructureError> errors) {
        int requiredGlassTier = getGlassEnergyTierLimit();
        if (requiredGlassTier < 0 || mGlassTier >= requiredGlassTier) {
            return;
        }
        for (MTEHatch hatch : this.mExoticEnergyHatches) {
            if (hatch.getConnectionType() == MTEHatch.ConnectionType.LASER) {
                errors.add(StructureErrors.glassTierNotEnough(requiredGlassTier));
                return;
            }
            if (this.mGlassTier < hatch.mTier) {
                errors.add(StructureErrorRegistry.ENERGY_TIER_EXCEED_GLASS);
                return;
            }
        }
        for (MTEHatchEnergy mEnergyHatch : this.mEnergyHatches) {
            if (this.mGlassTier < mEnergyHatch.mTier) {
                errors.add(StructureErrorRegistry.ENERGY_TIER_EXCEED_GLASS);
                return;
            }
        }
    }

    protected int getGlassEnergyTierLimit() {
        return -1;
    }

    protected void checkParallelControllerHatchMax(List<StructureError> errors, int max) {
        if (StructureTooltipHooks.onCheckHatches(this, 0, max, CustomHatchElement.ParallelCon)) return;
        int count = mParallelControllerHatches.size();
        if (count > max) {
            errors.add(GTNLStructureErrors.parallelControllerHatchCount(ErrorType.TOO_MANY, count, max));
        }
    }

    protected void checkHatchMin(List<StructureError> errors, IHatchElement<? super T> element, int min) {
        if (StructureTooltipHooks.onCheckHatches(this, min, StructureTooltipHooks.UNLIMITED, element)) return;
        int count = (int) element.count(self());
        if (count < min) {
            errors.add(StructureErrors.hatchCount(ErrorType.TOO_FEW, getHatchElementName(element), count, min));
        }
    }

    protected void checkHatchExact(List<StructureError> errors, IHatchElement<? super T> element, int target) {
        if (StructureTooltipHooks.onCheckHatches(this, target, target, element)) return;
        int count = (int) element.count(self());
        if (count != target) {
            errors.add(StructureErrors.hatchCount(ErrorType.NOT_MATCH, getHatchElementName(element), count, target));
        }
    }

    protected void checkHatchMax(List<StructureError> errors, IHatchElement<? super T> element, int max) {
        if (StructureTooltipHooks.onCheckHatches(this, 0, max, element)) return;
        int count = (int) element.count(self());
        if (count > max) {
            errors.add(StructureErrors.hatchCount(ErrorType.TOO_MANY, getHatchElementName(element), count, max));
        }
    }

    protected void checkHatchMin(List<StructureError> errors, TranslatableText name, int current, int min) {
        if (current < min) {
            errors.add(StructureErrors.hatchCount(ErrorType.TOO_FEW, name, current, min));
        }
    }

    protected void checkHatchExact(List<StructureError> errors, TranslatableText name, int current, int target) {
        if (current != target) {
            errors.add(StructureErrors.hatchCount(ErrorType.NOT_MATCH, name, current, target));
        }
    }

    protected void checkHatchMax(List<StructureError> errors, TranslatableText name, int current, int max) {
        if (current > max) {
            errors.add(StructureErrors.hatchCount(ErrorType.TOO_MANY, name, current, max));
        }
    }

    protected TranslatableText getHatchElementName(IHatchElement<? super T> element) {
        element.getDisplayName();
        return TranslatableText.lang(element.getDescriptionLangKey());
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        this.mExoticEnergyHatches.clear();
        this.mExoticDynamoHatches.clear();
        this.mParallelControllerHatches.clear();
        resetRecipeSearchBuffers();
        mCountCasing = 0;
        mParallelTier = 0;
        mEnergyHatchTier = 0;
        mHeatingCapacity = 0;
        mGlassTier = -1;
        this.setMCoilLevel(HeatingCoilLevel.None);
    }

    public void setupParameters() {
        mEnergyHatchTier = checkEnergyHatchTier();
    }

    public void resetParallelTier() {
        // Why: tooltip probes give the hatchless prototype a parallel controller tier that a reset must not clear.
        mParallelTier = tooltipParallelTier == NO_TOOLTIP_TIER ? 0 : tooltipParallelTier;
    }

    /**
     * 基于配方映射创建默认处理逻辑，只在机器实例化时构建一次。
     * Creates the default processing logic from the recipe map and builds it once per machine instance.
     * <p>
     * 若机器不依赖标准配方映射，或需要更复杂的业务流程，请改为覆写 {@link #checkProcessing()}。
     * Override {@link #checkProcessing()} when the machine does not use a standard recipe map or needs custom flow.
     */
    @ApiStatus.OverrideOnly
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @Override
            public @NotNull GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setHeatOC(getHeatOC())
                    .setMachineHeat(getMachineHeat())
                    .setHeatDiscount(getHeatDiscount())
                    .setAmperageOC(getAmperageOC())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setPerfectOC(getPerfectOC())
                    .setMaxTierSkips(getMaxTierSkip())
                    .setMaxOverclocks(getMaxOverclocks());
            }

        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    /**
     * 返回是否启用无损超频。
     * Returns whether perfect overclocking should be enabled.
     *
     * @return 为 `true` 时启用无损超频 / `true` enables perfect overclocking
     */
    @ApiStatus.OverrideOnly
    public boolean getPerfectOC() {
        return false;
    }

    @ApiStatus.OverrideOnly
    public boolean getHeatOC() {
        return false;
    }

    @ApiStatus.OverrideOnly
    public boolean getHeatDiscount() {
        return false;
    }

    @ApiStatus.OverrideOnly
    public boolean getAmperageOC() {
        return true;
    }

    @ApiStatus.OverrideOnly
    public int getMachineHeat() {
        return 0;
    }

    public boolean addsVoltageHeat() {
        return false;
    }

    protected int coilAndVoltageHeat() {
        return (int) mCoilLevel.getHeat()
            + VOLTAGE_HEAT_PER_TIER * (BWUtil.getTier(getMaxInputEu()) - VOLTAGE_HEAT_FROM_TIER);
    }

    @ApiStatus.OverrideOnly
    public int getMaxOverclocks() {
        return Integer.MAX_VALUE;
    }

    @ApiStatus.OverrideOnly
    public int getMaxTierSkip() {
        return ONE_TIER_SKIP;
    }

    @Override
    public void setupProcessingLogic(ProcessingLogic logic) {
        super.setupProcessingLogic(logic);
        // Why: a machine's own processing logic would otherwise keep GT's default instead of getMaxTierSkip.
        logic.setMaxTierSkips(getMaxTierSkip());
    }

    @ApiStatus.OverrideOnly
    public double getDurationModifier() {
        return 1.0F;
    }

    @ApiStatus.OverrideOnly
    public double getEUtDiscount() {
        return 1.0F;
    }

    /**
     * 返回当前机器允许的最大并行数。
     * Returns the current maximum parallel count supported by the machine.
     *
     * @return 动态最大并行值 / Dynamic maximum parallel value
     */
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public TooltipStats getTooltipStats() {
        if (!MultiblockTooltipReader.runsRecipes(this, this::createProcessingLogic)) return null;
        return tooltipStatsWith(null, BASE_TOOLTIP_TIER, NO_TOOLTIP_TIER);
    }

    @Override
    public TooltipStats getTooltipStatsAtTier(TieredPart part, int tier) {
        return switch (part) {
            case VOLTAGE, COIL, GLASS, CASING -> tooltipStatsWith(part, tier, NO_TOOLTIP_TIER);
            case PARALLEL_CONTROLLER -> tooltipStatsWith(null, BASE_TOOLTIP_TIER, tier);
            case HIGH_PRESSURE -> null;
        };
    }

    @Override
    public List<String> getTooltipFeatureLines() {
        List<String> lines = new ArrayList<>();
        int glassTier = getGlassEnergyTierLimit();
        if (glassTier >= 0) {
            lines.add(
                StatCollector.translateToLocalFormatted(
                    GLASS_LIMIT_LINE_KEY,
                    StatCollector.translateToLocal(GLASS_LIMIT_TOOLTIP_KEY),
                    TooltipHelper.voltageTierText(glassTier, false),
                    StatCollector.translateToLocal(GLASS_LIMIT_TIER_TOOLTIP_KEY)));
        }
        lines.addAll(casingEnergyLimitLines());
        return lines;
    }

    @Override
    public OptionalInt getTooltipTierSkips() {
        int skips = getMaxTierSkip();
        // Why: one tier skip is GT's own default, which GT's multiblock tooltips leave unsaid.
        return skips == ONE_TIER_SKIP ? OptionalInt.empty() : OptionalInt.of(skips);
    }

    @Override
    public HeatRules getTooltipHeatRules() {
        if (!(createProcessingLogic() instanceof GTNLProcessingLogic logic)) return null;
        GTNLOverclockCalculator calculator = logic.createOverclockCalculator(heatProbeRecipe());
        if (!calculator.heatOC && !calculator.heatDiscount) return null;
        return new HeatRules(
            calculator.heatOC,
            calculator.heatDiscount,
            calculator.heatDiscountExponent,
            calculator.recipeHeat == HEAT_PROBE_RECIPE_HEAT,
            addsVoltageHeat() ? VOLTAGE_HEAT_PER_TIER : 0,
            VOLTAGE_HEAT_FROM_TIER);
    }

    // Why: the overclock calculator only shows which heat rules it applies once it is built for a recipe.
    private static GTRecipe heatProbeRecipe() {
        return new GTRecipe(
            new ItemStack[0],
            new ItemStack[0],
            null,
            new int[0],
            new int[0],
            new int[0],
            new int[0],
            new FluidStack[0],
            new FluidStack[0],
            HEAT_PROBE_DURATION,
            HEAT_PROBE_EUT,
            HEAT_PROBE_RECIPE_HEAT);
    }

    private List<String> casingEnergyLimitLines() {
        int casing = mCasingTier;
        int hatch = mEnergyHatchTier;
        mEnergyHatchTier = VoltageIndex.MAX;
        try {
            Integer tiersAbove = null;
            for (int tier = 0; tier <= TieredPart.CASING.maxTier; tier++) {
                mCasingTier = tier;
                int limit = GTUtility.getTier(getMachineVoltageLimit());
                if (limit >= VoltageIndex.MAX) {
                    if (tiersAbove == null) return Collections.emptyList();
                    String unlock = TooltipHelper.voltageTierText(casingVoltageTier(tier), false);
                    return Arrays.asList(
                        StatCollector.translateToLocalFormatted(CASING_LIMIT_TOOLTIP_KEY, tiersAbove),
                        StatCollector.translateToLocalFormatted(CASING_LIMIT_UNLOCK_TOOLTIP_KEY, unlock));
                }
                int above = limit - casingVoltageTier(tier);
                if (tiersAbove != null && above != tiersAbove) return Collections.emptyList();
                tiersAbove = above;
            }
            return tiersAbove == null ? Collections.emptyList()
                : Collections
                    .singletonList(StatCollector.translateToLocalFormatted(CASING_LIMIT_TOOLTIP_KEY, tiersAbove));
        } finally {
            mCasingTier = casing;
            mEnergyHatchTier = hatch;
        }
    }

    // Why: casings that cap the voltage are component assembly line casings, whose tier 0 is LV.
    private static int casingVoltageTier(int casingTier) {
        return casingTier + 1;
    }

    protected TooltipStats readTooltipStats() {
        return new TooltipStats(
            getMaxParallelRecipes(),
            1 / getDurationModifier(),
            getEUtDiscount(),
            false,
            getPerfectOC());
    }

    private TooltipStats tooltipStatsWith(TieredPart probed, int tier, int parallelTier) {
        HeatingCoilLevel coil = mCoilLevel;
        int glass = mGlassTier;
        int casing = mCasingTier;
        int parallel = mParallelTier;
        // Why: the prototype has no coils, glass or casings, which their formulas would read as a penalty.
        mCoilLevel = HeatingCoilLevel.getFromTier((byte) tierOf(probed, TieredPart.COIL, tier));
        mGlassTier = tierOf(probed, TieredPart.GLASS, tier);
        mCasingTier = tierOf(probed, TieredPart.CASING, tier);
        tooltipVoltageTier = probed == TieredPart.VOLTAGE ? tier : NO_TOOLTIP_TIER;
        tooltipParallelTier = parallelTier;
        if (parallelTier != NO_TOOLTIP_TIER) mParallelTier = parallelTier;
        try {
            return readTooltipStats();
        } finally {
            mCoilLevel = coil;
            mGlassTier = glass;
            mCasingTier = casing;
            mParallelTier = parallel;
            tooltipVoltageTier = NO_TOOLTIP_TIER;
            tooltipParallelTier = NO_TOOLTIP_TIER;
        }
    }

    private static int tierOf(TieredPart probed, TieredPart part, int tier) {
        return probed == part ? tier : BASE_TOOLTIP_TIER;
    }

    @Override
    public List<String> getTooltipHatchLines(String hatchLangKey) {
        if (!CustomHatchElement.ParallelCon.getDescriptionLangKey()
            .equals(hatchLangKey)) return Collections.emptyList();
        String hatch = GTNLMultiblockTooltipBuilder.hatchName(hatchLangKey);
        TooltipStats low = tooltipStatsWith(
            null,
            BASE_TOOLTIP_TIER,
            MultiblockTooltipReader.FIRST_PARALLEL_CONTROLLER_PROBE_TIER);
        TooltipStats high = tooltipStatsWith(null, BASE_TOOLTIP_TIER, LAST_PARALLEL_CONTROLLER_PROBE_TIER);
        List<String> effects = parallelControllerEffects(low, high);
        List<String> lines = new ArrayList<>();
        lines.add(
            effects.isEmpty()
                ? StatCollector.translateToLocalFormatted(PARALLEL_CONTROLLER_TOOLTIP_KEY + ".accepts", hatch)
                : GTNLMultiblockTooltipBuilder.perTierText(TieredPart.PARALLEL_CONTROLLER, effects));
        int extra = extraParallels(low, MultiblockTooltipReader.FIRST_PARALLEL_CONTROLLER_PROBE_TIER);
        if (extra > 1 && extra == extraParallels(high, LAST_PARALLEL_CONTROLLER_PROBE_TIER)) {
            lines.add(
                StatCollector.translateToLocalFormatted(
                    PARALLEL_CONTROLLER_TOOLTIP_KEY + ".extra",
                    hatch,
                    TooltipHelper.parallelText(extra)));
        }
        int perfectFrom = parallelControllerPerfectOverclockTier();
        if (perfectFrom != NO_TOOLTIP_TIER) {
            lines.add(
                StatCollector.translateToLocalFormatted(
                    PARALLEL_CONTROLLER_TOOLTIP_KEY + ".perfect_oc",
                    TooltipHelper.voltageTierText(perfectFrom, false),
                    hatch));
        }
        for (String key : getTooltipParallelControllerKeys()) lines.add(StatCollector.translateToLocal(key));
        return lines;
    }

    private List<String> parallelControllerEffects(TooltipStats low, TooltipStats high) {
        List<TierBonus> bonuses = MultiblockTooltipReader.tierBonusesOf(this, TieredPart.PARALLEL_CONTROLLER);
        List<String> effects = new ArrayList<>();
        if (high.parallels() > low.parallels()) {
            effects.add(StatCollector.translateToLocal(PARALLEL_CONTROLLER_TOOLTIP_KEY + ".parallel"));
        }
        addParallelControllerEffect(
            effects,
            bonuses,
            high.speed() > low.speed(),
            ".speed",
            BonusStat.SPEED,
            BonusStat.DURATION);
        addParallelControllerEffect(
            effects,
            bonuses,
            high.energyUsage() < low.energyUsage(),
            ".energy",
            BonusStat.ENERGY);
        return effects;
    }

    // Why: a stat that changes by no steady step or factor has no number to show, so only its direction is named.
    private static void addParallelControllerEffect(List<String> effects, List<TierBonus> bonuses, boolean changes,
        String fallbackSuffix, BonusStat... stats) {
        List<BonusStat> shown = Arrays.asList(stats);
        for (TierBonus bonus : bonuses) {
            if (shown.contains(bonus.stat())) {
                effects.add(GTNLMultiblockTooltipBuilder.effectText(bonus));
                return;
            }
        }
        if (changes) effects.add(StatCollector.translateToLocal(PARALLEL_CONTROLLER_TOOLTIP_KEY + fallbackSuffix));
    }

    private int parallelControllerPerfectOverclockTier() {
        if (tooltipStatsWith(null, BASE_TOOLTIP_TIER, NO_TOOLTIP_TIER).perfectOverclock()) return NO_TOOLTIP_TIER;
        int from = NO_TOOLTIP_TIER;
        for (int tier = VoltageIndex.MAX; tier >= VoltageIndex.LV
            && tooltipStatsWith(null, BASE_TOOLTIP_TIER, tier).perfectOverclock(); tier--) {
            from = tier;
        }
        return from;
    }

    private static int extraParallels(TooltipStats stats, int hatchTier) {
        int hatchParallels = ParallelControllerHatch.maxParallelAt(hatchTier);
        return stats.parallels() % hatchParallels == 0 ? stats.parallels() / hatchParallels : 1;
    }

    public List<String> getTooltipParallelControllerKeys() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getTooltipUpgradeLines() {
        if (!(this instanceof IWirelessEnergy wireless)) return Collections.emptyList();
        List<String> lines = new ArrayList<>();
        String upgrade = wirelessUpgradeLine(wireless);
        if (upgrade != null) lines.add(upgrade);
        lines.addAll(getTooltipWirelessModeLines());
        return lines;
    }

    private String wirelessUpgradeLine(IWirelessEnergy wireless) {
        boolean installed = wireless.isWirelessUpgrade();
        TooltipStats without;
        TooltipStats with;
        try {
            wireless.setWirelessUpgrade(false);
            without = getTooltipStats();
            wireless.setWirelessUpgrade(true);
            with = getTooltipStats();
        } finally {
            wireless.setWirelessUpgrade(installed);
        }
        if (without == null || with == null) return null;
        List<String> bonuses = new ArrayList<>();
        if (rises(without.speed(), with.speed())) {
            bonuses.add(
                StatCollector.translateToLocalFormatted(
                    WIRELESS_UPGRADE_TOOLTIP_KEY + ".speed",
                    TooltipHelper.speedText(TooltipHelper.percentageFormat.format(with.speed() - without.speed()))));
        }
        if (rises(with.energyUsage(), without.energyUsage())) {
            bonuses.add(
                StatCollector.translateToLocalFormatted(
                    WIRELESS_UPGRADE_TOOLTIP_KEY + ".energy",
                    TooltipHelper
                        .effText(TooltipHelper.percentageFormat.format(without.energyUsage() - with.energyUsage()))));
        }
        if (bonuses.isEmpty()) return null;
        return StatCollector.translateToLocalFormatted(
            WIRELESS_UPGRADE_TOOLTIP_KEY + ".bonus",
            GTNLMultiblockTooltipBuilder.mechanicName(WIRELESS_UPGRADE_TOOLTIP_KEY),
            GTNLMultiblockTooltipBuilder.joinPhrases(bonuses));
    }

    private static boolean rises(double from, double to) {
        return to > from && !MultiblockTooltipReader.isNearly(from, to);
    }

    public List<String> getTooltipWirelessModeLines() {
        return Collections.emptyList();
    }

    public abstract int getCasingTextureID();

    public long getRealMaxInputAmps() {
        return getMaxWorkingInputAmps(getExoticAndNormalEnergyHatchList());
    }

    public static long getMaxWorkingInputAmps(Collection<? extends MTEHatch> hatches) {
        long maxAmps = 0L;
        for (MTEHatch tHatch : GTUtility.validMTEList(hatches)) {
            maxAmps = Math.max(maxAmps, tHatch.maxWorkingAmperesIn());
        }
        return maxAmps;
    }

    public long getMachineVoltageLimit() {
        return GTValues.V[mEnergyHatchTier];
    }

    public int checkEnergyHatchTier() {
        int tier = 0;
        for (MTEHatchEnergy tHatch : GTUtility.validMTEList(mEnergyHatches)) {
            tier = Math.max(tHatch.mTier, tier);
        }
        for (MTEHatch tHatch : GTUtility.validMTEList(mExoticEnergyHatches)) {
            tier = Math.max(tHatch.mTier, tier);
        }
        return tier;
    }

    public void createInventorySlots() {
        final SlotWidget inventorySlot = new SlotWidget(inventoryHandler, 1);
        inventorySlot.setBackground(GTUITextures.SLOT_DARK_GRAY);
        slotWidgets.add(inventorySlot);
    }

    @Deprecated
    public void addSharedScreen(ModularWindow.Builder builder) {
        // TODO: Remove this mui1 fallback after every GTNL multiblock is covered by mui2.
        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.PICTURE_SCREEN_BLACK)
                .setPos(4, 4)
                .setSize(190, 85));
    }

    public Column createSlotColumn() {
        Column slotColumn = new Column();
        for (int i = slotWidgets.size() - 1; i >= 0; i--) {
            slotColumn.widget(slotWidgets.get(i));
        }
        return slotColumn;
    }

    @Deprecated
    public void addBaseTextScroll(ModularWindow.Builder builder) {
        // TODO: Remove this mui1 fallback after every GTNL multiblock is covered by mui2.
        final DynamicPositionedColumn screenElements = new DynamicPositionedColumn();
        drawTexts(screenElements, !slotWidgets.isEmpty() ? slotWidgets.get(0) : null);
        builder.widget(
            new Scrollable().setVerticalScroll()
                .widget(screenElements.setPos(10, 0))
                .setPos(0, 7)
                .setSize(190, 79));
    }

    @Override
    public Pos2d getPowerSwitchButtonPos() {
        return new Pos2d(174, 166 - (slotWidgets.size() * 18));
    }

    @Override
    public Pos2d getStructureUpdateButtonPos() {
        return new Pos2d(174, 148 - (slotWidgets.size() * 18));
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new GTNLMultiBlockBaseGui<>(this);
    }

    @Override
    @Deprecated
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        // TODO: Remove this mui1 fallback after every GTNL multiblock is covered by mui2.
        addSharedScreen(builder);
        slotWidgets.clear();
        createInventorySlots();
        builder.widget(
            createSlotColumn().setAlignment(MainAxisAlignment.END)
                .setPos(173, 167 - 1));
        addBaseTextScroll(builder);

        if (supportsMachineModeSwitch() && machineModeIcons == null) {
            machineModeIcons = new ArrayList<>(4);
            setMachineModeIcons();
        }
        builder.widget(createPowerSwitchButton(builder))
            .widget(createVoidExcessButton(builder))
            .widget(createInputSeparationButton(builder))
            .widget(createModeSwitchButton(builder))
            .widget(createBatchModeButton(builder))
            .widget(createLockToSingleRecipeButton(builder))
            .widget(createStructureUpdateButton(builder))
            .widget(createMuffleButton(builder, true));

        DynamicPositionedRow configurationElements = new DynamicPositionedRow();
        addConfigurationWidgets(configurationElements, buildContext);

        if (supportsPowerPanel()) {
            builder.widget(createPowerPanelButton(builder));
            buildContext.addSyncedWindow(POWER_PANEL_WINDOW_ID, this::createPowerPanel);
        }

        if (supportsMachineInfo()) {
            builder.widget(createMachineInfoButton(builder));
            buildContext.addSyncedWindow(MACHINE_INFO_WINDOW_ID, this::createMachineInfo);
        }

        builder.widget(
            configurationElements.setSpace(2)
                .setAlignment(MainAxisAlignment.SPACE_BETWEEN)
                .setPos(getRecipeLockingButtonPos().add(18, 0)));
    }

    @Override
    @Deprecated
    public void addGregTechLogo(ModularWindow.Builder builder) {
        // TODO: Remove this mui1 fallback after every GTNL multiblock is covered by mui2.
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(172, 67));
    }

    @Override
    public void onMachineModeSwitchClick() {
        super.onMachineModeSwitchClick();
        if (getBaseMetaTileEntity().isClientSide()) return;
        clearRecipeMapForAllInputHatches();
        onModeChangeByButton();
        resetRecipeMapForAllInputHatches();
    }

    public void onModeChangeByButton() {

    }

    @Override
    public ButtonWidget createMuffleButton(IWidgetBuilder<?> builder, boolean canBeMuffled) {
        return (ButtonWidget) new ButtonWidget().setOnClick((clickData, widget) -> setMuffled(!isMuffled()))
            .setPlayClickSound(true)
            .setEnabled(canBeMuffled)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                if (isMuffled()) {
                    ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                    ret.add(GTUITextures.OVERLAY_BUTTON_MUFFLE_ON);
                } else {
                    ret.add(GTUITextures.BUTTON_STANDARD);
                    ret.add(GTUITextures.OVERLAY_BUTTON_MUFFLE_OFF);
                }
                return ret.toArray(new IDrawable[0]);
            })
            .attachSyncer(new FakeSyncWidget.BooleanSyncer(this::isMuffled, this::setMuffled), builder)
            .addTooltip(StatCollector.translateToLocal("GT5U.machines.muffled"))
            .setPos(200, 0)
            .setSize(12, 12);
    }

    public void addConfigurationWidgets(DynamicPositionedRow configurationElements, UIBuildContext buildContext) {

    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        int previousMachineMode = getMachineMode();
        clearRecipeMapForAllInputHatches();
        onModeChangeByScrewdriver(side, aPlayer, aX, aY, aZ, aTool);
        resetRecipeMapForAllInputHatches();
        if (previousMachineMode != getMachineMode()) {
            setSingleRecipeCheck(null);
            scheduleRecipeCheckImmediate();
        }
    }

    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {

    }

    @Override
    public boolean depleteInput(FluidStack aLiquid, boolean simulate) {
        return depleteInputQuantity(aLiquid, simulate) >= aLiquid.amount;
    }

    /**
     * Returns the amount of fluid drained from the input hatches.
     *
     * @param aLiquid  The fluid to drain. This stack is not modified.
     * @param simulate Whether to simulate the drain.
     * @return The amount of fluid drained.
     */
    public int depleteInputQuantity(FluidStack aLiquid, boolean simulate) {
        if (aLiquid == null) return 0;

        FluidStack remaining = aLiquid.copy();
        for (MTEHatchInput inputHatch : GTUtility.validMTEList(mInputHatches)) {
            setHatchRecipeMap(inputHatch);
            FluidStack drained = inputHatch.drain(ForgeDirection.UNKNOWN, remaining, !simulate);
            if (drained == null || drained.getFluid() != aLiquid.getFluid()) continue;

            if (drained.amount >= remaining.amount) {
                return aLiquid.amount - remaining.amount + drained.amount;
            }
            remaining.amount -= drained.amount;
        }
        return aLiquid.amount - remaining.amount;
    }

    /**
     * 执行配方检查并在成功时写入运行态数据。
     * Checks recipes and prepares machine runtime state when a recipe is found.
     * <p>
     * 对于基于标准配方映射的通用机器，优先通过 {@link #createProcessingLogic()} 复用共享逻辑。
     * Generic recipe-map machines should prefer {@link #createProcessingLogic()} to reuse the shared flow.
     */
    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        // 处理逻辑缺失时回退到旧版检查流程 / Fall back to the legacy recipe path when processing logic is absent
        if (processingLogic == null) {
            return checkRecipe(mInventory[1]) ? CheckRecipeResultRegistry.SUCCESSFUL
                : CheckRecipeResultRegistry.NO_RECIPE;
        }

        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        result = postCheckRecipe(result, processingLogic);
        // 到这里输入已被消耗，需要立刻同步槽位 / Inputs are consumed at this point, so slots must be synced now
        updateSlots();
        if (!result.wasSuccessful()) return result;

        mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = processingLogic.getDuration();
        setEnergyUsage(processingLogic);

        mOutputItems = processingLogic.getOutputItems();
        mOutputFluids = processingLogic.getOutputFluids();

        return result;
    }

    @Override
    @NotNull
    public CheckRecipeResult doCheckRecipe() {
        CheckRecipeResult result = searchDualInputRecipes();
        if (result.wasSuccessful()) {
            return result;
        }

        result = checkRecipeForCustomHatchInputs(result);
        if (result.wasSuccessful()) {
            return result;
        }

        return searchStandardInputRecipes(result);
    }

    public static boolean isColorAbsent(short hatchColors, byte color) {
        return (hatchColors & (1 << color)) == 0;
    }

    public short getHatchColors() {
        short hatchColors = 0;

        for (var bus : mInputBusses) hatchColors |= (short) (1 << bus.getColor());
        for (var hatch : mInputHatches) hatchColors |= (short) (1 << hatch.getColor());

        return hatchColors;
    }

    public void collectBusInputs(MTEHatchInputBus inputBus, List<ItemStack> inputItems) {
        for (int slot = inputBus.getSizeInventory() - 1; slot >= 0; slot--) {
            ItemStack inputStack = inputBus.getStackInSlot(slot);
            if (inputStack != null) {
                inputItems.add(inputStack);
            }
        }
    }

    public void addControllerSlotIfNeeded(List<ItemStack> inputItems) {
        ItemStack controllerSlot = getControllerSlot();
        if (canUseControllerSlotForRecipe() && controllerSlot != null) {
            inputItems.add(controllerSlot);
        }
    }

    public CheckRecipeResult processCurrentInputs(CheckRecipeResult seed) {
        CheckRecipeResult foundResult = processRecipeSearch();
        if (foundResult.wasSuccessful()) {
            return foundResult;
        }
        if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) {
            return foundResult;
        }
        return seed;
    }

    public CheckRecipeResult checkRecipeForCustomHatchInputs(CheckRecipeResult seed) {
        return checkRecipeForCustomHatches(seed);
    }

    public boolean tryCachePossibleRecipes(IDualInputInventoryWithPattern withPattern) {
        return processingLogic.tryCachePossibleRecipesFromPattern(withPattern);
    }

    public void setProcessingInputItems(List<ItemStack> inputItems) {
        processingLogic.setInputItems(inputItems);
    }

    public void setProcessingInputFluids(List<FluidStack> inputFluids) {
        processingLogic.setInputFluids(inputFluids);
    }

    public CheckRecipeResult processRecipeSearch() {
        return processingLogic.process();
    }

    public void resetRecipeSearchBuffers() {
        recipeSearchItemInputs.clear();
        recipeSearchFluidInputs.clear();
    }

    public void replaceRecipeSearchItems(Iterable<ItemStack> itemStacks) {
        recipeSearchItemInputs.clear();
        if (itemStacks instanceof Collection<?>itemCollection) {
            recipeSearchItemInputs.ensureCapacity(itemCollection.size());
        }
        for (ItemStack itemStack : itemStacks) {
            if (itemStack != null) {
                recipeSearchItemInputs.add(itemStack);
            }
        }
    }

    public void replaceRecipeSearchFluids(Iterable<FluidStack> fluidStacks) {
        recipeSearchFluidInputs.clear();
        if (fluidStacks instanceof Collection<?>fluidCollection) {
            recipeSearchFluidInputs.ensureCapacity(fluidCollection.size());
        }
        for (FluidStack fluidStack : fluidStacks) {
            if (fluidStack != null) {
                recipeSearchFluidInputs.add(fluidStack);
            }
        }
    }

    public void loadDualInputBuffers(ItemStack[] sharedItems, IDualInputInventory inventory) {
        ItemStack[] inventoryItemInputs = inventory.getItemInputs();
        FluidStack[] inventoryFluidInputs = inventory.getFluidInputs();
        resetRecipeSearchBuffers();
        recipeSearchItemInputs.ensureCapacity(sharedItems.length + inventoryItemInputs.length + 1);
        recipeSearchFluidInputs.ensureCapacity(inventoryFluidInputs.length);
        appendRecipeSearchItems(sharedItems);
        appendRecipeSearchItems(inventoryItemInputs);
        appendRecipeSearchFluids(inventoryFluidInputs);
        setProcessingInputItems(recipeSearchItemInputs);
        setProcessingInputFluids(recipeSearchFluidInputs);
    }

    public void appendRecipeSearchItems(ItemStack[] itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            if (itemStack != null) {
                recipeSearchItemInputs.add(itemStack);
            }
        }
    }

    public void appendRecipeSearchFluids(FluidStack[] fluidStacks) {
        for (FluidStack fluidStack : fluidStacks) {
            if (fluidStack != null) {
                recipeSearchFluidInputs.add(fluidStack);
            }
        }
    }

    @NotNull
    public CheckRecipeResult searchDualInputRecipes() {
        CheckRecipeResult result = CheckRecipeResultRegistry.NO_RECIPE;
        for (IDualInputHatch dualInputHatch : mDualInputHatches) {
            ItemStack[] sharedItems = dualInputHatch.getSharedItems();
            for (var inventoryIterator = dualInputHatch.inventories(); inventoryIterator.hasNext();) {
                IDualInputInventory inventory = inventoryIterator.next();
                if (inventory.isEmpty()) {
                    continue;
                }

                if (inventory instanceof IDualInputInventoryWithPattern withPattern
                    && !tryCachePossibleRecipes(withPattern)) {
                    continue;
                }

                loadDualInputBuffers(sharedItems, inventory);
                CheckRecipeResult foundResult = processRecipeSearch();
                if (foundResult.wasSuccessful()) {
                    return foundResult;
                }
                if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) {
                    result = foundResult;
                }
            }
        }
        return result;
    }

    @NotNull
    public CheckRecipeResult searchStandardInputRecipes(CheckRecipeResult seed) {
        short hatchColors = getHatchColors();
        boolean inputSeparated = isInputSeparationEnabled();
        if (hatchColors == 0) {
            replaceRecipeSearchFluids(getStoredFluids());
            setProcessingInputFluids(recipeSearchFluidInputs);
            if (inputSeparated) {
                return searchSeparatedInputsForColor((byte) 0, seed, false);
            }
            return searchCombinedInputsForColor((byte) 0, seed, false);
        }

        CheckRecipeResult result = seed;
        for (byte color = 0; color < 16; color++) {
            if (isColorAbsent(hatchColors, color)) {
                continue;
            }

            replaceRecipeSearchFluids(getStoredFluidsForColor(HATCH_COLOR_OPTIONS[color]));
            setProcessingInputFluids(recipeSearchFluidInputs);
            result = inputSeparated ? searchSeparatedInputsForColor(color, result, true)
                : searchCombinedInputsForColor(color, result, true);
            if (result.wasSuccessful()) {
                return result;
            }
        }
        return result;
    }

    @NotNull
    public CheckRecipeResult searchSeparatedInputsForColor(byte color, CheckRecipeResult seed, boolean colorLocked) {
        if (mInputBusses.isEmpty()) {
            return processCurrentInputs(seed);
        }

        CheckRecipeResult result = seed;
        for (MTEHatchInputBus inputBus : mInputBusses) {
            if (inputBus instanceof MTEHatchCraftingInputME || inputBus instanceof SuperCraftingInputHatchME) {
                continue;
            }

            byte busColor = inputBus.getColor();
            if (colorLocked && busColor != -1 && busColor != color) {
                continue;
            }

            recipeSearchItemInputs.clear();
            recipeSearchItemInputs.ensureCapacity(inputBus.getSizeInventory() + 1);
            collectBusInputs(inputBus, recipeSearchItemInputs);
            addControllerSlotIfNeeded(recipeSearchItemInputs);
            if (recipeSearchItemInputs.isEmpty() && recipeSearchFluidInputs.isEmpty()) {
                continue;
            }
            setProcessingInputItems(recipeSearchItemInputs);

            CheckRecipeResult foundResult = processRecipeSearch();
            if (foundResult.wasSuccessful()) {
                return foundResult;
            }
            if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) {
                result = foundResult;
            }
        }
        return result;
    }

    @NotNull
    public CheckRecipeResult searchCombinedInputsForColor(byte color, CheckRecipeResult seed, boolean colorLocked) {
        if (colorLocked) {
            replaceRecipeSearchItems(getStoredInputsForColor(HATCH_COLOR_OPTIONS[color]));
        } else {
            replaceRecipeSearchItems(getStoredInputs());
        }
        addControllerSlotIfNeeded(recipeSearchItemInputs);
        setProcessingInputItems(recipeSearchItemInputs);
        return processCurrentInputs(seed);
    }

    public long accumulateDynamoCapacity(Collection<? extends MTEHatch> dynamos) {
        long totalOutput = 0L;
        for (MTEHatch dynamo : dynamos) {
            if (dynamo == null || !dynamo.isValid()) {
                continue;
            }
            totalOutput += dynamo.maxAmperesOut() * dynamo.maxEUOutput();
        }
        return totalOutput;
    }

    public long injectEnergyIntoDynamos(Collection<? extends MTEHatch> dynamos, long actualOutputEU, long injected) {
        long totalInjected = injected;
        for (MTEHatch dynamo : dynamos) {
            if (dynamo == null || !dynamo.isValid()) {
                continue;
            }
            long leftToInject = actualOutputEU - totalInjected;
            if (leftToInject <= 0) {
                return totalInjected;
            }

            // Why: one write per hatch; a per-amp loop runs millions of times a tick on high-amp laser dynamos
            long energy = Math.min(leftToInject, dynamo.maxEUOutput() * dynamo.maxAmperesOut());
            dynamo.getBaseMetaTileEntity()
                .increaseStoredEnergyUnits(energy, false);
            totalInjected += energy;
        }
        return totalInjected;
    }

    public boolean depleteInputFromRestrictedHatches(Collection<CustomFluidHatch> aHatches, int aAmount) {
        for (CustomFluidHatch tHatch : GTUtility.validMTEList(aHatches)) {
            FluidStack tLiquid = tHatch.getFluid();
            if (tLiquid == null || tLiquid.amount < aAmount) {
                continue;
            }
            tLiquid = tHatch.drain(aAmount, false);
            if (tLiquid != null && tLiquid.amount >= aAmount) {
                tLiquid = tHatch.drain(aAmount, true);
                return tLiquid != null && tLiquid.amount >= aAmount;
            }
        }
        return false;
    }

    @Override
    public String[] getInfoData() {
        String dSpeed = String.format("%.3f", this.getDurationModifier() * 100) + "%";
        String dEUMod = String.format("%.3f", this.getEUtDiscount() * 100) + "%";

        String[] origin = super.getInfoData();
        String[] ret = new String[origin.length + 3];
        System.arraycopy(origin, 0, ret, 0, origin.length);
        ret[origin.length] = IGregTechDeviceInformation
            .encode("gtnl.machine.info.parallel", EnumChatFormatting.AQUA, EnumChatFormatting.GOLD, getTrueParallel());
        ret[origin.length + 1] = IGregTechDeviceInformation
            .encode("gtnl.machine.info.speed_multiplier", EnumChatFormatting.AQUA, EnumChatFormatting.GOLD, dSpeed);
        ret[origin.length + 2] = IGregTechDeviceInformation
            .encode("gtnl.machine.info.eu_modifier", EnumChatFormatting.AQUA, EnumChatFormatting.GOLD, dEUMod);
        return ret;
    }

    public IMetaTileEntity getMetaTileEntity(final IGregTechTileEntity aTileEntity) {
        if (aTileEntity == null) {
            return null;
        }
        return aTileEntity.getMetaTileEntity();
    }

    public boolean resetRecipeMapForHatch(MTEHatch aTileEntity, RecipeMap<?> aMap) {
        if (aTileEntity == null) {
            return false;
        }
        if (aTileEntity instanceof MTEHatchInput || aTileEntity instanceof MTEHatchInputBus
            || aTileEntity instanceof MTEHatchSteamBusInput) {
            if (aTileEntity instanceof MTEHatchInput) {
                ((MTEHatchInput) aTileEntity).mRecipeMap = aMap;
                if (aMap != null) {
                    ScienceNotLeisure.LOG.warn("Remapped Input Hatch to {}.", aMap.unlocalizedName);
                } else {
                    ScienceNotLeisure.LOG.warn("Cleared Input Hatch.");
                }
            } else if (aTileEntity instanceof MTEHatchInputBus) {
                ((MTEHatchInputBus) aTileEntity).mRecipeMap = aMap;
                if (aMap != null) {
                    ScienceNotLeisure.LOG.warn("Remapped Input Bus to {}.", aMap.unlocalizedName);
                } else {
                    ScienceNotLeisure.LOG.warn("Cleared Input Bus.");
                }
            } else {
                ((MTEHatchSteamBusInput) aTileEntity).mRecipeMap = null;
                ((MTEHatchSteamBusInput) aTileEntity).mRecipeMap = aMap;
                if (aMap != null) {
                    ScienceNotLeisure.LOG.warn("Remapped Input Bus to {}.", aMap.unlocalizedName);
                } else {
                    ScienceNotLeisure.LOG.warn("Cleared Input Bus.");
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    /**
     * 为支持该能力的仓室刷新外壳贴图。
     * Refreshes casing textures for hatches that support texture updates.
     */
    public boolean updateTexture(final IGregTechTileEntity aTileEntity, int aCasingID) {
        return updateTexture(getMetaTileEntity(aTileEntity), aCasingID);
    }

    /**
     * 为支持该能力的元方块实体刷新外壳贴图。
     * Refreshes casing textures for meta tile entities that expose texture updates.
     */
    public boolean updateTexture(final IMetaTileEntity aTileEntity, int aCasingID) {
        if (aTileEntity instanceof MTEHatch mteHatch) {
            mteHatch.updateTexture(aCasingID);
            return true;
        }
        return false;
    }

    @Override
    protected void onStructureCheckFinished(IGregTechTileEntity aBaseMetaTileEntity) {
        updateHatchTexture();
    }

    public <E> boolean addToMachineListInternal(ArrayList<E> aList, final IGregTechTileEntity aTileEntity,
        final int aBaseCasingIndex) {
        return addToMachineListInternal(aList, getMetaTileEntity(aTileEntity), aBaseCasingIndex);
    }

    public <E> boolean addToMachineListInternal(ArrayList<E> aList, final IMetaTileEntity aTileEntity,
        final int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }

        try {
            if (aTileEntity instanceof MTEHatchInput) {
                resetRecipeMapForHatch((MTEHatch) aTileEntity, getRecipeMap());
            }
            if (aTileEntity instanceof MTEHatchInputBus) {
                resetRecipeMapForHatch((MTEHatch) aTileEntity, getRecipeMap());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        if (aList.isEmpty()) {
            if (aTileEntity instanceof MTEHatch) {
                ScienceNotLeisure.LOG.warn(
                    "Adding {} at {}",
                    aTileEntity.getInventoryName(),
                    new BlockPos(aTileEntity.getBaseMetaTileEntity()).getLocationString());
                updateTexture(aTileEntity, aBaseCasingIndex);
                return aList.add((E) aTileEntity);
            }
        } else {
            IGregTechTileEntity aCur = aTileEntity.getBaseMetaTileEntity();
            if (aList.contains(aTileEntity)) {
                ScienceNotLeisure.LOG.warn(
                    "Found Duplicate {} @ {}",
                    aTileEntity.getInventoryName(),
                    new BlockPos(aCur).getLocationString());
                return false;
            }
            BlockPos aCurPos = new BlockPos(aCur);
            for (E m : aList) {
                IGregTechTileEntity b = ((IMetaTileEntity) m).getBaseMetaTileEntity();
                if (b != null) {
                    BlockPos aPos = new BlockPos(b);
                    if (aCurPos.equals(aPos)) {
                        ScienceNotLeisure.LOG
                            .warn("Found Duplicate {} at {}", b.getInventoryName(), aPos.getLocationString());
                        return false;
                    }
                }
            }
            if (aTileEntity instanceof MTEHatch) {
                ScienceNotLeisure.LOG.warn("Adding {} at {}", aCur.getInventoryName(), aCurPos.getLocationString());
                updateTexture(aTileEntity, aBaseCasingIndex);
                return aList.add((E) aTileEntity);
            }
        }
        return false;
    }

    @Override
    public boolean addEnergyOutput(long aEU) {
        if (aEU <= 0) {
            return true;
        }
        if (!mDynamoHatches.isEmpty() || !mExoticDynamoHatches.isEmpty()) {
            return addEnergyOutputMultipleDynamos(aEU, true);
        }
        return false;
    }

    @Override
    public boolean addEnergyOutputMultipleDynamos(long aEU, boolean aAllowMixedVoltageDynamos) {
        long totalOutput = accumulateDynamoCapacity(mDynamoHatches) + accumulateDynamoCapacity(mExoticDynamoHatches);
        long actualOutputEU = Math.min(totalOutput, aEU);
        long injected = injectEnergyIntoDynamos(mDynamoHatches, actualOutputEU, 0L);
        injected = injectEnergyIntoDynamos(mExoticDynamoHatches, actualOutputEU, injected);
        return injected > 0;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    @Override
    public boolean willExplodeInRain() {
        return false;
    }

    @Override
    public boolean supportsMachineInfo() {
        return false;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }

    @Override
    public int getRecipeCatalystPriority() {
        return -1;
    }

    public void updateHatchTexture() {
        for (MTEHatch h : mInputBusses) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mOutputBusses) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mInputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mOutputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mMufflerHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mMaintenanceHatches) h.updateTexture(getCasingTextureID());
        for (IDualInputHatch h : mDualInputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mEnergyHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mExoticEnergyHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mDynamoHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mExoticDynamoHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mParallelControllerHatches) h.updateTexture(getCasingTextureID());
    }

    @Override
    public long getMaxInputVoltage() {
        // Why: tooltip probes run on the prototype, which has no energy hatch to read a voltage from.
        if (tooltipVoltageTier != NO_TOOLTIP_TIER) return GTValues.V[tooltipVoltageTier];
        long rVoltage = 0;
        for (MTEHatchEnergy tHatch : GTUtility.validMTEList(mEnergyHatches)) rVoltage += tHatch.getBaseMetaTileEntity()
            .getInputVoltage();
        for (MTEHatch tHatch : GTUtility.validMTEList(mExoticEnergyHatches)) rVoltage += tHatch.getBaseMetaTileEntity()
            .getInputVoltage();
        return rVoltage;
    }

    @Override
    public long getAverageInputVoltage() {
        return Math.max(
            ExoticEnergyInputHelper.getAverageInputVoltageMulti(mEnergyHatches),
            ExoticEnergyInputHelper.getAverageInputVoltageMulti(mExoticEnergyHatches));
    }

    @Override
    public long getMaxInputAmps() {
        return Math.max(
            ExoticEnergyInputHelper.getMaxWorkingInputAmpsMulti(mEnergyHatches),
            ExoticEnergyInputHelper.getMaxWorkingInputAmpsMulti(mExoticEnergyHatches));
    }

    @Override
    public long getMaxInputEu() {
        return Math.max(
            ExoticEnergyInputHelper.getTotalEuMulti(mEnergyHatches),
            ExoticEnergyInputHelper.getTotalEuMulti(mExoticEnergyHatches));
    }

    @Override
    public long getMaxInputPower() {
        long eut = 0;
        for (MTEHatchEnergy tHatch : GTUtility.validMTEList(mEnergyHatches)) {
            IGregTechTileEntity baseTile = tHatch.getBaseMetaTileEntity();
            eut += baseTile.getInputVoltage() * baseTile.getInputAmperage();
        }
        for (MTEHatch tHatch : GTUtility.validMTEList(mExoticEnergyHatches)) {
            IGregTechTileEntity baseTile = tHatch.getBaseMetaTileEntity();
            eut += baseTile.getInputVoltage() * baseTile.getInputAmperage();
        }
        return eut;
    }

    @Override
    public long getInputVoltageTier() {
        long rTier = 0;
        if (!mEnergyHatches.isEmpty()) {
            rTier = mEnergyHatches.get(0)
                .getInputTier();
            for (int i = 1; i < mEnergyHatches.size(); i++) {
                if (mEnergyHatches.get(i)
                    .getInputTier() != rTier) return 0;
            }
        }
        if (!mExoticEnergyHatches.isEmpty()) {
            rTier = mExoticEnergyHatches.get(0)
                .getInputTier();
            for (int i = 1; i < mExoticEnergyHatches.size(); i++) {
                if (mExoticEnergyHatches.get(i)
                    .getInputTier() != rTier) return 0;
            }
        }

        return rTier;
    }

    public enum CustomHatchElement implements IHatchElement<MultiMachineBase<?>> {

        ParallelCon("gtnl.structure.hatch.parallel_controller", MultiMachineBase::addParallelControllerToMachineList,
            ParallelControllerHatch.class) {

            @Override
            public long count(MultiMachineBase<?> tileEntity) {
                return tileEntity.mParallelControllerHatches.size();
            }
        },

        ExoticDynamo("gtnl.structure.hatch.exotic_dynamo", MultiMachineBase::addTectechDynamoToMachineList,
            MTEHatchDynamoMulti.class) {

            @Override
            public long count(MultiMachineBase<?> tileEntity) {
                return tileEntity.mExoticDynamoHatches.size();
            }
        };

        public final List<Class<? extends IMetaTileEntity>> mteClasses;
        public final IGTHatchAdder<MultiMachineBase<?>> adder;
        private final String descriptionLangKey;

        @SafeVarargs
        CustomHatchElement(String descriptionLangKey, IGTHatchAdder<MultiMachineBase<?>> adder,
            Class<? extends IMetaTileEntity>... mteClasses) {
            this.descriptionLangKey = descriptionLangKey;
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public String getDescriptionLangKey() {
            return descriptionLangKey;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        public IGTHatchAdder<? super MultiMachineBase<?>> adder() {
            return adder;
        }
    }

    public boolean addParallelControllerToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof ParallelControllerHatch hatch) {
            hatch.updateTexture(aBaseCasingIndex);
            hatch.updateCraftingIcon(this.getMachineCraftingIcon());
            return mParallelControllerHatches.add(hatch);
        }
        return false;
    }

    public boolean addTectechDynamoToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof MTEHatchDynamoMulti hatch) {
            hatch.updateTexture(aBaseCasingIndex);
            hatch.updateCraftingIcon(this.getMachineCraftingIcon());
            return mExoticDynamoHatches.add(hatch);
        }
        return false;
    }

    public ParallelControllerHatch getSingleParallelControllerHatch() {
        if (mParallelControllerHatches.size() != 1) {
            return null;
        }
        ParallelControllerHatch parallelControllerHatch = mParallelControllerHatches.get(0);
        if (parallelControllerHatch == null || !parallelControllerHatch.isValid()) {
            return null;
        }
        return parallelControllerHatch;
    }

    public MTEHatchOutput getPrimaryOutputHatch() {
        if (mOutputHatches.isEmpty()) {
            return null;
        }
        MTEHatchOutput outputHatch = mOutputHatches.get(0);
        if (outputHatch == null || !outputHatch.isValid()) {
            return null;
        }
        return outputHatch;
    }

    @SuppressWarnings("unchecked")
    public T self() {
        return (T) this;
    }
}
