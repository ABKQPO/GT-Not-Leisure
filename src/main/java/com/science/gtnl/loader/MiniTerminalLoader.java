package com.science.gtnl.loader;

import net.minecraftforge.common.MinecraftForge;

import com.science.gtnl.client.meminiterminal.MiniTerminalClient;
import com.science.gtnl.common.meminiterminal.MiniTerminalSessionService;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MiniTerminalLoader implements IContentLoader {

    @Override
    public String name() {
        return "meminiterminal";
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(MiniTerminalSessionService.INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(MiniTerminalSessionService.INSTANCE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        MiniTerminalClient.init();
    }
}
