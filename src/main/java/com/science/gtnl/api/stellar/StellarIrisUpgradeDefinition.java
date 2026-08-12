package com.science.gtnl.api.stellar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.api.drawable.IDrawable;

import lombok.Getter;

public class StellarIrisUpgradeDefinition {

    @Getter
    private final String id;
    @Getter
    private final String branchId;
    @Getter
    private final int row;
    @Getter
    private final int baseCost;
    @Getter
    private final int maxLevel;
    @Getter
    private final String translationKey;
    @Getter
    private final String descriptionKey;
    @Getter
    private final StellarIrisNodeDisplay nodeDisplay;
    @Getter
    private final Set<String> prerequisiteIds;
    @Getter
    private final List<IStellarIrisUpgradeEffect> effects;

    public StellarIrisUpgradeDefinition(Builder builder) {
        if (builder.id == null || builder.id.isEmpty()) {
            throw new IllegalArgumentException("Upgrade ID must not be empty");
        }
        if (builder.branchId == null || builder.branchId.isEmpty()) {
            throw new IllegalArgumentException("Upgrade branch ID must not be empty");
        }
        if (builder.row < 0 || builder.baseCost < 0 || builder.maxLevel < 1) {
            throw new IllegalArgumentException("Upgrade values are outside their valid range");
        }
        this.id = builder.id;
        this.branchId = builder.branchId;
        this.row = builder.row;
        this.baseCost = builder.baseCost;
        this.maxLevel = builder.maxLevel;
        this.translationKey = builder.translationKey == null ? "gtnl.stellar_iris.upgrade." + id
            : builder.translationKey;
        this.descriptionKey = builder.descriptionKey == null ? translationKey + ".desc" : builder.descriptionKey;
        this.nodeDisplay = builder.nodeDisplay;
        this.prerequisiteIds = Collections.unmodifiableSet(new LinkedHashSet<>(builder.prerequisiteIds));
        this.effects = List.copyOf(builder.effects);
    }

    public boolean isRepeatable() {
        StellarIrisUpgradeBranch branch = StellarIrisUpgradeRegistry.getBranch(branchId);
        return branch != null && branch.isRepeatable();
    }

    public int getCostForLevel(int level) {
        if (!isRepeatable() || level <= 1) {
            return baseCost;
        }
        return (int) Math.ceil(baseCost * Math.pow(1.5D, level - 1));
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {

        private final String id;
        private String branchId;
        private int row;
        private int baseCost;
        private int maxLevel = 1;
        private String translationKey;
        private String descriptionKey;
        private StellarIrisNodeDisplay nodeDisplay;
        private final Set<String> prerequisiteIds = new LinkedHashSet<>();
        private final List<IStellarIrisUpgradeEffect> effects = new ArrayList<>();

        public Builder(String id) {
            this.id = id;
        }

        public Builder branch(String branchId) {
            this.branchId = branchId;
            return this;
        }

        public Builder row(int row) {
            this.row = row;
            return this;
        }

        public Builder baseCost(int baseCost) {
            this.baseCost = baseCost;
            return this;
        }

        public Builder maxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public Builder translationKey(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        public Builder descriptionKey(String descriptionKey) {
            this.descriptionKey = descriptionKey;
            return this;
        }

        public Builder nodeDisplay(String text) {
            nodeDisplay = StellarIrisNodeDisplay.text(text);
            return this;
        }

        public Builder nodeDisplay(ItemStack itemStack) {
            nodeDisplay = StellarIrisNodeDisplay.item(itemStack);
            return this;
        }

        public Builder nodeDisplay(FluidStack fluidStack) {
            nodeDisplay = StellarIrisNodeDisplay.fluid(fluidStack);
            return this;
        }

        public Builder nodeDisplay(IDrawable icon) {
            nodeDisplay = StellarIrisNodeDisplay.icon(icon);
            return this;
        }

        public Builder prerequisites(Collection<String> prerequisiteIds) {
            this.prerequisiteIds.clear();
            if (prerequisiteIds != null) {
                this.prerequisiteIds.addAll(prerequisiteIds);
            }
            return this;
        }

        public Builder prerequisite(String prerequisiteId) {
            if (prerequisiteId != null && !prerequisiteId.isEmpty()) {
                prerequisiteIds.add(prerequisiteId);
            }
            return this;
        }

        public Builder effect(IStellarIrisUpgradeEffect effect) {
            if (effect != null) {
                effects.add(effect);
            }
            return this;
        }

        public StellarIrisUpgradeDefinition build() {
            return new StellarIrisUpgradeDefinition(this);
        }
    }
}
