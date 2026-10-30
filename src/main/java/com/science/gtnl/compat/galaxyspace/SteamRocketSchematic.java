package com.science.gtnl.compat.galaxyspace;

import net.minecraft.entity.player.EntityPlayerMP;

import com.science.gtnl.config.MainConfig;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;

public class SteamRocketSchematic {

    public static void unlockFor(EntityPlayerMP player) {
        if (Mods.GalaxySpace.isModLoaded() && Mods.GalacticraftCore.isModLoaded()) unlock(player);
    }

    @Optional.Method(modid = "GalacticraftCore")
    private static void unlock(EntityPlayerMP player) {
        SchematicRegistry.addUnlockedPage(
            player,
            SchematicRegistry.getMatchingRecipeForID(MainConfig.item.steam_rocket.idSchematicRocketSteam));
    }
}
