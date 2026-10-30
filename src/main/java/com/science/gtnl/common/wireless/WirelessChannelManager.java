package com.science.gtnl.common.wireless;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.mixins.late.appliedEnergistics.wireless.AccessorTileController;

import appeng.api.AEApi;
import appeng.api.config.SecurityPermissions;
import appeng.api.exceptions.FailedConnection;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.pathing.ControllerState;
import appeng.api.networking.pathing.IPathingGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.core.AEConfig;
import appeng.core.features.AEFeature;
import appeng.me.GridNode;
import appeng.me.pathfinding.PathingCalculation;
import appeng.tile.networking.TileController;
import appeng.tile.networking.TileCreativeEnergyController;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

@EventBusSubscriber
public class WirelessChannelManager {

    private static final Int2ObjectLinkedOpenHashMap<Entrance> ENTRANCES = new Int2ObjectLinkedOpenHashMap<>();
    private static List<Entrance> entranceSnapshot = List.of();
    private static Object2IntOpenHashMap<WirelessFrequency> entranceCounts;
    private static final Map<IGridConnection, Entrance> OWNED_CONNECTIONS = new Reference2ObjectOpenHashMap<>();
    private static final Map<IGrid, Allocation> ALLOCATIONS = new WeakHashMap<>();
    private static int nextId = 1;
    private static long allocationRevision;
    private static Map<IGridNode, Set<IGridConnection>> connectionSnapshot;
    private static boolean creatingOwnedConnection;
    private static boolean entrancesVerified;

    public static boolean isSupportedController(TileEntity tile) {
        return tile != null
            && (tile.getClass() == TileController.class || tile.getClass() == TileCreativeEnergyController.class);
    }

    static void invalidateFrequencies() {
        WirelessFrequencies.invalidate();
        entranceCounts = null;
    }

    private WirelessChannelManager() {}

    public record Allocation(int capacity, int used, long revision) {}

    public static void recordAllocation(IGrid grid, int capacity, int used) {
        ALLOCATIONS.put(grid, new Allocation(capacity, used, ++allocationRevision));
    }

    public static void invalidateAllocation(IGrid grid) {
        ALLOCATIONS.remove(grid);
    }

    public static Allocation allocation(IGrid grid) {
        return AutomaticWirelessEntrances.isSettled(grid.getCache(IPathingGrid.class)) ? ALLOCATIONS.get(grid) : null;
    }

    public record Address(int dimension, int x, int y, int z, ForgeDirection side) {

        public static final Comparator<Address> POSITION_ORDER = Comparator.comparingInt(Address::x)
            .thenComparingInt(Address::y)
            .thenComparingInt(Address::z)
            .thenComparing(Address::side);

        public TileEntity tile() {
            World world = DimensionManager.getWorld(dimension);
            return world != null && world.blockExists(x, y, z) ? world.getTileEntity(x, y, z) : null;
        }

        public IGridNode node() {
            TileEntity tile = tile();
            if (tile instanceof IPartHost host) {
                IPart part = host.getPart(side);
                return part == null ? null : part.getGridNode();
            }
            return tile instanceof IGridHost host ? host.getGridNode(side) : null;
        }
    }

    public record Entrance(int id, WirelessFrequency frequency, Address source, Address target, IGridNode sourceNode,
        IGridNode targetNode, IGridConnection connection) {

        public boolean isLive() {
            return hasConnection(sourceNode, connection) && hasConnection(targetNode, connection);
        }
    }

    private static boolean hasConnection(IGridNode node, IGridConnection connection) {
        // Only native GridNode mutations are covered by our invalidation hook.
        if (connectionSnapshot == null || node.getClass() != GridNode.class) return node.getConnections()
            .contains(connection);
        return connectionSnapshot.computeIfAbsent(node, key -> {
            Set<IGridConnection> connections = new ReferenceOpenHashSet<>();
            for (IGridConnection current : key.getConnections()) connections.add(current);
            return connections;
        })
            .contains(connection);
    }

    public static void connectionsChanged(IGridNode node, IGridConnection connection) {
        if (connectionSnapshot != null) connectionSnapshot.remove(node);
        if (creatingOwnedConnection || isOwned(connection)) return;
        WirelessClusterManager.connectionChanged(node);
        IGrid grid = node.getGrid();
        if (grid != null && grid.getCache(IPathingGrid.class) instanceof IWirelessPathingState state)
            state.gtnl$topologyChanged();
    }

    public static boolean isOwned(IGridConnection connection) {
        return OWNED_CONNECTIONS.containsKey(connection);
    }

    public static boolean manages(IGrid grid) {
        for (Entrance entrance : ENTRANCES.values()) {
            if (entrance.sourceNode()
                .getGrid() == grid && entrance.isLive()) return true;
        }
        return false;
    }

    public static int capacity(IGrid grid) {
        Set<ChannelBudget.Position> controllers = new ObjectOpenHashSet<>();
        // AE already indexes hosts by their concrete class; avoid scanning every cable and device for each GUI update.
        Class<?> controllerType = TileController.class;
        for (Class<? extends IGridHost> machine : grid.getMachinesClasses()) {
            if (!controllerType.isAssignableFrom(machine)) continue;
            for (IGridNode node : grid.getMachines(machine)) {
                // AE retains empty machine-class buckets after removal. Only a controller that is
                // still present can make this structure unsupported, not its historical class key.
                // Keep the expression typed as TileEntity: AE2's controller superclass exposes optional mod APIs.
                TileEntity controller = (TileEntity) node.getMachine();
                if (!isSupportedController(controller) || controller.getWorldObj() == null) return 0;
                controllers.add(
                    new ChannelBudget.Position(
                        controller.getWorldObj().provider.dimensionId,
                        controller.xCoord,
                        controller.yCoord,
                        controller.zCoord));
            }
        }
        return ChannelBudget.capacityOf(controllers);
    }

    public static int connect(WirelessFrequency frequency, Address target) throws FailedConnection {
        if (!IWirelessPathingAccess.class.isAssignableFrom(PathingCalculation.class)) {
            throw new IllegalArgumentException("Wireless pathing hooks are unavailable; no connection was created.");
        }
        if (entranceLimitReached(frequency))
            throw new IllegalArgumentException("Wireless entrance limit reached for this frequency.");
        Address source = WirelessFrequencies.controller(frequency);
        TileEntity controller = source == null ? null : source.tile();
        if (!isSupportedController(controller)) {
            throw new IllegalArgumentException("Source must be a loaded ME Controller or Creative Energy Controller.");
        }
        IGridNode from = source.node();
        IGridNode to = target.node();
        if (from == null || to == null) throw new IllegalArgumentException("Both ME nodes must be loaded and ready.");
        if (to.getMachine() instanceof AccessorTileController)
            throw new IllegalArgumentException("Cannot target a controller.");
        IGrid grid = from.getGrid();
        IPathingGrid path = grid.getCache(IPathingGrid.class);
        if (path.isNetworkBooting() || path.getControllerState() != ControllerState.CONTROLLER_ONLINE) {
            throw new IllegalArgumentException("Wait for the source controller network to become valid and stable.");
        }
        if (capacity(grid) == 0) throw new IllegalArgumentException("Unsupported controller structure.");
        if (to.getGrid() != grid) {
            for (IGridNode node : to.getGrid()
                .getNodes()) {
                if (node.getMachine() instanceof AccessorTileController) {
                    throw new IllegalArgumentException("Cannot merge a second controller network.");
                }
            }
        }
        // AE2 performs its own security, self-link and duplicate-connection checks.
        IGridConnection connection;
        creatingOwnedConnection = true;
        try {
            connection = AEApi.instance()
                .createGridConnection(from, to);
        } finally {
            creatingOwnedConnection = false;
        }
        int id = nextId++;
        Entrance entrance = new Entrance(id, frequency, source, target, from, to, connection);
        ENTRANCES.put(id, entrance);
        entranceCountChanged(frequency, 1);
        OWNED_CONNECTIONS.put(connection, entrance);
        // createGridConnection already schedules AE2's repath.
        invalidateAllocation(grid);
        invalidateAllocation(from.getGrid());
        return id;
    }

    public static boolean disconnect(int id) {
        Entrance entrance = ENTRANCES.remove(id);
        if (entrance == null) return false;
        invalidateAllocation(
            entrance.sourceNode()
                .getGrid());
        invalidateAllocation(
            entrance.targetNode()
                .getGrid());
        entranceCountChanged(entrance.frequency(), -1);
        WirelessLinkPersistence.forgetEntrance(entrance);
        if (entrance.isLive()) entrance.connection()
            .destroy();
        OWNED_CONNECTIONS.remove(entrance.connection());
        return true;
    }

    public static void clear() {
        invalidateFrequencies();
        for (int id : ENTRANCES.keySet()
            .toIntArray()) disconnect(id);
        entranceSnapshot = List.of();
        OWNED_CONNECTIONS.clear();
        ALLOCATIONS.clear();
        WirelessClusterManager.clear();
        WirelessAutoConnect.clear();
        WirelessCopiedLinks.clear();
        WirelessOperations.clear();
        WirelessFrequencies.clear();
        nextId = 1;
        allocationRevision = 0;
    }

    public static List<Entrance> entrances() {
        if (entranceSnapshot == null) entranceSnapshot = List.copyOf(ENTRANCES.values());
        return entranceSnapshot;
    }

    public static Entrance entrance(int id) {
        return ENTRANCES.get(id);
    }

    public static int entranceLimit() {
        return Math.max(1, MainConfig.wireless.maxEntrancesPerFrequency);
    }

    public static int entranceCount(WirelessFrequency frequency) {
        if (entranceCounts == null) {
            entranceCounts = new Object2IntOpenHashMap<>();
            for (Entrance entrance : ENTRANCES.values()) {
                entranceCounts.addTo(WirelessFrequencies.canonical(entrance.frequency()), 1);
            }
        }
        return entranceCounts.getOrDefault(WirelessFrequencies.canonical(frequency), 0);
    }

    private static void entranceCountChanged(WirelessFrequency source, int change) {
        entranceSnapshot = null;
        if (entranceCounts == null) return;
        WirelessFrequency frequency = WirelessFrequencies.canonical(source);
        int count = entranceCounts.getOrDefault(frequency, 0) + change;
        if (count == 0) entranceCounts.removeInt(frequency);
        else entranceCounts.put(frequency, count);
    }

    public static boolean entranceLimitReached(WirelessFrequency frequency) {
        return entranceCount(frequency) >= entranceLimit();
    }

    public static boolean channelsEnabled() {
        return AEConfig.instance.isFeatureEnabled(AEFeature.Channels);
    }

    public static boolean canBuild(IGridNode node, EntityPlayer player) {
        return node != null && node.getGrid() != null
            && node.getGrid()
                .<ISecurityGrid>getCache(ISecurityGrid.class)
                .hasPermission(player, SecurityPermissions.BUILD);
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        connectionSnapshot = new Reference2ObjectOpenHashMap<>();
        try (var ignored = WirelessLinkData.readBatch()) {
            entranceCounts = null;
            WirelessFrequencies.tick();
            for (Entrance entrance : entrances()) {
                if (!entrance.isLive() || entrance.source()
                    .node() != entrance.sourceNode()
                    || entrance.target()
                        .node() != entrance.targetNode()
                    || !WirelessFrequencies.hosts(entrance.frequency(), entrance.source())) {
                    disconnect(entrance.id());
                    WirelessClusterManager.invalidateTopology();
                }
            }
            entrancesVerified = true;
            try {
                WirelessClusterManager.tick();
            } finally {
                entrancesVerified = false;
            }
            WirelessAutoConnect.tick();
            WirelessCopiedLinks.tick();
        } finally {
            connectionSnapshot = null;
        }
    }

    static boolean entrancesVerified() {
        return entrancesVerified;
    }

    @SubscribeEvent
    public static void loadChunk(ChunkEvent.Load event) {
        if (event.world.isRemote) return;
        if (WirelessLinkPersistence.discover(event.getChunk())) WirelessClusterManager.invalidateTopology();
    }

    @SubscribeEvent
    public static void unloadChunk(ChunkEvent.Unload event) {
        if (event.world.isRemote) return;
        WirelessFrequencies.forget(address -> inChunk(address, event));
        boolean changed = WirelessClusterManager.forgetTargets(address -> inChunk(address, event));
        for (Entrance entrance : entrances()) {
            if (inChunk(entrance.source(), event) || inChunk(entrance.target(), event)) {
                disconnect(entrance.id());
                changed = true;
            }
        }
        if (changed) WirelessClusterManager.invalidateTopology();
    }

    private static boolean inChunk(Address address, ChunkEvent event) {
        return address.dimension() == event.world.provider.dimensionId
            && (address.x() >> 4) == event.getChunk().xPosition
            && (address.z() >> 4) == event.getChunk().zPosition;
    }

    @SubscribeEvent
    public static void unloadWorld(WorldEvent.Unload event) {
        if (event.world.isRemote) return;
        WirelessFrequencies.forget(address -> address.dimension() == event.world.provider.dimensionId);
        WirelessClusterManager.forgetTargets(address -> address.dimension() == event.world.provider.dimensionId);
        WirelessClusterManager.invalidateTopology();
        for (Entrance entrance : entrances()) {
            if (entrance.source()
                .dimension() == event.world.provider.dimensionId
                || entrance.target()
                    .dimension() == event.world.provider.dimensionId)
                disconnect(entrance.id());
        }
    }
}
