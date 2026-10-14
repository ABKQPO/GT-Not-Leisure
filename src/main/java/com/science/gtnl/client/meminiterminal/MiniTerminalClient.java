package com.science.gtnl.client.meminiterminal;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.cleanroommc.modularui.api.IMuiScreen;
import com.cleanroommc.modularui.screen.OpenScreenEvent;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.ItemMEMiniPortableTerminal;
import com.science.gtnl.common.meminiterminal.MiniTerminalPinSettings;
import com.science.gtnl.common.meminiterminal.MiniTerminalRequestPacket;
import com.science.gtnl.common.meminiterminal.MiniTerminalUpdatePacket;

import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.implementations.guiobjects.IPortableCell;
import appeng.api.storage.data.IAEStack;
import appeng.client.gui.AEBaseGui;
import appeng.container.AEBaseContainer;
import appeng.helpers.IPinsHandler;
import appeng.helpers.MonitorableAction;
import appeng.util.Platform;
import codechicken.nei.api.API;
import codechicken.nei.api.IGuiContainerOverlay;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MiniTerminalClient {

    private static final int MAX_CACHED_VIEWS = 8;
    private static final MiniTerminalClient INSTANCE = new MiniTerminalClient();
    private static final Map<UUID, MiniTerminalModel> VIEWS = new LinkedHashMap<>(MAX_CACHED_VIEWS, 0.75f, true) {

        @Override
        protected boolean removeEldestEntry(Map.Entry<UUID, MiniTerminalModel> eldest) {
            return size() > MAX_CACHED_VIEWS;
        }
    };

    private static final MiniTerminalLayout LAYOUT = new MiniTerminalLayout();
    private static final Set<Long> RETIRED_SESSIONS = new LinkedHashSet<>();
    private static NetHandlerPlayClient connection;
    private static MiniTerminalInventoryPanel overlay;
    private static UUID selected;
    private static int selectedSlot = -1;
    private static long session;
    private static long action;
    private static long requestId;
    private static Container sessionContainer;
    private static int sessionWindowId;
    private static int retryTicks;
    private static int sessionTicks;
    private static boolean sessionPrepared;
    private static boolean collapsed;
    private static boolean cachedPickupToME;
    private static boolean waiting;
    private static boolean pinSettingsPending;
    private static int pinSettingsPendingTicks;
    private static MiniTerminalPinSettings requestedPins;
    private static long requestedPinRevision;
    private static int settingsRetryTicks;
    private static String status = "OFFLINE";
    private static String name = "ME";
    private static String network = "";

    private MiniTerminalClient() {}

    public static void init() {
        MiniTerminalClientConfig.load(LAYOUT);
        MiniTerminalBogoCompat.init();
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(INSTANCE);
        MiniTerminalNEIAdapter nei = new MiniTerminalNEIAdapter();
        API.registerNEIGuiHandler(nei);

        GuiContainerManager.objectHandlers.addFirst(nei);
        GuiContainerManager.addDrawHandler(nei);
        GuiContainerManager.addTooltipHandler(nei);
        GuiContainerManager.addSlotClickHandler(nei);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void prepareNativePanel(DrawScreenEvent.Pre event) {
        MiniTerminalHostLayer.beginFrame(event.gui);
        MiniTerminalInventoryPanel panel = overlay(event.gui);
        if (panel != null) panel.prepareFrame();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void finishHostFrame(DrawScreenEvent.Post event) {
        MiniTerminalHostLayer.endFrame(event.gui);
    }

    @SubscribeEvent
    public void open(OpenScreenEvent event) {
        MiniTerminalClientConfig.saveWindow(LAYOUT);
        checkConnection();
        if (selected != null && event.getScreen() instanceof GuiContainer gui
            && gui.inventorySlots instanceof AEBaseContainer nativeContainer
            && nativeContainer.getTarget() instanceof IPortableCell portable
            && selected.equals(ItemMEMiniPortableTerminal.getTerminalId(portable.getItemStack())))
            model().handOffSettings();
        if (session != 0 || waiting || sessionPrepared) closeSession();
        if (overlay != null) overlay.detach();
        overlay = null;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void initGui(InitGuiEvent.Post event) {
        if (!(event.gui instanceof IMuiScreen)) attachPanel(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void initNativePanel(InitGuiEvent.Post event) {
        if (event.gui instanceof IMuiScreen) attachPanel(event);
    }

    private static void attachPanel(InitGuiEvent.Post event) {
        checkConnection();
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != event.gui || overlay != null
            || !(event.gui instanceof GuiContainer inventory)
            || mc.thePlayer == null
            || mc.playerController == null
            || inventory instanceof GuiInventory && mc.playerController.isInCreativeMode()
            || inventory instanceof GuiContainerCreative && !mc.playerController.isInCreativeMode()
            || isAEInterface(inventory)
            || inventory instanceof IGuiContainerOverlay recipe && isAEInterface(recipe.getFirstScreen())
            || MiniTerminalBogoCompat.isTerminalScreen(inventory)) return;
        int slot = findSelectedSlot();
        if (slot < 0 && !hasMiniTerminal()) return;
        if (selected == null && slot >= 0) {
            selected = ItemMEMiniPortableTerminal
                .getTerminalId(ItemMEMiniPortableTerminal.getTerminalStack(mc.thePlayer, slot));
        }
        selectedSlot = slot;
        MiniTerminalModel model = model();
        if (slot >= 0) model.readSettings(ItemMEMiniPortableTerminal.getTerminalStack(mc.thePlayer, slot));
        model.beginSession();
        MiniTerminalPanelHost host = MiniTerminalPanelHost.create(inventory, model);
        overlay = host.panel();
        host.open();
        cachedPickupToME = readPickupToME();
        sessionPrepared = true;
        retryTicks = 0;
    }

    static void closeOverlay(MiniTerminalInventoryPanel closing) {
        checkConnection();
        if (overlay != closing) return;
        MiniTerminalClientConfig.saveWindow(LAYOUT);
        closeSession();
        overlay = null;
    }

    private static boolean isAEInterface(GuiContainer gui) {
        return gui instanceof AEBaseGui || gui instanceof IPinsHandler
            || gui != null && gui.inventorySlots instanceof AEBaseContainer;
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        checkConnection();
        Minecraft mc = Minecraft.getMinecraft();
        if (overlay == null || mc.currentScreen != overlay.inventory()) {
            MiniTerminalClientConfig.saveWindow(LAYOUT);
            if (session != 0 || waiting || sessionPrepared) closeSession();
            if (overlay != null) overlay.detach();
            overlay = null;
            return;
        }
        if (sessionContainer != null && !hasCurrentContainer()) {
            closeSession();
            sessionPrepared = true;
            retryTicks = 0;
        }
        overlay.syncLayout();
        cachedPickupToME = readPickupToME();
        ensureSession();
        if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) MiniTerminalClientConfig.saveWindow(LAYOUT);
        model().update();
        if (model().hasPendingSettings()) {
            if (++settingsRetryTicks >= 40) submitSettings(false);
        } else settingsRetryTicks = 0;
        if (pinSettingsPending && ++pinSettingsPendingTicks >= 40) {
            submitPinSettings();
        }
        if (!overlay.layout()
            .expanded()) {
            closeSession();
            return;
        }
        if (waiting || session != 0 && model().isLoading()) {
            if (++sessionTicks >= 100) {

                closeSession();
                model().retryAfterTimeout();
                retryTicks = 20;
            }
            return;
        }
        sessionTicks = 0;
        if (!model().isOnline() && --retryTicks <= 0) {
            begin();
        }
    }

    private static void checkConnection() {
        NetHandlerPlayClient current = Minecraft.getMinecraft()
            .getNetHandler();
        if (connection == current) return;
        MiniTerminalClientConfig.saveWindow(LAYOUT);
        connection = current;
        session = 0;
        sessionContainer = null;
        action = 0;
        requestId = 0;
        waiting = false;
        sessionPrepared = false;
        sessionTicks = 0;
        pinSettingsPending = false;
        settingsRetryTicks = 0;
        overlay = null;
        selected = null;
        selectedSlot = -1;
        status = "OFFLINE";
        name = "ME";
        network = "";
        RETIRED_SESSIONS.clear();
        VIEWS.clear();
    }

    private static void retire(long id) {
        if (id == 0) return;
        if (Minecraft.getMinecraft()
            .getNetHandler() != null) {
            ScienceNotLeisure.network.sendToServer(MiniTerminalRequestPacket.end(id));
        }
        RETIRED_SESSIONS.add(id);
        if (RETIRED_SESSIONS.size() > 128) RETIRED_SESSIONS.remove(
            RETIRED_SESSIONS.iterator()
                .next());
    }

    private static boolean hasMiniTerminal() {
        Minecraft mc = Minecraft.getMinecraft();
        return ItemMEMiniPortableTerminal.findFirstTerminalSlot(mc.thePlayer) >= 0;
    }

    private static int findSelectedSlot() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return -1;
        if (selected != null) return ItemMEMiniPortableTerminal.findTerminalSlot(mc.thePlayer, selected);
        return ItemMEMiniPortableTerminal.findFirstTerminalSlot(mc.thePlayer);
    }

    public static void begin() {
        Minecraft mc = Minecraft.getMinecraft();
        if (waiting || mc.thePlayer == null
            || overlay == null
            || !overlay.isOpen()
            || mc.currentScreen != overlay.inventory()
            || mc.thePlayer.openContainer == null) return;
        closeSession();

        sessionContainer = mc.thePlayer.openContainer;
        sessionWindowId = sessionContainer.windowId;
        selectedSlot = findSelectedSlot();
        waiting = true;
        model().beginSession();
        retryTicks = 20;
        ScienceNotLeisure.network
            .sendToServer(MiniTerminalRequestPacket.begin(selectedSlot, selected, ++requestId, sessionWindowId));
    }

    private static boolean hasCurrentContainer() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer != null && sessionContainer != null
            && mc.thePlayer.openContainer == sessionContainer
            && sessionContainer.windowId == sessionWindowId;
    }

    private static boolean connected() {
        return model().isOnline() && session != 0 && hasCurrentContainer();
    }

    public static void ensureSession() {
        if (!sessionPrepared || overlay == null
            || Minecraft.getMinecraft().currentScreen != overlay.inventory()
            || collapsed
            || !overlay.layout()
                .expanded()
            || session != 0
            || waiting) return;
        begin();
    }

    public static void closeSession() {
        if (session != 0) {
            retire(session);
        }
        session = 0;
        sessionContainer = null;
        waiting = false;
        sessionPrepared = false;
        sessionTicks = 0;
        pinSettingsPending = false;
        model().closeSession();
    }

    public static void receive(NBTTagCompound data, List<MiniTerminalUpdatePacket.Entry> entries) {
        checkConnection();
        Minecraft mc = Minecraft.getMinecraft();
        if ("RETURN".equals(data.getString("kind"))) {
            if (mc.thePlayer != null && mc.playerController != null) {
                mc.displayGuiScreen(
                    mc.playerController.isInCreativeMode() ? new GuiContainerCreative(mc.thePlayer)
                        : new GuiInventory(mc.thePlayer));
            }
            return;
        }
        long incoming = data.getLong("session");
        String kind = data.getString("kind");
        if ("SETTINGS".equals(kind)) {

            try {
                MiniTerminalModel view = VIEWS
                    .get(UUID.fromString(data.getString(MiniTerminalUpdatePacket.TERMINAL_ID_KEY)));
                if (view != null) view.readSettings(data.getCompoundTag("config"), data.getLong("actionId"));
            } catch (IllegalArgumentException ignored) {}
            return;
        }
        if (overlay == null || mc.currentScreen != overlay.inventory()
            || !hasCurrentContainer()
            || !overlay.layout()
                .expanded()) {

            if ("STATUS".equals(kind)) retire(incoming);
            return;
        }
        if (RETIRED_SESSIONS.contains(incoming)) return;
        if ("STATUS".equals(kind) && incoming != session && data.getLong("requestId") != requestId) {
            retire(incoming);
            return;
        }
        UUID id;
        try {
            id = UUID.fromString(data.getString(MiniTerminalUpdatePacket.TERMINAL_ID_KEY));
        } catch (IllegalArgumentException ignored) {
            id = selected;
        }
        if (selected != null && id != null && !selected.equals(id)) {
            if ("STATUS".equals(kind)) retire(incoming);
            return;
        }
        if ("STATUS".equals(kind)) {
            if (!waiting && incoming != session) {
                retire(incoming);
                return;
            }
            if (selected == null && id != null) {
                MiniTerminalModel old = model();
                VIEWS.remove(null);
                selected = id;
                VIEWS.put(id, old);
            }
            session = incoming;
            waiting = false;
            selectedSlot = data.getInteger("slot");
            status = data.getString("status");
            retryTicks = Math.max(retryTicks, data.getInteger("retryTicks"));
            name = data.getString("name");
            network = data.getString("network");
            if (data.hasKey("config", 10)) model().readSettings(data.getCompoundTag("config"));
            model().status("ONLINE".equals(status), network);
            if (model().hasPendingSettings()) submitSettings(false);
            return;
        }
        if (incoming != session || session == 0) return;
        if ("CATALOG".equals(kind) && !model().catalog(data, entries)) {
            ScienceNotLeisure.network.sendToServer(MiniTerminalRequestPacket.resync(session));
        } else if ("PINS".equals(kind)) {
            if (!model().pins(data, entries)) {
                ScienceNotLeisure.network.sendToServer(MiniTerminalRequestPacket.resync(session));
            } else if (pinSettingsPending && data.getBoolean("last")) {
                if (model().pinSettings()
                    .equals(requestedPins)) pinSettingsPending = false;
                else if (model().pinRevision() != requestedPinRevision) submitPinSettings();
            }
        }
    }

    public static void saveSettings() {
        submitSettings(true);
    }

    public static boolean pickupToME() {
        return cachedPickupToME;
    }

    private static boolean readPickupToME() {
        int slot = findSelectedSlot();
        return slot >= 0 && ItemMEMiniPortableTerminal
            .isPickupToMEEnabled(ItemMEMiniPortableTerminal.getTerminalStack(Minecraft.getMinecraft().thePlayer, slot));
    }

    public static void togglePickupToME() {
        int slot = findSelectedSlot();
        if (slot < 0) return;
        UUID terminalId = ItemMEMiniPortableTerminal
            .getTerminalId(ItemMEMiniPortableTerminal.getTerminalStack(Minecraft.getMinecraft().thePlayer, slot));
        if (terminalId == null) return;
        ScienceNotLeisure.network.sendToServer(
            MiniTerminalRequestPacket.pickupToggle(
                session,
                ++action,
                slot,
                terminalId,
                Minecraft.getMinecraft().thePlayer.openContainer.windowId));
    }

    private static void submitSettings(boolean changed) {
        MiniTerminalModel view = model();
        long id = ++action;
        settingsRetryTicks = 0;

        if (changed) view.settingsSubmitted(id);
        int slot = findSelectedSlot();
        if (selected == null || slot < 0) return;
        ScienceNotLeisure.network.sendToServer(
            MiniTerminalRequestPacket.settings(
                session,
                id,
                slot,
                selected,
                (SortOrder) view.getSortBy(),
                (SortDir) view.getSortDir(),
                (ViewItems) view.getSortDisplay(),
                view.showFluids(),
                Minecraft.getMinecraft().thePlayer.openContainer.windowId));
    }

    public static void setPinSettings(MiniTerminalPinSettings settings) {
        if (pinSettingsPending || !connected()) return;
        pinSettingsPending = true;
        requestedPins = settings;
        submitPinSettings();
    }

    private static void submitPinSettings() {
        if (!pinSettingsPending || !connected()) return;
        pinSettingsPendingTicks = 0;
        requestedPinRevision = model().pinRevision();
        ScienceNotLeisure.network.sendToServer(
            MiniTerminalRequestPacket.pinSettings(session, ++action, requestedPinRevision, requestedPins));
    }

    public static void send(MonitorableAction intent, IAEStack<?> target, int pin) {
        if (!connected()) return;
        MiniTerminalModel model = model();
        MiniTerminalRequestPacket packet = MiniTerminalRequestPacket.action(
            session,
            ++action,
            model.catalogRevision(),
            model.pinRevision(),
            model.entryId(target),
            pin,
            intent);
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen instanceof GuiContainerCreative && mc.playerController.isInCreativeMode()) {
            if (!packet.withCreativeCursor(mc.thePlayer.inventory.getItemStack())) return;
        }
        ScienceNotLeisure.network.sendToServer(packet);
    }

    static void sendPin(int pin, IAEStack<?> target) {
        if (!connected()) return;
        MiniTerminalModel model = model();
        try {
            ScienceNotLeisure.network
                .sendToServer(MiniTerminalRequestPacket.pinDrop(session, ++action, model.pinRevision(), pin, target));
        } catch (IOException exception) {
            ScienceNotLeisure.LOG.warn("Unable to pin NEI stack in ME mini portable terminal", exception);
            Minecraft.getMinecraft().thePlayer
                .addChatMessage(new ChatComponentTranslation("gtnl.meminiterminal.pin_failed"));
        }
    }

    public static void toggleCollapsed() {
        collapsed = !collapsed;
        if (collapsed) closeSession();
        else retryTicks = 0;

    }

    public static boolean shiftClickToME(Slot slot, int mouseButton, int clickMode) {
        if (mouseButton != 0 && mouseButton != 1) return false;

        if ((clickMode == 0 || clickMode == 1) && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            return sendBogoInventory(slot, MiniTerminalRequestPacket.DepositMode.NATIVE_REGION);
        }
        if (clickMode != 1) return false;
        return sendBogoInventory(slot, MiniTerminalRequestPacket.DepositMode.STACK);
    }

    public static boolean sendBogoInventory(Slot slot, MiniTerminalRequestPacket.DepositMode mode) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!MiniTerminalClientConfig.shiftClickToME || slot == null
            || mc.thePlayer == null
            || overlay == null
            || mc.currentScreen != overlay.inventory()
            || overlay.inventory().inventorySlots != mc.thePlayer.openContainer
            || !overlay.layout()
                .expanded()
            || !containsSlot(mc.thePlayer.openContainer, slot)
            || ItemMEMiniPortableTerminal.isMiniTerminal(slot.getStack())) return false;

        int inventorySlot = ItemMEMiniPortableTerminal.playerInventoryIndex(mc.thePlayer, slot);
        if (inventorySlot < 0) return false;

        if (!connected()) return false;
        if (!slot.getHasStack() && mode != MiniTerminalRequestPacket.DepositMode.NATIVE_REGION) return true;
        ScienceNotLeisure.network
            .sendToServer(MiniTerminalRequestPacket.inventoryDeposit(session, ++action, inventorySlot, mode));
        return true;
    }

    private static boolean containsSlot(Container container, Slot slot) {
        int index = slot.slotNumber;
        return index >= 0 && index < container.inventorySlots.size() && container.inventorySlots.get(index) == slot
            || container.inventorySlots.contains(slot);
    }

    public static void sendBogoExtract(IAEStack<?> stack, int nativePin, boolean emptyOnly, boolean single) {
        if (!connected()) return;
        MiniTerminalModel view = model();
        ScienceNotLeisure.network.sendToServer(
            MiniTerminalRequestPacket.inventoryExtract(
                session,
                ++action,
                view.catalogRevision(),
                view.pinRevision(),
                view.entryId(stack),
                nativePin,
                emptyOnly,
                single));
    }

    public static void selectNext() {
        Minecraft mc = Minecraft.getMinecraft();
        List<Integer> slots = ItemMEMiniPortableTerminal.getTerminalSlots(mc.thePlayer);
        int start = slots.indexOf(findSelectedSlot());
        for (int step = 1; step <= slots.size(); step++) {
            int slot = slots.get((start + step) % slots.size());
            ItemStack stack = ItemMEMiniPortableTerminal.getTerminalStack(mc.thePlayer, slot);
            if (!ItemMEMiniPortableTerminal.isMiniTerminal(stack)) continue;
            closeSession();
            selected = ItemMEMiniPortableTerminal.getTerminalId(stack);
            selectedSlot = slot;
            model().readSettings(stack);
            overlay.setModel(model());
            cachedPickupToME = readPickupToME();
            begin();
            retryTicks = 20;
            return;
        }
    }

    public static void openFull() {
        if (model().hasPendingSettings()) submitSettings(false);
        closeSession();
        ScienceNotLeisure.network.sendToServer(
            MiniTerminalRequestPacket
                .openFull(findSelectedSlot(), selected, Minecraft.getMinecraft().thePlayer.openContainer.windowId));
    }

    public static MiniTerminalModel model() {
        return VIEWS.computeIfAbsent(selected, key -> new MiniTerminalModel());
    }

    public static MiniTerminalInventoryPanel overlay() {
        return overlay;
    }

    public static MiniTerminalInventoryPanel overlay(GuiScreen gui) {
        return overlay != null && gui == overlay.inventory() ? overlay : null;
    }

    static MiniTerminalLayout layoutState() {
        return LAYOUT;
    }

    public static boolean collapsed() {
        return collapsed;
    }

    public static String status() {
        return model().isLoading() ? "LOADING" : status;
    }

    public static String description() {
        String location = selectedSlot >= Platform.baublesSlotsOffset ? StatCollector.translateToLocalFormatted(
            "gtnl.meminiterminal.location.bauble",
            selectedSlot - Platform.baublesSlotsOffset + 1) : Integer.toString(selectedSlot + 1);
        return name + " [" + location + "] " + network;
    }
}
