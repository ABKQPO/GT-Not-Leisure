package com.science.gtnl.mixins.late.thaumicEnergistics;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.block.blocks.tile.IMultiEssentiaContainer;
import com.science.gtnl.compat.thaumicenergistics.ThaumicEnergisticsCompat;

import appeng.api.config.Actionable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumicenergistics.api.IThETransportPermissions;
import thaumicenergistics.common.integration.tc.EssentiaTileContainerHelper;

@Mixin(value = EssentiaTileContainerHelper.class, remap = false)
public class MixinEssentiaTileContainerHelper {

    @Shadow
    @Final
    public IThETransportPermissions perms;

    @Inject(method = "injectEssentiaIntoContainer", at = @At("HEAD"), cancellable = true)
    private void gtnl$injectIntoMultiEssentiaContainer(IAspectContainer container, int amountToFill,
        Aspect aspectToFill, Actionable mode, CallbackInfoReturnable<Long> cir) {
        if (!(container instanceof IMultiEssentiaContainer multiContainer)) return;
        cir.setReturnValue(
            ThaumicEnergisticsCompat
                .injectIntoMultiContainer(container, multiContainer, amountToFill, aspectToFill, mode, perms));
    }
}
