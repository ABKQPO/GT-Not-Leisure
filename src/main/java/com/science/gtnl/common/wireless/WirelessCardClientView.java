package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessCardActions.NEXT_PAGE;
import static com.science.gtnl.common.wireless.WirelessCardActions.OPEN_DETAILS;
import static com.science.gtnl.common.wireless.WirelessCardActions.PREVIOUS_PAGE;
import static com.science.gtnl.common.wireless.WirelessCardActions.rowIndex;
import static com.science.gtnl.common.wireless.WirelessCardActions.type;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.wireless.WirelessWorldHighlight;
import com.science.gtnl.common.me.InterfaceNameLocalization;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@SideOnly(Side.CLIENT)
public class WirelessCardClientView {

    public static final int STATES = 6;
    private final WirelessCardContainer container;
    private final SearchMatcher matcher = new SearchMatcher();
    private final Map<SortView, List<RowOrder>> orders = new LinkedHashMap<>(16, 0.75f, true);
    private NBTTagCompound source = new NBTTagCompound();
    private List<Row> roots = List.of();
    private NBTTagCompound groupDisplay = new NBTTagCompound();
    private String query = "", machineKey = "", typeKey = "";
    private Integer dimension;
    private int state, page, groupPage, detailPage, machineParentPage;
    private int pageSize = WirelessCardLayout.MIN_ROWS;
    private List<int[]> dimensions = List.of();
    private byte[] incoming;
    private long incomingRevision = -1;
    private int received;

    WirelessCardClientView(WirelessCardContainer container) {
        this.container = container;
    }

    void acceptChunk(long revision, int total, int offset, byte[] chunk) {
        if (revision < incomingRevision) return;
        if (offset == 0) {
            incoming = new byte[total];
            incomingRevision = revision;
            received = 0;
        }
        if (incoming == null || revision != incomingRevision || incoming.length != total || offset != received) return;
        System.arraycopy(chunk, 0, incoming, offset, chunk.length);
        received += chunk.length;
        if (received != total) return;
        byte[] complete = incoming;
        incoming = null;
        try {
            accept(CompressedStreamTools.readCompressed(new ByteArrayInputStream(complete)));
        } catch (IOException failure) {
            ScienceNotLeisure.LOG.warn("Cannot decode wireless card view", failure);
        }
    }

    private void accept(NBTTagCompound data) {
        if (data.getBoolean("detail") && (!source.getBoolean("detail") || !data.getString("detailTarget")
            .equals(source.getString("detailTarget")))) detailPage = 0;
        if (source.hasKey("session") && data.getBoolean("collapseTypes") != source.getBoolean("collapseTypes"))
            page = 0;
        WirelessCardViewCodec.localizeDimensions(data);
        source = data;
        List<Row> next = new ObjectArrayList<>();
        NBTTagList rows = data.getTagList("rows", 10);
        for (int i = 0; i < rows.tagCount(); i++) {
            NBTTagCompound row = rows.getCompoundTagAt(i);
            String prefix = row.getBoolean("group") || !row.getString("typeKey")
                .isEmpty() ? iconName(row, "icon") : "";
            next.add(readRow(row, prefix, row.getString("position")));
        }
        roots = List.copyOf(next);
        if (!data.getBoolean("detail")) dimensions = countDimensions(roots);
        if (data.hasKey("highlight", 9)) {
            WirelessWorldHighlight.show(Minecraft.getMinecraft(), data.getTagList("highlight", 10));
            data.removeTag("highlight");
        }
        rebuild();
    }

    private Row readRow(NBTTagCompound data, String prefix, String position) {
        WirelessCardViewCodec.localizePosition(data, "position", source.getCompoundTag("dimensions"));
        WirelessCardViewCodec.localizeInterfaceNames(data);
        List<Row> children = new ObjectArrayList<>();
        NBTTagList entries = data.getTagList("children", 10);
        for (int i = 0; i < entries.tagCount(); i++)
            children.add(readRow(entries.getCompoundTagAt(i), prefix, position));
        StringBuilder names = new StringBuilder(prefix);
        NBTTagList customNames = data.getTagList("customNames", 8);
        for (int i = 0; i < customNames.tagCount(); i++) names.append(' ')
            .append(customNames.getStringTagAt(i));
        names.append(' ')
            .append(iconName(data, "searchIcon"));
        NBTTagList labels = data.getTagList("searchLabels", 10);
        for (int i = 0; i < labels.tagCount(); i++) {
            NBTTagCompound label = labels.getCompoundTagAt(i);
            names.append(' ')
                .append(label.getString("serverName"))
                .append(' ')
                .append(
                    InterfaceNameLocalization.localizeName(
                        label.getString("rawName"),
                        label.getString("suffix"),
                        ItemStack.loadItemStackFromNBT(label.getCompoundTag("icon"))));
        }
        NBTTagCompound display = copy(data);
        display.removeTag("children");
        display.removeTag("searchLabels");
        return new Row(
            display,
            List.copyOf(children),
            names.toString()
                .toLowerCase(Locale.ROOT),
            (position + " " + data.getString("position")).toLowerCase(Locale.ROOT));
    }

    private static String iconName(NBTTagCompound data, String key) {
        ItemStack icon = ItemStack.loadItemStackFromNBT(data.getCompoundTag(key));
        return data.getString(key.equals("searchIcon") ? "serverDeviceName" : "serverIconName") + " "
            + (icon == null ? "" : icon.getDisplayName());
    }

    public void filter(String text, Integer dimension, int state) {
        if (text.length() > 64 || state < 0 || state >= STATES) return;
        String nextQuery = text.strip()
            .toLowerCase(Locale.ROOT);
        if (query.equals(nextQuery) && Objects.equals(this.dimension, dimension) && this.state == state) return;
        query = nextQuery;
        this.dimension = dimension;
        this.state = state;
        page = groupPage = machineParentPage = 0;
        rebuild();
    }

    public Integer dimension() {
        return dimension;
    }

    public int state() {
        return state;
    }

    public List<int[]> dimensions() {
        return dimensions;
    }

    public String dimensionName(int id) {
        return source.getCompoundTag("dimensions")
            .getString(Integer.toString(id));
    }

    public NBTTagCompound groupDisplay() {
        return groupDisplay;
    }

    public int groupDepth() {
        return (typeKey.isEmpty() ? 0 : 1) + (machineKey.isEmpty() ? 0 : 1);
    }

    public void leaveGroups(int keep) {
        if (keep <= 0) closeGroup();
        else if (!typeKey.isEmpty() && !machineKey.isEmpty()) {
            machineKey = "";
            groupPage = machineParentPage;
        }
        rebuild();
    }

    public void closeGroup() {
        machineKey = "";
        typeKey = "";
        groupPage = machineParentPage = 0;
        groupDisplay = new NBTTagCompound();
    }

    public void resize(int rows) {
        if (!WirelessCardLayout.validRows(rows) || rows == pageSize) return;
        page = page * pageSize / rows;
        detailPage = detailPage * pageSize / rows;
        groupPage = groupPage * pageSize / rows;
        machineParentPage = machineParentPage * pageSize / rows;
        pageSize = rows;
        rebuild();
    }

    public boolean navigate(int action, boolean groupWindow) {
        boolean detail = source.getBoolean("detail");
        if (groupWindow && (detail || !groupDisplay.getBoolean("group"))) return true;
        if (action == PREVIOUS_PAGE || action == NEXT_PAGE) {
            int change = action == NEXT_PAGE ? 1 : -1;
            if (detail) detailPage += change;
            else if (groupWindow) groupPage += change;
            else page += change;
        } else if (type(action) == OPEN_DETAILS && !detail) {
            NBTTagList rows = (groupWindow ? groupDisplay : container.display).getTagList("rows", 10);
            int index = rowIndex(action);
            if (index < 0 || index >= rows.tagCount()) return true;
            NBTTagCompound row = rows.getCompoundTagAt(index);
            if (!row.getBoolean("group")) return false;
            if (!groupWindow) closeGroup();
            if (row.getBoolean("category")) {
                typeKey = row.getString("typeKey");
            } else {
                machineParentPage = groupPage;
                machineKey = row.getString("key");
            }
            groupPage = 0;
        } else return false;
        rebuild();
        return true;
    }

    public void rebuild() {
        if (!source.hasKey("session")) return;
        if (source.getBoolean("detail")) {
            container.display = buildListing(null, "", detailPage);
            detailPage = container.display.getInteger("page");
            return;
        }
        if (!typeKey.isEmpty() && roots.stream()
            .noneMatch(
                row -> typeKey.equals(
                    row.data()
                        .getString("typeKey"))))
            closeGroup();
        Row machine = machineKey.isEmpty() ? null
            : roots.stream()
                .filter(
                    row -> row.data()
                        .getBoolean("group")
                        && machineKey.equals(
                            row.data()
                                .getString("key"))
                        && (typeKey.isEmpty() || typeKey.equals(
                            row.data()
                                .getString("typeKey"))))
                .findFirst()
                .orElse(null);
        if (!machineKey.isEmpty() && machine == null) {
            machineKey = "";
            groupPage = machineParentPage;
            if (typeKey.isEmpty()) closeGroup();
        }
        container.display = buildListing(null, "", page);
        page = container.display.getInteger("page");
        if (machine != null || !typeKey.isEmpty()) {
            groupDisplay = buildListing(machine, typeKey, groupPage);
            groupPage = groupDisplay.getInteger("page");
        }
    }

    private NBTTagCompound buildListing(Row machine, String typeKey, int requestedPage) {
        boolean detail = source.getBoolean("detail");
        NBTTagCompound data = copy(source);
        List<Row> rows = new ObjectArrayList<>();
        if (detail) {
            rows.addAll(roots);
        } else {
            int[] counts = new int[STATES];
            for (Row row : machine == null ? roots : List.of(machine)) {
                if (!typeKey.isEmpty() && !typeKey.equals(
                    row.data()
                        .getString("typeKey")))
                    continue;
                for (Row leaf : row.children()
                    .isEmpty() ? List.of(row) : row.children()) {
                    if (!matchesPlace(leaf)) continue;
                    for (int s = 0; s < STATES; s++) if (matchesState(leaf.data(), s)) counts[s]++;
                }
                Row matched = filtered(row);
                if (matched == null) continue;
                if (machine == null) rows.add(matched);
                else rows.addAll(matched.children());
            }
            data.setIntArray("stateCounts", counts);
            if (!data.getBoolean("initializing")) {
                NBTTagCompound statistics = aggregate(new NBTTagCompound(), rows);
                for (String key : List.of("devices", "online", "missing", "stable"))
                    data.setTag(key, statistics.getTag(key));
                data.setInteger(
                    "total",
                    rows.stream()
                        .mapToInt(
                            row -> row.data()
                                .getInteger("clusters"))
                        .sum());
            }
            if (data.getBoolean("collapseTypes") && typeKey.isEmpty() && machine == null) rows = collapse(rows);
            var sortView = new SortView(
                machine == null ? ""
                    : machine.data()
                        .getString("key"),
                typeKey,
                data.getBoolean("collapseTypes"),
                query,
                dimension,
                state);
            orders.put(sortView, sort(rows, orders.getOrDefault(sortView, List.of())));
            if (orders.size() > 16) orders.remove(
                orders.keySet()
                    .iterator()
                    .next());
            data.setBoolean("group", machine != null || !typeKey.isEmpty());
            data.setBoolean("typeGroup", !typeKey.isEmpty());
            data.setBoolean("machineGroup", machine != null);
            Row first = typeKey.isEmpty() ? null
                : roots.stream()
                    .filter(
                        row -> typeKey.equals(
                            row.data()
                                .getString("typeKey")))
                    .findFirst()
                    .orElseThrow();
            if (first != null) data.setTag(
                "typeIcon",
                first.data()
                    .getCompoundTag("typeIcon"));
            if (machine != null) {
                data.setTag(
                    "groupIcon",
                    machine.data()
                        .getCompoundTag("icon"));
                data.setString(
                    "groupPosition",
                    machine.data()
                        .getString("position"));
            } else if (first != null) {
                data.setTag(
                    "groupIcon",
                    first.data()
                        .getCompoundTag("typeIcon"));
                data.setString(
                    "groupPosition",
                    "D" + first.data()
                        .getInteger("dimension"));
            }
            data.setBoolean(
                "sharedGroup",
                !typeKey.isEmpty() && roots.stream()
                    .anyMatch(
                        row -> typeKey.equals(
                            row.data()
                                .getString("typeKey"))
                            && row.data()
                                .getBoolean("shared")));
            WirelessCardViewCodec.localizePosition(data, "groupPosition", data.getCompoundTag("dimensions"));
        }
        data.setInteger("rowCount", rows.size());
        int pages = Math.max(1, (rows.size() + pageSize - 1) / pageSize);
        int currentPage = Math.max(0, Math.min(requestedPage, pages - 1));
        data.setInteger("page", currentPage);
        data.setInteger("pageSize", pageSize);
        data.setInteger("pages", pages);
        data.setInteger("filterState", state);
        data.setBoolean("filtered", !query.isEmpty() || dimension != null || state != 0);
        NBTTagList entries = new NBTTagList();
        for (Row row : rows.subList(currentPage * pageSize, Math.min(rows.size(), (currentPage + 1) * pageSize))) {
            if (row.data()
                .getBoolean("category"))
                WirelessCardViewCodec.localizePosition(row.data(), "position", data.getCompoundTag("dimensions"));
            entries.appendTag(row.data());
        }
        data.setTag("rows", entries);
        return data;
    }

    private Row filtered(Row row) {
        if (!row.children()
            .isEmpty()) {
            List<Row> children = row.children()
                .stream()
                .filter(this::matches)
                .toList();
            return children.isEmpty() ? null
                : new Row(aggregate(row.data(), children), children, row.names(), row.positions());
        }
        return matches(row) ? row : null;
    }

    private boolean matches(Row row) {
        return matchesState(row.data(), state) && matchesPlace(row);
    }

    private boolean matchesPlace(Row row) {
        if (dimension != null && dimension != row.data()
            .getInteger("dimension")) return false;
        return query.isEmpty() || (row.names() + " " + row.positions()).contains(query)
            || matcher.contains(row.names(), query);
    }

    private static boolean matchesState(NBTTagCompound data, int state) {
        boolean paused = data.getBoolean("paused");
        return switch (state) {
            case 1 -> paused;
            case 2 -> data.getBoolean("conflicted");
            case 3 -> !paused && data.getString("state")
                .equals("active");
            case 4 -> !paused && data.getString("state")
                .equals("waiting");
            case 5 -> !paused && data.getBoolean("stable") && data.getInteger("missing") > 0;
            default -> true;
        };
    }

    private static List<int[]> countDimensions(List<Row> rows) {
        Map<Integer, int[]> counts = new TreeMap<>();
        for (Row row : rows) for (Row leaf : row.children()
            .isEmpty() ? List.of(row) : row.children()) {
                int id = leaf.data()
                    .getInteger("dimension");
                counts.computeIfAbsent(id, key -> new int[] { key, 0 })[1]++;
            }
        return List.copyOf(counts.values());
    }

    private static List<Row> collapse(List<Row> rows) {
        List<Row> result = new ObjectArrayList<>();
        Map<String, List<Row>> types = new Object2ObjectLinkedOpenHashMap<>();
        for (Row row : rows) {
            String key = row.data()
                .getString("typeKey");
            if (key.isEmpty()) result.add(row);
            else types.computeIfAbsent(key, ignored -> new ObjectArrayList<>())
                .add(row);
        }
        types.forEach((key, children) -> {
            children.sort(
                Comparator.comparing(
                    row -> row.data()
                        .getString("sortKey")));
            if (children.size() == 1) result.add(children.get(0));
            else {
                NBTTagCompound category = aggregate(
                    children.get(0)
                        .data(),
                    children);
                category.setBoolean("group", true);
                category.setBoolean("category", true);
                category.setString("key", "type:" + key);
                category.setString("sortKey", key);
                category.setString("name", "");
                category.setTag("icon", category.getCompoundTag("typeIcon"));
                category.removeTag("targetName");
                category.setString("position", "D" + category.getInteger("dimension"));
                category.setInteger("items", children.size());
                category.removeTag("targetId");
                result.add(new Row(category, List.copyOf(children), "", ""));
            }
        });
        return result;
    }

    private static NBTTagCompound aggregate(NBTTagCompound template, List<Row> rows) {
        NBTTagCompound data = copy(template);
        int devices = 0, online = 0, missing = 0, entrances = 0, clusters = 0;
        boolean stable = true, paused = true, conflict = false, limited = false, active = true;
        for (Row row : rows) {
            NBTTagCompound child = row.data();
            devices += child.getInteger("devices");
            online += child.getInteger("online");
            missing += child.getInteger("missing");
            entrances += child.getInteger("entrances");
            clusters += child.getInteger("clusters");
            stable &= child.getBoolean("stable");
            paused &= child.getBoolean("paused");
            conflict |= child.getBoolean("conflicted");
            limited |= child.getString("state")
                .equals("limited");
            active &= child.getString("state")
                .equals("active");
        }
        data.setInteger("devices", devices);
        data.setInteger("online", online);
        data.setInteger("missing", missing);
        data.setInteger("entrances", entrances);
        data.setInteger("clusters", clusters);
        data.setBoolean("stable", stable);
        data.setBoolean("paused", paused);
        data.setBoolean("conflicted", conflict);
        data.setString(
            "state",
            conflict ? "conflict" : limited ? "limited" : paused ? "paused" : active ? "active" : "waiting");
        return data;
    }

    private static List<RowOrder> sort(List<Row> rows, List<RowOrder> previous) {
        rows.sort(
            Comparator.comparing(
                (Row row) -> row.data()
                    .getBoolean("paused"))
                .thenComparingInt(
                    row -> row.data()
                        .getBoolean("stable")
                        && row.data()
                            .getInteger("missing") > 0 ? 0 : 1)
                .thenComparing(
                    Comparator.comparingInt(
                        (Row row) -> row.data()
                            .getInteger("online"))
                        .reversed())
                .thenComparing(
                    row -> row.data()
                        .getString("sortKey")));
        boolean[] pinned = new boolean[rows.size()];
        if (!previous.isEmpty()) {
            Map<String, Row> remaining = new Object2ObjectLinkedOpenHashMap<>();
            for (Row row : rows) remaining.put(
                row.data()
                    .getString("key"),
                row);
            Row[] ordered = new Row[rows.size()];
            int lastSlot = ordered.length - 1;
            for (int i = previous.size() - 1; i >= 0; i--) {
                RowOrder old = previous.get(i);
                Row row = remaining.get(old.key());
                if (row == null || !row.data()
                    .getBoolean("paused") || old.paused() && !old.pinned()) continue;
                int slot = Math.min(i, lastSlot);
                ordered[slot] = row;
                pinned[slot] = true;
                lastSlot = slot - 1;
                remaining.remove(old.key());
            }
            var moving = remaining.values()
                .iterator();
            for (int i = 0; i < ordered.length; i++) rows.set(i, ordered[i] == null ? moving.next() : ordered[i]);
        }
        List<RowOrder> order = new ObjectArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            NBTTagCompound row = rows.get(i)
                .data();
            order.add(new RowOrder(row.getString("key"), row.getBoolean("paused"), pinned[i]));
        }
        return order;
    }

    private static NBTTagCompound copy(NBTTagCompound source) {
        NBTTagCompound copy = new NBTTagCompound();
        for (Object key : source.func_150296_c()) copy.setTag((String) key, source.getTag((String) key));
        return copy;
    }

    private record Row(NBTTagCompound data, List<Row> children, String names, String positions) {}

    private record RowOrder(String key, boolean paused, boolean pinned) {}

    private record SortView(String machine, String type, boolean collapse, String query, Integer dimension,
        int state) {}

    private static class SearchMatcher {

        private Object api;
        private Method contains;

        SearchMatcher() {
            if (!Loader.isModLoaded("nech")) return;
            try {
                Class<?> type = Class.forName("com.asdflj.nech.API");
                api = type.getField("INSTANCE")
                    .get(null);
                contains = type.getMethod("contains", String.class, String.class);
            } catch (ReflectiveOperationException | LinkageError failure) {
                ScienceNotLeisure.LOG.warn("Cannot initialize optional Rework search", failure);
            }
        }

        boolean contains(String text, String query) {
            if (contains == null) return false;
            try {
                return (boolean) contains.invoke(api, text, query);
            } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
                contains = null;
                ScienceNotLeisure.LOG.warn("Rework search failed; using literal matching", failure);
                return false;
            }
        }
    }
}
