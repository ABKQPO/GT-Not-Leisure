package com.science.gtnl.common.wireless;

import java.lang.ref.WeakReference;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.UnaryOperator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.common.wireless.WirelessLinkData.Claim;
import com.science.gtnl.common.wireless.WirelessLinkData.Slot;
import com.science.gtnl.common.wireless.WirelessOperations.Result;

import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.parts.PartItemStack;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;

public class WirelessCopiedLinks {

    public static final int READY_TICKS = 200;
    public static final int MAX_PENDING = 65536;
    public static final int MAX_PENDING_PER_PLAYER = 4096;
    public static final int CLAIMS_PER_TICK = 1;
    public static final int SCANS_PER_TICK = 2048;
    private static final ObjectArrayFIFOQueue<Pending> QUEUE = new ObjectArrayFIFOQueue<>();
    private static final Map<Address, List<Pending>> WAITING = new Object2ObjectOpenHashMap<>();
    private static final Object2IntOpenHashMap<UUID> PLAYER_WAITING = new Object2IntOpenHashMap<>();
    private static int waiting;
    private static int claimBudget;
    private static long ticks;

    public record CopiedLink(Address source, Class<? extends IPart> partType, Item partItem, int partDamage) {

        static CopiedLink capture(Address source, IPart part) {
            if (part == null) return new CopiedLink(source, null, null, 0);
            ItemStack stack = part.getItemStack(PartItemStack.Wrench);
            return stack == null || stack.getItem() == null ? null
                : new CopiedLink(source, part.getClass(), stack.getItem(), stack.getItemDamage());
        }

        boolean matches(IPart part) {
            if (partType == null) return part == null;
            if (part == null || part.getClass() != partType) return false;
            ItemStack stack = part.getItemStack(PartItemStack.Wrench);
            return stack != null && stack.getItem() == partItem && stack.getItemDamage() == partDamage;
        }
    }

    private static class Pending {

        public final UUID player;
        public final Address position;
        public final WeakReference<TileEntity> tile;
        public final WeakReference<IPart> part;
        public final Address source;
        public long expires = Long.MAX_VALUE;
        public boolean done;

        Pending(UUID player, Address position, TileEntity tile, IPart part, Address source) {
            this.player = player;
            this.position = position;
            this.tile = new WeakReference<>(tile);
            this.part = part == null ? null : new WeakReference<>(part);
            this.source = source;
        }

        boolean stillPasted(TileEntity current) {
            TileEntity pasted = tile.get();
            if (pasted == null || current != pasted || pasted.isInvalid()) return false;
            World world = pasted.getWorldObj();
            if (world == null || world.isRemote
                || world != DimensionManager.getWorld(position.dimension())
                || world.provider.dimensionId != position.dimension()
                || pasted.xCoord != position.x()
                || pasted.yCoord != position.y()
                || pasted.zCoord != position.z()) return false;
            if (part == null) return true;
            IPart original = part.get();
            return original != null && pasted instanceof IPartHost host && host.getPart(position.side()) == original;
        }

        boolean targets(Address node) {
            return part == null || position.side() == node.side();
        }
    }

    public static Map<ForgeDirection, CopiedLink> capture(TileEntity tile) {
        if (!(tile instanceof IWirelessTileData data) || !data.gtnl$hasWirelessData()
            || tile.getWorldObj() == null
            || tile.getWorldObj().isRemote) return null;
        Map<ForgeDirection, Address> frequencies = frequencies(WirelessLinkData.read(data.gtnl$wirelessData()));
        if (frequencies == null) return null;
        Map<ForgeDirection, CopiedLink> links = new EnumMap<>(ForgeDirection.class);
        frequencies.forEach((side, source) -> {
            IPart part = null;
            if (tile instanceof IPartHost host) {
                part = host.getPart(side);
                if (part == null) return;
            }
            CopiedLink link = CopiedLink.capture(source, part);
            if (link != null) links.put(side, link);
        });
        return links.isEmpty() ? null : Map.copyOf(links);
    }

    static Map<ForgeDirection, Address> frequencies(Map<Slot, Claim> claims) {
        Map<ForgeDirection, Address> links = new EnumMap<>(ForgeDirection.class);
        for (var entry : claims.entrySet()) {
            Set<Address> frequencies = entry.getValue()
                .frequencies();
            if (!entry.getKey()
                .external() && frequencies.size() == 1) links.put(
                    entry.getKey()
                        .side(),
                    frequencies.iterator()
                        .next());
        }
        return links.isEmpty() ? null : Map.copyOf(links);
    }

    public static <T> Map<ForgeDirection, T> rotate(Map<ForgeDirection, T> links,
        UnaryOperator<ForgeDirection> rotation) {
        if (links == null) return null;
        Map<ForgeDirection, T> rotated = new EnumMap<>(ForgeDirection.class);
        links.forEach(
            (side, source) -> rotated.put(side == ForgeDirection.UNKNOWN ? side : rotation.apply(side), source));
        return Map.copyOf(rotated);
    }

    public static void pasted(EntityPlayer player, TileEntity tile, Map<ForgeDirection, CopiedLink> links) {
        if (links == null || player == null
            || player instanceof FakePlayer
            || tile == null
            || tile.getWorldObj() == null
            || tile.getWorldObj().isRemote) return;
        int dimension = tile.getWorldObj().provider.dimensionId;
        for (var link : links.entrySet()) {
            IPart part = null;
            if (tile instanceof IPartHost host) {
                part = host.getPart(link.getKey());
                if (part == null) continue;
            }
            if (!link.getValue()
                .matches(part)) continue;
            Address position = new Address(dimension, tile.xCoord, tile.yCoord, tile.zCoord, link.getKey());
            if (!enqueue(
                player.getUniqueID(),
                position,
                tile,
                part,
                link.getValue()
                    .source())) {
                WirelessAutoConnect.notice(player, "copy_busy");
                return;
            }
        }
    }

    static boolean enqueue(UUID player, Address position, TileEntity tile, IPart part, Address source) {
        if (waiting >= MAX_PENDING || PLAYER_WAITING.getInt(player) >= MAX_PENDING_PER_PLAYER) return false;
        Pending pending = new Pending(player, position, tile, part, source);
        QUEUE.enqueue(pending);
        WAITING.computeIfAbsent(block(position), ignored -> new ObjectArrayList<>())
            .add(pending);
        waiting++;
        PLAYER_WAITING.addTo(player, 1);
        return true;
    }

    static int waiting() {
        return waiting;
    }

    static boolean clusterAgrees(Address source, List<Address> nodes) {
        return agree(source, waitingAt(nodes));
    }

    private static List<Pending> waitingAt(List<Address> nodes) {
        Set<Pending> group = new ReferenceLinkedOpenHashSet<>();
        for (Address node : nodes) {
            List<Pending> here = WAITING.get(block(node));
            if (here == null) continue;
            TileEntity current = node.tile();
            for (Pending pending : here) {
                if (pending.targets(node) && pending.stillPasted(current)) group.add(pending);
            }
        }
        return new ObjectArrayList<>(group);
    }

    private static boolean agree(Address source, List<Pending> group) {
        for (Pending other : group) {
            if (!WirelessChannelManager.sameSource(other.source, source)) return false;
        }
        return true;
    }

    static void tick() {
        ticks++;
        if (QUEUE.isEmpty()) return;
        Map<UUID, EntityPlayer> players = onlinePlayers();
        claimBudget = CLAIMS_PER_TICK;
        for (int scans = Math.min(QUEUE.size(), SCANS_PER_TICK); scans > 0; scans--) {
            Pending pending = QUEUE.dequeue();
            if (pending.done) continue;
            if (keep(pending, players.get(pending.player))) QUEUE.enqueue(pending);
            else finish(pending);
        }
    }

    private static boolean keep(Pending pending, EntityPlayer player) {
        Address position = pending.position;
        if (WirelessConstructionSites.contains(position)) return true;
        if (pending.expires == Long.MAX_VALUE) pending.expires = ticks + READY_TICKS;
        World world = DimensionManager.getWorld(position.dimension());
        if (player == null || world == null
            || !world.blockExists(position.x(), position.y(), position.z())
            || !pending.stillPasted(world.getTileEntity(position.x(), position.y(), position.z()))) return false;
        Address target = pending.part == null ? target(position) : position;
        IGridNode node = target == null ? null : target.node();
        if (node == null || node.getGrid() == null) return ticks <= pending.expires;
        if (WirelessClusterManager.isLinked(pending.source, node)) return false;
        if (claimBudget <= 0) return true;
        claimBudget--;
        List<Pending> group = new ObjectArrayList<>();
        Result result;
        try {
            result = WirelessOperations.claim(player, pending.source, target, nodes -> {
                group.addAll(waitingAt(nodes));
                return agree(pending.source, group);
            });
        } catch (IllegalArgumentException | IllegalStateException failure) {
            WirelessAutoConnect.notice(player, "copy_failed");
            return false;
        }
        switch (result) {
            case WAITING -> {
                if (ticks <= pending.expires) return true;
                WirelessAutoConnect.notice(player, "copy_unavailable");
            }
            case CONFLICT -> {
                WirelessAutoConnect.notice(player, "copy_conflict");
                group.forEach(WirelessCopiedLinks::finish);
            }
            case CARD_REQUIRED -> {
                WirelessAutoConnect.notice(player, "copy_card_required");
                group.stream()
                    .filter(other -> other.player.equals(pending.player))
                    .forEach(WirelessCopiedLinks::finish);
            }
            case APPLIED -> WirelessAutoConnect.notice(player, "copy_linked");
            case DENIED -> WirelessAutoConnect.notice(player, "denied");
            case UNAVAILABLE -> WirelessAutoConnect.notice(player, "copy_unavailable");
            default -> {}
        }
        return false;
    }

    private static void finish(Pending pending) {
        if (pending.done) return;
        pending.done = true;
        waiting--;
        if (PLAYER_WAITING.addTo(pending.player, -1) == 1) PLAYER_WAITING.removeInt(pending.player);
        Address block = block(pending.position);
        List<Pending> here = WAITING.get(block);
        if (here == null) return;
        here.remove(pending);
        if (here.isEmpty()) WAITING.remove(block);
    }

    private static Address block(Address address) {
        return new Address(address.dimension(), address.x(), address.y(), address.z(), ForgeDirection.UNKNOWN);
    }

    private static Address target(Address position) {
        for (ForgeDirection side : ForgeDirection.values()) {
            Address address = new Address(position.dimension(), position.x(), position.y(), position.z(), side);
            if (address.node() != null) return address;
        }
        return null;
    }

    private static Map<UUID, EntityPlayer> onlinePlayers() {
        Map<UUID, EntityPlayer> players = new Object2ObjectOpenHashMap<>();
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) return players;
        for (EntityPlayer player : server.getConfigurationManager().playerEntityList) {
            players.put(player.getUniqueID(), player);
        }
        return players;
    }

    static void clear() {
        QUEUE.clear();
        WAITING.clear();
        PLAYER_WAITING.clear();
        waiting = 0;
        claimBudget = 0;
        ticks = 0;
    }
}
