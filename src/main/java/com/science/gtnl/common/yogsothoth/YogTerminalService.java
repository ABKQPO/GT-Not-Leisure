package com.science.gtnl.common.yogsothoth;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.IYogTerminal;
import com.science.gtnl.common.item.items.ItemYogSothothTerminal;
import com.science.gtnl.common.packet.YogTerminalStatePacket;
import com.science.gtnl.common.packet.YogTerminalSyncPacket;
import com.science.gtnl.utils.CreativeCursor;

import appeng.api.AEApi;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@EventBusSubscriber
public class YogTerminalService {

    private static final int USE_TIMEOUT_TICKS = 20;
    private static final int REFRESH_INTERVAL = 10;
    private static final Map<UUID, Session> SESSIONS = new Object2ObjectOpenHashMap<>();
    // Per player and record: the structure and revision the client has.
    private static final Map<UUID, Map<UUID, long[]>> SENT = new Object2ObjectOpenHashMap<>();
    private static final Map<UUID, Set<UUID>> HELD = new Object2ObjectOpenHashMap<>();
    private static final List<Place> CLICK_CONVERTED = new ObjectArrayList<>();
    private static final Map<EntityPlayer, CarriedEntries> CARRIED = new WeakHashMap<>();
    private static int clickDepth;
    private static int openingSlot = Integer.MIN_VALUE;

    public static class Place {

        static final int MAIN = 0, BAUBLE = 1, CURSOR = 2, SLOT = 3;

        final EntityPlayer player;
        final int kind;
        final int index;
        final Slot slot;

        private Place(EntityPlayer player, int kind, int index, Slot slot) {
            this.player = player;
            this.kind = kind;
            this.index = index;
            this.slot = slot;
        }

        static Place main(EntityPlayer player, int index) {
            return new Place(player, MAIN, index, null);
        }

        static Place bauble(EntityPlayer player, int index) {
            return new Place(player, BAUBLE, index, null);
        }

        static Place cursor(EntityPlayer player) {
            return new Place(player, CURSOR, -1, null);
        }

        static Place slot(EntityPlayer player, Slot slot) {
            if (slot.inventory == player.inventory && slot.getSlotIndex() < player.inventory.mainInventory.length)
                return main(player, slot.getSlotIndex());
            return new Place(player, SLOT, slot.slotNumber, slot);
        }

        ItemStack get() {
            return switch (kind) {
                case MAIN -> player.inventory.mainInventory[index];
                case BAUBLE -> {
                    InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
                    yield baubles == null || index >= baubles.getSizeInventory() ? null : baubles.getStackInSlot(index);
                }
                case CURSOR -> player.inventory.getItemStack();
                default -> slot.getStack();
            };
        }

        void set(ItemStack stack) {
            switch (kind) {
                case MAIN -> player.inventory.setInventorySlotContents(index, stack);
                case BAUBLE -> {
                    InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
                    if (baubles != null && index < baubles.getSizeInventory())
                        baubles.setInventorySlotContents(index, stack);
                }
                case CURSOR -> player.inventory.setItemStack(stack);
                default -> slot.putStack(stack);
            }
        }

        void changedInPlace() {
            if (kind == BAUBLE) {
                InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
                if (baubles != null) baubles.syncSlotToClients(index);
            } else if (kind == MAIN) player.inventory.markDirty();
        }

        boolean isMain(int slot) {
            return kind == MAIN && index == slot;
        }
    }

    static List<Place> places(EntityPlayer player) {
        List<Place> places = new ObjectArrayList<>(48);
        for (int index = 0; index < player.inventory.mainInventory.length; index++)
            places.add(Place.main(player, index));
        InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
        if (baubles != null)
            for (int index = 0; index < baubles.getSizeInventory(); index++) places.add(Place.bauble(player, index));
        places.add(Place.cursor(player));
        return places;
    }

    static Place findToken(EntityPlayer player, UUID id) {
        if (id == null) return null;
        for (Place place : places(player)) {
            if (id.equals(YogToken.storageId(place.get()))) return place;
        }
        return null;
    }

    static Place findLinked(EntityPlayer player, YogRecord record) {
        for (Place place : places(player)) {
            YogToken.Link link = YogToken.link(place.get());
            if (link != null && link.id()
                .equals(record.id) && link.index() == record.active && link.generation() == record.generation)
                return place;
        }
        return null;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onWorldLoad(WorldEvent.Load event) {
        if (event.world.isRemote || event.world.provider.dimensionId != 0) return;
        YogTerminalStorage.load(event.world);
        SESSIONS.clear();
        SENT.clear();
        HELD.clear();
        for (YogRecord record : YogTerminalStorage.all()) {
            if (record.holder != null && record.active >= 0)
                HELD.computeIfAbsent(record.holder, ignored -> new ObjectOpenHashSet<>())
                    .add(record.id);
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.world.isRemote || event.world.provider.dimensionId != 0) return;
        YogTerminalStorage.unload();
        SESSIONS.clear();
        SENT.clear();
        HELD.clear();
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        SENT.remove(event.player.getUniqueID());
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        CreativeCursor.clear(event.player.getUniqueID());
        SESSIONS.remove(event.player.getUniqueID());
        SENT.remove(event.player.getUniqueID());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof EntityPlayerMP player)
            || !YogTerminalStorage.loaded()) return;
        if (clickDepth != 0) {
            clickDepth = 0;
            CLICK_CONVERTED.clear();
        }
        Session session = SESSIONS.get(player.getUniqueID());
        if (session != null) updateSession(player, session);
        boolean scanContainer = player.openContainer != player.inventoryContainer
            && player.ticksExisted % REFRESH_INTERVAL == 0;
        sweep(player, scanContainer ? player.openContainer : null);
        if (player.ticksExisted % REFRESH_INTERVAL == 0) refresh(player);
    }

    static void sweep(EntityPlayer player, Container container) {
        Set<UUID> previouslyHeld = HELD.get(player.getUniqueID());
        int links = countLinks(player);
        if ((previouslyHeld == null || previouslyHeld.isEmpty()) && links == 0) return;
        if (container == null && links == disguisesInPlace(player, previouslyHeld)) return;
        List<Place> places = places(player);
        if (container != null) {
            for (Slot slot : container.inventorySlots) {
                Place place = Place.slot(player, slot);
                if (place.kind == Place.SLOT) places.add(place);
            }
        }
        Set<UUID> held = HELD.computeIfAbsent(player.getUniqueID(), ignored -> new ObjectOpenHashSet<>());
        Set<UUID> seen = new ObjectOpenHashSet<>();
        for (Place place : places) {
            ItemStack stack = place.get();
            YogToken.Link link = YogToken.link(stack);
            if (link == null) continue;
            YogRecord record = YogTerminalStorage.get(link.id());
            if (record == null || record.generation != link.generation()) {
                YogToken.detach(stack);
                converted(place);
                continue;
            }
            boolean current = record.active == link.index() && player.getUniqueID()
                .equals(record.holder);
            if (current && place.isMain(link.home())) {
                if (record.terminals.get(link.index()) != stack) record.terminals.set(link.index(), stack);
                seen.add(record.id);
                held.add(record.id);
                continue;
            }
            if (current) {
                retract(player, record, place);
                seen.add(record.id);
            } else place.set(baseOf(link));
            converted(place);
        }
        for (Iterator<UUID> iterator = held.iterator(); iterator.hasNext();) {
            UUID id = iterator.next();
            if (seen.contains(id)) continue;
            YogRecord record = YogTerminalStorage.get(id);
            iterator.remove();
            if (record == null || record.active < 0) continue;
            // Lost without passing any return path; the original may still exist, so keep a detached copy.
            record.terminals.set(record.active, YogToken.unlinkedCopy(record.terminals.get(record.active)));
            record.clearActive();
            record.changed();
        }
    }

    private static int countLinks(EntityPlayer player) {
        int links = 0;
        for (ItemStack stack : player.inventory.mainInventory) if (hasLink(stack)) links++;
        if (hasLink(player.inventory.getItemStack())) links++;
        InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
        if (baubles != null) for (int slot = 0; slot < baubles.getSizeInventory(); slot++)
            if (hasLink(baubles.getStackInSlot(slot))) links++;
        return links;
    }

    // Disguises still in their slot and still the stored object; -1 when any held disguise is elsewhere.
    private static int disguisesInPlace(EntityPlayer player, Set<UUID> held) {
        if (held == null) return 0;
        for (UUID id : held) {
            YogRecord record = YogTerminalStorage.get(id);
            if (record == null || record.active < 0
                || record.home < 0
                || record.home >= player.inventory.mainInventory.length) return -1;
            ItemStack stack = player.inventory.mainInventory[record.home];
            YogToken.Link link = YogToken.link(stack);
            if (link == null || !link.id()
                .equals(id)
                || link.index() != record.active
                || link.generation() != record.generation
                || record.terminals.get(record.active) != stack) return -1;
        }
        return held.size();
    }

    private static boolean hasLink(ItemStack stack) {
        return stack != null && stack.getTagCompound() != null
            && stack.getTagCompound()
                .hasKey(YogToken.LINK_TAG);
    }

    private static void converted(Place place) {
        if (clickDepth > 0) CLICK_CONVERTED.add(place);
    }

    private static ItemStack baseOf(YogToken.Link link) {
        ItemStack base = link.restoreBase();
        if (YogToken.isYog(base)) return base;
        base = new ItemStack(ItemYogSothothTerminal.INSTANCE);
        YogToken.bind(base, link.id(), link.generation());
        return base;
    }

    static void retract(EntityPlayer player, YogRecord record) {
        if (record.active < 0) return;
        Place place = findLinked(player, record);
        if (place != null) retract(player, record, place);
        else {
            record.terminals.set(record.active, YogToken.unlinkedCopy(record.terminals.get(record.active)));
            record.clearActive();
            record.changed();
        }
        Set<UUID> held = HELD.get(player.getUniqueID());
        if (held != null) held.remove(record.id);
    }

    private static void retract(EntityPlayer player, YogRecord record, Place place) {
        ItemStack stack = place.get();
        YogToken.Link link = YogToken.link(stack);
        YogToken.detach(stack);
        record.terminals.set(record.active, stack);
        place.set(link == null ? null : record.base != null ? record.base : baseOf(link));
        record.clearActive();
        record.changed();
        Set<UUID> held = HELD.get(player.getUniqueID());
        if (held != null) held.remove(record.id);
    }

    public static boolean blocksClick(Container container, int slotId, int button, int mode, EntityPlayer player) {
        IntSet locked = player.worldObj.isRemote ? YogTerminalLocks.client() : locks(player);
        if (locked.isEmpty()) return false;
        if (mode == 2 && locked.contains(button)) return true;
        if (slotId < 0 || slotId >= container.inventorySlots.size()) return false;
        Slot slot = container.getSlot(slotId);
        return slot != null && slot.inventory == player.inventory && locked.contains(slot.getSlotIndex());
    }

    public static void beforeClick(EntityPlayer player) {
        if (!player.worldObj.isRemote) clickDepth++;
    }

    public static void afterClick(Container container, EntityPlayer player) {
        if (player.worldObj.isRemote || !(player instanceof EntityPlayerMP serverPlayer)) return;
        if (--clickDepth > 0 || !YogTerminalStorage.loaded()) return;
        CARRIED.remove(player);
        clickDepth = 1;
        try {
            sweep(player, container);
        } finally {
            clickDepth = 0;
        }
        if (CLICK_CONVERTED.isEmpty()) return;
        // The click is acknowledged as predicted, so slot changes would not be sent otherwise.
        for (Place place : CLICK_CONVERTED) {
            if (place.kind == Place.CURSOR) continue;
            Slot slot = place.kind == Place.SLOT ? place.slot : findSlot(container, player, place);
            if (slot != null) serverPlayer.playerNetServerHandler
                .sendPacket(new S2FPacketSetSlot(container.windowId, slot.slotNumber, place.get()));
        }
        CLICK_CONVERTED.clear();
        CreativeCursor.sync(serverPlayer);
    }

    private static Slot findSlot(Container container, EntityPlayer player, Place place) {
        if (place.kind != Place.MAIN) return null;
        for (Slot slot : container.inventorySlots) {
            if (slot.inventory == player.inventory && slot.getSlotIndex() == place.index) return slot;
        }
        return null;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onToss(ItemTossEvent event) {
        if (!event.player.worldObj.isRemote && YogTerminalStorage.loaded()) returnDropped(event.entityItem);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onJoinWorld(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && event.entity instanceof EntityItem item && YogTerminalStorage.loaded())
            returnDropped(item);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDrops(PlayerDropsEvent event) {
        if (event.entityPlayer.worldObj.isRemote || !YogTerminalStorage.loaded()) return;
        if (SESSIONS.remove(event.entityPlayer.getUniqueID()) != null
            && event.entityPlayer instanceof EntityPlayerMP player)
            ScienceNotLeisure.network.sendTo(YogTerminalStatePacket.ended(), player);
        for (EntityItem item : event.drops) returnDropped(item);
    }

    private static void returnDropped(EntityItem item) {
        ItemStack stack = item.getEntityItem();
        YogToken.Link link = YogToken.link(stack);
        if (link == null) return;
        YogRecord record = YogTerminalStorage.get(link.id());
        if (record == null || record.generation != link.generation()) {
            YogToken.detach(stack);
            item.setEntityItemStack(stack);
            return;
        }
        if (record.active == link.index()) {
            YogToken.detach(stack);
            record.terminals.set(record.active, stack);
            UUID holder = record.holder;
            record.clearActive();
            record.changed();
            if (holder != null && HELD.containsKey(holder)) HELD.get(holder)
                .remove(record.id);
        }
        item.setEntityItemStack(baseOf(link));
    }

    private static void refresh(EntityPlayerMP player) {
        Set<UUID> visible = new ObjectOpenHashSet<>();
        for (Place place : places(player)) {
            ItemStack stack = place.get();
            if (YogToken.isYog(stack)) {
                YogRecord record = touch(stack, player, place);
                if (record != null) visible.add(record.id);
            } else {
                YogToken.Link link = YogToken.link(stack);
                if (link != null) visible.add(link.id());
            }
        }
        Map<UUID, long[]> sent = SENT
            .computeIfAbsent(player.getUniqueID(), ignored -> new Object2ObjectOpenHashMap<>());
        sent.keySet()
            .retainAll(visible);
        for (UUID id : visible) {
            YogRecord record = YogTerminalStorage.get(id);
            if (record == null) continue;
            record.detectChanges();
            long[] known = sent.get(id);
            if (known == null || known[1] != record.revision) sync(player, record);
        }
    }

    public static YogRecord touch(ItemStack stack, EntityPlayer player) {
        YogRecord record = touch(stack, player, null);
        if (player != null) player.inventory.markDirty();
        return record;
    }

    static YogRecord touch(ItemStack stack, EntityPlayer player, Place place) {
        if (!YogTerminalStorage.loaded() || !YogToken.isYog(stack)) return null;
        boolean changed = false;
        UUID id = YogToken.id(stack);
        YogRecord record = YogTerminalStorage.get(id);
        if (id != null && (record == null || record.generation != YogToken.generation(stack))) {
            if (record != null) mergePending(stack, record);
            YogToken.unbind(stack);
            record = null;
            changed = true;
        }
        if (YogToken.pending(stack) != null) {
            if (record == null) record = bind(stack, player);
            mergePending(stack, record);
            changed = true;
        }
        if (changed && place != null) place.changedInPlace();
        return record;
    }

    private static YogRecord bind(ItemStack stack, EntityPlayer player) {
        YogRecord record = YogTerminalStorage.create(player == null ? null : player.getUniqueID());
        YogToken.bind(stack, record.id, record.generation);
        return record;
    }

    private static void mergePending(ItemStack stack, YogRecord record) {
        NBTTagList batches = YogToken.pending(stack);
        YogToken.clearPending(stack);
        if (batches == null) return;
        YogToken.bumpNonce(stack);
        for (int index = 0; index < batches.tagCount(); index++) {
            NBTTagCompound batch = batches.getCompoundTagAt(index);
            if (!record.batches.add(batch.getString("batch"))) continue;
            NBTTagList items = batch.getTagList("items", NBT.TAG_COMPOUND);
            for (int item = 0; item < items.tagCount(); item++) {
                ItemStack terminal = ItemStack.loadItemStackFromNBT(items.getCompoundTagAt(item));
                if (terminal != null) record.add(terminal);
            }
        }
        record.changed();
    }

    static void sync(EntityPlayerMP player, YogRecord record) {
        if (player.playerNetServerHandler == null) return;
        Map<UUID, long[]> sent = SENT
            .computeIfAbsent(player.getUniqueID(), ignored -> new Object2ObjectOpenHashMap<>());
        long[] known = sent.get(record.id);
        long since = known != null && known[0] == record.structure ? known[1] : -1;
        Session session = SESSIONS.get(player.getUniqueID());
        int opened = session != null && session.record.equals(record.id) ? record.terminals.indexOf(session.terminal)
            : -1;
        sent.put(record.id, new long[] { record.structure, record.revision });
        for (YogTerminalSyncPacket packet : YogTerminalSyncPacket.of(record, since, opened))
            ScienceNotLeisure.network.sendTo(packet, player);
    }

    public static class Session {

        final UUID record;
        final ItemStack terminal;
        final Container before;
        final IntSet locked;
        Container opened;
        boolean clientScreen;
        // Disguise to restore when nothing opened: the previous active entry, -1 for the bare Yog-Sothoth terminal.
        int rollback = Integer.MIN_VALUE;
        int ticks;

        Session(UUID record, ItemStack terminal, Container before, IntSet locked) {
            this.record = record;
            this.terminal = terminal;
            this.before = before;
            this.locked = locked;
        }
    }

    static IntSet locks(EntityPlayer player) {
        Session session = SESSIONS.get(player.getUniqueID());
        return session == null ? IntSets.emptySet() : session.locked;
    }

    static ItemStack opened(EntityPlayer player) {
        Session session = SESSIONS.get(player.getUniqueID());
        return session == null ? null : session.terminal;
    }

    public static int redirectSlot(int slot) {
        return slot == Integer.MIN_VALUE && openingSlot != Integer.MIN_VALUE ? openingSlot : slot;
    }

    private static void updateSession(EntityPlayerMP player, Session session) {
        session.ticks++;
        if (session.opened == null) {
            if (player.openContainer != session.before) session.opened = player.openContainer;
            else if (!session.clientScreen && session.ticks > USE_TIMEOUT_TICKS) endSession(player);
        } else if (player.openContainer != session.opened) endSession(player);
    }

    static void endSession(EntityPlayer player) {
        Session session = SESSIONS.remove(player.getUniqueID());
        if (session == null || !(player instanceof EntityPlayerMP serverPlayer)) return;
        ScienceNotLeisure.network.sendTo(YogTerminalStatePacket.ended(), serverPlayer);
        YogRecord record = YogTerminalStorage.get(session.record);
        if (record != null && session.opened == null && !session.clientScreen) rollback(serverPlayer, record, session);
        if (record != null) sync(serverPlayer, record);
    }

    private static void rollback(EntityPlayerMP player, YogRecord record, Session session) {
        if (session.rollback == Integer.MIN_VALUE || record.active < 0
            || record.terminals.indexOf(session.terminal) != record.active) return;
        int hand = record.home;
        retract(player, record);
        int previous = session.rollback;
        if (previous >= 0 && previous < record.size() && hand >= 0) disguise(player, record, previous, hand);
        sendInventory(player);
    }

    private static void sendInventory(EntityPlayerMP player) {
        player.inventoryContainer.detectAndSendChanges();
        if (player.openContainer != player.inventoryContainer) player.openContainer.detectAndSendChanges();
        CreativeCursor.sync(player);
    }

    private static YogRecord resolve(EntityPlayerMP player, UUID id, int location, boolean create) {
        Place place = locate(player, id, location);
        if (place == null) return null;
        ItemStack stack = place.get();
        YogRecord record = YogToken.isYog(stack) ? touch(stack, player, place)
            : YogTerminalStorage.get(YogToken.storageId(stack));
        if (record == null && create && YogToken.isYog(stack)) {
            record = bind(stack, player);
            place.changedInPlace();
        }
        return record;
    }

    private static Place locate(EntityPlayerMP player, UUID id, int location) {
        Place place = location >= 100 ? Place.bauble(player, location - 100)
            : location >= 0 && location < player.inventory.mainInventory.length ? Place.main(player, location) : null;
        if (place != null) {
            ItemStack stack = place.get();
            if (id == null ? YogToken.isYog(stack) : id.equals(YogToken.storageId(stack))) return place;
        }
        return findToken(player, id);
    }

    public static void click(EntityPlayerMP player, UUID id, int location, int index, boolean shift, boolean creative,
        ItemStack creativeCursor) {
        if (creative && !player.capabilities.isCreativeMode) return;
        // The creative screen keeps its cursor on the client: adopt it for the click and hand the result back.
        if (creative) player.inventory.setItemStack(creativeCursor);
        try {
            YogRecord record = resolve(player, id, location, true);
            if (record == null || !click(player, record, index, shift)) return;
            player.inventoryContainer.detectAndSendChanges();
            if (player.openContainer != player.inventoryContainer) player.openContainer.detectAndSendChanges();
            sync(player, record);
        } finally {
            if (creative) CreativeCursor.handBack(player);
            else CreativeCursor.sync(player);
        }
    }

    private static boolean click(EntityPlayerMP player, YogRecord record, int index, boolean shift) {
        if (index < 0 || index > record.size()) return false;
        ItemStack entry = record.get(index);
        if (entry != null && entry == opened(player)) return false;
        if (index == record.active) {
            retract(player, record);
            entry = record.get(index);
        }
        ItemStack cursor = player.inventory.getItemStack();
        if (shift) {
            if (entry == null || !player.inventory.addItemStackToInventory(entry)) return false;
            record.remove(index);
        } else if (cursor == null) {
            if (entry == null) return false;
            record.remove(index);
            player.inventory.setItemStack(entry);
        } else if (YogToken.isStorable(cursor)) {
            player.inventory.setItemStack(entry);
            record.set(index, cursor);
        } else return false;
        return true;
    }

    public static void use(EntityPlayerMP player, UUID id, int location, int index, boolean sneak) {
        endSession(player);
        YogRecord record = resolve(player, id, location, false);
        ItemStack terminal = record == null ? null : record.get(index);
        if (terminal == null) return;
        int hand = player.inventory.currentItem;
        Place token = findToken(player, record.id);
        if (token != null && token.isMain(hand)) {
            int previous = record.active;
            if (previous != index) {
                if (previous >= 0) retract(player, record);
                disguise(player, record, index, hand);
                sendInventory(player);
            }
            ItemStack held = record.get(index);
            startSession(player, record, held, true);
            Session session = SESSIONS.get(player.getUniqueID());
            if (previous != index) session.rollback = previous;
            sync(player, record);
            if (!opensInPlace(held)) {
                ScienceNotLeisure.network
                    .sendTo(YogTerminalStatePacket.use(locks(player), record.id, index, sneak), player);
                return;
            }
            // Open now: the screen bound to the previous terminal is replaced before it gets invalidated, which would
            // close the client screen first and recentre the cursor.
            Container before = player.openContainer;
            ScienceNotLeisure.network.sendTo(YogTerminalStatePacket.opening(locks(player), record.id, index), player);
            if (!open(player, held, hand, sneak) && player.openContainer == before) endSession(player);
            return;
        }
        int slot = YogTerminals.slotOf(player, terminal);
        Container before = player.openContainer;
        boolean opened = false;
        if (slot >= 0 && opensInPlace(terminal)) {
            startSession(player, record, terminal, false);
            ScienceNotLeisure.network.sendTo(YogTerminalStatePacket.opening(locks(player), record.id, index), player);
            opened = open(player, terminal, slot, sneak) || player.openContainer != before;
        }
        if (!opened) {
            SESSIONS.remove(player.getUniqueID());
            ScienceNotLeisure.network.sendTo(YogTerminalStatePacket.ended(), player);
            player.addChatMessage(new ChatComponentTranslation("gtnl.yog_sothoth_terminal.hold_to_open"));
        }
        sync(player, record);
    }

    private static void startSession(EntityPlayerMP player, YogRecord record, ItemStack terminal, boolean inHand) {
        IntOpenHashSet locked = new IntOpenHashSet();
        if (inHand) locked.add(player.inventory.currentItem);
        Place token = findToken(player, record.id);
        if (token != null && token.kind == Place.MAIN) locked.add(token.index);
        SESSIONS.put(player.getUniqueID(), new Session(record.id, terminal, player.openContainer, locked));
    }

    // AE-registered terminals open through AE's GUI bridge, which the redirect points at the stored stack; other
    // right-click behaviour is hand-bound and may have side effects, so it is not run in place.
    private static boolean opensInPlace(ItemStack terminal) {
        return terminal.getItem() instanceof IYogTerminal || AEApi.instance()
            .registries()
            .wireless()
            .isWirelessTerminal(terminal);
    }

    private static boolean open(EntityPlayerMP player, ItemStack terminal, int slot, boolean sneak) {
        boolean sneaking = player.isSneaking();
        if (sneak) player.setSneaking(true);
        openingSlot = slot;
        try {
            if (terminal.getItem() instanceof IYogTerminal yog && yog.openFromSlot(terminal, player, slot)) return true;
            if (!AEApi.instance()
                .registries()
                .wireless()
                .isWirelessTerminal(terminal)) return false;
            terminal.getItem()
                .onItemRightClick(terminal, player.worldObj, player);
            return false;
        } catch (RuntimeException exception) {
            ScienceNotLeisure.LOG.error("Stored terminal failed to open from a Yog-Sothoth terminal", exception);
            return false;
        } finally {
            openingSlot = Integer.MIN_VALUE;
            if (sneak) player.setSneaking(sneaking);
        }
    }

    public static void cycle(EntityPlayerMP player, boolean backwards) {
        int hand = player.inventory.currentItem;
        Place place = Place.main(player, hand);
        ItemStack stack = place.get();
        UUID id = YogToken.storageId(stack);
        if (id == null || locks(player).contains(hand)) return;
        YogToken.Link link = YogToken.link(stack);
        YogRecord record = YogToken.isYog(stack) ? touch(stack, player, place) : YogTerminalStorage.get(id);
        if (record == null || record.size() == 0) return;
        int current = link == null ? -1 : record.active;
        int next = Math.floorMod(current + 1 + (backwards ? -1 : 1), record.size() + 1) - 1;
        if (record.active >= 0) retract(player, record);
        if (next >= 0) disguise(player, record, next, hand);
        sendInventory(player);
        sync(player, record);
    }

    public static void dropFirst(EntityPlayerMP player) {
        ItemStack stack = player.inventory.mainInventory[player.inventory.currentItem];
        YogRecord record = YogTerminalStorage.get(YogToken.storageId(stack));
        if (record == null) return;
        ItemStack opened = opened(player);
        for (int index = 0; index < record.size(); index++) {
            if (index == record.active || record.get(index) == opened) continue;
            ItemStack terminal = record.remove(index);
            player.dropPlayerItemWithRandomChoice(terminal, false);
            sync(player, record);
            return;
        }
    }

    public static void openScreen(EntityPlayerMP player) {
        int hand = player.inventory.currentItem;
        ItemStack held = player.inventory.mainInventory[hand];
        YogToken.Link link = YogToken.link(held);
        if (link != null) {
            YogRecord record = YogTerminalStorage.get(link.id());
            if (record == null || locks(player).contains(hand)) return;
            retract(player, record);
            sendInventory(player);
            held = player.inventory.mainInventory[hand];
        }
        if (YogToken.isYog(held)) GuiFactories.playerInventory()
            .openFromMainHand(player);
    }

    public static void disguise(EntityPlayerMP player, UUID id, int location, int index) {
        int hand = player.inventory.currentItem;
        if (locks(player).contains(hand)) return;
        YogRecord record = resolve(player, id, location, false);
        if (record == null || index >= record.size()) return;
        Place token = findToken(player, record.id);
        if (token == null || !token.isMain(hand) || record.active == index) return;
        if (record.active >= 0) retract(player, record);
        if (index >= 0) disguise(player, record, index, hand);
        sendInventory(player);
        sync(player, record);
    }

    public static void toggleWindow(EntityPlayerMP player, UUID id, int location) {
        YogRecord record = resolve(player, id, location, true);
        if (record == null) return;
        record.hidden = !record.hidden;
        record.changed();
        sync(player, record);
    }

    public static void clientScreen(EntityPlayerMP player, boolean open) {
        Session session = SESSIONS.get(player.getUniqueID());
        if (session == null) return;
        if (open) session.clientScreen = true;
        else endSession(player);
    }

    public static void requestSync(EntityPlayerMP player, UUID id) {
        YogRecord record = YogTerminalStorage.get(id);
        if (record == null || findToken(player, id) == null) return;
        Map<UUID, long[]> sent = SENT.get(player.getUniqueID());
        if (sent != null) sent.remove(id);
        sync(player, record);
    }

    private static void disguise(EntityPlayer player, YogRecord record, int index, int hand) {
        ItemStack base = player.inventory.mainInventory[hand];
        ItemStack terminal = record.get(index);
        YogToken.attach(
            terminal,
            new YogToken.Link(record.id, record.generation, hand, index, base.writeToNBT(new NBTTagCompound())));
        player.inventory.setInventorySlotContents(hand, terminal);
        record.base = base;
        record.active = index;
        record.home = hand;
        record.holder = player.getUniqueID();
        record.changed();
        HELD.computeIfAbsent(player.getUniqueID(), ignored -> new ObjectOpenHashSet<>())
            .add(record.id);
    }

    static List<YogRecord> carriedRecords(EntityPlayer player, InventoryBaubles baubles) {
        Set<YogRecord> records = new ObjectLinkedOpenHashSet<>();
        if (baubles != null) for (int slot = 0; slot < baubles.getSizeInventory(); slot++)
            addRecord(records, baubles.getStackInSlot(slot));
        for (ItemStack stack : player.inventory.mainInventory) addRecord(records, stack);
        return new ObjectArrayList<>(records);
    }

    private static void addRecord(Set<YogRecord> records, ItemStack stack) {
        UUID id = YogToken.storageId(stack);
        if (id == null) return;
        YogRecord record = YogTerminalStorage.get(id);
        int generation = YogToken.isYog(stack) ? YogToken.generation(stack)
            : YogToken.link(stack)
                .generation();
        if (record != null && record.generation == generation) records.add(record);
    }

    private record CarriedEntries(long time, long modifications, InventoryBaubles baubles, List<Entry> entries) {}

    // Other mods ask for the baubles inventory many times a tick, slot by slot.
    static List<Entry> carriedEntries(EntityPlayer player, InventoryBaubles baubles) {
        long time = player.worldObj.getTotalWorldTime();
        CarriedEntries cached = CARRIED.get(player);
        if (cached != null && cached.time == time
            && cached.modifications == YogRecord.modifications
            && cached.baubles == baubles) return cached.entries;
        List<Entry> entries = null;
        for (YogRecord record : carriedRecords(player, baubles)) {
            if (entries == null) entries = new ObjectArrayList<>();
            for (int index = 0; index < record.size(); index++) entries.add(new Entry(record, index));
        }
        if (entries == null) entries = Collections.emptyList();
        CARRIED.put(player, new CarriedEntries(time, YogRecord.modifications, baubles, entries));
        return entries;
    }

    record Entry(YogRecord record, int index) implements YogBaublesView.Entry {

        @Override
        public ItemStack get() {
            return index == record.active ? null : record.get(index);
        }

        @Override
        public void set(EntityPlayer player, ItemStack stack) {
            ItemStack existing = record.get(index);
            if (existing == stack) return;
            if (stack == null) {
                if (index != record.active) record.remove(index);
                return;
            }
            if (index != record.active && ItemStack.areItemStacksEqual(existing, stack)) {
                record.terminals.set(index, stack);
                return;
            }
            accept(player, record, stack);
        }
    }

    // Entries shift when one is taken out, so a write-back index may point at another terminal: append, never
    // overwrite.
    private static void accept(EntityPlayer player, YogRecord record, ItemStack stack) {
        if (record.contains(stack)) return;
        if (YogToken.isStorable(stack)) record.add(stack);
        else giveBack(player, stack);
    }

    static boolean acceptsCarried(EntityPlayer player, InventoryBaubles baubles, ItemStack stack) {
        return YogToken.isStorable(stack) && !carriedRecords(player, baubles).isEmpty();
    }

    static void acceptCarried(EntityPlayer player, InventoryBaubles baubles, ItemStack stack) {
        if (stack == null) return;
        List<YogRecord> records = carriedRecords(player, baubles);
        if (records.isEmpty()) giveBack(player, stack);
        else accept(player, records.get(0), stack);
    }

    private static void giveBack(EntityPlayer player, ItemStack stack) {
        if (!player.inventory.addItemStackToInventory(stack) && stack.stackSize > 0)
            player.dropPlayerItemWithRandomChoice(stack, false);
    }
}
