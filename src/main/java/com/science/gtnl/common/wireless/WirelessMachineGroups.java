package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.api.mixinHelper.IInterfaceNameHatch;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.common.wireless.WirelessClusterManager.LinkView;

import appeng.api.implementations.parts.IPartCable;
import appeng.helpers.IInterfaceHost;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;

/** Presentation groups only; physical clusters remain the unit of every network operation. */
final class WirelessMachineGroups {

    private WirelessMachineGroups() {}

    static final class Startup {

        private final Set<World> inspected = Collections.newSetFromMap(new IdentityHashMap<>());
        private final List<MTEMultiBlockBase> pending = new ArrayList<>();
        private boolean finished;

        boolean waiting(List<LinkView> links) {
            if (finished) return false;
            for (LinkView link : links) {
                World world = link.node()
                    .getWorld();
                if (world == null || !inspected.add(world)) continue;
                // Ownership is rebuilt by GT's first structure check, independently of AE node readiness.
                for (Object tile : world.loadedTileEntityList) {
                    if (tile instanceof IGregTechTileEntity base
                        && base.getMetaTileEntity() instanceof MTEMultiBlockBase controller
                        && controller.getmStartUpCheck() > 0
                        && !controller.mMachine) pending.add(controller);
                }
            }
            pending.removeIf(
                controller -> controller.getmStartUpCheck() <= 0 || controller.mMachine
                    || !loadedController(controller));
            finished = !inspected.isEmpty() && pending.isEmpty();
            return !pending.isEmpty();
        }
    }

    record Classification(MTEMultiBlockBase machine, TypeKey type) {

        private static final Classification UNGROUPED = new Classification(null, null);
    }

    enum TypeKind {
        MACHINE,
        DEVICE,
        SHARED_HATCH
    }

    record TypeKey(TypeKind kind, Item item, int damage, int dimension) {

        static TypeKey of(TypeKind kind, ItemStack icon, int dimension) {
            return icon == null || icon.getItem() == null ? null
                : new TypeKey(kind, icon.getItem(), icon.getItemDamage(), dimension);
        }

        boolean shared() {
            return kind == TypeKind.SHARED_HATCH;
        }

        ItemStack icon() {
            return new ItemStack(item, 1, damage);
        }

        String sortKey() {
            return kind + ":" + Item.itemRegistry.getNameForObject(item) + ":" + damage + ":" + dimension;
        }
    }

    record Row(MTEMultiBlockBase machine, TypeKey type, List<Row> children, List<LinkView> links,
        WirelessNetworkStatistics statistics) {

        Row(MTEMultiBlockBase machine, List<LinkView> links) {
            this(
                machine,
                machine == null ? null
                    : TypeKey.of(
                        TypeKind.MACHINE,
                        machine.getStackForm(1),
                        links.get(0)
                            .target()
                            .dimension()),
                List.of(),
                List.copyOf(links),
                summarize(links));
        }

        static Row cluster(LinkView link) {
            return cluster(link, null);
        }

        static Row cluster(LinkView link, TypeKey type) {
            return new Row(null, type, List.of(), List.of(link), link.statistics());
        }

        static Row category(TypeKey type, List<Row> children) {
            List<LinkView> links = children.stream()
                .flatMap(
                    row -> row.links()
                        .stream())
                .toList();
            return new Row(null, type, List.copyOf(children), links, summarize(links));
        }

        boolean category() {
            return !children.isEmpty();
        }

        boolean group() {
            return machine != null || category();
        }

        private RowKey orderKey() {
            return new RowKey(category() ? type : null, category() ? null : target(), group());
        }

        LinkView link() {
            if (group()) throw new IllegalStateException("A presentation group is not an actionable physical cluster");
            return links.get(0);
        }

        Address target() {
            if (machine == null) return links.get(0)
                .target();
            var base = machine.getBaseMetaTileEntity();
            return new Address(
                base.getWorld().provider.dimensionId,
                base.getXCoord(),
                base.getYCoord(),
                base.getZCoord(),
                ForgeDirection.UNKNOWN);
        }

        ItemStack icon() {
            return machine != null ? machine.getStackForm(1)
                : type != null ? type.icon()
                    : link().node()
                        .getGridBlock()
                        .getMachineRepresentation();
        }

        boolean paused() {
            return links.stream()
                .allMatch(LinkView::paused);
        }

        boolean conflicted() {
            return links.stream()
                .anyMatch(LinkView::conflicted);
        }

        int entrances() {
            return links.stream()
                .mapToInt(LinkView::entrances)
                .sum();
        }

        String state() {
            if (!group()) return link().state();
            if (conflicted()) return "conflict";
            if (links.stream()
                .anyMatch(
                    link -> link.state()
                        .equals("limited")))
                return "limited";
            if (paused()) return "paused";
            return links.stream()
                .allMatch(
                    link -> link.state()
                        .equals("active")) ? "active" : "waiting";
        }

        Row filtered(WirelessViewFilter filter, WirelessInterfaceView interfaces) {
            boolean searching = !filter.query()
                .isEmpty();
            if (!searching && filter.dimension() == null && filter.state() == 0) return this;
            ItemStack icon = searching ? icon() : null;
            String machineName = (group() || type != null) && icon != null ? icon.getDisplayName() : "";
            String position = searching ? coordinates(target()) : "";
            List<LinkView> matches = links.stream()
                .filter(link -> {
                    String text = "", positions = "";
                    if (searching) {
                        ItemStack device = link.node()
                            .getGridBlock()
                            .getMachineRepresentation();
                        StringBuilder names = new StringBuilder(machineName).append(' ')
                            .append(String.join(" ", link.names()))
                            .append(' ')
                            .append(device == null ? "" : device.getDisplayName());
                        for (var name : interfaces.names(link)) names.append(' ')
                            .append(name.text());
                        text = names.toString();
                        positions = position + " " + coordinates(link.target());
                    }
                    return filter.matches(
                        text,
                        link.target()
                            .dimension(),
                        positions,
                        link.paused(),
                        link.conflicted() ? "conflict" : link.state());
                })
                .toList();
            return matches.isEmpty() ? null
                : matches.size() == links.size() ? this
                    : new Row(machine, type, List.of(), matches, summarize(matches));
        }
    }

    static List<Row> group(List<LinkView> links, WirelessInterfaceView interfaces) {
        List<Row> rows = new ArrayList<>();
        Map<MTEMultiBlockBase, List<LinkView>> machines = new IdentityHashMap<>();
        Map<MTEMultiBlockBase, Boolean> validControllers = new IdentityHashMap<>();
        for (LinkView link : links) {
            Classification classification = classify(link, validControllers, interfaces);
            if (classification.machine() == null) rows.add(Row.cluster(link, classification.type()));
            else machines.computeIfAbsent(classification.machine(), ignored -> new ArrayList<>())
                .add(link);
        }
        machines.forEach((machine, members) -> rows.add(new Row(machine, members)));
        return rows;
    }

    static List<Row> collapse(List<Row> rows) {
        List<Row> result = new ArrayList<>();
        Map<TypeKey, List<Row>> types = new LinkedHashMap<>();
        for (Row row : rows) {
            if (row.type() == null) result.add(row);
            else types.computeIfAbsent(row.type(), ignored -> new ArrayList<>())
                .add(row);
        }
        types.forEach((type, children) -> {
            // Membership order must not change just because one machine's online count changes.
            children.sort(
                Comparator.comparing(
                    row -> row.target()
                        .toString()));
            result.add(children.size() == 1 ? children.get(0) : Row.category(type, children));
        });
        return result;
    }

    static void sort(List<Row> rows) {
        rows.sort(
            Comparator.comparing(Row::paused)
                .thenComparing(Row::statistics, WirelessNetworkStatistics.DISPLAY_ORDER)
                .thenComparing(
                    row -> row.category() ? row.type()
                        .sortKey()
                        : row.target()
                            .toString()));
    }

    private record RowKey(TypeKey type, Address target, boolean group) {}

    record RowOrder(RowKey key, boolean paused, boolean pinned) {}

    static List<RowOrder> sort(List<Row> rows, List<RowOrder> previous) {
        sort(rows);
        boolean[] pinned = new boolean[rows.size()];
        if (!previous.isEmpty()) {
            Map<RowKey, Row> remaining = new LinkedHashMap<>();
            for (Row row : rows) remaining.put(row.orderKey(), row);
            Row[] ordered = new Row[rows.size()];
            int lastSlot = ordered.length - 1;
            // Only a pause observed in this view pins a row; already-paused arrivals stay at the end.
            for (int i = previous.size() - 1; i >= 0; i--) {
                RowOrder old = previous.get(i);
                Row row = remaining.get(old.key());
                if (row == null || !row.paused() || old.paused() && !old.pinned()) continue;
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
        List<RowOrder> order = new ArrayList<>(rows.size());
        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
            order.add(new RowOrder(row.orderKey(), row.paused(), pinned[i]));
        }
        return order;
    }

    static Classification classify(LinkView link, Map<MTEMultiBlockBase, Boolean> validControllers,
        WirelessInterfaceView interfaces) {
        Set<MTEMultiBlockBase> owners = null;
        TypeKey type = null;
        TypeKey sharedType = null;
        boolean pureDevices = true;
        boolean machineDevices = true;
        boolean pureSharedHatches = true;
        for (var node : link.members()) {
            Object host = node.getMachine();
            if (host instanceof IPartCable) continue;
            Object machine = host instanceof IGregTechTileEntity base ? base.getMetaTileEntity() : host;
            machineDevices &= machine instanceof MTEHatch || host instanceof IInterfaceHost;
            if (pureDevices) {
                TypeKey next = TypeKey.of(
                    TypeKind.DEVICE,
                    node.getGridBlock()
                        .getMachineRepresentation(),
                    link.target()
                        .dimension());
                if (next == null || type != null && !type.equals(next)) pureDevices = false;
                if (type == null) type = next;
            }
            if (!machineDevices && !pureDevices) return Classification.UNGROUPED;
            if (!(machine instanceof MTEHatch)) pureSharedHatches = false;
            List<MTEHatch> hatches;
            if (machine instanceof MTEHatch hatch) hatches = List.of(hatch);
            else if (host instanceof IInterfaceHost iface) {
                hatches = interfaces.hatches(iface);
                if (hatches == null) return Classification.UNGROUPED;
            } else continue;
            for (MTEHatch target : hatches) {
                if (!(target instanceof IInterfaceNameHatch hatch)) return Classification.UNGROUPED;
                var controllers = hatch.getInterfaceNameControllers();
                // Different owner sets make this a cross-machine cluster, including shared/exclusive mixtures.
                if (owners != null && !owners.equals(controllers)) return Classification.UNGROUPED;
                for (var controller : controllers) {
                    if (!validControllers.computeIfAbsent(controller, WirelessMachineGroups::validController))
                        return Classification.UNGROUPED;
                }
                owners = controllers;
                if (controllers.size() > 1) {
                    if (!pureSharedHatches) return Classification.UNGROUPED;
                    TypeKey next = TypeKey.of(
                        TypeKind.SHARED_HATCH,
                        target.getStackForm(1),
                        link.target()
                            .dimension());
                    if (next == null || sharedType != null && !sharedType.equals(next)) return Classification.UNGROUPED;
                    sharedType = next;
                }
            }
        }
        if (owners == null || owners.isEmpty())
            return pureDevices && type != null ? new Classification(null, type) : Classification.UNGROUPED;
        if (!machineDevices) return Classification.UNGROUPED;
        if (owners.size() == 1) return new Classification(
            owners.iterator()
                .next(),
            null);
        TypeKey result = pureSharedHatches ? sharedType : null;
        return result == null ? Classification.UNGROUPED : new Classification(null, result);
    }

    private static boolean validController(MTEMultiBlockBase controller) {
        return controller.mMachine && loadedController(controller);
    }

    private static boolean loadedController(MTEMultiBlockBase controller) {
        var base = controller.getBaseMetaTileEntity();
        return controller.isValid() && base != null
            && base.getWorld() != null
            && base.getWorld()
                .blockExists(base.getXCoord(), base.getYCoord(), base.getZCoord())
            && base.getWorld()
                .getTileEntity(base.getXCoord(), base.getYCoord(), base.getZCoord()) == base;
    }

    static WirelessNetworkStatistics summarize(List<LinkView> links) {
        int nodes = 0, devices = 0, online = 0, missing = 0;
        boolean stable = true;
        for (LinkView link : links) {
            var stats = link.statistics();
            nodes += stats.nodes();
            devices += stats.devices();
            online += stats.online();
            missing += stats.missing();
            stable &= stats.stable();
        }
        return new WirelessNetworkStatistics(nodes, devices, online, missing, stable);
    }

    static String coordinates(Address address) {
        return "D" + address.dimension() + "  " + address.x() + ", " + address.y() + ", " + address.z();
    }
}
