package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.machine.hatch.BeamlinePipeMirror;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gtnhlanth.common.hatch.MTEHatchBeamlineConnector;
import gtnhlanth.common.hatch.MTEHatchInputBeamline;
import gtnhlanth.common.hatch.MTEHatchOutputBeamline;

@Mixin(value = MTEHatchOutputBeamline.class, remap = false)
public abstract class MixinMTEHatchOutputBeamline extends MTEHatchBeamlineConnector {

    protected MixinMTEHatchOutputBeamline(int aID, String aName, String aNameRegional, int aTier, String descr) {
        super(aID, aName, aNameRegional, aTier, descr);
    }

    @Inject(method = "moveAround", at = @At("HEAD"), cancellable = true)
    private void redirectedMoveAround(IGregTechTileEntity aBaseMetaTileEntity, CallbackInfo ci) {
        MTEHatchInputBeamline input = BeamlinePipeMirror.findInputAlongMirrors(aBaseMetaTileEntity);
        if (input != null) {
            input.setContents(dataPacket);
            this.dataPacket = null;
        }
        ci.cancel();
    }
}
