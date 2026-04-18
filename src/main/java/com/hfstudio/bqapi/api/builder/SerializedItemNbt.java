package com.hfstudio.bqapi.api.builder;

import net.minecraft.nbt.NBTTagCompound;

public final class SerializedItemNbt {

    private SerializedItemNbt() {}

    public static NBTTagCompound of(String itemId, int count, int damage) {
        return of(itemId, count, damage, "");
    }

    public static NBTTagCompound of(String itemId, int count, int damage, String oreDict) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("id", itemId);
        nbt.setInteger("Count", count);
        nbt.setShort("Damage", (short) damage);
        nbt.setString("OreDict", oreDict == null ? "" : oreDict);
        return nbt;
    }
}
