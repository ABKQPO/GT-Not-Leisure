package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessCardActions.APPLY_FILTER;
import static com.science.gtnl.common.wireless.WirelessCardActions.BACK;
import static com.science.gtnl.common.wireless.WirelessCardActions.CLOSE_DETAILS;
import static com.science.gtnl.common.wireless.WirelessCardActions.CYCLE_VISUALISATION;
import static com.science.gtnl.common.wireless.WirelessCardActions.HIGHLIGHT;
import static com.science.gtnl.common.wireless.WirelessCardActions.NEXT_PAGE;
import static com.science.gtnl.common.wireless.WirelessCardActions.OPEN_DETAILS;
import static com.science.gtnl.common.wireless.WirelessCardActions.PREVIOUS_PAGE;
import static com.science.gtnl.common.wireless.WirelessCardActions.REFRESH;
import static com.science.gtnl.common.wireless.WirelessCardActions.RENAME;
import static com.science.gtnl.common.wireless.WirelessCardActions.SET_PAGE_SIZE;
import static com.science.gtnl.common.wireless.WirelessCardActions.TELEPORT;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_AUTO;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_GROUPING;
import static com.science.gtnl.common.wireless.WirelessCardActions.TOGGLE_PAUSE;
import static com.science.gtnl.common.wireless.WirelessCardActions.isRowAction;
import static com.science.gtnl.common.wireless.WirelessCardActions.rowIndex;
import static com.science.gtnl.common.wireless.WirelessCardActions.type;
import static com.science.gtnl.common.wireless.WirelessChannelManager.canBuild;
import static com.science.gtnl.common.wireless.WirelessMachineGroups.coordinates;

import java.util.ArrayList;
import java.util.HashMap;
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
import net.minecraftforge.common.DimensionManager;

import com.cleanroommc.modularui.network.NetworkUtils;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.item.items.WirelessFrequencyCard;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.common.wireless.WirelessMachineGroups.Row;
import com.science.gtnl.common.wireless.WirelessMachineGroups.RowOrder;
import com.science.gtnl.common.wireless.WirelessMachineGroups.TypeKey;
import com.science.gtnl.utils.appliedEnergistics.InterfaceNameLocalization;

import appeng.api.networking.IGridNode;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import galacticgreg.api.enums.DimensionDef;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gtneioreplugin.util.DimensionHelper;

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
    private int overviewPage;
    private int typeParentPage, machineParentPage;
    private int pageSize = WirelessCardLayout.MIN_ROWS;
    private int ticks;
    private IGridNode detailNode;
    private MTEMultiBlockBase groupMachine;
    private TypeKey groupType;
    private boolean collapseTypes = true;
    private WirelessViewFilter filter = WirelessViewFilter.parse("", "", 0);
    private NBTTagList pendingHighlight;
    private List<Row> rows = List.of();
    private final WirelessMachineGroups.Startup startup = new WirelessMachineGroups.Startup();
    private final Map<SortView, List<RowOrder>> rowOrders = new HashMap<>();
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
        collapseTypes = WirelessCardBinding.collapseTypes(card == null ? null : card.getTagCompound());
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
        boolean initializing = startup.waiting(all);
        var focus = detailNode == null ? null
            : all.stream()
                .filter(
                    link -> link.members()
                        .contains(detailNode))
                .findFirst()
                .orElse(null);
        if (detailNode != null && focus == null) {
            detailNode = null;
            page = overviewPage;
        }
        List<Row> groups = !initializing && (detailNode == null || groupMachine != null || groupType != null)
            ? WirelessMachineGroups.group(all, interfaces)
            : List.of();
        Row selectedGroup = validateGroups(groups);
        List<WirelessClusterManager.LinkView> summary = List.of();
        String detailName = "";
        String detailTarget = "";
        boolean multipleNames = false;
        RenameState nextRenameState = null;
        if (focus != null) {
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
                        .contains(detailNode))
                || groupType != null && groups.stream()
                    .noneMatch(
                        row -> groupType.equals(row.type()) && row.links()
                            .stream()
                            .anyMatch(
                                link -> link.members()
                                    .contains(detailNode)))) {
                groupMachine = null;
                groupType = null;
                selectedGroup = null;
                overviewPage = 0;
            }
        }
        List<Row> visible = new ArrayList<>();
        if (initializing) {
            summary = all;
        } else if (detailNode != null) {
            for (var link : all) visible.add(Row.cluster(link));
        } else {
            for (Row row : selectedGroup == null ? groups : List.of(selectedGroup)) {
                if (groupType != null && !groupType.equals(row.type())) continue;
                Row filtered = row.filtered(filter, interfaces);
                if (filtered == null) continue;
                if (selectedGroup == null) visible.add(filtered);
                else for (var link : filtered.links()) visible.add(Row.cluster(link));
            }
            summary = visible.stream()
                .flatMap(
                    row -> row.links()
                        .stream())
                .toList();
            if (collapseTypes && groupType == null && selectedGroup == null)
                visible = WirelessMachineGroups.collapse(visible);
            var sortView = new SortView(
                selectedGroup == null ? null : selectedGroup.target(),
                groupType,
                collapseTypes,
                filter);
            rowOrders.put(sortView, WirelessMachineGroups.sort(visible, rowOrders.getOrDefault(sortView, List.of())));
        }
        if (!Objects.equals(renameState, nextRenameState)) nameRevision++;
        renameState = nextRenameState;
        int pages = Math.max(1, (visible.size() + pageSize - 1) / pageSize);
        page = Math.max(0, Math.min(page, pages - 1));
        List<Row> next = visible.subList(page * pageSize, Math.min(visible.size(), (page + 1) * pageSize));
        NBTTagCompound data = new NBTTagCompound();
        NBTTagCompound dimensions = new NBTTagCompound();
        data.setInteger("page", page);
        data.setInteger("pageSize", pageSize);
        data.setInteger("pages", pages);
        data.setInteger("total", summary.size());
        data.setBoolean("detail", detailNode != null);
        data.setBoolean("initializing", initializing);
        data.setBoolean("group", groupMachine != null || groupType != null);
        data.setBoolean("collapseTypes", collapseTypes);
        data.setBoolean("typeGroup", groupType != null);
        data.setBoolean("sharedGroup", groupType != null && groupType.shared());
        if (selectedGroup != null) {
            Address target = selectedGroup.target();
            writeIcon(data, "groupIcon", selectedGroup.icon());
            data.setString("groupPosition", coordinates(target));
            writeDimensionName(dimensions, target.dimension());
        } else if (groupType != null) {
            writeIcon(data, "groupIcon", groupType.icon());
            data.setString("groupPosition", "D" + groupType.dimension());
            writeDimensionName(dimensions, groupType.dimension());
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
            Address sourceAddress = WirelessChannelManager.controllerSource(binding.source());
            data.setString("source", coordinates(sourceAddress));
            writeDimensionName(dimensions, sourceAddress.dimension());
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
            Address target = row.target();
            boolean group = row.group();
            NBTTagCompound entry = new NBTTagCompound();
            writeDimensionName(dimensions, target.dimension());
            entry.setString("position", row.category() ? "D" + target.dimension() : coordinates(target));
            entry.setBoolean("group", group);
            entry.setBoolean("category", row.category());
            entry.setBoolean(
                "shared",
                row.type() != null && row.type()
                    .shared());
            entry.setInteger(
                "items",
                row.children()
                    .size());
            entry.setInteger(
                "clusters",
                row.links()
                    .size());
            entry.setString(
                "name",
                group ? ""
                    : detailNode == null ? row.link()
                        .name() : detailName);
            entry.setString(
                "side",
                target.side()
                    .name());
            entry.setString("state", row.state());
            entry.setBoolean("paused", row.paused());
            entry.setBoolean("conflicted", row.conflicted());
            entry.setInteger("entrances", row.entrances());
            writeStatistics(entry, row.statistics());
            entry.setBoolean(
                "runtime",
                !group && row.link()
                    .runtimeEntrance());
            entry.setInteger(
                "usedChannels",
                group ? -1
                    : row.link()
                        .usedChannels());
            writeIcon(entry, "icon", row.icon());
            if (detailNode == null && !group) writeInterfaceNames(entry, interfaces.names(row.link()));
            entries.appendTag(entry);
        }
        data.setTag("rows", entries);
        data.setTag("dimensions", dimensions);
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

    private Row validateGroups(List<Row> groups) {
        if (groupType != null && groups.stream()
            .noneMatch(row -> groupType.equals(row.type()))) {
            groupType = null;
            groupMachine = null;
            restoreOverviewPage(typeParentPage);
        }
        Row selected = groupMachine == null ? null
            : groups.stream()
                .filter(row -> row.machine() == groupMachine && (groupType == null || groupType.equals(row.type())))
                .findFirst()
                .orElse(null);
        if (groupMachine != null && selected == null) {
            groupMachine = null;
            restoreOverviewPage(machineParentPage);
        }
        return selected;
    }

    private void restoreOverviewPage(int restoredPage) {
        if (detailNode == null) page = restoredPage;
        else overviewPage = restoredPage;
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

    private static void writeDimensionName(NBTTagCompound dimensions, int dimension) {
        String id = Integer.toString(dimension);
        if (dimensions.hasKey(id)) return;
        var world = DimensionManager.getWorld(dimension);
        dimensions.setString(id, world == null ? "" : Objects.toString(DimensionDef.getDimensionName(world), ""));
    }

    private static void localizeDimensions(NBTTagCompound data) {
        NBTTagCompound dimensions = data.getCompoundTag("dimensions");
        for (Object key : dimensions.func_150296_c()) {
            String id = (String) key;
            String name = dimensions.getString(id);
            if (!name.isEmpty()) dimensions.setString(id, DimensionHelper.getDimLocalizedName(name));
        }
        localizePosition(data, "source", dimensions);
        localizePosition(data, "groupPosition", dimensions);
        NBTTagList rows = data.getTagList("rows", 10);
        for (int i = 0; i < rows.tagCount(); i++) localizePosition(rows.getCompoundTagAt(i), "position", dimensions);
    }

    private static void localizePosition(NBTTagCompound data, String key, NBTTagCompound dimensions) {
        String position = data.getString(key);
        int end = position.indexOf(' ');
        String name = position.startsWith("D")
            ? dimensions.getString(position.substring(1, end < 0 ? position.length() : end))
            : "";
        // Keep the numeric form for coordinate copying and server-side filtering.
        data.setString(key + "Label", name.isEmpty() ? position : name + " " + position);
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
            if (a.machine() != b.machine() || !Objects.equals(a.type(), b.type())
                || a.category() != b.category()
                || (a.category() ? !sameRows(a.children(), b.children()) : !sameTargets(a.links(), b.links())))
                return false;
        }
        return true;
    }

    static void localizeInterfaceNames(NBTTagCompound data) {
        NBTTagList rows = data.getTagList("rows", 10);
        for (int rowIndex = 0; rowIndex < rows.tagCount(); rowIndex++) {
            NBTTagCompound row = rows.getCompoundTagAt(rowIndex);
            NBTTagList entries = row.getTagList("interfaceNames", 10);
            if (entries.tagCount() == 0) continue;
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
            String targetName = String.join(" / ", names) + (count > entries.tagCount() ? " … (" + count + ")" : "");
            row.setString("targetName", targetName);
            if (row.getString("name")
                .isEmpty()) row.setString("name", targetName);
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
            } catch (RuntimeException malformed) {
                return;
            }
            if (request.text() == null || request.dimension() == null) return;
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
                localizeDimensions(data);
                display = data;
                if (data.hasKey("highlight", 9))
                    com.science.gtnl.client.WirelessWorldHighlight.show(minecraft, data.getTagList("highlight", 10));
            });
        }
    }

    private record RenameState(Set<IGridNode> members, Map<IGridNode, String> names) {}

    private record SortView(Address machine, TypeKey type, boolean collapseTypes, WirelessViewFilter filter) {}

    record Action(long session, long revision, int action, String text, String dimension, int filter,
        long nameRevision) {

        static Action read(PacketBuffer buf) {
            return new Action(
                buf.readLong(),
                buf.readLong(),
                buf.readInt(),
                NetworkUtils.readStringSafe(buf),
                NetworkUtils.readStringSafe(buf),
                buf.readUnsignedByte(),
                buf.readLong());
        }

        void write(PacketBuffer buf) {
            buf.writeLong(session);
            buf.writeLong(revision);
            buf.writeInt(action);
            NetworkUtils.writeStringSafe(buf, text);
            NetworkUtils.writeStringSafe(buf, dimension);
            buf.writeByte(filter);
            buf.writeLong(nameRevision);
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
        // Closing a client panel must not be lost to throttling or a changing row revision.
        if (action == CLOSE_DETAILS) {
            if (detailNode != null) {
                detailNode = null;
                page = overviewPage;
                update(true);
            }
            return;
        }
        int actionType = type(action);
        int index = rowIndex(action);
        boolean teleport = actionType == TELEPORT && index < rows.size();
        long now = player.worldObj.getTotalWorldTime();
        if (!teleport) {
            if (now < actionAfter) return;
            actionAfter = now + 4;
        }
        if (actionType == OPEN_DETAILS) update(false);
        if (this.revision != revision && action != APPLY_FILTER && action != SET_PAGE_SIZE) {
            update(true);
            return;
        }
        // Group rows are navigation only, even if a client submits a hidden mutation button.
        if (isRowAction(action) && index < rows.size()
            && rows.get(index)
                .group()
            && actionType != OPEN_DETAILS) return;
        if (action == CYCLE_VISUALISATION) {
            WirelessCardVisualisation.cycle(card, player);
        } else if (action == SET_PAGE_SIZE) {
            // View-only layout negotiation reuses the bounded integer payload; never accept arbitrary row IDs.
            if (!WirelessCardLayout.validRows(filterState)) return;
            page = page * pageSize / filterState;
            overviewPage = overviewPage * pageSize / filterState;
            typeParentPage = typeParentPage * pageSize / filterState;
            machineParentPage = machineParentPage * pageSize / filterState;
            pageSize = filterState;
        } else if (action == APPLY_FILTER) {
            try {
                filter = WirelessViewFilter.parse(text, dimension, filterState);
                detailNode = null;
                groupMachine = null;
                groupType = null;
                page = 0;
            } catch (IllegalArgumentException invalid) {
                message("filter_invalid");
            }
        } else if (action == RENAME && detailNode != null && !rows.isEmpty() && binding != null) {
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
            var row = rows.get(index)
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
        } else if (actionType == HIGHLIGHT && index < rows.size()) {
            var row = rows.get(index)
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
        } else if (action == PREVIOUS_PAGE) page--;
        else if (action == NEXT_PAGE) page++;
        else if (action == TOGGLE_AUTO) {
            WirelessAutoConnect.toggleAutomatic(player, card);
        } else if (action == TOGGLE_GROUPING && detailNode == null) {
            collapseTypes = WirelessCardBinding.toggleGrouping(card);
            groupMachine = null;
            groupType = null;
            page = 0;
            WirelessCardInventory.sync(player, card);
        } else if (action == BACK) {
            if (detailNode != null) {
                detailNode = null;
                page = overviewPage;
            } else if (groupMachine != null) {
                groupMachine = null;
                page = machineParentPage;
            } else if (groupType != null) {
                groupType = null;
                page = typeParentPage;
            }
        } else if (actionType == OPEN_DETAILS && index < rows.size() && detailNode == null) {
            Row row = rows.get(index);
            if (row.category()) {
                typeParentPage = page;
                groupType = row.type();
            } else if (row.machine() != null) {
                machineParentPage = page;
                groupMachine = row.machine();
            } else {
                overviewPage = page;
                detailNode = row.link()
                    .node();
            }
            page = 0;
        } else if (actionType == TOGGLE_PAUSE && index < rows.size() && binding != null) {
            var row = rows.get(index)
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
        } else if (action != REFRESH) return;
        update(true);
    }

    private void message(String key, Object... args) {
        player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui." + key, args));
    }

}
