package com.science.gtnl.compat.bloodmagic;

import WayofTime.alchemicalWizardry.AlchemicalWizardry;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;

// Callable without Blood Magic installed: its classes are only touched inside the optional method.
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
