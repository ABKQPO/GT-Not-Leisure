package com.science.gtnl.mixins.late.gregtech;

import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.science.gtnl.common.machine.purification.PurificationUnitHooks;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitBaryonicPerfection;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitBase;

@Mixin(value = MTEPurificationUnitBaryonicPerfection.class, remap = false)
public abstract class MixinMTEPurificationUnitBaryonicPerfection
    extends MTEPurificationUnitBase<MixinMTEPurificationUnitBaryonicPerfection> {

    public MixinMTEPurificationUnitBaryonicPerfection(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Inject(
        method = "runMachine",
        at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;iterator()Ljava/util/Iterator;", ordinal = 1))
    private void captureInputCost(IGregTechTileEntity aBaseMetaTileEntity, long aTick, CallbackInfo ci,
        @Share("sharedInputCost") LocalRef<FluidStack> sharedInputCostRef,
        @Local(name = "inputCost") FluidStack inputCost) {
        sharedInputCostRef.set(inputCost);
    }

    @ModifyVariable(method = "runMachine", at = @At(value = "LOAD", ordinal = 0), name = "drained")
    private boolean syncDrainedStatus(boolean drained,
        @Share("sharedInputCost") LocalRef<FluidStack> sharedInputCostRef) {
        if (drained) return true;
        return PurificationUnitHooks.drainFromDualHatches(this.mDualInputHatches, sharedInputCostRef.get());
    }
}
