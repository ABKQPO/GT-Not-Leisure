package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessCardViewCodec.writeDimensionName;
import static com.science.gtnl.common.wireless.WirelessCardViewCodec.writeIcon;
import static com.science.gtnl.common.wireless.WirelessCardViewCodec.writeInterfaceNames;
import static com.science.gtnl.common.wireless.WirelessCardViewCodec.writeStatistics;
import static com.science.gtnl.common.wireless.WirelessMachineGroups.coordinates;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.common.wireless.WirelessMachineGroups.Row;
import com.science.gtnl.common.wireless.WirelessMachineGroups.RowOrder;
import com.science.gtnl.common.wireless.WirelessMachineGroups.TypeKey;

import appeng.api.networking.IGridNode;

final class WirelessCardViewBuilder {

    final WirelessCardNavigation navigation = new WirelessCardNavigation();
    final WirelessSearchNames searchNames = new WirelessSearchNames();
    private final WirelessMachineGroups.Startup startup = new WirelessMachineGroups.Startup();
    private final Map<SortView, List<RowOrder>> rowOrders = new LinkedHashMap<>(16, 0.75f, true);
    private long nameRevision;
    private RenameState renameState;

    long nameRevision() {
        return nameRevision;
    }

    WirelessCardSnapshot build(WirelessCardBinding binding, ItemStack card, long session,
        java.util.function.Consumer<NBTTagCompound> sendNames) {
        var interfaces = new WirelessInterfaceView();
        List<WirelessLinkView> all = binding == null ? List.of()
            : new ArrayList<>(WirelessClusterManager.links(binding.source()));
        boolean initializing = startup.waiting(all);
        var focus = navigation.detailNode == null ? null
            : all.stream()
                .filter(
                    link -> link.members()
                        .contains(navigation.detailNode))
                .findFirst()
                .orElse(null);
        if (navigation.detailNode != null && focus == null) {
            navigation.detailNode = null;
            navigation.page = navigation.overviewPage;
        }
        List<Row> groups = !initializing
            && (navigation.detailNode == null || navigation.groupMachine != null || navigation.groupType != null)
                ? WirelessMachineGroups.group(all, interfaces)
                : List.of();
        Row selectedGroup = validateGroups(groups);
        if (!initializing && navigation.detailNode == null) searchNames.update(groups, interfaces, session, sendNames);
        List<WirelessLinkView> summary = List.of();
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
            all = WirelessClusterManager.entranceLinks(binding.source(), focus.cluster());
            if (selectedGroup != null && selectedGroup.links()
                .stream()
                .noneMatch(
                    link -> link.members()
                        .contains(navigation.detailNode))
                || navigation.groupType != null && groups.stream()
                    .noneMatch(
                        row -> navigation.groupType.equals(row.type()) && row.links()
                            .stream()
                            .anyMatch(
                                link -> link.members()
                                    .contains(navigation.detailNode)))) {
                navigation.groupMachine = null;
                navigation.groupType = null;
                selectedGroup = null;
                navigation.overviewPage = 0;
            }
        }
        List<Row> visible = new ArrayList<>();
        if (initializing) {
            summary = all;
        } else if (navigation.detailNode != null) {
            for (var link : all) visible.add(Row.cluster(link));
        } else {
            for (Row row : selectedGroup == null ? groups : List.of(selectedGroup)) {
                if (navigation.groupType != null && !navigation.groupType.equals(row.type())) continue;
                Row filtered = row.filtered(navigation.filter, interfaces, searchNames);
                if (filtered == null) continue;
                if (selectedGroup == null) visible.add(filtered);
                else for (var link : filtered.links()) visible.add(Row.cluster(link));
            }
            summary = visible.stream()
                .flatMap(
                    row -> row.links()
                        .stream())
                .toList();
            if (navigation.collapseTypes && navigation.groupType == null && selectedGroup == null)
                visible = WirelessMachineGroups.collapse(visible);
            var sortView = new SortView(
                selectedGroup == null ? null : selectedGroup.target(),
                navigation.groupType,
                navigation.collapseTypes,
                navigation.filter);
            rowOrders.put(sortView, WirelessMachineGroups.sort(visible, rowOrders.getOrDefault(sortView, List.of())));
            if (rowOrders.size() > 16) rowOrders.remove(
                rowOrders.keySet()
                    .iterator()
                    .next());
        }
        if (!Objects.equals(renameState, nextRenameState)) nameRevision++;
        renameState = nextRenameState;
        int pages = Math.max(1, (visible.size() + navigation.pageSize - 1) / navigation.pageSize);
        navigation.page = Math.max(0, Math.min(navigation.page, pages - 1));
        List<Row> next = visible.subList(
            navigation.page * navigation.pageSize,
            Math.min(visible.size(), (navigation.page + 1) * navigation.pageSize));
        NBTTagCompound data = new NBTTagCompound();
        NBTTagCompound dimensions = new NBTTagCompound();
        data.setInteger("page", navigation.page);
        data.setInteger("pageSize", navigation.pageSize);
        data.setInteger("pages", pages);
        data.setInteger("total", summary.size());
        data.setBoolean("detail", navigation.detailNode != null);
        data.setBoolean("initializing", initializing);
        data.setBoolean("group", navigation.groupMachine != null || navigation.groupType != null);
        data.setBoolean("collapseTypes", navigation.collapseTypes);
        data.setBoolean("typeGroup", navigation.groupType != null);
        data.setBoolean("sharedGroup", navigation.groupType != null && navigation.groupType.shared());
        if (selectedGroup != null) {
            Address target = selectedGroup.target();
            writeIcon(data, "groupIcon", selectedGroup.icon());
            data.setString("groupPosition", coordinates(target));
            writeDimensionName(dimensions, target.dimension());
        } else if (navigation.groupType != null) {
            writeIcon(data, "groupIcon", navigation.groupType.icon());
            data.setString("groupPosition", "D" + navigation.groupType.dimension());
            writeDimensionName(dimensions, navigation.groupType.dimension());
        }
        data.setString("name", detailName);
        data.setLong("nameRevision", nameRevision);
        data.setString("detailTarget", detailTarget);
        data.setBoolean("multipleNames", multipleNames);
        data.setBoolean(
            "renameable",
            nextRenameState != null && !nextRenameState.names()
                .isEmpty());
        data.setInteger("filterState", navigation.filter.state());
        data.setBoolean(
            "filtered",
            !navigation.filter.query()
                .isEmpty() || navigation.filter.dimension() != null || navigation.filter.state() != 0);
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
                    : navigation.detailNode == null ? row.link()
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
            var entrance = !group && row.link() instanceof WirelessLinkView.EntranceView runtime ? runtime : null;
            entry.setBoolean("runtime", entrance != null);
            entry.setInteger(
                "usedChannels",
                entrance == null ? -1
                    : entrance.usedChannels()
                        .orElse(-1));
            writeIcon(entry, "icon", row.icon());
            if (navigation.detailNode == null && !group) writeInterfaceNames(entry, interfaces.names(row.link()));
            entries.appendTag(entry);
        }
        data.setTag("rows", entries);
        data.setTag("dimensions", dimensions);
        return new WirelessCardSnapshot(data, next);
    }

    private Row validateGroups(List<Row> groups) {
        if (navigation.groupType != null && groups.stream()
            .noneMatch(row -> navigation.groupType.equals(row.type()))) {
            navigation.groupType = null;
            navigation.groupMachine = null;
            restoreOverviewPage(navigation.typeParentPage);
        }
        Row selected = navigation.groupMachine == null ? null
            : groups.stream()
                .filter(
                    row -> row.machine() == navigation.groupMachine
                        && (navigation.groupType == null || navigation.groupType.equals(row.type())))
                .findFirst()
                .orElse(null);
        if (navigation.groupMachine != null && selected == null) {
            navigation.groupMachine = null;
            restoreOverviewPage(navigation.machineParentPage);
        }
        return selected;
    }

    private void restoreOverviewPage(int restoredPage) {
        if (navigation.detailNode == null) navigation.page = restoredPage;
        else navigation.overviewPage = restoredPage;
    }

    private record RenameState(Set<IGridNode> members, Map<IGridNode, String> names) {}

    private record SortView(Address machine, TypeKey type, boolean collapseTypes, WirelessViewFilter filter) {}

}
