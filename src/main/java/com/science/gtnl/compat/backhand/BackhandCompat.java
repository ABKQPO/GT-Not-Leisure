package com.science.gtnl.compat.backhand;

import gregtech.api.enums.Mods;

public class BackhandCompat {

    public static final boolean LOADED = Mods.Backhand.isModLoaded();

    private BackhandCompat() {}

    public static int offhandHotbarSlots() {
        return LOADED ? 1 : 0;
    }
}
