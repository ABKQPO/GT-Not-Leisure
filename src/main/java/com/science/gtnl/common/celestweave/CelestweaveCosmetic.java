package com.science.gtnl.common.celestweave;

import java.util.Arrays;

import net.minecraft.nbt.NBTTagCompound;

public class CelestweaveCosmetic {

    public static final int EVENT_HIT = 1, EVENT_DASH = 2, EVENT_LAND = 4, EVENT_REFLECT = 8, EVENT_WING = 16,
        EVENT_ANTENNA = 32, EVENT_LOW = 64, EVENT_ALL = 127;
    public static final int DEFAULT_COLOR = 0xFF3D9A;
    public static final CelestweaveCosmetic DEFAULT = new CelestweaveCosmetic(defaults());

    private final float[] values;

    private CelestweaveCosmetic(float[] values) {
        this.values = values;
    }

    private static float[] defaults() {
        CelestweaveCosmeticOption[] options = CelestweaveCosmeticOption.values();
        float[] values = new float[options.length];
        for (CelestweaveCosmeticOption option : options) values[option.ordinal()] = option.fallback;
        return values;
    }

    public static CelestweaveCosmetic read(NBTTagCompound tag) {
        if (tag == null || tag.hasNoTags()) return DEFAULT;
        float[] values = new float[CelestweaveCosmeticOption.values().length];
        for (CelestweaveCosmeticOption option : CelestweaveCosmeticOption.values())
            values[option.ordinal()] = option.read(tag);
        return new CelestweaveCosmetic(values);
    }

    public NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();
        for (CelestweaveCosmeticOption option : CelestweaveCosmeticOption.values())
            option.write(tag, values[option.ordinal()]);
        return tag;
    }

    public float get(CelestweaveCosmeticOption option) {
        return values[option.ordinal()];
    }

    public int getInt(CelestweaveCosmeticOption option) {
        return (int) values[option.ordinal()];
    }

    public CelestweaveCosmetic with(CelestweaveCosmeticOption option, float value) {
        float[] next = values.clone();
        next[option.ordinal()] = option.clamp(value);
        return new CelestweaveCosmetic(next);
    }

    public CelestweaveStripStyle stripStyle() {
        return CelestweaveStripStyle.values()[getInt(CelestweaveCosmeticOption.STRIP_STYLE)];
    }

    public boolean event(int flag) {
        return (getInt(CelestweaveCosmeticOption.ARC_EVENTS) & flag) != 0;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof CelestweaveCosmetic cosmetic && Arrays.equals(values, cosmetic.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
