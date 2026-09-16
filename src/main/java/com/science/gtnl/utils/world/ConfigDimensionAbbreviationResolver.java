package com.science.gtnl.utils.world;

import com.science.gtnl.api.DimensionNames;
import com.science.gtnl.api.IDimensionAbbreviationResolver;

import bartworks.common.configs.Configuration;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class ConfigDimensionAbbreviationResolver implements IDimensionAbbreviationResolver {

    private final Int2ObjectOpenHashMap<DimensionNames> names = new Int2ObjectOpenHashMap<>();

    public ConfigDimensionAbbreviationResolver() {
        put(0, "Overworld", "Ow");
        put(-1, "Nether", "Ne");
        put(1, "The End", "ED");
        put(7, "Twilight", "TF");
        put(100, null, "DD");
        put(Configuration.crossModInteractions.ross128BID, "Ross128b", "Rb");
        put(Configuration.crossModInteractions.ross128BAID, "Ross128ba", "Ra");
    }

    @Override
    public DimensionNames resolve(int dimensionId) {
        return names.get(dimensionId);
    }

    public void put(int dimensionId, String name, String abbreviation) {
        if (names.containsKey(dimensionId) || isAbbreviationUsed(abbreviation)) return;

        names.put(dimensionId, new DimensionNames(name, abbreviation));
    }

    public boolean isAbbreviationUsed(String abbreviation) {
        for (DimensionNames known : names.values()) {
            if (known.abbreviation()
                .equals(abbreviation)) return true;
        }

        return false;
    }
}
