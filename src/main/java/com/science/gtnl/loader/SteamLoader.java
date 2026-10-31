package com.science.gtnl.loader;

import com.science.gtnl.common.command.CommandSteamNetwork;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class SteamLoader implements IContentLoader {

    @Override
    public String name() {
        return "steam";
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSteamNetwork());
    }
}
