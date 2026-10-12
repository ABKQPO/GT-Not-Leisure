package com.science.gtnl.mixins.late.appliedEnergistics.wireless;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.wireless.WirelessChannelManager;
import com.science.gtnl.common.wireless.WirelessClusterManager;

import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridNode;
import appeng.me.GridNode;

@Mixin(value = GridNode.class, remap = false)
public abstract class MixinWirelessGridNode {

    @Inject(
        method = { "addConnection", "removeConnection" },
        at = @At(value = "INVOKE", target = "Lappeng/api/networking/IGridConnection;hasDirection()Z"))
    private void gtnl$connectionsChanged(IGridConnection gridConnection, CallbackInfo ci) {
        WirelessChannelManager.connectionsChanged((IGridNode) this);
    }

    @Inject(method = "updateState", at = @At("RETURN"))
    private void gtnl$nodeUpdated(CallbackInfo ci) {
        WirelessClusterManager.nodeChanged((IGridNode) this);
    }

    @Inject(method = "destroy", at = @At("HEAD"))
    private void gtnl$nodeDestroyed(CallbackInfo ci) {
        WirelessClusterManager.nodeChanged((IGridNode) this);
    }
}
