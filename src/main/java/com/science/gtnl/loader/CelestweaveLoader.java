package com.science.gtnl.loader;

import com.science.gtnl.client.celestweave.CelestweaveClientEvents;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CelestweaveLoader implements IContentLoader {

    @Override
    public String name() {
        return "celestweave";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        CelestweaveClientEvents.register();
    }
}
