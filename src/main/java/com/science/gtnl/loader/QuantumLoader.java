package com.science.gtnl.loader;

import com.science.gtnl.client.quantum.RecursivePatternReport;
import com.science.gtnl.common.crafting.quantum.ae2.QuantumCraftingCache;
import com.science.gtnl.common.crafting.quantum.ae2.QuantumCraftingMode;
import com.science.gtnl.common.crafting.quantum.hooks.ticking.TickHandler;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class QuantumLoader implements IContentLoader {

    @Override
    public String name() {
        return "quantum";
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        TickHandler.instance()
            .openSession();
    }

    @Override
    public void serverStopping() {
        TickHandler.instance()
            .closeSession();
        QuantumCraftingMode.clear();
        QuantumCraftingCache.clearAll();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        RecursivePatternReport.register();
    }
}
