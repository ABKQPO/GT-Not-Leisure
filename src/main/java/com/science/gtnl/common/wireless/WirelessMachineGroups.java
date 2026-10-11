package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.api.mixinHelper.IInterfaceNameHatch;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.common.wireless.WirelessClusterManager.LinkView;

import appeng.helpers.IInterfaceHost;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;

/** Presentation groups only; physical clusters remain the unit of every network operation. */
final class WirelessMachineGroups {

    private WirelessMachineGroups() {}

    record Row(MTEMultiBlockBase machine, List<LinkView> links, WirelessNetworkStatistics statistics) {

        Row(MTEMultiBlockBase machine, List<LinkView> links) {
            this(machine, List.copyOf(links), summarize(links));
        }

        static Row cluster(LinkView link) {
            return new Row(null, List.of(link), link.statistics());
        }

        boolean group() {
            return machine != null;
        }

        LinkView link() {
            if (group()) throw new IllegalStateException("A machine group is not an actionable physical cluster");
            return links.get(0);
        }

        Address target() {
            if (!group()) return link().target();
            var base = machine.getBaseMetaTileEntity();
            return new Address(
                base.getWorld().provider.dimensionId,
                base.getXCoord(),
                base.getYCoord(),
                base.getZCoord(),
                ForgeDirection.UNKNOWN);
        }

        ItemStack icon() {
            return group() ? machine.getStackForm(1)
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
            if (filter.query()
                .isEmpty() && filter.dimension() == null
                && filter.state() == 0) return this;
            ItemStack icon = icon();
            String machineName = group() && icon != null ? icon.getDisplayName() : "";
            String position = coordinates(target());
            List<LinkView> matches = links.stream()
                .filter(link -> {
                    ItemStack device = link.node()
                        .getGridBlock()
                        .getMachineRepresentation();
                    StringBuilder names = new StringBuilder(machineName).append(' ')
                        .append(String.join(" ", link.names()))
                        .append(' ')
                        .append(device == null ? "" : device.getDisplayName());
                    if (!filter.query()
                        .isEmpty()) {
                        for (var name : interfaces.names(link)) names.append(' ')
                            .append(name.text());
                    }
                    return filter.matches(
                        names.toString(),
                        link.target()
                            .dimension(),
                        position + " " + coordinates(link.target()),
                        link.paused(),
                        link.conflicted() ? "conflict" : link.state());
                })
                .toList();
            return matches.isEmpty() ? null : matches.size() == links.size() ? this : new Row(machine, matches);
        }
    }

    static List<Row> group(List<LinkView> links, WirelessInterfaceView interfaces) {
        List<Row> rows = new ArrayList<>();
        Map<MTEMultiBlockBase, List<LinkView>> machines = new IdentityHashMap<>();
        Map<MTEMultiBlockBase, Boolean> validControllers = new IdentityHashMap<>();
        for (LinkView link : links) {
            MTEMultiBlockBase machine = owner(link, validControllers, interfaces);
            if (machine == null) rows.add(Row.cluster(link));
            else machines.computeIfAbsent(machine, ignored -> new ArrayList<>())
                .add(link);
        }
        machines.forEach((machine, members) -> rows.add(new Row(machine, members)));
        return rows;
    }

    static void sort(List<Row> rows) {
        rows.sort(
            Comparator.comparing(Row::statistics, WirelessNetworkStatistics.DISPLAY_ORDER)
                .thenComparing(
                    row -> row.target()
                        .toString()));
    }

    static MTEMultiBlockBase owner(LinkView link, Map<MTEMultiBlockBase, Boolean> validControllers,
        WirelessInterfaceView interfaces) {
        MTEMultiBlockBase owner = null;
        for (var node : link.members()) {
            Object host = node.getMachine();
            Object machine = host instanceof IGregTechTileEntity base ? base.getMetaTileEntity() : host;
            List<MTEHatch> hatches;
            if (machine instanceof MTEHatch hatch) hatches = List.of(hatch);
            else if (host instanceof IInterfaceHost iface) {
                hatches = interfaces.hatches(iface);
                if (hatches == null) return null;
            } else continue;
            for (MTEHatch target : hatches) {
                if (!(target instanceof IInterfaceNameHatch hatch)) return null;
                var controllers = hatch.getInterfaceNameControllers();
                // An unknown/shared hatch disqualifies its entire physical cluster from automatic grouping.
                if (controllers.size() != 1) return null;
                MTEMultiBlockBase controller = controllers.iterator()
                    .next();
                if (owner != null && owner != controller
                    || !validControllers.computeIfAbsent(controller, WirelessMachineGroups::validController))
                    return null;
                owner = controller;
            }
        }
        return owner;
    }

    private static boolean validController(MTEMultiBlockBase controller) {
        var base = controller.getBaseMetaTileEntity();
        return controller.mMachine && controller.isValid()
            && base != null
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
