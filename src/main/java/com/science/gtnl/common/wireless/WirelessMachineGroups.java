package com.science.gtnl.common.wireless;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.mixinHelper.IInterfaceNameHatch;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;

import appeng.api.implementations.parts.IPartCable;
import appeng.helpers.IInterfaceHost;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public class WirelessMachineGroups {

    public static class Startup {

        public static final long TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(10);
        private static final int REPORTED_CONTROLLERS = 8;
        private final Set<World> inspected = new ReferenceOpenHashSet<>();
        private final List<MTEMultiBlockBase> pending = new ObjectArrayList<>();
        private boolean finished;
        private long deadline;

        boolean waiting(List<IWirelessLinkView> links) {
            return waiting(links, System.nanoTime());
        }

        boolean waiting(List<IWirelessLinkView> links, long now) {
            if (finished) return false;
            for (IWirelessLinkView link : links) {
                World world = link.node()
                    .getWorld();
                if (world == null || !inspected.add(world)) continue;
                deadline = now + TIMEOUT_NANOS;
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
            // A controller that is not ticking never finishes its first check; the view must still open.
            if (!pending.isEmpty() && now - deadline >= 0) giveUp();
            finished = !inspected.isEmpty() && pending.isEmpty();
            return !pending.isEmpty();
        }

        private void giveUp() {
            StringBuilder stuck = new StringBuilder();
            for (MTEMultiBlockBase controller : pending.subList(0, Math.min(REPORTED_CONTROLLERS, pending.size()))) {
                var base = controller.getBaseMetaTileEntity();
                stuck.append("\n  ")
                    .append(
                        controller.getClass()
                            .getName());
                if (base != null) stuck.append(" D")
                    .append(base.getWorld() == null ? "?" : base.getWorld().provider.dimensionId)
                    .append(' ')
                    .append(base.getXCoord())
                    .append(',')
                    .append(base.getYCoord())
                    .append(',')
                    .append(base.getZCoord());
                stuck.append(" startUpCheck=")
                    .append(controller.getmStartUpCheck());
            }
            ScienceNotLeisure.LOG
                .warn("Wireless card view stopped waiting for {} multiblock controllers:{}", pending.size(), stuck);
            pending.clear();
        }
    }

    record Classification(MTEMultiBlockBase machine, TypeKey type) {

        private static final Classification UNGROUPED = new Classification(null, null);
    }

    public enum TypeKind {
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

    record Row(MTEMultiBlockBase machine, TypeKey type, List<IWirelessLinkView> links,
        WirelessNetworkStatistics statistics) {

        Row(MTEMultiBlockBase machine, List<IWirelessLinkView> links) {
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

        static Row cluster(IWirelessLinkView link) {
            return cluster(link, null);
        }

        static Row cluster(IWirelessLinkView link, TypeKey type) {
            return new Row(null, type, List.of(link), link.statistics());
        }

        boolean group() {
            return machine != null;
        }

        IWirelessLinkView link() {
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
                .allMatch(IWirelessLinkView::paused);
        }

        boolean conflicted() {
            return links.stream()
                .anyMatch(IWirelessLinkView::conflicted);
        }

        int entrances() {
            return links.stream()
                .mapToInt(IWirelessLinkView::entrances)
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

    static List<Row> group(List<IWirelessLinkView> links, WirelessInterfaceView interfaces) {
        List<Row> rows = new ObjectArrayList<>();
        Map<MTEMultiBlockBase, List<IWirelessLinkView>> machines = new Reference2ObjectOpenHashMap<>();
        Reference2BooleanOpenHashMap<MTEMultiBlockBase> validControllers = new Reference2BooleanOpenHashMap<>();
        for (IWirelessLinkView link : links) {
            Classification classification = classify(link, validControllers, interfaces);
            if (classification.machine() == null) rows.add(Row.cluster(link, classification.type()));
            else machines.computeIfAbsent(classification.machine(), ignored -> new ObjectArrayList<>())
                .add(link);
        }
        machines.forEach((machine, members) -> rows.add(new Row(machine, members)));
        return rows;
    }

    static Classification classify(IWirelessLinkView link, Reference2BooleanMap<MTEMultiBlockBase> validControllers,
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

    static WirelessNetworkStatistics summarize(List<IWirelessLinkView> links) {
        int nodes = 0, devices = 0, online = 0, missing = 0;
        boolean stable = true;
        for (IWirelessLinkView link : links) {
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
