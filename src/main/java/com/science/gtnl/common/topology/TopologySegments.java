package com.science.gtnl.common.topology;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.api.topology.GridCoord;
import com.science.gtnl.api.topology.TopologyLinkKind;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.DimensionalCoord;
import appeng.tile.networking.TileWirelessBase;
import appeng.tile.qnb.TileQuantumBridge;

/**
 * Splits one logical grid into the segments that a wireless hop, a quantum ring or a tunnel mouth separates.
 * <p>
 * A wireless link does not create a second grid: it welds both sides into one. The physical shape is still a tree
 * though, and that shape is what a reader needs. The walk therefore crosses the grid's own connection graph and
 * treats every connection that is not an in-world adjacency as a cut. Both ends of a cut become relays: the near end
 * hangs off the segment it sits in, the far end hangs off the near end, and everything behind it hangs off the far
 * end.
 */
public class TopologySegments {

    /**
     * One end of a cut, in the order the walk met them.
     */
    public record Relay(int id, int parent, IGridNode node, TopologyLinkKind kind, boolean active) {

        /**
         * Anchor value for anything reached without crossing a hop, meaning it hangs off the network itself.
         */
        public static final int ROOT_ANCHOR = -1;
    }

    private final List<Relay> relays = new ArrayList<>();
    private final Map<IGridNode, Integer> anchorOf = new IdentityHashMap<>();
    private final Map<IGridNode, Integer> relayOf = new IdentityHashMap<>();
    private boolean truncated;
    private int visited;

    /**
     * @param budget maximum nodes the walk may visit before giving up and reporting a truncated result.
     */
    public TopologySegments(IGrid grid, IGridNode entry, int budget, int relayLimit) {
        IGridNode start = entry != null && GridCoord.of(entry) != null ? entry : entry(grid);
        if (start == null) return;
        Map<IGridNode, Integer> segment = new IdentityHashMap<>();
        ArrayDeque<IGridNode> pending = new ArrayDeque<>();
        segment.put(start, Relay.ROOT_ANCHOR);
        pending.add(start);

        while (!pending.isEmpty()) {
            if (visited++ >= budget) {
                truncated = true;
                return;
            }
            IGridNode node = pending.removeFirst();
            int anchor = segment.get(node);
            anchorOf.put(node, anchor);
            for (IGridConnection connection : node.getConnections()) {
                IGridNode other = connection.getOtherSide(node);
                if (other == null || segment.containsKey(other)) continue;
                if (!isCut(connection, node, other)) {
                    segment.put(other, anchor);
                    pending.add(other);
                    continue;
                }
                if (relays.size() + 2 > relayLimit) {
                    truncated = true;
                    continue;
                }
                TopologyLinkKind kind = kindOf(node, other);
                boolean live = node.isActive() && other.isActive();
                int near = relay(node, anchor, kind, live);
                int far = relay(other, near, kind, live);
                segment.put(other, far);
                pending.add(other);
            }
        }
    }

    public List<Relay> relays() {
        return relays;
    }

    /**
     * @return the relay a node hangs off, or {@link Relay#ROOT_ANCHOR} when it is reached without crossing a hop.
     */
    public int anchorOf(IGridNode node) {
        Integer anchor = anchorOf.get(node);
        return anchor == null ? Relay.ROOT_ANCHOR : anchor;
    }

    public boolean truncated() {
        return truncated;
    }

    /**
     * Nodes this walk actually touched, so a scan can spend one shared budget across every grid it visits.
     */
    public int visited() {
        return visited;
    }

    private int relay(IGridNode node, int parent, TopologyLinkKind kind, boolean active) {
        Integer existing = relayOf.get(node);
        if (existing != null) return existing;
        int id = relays.size();
        relays.add(new Relay(id, parent, node, kind, active));
        relayOf.put(node, id);
        return id;
    }

    /**
     * An in-world connection always joins the same block or two touching blocks. Anything else was created by a
     * machine that carries the grid somewhere, which is exactly what deserves its own node.
     */
    /**
     * Only a wireless link or a quantum ring earns its own pair of nodes. Both ends must be that kind of machine and
     * the connection must be the remote one it creates, which is what keeps ordinary cable and part wiring out: a
     * dense cable also reports no direction for some of its internal links, and those are not hops.
     */
    private static boolean isCut(IGridConnection connection, IGridNode from, IGridNode to) {
        if (kindOf(from) == TopologyLinkKind.ADJACENT || kindOf(to) == TopologyLinkKind.ADJACENT) return false;
        DimensionalCoord a = location(from);
        DimensionalCoord b = location(to);
        if (a == null || b == null) return !connection.hasDirection();
        if (a.getDimension() != b.getDimension()) return true;
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y) + Math.abs(a.z - b.z) > 1;
    }

    private static DimensionalCoord location(IGridNode node) {
        return node.getGridBlock() == null ? null
            : node.getGridBlock()
                .getLocation();
    }

    private static TopologyLinkKind kindOf(IGridNode from, IGridNode to) {
        TopologyLinkKind kind = kindOf(from);
        return kind == TopologyLinkKind.ADJACENT ? kindOf(to) : kind;
    }

    private static TopologyLinkKind kindOf(IGridNode node) {
        IGridHost machine = node.getMachine();
        if (machine instanceof TileWirelessBase) return TopologyLinkKind.WIRELESS;
        if (machine instanceof TileQuantumBridge) return TopologyLinkKind.QUANTUM;
        return TopologyLinkKind.ADJACENT;
    }

    private static IGridNode entry(IGrid grid) {
        IGridNode pivot = grid.getPivot();
        if (pivot != null && GridCoord.of(pivot) != null) return pivot;
        for (IGridNode node : grid.getNodes()) {
            if (GridCoord.of(node) != null) return node;
        }
        return null;
    }

    public static GridCoord position(IGridNode node) {
        GridCoord coord = GridCoord.of(node);
        if (coord != null) return coord;
        DimensionalCoord location = location(node);
        return location == null ? null : GridCoord.of(location, ForgeDirection.UNKNOWN);
    }
}
