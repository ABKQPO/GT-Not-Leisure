package com.science.gtnl.api.stellar;

public interface IStellarIrisUpgradeEffect {

    default void onLevelIncreased(StellarIrisUpgradeEffectContext context, int previousLevel, int newLevel) {}
}
