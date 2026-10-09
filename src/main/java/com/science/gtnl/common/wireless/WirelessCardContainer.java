package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessChannelPrototype.canBuild;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.OverloadedFrequencyCard;
import com.science.gtnl.common.packet.WirelessCardGuiPacket;

import appeng.api.networking.IGridNode;

/** A slotless, server-authoritative view. Clients send only a session/revision and a displayed button. */
public final class WirelessCardContainer extends Container {

    private static final AtomicLong SESSIONS = new AtomicLong();
    private final EntityPlayer player;
    private ItemStack card;
    private final int cardSlot;
    private final IInventory cardInventory;
    private final boolean heldOnly;
    private final WirelessCardBinding binding;
    private final long session = SESSIONS.incrementAndGet();
    private long revision;
    private int page;
    private int pageSize = WirelessCardLayout.MIN_ROWS;
    private int ticks;
    private IGridNode detailNode;
    private WirelessViewFilter filter = WirelessViewFilter.parse("", "", 0);
    private NBTTagList pendingHighlight;
    private List<WirelessClusterManager.LinkView> rows = List.of();
    private NBTTagCompound sent;
    private long actionAfter = Long.MIN_VALUE;
    public volatile NBTTagCompound display = new NBTTagCompound();

    public WirelessCardContainer(EntityPlayer player, int location) {
        this.player = player;
        heldOnly = location == 0;
        var slot = WirelessCardInventory.resolve(player, location);
        cardInventory = slot == null ? null : slot.inventory();
        cardSlot = slot == null ? -1 : slot.index();
        card = slot == null ? null : cardInventory.getStackInSlot(cardSlot);
        binding = card == null ? null : WirelessCardBinding.read(card.getTagCompound());
        // NetHandlerPlayServer queries this slot after onItemRightClick returns, even for an item GUI.
        if (slot != null) addSlotToContainer(new Slot(cardInventory, cardSlot, -10000, -10000) {

            @Override
            public boolean canTakeStack(EntityPlayer user) {
                return false;
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });
    }

    @Override
    public boolean canInteractWith(EntityPlayer user) {
        ItemStack held = cardInventory == null ? null : cardInventory.getStackInSlot(cardSlot);
        boolean valid = user == player && !user.isDead
            && card != null
            && card.getItem() instanceof OverloadedFrequencyCard
            && matchesHeldCard(cardSlot, heldOnly ? user.inventory.currentItem : cardSlot, card, held)
            && Objects.equals(binding, WirelessCardBinding.read(held.getTagCompound()))
            && (binding == null || binding.belongsTo(user.getUniqueID()));
        // Vanilla replaces the held stack with an equal copy at the end of every right-click packet.
        // Always mutate the live inventory stack, not the object captured before that copy.
        if (valid) card = held;
        return valid;
    }

    static boolean matchesHeldCard(int expectedSlot, int selectedSlot, ItemStack expected, ItemStack current) {
        return expectedSlot == selectedSlot && expected != null
            && current != null
            && current.stackSize == 1
            && ItemStack.areItemStacksEqual(expected, current);
    }

    @Override
    public ItemStack slotClick(int slot, int button, int mode, EntityPlayer user) {
        return null;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer user, int slot) {
        return null;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (!player.worldObj.isRemote && ++ticks % 20 == 1 && canInteractWith(player)) update(false);
    }

    private void update(boolean force) {
        if (!(player instanceof EntityPlayerMP serverPlayer)) return;
        List<WirelessClusterManager.LinkView> all = binding == null ? List.of()
            : WirelessClusterManager.links(binding.source());
        if (detailNode == null) all = filter(all);
        List<WirelessClusterManager.LinkView> summary = all;
        String detailName = "";
        String detailTarget = "";
        boolean multipleNames = false;
        if (detailNode != null) {
            var focus = all.stream()
                .filter(
                    link -> link.members()
                        .contains(detailNode))
                .findFirst()
                .orElse(null);
            if (focus == null) {
                detailNode = null;
                page = 0;
                all = filter(all);
                summary = all;
            } else {
                detailName = focus.name();
                detailTarget = focus.target()
                    .toString();
                multipleNames = focus.names()
                    .size() > 1;
                summary = List.of(focus);
                all = WirelessClusterManager.entranceLinks(binding.source(), focus);
            }
        }
        int pages = Math.max(1, (all.size() + pageSize - 1) / pageSize);
        page = Math.max(0, Math.min(page, pages - 1));
        List<WirelessClusterManager.LinkView> next = all
            .subList(page * pageSize, Math.min(all.size(), (page + 1) * pageSize));
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("page", page);
        data.setInteger("pageSize", pageSize);
        data.setInteger("pages", pages);
        data.setInteger("total", all.size());
        data.setBoolean("detail", detailNode != null);
        data.setString("name", detailName);
        data.setString("detailTarget", detailTarget);
        data.setBoolean("multipleNames", multipleNames);
        data.setBoolean(
            "filtered",
            !filter.query()
                .isEmpty() || filter.dimension() != null || filter.state() != 0);
        data.setBoolean("auto", WirelessCardBinding.automatic(card.getTagCompound()));
        data.setBoolean("bound", binding != null);
        int devices = 0, online = 0, missing = 0, entrances = 0;
        boolean stable = true;
        for (var cluster : summary) {
            devices += cluster.statistics()
                .devices();
            online += cluster.statistics()
                .online();
            missing += cluster.statistics()
                .missing();
            stable &= cluster.statistics()
                .stable();
            entrances += cluster.entrances();
        }
        data.setInteger("devices", devices);
        data.setInteger("online", online);
        data.setInteger("missing", missing);
        data.setInteger("entrances", entrances);
        data.setBoolean("stable", stable);
        if (binding != null) {
            data.setString("source", coordinates(WirelessChannelPrototype.controllerSource(binding.source())));
            IGridNode source = binding.source()
                .node();
            data.setBoolean("sourceLoaded", source != null);
            data.setBoolean("channels", WirelessChannelPrototype.channelsEnabled());
            if (source != null && source.getGrid() != null) {
                var allocation = WirelessChannelPrototype.allocation(source.getGrid());
                data.setInteger("capacity", WirelessChannelPrototype.capacity(source.getGrid()));
                data.setInteger("used", allocation == null ? -1 : allocation.used());
            }
        }
        NBTTagList entries = new NBTTagList();
        for (var row : next) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setString("position", coordinates(row.target()));
            entry.setString("name", row.name());
            entry.setString(
                "side",
                row.target()
                    .side()
                    .name());
            entry.setString("state", row.state());
            entry.setBoolean("paused", row.paused());
            entry.setBoolean("conflicted", row.conflicted());
            entry.setInteger("entrances", row.entrances());
            entry.setInteger(
                "devices",
                row.statistics()
                    .devices());
            entry.setInteger(
                "online",
                row.statistics()
                    .online());
            entry.setInteger(
                "missing",
                row.statistics()
                    .missing());
            entry.setBoolean(
                "stable",
                row.statistics()
                    .stable());
            entry.setBoolean("runtime", row.runtimeEntrance());
            entry.setInteger("usedChannels", row.usedChannels());
            ItemStack icon = row.node()
                .getGridBlock()
                .getMachineRepresentation();
            if (icon != null && icon.getItem() != null) {
                // Display-only identity, never ship inventories or other machine NBT as an icon.
                ItemStack clean = new ItemStack(icon.getItem(), 1, icon.getItemDamage());
                entry.setTag("icon", clean.writeToNBT(new NBTTagCompound()));
            }
            entries.appendTag(entry);
        }
        data.setTag("rows", entries);
        boolean sameNodes = sameTargets(next, rows);
        boolean changed = force || !sameNodes || !data.equals(sent);
        rows = List.copyOf(next);
        sent = (NBTTagCompound) data.copy();
        data.setLong("session", session);
        if (changed) revision++;
        data.setLong("revision", revision);
        if (pendingHighlight != null) {
            data.setTag("highlight", pendingHighlight);
            pendingHighlight = null;
        }
        ScienceNotLeisure.network.sendTo(new WirelessCardGuiPacket.Snapshot(windowId, data), serverPlayer);
    }

    static boolean sameTargets(List<WirelessClusterManager.LinkView> next,
        List<WirelessClusterManager.LinkView> previous) {
        if (next.size() != previous.size()) return false;
        for (int i = 0; i < next.size(); i++) {
            var a = next.get(i);
            var b = previous.get(i);
            // Replacing a non-anchor node can leave every displayed metric unchanged.
            // Its membership must still invalidate clicks issued against the older cluster.
            if (a.node() != b.node() || !a.members()
                .equals(b.members())) return false;
        }
        return true;
    }

    public void action(long session, long revision, int action, String text, String dimension, int filterState) {
        if (!canInteractWith(player) || this.session != session) return;
        boolean teleport = action >= 50 && action < 50 + rows.size();
        long now = player.worldObj.getTotalWorldTime();
        if (!teleport) {
            if (now < actionAfter) return;
            actionAfter = now + 4;
        }
        if (this.revision != revision && action != 6 && action != 8) {
            update(true);
            return;
        }
        if (action == 8) {
            // View-only layout negotiation reuses the bounded integer payload; never accept arbitrary row IDs.
            if (!WirelessCardLayout.validRows(filterState)) return;
            page = page * pageSize / filterState;
            pageSize = filterState;
        } else if (action == 6) {
            try {
                filter = WirelessViewFilter.parse(text, dimension, filterState);
                detailNode = null;
                page = 0;
            } catch (IllegalArgumentException invalid) {
                player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.filter_invalid"));
            }
        } else if (action == 5 && detailNode != null && !rows.isEmpty() && binding != null) {
            var row = rows.get(0);
            IGridNode source = binding.source()
                .node();
            if (!canBuild(row.node(), player) || !canBuild(source, player)) {
                player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.denied"));
            } else {
                try {
                    WirelessClusterManager.rename(binding.source(), row, text);
                } catch (IllegalStateException stale) {
                    player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.stale"));
                }
            }
        } else if (teleport && binding != null && player instanceof EntityPlayerMP serverPlayer) {
            var row = rows.get(action - 50);
            try {
                if (!WirelessClusterManager.isCurrentLink(binding.source(), row)) {
                    player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.stale"));
                } else {
                    IGridNode source = binding.source()
                        .node();
                    if (source == null) {
                        player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.teleport_unavailable"));
                    } else if (!canBuild(source, player) || !canBuild(row.node(), player)) {
                        player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.denied"));
                    } else if (WirelessCardTeleport.teleport(serverPlayer, row.target())) return;
                }
            } catch (PhysicalClusterTracker.ScanLimitException limit) {
                player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.stale"));
            }
        } else if (action >= 40 && action < 40 + rows.size()) {
            var row = rows.get(action - 40);
            if (row.target()
                .dimension() != player.dimension) {
                player.addChatMessage(
                    new ChatComponentTranslation(
                        "gtnl.wireless.gui.highlight_dimension",
                        row.target()
                            .dimension()));
            } else {
                pendingHighlight = new NBTTagList();
                var targets = WirelessClusterManager.highlight(row)
                    .stream()
                    .filter(target -> target.dimension() == player.dimension)
                    .sorted(
                        java.util.Comparator
                            .comparingDouble(target -> player.getDistanceSq(target.x(), target.y(), target.z())))
                    .limit(512)
                    .toList();
                for (var target : targets) {
                    NBTTagCompound pos = new NBTTagCompound();
                    pos.setInteger("dimension", target.dimension());
                    pos.setInteger("x", target.x());
                    pos.setInteger("y", target.y());
                    pos.setInteger("z", target.z());
                    pendingHighlight.appendTag(pos);
                }
                player.addChatMessage(
                    new ChatComponentTranslation("gtnl.wireless.gui.highlight_started", targets.size()));
            }
        } else if (action == 0) page--;
        else if (action == 1) page++;
        else if (action == 2) {
            if (card.getTagCompound() == null) card.setTagCompound(new NBTTagCompound());
            WirelessCardBinding.automatic(card.getTagCompound(), !WirelessCardBinding.automatic(card.getTagCompound()));
            WirelessCardInventory.sync(player, card);
        } else if (action == 4) {
            detailNode = null;
            page = 0;
        } else if (action >= 20 && action < 20 + rows.size() && detailNode == null) {
            detailNode = rows.get(action - 20)
                .node();
            page = 0;
        } else if (action >= 10 && action < 10 + rows.size() && binding != null) {
            var row = rows.get(action - 10);
            IGridNode source = binding.source()
                .node();
            // Never act on a replacement device or guess an unloaded source's permission when resuming.
            if (row.target()
                .node() != row.node() || !canBuild(row.node(), player)
                || source != null && !canBuild(source, player)
                || row.paused() && source == null) {
                player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.denied"));
            } else {
                try {
                    WirelessClusterManager.setPaused(binding.source(), row, !row.paused());
                } catch (IllegalStateException failure) {
                    player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui.stale"));
                }
            }
        } else if (action != 3) return;
        update(true);
    }

    private List<WirelessClusterManager.LinkView> filter(List<WirelessClusterManager.LinkView> links) {
        return links.stream()
            .filter(link -> {
                String position = coordinates(link.target());
                String state = link.conflicted() ? "conflict" : link.state();
                return filter.matches(
                    "",
                    link.target()
                        .dimension(),
                    position,
                    link.paused(),
                    state)
                    || link.names()
                        .stream()
                        .anyMatch(
                            name -> filter.matches(
                                name,
                                link.target()
                                    .dimension(),
                                position,
                                link.paused(),
                                state));
            })
            .toList();
    }

    private static String coordinates(WirelessChannelPrototype.Address address) {
        return "D" + address.dimension() + "  " + address.x() + ", " + address.y() + ", " + address.z();
    }
}
