package com.science.gtnl.mixins.late.appliedEnergistics.wireless;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.science.gtnl.common.wireless.WirelessPathingState;

import appeng.me.cache.PathGridCache;

@Mixin(value = PathGridCache.class, remap = false)
public abstract class MixinWirelessPathGridCache implements WirelessPathingState {

    @Shadow
    private boolean updateNetwork;

    @Shadow
    private boolean recalculateControllerNextTick;

    @Override
    public boolean gtnl$isRepathPending() {
        return updateNetwork || recalculateControllerNextTick;
    }
}
