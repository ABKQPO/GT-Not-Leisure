package com.science.gtnl.utils.structure;

import java.util.List;

import com.science.gtnl.api.IMultiblockTooltipRules.TieredPart;
import com.science.gtnl.api.IMultiblockTooltipRules.TooltipStats;

public record InfoSummary(String machineType, TooltipStats stats, List<TierBonus> tierBonuses, List<String> lines,
    boolean supportAny, boolean supportMultiAmp, String materialTierNotice) {

    public enum BonusStat {
        PARALLEL,
        SPEED,
        DURATION,
        ENERGY
    }

    public record TierBonus(TieredPart part, BonusStat stat, boolean multiplicative, double value) {}
}
