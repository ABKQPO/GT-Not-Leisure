package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.science.gtnl.api.mixinHelper.IOutputMEProviderTransfer;

import appeng.api.storage.data.IAEStack;
import gregtech.common.tileentities.machines.outputme.base.MTEHatchOutputMEBase;
import gregtech.common.tileentities.machines.outputme.util.AECacheCounter;

@Mixin(value = MTEHatchOutputMEBase.class, remap = false)
public abstract class MixinMTEHatchOutputMEBase<T extends IAEStack<T>> implements IOutputMEProviderTransfer<T> {

    @Shadow
    protected AECacheCounter<T> cache;

    @Override
    public boolean gtnl$transferCacheTo(MTEHatchOutputMEBase<T> targetProvider) {
        boolean[] transferred = { false };

        cache.updateAll((stack, amount) -> {
            long available = targetProvider.getAvailableSpace();
            if (available <= 0) {
                return amount;
            }

            long amountToMove = Math.min(amount, available);
            T movingStack = stack.copy()
                .setStackSize(amountToMove);

            targetProvider.addToCache(movingStack);
            transferred[0] = true;
            return amount - amountToMove;
        });

        return transferred[0];
    }
}
