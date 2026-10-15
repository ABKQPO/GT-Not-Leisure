package com.science.gtnl.api.topology;

import java.util.ArrayList;
import java.util.List;

public record TopologySnapshot(List<TopologyNetwork> networks, List<TopologyDevice> devices,
    List<TopologyBridge> bridges, boolean bound, boolean available, boolean truncated, long revision) {

    public static final TopologySnapshot EMPTY = new TopologySnapshot(
        List.of(),
        List.of(),
        List.of(),
        false,
        false,
        false,
        0L);

    public TopologySnapshot {
        networks = List.copyOf(networks);
        devices = List.copyOf(devices);
        bridges = List.copyOf(bridges);
    }

    public boolean isEmpty() {
        return networks.isEmpty();
    }

    public TopologyNetwork network(int id) {
        for (TopologyNetwork network : networks) {
            if (network.id() == id) return network;
        }
        return null;
    }

    public TopologyDevice device(int id) {
        for (TopologyDevice device : devices) {
            if (device.id() == id) return device;
        }
        return null;
    }

    public List<TopologyDevice> devicesOf(int networkId) {
        List<TopologyDevice> owned = new ArrayList<>();
        for (TopologyDevice device : devices) {
            if (device.network() == networkId) owned.add(device);
        }
        return owned;
    }

    public TopologyBridge bridge(int id) {
        for (TopologyBridge bridge : bridges) {
            if (bridge.id() == id) return bridge;
        }
        return null;
    }

    public TopologySnapshot withRevision(long newRevision) {
        return new TopologySnapshot(networks, devices, bridges, bound, available, truncated, newRevision);
    }
}
