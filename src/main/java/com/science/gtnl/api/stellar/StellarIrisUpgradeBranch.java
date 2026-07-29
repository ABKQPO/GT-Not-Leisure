package com.science.gtnl.api.stellar;

import lombok.Getter;

@Getter
public class StellarIrisUpgradeBranch {

    private final String id;
    private final String translationKey;
    private final float treeAngle;
    private final boolean repeatable;

    public StellarIrisUpgradeBranch(String id, String translationKey, float treeAngle, boolean repeatable) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Branch ID must not be empty");
        }
        if (translationKey == null || translationKey.isEmpty()) {
            throw new IllegalArgumentException("Branch translation key must not be empty");
        }
        this.id = id;
        this.translationKey = translationKey;
        this.treeAngle = treeAngle;
        this.repeatable = repeatable;
    }

}
