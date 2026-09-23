package com.science.gtnl.client.text.effect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** A reproducible tick schedule with independent idle trials and non-retriggerable flashes. */
public class TickFlashTimeline {

    private final int durationTicks;
    private final int cycleTicks;
    private final int[] starts;

    public TickFlashTimeline(double probability, int durationTicks, int cycleTicks, long seed) {
        if (!Double.isFinite(probability) || probability <= 0 || probability >= 1)
            throw new IllegalArgumentException("Flash probability must be between zero and one");
        if (durationTicks < 1 || cycleTicks <= durationTicks)
            throw new IllegalArgumentException("Flash duration must fit inside the cycle");
        this.durationTicks = durationTicks;
        this.cycleTicks = cycleTicks;
        Random random = new Random(seed);
        List<Integer> events = new ArrayList<>();
        double failureLog = Math.log1p(-probability);
        long tick = durationTicks;
        while (tick < cycleTicks) {
            // A geometric wait is equivalent to checking the source probability once per idle tick.
            double wait = Math.floor(Math.log1p(-random.nextDouble()) / failureLog);
            if (wait >= cycleTicks - tick) break;
            tick += (long) wait;
            if (tick + durationTicks > cycleTicks) break;
            events.add((int) tick);
            tick += durationTicks;
        }
        starts = events.stream()
            .mapToInt(Integer::intValue)
            .toArray();
    }

    public boolean active(int tick) {
        int localTick = Math.floorMod(tick, cycleTicks);
        int index = Arrays.binarySearch(starts, localTick);
        if (index < 0) index = -index - 2;
        return index >= 0 && localTick - starts[index] < durationTicks;
    }
}
