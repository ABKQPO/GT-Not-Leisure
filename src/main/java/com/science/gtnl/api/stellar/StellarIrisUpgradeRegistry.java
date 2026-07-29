package com.science.gtnl.api.stellar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StellarIrisUpgradeRegistry {

    private static final Map<String, StellarIrisUpgradeBranch> BRANCHES = new LinkedHashMap<>();
    private static final Map<String, StellarIrisUpgradeDefinition> UPGRADES = new LinkedHashMap<>();
    private static final Map<String, List<IStellarIrisUpgradeEffect>> EFFECTS = new LinkedHashMap<>();

    private StellarIrisUpgradeRegistry() {}

    public static void registerBranch(StellarIrisUpgradeBranch branch) {
        if (branch == null) {
            throw new IllegalArgumentException("Branch must not be null");
        }
        if (BRANCHES.containsKey(branch.getId())) {
            throw new IllegalArgumentException("Duplicate Stellar Iris branch: " + branch.getId());
        }
        BRANCHES.put(branch.getId(), branch);
    }

    public static void registerUpgrade(StellarIrisUpgradeDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("Upgrade definition must not be null");
        }
        if (!BRANCHES.containsKey(definition.getBranchId())) {
            throw new IllegalArgumentException("Unknown Stellar Iris branch: " + definition.getBranchId());
        }
        if (UPGRADES.containsKey(definition.getId())) {
            throw new IllegalArgumentException("Duplicate Stellar Iris upgrade: " + definition.getId());
        }
        UPGRADES.put(definition.getId(), definition);
        EFFECTS.put(definition.getId(), new ArrayList<>(definition.getEffects()));
    }

    public static void registerEffect(String upgradeId, IStellarIrisUpgradeEffect effect) {
        if (effect == null) {
            throw new IllegalArgumentException("Upgrade effect must not be null");
        }
        List<IStellarIrisUpgradeEffect> effects = EFFECTS.get(upgradeId);
        if (effects == null) {
            throw new IllegalArgumentException("Unknown Stellar Iris upgrade: " + upgradeId);
        }
        effects.add(effect);
    }

    public static StellarIrisUpgradeBranch getBranch(String id) {
        return BRANCHES.get(id);
    }

    public static StellarIrisUpgradeDefinition getUpgrade(String id) {
        return UPGRADES.get(id);
    }

    public static Collection<StellarIrisUpgradeBranch> getBranches() {
        return Collections.unmodifiableCollection(new ArrayList<>(BRANCHES.values()));
    }

    public static Collection<StellarIrisUpgradeDefinition> getUpgrades() {
        return Collections.unmodifiableCollection(UPGRADES.values());
    }

    public static int getUpgradeCount() {
        return UPGRADES.size();
    }

    public static Collection<StellarIrisUpgradeDefinition> getUpgradesByBranch(String branchId) {
        List<StellarIrisUpgradeDefinition> upgrades = new ArrayList<>();
        for (StellarIrisUpgradeDefinition definition : UPGRADES.values()) {
            if (definition.getBranchId()
                .equals(branchId)) {
                upgrades.add(definition);
            }
        }
        return Collections.unmodifiableList(upgrades);
    }

    public static Collection<IStellarIrisUpgradeEffect> getEffects(String upgradeId) {
        List<IStellarIrisUpgradeEffect> effects = EFFECTS.get(upgradeId);
        if (effects == null) {
            return Collections.emptyList();
        }
        return List.copyOf(effects);
    }
}
