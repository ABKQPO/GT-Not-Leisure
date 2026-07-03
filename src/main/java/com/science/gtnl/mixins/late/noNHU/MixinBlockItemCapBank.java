package com.science.gtnl.mixins.late.noNHU;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import crazypants.enderio.machine.capbank.BlockItemCapBank;
import crazypants.enderio.machine.capbank.CapBankType;

@Mixin(value = BlockItemCapBank.class, remap = false)
public class MixinBlockItemCapBank {

    @Redirect(
        method = "receiveEnergy",
        at = @At(value = "INVOKE", target = "Lcrazypants/enderio/machine/capbank/CapBankType;getMaxIO()I"),
        require = 1)
    private int modifyMaxIO(CapBankType instance) {
        if (instance == CapBankType.VIBRANT) {
            return Integer.MAX_VALUE;
        }
        return instance.getMaxIO();
    }
}
