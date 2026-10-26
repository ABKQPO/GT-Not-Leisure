package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableMap;
import com.science.gtnl.utils.structure.GTNLMultiblockTooltipBuilder;

import gregtech.GTMod;
import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReason;

@IMetaTileEntity.SkipGenerateDescription
public class SteamMonsterRepellentModule extends SteamElevatorModuleBase {

    public static final String[] TIER_TITLES = { "basic", "advanced", "elite" };

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
    public void stopMachine(@NotNull ShutDownReason reason) {
        final IGregTechTileEntity mte = this.getBaseMetaTileEntity();
        if (mte.isServerSide()) {
            GTMod.proxy.spawnEventHandler.removeRepellent(mte);
        }
        super.stopMachine(reason);
    }

    @Override
    public void onRemoval() {
        final IGregTechTileEntity mte = this.getBaseMetaTileEntity();
        if (mte.isServerSide()) {
            GTMod.proxy.spawnEventHandler.removeRepellent(mte);
        }
        super.onRemoval();
    }

    @Override
    public void onUnload() {
        final IGregTechTileEntity mte = this.getBaseMetaTileEntity();
        if (mte.isServerSide()) {
            GTMod.proxy.spawnEventHandler.removeRepellent(mte);
        }
        super.onUnload();
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
        this.lEUt = mTier * GTValues.V[3];
        this.mEfficiency = 10000;
        this.mMaxProgresstime = 1000;
        IGregTechTileEntity mte = getBaseMetaTileEntity();
        if (mte.isAllowedToWork() && mte.isServerSide()) {
            GTMod.proxy.spawnEventHandler.putRepellent(mte, mRange);
        }
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("gtnl.machine.steam_monster_repellent_module.recipe_type");
    }

    @Override
    public Map<String, Object> getTooltipVars() {
        return ImmutableMap.of(
            "tier_title",
            TIER_TITLES[Math.min(Math.max(mTier - 1, 0), TIER_TITLES.length - 1)],
            "range",
            getMachineEffectRange());
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip(this)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamMonsterRepellentModule(this.mName, this.mTier);
    }

    @Override
    public int getMachineEffectRange() {
        return mRange;
    }
}
