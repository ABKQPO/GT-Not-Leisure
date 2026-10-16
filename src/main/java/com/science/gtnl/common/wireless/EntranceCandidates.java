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

public class EntranceCandidates {

    private EntranceCandidates() {}

    public static <N> N select(Set<N> members, Collection<N> missing, Collection<N> occupied,
        Function<N, Iterable<N>> neighbours, Predicate<N> eligible, ToIntFunction<N> rank, Comparator<N> order) {
        return new Batch<>(members, occupied, neighbours).select(missing, eligible, rank, order);
    }

    public static final class Batch<N> {

        private final Set<N> members;
        private final Function<N, Iterable<N>> neighbours;
        private final Map<N, Integer> distanceFromEntrance;

        public Batch(Set<N> members, Collection<N> occupied, Function<N, Iterable<N>> neighbours) {
            this.members = members;
            this.neighbours = neighbours;
            distanceFromEntrance = distances(members, occupied, neighbours, Integer.MAX_VALUE);
        }

        public N select(Collection<N> missing, Predicate<N> eligible, ToIntFunction<N> rank, Comparator<N> order) {
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

        public void occupy(N entrance) {
            if (!members.contains(entrance) || distanceFromEntrance.getOrDefault(entrance, Integer.MAX_VALUE) == 0)
                return;
            distanceFromEntrance.put(entrance, 0);
            ArrayDeque<N> queue = new ArrayDeque<>();
            queue.add(entrance);
            while (!queue.isEmpty()) {
                N node = queue.removeFirst();
                int next = distanceFromEntrance.get(node) + 1;
                for (N other : neighbours.apply(node)) {
                    if (!members.contains(other) || distanceFromEntrance.getOrDefault(other, Integer.MAX_VALUE) <= next)
                        continue;
                    distanceFromEntrance.put(other, next);
                    queue.addLast(other);
                }
            }
        }
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
