/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2. If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package com.science.gtnl.common.crafting.quantum.crafting;

import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Stopwatch;
import com.gtnllib.utils.LongMath;
import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.networking.crafting.ICraftingService;
import com.science.gtnl.common.crafting.quantum.api.networking.storage.IStorageService;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey2LongMap;
import com.science.gtnl.common.crafting.quantum.api.stacks.GenericStack;
import com.science.gtnl.common.crafting.quantum.api.stacks.KeyCounter;
import com.science.gtnl.common.crafting.quantum.core.AEConfig;
import com.science.gtnl.common.crafting.quantum.core.AELog;
import com.science.gtnl.common.crafting.quantum.core.localization.PlayerMessages;
import com.science.gtnl.common.crafting.quantum.crafting.execution.InputTemplate;
import com.science.gtnl.common.crafting.quantum.crafting.graph.CraftingGraph;
import com.science.gtnl.common.crafting.quantum.crafting.graph.DemandPropagation;
import com.science.gtnl.common.crafting.quantum.crafting.graph.GraphBuilder;
import com.science.gtnl.common.crafting.quantum.crafting.graph.GraphExecutor;
import com.science.gtnl.common.crafting.quantum.crafting.graph.LocalPatternPlan;
import com.science.gtnl.common.crafting.quantum.crafting.inv.ChildCraftingSimulationState;
import com.science.gtnl.common.crafting.quantum.crafting.inv.CraftingSimulationState;
import com.science.gtnl.common.crafting.quantum.crafting.inv.NetworkCraftingSimulationState;
import com.science.gtnl.common.crafting.quantum.hooks.ticking.TickHandler;

import appeng.api.config.Actionable;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public class CraftingCalculation {

    private final String requesterName;
    final ICraftingService craftingService;
    private final NetworkCraftingSimulationState networkInv;
    private final World level;
    private KeyCounter missing = new KeyCounter();
    private final KeyCounter recursiveMissingSeeds = new KeyCounter();
    private final Object monitor = new Object();
    private final @Nullable CraftingPerformanceLog performanceLog;
    private final Stopwatch watch = Stopwatch.createUnstarted();
    private @Nullable CraftingTreeNode tree;
    private final AEKey output;
    private final ObjectArrayList<AEKey> requestStack = new ObjectArrayList<>();
    private final ObjectArrayList<AEKey> availabilityStack = new ObjectArrayList<>();
    private final ObjectArrayList<CraftingTreeProcess> processStack = new ObjectArrayList<>();
    private final IntArrayList processHashPrefixes = new IntArrayList();
    private final IntArrayList processHashPowers = new IntArrayList();
    private final Map<AEKey, ObjectList<IPatternDetails>> patternCache = new Object2ObjectOpenHashMap<>();
    private final Map<IPatternDetails, AEKey2LongMap> expandedPatternNetOutputCache = new Reference2ObjectOpenHashMap<>();
    private final Map<IPatternDetails, AEKey2LongMap> patternOutputCountCache = new Reference2ObjectOpenHashMap<>();
    private final Map<IPatternDetails, AEKey2LongMap> patternInputCountCache = new Reference2ObjectOpenHashMap<>();
    private final Map<IPatternDetails, Map<AEKey, RecursivePatternBatch>> recursivePatternBatchCache = new Reference2ObjectOpenHashMap<>();
    private final Reference2BooleanOpenHashMap<IPatternDetails> positiveRecursiveNetCache = new Reference2BooleanOpenHashMap<>();
    private final Map<RecursiveNetKey, RecursiveNet> recursiveNetCache = new Object2ObjectOpenHashMap<>();
    private final RecursiveNetKey recursiveNetLookup = RecursiveNetKey.mutableProbe();
    private final ObjectPool<RecursiveNetKey> recursiveNetKeyPool = new ObjectPool<>(RecursiveNetKey::new, 100);
    // Plans the crafting CPU was already shown to run, which need no repair
    private final Set<CraftingPlan> plansKnownToRun = new ReferenceOpenHashSet<>();
    private final Set<AEKey> realSeededRecursiveRequests = new ObjectOpenHashSet<>();
    private final Set<AEKey> realRecursiveSeeds = new ObjectOpenHashSet<>();
    private final Set<AEKey> realSeededRecursiveKeys = new ObjectOpenHashSet<>();
    private final Set<AEKey> recursiveFinalOutputInputs = new ObjectOpenHashSet<>();
    private final KeyCounter recursiveReserveCandidates = new KeyCounter();
    private @Nullable CraftingGraph graph;
    private final long recursiveIngredientReserveAmount;
    // The initially requested amount of "output", may be reduced depending on the strategy used
    private final long requestedAmount;
    private final long craftingSessionEpoch;
    private final List<CraftAttempt> attempts = AELog.isCraftingLogEnabled() ? new ObjectArrayList<>() : null;
    private boolean simulate = false;
    private boolean allowMissing = false;
    private int missingSuppression = 0;
    private boolean running = false;
    private static final int ALTERNATIVE_ROUNDS = 4;
    private static final int LEGACY_FALLBACK_STEPS = 20_000;
    // The legacy tree takes at least this many steps per node of the graph
    private static final int LEGACY_STEPS_PER_NODE = 32;
    private long fallbackDeadline = Long.MAX_VALUE;
    private int fallbackSteps;
    private long limitDeadline = Long.MAX_VALUE;
    private int limitChecks;
    private boolean limitExceeded;
    private static final ThreadLocal<CraftingCalculation> CURRENT = new ThreadLocal<>();
    @Nullable
    private Runnable steppingThreadTask;
    private boolean done = false;
    private boolean cancelled = false;
    @Nullable
    private Thread workerThread;
    private int time = 5;
    private int incTime = Integer.MAX_VALUE;
    private int maxRequestDepth = 0;
    private long intermediateFinalOutputAmount = 0;
    private int recursiveMissingSeedSuppression = 0;
    @Nullable
    private KeyCounter reserveProtectedMissingSeeds = null;
    private boolean applyingRecursiveIngredientReserve = false;
    private boolean recursiveReserveBlockedByMissingSeed = false;
    private boolean localUnitMode = false;
    private @Nullable Set<AEKey> localUnitScope;
    private @Nullable KeyCounter localBoundaryDemands;

    public CraftingCalculation(World level, ICraftingService craftingService, IStorageService storage,
        String requesterName, GenericStack output) {
        this.level = level;
        this.output = output.what();
        this.requestedAmount = output.amount();
        this.craftingSessionEpoch = TickHandler.instance()
            .getCraftingSessionEpoch();
        this.requesterName = requesterName;
        this.performanceLog = AEConfig.instance()
            .isCraftingPerformanceLogEnabled() ? new CraftingPerformanceLog() : null;

        this.craftingService = craftingService;
        this.networkInv = createNetworkInventory(storage);
        this.recursiveIngredientReserveAmount = Math.max(0, craftingService.getRecursiveIngredientReserveAmount());
        this.processHashPrefixes.add(0);
        this.processHashPowers.add(1);

    }

    /**
     * Copies only the keys of the cached graph when there is one for the output, the whole inventory otherwise. A graph
     * built without the alternative patterns of its keys cannot tell what the legacy tree reads.
     */
    private NetworkCraftingSimulationState createNetworkInventory(IStorageService storage) {
        if (storage.getInventorySize() > NetworkCraftingSimulationState.SNAPSHOT_SUBSET_THRESHOLD) {
            var graph = this.craftingService.peekCachedGraph(this.output);
            if (graph != null && !graph.hasAlternatives()) {
                var keys = graph.getSnapshotKeys();
                if (keys.size() < storage.getInventorySize()) {
                    var fuzzyKeys = new ObjectOpenHashSet<AEKey>();
                    fuzzyKeys.addAll(keys);
                    fuzzyKeys.retainAll(graph.getFuzzyKeys());
                    var subset = new NetworkCraftingSimulationState(storage.copyInventory(keys, fuzzyKeys));
                    recordPerformanceCount("inventorySnapshotKeys", subset.getSnapshotEntryCount());
                    return subset;
                }
            }
        }
        var snapshot = new NetworkCraftingSimulationState(storage.copyInventory());
        recordPerformanceCount("inventorySnapshotKeys", snapshot.getSnapshotEntryCount());
        return snapshot;
    }

    private CraftingTreeNode getOrCreateLegacyTree() {
        var result = this.tree;
        if (result != null) {
            return result;
        }

        if (isPerformanceTrackingEnabled()) {
            long treeStart = System.nanoTime();
            result = new CraftingTreeNode(this.craftingService, this, this.output, 1, null, -1);
            recordPerformanceStage("construct-tree", System.nanoTime() - treeStart);

            long preloadStart = System.nanoTime();
            preloadPatternCache(this.output);
            recordPerformanceStage("preload-pattern-cache", System.nanoTime() - preloadStart);
        } else {
            result = new CraftingTreeNode(this.craftingService, this, this.output, 1, null, -1);
            preloadPatternCache(this.output);
        }
        this.tree = result;
        return result;
    }

    private void preloadPatternCache(AEKey what) {
        var patterns = getCraftingFor(what);
        for (int i = 0, size = patterns.size(); i < size; i++) {
            var pattern = patterns.get(i);
            var inputs = pattern.getInputs();
            for (var input : inputs) {
                var possibleInputs = input.possibleInputs();
                for (var possible : possibleInputs) {
                    getCraftingFor(possible.what());
                }
            }
        }
    }

    private long getPatternOutputCount(IPatternDetails pattern, AEKey what) {
        var outputsByKey = this.patternOutputCountCache
            .computeIfAbsent(pattern, ignored -> new AEKey2LongMap.OpenHashMap());
        return outputsByKey.computeIfAbsent(what, key -> computePatternOutputCount(pattern, (AEKey) key));
    }

    private static long computePatternOutputCount(IPatternDetails pattern, AEKey what) {
        long total = 0;
        var outputs = pattern.getOutputs();
        if (outputs instanceof RandomAccess) {
            for (int i = 0, size = outputs.size(); i < size; i++) {
                var output = outputs.get(i);
                if (what.matches(output)) {
                    total = LongMath.saturatedAdd(total, output.amount());
                }
            }
        } else {
            for (var output : outputs) {
                if (what.matches(output)) {
                    total = LongMath.saturatedAdd(total, output.amount());
                }
            }
        }
        return total;
    }

    private long getPatternInputCount(IPatternDetails pattern, AEKey what) {
        var inputsByKey = this.patternInputCountCache
            .computeIfAbsent(pattern, ignored -> new AEKey2LongMap.OpenHashMap());
        return inputsByKey.computeIfAbsent(what, key -> computePatternInputCount(pattern, (AEKey) key));
    }

    private static long computePatternInputCount(IPatternDetails pattern, AEKey what) {
        long total = 0;
        for (var input : pattern.getInputs()) {
            var possibleInputs = input.possibleInputs();
            for (var possibleInput : possibleInputs) {
                if (what.matches(possibleInput)) {
                    total = LongMath
                        .saturatedAdd(total, LongMath.saturatedMultiply(possibleInput.amount(), input.getMultiplier()));
                    break;
                }
            }
        }
        return total;
    }

    private static void accumulatePatternNet(KeyCounter netByKey, IPatternDetails pattern) {
        accumulatePatternNet(netByKey, pattern, 1);
    }

    private static void accumulatePatternNet(KeyCounter netByKey, IPatternDetails pattern, long times) {
        accumulatePatternNet(netByKey, null, pattern, times);
    }

    private static void accumulatePatternNet(KeyCounter netByKey, @Nullable Collection<AEKey> inputKeys,
        IPatternDetails pattern, long times) {
        var outputs = pattern.getOutputs();
        if (outputs instanceof RandomAccess) {
            for (int i = 0, size = outputs.size(); i < size; i++) {
                var output = outputs.get(i);
                netByKey.add(output.what(), LongMath.saturatedMultiply(output.amount(), times));
            }
        } else {
            for (var output : outputs) {
                netByKey.add(output.what(), LongMath.saturatedMultiply(output.amount(), times));
            }
        }

        for (var input : pattern.getInputs()) {
            var possibleInputs = input.possibleInputs();
            if (possibleInputs.length == 0) {
                continue;
            }
            var primaryInput = possibleInputs[0];
            var inputKey = primaryInput.what();
            long inputAmount = LongMath
                .saturatedMultiply(primaryInput.amount(), LongMath.saturatedMultiply(input.getMultiplier(), times));
            netByKey.add(inputKey, LongMath.saturatedSubtract(0, inputAmount));
            if (inputKeys != null) {
                inputKeys.add(inputKey);
            }
        }
    }

    private static long divideCeil(long dividend, long divisor) {
        long quotient = dividend / divisor;
        if (dividend % divisor == 0) {
            return quotient;
        }
        return quotient + 1;
    }

    public void addMissing(AEKey what, long amount) {
        if (this.realSeededRecursiveKeys.contains(what) || this.realRecursiveSeeds.contains(what)
            || hasRealSeededRecursiveRequestFor(what)) {
            return;
        }
        missing.add(what, amount);
    }

    public ObjectList<IPatternDetails> getCraftingFor(AEKey what) {
        return this.patternCache
            .computeIfAbsent(what, key -> new ObjectArrayList<>(this.craftingService.getCraftingFor(key)));
    }

    public boolean canEmitFor(AEKey what) {
        return this.craftingService.canEmitFor(what);
    }

    @Nullable
    public AEKey getFuzzyCraftable(AEKey what) {
        return getFuzzyCraftable(what, any -> true);
    }

    @Nullable
    public AEKey getFuzzyCraftable(AEKey what, Predicate<AEKey> filter) {
        return this.craftingService.getFuzzyCraftable(what, filter);
    }

    public LocalPatternPlan previewLocalPattern(AEKey what, IPatternDetails pattern, long craftTimes, Set<AEKey> scope,
        CraftingSimulationState parent) throws InterruptedException, CraftBranchFailure {
        var localInventory = new ChildCraftingSimulationState(parent);
        var localRoot = new CraftingTreeNode(this.craftingService, this, what, 1, null, -1);
        var localProcess = new CraftingTreeProcess(this.craftingService, this, pattern, localRoot);
        try (var context = enterLocalUnit(scope)) {
            localProcess.request(localInventory, craftTimes);
            var boundaryDemands = context.boundaryDemands();
            var calculationDelta = context.calculationDelta();
            context.rollback();
            return new LocalPatternPlan(
                pattern,
                craftTimes,
                boundaryDemands,
                localInventory.freezeDiff(),
                calculationDelta);
        }
    }

    public LocalPatternPlan previewLocalUnit(AEKey what, List<IPatternDetails> candidates, long requestedAmount,
        Set<AEKey> scope, CraftingSimulationState parent) throws InterruptedException, CraftBranchFailure {
        CraftBranchFailure lastFailure = null;
        for (int i = 0, size = candidates.size(); i < size; i++) {
            var pattern = candidates.get(i);
            long outputPerCraft = getPatternOutputCount(pattern, what);
            try {
                return previewLocalPattern(what, pattern, divideCeil(requestedAmount, outputPerCraft), scope, parent);
            } catch (CraftBranchFailure failure) {
                lastFailure = failure;
            }
        }
        if (lastFailure != null) throw lastFailure;
        throw new CraftBranchFailure(what, requestedAmount);
    }

    List<InputTemplate> collectValidTemplates(Iterable<InputTemplate> templates) {
        ObjectArrayList<InputTemplate> collected = null;
        for (var template : templates) {
            if (collected == null) collected = new ObjectArrayList<>(1);
            collected.add(template);
        }
        return collected == null ? List.of() : collected;
    }

    public CraftingPlan run() throws InterruptedException {
        this.workerThread = Thread.currentThread();
        long limit = AEConfig.instance()
            .getCalculationLimitNanos();
        if (limit > 0) {
            this.limitDeadline = LongMath.saturatedAdd(workerTime(), limit);
        }
        CURRENT.set(this);
        try {
            startPerformanceListener();
            if (!TickHandler.instance()
                .registerCraftingSimulation(this.level, this, this.craftingSessionEpoch)) {
                throw new InterruptedException("Crafting calculation session is closed");
            }
            this.handlePausing();

            CraftingPlan plan;
            if (isPerformanceTrackingEnabled()) {
                plan = timed("compute-plan", this::computePlan);
            } else {
                plan = computePlan();
            }
            // The legacy tree overshoots on cycles spanning several patterns
            if (plan != null && !this.plansKnownToRun.contains(plan)) {
                plan = CraftingPlanCompactor.repair(plan, this);
            }
            if (!TickHandler.instance()
                .isCurrentSession(this.craftingSessionEpoch)) {
                throw new InterruptedException("Crafting calculation session is closed");
            }
            this.logCraftingJob(plan);
            return plan;
        } catch (InterruptedException ex) {
            Thread.currentThread()
                .interrupt();
            throw ex;
        } catch (CraftingCalculationFailure ex) {
            throw ex;
        } catch (Exception ex) {
            AELog.info(ex, "Exception during crafting calculation.");
            throw new RuntimeException(ex);
        } finally {
            this.finish();
            this.workerThread = null;
            CURRENT.remove();
        }
    }

    // For game code that must run on the server thread, such as crafting events; it waits in simulateFor meanwhile
    public static <T> T callOnSteppingThread(Supplier<T> task, Supplier<T> fallback) {
        final CraftingCalculation calculation = CURRENT.get();
        if (calculation == null) {
            return task.get();
        }
        return calculation.handOver(task, fallback);
    }

    private <T> T handOver(Supplier<T> task, Supplier<T> fallback) {
        final Object[] result = new Object[1];
        final RuntimeException[] failure = new RuntimeException[1];
        final Runnable runnable = () -> {
            try {
                result[0] = task.get();
            } catch (RuntimeException e) {
                failure[0] = e;
            }
        };
        synchronized (this.monitor) {
            this.steppingThreadTask = runnable;
            this.monitor.notifyAll();
            while (this.steppingThreadTask != null && !this.cancelled) {
                try {
                    this.monitor.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread()
                        .interrupt();
                    break;
                }
            }
            if (this.steppingThreadTask != null) {
                // Cancelled before the task ran: the result no longer matters
                this.steppingThreadTask = null;
                return fallback.get();
            }
        }
        if (failure[0] != null) {
            throw failure[0];
        }
        @SuppressWarnings("unchecked")
        final T value = (T) result[0];
        return value;
    }

    private void startPerformanceListener() {
        if (this.performanceLog != null) {
            this.performanceLog.start(this.output, this.requestedAmount);
        }
    }

    private CraftingPlan computePlan() throws InterruptedException {
        if (!isPerformanceTrackingEnabled()) {
            return computePlanUnmeasured();
        }

        long calculationStart = System.nanoTime();
        try {
            return computePlanUnmeasured();
        } finally {
            finishPerformanceListener(System.nanoTime() - calculationStart);
        }
    }

    private CraftingPlan computePlanUnmeasured() throws InterruptedException {
        return runGraphBasedAttempt(requestedAmount);
    }

    /**
     * Starts an attempt over. The missing items are a new counter: a plan of an earlier attempt keeps its own.
     */
    private void resetAttemptState() {
        this.simulate = false;
        this.allowMissing = true;
        this.missing = new KeyCounter();
        this.recursiveMissingSeeds.clear();
        for (var key : this.recursiveNetCache.keySet()) {
            this.recursiveNetKeyPool.release(key);
        }
        this.recursiveNetCache.clear();
        this.intermediateFinalOutputAmount = 0;
        this.recursiveMissingSeedSuppression = 0;
        this.realSeededRecursiveRequests.clear();
        this.realRecursiveSeeds.clear();
        this.realSeededRecursiveKeys.clear();
        this.recursiveFinalOutputInputs.clear();
        this.recursiveReserveCandidates.clear();
        this.graph = null;
    }

    private CraftingPlan runCraftAttempt(long productionAmount, long finalAmount) throws InterruptedException {
        var tree = getOrCreateLegacyTree();
        resetAttemptState();
        tree.resetPossible();

        final boolean logCrafting = AELog.isCraftingLogEnabled();
        final Stopwatch timer = logCrafting ? Stopwatch.createStarted() : null;
        final boolean trackPerformance = isPerformanceTrackingEnabled();
        final String attemptName = trackPerformance
            ? "attempt amount=%d final=%d".formatted(productionAmount, finalAmount)
            : null;
        final long attemptStart = trackPerformance ? System.nanoTime() : 0;

        ChildCraftingSimulationState craftingInventory = new ChildCraftingSimulationState(networkInv);

        // Do the crafting. Throws in case of failure.
        try {
            if (trackPerformance) {
                runTimedCrafting(
                    "tree-request " + attemptName,
                    () -> tree.request(craftingInventory, productionAmount, null));
            } else {
                tree.request(craftingInventory, productionAmount, null);
            }
        } catch (CraftBranchFailure failure) {
            if (failure.hasExplicitMessageKey()) {
                throw reported(failure);
            }
            if (logCrafting) {
                this.attempts.add(new CraftAttempt(productionAmount + " failed", timer));
            }
            if (trackPerformance) {
                recordPerformanceStage(attemptName + " failed", System.nanoTime() - attemptStart);
            }
            return null;
        }
        applyRecursiveIngredientReserve(craftingInventory);
        clearResolvedRecursiveMissingItems(craftingInventory);
        addRecursiveMissingSeedsToPlan();
        craftingInventory.addBytes((double) tree.getNodeCount() * 8);
        CraftingPlan plan;
        if (trackPerformance) {
            plan = timed(
                "build-plan " + attemptName,
                () -> CraftingSimulationState.buildCraftingPlan(craftingInventory, this, finalAmount));
        } else {
            plan = CraftingSimulationState.buildCraftingPlan(craftingInventory, this, finalAmount);
        }
        if (logCrafting) {
            this.attempts
                .add(new CraftAttempt("%d succeeded (%d bytes)".formatted(productionAmount, plan.bytes()), timer));
        }
        if (trackPerformance) {
            recordPerformanceStage(attemptName + " completed", System.nanoTime() - attemptStart);
        }
        return plan;
    }

    private CraftingPlan runGraphBasedAttempt(long productionAmount) throws InterruptedException {
        long graphCalculationStart = System.nanoTime();
        this.simulate = false;
        this.allowMissing = true;

        var service = this.craftingService;
        long graphRevision = service.getGraphCacheRevision();
        var graph = service.takeCachedGraph(this.output);
        if (graph == null) {
            var graphBuilder = new GraphBuilder(this);
            graph = timed("buildGraph", () -> graphBuilder.buildGraph(this.output, productionAmount));
            graph.setCacheRevision(graphRevision);
        } else {
            // Structural reuse: clear all per-attempt planning state, then re-anchor the root demand.
            graph.resetForReuse();
            var cachedRoot = graph.getRootNode();
            if (cachedRoot == null) {
                throw new IllegalStateException("Cached graph has no root node");
            }
            cachedRoot.setDemandAmount(productionAmount);
            recordPerformanceCount("graphCacheHit", 1);
        }
        this.graph = graph;
        try {
            if (needsLegacyTree(graph)) {
                graph.requireLegacyFallback();
                return runCraftAttempt(productionAmount, productionAmount);
            }
            return runGraphBasedAttemptBody(graph, productionAmount, graphCalculationStart);
        } finally {
            service.putCachedGraph(this.output, graph);
        }
    }

    private static boolean needsLegacyTree(CraftingGraph graph) {
        return graph.requiresLegacyFallback() || hasUnsolvableCycle(graph);
    }

    private CraftingPlan runGraphBasedAttemptBody(CraftingGraph graph, long productionAmount,
        long graphCalculationStart) throws InterruptedException {
        recordPerformanceCount("graphNodes", graph.getNodeCount());
        recordPerformanceCount("graphEdges", graph.getEdgeCount());
        long sccUnits = 0;
        var topology = graph.getTopology();
        if (topology.isCondensed()) {
            for (var component : topology.getComponents()) {
                if (component.cyclic()) {
                    sccUnits++;
                }
            }
        }
        recordPerformanceCount("sccUnits", sccUnits);

        var graphPlan = planGraph(graph, productionAmount);
        if (graphPlan == null) {
            return runCraftAttempt(productionAmount, productionAmount);
        }
        if (graph.hasAlternatives() && !this.missing.isEmpty()) {
            return tryAlternatives(graphPlan, productionAmount, graph.getNodeCount());
        }
        return graphPlan;
    }

    /**
     * Plans the graph, or returns null when only the legacy tree can plan it.
     */
    private @Nullable CraftingPlan planGraph(CraftingGraph graph, long productionAmount) throws InterruptedException {
        var plan = planGraphOnce(graph, productionAmount);
        if (plan == null || !hasNativeRing(graph)) {
            return plan;
        }
        // The CPU runs any craft whose inputs are there, so crafts outside a ring taking what it cycles can starve it
        // although the plan balances. Such a plan would be repaired after the calculation, leaving the graph's crafting
        // tree behind; the legacy tree plans it instead. The graph itself is kept for other amounts.
        if (!CraftingCpuReplay.runs(plan, this)) {
            return null;
        }
        this.plansKnownToRun.add(plan);
        return plan;
    }

    private @Nullable CraftingPlan planGraphOnce(CraftingGraph graph, long productionAmount)
        throws InterruptedException {
        var propagation = new DemandPropagation(this);
        var planningInventory = new ChildCraftingSimulationState(this.networkInv);
        timed("propagateDemand", () -> {
            propagation.propagate(graph, planningInventory);
            return null;
        });
        if (graph.requiresLegacyFallback()) {
            return null;
        }

        var craftingInventory = new ChildCraftingSimulationState(this.networkInv);
        var executor = new GraphExecutor(this, graph);
        timed("applyGraph", () -> {
            executor.applyGraph(craftingInventory);
            return null;
        });

        applyRecursiveIngredientReserve(craftingInventory);
        clearResolvedRecursiveMissingItems(craftingInventory);
        addRecursiveMissingSeedsToPlan();
        craftingInventory.addBytes((double) graph.getNodeCount() * 8);

        return CraftingSimulationState.buildCraftingPlan(craftingInventory, this, productionAmount);
    }

    // The graph uses the pattern of highest priority of each key. With items missing, other patterns may need less of
    // them: the graph is planned again with the patterns needing the least of what went missing, weighing each item
    // by how often it did. The legacy tree, which splits a request between patterns, gets a few steps last.
    private CraftingPlan tryAlternatives(CraftingPlan graphPlan, long productionAmount, int graphNodes)
        throws InterruptedException {
        var best = detached(graphPlan);
        var plan = best;
        var scarcity = new Object2DoubleOpenHashMap<AEKey>();
        var tried = new ObjectOpenHashSet<Map<AEKey, IPatternDetails>>();
        tried.add(Map.of());
        for (int round = 0; round < ALTERNATIVE_ROUNDS && !plan.missingItems()
            .isEmpty(); round++) {
            plan.missingItems()
                .forEach((entryKey, entryAmount) -> scarcity.addTo(entryKey, 1));
            var choices = timed("choosePatterns", () -> new GraphBuilder(this).choosePatterns(this.output, scarcity));
            if (!tried.add(choices)) continue;
            resetAttemptState();
            CraftingPlan alternative;
            try {
                var graph = timed(
                    "buildAlternativeGraph",
                    () -> new GraphBuilder(this, choices).buildGraph(this.output, productionAmount));
                this.graph = graph;
                alternative = needsLegacyTree(graph) ? null : planGraph(graph, productionAmount);
            } catch (CraftingCalculationFailure | IllegalArgumentException | IllegalStateException e) {
                if (this.limitExceeded) throw e;
                alternative = null;
            }
            if (alternative == null) break;
            plan = detached(alternative);
            if (total(plan.missingItems()) < total(best.missingItems())) {
                best = plan;
            }
        }
        long limit = AEConfig.instance()
            .getCraftingFallbackNanos();
        if (limit <= 0 || (long) graphNodes * LEGACY_STEPS_PER_NODE > LEGACY_FALLBACK_STEPS) {
            return best;
        }
        this.fallbackDeadline = LongMath.saturatedAdd(workerTime(), limit);
        this.fallbackSteps = LEGACY_FALLBACK_STEPS;
        try {
            var legacyPlan = runCraftAttempt(productionAmount, productionAmount);
            // The legacy tree uses each pattern as far as it goes before the next one, as the priorities ask
            if (legacyPlan != null && total(legacyPlan.missingItems()) <= total(best.missingItems())) {
                return legacyPlan;
            }
        } catch (FallbackTimeout ignored) {
            // Keep the plan of the graph
        } catch (CraftingCalculationFailure failure) {
            if (this.limitExceeded) throw failure;
        } finally {
            this.fallbackDeadline = Long.MAX_VALUE;
        }
        return best;
    }

    // A plan keeps the missing items of the calculation, which the next attempt starts over
    private static CraftingPlan detached(CraftingPlan plan) {
        var missing = new KeyCounter();
        missing.addAll(plan.missingItems());
        return plan.withMissing(missing);
    }

    private static long total(KeyCounter counter) {
        long total = 0;
        for (var entry : counter) {
            total = LongMath.saturatedAdd(total, entry.getLongValue());
        }
        return total;
    }

    private static long workerTime() {
        var threads = ManagementFactory.getThreadMXBean();
        return threads.isCurrentThreadCpuTimeSupported() ? threads.getCurrentThreadCpuTime() : System.nanoTime();
    }

    private static class FallbackTimeout extends RuntimeException {

        private FallbackTimeout() {
            super("Crafting calculation fallback ran out of time", null, false, false);
        }
    }

    public void handlePausing() throws InterruptedException {
        if (this.fallbackDeadline != Long.MAX_VALUE
            && (--this.fallbackSteps < 0 || (this.fallbackSteps & 1023) == 0 && workerTime() > this.fallbackDeadline)) {
            throw new FallbackTimeout();
        }
        if (this.limitDeadline != Long.MAX_VALUE && (++this.limitChecks & 1023) == 0
            && Thread.currentThread() == this.workerThread
            && workerTime() > this.limitDeadline) {
            this.limitExceeded = true;
            throw new CraftingCalculationFailure(PlayerMessages.CraftingTooLong);
        }
        if (this.incTime > AEConfig.CRAFTING.craftingCalculationPausingInterval) {
            this.incTime = 0;

            synchronized (this.monitor) {
                if (this.watch.elapsed(TimeUnit.MICROSECONDS) > this.time) {
                    this.running = false;
                    this.watch.stop();
                    this.monitor.notify();
                }

                if (!this.running) {
                    AELog.craftingDebug("crafting job will now sleep");

                    while (!this.running && !this.cancelled) {
                        this.monitor.wait();
                    }

                    if (this.cancelled) {
                        throw new InterruptedException("Crafting calculation cancelled");
                    }

                    AELog.craftingDebug("crafting job now active");
                }
            }

            if (this.cancelled || Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
        this.incTime++;
    }

    private void finish() {
        synchronized (this.monitor) {
            this.running = false;
            this.done = true;
            this.monitor.notifyAll();
        }
        TickHandler.instance()
            .unregisterCraftingSimulation(this.level, this);
    }

    public void cancel() {
        Thread thread;
        synchronized (this.monitor) {
            if (this.done) {
                return;
            }
            this.cancelled = true;
            this.running = false;
            this.monitor.notifyAll();
            thread = this.workerThread;
        }
        if (thread != null) {
            thread.interrupt();
        }
    }

    public boolean isSimulation() {
        return this.simulate;
    }

    boolean canUseMissingItems() {
        return (this.simulate || this.allowMissing) && this.missingSuppression == 0;
    }

    void pushMissingSuppression() {
        this.missingSuppression++;
    }

    void popMissingSuppression() {
        this.missingSuppression--;
    }

    boolean isRequesting(AEKey what) {
        for (int i = 0; i < this.requestStack.size(); i++) {
            if (this.requestStack.get(i)
                .equals(what)) {
                return true;
            }
        }
        return false;
    }

    boolean isCheckingAvailability(AEKey what) {
        for (int i = 0; i < this.availabilityStack.size(); i++) {
            if (this.availabilityStack.get(i)
                .equals(what)) {
                return true;
            }
        }
        return false;
    }

    void pushRequest(AEKey what) {
        this.requestStack.add(what);
        this.maxRequestDepth = Math.max(this.maxRequestDepth, this.requestStack.size());
    }

    void popRequest() {
        this.requestStack.removeLast();
    }

    @Nullable
    AEKey getCurrentRequestKey() {
        return this.requestStack.isEmpty() ? null : this.requestStack.get(this.requestStack.size() - 1);
    }

    void pushAvailabilityCheck(AEKey what) {
        this.availabilityStack.add(what);
    }

    void popAvailabilityCheck() {
        this.availabilityStack.removeLast();
    }

    void pushProcess(CraftingTreeProcess process) {
        this.processStack.add(process);
        int previousHash = this.processHashPrefixes.getInt(this.processHashPrefixes.size() - 1);
        this.processHashPrefixes.add(31 * previousHash + System.identityHashCode(process));
        int previousPower = this.processHashPowers.getInt(this.processHashPowers.size() - 1);
        this.processHashPowers.add(31 * previousPower);
    }

    void popProcess() {
        this.processStack.removeLast();
        this.processHashPrefixes.removeInt(this.processHashPrefixes.size() - 1);
        this.processHashPowers.removeInt(this.processHashPowers.size() - 1);
    }

    // The failure the player is told about, with the cycle without net output it ran into
    private CraftingCalculationFailure reported(CraftBranchFailure failure) {
        return new CraftingCalculationFailure(failure.getLocalizedMessageKey(), failure.getCycle());
    }

    public boolean cycleHasNetOutput(AEKey what) {
        if (getCycleNetOutput(what) > 0) {
            return true;
        }
        int requestIndex = findRecursiveRequestIndex(what);
        return requestIndex >= 0 && getCycleBatchRuns(what, requestIndex) != null;
    }

    long getCycleNetOutput(AEKey what) {
        int requestIndex = -1;
        for (int i = this.requestStack.size() - 1; i >= 0; i--) {
            if (this.requestStack.get(i)
                .equals(what)) {
                requestIndex = i;
                break;
            }
        }
        if (requestIndex < 0) {
            return 0;
        }

        long netOutput = 0;
        for (int i = requestIndex; i < this.processStack.size(); i++) {
            var process = this.processStack.get(i);
            netOutput = LongMath.saturatedAdd(netOutput, process.getOutputCount(what));
            netOutput = LongMath.saturatedSubtract(netOutput, process.getInputCount(what));
        }
        return netOutput;
    }

    boolean resolveRecursiveRequest(AEKey what, CraftingSimulationState inv, long amount) {
        var resolution = getRecursiveResolution(what, inv);
        if (resolution == null) {
            return false;
        }

        if (resolution.seed() != null) {
            if (resolution.missingSeed() != null) {
                clearRecursiveMissingSeed(
                    resolution.missingSeed()
                        .what());
            }
            clearRecursiveMissingSeed(
                resolution.seed()
                    .what());
            inv.extract(
                resolution.seed()
                    .what(),
                resolution.seed()
                    .amount(),
                Actionable.MODULATE);
            inv.insert(what, amount, Actionable.MODULATE);
            return true;
        }

        if (resolution.missingSeeds()) {
            this.recursiveMissingSeedSuppression++;
            try {
                inv.insert(what, amount, Actionable.MODULATE);
                return true;
            } finally {
                this.recursiveMissingSeedSuppression--;
            }
        }

        return false;
    }

    boolean canResolveRecursiveRequest(AEKey what, CraftingSimulationState inv, long locallyExtractedAmount) {
        return getRecursiveResolution(what, inv, locallyExtractedAmount) != null;
    }

    private RecursiveResolution getRecursiveResolution(AEKey what, CraftingSimulationState inv) {
        return getRecursiveResolution(what, inv, 0);
    }

    private RecursiveResolution getRecursiveResolution(AEKey what, CraftingSimulationState inv,
        long locallyExtractedAmount) {
        var recursiveNet = getRecursiveNet(what);
        if (recursiveNet == null || !recursiveNet.canResolve()) {
            return null;
        }

        var seed = getRecursiveSeed(inv, recursiveNet, what, locallyExtractedAmount);
        if (seed != null) {
            this.realSeededRecursiveRequests.add(getRecursiveRootKey(recursiveNet.requestIndex()));
            this.realRecursiveSeeds.add(seed.what());
            addRecursiveReserveCandidates(recursiveNet);
            addRealSeededRecursiveKeys(recursiveNet.netByKey());
            var missingSeed = getMissingRecursiveSeed(recursiveNet, what);
            if (missingSeed != null && recursiveNet.netByKey()
                .get(missingSeed.what()) >= 0) {
                clearRecursiveMissingSeed(missingSeed.what());
            }
            return new RecursiveResolution(seed, false, missingSeed);
        }

        AEKey recursiveRoot = getRecursiveRootKey(recursiveNet.requestIndex());
        if (canUseMissingItems() && this.recursiveMissingSeedSuppression == 0) {
            var missingSeed = getMissingRecursiveSeed(recursiveNet, what);
            if (missingSeed == null) {
                return null;
            }
            if (this.applyingRecursiveIngredientReserve && isReserveProtectedMissingSeed(missingSeed.what())) {
                this.recursiveReserveBlockedByMissingSeed = true;
                return null;
            }
            if (!this.realSeededRecursiveRequests.contains(recursiveRoot)) {
                addRecursiveMissingSeed(missingSeed.what(), missingSeed.amount());
            }
            addRecursiveReserveCandidates(recursiveNet);
            return new RecursiveResolution(null, true, missingSeed);
        }

        return null;
    }

    private AEKey getRecursiveRootKey(int requestIndex) {
        return this.requestStack.get(requestIndex);
    }

    private RecursiveNet getRecursiveNet(AEKey what) {
        int requestIndex = findRecursiveRequestIndex(what);
        if (requestIndex < 0) {
            return null;
        }

        int processCount = this.processStack.size() - requestIndex;
        int processHash = getProcessSuffixHash(requestIndex, processCount);
        this.recursiveNetLookup.reset(what, requestIndex, this.processStack, requestIndex, processCount, processHash);

        var cached = this.recursiveNetCache.get(this.recursiveNetLookup);
        if (cached != null) {
            return cached;
        }

        var frozenKey = this.recursiveNetLookup.freeze(this.recursiveNetKeyPool);
        var computed = computeRecursiveNet(frozenKey, null);
        if (!computed.canResolve() && computed.netByKey()
            .get(what) < 0) {
            // The batch only depends on the processes, which the cache key holds.
            long[] runs = getCycleBatchRuns(what, requestIndex);
            if (runs != null && runs[0] > 1) {
                computed = computeRecursiveNet(frozenKey, runs);
            }
        }
        this.recursiveNetCache.put(frozenKey, computed);
        return computed;
    }

    private int getProcessSuffixHash(int offset, int length) {
        int endHash = this.processHashPrefixes.getInt(offset + length);
        int startHash = this.processHashPrefixes.getInt(offset);
        return endHash - startHash * this.processHashPowers.getInt(length);
    }

    private int findRecursiveRequestIndex(AEKey what) {
        for (int i = this.requestStack.size() - 1; i >= 0; i--) {
            if (this.requestStack.get(i)
                .equals(what)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param runs how often each process runs, see {@link #getCycleBatchRuns}, or null for once each
     */
    private RecursiveNet computeRecursiveNet(RecursiveNetKey key, @Nullable long[] runs) {
        recordPerformanceCount("recursive-net-analysis", 1);
        var netByKey = new KeyCounter();
        var inputKeys = new ObjectOpenHashSet<AEKey>();
        var includedPatterns = new ObjectOpenHashSet<IPatternDetails>();
        for (int i = 0; i < key.processCount(); i++) {
            var process = key.processAt(i);
            process.accumulateNet(netByKey, runs == null ? 1 : runs[i]);
            process.accumulateInputKeys(inputKeys);
            includedPatterns.add(process.details);
        }

        expandRecursiveNetClosure(netByKey, inputKeys, includedPatterns);

        boolean hasPositiveNet = false;
        for (var entry : netByKey) {
            if (entry.getLongValue() > 0) {
                hasPositiveNet = true;
                break;
            }
        }

        return new RecursiveNet(
            key.requestIndex(),
            netByKey,
            inputKeys,
            hasPositiveNet && netByKey.get(key.what()) >= 0);
    }

    public long getExpandedPatternNetOutput(IPatternDetails pattern, AEKey what) {
        var outputsByKey = this.expandedPatternNetOutputCache
            .computeIfAbsent(pattern, ignored -> new AEKey2LongMap.OpenHashMap());
        return outputsByKey.computeIfAbsent(what, key -> computeExpandedPatternNetOutput(pattern, (AEKey) key));
    }

    private long computeExpandedPatternNetOutput(IPatternDetails pattern, AEKey what) {
        long directOutput = getPatternOutputCount(pattern, what);
        if (directOutput <= 0) {
            return 0;
        }

        var netByKey = new KeyCounter();
        var includedPatterns = new ObjectOpenHashSet<IPatternDetails>();
        includedPatterns.add(pattern);
        accumulatePatternNet(netByKey, pattern);
        expandRecursiveNetClosure(netByKey, null, includedPatterns);

        return netByKey.get(what);
    }

    /**
     * Whether the fully expanded recursive closure of the pattern has any positive net output for any key. A closure
     * without any positive net can never grow the simulated inventory by crafting, so the SCC can be served natively
     * from seed inventory without falling back to a legacy transaction.
     */
    public boolean hasPositiveRecursiveNet(IPatternDetails pattern) {
        return this.positiveRecursiveNetCache.computeIfAbsent(pattern, this::computeHasPositiveRecursiveNet);
    }

    private boolean computeHasPositiveRecursiveNet(IPatternDetails pattern) {
        var netByKey = new KeyCounter();
        var includedPatterns = new ObjectOpenHashSet<IPatternDetails>();
        includedPatterns.add(pattern);
        accumulatePatternNet(netByKey, pattern);
        expandRecursiveNetClosure(netByKey, null, includedPatterns);
        for (var entry : netByKey) {
            if (entry.getLongValue() > 0) {
                return true;
            }
        }
        return false;
    }

    public RecursivePatternBatch getRecursivePatternBatch(IPatternDetails pattern, AEKey what) {
        var batchesByKey = this.recursivePatternBatchCache
            .computeIfAbsent(pattern, ignored -> new Object2ObjectOpenHashMap<>());
        return batchesByKey.computeIfAbsent(what, key -> computeRecursivePatternBatch(pattern, key));
    }

    private RecursivePatternBatch computeRecursivePatternBatch(IPatternDetails pattern, AEKey what) {
        long directOutput = getPatternOutputCount(pattern, what);
        if (directOutput <= 0) {
            return new RecursivePatternBatch(1, 0);
        }

        // Only the single-root pass is needed for SCC eligibility: a cycle that only turns positive after multiple
        // root crafts can never be native (rootTimes must be 1), so scanning further is wasted work whose result is
        // ignored by the local fallback anyway.
        boolean rootConsumesTarget = getPatternInputCount(pattern, what) > 0;
        var netByKey = new KeyCounter();
        var recursiveUse = new RecursiveUse(rootConsumesTarget);
        accumulatePatternNet(netByKey, pattern, 1);
        if (!expandRecursiveBatchNet(netByKey, what, recursiveUse)) {
            return new RecursivePatternBatch(1, directOutput);
        }

        if (!recursiveUse.get()) {
            return new RecursivePatternBatch(1, directOutput);
        }

        long netOutput = netByKey.get(what);
        return new RecursivePatternBatch(1, netOutput > 0 ? netOutput : directOutput);
    }

    /**
     * Checks whether a recursive seed is available. Positive-net recursive closures do not net-consume their seed, so
     * the immutable network snapshot remains authoritative even when the current local transaction has already counted
     * the seed in {@code requiredExtract}.
     */
    static boolean hasRecursiveSeedAvailability(CraftingSimulationState networkInventory,
        CraftingSimulationState localInventory, AEKey seed, long requiredAmount, long locallyExtractedAmount,
        boolean seedIsRequested) {
        long localAvailable = localInventory.getAvailableNonProducedAmount(seed);
        if (seedIsRequested) {
            localAvailable = LongMath.saturatedAdd(localAvailable, locallyExtractedAmount);
        }
        long networkAvailable = networkInventory.getOriginalAmount(seed);
        return Math.max(networkAvailable, localAvailable) >= requiredAmount;
    }

    private RecursiveSeed getRecursiveSeed(CraftingSimulationState inv, RecursiveNet recursiveNet, AEKey what,
        long locallyExtractedAmount) {
        for (AEKey seed : recursiveNet.inputKeys()) {
            if (recursiveNet.netByKey()
                .get(seed) < 0 || isReserveProtectedMissingSeed(seed)) {
                continue;
            }
            long amount = getRecursiveSeedAmount(seed, recursiveNet.requestIndex());
            if (hasRecursiveSeedAvailability(
                this.networkInv,
                inv,
                seed,
                amount,
                locallyExtractedAmount,
                seed.equals(what))) {
                return new RecursiveSeed(seed, amount);
            }
        }
        return null;
    }

    private long getRecursiveSeedAmount(AEKey seed, int requestIndex) {
        if (requestIndex >= 0 && requestIndex < this.processStack.size()) {
            for (int i = requestIndex; i < this.processStack.size(); i++) {
                var process = this.processStack.get(i);
                long amount = process.getInputCount(seed);
                if (amount > 0) {
                    return amount;
                }
            }
        }
        return 1;
    }

    private RecursiveSeed getMissingRecursiveSeed(RecursiveNet recursiveNet, AEKey what) {
        int requestIndex = recursiveNet.requestIndex();
        if (requestIndex >= 0 && requestIndex < this.processStack.size()) {
            for (AEKey seed : recursiveNet.inputKeys()) {
                if (seed.equals(what) || recursiveNet.netByKey()
                    .get(seed) < 0 || isReserveProtectedMissingSeed(seed)) {
                    continue;
                }
                return new RecursiveSeed(seed, getRecursiveSeedAmount(seed, requestIndex));
            }
        }
        return null;
    }

    private void addRecursiveMissingSeed(AEKey what, long amount) {
        if (hasRealSeededRecursiveRequestFor(what)) {
            clearRecursiveMissingSeed(what);
            return;
        }
        if (this.realRecursiveSeeds.contains(what)) {
            clearRecursiveMissingSeed(what);
            return;
        }
        long existing = this.recursiveMissingSeeds.get(what);
        if (existing >= amount) {
            return;
        }

        long delta = amount - existing;
        this.recursiveMissingSeeds.add(what, delta);
    }

    /**
     * Records a seed cut resolved by the graph executor in the shared recursive bookkeeping.
     */
    public void recordGraphRecursiveSeed(AEKey recursiveRoot, AEKey what, long required, long allocated, long missing,
        long reserve) {
        if (missing > 0) {
            addRecursiveMissingSeed(what, missing);
        } else if (required > 0 && allocated >= required) {
            clearRecursiveMissingSeed(what);
            this.realSeededRecursiveRequests.add(recursiveRoot);
            this.realRecursiveSeeds.add(what);
            this.realSeededRecursiveKeys.add(what);
        }
        if (reserve > 0) {
            long existing = this.recursiveReserveCandidates.get(what);
            if (existing < reserve) {
                this.recursiveReserveCandidates.add(what, reserve - existing);
            }
        }
    }

    private void clearRecursiveMissingSeed(AEKey what) {
        this.recursiveMissingSeeds.remove(what);
        this.missing.remove(what);
    }

    private void applyRecursiveIngredientReserve(CraftingSimulationState inv) throws InterruptedException {
        if (this.recursiveIngredientReserveAmount <= 0 || this.recursiveReserveCandidates.isEmpty()) {
            return;
        }

        var protectedMissingSeeds = getRecursiveMissingSeedsMarker();
        this.reserveProtectedMissingSeeds = protectedMissingSeeds;
        this.applyingRecursiveIngredientReserve = true;
        try {
            var reserveCandidates = new ObjectArrayList<AEKey>();
            this.recursiveReserveCandidates.forEach((entryKey, entryAmount) -> reserveCandidates.add(entryKey));
            for (int i = 0; i < reserveCandidates.size(); i++) {
                var what = reserveCandidates.get(i);
                if (protectedMissingSeeds.get(what) > 0) {
                    continue;
                }

                long reservePerBatch = this.recursiveReserveCandidates.get(what);
                if (reservePerBatch <= 0) {
                    continue;
                }

                long targetReserve = Math.min(
                    LongMath.saturatedMultiply(this.recursiveIngredientReserveAmount, reservePerBatch),
                    inv.getOriginalAmount(what));
                if (targetReserve <= 0) {
                    continue;
                }

                long available = inv.getAvailableAmount(what);
                if (available >= targetReserve) {
                    continue;
                }

                long deficit = targetReserve - available;
                if (getCraftingFor(what).isEmpty()) {
                    long returned = inv.returnExtractedForReserve(what, deficit);
                    if (returned > 0) {
                        this.missing.add(what, returned);
                    }
                    continue;
                }

                var branchInv = new ChildCraftingSimulationState(inv);
                var branchMarker = createRecursiveReserveBranchMarker();
                this.recursiveReserveBlockedByMissingSeed = false;
                try {
                    reserveLocalBranch(what, deficit, branchInv);
                    if (this.recursiveReserveBlockedByMissingSeed) {
                        restoreRecursiveReserveBranchMarker(branchMarker);
                        continue;
                    }
                    branchInv.applyDiff(inv);
                } catch (CraftBranchFailure failure) {
                    if (this.recursiveReserveBlockedByMissingSeed) {
                        restoreRecursiveReserveBranchMarker(branchMarker);
                        continue;
                    }
                    if (failure.hasExplicitMessageKey()) {
                        throw reported(failure);
                    }
                    restoreRecursiveReserveBranchMarker(branchMarker);
                    this.missing.add(what, deficit);
                } finally {
                    this.recursiveReserveBlockedByMissingSeed = false;
                }
            }
        } finally {
            restoreProtectedRecursiveMissingSeeds(protectedMissingSeeds);
            this.applyingRecursiveIngredientReserve = false;
            this.reserveProtectedMissingSeeds = null;
            this.recursiveReserveBlockedByMissingSeed = false;
        }
    }

    private void reserveLocalBranch(AEKey what, long deficit, ChildCraftingSimulationState branchInv)
        throws InterruptedException, CraftBranchFailure {
        var unitPlan = previewLocalUnit(what, getCraftingFor(what), deficit, Set.of(what), branchInv);
        commitLocalPlanWithBoundaries(unitPlan, branchInv);
    }

    private RecursiveReserveBranchMarker createRecursiveReserveBranchMarker() {
        return new RecursiveReserveBranchMarker(createCalculationMarker());
    }

    private void restoreRecursiveReserveBranchMarker(RecursiveReserveBranchMarker marker) {
        restoreCalculationMarker(marker.calculationMarker());
    }

    /**
     * Captures calculation-global state which is not part of a {@link ChildCraftingSimulationState} diff.
     */
    public CalculationMarker createCalculationMarker() {
        var recursiveFinalOutputInputs = snapshotSet(this.recursiveFinalOutputInputs);
        var recursiveReserveCandidates = new KeyCounter();
        recursiveReserveCandidates.addAll(this.recursiveReserveCandidates);
        KeyCounter reserveProtectedMissingSeeds = null;
        if (this.reserveProtectedMissingSeeds != null) {
            reserveProtectedMissingSeeds = new KeyCounter();
            reserveProtectedMissingSeeds.addAll(this.reserveProtectedMissingSeeds);
        }
        return new CalculationMarker(
            getMissingItemsMarker(),
            getRecursiveMissingSeedsMarker(),
            getRealSeededRecursiveRequestsMarker(),
            getRealRecursiveSeedsMarker(),
            getRealSeededRecursiveKeysMarker(),
            recursiveFinalOutputInputs,
            recursiveReserveCandidates,
            getIntermediateFinalOutputMarker(),
            reserveProtectedMissingSeeds,
            this.missingSuppression,
            this.recursiveMissingSeedSuppression,
            this.applyingRecursiveIngredientReserve,
            this.recursiveReserveBlockedByMissingSeed);
    }

    public void restoreCalculationMarker(CalculationMarker marker) {
        restoreMissingItemsMarker(marker.missingItems());
        restoreRecursiveMissingSeedsMarker(marker.recursiveMissingSeeds());
        restoreRealSeededRecursiveRequestsMarker(marker.realSeededRecursiveRequests());
        restoreRealRecursiveSeedsMarker(marker.realRecursiveSeeds());
        restoreRealSeededRecursiveKeysMarker(marker.realSeededRecursiveKeys());
        this.recursiveFinalOutputInputs.clear();
        this.recursiveFinalOutputInputs.addAll(marker.recursiveFinalOutputInputs());
        this.recursiveReserveCandidates.clear();
        this.recursiveReserveCandidates.addAll(marker.recursiveReserveCandidates());
        restoreIntermediateFinalOutputMarker(marker.intermediateFinalOutputAmount());
        if (marker.reserveProtectedMissingSeeds() == null) {
            this.reserveProtectedMissingSeeds = null;
        } else {
            this.reserveProtectedMissingSeeds = new KeyCounter();
            this.reserveProtectedMissingSeeds.addAll(marker.reserveProtectedMissingSeeds());
        }
        this.missingSuppression = marker.missingSuppression();
        this.recursiveMissingSeedSuppression = marker.recursiveMissingSeedSuppression();
        this.applyingRecursiveIngredientReserve = marker.applyingRecursiveIngredientReserve();
        this.recursiveReserveBlockedByMissingSeed = marker.recursiveReserveBlockedByMissingSeed();
    }

    public CalculationDelta createCalculationDelta(CalculationMarker before) {
        return new CalculationDelta(
            positiveCounterDelta(before.missingItems(), this.missing),
            increasedCounterTargets(before.recursiveMissingSeeds(), this.recursiveMissingSeeds),
            setDelta(before.realSeededRecursiveRequests(), this.realSeededRecursiveRequests),
            setDelta(before.realRecursiveSeeds(), this.realRecursiveSeeds),
            setDelta(before.realSeededRecursiveKeys(), this.realSeededRecursiveKeys),
            setDelta(before.recursiveFinalOutputInputs(), this.recursiveFinalOutputInputs),
            increasedCounterTargets(before.recursiveReserveCandidates(), this.recursiveReserveCandidates),
            Math.max(0, this.intermediateFinalOutputAmount - before.intermediateFinalOutputAmount()));
    }

    public void mergeCalculationDelta(CalculationDelta delta) {
        this.missing.addAll(delta.missingItems());
        mergeCounterMaximum(this.recursiveMissingSeeds, delta.recursiveMissingSeeds());
        this.realSeededRecursiveRequests.addAll(delta.realSeededRecursiveRequests());
        this.realRecursiveSeeds.addAll(delta.realRecursiveSeeds());
        this.realSeededRecursiveKeys.addAll(delta.realSeededRecursiveKeys());
        this.recursiveFinalOutputInputs.addAll(delta.recursiveFinalOutputInputs());
        mergeCounterMaximum(this.recursiveReserveCandidates, delta.recursiveReserveCandidates());
        addIntermediateFinalOutput(delta.intermediateFinalOutputAmount());
    }

    private static KeyCounter positiveCounterDelta(KeyCounter before, KeyCounter after) {
        var result = new KeyCounter();
        after.forEach((entryKey, entryAmount) -> {
            long delta = entryAmount - before.get(entryKey);
            if (delta > 0) {
                result.add(entryKey, delta);
            }
        });
        return result;
    }

    private static KeyCounter increasedCounterTargets(KeyCounter before, KeyCounter after) {
        var result = new KeyCounter();
        after.forEach((entryKey, entryAmount) -> {
            if (entryAmount > before.get(entryKey)) {
                result.add(entryKey, entryAmount);
            }
        });
        return result;
    }

    private static void mergeCounterMaximum(KeyCounter target, KeyCounter additions) {
        additions.forEach((entryKey, entryAmount) -> {
            long current = target.get(entryKey);
            if (entryAmount > current) {
                target.add(entryKey, entryAmount - current);
            }
        });
    }

    private static <T> Set<T> setDelta(Set<T> before, Set<T> after) {
        Set<T> result = null;
        for (var key : after) {
            if (!before.contains(key)) {
                if (result == null) result = new ObjectOpenHashSet<>(Math.min(4, after.size()));
                result.add(key);
            }
        }
        return result == null ? Set.of() : Collections.unmodifiableSet(result);
    }

    /**
     * Starts a legacy compatibility unit. Requests outside {@code scope} are exposed as graph boundary demands instead
     * of recursively expanding another legacy subtree.
     */
    public LocalUnitContext enterLocalUnit(Set<AEKey> scope) {
        if (this.localUnitMode) {
            throw new IllegalStateException("Nested local crafting units are not supported");
        }
        this.localUnitMode = true;
        this.localUnitScope = Set.copyOf(scope);
        this.localBoundaryDemands = new KeyCounter();
        return new LocalUnitContext(this, createCalculationMarker());
    }

    boolean interceptLocalBoundaryRequest(AEKey what, long amount, CraftingSimulationState inventory) {
        var boundaryDemands = this.localBoundaryDemands;
        if (!this.localUnitMode || this.localUnitScope == null
            || boundaryDemands == null
            || this.localUnitScope.contains(what)) {
            return false;
        }
        if (amount > 0) {
            boundaryDemands.add(what, amount);
            inventory.insertBoundary(what, amount);
        }
        return true;
    }

    boolean interceptLocalBoundaryAvailability(AEKey what) {
        var boundaryDemands = this.localBoundaryDemands;
        return this.localUnitMode && this.localUnitScope != null
            && boundaryDemands != null
            && !this.localUnitScope.contains(what);
    }

    boolean isLocalUnitMode() {
        return this.localUnitMode;
    }

    private void commitLocalPlanWithBoundaries(LocalPatternPlan plan, ChildCraftingSimulationState parent)
        throws CraftBranchFailure {
        var marker = createCalculationMarker();
        var transaction = new ChildCraftingSimulationState(parent);
        try {
            extractLocalBoundaries(plan.boundaryDemands(), transaction);
            plan.commit(this, transaction);
            transaction.applyDiff(parent);
        } catch (RuntimeException | Error failure) {
            restoreCalculationMarker(marker);
            throw failure;
        }
    }

    private static void extractLocalBoundaries(KeyCounter boundaries, ChildCraftingSimulationState transaction)
        throws CraftBranchFailure {
        for (var boundary : boundaries) {
            long extracted = transaction.extract(boundary.getKey(), boundary.getLongValue(), Actionable.MODULATE);
            if (extracted < boundary.getLongValue()) {
                throw new CraftBranchFailure(boundary.getKey(), boundary.getLongValue() - extracted);
            }
            transaction.addStackBytes(boundary.getKey(), extracted, 1);
        }
    }

    private KeyCounter exitLocalUnit() {
        if (!this.localUnitMode || this.localBoundaryDemands == null) {
            throw new IllegalStateException("No local crafting unit is active");
        }
        var result = new KeyCounter();
        result.addAll(this.localBoundaryDemands);
        this.localUnitMode = false;
        this.localUnitScope = null;
        this.localBoundaryDemands = null;
        return result;
    }

    public static class LocalUnitContext implements AutoCloseable {

        private final CraftingCalculation calculation;
        private final CalculationMarker marker;
        private boolean closed;

        private LocalUnitContext(CraftingCalculation calculation, CalculationMarker marker) {
            this.calculation = calculation;
            this.marker = marker;
        }

        public KeyCounter finish() {
            if (this.closed) {
                throw new IllegalStateException("Local crafting unit is already closed");
            }
            this.closed = true;
            var demands = this.calculation.exitLocalUnit();
            return demands;
        }

        public KeyCounter boundaryDemands() {
            if (this.closed || this.calculation.localBoundaryDemands == null) {
                throw new IllegalStateException("Local crafting unit is already closed");
            }
            var result = new KeyCounter();
            result.addAll(this.calculation.localBoundaryDemands);
            return result;
        }

        public CalculationDelta calculationDelta() {
            if (this.closed) throw new IllegalStateException("Local crafting unit is already closed");
            return this.calculation.createCalculationDelta(this.marker);
        }

        public void rollback() {
            if (this.closed) {
                throw new IllegalStateException("Local crafting unit is already closed");
            }
            this.closed = true;
            this.calculation.exitLocalUnit();
            this.calculation.restoreCalculationMarker(this.marker);
        }

        @Override
        public void close() {
            if (!this.closed) {
                rollback();
            }
        }
    }

    private void restoreProtectedRecursiveMissingSeeds(KeyCounter protectedSeeds) {
        protectedSeeds.forEach((entryKey, entryAmount) -> {
            long current = this.recursiveMissingSeeds.get(entryKey);
            if (current < entryAmount) {
                this.recursiveMissingSeeds.add(entryKey, entryAmount - current);
            }
        });
    }

    private void addRecursiveMissingSeedsToPlan() {
        this.recursiveMissingSeeds.forEach((entryKey, entryAmount) -> {
            if (hasRealSeededRecursiveRequestFor(entryKey) || this.realRecursiveSeeds.contains(entryKey)
                || this.realSeededRecursiveKeys.contains(entryKey)
                || this.missing.get(entryKey) >= entryAmount) {
                return;
            }
            this.missing.add(entryKey, entryAmount);
        });
    }

    private void clearResolvedRecursiveMissingItems(CraftingSimulationState inv) {
        var keys = new ObjectArrayList<AEKey>();
        this.missing.forEach((entryKey, entryAmount) -> {
            if (this.realSeededRecursiveKeys.contains(entryKey) || this.realRecursiveSeeds.contains(entryKey)
                || hasRealSeededRecursiveRequestFor(entryKey)
                || (this.recursiveMissingSeeds.get(entryKey) <= 0
                    && inv.getNetCraftedAmount(entryKey) >= entryAmount)) {
                keys.add(entryKey);
            }
        });
        for (int i = 0; i < keys.size(); i++) {
            this.missing.remove(keys.get(i));
        }
    }

    private boolean hasRealSeededRecursiveRequestFor(AEKey seed) {
        if (isReserveProtectedMissingSeed(seed)) {
            return false;
        }
        for (AEKey recursiveRoot : this.realSeededRecursiveRequests) {
            var recursiveNet = getRecursiveNet(recursiveRoot);
            if (recursiveNet != null && recursiveNet.netByKey()
                .get(seed) >= 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isReserveProtectedMissingSeed(AEKey what) {
        return this.reserveProtectedMissingSeeds != null && this.reserveProtectedMissingSeeds.get(what) > 0;
    }

    private void addRealSeededRecursiveKeys(KeyCounter netByKey) {
        netByKey.forEach((entryKey, entryAmount) -> {
            if (entryAmount >= 0) {
                if (isReserveProtectedMissingSeed(entryKey)) {
                    return;
                }
                this.realSeededRecursiveKeys.add(entryKey);
                clearRecursiveMissingSeed(entryKey);
            }
        });
    }

    private void addRecursiveReserveCandidates(RecursiveNet recursiveNet) {
        recursiveNet.netByKey()
            .forEach((entryKey, entryAmount) -> {
                if (entryAmount < 0 || isReserveProtectedMissingSeed(entryKey)) {
                    return;
                }

                long reservePerBatch = entryAmount;
                if (reservePerBatch <= 0 && recursiveNet.inputKeys()
                    .contains(entryKey)) {
                    reservePerBatch = getRecursiveSeedAmount(entryKey, recursiveNet.requestIndex());
                }
                if (reservePerBatch <= 0) {
                    return;
                }

                long existing = this.recursiveReserveCandidates.get(entryKey);
                if (existing < reservePerBatch) {
                    this.recursiveReserveCandidates.add(entryKey, reservePerBatch - existing);
                }
            });
    }

    private void expandRecursiveNetClosure(KeyCounter netByKey, @Nullable Collection<AEKey> inputKeys,
        Set<IPatternDetails> includedPatterns) {
        // Collect all currently negative keys once, then expand each until it turns positive or has no new pattern.
        // Newly negative keys introduced by an expansion are appended to the queue, so the whole net table is only
        // scanned once instead of on every closure round.
        var pending = new ObjectArrayList<AEKey>();
        var queued = new ObjectOpenHashSet<AEKey>();
        netByKey.forEach((entryKey, entryAmount) -> {
            if (entryAmount < 0 && queued.add(entryKey)) {
                pending.add(entryKey);
            }
        });

        for (int i = 0; i < pending.size(); i++) {
            var key = pending.get(i);
            while (netByKey.get(key) < 0) {
                var patterns = this.getCraftingFor(key);
                IPatternDetails selected = null;
                for (int j = 0, size = patterns.size(); j < size; j++) {
                    var pattern = patterns.get(j);
                    if (includedPatterns.add(pattern)) {
                        selected = pattern;
                        break;
                    }
                }
                if (selected == null) {
                    break;
                }
                accumulatePatternNet(netByKey, inputKeys, selected, 1);
                netByKey.forEach((entryKey, entryAmount) -> {
                    if (entryAmount < 0 && queued.add(entryKey)) {
                        pending.add(entryKey);
                    }
                });
            }
        }
    }

    private boolean expandRecursiveBatchNet(KeyCounter netByKey, AEKey target, RecursiveUse recursiveUse) {
        for (int guard = 0; guard < 128; guard++) {
            AEKey missingKey = null;
            long missingAmount = 0;
            for (var entry : netByKey) {
                if (entry.getLongValue() < 0) {
                    missingKey = entry.getKey();
                    missingAmount = LongMath.saturatedSubtract(0, entry.getLongValue());
                    break;
                }
            }

            if (missingKey == null) {
                return true;
            }

            IPatternDetails selectedPattern = null;
            long outputAmount = 0;
            var candidates = this.getCraftingFor(missingKey);
            for (int i = 0, size = candidates.size(); i < size; i++) {
                var candidate = candidates.get(i);
                outputAmount = getPatternOutputCount(candidate, missingKey);
                if (outputAmount > 0) {
                    selectedPattern = candidate;
                    break;
                }
            }

            if (selectedPattern == null) {
                return recursiveUse.get() && netByKey.get(target) > 0;
            }

            if (getPatternInputCount(selectedPattern, target) > 0) {
                recursiveUse.set();
            }
            long times = divideCeil(missingAmount, outputAmount);
            accumulatePatternNet(netByKey, selectedPattern, times);
        }

        return false;
    }

    public AEKey getOutput() {
        return output;
    }

    public KeyCounter getMissingItems() {
        return missing;
    }

    public @Nullable CraftingTreeNode getTree() {
        return tree;
    }

    public long getIntermediateFinalOutputAmount() {
        return intermediateFinalOutputAmount;
    }

    void addIntermediateFinalOutput(long amount) {
        this.intermediateFinalOutputAmount = LongMath.saturatedAdd(this.intermediateFinalOutputAmount, amount);
    }

    void addRecursiveIntermediateFinalOutput(long amount) {
        this.intermediateFinalOutputAmount = LongMath.saturatedAdd(this.intermediateFinalOutputAmount, amount);
    }

    void addRecursiveFinalOutputInput(AEKey what) {
        this.recursiveFinalOutputInputs.add(what);
    }

    boolean isRecursiveFinalOutputInput(AEKey what) {
        return this.recursiveFinalOutputInputs.contains(what);
    }

    void addIntermediateFinalOutputInput(AEKey what, long amount) {
        if (what.equals(this.output)) {
            addIntermediateFinalOutput(amount);
        }
    }

    long getIntermediateFinalOutputMarker() {
        return this.intermediateFinalOutputAmount;
    }

    void restoreIntermediateFinalOutputMarker(long marker) {
        this.intermediateFinalOutputAmount = marker;
    }

    KeyCounter getRecursiveMissingSeedsMarker() {
        var marker = new KeyCounter();
        marker.addAll(this.recursiveMissingSeeds);
        return marker;
    }

    void restoreRecursiveMissingSeedsMarker(KeyCounter marker) {
        this.recursiveMissingSeeds.clear();
        this.recursiveMissingSeeds.addAll(marker);
    }

    Set<AEKey> getRealSeededRecursiveRequestsMarker() {
        return snapshotSet(this.realSeededRecursiveRequests);
    }

    void restoreRealSeededRecursiveRequestsMarker(Set<AEKey> marker) {
        this.realSeededRecursiveRequests.clear();
        this.realSeededRecursiveRequests.addAll(marker);
    }

    void addRealSeededRecursiveRequests(Collection<AEKey> requests) {
        this.realSeededRecursiveRequests.addAll(requests);
    }

    Set<AEKey> getRealRecursiveSeedsMarker() {
        return snapshotSet(this.realRecursiveSeeds);
    }

    void restoreRealRecursiveSeedsMarker(Set<AEKey> marker) {
        this.realRecursiveSeeds.clear();
        this.realRecursiveSeeds.addAll(marker);
    }

    void addRealRecursiveSeeds(Collection<AEKey> seeds) {
        for (AEKey seed : seeds) {
            if (!isReserveProtectedMissingSeed(seed)) {
                this.realRecursiveSeeds.add(seed);
            }
        }
    }

    Set<AEKey> getRealSeededRecursiveKeysMarker() {
        return snapshotSet(this.realSeededRecursiveKeys);
    }

    void restoreRealSeededRecursiveKeysMarker(Set<AEKey> marker) {
        this.realSeededRecursiveKeys.clear();
        this.realSeededRecursiveKeys.addAll(marker);
    }

    void addRealSeededRecursiveKeys(Collection<AEKey> keys) {
        for (AEKey key : keys) {
            if (!isReserveProtectedMissingSeed(key)) {
                this.realSeededRecursiveKeys.add(key);
            }
        }
    }

    void applyRecursiveMissingSeedPreview(KeyCounter clearedSeeds, KeyCounter addedSeeds) {
        clearedSeeds.forEach((entryKey, entryAmount) -> {
            if (!isReserveProtectedMissingSeed(entryKey)) {
                clearRecursiveMissingSeed(entryKey);
            }
        });
        this.recursiveMissingSeeds.addAll(addedSeeds);
    }

    KeyCounter getMissingItemsMarker() {
        var marker = new KeyCounter();
        marker.addAll(this.missing);
        return marker;
    }

    void restoreMissingItemsMarker(KeyCounter marker) {
        this.missing.clear();
        this.missing.addAll(marker);
    }

    long getNetworkAmount(AEKey what) {
        return this.networkInv.getAvailableAmount(what);
    }

    World getLevel() {
        return this.level;
    }

    /**
     * returns true if this needs more simulation.
     *
     * @param micros microseconds of simulation
     * @return true if this needs more simulation
     */
    public boolean simulateFor(int micros) {
        synchronized (this.monitor) {
            if (this.done || this.cancelled) {
                return false;
            }

            this.watch.reset();
            this.watch.start();
            this.time = micros;
            this.running = true;

            AELog.craftingDebug("main thread is now going to sleep");

            this.monitor.notify();

            while (!this.cancelled) {
                final Runnable task = this.steppingThreadTask;
                if (task != null) {
                    task.run();
                    this.steppingThreadTask = null;
                    this.monitor.notifyAll();
                    continue;
                }
                if (!this.running) {
                    break;
                }
                try {
                    this.monitor.wait();
                } catch (InterruptedException ignored) {}
            }

            AELog.craftingDebug("main thread is now active");
        }

        return !this.done && !this.cancelled;
    }

    private void logCraftingJob(CraftingPlan plan) {
        if (AELog.isCraftingLogEnabled()) {
            StringBuilder message = new StringBuilder();
            message.append(
                "CraftingCalculation issued by %s requesting [%dx%s] breakdown:\n"
                    .formatted(this.requesterName, this.requestedAmount, this.output));
            for (int i = 0; i < this.attempts.size(); i++) {
                var attempt = this.attempts.get(i);
                message.append(
                    " - %s in %d ms\n".formatted(
                        attempt.description(),
                        attempt.stopwatch()
                            .elapsed(TimeUnit.MILLISECONDS)));
            }
            message.append(
                " - final plan: %d (%d bytes)".formatted(
                    plan.finalOutput()
                        .amount(),
                    plan.bytes()));

            AELog.crafting(message.toString());
        }
    }

    int getTreeDepth() {
        var tree = this.tree;
        return tree == null ? getMaxRequestDepth() : tree.getDepth();
    }

    long getTreeNodeCount() {
        var tree = this.tree;
        if (tree != null) {
            return tree.getNodeCount();
        }
        var graph = this.graph;
        return graph == null ? 0 : graph.getNodeCount();
    }

    long getPatternNodeCount() {
        var tree = this.tree;
        if (tree != null) {
            return tree.getPatternNodeCount();
        }
        long result = 0;
        var graph = this.graph;
        if (graph != null) {
            for (var node : graph.getAllNodes()) {
                if (node.getPattern() != null) {
                    result++;
                }
            }
        }
        return result;
    }

    int getMaxRequestDepth() {
        return this.maxRequestDepth;
    }

    public boolean isPerformanceTrackingEnabled() {
        return this.performanceLog != null;
    }

    public void recordPerformanceStage(String name, long nanos) {
        if (this.performanceLog != null) {
            this.performanceLog.stage(name, nanos);
        }
    }

    public void recordPerformanceCount(String name, long amount) {
        if (this.performanceLog != null) {
            this.performanceLog.count(name, amount);
        }
    }

    private void finishPerformanceListener(long nanos) {
        if (this.performanceLog != null) {
            this.performanceLog.finish(nanos, this);
        }
    }

    <T> T timed(String name, InterruptibleSupplier<T> supplier) throws InterruptedException {
        if (this.performanceLog == null) {
            return supplier.get();
        }
        long start = System.nanoTime();
        try {
            return supplier.get();
        } finally {
            recordPerformanceStage(name, System.nanoTime() - start);
        }
    }

    void runTimedCrafting(String name, CraftingRunnable runnable) throws InterruptedException, CraftBranchFailure {
        if (this.performanceLog == null) {
            runnable.run();
            return;
        }
        long start = System.nanoTime();
        try {
            runnable.run();
        } finally {
            recordPerformanceStage(name, System.nanoTime() - start);
        }
    }

    @FunctionalInterface
    public interface InterruptibleSupplier<T> {

        T get() throws InterruptedException;
    }

    @FunctionalInterface
    public interface CraftingRunnable {

        void run() throws InterruptedException, CraftBranchFailure;
    }

    private record CraftAttempt(String description, Stopwatch stopwatch) {}

    private record RecursiveSeed(AEKey what, long amount) {}

    private static class RecursiveNetKey {

        private AEKey what;
        private int requestIndex;
        private ObjectArrayList<CraftingTreeProcess> liveProcesses;
        private CraftingTreeProcess[] frozenProcesses;
        private int processOffset;
        private int processCount;
        private int hashCode;

        private RecursiveNetKey() {}

        private static RecursiveNetKey mutableProbe() {
            return new RecursiveNetKey();
        }

        private void reset(AEKey what, int requestIndex, ObjectArrayList<CraftingTreeProcess> processes,
            int processOffset, int processCount, int processHash) {
            this.what = what;
            this.requestIndex = requestIndex;
            this.liveProcesses = processes;
            this.processOffset = processOffset;
            this.processCount = processCount;
            this.hashCode = computeHashCode(what, requestIndex, processCount, processHash);
        }

        private RecursiveNetKey freeze(ObjectPool<RecursiveNetKey> pool) {
            var processes = new CraftingTreeProcess[this.processCount];
            for (int i = 0; i < this.processCount; i++) {
                processes[i] = processAt(i);
            }
            var key = pool.acquire();
            key.what = this.what;
            key.requestIndex = this.requestIndex;
            key.frozenProcesses = processes;
            key.liveProcesses = null;
            key.processOffset = 0;
            key.processCount = processes.length;
            key.hashCode = computeHashCode(
                this.what,
                this.requestIndex,
                this.processCount,
                getProcessIdentityHash(processes));
            return key;
        }

        private static int getProcessIdentityHash(CraftingTreeProcess[] processes) {
            int result = 0;
            for (CraftingTreeProcess process : processes) {
                result = 31 * result + System.identityHashCode(process);
            }
            return result;
        }

        private static int computeHashCode(AEKey what, int requestIndex, int processCount, int processHash) {
            int result = what.hashCode();
            result = 31 * result + requestIndex;
            result = 31 * result + processCount;
            return 31 * result + processHash;
        }

        private AEKey what() {
            return this.what;
        }

        private int requestIndex() {
            return this.requestIndex;
        }

        private int processCount() {
            return this.processCount;
        }

        private CraftingTreeProcess processAt(int index) {
            if (this.frozenProcesses != null) {
                return this.frozenProcesses[index];
            }
            return this.liveProcesses.get(this.processOffset + index);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof RecursiveNetKey other) || this.hashCode != other.hashCode
                || this.requestIndex != other.requestIndex
                || this.processCount != other.processCount
                || !this.what.equals(other.what)) {
                return false;
            }
            for (int i = 0; i < this.processCount; i++) {
                if (this.processAt(i) != other.processAt(i)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }
    }

    private record RecursiveNet(int requestIndex, KeyCounter netByKey, Collection<AEKey> inputKeys,
        boolean canResolve) {}

    private record RecursiveResolution(RecursiveSeed seed, boolean missingSeeds, RecursiveSeed missingSeed) {}

    public record CalculationMarker(KeyCounter missingItems, KeyCounter recursiveMissingSeeds,
        Set<AEKey> realSeededRecursiveRequests, Set<AEKey> realRecursiveSeeds, Set<AEKey> realSeededRecursiveKeys,
        Set<AEKey> recursiveFinalOutputInputs, KeyCounter recursiveReserveCandidates,
        long intermediateFinalOutputAmount, @Nullable KeyCounter reserveProtectedMissingSeeds, int missingSuppression,
        int recursiveMissingSeedSuppression, boolean applyingRecursiveIngredientReserve,
        boolean recursiveReserveBlockedByMissingSeed) {}

    public record CalculationDelta(KeyCounter missingItems, KeyCounter recursiveMissingSeeds,
        Set<AEKey> realSeededRecursiveRequests, Set<AEKey> realRecursiveSeeds, Set<AEKey> realSeededRecursiveKeys,
        Set<AEKey> recursiveFinalOutputInputs, KeyCounter recursiveReserveCandidates,
        long intermediateFinalOutputAmount) {}

    private record RecursiveReserveBranchMarker(CalculationMarker calculationMarker) {}

    public record RecursivePatternBatch(long rootTimes, long netOutput) {}

    private static class RecursiveUse {

        private boolean aBoolean;

        private RecursiveUse(boolean aBoolean) {
            this.aBoolean = aBoolean;
        }

        private boolean get() {
            return this.aBoolean;
        }

        private void set() {
            this.aBoolean = true;
        }
    }

    private static class ObjectPool<T> {

        private final ObjectArrayList<T> pool = new ObjectArrayList<>();
        private final Supplier<T> factory;
        private final int maxRetained;

        ObjectPool(Supplier<T> factory, int maxRetained) {
            this.factory = factory;
            this.maxRetained = maxRetained;
        }

        T acquire() {
            if (!pool.isEmpty()) {
                return pool.removeLast();
            }
            return factory.get();
        }

        void release(T obj) {
            if (pool.size() < this.maxRetained) pool.add(obj);
        }
    }

    /**
     * How many root crafts of a cycle are tried before falling back to the batch in which every craft divides evenly,
     * see {@link #getCycleBatchRuns}.
     */
    private static final long MAX_CYCLE_BATCH_SEARCH = 1024;

    /**
     * The cycle the request for an item being requested further up runs around: the items requested from that request
     * down, and the patterns of the processes between them.
     */
    @Nullable
    NoNetOutputCycle describeCycle(AEKey what) {
        int requestIndex = this.requestStack.lastIndexOf(what);
        if (requestIndex < 0) {
            return null;
        }
        var items = new ObjectArrayList<>(this.requestStack.subList(requestIndex, this.requestStack.size()));
        items.add(what);
        var patterns = new ObjectArrayList<IPatternDetails>();
        for (int i = requestIndex; i < this.processStack.size(); i++) {
            patterns.add(
                this.processStack.get(i)
                    .getDetails());
        }
        return NoNetOutputCycle.of(items, patterns);
    }

    KeyCounter getAddedRecursiveMissingSeeds(KeyCounter marker) {
        return positiveCounterDelta(marker, this.recursiveMissingSeeds);
    }

    KeyCounter getClearedRecursiveMissingSeeds(KeyCounter marker) {
        return positiveCounterDelta(this.recursiveMissingSeeds, marker);
    }

    Set<AEKey> getRealSeededRecursiveRequestsDelta(Set<AEKey> marker) {
        return setDelta(marker, this.realSeededRecursiveRequests);
    }

    Set<AEKey> getRealRecursiveSeedsDelta(Set<AEKey> marker) {
        return setDelta(marker, this.realRecursiveSeeds);
    }

    Set<AEKey> getRealSeededRecursiveKeysDelta(Set<AEKey> marker) {
        return setDelta(marker, this.realSeededRecursiveKeys);
    }

    /**
     * One craft of each process around a cycle can take more of the requested key than it makes although the cycle
     * multiplies it: with 4 stone -> 5 bricks and 1 brick -> 2 stone, one craft each takes 4 stone to make 2. Crafting
     * in the ratio the processes feed each other shows the gain: 5 brick crafts make 10 stone from the 5 bricks that
     * one stone craft makes from 4 stone.
     * <p/>
     * Returns how often each process from {@code requestIndex} up the process stack runs in the smallest batch, by root
     * crafts, that makes at least as much of {@code what} as it takes, or null if the cycle can never net produce it.
     */
    @Nullable
    private long[] getCycleBatchRuns(AEKey what, int requestIndex) {
        int count = this.processStack.size() - requestIndex;
        if (count <= 0 || !what.equals(
            this.processStack.get(requestIndex)
                .getRequestedKey())) {
            return null;
        }

        // Process i crafts for its node from what process i + 1 crafts; the last one takes what itself. The keys come
        // from the processes: not every request in between is on the request stack.
        long[] made = new long[count];
        long[] taken = new long[count];
        var madeProduct = BigInteger.ONE;
        var takenProduct = BigInteger.ONE;
        for (int i = 0; i < count; i++) {
            var process = this.processStack.get(requestIndex + i);
            var input = i + 1 < count ? this.processStack.get(requestIndex + i + 1)
                .getRequestedKey() : what;
            var output = process.getRequestedKey();
            if (input == null || output == null) {
                return null;
            }
            made[i] = process.getOutputCount(output);
            taken[i] = process.getInputCount(input);
            if (made[i] <= 0 || taken[i] <= 0) {
                return null;
            }
            madeProduct = madeProduct.multiply(BigInteger.valueOf(made[i]));
            takenProduct = takenProduct.multiply(BigInteger.valueOf(taken[i]));
        }
        if (madeProduct.compareTo(takenProduct) <= 0) {
            return null;
        }

        // With this many root crafts every process divides evenly into the next, so the gain is exact.
        long evenRootRuns = 1;
        for (int i = 1; i < count; i++) {
            evenRootRuns = LongMath.saturatedMultiply(evenRootRuns, made[i]);
        }
        long[] runs = new long[count];
        long searched = Math.min(evenRootRuns, MAX_CYCLE_BATCH_SEARCH);
        for (long rootRuns = 1; rootRuns <= searched; rootRuns++) {
            if (fillCycleBatchRuns(what, requestIndex, made, taken, rootRuns, runs)) {
                return runs;
            }
        }
        if (evenRootRuns > searched && evenRootRuns < Long.MAX_VALUE
            && fillCycleBatchRuns(what, requestIndex, made, taken, evenRootRuns, runs)) {
            return runs;
        }
        return null;
    }

    private boolean fillCycleBatchRuns(AEKey what, int requestIndex, long[] made, long[] taken, long rootRuns,
        long[] runs) {
        runs[0] = rootRuns;
        for (int i = 1; i < runs.length; i++) {
            long needed = LongMath.saturatedMultiply(runs[i - 1], taken[i - 1]);
            runs[i] = needed / made[i] + (needed % made[i] == 0 ? 0 : 1);
        }

        long net = 0;
        for (int i = 0; i < runs.length; i++) {
            if (runs[i] == Long.MAX_VALUE) {
                return false;
            }
            var process = this.processStack.get(requestIndex + i);
            net = LongMath.saturatedAdd(net, LongMath.saturatedMultiply(runs[i], process.getOutputCount(what)));
            net = LongMath.saturatedSubtract(net, LongMath.saturatedMultiply(runs[i], process.getInputCount(what)));
        }
        return net >= 0;
    }

    private static boolean hasNativeRing(CraftingGraph graph) {
        var topology = graph.getTopology();
        if (!topology.isCondensed()) return false;
        for (var component : topology.getComponents()) {
            var sccPlan = component.cyclic() ? graph.getSccPlan(component.id()) : null;
            if (sccPlan != null && sccPlan.ring()) return true;
        }
        return false;
    }

    /**
     * Whether a cycle of several patterns is more than a ring the graph plans natively, see
     * DemandPropagation#ringEdges.
     */
    private static boolean hasUnsolvableCycle(CraftingGraph graph) {
        var topology = graph.getTopology();
        if (topology.isCondensed()) {
            for (var component : topology.getComponents()) {
                if (component.cyclic() && component.nodes()
                    .size() > 1 && DemandPropagation.ringEdges(topology, component) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * How much of a crafting graph's crafted final output its crafts take back.
     */
    public void addGraphIntermediateFinalOutput(long amount) {
        addIntermediateFinalOutput(amount);
    }

    /**
     * How much of each recursive ingredient taken from storage the plan should leave there, in crafts of what takes it.
     */
    public long getRecursiveIngredientReserveAmount() {
        return this.recursiveIngredientReserveAmount;
    }

    private static <T> Set<T> snapshotSet(Set<T> source) {
        // Backtracking mutates the source. Non-empty markers must remain detached; empty ones need no allocation.
        return source.isEmpty() ? Set.of() : Collections.unmodifiableSet(new ObjectOpenHashSet<>(source));
    }

    private static long totalAmount(KeyCounter counter) {
        long total = 0;
        for (var entry : counter) {
            total = LongMath.saturatedAdd(total, entry.getLongValue());
        }
        return total;
    }
}
