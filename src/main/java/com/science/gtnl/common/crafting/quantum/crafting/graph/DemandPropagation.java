package com.science.gtnl.common.crafting.quantum.crafting.graph;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.crafting.quantum.crafting.CraftingCalculation;
import com.science.gtnl.common.crafting.quantum.crafting.inv.CraftingSimulationState;
import com.science.gtnl.common.crafting.quantum.util.LongMath;

import appeng.api.config.Actionable;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;

public class DemandPropagation {

    /**
     * Rounds of going around a ring before giving it to the legacy tree, see {@link #fillRingRuns}. Each round takes
     * the remaining shortfall times the inverse of the ring's gain, so only a ring gaining very little needs many.
     */
    private static final int MAX_RING_ROUNDS = 100_000;
    private static final int RING_ROUNDS_PER_PAUSE = 1024;

    private final CraftingCalculation calculation;

    public DemandPropagation(CraftingCalculation calculation) {
        this.calculation = calculation;
    }

    public void propagate(CraftingGraph graph, @Nullable CraftingSimulationState inventory)
        throws InterruptedException {
        var order = graph.getTopologicalOrder();
        var topology = graph.getTopology();
        var solvedComponents = new IntOpenHashSet();

        for (var node : order) {
            calculation.handlePausing();
            long times;
            if (topology.isCyclic(node)) {
                int componentId = topology.getComponentId(node);
                if (solvedComponents.add(componentId)) {
                    var component = topology.getComponents()
                        .get(componentId);
                    solveCyclicComponent(graph, topology, component, inventory);
                }
                times = node.getCraftTimes();
            } else {
                long demand = node.getDemandAmount();
                if (inventory != null && node != graph.getRootNode() && !node.isEmitter() && !node.isExternalLeaf()) {
                    long available = inventory.extract(node.getWhat(), demand, Actionable.MODULATE);
                    demand = Math.max(0, demand - available);
                }
                times = divideCeil(demand, node.getOutputPerCraft());
                node.setCraftTimes(times);
            }

            boolean cyclic = topology.isCyclic(node);
            for (var edge : node.getInputs()) {
                // A solved component already planned what its nodes take from each other
                if (!edge.cycleCut() && edge.producer() != null
                    && !(cyclic && topology.getComponentId(edge.producer()) == topology.getComponentId(node))) {
                    edge.producer()
                        .addDemandAmount(LongMath.saturatedMultiply(times, edge.amountPerCraft()));
                }
            }
        }
    }

    private void solveCyclicComponent(CraftingGraph graph, CraftingGraphTopology topology,
        CraftingGraphTopology.Component component, @Nullable CraftingSimulationState inventory)
        throws InterruptedException {
        // Multi-pattern components cannot be solved from graph edge weights alone: expanded legacy closure can select
        // patterns and account for remainders that are intentionally absent from the graph. Keep that transaction
        // local, unless the component is a simple ring of native patterns.
        if (component.nodes()
            .size() != 1) {
            var ring = ringEdges(topology, component);
            if (ring == null || !solveRing(graph, component, ring, inventory)) {
                markLocalComponent(graph, component);
            }
            return;
        }

        var node = component.nodes()
            .get(0);
        var pattern = node.getPattern();
        if (pattern == null || node.isLocalUnit()) {
            markLocalComponent(graph, component);
            return;
        }

        CraftingGraphEdge feedback = null;
        for (var edge : node.getInputs()) {
            if (edge.producer() != null && topology.getComponentId(edge.producer()) == component.id()) {
                if (feedback != null || !edge.inputKey()
                    .equals(node.getWhat())) {
                    markLocalComponent(graph, component);
                    return;
                }
                feedback = edge;
            }
        }
        if (feedback == null) {
            markLocalComponent(graph, component);
            return;
        }

        long demand = Math.max(0, node.getDemandAmount());
        if (inventory != null && node != graph.getRootNode()) {
            long available = inventory.extract(node.getWhat(), demand, Actionable.MODULATE);
            demand = Math.max(0, demand - available);
        }
        long directNet = LongMath.saturatedSubtract(node.getOutputPerCraft(), feedback.amountPerCraft());
        long expandedNet = calculation.getExpandedPatternNetOutput(pattern, node.getWhat());
        var batch = calculation.getRecursivePatternBatch(pattern, node.getWhat());

        if (demand == 0) {
            node.setCraftTimes(0);
            feedback.markCycleCut(0);
            feedback.setReserveDemand(Math.max(0, directNet));
            graph.setSccPlan(component.id(), nativePlan(node, 0));
            return;
        }

        // A closure without any positive net output can never grow the inventory by crafting: only the real seed
        // inventory can satisfy the demand. Serve it natively as a seed-only plan instead of falling back to a legacy
        // transaction; execution records the actually allocated/missing seed amounts.
        if (expandedNet <= 0 && !calculation.hasPositiveRecursiveNet(pattern)) {
            node.setCraftTimes(0);
            feedback.markCycleCut(demand);
            long reserve = Math.max(1, feedback.amountPerCraft());
            feedback.setReserveDemand(reserve);
            graph.setSccPlan(component.id(), nativePlan(node, 0));
            return;
        }

        if (expandedNet != directNet || batch.rootTimes() != 1 || batch.netOutput() != directNet) {
            // Zero/negative-net recursion and expanded closures that differ from the single-pattern self-loop cannot
            // be expressed by the native closed form. The component-local legacy transaction records any seed
            // shortfall explicitly instead of silently treating it as fulfilled.
            markLocalComponent(graph, component);
            return;
        }

        // From here on the direct net is positive: non-positive nets never pass the expanded/batch guard above,
        // because getRecursivePatternBatch falls back to a positive direct output for them.
        long seedCapacity = feedback.amountPerCraft();
        long seedRequired = Math.min(demand, seedCapacity);
        long batches = divideCeil(demand - seedRequired, directNet);
        long plannedOutput = batches == 0 ? 0
            : LongMath.saturatedAdd(seedRequired, LongMath.saturatedMultiply(batches, directNet));
        node.setCraftTimes(batches);
        feedback.markCycleCut(seedRequired);
        feedback.setReserveDemand(directNet);
        graph.setSccPlan(component.id(), nativePlan(node, plannedOutput));
    }

    /**
     * In a ring each node takes exactly one input made inside its component, so the component is a single cycle the
     * crafts go around, like 4 stone -> 5 bricks and 1 brick -> 2 stone. Native patterns make nothing but their own
     * output, so what goes around is fixed by the edges alone.
     *
     * @return the input of each node made inside the component, in component order, or null if the component is not a
     *         ring of native patterns that makes more than it takes going around once
     */
    public static CraftingGraphEdge @Nullable [] ringEdges(CraftingGraphTopology topology,
        CraftingGraphTopology.Component component) {
        var nodes = component.nodes();
        if (nodes.size() < 2) return null;
        var ring = new CraftingGraphEdge[nodes.size()];
        var made = BigInteger.ONE;
        var taken = BigInteger.ONE;
        for (int i = 0; i < nodes.size(); i++) {
            var node = nodes.get(i);
            if (node.getKind() != CraftingGraphNode.Kind.NATIVE_PATTERN || node.getOutputPerCraft() <= 0) {
                return null;
            }
            for (var edge : node.getInputs()) {
                var producer = edge.producer();
                if (producer == null || topology.getComponentId(producer) != component.id()) continue;
                if (ring[i] != null || edge.amountPerCraft() <= 0 || !edge.inputKey()
                    .equals(producer.getWhat())) {
                    return null;
                }
                ring[i] = edge;
            }
            if (ring[i] == null) return null;
            made = made.multiply(BigInteger.valueOf(node.getOutputPerCraft()));
            taken = taken.multiply(BigInteger.valueOf(ring[i].amountPerCraft()));
        }
        // A ring breaking even or losing has no net output; the legacy tree reports which cycle
        return made.compareTo(taken) > 0 ? ring : null;
    }

    /**
     * Plans how often each node of a ring crafts: enough that its crafts and stock cover the demand from outside the
     * ring, what the next node of the ring takes and the reserve kept of what was stocked. The requested item is never
     * taken from storage, only what the ring takes back of it.
     * <p/>
     * The executor applies the ring in reverse component order, see {@link GraphExecutor}, so a node taking from one
     * applied after it cannot take that node's output yet. Such an input is cut: it only takes stock, or the seed the
     * ring is started from when no stock covers a craft, and the producer later leaves out what it takes.
     *
     * @return false if the ring needs too many rounds or crafts to plan natively
     */
    private boolean solveRing(CraftingGraph graph, CraftingGraphTopology.Component component, CraftingGraphEdge[] ring,
        @Nullable CraftingSimulationState inventory) throws InterruptedException {
        var nodes = component.nodes();
        int count = nodes.size();
        var indexes = new Reference2IntOpenHashMap<CraftingGraphNode>(count);
        for (int i = 0; i < count; i++) indexes.put(nodes.get(i), i);
        // consumer[i] takes what node i makes
        int[] consumer = new int[count];
        for (int i = 0; i < count; i++) consumer[indexes.getInt(ring[i].producer())] = i;

        long reserveCrafts = this.calculation.getRecursiveIngredientReserveAmount();
        long[] demand = new long[count];
        long[] stock = new long[count];
        long[] reserve = new long[count];
        for (int i = 0; i < count; i++) {
            var node = nodes.get(i);
            demand[i] = Math.max(0, node.getDemandAmount());
            if (inventory != null) {
                // Nodes planned later are below the ring and cannot take its items, so the stock is not claimed
                stock[i] = inventory.extract(node.getWhat(), Long.MAX_VALUE, Actionable.SIMULATE);
                reserve[i] = Math
                    .min(stock[i], LongMath.saturatedMultiply(reserveCrafts, ring[consumer[i]].amountPerCraft()));
            }
        }

        var root = graph.getRootNode();
        long[] seed = new long[count];
        long[] runs = new long[count];
        if (!fillRingRuns(nodes, ring, consumer, root, demand, stock, seed, reserve, runs)) return false;
        if (hasRuns(runs) && !canStart(ring, consumer, stock)) {
            // Nothing stocked covers a craft: the ring starts from one craft of the first cut input, which is missing
            for (int i = 0; i < count; i++) {
                int producer = indexes.getInt(ring[i].producer());
                if (i > producer) {
                    seed[producer] = ring[i].amountPerCraft() - stock[producer];
                    break;
                }
            }
            if (!fillRingRuns(nodes, ring, consumer, root, demand, stock, seed, reserve, runs)) return false;
        }

        var plannedOutput = new Reference2LongOpenHashMap<CraftingGraphNode>(count);
        var takenByCycle = new Reference2LongOpenHashMap<CraftingGraphNode>(count);
        for (int i = 0; i < count; i++) {
            var node = nodes.get(i);
            node.setCraftTimes(runs[i]);
            plannedOutput.put(node, LongMath.saturatedMultiply(node.getOutputPerCraft(), runs[i]));
            int producer = indexes.getInt(ring[i].producer());
            if (i > producer) {
                long taken = LongMath.saturatedMultiply(ring[i].amountPerCraft(), runs[i]);
                long fromStorage = Math.min(LongMath.saturatedAdd(stock[producer], seed[producer]), taken);
                ring[i].markCycleCut(fromStorage);
                ring[i].setReserveDemand(0);
                takenByCycle.put(nodes.get(producer), taken - fromStorage);
            }
        }
        graph.setSccPlan(component.id(), SccPlan.ringPlan(plannedOutput, takenByCycle));
        return true;
    }

    /**
     * Raises the crafts of each node of the ring until they cover what it has to make. Starting from none, they only
     * grow, and settle on the fewest that do since the ring makes more than it takes going around.
     */
    private boolean fillRingRuns(List<CraftingGraphNode> nodes, CraftingGraphEdge[] ring, int[] consumer,
        @Nullable CraftingGraphNode root, long[] demand, long[] stock, long[] seed, long[] reserve, long[] runs)
        throws InterruptedException {
        Arrays.fill(runs, 0);
        for (int round = 0; round < MAX_RING_ROUNDS; round++) {
            if (round % RING_ROUNDS_PER_PAUSE == RING_ROUNDS_PER_PAUSE - 1) {
                this.calculation.handlePausing();
            }
            boolean changed = false;
            for (int i = 0; i < runs.length; i++) {
                long taken = LongMath.saturatedMultiply(ring[consumer[i]].amountPerCraft(), runs[consumer[i]]);
                long internal = LongMath.saturatedAdd(taken, reserve[i]);
                long supply = LongMath.saturatedAdd(stock[i], seed[i]);
                long needed = nodes.get(i) == root ? LongMath.saturatedAdd(demand[i], Math.max(0, internal - supply))
                    : Math.max(0, LongMath.saturatedAdd(demand[i], internal) - supply);
                if (needed == Long.MAX_VALUE) return false;
                long times = divideCeil(needed, nodes.get(i)
                    .getOutputPerCraft());
                if (times != runs[i]) {
                    runs[i] = times;
                    changed = true;
                }
            }
            if (!changed) return true;
        }
        return false;
    }

    private static boolean hasRuns(long[] runs) {
        for (long times : runs) {
            if (times > 0) return true;
        }
        return false;
    }

    // Whether the stock of an item made in the ring covers one craft of the node taking it
    private static boolean canStart(CraftingGraphEdge[] ring, int[] consumer, long[] stock) {
        for (int i = 0; i < stock.length; i++) {
            if (stock[i] >= ring[consumer[i]].amountPerCraft()) return true;
        }
        return false;
    }

    private static SccPlan nativePlan(CraftingGraphNode node, long plannedOutput) {
        var plannedOutputs = new Reference2LongOpenHashMap<CraftingGraphNode>();
        plannedOutputs.put(node, plannedOutput);
        return new SccPlan(plannedOutputs);
    }

    private static void markLocalComponent(CraftingGraph graph, CraftingGraphTopology.Component component) {
        graph.requireLegacyFallback();
        for (var node : component.nodes()) {
            node.setCraftTimes(0);
        }
    }

    private static long divideCeil(long dividend, long divisor) {
        if (dividend <= 0 || divisor <= 0) return 0;
        return 1 + (dividend - 1) / divisor;
    }
}
