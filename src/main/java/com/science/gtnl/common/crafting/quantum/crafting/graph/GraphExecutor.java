package com.science.gtnl.common.crafting.quantum.crafting.graph;

import com.science.gtnl.common.crafting.quantum.api.stacks.GenericStack;
import com.science.gtnl.common.crafting.quantum.crafting.CraftingCalculation;
import com.science.gtnl.common.crafting.quantum.crafting.inv.CraftingSimulationState;
import com.science.gtnl.common.crafting.quantum.util.LongMath;

import appeng.api.config.Actionable;

public class GraphExecutor {

    private final CraftingCalculation calc;
    private final CraftingGraph graph;

    public GraphExecutor(CraftingCalculation calc, CraftingGraph graph) {
        this.calc = calc;
        this.graph = graph;
    }

    public void applyGraph(CraftingSimulationState inv) throws InterruptedException {
        var order = graph.getTopologicalOrder();
        for (int nodeIndex = order.size() - 1; nodeIndex >= 0; nodeIndex--) {
            var node = order.get(nodeIndex);
            calc.handlePausing();
            if (node.isExternalLeaf()) {
                boolean root = node == graph.getRootNode();
                inv.addStackBytes(node.getWhat(), 1, node.getDemandAmount());
                long extracted = root ? 0 : inv.extract(node.getWhat(), node.getDemandAmount(), Actionable.MODULATE);
                long missing = node.getDemandAmount() - extracted;
                if (missing > 0) calc.addMissing(node.getWhat(), missing);
                continue;
            }
            if (node.getCraftTimes() == 0 && !hasCycleSeed(node)) continue;
            applyNode(node, inv);
        }
        var root = graph.getRootNode();
        if (root == null) {
            throw new IllegalStateException("Graph has no root node");
        }
        if (!root.isExternalLeaf() && root.getCraftTimes() >= 0) {
            // No parent edge owns the root native/emitter output stack.
            inv.addStackBytes(root.getWhat(), root.getDemandAmount(), 1);
        }
    }

    private void applyNode(CraftingGraphNode node, CraftingSimulationState inv) {
        long times = node.getCraftTimes();
        var pattern = node.getPattern();

        if (node.isEmitter()) {
            applyEmitter(node, inv, times, node == graph.getRootNode());
            return;
        }

        for (var edge : node.getInputs()) {
            long needed = edge.cycleCut() ? edge.seedDemand()
                : LongMath.saturatedMultiply(edge.amountPerCraft(), times);
            if (!edge.cycleCut()) {
                inv.addStackBytes(edge.inputKey(), edge.amountPerCraft(), times);
            }
            if (edge.cycleCut() && needed == 0) {
                continue;
            }
            long got = inv.extract(edge.inputKey(), needed, Actionable.MODULATE);
            long shortfall = needed - got;
            if (edge.cycleCut()) {
                inv.addStackBytes(edge.inputKey(), got, 1);
                edge.recordSeedResult(got, shortfall);
                calc.recordGraphRecursiveSeed(
                    node.getWhat(),
                    edge.inputKey(),
                    needed,
                    got,
                    shortfall,
                    edge.reserveDemand());
            } else if (shortfall > 0) {
                calc.addMissing(edge.inputKey(), shortfall);
            }
        }

        CraftingGraphEdge cycleCut = null;
        for (var edge : node.getInputs()) {
            if (edge.cycleCut()) {
                cycleCut = edge;
                break;
            }
        }
        for (GenericStack output : node.getOutputs()) {
            long outputAmount = LongMath.saturatedMultiply(output.amount(), times);
            if (cycleCut != null && output.what()
                .equals(node.getWhat())) {
                int componentId = graph.getTopology()
                    .getComponentId(node);
                var sccPlan = graph.getSccPlan(componentId);
                if (sccPlan != null) {
                    // Replace the planned seed portion with the amount actually allocated. Missing seeds must not
                    // become synthetic output; the remaining planned amount is the real net gain of crafted batches.
                    long planned = sccPlan.plannedOutput(node);
                    long netGain = Math.max(0, planned - cycleCut.seedRequired());
                    outputAmount = LongMath.saturatedAdd(cycleCut.seedAllocated(), netGain);
                }
            }
            inv.insert(output.what(), outputAmount, Actionable.MODULATE);
        }

        inv.addCrafting(pattern, times);
        inv.addBytes(times);
    }

    private static boolean hasCycleSeed(CraftingGraphNode node) {
        for (var edge : node.getInputs()) {
            if (edge.cycleCut() && edge.seedDemand() > 0) return true;
        }
        return false;
    }

    private void applyEmitter(CraftingGraphNode node, CraftingSimulationState inv, long amount, boolean root) {
        long extracted = root ? 0 : inv.extract(node.getWhat(), amount, Actionable.MODULATE);
        long emitted = amount - extracted;
        if (emitted > 0) {
            inv.emitItems(node.getWhat(), emitted);
        }
        // Downstream graph consumers still extract their input from the simulation inventory. Reinsert the fulfilled
        // amount for them; at the root, the extraction/emission itself is the final fulfillment.
        if (!node.getParents()
            .isEmpty()) {
            inv.insert(node.getWhat(), amount, Actionable.MODULATE);
        }
    }
}
