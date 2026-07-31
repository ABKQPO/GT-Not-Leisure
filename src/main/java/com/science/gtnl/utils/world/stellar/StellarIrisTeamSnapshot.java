package com.science.gtnl.utils.world.stellar;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.cleanroommc.modularui.utils.item.INBTSerializable;

import lombok.Getter;

public class StellarIrisTeamSnapshot implements INBTSerializable<NBTTagCompound> {

    private static final String SPENDABLE_POINTS_TAG = "spendablePoints";
    private static final String LIFETIME_POINTS_TAG = "lifetimePoints";
    private static final String TIER_TAG = "tier";
    private static final String LEVELS_TAG = "levels";
    private static final String LEVEL_ID_TAG = "id";
    private static final String LEVEL_VALUE_TAG = "level";

    @Getter
    private int spendablePoints;
    @Getter
    private int lifetimePoints;
    @Getter
    private int tier;
    private final Map<String, Integer> levels = new LinkedHashMap<>();

    public StellarIrisTeamSnapshot() {}

    public StellarIrisTeamSnapshot(int spendablePoints, int lifetimePoints, int tier, Map<String, Integer> levels) {
        this.spendablePoints = spendablePoints;
        this.lifetimePoints = lifetimePoints;
        this.tier = tier;
        this.levels.putAll(levels);
    }

    public int getLevel(String upgradeId) {
        return levels.getOrDefault(upgradeId, 0);
    }

    public Map<String, Integer> getLevels() {
        return Collections.unmodifiableMap(levels);
    }

    public boolean sameAs(StellarIrisTeamSnapshot other) {
        return other != null && spendablePoints == other.spendablePoints
            && lifetimePoints == other.lifetimePoints
            && tier == other.tier
            && levels.equals(other.levels);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(SPENDABLE_POINTS_TAG, spendablePoints);
        tag.setInteger(LIFETIME_POINTS_TAG, lifetimePoints);
        tag.setInteger(TIER_TAG, tier);
        NBTTagList levelTags = new NBTTagList();
        for (Map.Entry<String, Integer> entry : levels.entrySet()) {
            NBTTagCompound levelTag = new NBTTagCompound();
            levelTag.setString(LEVEL_ID_TAG, entry.getKey());
            levelTag.setInteger(LEVEL_VALUE_TAG, entry.getValue());
            levelTags.appendTag(levelTag);
        }
        tag.setTag(LEVELS_TAG, levelTags);
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        spendablePoints = Math.max(0, tag.getInteger(SPENDABLE_POINTS_TAG));
        lifetimePoints = Math.max(0, tag.getInteger(LIFETIME_POINTS_TAG));
        tier = Math.max(0, tag.getInteger(TIER_TAG));
        levels.clear();
        NBTTagList levelTags = tag.getTagList(LEVELS_TAG, 10);
        for (int index = 0; index < levelTags.tagCount(); index++) {
            NBTTagCompound levelTag = levelTags.getCompoundTagAt(index);
            String id = levelTag.getString(LEVEL_ID_TAG);
            int level = levelTag.getInteger(LEVEL_VALUE_TAG);
            if (!id.isEmpty() && level > 0) {
                levels.put(id, level);
            }
        }
    }
}
