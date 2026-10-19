package com.science.gtnl.client.celestweave;

import java.util.List;

import gregtech.api.enums.Mods;

public final class CelestweaveBackpackTabs {

    private CelestweaveBackpackTabs() {}

    public static void register() {
        if (Mods.TinkerConstruct.isModLoaded()) CelestweaveBackpackTicCompat.register();
    }

    public static void add(int left, int top, List<?> buttons) {
        if (Mods.TinkerConstruct.isModLoaded()) CelestweaveBackpackTicCompat.add(left, top, buttons);
    }
}
