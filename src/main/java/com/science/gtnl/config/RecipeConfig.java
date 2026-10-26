package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.RequiresMcRestart;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;

@Sync
@Comment("Recipe section")
public class RecipeConfig {

    @Comment("Significantly reduce the time required for crafting recipes")
    @DefaultBoolean(false)
    @RequiresMcRestart
    public boolean enableAssemblingLineRecipesTimeChange = false;

    @Comment("Buff all chamber recipes")
    @DefaultBoolean(true)
    @RequiresMcRestart
    public boolean enableChamberRecipesBuff = true;

    @Comment("Enable Delete Recipe")
    @DefaultBoolean(true)
    @RequiresMcRestart
    public boolean enableDeleteRecipe = true;

    @Comment("Enable when player join world, Show Big Title")
    @DefaultBoolean(true)
    public boolean enableShowDelRecipeTitle = true;

    @Comment("Enable Something Cheap Recipe")
    @DefaultBoolean(true)
    @RequiresMcRestart
    public boolean enableSomethingRecipe = true;

    @Comment("Enable generated Shimmer disassembly recipes")
    @DefaultBoolean(true)
    @RequiresMcRestart
    public boolean enableShimmerDisassemblyRecipes = true;

    @Comment("Register generated Shimmer fake recipes in NEI")
    @DefaultBoolean(true)
    @RequiresMcRestart
    public boolean enableShimmerFakeRecipeInNEI = true;
}
