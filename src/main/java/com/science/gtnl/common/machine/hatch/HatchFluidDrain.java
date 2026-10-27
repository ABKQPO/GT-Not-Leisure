package com.science.gtnl.common.machine.hatch;

import java.util.List;
import java.util.function.ToIntBiFunction;

public class HatchFluidDrain {

    private HatchFluidDrain() {}

    public static <H> int drainAcross(List<H> hatches, int amount, ToIntBiFunction<H, Integer> drainUpTo) {
        int remaining = amount;
        // A dual input hatch gives one slot per call, so keep visiting the hatches while any of them still gives.
        while (remaining > 0) {
            int drainedThisRound = 0;
            for (H hatch : hatches) {
                if (remaining <= 0) break;
                int drained = Math.min(remaining, drainUpTo.applyAsInt(hatch, remaining));
                drainedThisRound += drained;
                remaining -= drained;
            }
            if (drainedThisRound <= 0) break;
        }
        return amount - remaining;
    }
}
