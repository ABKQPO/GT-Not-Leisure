package com.science.gtnl.compat.visualprospecting;

import com.science.gtnl.common.world.GTNLOreMixer;
import com.science.gtnl.common.world.GTNLVeinCatalog;
import com.science.gtnl.mixins.late.visualProspecting.AccessorVeinTypeCaching;
import com.sinthoras.visualprospecting.database.veintypes.VeinType;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;
import gregtech.common.OreMixBuilder;

public class OreVeinTypes {

    private OreVeinTypes() {}

    public static void register() {
        if (Mods.VisualProspecting.isModLoaded()) registerVeins();
    }

    @Optional.Method(modid = "visualprospecting")
    private static void registerVeins() {
        for (GTNLOreMixer oreMix : GTNLOreMixer.values()) {
            AccessorVeinTypeCaching.getVeinTypes()
                .put(oreMix.oreMixBuilder.oreMixName, new VeinType(oreMix.oreMixBuilder));
        }
        for (OreMixBuilder vein : GTNLVeinCatalog.additionalVeins()) {
            AccessorVeinTypeCaching.getVeinTypes()
                .put(vein.oreMixName, new VeinType(vein));
        }
    }
}
