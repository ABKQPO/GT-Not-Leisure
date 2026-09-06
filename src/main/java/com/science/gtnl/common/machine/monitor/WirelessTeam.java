package com.science.gtnl.common.machine.monitor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.science.gtnl.utils.world.teams.TeamNetworkManager;

import lombok.Getter;

@Deprecated
public class WirelessTeam {

    public static TeamContext resolveContext(UUID viewerUuid) {
        if (viewerUuid == null) {
            return new TeamContext(null, Set.of());
        }
        UUID teamId = TeamNetworkManager.getTeamId(viewerUuid);
        return new TeamContext(teamId, new HashSet<>(TeamNetworkManager.getMembers(viewerUuid)));
    }

    public static Set<UUID> resolveMembers(UUID viewerUuid) {
        return resolveContext(viewerUuid).getMembers();
    }

    public static UUID resolveLeader(UUID viewerUuid) {
        return resolveContext(viewerUuid).getLeader();
    }

    @Getter
    @Deprecated
    public static class TeamContext {

        private final UUID leader;
        private final Set<UUID> members;

        public TeamContext(UUID leader, Set<UUID> members) {
            this.leader = leader;
            this.members = members == null ? Set.of() : members;
        }

    }
}
