package com.gtnltest.runtime;

import java.util.Locale;

import com.science.gtnl.utils.enums.GTNLMachineID;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Mods;

/** Registry identity shared by the independent persistence and GUI scenarios. */
record MachineCatalogEntry(GTNLMachineID type, int variant) {

    int metaId() {
        return type.ID + variant;
    }

    private String key() {
        return "machine." + type.name()
            .toLowerCase(Locale.ROOT) + (variant == 0 ? "" : ".variant_" + variant);
    }

    String persistenceId() {
        return key() + ".persistence";
    }

    String guiId() {
        return key() + ".gui";
    }

    String skipReason() {
        if (type == GTNLMachineID.STAR_GATE_WIRE && variant >= 6 && GregTechAPI.METATILEENTITIES[metaId()] == null)
            return "Insulated Stargate cable IDs are reserved; registration currently disables insulation";
        if (type == GTNLMachineID.BLOOD_SOUL_SACRIFICIAL_ARRAY && !Mods.BloodMagic.isModLoaded())
            return "BloodMagic is not installed";
        if (type == GTNLMachineID.LOOT_BAG_REDEMPTION && !Mods.EnhancedLootBags.isModLoaded())
            return "EnhancedLootBags is not installed";
        return null;
    }
}
