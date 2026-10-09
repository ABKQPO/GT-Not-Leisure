package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessChannelPrototype.sameSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;

import com.science.gtnl.common.wireless.WirelessChannelPrototype.Address;
import com.science.gtnl.common.wireless.WirelessLinkData.Claim;
import com.science.gtnl.common.wireless.WirelessLinkData.Slot;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;

/**
 * Claims live in tile-owned NBT, which is saved even after an AE proxy has invalidated its grid node.
 * No coordinate registry outlives a removed tile. Only loaded tiles are inspected; wireless links never load chunks.
 */
public final class WirelessLinkPersistence {

    private static final Set<TileEntity> TILES = Collections.newSetFromMap(new IdentityHashMap<>());
    private static final Map<IGridNode, Endpoint> IMPORTED = new IdentityHashMap<>();
    private static boolean initialized;

    private record Endpoint(TileEntity tile, Address address, Slot slot, String kind) {

        boolean loaded() {
            return !tile.isInvalid() && address.tile() == tile;
        }

        IGridNode node() {
            if (tile instanceof IPartHost host) {
                IPart part = host.getPart(slot.side());
                return part == null ? null : slot.external() ? part.getExternalFacingNode() : part.getGridNode();
            }
            return !slot.external() && tile instanceof IGridHost host ? host.getGridNode(slot.side()) : null;
        }
    }

    private WirelessLinkPersistence() {}

    public static void discover(TileEntity tile) {
        if (((WirelessTileData) tile).gtnl$hasWirelessData()) TILES.add(tile);
    }

    public static void discover(Chunk chunk) {
        // World.loadedTileEntityList contains only ticking tiles in Forge 1.7.10; most AE cable hosts are absent.
        for (Object object : chunk.chunkTileEntityMap.values()) {
            if (object instanceof TileEntity tile) discover(tile);
        }
    }

    static void discoverLoaded(Iterable<Chunk> chunks) {
        for (Chunk chunk : chunks) discover(chunk);
    }

    static Collection<IGridNode> seeds() {
        if (!initialized) {
            initialized = true;
            for (World world : DimensionManager.getWorlds()) {
                if (world.getChunkProvider() instanceof ChunkProviderServer provider) {
                    // Inspect only the provider's existing chunks; never call provideChunk or loadChunk here.
                    discoverLoaded(provider.loadedChunks);
                }
            }
        }
        // A removed/replaced part in a surviving multipart host must not inherit its predecessor's slot claim.
        var previous = IMPORTED.entrySet()
            .iterator();
        while (previous.hasNext()) {
            var entry = previous.next();
            Endpoint endpoint = entry.getValue();
            if (!endpoint.loaded()) {
                previous.remove();
            } else if (endpoint.node() != entry.getKey()) {
                Map<Slot, Claim> saved = WirelessLinkData.read(WirelessTileData.of(endpoint.tile()));
                saved.remove(endpoint.slot());
                save(endpoint.tile(), saved);
                previous.remove();
            }
        }
        List<IGridNode> seeds = new ArrayList<>();
        var tiles = TILES.iterator();
        while (tiles.hasNext()) {
            TileEntity tile = tiles.next();
            if (!loaded(tile)) {
                tiles.remove();
                continue;
            }
            Map<Slot, Claim> claims = WirelessLinkData.read(WirelessTileData.of(tile));
            boolean changed = false;
            var entries = claims.entrySet()
                .iterator();
            while (entries.hasNext()) {
                var entry = entries.next();
                String kind = kind(tile, entry.getKey());
                if (!entry.getValue()
                    .kind()
                    .equals(kind)) {
                    entries.remove();
                    changed = true;
                    continue;
                }
                Endpoint endpoint = endpoint(tile, entry.getKey(), kind);
                IGridNode node = endpoint.node();
                if (node != null) seeds.add(node); // Keep a not-yet-ready AE part for a later server tick.
            }
            if (changed) save(tile, claims);
            if (claims.isEmpty()) tiles.remove();
        }
        return seeds;
    }

    static Set<Address> restore(IGridNode node, PhysicalMeTopology topology) {
        if (IMPORTED.containsKey(node)) return Set.of();
        Endpoint endpoint = endpoint(node, topology);
        if (endpoint == null) return Set.of();
        Claim saved = WirelessLinkData.read(WirelessTileData.of(endpoint.tile()))
            .get(endpoint.slot());
        return saved != null && saved.kind()
            .equals(endpoint.kind()) ? saved.frequencies() : Set.of();
    }

    /** Called only after a complete topology scan, so a scan limit never commits a partial provenance snapshot. */
    static void synchronize(PhysicalClusterTracker<IGridNode, Address> tracker, PhysicalMeTopology topology) {
        Map<TileEntity, Map<Slot, Claim>> updates = new IdentityHashMap<>();
        Set<IGridNode> retained = Collections.newSetFromMap(new IdentityHashMap<>());
        for (var cluster : tracker.clusters()) {
            Set<Address> paused = new java.util.HashSet<>();
            Map<Address, java.util.SortedSet<String>> names = names(cluster.nodes(), topology);
            for (IGridNode node : cluster.nodes()) {
                Claim saved = claim(node, topology);
                if (saved != null) paused.addAll(saved.paused());
            }
            for (IGridNode node : cluster.nodes()) {
                Endpoint endpoint = endpoint(node, topology);
                if (endpoint == null) continue;
                retained.add(node);
                IMPORTED.put(node, endpoint);
                Map<Slot, Claim> claims = updates
                    .computeIfAbsent(endpoint.tile(), tile -> WirelessLinkData.read(WirelessTileData.of(tile)));
                Set<Address> frequencies = tracker.claimsOf(node);
                if (frequencies.isEmpty()) claims.remove(endpoint.slot());
                else {
                    Claim old = claims.get(endpoint.slot());
                    Map<Address, String> labels = new java.util.HashMap<>();
                    Set<Address> sourcePauses = new java.util.HashSet<>();
                    for (Address source : frequencies) {
                        if (paused.stream()
                            .anyMatch(label -> sameSource(label, source))) sourcePauses.add(source);
                        java.util.SortedSet<String> choices = new java.util.TreeSet<>();
                        names.forEach((label, values) -> { if (sameSource(label, source)) choices.addAll(values); });
                        if (choices.isEmpty()) continue;
                        // Keep distinct names on their original nodes after a merge; split can recover them.
                        String previous = old == null ? null
                            : old.names()
                                .get(source);
                        labels.put(source, previous == null ? choices.first() : previous);
                    }
                    // Entrance locations belong to the original node, never to every member of the cluster.
                    claims.put(
                        endpoint.slot(),
                        new Claim(
                            endpoint.kind(),
                            frequencies,
                            sourcePauses,
                            labels,
                            old == null ? Set.of() : old.entrances()));
                }
            }
        }
        IMPORTED.keySet()
            .retainAll(retained);
        updates.forEach((tile, claims) -> {
            save(tile, claims);
            if (claims.isEmpty()) TILES.remove(tile);
            else TILES.add(tile);
        });
    }

    private static Claim claim(IGridNode node, PhysicalMeTopology topology) {
        Endpoint endpoint = endpoint(node, topology);
        if (endpoint == null) return null;
        Claim saved = WirelessLinkData.read(WirelessTileData.of(endpoint.tile()))
            .get(endpoint.slot());
        return saved != null && saved.kind()
            .equals(endpoint.kind()) ? saved : null;
    }

    static Set<Address> pausedSources(Collection<IGridNode> nodes, PhysicalMeTopology topology) {
        Set<Address> paused = new java.util.HashSet<>();
        for (IGridNode node : nodes) {
            Claim saved = claim(node, topology);
            if (saved != null) saved.paused()
                .stream()
                .map(WirelessChannelPrototype::controllerSource)
                .forEach(paused::add);
        }
        return paused;
    }

    static Set<IGridNode> savedEntrances(Collection<IGridNode> nodes, Address source, PhysicalMeTopology topology) {
        Set<IGridNode> result = Collections.newSetFromMap(new IdentityHashMap<>());
        for (IGridNode node : nodes) {
            Claim saved = claim(node, topology);
            if (saved != null && saved.entrances()
                .stream()
                .anyMatch(label -> sameSource(label, source))) result.add(node);
        }
        return result;
    }

    /** Only call after native pathing has confirmed the entry, never when creating a speculative connection. */
    static void rememberEntrance(IGridNode node, Address source, PhysicalMeTopology topology) {
        Endpoint endpoint = endpoint(node, topology);
        if (endpoint == null || endpoint.node() != node
            || endpoint.slot()
                .external())
            return;
        Map<Slot, Claim> claims = WirelessLinkData.read(WirelessTileData.of(endpoint.tile()));
        Claim saved = claims.get(endpoint.slot());
        if (saved == null || !saved.kind()
            .equals(endpoint.kind())
            || saved.frequencies()
                .stream()
                .noneMatch(label -> sameSource(label, source)))
            return;
        Set<Address> entrances = new java.util.HashSet<>(saved.entrances());
        saved.frequencies()
            .stream()
            .filter(label -> sameSource(label, source))
            .forEach(entrances::add);
        if (entrances.equals(saved.entrances())) return;
        claims.put(
            endpoint.slot(),
            new Claim(saved.kind(), saved.frequencies(), saved.paused(), saved.names(), entrances));
        save(endpoint.tile(), claims);
    }

    private static Map<Address, java.util.SortedSet<String>> names(Collection<IGridNode> nodes,
        PhysicalMeTopology topology) {
        Map<Address, java.util.SortedSet<String>> names = new java.util.HashMap<>();
        for (IGridNode node : nodes) {
            Claim saved = claim(node, topology);
            if (saved != null) saved.names()
                .forEach(
                    (source, name) -> names.computeIfAbsent(source, ignored -> new java.util.TreeSet<>())
                        .add(name));
        }
        return names;
    }

    static Set<String> names(Collection<IGridNode> nodes, Address source, PhysicalMeTopology topology) {
        Set<String> result = new java.util.HashSet<>();
        names(nodes, topology).forEach((label, names) -> { if (sameSource(label, source)) result.addAll(names); });
        return Set.copyOf(result);
    }

    static void rename(Collection<IGridNode> nodes, Address source, String name, PhysicalMeTopology topology) {
        name = WirelessLinkData.cleanName(name);
        for (IGridNode node : nodes) {
            Endpoint endpoint = endpoint(node, topology);
            if (endpoint == null) continue;
            Map<Slot, Claim> claims = WirelessLinkData.read(WirelessTileData.of(endpoint.tile()));
            Claim saved = claims.get(endpoint.slot());
            if (saved == null) continue;
            Map<Address, String> names = new java.util.HashMap<>(saved.names());
            for (Address label : saved.frequencies()) {
                if (!sameSource(label, source)) continue;
                if (name.isEmpty()) names.remove(label);
                else names.put(label, name);
            }
            claims.put(
                endpoint.slot(),
                new Claim(saved.kind(), saved.frequencies(), saved.paused(), names, saved.entrances()));
            save(endpoint.tile(), claims);
        }
    }

    static void setPaused(Collection<IGridNode> nodes, Address source, boolean paused, PhysicalMeTopology topology) {
        for (IGridNode node : nodes) {
            Endpoint endpoint = endpoint(node, topology);
            if (endpoint == null) continue;
            Map<Slot, Claim> claims = WirelessLinkData.read(WirelessTileData.of(endpoint.tile()));
            Claim saved = claims.get(endpoint.slot());
            if (saved == null) continue;
            Set<Address> next = new java.util.HashSet<>(saved.paused());
            for (Address label : saved.frequencies()) {
                if (!sameSource(label, source)) continue;
                if (paused) next.add(label);
                else next.remove(label);
            }
            claims.put(
                endpoint.slot(),
                new Claim(saved.kind(), saved.frequencies(), next, saved.names(), saved.entrances()));
            save(endpoint.tile(), claims);
        }
    }

    private static Endpoint endpoint(IGridNode node, PhysicalMeTopology topology) {
        Address address = topology.nodeAddress(node);
        if (address == null) return null;
        TileEntity tile = address.tile();
        if (tile == null || tile.isInvalid()) return null;
        Slot slot = new Slot(address.side(), topology.external(node));
        String kind = kind(tile, slot);
        return kind == null ? null : new Endpoint(tile, address, slot, kind);
    }

    private static Endpoint endpoint(TileEntity tile, Slot slot, String kind) {
        return new Endpoint(
            tile,
            new Address(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord, slot.side()),
            slot,
            kind);
    }

    private static String kind(TileEntity tile, Slot slot) {
        if (tile instanceof IPartHost host) {
            IPart part = host.getPart(slot.side());
            return part == null ? null
                : part.getClass()
                    .getName();
        }
        return tile instanceof IGridHost && !slot.external() ? tile.getClass()
            .getName() : null;
    }

    private static boolean loaded(TileEntity tile) {
        World world = tile.getWorldObj();
        return world != null && !world.isRemote
            && !tile.isInvalid()
            && DimensionManager.getWorld(world.provider.dimensionId) == world
            && world.blockExists(tile.xCoord, tile.yCoord, tile.zCoord)
            && world.getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord) == tile;
    }

    private static void save(TileEntity tile, Map<Slot, Claim> claims) {
        if (WirelessLinkData.write(WirelessTileData.of(tile), claims)) tile.markDirty();
    }

    /** Unloading forgets object identities only. The tile's saved claims remain intact. */
    static void forget(Predicate<Address> removed) {
        IMPORTED.values()
            .removeIf(endpoint -> removed.test(endpoint.address()));
        TILES.removeIf(
            tile -> tile.getWorldObj() != null && removed.test(
                new Address(
                    tile.getWorldObj().provider.dimensionId,
                    tile.xCoord,
                    tile.yCoord,
                    tile.zCoord,
                    net.minecraftforge.common.util.ForgeDirection.UNKNOWN)));
    }

    static void clear() {
        TILES.clear();
        IMPORTED.clear();
        initialized = false;
    }

    public static void clearLoadedClaims() {
        seeds();
        for (TileEntity tile : new ArrayList<>(TILES)) {
            if (loaded(tile)) save(tile, Map.of());
        }
    }
}
