package com.science.gtnl.common.wireless;

import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import com.science.gtnl.common.wireless.WirelessChannelManager.Address;

import appeng.api.networking.IGridNode;

public interface IWirelessLinkView {

    ClusterView cluster();

    Address target();

    IGridNode node();

    default Map<IGridNode, String> deviceNames() {
        return cluster().deviceNames();
    }

    default Set<IGridNode> members() {
        return cluster().members();
    }

    default boolean conflicted() {
        return cluster().conflicted();
    }

    default boolean paused() {
        return cluster().paused();
    }

    default String state() {
        return cluster().state();
    }

    default int entrances() {
        return cluster().entrances();
    }

    default WirelessNetworkStatistics statistics() {
        return cluster().statistics();
    }

    default Set<String> names() {
        return deviceNames().values()
            .stream()
            .filter(name -> !name.isEmpty())
            .distinct()
            .map(WirelessLinkPersistence::cleanName)
            .filter(name -> !name.isEmpty())
            .collect(Collectors.toUnmodifiableSet());
    }

    default String name() {
        Set<String> names = names();
        return names.stream()
            .sorted()
            .limit(4)
            .collect(Collectors.joining(" / ")) + (names.size() > 4 ? " … (" + names.size() + ")" : "");
    }

    record ClusterView(Address target, IGridNode node, Map<IGridNode, String> deviceNames, boolean conflicted,
        boolean paused, String state, int entrances, WirelessNetworkStatistics statistics, Set<IGridNode> members)
        implements IWirelessLinkView {

        public ClusterView {
            deviceNames = Map.copyOf(deviceNames);
            members = Set.copyOf(members);
        }

        @Override
        public ClusterView cluster() {
            return this;
        }
    }

    record EntranceView(ClusterView cluster, Address target, IGridNode node, OptionalInt usedChannels)
        implements IWirelessLinkView {

        public EntranceView {
            if (!cluster.members()
                .contains(node)) throw new IllegalArgumentException("Entrance outside its physical cluster");
            if (usedChannels.isPresent() && usedChannels.getAsInt() < 0)
                throw new IllegalArgumentException("Channel usage cannot be negative");
        }
    }
}
