package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
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
import appeng.me.pathfinding.PathingCalculation;
import appeng.tile.networking.TileController;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Low-level owned entrance registry. Cluster lifetime and recovery are handled by WirelessClusterManager.
 * All mutations happen on the server thread. No world lookup here may load a chunk or dimension.
 */
public final class WirelessChannelPrototype {

    public static final WirelessChannelPrototype INSTANCE = new WirelessChannelPrototype();
    private static final int MAX_ENTRANCES = 256;
    private static final Map<Integer, Entrance> ENTRANCES = new LinkedHashMap<>();
    private static final Map<IGridConnection, Entrance> OWNED_CONNECTIONS = new IdentityHashMap<>();
    private static final Map<IGrid, Allocation> ALLOCATIONS = new WeakHashMap<>();
    private static int nextId = 1;
    private static long allocationRevision;

    private WirelessChannelPrototype() {}

    public record Allocation(int capacity, int used, long revision) {}

    public static void recordAllocation(IGrid grid, ChannelBudget budget) {
        ALLOCATIONS.put(grid, new Allocation(budget.capacity(), budget.allocated(), ++allocationRevision));
    }

    public static Allocation allocation(IGrid grid) {
        return ALLOCATIONS.get(grid);
    }

    public record Address(int dimension, int x, int y, int z, ForgeDirection side) {

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

    public record Entrance(int id, Address source, Address target, IGridNode sourceNode, IGridNode targetNode,
        IGridConnection connection) {

        public boolean isLive() {
            return sourceNode.getConnections()
                .contains(connection)
                && targetNode.getConnections()
                    .contains(connection);
        }
    }

    public static boolean isOwned(IGridConnection connection) {
        return OWNED_CONNECTIONS.containsKey(connection);
    }

    public static boolean manages(IGrid grid) {
        for (Entrance entrance : ENTRANCES.values()) {
            if (entrance.isLive() && entrance.sourceNode()
                .getGrid() == grid) return true;
        }
        return false;
    }

    /** Snapshot recomputed for each pathing pass, so adding/removing controllers changes the budget. */
    public static int capacity(IGrid grid) {
        Set<ChannelBudget.Position> controllers = new HashSet<>();
        // AE already indexes hosts by their concrete class; avoid scanning every cable and device for each GUI update.
        Class<?> controllerType = TileController.class;
        for (Class<? extends IGridHost> machine : grid.getMachinesClasses()) {
            if (!controllerType.isAssignableFrom(machine)) continue;
            if (machine != controllerType) return 0;
            for (IGridNode node : grid.getMachines(machine)) {
                // Keep the expression typed as TileEntity: AE2's controller superclass exposes optional mod APIs.
                TileEntity controller = (TileEntity) node.getMachine();
                // Wireless links support only ordinary controllers. Unknown controller subclasses fail closed.
                if (controller.getClass() != TileController.class || controller.getWorldObj() == null) return 0;
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

    public static int connect(Address source, Address target) throws FailedConnection {
        if (!WirelessPathingAccess.class.isAssignableFrom(PathingCalculation.class)) {
            throw new IllegalArgumentException("Wireless pathing hooks are unavailable; no connection was created.");
        }
        if (ENTRANCES.size() >= MAX_ENTRANCES) throw new IllegalArgumentException("Wireless entrance limit reached.");
        TileEntity controller = source.tile();
        if (controller == null || controller.getClass() != TileController.class) {
            throw new IllegalArgumentException("Source must be a loaded ordinary ME controller.");
        }
        IGridNode from = source.node();
        IGridNode to = target.node();
        if (from == null || to == null) throw new IllegalArgumentException("Both ME nodes must be loaded and ready.");
        if ((Object) to.getMachine() instanceof TileController)
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
                if ((Object) node.getMachine() instanceof TileController) {
                    throw new IllegalArgumentException("Cannot merge a second controller network.");
                }
            }
        }
        // AE2 performs its own security, self-link and duplicate-connection checks.
        IGridConnection connection = AEApi.instance()
            .createGridConnection(from, to);
        int id = nextId++;
        Entrance entrance = new Entrance(id, source, target, from, to, connection);
        ENTRANCES.put(id, entrance);
        OWNED_CONNECTIONS.put(connection, entrance);
        from.getGrid()
            .<IPathingGrid>getCache(IPathingGrid.class)
            .repath();
        return id;
    }

    public static boolean disconnect(int id) {
        Entrance entrance = ENTRANCES.remove(id);
        if (entrance == null) return false;
        OWNED_CONNECTIONS.remove(entrance.connection());
        if (entrance.isLive()) entrance.connection()
            .destroy();
        return true;
    }

    public static void clear() {
        for (int id : new ArrayList<>(ENTRANCES.keySet())) disconnect(id);
        OWNED_CONNECTIONS.clear();
        ALLOCATIONS.clear();
        WirelessClusterManager.clear();
        WirelessAutoConnect.clear();
        nextId = 1;
        allocationRevision = 0;
    }

    public static Iterable<Entrance> entrances() {
        return new ArrayList<>(ENTRANCES.values());
    }

    public static Entrance entrance(int id) {
        return ENTRANCES.get(id);
    }

    public static boolean entranceLimitReached() {
        return ENTRANCES.size() >= MAX_ENTRANCES;
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
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        for (Entrance entrance : new ArrayList<>(ENTRANCES.values())) {
            if (!entrance.isLive() || entrance.source()
                .node() != entrance.sourceNode()
                || entrance.target()
                    .node() != entrance.targetNode()) {
                disconnect(entrance.id());
                WirelessClusterManager.invalidateTopology();
            }
        }
        WirelessClusterManager.tick();
        WirelessAutoConnect.tick();
    }

    @SubscribeEvent
    public void loadChunk(ChunkEvent.Load event) {
        if (event.world.isRemote) return;
        WirelessLinkPersistence.discover(event.getChunk());
        WirelessClusterManager.invalidateTopology();
    }

    @SubscribeEvent
    public void unloadChunk(ChunkEvent.Unload event) {
        if (event.world.isRemote) return;
        WirelessClusterManager.forgetTargets(address -> inChunk(address, event));
        for (Entrance entrance : new ArrayList<>(ENTRANCES.values())) {
            if (inChunk(entrance.source(), event) || inChunk(entrance.target(), event)) disconnect(entrance.id());
        }
    }

    private static boolean inChunk(Address address, ChunkEvent event) {
        return address.dimension() == event.world.provider.dimensionId
            && (address.x() >> 4) == event.getChunk().xPosition
            && (address.z() >> 4) == event.getChunk().zPosition;
    }

    @SubscribeEvent
    public void unloadWorld(WorldEvent.Unload event) {
        if (event.world.isRemote) return;
        WirelessClusterManager.forgetTargets(address -> address.dimension() == event.world.provider.dimensionId);
        for (Entrance entrance : new ArrayList<>(ENTRANCES.values())) {
            if (entrance.source()
                .dimension() == event.world.provider.dimensionId
                || entrance.target()
                    .dimension() == event.world.provider.dimensionId)
                disconnect(entrance.id());
        }
    }
}
