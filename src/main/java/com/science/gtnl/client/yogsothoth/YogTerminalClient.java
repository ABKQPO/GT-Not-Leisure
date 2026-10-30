package com.science.gtnl.client.yogsothoth;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.cleanroommc.modularui.api.IMuiScreen;
import com.cleanroommc.modularui.screen.OpenScreenEvent;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.floatingwindow.FloatingWindowHost;
import com.science.gtnl.client.floatingwindow.FloatingWindows;
import com.science.gtnl.common.packet.CreativeScreenPacket;
import com.science.gtnl.common.yogsothoth.YogBaublesView;
import com.science.gtnl.common.yogsothoth.YogRecord;
import com.science.gtnl.common.yogsothoth.YogTerminalActionPacket;
import com.science.gtnl.common.yogsothoth.YogTerminalLocks;
import com.science.gtnl.common.yogsothoth.YogTerminals;
import com.science.gtnl.common.yogsothoth.YogToken;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(side = Side.CLIENT, phase = Phase.PRE)
public class YogTerminalClient {

    private static final YogTerminalLayout LAYOUT = new YogTerminalLayout();
    private static final Map<UUID, View> VIEWS = new Object2ObjectOpenHashMap<>();
    private static final Map<UUID, Assembly> ASSEMBLIES = new Object2ObjectOpenHashMap<>();
    private static final int USE_RETRY_TICKS = 20;
    private static NetHandlerPlayClient connection;
    private static YogTerminalPanel window;
    private static UUID selected;
    private static long nextWheel;
    private static int attackHeldTicks;
    private static final int WHEEL_HOLD_TICKS = 6;
    private static PendingUse pendingUse;
    private static volatile UUID sessionRecord;
    private static volatile int sessionIndex = -1;
    private static long modifications;
    private static List<Token> tokensCache;
    private static List<Token> windowCache;
    private static List<Token> windowSource;
    private static long tokensTime;
    private static long tokensModifications;
    private static List<YogBaublesView.Entry> entriesCache;
    private static long entriesTime;
    private static long entriesModifications;
    private static InventoryBaubles entriesBaubles;
    private static GuiScreen usedFrom;
    private static GuiScreen sessionScreen;
    private static boolean awaitingScreen;
    private static boolean creativeScreen;

    public static void init() {
        YogTerminalClientConfig.load(LAYOUT);
        FloatingWindows.register(YogTerminalClient::window);
        YogBaublesView.setClientEntries(YogTerminalClient::carriedEntries);
        YogTerminals.setClient(YogTerminalClient::stored, YogTerminalClient::opened);
    }

    public static class View {

        final UUID id;
        int generation;
        long revision = -1;
        int active = -1;
        boolean hidden;
        long structure = -1;
        final List<ItemStack> terminals = new ObjectArrayList<>();

        View(UUID id) {
            this.id = id;
        }

        ItemStack get(int index) {
            return index < 0 || index >= terminals.size() ? null : terminals.get(index);
        }

        int count() {
            return terminals.size();
        }
    }

    private static class Assembly {

        long revision;
        NBTTagCompound[] parts;
    }

    public static void receive(NBTTagCompound data) {
        checkConnection();
        UUID id;
        try {
            id = UUID.fromString(data.getString("id"));
        } catch (IllegalArgumentException ignored) {
            return;
        }
        int parts = Math.max(1, data.getInteger("parts"));
        int part = data.getInteger("part");
        if (part < 0 || part >= parts) return;
        long revision = data.getLong("rev");
        Assembly assembly = ASSEMBLIES.get(id);
        if (assembly == null || assembly.revision != revision || assembly.parts.length != parts) {
            assembly = new Assembly();
            assembly.revision = revision;
            assembly.parts = new NBTTagCompound[parts];
            ASSEMBLIES.put(id, assembly);
        }
        assembly.parts[part] = data;
        for (NBTTagCompound received : assembly.parts) if (received == null) return;
        ASSEMBLIES.remove(id);
        View view = VIEWS.computeIfAbsent(id, View::new);
        NBTTagCompound first = assembly.parts[0];
        int size = first.getInteger("size");
        long structure = first.getLong("structure");
        NBTTagList terminals = new NBTTagList();
        for (NBTTagCompound received : assembly.parts) {
            NBTTagList list = received.getTagList("terminals", NBT.TAG_COMPOUND);
            for (int index = 0; index < list.tagCount(); index++) terminals.appendTag(list.getCompoundTagAt(index));
        }
        if (first.getBoolean("patch")) {
            if (view.structure != structure || view.terminals.size() != size || !applyPatch(view, terminals)) {
                send(YogTerminalActionPacket.sync(id));
                return;
            }
        } else YogRecord.readTerminals(terminals, view.terminals);
        view.structure = structure;
        view.generation = first.getInteger("gen");
        view.revision = revision;
        view.active = first.getInteger("active");
        view.hidden = first.getBoolean("hidden");
        int session = first.getInteger("session");
        if (session >= 0 && id.equals(sessionRecord)) sessionIndex = session;
        modifications++;
    }

    private static boolean applyPatch(View view, NBTTagList entries) {
        for (int index = 0; index < entries.tagCount(); index++) {
            NBTTagCompound entry = entries.getCompoundTagAt(index);
            int slot = entry.getInteger("slot");
            ItemStack stack = ItemStack.loadItemStackFromNBT(entry);
            if (stack == null || slot < 0 || slot >= view.terminals.size()) return false;
            view.terminals.set(slot, stack);
        }
        return true;
    }

    private static void pruneViews() {
        Set<UUID> carried = new ObjectOpenHashSet<>();
        for (Token token : tokens()) carried.add(token.id());
        if (VIEWS.keySet()
            .removeIf(id -> !carried.contains(id) && !id.equals(sessionRecord))) modifications++;
    }

    static View view(UUID id) {
        return id == null ? null : VIEWS.get(id);
    }

    static View view(UUID id, int generation) {
        View view = id == null ? null : VIEWS.get(id);
        return view == null || view.generation != generation ? null : view;
    }

    private static List<ItemStack> stored(UUID id, int generation) {
        View view = view(id, generation);
        if (view == null) return Collections.emptyList();
        List<ItemStack> stored = new ObjectArrayList<>(view.terminals.size());
        for (int index = 0; index < view.terminals.size(); index++) {
            ItemStack stack = view.terminals.get(index);
            if (stack != null && index != view.active) stored.add(stack);
        }
        return stored;
    }

    private static List<YogBaublesView.Entry> carriedEntries(EntityPlayer player, InventoryBaubles baubles) {
        if (player != Minecraft.getMinecraft().thePlayer) return Collections.emptyList();
        long time = player.worldObj.getTotalWorldTime();
        if (entriesCache != null && entriesTime == time
            && entriesModifications == modifications
            && entriesBaubles == baubles) return entriesCache;
        entriesCache = computeEntries(player, baubles);
        entriesTime = time;
        entriesModifications = modifications;
        entriesBaubles = baubles;
        return entriesCache;
    }

    private static List<YogBaublesView.Entry> computeEntries(EntityPlayer player, InventoryBaubles baubles) {
        Set<View> views = new ObjectLinkedOpenHashSet<>();
        for (int slot = 0; slot < baubles.getSizeInventory(); slot++) addView(views, baubles.getStackInSlot(slot));
        for (ItemStack stack : player.inventory.mainInventory) addView(views, stack);
        if (views.isEmpty()) return Collections.emptyList();
        List<YogBaublesView.Entry> entries = new ObjectArrayList<>();
        for (View view : views) {
            for (int index = 0; index < view.terminals.size(); index++) {
                int entry = index;
                entries.add(new YogBaublesView.Entry() {

                    @Override
                    public ItemStack get() {
                        return entry == view.active ? null : view.get(entry);
                    }

                    @Override
                    public void set(EntityPlayer player, ItemStack stack) {}
                });
            }
        }
        return entries;
    }

    private static void addView(Set<View> views, ItemStack stack) {
        YogToken.Link link = YogToken.link(stack);
        View view = YogToken.isYog(stack) ? view(YogToken.id(stack), YogToken.generation(stack))
            : link == null ? null : view(link.id(), link.generation());
        if (view != null) views.add(view);
    }

    public static void session(UUID record, int index) {
        sessionRecord = record;
        sessionIndex = record == null ? -1 : index;
    }

    private static ItemStack opened(EntityPlayer player) {
        UUID record = sessionRecord;
        if (record == null || player != Minecraft.getMinecraft().thePlayer) return null;
        View view = VIEWS.get(record);
        return view == null ? null : view.get(sessionIndex);
    }

    private static void checkConnection() {
        NetHandlerPlayClient current = Minecraft.getMinecraft()
            .getNetHandler();
        if (connection == current) return;
        connection = current;
        VIEWS.clear();
        ASSEMBLIES.clear();
        modifications++;
        tokensCache = null;
        entriesCache = null;
        if (window != null) window.detach();
        window = null;
        selected = null;
        pendingUse = null;
        session(null, -1);
        creativeScreen = false;
        awaitingScreen = false;
        sessionScreen = null;
        YogTerminalLocks.clearClient();
    }

    record Token(UUID id, int generation, int location, ItemStack stack, boolean disguised) {

        View view() {
            return YogTerminalClient.view(id, generation);
        }
    }

    static List<Token> tokens() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return Collections.emptyList();
        long time = mc.thePlayer.worldObj.getTotalWorldTime();
        if (tokensCache == null || tokensTime != time || tokensModifications != modifications) {
            tokensCache = Collections.unmodifiableList(scanTokens(mc));
            tokensTime = time;
            tokensModifications = modifications;
        }
        return tokensCache;
    }

    private static List<Token> scanTokens(Minecraft mc) {
        List<Token> tokens = new ObjectArrayList<>();
        ItemStack[] main = mc.thePlayer.inventory.mainInventory;
        for (int slot = 0; slot < main.length; slot++) addToken(tokens, main[slot], slot);
        InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(mc.thePlayer);
        if (baubles != null) for (int slot = 0; slot < baubles.getSizeInventory(); slot++)
            addToken(tokens, baubles.getStackInSlot(slot), 100 + slot);
        return tokens;
    }

    private static void addToken(List<Token> tokens, ItemStack stack, int location) {
        if (YogToken.isYog(stack)) {
            tokens.add(new Token(YogToken.id(stack), YogToken.generation(stack), location, stack, false));
            return;
        }
        YogToken.Link link = YogToken.link(stack);
        if (link != null) tokens.add(new Token(link.id(), link.generation(), location, stack, true));
    }

    static List<Token> windowTokens() {
        List<Token> tokens = tokens();
        if (windowCache == null || windowSource != tokens) {
            List<Token> visible = new ObjectArrayList<>(tokens);
            visible.removeIf(token -> token.view() != null && token.view().hidden);
            windowCache = Collections.unmodifiableList(visible);
            windowSource = tokens;
        }
        return windowCache;
    }

    static Token token(UUID id) {
        if (id == null) return null;
        for (Token token : tokens()) if (id.equals(token.id())) return token;
        return null;
    }

    static Token selectedToken() {
        List<Token> tokens = windowTokens();
        if (tokens.isEmpty()) return null;
        if (selected != null) for (Token token : tokens) if (selected.equals(token.id())) return token;
        Minecraft mc = Minecraft.getMinecraft();
        int hand = mc.thePlayer.inventory.currentItem;
        Token best = tokens.get(0);
        for (Token token : tokens) {
            if (token.location() == hand) return token;
            if (token.location() >= 100 && best.location() < 100) best = token;
        }
        return best;
    }

    static void selectNext() {
        List<Token> tokens = windowTokens();
        if (tokens.size() < 2) return;
        Token current = selectedToken();
        int index = tokens.indexOf(current);
        Token next = tokens.get((index + 1) % tokens.size());
        selected = next.id();
        requestSync(next);
    }

    static void requestSync(Token token) {
        if (token != null && token.id() != null)
            ScienceNotLeisure.network.sendToServer(YogTerminalActionPacket.sync(token.id()));
    }

    static boolean isSessionEntry(View view, int index) {
        return view.id.equals(sessionRecord) && index == sessionIndex;
    }

    public static YogTerminalPanel window(GuiScreen gui) {
        return window != null && gui == window.inventory() ? window : null;
    }

    static YogTerminalLayout layout() {
        return LAYOUT;
    }

    static boolean collapsed() {
        return YogTerminalClientConfig.collapsed;
    }

    static void toggleWindow(Token token) {
        if (token != null) send(YogTerminalActionPacket.toggleWindow(token.id(), token.location()));
    }

    static void toggleCollapsed() {
        YogTerminalClientConfig.setCollapsed(!YogTerminalClientConfig.collapsed);
    }

    static void saveWindow() {
        YogTerminalClientConfig.saveWindow(LAYOUT);
    }

    static void closeWindow(YogTerminalPanel closing) {
        if (window != closing) return;
        saveWindow();
        window = null;
    }

    @SubscribeEvent
    public static void open(OpenScreenEvent event) {
        checkConnection();
        saveWindow();
        if (window != null) window.detach();
        window = null;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void initGui(InitGuiEvent.Post event) {
        if (!(event.gui instanceof IMuiScreen)) attach(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void initNativeGui(InitGuiEvent.Post event) {
        if (event.gui instanceof IMuiScreen) attach(event);
    }

    private static void attach(InitGuiEvent.Post event) {
        checkConnection();
        attach(event.gui);
    }

    private static void attach(GuiScreen gui) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != gui || window != null
            || YogTerminalScreenWidgets.isScreen(gui)
            || !(gui instanceof GuiContainer inventory)
            || mc.thePlayer == null
            || mc.playerController == null
            || inventory instanceof GuiInventory && mc.playerController.isInCreativeMode()
            || inventory instanceof GuiContainerCreative && !mc.playerController.isInCreativeMode()) return;
        Token token = selectedToken();
        if (token == null) return;
        FloatingWindowHost<YogTerminalPanel> host = FloatingWindowHost
            .create(inventory, "yog_sothoth_terminal", YogTerminalPanel::new, panel -> {});
        window = host.panel();
        host.open();
        requestSync(token);
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.phase == TickEvent.Phase.START) {
            checkConnection();
            return;
        }
        if (window != null && !window.isValid()) window = null;
        if (window != null && mc.currentScreen != window.inventory()) {
            saveWindow();
            window.detach();
            window = null;
        }
        if (window != null && selectedToken() == null) {
            saveWindow();
            window.closeWindow();
            window = null;
        } else if (window == null && mc.currentScreen != null) attach(mc.currentScreen);
        if (sessionRecord == null && pendingUse == null) awaitingScreen = false;
        if (pendingUse != null) tryUse();
        trackSessionScreen(mc);
        trackWheelHold(mc);
        if (mc.thePlayer != null && mc.thePlayer.ticksExisted % 200 == 0) pruneViews();
        boolean creative = mc.currentScreen instanceof GuiContainerCreative;
        if (creative != creativeScreen && mc.thePlayer != null) {
            creativeScreen = creative;
            ScienceNotLeisure.network.sendToServer(new CreativeScreenPacket(creative));
        }
    }

    private static void trackSessionScreen(Minecraft mc) {
        if (awaitingScreen && mc.currentScreen != null && mc.currentScreen != usedFrom) {
            awaitingScreen = false;
            sessionScreen = mc.currentScreen;
            ScienceNotLeisure.network.sendToServer(YogTerminalActionPacket.screen(true));
        } else if (sessionScreen != null && mc.currentScreen != sessionScreen) {
            sessionScreen = null;
            ScienceNotLeisure.network.sendToServer(YogTerminalActionPacket.screen(false));
        }
    }

    private static boolean holdsUsable() {
        ItemStack held = Minecraft.getMinecraft().thePlayer.getHeldItem();
        if (!YogToken.isYog(held)) return YogToken.link(held) != null;
        View view = view(YogToken.id(held), YogToken.generation(held));
        return view != null && view.count() > 0;
    }

    // Fired inside the input loops, before vanilla handles the drop and pick block keys in the same tick.
    @SubscribeEvent
    public static void keyInput(InputEvent.KeyInputEvent event) {
        handleWorldInput();
    }

    @SubscribeEvent
    public static void mouseInput(InputEvent.MouseInputEvent event) {
        handleWorldInput();
    }

    private static void handleWorldInput() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null || mc.thePlayer == null) return;
        ItemStack held = mc.thePlayer.getHeldItem();
        if (!YogToken.isYog(held) && YogToken.link(held) == null) return;
        if (GuiScreen.isShiftKeyDown() && holdsUsable()) {
            while (mc.gameSettings.keyBindDrop.isPressed())
                ScienceNotLeisure.network.sendToServer(YogTerminalActionPacket.dropFirst());
        }
    }

    private static void trackWheelHold(Minecraft mc) {
        if (mc.currentScreen != null || mc.thePlayer == null
            || !mc.inGameHasFocus
            || !mc.gameSettings.keyBindAttack.getIsKeyPressed()
            || !holdsUsable()) {
            attackHeldTicks = 0;
            return;
        }
        if (++attackHeldTicks == WHEEL_HOLD_TICKS) YogTerminalWheelScreen.open(mc.gameSettings.keyBindAttack);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void leftClick(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK
            && event.entityPlayer == Minecraft.getMinecraft().thePlayer
            && heldDisguise()) {
            event.setCanceled(true);
            event.useItem = Event.Result.DENY;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void attack(AttackEntityEvent event) {
        if (event.entityPlayer == Minecraft.getMinecraft().thePlayer && heldDisguise()) event.setCanceled(true);
    }

    private static boolean heldDisguise() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer != null && YogToken.link(mc.thePlayer.getHeldItem()) != null;
    }

    @SubscribeEvent
    public static void mouse(MouseEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null || mc.thePlayer == null) return;
        if (event.button == 2 && event.buttonstate) {
            ItemStack held = mc.thePlayer.getHeldItem();
            if (!YogToken.isYog(held) && YogToken.link(held) == null) return;
            event.setCanceled(true);
            send(YogTerminalActionPacket.openScreen());
            return;
        }
        if (event.dwheel == 0 || !mc.thePlayer.isSneaking()) return;
        if (!holdsUsable()) return;
        event.setCanceled(true);
        long now = System.nanoTime();
        if (now < nextWheel) return;
        nextWheel = now + 100_000_000L;
        ScienceNotLeisure.network.sendToServer(YogTerminalActionPacket.cycle(event.dwheel > 0));
    }

    private record PendingUse(UUID record, int index, boolean sneak, int[] ticks) {}

    static void clickEntry(GuiContainer gui, Token token, int index, int button) {
        if (token == null) return;
        View view = token.view();
        ItemStack entry = view == null ? null : view.get(index);
        if (button == 1) {
            if (entry != null) use(token, index);
            return;
        }
        if (button != 0) return;
        if (GuiScreen.isShiftKeyDown()) {
            if (entry != null) send(YogTerminalActionPacket.click(token.id(), token.location(), index, true));
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        InventoryPlayer inventory = mc.thePlayer.inventory;
        ItemStack cursor = inventory.getItemStack();
        if (cursor == null && entry == null || cursor != null && !YogToken.isStorable(cursor)) return;
        if (gui instanceof GuiContainerCreative && mc.playerController.isInCreativeMode()) {
            send(YogTerminalActionPacket.creativeClick(token.id(), token.location(), index, cursor));
        } else if (gui.inventorySlots == mc.thePlayer.openContainer) {
            send(YogTerminalActionPacket.click(token.id(), token.location(), index, false));
        } else if (entry != null) {
            send(YogTerminalActionPacket.click(token.id(), token.location(), index, true));
        }
    }

    static YogTerminalClient.Token heldToken() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return null;
        int hand = mc.thePlayer.inventory.currentItem;
        for (Token token : tokens()) if (token.location() == hand) return token;
        return null;
    }

    static void send(YogTerminalActionPacket packet) {
        ScienceNotLeisure.network.sendToServer(packet);
    }

    static void use(Token token, int index) {
        if (token == null || token.id() == null) return;
        ScienceNotLeisure.network
            .sendToServer(YogTerminalActionPacket.use(token.id(), token.location(), index, GuiScreen.isShiftKeyDown()));
    }

    public static void useReady(UUID record, int index, boolean sneak) {
        pendingUse = new PendingUse(record, index, sneak, new int[] { 0 });
        tryUse();
    }

    private static void tryUse() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;
        PendingUse use = pendingUse;
        if (player == null || mc.playerController == null || use == null) {
            pendingUse = null;
            return;
        }
        ItemStack held = player.getHeldItem();
        YogToken.Link link = YogToken.link(held);
        if (link == null || !link.id()
            .equals(use.record())) {
            if (++use.ticks()[0] > USE_RETRY_TICKS) pendingUse = null;
            return;
        }
        pendingUse = null;
        usedFrom = mc.currentScreen;
        awaitingScreen = true;
        sessionScreen = null;
        boolean sneak = use.sneak() && !player.isSneaking();
        if (sneak) {
            player.sendQueue.addToSendQueue(new C0BPacketEntityAction(player, 1));
            player.setSneaking(true);
        }
        try {
            mc.playerController.sendUseItem(player, mc.theWorld, held);
        } finally {
            if (sneak) {
                player.setSneaking(false);
                player.sendQueue.addToSendQueue(new C0BPacketEntityAction(player, 2));
            }
        }
    }
}
