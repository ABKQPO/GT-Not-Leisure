package com.science.gtnl.utils.world.steam;

import net.minecraftforge.event.world.WorldEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.teams.TeamEvents.TeamLeaveEvent;
import com.gtnewhorizon.gtnhlib.teams.TeamEvents.TeamMergeEvent;
import com.science.gtnl.utils.world.teams.TeamNetworkManager;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class SteamNetworkEventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onWorldLoad(WorldEvent.Load event) {
        if (event.world.isRemote || event.world.provider.dimensionId != 0) return;

        // GTNHLib must finish loading team IDs before any balance or membership migration.
        GlobalSteamWorldSavedData.loadInstance(event.world);
        TeamNetworkManager.migrateLegacyTeams(event.world);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.world.isRemote || event.world.provider.dimensionId != 0) return;

        SteamWirelessNetworkManager.clearGlobalSteamInformationMaps();
        GlobalSteamWorldSavedData.INSTANCE = null;
    }

    @SubscribeEvent
    public static void onTeamMerge(TeamMergeEvent event) {
        SteamWirelessNetworkManager.mergeTeamSteam(event.consumed.getTeamId(), event.surviving.getTeamId());
    }

    @SubscribeEvent
    public static void onTeamLeave(TeamLeaveEvent event) {
        // A departing member cannot take a shared team's balance while other members remain.
        if (event.teamDisbanded) {
            SteamWirelessNetworkManager.mergeTeamSteam(event.team.getTeamId(), event.newTeam.getTeamId());
        }
    }
}
