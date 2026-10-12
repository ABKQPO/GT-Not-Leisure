package com.science.gtnl.common.wireless;

import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/** A frozen payload and server-issued operation targets, independent of client sorting and pagination. */
final class WirelessCardSnapshot {

    static final int CHUNK_BYTES = 24 * 1024;

    private final NBTTagCompound payload;
    private final Map<Long, WirelessLinkView> targets;

    WirelessCardSnapshot(NBTTagCompound payload, Map<Long, WirelessLinkView> targets) {
        this.payload = (NBTTagCompound) payload.copy();
        this.targets = Map.copyOf(targets);
    }

    WirelessLinkView target(long id) {
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
