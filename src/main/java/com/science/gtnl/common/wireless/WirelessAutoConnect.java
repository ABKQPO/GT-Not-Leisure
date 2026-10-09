package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessChannelPrototype.canBuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BooleanSupplier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;

import com.science.gtnl.common.item.items.OverloadedFrequencyCard;
import com.science.gtnl.common.wireless.WirelessCardSelection.Candidate;
import com.science.gtnl.common.wireless.WirelessChannelPrototype.Address;

import appeng.api.exceptions.FailedConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.tile.networking.TileController;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/** Server-thread placement work, delayed until both Forge placement and AE node initialization have finished. */
public final class WirelessAutoConnect {

    public static final WirelessAutoConnect INSTANCE = new WirelessAutoConnect();
    private static final String LANG = "item.gtnl.overloaded_frequency_card.";
    private static final List<Pending> PENDING = new ArrayList<>();
    private static final Map<UUID, Long> NOTICES = new HashMap<>();
    private static final Map<UUID, Long> TOGGLES = new HashMap<>();
    private static long ticks;

    private static final class Pending {

        final EntityPlayer player;
        final TileEntity tile;
        final World world;
        final int x, y, z;
        final ForgeDirection side;
        final IPart part;
        final ItemStack card;
        final WirelessCardBinding binding;
        final ItemStack placedItem;
        final BooleanSupplier canceled;
        final long expires;
        long next;

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
            this.card = card;
            this.binding = binding;
            this.canceled = canceled;
            ItemStack held = player.getCurrentEquippedItem();
            this.placedItem = held == null ? null : held.copy();
            this.next = ticks + 2;
            this.expires = ticks + 200;
        }
    }

    private WirelessAutoConnect() {}

    private static List<Candidate<ItemStack>> cards(EntityPlayer player) {
        List<Candidate<ItemStack>> candidates = new ArrayList<>();
        for (ItemStack stack : WirelessCardInventory.carried(player)) {
            if (stack == null || !(stack.getItem() instanceof OverloadedFrequencyCard)) continue;
            WirelessCardBinding binding = WirelessCardBinding.read(stack.getTagCompound());
            candidates.add(
                new Candidate<>(
                    stack,
                    stack == player.getCurrentEquippedItem(),
                    binding == null || binding.belongsTo(player.getUniqueID()),
                    binding != null,
                    WirelessCardBinding.automatic(stack.getTagCompound())));
        }
        return candidates;
    }

    public static void toggle(EntityPlayer player, boolean heldOnly, int expectedSlot) {
        if (player.isDead || player.worldObj.isRemote) return;
        if (ticks < TOGGLES.getOrDefault(player.getUniqueID(), Long.MIN_VALUE)) return;
        ItemStack card;
        if (heldOnly) {
            if (!player.isSneaking() || player.inventory.currentItem != expectedSlot) return;
            card = player.getCurrentEquippedItem();
            if (card == null || !(card.getItem() instanceof OverloadedFrequencyCard)) return;
            WirelessCardBinding binding = WirelessCardBinding.read(card.getTagCompound());
            if (binding != null && !binding.belongsTo(player.getUniqueID())) {
                message(player, "wrong_owner", binding.ownerName());
                return;
            }
        } else {
            var selection = WirelessCardSelection.select(cards(player), false);
            if (selection.card() == null) {
                message(player, selection.ambiguous() ? "auto_ambiguous" : "auto_no_card");
                return;
            }
            card = selection.card();
        }
        TOGGLES.put(player.getUniqueID(), ticks + 5);
        if (card.getTagCompound() == null) card.setTagCompound(new NBTTagCompound());
        boolean enabled = !WirelessCardBinding.automatic(card.getTagCompound());
        WirelessCardBinding.automatic(card.getTagCompound(), enabled);
        WirelessCardInventory.sync(player, card);
        message(player, enabled ? "auto_on" : "auto_off");
    }

    public static void openGui(EntityPlayer player) {
        if (player.isDead || player.worldObj.isRemote || player.openContainer != player.inventoryContainer) return;
        if (ticks < TOGGLES.getOrDefault(player.getUniqueID(), Long.MIN_VALUE)) return;
        TOGGLES.put(player.getUniqueID(), ticks + 5);
        var selection = WirelessCardSelection.select(cards(player), false);
        if (selection.card() == null) {
            message(player, selection.ambiguous() ? "gui_ambiguous" : "auto_no_card");
            return;
        }
        int location = WirelessCardInventory.location(player, selection.card());
        if (location == Integer.MIN_VALUE) return;
        com.science.gtnl.CommonProxy.openGui(
            player,
            com.science.gtnl.utils.enums.GuiType.WirelessCardGUI,
            null,
            player.worldObj,
            location,
            0,
            0);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void placed(BlockEvent.PlaceEvent event) {
        if (event.isCanceled() || event.world.isRemote) return;
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

    public static void placedPart(EntityPlayer player, TileEntity tile, ForgeDirection side, IPart part) {
        if (side != null && part != null) enqueue(player, tile, side, part, () -> false);
    }

    private static void enqueue(EntityPlayer player, TileEntity tile, ForgeDirection side, IPart part,
        BooleanSupplier canceled) {
        if (player == null || player instanceof FakePlayer
            || player.worldObj.isRemote
            || tile == null
            || tile instanceof TileController
            || !(tile instanceof IGridHost || tile instanceof IPartHost)) return;
        // Native cable/part placement is reported with an exact side and part identity by the AE hook.
        if (part == null && tile instanceof IPartHost) return;
        var selection = WirelessCardSelection.select(cards(player), true);
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
        PENDING.add(
            new Pending(player, tile, side, part, card, WirelessCardBinding.read(card.getTagCompound()), canceled));
    }

    public static void tick() {
        ticks++;
        var iterator = PENDING.iterator();
        while (iterator.hasNext()) {
            Pending pending = iterator.next();
            if (ticks < pending.next) continue;
            if (ticks > pending.expires) {
                notice(pending.player, "auto_unavailable");
                iterator.remove();
            } else if (attempt(pending)) iterator.remove();
            else pending.next = ticks + 1;
        }
        if (ticks % 200 == 0) {
            NOTICES.values()
                .removeIf(until -> until < ticks);
            TOGGLES.values()
                .removeIf(until -> until < ticks);
        }
    }

    /** True finishes the request; false retries only temporary node/source initialization. */
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
        boolean hasCard = false;
        for (ItemStack stack : WirelessCardInventory.carried(player)) if (stack == pending.card) hasCard = true;
        if (!hasCard || !pending.binding.equals(WirelessCardBinding.read(pending.card.getTagCompound()))
            || !WirelessCardBinding.automatic(pending.card.getTagCompound())) return true;
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
        if (node == null || node.getGrid() == null) return false;
        if (!world.canMineBlock(player, pending.x, pending.y, pending.z) || !player.canPlayerEdit(
            pending.x,
            pending.y,
            pending.z,
            target.side()
                .ordinal(),
            pending.placedItem) || !canBuild(node, player)) {
            notice(player, "denied");
            return true;
        }
        try {
            IGridNode source = pending.binding.source()
                .node();
            if (source == null || source.getGrid() == null) return false;
            if (!canBuild(source, player)) {
                notice(player, "denied");
                return true;
            }
            if (!AutomaticWirelessEntrances.isSettled(
                source.getGrid()
                    .getCache(appeng.api.networking.pathing.IPathingGrid.class)))
                return false;
            WirelessClusterManager.refreshTarget(node);
            if (WirelessClusterManager.isLinked(pending.binding.source(), node)) return true; // Never toggle/unlink.
            WirelessClusterManager.connectCluster(pending.binding.source(), target);
            return true;
        } catch (IllegalArgumentException | IllegalStateException | FailedConnection failure) {
            notice(player, "auto_failed");
            return true;
        }
    }

    private static void notice(EntityPlayer player, String key) {
        if (ticks < NOTICES.getOrDefault(player.getUniqueID(), Long.MIN_VALUE)) return;
        NOTICES.put(player.getUniqueID(), ticks + 40);
        message(player, key);
    }

    private static void message(EntityPlayer player, String key, Object... args) {
        player.addChatMessage(new ChatComponentTranslation(LANG + key, args));
    }

    public static void clear() {
        PENDING.clear();
        NOTICES.clear();
        TOGGLES.clear();
        ticks = 0;
    }
}
