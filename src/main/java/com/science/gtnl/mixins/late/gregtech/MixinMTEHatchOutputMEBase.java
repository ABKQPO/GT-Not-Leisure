package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.science.gtnl.api.mixinHelper.IOutputMEProviderTransfer;
import com.science.gtnl.common.machine.hatch.OutputMEHooks;

import appeng.api.storage.data.IAEStack;
import gregtech.common.tileentities.machines.outputme.base.MTEHatchOutputMEBase;
import gregtech.common.tileentities.machines.outputme.util.AECacheCounter;

@Mixin(value = MTEHatchOutputMEBase.class, remap = false)
public abstract class MixinMTEHatchOutputMEBase<T extends IAEStack<T>> implements IOutputMEProviderTransfer<T> {

    @Final
    @Shadow
    protected AECacheCounter<T> cache;

    @Override
    public boolean gtnl$transferCacheTo(MTEHatchOutputMEBase<T> targetProvider) {
        return OutputMEHooks.transferCache(cache, targetProvider);
    }
}
