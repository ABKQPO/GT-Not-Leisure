package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.science.gtnl.common.wireless.ChannelBudget.Position;
import com.science.gtnl.common.wireless.PhysicalClusterTracker.Cluster;

/** Stateful topology scenarios: these deliberately outlive the originally clicked entrance node. */
public final class PhysicalClusterTrackerTest {

    private static final class Node {

        final List<Node> neighbours = new ArrayList<>();
        boolean live = true;
        boolean controller;
    }

    private static final class Graph implements PhysicalClusterTracker.Topology<Node> {

        @Override
        public boolean isLive(Node node) {
            return node.live;
        }

        @Override
        public Iterable<Node> neighbours(Node node) {
            return node.neighbours;
        }

        @Override
        public boolean blocksWireless(Node node) {
            return node.controller;
        }

        void join(Node a, Node b) {
            a.neighbours.add(b);
            b.neighbours.add(a);
        }

        void cut(Node a, Node b) {
            a.neighbours.remove(b);
            b.neighbours.remove(a);
        }
    }

    public static void main(String[] args) {
        splitAndGrowth();
        sameFrequencyMerge();
        controllerAliases();
        conflictingMergeAndRecovery();
        removeOnlyOneFrequency();
        controllerMergeDoesNotClaimBase();
        unloadAndReplacement();
        scanLimitIsAtomic();
        physicalEdgeClassification();
        System.out
            .println("PhysicalClusterTrackerTest: split, merge, conflict, recovery and scan-boundary checks passed.");
    }

    private static void controllerAliases() {
        Graph graph = new Graph();
        var tracker = new PhysicalClusterTracker<Node, String>(100);
        Node a = new Node(), b = new Node(), c = new Node();
        graph.join(a, b);
        graph.join(b, c);
        java.util.function.UnaryOperator<String> joinedControllers = source -> source.equals("controller-b")
            ? "controller-a"
            : source;
        tracker.refresh(
            graph,
            List.of(a),
            node -> node == a ? Set.of("controller-a") : node == c ? Set.of("controller-b") : Set.of(),
            joinedControllers);
        check(
            !tracker.clusterOf(a)
                .conflicted(),
            "Old cards bound to different blocks of one controller do not conflict");
        check(
            tracker.clusterOf(a)
                .frequencies()
                .equals(Set.of("controller-a")),
            "One multiblock has one runtime frequency");
        check(
            tracker.claimsOf(c)
                .equals(Set.of("controller-b")),
            "Original saved controller address survives resolution");
        check(
            tracker.claimsOf(b)
                .equals(Set.of("controller-a")),
            "New members inherit the resolved frequency");

        // Reload from the unchanged per-node provenance, then detach the controller blocks.
        var saved = new java.util.IdentityHashMap<Node, Set<String>>();
        for (Node node : List.of(a, b, c)) saved.put(node, tracker.claimsOf(node));
        tracker.clear();
        tracker.refresh(graph, List.of(a), node -> saved.getOrDefault(node, Set.of()), joinedControllers);
        check(
            !tracker.clusterOf(a)
                .conflicted(),
            "Reloading old aliases still produces one frequency");
        tracker.refresh(graph, List.of());
        check(
            tracker.clusterOf(a)
                .conflicted(),
            "Separating source controllers restores genuinely different identities");
        graph.cut(b, c);
        tracker.refresh(graph, List.of());
        check(
            tracker.clusterOf(c)
                .frequencies()
                .equals(Set.of("controller-b")),
            "Split targets retain original source provenance");

        graph.join(b, c);
        tracker.refresh(graph, List.of(), node -> Set.of(), joinedControllers);
        tracker.unlink(tracker.clusterOf(a), "controller-a", joinedControllers);
        tracker.refresh(graph, List.of(a), node -> Set.of(), joinedControllers);
        check(
            tracker.clusterOf(a)
                .frequencies()
                .isEmpty(),
            "Disconnect removes every alias in the physical cluster");

        tracker.clear();
        tracker.refresh(
            graph,
            List.of(a),
            node -> node == a ? Set.of("controller-a") : node == c ? Set.of("other-controller") : Set.of(),
            joinedControllers);
        check(
            tracker.clusterOf(a)
                .conflicted(),
            "Unrelated controller groups still conflict");
        tracker.unlink(tracker.clusterOf(a), "controller-a", joinedControllers);
        tracker.refresh(graph, List.of(a), node -> Set.of(), joinedControllers);
        check(
            tracker.clusterOf(c)
                .frequencies()
                .equals(Set.of("other-controller")),
            "Unlink does not erase the other group");
    }

    private static void splitAndGrowth() {
        Graph graph = new Graph();
        var tracker = new PhysicalClusterTracker<Node, String>(100);
        Node a = new Node(), b = new Node(), c = new Node(), d = new Node();
        graph.join(a, b);
        graph.join(b, c);
        bind(tracker, graph, b, "red");
        graph.join(c, d);
        tracker.refresh(graph, List.of());
        check(
            tracker.clusterOf(d)
                .frequencies()
                .equals(Set.of("red")),
            "New devices inherit the only frequency");
        b.live = false; // The only original entrance disappears with the middle cable.
        tracker.refresh(graph, List.of());
        check(
            tracker.clusters()
                .size() == 2,
            "Removing B splits A-B-C into two managed components");
        check(
            tracker.clusterOf(a)
                .frequencies()
                .equals(Set.of("red")),
            "A retains the frequency without the anchor");
        check(
            tracker.clusterOf(d)
                .frequencies()
                .equals(Set.of("red")),
            "The other child, including new devices, inherits");
        tracker.unlink(tracker.clusterOf(d), "red");
        tracker.refresh(graph, List.of(d));
        check(
            tracker.clusterOf(c)
                .frequencies()
                .isEmpty(),
            "Clicking D disconnects the whole C-D cluster");
        check(
            tracker.clusterOf(a)
                .frequencies()
                .equals(Set.of("red")),
            "Disconnect does not leak over former wireless connectivity");
    }

    private static void sameFrequencyMerge() {
        Graph graph = new Graph();
        var tracker = new PhysicalClusterTracker<Node, String>(100);
        Node a = new Node(), b = new Node();
        bind(tracker, graph, a, "red");
        bind(tracker, graph, b, "red");
        graph.join(a, b);
        tracker.refresh(graph, List.of());
        check(
            tracker.clusters()
                .size() == 1
                && !tracker.clusterOf(a)
                    .conflicted(),
            "Same-frequency links collapse to one record");
        graph.cut(a, b);
        tracker.refresh(graph, List.of());
        check(
            tracker.clusters()
                .size() == 2,
            "Both sides survive a later split of a merged same-frequency record");
    }

    private static void conflictingMergeAndRecovery() {
        Graph graph = new Graph();
        var tracker = new PhysicalClusterTracker<Node, String>(100);
        Node a = new Node(), b = new Node(), bridge = new Node();
        bind(tracker, graph, a, "red");
        bind(tracker, graph, b, "blue");
        graph.join(a, bridge);
        graph.join(bridge, b);
        for (int tick = 0; tick < 20; tick++) tracker.refresh(graph, List.of());
        check(
            tracker.clusters()
                .size() == 1 && tracker.clusterOf(a)
                    .conflicted(),
            "Different frequencies suspend the merged cluster");
        check(
            tracker.clusterOf(b)
                .frequencies()
                .equals(Set.of("red", "blue")),
            "Neither conflicting record is deleted");
        graph.cut(bridge, b);
        tracker.refresh(graph, List.of());
        check(
            tracker.clusterOf(a)
                .frequencies()
                .equals(Set.of("red")),
            "Conflict did not spread blue onto A");
        check(
            tracker.clusterOf(b)
                .frequencies()
                .equals(Set.of("blue")),
            "Conflict did not spread red onto B");
        check(
            tracker.clusterOf(bridge)
                .frequencies()
                .equals(Set.of("red")),
            "Neutral conflict-time additions inherit after resolution");
    }

    private static void removeOnlyOneFrequency() {
        Graph graph = new Graph();
        var tracker = new PhysicalClusterTracker<Node, String>(100);
        Node a = new Node(), b = new Node(), unrelated = new Node();
        bind(tracker, graph, a, "red");
        bind(tracker, graph, b, "blue");
        bind(tracker, graph, unrelated, "red");
        graph.join(a, b);
        tracker.refresh(graph, List.of());
        // A red card used on B must still find and remove red from this entire physical cluster.
        tracker.unlink(tracker.clusterOf(b), "red");
        tracker.refresh(graph, List.of());
        check(
            tracker.clusterOf(a)
                .frequencies()
                .equals(Set.of("blue")),
            "Remaining frequency recovers across the whole merged cluster");
        check(
            tracker.clusterOf(unrelated)
                .frequencies()
                .equals(Set.of("red")),
            "Same source's other remote cluster stays linked");
        graph.cut(a, b);
        tracker.refresh(graph, List.of());
        check(
            tracker.clusterOf(a)
                .frequencies()
                .equals(Set.of("blue")),
            "Removed frequency does not resurrect on a later split");
    }

    private static void controllerMergeDoesNotClaimBase() {
        Graph graph = new Graph();
        var tracker = new PhysicalClusterTracker<Node, String>(100);
        Node remote = new Node(), controller = new Node(), base = new Node();
        controller.controller = true;
        graph.join(controller, base);
        bind(tracker, graph, remote, "red");
        graph.join(remote, base);
        tracker.refresh(graph, List.of());
        check(
            tracker.clusterOf(remote)
                .blocked(),
            "Physical controller membership suspends wireless entrances");
        graph.cut(remote, base);
        tracker.refresh(graph, List.of(base));
        check(
            !tracker.clusterOf(remote)
                .blocked(),
            "Removing the physical controller connection allows recovery");
        check(
            tracker.clusterOf(base)
                .frequencies()
                .isEmpty(),
            "Blocked period did not spread claims into the wired base");
        boolean rejected = false;
        try {
            tracker.link(tracker.clusterOf(base), "blue");
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        check(rejected, "A controller cluster cannot acquire a wireless claim");
    }

    private static void unloadAndReplacement() {
        Graph graph = new Graph();
        var tracker = new PhysicalClusterTracker<Node, String>(100);
        Node a = new Node(), b = new Node();
        graph.join(a, b);
        bind(tracker, graph, a, "red");
        tracker.forget(node -> node == a);
        a.live = false;
        tracker.refresh(graph, List.of());
        check(
            tracker.clusterOf(b)
                .frequencies()
                .equals(Set.of("red")),
            "Loaded child retains its claim when another chunk unloads");
        b.live = false;
        tracker.refresh(graph, List.of());
        Node replacementAtSamePosition = new Node();
        tracker.refresh(graph, List.of(replacementAtSamePosition));
        check(
            tracker.clusterOf(replacementAtSamePosition)
                .frequencies()
                .isEmpty(),
            "A replaced node does not inherit merely by coordinate reuse");
        tracker.clear();
        check(
            tracker.clusters()
                .isEmpty(),
            "Server stop clears all runtime records");
    }

    private static void scanLimitIsAtomic() {
        Graph graph = new Graph();
        var tracker = new PhysicalClusterTracker<Node, String>(2);
        Node a = new Node(), b = new Node(), c = new Node();
        graph.join(a, b);
        bind(tracker, graph, a, "red");
        Cluster<Node, String> previous = tracker.clusterOf(a);
        graph.join(b, c);
        boolean rejected = false;
        try {
            tracker.refresh(graph, List.of());
        } catch (PhysicalClusterTracker.ScanLimitException expected) {
            rejected = true;
        }
        check(rejected && tracker.clusterOf(a) == previous, "An oversized scan cannot partially rewrite claims");
        graph.cut(b, c);
        tracker.refresh(graph, List.of());
        check(
            tracker.clusterOf(a)
                .frequencies()
                .equals(Set.of("red")),
            "Reducing an oversized cluster recovers its original binding");
    }

    private static void physicalEdgeClassification() {
        Position a = new Position(0, 10, 64, 10);
        Position adjacent = new Position(0, 11, 64, 10);
        Position remote = new Position(0, 500, 64, 10);
        Position otherDimension = new Position(1, 10, 64, 10);
        check(PhysicalMeTopology.isPhysical(false, true, a, adjacent), "Native neighbouring cable edge is physical");
        check(PhysicalMeTopology.isPhysical(false, false, a, a), "Internal cable-to-part edge is physical");
        check(
            !PhysicalMeTopology.isPhysical(true, false, a, adjacent),
            "Owned wireless is excluded even when endpoints touch");
        check(
            !PhysicalMeTopology.isPhysical(false, false, a, adjacent),
            "A foreign UNKNOWN bridge is not a cable just because endpoints touch");
        check(
            !PhysicalMeTopology.isPhysical(false, false, a, remote),
            "Quantum and P2P remote edges are not physical clusters");
        check(
            !PhysicalMeTopology.isPhysical(false, true, a, otherDimension),
            "Coordinates across dimensions never merge physical clusters");
    }

    private static void bind(PhysicalClusterTracker<Node, String> tracker, Graph graph, Node target, String frequency) {
        tracker.refresh(graph, List.of(target));
        tracker.link(tracker.clusterOf(target), frequency);
        tracker.refresh(graph, List.of());
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
