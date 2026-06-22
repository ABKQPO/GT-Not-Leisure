package com.science.gtnl.common.machine.monitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import gregtech.common.misc.spaceprojects.SpaceProjectManager;

public class WirelessTeam {

    public static TeamContext resolveContext(UUID viewerUuid) {
        if (viewerUuid == null) {
            return new TeamContext(null, Set.of());
        }
        SpaceProjectManager.checkOrCreateTeam(viewerUuid);
        UUID leader = SpaceProjectManager.getLeader(viewerUuid);
        Collection<UUID> members = SpaceProjectManager.getTeamMembers(leader);
        Set<UUID> resolved = new HashSet<>(members);
        resolved.add(leader);
        return new TeamContext(leader, resolved);
    }

    public static Set<UUID> resolveMembers(UUID viewerUuid) {
        return resolveContext(viewerUuid).getMembers();
    }

    public static UUID resolveLeader(UUID viewerUuid) {
        return resolveContext(viewerUuid).getLeader();
    }

    public static class TeamContext {

        private final UUID leader;
        private final Set<UUID> members;

        public TeamContext(UUID leader, Set<UUID> members) {
            this.leader = leader;
            this.members = members == null ? Set.of() : members;
        }

        public UUID getLeader() {
            return leader;
        }

        public Set<UUID> getMembers() {
            return members;
        }
    }
}
