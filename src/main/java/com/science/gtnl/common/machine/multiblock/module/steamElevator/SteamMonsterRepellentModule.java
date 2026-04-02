package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import static gregtech.api.enums.GTValues.V;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import gregtech.GTMod;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReason;

public class SteamMonsterRepellentModule extends SteamElevatorModule {

    public int mRange;

    public SteamMonsterRepellentModule(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
        mRange = 1 << (5 + aTier);
    }

    public SteamMonsterRepellentModule(String aName, int aTier) {
        super(aName, aTier);
        mRange = 1 << (5 + aTier);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamMonsterRepellentModule(this.mName, this.mTier);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamMonsterRepellentModuleRecipeType");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SteamMonsterRepellentModuleRecipeType"));
        switch (mTier) {
            case 1 -> tt.addInfo(StatCollector.translateToLocal("Tooltip_SteamMonsterRepellentModuleI_00"));
            case 2 -> tt.addInfo(StatCollector.translateToLocal("Tooltip_SteamMonsterRepellentModuleII_00"));
            case 3 -> tt.addInfo(StatCollector.translateToLocal("Tooltip_SteamMonsterRepellentModuleIII_00"));
        }
        tt.addInfo(StatCollector.translateToLocal("Tooltip_SteamMonsterRepellentModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamMonsterRepellentModule_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamMonsterRepellentModule_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamMonsterRepellentModule_03") + (1 << (5 + mTier)))
            .beginStructureBlock(1, 5, 2, false)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return null;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mRange", mRange);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mRange = aNBT.getInteger("mRange");
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        this.lEUt = mTier * V[3];
        this.mEfficiency = 10000;
        this.mMaxProgresstime = 1000;
        IGregTechTileEntity mte = getBaseMetaTileEntity();
        if (mte.isAllowedToWork() && mte.isServerSide()) {
            GTMod.gregtechproxy.spawnEventHandler.putRepellent(mte, mRange);
        }
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public void stopMachine(@Nonnull ShutDownReason reason) {
        final IGregTechTileEntity mte = this.getBaseMetaTileEntity();
        if (mte.isServerSide()) {
            GTMod.gregtechproxy.spawnEventHandler.removeRepellent(mte);
        }
        super.stopMachine(reason);
    }

    @Override
    public void onRemoval() {
        final IGregTechTileEntity mte = this.getBaseMetaTileEntity();
        if (mte.isServerSide()) {
            GTMod.gregtechproxy.spawnEventHandler.removeRepellent(mte);
        }
        super.onRemoval();
    }

    @Override
    public void onUnload() {
        final IGregTechTileEntity mte = this.getBaseMetaTileEntity();
        if (mte.isServerSide()) {
            GTMod.gregtechproxy.spawnEventHandler.removeRepellent(mte);
        }
        super.onUnload();
    }

    @Override
    public int getMachineEffectRange() {
        return mRange;
    }
}
