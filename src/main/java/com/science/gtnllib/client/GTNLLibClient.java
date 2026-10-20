package com.science.gtnllib.client;

import net.minecraftforge.common.MinecraftForge;

import com.science.gtnllib.client.hud.HudOverlay;
import com.science.gtnllib.client.shader.ShaderEffect;
import com.science.gtnllib.config.GTNLLibConfig;
import com.science.gtnllib.gui.theme.PaletteThemes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GTNLLibClient {

    private GTNLLibClient() {}

    public static void preInit() {
        GTNLLibConfig.register();
        HudOverlay.register();
        MinecraftForge.EVENT_BUS.register(new ShaderEffect.Reloader());
        MinecraftForge.EVENT_BUS.register(new PaletteThemes.Reloader());
    }
}
