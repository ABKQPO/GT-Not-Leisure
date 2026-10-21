package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.ChannelBudget.Position;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.mixins.late.appliedEnergistics.wireless.AccessorTileController;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.util.DimensionalCoord;

public class PhysicalMeTopology implements PhysicalClusterTracker.Topology<IGridNode> {

    private record NodeRef(Address address, boolean external) {

        IGridNode resolve() {
            if (!external) return address.node();
            TileEntity tile = address.tile();
            if (!(tile instanceof IPartHost host)) return null;
            IPart part = host.getPart(address.side());
            return part == null ? null : part.getExternalFacingNode();
        }

        Position position() {
            return new Position(address.dimension(), address.x(), address.y(), address.z());
        }
    }

    private final Map<IGridNode, NodeRef> references = new IdentityHashMap<>();
    private final Map<IGridNode, Boolean> live = new IdentityHashMap<>();

    void beginScan() {
        live.clear();
    }

    @Override
    public boolean isLive(IGridNode node) {
        return live.computeIfAbsent(node, this::resolveLive);
    }

    private boolean resolveLive(IGridNode node) {
        NodeRef reference = references.get(node);
        if (reference != null) return reference.resolve() == node;
        World nodeWorld = node.getWorld();
        DimensionalCoord location = node.getGridBlock()
            .getLocation();
        if (nodeWorld == null || nodeWorld.isRemote || location == null) return false;
        int dimension = nodeWorld.provider.dimensionId;
        World world = DimensionManager.getWorld(dimension);
        if (world != nodeWorld || !world.blockExists(location.x, location.y, location.z)) return false;
        TileEntity tile = world.getTileEntity(location.x, location.y, location.z);
        if (tile == null || tile.isInvalid()) return false;
        for (ForgeDirection side : ForgeDirection.values()) {
            Address address = new Address(dimension, location.x, location.y, location.z, side);
            if (tile instanceof IPartHost host) {
                IPart part = host.getPart(side);
                if (part == null) continue;
                if (part.getGridNode() == node) reference = new NodeRef(address, false);
                else if (part.getExternalFacingNode() == node) reference = new NodeRef(address, true);
            } else if (tile instanceof IGridHost host && host.getGridNode(side) == node) {
                reference = new NodeRef(address, false);
            }
            if (reference != null) {
                references.put(node, reference);
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterable<IGridNode> neighbours(IGridNode node) {
        List<IGridNode> result = new ArrayList<>();
        for (IGridConnection connection : node.getConnections()) {
            if (WirelessChannelManager.isOwned(connection)) continue;
            IGridNode other = connection.getOtherSide(node);
            if (!isLive(other)) continue;
            if (isPhysical(
                false,
                connection.hasDirection(),
                references.get(node)
                    .position(),
                references.get(other)
                    .position()))
                result.add(other);
        }
        return result;
    }

    @Override
    public boolean blocksWireless(IGridNode node) {
        return node.getMachine() instanceof AccessorTileController;
    }

    static boolean isPhysical(boolean ownedWireless, boolean directed, Position a, Position b) {
        if (ownedWireless || a == null || b == null || a.dimension() != b.dimension()) return false;
        long distance = Math.abs((long) a.x() - b.x()) + Math.abs((long) a.y() - b.y())
            + Math.abs((long) a.z() - b.z());
        // UNKNOWN connections within a host include AE cable-to-part connections. Remote UNKNOWN edges are bridges.
        return distance == 0 || directed && distance == 1;
    }

    static int entranceRank(IGridNode node) {
        if (node.hasFlag(GridFlags.DENSE_CAPACITY)) return 0;
        return node.hasFlag(GridFlags.REQUIRE_CHANNEL) ? 2 : 1;
    }

    static int entranceCapacity(IGridNode node) {
        return node.hasFlag(GridFlags.DENSE_CAPACITY) ? ChannelBudget.CHANNELS_PER_FACE : 8;
    }

    Address entranceAddress(IGridNode node) {
        NodeRef reference = references.get(node);
        return reference == null || reference.external() ? null : reference.address();
    }

    Address nodeAddress(IGridNode node) {
        NodeRef reference = references.get(node);
        return reference == null ? null : reference.address();
    }

    boolean external(IGridNode node) {
        NodeRef reference = references.get(node);
        return reference != null && reference.external();
    }

    boolean matches(IGridNode node, Predicate<Address> predicate) {
        NodeRef reference = references.get(node);
        return reference != null && predicate.test(reference.address());
    }

    void retain(Set<IGridNode> nodes) {
        references.keySet()
            .retainAll(nodes);
        live.clear();
    }

    void clear() {
        references.clear();
        live.clear();
    }
}
