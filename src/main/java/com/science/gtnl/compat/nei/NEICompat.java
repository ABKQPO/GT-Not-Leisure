package com.science.gtnl.compat.nei;

import net.minecraftforge.common.MinecraftForge;

import com.science.gtnl.client.GTNLInputHandler;
import com.science.gtnl.client.GTNLTooltipManager;
import com.science.gtnl.compat.IModCompat;

import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class NEICompat implements IModCompat {

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPreInit() {
        GuiContainerManager.addTooltipHandler(new GTNLTooltipManager());
        MinecraftForge.EVENT_BUS.register(GTNLInputHandler.INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(GTNLInputHandler.INSTANCE);
    }
}
