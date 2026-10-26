package com.science.gtnl.client.quantum;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.crafting.cycle.RecursivePatternFinder.Cycle;
import com.science.gtnl.common.crafting.cycle.RecursivePatternFinder.PatternLocation;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

/**
 * The crafting cycles found for a request, by the pattern of each, so the crafts of the crafting tree can be told which
 * cycle they belong to and where its machines are.
 *
 * @param blocking the cycles the plan cannot start because of
 */
public record CraftingCycleIndex(IAEStack<?> request, List<Cycle> cycles, Set<Cycle> blocking,
    Map<Item, List<Match>> byPattern, List<Match> unencoded) {

    /**
     * A pattern of a cycle, numbered as the cycles are listed in the chat.
     */
    public record Match(int number, Cycle cycle, PatternLocation location, boolean blocking) {}

    public static CraftingCycleIndex of(IAEStack<?> request, List<Cycle> cycles, List<Cycle> blocking) {
        var set = new ReferenceOpenHashSet<Cycle>();
        set.addAll(blocking);
        var byPattern = new Object2ObjectOpenHashMap<Item, List<Match>>();
        var unencoded = new ObjectArrayList<Match>();
        for (int i = 0; i < cycles.size(); i++) {
            var cycle = cycles.get(i);
            for (var location : cycle.patterns()) {
                var match = new Match(i + 1, cycle, location, set.contains(cycle));
                if (location.pattern() == null) {
                    unencoded.add(match);
                } else {
                    byPattern.computeIfAbsent(
                        location.pattern()
                            .getItem(),
                        k -> new ObjectArrayList<>())
                        .add(match);
                }
            }
        }
        return new CraftingCycleIndex(request, cycles, set, byPattern, unencoded);
    }

    public @Nullable Match match(ICraftingPatternDetails pattern) {
        var encoded = pattern.getPattern();
        if (encoded != null) {
            for (var match : this.byPattern.getOrDefault(encoded.getItem(), List.of())) {
                if (ItemStack.areItemStacksEqual(
                    encoded,
                    match.location()
                        .pattern()))
                    return match;
            }
        }
        // Without an encoded pattern to tell it by, by what it makes
        var outputs = pattern.getCondensedAEOutputs();
        var output = outputs.length > 0 ? outputs[0] : null;
        if (output == null) return null;
        var candidates = encoded == null ? this.all(this.cycles) : this.unencoded;
        for (var match : candidates) {
            if (match.location()
                .output()
                .isSameType(output)) return match;
        }
        return null;
    }

    public List<Cycle> cyclesWith(IAEStack<?> stack) {
        var found = new ObjectArrayList<Cycle>();
        for (var cycle : this.cycles) {
            for (var item : cycle.items()) {
                if (item.isSameType(stack)) {
                    found.add(cycle);
                    break;
                }
            }
        }
        return found;
    }

    /**
     * The cycle patterns making an item, or when none of its cycles' patterns does, all of them.
     */
    public List<Match> matchesFor(IAEStack<?> stack) {
        var all = all(cyclesWith(stack));
        var making = new ObjectArrayList<Match>();
        for (var match : all) {
            if (match.location()
                .output()
                .isSameType(stack)) making.add(match);
        }
        return making.isEmpty() ? all : making;
    }

    public boolean blocks(IAEStack<?> stack) {
        for (var cycle : cyclesWith(stack)) {
            if (this.blocking.contains(cycle)) return true;
        }
        return false;
    }

    public List<Match> all(Iterable<Cycle> cycles) {
        var matches = new ObjectArrayList<Match>();
        for (var cycle : cycles) {
            int number = this.cycles.indexOf(cycle) + 1;
            for (var location : cycle.patterns()) {
                matches.add(new Match(number, cycle, location, this.blocking.contains(cycle)));
            }
        }
        return matches;
    }
}
