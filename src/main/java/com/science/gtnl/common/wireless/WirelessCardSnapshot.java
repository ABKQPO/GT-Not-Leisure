package com.science.gtnl.common.wireless;

import java.util.List;
import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.science.gtnl.common.wireless.WirelessMachineGroups.Row;

/** A frozen payload plus server-only member identities. Callers receive copies of mutable wire data. */
final class WirelessCardSnapshot {

    private final NBTTagCompound payload;
    private final List<Row> rows;

    WirelessCardSnapshot(NBTTagCompound payload, List<Row> rows) {
        this.payload = (NBTTagCompound) payload.copy();
        this.rows = List.copyOf(rows);
    }

    List<Row> rows() {
        return rows;
    }

    boolean sameView(WirelessCardSnapshot previous) {
        return previous != null && payload.equals(previous.payload) && sameRows(rows, previous.rows);
    }

    NBTTagCompound packet(long session, long revision, NBTTagList highlight) {
        var data = (NBTTagCompound) payload.copy();
        data.setLong("session", session);
        data.setLong("revision", revision);
        if (highlight != null) data.setTag("highlight", highlight.copy());
        return data;
    }

    static boolean sameRows(List<Row> next, List<Row> previous) {
        if (next.size() != previous.size()) return false;
        for (int i = 0; i < next.size(); i++) {
            Row a = next.get(i), b = previous.get(i);
            if (a.machine() != b.machine() || !Objects.equals(a.type(), b.type())
                || a.category() != b.category()
                || (a.category() ? !sameRows(a.children(), b.children()) : !sameTargets(a.links(), b.links())))
                return false;
        }
        return true;
    }

    static boolean sameTargets(List<WirelessLinkView> next, List<WirelessLinkView> previous) {
        if (next.size() != previous.size()) return false;
        for (int i = 0; i < next.size(); i++) {
            var a = next.get(i);
            var b = previous.get(i);
            // Replacing a non-anchor node can leave every displayed metric unchanged.
            // Its membership must still invalidate clicks issued against the older cluster.
            if (a.node() != b.node() || !a.members()
                .equals(b.members())) return false;
        }
        return true;
    }

}
