package com.science.gtnl.utils.machine.bee;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;

import gregtech.api.enums.VoltageIndex;

public final class BeeBreedingTiers {

    private static volatile TierTable cachedTable;

    private BeeBreedingTiers() {}

    public static int getVoltageTier(String species) {
        String[] entries = MainConfig.machine.bee_breeder.speciesVoltageTiers;
        TierTable table = cachedTable;
        if (table == null || table.entries != entries) {
            table = new TierTable(entries);
            cachedTable = table;
        }
        return table.tiers.getOrDefault(species, VoltageIndex.UHV);
    }

    private static final class TierTable {

        private final String[] entries;
        private final Map<String, Integer> tiers = new HashMap<>();

        private TierTable(String[] entries) {
            this.entries = entries;
            if (entries == null) return;
            for (String entry : entries) {
                if (entry == null || entry.trim()
                    .isEmpty()) continue;
                int separator = entry.lastIndexOf('=');
                String uid = separator < 0 ? ""
                    : entry.substring(0, separator)
                        .trim();
                int tier = separator < 0 ? -1
                    : switch (entry.substring(separator + 1)
                        .trim()
                        .toUpperCase(Locale.ROOT)) {
                    case "IV" -> VoltageIndex.IV;
                    case "ZPM" -> VoltageIndex.ZPM;
                    case "UHV" -> VoltageIndex.UHV;
                    default -> -1;
                    };
                if (uid.isEmpty() || tier < 0) {
                    ScienceNotLeisure.LOG.warn("ignoring invalid bee breeder voltage entry: {}", entry);
                    continue;
                }
                tiers.put(uid, tier);
            }
        }
    }
}
