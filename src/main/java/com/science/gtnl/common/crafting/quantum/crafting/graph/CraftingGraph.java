package com.science.gtnl.common.crafting.quantum.crafting.graph;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;

public class CraftingGraph {

    private final Object2ObjectMap<NodeKey, CraftingGraphNode> nodes = new Object2ObjectOpenHashMap<>();
    private final ObjectList<CraftingGraphNode> nodesInCreationOrder = new ObjectArrayList<>();
    private final List<CraftingGraphNode> allNodes = ObjectLists.unmodifiable(nodesInCreationOrder);
    private final ObjectOpenHashSet<AEKey> cyclicKeys = new ObjectOpenHashSet<>();
    private CraftingGraphNode rootNode;
    private CraftingGraphTopology topology;
    private final Int2ObjectMap<SccPlan> sccPlans = new Int2ObjectOpenHashMap<>();
    private Object2ObjectMap<AEKey, CraftingGraphNode> nodeByWhat;
    private boolean requiresLegacyFallback;
    private boolean hasRecursion;
    private boolean hasAlternatives;
    private long cacheRevision = Long.MIN_VALUE;
    private final ObjectOpenHashSet<AEKey> fuzzyKeys = new ObjectOpenHashSet<>();
    private ObjectSet<AEKey> snapshotKeys;
    private final ObjectOpenHashSet<AEKey> extraSnapshotKeys = new ObjectOpenHashSet<>();

    public record NodeKey(AEKey what, @Nullable IPatternDetails pattern) {

        @Override
        public int hashCode() {
            return what.hashCode() * 31 + Objects.hashCode(pattern);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof NodeKey(AEKey what1, IPatternDetails pattern1))) return false;
            return what.equals(what1) && Objects.equals(pattern, pattern1);
        }
    }

    @Nullable
    public CraftingGraphNode getNode(NodeKey key) {
        return nodes.get(key);
    }

    public void putNode(NodeKey key, CraftingGraphNode node) {
        if (nodes.containsKey(key)) {
            return;
        }
        node.assignOrdinal(nodesInCreationOrder.size());
        nodes.put(key, node);
        nodesInCreationOrder.add(node);
        topology = null;
        cyclicKeys.clear();
        nodeByWhat = null;
    }

    public List<CraftingGraphNode> getAllNodes() {
        return allNodes;
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        int count = 0;
        for (var node : nodesInCreationOrder) {
            count += node.getInputs()
                .size();
        }
        return count;
    }

    @Nullable
    public CraftingGraphNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(CraftingGraphNode rootNode) {
        this.rootNode = rootNode;
    }

    public boolean requiresLegacyFallback() {
        return this.requiresLegacyFallback;
    }

    public void requireLegacyFallback() {
        this.requiresLegacyFallback = true;
    }

    public long cacheRevision() {
        return this.cacheRevision;
    }

    public void setCacheRevision(long cacheRevision) {
        this.cacheRevision = cacheRevision;
    }

    /**
     * Records a key the simulation can consume even though it has no node of its own, such as material reached only
     * through fuzzy ingredient substitution.
     */
    public void addExtraSnapshotKey(AEKey key) {
        if (this.extraSnapshotKeys.add(key)) {
            this.snapshotKeys = null;
        }
    }

    /**
     * Marks a key whose fuzzy variants the crafting tree can consume: an input of an assembler pattern that allows
     * ingredient substitution. Every other key is only ever extracted exactly.
     */
    public void addFuzzyKey(AEKey key) {
        this.fuzzyKeys.add(key);
    }

    /**
     * @return the keys whose whole fuzzy group has to be part of an inventory snapshot
     */
    public ObjectSet<AEKey> getFuzzyKeys() {
        return ObjectSets.unmodifiable(this.fuzzyKeys);
    }

    /**
     * Marks that the structure contains a crafting cycle, whose material can also be supplied by net-positive loops.
     */
    public void markRecursion() {
        this.hasRecursion = true;
    }

    public boolean hasRecursion() {
        return this.hasRecursion;
    }

    public void markAlternatives() {
        this.hasAlternatives = true;
    }

    public boolean hasAlternatives() {
        return this.hasAlternatives;
    }

    public synchronized ObjectSet<AEKey> getSnapshotKeys() {
        if (this.snapshotKeys == null) {
            var keys = new ObjectOpenHashSet<AEKey>();
            for (var node : this.nodesInCreationOrder) {
                keys.add(node.getWhat());
                for (var edge : node.getInputs()) {
                    keys.add(edge.inputKey());
                }
            }
            keys.addAll(this.extraSnapshotKeys);
            this.snapshotKeys = ObjectSets.unmodifiable(keys);
        }
        return this.snapshotKeys;
    }

    /**
     * Lazily computes and caches the graph order. Tarjan decomposition is only performed if the graph is not a DAG.
     * The graph must be fully built before this method is first called.
     */
    public CraftingGraphTopology getTopology() {
        if (topology == null) {
            topology = CraftingGraphTopology.analyze(this);
        }
        return topology;
    }

    /**
     * Returns the cached parent-before-dependency order used by demand propagation and, in reverse, execution.
     */
    public List<CraftingGraphNode> getTopologicalOrder() {
        return getTopology().getTopologicalOrder();
    }

    public boolean isCyclicNode(NodeKey key) {
        if (topology == null || !topology.isCondensed()) {
            return false;
        }
        if (key.pattern() != null) {
            var node = nodes.get(key);
            return node != null && topology.isCyclic(node);
        }
        if (cyclicKeys.isEmpty()) {
            for (var node : nodesInCreationOrder) {
                if (topology.isCyclic(node)) {
                    cyclicKeys.add(node.getWhat());
                }
            }
        }
        return cyclicKeys.contains(key.what());
    }

    public void setSccPlan(int componentId, SccPlan plan) {
        sccPlans.put(componentId, plan);
    }

    @Nullable
    public SccPlan getSccPlan(int componentId) {
        return sccPlans.get(componentId);
    }

    @Nullable
    public CraftingGraphNode getNodeFor(AEKey what) {
        if (nodeByWhat == null) {
            nodeByWhat = new Object2ObjectOpenHashMap<>();
            for (var node : nodesInCreationOrder) {
                nodeByWhat.putIfAbsent(node.getWhat(), node);
            }
        }
        return nodeByWhat.get(what);
    }

    /**
     * Resets all per-attempt planning state so the graph structure can be reused by the next calculation. The topology
     * and creation order are structural and kept; every mutable demand/seed/component marker is cleared.
     */
    public void resetForReuse() {
        for (var node : nodesInCreationOrder) {
            node.resetPlanningAmounts();
        }
        sccPlans.clear();
    }
}
