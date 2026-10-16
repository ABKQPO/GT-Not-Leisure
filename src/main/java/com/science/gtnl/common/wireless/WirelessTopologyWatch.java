package com.science.gtnl.common.wireless;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.science.gtnl.api.IBlockStateListener;
import com.science.gtnl.common.wireless.PhysicalClusterTracker.Cluster;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.common.world.WorldListener;

import appeng.api.networking.IGridNode;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

public class WirelessTopologyWatch implements IBlockStateListener {

    private final Map<World, LongSet> watched = new IdentityHashMap<>();

    void update(Collection<Cluster<IGridNode, Address>> clusters, PhysicalMeTopology topology) {
        Map<World, LongSet> next = new IdentityHashMap<>();
        for (var cluster : clusters) {
            if (cluster.frequencies()
                .isEmpty()) continue;
            for (IGridNode node : cluster.nodes()) add(next, topology.nodeAddress(node));
            for (Address source : cluster.frequencies()) add(next, source);
        }
        for (World world : watched.keySet()) {
            if (!next.containsKey(world)) WorldListener.INSTANCE.unregisterBlockStateListener(world, this);
        }
        for (var entry : next.entrySet()) {
            if (entry.getValue()
                .equals(watched.get(entry.getKey()))) continue;
            Iterable<BlockPos> positions = () -> entry.getValue()
                .longStream()
                .mapToObj(
                    position -> new BlockPos(
                        CoordinatePacker.unpackX(position),
                        CoordinatePacker.unpackY(position),
                        CoordinatePacker.unpackZ(position)))
                .iterator();
            WorldListener.INSTANCE.registerBlockStateListener(entry.getKey(), this, positions);
        }
        watched.clear();
        watched.putAll(next);
    }

    private static void add(Map<World, LongSet> map, Address address) {
        if (address == null) return;
        var tile = address.tile();
        if (tile == null) return;
        LongSet positions = map.computeIfAbsent(tile.getWorldObj(), ignored -> new LongOpenHashSet());
        positions.add(CoordinatePacker.pack(address.x(), address.y(), address.z()));
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            positions.add(
                CoordinatePacker
                    .pack(address.x() + side.offsetX, address.y() + side.offsetY, address.z() + side.offsetZ));
        }
    }

    void nodeChanged(IGridNode node) {
        if (watched.isEmpty()) return;
        var block = node.getGridBlock();
        if (block == null) return;
        var position = block.getLocation();
        if (position == null) return;
        World world = position.getWorld();
        if (world == null || world.isRemote) return;
        LongSet positions = watched.get(world);
        if (positions != null && positions.contains(CoordinatePacker.pack(position.x, position.y, position.z))) {
            WirelessClusterManager.invalidateTopology();
        }
    }

    @Override
    public void onBlockChanged(BlockPos pos) {
        WirelessClusterManager.invalidateTopology();
    }

    void clear() {
        for (World world : watched.keySet()) WorldListener.INSTANCE.unregisterBlockStateListener(world, this);
        watched.clear();
    }
}
