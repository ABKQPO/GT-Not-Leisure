package com.science.gtnl.utils.world.teams;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gtnewhorizon.gtnhlib.teams.Team;
import com.gtnewhorizon.gtnhlib.teams.TeamDataTransferReason;
import com.gtnewhorizon.gtnhlib.teams.TeamManager;
import com.gtnewhorizon.gtnhlib.util.ServerPlayerUtils;
import com.mojang.authlib.GameProfile;
import com.science.gtnl.ScienceNotLeisure;

public class TeamNetworkManager {

    private static final String LEGACY_DATA_FILE = "GT_SpaceProjectData.dat";
    private static final String LEGACY_TEAMS_FILE = "spaceTeams.json";
    private static final String LEGACY_TEAMS_TAG = "spaceTeams";
    private static final String MEMBER_UUID_TAG = "MEMBER_UUID";
    private static final String LEADER_UUID_TAG = "LEADER_UUID";

    private TeamNetworkManager() {}

    public static Team getOrCreateTeam(UUID playerId) {
        return TeamManager.getOrCreateTeam(getPlayerName(playerId), playerId);
    }

    public static UUID getTeamId(UUID playerId) {
        return getOrCreateTeam(playerId).getTeamId();
    }

    public static Set<UUID> getMembers(UUID playerId) {
        return getOrCreateTeam(playerId).getMembers();
    }

    public static boolean isTeamOwner(UUID playerId) {
        return getOrCreateTeam(playerId).isOwner(playerId);
    }

    public static String getTeamName(UUID teamId) {
        Team team = TeamManager.getTeamById(teamId);
        return team == null ? "Unknown Team" : team.getTeamName();
    }

    public static boolean isSameTeam(UUID firstPlayerId, UUID secondPlayerId) {
        return getTeamId(firstPlayerId).equals(getTeamId(secondPlayerId));
    }

    public static boolean joinTeam(UUID memberId, UUID targetMemberId) {
        Team memberTeam = getOrCreateTeam(memberId);
        Team targetTeam = getOrCreateTeam(targetMemberId);
        if (memberTeam.getTeamId()
            .equals(targetTeam.getTeamId())) {
            return false;
        }

        TeamManager.transferTeamData(memberTeam, targetTeam, memberId, TeamDataTransferReason.JoinedExistingTeam);
        memberTeam.removeMember(memberId);
        targetTeam.addMember(memberId);
        memberTeam.markDirty();
        targetTeam.markDirty();
        return true;
    }

    public static boolean createPersonalTeam(UUID playerId) {
        Team previousTeam = getOrCreateTeam(playerId);
        if (previousTeam.getMembers()
            .size() == 1) {
            return false;
        }

        previousTeam.removeMember(playerId);
        Team personalTeam = TeamManager.createTeam(getPlayerName(playerId), playerId);
        TeamManager.transferTeamData(previousTeam, personalTeam, playerId, TeamDataTransferReason.JoinedNewTeam);
        previousTeam.markDirty();
        personalTeam.markDirty();
        return true;
    }

    public static String getPlayerName(UUID playerId) {
        return ServerPlayerUtils.getPlayerName(playerId);
    }

    public static UUID getPlayerId(String playerName) {
        GameProfile profile = MinecraftServer.getServer()
            .func_152358_ax()
            .func_152655_a(playerName);
        return profile == null ? null : profile.getId();
    }

    public static void migrateLegacyTeams(World world) {
        TeamMigrationWorldSavedData migrationData = TeamMigrationWorldSavedData.get(world.mapStorage);
        if (migrationData.isComplete()) {
            return;
        }

        LegacyTeamReadResult legacyTeams = readLegacyTeams(world);
        if (!legacyTeams.readSuccessfully()) {
            ScienceNotLeisure.LOG.warn("Legacy team migration is pending until its source data can be read");
            return;
        }

        mergeLegacyTeams(legacyTeams.teams());
        migrationData.markComplete();
    }

    public static void mergeLegacyTeams(Map<UUID, UUID> legacyTeams) {
        for (Map.Entry<UUID, UUID> entry : legacyTeams.entrySet()) {
            UUID memberId = entry.getKey();
            UUID leaderId = entry.getValue();
            if (memberId.equals(leaderId)) {
                getOrCreateTeam(leaderId);
                continue;
            }
            joinTeam(memberId, leaderId);
        }
    }

    private static LegacyTeamReadResult readLegacyTeams(World world) {
        File worldDirectory = world.getSaveHandler()
            .getWorldDirectory();
        LegacyTeamReadResult dataFileTeams = readLegacyTeamsFromDataFile(
            new File(worldDirectory, "data" + File.separator + LEGACY_DATA_FILE));
        if (dataFileTeams.readSuccessfully() && !dataFileTeams.teams()
            .isEmpty()) {
            return dataFileTeams;
        }

        LegacyTeamReadResult jsonTeams = readLegacyTeamsFromJson(new File(worldDirectory, LEGACY_TEAMS_FILE));
        if (jsonTeams.readSuccessfully() && !jsonTeams.teams()
            .isEmpty()) {
            return jsonTeams;
        }

        return new LegacyTeamReadResult(
            new HashMap<>(),
            dataFileTeams.readSuccessfully() && jsonTeams.readSuccessfully());
    }

    private static LegacyTeamReadResult readLegacyTeamsFromDataFile(File dataFile) {
        if (!dataFile.isFile()) {
            return new LegacyTeamReadResult(new HashMap<>(), true);
        }
        try {
            NBTTagCompound rootTag = CompressedStreamTools.read(dataFile);
            NBTTagCompound dataTag = rootTag.hasKey("data") ? rootTag.getCompoundTag("data") : rootTag;
            return new LegacyTeamReadResult(parseLegacyTeams(dataTag.getString(LEGACY_TEAMS_TAG)), true);
        } catch (IOException | RuntimeException exception) {
            ScienceNotLeisure.LOG.warn("Unable to read legacy team data from {}", dataFile);
            return new LegacyTeamReadResult(new HashMap<>(), false);
        }
    }

    private static LegacyTeamReadResult readLegacyTeamsFromJson(File teamsFile) {
        if (!teamsFile.isFile()) {
            return new LegacyTeamReadResult(new HashMap<>(), true);
        }
        try (BufferedReader reader = Files.newBufferedReader(teamsFile.toPath(), StandardCharsets.UTF_8)) {
            return new LegacyTeamReadResult(
                parseLegacyTeams(
                    new JsonParser().parse(reader)
                        .toString()),
                true);
        } catch (IOException | RuntimeException exception) {
            ScienceNotLeisure.LOG.warn("Unable to read legacy team JSON from {}", teamsFile);
            return new LegacyTeamReadResult(new HashMap<>(), false);
        }
    }

    private static Map<UUID, UUID> parseLegacyTeams(String encodedTeams) {
        Map<UUID, UUID> teams = new HashMap<>();
        if (encodedTeams == null || encodedTeams.isEmpty()) {
            return teams;
        }
        JsonArray entries = new JsonParser().parse(encodedTeams)
            .getAsJsonArray();
        for (JsonElement entry : entries) {
            JsonObject team = entry.getAsJsonObject();
            teams.put(
                UUID.fromString(
                    team.get(MEMBER_UUID_TAG)
                        .getAsString()),
                UUID.fromString(
                    team.get(LEADER_UUID_TAG)
                        .getAsString()));
        }
        return teams;
    }

    private record LegacyTeamReadResult(Map<UUID, UUID> teams, boolean readSuccessfully) {}
}
