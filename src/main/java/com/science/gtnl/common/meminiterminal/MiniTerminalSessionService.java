package com.science.gtnl.common.meminiterminal;

import static appeng.util.item.AEItemStackType.ITEM_STACK_TYPE;

import java.io.IOException;
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

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.ItemMEMiniPortableTerminal;
import com.science.gtnl.common.meminiterminal.MiniTerminalRequestPacket.DepositMode;
import com.science.gtnl.compat.bogosorter.BogoSorterSlots;
import com.science.gtnl.utils.CreativeCursor;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
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
import appeng.api.storage.data.AEStackTypeRegistry;
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
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

@EventBusSubscriber
public class MiniTerminalSessionService {

    public enum MiniTerminalStatus {
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

    private static final Map<UUID, Session> SESSIONS = new Object2ObjectOpenHashMap<>();
    private static final Map<UUID, UUID> SELECTED = new Object2ObjectOpenHashMap<>();
    private static final Map<UUID, RequestBudget> BUDGETS = new Object2ObjectOpenHashMap<>();
    private static final Map<UUID, ParkedCatalog> PARKED = new Object2ObjectOpenHashMap<>();
    private static final int PARK_TICKS = 1200;
    private static long nextSession;

    public static void end(EntityPlayerMP player) {
        end(player, true);
    }

    private static void end(EntityPlayerMP player, boolean park) {
        Session session = SESSIONS.remove(player.getUniqueID());
        if (session == null) return;
        MiniTerminalCatalog parked = park ? session.detachCatalog() : null;
        session.close();
        if (parked == null) return;
        ParkedCatalog previous = PARKED.put(
            player.getUniqueID(),
            new ParkedCatalog(session.terminalId, session.binding, parked, player.ticksExisted));
        if (previous != null) previous.catalog()
            .close();
    }

    private static void discardParked(UUID player) {
        ParkedCatalog parked = PARKED.remove(player);
        if (parked != null) parked.catalog()
            .close();
    }

    private static MiniTerminalCatalog resumeCatalog(EntityPlayerMP player, MiniTerminalRequestPacket request,
        Session session) {
        ParkedCatalog parked = PARKED.remove(player.getUniqueID());
        if (parked == null) return null;
        MiniTerminalCatalog catalog = parked.catalog();
        if (!parked.expired(player.ticksExisted) && request.catalogEpoch == catalog.epoch
            && request.catalogRevision > 0
            && request.catalogRevision <= catalog.revision
            && parked.terminalId()
                .equals(session.terminalId)
            && Objects.equals(parked.binding(), session.binding)
            && catalog.resume(session.monitors)) return catalog;
        catalog.close();
        return null;
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
            if (request.kind == MiniTerminalRequestPacket.Kind.BEGIN
                && BUDGETS.computeIfAbsent(player.getUniqueID(), key -> new RequestBudget())
                    .accept(player.ticksExisted)) {
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
            if (!accepted) {
                budget.cancelPendingBegin();
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
            if (!budget.requestBegin(player.ticksExisted, request)) return;
        } else if (!accepted) return;
        if (request.kind == MiniTerminalRequestPacket.Kind.BEGIN
            || request.kind == MiniTerminalRequestPacket.Kind.FULL) {
            budget.cancelPendingBegin();
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
        if (session == null || session.id != request.sessionId) {
            synchronizeCreativeCursor(player, request);
            return;
        }
        MiniTerminalStatus status = session.validate();
        session.validated = status;
        session.validationTick = player.ticksExisted;
        if (status != MiniTerminalStatus.ONLINE) {
            synchronizeCreativeCursor(player, request);
            session.status(status);
            end(player);
            return;
        }
        if (request.kind == MiniTerminalRequestPacket.Kind.RESYNC) {
            session.resync();
        } else if (request.kind == MiniTerminalRequestPacket.Kind.ACTION) {
            session.action(request);
        } else if (request.kind == MiniTerminalRequestPacket.Kind.CREATIVE_ACTION) {
            session.creativeAction(request);
        } else if (request.kind == MiniTerminalRequestPacket.Kind.PIN_SETTINGS) {
            session.updatePinSettings(request);
        } else if (request.kind == MiniTerminalRequestPacket.Kind.PIN_DROP) {
            session.pinDrop(request);
        } else if (request.kind == MiniTerminalRequestPacket.Kind.INVENTORY_DEPOSIT) {
            session.inventoryDeposit(request);
        } else if (request.kind == MiniTerminalRequestPacket.Kind.INVENTORY_EXTRACT) {
            session.inventoryExtract(request);
        }
    }

    static boolean isEnabledPin(int index, MiniTerminalPinSettings settings) {
        return index >= 0 && index < settings.craftingCount()
            || index >= PinList.PLAYER_OFFSET && index < PinList.PLAYER_OFFSET + settings.playerCount();
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

    private static void synchronizeCreativeCursor(EntityPlayerMP player, MiniTerminalRequestPacket request) {
        if (request.kind != MiniTerminalRequestPacket.Kind.CREATIVE_ACTION || !player.theItemInWorldManager.isCreative()
            || player.openContainer != player.inventoryContainer) return;
        player.inventory.setItemStack(ItemStack.copyItemStack(request.creativeCursor));
        CreativeCursor.returnToClient(player);
    }

    public static boolean isSessionStack(EntityPlayerMP player, ItemStack stack) {
        return activeHost(player, stack) != null;
    }

    static WirelessTerminalGuiObject activeHost(EntityPlayerMP player, ItemStack stack) {
        Session session = SESSIONS.get(player.getUniqueID());
        return session != null && session.player == player && session.stack == stack ? session.host : null;
    }

    static IGridNode resolveBindingNode(String binding) {
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

    private static void togglePickup(EntityPlayerMP player, MiniTerminalRequestPacket request, Session session) {
        ItemStack stack = requestedTerminal(player, request, session);
        if (stack == null) return;
        boolean enabled = !ItemMEMiniPortableTerminal.isPickupToMEEnabled(stack);

        if (enabled) {
            IntList slots = ItemMEMiniPortableTerminal.getTerminalSlots(player);
            for (int i = 0; i < slots.size(); i++) {
                ItemStack other = ItemMEMiniPortableTerminal.getTerminalStack(player, slots.getInt(i));
                if (other != stack && ItemMEMiniPortableTerminal.isPickupToMEEnabled(other)) {
                    ItemMEMiniPortableTerminal.setPickupToMEEnabled(other, false);
                }
            }
        }
        ItemMEMiniPortableTerminal.setPickupToMEEnabled(stack, enabled);
        MiniTerminalTransferService.synchronize(player);
    }

    private static void settings(EntityPlayerMP player, MiniTerminalRequestPacket request, Session session) {
        ItemStack stack = requestedTerminal(player, request, session);
        if (stack == null) return;
        IConfigManager config = ((IWirelessTermHandler) stack.getItem()).getConfigManager(stack);

        config.putSetting(Settings.SORT_BY, request.sortOrder);
        config.putSetting(Settings.SORT_DIRECTION, request.sortDirection);
        config.putSetting(Settings.VIEW_MODE, request.viewMode);
        if (session != null && session.stack == stack) {
            Reference2BooleanMap<IAEStackType<?>> filters = new Reference2BooleanOpenHashMap<>(request.typeFilters);
            filters.put(ITEM_STACK_TYPE, true);
            session.terminal.updateTypeFilters(filters);
        } else {
            TerminalSettings terminalSettings = new TerminalSettings();
            terminalSettings.readFromNBT(stack.getTagCompound());
            AEStackTypeFilter filters = terminalSettings.getFilters(player);
            for (Reference2BooleanMap.Entry<IAEStackType<?>> entry : request.typeFilters.reference2BooleanEntrySet())
                filters.setEnabled(entry.getKey(), entry.getBooleanValue());
            filters.setEnabled(ITEM_STACK_TYPE, true);
            terminalSettings.writeToNBT(stack);
        }
        NBTTagCompound response = message("SETTINGS", request.sessionId);
        response.setString(MiniTerminalUpdatePacket.TERMINAL_ID_KEY, request.terminalId.toString());
        response.setLong("actionId", request.actionId);
        response.setTag("config", configTag(stack, player, config));
        send(player, response);
        MiniTerminalTransferService.synchronize(player);
    }

    private static ItemStack requestedTerminal(EntityPlayerMP player, MiniTerminalRequestPacket request,
        Session session) {
        if (!allowedContainer(player) || request.slot < 0
            || ItemMEMiniPortableTerminal.findTerminalSlot(player, request.terminalId) != request.slot) return null;
        if (session == null || session.id != request.sessionId || !session.acceptAction(request)) return null;
        return authoritativeSessionStack(
            player,
            request,
            session,
            ItemMEMiniPortableTerminal.getTerminalStack(player, request.slot));
    }

    private static MiniTerminalStatus missingStatus(EntityPlayer player, UUID id) {
        if (id != null && id.equals(ItemMEMiniPortableTerminal.getTerminalId(player.inventory.getItemStack())))
            return MiniTerminalStatus.ON_CURSOR;
        return ItemMEMiniPortableTerminal.isTerminalDuplicated(player, id) ? MiniTerminalStatus.AMBIGUOUS
            : MiniTerminalStatus.MISSING;
    }

    private static NBTTagCompound configTag(ItemStack stack, EntityPlayer player, IConfigManager config) {
        NBTTagCompound data = new NBTTagCompound();
        config.writeToNBT(data);
        readTypeFilters(stack, player).writeToNBT(data);
        return data;
    }

    private static ItemStack authoritativeSessionStack(EntityPlayerMP player, MiniTerminalRequestPacket request,
        Session session, ItemStack current) {
        if (session == null || session.id != request.sessionId
            || session.slot != request.slot
            || !session.terminalId.equals(request.terminalId)) return current;
        if (!ItemMEMiniPortableTerminal.restoreTerminalStack(player, request.slot, session.stack)) return null;
        return session.stack;
    }

    private static void begin(EntityPlayerMP player, MiniTerminalRequestPacket request) {
        end(player);
        if (!allowedContainer(player) || player.openContainer.windowId != request.windowId) {
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
            sendStatus(player, id, request.requestId, slot, preferred, null, missingStatus(player, preferred));
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
            session.validated = status;
            session.validationTick = player.ticksExisted;
            if (status == MiniTerminalStatus.ONLINE) {
                session.catalog = resumeCatalog(player, request, session);
                session.resumed = session.catalog != null;
                if (!session.resumed) session.catalog = new MiniTerminalCatalog(session.monitors);
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
    public static void tick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof EntityPlayerMP player)) return;
        MiniTerminalOutbound.flush(player);
        ParkedCatalog parked = PARKED.get(player.getUniqueID());
        if (parked != null && parked.expired(player.ticksExisted)) discardParked(player.getUniqueID());
        RequestBudget budget = BUDGETS.get(player.getUniqueID());
        if (budget != null) {
            MiniTerminalRequestPacket pending = budget.pollBegin(player.ticksExisted);
            if (pending != null) begin(player, pending);
        }
        Session session = SESSIONS.get(player.getUniqueID());
        if (session == null) return;
        MiniTerminalStatus status;
        if (session.validated == null || player.ticksExisted - session.validationTick >= 5) {
            status = session.validate();
            session.validated = status;
            session.validationTick = player.ticksExisted;
        } else {
            status = session.validated;
        }
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
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP player) end(player, false);
        discardParked(event.player.getUniqueID());
        SELECTED.remove(event.player.getUniqueID());
        BUDGETS.remove(event.player.getUniqueID());
        MiniTerminalOutbound.forget(event.player.getUniqueID());
        MiniTerminalTransferService.forget(event.player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPickup(EntityItemPickupEvent event) {
        MiniTerminalTransferService.pickup(event);
    }

    private static void send(EntityPlayerMP player, NBTTagCompound data) {
        MiniTerminalOutbound.send(player, data);
    }

    private static NBTTagCompound message(String kind, long session) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("kind", kind);
        tag.setLong("session", session);
        return tag;
    }

    private static void sendStatus(EntityPlayerMP player, long session, long requestId, int slot, UUID terminalId,
        ItemStack stack, MiniTerminalStatus status) {
        send(player, statusTag(player, session, requestId, slot, terminalId, stack, status));
    }

    private static NBTTagCompound statusTag(EntityPlayerMP player, long session, long requestId, int slot,
        UUID terminalId, ItemStack stack, MiniTerminalStatus status) {
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
            tag.setTag("config", configTag(stack, player, handler.getConfigManager(stack)));
        }
        return tag;
    }

    private static AEStackTypeFilter readTypeFilters(ItemStack stack, EntityPlayer player) {
        TerminalSettings settings = new TerminalSettings();
        settings.readFromNBT(stack.getTagCompound());
        return settings.getFilters(player);
    }

    private static void sendParts(EntityPlayerMP player, NBTTagCompound header,
        List<MiniTerminalUpdatePacket.Entry> entries, boolean snapshot) {
        MiniTerminalOutbound.sendParts(player, header, entries, snapshot, MiniTerminalSessionService::failUpdate);
    }

    private static void failUpdate(EntityPlayerMP player, long session, Throwable exception) {
        ScienceNotLeisure.LOG.warn("Cannot encode ME mini portable terminal update", exception);
        Session current = SESSIONS.get(player.getUniqueID());
        if (current != null && current.id == session) end(player, false);
        NBTTagCompound failed = message("STATUS", session);
        failed.setString("status", MiniTerminalStatus.STALE.name());
        failed.setInteger("retryTicks", 200);
        send(player, failed);
    }

    private static class RequestBudget {

        private int tick;
        private int count;
        private int lastBegin = -10;
        private MiniTerminalRequestPacket pendingBegin;

        boolean accept(int now) {
            if (now < tick || now - tick >= 20) {
                tick = now;
                count = 0;
            }
            return ++count <= 64;
        }

        boolean requestBegin(int now, MiniTerminalRequestPacket request) {
            pendingBegin = request;
            if (!acceptBegin(now)) return false;
            pendingBegin = null;
            return true;
        }

        MiniTerminalRequestPacket pollBegin(int now) {
            if (pendingBegin == null || !acceptBegin(now)) return null;
            MiniTerminalRequestPacket request = pendingBegin;
            pendingBegin = null;
            return request;
        }

        void cancelPendingBegin() {
            pendingBegin = null;
        }

        private boolean acceptBegin(int now) {
            if (now >= lastBegin && now - lastBegin < 10) return false;
            lastBegin = now;
            return true;
        }
    }

    private static class Session {

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
        final Map<IAEStackType<?>, IMEMonitor<?>> monitors;
        final MiniTerminalMonitorableAdapter terminal;
        MiniTerminalPinSettings pinSettings;
        NBTTagList observedPins;
        NBTTagList observedPinsSource;
        MiniTerminalStatus validated;
        int validationTick = Integer.MIN_VALUE;
        MiniTerminalCatalog catalog;
        long pinRevision = 1;
        long lastAction;
        int energyTicks;
        int lastResync;
        boolean resumed;

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
            try {
                this.monitor = terminal.getItemMonitor();
                Map<IAEStackType<?>, IMEMonitor<?>> available = new Reference2ObjectOpenHashMap<>();
                for (IAEStackType<?> type : AEStackTypeRegistry.getAllTypes()) {
                    IMEMonitor<?> typeMonitor = host.getMEMonitor(type);
                    if (typeMonitor != null) available.put(type, typeMonitor);
                }
                this.monitors = available;
                this.pinSettings = MiniTerminalPinSettings
                    .read(stack, player, terminal.getCraftingPinsRows(), terminal.getPlayerPinsRows());
                observePins();
            } catch (RuntimeException exception) {
                terminal.close();
                throw exception;
            }
        }

        MiniTerminalStatus validate() {
            if (!allowedContainer(player) || player.openContainer != container || container.windowId != windowId)
                return MiniTerminalStatus.INVALID_CONTAINER;
            ItemStack currentStack = ItemMEMiniPortableTerminal.getTerminalStack(player, slot);
            if (currentStack != stack) {
                int current = ItemMEMiniPortableTerminal.findTerminalSlot(player, terminalId);
                if (current < 0) return missingStatus(player, terminalId);
                if (current != slot) return MiniTerminalStatus.STALE;
                if (!ItemStack.areItemStacksEqual(stack, currentStack)
                    || !ItemMEMiniPortableTerminal.restoreTerminalStack(player, current, stack))
                    return MiniTerminalStatus.STALE;
            }
            if (!Objects.equals(binding, handler.getEncryptionKey(stack))) return MiniTerminalStatus.STALE;

            NBTTagList livePins = pinData();
            if (livePins != observedPinsSource) {
                if (!observedPins.equals(livePins)) return MiniTerminalStatus.STALE;
                observedPinsSource = livePins;
            }
            if (binding == null || binding.isEmpty()) return MiniTerminalStatus.UNBOUND;
            IGridNode liveNode = resolveBindingNode(binding);
            if (liveNode == null || grid == null) return MiniTerminalStatus.OFFLINE;
            if (liveNode != node || liveNode.getGrid() != grid) return MiniTerminalStatus.STALE;
            IStorageGrid storage = grid.getCache(IStorageGrid.class);
            if (storage == null || storage.getItemInventory() != monitor || monitor == null)
                return MiniTerminalStatus.STALE;
            for (IAEStackType<?> type : AEStackTypeRegistry.getAllTypes()) {
                if (storage.getMEMonitor(type) != monitors.get(type)) return MiniTerminalStatus.STALE;
            }
            return networkStatus(player, grid, host, SecurityPermissions.EXTRACT);
        }

        boolean permission(SecurityPermissions permission) {
            ISecurityGrid security = grid == null ? null : grid.getCache(ISecurityGrid.class);
            return security != null && security.hasPermission(player, permission);
        }

        void status(MiniTerminalStatus status) {
            NBTTagCompound tag = statusTag(player, id, requestId, slot, terminalId, stack, status);
            if (status == MiniTerminalStatus.ONLINE && catalog != null) {
                tag.setLong("catalogEpoch", catalog.epoch);
                tag.setLong("catalogRevision", catalog.revision);
                tag.setBoolean("resumed", resumed);
            }
            send(player, tag);
        }

        private NBTTagList pinData() {
            return stack.hasTagCompound() ? stack.getTagCompound()
                .getTagList("pins", 10) : new NBTTagList();
        }

        private boolean operationValid(SecurityPermissions permission) {
            if (SESSIONS.get(player.getUniqueID()) != this || player.openContainer != container
                || !container.isPlayerNotUsingContainer(player)) return false;
            if (validated == null) {
                validated = validate();
                validationTick = player.ticksExisted;
            }
            return validated == MiniTerminalStatus.ONLINE && permission(permission);
        }

        private void observePins() {
            observedPinsSource = pinData();
            observedPins = (NBTTagList) observedPinsSource.copy();
        }

        private void recordPinChanges() {
            if (!terminal.consumePinsChanged()) return;
            observePins();
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
            List<MiniTerminalUpdatePacket.Entry> entries = new ObjectArrayList<>();
            for (int index = 0; index < PinList.TOTAL_SLOTS; index++) {
                IAEStack<?> pin = terminal.getPin(index);
                if (pin != null) entries.add(new MiniTerminalUpdatePacket.Entry(index, pin.copy()));
            }
            NBTTagCompound header = message("PINS", id);
            header.setLong("revision", pinRevision);
            header.setInteger("craftingCount", pinSettings.craftingCount());
            header.setInteger("playerCount", pinSettings.playerCount());
            sendParts(player, header, entries, true);
        }

        boolean acceptAction(MiniTerminalRequestPacket request) {
            if (request.actionId <= lastAction) return false;
            lastAction = request.actionId;
            return true;
        }

        void updatePinSettings(MiniTerminalRequestPacket request) {
            if (!acceptAction(request)) return;
            if (request.pinRevision != pinRevision) {
                sendPins();
                return;
            }
            try {
                if (terminal.setPinSettings(request.pinSettings, () -> operationValid(SecurityPermissions.EXTRACT))) {
                    pinSettings = request.pinSettings;
                    pinSettings.save(stack, player);
                }
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
            if (nativePin < PinList.PLAYER_OFFSET || !isEnabledPin(nativePin, pinSettings)) return;
            IAEStack<?> offered = request.pinTarget.copy()
                .reset();
            IAEStack<?> previous = terminal.getPin(nativePin);
            if (previous != null && previous.isSameType(offered)) return;
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
                case MATCHING, REGION -> BogoSorterSlots.slotGroup(player, container, source);
                case NATIVE_REGION -> nativeRegionSources(source);
            };
            ItemStack selected = offered == null ? null : offered.copy();
            BooleanSupplier valid = () -> operationValid(SecurityPermissions.INJECT);
            try {
                for (Slot candidate : sources) {
                    if (!valid.getAsBoolean()) return;
                    int inventorySlot = ItemMEMiniPortableTerminal.playerInventoryIndex(player, candidate);
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
            List<Slot> slots = new ObjectArrayList<>();

            for (Object entry : container.inventorySlots) {
                Slot candidate = (Slot) entry;
                if (candidate.getClass() != source.getClass()) continue;
                if (BogoSorterSlots.isPinned(player, container, candidate)) continue;
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
            if (request.nativePin >= 0 && !isEnabledPin(request.nativePin, pinSettings)) return;
            IAEStack<?> selected = request.nativePin >= 0 ? terminal.getPin(request.nativePin)
                : catalog.target(request.entryId);
            if (!(selected instanceof IAEItemStack target)) return;
            IntLinkedOpenHashSet destinations = new IntLinkedOpenHashSet();

            for (Object entry : player.inventoryContainer.inventorySlots) {
                Slot playerSlot = (Slot) entry;
                int inventorySlot = ItemMEMiniPortableTerminal.playerInventoryIndex(player, playerSlot);
                if (inventorySlot < 0) continue;
                Slot hostSlot = container.getSlotFromInventory(player.inventory, inventorySlot);
                Slot destination = hostSlot == null ? playerSlot : hostSlot;
                if (!destination.isItemValid(target.getItemStack())) continue;
                Container destinationContainer = hostSlot == null ? player.inventoryContainer : container;
                if (BogoSorterSlots.isPinned(player, destinationContainer, destination)) continue;
                destinations.add(inventorySlot);
            }
            MiniTerminalTransferService.extractInventory(
                player,
                terminal,
                target,
                destinations.toIntArray(),
                request.emptyOnly,
                request.single,
                () -> operationValid(SecurityPermissions.EXTRACT));
        }

        void creativeAction(MiniTerminalRequestPacket request) {
            if (!player.theItemInWorldManager.isCreative() || container != player.inventoryContainer) return;

            player.inventory.setItemStack(ItemStack.copyItemStack(request.creativeCursor));
            try {
                action(request);
            } finally {
                CreativeCursor.returnToClient(player);
            }
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
            if (nativePin >= 0 && !isEnabledPin(nativePin, pinSettings)) return;
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
            if ((drain || fill) && MiniTerminalStackTypes.containerType(hand) == null) return;
            if (fill && (target == null || target.getStackType() == ITEM_STACK_TYPE
                || !target.getStackType()
                    .isContainerItemForType(hand)))
                return;
            boolean inject = drain || MiniTerminalTransferService.depositsCursor(hand, request.action);
            if (inject && !permission(SecurityPermissions.INJECT)) return;
            if (fill) {
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
                        || !(serverPlayer.openContainer instanceof AEBaseContainer aeBaseContainer)
                        || !(aeBaseContainer instanceof IContainerSubGui)
                        || aeBaseContainer.getInventoryPlayer().player != serverPlayer
                        || aeBaseContainer.getPrimaryGui() != this) return;
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

        MiniTerminalCatalog detachCatalog() {
            if (catalog == null || !catalog.suspend()) return null;
            MiniTerminalCatalog detached = catalog;
            catalog = null;
            return detached;
        }

        void close() {
            try {
                if (catalog != null) catalog.close();
            } finally {
                terminal.close();
            }
        }
    }

    private record ParkedCatalog(UUID terminalId, String binding, MiniTerminalCatalog catalog, int tick) {

        boolean expired(int now) {
            return now < tick || now - tick > PARK_TICKS;
        }
    }
}
