package com.science.gtnl.mixins.late.tecTech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import tectech.thing.metaTileEntity.multi.MTEResearchStation;

@Mixin(value = MTEResearchStation.class, remap = false)
public interface AccessorMTEResearchStation {

    @Accessor
    long getPacketLossDecayFrom();

    @Accessor
    void setPacketLossDecayFrom(long value);
}
