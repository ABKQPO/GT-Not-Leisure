package com.science.gtnl.common.wireless;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public class PhysicalClusterTracker<N, F> {

    public interface Topology<N> {

        boolean isLive(N node);

        Iterable<N> neighbours(N node);

        boolean blocksWireless(N node);
    }

    public record Cluster<N, F> (Set<N> nodes, Set<F> frequencies, boolean blocked, boolean limited) {

        public boolean conflicted() {
            return frequencies.size() > 1;
        }
    }

    public static class ScanLimitException extends IllegalStateException {

        public ScanLimitException() {
            super("Physical cluster scan limit exceeded; this cluster's entrances paused without deleting bindings.");
        }
    }

    private final int maxNodes;
    private Map<N, Set<F>> claims = new Reference2ObjectOpenHashMap<>();
    private Map<N, Cluster<N, F>> byNode = new Reference2ObjectOpenHashMap<>();
    private List<Cluster<N, F>> clusters = List.of();

    public PhysicalClusterTracker(int maxNodes) {
        this.maxNodes = maxNodes;
    }

    public List<Cluster<N, F>> clusters() {
        return clusters;
    }

    public Set<N> nodes() {
        return Collections.unmodifiableSet(byNode.keySet());
    }

    public Cluster<N, F> clusterOf(N node) {
        return byNode.get(node);
    }

    public Set<F> claimsOf(N node) {
        return claims.getOrDefault(node, Set.of());
    }

    public void refresh(Topology<N> topology, Collection<N> extraSeeds, Function<N, Set<F>> savedClaims,
        UnaryOperator<F> frequencyIdentity) {
        Map<N, Scan> owners = new Reference2ObjectOpenHashMap<>();
        List<N> seeds = new ObjectArrayList<>(claims.keySet());
        seeds.addAll(extraSeeds);
        for (N seed : seeds) {
            if (owners.containsKey(seed) || !topology.isLive(seed)) continue;
            Scan scan = new Scan();
            int discovered = 1;
            ObjectArrayFIFOQueue<N> pending = new ObjectArrayFIFOQueue<>();
            pending.enqueue(seed);
            owners.put(seed, scan);
            while (!pending.isEmpty() && !scan.root().limited) {
                N node = pending.dequeue();
                for (N neighbour : topology.neighbours(node)) {
                    if (!topology.isLive(neighbour)) continue;
                    Scan owner = owners.get(neighbour);
                    if (owner == null) {
                        owners.put(neighbour, scan);
                        pending.enqueue(neighbour);
                        if (++discovered > maxNodes) scan.root().limited = true;
                    } else if (owner.root() != scan.root()) {
                        // A complete component cannot meet a different scan in an undirected physical graph.
                        Scan root = scan.root();
                        root.parent = owner.root();
                        root.parent.limited = true;
                    }
                    if (scan.root().limited) break;
                }
            }
        }
        Map<Scan, Set<N>> components = new Reference2ObjectOpenHashMap<>();
        owners.forEach(
            (node, owner) -> components.computeIfAbsent(owner.root(), ignored -> identitySet())
                .add(node));
        List<Cluster<N, F>> scanned = new ObjectArrayList<>();
        components.forEach(
            (scan, members) -> scanned.add(
                new Cluster<>(
                    Set.copyOf(members),
                    Set.of(),
                    members.stream()
                        .anyMatch(topology::blocksWireless),
                    scan.limited)));
        rebuildClaims(scanned, savedClaims, frequencyIdentity);
    }

    void commitClaims(UnaryOperator<F> frequencyIdentity) {
        rebuildClaims(clusters, node -> Set.of(), frequencyIdentity);
    }

    private void rebuildClaims(List<Cluster<N, F>> scanned, Function<N, Set<F>> savedClaims,
        UnaryOperator<F> frequencyIdentity) {
        List<Cluster<N, F>> nextClusters = new ObjectArrayList<>();
        Map<N, Set<F>> nextClaims = new Reference2ObjectOpenHashMap<>();
        Map<N, Cluster<N, F>> nextByNode = new Reference2ObjectOpenHashMap<>();
        for (var component : scanned) {
            Set<N> members = component.nodes();
            boolean limited = component.limited();
            Map<N, Set<F>> provenance = new Reference2ObjectOpenHashMap<>();
            Set<F> frequencies = new ObjectOpenHashSet<>();
            boolean blocked = component.blocked();
            for (N node : members) {
                Set<F> labels = claims.containsKey(node) ? claims.get(node) : savedClaims.apply(node);
                provenance.put(node, labels);
                frequencies.addAll(labels);
            }
            Cluster<N, F> cluster = new Cluster<>(
                Set.copyOf(members),
                frequencies.stream()
                    .map(frequencyIdentity)
                    .collect(Collectors.toUnmodifiableSet()),
                blocked,
                limited);
            Set<F> sharedClaim = frequencies.size() == 1 && !blocked && !limited ? Set.copyOf(frequencies) : null;
            nextClusters.add(cluster);
            for (N node : members) {
                nextByNode.put(node, cluster);
                // Never spread competing frequencies across a conflict. Preserve each side's original provenance.
                Set<F> labels = sharedClaim != null ? sharedClaim : provenance.get(node);
                if ((labels == null || labels.isEmpty()) && !blocked
                    && !limited
                    && cluster.frequencies()
                        .size() == 1)
                    labels = cluster.frequencies();
                if (labels != null && !labels.isEmpty()) nextClaims.put(node, labels);
            }
        }
        claims = nextClaims;
        byNode = nextByNode;
        clusters = List.copyOf(nextClusters);
    }

    private static class Scan {

        private Scan parent = this;
        private boolean limited;

        private Scan root() {
            Scan root = this;
            while (root.parent != root) root = root.parent;
            Scan current = this;
            while (current.parent != current) {
                Scan next = current.parent;
                current.parent = root;
                current = next;
            }
            return root;
        }
    }

    public void link(Cluster<N, F> cluster, F frequency, UnaryOperator<F> frequencyIdentity) {
        if (cluster.limited() || cluster.blocked()
            || cluster.conflicted()
            || !cluster.frequencies()
                .isEmpty()
                && !cluster.frequencies()
                    .contains(frequencyIdentity.apply(frequency))) {
            throw new IllegalArgumentException("This physical cluster is blocked or belongs to another frequency.");
        }
        Set<F> sharedClaim = Set.of(frequency);
        for (N node : cluster.nodes()) claims.put(node, sharedClaim);
    }

    public void unlink(Cluster<N, F> cluster, F frequency, UnaryOperator<F> frequencyIdentity) {
        F identity = frequencyIdentity.apply(frequency);
        for (N node : cluster.nodes()) {
            Set<F> previous = claims.get(node);
            if (previous == null) continue;
            Set<F> remaining = new ObjectOpenHashSet<>(previous);
            remaining.removeIf(
                label -> frequencyIdentity.apply(label)
                    .equals(identity));
            if (remaining.isEmpty()) claims.remove(node);
            else claims.put(node, Set.copyOf(remaining));
        }
    }

    public boolean forget(Predicate<N> removed) {
        return claims.keySet()
            .removeIf(removed);
    }

    public void clear() {
        claims.clear();
        byNode.clear();
        clusters = List.of();
    }

    private static <N> Set<N> identitySet() {
        return new ReferenceOpenHashSet<>();
    }
}
