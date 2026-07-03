package com.science.gtnl.mixins.late.gregtech;

import java.util.Optional;

import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
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
    private boolean syncDrainedStatus(boolean original,
        @Share("sharedInputCost") LocalRef<FluidStack> sharedInputCostRef) {
        if (original) return true;

        FluidStack inputCost = sharedInputCostRef.get();
        if (inputCost == null) return false;

        for (IDualInputHatch tHatch : this.mDualInputHatches) {
            if (!tHatch.supportsFluids()) continue;

            Optional<IDualInputInventory> inventoryOpt = tHatch.getFirstNonEmptyInventory();
            if (inventoryOpt.isPresent()) {
                IDualInputInventory inventory = inventoryOpt.get();
                for (FluidStack stored : inventory.getFluidInputs()) {
                    if (stored != null && stored.amount >= inputCost.amount && stored.isFluidEqual(inputCost)) {
                        stored.amount -= inputCost.amount;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // @Unique
    // private boolean gtnl$tempDrained = false;
    //
    // @Redirect(
    // method = "runMachine",
    // at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;iterator()Ljava/util/Iterator;", ordinal = 1))
    // private Iterator<MTEHatchInput> redirectHatchIterator(ArrayList<MTEHatchInput> instance,
    // @Local(name = "inputCost") FluidStack inputCost) {
    // Iterator<MTEHatchInput> originalIterator = instance.iterator();
    // return new Iterator<>() {
    //
    // @Override
    // public boolean hasNext() {
    // boolean hasNext = originalIterator.hasNext();
    // if (!hasNext) {
    // return gtnl$checkDualInputHatches(inputCost);
    // }
    // return true;
    // }
    //
    // @Override
    // public MTEHatchInput next() {
    // return originalIterator.next();
    // }
    // };
    // }
    //
    // @Unique
    // private boolean gtnl$checkDualInputHatches(FluidStack inputCost) {
    // for (IDualInputHatch tHatch : this.mDualInputHatches) {
    // if (!tHatch.supportsFluids()) continue;
    //
    // Optional<IDualInputInventory> inventoryOpt = tHatch.getFirstNonEmptyInventory();
    // if (inventoryOpt.isPresent()) {
    // IDualInputInventory inventory = inventoryOpt.get();
    // for (FluidStack stored : inventory.getFluidInputs()) {
    // if (stored != null && stored.amount >= inputCost.amount && stored.isFluidEqual(inputCost)) {
    // stored.amount -= inputCost.amount;
    // gtnl$tempDrained = true;
    // return false;
    // }
    // }
    // }
    // }
    // return false;
    // }
    //
    // @ModifyVariable(
    // method = "runMachine",
    // at = @At(value = "LOAD", ordinal = 0),
    // ordinal = 0
    // )
    // private boolean syncDrainedStatus(boolean original) {
    // boolean result = original || this.gtnl$tempDrained;
    // this.gtnl$tempDrained = false;
    // return result;
    // }
}
