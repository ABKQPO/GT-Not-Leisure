package com.science.gtnl.common.crafting.cycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import appeng.api.AEApi;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.IActionHost;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.DimensionalCoord;
import appeng.me.cache.CraftingGridCache;

/**
 * Finds the crafting cycles a request can run into, whatever calculates it: the patterns reachable from the requested
 * item whose inputs lead back to their own outputs, and the machines that hold them. A cycle none of whose patterns
 * makes more of the cycle than it takes cannot make anything on its own, which is what a calculation reports as having
 * no net output.
 */
public final class RecursivePatternFinder {

    private static final int MAX_KEYS = 8192;

    /**
     * @param pattern the encoded pattern, by which the crafts of a crafting tree are matched to it
     * @param icon    what a crafting tree shows for the machine crafting it
     */
    public record PatternLocation(@Nullable ItemStack pattern, IAEStack<?> output, List<DimensionalCoord> machines,
        @Nullable ItemStack icon) {}

    public record Cycle(boolean netOutput, List<IAEStack<?>> items, List<PatternLocation> patterns) {}

    private final Function<IAEStack<?>, Collection<ICraftingPatternDetails>> craftingFor;
    private final Function<ICraftingPatternDetails, List<DimensionalCoord>> machines;
    private final Function<ICraftingPatternDetails, ItemStack> icons;
    private final List<IAEStack<?>> keys = new ArrayList<>();
    private final Map<IAEStack<?>, Integer> index = new HashMap<>();
    // Per key, the patterns making it and, per pattern, the keys it takes
    private final List<List<ICraftingPatternDetails>> patterns = new ArrayList<>();
    private final List<int[]> edges = new ArrayList<>();

    private RecursivePatternFinder(Function<IAEStack<?>, Collection<ICraftingPatternDetails>> craftingFor,
        Function<ICraftingPatternDetails, List<DimensionalCoord>> machines,
        Function<ICraftingPatternDetails, ItemStack> icons) {
        this.craftingFor = craftingFor;
        this.machines = machines;
        this.icons = icons;
    }

    public static List<Cycle> find(CraftingGridCache crafting, IAEStack<?> request, World world) {
        return find(request, stack -> crafting.getCraftingFor(stack, null, 0, world), pattern -> {
            var machines = new ArrayList<DimensionalCoord>();
            for (ICraftingMedium medium : crafting.getMediums(pattern)) {
                var coord = locate(medium);
                if (coord != null && !machines.contains(coord)) {
                    machines.add(coord);
                }
            }
            return machines;
        }, pattern -> {
            for (ICraftingMedium medium : crafting.getMediums(pattern)) {
                var icon = medium.getCrafterIcon();
                if (icon != null) return icon;
            }
            return AEApi.instance()
                .definitions()
                .blocks()
                .iface()
                .maybeStack(1)
                .orNull();
        });
    }

    /**
     * @param craftingFor the patterns making a key
     * @param machines    where a pattern is
     * @param icons       the machine crafting a pattern, as a crafting tree shows it
     */
    public static List<Cycle> find(IAEStack<?> request,
        Function<IAEStack<?>, Collection<ICraftingPatternDetails>> craftingFor,
        Function<ICraftingPatternDetails, List<DimensionalCoord>> machines,
        Function<ICraftingPatternDetails, ItemStack> icons) {
        var finder = new RecursivePatternFinder(craftingFor, machines, icons);
        finder.explore(request);
        return finder.cycles();
    }

    private int indexOf(IAEStack<?> stack) {
        var key = stack.copy();
        key.setStackSize(1);
        var known = this.index.get(key);
        if (known != null) {
            return known;
        }
        int id = this.keys.size();
        this.keys.add(key);
        this.index.put(key, id);
        this.patterns.add(null);
        this.edges.add(null);
        return id;
    }

    // Breadth-first over every key the request can need, with the patterns that make each one
    private void explore(IAEStack<?> request) {
        var queue = new ArrayList<Integer>();
        queue.add(indexOf(request));
        for (int head = 0; head < queue.size() && this.keys.size() < MAX_KEYS; head++) {
            int id = queue.get(head);
            if (this.patterns.get(id) != null) continue;
            var found = new ArrayList<>(this.craftingFor.apply(this.keys.get(id)));
            this.patterns.set(id, found);
            var targets = new LinkedHashSet<Integer>();
            for (var pattern : found) {
                for (var input : pattern.getCondensedAEInputs()) {
                    if (input == null) continue;
                    int target = indexOf(input);
                    if (targets.add(target) && this.patterns.get(target) == null) {
                        queue.add(target);
                    }
                }
            }
            this.edges.set(
                id,
                targets.stream()
                    .mapToInt(Integer::intValue)
                    .toArray());
        }
    }

    private int[] edgesOf(int id) {
        var targets = this.edges.get(id);
        return targets == null ? new int[0] : targets;
    }

    // Iterative Tarjan: every strongly connected group of keys with a pattern leading back into it
    private List<Cycle> cycles() {
        int count = this.keys.size();
        int[] order = new int[count];
        int[] low = new int[count];
        boolean[] onStack = new boolean[count];
        java.util.Arrays.fill(order, -1);
        var stack = new ArrayList<Integer>();
        var result = new ArrayList<Cycle>();
        int counter = 0;
        for (int start = 0; start < count; start++) {
            if (order[start] >= 0) continue;
            var work = new ArrayList<int[]>();
            work.add(new int[] { start, 0 });
            order[start] = low[start] = counter++;
            stack.add(start);
            onStack[start] = true;
            while (!work.isEmpty()) {
                var frame = work.get(work.size() - 1);
                int node = frame[0];
                var targets = edgesOf(node);
                if (frame[1] < targets.length) {
                    int next = targets[frame[1]++];
                    if (order[next] < 0) {
                        order[next] = low[next] = counter++;
                        stack.add(next);
                        onStack[next] = true;
                        work.add(new int[] { next, 0 });
                    } else if (onStack[next]) {
                        low[node] = Math.min(low[node], order[next]);
                    }
                    continue;
                }
                work.remove(work.size() - 1);
                if (!work.isEmpty()) {
                    int parent = work.get(work.size() - 1)[0];
                    low[parent] = Math.min(low[parent], low[node]);
                }
                if (low[node] == order[node]) {
                    var members = new LinkedHashSet<Integer>();
                    int member;
                    do {
                        member = stack.remove(stack.size() - 1);
                        onStack[member] = false;
                        members.add(member);
                    } while (member != node);
                    var cycle = describe(members);
                    if (cycle != null) {
                        result.add(cycle);
                    }
                }
            }
        }
        // Cycles without net output first: they are what stops a calculation
        result.sort((a, b) -> Boolean.compare(a.netOutput(), b.netOutput()));
        return result;
    }

    private @Nullable Cycle describe(Set<Integer> members) {
        var inCycle = new ArrayList<ICraftingPatternDetails>();
        boolean netOutput = false;
        for (int member : members) {
            var made = this.patterns.get(member);
            if (made == null) continue;
            for (var pattern : made) {
                long taken = 0;
                boolean closes = false;
                for (var input : pattern.getCondensedAEInputs()) {
                    if (input == null) continue;
                    var key = this.index.get(normalized(input));
                    if (key != null && members.contains(key)) {
                        closes = true;
                        taken += input.getStackSize();
                    }
                }
                if (!closes || inCycle.contains(pattern)) continue;
                long given = 0;
                for (var output : pattern.getCondensedAEOutputs()) {
                    if (output == null) continue;
                    var key = this.index.get(normalized(output));
                    if (key != null && members.contains(key)) {
                        given += output.getStackSize();
                    }
                }
                inCycle.add(pattern);
                netOutput |= given > taken;
            }
        }
        if (inCycle.isEmpty()) {
            return null;
        }
        var items = new ArrayList<IAEStack<?>>();
        for (int member : members) {
            items.add(this.keys.get(member));
        }
        var located = new ArrayList<PatternLocation>();
        for (var pattern : inCycle) {
            var outputs = pattern.getCondensedAEOutputs();
            var output = outputs.length > 0 && outputs[0] != null ? outputs[0].copy() : null;
            if (output == null) continue;
            var encoded = pattern.getPattern();
            var icon = this.icons.apply(pattern);
            located.add(
                new PatternLocation(
                    encoded == null ? null : encoded.copy(),
                    output,
                    this.machines.apply(pattern),
                    icon == null ? null : icon.copy()));
        }
        return new Cycle(netOutput, items, located);
    }

    private static IAEStack<?> normalized(IAEStack<?> stack) {
        var key = stack.copy();
        key.setStackSize(1);
        return key;
    }

    private static @Nullable DimensionalCoord locate(Object medium) {
        IGridNode node = null;
        if (medium instanceof IActionHost host) {
            node = host.getActionableNode();
        }
        if (node == null && medium instanceof IGridHost host) {
            node = host.getGridNode(ForgeDirection.UNKNOWN);
        }
        if (node != null && node.getGridBlock() != null) {
            return node.getGridBlock()
                .getLocation();
        }
        if (medium instanceof TileEntity tile) {
            return new DimensionalCoord(tile);
        }
        return null;
    }
}
