package com.science.gtnl.common.wireless;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldServer;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IMachineSet;
import appeng.tile.networking.TileController;
import appeng.tile.networking.TileCreativeEnergyController;
import appeng.util.ReadOnlyCollection;
import sun.misc.Unsafe;

/** Geometry and actual grid-index budget checks; native routing still needs in-game acceptance. */
public final class ChannelBudgetTest {

    public static void main(String[] args) {
        var a = new ChannelBudget.Position(0, 0, 64, 0);
        check(ChannelBudget.capacityOf(Set.of()) == 0, "No controllers supply no channels");
        check(ChannelBudget.capacityOf(Set.of(a)) == 192, "Single controller exposes six faces");
        check(
            ChannelBudget.capacityOf(Set.of(a, new ChannelBudget.Position(0, 1, 64, 0))) == 320,
            "Two adjacent controllers share two internal faces");
        check(
            ChannelBudget.capacityOf(Set.of(a, new ChannelBudget.Position(1, 1, 64, 0))) == 384,
            "Coordinates in different dimensions are not neighbors");
        Set<ChannelBudget.Position> cube = new HashSet<>();
        for (int x = 0; x < 3; x++) for (int y = 0; y < 3; y++) for (int z = 0; z < 3; z++) {
            cube.add(new ChannelBudget.Position(0, x, y, z));
        }
        // Geometry only: AE2 remains responsible for validating whether a controller structure is legal.
        check(ChannelBudget.capacityOf(cube) == 54 * 32, "Internal faces cannot mint wireless capacity");
        ChannelBudget budget = new ChannelBudget(192);
        // A wired branch consumes 32; six wireless branches request another 192.
        for (int i = 0; i < 32; i++) budget.recordAllocation();
        int acceptedWireless = 0;
        for (int branch = 0; branch < 6; branch++) for (int i = 0; i < 32; i++) {
            if (budget.hasRemaining()) {
                budget.recordAllocation();
                acceptedWireless++;
            }
        }
        check(acceptedWireless == 160 && budget.allocated() == 192, "Wired and wireless share one total budget");
        boolean rejected = false;
        try {
            budget.recordAllocation();
        } catch (IllegalStateException expected) {
            rejected = true;
        }
        check(rejected, "Cannot overdraw the budget");
        check(!new ChannelBudget(0).hasRemaining(), "Unsupported structures fail closed");
        check(new ChannelBudget(192).allocated() == 0, "Repath starts a new allocation pass");
        System.out.println("ChannelBudgetTest: all policy checks passed.");
    }

    /** Run by the Mixin harness after AE's optional power interfaces have been stripped. */
    public static void retainedMachineClasses() throws Exception {
        // Construct inert coordinate fixtures without starting a server or initializing AE's item registry.
        var field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);
        World world = (World) unsafe.allocateInstance(WorldServer.class);
        var provider = World.class.getField("provider");
        provider.setAccessible(true);
        provider.set(world, new WorldProviderSurface());
        TileEntity ordinary = (TileEntity) unsafe.allocateInstance(TileController.class);
        ordinary.setWorldObj(world);
        TileEntity creative = (TileEntity) unsafe.allocateInstance(TileCreativeEnergyController.class);
        creative.setWorldObj(world);
        creative.xCoord = 1;
        Map<Class<?>, List<IGridNode>> machines = new LinkedHashMap<>();
        machines.put(TileCreativeEnergyController.class, List.of());
        machines.put(TileController.class, List.of(node(ordinary)));
        IGrid grid = (IGrid) Proxy.newProxyInstance(
            ChannelBudgetTest.class.getClassLoader(),
            new Class<?>[] { IGrid.class },
            (proxy, method, args) -> switch (method.getName()) {
            case "getMachinesClasses" -> new ReadOnlyCollection<>(machines.keySet());
            case "getMachines" -> machineSet((Class<?>) args[0], machines.get(args[0]));
            default -> throw new AssertionError(method);
            });
        check(
            WirelessChannelPrototype.capacity(grid) == 192,
            "An empty creative-controller index must not disable the remaining ordinary controller");
        machines.put(TileCreativeEnergyController.class, List.of(node(creative)));
        check(
            WirelessChannelPrototype.capacity(grid) == 320,
            "Adjacent ordinary and creative-energy controllers share one geometric budget");
        machines.put(TileController.class, List.of());
        check(
            WirelessChannelPrototype.capacity(grid) == 192,
            "A creative-energy controller supplies six channel faces, not infinite channels");
        machines.put(TileController.class, List.of(node(ordinary)));
        machines.put(TileCreativeEnergyController.class, List.of());
        check(
            WirelessChannelPrototype.capacity(grid) == 192,
            "Removing the creative controller restores the budget without rebuilding the grid index");
        machines.put(TileController.class, List.of());
        check(WirelessChannelPrototype.capacity(grid) == 0, "Empty historical indexes supply no channels");
        System.out
            .println("ChannelBudgetTest: ordinary, creative-energy, mixed and removed-controller budgets passed.");
    }

    private static IGridNode node(TileEntity tile) {
        return (IGridNode) Proxy.newProxyInstance(
            ChannelBudgetTest.class.getClassLoader(),
            new Class<?>[] { IGridNode.class },
            (proxy, method, args) -> {
                if (method.getName()
                    .equals("getMachine")) return tile;
                throw new AssertionError(method);
            });
    }

    private static IMachineSet machineSet(Class<?> type, List<IGridNode> nodes) {
        return (IMachineSet) Proxy.newProxyInstance(
            ChannelBudgetTest.class.getClassLoader(),
            new Class<?>[] { IMachineSet.class },
            (proxy, method, args) -> switch (method.getName()) {
            case "getMachineClass" -> type;
            case "iterator" -> nodes.iterator();
            case "size" -> nodes.size();
            case "isEmpty" -> nodes.isEmpty();
            case "contains" -> nodes.contains(args[0]);
            default -> throw new AssertionError(method);
            });
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
