package com.gtnllib.config;

import net.minecraft.client.gui.GuiScreen;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.SimpleGuiConfig;
import com.gtnewhorizon.gtnhlib.config.SimpleGuiFactory;
import com.gtnllib.GTNLLib;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GTNLLibGuiFactory implements SimpleGuiFactory {

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return Screen.class;
    }

    public static class Screen extends SimpleGuiConfig {

        public Screen(GuiScreen parent) throws ConfigException {
            super(parent, GTNLLib.MODID, GTNLLib.NAME, true, GTNLLibConfig.class);
        }
    }
}
