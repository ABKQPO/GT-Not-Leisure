package com.science.gtnl.api.stellar;

import net.minecraft.util.EnumChatFormatting;

import lombok.Getter;

@Getter
public class StellarIrisUpgradeBranch extends StellarIrisUpgradeTree {

    private final String translationKey;

    public StellarIrisUpgradeBranch(String id, String translationKey, float treeAngle, boolean repeatable,
        int defaultColor, int tooltipColor) {
        super(id, treeAngle, repeatable, defaultColor, tooltipColor);
        if (translationKey == null || translationKey.isEmpty()) {
            throw new IllegalArgumentException("Branch translation key must not be empty");
        }
        this.translationKey = translationKey;
    }

    public StellarIrisUpgradeBranch(String id, String translationKey, float treeAngle, boolean repeatable,
        int defaultColor, EnumChatFormatting tooltipColor) {
        super(id, treeAngle, repeatable, defaultColor, tooltipColor);
        if (translationKey == null || translationKey.isEmpty()) {
            throw new IllegalArgumentException("Branch translation key must not be empty");
        }
        this.translationKey = translationKey;
    }

}
