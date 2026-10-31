package com.science.gtnl.common.crafting.quantum.crafting.graph;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntHeapPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

/**
 * Cached dependency order for a crafting graph. For an acyclic graph, only the node order is retained. Strongly
 * connected components and their condensation DAG are allocated only after Kahn's algorithm detects a cycle.
 */
public class CraftingGraphTopology {

    private static final Comparator<CraftingGraphNode> NODE_ORDER = Comparator
        .comparingInt(CraftingGraphNode::getOrdinal);

    private final List<CraftingGraphNode> topologicalOrder;
    private final List<Component> components;
    private final Reference2IntMap<CraftingGraphNode> componentIds;
    private final int edgeCount;

    private CraftingGraphTopology(List<CraftingGraphNode> topologicalOrder, List<Component> components,
        Reference2IntMap<CraftingGraphNode> componentIds, int edgeCount) {
        // Both lists are built by this class and never mutated afterwards; wrap them instead of copying.
        this.topologicalOrder = Collections.unmodifiableList(topologicalOrder);
        this.components = Collections.unmodifiableList(components);
        this.componentIds = componentIds;
        this.edgeCount = edgeCount;
    }

    static CraftingGraphTopology analyze(CraftingGraph graph) {
        int edgeCount = 0;
        var nodes = graph.getAllNodes();
        for (int i = 0; i < nodes.size(); i++) {
            var edges = nodes.get(i)
                .getInputs();
            for (int j = 0; j < edges.size(); j++) edges.get(j)
                .assignOrdinal(edgeCount++);
        }
        var nodeOrder = kahnSortNodes(graph);
        if (nodeOrder.size() == graph.getNodeCount()) {
            return new CraftingGraphTopology(nodeOrder, List.of(), emptyComponentIds(), edgeCount);
        }

        return condense(graph, edgeCount);
    }

    private static CraftingGraphTopology condense(CraftingGraph graph, int edgeCount) {
        var tarjan = new Tarjan();
        for (var node : graph.getAllNodes()) {
            tarjan.visitIfNeeded(node);
        }

        int componentCount = tarjan.components.size();
        var dependencies = new IntArrayList[componentCount];
        var dependencySets = new IntOpenHashSet[componentCount];
        var inDegree = new int[componentCount];
        var componentOrdinals = new int[componentCount];
        for (int i = 0; i < componentCount; i++) {
            dependencies[i] = new IntArrayList();
            dependencySets[i] = new IntOpenHashSet();
            componentOrdinals[i] = tarjan.components.get(i)
                .get(0)
                .getOrdinal();
        }

        for (int componentId = 0; componentId < componentCount; componentId++) {
            for (var node : tarjan.components.get(componentId)) {
                for (var edge : node.getInputs()) {
                    var producer = edge.producer();
                    if (producer == null) {
                        continue;
                    }
                    int dependencyId = tarjan.componentIds.getInt(producer);
                    if (dependencyId != componentId && dependencySets[componentId].add(dependencyId)) {
                        dependencies[componentId].add(dependencyId);
                        inDegree[dependencyId]++;
                    }
                }
            }
        }

        IntComparator componentOrdinalOrder = (left, right) -> Integer
            .compare(componentOrdinals[left], componentOrdinals[right]);
        var queue = new IntHeapPriorityQueue(componentOrdinalOrder);
        for (int i = 0; i < componentCount; i++) {
            if (inDegree[i] == 0) {
                queue.enqueue(i);
            }
        }

        var orderedOldIds = new IntArrayList(componentCount);
        while (!queue.isEmpty()) {
            int componentId = queue.dequeueInt();
            orderedOldIds.add(componentId);
            for (int dependencyId : dependencies[componentId]) {
                if (--inDegree[dependencyId] == 0) {
                    queue.enqueue(dependencyId);
                }
            }
        }
        if (orderedOldIds.size() != componentCount) {
            throw new IllegalStateException("Condensation graph unexpectedly contains a cycle");
        }

        var componentIds = new Reference2IntOpenHashMap<CraftingGraphNode>();
        componentIds.defaultReturnValue(-1);
        var components = new ObjectArrayList<Component>(componentCount);
        var nodeOrder = new ObjectArrayList<CraftingGraphNode>(graph.getNodeCount());
        for (int newId = 0; newId < componentCount; newId++) {
            int oldId = orderedOldIds.getInt(newId);
            var nodes = tarjan.components.get(oldId);
            boolean cyclic = nodes.size() > 1 || hasSelfLoop(nodes.get(0));
            for (var node : nodes) {
                componentIds.put(node, newId);
                nodeOrder.add(node);
            }
            components.add(new Component(newId, nodes, cyclic));
        }

        return new CraftingGraphTopology(nodeOrder, components, componentIds, edgeCount);
    }

    private static List<CraftingGraphNode> kahnSortNodes(CraftingGraph graph) {
        var inDegree = new Reference2IntOpenHashMap<CraftingGraphNode>();
        inDegree.defaultReturnValue(0);
        var queue = new ObjectHeapPriorityQueue<>(NODE_ORDER);
        var result = new ObjectArrayList<CraftingGraphNode>(graph.getNodeCount());

        for (var node : graph.getAllNodes()) {
            int degree = node.getParents()
                .size();
            inDegree.put(node, degree);
            if (degree == 0) {
                queue.enqueue(node);
            }
        }

        while (!queue.isEmpty()) {
            var node = queue.dequeue();
            result.add(node);

            for (var edge : node.getInputs()) {
                var producer = edge.producer();
                if (producer == null) {
                    continue;
                }
                int previousDegree = inDegree.addTo(producer, -1);
                if (previousDegree == 1) {
                    queue.enqueue(producer);
                }
            }
        }

        return result;
    }

    /**
     * Returns parent-before-dependency order. Nodes in a recursive component are adjacent, in stable component-local
     * order. Callers that execute producers first can traverse this list in reverse.
     */
    public List<CraftingGraphNode> getTopologicalOrder() {
        return topologicalOrder;
    }

    /**
     * Returns whether Tarjan decomposition was necessary.
     */
    public boolean isCondensed() {
        return !components.isEmpty();
    }

    /**
     * Returns the condensation DAG in parent-before-dependency order. This is empty on the pure-DAG fast path.
     */
    public List<Component> getComponents() {
        return components;
    }

    /**
     * Returns the component id, or {@code -1} when this is the pure-DAG fast path or the node is not in this graph.
     */
    public int getComponentId(CraftingGraphNode node) {
        return componentIds.getInt(node);
    }

    public boolean isCyclic(CraftingGraphNode node) {
        int componentId = getComponentId(node);
        return componentId >= 0 && components.get(componentId)
            .cyclic();
    }

    int edgeCount() {
        return edgeCount;
    }

    private static boolean hasSelfLoop(CraftingGraphNode node) {
        for (var edge : node.getInputs()) {
            if (edge.producer() == node) {
                return true;
            }
        }
        return false;
    }

    private static Reference2IntMap<CraftingGraphNode> emptyComponentIds() {
        var result = new Reference2IntOpenHashMap<CraftingGraphNode>();
        result.defaultReturnValue(-1);
        return result;
    }

    public record Component(int id, List<CraftingGraphNode> nodes, boolean cyclic) {

        public Component {
            // Node lists are built by Tarjan and never mutated afterwards; wrap instead of copying.
            nodes = Collections.unmodifiableList(nodes);
        }
    }

    private static class Tarjan {

        private final Reference2IntMap<CraftingGraphNode> indexes = new Reference2IntOpenHashMap<>();
        private final Reference2IntMap<CraftingGraphNode> lowLinks = new Reference2IntOpenHashMap<>();
        private final Reference2IntMap<CraftingGraphNode> componentIds = new Reference2IntOpenHashMap<>();
        private final ReferenceOpenHashSet<CraftingGraphNode> onStack = new ReferenceOpenHashSet<>();
        private final ReferenceOpenHashSet<CraftingGraphNode> visited = new ReferenceOpenHashSet<>();
        private final ObjectArrayList<CraftingGraphNode> stack = new ObjectArrayList<>();
        private final ObjectArrayList<ObjectArrayList<CraftingGraphNode>> components = new ObjectArrayList<>();
        private final ObjectArrayList<Frame> depthFirstSearch = new ObjectArrayList<>();
        private int nextIndex;

        private Tarjan() {
            indexes.defaultReturnValue(-1);
            lowLinks.defaultReturnValue(-1);
            componentIds.defaultReturnValue(-1);
        }

        void visitIfNeeded(CraftingGraphNode node) {
            if (visited.add(node)) {
                visitIteratively(node);
            }
        }

        private void visitIteratively(CraftingGraphNode root) {
            push(root, null);
            while (!depthFirstSearch.isEmpty()) {
                var frame = depthFirstSearch.top();
                if (frame.nextEdge < frame.node.getInputs()
                    .size()) {
                    var producer = frame.node.getInputs()
                        .get(frame.nextEdge++)
                        .producer();
                    if (producer == null) {
                        continue;
                    }
                    if (visited.add(producer)) {
                        push(producer, frame.node);
                    } else {
                        if (onStack.contains(producer)) {
                            lowLinks.put(frame.node, Math.min(lowLinks.getInt(frame.node), indexes.getInt(producer)));
                        }
                    }
                    continue;
                }

                depthFirstSearch.pop();
                if (frame.parent != null) {
                    lowLinks.put(frame.parent, Math.min(lowLinks.getInt(frame.parent), lowLinks.getInt(frame.node)));
                }
                if (lowLinks.getInt(frame.node) == indexes.getInt(frame.node)) {
                    popComponent(frame.node);
                }
            }
        }

        private void push(CraftingGraphNode node, CraftingGraphNode parent) {
            int index = nextIndex++;
            indexes.put(node, index);
            lowLinks.put(node, index);
            onStack.add(node);
            stack.push(node);
            depthFirstSearch.push(new Frame(node, parent));
        }

        private void popComponent(CraftingGraphNode root) {
            var component = new ObjectArrayList<CraftingGraphNode>();
            CraftingGraphNode member;
            do {
                member = stack.pop();
                onStack.remove(member);
                componentIds.put(member, components.size());
                component.add(member);
            } while (member != root);
            component.sort(NODE_ORDER);
            components.add(component);
        }

        private static class Frame {

            private final CraftingGraphNode node;
            private final CraftingGraphNode parent;
            private int nextEdge;

            private Frame(CraftingGraphNode node, CraftingGraphNode parent) {
                this.node = node;
                this.parent = parent;
            }
        }
    }
}
