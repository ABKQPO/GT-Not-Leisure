package com.science.gtnl.compat.lwjgl3ify;

import appeng.util.Platform;
import cpw.mods.fml.common.Loader;

public final class MouseWheelCompat {

    private MouseWheelCompat() {}

    public static int normalizeTicks(int amount) {
        return Loader.isModLoaded("lwjgl3ify") ? amount : (int) Platform.ceilDiv(amount, 120);
    }
}
