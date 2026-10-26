package com.science.gtnl.compat.vmtweak;

import com.gtnllib.mod.ModList;
import com.science.gtnl.config.MainConfig;

// GTNL's void miner tweak yields to the VMTweak mod, which changes the same machines.
public class VoidMinerTweak {

    private VoidMinerTweak() {}

    public static boolean enabled() {
        return !ModList.VMTweak.isModLoaded() && MainConfig.machine.enableVoidMinerTweak;
    }
}
