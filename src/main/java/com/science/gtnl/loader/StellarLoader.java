package com.science.gtnl.loader;

import com.science.gtnl.api.stellar.StellarIrisUpgradeCatalog;
import com.science.gtnl.common.command.CommandStellarIris;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class StellarLoader implements IContentLoader {

    @Override
    public String name() {
        return "stellar";
    }

    @Override
    public void postInit() {
        StellarIrisUpgradeCatalog.registerDefaults();
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandStellarIris());
    }
}
