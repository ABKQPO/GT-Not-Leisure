package com.science.gtnl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.science.gtnl.common.packet.NetWorkHandler;
import com.science.gtnl.loader.ContentLoaders;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.item.MissingMappingsHandler;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(
    modid = "sciencenotleisure",
    version = Tags.VERSION,
    name = ScienceNotLeisure.MODNAME,
    dependencies = "after:AWWayofTime;" + "after:Avaritia;"
        + "after:BloodArsenal;"
        + "required-after:Botania;"
        + "required-after:bartworks;"
        + "after:eternalsingularity;"
        + "after:etfuturum;"
        + "after:GalacticraftCore;"
        + "after:GalacticraftMars;"
        + "after:GalacticraftPlanets;"
        + "required-after:gtnhintergalactic;"
        + "required-after:gregtech;"
        + "required-after:galacticgreg;"
        + "required-after:IC2;"
        + "required-after:modularui;"
        + "after:miscutils;"
        + "before:neicustomdiagram;"
        + "after:dreamcraft;"
        + "required-after:structurelib;"
        + "required-after:Thaumcraft;"
        + "before:TwistSpaceTechnology;",
    acceptedMinecraftVersions = "1.7.10",
    guiFactory = "com.science.gtnl.config.ConfigGuiFactory")
public class ScienceNotLeisure {

    public static final String MODID = "sciencenotleisure";
    @Mod.Instance(MODID)
    public static ScienceNotLeisure instance;
    public static final String MODNAME = "GTNotLeisure";
    public static final String VERSION = Tags.VERSION;
    public static final String RESOURCE_ROOT_ID = ModList.ScienceNotLeisure.ID;
    public static final Logger LOG = LogManager.getLogger(ScienceNotLeisure.MODID);

    public static SimpleNetworkWrapper network;

    @SidedProxy(clientSide = "com.science.gtnl.ClientProxy", serverSide = "com.science.gtnl.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(ScienceNotLeisure.MODID);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
        NetWorkHandler.register();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void completeInit(FMLLoadCompleteEvent event) {
        proxy.completeInit(event);
    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        ContentLoaders.serverAboutToStart();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        ContentLoaders.serverStarting(event);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        ContentLoaders.serverStopping();
    }

    @Mod.EventHandler
    public void onMissingMappings(FMLMissingMappingsEvent event) {
        MissingMappingsHandler.handleMappings(event.getAll());
    }
}
