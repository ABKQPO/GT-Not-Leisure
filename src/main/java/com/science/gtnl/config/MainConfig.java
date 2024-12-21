package com.science.gtnl.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

// spotless:off
public class MainConfig {

    public static long EUEveryBlazeCube = 1L * Integer.MAX_VALUE;// default 1024L * Integer.MAX_VALUE;
    public static long EUEveryDepletedExcitedNaquadahFuelRod = 2750000L * Integer.MAX_VALUE;
    public static double secondsOfArtificialStarProgressCycleTime = 6.4;
    public static boolean EnableRenderDefaultArtificialStar = true;
    public static long EUEveryStrangeAnnihilationFuelRod = 32768L * Integer.MAX_VALUE;
    public static boolean MultiBlockStructureEnable = true;

    private static Configuration config;

    public static void init(File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            loadConfig();
        }
    }

    public static void reloadConfig() {
        if (config != null) {
            config.load();
            loadConfig();
        }
    }

    public static void loadConfig() {

        MultiBlockStructureEnable = config
            .get("多方块结构修改", "多方块修改开启", MultiBlockStructureEnable, "开启后使用本MOD重置的多方块结构，原结构将会被覆盖")
            .getBoolean(MultiBlockStructureEnable);

        if (config.hasChanged()) {
            config.save();
        }
    }
}
