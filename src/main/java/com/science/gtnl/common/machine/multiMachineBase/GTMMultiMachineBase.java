package com.science.gtnl.common.machine.multiMachineBase;

import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.NotNull;

import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;

import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTUtility;

public abstract class GTMMultiMachineBase<T extends GTMMultiMachineBase<T>> extends MultiMachineBase<T> {

    public GTMMultiMachineBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GTMMultiMachineBase(String aName) {
        super(aName);
    }

    @Override
    public boolean getPerfectOC() {
        return false;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("parallelTier", mParallelTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mParallelTier = aNBT.getInteger("parallelTier");
    }

    @Override
    public double getEUtDiscount() {
        return 0.8 - (mParallelTier / 50.0);
    }

    @Override
    public double getDurationModifier() {
        return 1 / 1.67 - (Math.max(0, mParallelTier - 1) / 50.0);
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        resetParallelTier();
    }

    @Override
    public void resetParallelTier() {
        super.resetParallelTier();
        mParallelTier = getParallelTier(getControllerSlot());
        for (ParallelControllerHatch module : GTUtility.filterValidMTEs(mParallelControllerHatches)) {
            mParallelTier = module.mTier;
            break;
        }
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && checkEnergyHatch();
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty() && getMaxInputAmps() <= 4;
        logic.setAvailableVoltage(getMachineVoltageLimit());
        logic.setAvailableAmperage(
            useSingleAmp ? 1
                : ExoticEnergyInputHelper.getMaxWorkingInputAmpsMulti(getExoticAndNormalEnergyHatchList()));
        logic.setAmperageOC(!useSingleAmp);
    }

    @Override
    public int getMaxParallelRecipes() {
        resetParallelTier();

        for (ParallelControllerHatch module : GTUtility.filterValidMTEs(mParallelControllerHatches)) {
            mParallelTier = module.mTier;
            return module.getParallel();
        }

        if (mParallelTier <= 1) {
            return 8;
        }

        return 1 << (2 * (mParallelTier - 2));
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        resetParallelTier();
        return super.checkProcessing();
    }

}
