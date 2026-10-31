package com.science.gtnl.mixins.early.noNHU;

import org.spongepowered.asm.mixin.Mixin;

import com.science.gtnl.api.ITileEntityTickAcceleration;
import com.science.gtnl.common.tickrate.TickAccelerationHooks;

import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.CommonBaseMetaTileEntity;

@Mixin(value = BaseMetaTileEntity.class, remap = false)
public abstract class MixinBaseMetaTileEntity extends CommonBaseMetaTileEntity implements ITileEntityTickAcceleration {

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public boolean tickAcceleration(int tickAcceleratedRate) {
        return TickAccelerationHooks.accelerate((BaseMetaTileEntity) (Object) this, tickAcceleratedRate);
    }
}
