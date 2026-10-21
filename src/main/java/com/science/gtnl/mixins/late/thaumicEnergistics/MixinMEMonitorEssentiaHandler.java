package com.science.gtnl.mixins.late.thaumicEnergistics;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.block.blocks.tile.IMultiEssentiaContainer;

import thaumcraft.api.aspects.IAspectContainer;
import thaumicenergistics.common.inventory.MEMonitorEssentiaHandler;
import thaumicenergistics.common.storage.AEEssentiaStack;

@Mixin(value = MEMonitorEssentiaHandler.class, remap = false)
public class MixinMEMonitorEssentiaHandler {

    @Shadow
    @Final
    private IAspectContainer aspectContainer;

    @Inject(
        method = "canAccept(Lthaumicenergistics/common/storage/AEEssentiaStack;)Z",
        at = @At("HEAD"),
        cancellable = true)
    private void gtnl$acceptEveryStorableAspect(AEEssentiaStack input, CallbackInfoReturnable<Boolean> cir) {
        if (aspectContainer instanceof IMultiEssentiaContainer) {
            cir.setReturnValue(input != null && aspectContainer.doesContainerAccept(input.getAspect()));
        }
    }
}
