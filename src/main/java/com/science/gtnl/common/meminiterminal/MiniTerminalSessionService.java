package com.science.gtnl.common.meminiterminal;

import static appeng.util.item.AEFluidStackType.FLUID_STACK_TYPE;
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
import com.science.gtnl.common.item.items.ItemMEMiniPortableTerminal;
import com.science.gtnl.common.meminiterminal.MiniTerminalRequestPacket.DepositMode;
import com.science.gtnl.utils.enums.ModList;

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
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
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
import appeng.util.AEStackTypeFilter;
import appeng.util.Platform;
import appeng.util.TerminalSettings;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;

public final class MiniTerminalSessionService {

    enum MiniTerminalStatus {
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

    public static final MiniTerminalSessionService INSTANCE = new MiniTerminalSessionService();
    private static final Map<UUID, Session> SESSIONS = new HashMap<>();
    private static final Map<UUID, UUID> SELECTED = new HashMap<>();
    private static final Map<UUID, RequestBudget> BUDGETS = new HashMap<>();
    private static long nextSession;

    private MiniTerminalSessionService() {}

    /** Returns the real main-inventory index, including wrapped slots, or -1 for other inventories. */
    public static int playerInventoryIndex(EntityPlayer player, Slot slot) {
        if (player == null || slot == null) return -1;
        int inventorySize = Math.min(36, player.inventory.mainInventory.length);
        int slotIndex = slot.getSlotIndex();
        if (slotIndex >= 0 && slotIndex < inventorySize && slot.isSlotInInventory(player.inventory, slotIndex))
            return slotIndex;
        for (int index = 0; index < inventorySize; index++) {
            if (slot.isSlotInInventory(player.inventory, index)) return index;
        }
        return -1;
    }

    public static void end(EntityPlayerMP player) {
        Session session = SESSIONS.remove(player.getUniqueID());
        if (session != null) session.close();
    }

    static void handle(EntityPlayerMP player, MiniTerminalRequestPacket request) {
        Session session = SESSIONS.get(player.getUniqueID());
        if (request.kind == MiniTerminalRequestPacket.Kind.END) {
            if (session != null && session.id == request.sessionId) end(player);
            return;
        }

        if ((request.kind == MiniTerminalRequestPacket.Kind.BEGIN || request.kind == MiniTerminalRequestPacket.Kind.FULL
            || request.kind == MiniTerminalRequestPacket.Kind.SETTINGS
            || request.kind == MiniTerminalRequestPacket.Kind.PICKUP_TOGGLE)
            && (player.openContainer == null || player.openContainer.windowId != request.windowId)) {
            if (request.kind == MiniTerminalRequestPacket.Kind.BEGIN) {
                sendStatus(
                    player,
                    ++nextSession,
                    request.requestId,
                    request.slot,
                    request.terminalId,
                    null,
                    MiniTerminalStatus.INVALID_CONTAINER);
            }
            return;
        }
        RequestBudget budget = BUDGETS.computeIfAbsent(player.getUniqueID(), key -> new RequestBudget());
        boolean accepted = budget.accept(player.ticksExisted);
        if (request.kind == MiniTerminalRequestPacket.Kind.BEGIN) {
            if (!accepted || !budget.acceptBegin(player.ticksExisted)) {
                end(player);

                sendStatus(
                    player,
                    ++nextSession,
                    request.requestId,
                    request.slot,
                    request.terminalId,
                    null,
                    MiniTerminalStatus.STALE);
                return;
            }
        } else if (!accepted) return;
        if (request.kind == MiniTerminalRequestPacket.Kind.BEGIN
            || request.kind == MiniTerminalRequestPacket.Kind.FULL) {
            begin(player, request);
            return;
        }
        if (request.kind == MiniTerminalRequestPacket.Kind.SETTINGS) {
            settings(player, request, session);
            return;
        }
        if (request.kind == MiniTerminalRequestPacket.Kind.PICKUP_TOGGLE) {
            togglePickup(player, request, session);
            return;
        }
        if (session == null || session.id != request.sessionId) return;
        MiniTerminalStatus status = session.validate();
        if (status != MiniTerminalStatus.ONLINE) {
            session.status(status);
            end(player);
            return;
        }
        if (request.kind == MiniTerminalRequestPacket.Kind.RESYNC) {
            session.resync();
        } else if (request.kind == MiniTerminalRequestPacket.Kind.ACTION) {
            session.action(request);
        } else if (request.kind == MiniTerminalRequestPacket.Kind.PIN_ROWS) {
            session.pinsRows(request);
        } else if (request.kind == MiniTerminalRequestPacket.Kind.PIN_DROP) {
            session.pinDrop(request);
        } else if (request.kind == MiniTerminalRequestPacket.Kind.INVENTORY_DEPOSIT) {
            session.inventoryDeposit(request);
        } else if (request.kind == MiniTerminalRequestPacket.Kind.INVENTORY_EXTRACT) {
            session.inventoryExtract(request);
        }
    }

    static boolean isEnabledPin(int index, PinsRows craftingRows, PinsRows playerRows) {
        return index >= 0 && index < craftingRows.getSlotCount()
            || index >= PinList.PLAYER_OFFSET && index < PinList.PLAYER_OFFSET + playerRows.getSlotCount();
    }

    static boolean isTargetRevisionValid(MiniTerminalRequestPacket request, long catalogRevision, long pinRevision) {

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

    /** Network, range, battery and security checks shared by sessions and pickup insertion. */
    static MiniTerminalStatus networkStatus(EntityPlayer player, IGrid grid, WirelessTerminalGuiObject host,
        SecurityPermissions permission) {
        if (grid == null) return MiniTerminalStatus.OFFLINE;
        IEnergyGrid energy = grid.getCache(IEnergyGrid.class);
        if (energy == null || !energy.isNetworkPowered()) return MiniTerminalStatus.OFFLINE;
        if (!host.rangeCheck()) return MiniTerminalStatus.OUT_OF_RANGE;
        IGridNode actionableNode = host.getActionableNode();
        if (actionableNode == null || actionableNode.getGrid() != grid) return MiniTerminalStatus.OUT_OF_RANGE;
        if (host.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.CONFIG) < 1) return MiniTerminalStatus.NO_POWER;
        ISecurityGrid security = grid.getCache(ISecurityGrid.class);
        return security != null && security.hasPermission(player, permission) ? MiniTerminalStatus.ONLINE
            : MiniTerminalStatus.NO_PERMISSION;
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

    private static void togglePickup(EntityPlayerMP player, MiniTerminalRequestPacket request, Session session) {
        if (!allowedContainer(player) || request.slot < 0
            || ItemMEMiniPortableTerminal.findTerminalSlot(player, request.terminalId) != request.slot) return;
        if (session != null && session.id == request.sessionId && !session.acceptAction(request)) return;
        ItemStack stack = ItemMEMiniPortableTerminal.getTerminalStack(player, request.slot);
        boolean enabled = !ItemMEMiniPortableTerminal.isPickupToMEEnabled(stack);

        if (enabled) {
            for (int slot : ItemMEMiniPortableTerminal.getTerminalSlots(player)) {
                ItemStack other = ItemMEMiniPortableTerminal.getTerminalStack(player, slot);
                if (other != stack && ItemMEMiniPortableTerminal.isPickupToMEEnabled(other)) {
                    ItemMEMiniPortableTerminal.setPickupToMEEnabled(other, false);
                }
            }
        }
        ItemMEMiniPortableTerminal.setPickupToMEEnabled(stack, enabled);
        MiniTerminalTransferService.synchronize(player);
    }

    private static void settings(EntityPlayerMP player, MiniTerminalRequestPacket request, Session session) {
        if (!allowedContainer(player) || request.slot < 0
            || ItemMEMiniPortableTerminal.findTerminalSlot(player, request.terminalId) != request.slot) return;
        ItemStack stack = ItemMEMiniPortableTerminal.getTerminalStack(player, request.slot);
        if (!ItemMEMiniPortableTerminal.isMiniTerminal(stack)) return;
        if (session != null && session.id == request.sessionId && !session.acceptAction(request)) return;
        IConfigManager config = ((IWirelessTermHandler) stack.getItem()).getConfigManager(stack);

        config.putSetting(Settings.SORT_BY, request.sortOrder);
        config.putSetting(Settings.SORT_DIRECTION, request.sortDirection);
        config.putSetting(Settings.VIEW_MODE, request.viewMode);
        if (session != null && session.stack == stack) {
            Reference2BooleanMap<IAEStackType<?>> filters = new Reference2BooleanOpenHashMap<>();
            filters.put(ITEM_STACK_TYPE, true);
            filters.put(FLUID_STACK_TYPE, request.showFluids);
            // AE writes the host's live TerminalSettings back to the stack, so the reply below reads it.
            session.terminal.updateTypeFilters(filters);
            if (session.catalog != null) session.catalog.showFluids(request.showFluids);
        } else {
            TerminalSettings terminalSettings = new TerminalSettings();
            terminalSettings.readFromNBT(stack.getTagCompound());
            AEStackTypeFilter filters = terminalSettings.getFilters(player);
            filters.setEnabled(ITEM_STACK_TYPE, true);
            filters.setEnabled(FLUID_STACK_TYPE, request.showFluids);
            terminalSettings.writeToNBT(stack);
        }
        NBTTagCompound data = new NBTTagCompound();
        config.writeToNBT(data);
        readTypeFilters(stack, player).writeToNBT(data);
        NBTTagCompound response = message("SETTINGS", request.sessionId);
        response.setString(MiniTerminalUpdatePacket.TERMINAL_ID_KEY, request.terminalId.toString());
        response.setLong("actionId", request.actionId);
        response.setTag("config", data);
        send(player, response);
        MiniTerminalTransferService.synchronize(player);
    }

    private static void begin(EntityPlayerMP player, MiniTerminalRequestPacket request) {
        end(player);
        if (!allowedContainer(player)) {
            sendStatus(
                player,
                ++nextSession,
                request.requestId,
                request.slot,
                request.terminalId,
                null,
                MiniTerminalStatus.INVALID_CONTAINER);
            return;
        }
        UUID preferred = request.terminalId;

        if (preferred == null && request.slot >= 0) {
            preferred = ItemMEMiniPortableTerminal
                .ensureIdentity(ItemMEMiniPortableTerminal.getTerminalStack(player, request.slot), player);
        }
        if (preferred == null) preferred = SELECTED.get(player.getUniqueID());
        if (preferred == null) {
            int candidate = ItemMEMiniPortableTerminal.findFirstTerminalSlot(player);
            if (candidate >= 0) preferred = ItemMEMiniPortableTerminal
                .ensureIdentity(ItemMEMiniPortableTerminal.getTerminalStack(player, candidate), player);
        }
        long id = ++nextSession;
        int slot = ItemMEMiniPortableTerminal.findTerminalSlot(player, preferred);
        if (slot < 0) {
            MiniTerminalStatus state = preferred != null
                && preferred.equals(ItemMEMiniPortableTerminal.getTerminalId(player.inventory.getItemStack()))
                    ? MiniTerminalStatus.ON_CURSOR
                    : ItemMEMiniPortableTerminal.isTerminalDuplicated(player, preferred) ? MiniTerminalStatus.AMBIGUOUS
                        : MiniTerminalStatus.MISSING;
            sendStatus(player, id, request.requestId, slot, preferred, null, state);
            return;
        }
        SELECTED.put(player.getUniqueID(), preferred);
        ItemStack stack = ItemMEMiniPortableTerminal.getTerminalStack(player, slot);
        ItemMEMiniPortableTerminal.initializePins(stack, player);
        if (request.kind == MiniTerminalRequestPacket.Kind.FULL) {
            AEApi.instance()
                .registries()
                .wireless()
                .openWirelessTerminalGui(stack, player.worldObj, player);
            return;
        }
        Session session = new Session(player, id, request.requestId, slot, preferred, stack);
        try {
            MiniTerminalStatus status = session.validate();
            if (status == MiniTerminalStatus.ONLINE) {
                session.catalog = new MiniTerminalCatalog(
                    session.monitor,
                    session.fluidMonitor,
                    session.host.getTypeFilter(player)
                        .getBoolean(FLUID_STACK_TYPE));
                session.terminal.observeCatalog(session.catalog);
                SESSIONS.put(player.getUniqueID(), session);
            }
            session.status(status);
            session.sendPins();
            if (SESSIONS.get(player.getUniqueID()) == session) session.sendCatalog();
            MiniTerminalTransferService.synchronize(player);
        } finally {

            if (SESSIONS.get(player.getUniqueID()) != session) session.close();
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof EntityPlayerMP player)) return;
        Session session = SESSIONS.get(player.getUniqueID());
        if (session == null) return;
        MiniTerminalStatus status = session.validate();
        if (status == MiniTerminalStatus.ONLINE && ++session.energyTicks >= 11) {
            double drain = AEConfig.instance.wireless_getDrainRate(session.host.getRange()) * session.energyTicks;
            session.energyTicks = 0;
            if (session.host.extractAEPower(drain, Actionable.SIMULATE, PowerMultiplier.CONFIG) < drain
                || session.host.extractAEPower(drain, Actionable.MODULATE, PowerMultiplier.CONFIG) < drain) {
                status = MiniTerminalStatus.NO_POWER;
            }
        }
        if (status != MiniTerminalStatus.ONLINE) {
            session.status(status);
            end(player);
        } else {
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
        MiniTerminalTransferService.pickup(event);
    }

    private static void send(EntityPlayerMP player, NBTTagCompound data) {
        ScienceNotLeisure.network.sendTo(new MiniTerminalUpdatePacket(data), player);
    }

    private static NBTTagCompound message(String kind, long session) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("kind", kind);
        tag.setLong("session", session);
        return tag;
    }

    private static void sendStatus(EntityPlayerMP player, long session, long requestId, int slot, UUID terminalId,
        ItemStack stack, MiniTerminalStatus status) {
        NBTTagCompound tag = message("STATUS", session);
        tag.setLong("requestId", requestId);
        tag.setString("status", status.name());
        tag.setInteger("slot", slot);
        if (terminalId != null) tag.setString(MiniTerminalUpdatePacket.TERMINAL_ID_KEY, terminalId.toString());
        if (stack != null) {
            tag.setString("name", stack.getDisplayName());
            IWirelessTermHandler handler = (IWirelessTermHandler) stack.getItem();
            String key = handler.getEncryptionKey(stack);
            tag.setString("network", key == null ? "" : key);
            NBTTagCompound config = new NBTTagCompound();
            handler.getConfigManager(stack)
                .writeToNBT(config);
            readTypeFilters(stack, player).writeToNBT(config);
            tag.setTag("config", config);
        }
        send(player, tag);
    }

    private static AEStackTypeFilter readTypeFilters(ItemStack stack, EntityPlayer player) {
        TerminalSettings settings = new TerminalSettings();
        settings.readFromNBT(stack.getTagCompound());
        return settings.getFilters(player);
    }

    private static void sendParts(EntityPlayerMP player, NBTTagCompound header,
        List<MiniTerminalUpdatePacket.Entry> entries, boolean snapshot) {
        List<byte[]> parts;
        try {
            parts = MiniTerminalUpdatePacket.encode(entries);
        } catch (IOException | RuntimeException exception) {
            failUpdate(player, header.getLong("session"), exception);
            return;
        }
        for (int index = 0; index < parts.size(); index++) {
            NBTTagCompound tag = (NBTTagCompound) header.copy();
            tag.setBoolean("snapshot", snapshot);
            tag.setBoolean("reset", snapshot && index == 0);
            tag.setBoolean("last", index == parts.size() - 1);
            tag.setInteger("part", index);
            tag.setInteger("parts", parts.size());
            ScienceNotLeisure.network.sendTo(new MiniTerminalUpdatePacket(tag, parts.get(index)), player);
        }
    }

    private static void failUpdate(EntityPlayerMP player, long session, Exception exception) {
        ScienceNotLeisure.LOG.warn("Cannot encode ME mini portable terminal update", exception);
        end(player);
        NBTTagCompound failed = message("STATUS", session);
        failed.setString("status", MiniTerminalStatus.STALE.name());
        failed.setInteger("retryTicks", 200);
        send(player, failed);
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
        final UUID terminalId;
        final ItemStack stack;
        final Container container;
        final int windowId;
        final String binding;
        final IGridNode node;
        final IGrid grid;
        final IWirelessTermHandler handler;
        final WirelessTerminalGuiObject host;
        final IMEMonitor<IAEItemStack> monitor;
        final IMEMonitor<IAEFluidStack> fluidMonitor;
        final MiniTerminalMonitorableAdapter terminal;
        NBTTagList observedPins;
        MiniTerminalCatalog catalog;
        long pinRevision = 1;
        long lastAction;
        int energyTicks;
        int lastResync;

        Session(EntityPlayerMP player, long id, long requestId, int slot, UUID terminalId, ItemStack stack) {
            this.player = player;
            this.id = id;
            this.requestId = requestId;
            this.slot = slot;
            this.terminalId = terminalId;
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
            this.terminal = new MiniTerminalMonitorableAdapter(player, host);
            this.monitor = terminal.getItemMonitor();
            this.fluidMonitor = host.getFluidInventory();
            this.observedPins = (NBTTagList) pinData().copy();
        }

        MiniTerminalStatus validate() {
            if (!allowedContainer(player) || player.openContainer != container || container.windowId != windowId)
                return MiniTerminalStatus.INVALID_CONTAINER;
            if (terminalId.equals(ItemMEMiniPortableTerminal.getTerminalId(player.inventory.getItemStack())))
                return MiniTerminalStatus.ON_CURSOR;
            int current = ItemMEMiniPortableTerminal.findTerminalSlot(player, terminalId);
            if (current < 0) return ItemMEMiniPortableTerminal.isTerminalDuplicated(player, terminalId)
                ? MiniTerminalStatus.AMBIGUOUS
                : MiniTerminalStatus.MISSING;

            if (current != slot || ItemMEMiniPortableTerminal.getTerminalStack(player, current) != stack)
                return MiniTerminalStatus.STALE;
            if (!Objects.equals(binding, handler.getEncryptionKey(stack))) return MiniTerminalStatus.STALE;

            if (!observedPins.equals(pinData())) return MiniTerminalStatus.STALE;
            if (binding == null || binding.isEmpty()) return MiniTerminalStatus.UNBOUND;
            IGridNode liveNode = resolveBindingNode(binding);
            if (liveNode == null || grid == null) return MiniTerminalStatus.OFFLINE;
            if (liveNode != node || liveNode.getGrid() != grid) return MiniTerminalStatus.STALE;
            IStorageGrid storage = grid.getCache(IStorageGrid.class);
            if (storage == null || storage.getItemInventory() != monitor || monitor == null)
                return MiniTerminalStatus.STALE;
            if (storage.getFluidInventory() != fluidMonitor) return MiniTerminalStatus.STALE;
            return networkStatus(player, grid, host, SecurityPermissions.EXTRACT);
        }

        boolean permission(SecurityPermissions permission) {
            ISecurityGrid security = grid == null ? null : grid.getCache(ISecurityGrid.class);
            return security != null && security.hasPermission(player, permission);
        }

        void status(MiniTerminalStatus status) {
            sendStatus(player, id, requestId, slot, terminalId, stack, status);
        }

        private NBTTagList pinData() {
            return stack.hasTagCompound() ? stack.getTagCompound()
                .getTagList("pins", 10) : new NBTTagList();
        }

        private boolean operationValid(SecurityPermissions permission) {
            return SESSIONS.get(player.getUniqueID()) == this && validate() == MiniTerminalStatus.ONLINE
                && container.isPlayerNotUsingContainer(player)
                && permission(permission);
        }

        private void recordPinChanges() {
            if (!terminal.consumePinsChanged()) return;
            observedPins = (NBTTagList) pinData().copy();
            pinRevision++;
        }

        void sendCatalog() {
            if (!catalog.canCollect(player.ticksExisted)) return;
            boolean snapshot = catalog.needsSnapshot();
            long base = catalog.revision;
            List<MiniTerminalUpdatePacket.Entry> entries;
            try {
                entries = catalog.collect(player.ticksExisted);
            } catch (IOException exception) {
                failUpdate(player, id, exception);
                return;
            }
            if (!snapshot && entries.isEmpty()) return;
            NBTTagCompound header = message("CATALOG", id);
            header.setLong("baseRevision", base);
            header.setLong("revision", catalog.revision);
            sendParts(player, header, entries, snapshot);
        }

        void resync() {
            catalog.requestSnapshot();
            int tick = player.ticksExisted;
            if (tick >= lastResync && tick - lastResync < 20) return;
            lastResync = tick;
            sendCatalog();
            if (SESSIONS.get(player.getUniqueID()) == this) sendPins();
        }

        void sendPins() {
            List<MiniTerminalUpdatePacket.Entry> entries = new ArrayList<>();
            for (int index = 0; index < PinList.TOTAL_SLOTS; index++) {
                IAEStack<?> pin = terminal.getPin(index);
                if (pin != null) entries.add(new MiniTerminalUpdatePacket.Entry(index, pin.copy()));
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
            sendParts(player, header, entries, true);
        }

        boolean acceptAction(MiniTerminalRequestPacket request) {
            if (request.actionId <= lastAction) return false;
            lastAction = request.actionId;
            return true;
        }

        void pinsRows(MiniTerminalRequestPacket request) {
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
                MiniTerminalTransferService.synchronize(player);
            }
            sendPins();
        }

        void pinDrop(MiniTerminalRequestPacket request) {
            if (!acceptAction(request) || !permission(SecurityPermissions.EXTRACT)) return;
            if (request.pinRevision != pinRevision) {
                sendPins();
                return;
            }
            int nativePin = request.nativePin;
            if (nativePin < PinList.PLAYER_OFFSET || nativePin >= PinList.PLAYER_OFFSET + terminal.getPlayerPinsRows()
                .getSlotCount()) return;
            IAEStack<?> offered = request.pinTarget.copy()
                .reset();
            IAEStack<?> previous = terminal.getPin(nativePin);
            if (previous != null && previous.isSameType((Object) offered)) return;
            try {
                terminal.pin(offered, nativePin, () -> operationValid(SecurityPermissions.EXTRACT));
            } finally {
                recordPinChanges();
                MiniTerminalTransferService.synchronize(player);
            }
            sendPins();
        }

        void inventoryDeposit(MiniTerminalRequestPacket request) {
            if (!acceptAction(request) || request.slot < 0
                || request.slot >= 36
                || request.slot == slot
                || !permission(SecurityPermissions.INJECT)) return;
            ItemStack offered = player.inventory.mainInventory[request.slot];

            if (offered != null && offered.getItem() instanceof ItemMEMiniPortableTerminal) return;
            if (request.depositMode != DepositMode.NATIVE_REGION && (offered == null || offered.stackSize <= 0)) return;
            Slot source = container.getSlotFromInventory(player.inventory, request.slot);
            if (source == null || !source.canTakeStack(player)) return;

            List<Slot> sources = switch (request.depositMode) {
                case STACK, ONE -> List.of(source);
                case MATCHING, REGION -> ModList.InventoryBogoSorter.isModLoaded()
                    ? BogoInventory.sources(player, container, source)
                    : List.of();
                case NATIVE_REGION -> nativeRegionSources(source);
            };
            ItemStack selected = offered == null ? null : offered.copy();
            BooleanSupplier valid = () -> operationValid(SecurityPermissions.INJECT);
            try {
                for (Slot candidate : sources) {
                    if (!valid.getAsBoolean()) return;
                    int inventorySlot = playerInventoryIndex(player, candidate);
                    if (inventorySlot < 0 || !candidate.canTakeStack(player)) continue;
                    ItemStack stackInSlot = player.inventory.mainInventory[inventorySlot];
                    if (stackInSlot == null || stackInSlot.stackSize <= 0
                        || stackInSlot.getItem() instanceof ItemMEMiniPortableTerminal) continue;

                    if (request.depositMode == DepositMode.MATCHING && !stackInSlot.isItemEqual(selected)) continue;
                    MiniTerminalTransferService.depositInventory(
                        player,
                        terminal,
                        candidate,
                        request.depositMode == DepositMode.ONE ? 1 : Integer.MAX_VALUE,
                        valid);
                }
            } finally {

                MiniTerminalTransferService.synchronize(player);
            }
        }

        private List<Slot> nativeRegionSources(Slot source) {
            List<Slot> slots = new ArrayList<>();

            for (Object entry : container.inventorySlots) {
                Slot candidate = (Slot) entry;
                if (candidate.getClass() != source.getClass()) continue;
                if (ModList.InventoryBogoSorter.isModLoaded() && BogoInventory.pinned(player, container, candidate))
                    continue;
                slots.add(candidate);
            }
            return slots;
        }

        void inventoryExtract(MiniTerminalRequestPacket request) {
            if (!acceptAction(request) || !permission(SecurityPermissions.EXTRACT)) return;
            if (!isTargetRevisionValid(request, catalog.revision, pinRevision)) {
                resync();
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
                int inventorySlot = playerInventoryIndex(player, playerSlot);
                if (inventorySlot < 0) continue;
                Slot hostSlot = container.getSlotFromInventory(player.inventory, inventorySlot);
                Slot destination = hostSlot == null ? playerSlot : hostSlot;
                if (!destination.isItemValid(target.getItemStack())) continue;
                Container destinationContainer = hostSlot == null ? player.inventoryContainer : container;
                if (ModList.InventoryBogoSorter.isModLoaded()
                    && BogoInventory.pinned(player, destinationContainer, destination)) continue;
                destinations.add(inventorySlot);
            }
            MiniTerminalTransferService.extractInventory(
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

        void action(MiniTerminalRequestPacket request) {
            if (!acceptAction(request)) return;
            if (request.action == MonitorableAction.SET_PIN) return;
            if (!isTargetRevisionValid(request, catalog.revision, pinRevision)) {
                resync();
                return;
            }
            ItemStack hand = player.inventory.getItemStack();
            int nativePin = request.nativePin;
            if (nativePin >= 0
                && !isEnabledPin(nativePin, terminal.getCraftingPinsRows(), terminal.getPlayerPinsRows())) return;
            IAEStack<?> pin = nativePin < 0 ? null : terminal.getPin(nativePin);

            if (request.action == MonitorableAction.UNSET_PIN) {
                if (nativePin < 0 || hand != null) return;
                try {
                    terminal.pin(null, nativePin, () -> operationValid(SecurityPermissions.EXTRACT));
                } finally {
                    recordPinChanges();
                    MiniTerminalTransferService.synchronize(player);
                }
                sendPins();
                return;
            }
            IAEStack<?> target = nativePin >= 0 ? (pin == null ? null : pin.copy()) : catalog.target(request.entryId);
            if (request.action == MonitorableAction.AUTO_CRAFT) {
                if (target == null || hand != null || !permission(SecurityPermissions.CRAFT)) return;
                IAEStack<?> live = catalog.current(target);
                if (live == null || !live.isCraftable()) return;
                openCrafting(live);
                return;
            }
            switch (request.action) {
                case PICKUP_OR_SET_DOWN, SPLIT_OR_PLACE_SINGLE, SHIFT_CLICK, PICKUP_SINGLE, ROLL_UP, ROLL_DOWN, MOVE_REGION, FILL_SINGLE_CONTAINER, FILL_CONTAINERS, DRAIN_SINGLE_CONTAINER, DRAIN_CONTAINERS -> {}
                default -> {
                    return;
                }
            }
            boolean drain = request.action == MonitorableAction.DRAIN_SINGLE_CONTAINER
                || request.action == MonitorableAction.DRAIN_CONTAINERS;
            boolean fill = request.action == MonitorableAction.FILL_SINGLE_CONTAINER
                || request.action == MonitorableAction.FILL_CONTAINERS;
            // Container actions need a held fluid container, and filling needs a fluid target.
            if ((drain || fill) && (hand == null || !FLUID_STACK_TYPE.isContainerItemForType(hand))) return;
            if (fill && !(target instanceof IAEFluidStack)) return;
            boolean inject = drain || MiniTerminalTransferService.depositsCursor(hand, request.action);
            if (inject && !permission(SecurityPermissions.INJECT)) return;
            if (fill) {
                // Pins store zero-sized templates; AE's container filling uses the target quantity as its limit.
                target = catalog.current(target);
                if (target == null || target.getStackSize() <= 0) return;
            }
            if (target == null && !inject) return;
            MiniTerminalTransferService.execute(
                player,
                terminal,
                target,
                request.action,
                () -> operationValid(inject ? SecurityPermissions.INJECT : SecurityPermissions.EXTRACT));
        }

        private void openCrafting(IAEStack<?> target) {
            if (target == null || !allowedContainer(player)
                || ItemMEMiniPortableTerminal.findTerminalSlot(player, terminalId) != slot) return;
            ItemStack current = ItemMEMiniPortableTerminal.getTerminalStack(player, slot);
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
            IAEStack<?> requested = target.copy();
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
                ScienceNotLeisure.LOG.error(
                    "Failed to open ME mini portable terminal crafting for {}",
                    player.getCommandSenderName(),
                    exception);
            }

            player.closeContainer();
            returnToInventory(player);
        }

        private void returnToInventory(EntityPlayerMP returned) {
            NBTTagCompound tag = message("RETURN", id);
            tag.setString(MiniTerminalUpdatePacket.TERMINAL_ID_KEY, terminalId.toString());
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
