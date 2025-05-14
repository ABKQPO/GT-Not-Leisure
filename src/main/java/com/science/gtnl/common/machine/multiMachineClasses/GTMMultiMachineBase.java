package com.science.gtnl.common.machine.multiMachineClasses;

import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.NotNull;

import com.science.gtnl.api.mixinHelper.IOverclockCalculatorExtension;
import com.science.gtnl.common.GTNLItemList;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.OverclockCalculator;

public abstract class GTMMultiMachineBase<T extends GTMMultiMachineBase<T>> extends MultiMachineBase<T> {

    public GTMMultiMachineBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GTMMultiMachineBase(String aName) {
        super(aName);
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return false;
    }

    @Override
    public float getSpeedBonus() {
        return 1;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("parallelTier", ParallelTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        ParallelTier = aNBT.getInteger("parallelTier");
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if (mParallelControllerHatches.size() == 1 && aTick % 20 == 0) {
                for (ParallelControllerHatch module : mParallelControllerHatches) {
                    setMaxParallel(module.getParallel());
                    ParallelTier = module.mTier;
                }
            } else {
                setMaxParallel(8);
            }
            if (mEfficiency < 0) mEfficiency = 0;
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                OverclockCalculator calc = super.createOverclockCalculator(recipe).setAmperageOC(true)
                    .setDurationDecreasePerOC(2)
                    .setEUtIncreasePerOC(4)
                    .setAmperage(availableAmperage)
                    .setRecipeEUt(recipe.mEUt)
                    .setEUt(availableVoltage)
                    .setEUtDiscount(0.8 - (ParallelTier / 50.0))
                    .setSpeedBoost(1 / 1.67 - (ParallelTier / 200.0));

                ((IOverclockCalculatorExtension) calc).setMoreSpeedBoost(configSpeedBoost);

                return calc;
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty() && getMaxInputAmps() <= 2;
        logic.setAvailableVoltage(getMachineVoltageLimit());
        logic.setAvailableAmperage(
            useSingleAmp ? 1
                : ExoticEnergyInputHelper.getMaxWorkingInputAmpsMulti(getExoticAndNormalEnergyHatchList()));
        logic.setAmperageOC(useSingleAmp);
    }

    @Override
    public int getMaxParallelRecipes() {
        if (mParallelControllerHatches.size() == 1) {
            for (ParallelControllerHatch module : mParallelControllerHatches) {
                ParallelTier = module.mTier;
                return module.getParallel();
            }
        } else if (ParallelTier <= 1) {
            return 8;
        } else {
            return (int) Math.pow(4, ParallelTier - 2);
        }
        return 8;
    }

    public int getParallelTier(ItemStack inventory) {
        if (inventory == null) return 0;
        if (inventory.isItemEqual(GTNLItemList.LVParallelControllerCore.getInternalStack_unsafe())) {
            return 1;
        } else if (inventory.isItemEqual(GTNLItemList.MVParallelControllerCore.getInternalStack_unsafe())) {
            return 2;
        } else if (inventory.isItemEqual(GTNLItemList.HVParallelControllerCore.getInternalStack_unsafe())) {
            return 3;
        } else if (inventory.isItemEqual(GTNLItemList.EVParallelControllerCore.getInternalStack_unsafe())) {
            return 4;
        } else if (inventory.isItemEqual(GTNLItemList.IVParallelControllerCore.getInternalStack_unsafe())) {
            return 5;
        } else if (inventory.isItemEqual(GTNLItemList.LuVParallelControllerCore.getInternalStack_unsafe())) {
            return 6;
        } else if (inventory.isItemEqual(GTNLItemList.ZPMParallelControllerCore.getInternalStack_unsafe())) {
            return 7;
        } else if (inventory.isItemEqual(GTNLItemList.UVParallelControllerCore.getInternalStack_unsafe())) {
            return 8;
        } else if (inventory.isItemEqual(GTNLItemList.UHVParallelControllerCore.getInternalStack_unsafe())) {
            return 9;
        } else if (inventory.isItemEqual(GTNLItemList.UEVParallelControllerCore.getInternalStack_unsafe())) {
            return 10;
        } else if (inventory.isItemEqual(GTNLItemList.UIVParallelControllerCore.getInternalStack_unsafe())) {
            return 11;
        } else if (inventory.isItemEqual(GTNLItemList.UMVParallelControllerCore.getInternalStack_unsafe())) {
            return 12;
        } else if (inventory.isItemEqual(GTNLItemList.UXVParallelControllerCore.getInternalStack_unsafe())) {
            return 13;
        } else if (inventory.isItemEqual(GTNLItemList.MAXParallelControllerCore.getInternalStack_unsafe())) {
            return 14;
        }
        return 0;
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        ItemStack controllerItem = getControllerSlot();
        int ParallelTierItem = getParallelTier(controllerItem);
        ParallelTier = Math.max(ParallelTier, ParallelTierItem);
        return super.checkProcessing();
    }

    @Override
    public long getMaxInputAmps() {
        return getMaxInputAmpsHatch(getExoticAndNormalEnergyHatchList());
    }

    public static long getMaxInputAmpsHatch(Collection<? extends MTEHatch> hatches) {
        List<Long> ampsList = new ArrayList<>();
        for (MTEHatch tHatch : validMTEList(hatches)) {
            long currentAmp = tHatch.getBaseMetaTileEntity()
                .getInputAmperage();
            ampsList.add(currentAmp);
        }

        if (ampsList.isEmpty()) {
            return 0L;
        }

        return Collections.max(ampsList);
    }

}
