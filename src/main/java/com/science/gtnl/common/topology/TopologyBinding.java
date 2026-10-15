package com.science.gtnl.common.topology;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.api.topology.GridCoord;

public record TopologyBinding(GridCoord source, UUID owner, String ownerName) {

    private static final String KEY = "gtnlTopology";

    public static TopologyBinding read(NBTTagCompound tag) {
        if (tag == null || !tag.hasKey(KEY)) return null;
        NBTTagCompound data = tag.getCompoundTag(KEY);
        if (!data.hasKey("owner")) return null;
        UUID owner;
        try {
            owner = UUID.fromString(data.getString("owner"));
        } catch (IllegalArgumentException malformed) {
            return null;
        }
        return new TopologyBinding(
            new GridCoord(
                data.getInteger("dim"),
                data.getInteger("x"),
                data.getInteger("y"),
                data.getInteger("z"),
                ForgeDirection.getOrientation(data.getInteger("side"))),
            owner,
            data.getString("ownerName"));
    }

    public void write(NBTTagCompound tag) {
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("dim", source.dimension());
        data.setInteger("x", source.x());
        data.setInteger("y", source.y());
        data.setInteger("z", source.z());
        data.setInteger(
            "side",
            source.side()
                .ordinal());
        data.setString("owner", owner.toString());
        data.setString("ownerName", ownerName);
        tag.setTag(KEY, data);
    }

    public static void clear(NBTTagCompound tag) {
        if (tag != null) tag.removeTag(KEY);
    }

    public boolean belongsTo(UUID player) {
        return owner.equals(player);
    }
}
