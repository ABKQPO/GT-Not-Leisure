package com.science.gtnl.common.me.processingPattern;

import com.science.gtnl.config.OtherConfig;

public class ProcessingPatternCapacity {

    public static final int DEFAULT_MULTIPLIER = 2;
    public static final int MAX_MULTIPLIER = 64;
    public static final int SLOTS_PER_PAGE = 16;
    public static final int SMALL_SIDE_SLOTS_PER_PAGE = 4;

    public static boolean isEnabled() {
        return OtherConfig.applied_energistics.enableProcessingPatternCapacityMixin;
    }

    public static int multiplier() {
        return Math.clamp(OtherConfig.applied_energistics.processingPatternCapacityMultiplier, 1, MAX_MULTIPLIER);
    }

    public static int pageCount() {
        return multiplier();
    }

    public static int largeSideCapacity() {
        return SLOTS_PER_PAGE * multiplier();
    }

    public static int smallSideCapacity() {
        return SMALL_SIDE_SLOTS_PER_PAGE * multiplier();
    }

    public static int clampPage(int page) {
        return Math.max(0, Math.min(pageCount() - 1, page));
    }
}
