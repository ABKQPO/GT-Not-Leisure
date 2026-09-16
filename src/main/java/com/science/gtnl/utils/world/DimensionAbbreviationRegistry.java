package com.science.gtnl.utils.world;

import java.util.ArrayList;
import java.util.List;

import com.science.gtnl.api.DimensionNames;
import com.science.gtnl.api.IDimensionAbbreviationRegistry;
import com.science.gtnl.api.IDimensionAbbreviationResolver;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class DimensionAbbreviationRegistry implements IDimensionAbbreviationRegistry {

    private final List<IDimensionAbbreviationResolver> resolvers = new ArrayList<>();
    private final Int2ObjectOpenHashMap<DimensionNames> namesByDimension = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<String> abbreviationsByDimension = new Int2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, Integer> dimensionsByAbbreviation = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, String> abbreviationsByName = new Object2ObjectOpenHashMap<>();

    @Override
    public void registerResolver(IDimensionAbbreviationResolver resolver) {
        if (resolver == null || resolvers.contains(resolver)) return;

        resolvers.add(resolver);
    }

    @Override
    public String getAbbreviation(int dimensionId) {
        String cached = abbreviationsByDimension.get(dimensionId);
        if (cached != null) return cached;

        DimensionNames names = getNames(dimensionId);
        if (names == null || names.abbreviation() == null) return null;

        bindIfAbsent(dimensionId, names.abbreviation());
        return names.abbreviation();
    }

    @Override
    public String getAbbreviationByName(String dimensionName) {
        if (dimensionName == null) return null;

        String cached = abbreviationsByName.get(dimensionName);
        if (cached != null) return cached;

        for (IDimensionAbbreviationResolver resolver : resolvers) {
            String abbreviation = resolver.resolveAbbreviationByName(dimensionName);
            if (abbreviation == null) continue;

            abbreviationsByName.put(dimensionName, abbreviation);
            return abbreviation;
        }

        return null;
    }

    @Override
    public String getDimensionName(int dimensionId) {
        DimensionNames names = getNames(dimensionId);
        return names == null ? null : names.name();
    }

    @Override
    public Integer getDimensionId(String abbreviation) {
        return abbreviation == null ? null : dimensionsByAbbreviation.get(abbreviation);
    }

    @Override
    public void bind(int dimensionId, String abbreviation) {
        if (abbreviation == null) return;

        Integer previousDimension = dimensionsByAbbreviation.get(abbreviation);
        if (previousDimension != null && previousDimension != dimensionId) {
            abbreviationsByDimension.remove(previousDimension.intValue());
        }

        String previousAbbreviation = abbreviationsByDimension.get(dimensionId);
        if (previousAbbreviation != null && !previousAbbreviation.equals(abbreviation)) {
            dimensionsByAbbreviation.remove(previousAbbreviation);
        }

        abbreviationsByDimension.put(dimensionId, abbreviation);
        dimensionsByAbbreviation.put(abbreviation, dimensionId);
    }

    public void bindIfAbsent(int dimensionId, String abbreviation) {
        if (abbreviationsByDimension.containsKey(dimensionId) || dimensionsByAbbreviation.containsKey(abbreviation)) {
            return;
        }

        abbreviationsByDimension.put(dimensionId, abbreviation);
        dimensionsByAbbreviation.put(abbreviation, dimensionId);
    }

    public DimensionNames getNames(int dimensionId) {
        DimensionNames cached = namesByDimension.get(dimensionId);
        if (cached != null) return cached;

        for (IDimensionAbbreviationResolver resolver : resolvers) {
            DimensionNames names = resolver.resolve(dimensionId);
            if (names == null) continue;

            namesByDimension.put(dimensionId, names);
            return names;
        }

        return null;
    }
}
