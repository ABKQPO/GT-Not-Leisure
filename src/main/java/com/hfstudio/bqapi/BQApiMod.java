package com.hfstudio.bqapi;

import com.hfstudio.bqapi.runtime.BQReinjector;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import gregtech.api.enums.Mods;

@Mod(
    modid = BQApiMod.MODID,
    name = "HFStudio BetterQuesting Runtime API",
    version = BQApiMod.VERSION,
    dependencies = "after:betterquesting;",
    acceptedMinecraftVersions = "1.7.10")
public class BQApiMod {

    public static final String MODID = "hfstudiobqapi";
    public static final String VERSION = "0.1.0";

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        if (!Mods.BetterQuesting.isModLoaded()) return;
        BQReinjector.reinject(
            FMLCommonHandler.instance()
                .getMinecraftServerInstance());
    }
}
