package com.science.gtnl.client.metoolbox;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.cleanroommc.modularui.overlay.OverlayStack;
import com.cleanroommc.modularui.screen.OpenScreenEvent;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.ItemMEToolbox;
import com.science.gtnl.common.metoolbox.ToolboxRequestPacket;
import com.science.gtnl.common.metoolbox.ToolboxSessionService;

import appeng.api.config.PinsRows;
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
public final class ToolboxClient {

    private static final int MAX_CACHED_VIEWS = 8;
    private static final ToolboxClient INSTANCE = new ToolboxClient();
    private static final Map<UUID, ToolboxTerminalModel> VIEWS = new LinkedHashMap<>(MAX_CACHED_VIEWS, 0.75f, true) {

        @Override
        protected boolean removeEldestEntry(Map.Entry<UUID, ToolboxTerminalModel> eldest) {
            return size() > MAX_CACHED_VIEWS;
        }
    };

    private static final ToolboxLayout LAYOUT = new ToolboxLayout();
    private static final Set<Long> RETIRED_SESSIONS = new LinkedHashSet<>();
    private static NetHandlerPlayClient connection;
    private static ToolboxInventoryOverlay overlay;
    private static DrawScreenEvent.Post deferredTooltipEvent;
    private static Runnable deferredTooltipRender;
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
    private static boolean waiting;
    private static boolean pinRowsPending;
    private static int pinRowsPendingTicks;
    private static PinsRows requestedCraftingPins;
    private static PinsRows requestedPlayerPins;
    private static long requestedPinRevision;
    private static int settingsRetryTicks;
    private static String status = "OFFLINE";
    private static String name = "ME";
    private static String network = "";

    private ToolboxClient() {}

    public static void init() {
        ToolboxClientConfig.load(LAYOUT);
        ToolboxBogoCompat.init();
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(INSTANCE);
        ToolboxNEIAdapter nei = new ToolboxNEIAdapter();
        API.registerNEIGuiHandler(nei);

        GuiContainerManager.objectHandlers.addFirst(nei);
        GuiContainerManager.addDrawHandler(nei);
        GuiContainerManager.addTooltipHandler(nei);
        GuiContainerManager.addSlotClickHandler(nei);
    }

    public static boolean deferChromaticTooltip(DrawScreenEvent.Post event, Runnable renderTooltip) {
        if (overlay == null || event.gui != overlay.inventory() || Minecraft.getMinecraft().currentScreen != event.gui)
            return false;
        deferredTooltipEvent = event;
        deferredTooltipRender = renderTooltip;
        return true;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void drawDeferredTooltip(DrawScreenEvent.Post event) {
        if (deferredTooltipEvent != event) return;
        Runnable renderTooltip = deferredTooltipRender;
        deferredTooltipEvent = null;
        deferredTooltipRender = null;
        if (renderTooltip != null && Minecraft.getMinecraft().currentScreen == event.gui) renderTooltip.run();
    }

    @SubscribeEvent
    public void open(OpenScreenEvent event) {
        deferredTooltipEvent = null;
        deferredTooltipRender = null;
        ToolboxClientConfig.saveWindow(LAYOUT);
        checkConnection();
        if (selected != null && event.getScreen() instanceof GuiContainer gui
            && gui.inventorySlots instanceof AEBaseContainer nativeContainer
            && nativeContainer.getTarget() instanceof IPortableCell portable
            && selected.equals(ItemMEToolbox.getToolboxId(portable.getItemStack()))) model().handOffSettings();
        if (session != 0 || waiting || sessionPrepared) closeSession();
        overlay = null;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void initGui(InitGuiEvent.Post event) {
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
            || ToolboxBogoCompat.isTerminalScreen(inventory)) return;
        int slot = findSelectedSlot();
        if (slot < 0 && !hasToolbox()) return;
        if (selected == null && slot >= 0) {
            selected = ItemMEToolbox.getToolboxId(ItemMEToolbox.getToolboxStack(mc.thePlayer, slot));
        }
        selectedSlot = slot;
        ToolboxTerminalModel model = model();
        if (slot >= 0) model.readSettings(ItemMEToolbox.getToolboxStack(mc.thePlayer, slot));
        model.beginSession();
        overlay = new ToolboxInventoryOverlay(inventory, model);
        overlay.constructOverlay(inventory);
        OverlayStack.open(overlay);
        sessionPrepared = true;
        retryTicks = 0;
    }

    static void closeOverlay(ToolboxInventoryOverlay closing) {
        checkConnection();
        if (overlay != closing) return;
        deferredTooltipEvent = null;
        deferredTooltipRender = null;
        ToolboxClientConfig.saveWindow(LAYOUT);
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
            ToolboxClientConfig.saveWindow(LAYOUT);
            if (session != 0 || waiting || sessionPrepared) closeSession();
            overlay = null;
            return;
        }
        if (sessionContainer != null && !hasCurrentContainer()) {
            closeSession();
            sessionPrepared = true;
            retryTicks = 0;
        }
        overlay.syncLayout();
        ensureSession();
        if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) ToolboxClientConfig.saveWindow(LAYOUT);
        model().update();
        if (model().hasPendingSettings()) {
            if (++settingsRetryTicks >= 40) submitSettings(false);
        } else settingsRetryTicks = 0;
        if (pinRowsPending && ++pinRowsPendingTicks >= 40) {
            submitPinRows();
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
        ToolboxClientConfig.saveWindow(LAYOUT);
        connection = current;
        session = 0;
        sessionContainer = null;
        action = 0;
        requestId = 0;
        waiting = false;
        sessionPrepared = false;
        sessionTicks = 0;
        pinRowsPending = false;
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
            ScienceNotLeisure.network.sendToServer(ToolboxRequestPacket.end(id));
        }
        RETIRED_SESSIONS.add(id);
        if (RETIRED_SESSIONS.size() > 128) RETIRED_SESSIONS.remove(
            RETIRED_SESSIONS.iterator()
                .next());
    }

    private static boolean hasToolbox() {
        Minecraft mc = Minecraft.getMinecraft();
        return ItemMEToolbox.findFirstToolboxSlot(mc.thePlayer) >= 0;
    }

    private static int findSelectedSlot() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return -1;
        if (selected != null) return ItemMEToolbox.findToolboxSlot(mc.thePlayer, selected);
        return ItemMEToolbox.findFirstToolboxSlot(mc.thePlayer);
    }

    public static void begin() {
        Minecraft mc = Minecraft.getMinecraft();
        if (waiting || mc.thePlayer == null
            || overlay == null
            || !overlay.getPanelManager()
                .isOpen()
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
            .sendToServer(ToolboxRequestPacket.begin(selectedSlot, selected, ++requestId, sessionWindowId));
    }

    private static boolean hasCurrentContainer() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer != null && sessionContainer != null
            && mc.thePlayer.openContainer == sessionContainer
            && sessionContainer.windowId == sessionWindowId;
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
        pinRowsPending = false;
        model().closeSession();
    }

    public static void receive(NBTTagCompound data) {
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
                ToolboxTerminalModel view = VIEWS.get(UUID.fromString(data.getString("toolbox")));
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
            id = UUID.fromString(data.getString("toolbox"));
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
                ToolboxTerminalModel old = model();
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
        if ("CATALOG".equals(kind) && !model().catalog(data)) {
            ScienceNotLeisure.network.sendToServer(ToolboxRequestPacket.resync(session));
        } else if ("PINS".equals(kind)) {
            if (!model().pins(data)) {
                ScienceNotLeisure.network.sendToServer(ToolboxRequestPacket.resync(session));
            } else if (pinRowsPending && data.getBoolean("last")) {
                if (model().craftingPinsRows() == requestedCraftingPins
                    && model().playerPinsRows() == requestedPlayerPins) pinRowsPending = false;
                else if (model().pinRevision() != requestedPinRevision) submitPinRows();
            }
        }
    }

    public static void saveSettings() {
        submitSettings(true);
    }

    public static boolean pickupToME() {
        int slot = findSelectedSlot();
        return slot >= 0 && ItemMEToolbox
            .isPickupToMEEnabled(ItemMEToolbox.getToolboxStack(Minecraft.getMinecraft().thePlayer, slot));
    }

    public static void togglePickupToME() {
        int slot = findSelectedSlot();
        if (slot < 0) return;
        UUID toolbox = ItemMEToolbox
            .getToolboxId(ItemMEToolbox.getToolboxStack(Minecraft.getMinecraft().thePlayer, slot));
        if (toolbox == null) return;
        ScienceNotLeisure.network.sendToServer(
            ToolboxRequestPacket.pickupToggle(
                session,
                ++action,
                slot,
                toolbox,
                Minecraft.getMinecraft().thePlayer.openContainer.windowId));
    }

    private static void submitSettings(boolean changed) {
        ToolboxTerminalModel view = model();
        long id = ++action;
        settingsRetryTicks = 0;

        if (changed) view.settingsSubmitted(id);
        int slot = findSelectedSlot();
        if (selected == null || slot < 0) return;
        ScienceNotLeisure.network.sendToServer(
            ToolboxRequestPacket.settings(
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

    public static void setPinRows(PinsRows crafting, PinsRows player) {
        if (pinRowsPending || !model().isOnline() || session == 0 || !hasCurrentContainer()) return;
        pinRowsPending = true;
        requestedCraftingPins = crafting;
        requestedPlayerPins = player;
        submitPinRows();
    }

    private static void submitPinRows() {
        if (!pinRowsPending || !model().isOnline() || session == 0 || !hasCurrentContainer()) return;
        pinRowsPendingTicks = 0;
        requestedPinRevision = model().pinRevision();
        ScienceNotLeisure.network.sendToServer(
            ToolboxRequestPacket
                .pinsRows(session, ++action, requestedPinRevision, requestedCraftingPins, requestedPlayerPins));
    }

    public static void send(MonitorableAction intent, IAEStack<?> target, int pin) {
        ToolboxTerminalModel model = model();
        if (!model.isOnline() || session == 0 || !hasCurrentContainer()) return;
        ScienceNotLeisure.network.sendToServer(
            ToolboxRequestPacket.action(
                session,
                ++action,
                model.catalogRevision(),
                model.pinRevision(),
                model.entryId(target),
                pin,
                intent,
                intent == MonitorableAction.SET_PIN && AEBaseGui.isCtrlKeyDown()));
    }

    public static void toggleCollapsed() {
        collapsed = !collapsed;
        if (collapsed) closeSession();
        else retryTicks = 0;

    }

    public static boolean shiftClickToME(Slot slot, int mouseButton, int clickMode) {
        if (mouseButton != 0 && mouseButton != 1) return false;

        if ((clickMode == 0 || clickMode == 1) && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            return sendBogoInventory(slot, ToolboxRequestPacket.DepositMode.NATIVE_REGION);
        }
        if (clickMode != 1) return false;
        return sendBogoInventory(slot, ToolboxRequestPacket.DepositMode.STACK);
    }

    public static boolean sendBogoInventory(Slot slot, ToolboxRequestPacket.DepositMode mode) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!ToolboxClientConfig.shiftClickToME || slot == null
            || mc.thePlayer == null
            || overlay == null
            || mc.currentScreen != overlay.inventory()
            || overlay.inventory().inventorySlots != mc.thePlayer.openContainer
            || !overlay.layout()
                .expanded()
            || !containsSlot(mc.thePlayer.openContainer, slot)
            || ItemMEToolbox.isToolbox(slot.getStack())) return false;

        int inventorySlot = ToolboxSessionService.playerInventoryIndex(mc.thePlayer, slot);
        if (inventorySlot < 0) return false;

        if (!slot.getHasStack() && mode != ToolboxRequestPacket.DepositMode.NATIVE_REGION) return true;
        if (model().isOnline() && session != 0 && hasCurrentContainer()) {
            ScienceNotLeisure.network
                .sendToServer(ToolboxRequestPacket.inventoryDeposit(session, ++action, inventorySlot, mode));
        }
        return true;
    }

    private static boolean containsSlot(Container container, Slot slot) {
        int index = slot.slotNumber;
        return index >= 0 && index < container.inventorySlots.size() && container.inventorySlots.get(index) == slot
            || container.inventorySlots.contains(slot);
    }

    public static void sendBogoExtract(IAEStack<?> stack, int nativePin, boolean emptyOnly, boolean single) {
        ToolboxTerminalModel view = model();
        if (!view.isOnline() || session == 0 || !hasCurrentContainer()) return;
        ScienceNotLeisure.network.sendToServer(
            ToolboxRequestPacket.inventoryExtract(
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
        List<Integer> slots = ItemMEToolbox.getToolboxSlots(mc.thePlayer);
        int start = slots.indexOf(findSelectedSlot());
        for (int step = 1; step <= slots.size(); step++) {
            int slot = slots.get((start + step) % slots.size());
            ItemStack stack = ItemMEToolbox.getToolboxStack(mc.thePlayer, slot);
            if (!ItemMEToolbox.isToolbox(stack)) continue;
            closeSession();
            selected = ItemMEToolbox.getToolboxId(stack);
            selectedSlot = slot;
            model().readSettings(stack);
            overlay.setModel(model());
            begin();
            retryTicks = 20;
            return;
        }
    }

    public static void openFull() {
        if (model().hasPendingSettings()) submitSettings(false);
        closeSession();
        ScienceNotLeisure.network.sendToServer(
            ToolboxRequestPacket
                .openFull(findSelectedSlot(), selected, Minecraft.getMinecraft().thePlayer.openContainer.windowId));
    }

    public static ToolboxTerminalModel model() {
        return VIEWS.computeIfAbsent(selected, key -> new ToolboxTerminalModel());
    }

    public static ToolboxInventoryOverlay overlay() {
        return overlay;
    }

    static ToolboxLayout layoutState() {
        return LAYOUT;
    }

    public static boolean collapsed() {
        return collapsed;
    }

    public static String status() {
        return model().isLoading() ? "LOADING" : status;
    }

    public static String description() {
        String location = selectedSlot >= Platform.baublesSlotsOffset ? StatCollector
            .translateToLocalFormatted("gtnl.metoolbox.location.bauble", selectedSlot - Platform.baublesSlotsOffset + 1)
            : Integer.toString(selectedSlot + 1);
        return name + " [" + location + "] " + network;
    }
}
