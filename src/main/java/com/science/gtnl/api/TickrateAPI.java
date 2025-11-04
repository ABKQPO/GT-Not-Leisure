package com.science.gtnl.api;

import static com.science.gtnl.ScienceNotLeisure.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.science.gtnl.asm.GTNLEarlyCoreMod;
import com.science.gtnl.common.packet.TickratePacket;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class TickrateAPI {

    /**
     * Let you change the client & server tickrate
     * Can only be called from server-side. Can also be called from client-side if is singleplayer.
     *
     * @param ticksPerSecond Tickrate to be set
     */
    public static void changeTickrate(float ticksPerSecond) {
        changeServerTickrate(ticksPerSecond);
        changeClientTickrate(ticksPerSecond);
    }

    /**
     * Let you change the server tickrate
     * Can only be called from server-side. Can also be called from client-side if is singleplayer.
     *
     * @param ticksPerSecond Tickrate to be set
     */
    public static void changeServerTickrate(float ticksPerSecond) {
        GTNLEarlyCoreMod.INSTANCE.updateServerTickrate(ticksPerSecond);
    }

    /**
     * Let you change the all clients tickrate
     * Can be called either from server-side or client-side
     *
     * @param ticksPerSecond Tickrate to be set
     */
    public static void changeClientTickrate(float ticksPerSecond) {
        MinecraftServer server = MinecraftServer.getServer();
        if ((server != null) && (server.getConfigurationManager() != null)) { // Is a server or singleplayer
            for (EntityPlayer p : server.getConfigurationManager().playerEntityList) {
                changeClientTickrate(p, ticksPerSecond);
            }
        } else { // Is in menu or a player connected in a server. We can say this is client.
            changeClientTickrate(null, ticksPerSecond);
        }
    }

    /**
     * Let you change the all clients tickrate
     * Can be called either from server-side or client-side.
     * Will only take effect in the client-side if the player is Minecraft.thePlayer
     *
     * @param player         The Player
     * @param ticksPerSecond Tickrate to be set
     */
    public static void changeClientTickrate(EntityPlayer player, float ticksPerSecond) {
        if ((player == null) || (player.worldObj.isRemote)) { // Client
            if (FMLCommonHandler.instance()
                .getSide() != Side.CLIENT) return;
            if ((player != null) && (player != Minecraft.getMinecraft().thePlayer)) return;
            GTNLEarlyCoreMod.INSTANCE.updateClientTickrate(ticksPerSecond);
        } else { // Server
            network.sendTo(new TickratePacket(ticksPerSecond), (EntityPlayerMP) player);
        }
    }

    /**
     * Let you change the server tickrate
     * Can only be called from server-side. Can also be called from client-side if is singleplayer.
     * This will not update the tickrate from the server/clients.
     *
     * @param ticksPerSecond Tickrate to be set
     * @param save           If will be saved in the config file
     */
    public static void changeDefaultTickrate(float ticksPerSecond, boolean save) {
        MainConfig.defaultTickrate = ticksPerSecond;
        if (save) {
            Configuration cfg = new Configuration(GTNLEarlyCoreMod.CONFIG_FILE);
            cfg.get("default", "tickrate", 20.0, "Default tickrate. The game will always initialize with this value.")
                .set(ticksPerSecond);
            cfg.save();
        }
    }

    /**
     * Let you change the map tickrate
     * Can only be called from server-side. Can also be called from client-side if is singleplayer.
     * This will not update the tickrate from the server/clients
     *
     * @param ticksPerSecond Tickrate to be set
     */
    public static void changeMapTickrate(float ticksPerSecond) {
        World world = MinecraftServer.getServer()
            .getEntityWorld();
        world.getGameRules()
            .setOrCreateGameRule(GTNLEarlyCoreMod.GAME_RULE, ticksPerSecond + "");
    }

    /**
     * Only returns the real tickrate if you call the method server-side or in singleplayer
     *
     * @return The server tickrate or the client server tickrate if it doesn't have access to the real tickrate.
     */
    public static float getServerTickrate() {
        return 1000F / GTNLEarlyCoreMod.MILISECONDS_PER_TICK;
    }

    /**
     * Can only be called in the client-side
     *
     * @return The client tickrate
     */
    public static float getClientTickrate() {
        return GTNLEarlyCoreMod.TICKS_PER_SECOND;
    }

    /**
     * Can only be called in the server-side or singleplayer
     *
     * @return The map tickrate or the server tickrate if it doesn't have a map tickrate.
     */
    public static float getMapTickrate() {
        GameRules rules = MinecraftServer.getServer()
            .getEntityWorld()
            .getGameRules();
        if (rules.hasRule(GTNLEarlyCoreMod.GAME_RULE)) {
            return Float.parseFloat(rules.getGameRuleStringValue(GTNLEarlyCoreMod.GAME_RULE));
        }
        return getServerTickrate();
    }

    /**
     * Checks if the tickrate is valid
     *
     * @param ticksPerSecond Tickrate to be checked
     */
    public static boolean isValidTickrate(float ticksPerSecond) {
        return ticksPerSecond > 0F;
    }
}
