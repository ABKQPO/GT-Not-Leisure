package com.science.gtnl.common.wireless;

/** Coalesces notifications; callbacks during a scan remain pending for the next tick. */
final class WirelessScanSchedule {

    private boolean dirty = true;
    private int remaining;

    void invalidate() {
        dirty = true;
    }

    boolean tick() {
        if (remaining > 0) remaining--;
        return dirty || remaining == 0;
    }

    void beginScan() {
        dirty = false;
        remaining = 20;
    }

    void clear() {
        dirty = true;
        remaining = 0;
    }
}
