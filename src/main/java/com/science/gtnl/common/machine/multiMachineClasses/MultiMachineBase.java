package com.science.gtnl.common.machine.multiMachineClasses;

import static com.science.gtnl.Utils.Utils.filterValidMTEs;
import static com.science.gtnl.Utils.item.TextHandler.texter;
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizons.modularui.api.math.MainAxisAlignment;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ITileWithModularUI;
import com.gtnewhorizons.modularui.api.screen.ModularUIContext;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.builder.UIBuilder;
import com.gtnewhorizons.modularui.common.builder.UIInfo;
import com.gtnewhorizons.modularui.common.internal.wrapper.ModularGui;
import com.gtnewhorizons.modularui.common.internal.wrapper.ModularUIContainer;
import com.gtnewhorizons.modularui.common.widget.Column;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedRow;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.science.gtnl.Utils.item.ItemUtils;
import com.science.gtnl.Utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.Utils.recipes.GTNL_ProcessingLogic;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;
import com.science.gtnl.common.machine.hatch.CustomMaintenanceHatch;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;

import gregtech.api.enums.GTValues;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchMaintenance;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.IGTHatchAdder;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gtPlusPlus.GTplusplus;
import gtPlusPlus.api.objects.Logger;
import gtPlusPlus.api.objects.minecraft.BlockPos;
import gtPlusPlus.core.config.ASMConfiguration;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.METHatchAirIntake;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchInputBattery;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchOutputBattery;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;

public abstract class MultiMachineBase<T extends MultiMachineBase<T>> extends MTEExtendedPowerMultiBlockBase<T>
    implements IConstructable, ISurvivalConstructable {

    protected int tCountCasing;
    protected int mGlassTier = 0;
    protected int mParallelTier;
    protected int energyHatchTier;
    protected int mMaxParallel = 0;
    protected int cleanroomTier = 0;
    protected double configSpeedBoost = 1;

    // region Class Constructor
    public MultiMachineBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public MultiMachineBase(String aName) {
        super(aName);
    }

    // endregion
    /**
     * This is the array Used to Store the Tectech Multi-Amp Dynamo hatches.
     */
    public ArrayList<MTEHatch> mTecTechDynamoHatches = new ArrayList<>();
    public ArrayList<ParallelControllerHatch> mParallelControllerHatches = new ArrayList<>();
    public ArrayList<CustomFluidHatch> FluidIceInputHatch = new ArrayList<>();
    public ArrayList<CustomFluidHatch> FluidBlazeInputHatch = new ArrayList<>();
    public ArrayList<CustomFluidHatch> FluidManaInputHatch = new ArrayList<>();

    /**
     * This is the array Used to Store the Tectech Multi-Amp Energy hatches.
     */
    public ArrayList<MTEHatch> mTecTechEnergyHatches = new ArrayList<>();

    public ArrayList<METHatchAirIntake> mAirIntakes = new ArrayList<>();

    public ArrayList<MTEHatchInputBattery> mChargeHatches = new ArrayList<>();
    public ArrayList<MTEHatchOutputBattery> mDischargeHatches = new ArrayList<>();
    public ArrayList<MTEHatch> mAllEnergyHatches = new ArrayList<>();
    public ArrayList<MTEHatch> mAllDynamoHatches = new ArrayList<>();

    // region new methods
    public void repairMachine() {
        mHardHammer = true;
        mSoftMallet = true;
        mScrewdriver = true;
        mCrowbar = true;
        mSolderingTool = true;
        mWrench = true;
    }

    // endregion

    // region Processing Logic

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
            if (aTick % 20 == 0) {
                boolean found = false;
                for (MTEHatchMaintenance module : mMaintenanceHatches) {
                    if (module instanceof CustomMaintenanceHatch customMaintenanceHatch) {
                        int maintenanceCleanroomTier = customMaintenanceHatch.getCleanroomTier();
                        cleanroomTier = Math.max(cleanroomTier, maintenanceCleanroomTier);
                        if (customMaintenanceHatch.isConfiguration())
                            configSpeedBoost = customMaintenanceHatch.getConfigTime() / 100d;
                        found = true;
                    }
                }
                if (!found) {
                    configSpeedBoost = 1;
                    cleanroomTier = 0;
                }
            }
            if (mEfficiency < 0) mEfficiency = 0;
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    /**
     * Creates logic to run recipe check based on recipemap. This runs only once, on class instantiation.
     * <p>
     * If this machine doesn't use recipemap or does some complex things, override {@link #checkProcessing()}.
     */
    @ApiStatus.OverrideOnly
    protected ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setEuModifier(getEuModifier());
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
                return super.process();
            }

            @NotNull
            @Override
            public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(configSpeedBoost);
            }

        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    /**
     * Proxy Perfect Overclock Supplier.
     *
     * @return If true, enable Perfect Overclock.
     */
    protected abstract boolean isEnablePerfectOverclock();

    /**
     * Proxy Standard Eu Modifier Supplier.
     *
     * @return The value (or a method to get the value) of Eu Modifier (dynamically) .
     */
    @ApiStatus.OverrideOnly
    protected float getEuModifier() {
        return 1.0F;
    }

    /**
     * Proxy Standard Speed Multiplier Supplier.
     *
     * @return The value (or a method to get the value) of Speed Multiplier (dynamically) .
     */
    @ApiStatus.OverrideOnly
    protected abstract float getSpeedBonus();

    /**
     * Proxy Standard Parallel Supplier.
     *
     * @return The value (or a method to get the value) of Max Parallel (dynamically) .
     */
    @ApiStatus.OverrideOnly
    public abstract int getMaxParallelRecipes();

    public abstract int getCasingTextureID();

    protected long getMachineVoltageLimit() {
        return GTValues.V[energyHatchTier];
    }

    protected int checkEnergyHatchTier() {
        int tier = 0;
        for (MTEHatchEnergy tHatch : validMTEList(mEnergyHatches)) {
            tier = Math.max(tHatch.mTier, tier);
        }
        for (MTEHatch tHatch : validMTEList(mExoticEnergyHatches)) {
            tier = Math.max(tHatch.mTier, tier);
        }
        return tier;
    }

    @FunctionalInterface
    protected interface ContainerConstructor<T extends MultiMachineBase<?>> {

        ModularUIContainer of(ModularUIContext context, ModularWindow mainWindow, T multiBlock);
    }

    @SuppressWarnings("unchecked")
    protected static <K extends MultiMachineBase<?>> UIInfo<?, ?> createMetaTileEntityUI(
        MultiMachineBase.ContainerConstructor<K> containerConstructor) {
        return UIBuilder.of()
            .container((player, world, x, y, z) -> {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof BaseMetaTileEntity) {
                    IMetaTileEntity mte = ((BaseMetaTileEntity) te).getMetaTileEntity();
                    if (!(mte instanceof MultiMachineBase)) return null;
                    final UIBuildContext buildContext = new UIBuildContext(player);
                    final ModularWindow window = ((ITileWithModularUI) te).createWindow(buildContext);
                    return containerConstructor.of(new ModularUIContext(buildContext, te::markDirty), window, (K) mte);
                }
                return null;
            })
            .gui(((player, world, x, y, z) -> {
                if (!world.isRemote) return null;
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof BaseMetaTileEntity) {
                    IMetaTileEntity mte = ((BaseMetaTileEntity) te).getMetaTileEntity();
                    if (!(mte instanceof MultiMachineBase)) return null;
                    final UIBuildContext buildContext = new UIBuildContext(player);
                    final ModularWindow window = ((ITileWithModularUI) te).createWindow(buildContext);
                    return new ModularGui(
                        containerConstructor.of(new ModularUIContext(buildContext, null), window, (K) mte));
                }
                return null;
            }))
            .build();
    }

    protected List<SlotWidget> slotWidgets = new ArrayList<>(1);

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

        builder.widget(createPowerSwitchButton(builder))
            .widget(createVoidExcessButton(builder))
            .widget(createInputSeparationButton(builder))
            .widget(createBatchModeButton(builder))
            .widget(createLockToSingleRecipeButton(builder))
            .widget(createStructureUpdateButton(builder));

        DynamicPositionedRow configurationElements = new DynamicPositionedRow();
        addConfigurationWidgets(configurationElements, buildContext);

        builder.widget(
            configurationElements.setSpace(2)
                .setAlignment(MainAxisAlignment.SPACE_BETWEEN)
                .setPos(getRecipeLockingButtonPos().add(18, 0)));
    }

    protected void addConfigurationWidgets(DynamicPositionedRow configurationElements, UIBuildContext buildContext) {

    }

    /**
     * Prevent overflow during power consumption calculation.
     *
     * @return Eu consumption per tick.
     */
    @Override
    protected long getActualEnergyUsage() {
        return (long) (-this.lEUt * (10000.0 / Math.max(1000, mEfficiency)));
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

    protected boolean depleteInputFromRestrictedHatches(Collection<CustomFluidHatch> aHatches, int aAmount) {
        for (final CustomFluidHatch tHatch : validMTEList(aHatches)) {
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
        ret[origin.length] = EnumChatFormatting.AQUA + texter("Parallels", "MachineInfoData.Parallels")
            + ": "
            + EnumChatFormatting.GOLD
            + this.getTrueParallel();
        ret[origin.length + 1] = EnumChatFormatting.AQUA + texter("Speed multiplier", "MachineInfoData.SpeedMultiplier")
            + ": "
            + EnumChatFormatting.GOLD
            + dSpeed;
        ret[origin.length + 2] = EnumChatFormatting.AQUA + texter("EU Modifier", "MachineInfoData.EuModifier")
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

    @Override
    public void explodeMultiblock() {
        MetaTileEntity tTileEntity;
        for (final Iterator<MTEHatchInputBattery> localIterator = this.mChargeHatches.iterator(); localIterator
            .hasNext(); tTileEntity.getBaseMetaTileEntity()
                .doExplosion(GTValues.V[8])) {
            tTileEntity = localIterator.next();
        }
        for (final Iterator<MTEHatchOutputBattery> localIterator = this.mDischargeHatches.iterator(); localIterator
            .hasNext(); tTileEntity.getBaseMetaTileEntity()
                .doExplosion(GTValues.V[8])) {
            tTileEntity = localIterator.next();
        }
        for (final Iterator<MTEHatch> localIterator = this.mTecTechDynamoHatches.iterator(); localIterator
            .hasNext(); tTileEntity.getBaseMetaTileEntity()
                .doExplosion(GTValues.V[8])) {
            tTileEntity = localIterator.next();
        }
        for (final Iterator<MTEHatch> localIterator = this.mTecTechEnergyHatches.iterator(); localIterator
            .hasNext(); tTileEntity.getBaseMetaTileEntity()
                .doExplosion(GTValues.V[8])) {
            tTileEntity = localIterator.next();
        }

        super.explodeMultiblock();
    }

    public boolean checkHatch() {
        return mMaintenanceHatches.size() <= 1 && (this.getPollutionPerSecond(null) <= 0 || !mMufflerHatches.isEmpty())
            && mParallelControllerHatches.size() <= 1;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        this.mChargeHatches.clear();
        this.mDischargeHatches.clear();
        this.mAirIntakes.clear();
        this.mTecTechEnergyHatches.clear();
        this.mTecTechDynamoHatches.clear();
        this.mAllEnergyHatches.clear();
        this.mAllDynamoHatches.clear();
        this.mParallelControllerHatches.clear();
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
                ((MTEHatchInput) aTileEntity).mRecipeMap = null;
                ((MTEHatchInput) aTileEntity).mRecipeMap = aMap;
                if (aMap != null) {
                    log("Remapped Input Hatch to " + aMap.unlocalizedName + ".");
                } else {
                    log("Cleared Input Hatch.");
                }
            } else if (aTileEntity instanceof MTEHatchInputBus) {
                ((MTEHatchInputBus) aTileEntity).mRecipeMap = null;
                ((MTEHatchInputBus) aTileEntity).mRecipeMap = aMap;
                if (aMap != null) {
                    log("Remapped Input Bus to " + aMap.unlocalizedName + ".");
                } else {
                    log("Cleared Input Bus.");
                }
            } else {
                ((MTEHatchSteamBusInput) aTileEntity).mRecipeMap = null;
                ((MTEHatchSteamBusInput) aTileEntity).mRecipeMap = aMap;
                if (aMap != null) {
                    log("Remapped Input Bus to " + aMap.unlocalizedName + ".");
                } else {
                    log("Cleared Input Bus.");
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void log(String s) {
        if (!ASMConfiguration.debug.disableAllLogging) {
            if (ASMConfiguration.debug.debugMode) {
                Logger.INFO(s);
            } else {
                Logger.MACHINE_INFO(s);
            }
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
                    log(
                        "Adding " + aTileEntity.getInventoryName()
                            + " at "
                            + new BlockPos(aTileEntity.getBaseMetaTileEntity()).getLocationString());
                }
                updateTexture(aTileEntity, aBaseCasingIndex);
                return aList.add((E) aTileEntity);
            }
        } else {
            IGregTechTileEntity aCur = aTileEntity.getBaseMetaTileEntity();
            if (aList.contains(aTileEntity)) {
                log(
                    "Found Duplicate " + aTileEntity.getInventoryName()
                        + " @ "
                        + new BlockPos(aCur).getLocationString());
                return false;
            }
            BlockPos aCurPos = new BlockPos(aCur);
            for (E m : aList) {
                IGregTechTileEntity b = ((IMetaTileEntity) m).getBaseMetaTileEntity();
                if (b != null) {
                    BlockPos aPos = new BlockPos(b);
                    if (aCurPos.equals(aPos)) {
                        if (GTplusplus.CURRENT_LOAD_PHASE == GTplusplus.INIT_PHASE.STARTED) {
                            log("Found Duplicate " + b.getInventoryName() + " at " + aPos.getLocationString());
                        }
                        return false;
                    }
                }
            }
            if (aTileEntity instanceof MTEHatch) {
                if (GTplusplus.CURRENT_LOAD_PHASE == GTplusplus.INIT_PHASE.STARTED) {
                    log("Adding " + aCur.getInventoryName() + " at " + aCurPos.getLocationString());
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
        if (!mDynamoHatches.isEmpty()) {
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

    protected void updateHatchTexture() {
        for (MTEHatch h : mInputBusses) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mOutputBusses) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mInputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mOutputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mOutputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mMufflerHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mMaintenanceHatches) h.updateTexture(getCasingTextureID());
        for (IDualInputHatch h : mDualInputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mEnergyHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mExoticEnergyHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mDynamoHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mParallelControllerHatches) h.updateTexture(getCasingTextureID());
    }

    public enum ParallelControllerElement implements IHatchElement<MultiMachineBase<?>> {

        ParallelCon(MultiMachineBase::addParallelControllerToMachineList, ParallelControllerHatch.class) {

            @Override
            public long count(MultiMachineBase<?> tileEntity) {
                return tileEntity.mParallelControllerHatches.size();
            }
        };

        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<MultiMachineBase<?>> adder;

        @SafeVarargs
        ParallelControllerElement(IGTHatchAdder<MultiMachineBase<?>> adder,
            Class<? extends IMetaTileEntity>... mteClasses) {
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

    public boolean addFluidManaInputHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        } else {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof CustomFluidHatch hatch && aMetaTileEntity.getBaseMetaTileEntity()
                .getMetaTileID() == 21501) {
                hatch.updateTexture(aBaseCasingIndex);
                hatch.updateCraftingIcon(this.getMachineCraftingIcon());
                return addToMachineListInternal(FluidManaInputHatch, aTileEntity, aBaseCasingIndex);
            }
        }
        return false;
    }

    public boolean addFluidIceInputHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        } else {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof CustomFluidHatch hatch && aMetaTileEntity.getBaseMetaTileEntity()
                .getMetaTileID() == 21502) {
                hatch.updateTexture(aBaseCasingIndex);
                hatch.updateCraftingIcon(this.getMachineCraftingIcon());
                return addToMachineListInternal(FluidIceInputHatch, aTileEntity, aBaseCasingIndex);
            }
        }
        return false;
    }

    public boolean addFluidBlazeInputHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        } else {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof CustomFluidHatch hatch && aMetaTileEntity.getBaseMetaTileEntity()
                .getMetaTileID() == 21503) {
                hatch.updateTexture(aBaseCasingIndex);
                hatch.updateCraftingIcon(this.getMachineCraftingIcon());
                return addToMachineListInternal(FluidBlazeInputHatch, aTileEntity, aBaseCasingIndex);
            }
        }
        return false;
    }

    // endregion
}
