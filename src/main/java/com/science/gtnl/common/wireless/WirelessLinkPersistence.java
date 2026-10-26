package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessChannelManager.sameSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.common.wireless.WirelessChannelManager.Entrance;
import com.science.gtnl.common.wireless.WirelessLinkData.Claim;
import com.science.gtnl.common.wireless.WirelessLinkData.Slot;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.helpers.ICustomNameObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

/**
 * Claims live in tile-owned NBT, which is saved even after an AE proxy has invalidated its grid node.
 * No coordinate registry outlives a removed tile. Only loaded tiles are inspected; wireless links never load chunks.
 */
public class WirelessLinkPersistence {

    private static final Set<TileEntity> TILES = new ReferenceOpenHashSet<>();
    private static final Set<TileEntity> PLACED = new ReferenceOpenHashSet<>();
    private static final Map<IGridNode, Endpoint> IMPORTED = new Reference2ObjectOpenHashMap<>();
    private static final Map<Entrance, RememberedEntrance> REMEMBERED_ENTRANCES = new Reference2ObjectOpenHashMap<>();
    private static boolean initialized;
    private static boolean pendingSeeds;

    private record RememberedEntrance(Endpoint endpoint, NBTBase payload) {

        boolean matches(IGridNode node) {
            return endpoint.loaded() && endpoint.node() == node
                && IWirelessTileData.of(endpoint.tile())
                    .getTag(WirelessLinkData.KEY) == payload;
        }
    }

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

    public static boolean discover(TileEntity tile) {
        if (!(tile instanceof IWirelessTileData data) || !data.gtnl$hasWirelessData()) return false;
        TILES.add(tile);
        return true;
    }

    public static void placed(TileEntity tile) {
        World world = tile.getWorldObj();
        if (world == null || world.isRemote) return;
        // Tiles moved by tools such as Matter Manipulator arrive through setTileEntity, without a chunk load.
        PLACED.add(tile);
        WirelessClusterManager.invalidateTopology();
    }

    public static boolean discover(Chunk chunk) {
        // World.loadedTileEntityList contains only ticking tiles in Forge 1.7.10; most AE cable hosts are absent.
        boolean found = false;
        for (Object object : chunk.chunkTileEntityMap.values()) {
            if (object instanceof TileEntity tile) found |= discover(tile);
        }
        return found;
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
                Map<Slot, Claim> saved = WirelessLinkData.read(IWirelessTileData.of(endpoint.tile()));
                saved.remove(endpoint.slot());
                save(endpoint.tile(), saved);
                previous.remove();
            }
        }
        List<IGridNode> seeds = new ObjectArrayList<>();
        pendingSeeds = false;
        var placed = PLACED.iterator();
        while (placed.hasNext()) {
            TileEntity tile = placed.next();
            if (!loaded(tile)) placed.remove();
            else if (!WirelessConstructionSites.contains(address(tile))) {
                placed.remove();
                discover(tile);
            }
        }
        var tiles = TILES.iterator();
        while (tiles.hasNext()) {
            TileEntity tile = tiles.next();
            if (!loaded(tile)) {
                tiles.remove();
                continue;
            }
            Map<Slot, Claim> claims = WirelessLinkData.read(IWirelessTileData.of(tile));
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
                if (node != null) seeds.add(node);
                else pendingSeeds = true;
            }
            if (changed) save(tile, claims);
            if (claims.isEmpty()) tiles.remove();
        }
        return seeds;
    }

    static boolean hasPendingSeeds() {
        return pendingSeeds;
    }

    static Set<Address> restore(IGridNode node, PhysicalMeTopology topology) {
        if (IMPORTED.containsKey(node)) return Set.of();
        Claim saved = claim(node, topology);
        return saved == null ? Set.of() : saved.frequencies();
    }

    static void synchronize(PhysicalClusterTracker<IGridNode, Address> tracker, PhysicalMeTopology topology) {
        REMEMBERED_ENTRANCES.clear();
        Map<TileEntity, Map<Slot, Claim>> updates = new Reference2ObjectOpenHashMap<>();
        Set<IGridNode> retained = new ReferenceOpenHashSet<>();
        for (var cluster : tracker.clusters()) {
            Set<Address> paused = new ObjectOpenHashSet<>();
            if (!cluster.limited()) for (IGridNode node : cluster.nodes()) {
                Claim saved = claim(node, topology);
                if (saved != null) paused.addAll(saved.paused());
            }
            for (IGridNode node : cluster.nodes()) {
                Endpoint endpoint = endpoint(node, topology);
                if (endpoint == null) continue;
                retained.add(node);
                IMPORTED.put(node, endpoint);
                // Even a quarantined component needs node/slot identity tracking: replacing a part of the same
                // class must not import its predecessor's saved claim. Its payload stays untouched until recovery.
                if (cluster.limited()) continue;
                Map<Slot, Claim> claims = updates
                    .computeIfAbsent(endpoint.tile(), tile -> WirelessLinkData.read(IWirelessTileData.of(tile)));
                Set<Address> frequencies = tracker.claimsOf(node);
                if (frequencies.isEmpty()) claims.remove(endpoint.slot());
                else {
                    Claim old = claims.get(endpoint.slot());
                    Set<Address> sourcePauses = new ObjectOpenHashSet<>();
                    for (Address source : frequencies) {
                        if (paused.stream()
                            .anyMatch(label -> sameSource(label, source))) sourcePauses.add(source);
                    }
                    // Entrance locations belong to the original node, never to every member of the cluster.
                    claims.put(
                        endpoint.slot(),
                        new Claim(
                            endpoint.kind(),
                            frequencies,
                            sourcePauses,
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
        Claim saved = WirelessLinkData.claim(IWirelessTileData.of(endpoint.tile()), endpoint.slot());
        return saved != null && saved.kind()
            .equals(endpoint.kind()) ? saved : null;
    }

    static Set<Address> pausedSources(Collection<IGridNode> nodes, PhysicalMeTopology topology) {
        Set<Address> paused = new ObjectOpenHashSet<>();
        for (IGridNode node : nodes) {
            Claim saved = claim(node, topology);
            if (saved != null) saved.paused()
                .stream()
                .map(WirelessChannelManager::controllerSource)
                .forEach(paused::add);
        }
        return paused;
    }

    static Set<IGridNode> savedEntrances(Collection<IGridNode> nodes, Address source, PhysicalMeTopology topology) {
        Set<IGridNode> result = new ReferenceOpenHashSet<>();
        for (IGridNode node : nodes) {
            Claim saved = claim(node, topology);
            if (saved != null && saved.entrances()
                .stream()
                .anyMatch(label -> sameSource(label, source))) result.add(node);
        }
        return result;
    }

    static void rememberEntrance(Entrance entrance, PhysicalMeTopology topology) {
        if (entrance == null) return;
        IGridNode node = entrance.targetNode();
        RememberedEntrance remembered = REMEMBERED_ENTRANCES.get(entrance);
        if (remembered != null && remembered.matches(node)) return;
        REMEMBERED_ENTRANCES.remove(entrance);
        Endpoint endpoint = endpoint(node, topology);
        if (endpoint == null || endpoint.node() != node
            || endpoint.slot()
                .external())
            return;
        NBTTagCompound tileData = IWirelessTileData.of(endpoint.tile());
        Claim saved = WirelessLinkData.claim(tileData, endpoint.slot());
        if (saved == null || !saved.kind()
            .equals(endpoint.kind())) return;
        boolean matched = false;
        Set<Address> entrances = null;
        for (Address label : saved.frequencies()) {
            if (!sameSource(label, entrance.source())) continue;
            matched = true;
            if (saved.entrances()
                .contains(label)) continue;
            if (entrances == null) entrances = new ObjectOpenHashSet<>(saved.entrances());
            entrances.add(label);
        }
        if (!matched) return;
        if (entrances != null) {
            Map<Slot, Claim> claims = WirelessLinkData.read(tileData);
            claims.put(endpoint.slot(), new Claim(saved.kind(), saved.frequencies(), saved.paused(), entrances));
            save(endpoint.tile(), claims);
        }
        REMEMBERED_ENTRANCES.put(entrance, new RememberedEntrance(endpoint, tileData.getTag(WirelessLinkData.KEY)));
    }

    static void forgetEntrance(Entrance entrance) {
        REMEMBERED_ENTRANCES.remove(entrance);
    }

    static Map<IGridNode, String> names(Collection<IGridNode> nodes) {
        Map<IGridNode, String> names = new Reference2ObjectOpenHashMap<>();
        for (IGridNode node : nodes) {
            if (node.getMachine() instanceof ICustomNameObject named) names.put(node, customName(named));
        }
        return Map.copyOf(names);
    }

    private static String customName(ICustomNameObject named) {
        return named.hasCustomName() ? Objects.toString(named.getCustomName(), "") : "";
    }

    static String cleanName(String value) {
        if (value == null) return "";
        return value.codePoints()
            .filter(c -> !Character.isISOControl(c) && c != 0xA7 && !(c >= 0xD800 && c <= 0xDFFF))
            .limit(32)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString()
            .strip();
    }

    static boolean rename(Map<IGridNode, String> names, String name, PhysicalMeTopology topology,
        Predicate<IGridNode> canRename) {
        Map<ICustomNameObject, TileEntity> targets = new Reference2ObjectOpenHashMap<>();
        // Validate every member before any setter runs; internal and external nodes may share the same owner.
        for (IGridNode node : names.keySet()) {
            if (!canRename.test(node)) return false;
            Endpoint endpoint = endpoint(node, topology);
            if (endpoint == null || !endpoint.loaded()
                || endpoint.node() != node
                || !(node.getMachine() instanceof ICustomNameObject named)
                || !customName(named).equals(names.get(node)))
                throw new IllegalStateException("The displayed device changed");
            targets.put(named, endpoint.tile());
        }
        name = cleanName(name);
        for (var target : targets.entrySet()) {
            if (customName(target.getKey()).equals(name)) continue;
            target.getKey()
                .setCustomName(name);
            if (target.getValue() instanceof IPartHost host) {
                host.markForSave();
                host.markForUpdate();
            } else {
                target.getValue()
                    .markDirty();
            }
        }
        return !targets.isEmpty();
    }

    static void setPaused(Collection<IGridNode> nodes, Address source, boolean paused, PhysicalMeTopology topology) {
        for (IGridNode node : nodes) {
            Endpoint endpoint = endpoint(node, topology);
            if (endpoint == null) continue;
            Map<Slot, Claim> claims = WirelessLinkData.read(IWirelessTileData.of(endpoint.tile()));
            Claim saved = claims.get(endpoint.slot());
            if (saved == null) continue;
            Set<Address> next = new ObjectOpenHashSet<>(saved.paused());
            for (Address label : saved.frequencies()) {
                if (!sameSource(label, source)) continue;
                if (paused) next.add(label);
                else next.remove(label);
            }
            claims.put(endpoint.slot(), new Claim(saved.kind(), saved.frequencies(), next, saved.entrances()));
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
        if (WirelessLinkData.write(IWirelessTileData.of(tile), claims)) tile.markDirty();
    }

    static boolean forget(Predicate<Address> removed) {
        REMEMBERED_ENTRANCES.keySet()
            .removeIf(entrance -> removed.test(entrance.source()) || removed.test(entrance.target()));
        boolean forgotten = IMPORTED.values()
            .removeIf(endpoint -> removed.test(endpoint.address()));
        PLACED.removeIf(tile -> tile.getWorldObj() != null && removed.test(address(tile)));
        return TILES.removeIf(tile -> tile.getWorldObj() != null && removed.test(address(tile))) || forgotten;
    }

    static Address address(TileEntity tile) {
        return new Address(
            tile.getWorldObj().provider.dimensionId,
            tile.xCoord,
            tile.yCoord,
            tile.zCoord,
            ForgeDirection.UNKNOWN);
    }

    static void clear() {
        PLACED.clear();
        TILES.clear();
        IMPORTED.clear();
        REMEMBERED_ENTRANCES.clear();
        initialized = false;
        pendingSeeds = false;
    }

    public static void clearLoadedClaims() {
        seeds();
        for (TileEntity tile : new ObjectArrayList<>(TILES)) {
            if (loaded(tile)) save(tile, Map.of());
        }
    }
}
