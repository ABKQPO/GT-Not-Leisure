package com.science.gtnl.compat.bloodmagic;

import WayofTime.alchemicalWizardry.AlchemicalWizardry;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;

public class BloodMagicPotions {

    private BloodMagicPotions() {}

    public static int flightId(int fallback) {
        return Mods.BloodMagic.isModLoaded() ? flight() : fallback;
    }

    @Optional.Method(modid = "AWWayofTime")
    private static int flight() {
        return AlchemicalWizardry.customPotionFlightID;
    }
}
