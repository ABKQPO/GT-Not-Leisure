package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessChannelManager.canBuild;
import static com.science.gtnl.common.wireless.WirelessMachineGroups.coordinates;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChatComponentTranslation;

import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.item.items.WirelessFrequencyCard;
import com.science.gtnl.common.wireless.WirelessMachineGroups.Row;
import com.science.gtnl.utils.appliedEnergistics.InterfaceNameLocalization;

import appeng.api.networking.IGridNode;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import io.netty.buffer.ByteBuf;

/** A slotless, server-authoritative view. Clients send only a session/revision and a displayed button. */
public final class WirelessCardContainer extends ModularContainer {

    public final ViewSync sync = new ViewSync().allowC2S();

    private static final AtomicLong SESSIONS = new AtomicLong();
    private final EntityPlayer player;
    private ItemStack card;
    private final int cardSlot;
    private final IInventory cardInventory;
    private final boolean heldOnly;
    private final WirelessCardBinding binding;
    private final long session = SESSIONS.incrementAndGet();
    private long revision;
    private long nameRevision;
    private RenameState renameState;
    private int page;
    private int pageSize = WirelessCardLayout.MIN_ROWS;
    private int ticks;
    private IGridNode detailNode;
    private MTEMultiBlockBase groupMachine;
    private WirelessViewFilter filter = WirelessViewFilter.parse("", "", 0);
    private NBTTagList pendingHighlight;
    private List<Row> rows = List.of();
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
            && card.getItem() instanceof WirelessFrequencyCard
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

    private void update(boolean force) {
        if (!(player instanceof EntityPlayerMP)) return;
        var interfaces = new WirelessInterfaceView();
        List<WirelessClusterManager.LinkView> all = binding == null ? List.of()
            : WirelessClusterManager.links(binding.source());
        List<Row> groups = detailNode == null || groupMachine != null ? WirelessMachineGroups.group(all, interfaces)
            : List.of();
        Row selectedGroup = groups.stream()
            .filter(row -> row.machine() == groupMachine && row.group())
            .findFirst()
            .orElse(null);
        if (groupMachine != null && selectedGroup == null) {
            groupMachine = null;
            page = 0;
        }
        List<WirelessClusterManager.LinkView> summary = List.of();
        String detailName = "";
        String detailTarget = "";
        boolean multipleNames = false;
        RenameState nextRenameState = null;
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
                groups = WirelessMachineGroups.group(all, interfaces);
            } else {
                nextRenameState = new RenameState(focus.members(), focus.deviceNames());
                detailName = focus.name();
                detailTarget = focus.target()
                    .toString();
                multipleNames = focus.names()
                    .size() > 1;
                summary = List.of(focus);
                all = WirelessClusterManager.entranceLinks(binding.source(), focus);
                if (selectedGroup != null && selectedGroup.links()
                    .stream()
                    .noneMatch(
                        link -> link.members()
                            .contains(detailNode))) {
                    groupMachine = null;
                    selectedGroup = null;
                }
            }
        }
        List<Row> visible = new ArrayList<>();
        if (detailNode != null) {
            for (var link : all) visible.add(Row.cluster(link));
        } else {
            for (Row row : selectedGroup == null ? groups : List.of(selectedGroup)) {
                Row filtered = row.filtered(filter, interfaces);
                if (filtered == null) continue;
                if (selectedGroup == null) visible.add(filtered);
                else for (var link : filtered.links()) visible.add(Row.cluster(link));
            }
            WirelessMachineGroups.sort(visible);
            summary = visible.stream()
                .flatMap(
                    row -> row.links()
                        .stream())
                .toList();
        }
        if (!Objects.equals(renameState, nextRenameState)) nameRevision++;
        renameState = nextRenameState;
        int pages = Math.max(1, (visible.size() + pageSize - 1) / pageSize);
        page = Math.max(0, Math.min(page, pages - 1));
        List<Row> next = visible.subList(page * pageSize, Math.min(visible.size(), (page + 1) * pageSize));
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("page", page);
        data.setInteger("pageSize", pageSize);
        data.setInteger("pages", pages);
        data.setInteger("total", summary.size());
        data.setBoolean("detail", detailNode != null);
        data.setBoolean("group", groupMachine != null);
        if (selectedGroup != null) {
            writeIcon(data, "groupIcon", selectedGroup.icon());
            data.setString("groupPosition", coordinates(selectedGroup.target()));
        }
        data.setString("name", detailName);
        data.setLong("nameRevision", nameRevision);
        data.setString("detailTarget", detailTarget);
        data.setBoolean("multipleNames", multipleNames);
        data.setBoolean(
            "renameable",
            nextRenameState != null && !nextRenameState.names()
                .isEmpty());
        data.setInteger("filterState", filter.state());
        data.setBoolean(
            "filtered",
            !filter.query()
                .isEmpty() || filter.dimension() != null || filter.state() != 0);
        data.setBoolean("auto", WirelessCardBinding.automatic(card.getTagCompound()));
        data.setInteger("visualMode", WirelessCardVisualisation.mode(card.getTagCompound()));
        data.setBoolean("bound", binding != null);
        writeStatistics(data, WirelessMachineGroups.summarize(summary));
        data.setInteger("entrances", binding == null ? 0 : WirelessChannelManager.entranceCount(binding.source()));
        data.setInteger("entranceLimit", WirelessChannelManager.entranceLimit());
        if (binding != null) {
            data.setString("source", coordinates(WirelessChannelManager.controllerSource(binding.source())));
            IGridNode source = binding.source()
                .node();
            data.setBoolean("sourceLoaded", source != null);
            data.setBoolean("channels", WirelessChannelManager.channelsEnabled());
            if (source != null && source.getGrid() != null) {
                var allocation = WirelessChannelManager.allocation(source.getGrid());
                data.setInteger("capacity", WirelessChannelManager.capacity(source.getGrid()));
                data.setInteger("used", allocation == null ? -1 : allocation.used());
            }
        }
        NBTTagList entries = new NBTTagList();
        for (var row : next) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setString("position", coordinates(row.target()));
            entry.setBoolean("group", row.group());
            entry.setInteger(
                "clusters",
                row.links()
                    .size());
            entry.setString(
                "name",
                row.group() ? ""
                    : detailNode == null ? row.link()
                        .name() : detailName);
            entry.setString(
                "side",
                row.target()
                    .side()
                    .name());
            entry.setString("state", row.state());
            entry.setBoolean("paused", row.paused());
            entry.setBoolean("conflicted", row.conflicted());
            entry.setInteger("entrances", row.entrances());
            writeStatistics(entry, row.statistics());
            entry.setBoolean(
                "runtime",
                !row.group() && row.link()
                    .runtimeEntrance());
            entry.setInteger(
                "usedChannels",
                row.group() ? -1
                    : row.link()
                        .usedChannels());
            writeIcon(entry, "icon", row.icon());
            if (detailNode == null && !row.group()
                && entry.getString("name")
                    .isEmpty())
                writeInterfaceNames(entry, interfaces.names(row.link()));
            entries.appendTag(entry);
        }
        data.setTag("rows", entries);
        boolean sameNodes = sameRows(next, rows);
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
        if (changed || data.hasKey("highlight")) sync.syncToClient(0, buf -> ByteBufUtils.writeTag(buf, data));
    }

    private static void writeStatistics(NBTTagCompound data, WirelessNetworkStatistics statistics) {
        data.setInteger("devices", statistics.devices());
        data.setInteger("online", statistics.online());
        data.setInteger("missing", statistics.missing());
        data.setBoolean("stable", statistics.stable());
    }

    private static void writeIcon(NBTTagCompound data, String key, ItemStack icon) {
        if (icon == null || icon.getItem() == null) return;
        // Display-only identity, never ship inventories or other machine NBT as an icon.
        ItemStack clean = new ItemStack(icon.getItem(), 1, icon.getItemDamage());
        data.setTag(key, clean.writeToNBT(new NBTTagCompound()));
    }

    static void writeInterfaceNames(NBTTagCompound data, List<WirelessInterfaceView.Name> names) {
        if (names.isEmpty()) return;
        NBTTagList entries = new NBTTagList();
        for (var name : names.subList(0, Math.min(4, names.size()))) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setString("rawName", name.rawName());
            entry.setString("suffix", name.suffix());
            writeIcon(entry, "icon", name.icon());
            entries.appendTag(entry);
        }
        data.setTag("interfaceNames", entries);
        data.setInteger("interfaceNameCount", names.size());
        writeIcon(
            data,
            "icon",
            names.get(0)
                .icon());
    }

    static boolean sameRows(List<Row> next, List<Row> previous) {
        if (next.size() != previous.size()) return false;
        for (int i = 0; i < next.size(); i++) {
            Row a = next.get(i), b = previous.get(i);
            if (a.machine() != b.machine() || !sameTargets(a.links(), b.links())) return false;
        }
        return true;
    }

    static void localizeInterfaceNames(NBTTagCompound data) {
        NBTTagList rows = data.getTagList("rows", 10);
        for (int rowIndex = 0; rowIndex < rows.tagCount(); rowIndex++) {
            NBTTagCompound row = rows.getCompoundTagAt(rowIndex);
            NBTTagList entries = row.getTagList("interfaceNames", 10);
            if (!row.getString("name")
                .isEmpty() || entries.tagCount() == 0) continue;
            List<String> names = new ArrayList<>();
            for (int i = 0; i < entries.tagCount(); i++) {
                NBTTagCompound entry = entries.getCompoundTagAt(i);
                names.add(
                    InterfaceNameLocalization.localizeName(
                        entry.getString("rawName"),
                        entry.getString("suffix"),
                        ItemStack.loadItemStackFromNBT(entry.getCompoundTag("icon"))));
            }
            int count = row.getInteger("interfaceNameCount");
            row.setString("name", String.join(" / ", names) + (count > entries.tagCount() ? " … (" + count + ")" : ""));
        }
    }

    /** MUI owns transport and screen lifetime; application revisions still protect changing physical clusters. */
    public final class ViewSync extends SyncHandler<ViewSync> {

        @Override
        public void detectAndSendChanges(boolean init) {
            if ((init || ++ticks % 20 == 0) && canInteractWith(player)) update(init);
        }

        @SideOnly(Side.CLIENT)
        public void request(int action, String text, String dimension, int filter, long nameRevision) {
            if (!isValid() || !display.hasKey("session")) return;
            var request = new Action(
                display.getLong("session"),
                display.getLong("revision"),
                action,
                text,
                dimension,
                filter,
                nameRevision);
            syncToServer(1, request::write);
        }

        @Override
        public void readOnServer(int id, PacketBuffer buf) {
            if (id != 1) return;
            final Action request;
            try {
                request = Action.read(buf);
            } catch (IllegalArgumentException | IndexOutOfBoundsException malformed) {
                return;
            }
            ServerThreadUtil.addScheduledTask(() -> {
                if (isValid() && player.openContainer == WirelessCardContainer.this) {
                    action(
                        request.session(),
                        request.revision(),
                        request.action(),
                        request.text(),
                        request.dimension(),
                        request.filter(),
                        request.nameRevision());
                }
            });
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void readOnClient(int id, PacketBuffer buf) {
            if (id != 0) return;
            NBTTagCompound data = ByteBufUtils.readTag(buf);
            Minecraft minecraft = Minecraft.getMinecraft();
            minecraft.func_152344_a(() -> {
                if (data == null || !isValid()
                    || minecraft.thePlayer != player
                    || player.openContainer != WirelessCardContainer.this) return;
                localizeInterfaceNames(data);
                display = data;
                if (data.hasKey("highlight", 9))
                    com.science.gtnl.client.WirelessWorldHighlight.show(minecraft, data.getTagList("highlight", 10));
            });
        }
    }

    private record RenameState(Set<IGridNode> members, Map<IGridNode, String> names) {}

    record Action(long session, long revision, int action, String text, String dimension, int filter,
        long nameRevision) {

        static Action read(ByteBuf buf) {
            return new Action(
                buf.readLong(),
                buf.readLong(),
                buf.readInt(),
                readText(buf),
                readText(buf),
                buf.readUnsignedByte(),
                buf.readLong());
        }

        void write(ByteBuf buf) {
            buf.writeLong(session);
            buf.writeLong(revision);
            buf.writeInt(action);
            writeText(buf, text);
            writeText(buf, dimension);
            buf.writeByte(filter);
            buf.writeLong(nameRevision);
        }

        private static String readText(ByteBuf buf) {
            int length = buf.readUnsignedShort();
            if (length > 256 || length > buf.readableBytes()) throw new IllegalArgumentException("Oversized GUI text");
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        private static void writeText(ByteBuf buf, String value) {
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            if (bytes.length > 256) throw new IllegalArgumentException("Oversized GUI text");
            buf.writeShort(bytes.length);
            buf.writeBytes(bytes);
        }
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

    public void action(long session, long revision, int action, String text, String dimension, int filterState,
        long nameRevision) {
        if (!canInteractWith(player) || this.session != session) return;
        boolean teleport = action >= 50 && action < 50 + rows.size();
        long now = player.worldObj.getTotalWorldTime();
        if (!teleport) {
            if (now < actionAfter) return;
            actionAfter = now + 4;
        }
        if (action >= 20 && action < 30) update(false);
        if (this.revision != revision && action != 6 && action != 8) {
            update(true);
            return;
        }
        // Group rows are navigation only, even if a client submits a hidden mutation button.
        if (action >= 10 && action < 60
            && action % 10 < rows.size()
            && rows.get(action % 10)
                .group()
            && !(action >= 20 && action < 30)) return;
        if (action == 9) {
            WirelessCardVisualisation.cycle(card, player);
        } else if (action == 8) {
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
                message("filter_invalid");
            }
        } else if (action == 5 && detailNode != null && !rows.isEmpty() && binding != null) {
            var row = rows.get(0)
                .link();
            IGridNode source = binding.source()
                .node();
            if (this.nameRevision != nameRevision) {
                message("stale");
            } else if (!canBuild(row.node(), player) || !canBuild(source, player)) {
                message("denied");
            } else {
                try {
                    if (!WirelessClusterManager.rename(binding.source(), row, text, node -> canBuild(node, player)))
                        message("denied");
                } catch (IllegalStateException stale) {
                    message("stale");
                }
            }
        } else if (teleport && binding != null && player instanceof EntityPlayerMP serverPlayer) {
            var row = rows.get(action - 50)
                .link();
            try {
                if (!WirelessClusterManager.isCurrentLink(binding.source(), row)) {
                    message("stale");
                } else {
                    IGridNode source = binding.source()
                        .node();
                    if (source == null) {
                        message("teleport_unavailable");
                    } else if (!canBuild(source, player) || !canBuild(row.node(), player)) {
                        message("denied");
                    } else if (WirelessCardTeleport.teleport(serverPlayer, row.target())) return;
                }
            } catch (PhysicalClusterTracker.ScanLimitException limit) {
                message("stale");
            }
        } else if (action >= 40 && action < 40 + rows.size()) {
            var row = rows.get(action - 40)
                .link();
            if (row.target()
                .dimension() != player.dimension) {
                message(
                    "highlight_dimension",
                    row.target()
                        .dimension());
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
                message("highlight_started", targets.size());
            }
        } else if (action == 0) page--;
        else if (action == 1) page++;
        else if (action == 2) {
            WirelessAutoConnect.toggleAutomatic(player, card);
        } else if (action == 4) {
            if (detailNode != null) detailNode = null;
            else groupMachine = null;
            page = 0;
        } else if (action >= 20 && action < 20 + rows.size() && detailNode == null) {
            Row row = rows.get(action - 20);
            if (row.group()) groupMachine = row.machine();
            else detailNode = row.link()
                .node();
            page = 0;
        } else if (action >= 10 && action < 10 + rows.size() && binding != null) {
            var row = rows.get(action - 10)
                .link();
            IGridNode source = binding.source()
                .node();
            // Never act on a replacement device or guess an unloaded source's permission when resuming.
            if (row.target()
                .node() != row.node() || !canBuild(row.node(), player)
                || source != null && !canBuild(source, player)
                || row.paused() && source == null) {
                message("denied");
            } else {
                try {
                    WirelessClusterManager.setPaused(binding.source(), row, !row.paused());
                } catch (IllegalStateException failure) {
                    message("stale");
                }
            }
        } else if (action != 3) return;
        update(true);
    }

    private void message(String key, Object... args) {
        player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui." + key, args));
    }

}
