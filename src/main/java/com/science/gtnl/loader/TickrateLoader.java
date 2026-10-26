package com.science.gtnl.loader;

import com.science.gtnl.api.TickrateAPI;
import com.science.gtnl.common.command.CommandTickrate;
import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class TickrateLoader implements IContentLoader {

    @Override
    public String name() {
        return "tickrate";
    }

    @Override
    public void init() {
        TickrateAPI.changeTickrate(MainConfig.tickrate.defaultTickrate);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandTickrate());
    }
}
