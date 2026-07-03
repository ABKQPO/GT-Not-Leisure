package com.science.gtnl.mixins.late.randomComplement;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.utils.RCQuantumCluster;

import appeng.me.cluster.implementations.QuantumCluster;
import appeng.tile.grid.AENetworkInvTile;
import appeng.tile.qnb.TileQuantumBridge;
import appeng.util.Platform;

@Mixin(value = TileQuantumBridge.class, remap = false)
public abstract class MixinTileQuantumBridge extends AENetworkInvTile {

    @Shadow
    private QuantumCluster cluster;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        this.getProxy()
            .setIdlePowerUsage(0);
    }

    @Inject(method = "isPowered", at = @At("HEAD"), cancellable = true)
    public void isPoweredI(CallbackInfoReturnable<Boolean> cir) {
        if (!Platform.isClient()) {
            if (this.cluster == null) {
                cir.setReturnValue(true);
            } else if (((RCQuantumCluster) this.cluster).r$noWork()) {
                cir.setReturnValue(true);
            }
        }
    }
}
