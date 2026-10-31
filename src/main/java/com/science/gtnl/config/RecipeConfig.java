package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.RequiresMcRestart;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;
import com.science.gtnl.ScienceNotLeisure;

@Config(
    modid = ScienceNotLeisure.MODID,
    category = "recipe",
    configSubDirectory = ConfigRegistry.DIRECTORY,
    filename = ConfigRegistry.RECIPE_FILE)
@Config.LangKeyPattern(pattern = ConfigRegistry.LANG_PATTERN, fullyQualified = true)
@Sync
@Comment("Recipe section")
public class RecipeConfig {

    @Comment("Significantly reduce the time required for crafting recipes")
    @DefaultBoolean(false)
    @RequiresMcRestart
    public static boolean enableAssemblingLineRecipesTimeChange = false;

    @Comment("Buff all chamber recipes")
    @DefaultBoolean(true)
    @RequiresMcRestart
    public static boolean enableChamberRecipesBuff = true;

    @Comment("Enable Delete Recipe")
    @DefaultBoolean(true)
    @RequiresMcRestart
    public static boolean enableDeleteRecipe = true;

    @Comment("Enable when player join world, Show Big Title")
    @DefaultBoolean(true)
    public static boolean enableShowDelRecipeTitle = true;

    @Comment("Enable Something Cheap Recipe")
    @DefaultBoolean(true)
    @RequiresMcRestart
    public static boolean enableSomethingRecipe = true;

    @Comment("Enable generated Shimmer disassembly recipes")
    @DefaultBoolean(true)
    @RequiresMcRestart
    public static boolean enableShimmerDisassemblyRecipes = true;

    @Comment("Register generated Shimmer fake recipes in NEI")
    @DefaultBoolean(true)
    @RequiresMcRestart
    public static boolean enableShimmerFakeRecipeInNEI = true;
}
