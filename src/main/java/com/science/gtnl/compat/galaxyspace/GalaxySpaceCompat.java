package com.science.gtnl.compat.galaxyspace;

import com.science.gtnl.common.item.steamRocket.SchematicSteamRocket;
import com.science.gtnl.common.recipe.gtnl.RocketAssemblerRecipes;
import com.science.gtnl.compat.IModCompat;

import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;

public class GalaxySpaceCompat implements IModCompat {

    @Override
    public void postInit() {
        SchematicRegistry.registerSchematicRecipe(new SchematicSteamRocket());
        RocketAssemblerRecipes.loadSteamRocketRecipe();
    }
}
