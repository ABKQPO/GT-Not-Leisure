package com.science.gtnl.loader;

import net.minecraftforge.common.MinecraftForge;

import com.science.gtnl.client.wireless.WirelessCardInput;
import com.science.gtnl.client.wireless.WirelessWorldHighlight;
import com.science.gtnl.common.command.CommandWirelessME;
import com.science.gtnl.common.wireless.WirelessAutoConnect;
import com.science.gtnl.common.wireless.WirelessChannelManager;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WirelessLoader implements IContentLoader {

    @Override
    public String name() {
        return "wireless";
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(WirelessChannelManager.INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(WirelessChannelManager.INSTANCE);
        MinecraftForge.EVENT_BUS.register(WirelessAutoConnect.INSTANCE);
    }

    @Override
    public void serverAboutToStart() {
        // Spawn chunks load before ServerStarting. Do not erase their restored-link discoveries afterwards.
        WirelessChannelManager.clear();
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandWirelessME());
    }

    @Override
    public void serverStopping() {
        WirelessChannelManager.clear();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        WirelessCardInput.register();
        MinecraftForge.EVENT_BUS.register(WirelessWorldHighlight.INSTANCE);
    }
}
