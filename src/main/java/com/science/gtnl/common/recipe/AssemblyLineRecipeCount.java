package com.science.gtnl.common.recipe;

import gregtech.api.util.GTRecipe;
import tectech.recipe.TecTechRecipeMaps;

public class AssemblyLineRecipeCount {

    public static final long NONE = -1L;

    // Changes whenever either assembly line recipe list grows or shrinks; caches keyed on it rebuild then.
    public static long stamp() {
        return (long) GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes.size() << 32
            | TecTechRecipeMaps.researchableALRecipeList.size();
    }
}
