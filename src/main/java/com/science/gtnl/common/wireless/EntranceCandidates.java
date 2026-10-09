package com.science.gtnl.common.wireless;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/** Bounded local search near unserved consumers, starting with the part furthest from existing entrances. */
public final class EntranceCandidates {

    private EntranceCandidates() {}

    public static <N> N select(Set<N> members, Collection<N> missing, Collection<N> occupied,
        Function<N, Iterable<N>> neighbours, Predicate<N> eligible, ToIntFunction<N> rank, Comparator<N> order) {
        Map<N, Integer> distanceFromEntrance = distances(members, occupied, neighbours, Integer.MAX_VALUE);
        List<N> needs = new ArrayList<>(missing);
        needs.sort(
            Comparator.<N>comparingInt(node -> distanceFromEntrance.getOrDefault(node, Integer.MAX_VALUE))
                .reversed()
                .thenComparing(order));
        for (N need : needs) {
            Map<N, Integer> local = distances(members, List.of(need), neighbours, 2);
            N best = local.keySet()
                .stream()
                .filter(eligible)
                .min(
                    Comparator.comparingInt(rank)
                        .thenComparingInt(local::get)
                        .thenComparing(order))
                .orElse(null);
            if (best != null) return best;
        }
        return null;
    }

    private static <N> Map<N, Integer> distances(Set<N> members, Collection<N> seeds,
        Function<N, Iterable<N>> neighbours, int maxDistance) {
        Map<N, Integer> result = new IdentityHashMap<>();
        ArrayDeque<N> queue = new ArrayDeque<>();
        for (N seed : seeds) {
            if (members.contains(seed) && !result.containsKey(seed)) {
                result.put(seed, 0);
                queue.add(seed);
            }
        }
        while (!queue.isEmpty()) {
            N node = queue.removeFirst();
            int distance = result.get(node);
            if (distance >= maxDistance) continue;
            for (N other : neighbours.apply(node)) {
                if (members.contains(other) && !result.containsKey(other)) {
                    result.put(other, distance + 1);
                    queue.addLast(other);
                }
            }
        }
        return result;
    }
}
