package com.science.gtnl.common.crafting.quantum.crafting.graph;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.crafting.quantum.crafting.CraftingCalculation;
import com.science.gtnl.common.crafting.quantum.crafting.inv.CraftingSimulationState;
import com.science.gtnl.common.crafting.quantum.util.LongMath;

import appeng.api.config.Actionable;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2LongOpenHashMap;

public class DemandPropagation {

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

            for (var edge : node.getInputs()) {
                if (!edge.cycleCut() && edge.producer() != null) {
                    edge.producer()
                        .addDemandAmount(LongMath.saturatedMultiply(times, edge.amountPerCraft()));
                }
            }
        }
    }

    private void solveCyclicComponent(CraftingGraph graph, CraftingGraphTopology topology,
        CraftingGraphTopology.Component component, @Nullable CraftingSimulationState inventory) {
        // Multi-pattern components cannot be solved from graph edge weights alone: expanded legacy closure can select
        // patterns and account for remainders that are intentionally absent from the graph. Keep that transaction
        // local.
        if (component.nodes()
            .size() != 1) {
            markLocalComponent(graph, component);
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
