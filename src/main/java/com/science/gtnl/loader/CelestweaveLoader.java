package com.science.gtnl.loader;

import com.science.gtnl.client.celestweave.CelestweaveClientEvents;
import com.science.gtnl.common.celestweave.CelestweaveServerEvents;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CelestweaveLoader implements IContentLoader {

    @Override
    public String name() {
        return "celestweave";
    }

    @Override
    public void preInit() {
        CelestweaveServerEvents.register();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        CelestweaveClientEvents.register();
    }
}
