package com.science.gtnl.common.machine.hatch;

import static com.gtnltest.TestSupport.check;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("machine")
public class HatchFluidDrainTest {

    @Test
    void paysExactlyTheAmountAcceptedBySimulation() {
        int[] full = { 4000, 4000, 4000 };
        int drained = HatchFluidDrain.drainAcross(indices(full), 1000, (hatch, upTo) -> take(full, hatch, upTo));
        check(
            drained == 1000 && Arrays.equals(full, new int[] { 3000, 4000, 4000 }),
            "a request is drained once across all input hatches, not once per hatch: " + Arrays.toString(full));

        int[] scarce = { 300, 300, 300 };
        drained = HatchFluidDrain.drainAcross(indices(scarce), 1000, (hatch, upTo) -> take(scarce, hatch, upTo));
        check(
            drained == 900 && Arrays.stream(scarce)
                .sum() == 0,
            "when the hatches hold too little, everything they hold is drained and the shortfall reported");

        int[] slotted = { 500 };
        drained = HatchFluidDrain
            .drainAcross(indices(slotted), 450, (hatch, upTo) -> take(slotted, hatch, Math.min(100, upTo)));
        check(
            drained == 450 && slotted[0] == 50,
            "a hatch that gives one slot per call is visited again until the request is met");
    }

    private static List<Integer> indices(int[] hatches) {
        Integer[] indices = new Integer[hatches.length];
        for (int i = 0; i < hatches.length; i++) indices[i] = i;
        return Arrays.asList(indices);
    }

    private static int take(int[] hatches, int hatch, int upTo) {
        int taken = Math.min(hatches[hatch], upTo);
        hatches[hatch] -= taken;
        return taken;
    }
}
