package com.science.gtnl.api.stellar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StellarIrisUpgradeRegistry {

    private static final Map<String, StellarIrisUpgradeBranch> BRANCHES = new LinkedHashMap<>();
    private static final Map<String, StellarIrisUpgradeTree> TREES = new LinkedHashMap<>();
    private static final Map<String, StellarIrisUpgradeDefinition> UPGRADES = new LinkedHashMap<>();
    private static final List<StellarIrisUpgradeDefinition> UPGRADES_BY_NETWORK_ID = new ArrayList<>();
    private static final Map<String, List<IStellarIrisUpgradeEffect>> EFFECTS = new LinkedHashMap<>();

    private StellarIrisUpgradeRegistry() {}

    public static void registerBranch(StellarIrisUpgradeBranch branch) {
        if (branch == null) {
            throw new IllegalArgumentException("Branch must not be null");
        }
        if (BRANCHES.containsKey(branch.getId())) {
            throw new IllegalArgumentException("Duplicate Stellar Iris branch: " + branch.getId());
        }
        if (TREES.containsKey(branch.getId())) {
            throw new IllegalArgumentException("Duplicate Stellar Iris tree: " + branch.getId());
        }
        BRANCHES.put(branch.getId(), branch);
        TREES.put(branch.getId(), branch);
    }

    public static void registerTree(StellarIrisUpgradeTree tree) {
        if (tree == null) {
            throw new IllegalArgumentException("Tree must not be null");
        }
        if (TREES.containsKey(tree.getId())) {
            throw new IllegalArgumentException("Duplicate Stellar Iris tree: " + tree.getId());
        }
        TREES.put(tree.getId(), tree);
    }

    public static void registerUpgrade(StellarIrisUpgradeDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("Upgrade definition must not be null");
        }
        StellarIrisUpgradeTree tree = TREES.get(definition.getTreeId());
        if (tree == null) {
            throw new IllegalArgumentException("Unknown Stellar Iris tree: " + definition.getTreeId());
        }
        if (definition.getBranchId() != null && !BRANCHES.containsKey(definition.getBranchId())) {
            throw new IllegalArgumentException("Unknown Stellar Iris branch: " + definition.getBranchId());
        }
        if (UPGRADES.containsKey(definition.getId())) {
            throw new IllegalArgumentException("Duplicate Stellar Iris upgrade: " + definition.getId());
        }
        definition.assignTree(tree);
        definition.assignNetworkId(UPGRADES_BY_NETWORK_ID.size());
        UPGRADES.put(definition.getId(), definition);
        UPGRADES_BY_NETWORK_ID.add(definition);
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

    public static StellarIrisUpgradeTree getTree(String id) {
        return TREES.get(id);
    }

    public static StellarIrisUpgradeDefinition getUpgrade(String id) {
        return UPGRADES.get(id);
    }

    public static StellarIrisUpgradeDefinition getUpgrade(int networkId) {
        return networkId < 0 || networkId >= UPGRADES_BY_NETWORK_ID.size() ? null
            : UPGRADES_BY_NETWORK_ID.get(networkId);
    }

    public static Collection<StellarIrisUpgradeBranch> getBranches() {
        return Collections.unmodifiableCollection(new ArrayList<>(BRANCHES.values()));
    }

    public static Collection<StellarIrisUpgradeTree> getTrees() {
        return Collections.unmodifiableCollection(new ArrayList<>(TREES.values()));
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
            if (branchId == null ? definition.getBranchId() == null : branchId.equals(definition.getBranchId())) {
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
