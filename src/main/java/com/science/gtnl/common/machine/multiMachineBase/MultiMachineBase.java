package com.science.gtnl.common.machine.multiMachineBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
import com.science.gtnl.common.gui.modularui.GTNLMultiBlockBaseGui;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputHatchME;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;
import com.science.gtnl.utils.structure.GTNLStructureErrors;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
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
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.data.GTCoilTracker;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.IDualInputInventoryWithPattern;
import gregtech.common.tileentities.machines.ISmartInputHatch;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gtPlusPlus.api.objects.minecraft.BlockPos;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import lombok.Getter;
import lombok.Setter;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;

public abstract class MultiMachineBase<T extends MultiMachineBase<T>> extends MTEExtendedPowerMultiBlockBase<T>
    implements IConstructable, ISurvivalConstructable, IControllerInfo {

    public MultiMachineBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public MultiMachineBase(String aName) {
        super(aName);
    }

    public ArrayList<MTEHatch> mExoticDynamoHatches = new ArrayList<>();
    public ArrayList<ParallelControllerHatch> mParallelControllerHatches = new ArrayList<>();

    public GTCoilTracker.MultiCoilLease coilLease = null;
    public final ArrayList<ItemStack> recipeSearchItemInputs = new ArrayList<>();
    public final ArrayList<FluidStack> recipeSearchFluidInputs = new ArrayList<>();
    public static final Optional<Byte>[] HATCH_COLOR_OPTIONS = createHatchColorOptions();

    public static final int CHECK_INTERVAL = 100; // 空闲机器的配方轮询间隔 / Recipe polling interval for idle machines
    public int randomTickOffset = (int) (Math.random() * CHECK_INTERVAL + 1);

    public int mCountCasing = -1;
    public int mGlassTier = -1;
    public int mParallelTier = 0;
    public int mEnergyHatchTier = 0;
    public double mConfigSpeedBoost = 1;

    @Getter
    @Setter
    public HeatingCoilLevel mCoilLevel = HeatingCoilLevel.None;
    public int mHeatingCapacity = 0;

    @SuppressWarnings("unchecked")
    public static Optional<Byte>[] createHatchColorOptions() {
        Optional<Byte>[] colorOptions = new Optional[16];
        for (byte color = 0; color < colorOptions.length; color++) {
            colorOptions[color] = Optional.of(color);
        }
        return colorOptions;
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
        if (hasUpdatedCraftingInputs()) {
            return true;
        }
        if (aBaseMetaTileEntity != null
            && (aBaseMetaTileEntity.hasWorkJustBeenEnabled() || aBaseMetaTileEntity.hasInventoryBeenModified())) {
            return true;
        }
        long timeElapsed = mTotalRunTime - mLastWorkingTick;
        if (timeElapsed >= CHECK_INTERVAL) return (mTotalRunTime + randomTickOffset) % CHECK_INTERVAL == 0;
        if (!isBatchModeEnabled()) {
            return timeElapsed == 5 || timeElapsed == 12
                || timeElapsed == 20
                || timeElapsed == 30
                || timeElapsed == 40
                || timeElapsed == 55
                || timeElapsed == 70
                || timeElapsed == 85;
        }
        return false;
    }

    public boolean hasUpdatedCraftingInputs() {
        // 任意合成输入仓刚写入物品时立即重查配方 / Recheck recipes immediately when any crafting hatch receives items
        boolean shouldCheck = false;
        // 必须遍历全部仓室以重置更新标记 / Visit every hatch so each update flag gets cleared
        for (IDualInputHatch craftingInputMe : mDualInputHatches) {
            shouldCheck |= craftingInputMe.justUpdated();
        }
        if (shouldCheck) return true;

        for (ISmartInputHatch smartInputHatch : mSmartInputHatches) {
            shouldCheck |= smartInputHatch.justUpdated();
        }
        return shouldCheck;
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

    @ApiStatus.OverrideOnly
    public int getMaxOverclocks() {
        return Integer.MAX_VALUE;
    }

    @ApiStatus.OverrideOnly
    public int getMaxTierSkip() {
        return Integer.MAX_VALUE;
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

    public List<SlotWidget> slotWidgets = new ArrayList<>(1);

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
        clearRecipeMapForAllInputHatches();
        onModeChangeByScrewdriver(side, aPlayer, aX, aY, aZ, aTool);
        resetRecipeMapForAllInputHatches();
    }

    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {

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

            long voltage = dynamo.maxEUOutput();
            int ampsToInject = (int) (leftToInject / voltage);
            int remainder = (int) (leftToInject - (ampsToInject * voltage));
            int ampsOnCurrentHatch = (int) Math.min(dynamo.maxAmperesOut(), ampsToInject);
            for (int i = 0; i < ampsOnCurrentHatch; i++) {
                dynamo.getBaseMetaTileEntity()
                    .increaseStoredEnergyUnits(voltage, false);
            }
            totalInjected += voltage * ampsOnCurrentHatch;
            if (remainder > 0 && ampsOnCurrentHatch < dynamo.maxAmperesOut()) {
                dynamo.getBaseMetaTileEntity()
                    .increaseStoredEnergyUnits(remainder, false);
                totalInjected += remainder;
            }
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
        ret[origin.length] = EnumChatFormatting.AQUA + StatCollector.translateToLocal("MachineInfoData.Parallels")
            + ": "
            + EnumChatFormatting.GOLD
            + this.getTrueParallel();
        ret[origin.length + 1] = EnumChatFormatting.AQUA + StatCollector
            .translateToLocal("MachineInfoData.SpeedMultiplier") + ": " + EnumChatFormatting.GOLD + dSpeed;
        ret[origin.length + 2] = EnumChatFormatting.AQUA + StatCollector.translateToLocal("MachineInfoData.EuModifier")
            + ": "
            + EnumChatFormatting.GOLD
            + dEUMod;
        return ret;
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    public boolean checkHatch() {
        return mMaintenanceHatches.size() <= 1 && (this.getPollutionPerSecond(null) <= 0 || !mMufflerHatches.isEmpty())
            && mParallelControllerHatches.size() <= 1;
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        failStructureCheck(errors);
    }

    protected void validateStructureErrors(List<StructureError> errors) {
        int existingErrors = errors.size();
        checkHatch(errors);
        if (errors.size() == existingErrors) {
            failStructureCheck(errors);
        }
    }

    protected void checkHatch(List<StructureError> errors) {
        int existingErrors = errors.size();
        checkHatchMax(errors, HatchElement.Maintenance, 1);
        if (getPollutionPerSecond(null) > 0) {
            checkHasMufflerHatch(errors);
        }
        checkParallelControllerHatchMax(errors, 1);
        checkEnergyHatch(errors);
        if (!checkHatch() && errors.size() == existingErrors) {
            errors.add(GTNLStructureErrors.invalidHatchConfiguration());
        }
    }

    protected void checkEnergyHatch(List<StructureError> errors) {
        if (checkEnergyHatch()) {
            return;
        }
        if (MainConfig.machine.enableLaserHatch) {
            boolean hasEnergyTunnel = false;
            for (MTEHatch hatch : getExoticEnergyHatches()) {
                if (hatch instanceof MTEHatchEnergyTunnel) {
                    hasEnergyTunnel = true;
                    break;
                }
            }
            if (hasEnergyTunnel) {
                errors.add(GTNLStructureErrors.laserEnergyTunnelDisabled());
            }
            if (getRealMaxInputAmps() > 64) {
                errors.add(GTNLStructureErrors.energyInputAmperageTooHigh());
            }
            return;
        }
        errors.add(GTNLStructureErrors.invalidEnergyHatchConfiguration());
    }

    protected boolean checkPieceAndHatch(String piece, int horizontalOffset, int verticalOffset, int depthOffset,
        List<StructureError> errors) {
        int existingErrors = errors.size();
        if (!checkPiece(piece, horizontalOffset, verticalOffset, depthOffset, errors)) {
            return false;
        }
        checkHatch(errors);
        return errors.size() == existingErrors;
    }

    protected void checkStructureCondition(List<StructureError> errors, boolean condition) {
        if (!condition) {
            failStructureCheck(errors);
        }
    }

    protected void failStructureCheck(List<StructureError> errors) {
        errors.add(GTNLStructureErrors.unknownLegacyCheckFailure());
    }

    protected void checkParallelControllerHatchMax(List<StructureError> errors, int max) {
        int count = mParallelControllerHatches.size();
        if (count > max) {
            errors.add(GTNLStructureErrors.parallelControllerHatchCount(ErrorType.TOO_MANY, count, max));
        }
    }

    protected void checkOneParallelControllerHatch(List<StructureError> errors) {
        int count = mParallelControllerHatches.size();
        if (count != 1) {
            errors.add(GTNLStructureErrors.parallelControllerHatchCount(ErrorType.NOT_MATCH, count, 1));
        }
    }

    public boolean checkEnergyHatch() {
        if (MainConfig.machine.enableLaserHatch) {
            for (MTEHatch hatch : getExoticEnergyHatches()) {
                if (hatch instanceof MTEHatchEnergyTunnel) {
                    return false;
                }
            }
            return getRealMaxInputAmps() <= 64;
        }
        return true;
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
        mParallelTier = 0;
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
    public boolean supportsSingleRecipeLocking() {
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

        ParallelCon(MultiMachineBase::addParallelControllerToMachineList, ParallelControllerHatch.class) {

            @Override
            public long count(MultiMachineBase<?> tileEntity) {
                return tileEntity.mParallelControllerHatches.size();
            }
        },

        ExoticDynamo(MultiMachineBase::addTectechDynamoToMachineList, MTEHatchDynamoMulti.class) {

            @Override
            public long count(MultiMachineBase<?> tileEntity) {
                return tileEntity.mExoticDynamoHatches.size();
            }
        };

        public final List<Class<? extends IMetaTileEntity>> mteClasses;
        public final IGTHatchAdder<MultiMachineBase<?>> adder;

        @SafeVarargs
        CustomHatchElement(IGTHatchAdder<MultiMachineBase<?>> adder, Class<? extends IMetaTileEntity>... mteClasses) {
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
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
