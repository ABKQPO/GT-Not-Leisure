package com.science.gtnl.common.wireless;

import com.science.gtnl.common.wireless.ChannelBudget.Position;

/** Geometry filters an existing AE connection; adjacency alone never creates a physical connection. */
public final class PhysicalConnectionRules {

    private PhysicalConnectionRules() {}

    public static boolean isPhysical(boolean ownedWireless, boolean directed, Position a, Position b) {
        if (ownedWireless || a == null || b == null || a.dimension() != b.dimension()) return false;
        long distance = Math.abs((long) a.x() - b.x()) + Math.abs((long) a.y() - b.y())
            + Math.abs((long) a.z() - b.z());
        // UNKNOWN connections within a host include AE cable-to-part connections. Remote UNKNOWN edges are bridges.
        return distance == 0 || directed && distance == 1;
    }
}
