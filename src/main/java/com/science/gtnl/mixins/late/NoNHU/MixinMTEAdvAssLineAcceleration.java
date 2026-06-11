package com.science.gtnl.mixins.late.NoNHU;

import net.minecraft.item.ItemStack;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.api.mixinHelper.IAccelerationState;

import ggfab.mte.MTEAdvAssLine;

@Pseudo
@Mixin(value = MTEAdvAssLine.class, remap = false)
public abstract class MixinMTEAdvAssLineAcceleration implements IAccelerationState {

    @Shadow
    private boolean stuck;

    @Unique
    private boolean gtnl$isAccelerationState;

    @Override
    public boolean gtnl$getMachineAccelerationState() {
        return stuck;
    }

    @Override
    public void gtnl$setIsAccelerationState(boolean state) {
        gtnl$isAccelerationState = state;
    }

    @Inject(
        method = "onRunningTick",
        at = @At(
            value = "FIELD",
            target = "Lggfab/mte/MTEAdvAssLine;baseEUt:J",
            ordinal = 0,
            opcode = Opcodes.GETFIELD),
        cancellable = true)
    private void gtnl$modifyDrainEnergy(ItemStack aStack, CallbackInfoReturnable<Boolean> cir) {
        if (gtnl$isAccelerationState) cir.setReturnValue(true);
    }

}
