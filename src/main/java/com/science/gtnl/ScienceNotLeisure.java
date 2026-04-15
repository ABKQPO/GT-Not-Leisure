package com.science.gtnl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.science.gtnl.common.command.CommandEnergyNetwork;
import com.science.gtnl.common.command.CommandItemInfo;
import com.science.gtnl.common.command.CommandPlaySound;
import com.science.gtnl.common.command.CommandSteamNetwork;
import com.science.gtnl.common.command.CommandSudo;
import com.science.gtnl.common.command.CommandSuicide;
import com.science.gtnl.common.command.CommandTickrate;
import com.science.gtnl.common.command.CommandTitle;
import com.science.gtnl.loader.MaterialLoader;
import com.science.gtnl.loader.RecipeLoader;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.item.MissingMappingsHandler;
import com.science.gtnl.utils.text.LanguageLoader;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(
    modid = ScienceNotLeisure.MODID,
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
    acceptedMinecraftVersions = "1.7.10")
public class ScienceNotLeisure {

    @Mod.Instance(ModList.ModIds.SCIENCE_NOT_LEISURE)
    public static ScienceNotLeisure instance;
    public static final String MODID = ModList.ModIds.SCIENCE_NOT_LEISURE;
    public static final String MODNAME = "GTNotLeisure";
    public static final String VERSION = Tags.VERSION;
    public static final String ARTHOR = "HFstudio";
    public static final String RESOURCE_ROOT_ID = ModList.ModIds.SCIENCE_NOT_LEISURE;
    public static final Logger LOG = LogManager.getLogger(ScienceNotLeisure.MODID);

    public static SimpleNetworkWrapper network;

    @SidedProxy(clientSide = "com.science.gtnl.ClientProxy", serverSide = "com.science.gtnl.CommonProxy")
    public static CommonProxy proxy;

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(ScienceNotLeisure.MODID);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
        proxy.preInit(event);
        MaterialLoader.loadPreInit();
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        MaterialLoader.loadInit();
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        MaterialLoader.loadPostInit();
        RecipeLoader.loadPostInit();
    }

    @Mod.EventHandler
    public void completeInit(FMLLoadCompleteEvent event) {
        proxy.completeInit(event);
        LanguageLoader.registry();
    }

    // register server commands in this event handler (Remove if not needed)
    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandTitle());
        event.registerServerCommand(new CommandTickrate());
        event.registerServerCommand(new CommandSteamNetwork());
        event.registerServerCommand(new CommandEnergyNetwork());
        event.registerServerCommand(new CommandPlaySound());
        event.registerServerCommand(new CommandItemInfo());
        event.registerServerCommand(new CommandSudo());
        event.registerServerCommand(new CommandSuicide());
        RecipeLoader.loadServerStart();
    }

    @Mod.EventHandler
    public void onMissingMappings(FMLMissingMappingsEvent event) {
        MissingMappingsHandler.handleMappings(event.getAll());
    }
}
