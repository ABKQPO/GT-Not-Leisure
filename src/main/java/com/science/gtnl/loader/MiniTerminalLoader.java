package com.science.gtnl.loader;

import com.science.gtnl.client.meminiterminal.MiniTerminalClient;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MiniTerminalLoader implements IContentLoader {

    @Override
    public String name() {
        return "meminiterminal";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        MiniTerminalClient.init();
    }
}
