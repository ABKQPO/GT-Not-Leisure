package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.api.mixinHelper.IInterfaceNameHatch;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;

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

        boolean waiting(List<WirelessLinkView> links) {
            if (finished) return false;
            for (WirelessLinkView link : links) {
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

    record Row(MTEMultiBlockBase machine, TypeKey type, List<WirelessLinkView> links,
        WirelessNetworkStatistics statistics) {

        Row(MTEMultiBlockBase machine, List<WirelessLinkView> links) {
            this(
                machine,
                machine == null ? null
                    : TypeKey.of(
                        TypeKind.MACHINE,
                        machine.getStackForm(1),
                        links.get(0)
                            .target()
                            .dimension()),
                List.copyOf(links),
                summarize(links));
        }

        static Row cluster(WirelessLinkView link) {
            return cluster(link, null);
        }

        static Row cluster(WirelessLinkView link, TypeKey type) {
            return new Row(null, type, List.of(link), link.statistics());
        }

        boolean group() {
            return machine != null;
        }

        WirelessLinkView link() {
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
                .allMatch(WirelessLinkView::paused);
        }

        boolean conflicted() {
            return links.stream()
                .anyMatch(WirelessLinkView::conflicted);
        }

        int entrances() {
            return links.stream()
                .mapToInt(WirelessLinkView::entrances)
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

    }

    static List<Row> group(List<WirelessLinkView> links, WirelessInterfaceView interfaces) {
        List<Row> rows = new ArrayList<>();
        Map<MTEMultiBlockBase, List<WirelessLinkView>> machines = new IdentityHashMap<>();
        Map<MTEMultiBlockBase, Boolean> validControllers = new IdentityHashMap<>();
        for (WirelessLinkView link : links) {
            Classification classification = classify(link, validControllers, interfaces);
            if (classification.machine() == null) rows.add(Row.cluster(link, classification.type()));
            else machines.computeIfAbsent(classification.machine(), ignored -> new ArrayList<>())
                .add(link);
        }
        machines.forEach((machine, members) -> rows.add(new Row(machine, members)));
        return rows;
    }

    static Classification classify(WirelessLinkView link, Map<MTEMultiBlockBase, Boolean> validControllers,
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

    static WirelessNetworkStatistics summarize(List<WirelessLinkView> links) {
        int nodes = 0, devices = 0, online = 0, missing = 0;
        boolean stable = true;
        for (WirelessLinkView link : links) {
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
