package com.science.gtnl.common.wireless;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public class EntranceCandidates {

    private static final int NEED_RADIUS = 2;
    private static final int CAPTURE_RADIUS = 6;

    public static <N> N select(Set<N> members, Collection<N> missing, Collection<N> occupied,
        Function<N, Iterable<N>> neighbours, Predicate<N> eligible, ToIntFunction<N> rank, ToIntFunction<N> capacity,
        Comparator<N> order) {
        return new Batch<>(members, occupied, neighbours).select(missing, eligible, rank, capacity, order);
    }

    public static class Batch<N> {

        private final Set<N> members;
        private final Function<N, Iterable<N>> neighbours;
        private final Reference2IntOpenHashMap<N> distanceFromEntrance;
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
                missingSet = new ReferenceOpenHashSet<>();
                missingSet.addAll(missing);
            }
            List<N> needs = new ObjectArrayList<>(missing);
            needs.sort(
                Comparator.<N>comparingInt(node -> distanceFromEntrance.getOrDefault(node, Integer.MAX_VALUE))
                    .reversed()
                    .thenComparing(order));
            for (N need : needs) {
                Reference2IntOpenHashMap<N> local = distances(members, List.of(need), neighbours, NEED_RADIUS);
                Reference2IntOpenHashMap<N> captured = new Reference2IntOpenHashMap<>();
                N best = local.keySet()
                    .stream()
                    .filter(eligible)
                    .min(
                        Comparator.<N>comparingInt(
                            node -> -captured.computeIfAbsent(node, (N key) -> captured(key, capacity.applyAsInt(key))))
                            .thenComparingInt(rank)
                            .thenComparingInt(local::getInt)
                            .thenComparing(order))
                    .orElse(null);
                if (best != null) return best;
            }
            return null;
        }

        private int captured(N candidate, int limit) {
            Reference2IntOpenHashMap<N> seen = new Reference2IntOpenHashMap<>();
            ObjectArrayFIFOQueue<N> queue = new ObjectArrayFIFOQueue<>();
            seen.put(candidate, 0);
            queue.enqueue(candidate);
            int count = 0;
            while (!queue.isEmpty()) {
                N node = queue.dequeue();
                int distance = seen.getInt(node);
                if (distance >= distanceFromEntrance.getOrDefault(node, Integer.MAX_VALUE)) continue;
                if (missingSet.contains(node) && ++count >= limit) return count;
                if (distance >= CAPTURE_RADIUS) continue;
                for (N other : neighbours.apply(node)) {
                    if (members.contains(other) && !seen.containsKey(other)) {
                        seen.put(other, distance + 1);
                        queue.enqueue(other);
                    }
                }
            }
            return count;
        }

        public void occupy(N entrance) {
            if (!members.contains(entrance) || distanceFromEntrance.getOrDefault(entrance, Integer.MAX_VALUE) == 0)
                return;
            distanceFromEntrance.put(entrance, 0);
            ObjectArrayFIFOQueue<N> queue = new ObjectArrayFIFOQueue<>();
            queue.enqueue(entrance);
            while (!queue.isEmpty()) {
                N node = queue.dequeue();
                int next = distanceFromEntrance.getInt(node) + 1;
                for (N other : neighbours.apply(node)) {
                    if (!members.contains(other) || distanceFromEntrance.getOrDefault(other, Integer.MAX_VALUE) <= next)
                        continue;
                    distanceFromEntrance.put(other, next);
                    queue.enqueue(other);
                }
            }
        }
    }

    private static <N> Reference2IntOpenHashMap<N> distances(Set<N> members, Collection<N> seeds,
        Function<N, Iterable<N>> neighbours, int maxDistance) {
        Reference2IntOpenHashMap<N> result = new Reference2IntOpenHashMap<>();
        ObjectArrayFIFOQueue<N> queue = new ObjectArrayFIFOQueue<>();
        for (N seed : seeds) {
            if (members.contains(seed) && !result.containsKey(seed)) {
                result.put(seed, 0);
                queue.enqueue(seed);
            }
        }
        while (!queue.isEmpty()) {
            N node = queue.dequeue();
            int distance = result.getInt(node);
            if (distance >= maxDistance) continue;
            for (N other : neighbours.apply(node)) {
                if (members.contains(other) && !result.containsKey(other)) {
                    result.put(other, distance + 1);
                    queue.enqueue(other);
                }
            }
        }
        return result;
    }
}
