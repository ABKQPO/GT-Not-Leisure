package com.science.gtnl.common.wireless;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/** Server-thread topology policy. Node identity, rather than a reusable block coordinate, owns a claim. */
public final class PhysicalClusterTracker<N, F> {

    public interface Topology<N> {

        boolean isLive(N node);

        Iterable<N> neighbours(N node);

        boolean blocksWireless(N node);
    }

    public record Cluster<N, F> (Set<N> nodes, Set<F> frequencies, boolean blocked) {

        public boolean conflicted() {
            return frequencies.size() > 1;
        }
    }

    public static final class ScanLimitException extends IllegalStateException {

        public ScanLimitException() {
            super("Physical cluster scan limit exceeded; wireless entrances paused without deleting bindings.");
        }
    }

    private final int maxNodes;
    private Map<N, Set<F>> claims = new IdentityHashMap<>();
    private Map<N, Cluster<N, F>> byNode = new IdentityHashMap<>();
    private List<Cluster<N, F>> clusters = List.of();

    public PhysicalClusterTracker(int maxNodes) {
        this.maxNodes = maxNodes;
    }

    public List<Cluster<N, F>> clusters() {
        return clusters;
    }

    public Cluster<N, F> clusterOf(N node) {
        return byNode.get(node);
    }

    public Set<F> claimsOf(N node) {
        return claims.getOrDefault(node, Set.of());
    }

    /** No claims or snapshots change until the whole bounded scan succeeds. */
    public void refresh(Topology<N> topology, Collection<N> extraSeeds) {
        refresh(topology, extraSeeds, node -> Set.of());
    }

    /** Saved provenance is imported before merging, including nodes first discovered through a physical edge. */
    public void refresh(Topology<N> topology, Collection<N> extraSeeds, Function<N, Set<F>> savedClaims) {
        Set<N> seen = identitySet();
        List<N> seeds = new ArrayList<>(claims.keySet());
        seeds.addAll(extraSeeds);
        List<Cluster<N, F>> nextClusters = new ArrayList<>();
        Map<N, Set<F>> nextClaims = new IdentityHashMap<>();
        Map<N, Cluster<N, F>> nextByNode = new IdentityHashMap<>();
        for (N seed : seeds) {
            if (seen.contains(seed) || !topology.isLive(seed)) continue;
            Set<N> members = identitySet();
            Map<N, Set<F>> provenance = new IdentityHashMap<>();
            Set<F> frequencies = new HashSet<>();
            ArrayDeque<N> pending = new ArrayDeque<>();
            pending.add(seed);
            seen.add(seed);
            boolean blocked = false;
            while (!pending.isEmpty()) {
                if (seen.size() > maxNodes) throw new ScanLimitException();
                N node = pending.removeFirst();
                members.add(node);
                Set<F> labels = claims.containsKey(node) ? claims.get(node) : savedClaims.apply(node);
                provenance.put(node, labels);
                frequencies.addAll(labels);
                blocked |= topology.blocksWireless(node);
                for (N neighbour : topology.neighbours(node)) {
                    if (!seen.contains(neighbour) && topology.isLive(neighbour)) {
                        seen.add(neighbour);
                        pending.addLast(neighbour);
                    }
                }
            }
            Cluster<N, F> cluster = new Cluster<>(
                Collections.unmodifiableSet(members),
                Set.copyOf(frequencies),
                blocked);
            nextClusters.add(cluster);
            for (N node : members) {
                nextByNode.put(node, cluster);
                // Never spread competing frequencies across a conflict. Preserve each side's original provenance.
                Set<F> labels = frequencies.size() == 1 && !blocked ? cluster.frequencies() : provenance.get(node);
                if (labels != null && !labels.isEmpty()) nextClaims.put(node, labels);
            }
        }
        claims = nextClaims;
        byNode = nextByNode;
        clusters = List.copyOf(nextClusters);
    }

    public void link(Cluster<N, F> cluster, F frequency) {
        if (cluster.blocked() || cluster.conflicted()
            || !cluster.frequencies()
                .isEmpty()
                && !cluster.frequencies()
                    .contains(frequency)) {
            throw new IllegalArgumentException("This physical cluster is blocked or belongs to another frequency.");
        }
        for (N node : cluster.nodes()) claims.put(node, Set.of(frequency));
    }

    public void unlink(Cluster<N, F> cluster, F frequency) {
        for (N node : cluster.nodes()) {
            Set<F> previous = claims.get(node);
            if (previous == null || !previous.contains(frequency)) continue;
            Set<F> remaining = new HashSet<>(previous);
            remaining.remove(frequency);
            if (remaining.isEmpty()) claims.remove(node);
            else claims.put(node, Set.copyOf(remaining));
        }
    }

    public void forget(Predicate<N> removed) {
        claims.keySet()
            .removeIf(removed);
    }

    public void clear() {
        claims.clear();
        byNode.clear();
        clusters = List.of();
    }

    private static <N> Set<N> identitySet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }
}
