package com.science.gtnl.common.wireless;

import com.science.gtnl.common.world.WorldListenerTest;

public final class WirelessEventTest {

    public static void main(String[] args) throws Exception {
        var schedule = new WirelessClusterManager.ScanSchedule();
        check(schedule.tick(), "Startup must discover saved targets immediately");
        schedule.beginScan();
        for (int i = 0; i < 19; i++) check(!schedule.tick(), "Quiet ticks do not scan the full graph");
        check(schedule.tick(), "A missed notification gets a one-second fallback scan");
        schedule.beginScan();
        for (int i = 0; i < 100; i++) schedule.invalidate();
        check(schedule.tick(), "A burst of events schedules a scan on the next tick");
        schedule.beginScan();
        check(!schedule.tick(), "A burst coalesces instead of leaving hundreds of queued scans");
        schedule.invalidate();
        check(schedule.tick(), "Events during scan/reconciliation must not be swallowed");
        schedule.beginScan();
        schedule.clear();
        check(schedule.tick(), "A new server session scans immediately");
        WorldListenerTest.run();
        System.out.println(
            "WirelessEventTest: notification coalescing, fallback, arbitrary positions and listener cleanup passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
