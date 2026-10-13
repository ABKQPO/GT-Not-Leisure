package com.science.gtnl.common.metoolbox;

import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BooleanSupplier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.ItemMEToolbox;
import com.science.gtnl.common.metoolbox.ToolboxRequestPacket.DepositMode;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PinsRows;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.SecurityPermissions;
import appeng.api.config.Settings;
import appeng.api.features.ILocatable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.IConfigManager;
import appeng.container.AEBaseContainer;
import appeng.container.PrimaryGui;
import appeng.container.implementations.ContainerCraftAmount;
import appeng.container.interfaces.IContainerSubGui;
import appeng.core.AEConfig;
import appeng.core.sync.GuiBridge;
import appeng.helpers.IPinsHandler;
import appeng.helpers.MonitorableAction;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.items.contents.PinList;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public final class ToolboxSessionService {

    private enum ToolboxStatus {
        ONLINE,
        UNBOUND,
        OFFLINE,
        NO_POWER,
        OUT_OF_RANGE,
        NO_PERMISSION,
        MISSING,
        ON_CURSOR,
        INVALID_CONTAINER,
        AMBIGUOUS,
        STALE
    }

    public static final ToolboxSessionService INSTANCE = new ToolboxSessionService();
    private static final Map<UUID, Session> SESSIONS = new HashMap<>();
    private static final Map<UUID, UUID> SELECTED = new HashMap<>();
    private static final Map<UUID, RequestBudget> BUDGETS = new HashMap<>();
    private static long nextSession;

    private ToolboxSessionService() {}

    public static void end(EntityPlayerMP player) {
        Session session = SESSIONS.remove(player.getUniqueID());
        if (session != null) session.close();
    }

    static void handle(EntityPlayerMP player, ToolboxRequestPacket request) {
        Session session = SESSIONS.get(player.getUniqueID());
        if (request.kind == ToolboxRequestPacket.Kind.END) {
            if (session != null && session.id == request.sessionId) end(player);
            return;
        }

        if ((request.kind == ToolboxRequestPacket.Kind.BEGIN || request.kind == ToolboxRequestPacket.Kind.FULL
            || request.kind == ToolboxRequestPacket.Kind.SETTINGS
            || request.kind == ToolboxRequestPacket.Kind.PICKUP_TOGGLE)
            && (player.openContainer == null || player.openContainer.windowId != request.windowId)) {
            if (request.kind == ToolboxRequestPacket.Kind.BEGIN) {
                sendStatus(
                    player,
                    ++nextSession,
                    request.requestId,
                    request.slot,
                    request.toolbox,
                    null,
                    ToolboxStatus.INVALID_CONTAINER);
            }
            return;
        }
        RequestBudget budget = BUDGETS.computeIfAbsent(player.getUniqueID(), key -> new RequestBudget());
        boolean accepted = budget.accept(player.ticksExisted);
        if (request.kind == ToolboxRequestPacket.Kind.BEGIN) {
            if (!accepted || !budget.acceptBegin(player.ticksExisted)) {
                end(player);

                sendStatus(
                    player,
                    ++nextSession,
                    request.requestId,
                    request.slot,
                    request.toolbox,
                    null,
                    ToolboxStatus.STALE);
                return;
            }
        } else if (!accepted) return;
        if (request.kind == ToolboxRequestPacket.Kind.BEGIN || request.kind == ToolboxRequestPacket.Kind.FULL) {
            begin(player, request);
            return;
        }
        if (request.kind == ToolboxRequestPacket.Kind.SETTINGS) {
            settings(player, request, session);
            return;
        }
        if (request.kind == ToolboxRequestPacket.Kind.PICKUP_TOGGLE) {
            togglePickup(player, request, session);
            return;
        }
        if (session == null || session.id != request.sessionId) return;
        ToolboxStatus status = session.validate();
        if (status != ToolboxStatus.ONLINE) {
            session.status(status);
            end(player);
            return;
        }
        if (request.kind == ToolboxRequestPacket.Kind.RESYNC) {
            if (player.ticksExisted - session.lastResync < 10) return;
            session.lastResync = player.ticksExisted;
            session.catalog.requestSnapshot();
            session.sendCatalog();
            session.sendPins();
        } else if (request.kind == ToolboxRequestPacket.Kind.ACTION) {
            session.action(request);
        } else if (request.kind == ToolboxRequestPacket.Kind.PIN_ROWS) {
            session.pinsRows(request);
        } else if (request.kind == ToolboxRequestPacket.Kind.INVENTORY_DEPOSIT) {
            session.inventoryDeposit(request);
        } else if (request.kind == ToolboxRequestPacket.Kind.INVENTORY_EXTRACT) {
            session.inventoryExtract(request);
        }
    }

    static boolean isEnabledPin(int index, PinsRows craftingRows, PinsRows playerRows) {
        return index >= 0 && index < craftingRows.getSlotCount()
            || index >= PinList.PLAYER_OFFSET && index < PinList.PLAYER_OFFSET + playerRows.getSlotCount();
    }

    static boolean isTargetRevisionValid(ToolboxRequestPacket request, long catalogRevision, long pinRevision) {

        return request.catalogRevision > 0 && request.catalogRevision <= catalogRevision
            && (request.nativePin < 0 || request.pinRevision == pinRevision);
    }

    private static boolean allowedContainer(EntityPlayerMP player) {
        return !player.isDead && player.openContainer != null
            && !(player.openContainer instanceof AEBaseContainer)
            && !(player.openContainer instanceof IPinsHandler)
            && player.openContainer.canInteractWith(player);
    }

    private static IGridNode resolveBindingNode(String binding) {
        if (binding == null || binding.isEmpty()) return null;
        try {
            ILocatable locatable = AEApi.instance()
                .registries()
                .locatable()
                .getLocatableBy(Long.parseLong(binding));
            return locatable instanceof IGridHost host ? host.getGridNode(ForgeDirection.UNKNOWN) : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static final class BogoInventory {

        static List<Slot> sources(EntityPlayerMP player, Container container, Slot source) {
            com.cleanroommc.bogosorter.api.ISlotGroup group = com.cleanroommc.bogosorter.common.sort.GuiSortingContext
                .getOrCreate(container)
                .getSlotGroup(source.slotNumber);
            if (group == null) return List.of();
            List<Slot> slots = new ArrayList<>();
            for (com.cleanroommc.bogosorter.mixins.early.minecraft.SlotAccessor entry : group.getSlots()) {
                int index = entry.getSlotNumber();
                if (index < 0 || index >= container.inventorySlots.size()) continue;
                Slot slot = container.getSlot(index);

                if (slot != source && pinned(player, container, slot)) continue;
                slots.add(slot);
            }
            return slots;
        }

        static boolean pinned(EntityPlayerMP player, Container container, Slot slot) {
            return com.cleanroommc.bogosorter.api.IBogoSortAPI.getInstance()
                .isPinned(player, container, slot);
        }
    }

    private static void togglePickup(EntityPlayerMP player, ToolboxRequestPacket request, Session session) {
        if (!allowedContainer(player) || request.slot < 0
            || ItemMEToolbox.findToolboxSlot(player, request.toolbox) != request.slot) return;
        if (session != null && session.id == request.sessionId && !session.acceptAction(request)) return;
        ItemStack stack = ItemMEToolbox.getToolboxStack(player, request.slot);
        boolean enabled = !ItemMEToolbox.isPickupToMEEnabled(stack);

        if (enabled) {
            for (int slot : ItemMEToolbox.getToolboxSlots(player)) {
                ItemStack other = ItemMEToolbox.getToolboxStack(player, slot);
                if (other != stack && ItemMEToolbox.isPickupToMEEnabled(other)) {
                    ItemMEToolbox.setPickupToMEEnabled(other, false);
                }
            }
        }
        ItemMEToolbox.setPickupToMEEnabled(stack, enabled);
        ToolboxTransferService.synchronize(player);
    }

    private static void settings(EntityPlayerMP player, ToolboxRequestPacket request, Session session) {
        if (!allowedContainer(player) || request.slot < 0
            || ItemMEToolbox.findToolboxSlot(player, request.toolbox) != request.slot) return;
        ItemStack stack = ItemMEToolbox.getToolboxStack(player, request.slot);
        if (!ItemMEToolbox.isToolbox(stack)) return;
        if (session != null && session.id == request.sessionId && !session.acceptAction(request)) return;
        IConfigManager config = ((IWirelessTermHandler) stack.getItem()).getConfigManager(stack);

        config.putSetting(Settings.SORT_BY, request.sortOrder);
        config.putSetting(Settings.SORT_DIRECTION, request.sortDirection);
        config.putSetting(Settings.VIEW_MODE, request.viewMode);
        NBTTagCompound data = new NBTTagCompound();
        config.writeToNBT(data);
        NBTTagCompound response = message("SETTINGS", request.sessionId);
        response.setString("toolbox", request.toolbox.toString());
        response.setLong("actionId", request.actionId);
        response.setTag("config", data);
        send(player, response);
        ToolboxTransferService.synchronize(player);
    }

    private static void begin(EntityPlayerMP player, ToolboxRequestPacket request) {
        end(player);
        if (!allowedContainer(player)) {
            sendStatus(
                player,
                ++nextSession,
                request.requestId,
                request.slot,
                request.toolbox,
                null,
                ToolboxStatus.INVALID_CONTAINER);
            return;
        }
        UUID preferred = request.toolbox;

        if (preferred == null && request.slot >= 0) {
            preferred = ItemMEToolbox.ensureIdentity(ItemMEToolbox.getToolboxStack(player, request.slot), player);
        }
        if (preferred == null) preferred = SELECTED.get(player.getUniqueID());
        if (preferred == null) {
            for (int candidate : ItemMEToolbox.getToolboxSlots(player)) {
                ItemStack stack = ItemMEToolbox.getToolboxStack(player, candidate);
                if (ItemMEToolbox.isToolbox(stack)) {
                    preferred = ItemMEToolbox.ensureIdentity(stack, player);
                    break;
                }
            }
        }
        long id = ++nextSession;
        int slot = ItemMEToolbox.findToolboxSlot(player, preferred);
        if (slot < 0) {
            ToolboxStatus state = Objects.equals(preferred, ItemMEToolbox.getToolboxId(player.inventory.getItemStack()))
                && preferred != null ? ToolboxStatus.ON_CURSOR : ToolboxStatus.MISSING;
            sendStatus(player, id, request.requestId, slot, preferred, null, state);
            return;
        }
        SELECTED.put(player.getUniqueID(), preferred);
        ItemStack stack = ItemMEToolbox.getToolboxStack(player, slot);
        ItemMEToolbox.initializePins(stack, player);
        if (request.kind == ToolboxRequestPacket.Kind.FULL) {
            AEApi.instance()
                .registries()
                .wireless()
                .openWirelessTerminalGui(stack, player.worldObj, player);
            return;
        }
        Session session = new Session(player, id, request.requestId, slot, preferred, stack);
        try {
            ToolboxStatus status = session.validate();
            if (status == ToolboxStatus.ONLINE) {
                session.catalog = new ToolboxCatalog(session.monitor);
                session.terminal.observeCatalog(session.catalog);
                SESSIONS.put(player.getUniqueID(), session);
            }
            session.status(status);
            session.sendPins();
            if (SESSIONS.get(player.getUniqueID()) == session) session.sendCatalog();
            ToolboxTransferService.synchronize(player);
        } finally {

            if (SESSIONS.get(player.getUniqueID()) != session) session.close();
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof EntityPlayerMP player)) return;
        Session session = SESSIONS.get(player.getUniqueID());
        if (session == null) return;
        ToolboxStatus status = session.validate();
        if (status == ToolboxStatus.ONLINE && ++session.energyTicks >= 11) {
            double drain = AEConfig.instance.wireless_getDrainRate(session.host.getRange()) * session.energyTicks;
            session.energyTicks = 0;
            if (session.host.extractAEPower(drain, Actionable.SIMULATE, PowerMultiplier.CONFIG) < drain
                || session.host.extractAEPower(drain, Actionable.MODULATE, PowerMultiplier.CONFIG) < drain) {
                status = ToolboxStatus.NO_POWER;
            }
        }
        if (status != ToolboxStatus.ONLINE) {
            session.status(status);
            end(player);
        } else if (player.ticksExisted % 2 == 0 && session.catalog.hasChanges()) {
            session.sendCatalog();
        }
    }

    @SubscribeEvent
    public void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP player) end(player);
        SELECTED.remove(event.player.getUniqueID());
        BUDGETS.remove(event.player.getUniqueID());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPickup(EntityItemPickupEvent event) {
        ToolboxTransferService.pickup(event);
    }

    private static void send(EntityPlayerMP player, NBTTagCompound data) {
        ScienceNotLeisure.network.sendTo(new ToolboxUpdatePacket(data), player);
    }

    private static NBTTagCompound message(String kind, long session) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("kind", kind);
        tag.setLong("session", session);
        return tag;
    }

    private static void sendStatus(EntityPlayerMP player, long session, long requestId, int slot, UUID toolbox,
        ItemStack stack, ToolboxStatus status) {
        NBTTagCompound tag = message("STATUS", session);
        tag.setLong("requestId", requestId);
        tag.setString("status", status.name());
        tag.setInteger("slot", slot);
        if (toolbox != null) tag.setString("toolbox", toolbox.toString());
        if (stack != null) {
            tag.setString("name", stack.getDisplayName());
            IWirelessTermHandler handler = (IWirelessTermHandler) stack.getItem();
            String key = handler.getEncryptionKey(stack);
            tag.setString("network", key == null ? "" : key);
            NBTTagCompound config = new NBTTagCompound();
            handler.getConfigManager(stack)
                .writeToNBT(config);
            tag.setTag("config", config);
        }
        send(player, tag);
    }

    private static void sendParts(EntityPlayerMP player, NBTTagCompound header, String key,
        List<NBTTagCompound> entries, boolean snapshot) {
        List<NBTTagList> parts;
        try {
            parts = ToolboxUpdatePacket.split(entries);
        } catch (IOException exception) {
            ScienceNotLeisure.LOG.warn("Cannot encode ME toolbox snapshot", exception);
            end(player);
            NBTTagCompound failed = message("STATUS", header.getLong("session"));
            failed.setString("status", ToolboxStatus.STALE.name());
            send(player, failed);
            return;
        }
        for (int index = 0; index < parts.size(); index++) {
            NBTTagCompound tag = (NBTTagCompound) header.copy();
            tag.setBoolean("snapshot", snapshot);
            tag.setBoolean("reset", snapshot && index == 0);
            tag.setBoolean("last", index == parts.size() - 1);
            tag.setInteger("part", index);
            tag.setInteger("parts", parts.size());
            tag.setTag(key, parts.get(index));
            send(player, tag);
        }
    }

    private static final class RequestBudget {

        private int tick;
        private int count;
        private int lastBegin = -10;

        boolean accept(int now) {
            if (now < tick || now - tick >= 20) {
                tick = now;
                count = 0;
            }
            return ++count <= 64;
        }

        boolean acceptBegin(int now) {
            if (now >= lastBegin && now - lastBegin < 10) return false;
            lastBegin = now;
            return true;
        }
    }

    private static final class Session {

        final EntityPlayerMP player;
        final long id;
        final long requestId;
        final int slot;
        final UUID toolbox;
        final ItemStack stack;
        final Container container;
        final int windowId;
        final String binding;
        final IGridNode node;
        final IGrid grid;
        final IWirelessTermHandler handler;
        final WirelessTerminalGuiObject host;
        final IMEMonitor<IAEItemStack> monitor;
        final ToolboxMonitorableAdapter terminal;
        NBTTagList observedPins;
        ToolboxCatalog catalog;
        long pinRevision = 1;
        long lastAction;
        int energyTicks;
        int lastResync;

        Session(EntityPlayerMP player, long id, long requestId, int slot, UUID toolbox, ItemStack stack) {
            this.player = player;
            this.id = id;
            this.requestId = requestId;
            this.slot = slot;
            this.toolbox = toolbox;
            this.stack = stack;
            this.container = player.openContainer;
            this.windowId = container.windowId;
            this.handler = (IWirelessTermHandler) stack.getItem();
            this.binding = handler.getEncryptionKey(stack);
            this.node = resolveBindingNode(binding);
            this.grid = node == null ? null : node.getGrid();
            this.host = new WirelessTerminalGuiObject(handler, stack, player, player.worldObj, slot, 0, 0);

            this.host.getTypeFilter(player)
                .put(ITEM_STACK_TYPE, true);
            this.terminal = new ToolboxMonitorableAdapter(player, host);
            this.monitor = terminal.getItemMonitor();
            this.observedPins = (NBTTagList) pinData().copy();
        }

        ToolboxStatus validate() {
            if (!allowedContainer(player) || player.openContainer != container || container.windowId != windowId)
                return ToolboxStatus.INVALID_CONTAINER;
            if (toolbox.equals(ItemMEToolbox.getToolboxId(player.inventory.getItemStack())))
                return ToolboxStatus.ON_CURSOR;
            int current = ItemMEToolbox.findToolboxSlot(player, toolbox);
            if (current < 0) return ToolboxStatus.MISSING;

            if (current != slot || ItemMEToolbox.getToolboxStack(player, current) != stack) return ToolboxStatus.STALE;
            if (!Objects.equals(binding, handler.getEncryptionKey(stack))) return ToolboxStatus.STALE;

            if (!observedPins.equals(pinData())) return ToolboxStatus.STALE;
            if (binding == null || binding.isEmpty()) return ToolboxStatus.UNBOUND;
            IGridNode liveNode = resolveBindingNode(binding);
            if (liveNode == null || grid == null) return ToolboxStatus.OFFLINE;
            if (liveNode != node || liveNode.getGrid() != grid) return ToolboxStatus.STALE;
            IStorageGrid storage = grid.getCache(IStorageGrid.class);
            if (storage == null || storage.getItemInventory() != monitor || monitor == null) return ToolboxStatus.STALE;
            IEnergyGrid energy = grid.getCache(IEnergyGrid.class);
            if (energy == null || !energy.isNetworkPowered()) return ToolboxStatus.OFFLINE;

            if (!host.rangeCheck()) return ToolboxStatus.OUT_OF_RANGE;
            IGridNode actionableNode = host.getActionableNode();
            if (actionableNode == null || actionableNode.getGrid() != grid) return ToolboxStatus.OUT_OF_RANGE;
            if (host.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.CONFIG) < 1) return ToolboxStatus.NO_POWER;
            return permission(SecurityPermissions.EXTRACT) ? ToolboxStatus.ONLINE : ToolboxStatus.NO_PERMISSION;
        }

        boolean permission(SecurityPermissions permission) {
            ISecurityGrid security = grid == null ? null : grid.getCache(ISecurityGrid.class);
            return security != null && security.hasPermission(player, permission);
        }

        void status(ToolboxStatus status) {
            sendStatus(player, id, requestId, slot, toolbox, stack, status);
        }

        private NBTTagList pinData() {
            return stack.hasTagCompound() ? stack.getTagCompound()
                .getTagList("pins", 10) : new NBTTagList();
        }

        private boolean operationValid(SecurityPermissions permission) {
            return SESSIONS.get(player.getUniqueID()) == this && validate() == ToolboxStatus.ONLINE
                && container.isPlayerNotUsingContainer(player)
                && permission(permission);
        }

        private void recordPinChanges() {
            if (!terminal.consumePinsChanged()) return;
            observedPins = (NBTTagList) pinData().copy();
            pinRevision++;
        }

        void sendCatalog() {
            boolean snapshot = catalog.needsSnapshot();
            long base = catalog.revision;
            List<NBTTagCompound> entries = catalog.collect();
            NBTTagCompound header = message("CATALOG", id);
            header.setLong("baseRevision", base);
            header.setLong("revision", catalog.revision);
            sendParts(player, header, "entries", entries, snapshot);
        }

        void sendPins() {
            List<NBTTagCompound> entries = new ArrayList<>();
            for (int index = 0; index < PinList.TOTAL_SLOTS; index++) {
                IAEStack<?> pin = terminal.getPin(index);
                if (pin == null) continue;
                NBTTagCompound entry = new NBTTagCompound();
                entry.setInteger("index", index);
                entry.setTag(
                    "stack",
                    pin.copy()
                        .toNBTGeneric());
                entries.add(entry);
            }
            NBTTagCompound header = message("PINS", id);
            header.setLong("revision", pinRevision);
            header.setInteger(
                "craftingRows",
                terminal.getCraftingPinsRows()
                    .ordinal());
            header.setInteger(
                "playerRows",
                terminal.getPlayerPinsRows()
                    .ordinal());
            sendParts(player, header, "slots", entries, true);
        }

        boolean acceptAction(ToolboxRequestPacket request) {
            if (request.actionId <= lastAction) return false;
            lastAction = request.actionId;
            return true;
        }

        void pinsRows(ToolboxRequestPacket request) {
            if (!acceptAction(request)) return;
            if (request.pinRevision != pinRevision) {
                sendPins();
                return;
            }
            try {
                terminal.setPinsRows(
                    request.craftingRows,
                    request.playerRows,
                    () -> operationValid(SecurityPermissions.EXTRACT));
            } finally {
                recordPinChanges();
                ToolboxTransferService.synchronize(player);
            }
            sendPins();
        }

        void inventoryDeposit(ToolboxRequestPacket request) {
            if (!acceptAction(request) || request.slot < 0
                || request.slot >= 36
                || request.slot == slot
                || !permission(SecurityPermissions.INJECT)) return;
            ItemStack offered = player.inventory.mainInventory[request.slot];

            if (offered != null && offered.getItem() instanceof ItemMEToolbox) return;
            if (request.depositMode != DepositMode.NATIVE_REGION && (offered == null || offered.stackSize <= 0)) return;
            Slot source = container.getSlotFromInventory(player.inventory, request.slot);
            if (source == null || !source.canTakeStack(player)) return;

            List<Slot> sources = switch (request.depositMode) {
                case STACK, ONE -> List.of(source);
                case MATCHING, REGION -> Platform.isBogoLoaded ? BogoInventory.sources(player, container, source)
                    : List.of();
                case NATIVE_REGION -> nativeRegionSources(source);
            };
            ItemStack selected = offered == null ? null : offered.copy();
            BooleanSupplier valid = () -> operationValid(SecurityPermissions.INJECT);
            try {
                for (Slot candidate : sources) {
                    if (!valid.getAsBoolean()) return;
                    if (candidate.inventory != player.inventory || candidate.getSlotIndex() < 0
                        || candidate.getSlotIndex() >= 36
                        || !candidate.canTakeStack(player)) continue;
                    ItemStack stackInSlot = candidate.getStack();
                    if (stackInSlot == null || stackInSlot.stackSize <= 0
                        || stackInSlot.getItem() instanceof ItemMEToolbox) continue;

                    if (request.depositMode == DepositMode.MATCHING && !stackInSlot.isItemEqual(selected)) continue;
                    ToolboxTransferService.depositInventory(
                        player,
                        terminal,
                        candidate,
                        request.depositMode == DepositMode.ONE ? 1 : Integer.MAX_VALUE,
                        valid);
                }
            } finally {

                ToolboxTransferService.synchronize(player);
            }
        }

        private List<Slot> nativeRegionSources(Slot source) {
            List<Slot> slots = new ArrayList<>();

            for (Object entry : container.inventorySlots) {
                Slot candidate = (Slot) entry;
                if (candidate.getClass() != source.getClass()) continue;
                if (Platform.isBogoLoaded && BogoInventory.pinned(player, container, candidate)) continue;
                slots.add(candidate);
            }
            return slots;
        }

        void inventoryExtract(ToolboxRequestPacket request) {
            if (!acceptAction(request) || !permission(SecurityPermissions.EXTRACT)) return;
            if (!isTargetRevisionValid(request, catalog.revision, pinRevision)) {
                catalog.requestSnapshot();
                sendCatalog();
                sendPins();
                return;
            }
            if (request.nativePin >= 0
                && !isEnabledPin(request.nativePin, terminal.getCraftingPinsRows(), terminal.getPlayerPinsRows()))
                return;
            IAEStack<?> selected = request.nativePin >= 0 ? terminal.getPin(request.nativePin)
                : catalog.target(request.entryId);
            if (!(selected instanceof IAEItemStack target)) return;
            List<Integer> destinations = new ArrayList<>();

            for (Object entry : player.inventoryContainer.inventorySlots) {
                Slot playerSlot = (Slot) entry;
                if (playerSlot.inventory != player.inventory || playerSlot.getSlotIndex() < 0
                    || playerSlot.getSlotIndex() >= 36) continue;
                Slot hostSlot = container.getSlotFromInventory(player.inventory, playerSlot.getSlotIndex());
                Slot destination = hostSlot == null ? playerSlot : hostSlot;
                if (!destination.isItemValid(target.getItemStack())) continue;
                Container destinationContainer = hostSlot == null ? player.inventoryContainer : container;
                if (Platform.isBogoLoaded && BogoInventory.pinned(player, destinationContainer, destination)) continue;
                destinations.add(destination.getSlotIndex());
            }
            ToolboxTransferService.extractInventory(
                player,
                terminal,
                target,
                destinations.stream()
                    .mapToInt(Integer::intValue)
                    .distinct()
                    .toArray(),
                request.emptyOnly,
                request.single,
                () -> operationValid(SecurityPermissions.EXTRACT));
        }

        void action(ToolboxRequestPacket request) {
            if (!acceptAction(request)) return;
            if (!isTargetRevisionValid(request, catalog.revision, pinRevision)) {
                catalog.requestSnapshot();
                sendCatalog();
                sendPins();
                return;
            }
            ItemStack hand = player.inventory.getItemStack();
            int nativePin = request.nativePin;
            if (nativePin >= 0
                && !isEnabledPin(nativePin, terminal.getCraftingPinsRows(), terminal.getPlayerPinsRows())) return;
            IAEStack<?> pin = nativePin < 0 ? null : terminal.getPin(nativePin);

            if (pin != null && !(pin instanceof IAEItemStack)) return;
            if (request.action == MonitorableAction.SET_PIN || request.action == MonitorableAction.UNSET_PIN) {
                if (nativePin < 0) return;
                if (request.action == MonitorableAction.UNSET_PIN) {
                    if (hand != null) return;
                } else {
                    if (nativePin < PinList.PLAYER_OFFSET || hand == null
                        || (pin instanceof IAEItemStack item && item.getItemStack()
                            .isItemEqual(hand)))
                        return;
                }
                try {
                    terminal.pin(
                        request.action == MonitorableAction.UNSET_PIN ? null : AEItemStack.create(hand),
                        nativePin,
                        () -> operationValid(SecurityPermissions.EXTRACT));
                } finally {
                    recordPinChanges();
                    ToolboxTransferService.synchronize(player);
                }
                sendPins();
                return;
            }
            IAEItemStack target = nativePin >= 0 ? (pin instanceof IAEItemStack item ? item.copy() : null)
                : catalog.target(request.entryId);
            if (request.action == MonitorableAction.AUTO_CRAFT) {
                if (target == null || hand != null || !permission(SecurityPermissions.CRAFT)) return;
                IAEItemStack live = monitor.getStorageList()
                    .findPrecise(target);
                if (live == null || !live.isCraftable()) return;
                openCrafting(live);
                return;
            }
            switch (request.action) {
                case PICKUP_OR_SET_DOWN, SPLIT_OR_PLACE_SINGLE, SHIFT_CLICK, PICKUP_SINGLE, ROLL_UP, ROLL_DOWN, MOVE_REGION -> {}
                default -> {
                    return;
                }
            }
            boolean inject = ToolboxTransferService.depositsCursor(hand, request.action);
            if (inject && !permission(SecurityPermissions.INJECT)) return;
            if (target == null && !inject) return;
            ToolboxTransferService.execute(
                player,
                terminal,
                target,
                request.action,
                () -> operationValid(inject ? SecurityPermissions.INJECT : SecurityPermissions.EXTRACT));
        }

        private void openCrafting(IAEItemStack target) {
            if (target == null || !allowedContainer(player) || ItemMEToolbox.findToolboxSlot(player, toolbox) != slot)
                return;
            ItemStack current = ItemMEToolbox.getToolboxStack(player, slot);
            if (current != stack) return;

            PrimaryGui primary = new PrimaryGui(null, current.copy(), null, ForgeDirection.UNKNOWN) {

                private boolean returned;

                @Override
                public void open(EntityPlayer owner) {
                    if (!(owner instanceof EntityPlayerMP serverPlayer) || returned
                        || !(serverPlayer.openContainer instanceof AEBaseContainer container)
                        || !(container instanceof IContainerSubGui)
                        || container.getInventoryPlayer().player != serverPlayer
                        || container.getPrimaryGui() != this) return;
                    returned = true;

                    serverPlayer.closeContainer();
                    returnToInventory(serverPlayer);
                }
            };
            IAEItemStack requested = target.copy();
            requested.setStackSize(1);

            end(player);
            try {
                Platform.openGUI(player, null, ForgeDirection.UNKNOWN, GuiBridge.GUI_CRAFTING_AMOUNT, slot);
                if (player.openContainer instanceof ContainerCraftAmount amount) {
                    amount.setPrimaryGui(primary);
                    amount.setItemToCraft(requested);
                    amount.setInitialCraftAmount(1);
                    amount.detectAndSendChanges();
                    return;
                }
            } catch (RuntimeException exception) {
                ScienceNotLeisure.LOG
                    .error("Failed to open ME toolbox crafting for {}", player.getCommandSenderName(), exception);
            }

            player.closeContainer();
            returnToInventory(player);
        }

        private void returnToInventory(EntityPlayerMP returned) {
            NBTTagCompound tag = message("RETURN", id);
            tag.setString("toolbox", toolbox.toString());
            tag.setInteger("slot", slot);
            send(returned, tag);
        }

        void close() {
            try {
                if (catalog != null) catalog.close();
            } finally {
                terminal.close();
            }
        }
    }
}
