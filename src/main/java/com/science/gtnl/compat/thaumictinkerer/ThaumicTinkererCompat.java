package com.science.gtnl.compat.thaumictinkerer;

import com.science.gtnl.common.greenhouse.GreenHouseModes;
import com.science.gtnl.common.greenhouse.buckets.GreenHouseInfusedSeedBucket;

import gregtech.api.enums.Mods;

public class ThaumicTinkererCompat {

    public static void addGreenhouseBuckets() {
        if (!Mods.ThaumicTinkerer.isModLoaded()) return;
        GreenHouseModes.Normal.addLowPriorityFactory(GreenHouseInfusedSeedBucket.factory);
    }
}
