package com.science.gtnl.compat.thaumicbases;

import com.science.gtnl.common.greenhouse.GreenHouseModes;
import com.science.gtnl.common.greenhouse.buckets.GreenHouseRainbowCactusBucket;

import gregtech.api.enums.Mods;

public class ThaumicBasesCompat {

    private ThaumicBasesCompat() {}

    public static void addGreenhouseBuckets() {
        if (!Mods.ThaumicBases.isModLoaded()) return;
        GreenHouseModes.Normal.addLowPriorityFactory(GreenHouseRainbowCactusBucket.factory);
    }
}
