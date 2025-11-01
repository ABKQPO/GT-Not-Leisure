package com.science.gtnl.common.machine.multiblock.module.nanitesIntegratedProcessingCenter;

import static gregtech.api.enums.GTValues.V;

import javax.annotation.Nonnull;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;

public abstract class NanitesBaseModule<T extends NanitesBaseModule<T>> extends WirelessEnergyMultiMachineBase<T> {

    protected boolean isConnected = false;
    protected boolean isOreModule = false;
    protected boolean isBioModule = false;
    protected boolean isPolModule = false;
    protected double setEUtDiscount = 0;
    protected double setDurationModifier = 0;
    protected int setMaxParallel = 0;
    private static final int HORIZONTAL_OFF_SET = 7;
    private static final int VERTICAL_OFF_SET = 17;
    private static final int DEPTH_OFF_SET = 0;
    protected int mHeatingCapacity = 0;

    public NanitesBaseModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public NanitesBaseModule(String aName) {
        super(aName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("NanitesIntegratedProcessingCenterRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(15, 18, 31, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_NanitesBaseModule_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_NanitesBaseModule_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_NanitesBaseModule_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_NanitesBaseModule_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_NanitesBaseModule_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide() && isConnected) {
            super.onPostTick(aBaseMetaTileEntity, aTick);
            if (mEfficiency < 0) mEfficiency = 0;
        }
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        isOreModule = false;
        isBioModule = false;
        isPolModule = false;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @Nonnull
            @Override
            protected GTNL_OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(mHeatingCapacity)
                    .setEUtDiscount(setEUtDiscount)
                    .setDurationModifier(setDurationModifier)
                    .setPerfectOC(true);
            }

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                if (recipe.mEUt > V[Math.min(mParallelTier + 1, 14)] * 4 && wirelessMode) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                return recipe.mSpecialValue <= mHeatingCapacity ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    public void connect() {
        isConnected = true;
    }

    public void disconnect() {
        isConnected = false;
    }

    public void getOreModule() {
        isOreModule = false;
    }

    public void getBioModule() {
        isBioModule = false;
    }

    public void getPolModule() {
        isPolModule = false;
    }

    @Override
    public double getEUtDiscount() {
        return setEUtDiscount;
    }

    public void setEUtDiscount(double discount) {
        setEUtDiscount = discount;
    }

    @Override
    public double getDurationModifier() {
        return setDurationModifier;
    }

    public void setDurationModifier(double boost) {
        setDurationModifier = boost;
    }

    @Override
    public int getMaxParallel() {
        return setMaxParallel;
    }

    @Override
    public void setMaxParallel(int parallel) {
        setMaxParallel = parallel;
    }

    public int getHeatingCapacity() {
        return mHeatingCapacity;
    }

    public void setHeatingCapacity(int HeatingCapacity) {
        mHeatingCapacity = HeatingCapacity;
    }

}
