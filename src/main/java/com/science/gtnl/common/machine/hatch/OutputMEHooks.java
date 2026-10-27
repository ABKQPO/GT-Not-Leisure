package com.science.gtnl.common.machine.hatch;

import appeng.api.storage.data.IAEStack;
import gregtech.common.tileentities.machines.outputme.base.MTEHatchOutputMEBase;
import gregtech.common.tileentities.machines.outputme.util.AECacheCounter;

public class OutputMEHooks {

    private OutputMEHooks() {}

    public static <T extends IAEStack<T>> boolean transferCache(AECacheCounter<T> cache,
        MTEHatchOutputMEBase<T> target) {
        boolean[] transferred = { false };
        cache.updateAll((stack, amount) -> {
            long available = target.getPhysicalSpace();
            if (available <= 0) return amount;
            long amountToMove = Math.min(amount, available);
            target.addToCache(
                stack.copy()
                    .setStackSize(amountToMove));
            transferred[0] = true;
            return amount - amountToMove;
        });
        return transferred[0];
    }
}
