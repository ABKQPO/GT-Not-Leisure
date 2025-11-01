package com.science.gtnl.mixins.late.NoNHU;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.science.gtnl.api.ITileEntityTickAcceleration;

import tectech.thing.metaTileEntity.multi.MTEResearchStation;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

@Mixin(value = MTEResearchStation.class, remap = false)
public abstract class MixinResearchStationAcceleration extends TTMultiblockBase implements ITileEntityTickAcceleration {

    @Shadow(remap = false)
    private long computationRemaining;

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public boolean tickAcceleration(int tickAcceleratedRate) {
        if (this.computationRemaining <= 0) return true;
        this.computationRemaining -= tickAcceleratedRate * eAvailableData;
        return true;
    }

    public MixinResearchStationAcceleration(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }
}
