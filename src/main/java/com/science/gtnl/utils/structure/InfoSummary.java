package com.science.gtnl.utils.structure;

import java.util.List;
import java.util.OptionalInt;

import com.science.gtnl.api.IMultiblockTooltipRules.HeatRules;
import com.science.gtnl.api.IMultiblockTooltipRules.TieredPart;
import com.science.gtnl.api.IMultiblockTooltipRules.TooltipStats;

public record InfoSummary(String machineType, TooltipStats stats, List<TierBonus> tierBonuses, List<String> lines,
    List<String> features, HeatRules heat, List<TierThreshold> perfectOverclockFrom, OptionalInt tierSkips,
    int pollution, List<List<String>> groups, boolean supportAny, boolean supportMultiAmp) {

    public enum BonusStat {
        PARALLEL,
        SPEED,
        DURATION,
        ENERGY
    }

    public record TierBonus(TieredPart part, BonusStat stat, boolean multiplicative, double value, boolean perTier) {}

    public record TierThreshold(TieredPart part, int tier, String tierName) {}
}
