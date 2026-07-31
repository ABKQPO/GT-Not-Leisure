package com.science.gtnl.utils.world.stellar;

import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.science.gtnl.api.stellar.IStellarIrisUpgradeEffect;
import com.science.gtnl.api.stellar.StellarIrisUpgradeDefinition;
import com.science.gtnl.api.stellar.StellarIrisUpgradeEffectContext;
import com.science.gtnl.api.stellar.StellarIrisUpgradeRegistry;

import gregtech.common.misc.spaceprojects.SpaceProjectManager;

public class StellarIrisUpgradeManager {

    private StellarIrisUpgradeManager() {}

    public static StellarIrisTeamSnapshot getSnapshot(EntityPlayer player) {
        StellarIrisTeamState state = getOrCreateState(player.worldObj, player.getUniqueID());
        return state.createSnapshot();
    }

    public static int getUpgradeLevel(EntityPlayer player, String upgradeId) {
        return getOrCreateState(player.worldObj, player.getUniqueID()).getLevel(upgradeId);
    }

    public static StellarIrisTeamSnapshot getSnapshot(World world, UUID memberId) {
        return getOrCreateState(world, memberId).createSnapshot();
    }

    public static void addPoints(EntityPlayer player, int amount) {
        addPoints(player.worldObj, player.getUniqueID(), amount);
    }

    public static void setSpendablePoints(EntityPlayer player, int points) {
        setSpendablePoints(player.worldObj, player.getUniqueID(), points);
    }

    public static void setTier(EntityPlayer player, int tier) {
        setTier(player.worldObj, player.getUniqueID(), tier);
    }

    public static boolean addPoints(World world, UUID memberId, int amount) {
        if (amount == 0) {
            return true;
        }
        StellarIrisTeamState state = getOrCreateState(world, memberId);
        long updatedPoints = (long) state.getSpendablePoints() + amount;
        if (updatedPoints < 0 || updatedPoints > Integer.MAX_VALUE) {
            return false;
        }
        state.setSpendablePoints((int) updatedPoints);
        if (amount > 0) {
            long updatedLifetime = (long) state.getLifetimePoints() + amount;
            state.setLifetimePoints((int) Math.min(Integer.MAX_VALUE, updatedLifetime));
        }
        StellarIrisWorldSavedData.get(world)
            .markDirty();
        return true;
    }

    public static void setSpendablePoints(World world, UUID memberId, int points) {
        StellarIrisTeamState state = getOrCreateState(world, memberId);
        state.setSpendablePoints(points);
        StellarIrisWorldSavedData.get(world)
            .markDirty();
    }

    public static void setTier(World world, UUID memberId, int tier) {
        StellarIrisTeamState state = getOrCreateState(world, memberId);
        state.setTier(tier);
        StellarIrisWorldSavedData.get(world)
            .markDirty();
    }

    public static boolean setUpgradeLevel(World world, UUID memberId, String upgradeId, int level) {
        StellarIrisUpgradeDefinition definition = StellarIrisUpgradeRegistry.getUpgrade(upgradeId);
        if (definition == null || level < 0 || level > definition.getMaxLevel()) {
            return false;
        }
        StellarIrisTeamState state = getOrCreateState(world, memberId);
        state.setLevel(upgradeId, level);
        StellarIrisWorldSavedData.get(world)
            .markDirty();
        return true;
    }

    public static boolean tryUnlock(EntityPlayer player, String upgradeId) {
        if (upgradeId == null || upgradeId.isEmpty()) {
            return false;
        }
        StellarIrisUpgradeDefinition definition = StellarIrisUpgradeRegistry.getUpgrade(upgradeId);
        if (definition == null) {
            return false;
        }
        World world = player.worldObj;
        UUID teamLeaderId = getTeamLeaderId(player.getUniqueID());
        StellarIrisWorldSavedData data = StellarIrisWorldSavedData.get(world);
        StellarIrisTeamState state = data.getOrCreateTeamState(teamLeaderId);
        int currentLevel = state.getLevel(upgradeId);
        if (!canUnlock(state, definition, currentLevel)) {
            return false;
        }
        int cost = definition.getCostForLevel(currentLevel + 1);
        if (state.getSpendablePoints() < cost) {
            return false;
        }
        state.setSpendablePoints(state.getSpendablePoints() - cost);
        state.setLevel(upgradeId, currentLevel + 1);
        data.markDirty();
        StellarIrisUpgradeEffectContext context = new StellarIrisUpgradeEffectContext(world, teamLeaderId, definition);
        for (IStellarIrisUpgradeEffect effect : StellarIrisUpgradeRegistry.getEffects(definition.getId())) {
            effect.onLevelIncreased(context, currentLevel, currentLevel + 1);
        }
        return true;
    }

    public static boolean canUnlock(StellarIrisTeamSnapshot snapshot, StellarIrisUpgradeDefinition definition) {
        return snapshot != null && definition != null
            && canUnlock(snapshot.getTier(), snapshot.getLevels(), definition, snapshot.getLevel(definition.getId()));
    }

    private static boolean canUnlock(StellarIrisTeamState state, StellarIrisUpgradeDefinition definition,
        int currentLevel) {
        return canUnlock(state.getTier(), state.getLevels(), definition, currentLevel);
    }

    private static boolean canUnlock(int tier, Map<String, Integer> levels, StellarIrisUpgradeDefinition definition,
        int currentLevel) {
        if (currentLevel >= definition.getMaxLevel()) {
            return false;
        }
        if (definition.isRepeatable()) {
            return true;
        }
        if (tier < Math.max(0, definition.getRow() - 1)) {
            return false;
        }
        if (definition.getPrerequisiteIds()
            .isEmpty()) {
            return true;
        }
        if (definition.getRow() <= 2) {
            for (String prerequisiteId : definition.getPrerequisiteIds()) {
                if (levels.getOrDefault(prerequisiteId, 0) > 0) {
                    return true;
                }
            }
            return false;
        }
        for (String prerequisiteId : definition.getPrerequisiteIds()) {
            if (levels.getOrDefault(prerequisiteId, 0) <= 0) {
                return false;
            }
        }
        return true;
    }

    private static StellarIrisTeamState getOrCreateState(World world, UUID playerId) {
        UUID teamLeaderId = getTeamLeaderId(playerId);
        return StellarIrisWorldSavedData.get(world)
            .getOrCreateTeamState(teamLeaderId);
    }

    private static UUID getTeamLeaderId(UUID playerId) {
        SpaceProjectManager.checkOrCreateTeam(playerId);
        return SpaceProjectManager.getLeader(playerId);
    }
}
