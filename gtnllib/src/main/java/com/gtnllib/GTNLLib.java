package com.gtnllib;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnllib.client.GTNLLibClient;
import com.gtnllib.command.CommandItemInfo;
import com.gtnllib.command.CommandSuicide;
import com.gtnllib.world.WorldListener;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = GTNLLib.MODID,
    name = GTNLLib.NAME,
    useMetadata = true,
    dependencies = "required-after:gtnhlib;required-after:modularui2;required-after:Baubles",
    acceptedMinecraftVersions = "1.7.10",
    guiFactory = "com.gtnllib.config.GTNLLibGuiFactory")
public class GTNLLib {

    public static final String MODID = "gtnllib", NAME = "GTNLLib", CONFIG_DIRECTORY = "GTNotLeisure";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide()
            .isClient()) GTNLLibClient.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        WorldListener listener = new WorldListener();
        MinecraftForge.EVENT_BUS.register(listener);
        FMLCommonHandler.instance()
            .bus()
            .register(listener);
        LOG.info("GTNLLib initialized");
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandItemInfo());
        event.registerServerCommand(new CommandSuicide());
    }
}
