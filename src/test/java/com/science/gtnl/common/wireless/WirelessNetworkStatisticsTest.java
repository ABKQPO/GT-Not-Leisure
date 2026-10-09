package com.science.gtnl.common.wireless;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.pathing.IPathingGrid;

public final class WirelessNetworkStatisticsTest {

    public static void main(String[] args) {
        List<IGridNode> cluster = new ArrayList<>();
        for (int i = 0; i < 64; i++) cluster.add(node(true, true, true, false, false));
        for (int i = 0; i < 20; i++) cluster.add(node(false, true, true, false, false));
        var full = WirelessNetworkStatistics.measure(cluster, true);
        check(
            full.nodes() == 84 && full.devices() == 64 && full.online() == 64 && full.missing() == 0,
            "Cable nodes must not inflate the count of 64 working interfaces");

        var unpowered = WirelessNetworkStatistics.measure(List.of(node(true, false, true, false, false)), true);
        check(
            unpowered.online() == 0 && unpowered.missing() == 0,
            "Allocated channels without power must not be counted as online or missing channels");
        var missing = WirelessNetworkStatistics.measure(List.of(node(true, false, false, false, false)), true);
        check(
            missing.devices() == 1 && missing.online() == 0 && missing.missing() == 1 && missing.stable(),
            "A settled unserved consumer must report a channel deficit");
        check(
            !WirelessNetworkStatistics.measure(List.of(node(true, false, false, true, false)), true)
                .stable(),
            "Booting must mark channel counts as provisional");
        check(
            !WirelessNetworkStatistics.measure(List.of(node(true, true, true, false, true)), true)
                .stable(),
            "A queued repath must also mark stale allocations as provisional");
        check(
            WirelessNetworkStatistics.measure(List.of(node(true, false, false, false, false)), false)
                .missing() == 0,
            "Disabled channels must not be reported as a channel deficit");
        var empty = WirelessNetworkStatistics.measure(List.of(), true);
        check(
            empty.nodes() == 0 && empty.devices() == 0 && empty.online() == 0 && empty.missing() == 0,
            "An empty loaded view must not retain prior counts");
        System.out.println(
            "WirelessNetworkStatisticsTest: consumer counts, power, channel deficit and pending pathing passed.");
    }

    private static IGridNode node(boolean consumer, boolean active, boolean channels, boolean booting,
        boolean pending) {
        IPathingGrid path = (IPathingGrid) Proxy.newProxyInstance(
            WirelessNetworkStatisticsTest.class.getClassLoader(),
            new Class<?>[] { IPathingGrid.class, WirelessPathingState.class },
            (proxy, method, args) -> switch (method.getName()) {
            case "isNetworkBooting" -> booting;
            case "gtnl$isRepathPending" -> pending;
            default -> throw new AssertionError(method);
            });
        IGrid grid = (IGrid) Proxy.newProxyInstance(
            WirelessNetworkStatisticsTest.class.getClassLoader(),
            new Class<?>[] { IGrid.class },
            (proxy, method, args) -> {
                if (method.getName()
                    .equals("getCache") && args[0] == IPathingGrid.class) return path;
                throw new AssertionError(method);
            });
        return (IGridNode) Proxy.newProxyInstance(
            WirelessNetworkStatisticsTest.class.getClassLoader(),
            new Class<?>[] { IGridNode.class },
            (proxy, method, args) -> switch (method.getName()) {
            case "hasFlag" -> consumer && args[0] == GridFlags.REQUIRE_CHANNEL;
            case "isActive" -> active;
            case "meetsChannelRequirements" -> channels;
            case "getGrid" -> grid;
            default -> throw new AssertionError(method);
            });
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
