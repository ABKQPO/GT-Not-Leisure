package com.science.gtnl.loader;

import com.science.gtnl.client.yogsothoth.YogTerminalClient;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class YogSothothLoader implements IContentLoader {

    @Override
    public String name() {
        return "yogsothoth";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        YogTerminalClient.init();
    }
}
