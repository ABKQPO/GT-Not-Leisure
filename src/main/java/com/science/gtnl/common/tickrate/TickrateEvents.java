package com.science.gtnl.common.tickrate;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.TickrateAPI;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

@EventBusSubscriber
public class TickrateEvents {

    public static final long MAX_SERVER_SLEEP_MILLIS = 2000L;
    public static long SLEEP_TIME = 0;
    public static long SERVER_TICK = 0;

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;
        TickrateAPI.changeClientTickrate(event.player, savedTickrate());
    }

    @SubscribeEvent
    public static void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (event.isLocal) {
            float tickrate = savedTickrate();
            TickrateAPI.changeServerTickrate(tickrate);
            TickrateAPI.changeClientTickrate(null, tickrate);
        } else {
            TickrateAPI.changeClientTickrate(null, 20F);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ++SERVER_TICK;
        } else if (SLEEP_TIME > 0 && SERVER_TICK % 20 == 0) {
            try {
                long sleepMillis = Math.min(MAX_SERVER_SLEEP_MILLIS, SLEEP_TIME);
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                Thread.currentThread()
                    .interrupt();
                ScienceNotLeisure.LOG.warn("Server tick sleep was interrupted", e);
            } finally {
                SLEEP_TIME = 0;
            }
        }
    }

    public static void queueServerSleep(long sleepMillis) {
        if (sleepMillis <= 0L) {
            return;
        }
        SLEEP_TIME = Math.min(MAX_SERVER_SLEEP_MILLIS, SLEEP_TIME + sleepMillis);
    }

    public static float savedTickrate() {
        float tickrate = MainConfig.tickrate.defaultTickrate;
        try {
            GameRules rules = MinecraftServer.getServer()
                .getEntityWorld()
                .getGameRules();
            if (rules.hasRule(TickrateAPI.GAME_RULE)) {
                tickrate = Float.parseFloat(rules.getGameRuleStringValue(TickrateAPI.GAME_RULE));
            }
        } catch (Exception ex) {
            ScienceNotLeisure.LOG.warn("Failed to read tickrate game rule", ex);
        }
        return tickrate;
    }
}
