package com.science.gtnl.common.wireless;

import java.util.Collection;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.gtnllib.world.IBlockStateListener;
import com.gtnllib.world.WorldListener;
import com.science.gtnl.common.wireless.PhysicalClusterTracker.Cluster;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;

import appeng.api.networking.IGridNode;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

public class WirelessTopologyWatch implements IBlockStateListener {

    private final Map<World, LongSet> watched = new Reference2ObjectOpenHashMap<>();
    private final Map<World, Long2ObjectMap<TileEntity>> tiles = new Reference2ObjectOpenHashMap<>();

    void update(Collection<Cluster<IGridNode, WirelessFrequency>> clusters, PhysicalMeTopology topology) {
        Map<World, LongSet> next = new Reference2ObjectOpenHashMap<>();
        Map<World, Long2ObjectMap<TileEntity>> nextTiles = new Reference2ObjectOpenHashMap<>();
        for (var cluster : clusters) {
            if (cluster.frequencies()
                .isEmpty()) continue;
            for (IGridNode node : cluster.nodes()) add(next, nextTiles, topology.nodeAddress(node));
            for (WirelessFrequency source : cluster.frequencies())
                add(next, nextTiles, WirelessFrequencies.controller(source));
        }
        for (World world : tiles.keySet()) {
            if (!nextTiles.containsKey(world)) WorldListener.INSTANCE.unregisterBlockStateListener(world, this);
        }
        for (var entry : nextTiles.entrySet()) {
            Long2ObjectMap<TileEntity> previous = tiles.get(entry.getKey());
            if (previous != null && previous.keySet()
                .equals(
                    entry.getValue()
                        .keySet()))
                continue;
            Iterable<BlockPos> positions = () -> entry.getValue()
                .keySet()
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
        tiles.clear();
        tiles.putAll(nextTiles);
    }

    private static void add(Map<World, LongSet> map, Map<World, Long2ObjectMap<TileEntity>> tiles, Address address) {
        if (address == null) return;
        var tile = address.tile();
        if (tile == null) return;
        World world = tile.getWorldObj();
        long position = CoordinatePacker.pack(address.x(), address.y(), address.z());
        tiles.computeIfAbsent(world, ignored -> new Long2ObjectOpenHashMap<>())
            .put(position, tile);
        LongSet positions = map.computeIfAbsent(world, ignored -> new LongOpenHashSet());
        positions.add(position);
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
        long packed = CoordinatePacker.pack(pos.x, pos.y, pos.z);
        boolean known = false;
        for (var entry : tiles.entrySet()) {
            TileEntity expected = entry.getValue()
                .get(packed);
            if (expected == null) continue;
            known = true;
            World world = entry.getKey();
            if (expected.isInvalid() || !world.blockExists(pos.x, pos.y, pos.z)
                || world.getTileEntity(pos.x, pos.y, pos.z) != expected) {
                WirelessClusterManager.invalidateTopology();
                return;
            }
        }
        if (!known) WirelessClusterManager.invalidateTopology();
    }

    void clear() {
        for (World world : tiles.keySet()) WorldListener.INSTANCE.unregisterBlockStateListener(world, this);
        watched.clear();
        tiles.clear();
    }
}
