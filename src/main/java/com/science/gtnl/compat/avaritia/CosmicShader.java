package com.science.gtnl.compat.avaritia;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fox.spiteful.avaritia.render.CosmicRenderShenanigans;
import gregtech.api.enums.Mods;

// Callable without Avaritia installed: its classes are only touched inside the optional methods.
@SideOnly(Side.CLIENT)
public class CosmicShader {

    private CosmicShader() {}

    public static boolean isAvailable() {
        return Mods.Avaritia.isModLoaded();
    }

    @Optional.Method(modid = "Avaritia")
    public static void beginInventory(float opacity) {
        CosmicRenderShenanigans.cosmicOpacity = opacity;
        CosmicRenderShenanigans.inventoryRender = true;
        CosmicRenderShenanigans.useShader();
    }

    @Optional.Method(modid = "Avaritia")
    public static void endInventory() {
        CosmicRenderShenanigans.releaseShader();
        CosmicRenderShenanigans.inventoryRender = false;
    }
}
