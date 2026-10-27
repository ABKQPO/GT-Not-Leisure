package com.science.gtnl.common.greenhouse;

import com.science.gtnl.common.greenhouse.modes.GreenHouseIC2Mode;
import com.science.gtnl.common.greenhouse.modes.GreenHouseNormalMode;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseMode;

import gregtech.api.util.MultiblockTooltipBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class GreenHouseModes {

    private static final Object2ObjectOpenHashMap<String, GreenHouseMode> modes = new Object2ObjectOpenHashMap<>();

    public static final GreenHouseMode Normal = addMode(GreenHouseNormalMode.instance);
    public static final GreenHouseMode IC2 = addMode(GreenHouseIC2Mode.instance);

    // this is basically a fake enum, plz don't instantiate
    private static GreenHouseMode addMode(GreenHouseMode mode) {
        modes.put(mode.getName(), mode);
        return mode;
    }

    public static GreenHouseMode getModeFromName(String name) {
        return modes.get(name);
    }

    public static GreenHouseMode getNextMode(GreenHouseMode from) {
        int id = (from.getUIIndex() + 1) % modes.size();
        for (GreenHouseMode mode : modes.values()) {
            if (mode.getUIIndex() == id) return mode;
        }
        return Normal;
    }

    public static void addTooltipInfo(MultiblockTooltipBuilder tt) {
        // maybe make this use the mods list instead
        GreenHouseModes.Normal.addTooltipInfo(tt);
        GreenHouseModes.IC2.addTooltipInfo(tt);
    }
}
