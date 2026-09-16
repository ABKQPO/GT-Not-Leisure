package com.science.gtnl.utils.world;

import java.util.stream.Collectors;

import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.DimensionNames;
import com.science.gtnl.api.IDimensionAbbreviationResolver;

import galacticgreg.api.ModDimensionDef;
import galacticgreg.registry.GalacticGregRegistry;
import gtneioreplugin.util.DimensionHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

public class GalacticGregDimensionAbbreviationResolver implements IDimensionAbbreviationResolver {

    private ObjectList<ModDimensionDef> dimensionDefinitions;

    @Override
    public DimensionNames resolve(int dimensionId) {
        String chunkProviderName = createChunkProviderName(dimensionId);
        if (chunkProviderName == null) return null;

        for (ModDimensionDef definition : getDimensionDefinitions()) {
            if (!chunkProviderName.equals(definition.getChunkProviderName())) continue;

            String name = definition.getDimIdentifier();
            return new DimensionNames(name, resolveAbbreviationByName(name));
        }

        return null;
    }

    @Override
    public String resolveAbbreviationByName(String dimensionName) {
        if (dimensionName == null) return null;

        int index = DimensionHelper.ALL_DIM_NAMES.indexOf(dimensionName);
        return index < 0 ? null : DimensionHelper.ALL_DISPLAYED_NAMES.get(index);
    }

    public static String createChunkProviderName(int dimensionId) {
        try {
            var world = DimensionManager.getWorld(dimensionId);
            if (world == null) return null;

            var chunkProvider = world.getChunkProvider();
            if (chunkProvider instanceof ChunkProviderServer server) {
                chunkProvider = server.currentChunkProvider;
            }
            return chunkProvider == null ? null
                : chunkProvider.getClass()
                    .getName();
        } catch (Exception e) {
            ScienceNotLeisure.LOG.debug("Failed to create the chunk provider of dimension {}", dimensionId, e);
            return null;
        }
    }

    public ObjectList<ModDimensionDef> getDimensionDefinitions() {
        ObjectList<ModDimensionDef> cached = dimensionDefinitions;
        if (cached != null) return cached;

        ObjectList<ModDimensionDef> collected = GalacticGregRegistry.getModContainers()
            .stream()
            .flatMap(
                modContainer -> modContainer.getDimensionList()
                    .stream())
            .collect(Collectors.toCollection(ObjectArrayList::new));
        dimensionDefinitions = collected;
        return collected;
    }
}
