package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.machine.hatch.BeamlinePipeMirror;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gtnhlanth.common.beamline.MTEBeamlinePipe;
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
        ForgeDirection front = aBaseMetaTileEntity.getFrontFacing();
        ForgeDirection opposite = front.getOpposite();

        for (short dist = 1; dist <= 129; dist++) {
            IGregTechTileEntity tGTTileEntity = aBaseMetaTileEntity
                .getIGregTechTileEntityAtSideAndDistance(front, dist);

            if (tGTTileEntity != null) {
                IMetaTileEntity aMetaTileEntity = tGTTileEntity.getMetaTileEntity();

                if (aMetaTileEntity instanceof BeamlinePipeMirror tMirror) {
                    tGTTileEntity = tMirror.bendAround(opposite);
                    if (tGTTileEntity == null) break;

                    aMetaTileEntity = tGTTileEntity.getMetaTileEntity();
                    opposite = tMirror.chainedFrontFacing;
                }

                if (aMetaTileEntity instanceof MTEHatchInputBeamline inputBeamline
                    && opposite == tGTTileEntity.getFrontFacing()) {
                    inputBeamline.setContents(dataPacket);
                    this.dataPacket = null;
                    ci.cancel();
                    return;
                } else if (aMetaTileEntity instanceof MTEBeamlinePipe beamlinePipe) {
                    beamlinePipe.markUsed();
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        ci.cancel();
    }
}
