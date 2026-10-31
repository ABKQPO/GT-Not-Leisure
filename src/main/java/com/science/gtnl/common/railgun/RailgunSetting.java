package com.science.gtnl.common.railgun;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import com.science.gtnl.config.ItemConfig;

public enum RailgunSetting {

    TERRAIN("terrain", false, stack -> ItemConfig.railgun.enableTerrain),
    PVP("pvp", false, stack -> ItemConfig.railgun.enablePlayerDamage),
    SPLASH("splash", true, null),
    CHAIN("chain", true, null),
    EHV_BEAM("ehvBeam", false, stack -> RailgunData.modules(stack)
        .count(RailgunModule.EHV_BEAM) > 0);

    private static final String TAG = "Settings";

    public final String key;
    public final boolean fallback;
    private final Predicate<ItemStack> available;

    RailgunSetting(String key, boolean fallback, Predicate<ItemStack> available) {
        this.key = key;
        this.fallback = fallback;
        this.available = available;
    }

    public boolean on(ItemStack stack) {
        NBTTagCompound root = RailgunData.root(stack);
        if (!root.hasKey(TAG, Constants.NBT.TAG_COMPOUND)) return fallback;
        NBTTagCompound tag = root.getCompoundTag(TAG);
        return tag.hasKey(key) ? tag.getBoolean(key) : fallback;
    }

    public boolean available(ItemStack stack) {
        return available == null || available.test(stack);
    }

    public boolean active(ItemStack stack) {
        return on(stack) && available(stack);
    }

    public void set(ItemStack stack, boolean value) {
        NBTTagCompound root = RailgunData.rootForWrite(stack);
        if (!root.hasKey(TAG, Constants.NBT.TAG_COMPOUND)) root.setTag(TAG, new NBTTagCompound());
        root.getCompoundTag(TAG)
            .setBoolean(key, value);
    }
}
