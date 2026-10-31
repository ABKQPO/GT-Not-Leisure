package com.science.gtnl.common.machine.multiblock;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import thaumcraft.api.aspects.Aspect;

public class LargeEssentiaEnergyData {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_AIR = 1;
    private static final int TYPE_THERMAL = 2;
    private static final int TYPE_UNSTABLE = 3;
    private static final int TYPE_VICTUS = 4;
    private static final int TYPE_TAINTED = 5;
    private static final int TYPE_MECHANIC = 6;
    private static final int TYPE_SPIRIT = 7;
    private static final int TYPE_RADIATION = 8;
    private static final int TYPE_ELECTRIC = 9;

    private static final Object2IntOpenHashMap<String> TYPE_MAP = new Object2IntOpenHashMap<>();
    private static final Object2LongOpenHashMap<String> FUEL_MAP = new Object2LongOpenHashMap<>();
    private static final Object2DoubleOpenHashMap<String> CEO_MAP = new Object2DoubleOpenHashMap<>();

    static {
        TYPE_MAP.defaultReturnValue(TYPE_NORMAL);
        FUEL_MAP.defaultReturnValue(2048L);
        CEO_MAP.defaultReturnValue(1.0D);

        registerDefaults();
    }

    private static void registerDefaults() {
        register("aer", TYPE_AIR, 512L, 2.0D);
        register("terra", TYPE_NORMAL, 768L, 3.0D);
        register("ignis", TYPE_THERMAL, 1024L, 3.5D);
        register("aqua", TYPE_NORMAL, 640L, 2.5D);
        register("ordo", TYPE_MECHANIC, 896L, 2.0D);
        register("perditio", TYPE_UNSTABLE, 896L, 2.0D);

        register("victus", TYPE_VICTUS, 4096L, 12.0D);
        register("mortuus", TYPE_SPIRIT, 4096L, 6.0D);
        register("spiritus", TYPE_SPIRIT, 8192L, 8.0D);
        register("praecantatio", TYPE_ELECTRIC, 12288L, 4.0D);
        register("potentia", TYPE_ELECTRIC, 16384L, 6.0D);
        register("permutatio", TYPE_MECHANIC, 6144L, 5.0D);
        register("machina", TYPE_MECHANIC, 12288L, 10.0D);
        register("motus", TYPE_AIR, 3072L, 4.0D);
        register("tempestas", TYPE_AIR, 8192L, 10.0D);
        register("gelum", TYPE_THERMAL, 6144L, 5.0D);
        register("vacuos", TYPE_UNSTABLE, 12288L, 6.0D);
        register("venenum", TYPE_TAINTED, 4096L, 4.0D);
        register("vitium", TYPE_TAINTED, 32768L, 12.0D);
        register("radio", TYPE_RADIATION, 65536L, 16.0D);
        register("lux", TYPE_ELECTRIC, 6144L, 3.0D);
        register("tenebrae", TYPE_SPIRIT, 6144L, 3.0D);
        register("iter", TYPE_AIR, 6144L, 4.0D);
    }

    private static void register(String tag, int type, long fuel, double ceo) {
        TYPE_MAP.put(tag, type);
        FUEL_MAP.put(tag, fuel);
        CEO_MAP.put(tag, ceo);
    }

    public static int getAspectTypeIndex(Aspect aspect) {
        return aspect == null ? -1 : TYPE_MAP.getInt(aspect.getTag());
    }

    public static long getAspectFuelValue(Aspect aspect) {
        return aspect == null ? 0L : FUEL_MAP.getLong(aspect.getTag());
    }

    public static double getAspectCeo(Aspect aspect) {
        return aspect == null ? 0.0D : CEO_MAP.getDouble(aspect.getTag());
    }
}
