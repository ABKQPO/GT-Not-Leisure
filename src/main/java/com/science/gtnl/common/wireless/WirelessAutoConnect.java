package com.science.gtnl.common.wireless;

import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnllib.utils.HotbarMessages;
import com.science.gtnl.common.item.items.WirelessFrequencyCard;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.mixins.late.appliedEnergistics.wireless.AccessorTileController;

import appeng.api.exceptions.FailedConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@EventBusSubscriber
public class WirelessAutoConnect {

    private static final String LANG = "item.gtnl.wireless_frequency_card.";
    private static final List<Pending> PENDING = new ObjectArrayList<>();
    private static final Object2LongOpenHashMap<UUID> NOTICES = new Object2LongOpenHashMap<>();
    private static final Object2LongOpenHashMap<UUID> TOGGLES = new Object2LongOpenHashMap<>();
    private static long ticks;
    private static int scanBudget;

    private static class Pending {

        public final EntityPlayer player;
        public final TileEntity tile;
        public final World world;
        public final int x, y, z;
        public final ForgeDirection side;
        public final IPart part;
        private final WirelessCardInventory.TrackedCard card;
        public final WirelessCardBinding binding;
        public final ItemStack placedItem;
        public final BooleanSupplier canceled;
        public long expires;
        public long next;

        Pending(EntityPlayer player, TileEntity tile, ForgeDirection side, IPart part, ItemStack card,
            WirelessCardBinding binding, BooleanSupplier canceled) {
            this.player = player;
            this.tile = tile;
            this.world = player.worldObj;
            this.x = tile.xCoord;
            this.y = tile.yCoord;
            this.z = tile.zCoord;
            this.side = side;
            this.part = part;
            this.card = new WirelessCardInventory.TrackedCard(card, binding);
            this.binding = binding;
            this.canceled = canceled;
            ItemStack held = player.getCurrentEquippedItem();
            this.placedItem = held == null ? null : held.copy();
            this.next = ticks + 2;
            this.expires = ticks + 200;
        }

        boolean hasCard() {
            return card.present(player);
        }
    }

    private WirelessAutoConnect() {}

    public static void toggle(EntityPlayer player) {
        if (player.isDead || player.worldObj.isRemote) return;
        if (ticks < TOGGLES.getOrDefault(player.getUniqueID(), Long.MIN_VALUE)) return;
        var selection = WirelessCardInventory.select(player, false);
        if (selection.card() == null) {
            hint(player, selection.ambiguous() ? "auto_ambiguous" : "auto_no_card");
            return;
        }
        TOGGLES.put(player.getUniqueID(), ticks + 5);
        boolean enabled = toggleAutomatic(player, selection.card());
        hint(player, enabled ? "auto_on" : "auto_off");
    }

    static boolean toggleAutomatic(EntityPlayer player, ItemStack card) {
        boolean enabled = WirelessCardBinding.toggleAutomatic(card);
        WirelessCardInventory.sync(player, card);
        return enabled;
    }

    private static ItemStack shortcutCard(EntityPlayer player, boolean heldOnly, int expectedSlot) {
        if (player.isDead || player.worldObj.isRemote || player.openContainer != player.inventoryContainer) return null;
        if (ticks < TOGGLES.getOrDefault(player.getUniqueID(), Long.MIN_VALUE)) return null;
        TOGGLES.put(player.getUniqueID(), ticks + 5);
        if (heldOnly) {
            if (!player.isSneaking() || player.inventory.currentItem != expectedSlot) return null;
            ItemStack card = player.getCurrentEquippedItem();
            if (card == null || !(card.getItem() instanceof WirelessFrequencyCard)) return null;
            WirelessCardBinding binding = WirelessCardBinding.read(card.getTagCompound());
            if (binding != null && !binding.belongsTo(player.getUniqueID())) {
                hint(player, "wrong_owner", binding.ownerName());
                return null;
            }
            return card;
        }
        var selection = WirelessCardInventory.select(player, false);
        if (selection.card() == null) hint(player, selection.ambiguous() ? "gui_ambiguous" : "auto_no_card");
        return selection.card();
    }

    public static void openGui(EntityPlayer player) {
        ItemStack card = shortcutCard(player, false, 0);
        if (card == null) return;
        int location = WirelessCardInventory.location(player, card);
        if (location == Integer.MIN_VALUE) return;
        var factory = GuiFactories.playerInventory();
        if (location < 0) factory.openFromBaubles(player, -location - 1);
        else if (location == 0) factory.openFromMainHand(player);
        else factory.openFromPlayerInventory(player, location - 1);
    }

    public static void cycleVisualisation(EntityPlayer player, boolean heldOnly, int expectedSlot, boolean backwards) {
        ItemStack card = shortcutCard(player, heldOnly, expectedSlot);
        if (card == null) return;
        WirelessCardVisualisation.cycle(card, player, backwards);
        HotbarMessages.send(
            player,
            new ChatComponentTranslation(
                "gtnl.wireless.gui.visualisation",
                new ChatComponentTranslation(
                    WirelessCardVisualisation.modeKey(WirelessCardVisualisation.mode(card.getTagCompound())))));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void placed(BlockEvent.PlaceEvent event) {
        if (event.isCanceled() || event.world.isRemote || unchanged(event.blockSnapshot)) return;
        if (event instanceof BlockEvent.MultiPlaceEvent multi) {
            for (var snapshot : multi.getReplacedBlockSnapshots()) {
                enqueue(
                    event.player,
                    event.world.getTileEntity(snapshot.x, snapshot.y, snapshot.z),
                    null,
                    null,
                    event::isCanceled);
            }
        } else {
            enqueue(event.player, event.world.getTileEntity(event.x, event.y, event.z), null, null, event::isCanceled);
        }
    }

    static boolean unchanged(BlockSnapshot snapshot) {
        // Matter Manipulator posts a PlaceEvent as a permission probe before it places anything there.
        return snapshot.getReplacedBlock() == snapshot.getCurrentBlock()
            && snapshot.meta == snapshot.world.getBlockMetadata(snapshot.x, snapshot.y, snapshot.z);
    }

    public static void placedPart(EntityPlayer player, TileEntity tile, ForgeDirection side, IPart part) {
        if (side != null && part != null) enqueue(player, tile, side, part, () -> false);
    }

    private static void enqueue(EntityPlayer player, TileEntity tile, ForgeDirection side, IPart part,
        BooleanSupplier canceled) {
        if (player == null || player instanceof FakePlayer
            || player.worldObj.isRemote
            || tile instanceof AccessorTileController
            || !(tile instanceof IGridHost || tile instanceof IPartHost)) return;
        // Bulk pastes restore each block's copied frequency once they finish, instead of the card's.
        if (WirelessConstructionSites.contains(WirelessLinkPersistence.address(tile))) return;
        // Native cable/part placement is reported with an exact side and part identity by the AE hook.
        if (part == null && tile instanceof IPartHost) return;
        var selection = WirelessCardInventory.select(player, true);
        if (selection.card() == null) {
            if (selection.ambiguous()) notice(player, "auto_ambiguous");
            return;
        }
        if (PENDING.size() >= 1024 || PENDING.stream()
            .filter(p -> p.player == player)
            .count() >= 128) {
            notice(player, "auto_busy");
            return;
        }
        for (Pending pending : PENDING) {
            if (pending.player == player && pending.tile == tile && pending.part == part) return;
        }
        ItemStack card = selection.card();
        if (WirelessCardBinding.identify(card)) WirelessCardInventory.sync(player, card);
        PENDING.add(
            new Pending(player, tile, side, part, card, WirelessCardBinding.read(card.getTagCompound()), canceled));
    }

    public static void tick() {
        ticks++;
        scanBudget = 1;
        Pending retried = null;
        var iterator = PENDING.iterator();
        while (iterator.hasNext()) {
            Pending pending = iterator.next();
            if (ticks < pending.next) continue;
            if (ticks > pending.expires) {
                notice(pending.player, "auto_unavailable");
                iterator.remove();
            } else {
                int before = scanBudget;
                if (attempt(pending)) iterator.remove();
                else {
                    pending.next = ticks + 1;
                    if (before > scanBudget) retried = pending;
                }
            }
        }
        if (retried != null) {
            PENDING.remove(retried);
            PENDING.add(retried);
        }
        if (ticks % 200 == 0) {
            NOTICES.values()
                .removeIf((long until) -> until < ticks);
            TOGGLES.values()
                .removeIf((long until) -> until < ticks);
        }
    }

    private static boolean attempt(Pending pending) {
        EntityPlayer player = pending.player;
        World world = pending.world;
        if (player.isDead || player.worldObj != world
            || !world.playerEntities.contains(player)
            || pending.canceled.getAsBoolean()
            || !world.blockExists(pending.x, pending.y, pending.z)
            || world.getTileEntity(pending.x, pending.y, pending.z) != pending.tile
            || pending.tile.isInvalid()) return true;
        if (pending.part != null
            && (!(pending.tile instanceof IPartHost host) || host.getPart(pending.side) != pending.part)) return true;
        if (!pending.hasCard()) return true;
        ForgeDirection side = pending.side == null ? ForgeDirection.UNKNOWN : pending.side;
        Address target = new Address(world.provider.dimensionId, pending.x, pending.y, pending.z, side);
        IGridNode node = target.node();
        if (node == null && pending.part == null) {
            for (ForgeDirection candidate : ForgeDirection.VALID_DIRECTIONS) {
                target = new Address(world.provider.dimensionId, pending.x, pending.y, pending.z, candidate);
                node = target.node();
                if (node != null) break;
            }
        }
        if (node == null) {
            // GT exposes IGridHost on ordinary machines too; no proxy means there is no AE node to await.
            return pending.tile instanceof BaseMetaTileEntity base && base.getMetaTileEntity() != null
                && base.getProxy() == null;
        }
        if (node.getGrid() == null) return false;
        if (!world.canMineBlock(player, pending.x, pending.y, pending.z) || !player.canPlayerEdit(
            pending.x,
            pending.y,
            pending.z,
            target.side()
                .ordinal(),
            pending.placedItem)) {
            notice(player, "denied");
            return true;
        }
        try {
            var result = WirelessOperations.ensureConnected(player, pending.binding, target, () -> {
                if (scanBudget <= 0) return false;
                scanBudget--;
                return true;
            });
            if (result == WirelessOperations.Result.DEFERRED) {
                pending.expires++;
                return false;
            }
            if (result == WirelessOperations.Result.DENIED) notice(player, "denied");
            else if (result == WirelessOperations.Result.UNAVAILABLE) notice(player, "auto_failed");
            return result != WirelessOperations.Result.WAITING;
        } catch (IllegalArgumentException | IllegalStateException | FailedConnection failure) {
            notice(player, "auto_failed");
            return true;
        }
    }

    static void notice(EntityPlayer player, String key) {
        if (ticks < NOTICES.getOrDefault(player.getUniqueID(), Long.MIN_VALUE)) return;
        NOTICES.put(player.getUniqueID(), ticks + 40);
        message(player, key);
    }

    private static void message(EntityPlayer player, String key, Object... args) {
        player.addChatMessage(new ChatComponentTranslation(LANG + key, args));
    }

    static void hint(EntityPlayer player, String key, Object... args) {
        HotbarMessages.send(player, new ChatComponentTranslation(LANG + key, args));
    }

    public static void clear() {
        PENDING.clear();
        NOTICES.clear();
        TOGGLES.clear();
        ticks = 0;
        scanBudget = 0;
    }
}
