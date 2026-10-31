package com.science.gtnl.compat.nei;

import com.science.gtnl.client.GTNLInputHandler;
import com.science.gtnl.client.GTNLTooltipManager;
import com.science.gtnl.compat.IModCompat;

import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class NEICompat implements IModCompat {

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        GuiContainerManager.addTooltipHandler(new GTNLTooltipManager());
        GuiContainerManager.addInputHandler(GTNLInputHandler.INSTANCE);
    }
}
