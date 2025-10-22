package com.science.gtnl.utils.machine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import bartworks.common.configs.Configuration;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import galacticgreg.api.ModDimensionDef;
import galacticgreg.registry.GalacticGregRegistry;
import gtneioreplugin.util.DimensionHelper;

public class VMTweakHelper {

    public static final List<String> dimName = Arrays.asList(DimensionHelper.DimName);
    public static final List<String> dimNameShort = Arrays.asList(DimensionHelper.DimNameDisplayed);
    public static final BiMap<Integer, String> dimMapping = HashBiMap.create();
    public static final HashMap<Integer, String> cache = new HashMap<>();

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        for (int i : DimensionManager.getStaticDimensionIDs()) {
            if (dimMapping.containsKey(i)) continue;
            String name = getNameForID(i);

            int index = dimName.indexOf(name);
            if (index >= 0) {
                dimMapping.forcePut(i, DimensionHelper.DimNameDisplayed[index]);
            }
        }

        try {
            World world = event.world;
            if (world.getChunkProvider() instanceof ChunkProviderServer) {
                cache.put(
                    world.provider.dimensionId,
                    ((ChunkProviderServer) world.getChunkProvider()).currentChunkProvider.getClass()
                        .getName());
            }
        } catch (Exception ignored) {}
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
            return "TheEnd";
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
