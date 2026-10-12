package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessCardViewCodec.writeDimensionName;
import static com.science.gtnl.common.wireless.WirelessCardViewCodec.writeIcon;
import static com.science.gtnl.common.wireless.WirelessCardViewCodec.writeInterfaceNames;
import static com.science.gtnl.common.wireless.WirelessCardViewCodec.writeStatistics;
import static com.science.gtnl.common.wireless.WirelessMachineGroups.coordinates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.science.gtnl.common.wireless.WirelessMachineGroups.Row;
import com.science.gtnl.utils.appliedEnergistics.InterfaceNameLocalization;

import appeng.api.networking.IGridNode;

final class WirelessCardViewBuilder {

    IGridNode detailNode;
    private final WirelessMachineGroups.Startup startup = new WirelessMachineGroups.Startup();
    private Map<Target, Long> targetIds = new HashMap<>();
    private long nextTargetId, nameRevision;
    private RenameState renameState;

    long nameRevision() {
        return nameRevision;
    }

    WirelessCardSnapshot build(WirelessCardBinding binding, ItemStack card) {
        var interfaces = new WirelessInterfaceView();
        List<WirelessLinkView> all = binding == null ? List.of()
            : new ArrayList<>(WirelessClusterManager.links(binding.source()));
        boolean initializing = startup.waiting(all);
        var focus = detailNode == null ? null
            : all.stream()
                .filter(
                    link -> link.members()
                        .contains(detailNode))
                .findFirst()
                .orElse(null);
        if (focus == null) detailNode = null;
        RenameState nextRenameState = focus == null ? null : new RenameState(focus.members(), focus.deviceNames());
        if (!Objects.equals(renameState, nextRenameState)) nameRevision++;
        renameState = nextRenameState;
        List<Row> rows = initializing ? List.of()
            : focus == null ? WirelessMachineGroups.group(all, interfaces)
                : WirelessClusterManager.entranceLinks(binding.source(), focus.cluster())
                    .stream()
                    .map(Row::cluster)
                    .toList();
        NBTTagCompound data = new NBTTagCompound();
        NBTTagCompound dimensions = new NBTTagCompound();
        data.setBoolean("detail", focus != null);
        data.setBoolean("initializing", initializing);
        data.setBoolean("collapseTypes", WirelessCardBinding.collapseTypes(card.getTagCompound()));
        data.setString("name", focus == null ? "" : focus.name());
        data.setLong("nameRevision", nameRevision);
        data.setString(
            "detailTarget",
            focus == null ? ""
                : focus.target()
                    .toString());
        data.setBoolean(
            "multipleNames",
            focus != null && focus.names()
                .size() > 1);
        data.setBoolean(
            "renameable",
            focus != null && !focus.deviceNames()
                .isEmpty());
        data.setBoolean("auto", WirelessCardBinding.automatic(card.getTagCompound()));
        data.setInteger("visualMode", WirelessCardVisualisation.mode(card.getTagCompound()));
        data.setBoolean("bound", binding != null);
        writeStatistics(data, WirelessMachineGroups.summarize(focus == null ? all : List.of(focus)));
        data.setInteger("total", focus == null ? all.size() : 1);
        data.setInteger("entrances", binding == null ? 0 : WirelessChannelManager.entranceCount(binding.source()));
        data.setInteger("entranceLimit", WirelessChannelManager.entranceLimit());
        if (binding != null) {
            var sourceAddress = WirelessChannelManager.controllerSource(binding.source());
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
        Map<Target, Long> nextIds = new HashMap<>();
        Map<Long, WirelessLinkView> targets = new LinkedHashMap<>();
        if (focus != null) data.setLong("detailId", identify(focus, nextIds, targets));
        NBTTagList entries = new NBTTagList();
        for (Row row : rows) entries.appendTag(writeRow(row, interfaces, dimensions, nextIds, targets, focus != null));
        targetIds = nextIds;
        data.setTag("rows", entries);
        data.setTag("dimensions", dimensions);
        return new WirelessCardSnapshot(data, targets);
    }

    private long identify(WirelessLinkView link, Map<Target, Long> nextIds, Map<Long, WirelessLinkView> targets) {
        Target key = new Target(link.node(), link.members(), link instanceof WirelessLinkView.EntranceView);
        long id = nextIds.computeIfAbsent(key, ignored -> {
            Long previous = targetIds.get(key);
            return previous == null ? ++nextTargetId : previous;
        });
        targets.put(id, link);
        return id;
    }

    private NBTTagCompound writeRow(Row row, WirelessInterfaceView interfaces, NBTTagCompound dimensions,
        Map<Target, Long> nextIds, Map<Long, WirelessLinkView> targets, boolean detail) {
        var target = row.target();
        NBTTagCompound entry = new NBTTagCompound();
        writeDimensionName(dimensions, target.dimension());
        entry.setInteger("dimension", target.dimension());
        entry.setString("position", coordinates(target));
        entry.setString("key", (row.group() ? "machine:" : "cluster:") + target);
        entry.setString("sortKey", target.toString());
        entry.setString(
            "typeKey",
            row.type() == null ? ""
                : row.type()
                    .sortKey());
        entry.setBoolean("group", row.group());
        entry.setBoolean(
            "shared",
            row.type() != null && row.type()
                .shared());
        entry.setInteger(
            "clusters",
            row.links()
                .size());
        entry.setString(
            "name",
            row.group() ? ""
                : row.link()
                    .name());
        entry.setString(
            "side",
            target.side()
                .name());
        entry.setString("state", row.conflicted() ? "conflict" : row.state());
        entry.setBoolean("paused", row.paused());
        entry.setBoolean("conflicted", row.conflicted());
        entry.setInteger("entrances", row.entrances());
        writeStatistics(entry, row.statistics());
        var entrance = !row.group() && row.link() instanceof WirelessLinkView.EntranceView runtime ? runtime : null;
        entry.setBoolean("runtime", entrance != null);
        entry.setInteger(
            "usedChannels",
            entrance == null ? -1
                : entrance.usedChannels()
                    .orElse(-1));
        ItemStack icon = row.icon();
        writeIcon(entry, "icon", icon);
        if (row.type() != null) writeIcon(
            entry,
            "typeIcon",
            row.type()
                .icon());
        entry.setString("serverIconName", icon == null ? "" : icon.getDisplayName());
        if (row.group()) {
            NBTTagList children = new NBTTagList();
            for (var link : row.links())
                children.appendTag(writeRow(Row.cluster(link), interfaces, dimensions, nextIds, targets, false));
            entry.setTag("children", children);
        } else {
            var link = row.link();
            entry.setLong("targetId", identify(link, nextIds, targets));
            if (!detail) {
                var names = interfaces.names(link);
                NBTTagList labels = new NBTTagList();
                for (var name : names) {
                    NBTTagCompound label = new NBTTagCompound();
                    label.setString("rawName", name.rawName());
                    label.setString("suffix", name.suffix());
                    label.setString(
                        "serverName",
                        InterfaceNameLocalization.localizeName(name.rawName(), name.suffix(), name.icon()));
                    writeIcon(label, "icon", name.icon());
                    labels.appendTag(label);
                }
                entry.setTag("searchLabels", labels);
                NBTTagList customNames = new NBTTagList();
                link.names()
                    .stream()
                    .sorted()
                    .forEach(name -> customNames.appendTag(new NBTTagString(name)));
                entry.setTag("customNames", customNames);
                // Interface display icons can differ from the underlying device's searchable name.
                ItemStack device = link.node()
                    .getGridBlock()
                    .getMachineRepresentation();
                writeIcon(entry, "searchIcon", device);
                entry.setString("serverDeviceName", device == null ? "" : device.getDisplayName());
                writeInterfaceNames(entry, names);
            }
        }
        return entry;
    }

    private record Target(IGridNode node, Set<IGridNode> members, boolean entrance) {}

    private record RenameState(Set<IGridNode> members, Map<IGridNode, String> names) {}
}
