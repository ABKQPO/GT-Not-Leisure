package com.science.gtnl.compat.overpowered;

import com.gtnllib.mod.ModList;
import com.science.gtnl.config.MainConfig;

// GTNL's recipe output chance yields to Overpowered, which brings its own.
public class RecipeOutputChance {

    private RecipeOutputChance() {}

    public static boolean enabled() {
        return !ModList.Overpowered.isModLoaded() && MainConfig.machine.enableRecipeOutputChance;
    }
}
