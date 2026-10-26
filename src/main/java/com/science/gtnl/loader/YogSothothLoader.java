package com.science.gtnl.loader;

import net.minecraftforge.common.MinecraftForge;

import com.science.gtnl.client.yogsothoth.YogTerminalClient;
import com.science.gtnl.common.yogsothoth.YogTerminalService;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class YogSothothLoader implements IContentLoader {

    @Override
    public String name() {
        return "yogsothoth";
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(YogTerminalService.INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(YogTerminalService.INSTANCE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        YogTerminalClient.init();
    }
}
