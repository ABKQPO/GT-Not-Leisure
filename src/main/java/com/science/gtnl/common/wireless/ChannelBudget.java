package com.science.gtnl.common.wireless;

import java.util.Set;

/** A budget for one AE2 pathing pass, counting successful channel allocations, not traversed edges. */
public final class ChannelBudget {

    public static final int CHANNELS_PER_FACE = 32;
    private final int capacity;
    private int allocated;

    public ChannelBudget(int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("Negative channel capacity");
        this.capacity = capacity;
    }

    public boolean hasRemaining() {
        return allocated < capacity;
    }

    public void recordAllocation() {
        if (!hasRemaining()) throw new IllegalStateException("Channel budget exhausted");
        allocated++;
    }

    public int capacity() {
        return capacity;
    }

    public int allocated() {
        return allocated;
    }

    /** Geometric faces, including unwired faces; internal controller faces contribute no capacity. */
    public static int capacityOf(Set<Position> controllers) {
        long faces = 0;
        for (Position p : controllers) {
            for (int axis = 0; axis < 3; axis++) {
                for (int step = -1; step <= 1; step += 2) {
                    if (!controllers.contains(p.offset(axis, step))) faces++;
                }
            }
        }
        return (int) Math.min(Integer.MAX_VALUE, faces * CHANNELS_PER_FACE);
    }

    public record Position(int dimension, int x, int y, int z) {

        private Position offset(int axis, int step) {
            return new Position(
                dimension,
                x + (axis == 0 ? step : 0),
                y + (axis == 1 ? step : 0),
                z + (axis == 2 ? step : 0));
        }
    }
}
