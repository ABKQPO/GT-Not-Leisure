package com.science.gtnl.common.wireless;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.item.ItemStackNBT;
import com.science.gtnl.common.wireless.WirelessChannelPrototype.Address;

/** Item data only. Stores the source controller address and bound owner. */
public record WirelessCardBinding(Address source, UUID owner, String ownerName) {

    private static final String KEY = "GTNLWirelessCard";
    private static final int VERSION = 1;
    private static final String AUTO_KEY = "GTNLWirelessAutoConnect";

    public static boolean automatic(NBTTagCompound itemTag) {
        return itemTag != null && itemTag.getBoolean(AUTO_KEY);
    }

    public static boolean toggleAutomatic(ItemStack card) {
        return ItemStackNBT.invertBoolean(card, AUTO_KEY);
    }

    public static WirelessCardBinding read(NBTTagCompound itemTag) {
        if (itemTag == null || !itemTag.hasKey(KEY, 10)) return null;
        NBTTagCompound data = itemTag.getCompoundTag(KEY);
        if (!data.hasKey("version", 3) || data.getInteger("version") != VERSION
            || !data.hasKey("dimension", 3)
            || !data.hasKey("x", 3)
            || !data.hasKey("y", 3)
            || !data.hasKey("z", 3)
            || !data.hasKey("owner", 8)) return null;
        try {
            return new WirelessCardBinding(
                new Address(
                    data.getInteger("dimension"),
                    data.getInteger("x"),
                    data.getInteger("y"),
                    data.getInteger("z"),
                    ForgeDirection.UNKNOWN),
                UUID.fromString(data.getString("owner")),
                data.getString("ownerName"));
        } catch (IllegalArgumentException invalidOwner) {
            return null;
        }
    }

    public void write(NBTTagCompound itemTag) {
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("version", VERSION);
        data.setInteger("dimension", source.dimension());
        data.setInteger("x", source.x());
        data.setInteger("y", source.y());
        data.setInteger("z", source.z());
        data.setString("owner", owner.toString());
        data.setString("ownerName", ownerName);
        itemTag.setTag(KEY, data);
    }

    public static void clear(NBTTagCompound itemTag) {
        if (itemTag != null) itemTag.removeTag(KEY);
    }

    public boolean belongsTo(UUID player) {
        return owner.equals(player);
    }
}
