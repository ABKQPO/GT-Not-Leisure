package com.science.gtnl.client.quantum;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.client.quantum.CraftingCycleIndex.Match;
import com.science.gtnl.client.quantum.CraftingTreeRings.Ring;
import com.science.gtnl.common.crafting.cycle.RecursivePatternFinder.Cycle;

import appeng.api.config.CraftingMode;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEStack;
import appeng.client.gui.widgets.GuiCraftingTree;
import appeng.crafting.v2.CraftingRequest;
import appeng.crafting.v2.resolvers.CraftableItemResolver.CraftFromPatternTask;
import appeng.util.item.AEItemStack;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

/**
 * Makes a ring of a crafting cycle found in the patterns, for when the crafting tree has none to show: the lightweight
 * calculation makes no tree, and the standard one keeps what a pattern takes of its own output out of it. The ring is
 * made of the nodes a crafting tree would have for one round of the cycle, each craft done once.
 */
public final class CraftingCycleRings {

    private static final int MAX_STEPS = 50_000;

    /**
     * @param matches the cycle pattern each craft of the ring uses
     * @param omitted the patterns of the cycle the ring does not go through
     */
    public record Built(Ring ring, Map<Object, Match> matches, int omitted) {}

    /**
     * @param start the item the ring should go through, if it can
     */
    public static @Nullable Built build(GuiCraftingTree tree, CraftingCycleIndex index, Cycle cycle,
        @Nullable IAEStack<?> start, World world) {
        return build(tree, index, cycle, start, match -> {
            var stack = match.location()
                .pattern();
            return stack != null && stack.getItem() instanceof ICraftingPatternItem item
                ? item.getPatternForItem(stack, world)
                : null;
        });
    }

    /**
     * @param decode the pattern a cycle pattern is encoded as
     */
    public static @Nullable Built build(GuiCraftingTree tree, CraftingCycleIndex index, Cycle cycle,
        @Nullable IAEStack<?> start, Function<Match, ICraftingPatternDetails> decode) {
        var patterns = new ObjectArrayList<ICraftingPatternDetails>();
        var located = new ObjectArrayList<Match>();
        for (var match : index.all(List.of(cycle))) {
            var details = decode.apply(match);
            if (details == null) continue;
            patterns.add(details);
            located.add(match);
        }
        if (patterns.isEmpty()) return null;
        var items = cycle.items();
        var order = new ObjectArrayList<IAEStack<?>>();
        if (start != null) {
            for (var item : items) {
                if (item.isSameType(start)) order.add(item);
            }
        }
        for (var item : items) {
            if (!order.contains(item)) order.add(item);
        }
        for (var first : order) {
            var search = new Search(first, patterns, items);
            search.from(first);
            if (search.best != null) {
                return assemble(tree, first, search.best, search.bestChain, patterns, located);
            }
        }
        return null;
    }

    // The longest way around the cycle from an item back to it, through each pattern at most once
    private static final class Search {

        private final IAEStack<?> start;
        private final List<ICraftingPatternDetails> patterns;
        private final List<IAEStack<?>> items;
        private final boolean[] used;
        private final IntArrayList path = new IntArrayList();
        private final List<IAEStack<?>> chain = new ObjectArrayList<>();
        private IntArrayList best;
        private List<IAEStack<?>> bestChain;
        private int steps;

        private Search(IAEStack<?> start, List<ICraftingPatternDetails> patterns, List<IAEStack<?>> items) {
            this.start = start;
            this.patterns = patterns;
            this.items = items;
            this.used = new boolean[patterns.size()];
        }

        private boolean inCycle(IAEStack<?> stack) {
            for (var item : this.items) {
                if (item.isSameType(stack)) return true;
            }
            return false;
        }

        private void from(IAEStack<?> item) {
            if (this.steps++ > MAX_STEPS) return;
            for (int p = 0; p < this.patterns.size(); p++) {
                if (this.used[p] || !makes(this.patterns.get(p), item)) continue;
                this.used[p] = true;
                this.path.add(p);
                for (var input : this.patterns.get(p)
                    .getCondensedAEInputs()) {
                    if (input == null || !inCycle(input)) continue;
                    if (input.isSameType(this.start)) {
                        if (this.best == null || this.path.size() > this.best.size()) {
                            this.best = new IntArrayList(this.path);
                            this.bestChain = new ObjectArrayList<>(this.chain);
                            this.bestChain.add(input);
                        }
                    } else if (this.chain.stream()
                        .noneMatch(input::isSameType)) {
                            this.chain.add(input);
                            from(input);
                            this.chain.remove(this.chain.size() - 1);
                        }
                }
                this.path.removeInt(this.path.size() - 1);
                this.used[p] = false;
            }
        }
    }

    private static boolean makes(ICraftingPatternDetails pattern, IAEStack<?> item) {
        for (var output : pattern.getCondensedAEOutputs()) {
            if (output != null && output.isSameType(item)) return true;
        }
        return false;
    }

    private static long made(ICraftingPatternDetails pattern, IAEStack<?> item) {
        for (var output : pattern.getCondensedAEOutputs()) {
            if (output != null && output.isSameType(item)) return output.getStackSize();
        }
        return 1;
    }

    private static CraftingRequest request(@Nullable CraftingRequest parent, IAEStack<?> stack) {
        return new CraftingRequest(
            parent,
            stack,
            CraftingRequest.SubstitutionMode.PRECISE,
            false,
            CraftingMode.STANDARD,
            x -> true);
    }

    private static Built assemble(GuiCraftingTree tree, IAEStack<?> start, IntList path, List<IAEStack<?>> chain,
        List<ICraftingPatternDetails> patterns, List<Match> located) {
        var first = patterns.get(path.getInt(0));
        var request = request(
            null,
            start.copy()
                .setStackSize(made(first, start)));
        var entry = CraftingTreeRings.newRequestNode(tree, null, request);
        var members = new ObjectArrayList<Object>();
        members.add(entry);
        var external = new ObjectArrayList<Object>();
        var matches = new Reference2ObjectOpenHashMap<Object, Match>();
        Object parent = entry;
        for (int k = 0; k < path.size(); k++) {
            var pattern = patterns.get(path.getInt(k));
            var match = located.get(path.getInt(k));
            var task = new CraftFromPatternTask(request, pattern, 0, false, false);
            CraftingTreeRings.setCraftsDone(task, 1);
            var icon = match.location()
                .icon();
            if (icon != null) task.craftingMachine = AEItemStack.create(icon);
            var resolver = new CraftingRequest.UsedResolverEntry(request, task, request.stack.copy());
            request.usedResolvers.add(resolver);
            var taskNode = CraftingTreeRings.newTaskNode(tree, parent, resolver);
            members.add(taskNode);
            matches.put(taskNode, match);
            var next = chain.get(k);
            Object nextNode = null;
            CraftingRequest nextRequest = null;
            for (var input : pattern.getCondensedAEInputs()) {
                if (input == null) continue;
                var inputRequest = request(request, input.copy());
                var node = CraftingTreeRings.newRequestNode(tree, taskNode, inputRequest);
                if (nextNode == null && input.isSameType(next)) {
                    nextNode = node;
                    nextRequest = inputRequest;
                } else {
                    external.add(node);
                }
            }
            members.add(nextNode);
            parent = nextNode;
            request = nextRequest;
        }
        var back = new ReferenceOpenHashSet<>();
        back.add(parent);
        return new Built(new Ring(entry, members, back, external), matches, patterns.size() - path.size());
    }
}
