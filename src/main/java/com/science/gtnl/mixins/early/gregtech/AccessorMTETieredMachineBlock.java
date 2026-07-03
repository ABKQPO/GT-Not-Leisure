package com.science.gtnl.mixins.early.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import gregtech.api.metatileentity.implementations.MTETieredMachineBlock;

@Mixin(value = MTETieredMachineBlock.class, remap = false)
public interface AccessorMTETieredMachineBlock {

    @Accessor("mTier")
    byte getMachineTier();

    @Mutable
    @Accessor("mTier")
    void setMachineTier(byte mTier);

}
