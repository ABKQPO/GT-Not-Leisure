package com.science.gtnl.common.machine.monitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import gregtech.common.misc.spaceprojects.SpaceProjectManager;

public class WirelessTeam {

    public static Set<UUID> resolveMembers(UUID viewerUuid) {
        if (viewerUuid == null) {
            return Set.of();
        }
        SpaceProjectManager.checkOrCreateTeam(viewerUuid);
        UUID leader = SpaceProjectManager.getLeader(viewerUuid);
        Collection<UUID> members = SpaceProjectManager.getTeamMembers(leader);
        Set<UUID> resolved = new HashSet<>(members);
        resolved.add(leader);
        return resolved;
    }

    public static UUID resolveLeader(UUID viewerUuid) {
        if (viewerUuid == null) {
            return null;
        }
        SpaceProjectManager.checkOrCreateTeam(viewerUuid);
        return SpaceProjectManager.getLeader(viewerUuid);
    }
}
