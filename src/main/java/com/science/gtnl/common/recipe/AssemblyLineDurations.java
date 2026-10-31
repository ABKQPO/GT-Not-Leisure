package com.science.gtnl.common.recipe;

import com.science.gtnl.config.RecipeConfig;

public class AssemblyLineDurations {

    public static int scale(int duration) {
        if (!RecipeConfig.enableAssemblingLineRecipesTimeChange) return duration;
        if (duration >= 200000) return duration / 100;
        if (duration >= 40000) return duration / 10;
        if (duration >= 10000) return duration / 4;
        if (duration >= 4000) return duration / 2;
        return duration;
    }
}
