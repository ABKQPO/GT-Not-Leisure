package com.science.gtnl.config;

import net.minecraft.client.gui.GuiScreen;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.SimpleGuiConfig;
import com.gtnewhorizon.gtnhlib.config.SimpleGuiFactory;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnllib.config.GTNLLibConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConfigGuiFactory implements SimpleGuiFactory {

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return Screen.class;
    }

    public static class Screen extends SimpleGuiConfig {

        public Screen(GuiScreen parent) throws ConfigException {
            super(
                parent,
                ScienceNotLeisure.MODID,
                ScienceNotLeisure.MODNAME,
                true,
                MainConfig.class,
                EquipmentSoundConfig.class,
                GTNLLibConfig.class);
        }
    }
}
