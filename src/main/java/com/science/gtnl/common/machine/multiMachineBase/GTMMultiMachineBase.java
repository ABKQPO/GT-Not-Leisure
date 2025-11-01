package com.science.gtnl.common.machine.multiMachineBase;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.util.ExoticEnergyInputHelper;

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
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if (mParallelControllerHatches.size() == 1 && aTick % 20 == 0) {
                for (ParallelControllerHatch module : mParallelControllerHatches) {
                    setMaxParallel(module.getParallel());
                    mParallelTier = module.mTier;
                }
            } else {
                setMaxParallel(8);
            }
            if (mEfficiency < 0) mEfficiency = 0;
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public boolean getAmperageOC() {
        return true;
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
        mParallelTier = getParallelTier(getControllerSlot());
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && checkEnergyHatch();
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty() && getMaxInputAmps() <= 4;
        logic.setAvailableVoltage(getMachineVoltageLimit());
        logic.setAvailableAmperage(
            useSingleAmp ? 1
                : ExoticEnergyInputHelper.getMaxWorkingInputAmpsMulti(getExoticAndNormalEnergyHatchList()));
        logic.setAmperageOC(!useSingleAmp);
    }

    @Override
    public int getMaxParallelRecipes() {
        mParallelTier = getParallelTier(getControllerSlot());

        if (mParallelControllerHatches.size() == 1) {
            ParallelControllerHatch module = mParallelControllerHatches.get(0);
            mParallelTier = module.mTier;
            return module.getParallel();
        }

        if (mParallelTier <= 1) {
            return 8;
        }

        return 1 << (2 * (mParallelTier - 2));
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        mParallelTier = 0;
        ItemStack controllerItem = getControllerSlot();
        int parallelTierItem = getParallelTier(controllerItem);
        mParallelTier = Math.max(mParallelTier, parallelTierItem);
        return super.checkProcessing();
    }

}
