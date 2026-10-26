package com.science.gtnl.compat.thaumichorizons;

import com.kentington.thaumichorizons.common.lib.potion.PotionVisRegen;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;

public class ThaumicHorizonsPotions {

    private ThaumicHorizonsPotions() {}

    public static boolean hasVisRegen() {
        return Mods.ThaumicHorizons.isModLoaded();
    }

    public static int visRegenId() {
        return hasVisRegen() ? visRegen() : -1;
    }

    @Optional.Method(modid = "ThaumicHorizons")
    private static int visRegen() {
        return PotionVisRegen.instance.id;
    }
}
