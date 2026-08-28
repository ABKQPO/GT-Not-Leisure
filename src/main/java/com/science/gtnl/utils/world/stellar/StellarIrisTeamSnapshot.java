package com.science.gtnl.utils.world.stellar;

import java.util.Map;

import com.science.gtnl.api.stellar.StellarIrisUpgradeDefinition;
import com.science.gtnl.api.stellar.StellarIrisUpgradeRegistry;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.Getter;

public class StellarIrisTeamSnapshot {

    @Getter
    private int spendablePoints;
    @Getter
    private int lifetimePoints;
    @Getter
    private int tier;
    private final Int2IntMap networkLevels = new Int2IntOpenHashMap();

    public StellarIrisTeamSnapshot() {}

    public StellarIrisTeamSnapshot(int spendablePoints, int lifetimePoints, int tier, Map<String, Integer> levels) {
        this.spendablePoints = spendablePoints;
        this.lifetimePoints = lifetimePoints;
        this.tier = tier;
        for (Map.Entry<String, Integer> entry : levels.entrySet()) {
            StellarIrisUpgradeDefinition definition = StellarIrisUpgradeRegistry.getUpgrade(entry.getKey());
            if (definition != null && entry.getValue() > 0) {
                networkLevels.put(
                    definition.getNetworkId(),
                    entry.getValue()
                        .intValue());
            }
        }
    }

    public static StellarIrisTeamSnapshot fromNetworkLevels(int spendablePoints, int lifetimePoints, int tier,
        Int2IntMap networkLevels) {
        StellarIrisTeamSnapshot snapshot = new StellarIrisTeamSnapshot();
        snapshot.spendablePoints = spendablePoints;
        snapshot.lifetimePoints = lifetimePoints;
        snapshot.tier = tier;
        snapshot.networkLevels.putAll(networkLevels);
        return snapshot;
    }

    public int getLevel(StellarIrisUpgradeDefinition definition) {
        return definition == null ? 0 : networkLevels.get(definition.getNetworkId());
    }

    public Int2IntMap getNetworkLevels() {
        return Int2IntMaps.unmodifiable(networkLevels);
    }

    public boolean sameAs(StellarIrisTeamSnapshot other) {
        return other != null && spendablePoints == other.spendablePoints
            && lifetimePoints == other.lifetimePoints
            && tier == other.tier
            && networkLevels.equals(other.networkLevels);
    }
}
