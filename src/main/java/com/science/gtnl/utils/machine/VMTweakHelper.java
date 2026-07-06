package com.science.gtnl.utils.machine;

import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.science.gtnl.ScienceNotLeisure;

import bartworks.common.configs.Configuration;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import galacticgreg.api.ModDimensionDef;
import galacticgreg.registry.GalacticGregRegistry;
import gtneioreplugin.util.DimensionHelper;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class VMTweakHelper {

    private static final String UNDERDARK_CHUNK_PROVIDER = "com.rwtema.extrautils.worldgen.Underdark.ChunkProviderUnderdark";
    private static final String UNDERDARK_DIMENSION_NAME = "Underdark";

    public static final BiMap<Integer, String> DIM_MAPPING = HashBiMap.create();
    public static final Int2ObjectOpenHashMap<String> CACHE = new Int2ObjectOpenHashMap<>();

    public static void initializeDimensionMappings() {
        DIM_MAPPING.forcePut(0, "Ow");
        DIM_MAPPING.forcePut(-1, "Ne");
        DIM_MAPPING.forcePut(1, "ED");
        DIM_MAPPING.forcePut(7, "TF");
        DIM_MAPPING.forcePut(100, "DD");
        putConfiguredDimension(Configuration.crossModInteractions.ross128BID, "Rb");
        putConfiguredDimension(Configuration.crossModInteractions.ross128BAID, "Ra");
    }

    private static void putConfiguredDimension(int dimensionId, String abbreviation) {
        if (DIM_MAPPING.containsKey(dimensionId) || DIM_MAPPING.containsValue(abbreviation)) return;
        DIM_MAPPING.forcePut(dimensionId, abbreviation);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        String loadedChunkProviderName = cacheChunkProviderName(event.world);
        if (UNDERDARK_CHUNK_PROVIDER.equals(loadedChunkProviderName)) {
            mapDimensionName(event.world.provider.dimensionId, UNDERDARK_DIMENSION_NAME, true);
        }

        for (int i : DimensionManager.getStaticDimensionIDs()) {
            if (DIM_MAPPING.containsKey(i)) continue;
            String name = getNameForID(i);
            mapDimensionName(i, name, false);
        }
    }

    private static String cacheChunkProviderName(World world) {
        try {
            if (world.getChunkProvider() instanceof ChunkProviderServer) {
                String chunkProviderName = ((ChunkProviderServer) world.getChunkProvider()).currentChunkProvider
                    .getClass()
                    .getName();
                CACHE.put(world.provider.dimensionId, chunkProviderName);
                return chunkProviderName;
            }
        } catch (Exception e) {
            ScienceNotLeisure.LOG
                .debug("Failed to cache chunk provider class for dim {}", world.provider.dimensionId, e);
        }
        return null;
    }

    private static void mapDimensionName(int dimensionId, String dimensionName, boolean replaceExistingAbbreviation) {
        int index = DimensionHelper.ALL_DIM_NAMES.indexOf(dimensionName);
        if (index < 0) return;

        String abbreviation = DimensionHelper.ALL_DISPLAYED_NAMES.get(index);
        if (replaceExistingAbbreviation) {
            DIM_MAPPING.forcePut(dimensionId, abbreviation);
            return;
        }
        putConfiguredDimension(dimensionId, abbreviation);
    }

    public static String getNameForID(int id) {
        if (id == Configuration.crossModInteractions.ross128BID) {
            return "Ross128b";
        }
        if (id == Configuration.crossModInteractions.ross128BAID) {
            return "Ross128ba";
        }
        if (id == 0) {
            return "Overworld";
        }
        if (id == -1) {
            return "Nether";
        }
        if (id == 7) {
            return "Twilight";
        }
        if (id == 1) {
            return "The End";
        }

        return GalacticGregRegistry.getModContainers()
            .stream()
            .flatMap(
                modContainer -> modContainer.getDimensionList()
                    .stream())
            .filter(def -> {
                if (DimensionManager.getWorld(id) == null) return false;
                return def.getChunkProviderName()
                    .equals(
                        DimensionManager.getProvider(id)
                            .createChunkGenerator()
                            .getClass()
                            .getName());
            })
            .map(ModDimensionDef::getDimIdentifier)
            .findFirst()
            .orElse(null);
    }
}
