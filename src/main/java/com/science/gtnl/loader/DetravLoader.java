package com.science.gtnl.loader;

import net.minecraftforge.common.MinecraftForge;

import com.science.gtnl.client.detrav.DetravOreMarkerRenderer;
import com.science.gtnl.client.detrav.DetravScannerInputHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DetravLoader implements IContentLoader {

    @Override
    public String name() {
        return "detrav";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        MinecraftForge.EVENT_BUS.register(new DetravOreMarkerRenderer());
        MinecraftForge.EVENT_BUS.register(new DetravScannerInputHandler());
    }
}
