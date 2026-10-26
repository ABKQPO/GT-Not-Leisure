package com.science.gtnl.compat.visualprospecting;

import java.util.Collections;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import com.sinthoras.visualprospecting.VisualProspecting_API;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;

// Callable without VisualProspecting installed: its classes are only touched inside the optional methods.
public class ProspectionResults {

    private ProspectionResults() {}

    public static void sendOres(World world, EntityPlayerMP player, int blockX, int blockZ, int blockRadius) {
        if (Mods.VisualProspecting.isModLoaded()) ores(world, player, blockX, blockZ, blockRadius);
    }

    public static void sendFluids(World world, EntityPlayerMP player, int blockX, int blockZ, int blockRadius) {
        if (Mods.VisualProspecting.isModLoaded()) fluids(world, player, blockX, blockZ, blockRadius);
    }

    @Optional.Method(modid = "visualprospecting")
    private static void ores(World world, EntityPlayerMP player, int blockX, int blockZ, int blockRadius) {
        VisualProspecting_API.LogicalServer.sendProspectionResultsToClient(
            player,
            VisualProspecting_API.LogicalServer
                .prospectOreVeinsWithinRadius(world.provider.dimensionId, blockX, blockZ, blockRadius),
            Collections.emptyList());
    }

    @Optional.Method(modid = "visualprospecting")
    private static void fluids(World world, EntityPlayerMP player, int blockX, int blockZ, int blockRadius) {
        VisualProspecting_API.LogicalServer.sendProspectionResultsToClient(
            player,
            Collections.emptyList(),
            VisualProspecting_API.LogicalServer
                .prospectUndergroundFluidsWithingRadius(world, blockX, blockZ, blockRadius));
    }
}
