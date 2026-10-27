package com.science.gtnl.compat.variablehorizons;

import com.LazyFlesh.variablehorizons.variants.VariantNames;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;

public class VariableHorizonsCompat {

    private VariableHorizonsCompat() {}

    public static boolean anyActive(String... variantNames) {
        return Mods.VariableHorizons.isModLoaded() && anyActiveVariant(variantNames);
    }

    @Optional.Method(modid = Mods.ModIDs.VARIABLE_HORIZONS)
    private static boolean anyActiveVariant(String... variantNames) {
        for (String variantName : variantNames) {
            if (VariantNames.activeContains(variantName)) return true;
        }
        return false;
    }
}
