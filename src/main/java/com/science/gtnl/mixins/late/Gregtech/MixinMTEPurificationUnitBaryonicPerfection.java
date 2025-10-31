package com.science.gtnl.mixins.late.Gregtech;

import java.util.Optional;

import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Local;

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

    @ModifyVariable(method = "runMachine", at = @At("STORE"), ordinal = 0)
    private boolean redirectDrainedInitialization(boolean original, @Local(name = "inputCost") FluidStack inputCost) {

        for (IDualInputHatch tHatch : this.mDualInputHatches) {
            if (!tHatch.supportsFluids()) continue;

            Optional<IDualInputInventory> inventoryOpt = tHatch.getFirstNonEmptyInventory();
            if (inventoryOpt.isPresent()) {
                IDualInputInventory inventory = inventoryOpt.get();
                for (FluidStack stored : Lists.newArrayList(inventory.getFluidInputs())) {
                    if (stored != null && stored.amount > 0
                        && stored.isFluidEqual(inputCost)
                        && stored.amount >= inputCost.amount) {
                        stored.amount -= inputCost.amount;
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
