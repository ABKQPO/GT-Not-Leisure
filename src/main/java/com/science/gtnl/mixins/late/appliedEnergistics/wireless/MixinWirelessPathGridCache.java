package com.science.gtnl.mixins.late.appliedEnergistics.wireless;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.wireless.IWirelessPathingState;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.me.cache.PathGridCache;

@Mixin(value = PathGridCache.class, remap = false)
public abstract class MixinWirelessPathGridCache implements IWirelessPathingState {

    @Shadow
    private boolean updateNetwork;

    @Shadow
    private boolean recalculateControllerNextTick;

    @Unique
    private long gtnl$topologyRevision;

    @Override
    public boolean gtnl$isRepathPending() {
        return updateNetwork || recalculateControllerNextTick;
    }

    @Override
    public long gtnl$topologyRevision() {
        return gtnl$topologyRevision;
    }

    @Override
    public void gtnl$topologyChanged() {
        gtnl$topologyRevision++;
    }

    @Inject(method = { "addNode", "removeNode" }, at = @At("HEAD"))
    private void gtnl$membershipChanged(IGridNode node, IGridHost machine, CallbackInfo ci) {
        gtnl$topologyRevision++;
    }
}
