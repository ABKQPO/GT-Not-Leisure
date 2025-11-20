package com.science.gtnl.common.machine.multiMachineBase;

import static com.science.gtnl.utils.Utils.filterValidMTEs;
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.ArrayUtils;
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
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputHatchME;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.data.GTCoilTracker;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.IDualInputInventoryWithPattern;
import gregtech.common.tileentities.machines.ISmartInputHatch;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gtPlusPlus.GTplusplus;
import gtPlusPlus.api.objects.minecraft.BlockPos;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import lombok.Getter;
import lombok.Setter;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;

public abstract class MultiMachineBase<T extends MultiMachineBase<T>> extends MTEExtendedPowerMultiBlockBase<T>
    implements IConstructable, ISurvivalConstructable {

    public MultiMachineBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public MultiMachineBase(String aName) {
        super(aName);
    }

    public ArrayList<MTEHatch> mExoticDynamoHatches = new ArrayList<>();
    public ArrayList<ParallelControllerHatch> mParallelControllerHatches = new ArrayList<>();

    public GTCoilTracker.MultiCoilLease coilLease = null;

    public static final int CHECK_INTERVAL = 100; // How often should we check for a new recipe on an idle machine?
    public int randomTickOffset = (int) (Math.random() * CHECK_INTERVAL + 1);

    public int mCountCasing = -1;
    public int mGlassTier = -1;
    public int mParallelTier = 0;
    public int mEnergyHatchTier = 0;
    public int mMaxParallel = 0;
    public double mConfigSpeedBoost = 1;

    @Getter
    @Setter
    public HeatingCoilLevel mCoilLevel = HeatingCoilLevel.None;
    public int mHeatingCapacity = 0;

    public static Map<ItemStack, Integer> PARALLEL_TIERS = new HashMap<>() {

        {
            put(GTNLItemList.LVParallelControllerCore.get(1), 1);
            put(GTNLItemList.MVParallelControllerCore.getInternalStack_unsafe(), 2);
            put(GTNLItemList.HVParallelControllerCore.getInternalStack_unsafe(), 3);
            put(GTNLItemList.EVParallelControllerCore.getInternalStack_unsafe(), 4);
            put(GTNLItemList.IVParallelControllerCore.getInternalStack_unsafe(), 5);
            put(GTNLItemList.LuVParallelControllerCore.getInternalStack_unsafe(), 6);
            put(GTNLItemList.ZPMParallelControllerCore.getInternalStack_unsafe(), 7);
            put(GTNLItemList.UVParallelControllerCore.getInternalStack_unsafe(), 8);
            put(GTNLItemList.UHVParallelControllerCore.getInternalStack_unsafe(), 9);
            put(GTNLItemList.UEVParallelControllerCore.getInternalStack_unsafe(), 10);
            put(GTNLItemList.UIVParallelControllerCore.getInternalStack_unsafe(), 11);
            put(GTNLItemList.UMVParallelControllerCore.getInternalStack_unsafe(), 12);
            put(GTNLItemList.UXVParallelControllerCore.getInternalStack_unsafe(), 13);
            put(GTNLItemList.MAXParallelControllerCore.getInternalStack_unsafe(), 14);
        }
    };

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(172, 67));
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            mTotalRunTime++;
            if (mEfficiency < 0) mEfficiency = 0;

            if (mUpdated) {
                if (mUpdate <= 0) mUpdate = 50;
                mUpdated = false;
            }

            if (--mUpdate == 0 || --mStartUpCheck == 0) {
                checkStructure(true, aBaseMetaTileEntity);
            }

            if (mStartUpCheck < 0) {
                if (mMachine) {
                    checkMaintenance();
                    if (getRepairStatus() > 0) {
                        runMachine(aBaseMetaTileEntity, aTick);
                    } else if (aBaseMetaTileEntity.isAllowedToWork()) {
                        stopMachine(ShutDownReasonRegistry.NO_REPAIR);
                    }
                } else if (aBaseMetaTileEntity.isAllowedToWork()) {
                    stopMachine(ShutDownReasonRegistry.STRUCTURE_INCOMPLETE);
                }
            }

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

            boolean isActive = mMaxProgresstime > 0;

            if (!mMachine || !isActive) {
                deactivateCoilLease();
            }

            if (mMachine && !mCoils.isEmpty() && isActive && coilLease == null) {
                coilLease = GTCoilTracker.activate(this, mCoils);
            }
        } else {
            doActivitySound(getActivitySoundLoop());
        }
    }

    @Override
    public void runMachine(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (mMaxProgresstime > 0 && doRandomMaintenanceDamage()) {
            if (onRunningTick(mInventory[1])) {
                markDirty();
                if (!polluteEnvironment(getPollutionPerTick(mInventory[1]))) {
                    stopMachine(ShutDownReasonRegistry.POLLUTION_FAIL);
                }
                if (mMaxProgresstime > 0 && ++mProgresstime >= mMaxProgresstime) {
                    if (mOutputItems != null) {
                        for (ItemStack tStack : mOutputItems) {
                            if (tStack != null) {
                                addOutput(tStack);
                            }
                        }
                        mOutputItems = null;
                    }
                    if (mOutputFluids != null) {
                        addFluidOutputs(mOutputFluids);
                        mOutputFluids = null;
                    }
                    outputAfterRecipe();
                    mEfficiency = Math.max(
                        0,
                        Math.min(
                            mEfficiency + mEfficiencyIncrease,
                            getMaxEfficiency(getControllerSlot()) - ((getIdealStatus() - getRepairStatus()) * 1000)));
                    mOutputItems = null;
                    mProgresstime = 0;
                    mMaxProgresstime = 0;
                    mEfficiencyIncrease = 0;
                    mLastWorkingTick = mTotalRunTime;
                    if (aBaseMetaTileEntity.isAllowedToWork()) {
                        checkRecipe();
                    }
                }
            }
        } else {
            // Check if the machine is enabled in the first place!
            if (aBaseMetaTileEntity.isAllowedToWork()) {

                if (shouldCheckRecipeThisTick(aTick) || aBaseMetaTileEntity.hasWorkJustBeenEnabled()
                    || aBaseMetaTileEntity.hasInventoryBeenModified()) {
                    if (checkRecipe()) {
                        markDirty();
                    }
                }
            }
            if (mMaxProgresstime <= 0) mEfficiency = Math.max(0, mEfficiency - 1000);
        }
    }

    public boolean shouldCheckRecipeThisTick(long aTick) {
        // do a recipe check if any crafting input hatch just got pushed in items
        boolean shouldCheck = false;
        // check all of them (i.e. do not return early) to reset the state of all of them.
        for (IDualInputHatch craftingInputMe : mDualInputHatches) {
            shouldCheck |= craftingInputMe.justUpdated();
        }
        if (shouldCheck) return true;
        // Do the same for Smart Input Hatches
        for (ISmartInputHatch smartInputHatch : mSmartInputHatches) {
            shouldCheck |= smartInputHatch.justUpdated();
        }
        if (shouldCheck) return true;

        // Perform more frequent recipe change after the machine just shuts down.
        long timeElapsed = mTotalRunTime - mLastWorkingTick;

        if (timeElapsed >= CHECK_INTERVAL) return (mTotalRunTime + randomTickOffset) % CHECK_INTERVAL == 0;
        // Batch mode should be a lot less aggressive at recipe checking
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

    /**
     * Creates logic to run recipe check based on recipemap. This runs only once, on class instantiation.
     * <p>
     * If this machine doesn't use recipemap or does some complex things, override {@link #checkProcessing()}.
     */
    @ApiStatus.OverrideOnly
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @Override
            @Nonnull
            public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
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
     * Proxy Perfect Overclock Supplier.
     *
     * @return If true, enable Perfect Overclock.
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
        return false;
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
     * Proxy Standard Eu Modifier Supplier.
     *
     * @return The value (or a method to get the value) of Eu Modifier (dynamically) .
     */
    @Deprecated
    @ApiStatus.OverrideOnly
    public double getEuModifier() {
        return 1.0F;
    }

    /**
     * Proxy Standard Speed Multiplier Supplier.
     *
     * @return The value (or a method to get the value) of Speed Multiplier (dynamically) .
     */
    @Deprecated
    @ApiStatus.OverrideOnly
    public double getSpeedBonus() {
        return 1.0F;
    }

    /**
     * Proxy Standard Parallel Supplier.
     *
     * @return The value (or a method to get the value) of Max Parallel (dynamically) .
     */
    @ApiStatus.OverrideOnly
    public abstract int getMaxParallelRecipes();

    public abstract int getCasingTextureID();

    public long getRealMaxInputAmps() {
        return getMaxWorkingInputAmps(getExoticAndNormalEnergyHatchList());
    }

    public static long getMaxWorkingInputAmps(Collection<? extends MTEHatch> hatches) {
        List<Long> ampsList = new ArrayList<>();
        for (MTEHatch tHatch : validMTEList(hatches)) {
            long currentAmp = tHatch.maxWorkingAmperesIn();
            ampsList.add(currentAmp);
        }

        if (ampsList.isEmpty()) {
            return 0L;
        }

        return Collections.max(ampsList);
    }

    public long getMachineVoltageLimit() {
        return GTValues.V[mEnergyHatchTier];
    }

    public int checkEnergyHatchTier() {
        int tier = 0;
        for (MTEHatchEnergy tHatch : validMTEList(mEnergyHatches)) {
            tier = Math.max(tHatch.mTier, tier);
        }
        for (MTEHatch tHatch : validMTEList(mExoticEnergyHatches)) {
            tier = Math.max(tHatch.mTier, tier);
        }
        return tier;
    }

    public int getParallelTier(ItemStack inventory) {
        if (inventory == null) return 0;
        for (Map.Entry<ItemStack, Integer> entry : PARALLEL_TIERS.entrySet()) {
            if (GTUtility.areStacksEqual(inventory, entry.getKey())) {
                return entry.getValue();
            }
        }
        return 0;
    }

    public List<SlotWidget> slotWidgets = new ArrayList<>(1);

    public void createInventorySlots() {
        final SlotWidget inventorySlot = new SlotWidget(inventoryHandler, 1);
        inventorySlot.setBackground(GTUITextures.SLOT_DARK_GRAY);
        slotWidgets.add(inventorySlot);
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
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.PICTURE_SCREEN_BLACK)
                .setPos(4, 4)
                .setSize(190, 85));

        slotWidgets.clear();
        createInventorySlots();

        Column slotsColumn = new Column();
        for (int i = slotWidgets.size() - 1; i >= 0; i--) {
            slotsColumn.widget(slotWidgets.get(i));
        }
        builder.widget(
            slotsColumn.setAlignment(MainAxisAlignment.END)
                .setPos(173, 167 - 1));

        final DynamicPositionedColumn screenElements = new DynamicPositionedColumn();
        drawTexts(screenElements, !slotWidgets.isEmpty() ? slotWidgets.get(0) : null);
        builder.widget(
            new Scrollable().setVerticalScroll()
                .widget(screenElements.setPos(10, 0))
                .setPos(0, 7)
                .setSize(190, 79));

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
            .widget(createMuffleButton(builder));

        DynamicPositionedRow configurationElements = new DynamicPositionedRow();
        addConfigurationWidgets(configurationElements, buildContext);

        if (supportsPowerPanel()) {
            builder.widget(createPowerPanelButton(builder));
            buildContext.addSyncedWindow(POWER_PANEL_WINDOW_ID, this::createPowerPanel);
        }

        builder.widget(
            configurationElements.setSpace(2)
                .setAlignment(MainAxisAlignment.SPACE_BETWEEN)
                .setPos(getRecipeLockingButtonPos().add(18, 0)));
    }

    @Override
    public ButtonWidget createMuffleButton(IWidgetBuilder<?> builder) {
        return (ButtonWidget) new ButtonWidget().setOnClick((clickData, widget) -> setMuffled(!isMuffled()))
            .setPlayClickSound(true)
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

    /**
     * Checks recipe and setup machine if it's successful.
     * <p>
     * For generic machine working with recipemap, use {@link #createProcessingLogic()} to make use of shared codebase.
     */
    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        // If no logic is found, try legacy checkRecipe
        if (processingLogic == null) {
            return checkRecipe(mInventory[1]) ? CheckRecipeResultRegistry.SUCCESSFUL
                : CheckRecipeResultRegistry.NO_RECIPE;
        }

        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        result = postCheckRecipe(result, processingLogic);
        // inputs are consumed at this point
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
    @Nonnull
    public CheckRecipeResult doCheckRecipe() {
        CheckRecipeResult result = CheckRecipeResultRegistry.NO_RECIPE;

        // check crafting input hatches first
        for (IDualInputHatch dualInputHatch : mDualInputHatches) {
            ItemStack[] sharedItems = dualInputHatch.getSharedItems();
            for (var it = dualInputHatch.inventories(); it.hasNext();) {
                IDualInputInventory slot = it.next();

                if (!slot.isEmpty()) {
                    // try to cache the possible recipes from pattern
                    if (slot instanceof IDualInputInventoryWithPattern withPattern) {
                        if (!processingLogic.tryCachePossibleRecipesFromPattern(withPattern)) {
                            // move on to next slots if it returns false, which means there is no possible recipes with
                            // given pattern.
                            continue;
                        }
                    }

                    processingLogic.setInputItems(ArrayUtils.addAll(sharedItems, slot.getItemInputs()));
                    processingLogic.setInputFluids(slot.getFluidInputs());

                    CheckRecipeResult foundResult = processingLogic.process();
                    if (foundResult.wasSuccessful()) {
                        return foundResult;
                    }
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) {
                        // Recipe failed in interesting way, so remember that and continue searching
                        result = foundResult;
                    }
                }
            }
        }

        result = checkRecipeForCustomHatches(result);
        if (result.wasSuccessful()) {
            return result;
        }

        // Use hatch colors if any; fallback to color 1 otherwise.
        short hatchColors = getHatchColors();
        boolean doColorChecking = hatchColors != 0;
        if (!doColorChecking) hatchColors = 0b1;

        for (byte color = 0; color < (doColorChecking ? 16 : 1); color++) {
            if (isColorAbsent(hatchColors, color)) continue;
            processingLogic.setInputFluids(getStoredFluidsForColor(Optional.of(color)));
            if (isInputSeparationEnabled()) {
                if (mInputBusses.isEmpty()) {
                    CheckRecipeResult foundResult = processingLogic.process();
                    if (foundResult.wasSuccessful()) return foundResult;
                    // Recipe failed in interesting way, so remember that and continue searching
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                } else {
                    for (MTEHatchInputBus bus : mInputBusses) {
                        if (bus instanceof MTEHatchCraftingInputME || bus instanceof SuperCraftingInputHatchME)
                            continue;
                        byte busColor = bus.getColor();
                        if (busColor != -1 && busColor != color) continue;
                        List<ItemStack> inputItems = new ArrayList<>();
                        for (int i = bus.getSizeInventory() - 1; i >= 0; i--) {
                            ItemStack stored = bus.getStackInSlot(i);
                            if (stored != null) inputItems.add(stored);
                        }
                        if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                            inputItems.add(getControllerSlot());
                        }
                        processingLogic.setInputItems(inputItems);
                        CheckRecipeResult foundResult = processingLogic.process();
                        if (foundResult.wasSuccessful()) return foundResult;
                        // Recipe failed in interesting way, so remember that and continue searching
                        if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                    }
                }
            } else {
                List<ItemStack> inputItems = getStoredInputsForColor(Optional.of(color));
                if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                    inputItems.add(getControllerSlot());
                }
                processingLogic.setInputItems(inputItems);
                CheckRecipeResult foundResult = processingLogic.process();
                if (foundResult.wasSuccessful()) return foundResult;
                // Recipe failed in interesting way, so remember that
                if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
            }
        }
        return result;
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

    public boolean depleteInputFromRestrictedHatches(Collection<CustomFluidHatch> aHatches, int aAmount) {
        for (CustomFluidHatch tHatch : validMTEList(aHatches)) {
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

    // region Overrides
    @Override
    public String[] getInfoData() {
        String dSpeed = String.format("%.3f", this.getSpeedBonus() * 100) + "%";
        String dEUMod = String.format("%.3f", this.getEuModifier() * 100) + "%";

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

    public boolean addInputBusOrOutputBusToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return addInputBusToMachineList(aTileEntity, aBaseCasingIndex)
            || addOutputBusToMachineList(aTileEntity, aBaseCasingIndex);
    }

    public boolean checkHatch() {
        return mMaintenanceHatches.size() <= 1 && (this.getPollutionPerSecond(null) <= 0 || !mMufflerHatches.isEmpty())
            && mParallelControllerHatches.size() <= 1;
    }

    public boolean checkEnergyHatch() {
        if (MainConfig.enableMachineAmpLimit) {
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
     * Enable Texture Casing Support if found in GT 5.09
     */
    public boolean updateTexture(final IGregTechTileEntity aTileEntity, int aCasingID) {
        return updateTexture(getMetaTileEntity(aTileEntity), aCasingID);
    }

    /**
     * Enable Texture Casing Support if found in GT 5.09
     */
    public boolean updateTexture(final IMetaTileEntity aTileEntity, int aCasingID) {
        if (aTileEntity instanceof MTEHatch mteHatch) {
            mteHatch.updateTexture(aCasingID);
            return true;
        }
        return false;
    }

    @Override
    public boolean checkStructure(boolean aForceReset) {
        return checkStructure(aForceReset, getBaseMetaTileEntity());
    }

    @Override
    public boolean checkStructure(boolean aForceReset, IGregTechTileEntity aBaseMetaTileEntity) {
        if (!aBaseMetaTileEntity.isServerSide()) return mMachine;
        // Only trigger an update if forced (from onPostTick, generally), or if the structure has changed
        if ((mStructureChanged || aForceReset)) {
            clearHatches();

            mMachine = checkMachine(aBaseMetaTileEntity, mInventory[1]);
            updateHatchTexture();

            doStructureValidation();
        }
        mStructureChanged = false;
        return mMachine;
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
                if (GTplusplus.CURRENT_LOAD_PHASE == GTplusplus.INIT_PHASE.STARTED) {
                    ScienceNotLeisure.LOG.warn(
                        "Adding {} at {}",
                        aTileEntity.getInventoryName(),
                        new BlockPos(aTileEntity.getBaseMetaTileEntity()).getLocationString());
                }
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
                        if (GTplusplus.CURRENT_LOAD_PHASE == GTplusplus.INIT_PHASE.STARTED) {
                            ScienceNotLeisure.LOG
                                .warn("Found Duplicate {} at {}", b.getInventoryName(), aPos.getLocationString());
                        }
                        return false;
                    }
                }
            }
            if (aTileEntity instanceof MTEHatch) {
                if (GTplusplus.CURRENT_LOAD_PHASE == GTplusplus.INIT_PHASE.STARTED) {
                    ScienceNotLeisure.LOG.warn("Adding {} at {}", aCur.getInventoryName(), aCurPos.getLocationString());
                }
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
        long injected = 0;
        long totalOutput = 0;
        long aFirstVoltageFound = -1;
        for (MTEHatchDynamo aDynamo : filterValidMTEs(mDynamoHatches)) {
            long aVoltage = aDynamo.maxEUOutput();
            long aTotal = aDynamo.maxAmperesOut() * aVoltage;
            // Check against voltage to check when hatch mixing
            if (aFirstVoltageFound == -1) {
                aFirstVoltageFound = aVoltage;
            }
            totalOutput += aTotal;
        }
        for (MTEHatch aDynamo : filterValidMTEs(mExoticDynamoHatches)) {
            long aVoltage = aDynamo.maxEUOutput();
            long aTotal = aDynamo.maxAmperesOut() * aVoltage;
            // Check against voltage to check when hatch mixing
            if (aFirstVoltageFound == -1) {
                aFirstVoltageFound = aVoltage;
            }
            totalOutput += aTotal;
        }

        long actualOutputEU = Math.min(totalOutput, aEU);

        long leftToInject;
        long aVoltage;
        int aAmpsToInject;
        int aRemainder;
        int ampsOnCurrentHatch;
        for (MTEHatchDynamo aDynamo : filterValidMTEs(mDynamoHatches)) {
            leftToInject = actualOutputEU - injected;
            aVoltage = aDynamo.maxEUOutput();
            aAmpsToInject = (int) (leftToInject / aVoltage);
            aRemainder = (int) (leftToInject - (aAmpsToInject * aVoltage));
            ampsOnCurrentHatch = (int) Math.min(aDynamo.maxAmperesOut(), aAmpsToInject);
            for (int i = 0; i < ampsOnCurrentHatch; i++) {
                aDynamo.getBaseMetaTileEntity()
                    .increaseStoredEnergyUnits(aVoltage, false);
            }
            injected += aVoltage * ampsOnCurrentHatch;
            if (aRemainder > 0 && ampsOnCurrentHatch < aDynamo.maxAmperesOut()) {
                aDynamo.getBaseMetaTileEntity()
                    .increaseStoredEnergyUnits(aRemainder, false);
                injected += aRemainder;
            }
        }
        for (MTEHatch aDynamo : filterValidMTEs(mExoticDynamoHatches)) {
            leftToInject = actualOutputEU - injected;
            aVoltage = aDynamo.maxEUOutput();
            aAmpsToInject = (int) (leftToInject / aVoltage);
            aRemainder = (int) (leftToInject - (aAmpsToInject * aVoltage));
            ampsOnCurrentHatch = (int) Math.min(aDynamo.maxAmperesOut(), aAmpsToInject);
            for (int i = 0; i < ampsOnCurrentHatch; i++) {
                aDynamo.getBaseMetaTileEntity()
                    .increaseStoredEnergyUnits(aVoltage, false);
            }
            injected += aVoltage * ampsOnCurrentHatch;
            if (aRemainder > 0 && ampsOnCurrentHatch < aDynamo.maxAmperesOut()) {
                aDynamo.getBaseMetaTileEntity()
                    .increaseStoredEnergyUnits(aRemainder, false);
                injected += aRemainder;
            }
        }
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
    public boolean getDefaultBatchMode() {
        return false;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return true;
    }

    @Override
    public int getRecipeCatalystPriority() {
        return -1;
    }

    public int getMaxParallel() {
        return mMaxParallel;
    }

    public void setMaxParallel(int parallel) {
        mMaxParallel = parallel;
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
        for (MTEHatchEnergy tHatch : validMTEList(mEnergyHatches)) rVoltage += tHatch.getBaseMetaTileEntity()
            .getInputVoltage();
        for (MTEHatch tHatch : validMTEList(mExoticEnergyHatches)) rVoltage += tHatch.getBaseMetaTileEntity()
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
        for (MTEHatchEnergy tHatch : validMTEList(mEnergyHatches)) {
            IGregTechTileEntity baseTile = tHatch.getBaseMetaTileEntity();
            eut += baseTile.getInputVoltage() * baseTile.getInputAmperage();
        }
        for (MTEHatch tHatch : validMTEList(mExoticEnergyHatches)) {
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
}
