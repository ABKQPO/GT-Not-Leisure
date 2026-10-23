package com.science.gtnl.common.wireless;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public class EntranceCandidates {

    private static final int NEED_RADIUS = 2;
    private static final int CAPTURE_RADIUS = 6;

    private EntranceCandidates() {}

    public static <N> N select(Set<N> members, Collection<N> missing, Collection<N> occupied,
        Function<N, Iterable<N>> neighbours, Predicate<N> eligible, ToIntFunction<N> rank, ToIntFunction<N> capacity,
        Comparator<N> order) {
        return new Batch<>(members, occupied, neighbours).select(missing, eligible, rank, capacity, order);
    }

    public static final class Batch<N> {

        private final Set<N> members;
        private final Function<N, Iterable<N>> neighbours;
        private final Map<N, Integer> distanceFromEntrance;
        private Collection<N> lastMissing;
        private Set<N> missingSet = Set.of();

        public Batch(Set<N> members, Collection<N> occupied, Function<N, Iterable<N>> neighbours) {
            this.members = members;
            this.neighbours = neighbours;
            distanceFromEntrance = distances(members, occupied, neighbours, Integer.MAX_VALUE);
        }

        public N select(Collection<N> missing, Predicate<N> eligible, ToIntFunction<N> rank, ToIntFunction<N> capacity,
            Comparator<N> order) {
            if (missing != lastMissing) {
                lastMissing = missing;
                missingSet = Collections.newSetFromMap(new IdentityHashMap<>());
                missingSet.addAll(missing);
            }
            List<N> needs = new ArrayList<>(missing);
            needs.sort(
                Comparator.<N>comparingInt(node -> distanceFromEntrance.getOrDefault(node, Integer.MAX_VALUE))
                    .reversed()
                    .thenComparing(order));
            for (N need : needs) {
                Map<N, Integer> local = distances(members, List.of(need), neighbours, NEED_RADIUS);
                Map<N, Integer> captured = new IdentityHashMap<>();
                N best = local.keySet()
                    .stream()
                    .filter(eligible)
                    .min(
                        Comparator
                            .<N>comparingInt(
                                node -> -captured.computeIfAbsent(node, key -> captured(key, capacity.applyAsInt(key))))
                            .thenComparingInt(rank)
                            .thenComparingInt(local::get)
                            .thenComparing(order))
                    .orElse(null);
                if (best != null) return best;
            }
            return null;
        }

        private int captured(N candidate, int limit) {
            Map<N, Integer> seen = new IdentityHashMap<>();
            ArrayDeque<N> queue = new ArrayDeque<>();
            seen.put(candidate, 0);
            queue.add(candidate);
            int count = 0;
            while (!queue.isEmpty()) {
                N node = queue.removeFirst();
                int distance = seen.get(node);
                if (distance >= distanceFromEntrance.getOrDefault(node, Integer.MAX_VALUE)) continue;
                if (missingSet.contains(node) && ++count >= limit) return count;
                if (distance >= CAPTURE_RADIUS) continue;
                for (N other : neighbours.apply(node)) {
                    if (members.contains(other) && !seen.containsKey(other)) {
                        seen.put(other, distance + 1);
                        queue.addLast(other);
                    }
                }
            }
            return count;
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
