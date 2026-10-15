package com.science.gtnl.api.topology;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import appeng.api.networking.IGridHost;

public class MeBridgeProbeRegistry {

    private static final Map<String, MeBridgeProbe> PROBES = new LinkedHashMap<>();
    private static List<MeBridgeProbe> snapshot = List.of();

    private MeBridgeProbeRegistry() {}

    public static synchronized void register(MeBridgeProbe probe) {
        if (probe == null || probe.id() == null
            || probe.id()
                .isEmpty())
            throw new IllegalArgumentException("Probe and its id are required");
        PROBES.put(probe.id(), probe);
        snapshot = List.copyOf(PROBES.values());
    }

    public static synchronized void unregister(String id) {
        if (PROBES.remove(id) != null) snapshot = List.copyOf(PROBES.values());
    }

    public static List<MeBridgeProbe> probes() {
        return snapshot;
    }

    public static List<MeBridgeProbe> probesFor(Class<? extends IGridHost> hostType) {
        List<MeBridgeProbe> matching = null;
        for (MeBridgeProbe probe : snapshot) {
            if (!probe.accepts(hostType)) continue;
            if (matching == null) matching = new ArrayList<>(2);
            matching.add(probe);
        }
        return matching == null ? List.of() : matching;
    }
}
