package com.science.gtnl.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;

import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.ITexture;
import gregtech.api.metatileentity.implementations.MTETieredMachineBlock;
import gregtech.common.tileentities.generators.MTELightningRod;

@Mixin(value = MTELightningRod.class, remap = false)
public abstract class MixinMTELightningRod extends MTETieredMachineBlock {

    public MixinMTELightningRod(int aID, String aName, String aNameRegional, int aTier, int aInvSlotCount,
        String aDescription, ITexture... aTextures) {
        super(aID, aName, aNameRegional, aTier, aInvSlotCount, aDescription, aTextures);
    }

    @Override
    public long maxEUStore() {
        return 50_000_000L * (1L << (2 * (mTier - 3)));
    }

    @Override
    public long maxEUOutput() {
        return GTValues.V[mTier] * 64;
    }
}
