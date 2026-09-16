package com.science.gtnl.utils.machine;

import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.event.world.WorldEvent;

import com.rwtema.extrautils.worldgen.Underdark.ChunkProviderUnderdark;
import com.science.gtnl.api.IDimensionAbbreviationRegistry;
import com.science.gtnl.utils.world.ConfigDimensionAbbreviationResolver;
import com.science.gtnl.utils.world.DimensionAbbreviationRegistry;
import com.science.gtnl.utils.world.GalacticGregDimensionAbbreviationResolver;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gregtech.api.enums.Mods;

public class VMTweakHelper {

    public static final String UNDERDARK_DIMENSION_NAME = "Underdark";

    public static final IDimensionAbbreviationRegistry DIM_MAPPING = new DimensionAbbreviationRegistry();

    public static void initializeDimensionMappings() {
        DIM_MAPPING.registerResolver(new ConfigDimensionAbbreviationResolver());
        DIM_MAPPING.registerResolver(new GalacticGregDimensionAbbreviationResolver());
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        World world = event.world;
        if (world == null) return;

        bindUnderdarkDimension(world);
        DIM_MAPPING.getAbbreviation(world.provider.dimensionId);
    }

    public static void bindUnderdarkDimension(World world) {
        if (!Mods.ExtraUtilities.isModLoaded() || world == null) return;
        if (!isUnderdarkChunkProvider(world)) return;
        String abbreviation = DIM_MAPPING.getAbbreviationByName(UNDERDARK_DIMENSION_NAME);
        if (abbreviation == null) return;
        DIM_MAPPING.bind(world.provider.dimensionId, abbreviation);
    }

    @Optional.Method(modid = "ExtraUtilities")
    public static boolean isUnderdarkChunkProvider(World world) {
        if (!(world.getChunkProvider() instanceof ChunkProviderServer chunkProviderServer)) return false;
        return chunkProviderServer.currentChunkProvider instanceof ChunkProviderUnderdark;
    }
}
