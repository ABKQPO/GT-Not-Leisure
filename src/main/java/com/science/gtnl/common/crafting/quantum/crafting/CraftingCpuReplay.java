package com.science.gtnl.common.crafting.quantum.crafting;

import java.util.List;
import java.util.RandomAccess;

import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey2LongMap;
import com.science.gtnl.common.crafting.quantum.api.stacks.GenericStack;
import com.science.gtnl.common.crafting.quantum.api.stacks.KeyCounter;
import com.science.gtnl.common.crafting.quantum.util.LongMath;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

/**
 * Replays a plan the way a crafting CPU runs it. The CPU starts with the used, emitted and missing items of the plan
 * and visits its tasks in turn, pushing one craft per visit whose inputs it holds. An input takes its possible stacks,
 * and the keys of their fuzzy groups the input accepts, in order until it has enough. What a crafting pattern leaves
 * behind is the container its input gives, see {@link IPatternDetails.IInput#getRemainingKey}.
 */
final class CraftingCpuReplay {

    private static final int MAX_PASSES = 1_000_000;

    private final World level;
    private final CraftingCalculation calculation;
    private final AEKey2LongMap inventory = new AEKey2LongMap.OpenHashMap();
    // The remaining tasks in the order the first pass visited them
    private final ObjectArrayList<Task> tasks;
    private final Reference2ObjectOpenHashMap<IPatternDetails.IInput, ObjectArrayList<GenericStack>> candidates = new Reference2ObjectOpenHashMap<>();
    private AEKey2LongMap passDelta;
    private AEKey2LongMap passTaken;
    // A Taken value is consumed before the next takeOnce call. Reuse its maps across replay passes.
    private AEKey2LongMap takenKeys;
    private AEKey2LongMap takenContainers;
    // Every key the replay can hold, by fuzzy group, and the candidates of each input among them
    private final Object2ObjectOpenHashMap<Object, ObjectArrayList<AEKey>> keysByGroup = new Object2ObjectOpenHashMap<>();
    private Taken taken;

    private CraftingCpuReplay(CraftingCalculation calculation, CraftingPlan plan, KeyCounter extra) {
        this.calculation = calculation;
        this.level = calculation.getLevel();
        plan.usedItems()
            .forEach(this.inventory::addTo);
        plan.emittedItems()
            .forEach(this.inventory::addTo);
        plan.missingItems()
            .forEach(this.inventory::addTo);
        extra.forEach(this.inventory::addTo);
        var taskMap = new Object2ObjectOpenHashMap<IPatternDetails, Task>();
        for (var entry : plan.patternTimes()
            .object2LongEntrySet()) {
            taskMap.put(entry.getKey(), new Task(entry.getKey(), entry.getLongValue()));
        }
        this.tasks = new ObjectArrayList<>(taskMap.values());
        for (var key : this.inventory.keySet()) {
            index(key);
        }
        for (int i = 0; i < this.tasks.size(); i++) {
            var task = this.tasks.get(i);
            for (int outputIndex = 0; outputIndex < task.outputs.size(); outputIndex++) {
                index(
                    task.outputs.get(outputIndex)
                        .what());
            }
            for (var input : task.inputs) {
                for (var possible : input.possibleInputs()) {
                    index(possible.what());
                    var container = input.getRemainingKey(possible.what());
                    if (container != null) {
                        index(container);
                    }
                }
            }
        }
    }

    /**
     * @return whether the CPU runs every craft of the plan and ends up with the requested output
     */
    static boolean runs(CraftingPlan plan, CraftingCalculation calculation) throws InterruptedException {
        calculation.handlePausing();
        var replay = new CraftingCpuReplay(calculation, plan, new KeyCounter());
        if (replay.orderFree()) {
            return replay.balances(plan);
        }
        return replay.run() && replay.coversOutput(plan);
    }

    /**
     * The items the CPU would have to be given on top of the plan to run it, found by running the plan and, whenever
     * no craft can run, adding everything the stuck crafts lack. Null when the replay gives up.
     */
    static @Nullable KeyCounter lacking(CraftingPlan plan, CraftingCalculation calculation)
        throws InterruptedException {
        var extra = new KeyCounter();
        for (int attempt = 0; attempt < 1024; attempt++) {
            calculation.handlePausing();
            var replay = new CraftingCpuReplay(calculation, plan, extra);
            if (replay.run()) {
                var output = plan.finalOutput();
                long shortOfOutput = output.amount() - replay.inventory.getLong(output.what());
                if (shortOfOutput <= 0) {
                    return extra;
                }
                // Crafts may take some of the output given to them as well, so check again
                extra.add(output.what(), shortOfOutput);
                continue;
            }
            var shortages = replay.shortages();
            if (shortages.isEmpty()) {
                return null;
            }
            extra.addAll(shortages);
        }
        return null;
    }

    private boolean coversOutput(CraftingPlan plan) {
        var output = plan.finalOutput();
        return this.inventory.getLong(output.what()) >= output.amount();
    }

    // Depth-first search from a craft to the producers of its inputs
    private static boolean hasCycle(IPatternDetails pattern,
        Object2ObjectOpenHashMap<AEKey, ObjectArrayList<IPatternDetails>> producers,
        ReferenceOpenHashSet<IPatternDetails> done, ReferenceOpenHashSet<IPatternDetails> onPath) {
        if (done.contains(pattern)) return false;
        if (!onPath.add(pattern)) return true;
        for (var input : pattern.getInputs()) {
            var possible = input.possibleInputs();
            if (possible.length == 0) continue;
            var makers = producers.get(possible[0].what());
            if (makers == null) continue;
            for (int i = 0; i < makers.size(); i++) {
                if (hasCycle(makers.get(i), producers, done, onPath)) return true;
            }
        }
        onPath.remove(pattern);
        done.add(pattern);
        return false;
    }

    private void index(AEKey key) {
        var group = this.keysByGroup.computeIfAbsent(key.getPrimaryKey(), g -> new ObjectArrayList<>(1));
        if (!group.contains(key)) {
            group.add(key);
        }
    }

    /*
     * Without a cycle among the crafts, inputs with a choice of stacks, or containers, no craft can keep another from
     * ever running: every key is made by crafts that do not wait for its consumers, and what one consumer takes is not
     * what another needs instead. Whether the plan runs then only depends on its totals.
     */
    private boolean orderFree() {
        for (int i = 0; i < this.tasks.size(); i++) {
            var task = this.tasks.get(i);
            if (task.pattern.canSubstitute()) return false;
            for (var input : task.inputs) {
                var possible = input.possibleInputs();
                if (possible.length > 1 || candidates(input).size() > 1) return false;
                if (possible.length == 1 && input.getRemainingKey(possible[0].what()) != null) return false;
            }
        }
        var producers = new Object2ObjectOpenHashMap<AEKey, ObjectArrayList<IPatternDetails>>(this.tasks.size());
        for (int i = 0; i < this.tasks.size(); i++) {
            var task = this.tasks.get(i);
            for (int outputIndex = 0; outputIndex < task.outputs.size(); outputIndex++) {
                producers.computeIfAbsent(
                    task.outputs.get(outputIndex)
                        .what(),
                    k -> new ObjectArrayList<>(1))
                    .add(task.pattern);
            }
        }
        var done = new ReferenceOpenHashSet<IPatternDetails>(this.tasks.size());
        var onPath = new ReferenceOpenHashSet<IPatternDetails>();
        for (int i = 0; i < this.tasks.size(); i++) {
            if (hasCycle(this.tasks.get(i).pattern, producers, done, onPath)) return false;
        }
        return true;
    }

    private boolean balances(CraftingPlan plan) {
        // This is the terminal totals-only path; the replay is discarded after returning the result.
        var balance = this.inventory;
        for (int i = 0; i < this.tasks.size(); i++) {
            var task = this.tasks.get(i);
            long times = task.remaining;
            for (var input : task.inputs) {
                var possible = input.possibleInputs();
                if (possible.length == 0) continue;
                balance.addTo(
                    possible[0].what(),
                    -LongMath.saturatedMultiply(
                        LongMath.saturatedMultiply(possible[0].amount(), input.getMultiplier()),
                        times));
            }
            for (int outputIndex = 0; outputIndex < task.outputs.size(); outputIndex++) {
                var output = task.outputs.get(outputIndex);
                balance.addTo(output.what(), LongMath.saturatedMultiply(output.amount(), times));
            }
        }
        var values = balance.values()
            .iterator();
        while (values.hasNext()) {
            if (values.nextLong() < 0) return false;
        }
        var output = plan.finalOutput();
        return balance.getLong(output.what()) >= output.amount();
    }

    /*
     * Each pass visits every task once and pushes one craft of it if its inputs are there. A pass the CPU just ran the
     * same way twice runs the same way again as long as what it takes stays there and no idle task gets what it waits
     * for, so it is repeated at once that often: a catalyst or container passed between crafts costs a few passes, not
     * one per craft.
     */
    private boolean run() throws InterruptedException {
        this.passDelta = new AEKey2LongMap.OpenHashMap(this.tasks.size());
        this.passTaken = new AEKey2LongMap.OpenHashMap(this.tasks.size());
        this.takenKeys = new AEKey2LongMap.OpenHashMap(4);
        this.takenContainers = new AEKey2LongMap.OpenHashMap(1);
        this.taken = new Taken(this.takenKeys, this.takenContainers);
        var crafted = new ObjectArrayList<Task>(this.tasks.size());
        var previousCrafted = new ObjectArrayList<Task>(this.tasks.size());
        var previousDelta = new AEKey2LongMap.OpenHashMap(this.tasks.size());
        var idle = new ObjectArrayList<Task>();
        for (int pass = 0; pass < MAX_PASSES; pass++) {
            crafted.clear();
            idle.clear();
            this.passDelta.clear();
            this.passTaken.clear();
            boolean exact = true;
            for (int taskIndex = 0; taskIndex < this.tasks.size(); taskIndex++) {
                this.calculation.handlePausing();
                var task = this.tasks.get(taskIndex);
                if (task.remaining <= 0) continue;
                var taken = takeOnce(task);
                if (taken == null) {
                    idle.add(task);
                    continue;
                }
                apply(task, taken);
                task.remaining--;
                crafted.add(task);
                exact &= taken.exact;
            }
            if (crafted.isEmpty()) {
                return idle.isEmpty();
            }
            if (exact && crafted.equals(previousCrafted) && this.passDelta.equals(previousDelta)) {
                repeatPass(crafted, idle);
            }
            previousCrafted.clear();
            previousCrafted.addAll(crafted);
            previousDelta.clear();
            previousDelta.putAll(this.passDelta);
            int remaining = 0;
            for (int i = 0; i < this.tasks.size(); i++) {
                var task = this.tasks.get(i);
                if (task.remaining > 0) this.tasks.set(remaining++, task);
            }
            this.tasks.size(remaining);
        }
        return false;
    }

    private void repeatPass(ObjectArrayList<Task> crafted, ObjectArrayList<Task> idle) {
        // A task visited before the crafts that made its inputs waits for the next pass, which then runs differently
        for (int taskIndex = 0; taskIndex < idle.size(); taskIndex++) {
            if (takeOnce(idle.get(taskIndex)) != null) {
                return;
            }
        }
        long repeats = Long.MAX_VALUE;
        for (int taskIndex = 0; taskIndex < crafted.size(); taskIndex++) {
            repeats = Math.min(repeats, crafted.get(taskIndex).remaining);
        }
        for (var entry : this.passDelta.object2LongEntrySet()) {
            long delta = entry.getLongValue();
            if (delta >= 0) continue;
            // Every repeat starts with at least what the pass took, so each of its crafts finds its inputs
            long spare = this.inventory.getLong(entry.getKey()) - this.passTaken.getLong(entry.getKey());
            repeats = Math.min(repeats, spare < 0 ? 0 : spare / -delta);
        }
        // An idle task must stay idle: stop before any of its short inputs could be covered
        for (int taskIndex = 0; taskIndex < idle.size(); taskIndex++) {
            var task = idle.get(taskIndex);
            for (var input : task.inputs) {
                var possible = input.possibleInputs();
                if (possible.length == 0) continue;
                long held = 0;
                long gained = 0;
                var options = candidates(input);
                for (int candidateIndex = 0; candidateIndex < options.size(); candidateIndex++) {
                    var candidate = options.get(candidateIndex);
                    held = LongMath.saturatedAdd(held, Math.max(0, this.inventory.getLong(candidate.what())));
                    gained = LongMath.saturatedAdd(gained, Math.max(0, this.passDelta.getLong(candidate.what())));
                }
                long need = LongMath.saturatedMultiply(possible[0].amount(), input.getMultiplier());
                if (gained > 0 && held < need) {
                    repeats = Math.min(repeats, (need - 1 - held) / gained);
                }
            }
        }
        if (repeats <= 0 || repeats == Long.MAX_VALUE) {
            return;
        }
        for (int taskIndex = 0; taskIndex < crafted.size(); taskIndex++) {
            crafted.get(taskIndex).remaining -= repeats;
        }
        for (var entry : this.passDelta.object2LongEntrySet()) {
            this.inventory.addTo(entry.getKey(), LongMath.saturatedMultiply(entry.getLongValue(), repeats));
        }
    }

    private void apply(Task task, Taken taken) {
        taken.keys.forEachEntry(this::applyTaken);
        taken.containers.forEachEntry(this::applyContainer);
        for (int i = 0; i < task.outputs.size(); i++) {
            var output = task.outputs.get(i);
            this.inventory.addTo(output.what(), output.amount());
            this.passDelta.addTo(output.what(), output.amount());
        }
    }

    private void applyTaken(AEKey key, long amount) {
        this.inventory.addTo(key, -amount);
        this.passDelta.addTo(key, -amount);
        this.passTaken.addTo(key, amount);
    }

    private void applyContainer(AEKey key, long amount) {
        this.inventory.addTo(key, amount);
        this.passDelta.addTo(key, amount);
    }

    // What one craft takes from the current inventory, or null when it cannot run
    private @Nullable Taken takeOnce(Task task) {
        var keys = this.takenKeys;
        var containers = this.takenContainers;
        keys.clear();
        containers.clear();
        boolean exact = true;
        for (var input : task.inputs) {
            if (input.possibleInputs().length == 0) continue;
            // Counted in templates, each one stack of its candidate
            long need = input.getMultiplier();
            var first = input.possibleInputs()[0].what();
            var options = candidates(input);
            for (int candidateIndex = 0; candidateIndex < options.size(); candidateIndex++) {
                var candidate = options.get(candidateIndex);
                long per = candidate.amount();
                long available = this.inventory.getLong(candidate.what()) - keys.getLong(candidate.what());
                long templates = Math.min(Math.max(0, available) / per, need);
                if (templates <= 0) continue;
                if (candidate.what() != first || templates < need) {
                    exact = false;
                }
                keys.addTo(candidate.what(), templates * per);
                var container = input.getRemainingKey(candidate.what());
                if (container != null) {
                    containers.addTo(container, templates);
                }
                need -= templates;
                if (need <= 0) break;
            }
            if (need > 0) {
                return null;
            }
        }
        this.taken.exact = exact;
        return this.taken;
    }

    // What each planned craft that cannot run lacks for one craft, on the first possible stack of each short input
    private KeyCounter shortages() {
        var result = new KeyCounter();
        var reserved = this.takenKeys;
        for (int i = 0; i < this.tasks.size(); i++) {
            var task = this.tasks.get(i);
            if (task.remaining <= 0) continue;
            reserved.clear();
            for (var input : task.inputs) {
                var possible = input.possibleInputs();
                if (possible.length == 0) continue;
                long need = input.getMultiplier();
                var options = candidates(input);
                for (int candidateIndex = 0; candidateIndex < options.size(); candidateIndex++) {
                    var candidate = options.get(candidateIndex);
                    long available = this.inventory.getLong(candidate.what()) - reserved.getLong(candidate.what());
                    long templates = Math.min(Math.max(0, available) / candidate.amount(), need);
                    if (templates > 0) {
                        reserved.addTo(candidate.what(), templates * candidate.amount());
                        need -= templates;
                    }
                    if (need <= 0) break;
                }
                if (need > 0) {
                    result.add(possible[0].what(), LongMath.saturatedMultiply(need, possible[0].amount()));
                }
            }
        }
        return result;
    }

    // The possible stacks of an input in order, each followed by the other keys of its fuzzy group it accepts
    private ObjectArrayList<GenericStack> candidates(IPatternDetails.IInput input) {
        var result = this.candidates.get(input);
        if (result == null) {
            result = new ObjectArrayList<>(input.possibleInputs().length);
            for (var possible : input.possibleInputs()) {
                result.add(possible);
                var group = this.keysByGroup.get(
                    possible.what()
                        .getPrimaryKey());
                if (group == null) continue;
                for (int keyIndex = 0; keyIndex < group.size(); keyIndex++) {
                    var key = group.get(keyIndex);
                    if (key != possible.what() && input.isValid(key, this.level)) {
                        result.add(new GenericStack(key, possible.amount()));
                    }
                }
            }
            this.candidates.put(input, result);
        }
        return result;
    }

    /**
     * What one craft takes. Exact when every input took its first possible stack alone, so the next craft takes the
     * same as long as that stack lasts; otherwise an input that moved on to a later stack may come back to an earlier
     * one as soon as a craft makes it.
     */
    private static final class Taken {

        final AEKey2LongMap keys;
        final AEKey2LongMap containers;
        boolean exact;

        Taken(AEKey2LongMap keys, AEKey2LongMap containers) {
            this.keys = keys;
            this.containers = containers;
        }
    }

    private static final class Task {

        final IPatternDetails pattern;
        final IPatternDetails.IInput[] inputs;
        final List<GenericStack> outputs;
        long remaining;

        Task(IPatternDetails pattern, long remaining) {
            this.pattern = pattern;
            this.inputs = pattern.getInputs();
            var outputs = pattern.getOutputs();
            this.outputs = outputs instanceof RandomAccess ? outputs : new ObjectArrayList<>(outputs);
            this.remaining = remaining;
        }
    }
}
