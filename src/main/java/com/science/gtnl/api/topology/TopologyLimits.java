package com.science.gtnl.api.topology;

/**
 * Hard bounds the explorer never exceeds, whatever the configuration asks for.
 */
public class TopologyLimits {

    /**
     * Nesting levels a single scan may walk, counting the bound main network as the first level. A network at
     * {@link #MAX_DEPTH} is still described, but its own boundaries are never followed any further.
     */
    public static final int MAX_NESTING_LEVELS = 5;

    /**
     * Greatest {@link TopologyNetwork#depth()} a snapshot can carry.
     */
    public static final int MAX_DEPTH = MAX_NESTING_LEVELS - 1;

    private TopologyLimits() {}

    public static int clampLevels(int levels) {
        return levels < 1 ? 1 : Math.min(levels, MAX_NESTING_LEVELS);
    }

    public static int clampDepth(int depth) {
        return depth < 0 ? 0 : Math.min(depth, MAX_DEPTH);
    }
}
