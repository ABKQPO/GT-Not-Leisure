package com.science.gtnl.common.celestweave;

import java.util.Locale;
import java.util.function.IntSupplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.config.MainConfig;

public enum CelestweaveModuleSetting {

    FLIGHT_SPEED(CelestweaveModule.FLIGHT, "Speed", Format.MULTIPLIER, 1,
        () -> MainConfig.item.celestweave.maxFlightLevel, 1),
    FLIGHT_VSPEED(CelestweaveModule.FLIGHT, "VSpeed", Format.MULTIPLIER, 1,
        () -> MainConfig.item.celestweave.maxFlightLevel, 1),
    FLIGHT_LEVITATE(CelestweaveModule.FLIGHT, "Levitate", false),
    FLIGHT_INERTIA(CelestweaveModule.FLIGHT, "Inertia", false),
    WALK_SPEED(CelestweaveModule.WALK_ASSIST, "Speed", Format.MULTIPLIER, 1,
        () -> MainConfig.item.celestweave.maxWalkLevel, 2),
    WALK_JUMP(CelestweaveModule.WALK_ASSIST, "Jump", Format.MULTIPLIER, 1,
        () -> MainConfig.item.celestweave.maxWalkLevel, 1),
    WALK_STEP(CelestweaveModule.WALK_ASSIST, "Step", true),
    WALK_OMNI(CelestweaveModule.WALK_ASSIST, "Omni", true),
    REACH_LEVEL(CelestweaveModule.REACH, "Level", Format.LEVEL, 0, () -> 2, 0),
    REFLECT_PROJECTILE(CelestweaveModule.REFLECT, "Projectile", true),
    REFLECT_IGNITE(CelestweaveModule.REFLECT, "Ignite", true),
    REFLECT_BLIND(CelestweaveModule.REFLECT, "Blind", true);

    public enum Format {
        TOGGLE,
        MULTIPLIER,
        LEVEL
    }

    public final CelestweaveModule module;
    public final String key;
    public final Format format;
    public final int min, fallback;
    private final IntSupplier limit;

    CelestweaveModuleSetting(CelestweaveModule module, String key, boolean fallback) {
        this(module, key, Format.TOGGLE, 0, () -> 1, fallback ? 1 : 0);
    }

    CelestweaveModuleSetting(CelestweaveModule module, String key, Format format, int min, IntSupplier limit,
        int fallback) {
        this.module = module;
        this.key = key;
        this.format = format;
        this.min = min;
        this.limit = limit;
        this.fallback = fallback;
    }

    public boolean bool() {
        return format == Format.TOGGLE;
    }

    public int max() {
        return limit.getAsInt();
    }

    public int get(ItemStack stack) {
        NBTTagCompound values = CelestweaveData.settings(stack, module);
        if (!values.hasKey(key, 99)) return fallback;
        return bool() ? values.getBoolean(key) ? 1 : 0 : Math.max(min, Math.min(max(), values.getInteger(key)));
    }

    public boolean on(ItemStack stack) {
        return get(stack) != 0;
    }

    void write(ItemStack stack, float value) {
        NBTTagCompound values = CelestweaveData.settingsForWrite(stack, module);
        if (bool()) values.setBoolean(key, value >= 0.5F);
        else values.setInteger(key, Math.max(min, Math.min(max(), Math.round(value))));
    }

    public String lang() {
        return "item.gtnl.celestweave.setting." + module.id + "." + key.toLowerCase(Locale.ROOT);
    }

    public String display(int value) {
        return format == Format.LEVEL ? String.valueOf(value + 1) : "×" + value;
    }
}
