package com.science.gtnl.compat.galaxyspace;

import com.science.gtnl.common.item.steamRocket.SchematicSteamRocket;
import com.science.gtnl.common.recipe.gtnl.RocketAssemblerRecipes;
import com.science.gtnl.compat.IModCompat;

import gregtech.api.enums.Mods;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;

public class GalaxySpaceCompat implements IModCompat {

    @Override
    public boolean isEnabled() {
        return Mods.StevesCarts2.isModLoaded() && Mods.Railcraft.isModLoaded()
            && Mods.IronTanks.isModLoaded()
            && Mods.GraviSuite.isModLoaded();
    }

    @Override
    public void postInit() {
        SchematicRegistry.registerSchematicRecipe(new SchematicSteamRocket());
        RocketAssemblerRecipes.loadSteamRocketRecipe();
    }
}
