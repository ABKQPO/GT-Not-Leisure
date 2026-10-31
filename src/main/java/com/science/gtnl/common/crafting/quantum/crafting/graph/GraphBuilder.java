package com.science.gtnl.common.crafting.quantum.crafting.graph;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnllib.utils.LongMath;
import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.GenericStack;
import com.science.gtnl.common.crafting.quantum.crafting.CraftingCalculation;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class GraphBuilder {

    private final CraftingCalculation calc;
    private final boolean trackPerformance;
    private long patternLookupNanos;
    private long emitLookupNanos;
    private long nodeCount;
    private final Set<AEKey> substituteClosureVisited = new ObjectOpenHashSet<>();
    private final Map<AEKey, IPatternDetails> choices;

    public GraphBuilder(CraftingCalculation calc) {
        this(calc, Map.of());
    }

    // Builds the graph with the given pattern of each key instead of the one of highest priority
    public GraphBuilder(CraftingCalculation calc, Map<AEKey, IPatternDetails> choices) {
        this.calc = calc;
        this.trackPerformance = calc.isPerformanceTrackingEnabled();
        this.choices = choices;
    }

    // For each key reachable from the output, the pattern needing the least of the scarce keys per item it makes. On
    // a tie, the one of highest priority.
    public Map<AEKey, IPatternDetails> choosePatterns(AEKey output, Object2DoubleMap<AEKey> scarcity)
        throws InterruptedException {
        var chosen = new Object2ObjectOpenHashMap<AEKey, IPatternDetails>();
        price(output, scarcity, new Object2DoubleOpenHashMap<>(), chosen);
        return chosen;
    }

    private double price(AEKey what, Object2DoubleMap<AEKey> scarcity, Object2DoubleOpenHashMap<AEKey> prices,
        Map<AEKey, IPatternDetails> chosen) throws InterruptedException {
        if (prices.containsKey(what)) {
            return prices.getDouble(what);
        }
        double own = scarcity.getDouble(what);
        if (calc.canEmitFor(what)) {
            prices.put(what, 0);
            return 0;
        }
        var patterns = calc.getCraftingFor(what);
        // A cycle back to the key is priced as free, so cycles alone never change the choice
        prices.put(what, 0);
        calc.handlePausing();
        var primary = patterns.isEmpty() ? null : patterns.get(0);
        IPatternDetails bestPattern = primary;
        double best = Double.POSITIVE_INFINITY;
        for (var pattern : patterns) {
            if (pattern != primary && (!makesFirst(what, pattern) || !isNativePattern(pattern))) continue;
            long made = madeAmount(what, pattern);
            if (made <= 0) continue;
            double cost = 0;
            for (var input : pattern.getInputs()) {
                var possible = input.possibleInputs();
                if (possible.length == 0 || input.getMultiplier() <= 0) continue;
                double amount = (double) possible[0].amount() * input.getMultiplier();
                cost += amount * price(possible[0].what(), scarcity, prices, chosen);
            }
            cost /= made;
            if (cost < best) {
                best = cost;
                bestPattern = pattern;
            }
        }
        if (bestPattern != primary) {
            chosen.put(what, bestPattern);
        }
        double result = best == Double.POSITIVE_INFINITY ? own : own + best;
        prices.put(what, result);
        return result;
    }

    public CraftingGraph buildGraph(AEKey output, long requestedAmount) throws InterruptedException {
        long start = this.trackPerformance ? System.nanoTime() : 0;
        this.patternLookupNanos = 0;
        this.emitLookupNanos = 0;
        this.nodeCount = 0;
        this.substituteClosureVisited.clear();
        var graph = new CraftingGraph();
        var requestStack = new ObjectOpenHashSet<AEKey>();

        var rootNode = buildNodeRecursive(output, graph, requestStack, true);
        if (rootNode == null) throw new IllegalStateException("Graph root was not created");
        graph.setRootNode(rootNode);
        rootNode.setDemandAmount(requestedAmount);

        if (this.trackPerformance) {
            long total = System.nanoTime() - start;
            calc.recordPerformanceCount("buildGraphNodes", nodeCount);
            calc.recordPerformanceStage("buildGraphPatternLookup", patternLookupNanos);
            calc.recordPerformanceStage("buildGraphEmitLookup", emitLookupNanos);
            calc.recordPerformanceStage(
                "buildGraphStructure",
                Math.max(0, total - patternLookupNanos - emitLookupNanos));
        }
        return graph;
    }

    private CraftingGraphNode buildNodeRecursive(AEKey what, CraftingGraph graph, Set<AEKey> requestStack, boolean root)
        throws InterruptedException {
        long emitStart = this.trackPerformance ? System.nanoTime() : 0;
        boolean canEmit = calc.canEmitFor(what);
        if (this.trackPerformance) {
            this.emitLookupNanos += System.nanoTime() - emitStart;
        }
        if (canEmit) {
            var nodeKey = new CraftingGraph.NodeKey(what, null);
            var existing = graph.getNode(nodeKey);
            if (existing != null) {
                return existing;
            }
            var emitter = new CraftingGraphNode(what, null, List.of(new GenericStack(what, 1)), 1);
            graph.putNode(nodeKey, emitter);
            this.nodeCount++;
            return emitter;
        }

        long patternStart = this.trackPerformance ? System.nanoTime() : 0;
        var patterns = calc.getCraftingFor(what);
        if (this.trackPerformance) {
            this.patternLookupNanos += System.nanoTime() - patternStart;
        }
        if (patterns.isEmpty()) {
            var substitute = root ? null : calc.getFuzzyCraftable(what);
            if (substitute != null) {
                graph.requireLegacyFallback();
                collectSubstituteClosure(substitute, graph);
            }
            if (root) {
                var nodeKey = new CraftingGraph.NodeKey(what, null);
                var leaf = new CraftingGraphNode(what, null, List.of(), 1, false, true);
                graph.putNode(nodeKey, leaf);
                this.nodeCount++;
                return leaf;
            }
            return null;
        }
        boolean localUnit = false;
        String localReason = null;
        if (patterns.size() != 1 && countMadeFirst(what, patterns) > 1) {
            graph.markAlternatives();
        }

        var primaryPattern = this.choices.getOrDefault(what, patterns.get(0));
        var nodeKey = new CraftingGraph.NodeKey(what, primaryPattern);

        var existing = graph.getNode(nodeKey);
        if (existing != null) {
            return existing;
        }

        if (requestStack.contains(what)) {
            graph.markRecursion();
            return graph.getNode(nodeKey);
        }

        long outputAmount = getRequestedOutputAmount(what, primaryPattern);
        if (outputAmount <= 0) {
            throw invalidPattern("mismatched pattern outputs");
        }
        if (!localUnit && !isNativePattern(primaryPattern)) {
            localUnit = true;
            localReason = "unsupported-pattern-semantics";
        }
        if (localUnit) {
            graph.requireLegacyFallback();
        }
        var node = new CraftingGraphNode(
            what,
            primaryPattern,
            patterns,
            primaryPattern.getOutputs(),
            outputAmount,
            localUnit,
            false,
            localReason);
        graph.putNode(nodeKey, node);
        this.nodeCount++;

        requestStack.add(what);
        calc.handlePausing();

        var graphInputs = new Object2LongLinkedOpenHashMap<AEKey>();
        var patternsToScan = localUnit ? patterns : List.of(primaryPattern);
        for (var pattern : patternsToScan) {
            var patternInputs = new Object2LongLinkedOpenHashMap<AEKey>();
            for (var inputEntry : pattern.getInputs()) {
                var possibleInputs = inputEntry.possibleInputs();
                if (possibleInputs.length == 0 || inputEntry.getMultiplier() <= 0) continue;
                // Fuzzy variants are only consumed by ingredient substitution, an assembler-pattern feature, so gate
                // on the pattern type instead of inferring it from the input list.
                boolean substitutable = pattern.canSubstitute();
                int possibleInputIndex = 0;
                while (possibleInputIndex < possibleInputs.length) {
                    var possible = possibleInputs[possibleInputIndex++];
                    if (possible.amount() > 0 && (!possible.what()
                        .equals(what) || !localUnit)) {
                        long amount = LongMath.saturatedMultiply(possible.amount(), inputEntry.getMultiplier());
                        patternInputs.put(
                            possible.what(),
                            LongMath.saturatedAdd(patternInputs.getLong(possible.what()), amount));
                        if (substitutable) {
                            graph.addFuzzyKey(possible.what());
                        }
                    }
                }
            }
            // A local unit is previewed with one selected pattern. Keep the largest per-key demand
            // across candidates, while still summing repeated slots within the same candidate.
            for (var input : patternInputs.object2LongEntrySet()) {
                graphInputs.put(input.getKey(), Math.max(graphInputs.getLong(input.getKey()), input.getLongValue()));
            }
        }

        for (var graphInput : graphInputs.object2LongEntrySet()) {
            var inputKey = graphInput.getKey();
            long inputAmount = graphInput.getLongValue();

            var childNode = buildNodeRecursive(inputKey, graph, requestStack, false);

            var edge = new CraftingGraphEdge(inputKey, inputAmount, childNode);
            node.addInput(edge);

            if (childNode != null) {
                childNode.addParent(node);
            }
        }

        requestStack.remove(what);
        return node;
    }

    /**
     * Records the keys the tree can reach through fuzzy substitution: it replaces a key without patterns of its own by
     * a craftable fuzzy variant and crafts that variant's patterns instead, so their material is consumed even though
     * the graph never expands it.
     */
    private void collectSubstituteClosure(AEKey what, CraftingGraph graph) throws InterruptedException {
        if (!this.substituteClosureVisited.add(what)) {
            return;
        }
        graph.addExtraSnapshotKey(what);
        calc.handlePausing();
        if (calc.canEmitFor(what)) {
            return;
        }
        var patterns = calc.getCraftingFor(what);
        if (patterns.isEmpty()) {
            collectFuzzyVariants(what, graph);
            return;
        }
        for (var pattern : patterns) {
            for (var input : pattern.getInputs()) {
                if (input.getMultiplier() <= 0) {
                    continue;
                }
                for (var possible : input.possibleInputs()) {
                    if (possible.amount() > 0) {
                        collectSubstituteClosure(possible.what(), graph);
                    }
                }
            }
        }
    }

    private void collectFuzzyVariants(AEKey what, CraftingGraph graph) throws InterruptedException {
        var collected = new ObjectOpenHashSet<AEKey>();
        while (true) {
            var variant = calc.getFuzzyCraftable(what, candidate -> !collected.contains(candidate));
            if (variant == null) {
                return;
            }
            collected.add(variant);
            collectSubstituteClosure(variant, graph);
        }
    }

    private static long getRequestedOutputAmount(AEKey what, IPatternDetails pattern) {
        var outputs = pattern.getOutputs();
        long requestedOutputAmount = 0;
        for (var output : outputs) {
            if (output.amount() <= 0) {
                throw invalidPattern("empty pattern output");
            }
            if (output.what()
                .equals(what)) {
                requestedOutputAmount = LongMath.saturatedAdd(requestedOutputAmount, output.amount());
            }
        }
        if (requestedOutputAmount <= 0) {
            throw invalidPattern("mismatched pattern outputs");
        }
        return requestedOutputAmount;
    }

    // AE2 registers a pattern for each of its outputs; one making the key on the side is no real alternative
    private static int countMadeFirst(AEKey what, List<IPatternDetails> patterns) {
        int count = 0;
        for (var pattern : patterns) {
            if (makesFirst(what, pattern)) {
                count++;
            }
        }
        return count;
    }

    private static boolean makesFirst(AEKey what, IPatternDetails pattern) {
        var outputs = pattern.getOutputs();
        return !outputs.isEmpty() && outputs.get(0)
            .what()
            .equals(what);
    }

    private static long madeAmount(AEKey what, IPatternDetails pattern) {
        long made = 0;
        for (var output : pattern.getOutputs()) {
            if (output.what()
                .equals(what)) {
                made = LongMath.saturatedAdd(made, output.amount());
            }
        }
        return made;
    }

    private static boolean isNativePattern(IPatternDetails pattern) {
        // The variants a substituting pattern lists depend on the stored items, which a cached graph must not
        if (pattern.canSubstitute()) return false;
        for (var input : pattern.getInputs()) {
            var possible = input.possibleInputs();
            if (possible.length != 1 || possible[0].amount() <= 0
                || input.getMultiplier() <= 0
                || input.getRemainingKey(possible[0].what()) != null) {
                return false;
            }
        }
        return true;
    }

    private static IllegalArgumentException invalidPattern(String reason) {
        return new IllegalArgumentException("Invalid graph crafting pattern: " + reason);
    }
}
