package com.science.gtnl.common.topology;

import com.science.gtnl.api.topology.MeBridgeProbeRegistry;
import com.science.gtnl.api.topology.TopologyDimensionBadges;
import com.science.gtnl.common.topology.probe.AdjacentHostBridgeProbe;
import com.science.gtnl.common.topology.probe.ExternalNodeBridgeProbe;
import com.science.gtnl.common.topology.probe.InterfaceInboundProbe;
import com.science.gtnl.common.topology.probe.QuantumBridgeProbe;
import com.science.gtnl.common.topology.probe.StorageBusBridgeProbe;

public class TopologyProbes {

    private TopologyProbes() {}

    public static void registerDefaults() {
        MeBridgeProbeRegistry.register(new StorageBusBridgeProbe());
        MeBridgeProbeRegistry.register(new ExternalNodeBridgeProbe());
        MeBridgeProbeRegistry.register(new AdjacentHostBridgeProbe());
        MeBridgeProbeRegistry.register(new QuantumBridgeProbe());
        MeBridgeProbeRegistry.register(new InterfaceInboundProbe());
        TopologyDimensionBadges.registerDefaults();
    }
}
