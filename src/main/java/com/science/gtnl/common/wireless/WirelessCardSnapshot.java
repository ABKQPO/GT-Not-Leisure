package com.science.gtnl.common.wireless;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class WirelessCardSnapshot {

    public static final int CHUNK_BYTES = 24 * 1024;

    private final NBTTagCompound payload;
    private final Long2ObjectMap<IWirelessLinkView> targets;

    WirelessCardSnapshot(NBTTagCompound payload, Long2ObjectMap<IWirelessLinkView> targets) {
        this.payload = (NBTTagCompound) payload.copy();
        this.targets = Long2ObjectMaps.unmodifiable(new Long2ObjectOpenHashMap<>(targets));
    }

    IWirelessLinkView target(long id) {
        return targets.get(id);
    }

    boolean sameView(WirelessCardSnapshot previous) {
        return previous != null && payload.equals(previous.payload);
    }

    NBTTagCompound packet(long session, long revision, NBTTagList highlight) {
        var data = (NBTTagCompound) payload.copy();
        data.setLong("session", session);
        data.setLong("revision", revision);
        if (highlight != null) data.setTag("highlight", highlight.copy());
        return data;
    }
}
