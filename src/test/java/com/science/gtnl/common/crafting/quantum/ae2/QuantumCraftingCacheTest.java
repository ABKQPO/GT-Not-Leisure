package com.science.gtnl.common.crafting.quantum.ae2;

import static com.gtnltest.TestSupport.check;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKeyType;
import com.science.gtnl.common.crafting.quantum.crafting.graph.CraftingGraph;
import com.science.gtnl.common.crafting.quantum.crafting.graph.CraftingGraphEdge;
import com.science.gtnl.common.crafting.quantum.crafting.graph.CraftingGraphNode;

@Tag("machine")
public final class QuantumCraftingCacheTest {

    @Test
    void preservesCacheOrderingAndTraversal() throws Exception {
        var constructor = QuantumCraftingCache.Revision.class
            .getDeclaredConstructor(long.class, ImmutableMap.class, ImmutableSet.class, StackKeyFactory.class);
        constructor.setAccessible(true);
        var revision = constructor.newInstance(7L, ImmutableMap.of(), ImmutableSet.of(), new StackKeyFactory());
        CraftingGraph first = graph(7);
        revision.putGraph(new Key(0), first);
        for (int i = 1; i < 64; i++) revision.putGraph(new Key(i), graph(7));
        check(revision.peekGraph(new Key(0)) == first, "equal keys find and refresh the least recently used graph");
        CraftingGraph replacement = graph(7);
        revision.putGraph(new Key(1), replacement);
        revision.putGraph(new Key(64), graph(7));
        check(revision.peekGraph(new Key(2)) == null, "the oldest untouched graph is evicted at capacity");
        check(revision.peekGraph(new Key(0)) == first, "a cache hit prevents eviction");
        check(revision.peekGraph(new Key(1)) == replacement, "replacing a graph refreshes access order");
        revision.putGraph(new Key(65), graph(6));
        check(revision.peekGraph(new Key(65)) == null, "a stale revision cannot enter the graph cache");
        check(
            revision.takeGraph(new Key(0)) == first && revision.takeGraph(new Key(0)) == null,
            "taking a graph removes exactly that entry");
        topology();
    }

    private static CraftingGraph graph(long revision) {
        CraftingGraph graph = new CraftingGraph();
        graph.setCacheRevision(revision);
        return graph;
    }

    private static void topology() {
        CraftingGraph graph = new CraftingGraph();
        CraftingGraphNode dependency = node(graph, 0);
        CraftingGraphNode root = node(graph, 1);
        CraftingGraphNode second = node(graph, 2);
        connect(root, second);
        connect(root, dependency);
        check(
            graph.getTopologicalOrder()
                .equals(List.of(root, dependency, second)),
            "ready dependencies use creation order even when edges are inserted in the opposite order");
        check(
            !graph.getTopology()
                .isCondensed(),
            "an acyclic graph stays on the priority-queue path");

        CraftingGraph cycle = new CraftingGraph();
        CraftingGraphNode a = node(cycle, 0);
        CraftingGraphNode b = node(cycle, 1);
        connect(a, b);
        connect(b, a);
        check(
            cycle.getTopology()
                .isCondensed()
                && cycle.getTopologicalOrder()
                    .size() == 2,
            "an empty ready queue preserves the cyclic-graph fallback");
    }

    private static CraftingGraphNode node(CraftingGraph graph, int id) {
        Key key = new Key(id);
        CraftingGraphNode node = new CraftingGraphNode(key, null, List.of(), 1);
        graph.putNode(new CraftingGraph.NodeKey(key, null), node);
        return node;
    }

    private static void connect(CraftingGraphNode parent, CraftingGraphNode dependency) {
        parent.addInput(new CraftingGraphEdge(dependency.getWhat(), 1, dependency));
        dependency.addParent(parent);
    }

    private static final class Key extends AEKey {

        private final int id;

        private Key(int id) {
            this.id = id;
        }

        @Override
        public AEKeyType getType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getPrimaryKey() {
            return id;
        }

        @Override
        public Object toTag() {
            return id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Key key && id == key.id;
        }
    }
}
