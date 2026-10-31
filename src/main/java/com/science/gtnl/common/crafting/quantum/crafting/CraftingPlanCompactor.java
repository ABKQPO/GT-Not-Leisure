package com.science.gtnl.common.crafting.quantum.crafting;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.gtnllib.utils.LongMath;
import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.GenericStack;
import com.science.gtnl.common.crafting.quantum.api.stacks.KeyCounter;
import com.science.gtnl.common.crafting.quantum.core.AELog;
import com.science.gtnl.common.crafting.quantum.core.localization.PlayerMessages;

import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntHeapPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;

public class CraftingPlanCompactor {

    private static final int MAX_STEPS = 1_000_000;
    private static final int MAX_REPLAY_ROUNDS = 100_000;
    private static final int MAX_ALLOCATION_ROUNDS = 4096;
    private static final int MAX_CHAIN = 1024;
    private static final int MAX_BORROWS = 4096;
    private static final int MAX_OUTPUT_STOCK_ATTEMPTS = 8;
    private static final int PAUSE_INTERVAL = 1024;
    private static final int UNKNOWN = -2;

    private final CraftingCalculation calculation;
    @Nullable
    private final World level;
    private final List<Recipe> recipes = new ObjectArrayList<>();
    private final Object2IntOpenHashMap<AEKey> keyIndex = new Object2IntOpenHashMap<>();
    private final List<AEKey> keys = new ObjectArrayList<>();
    private final List<List<Recipe>> producers = new ObjectArrayList<>();
    private final Map<Object, IntArrayList> groups = new Object2ObjectOpenHashMap<>();
    private boolean flexible;
    private boolean planBalanced = true;
    private int steps;
    private long[] balance = new long[0];
    private long[] shortfall = new long[0];
    private int[] rank = new int[0];
    private long[] stock = new long[0];

    private CraftingPlanCompactor(CraftingCalculation calculation) {
        this.calculation = calculation;
        this.level = calculation.getLevel();
        this.keyIndex.defaultReturnValue(-1);
    }

    public static CraftingPlan repair(CraftingPlan plan, CraftingCalculation calculation) throws InterruptedException {
        if (plan.patternTimes()
            .isEmpty()) {
            return compact(plan, calculation);
        }
        if (CraftingCpuReplay.runs(plan, calculation)) {
            return plan;
        }

        CraftingPlan repaired;
        try {
            repaired = compact(plan, calculation);
        } catch (CraftingCalculationFailure | ArithmeticException unbalanced) {
            repaired = plan;
        }
        if (repaired != plan && CraftingCpuReplay.runs(repaired, calculation)) {
            return repaired;
        }

        try {
            final CraftingPlan inputChoices = new CraftingPlanCompactor(calculation).repairInputChoices(plan);
            if (inputChoices != null && CraftingCpuReplay.runs(inputChoices, calculation)) {
                return inputChoices;
            }
        } catch (ArithmeticException overflow) {
            // A conservative input bound may overflow even when the general solver can find a valid plan.
        }

        final KeyCounter lacking = CraftingCpuReplay.lacking(repaired, calculation);
        if (lacking == null) {
            return keepOrFail(repaired, false);
        }
        if (lacking.isEmpty()) {
            return repaired;
        }
        final KeyCounter missing = new KeyCounter();
        missing.addAll(repaired.missingItems());
        missing.addAll(lacking);
        return repaired.withMissing(missing);
    }

    private @Nullable CraftingPlan repairInputChoices(CraftingPlan plan) throws InterruptedException {
        for (Object2LongMap.Entry<IPatternDetails> entry : plan.patternTimes()
            .object2LongEntrySet()) {
            if (entry.getLongValue() <= 0) continue;
            final Recipe recipe = Recipe.of(entry.getKey(), entry.getLongValue(), this);
            if (recipe == null) return null;
            this.recipes.add(recipe);
            this.flexible |= recipe.slots.length > 0;
        }
        if (!this.flexible) return null;
        final int finalKey = this.keyIndex.getInt(
            plan.finalOutput()
                .what());
        if (finalKey < 0 || this.producers.get(finalKey)
            .size() != 1) return null;

        this.stock = new long[this.keys.size()];
        for (int key = 0; key < this.stock.length; key++) {
            this.stock[key] = key == finalKey ? plan.usedItems()
                .get(this.keys.get(key))
                : Math.max(
                    plan.usedItems()
                        .get(this.keys.get(key)),
                    this.calculation.getNetworkAmount(this.keys.get(key)));
        }
        final long[] available = this.stock.clone();
        addAll(available, plan.emittedItems());
        final List<Int2LongLinkedOpenHashMap> inputs = new ObjectArrayList<>(this.recipes.size());
        final List<IntArrayList> dependencies = new ObjectArrayList<>(this.recipes.size());
        final int[] consumers = new int[this.recipes.size()];

        for (Recipe recipe : this.recipes) {
            this.calculation.handlePausing();
            if (recipe.pattern.canSubstitute()) return null;
            for (int i = 0; i < recipe.keys.length; i++) {
                if (recipe.in[i] > 0 && recipe.out[i] > 0) return null;
            }
            final Int2LongLinkedOpenHashMap bound = new Int2LongLinkedOpenHashMap();
            for (IPatternDetails.IInput input : recipe.pattern.getInputs()) {
                final GenericStack[] possible = input.possibleInputs();
                if (possible.length == 0) return null;
                final IntOpenHashSet included = new IntOpenHashSet(possible.length);
                for (GenericStack variant : possible) {
                    if (input.getRemainingKey(variant.what()) != null) return null;
                    final int key = this.keyIndex.getInt(variant.what());
                    if (possible.length == 1 || available[key] > 0
                        || !this.producers.get(key)
                            .isEmpty()
                        || plan.missingItems()
                            .get(variant.what()) > 0) {
                        included.add(key);
                    }
                }
                if (included.isEmpty()) included.add(this.keyIndex.getInt(possible[0].what()));
                final long amount = Math.multiplyExact(possible[0].amount(), input.getMultiplier());
                for (int key : included) bound.put(key, Math.addExact(bound.get(key), amount));
            }
            inputs.add(bound);

            final IntArrayList before = new IntArrayList();
            final IntOpenHashSet seen = new IntOpenHashSet(bound.size());
            for (int key : bound.keySet()) {
                final List<Recipe> makers = this.producers.get(key);
                if (makers.size() > 1) return null;
                if (!makers.isEmpty()) {
                    final int producer = makers.get(0).index;
                    if (seen.add(producer)) {
                        before.add(producer);
                        consumers[producer]++;
                    }
                }
            }
            dependencies.add(before);
        }

        final IntArrayList order = new IntArrayList();
        for (int i = 0; i < consumers.length; i++) {
            if (consumers[i] == 0) order.add(i);
        }
        final long[] demand = new long[this.keys.size()];
        final long[] produced = new long[this.keys.size()];
        final long[] times = new long[this.recipes.size()];
        demand[finalKey] = plan.finalOutput()
            .amount();
        for (int next = 0; next < order.size(); next++) {
            this.calculation.handlePausing();
            final int index = order.getInt(next);
            final Recipe recipe = this.recipes.get(index);
            long crafts = recipe.limit;
            for (int i = 0; i < recipe.keys.length; i++) {
                final int key = recipe.keys[i];
                if (recipe.out[i] > 0 && demand[key] > available[key]) {
                    crafts = Math.max(crafts, ceilDiv(demand[key] - available[key], recipe.out[i]));
                }
            }
            times[index] = crafts;
            for (int i = 0; i < recipe.keys.length; i++) {
                final int key = recipe.keys[i];
                produced[key] = Math.addExact(produced[key], Math.multiplyExact(recipe.out[i], crafts));
            }
            for (Int2LongMap.Entry input : inputs.get(index)
                .int2LongEntrySet()) {
                final int key = input.getIntKey();
                demand[key] = Math.addExact(demand[key], Math.multiplyExact(input.getLongValue(), crafts));
            }
            for (int producer : dependencies.get(index)) {
                if (--consumers[producer] == 0) order.add(producer);
            }
        }
        if (order.size() != this.recipes.size()) return null;

        final long[] taken = new long[this.keys.size()];
        for (int key = 0; key < taken.length; key++) taken[key] = Math.max(0, demand[key] - produced[key]);
        return rebuild(plan, times, taken);
    }

    public static CraftingPlan compact(CraftingPlan plan, CraftingCalculation calculation) throws InterruptedException {
        if (plan.patternTimes()
            .isEmpty()) {
            // The request is never taken from storage, so without crafts only an emitter makes it, or it is missing
            final AEKey output = plan.finalOutput()
                .what();
            final long lacking = plan.finalOutput()
                .amount()
                - plan.emittedItems()
                    .get(output)
                - plan.missingItems()
                    .get(output);
            return lacking <= 0 ? plan : withMissing(plan, output, lacking);
        }
        final CraftingPlanCompactor compactor = new CraftingPlanCompactor(calculation);
        try {
            final CraftingPlan compacted = compactor.run(plan);
            // Balanced as a whole, a catalyst the plan crafted looks like surplus, yet the first craft needs it
            if (!compactor.flexible && total(compacted.missingItems()) > total(plan.missingItems())
                && compactor.runsAsIs(plan)) {
                return plan;
            }
            return compacted;
        } catch (ArithmeticException overflow) {
            // Raising crafts without limit overflows on a cycle that consumes more than it makes
            try {
                return compactor.repairAsIs(plan);
            } catch (ArithmeticException again) {
                return keepOrFail(plan, compactor.planBalanced);
            }
        }
    }

    private CraftingPlan run(CraftingPlan plan) throws InterruptedException {
        for (Object2LongMap.Entry<IPatternDetails> entry : plan.patternTimes()
            .object2LongEntrySet()) {
            if (entry.getLongValue() <= 0) continue;
            final Recipe recipe = Recipe.of(entry.getKey(), entry.getLongValue(), this);
            if (recipe == null) {
                return plan;
            }
            this.recipes.add(recipe);
            this.flexible |= recipe.slots.length > 0;
        }
        final int finalKey = this.keyIndex.getInt(
            plan.finalOutput()
                .what());
        if (finalKey < 0) {
            return plan;
        }
        for (int key = 0; key < this.producers.size(); key++) {
            final AEKey what = this.keys.get(key);
            // A pattern made for the key comes before those that only make it on the side
            this.producers.get(key)
                .sort((a, b) -> Boolean.compare(!a.makesFirst(what), !b.makesFirst(what)));
        }
        this.rank = rankFrom(finalKey);

        // Everything the network held, not just what the plan took: the legacy tree takes whole crafts of each
        // variant and may report items missing that a different split covers. The requested output itself is never
        // taken from storage, see CraftingTreeNode#isTopLevelRequestedOutput.
        this.stock = new long[this.keys.size()];
        final long[] planUsed = new long[this.keys.size()];
        addAll(planUsed, plan.usedItems());
        for (int key = 0; key < this.stock.length; key++) {
            this.stock[key] = key == finalKey ? planUsed[key]
                : Math.max(planUsed[key], this.calculation.getNetworkAmount(this.keys.get(key)));
        }
        final long[] available = this.stock.clone();
        addAll(available, plan.emittedItems());
        final long[] initial = available.clone();
        initial[finalKey] = Math.subtractExact(
            initial[finalKey],
            plan.finalOutput()
                .amount());

        final long[] limits = new long[this.recipes.size()];
        for (Recipe recipe : this.recipes) {
            limits[recipe.index] = recipe.limit;
        }
        // Without variants a plan can be checked for balance as it is; with them, which variant each craft took is
        // not part of the plan
        if (!this.flexible) {
            final long[] planBalance = initial.clone();
            addAll(planBalance, plan.missingItems());
            for (Recipe recipe : this.recipes) {
                recipe.apply(planBalance, recipe.limit);
            }
            for (long value : planBalance) {
                if (value < 0) {
                    this.planBalanced = false;
                    break;
                }
            }
        }

        long[] times = null;
        // The stored requested output may only serve crafts consuming it: as long as more of it is stored than the
        // crafts consume, the rest went to the request, so solve again with less of it
        for (int attempt = 0; attempt < MAX_OUTPUT_STOCK_ATTEMPTS; attempt++) {
            // Below the crafts of the plan first. They bound the crafts from what the plan took while the fill starts
            // from what the network held, and the legacy tree may miscount the pattern closing a cycle, so without
            // that bound next.
            times = fill(initial, limits);
            if (times == null) {
                final long[] unlimited = new long[limits.length];
                Arrays.fill(unlimited, Long.MAX_VALUE);
                times = fill(initial, unlimited);
            }
            if (times == null) break;
            final long consumed = consumedOf(finalKey, times);
            if (this.stock[finalKey] <= consumed) break;
            final long excess = this.stock[finalKey] - consumed;
            this.stock[finalKey] = consumed;
            available[finalKey] -= excess;
            initial[finalKey] -= excess;
        }
        if (times == null) {
            return repairAsIs(plan);
        }

        // What nothing could make is missing, and the replay starts with it like the CPU does in missing mode. Even
        // crafts that are already the fewest are replayed: a catalyst nothing holds balances out, yet the first craft
        // needs one.
        final long[] grownSupply = Arrays.copyOf(available, this.keys.size());
        for (int key = 0; key < grownSupply.length; key++) {
            grownSupply[key] = Math.addExact(grownSupply[key], this.shortfall[key]);
        }
        final long[] demand = new long[this.keys.size()];
        demand[finalKey] = plan.finalOutput()
            .amount();
        final long[] borrowed = new long[grownSupply.length];
        if (replay(times, grownSupply, demand, borrowed) == null) {
            return repairAsIs(plan);
        }
        // Seeds the replay had to borrow are there from the start, like the missing items the CPU is given
        for (int key = 0; key < grownSupply.length; key++) {
            grownSupply[key] = Math.addExact(grownSupply[key], borrowed[key]);
        }
        final long[] lowest = replay(times, grownSupply, demand);
        if (lowest == null) {
            return repairAsIs(plan);
        }
        long[] taken = new long[grownSupply.length];
        for (int key = 0; key < grownSupply.length; key++) {
            taken[key] = grownSupply[key] - lowest[key];
        }
        if (replay(times, taken, demand) == null) {
            // Another order may need more than the first replay borrowed, keep the original amounts
            taken = grownSupply;
        }
        return rebuild(plan, times, taken);
    }

    private long consumedOf(int key, long[] times) {
        long consumed = 0;
        for (Recipe recipe : this.recipes) {
            for (int i = 0; i < recipe.keys.length; i++) {
                if (recipe.keys[i] == key && recipe.in[i] > 0) {
                    consumed = Math.addExact(consumed, Math.multiplyExact(recipe.in[i], times[recipe.index]));
                }
            }
            for (Slot slot : recipe.slots) {
                consumed = Math.addExact(consumed, Math.multiplyExact(slot.amount, slot.allocation.get(key)));
            }
        }
        return consumed;
    }

    private CraftingPlan repairAsIs(CraftingPlan plan) throws InterruptedException {
        final int finalKey = this.keyIndex.getInt(
            plan.finalOutput()
                .what());
        if (finalKey < 0 || this.recipes.isEmpty()) {
            return keepOrFail(plan, this.planBalanced);
        }
        final long[] times = new long[this.recipes.size()];
        for (Recipe recipe : this.recipes) {
            times[recipe.index] = recipe.limit;
            for (Slot slot : recipe.slots) {
                slot.allocation.clear();
            }
        }
        // The plan takes what it listed, nothing else
        this.stock = new long[this.keys.size()];
        addAll(this.stock, plan.usedItems());
        final long[] supply = this.stock.clone();
        addAll(supply, plan.emittedItems());
        addAll(supply, plan.missingItems());
        final long[] demand = new long[this.keys.size()];
        demand[finalKey] = plan.finalOutput()
            .amount();
        final long[] borrowed = new long[this.keys.size()];
        if (replayGreedy(times, supply, demand, borrowed, finalKey) != Replay.DONE) {
            return keepOrFail(plan, this.planBalanced);
        }
        final long[] taken = Arrays.copyOf(supply, this.keys.size());
        for (int key = 0; key < taken.length; key++) {
            taken[key] = Math.addExact(taken[key], key < borrowed.length ? borrowed[key] : 0);
        }
        return rebuild(plan, times, taken);
    }

    // Whether every craft of the plan runs, in some order, from what the plan lists alone
    private boolean runsAsIs(CraftingPlan plan) throws InterruptedException {
        final int finalKey = this.keyIndex.getInt(
            plan.finalOutput()
                .what());
        if (finalKey < 0) {
            return false;
        }
        final long[] times = new long[this.recipes.size()];
        for (Recipe recipe : this.recipes) {
            times[recipe.index] = recipe.limit;
        }
        this.stock = new long[this.keys.size()];
        addAll(this.stock, plan.usedItems());
        final long[] supply = this.stock.clone();
        addAll(supply, plan.emittedItems());
        addAll(supply, plan.missingItems());
        final long[] demand = new long[this.keys.size()];
        demand[finalKey] = plan.finalOutput()
            .amount();
        final long[] borrowed = new long[this.keys.size()];
        if (replayGreedy(times, supply, demand, borrowed, finalKey) != Replay.DONE) {
            return false;
        }
        for (long amount : borrowed) {
            if (amount > 0) {
                return false;
            }
        }
        return true;
    }

    private Replay replayGreedy(long[] times, long[] supply, long[] demand, long[] borrowed, int avoided)
        throws InterruptedException {
        this.steps = 0;
        final long[] inventory = Arrays.copyOf(supply, this.keys.size());
        final long[] lowest = inventory.clone();
        final long[] left = times.clone();
        long remaining = 0;
        for (long value : left) {
            remaining = Math.addExact(remaining, value);
        }
        final Batch batch = new Batch();
        int rounds = 0;
        int borrows = 0;
        while (remaining > 0) {
            if (++rounds > MAX_REPLAY_ROUNDS) return Replay.LIMIT;
            boolean progress = false;
            for (Recipe recipe : this.recipes) {
                while (left[recipe.index] > 0) {
                    final long crafts = recipe.slots.length == 0 ? recipe.runnable(inventory, left[recipe.index])
                        : prepareGreedyBatch(recipe, inventory, left[recipe.index], batch, avoided);
                    if (crafts <= 0) break;
                    if (recipe.slots.length == 0) {
                        recipe.run(inventory, lowest, crafts);
                    } else {
                        batch.run(inventory, lowest, crafts);
                        for (int s = 0; s < recipe.slots.length; s++) {
                            recipe.slots[s].allocation.addTo(batch.variants[s], crafts);
                        }
                    }
                    left[recipe.index] -= crafts;
                    remaining -= crafts;
                    progress = true;
                    if (!step()) return Replay.LIMIT;
                }
            }
            if (!progress) {
                if (++borrows > MAX_BORROWS) return Replay.LIMIT;
                if (!borrowGreedy(left, inventory, borrowed)) return Replay.SHORT;
            }
        }
        // Every craft ran and the request is still not covered: the rest of it is missing, as the legacy tree reports
        // a request it cannot make
        for (int key = 0; key < demand.length; key++) {
            if (inventory[key] < demand[key]) {
                borrow(key, demand[key] - inventory[key], inventory, borrowed);
            }
        }
        return Replay.DONE;
    }

    private enum Replay {
        DONE,
        SHORT,
        LIMIT
    }

    private long prepareGreedyBatch(Recipe recipe, long[] inventory, long wanted, Batch batch, int avoided) {
        batch.reset(recipe);
        for (int s = 0; s < recipe.slots.length; s++) {
            final Slot slot = recipe.slots[s];
            int chosen = -1;
            final IntArrayList candidates = candidates(slot);
            if (candidates.rem(avoided)) candidates.add(avoided);
            for (int i = 0; i < candidates.size(); i++) {
                final int variant = candidates.getInt(i);
                if (variant < inventory.length && inventory[variant] >= slot.amount + batch.inputOf(variant)) {
                    chosen = variant;
                    break;
                }
            }
            if (chosen < 0) return 0;
            batch.variants[s] = chosen;
            batch.add(chosen, slot.amount, 0);
            final int remaining = remaining(slot, chosen);
            if (remaining >= 0 && remaining < inventory.length) {
                batch.add(remaining, 0, slot.amount);
            }
        }
        return batch.runnable(inventory, wanted);
    }

    private boolean borrowGreedy(long[] left, long[] inventory, long[] borrowed) {
        for (Recipe recipe : this.recipes) {
            if (left[recipe.index] <= 0) continue;
            boolean any = false;
            for (int i = 0; i < recipe.keys.length; i++) {
                final long lacking = recipe.in[i] - inventory[recipe.keys[i]];
                if (lacking > 0) {
                    borrow(recipe.keys[i], lacking, inventory, borrowed);
                    any = true;
                }
            }
            for (Slot slot : recipe.slots) {
                boolean covered = false;
                for (int variant : slot.fixed) {
                    if (inventory[variant] >= slot.amount && valid(slot, variant)) {
                        covered = true;
                        break;
                    }
                }
                if (!covered) {
                    borrow(slot.fixed[0], slot.amount - inventory[slot.fixed[0]], inventory, borrowed);
                    any = true;
                }
            }
            if (any) return true;
        }
        return false;
    }

    private static CraftingPlan withMissing(CraftingPlan plan, AEKey what, long amount) {
        final KeyCounter missing = new KeyCounter();
        missing.addAll(plan.missingItems());
        missing.add(what, amount);
        return plan.withMissing(missing);
    }

    private static long total(KeyCounter counter) {
        long total = 0;
        for (Object2LongMap.Entry<AEKey> entry : counter) {
            total = LongMath.saturatedAdd(total, entry.getLongValue());
        }
        return total;
    }

    private static CraftingPlan keepOrFail(CraftingPlan plan, boolean planBalanced) {
        if (planBalanced) {
            return plan;
        }
        AELog.info("Refusing an unbalanced crafting plan for %s", plan.finalOutput());
        throw new CraftingCalculationFailure(PlayerMessages.CraftingNoPlan);
    }

    private void addAll(long[] target, KeyCounter counter) {
        for (Object2LongMap.Entry<AEKey> entry : counter) {
            final int key = this.keyIndex.getInt(entry.getKey());
            if (key >= 0 && entry.getLongValue() > 0) {
                target[key] = Math.addExact(target[key], entry.getLongValue());
            }
        }
    }

    private int indexOf(AEKey key) {
        int index = this.keyIndex.getInt(key);
        if (index < 0) {
            index = this.keys.size();
            this.keyIndex.put(key, index);
            this.keys.add(key);
            this.producers.add(new ObjectArrayList<>(1));
            this.groups.computeIfAbsent(key.getPrimaryKey(), k -> new IntArrayList(1))
                .add(index);
            if (this.balance.length <= index) {
                this.balance = Arrays.copyOf(this.balance, Math.max(16, this.balance.length * 2));
                this.shortfall = Arrays.copyOf(this.shortfall, this.balance.length);
            }
        }
        return index;
    }

    @Nullable
    private long[] fill(long[] initial, long[] limits) throws InterruptedException {
        this.steps = 0;
        this.balance = Arrays.copyOf(initial, Math.max(initial.length, this.balance.length));
        this.shortfall = new long[this.balance.length];
        for (Recipe recipe : this.recipes) {
            for (Slot slot : recipe.slots) {
                slot.allocation.clear();
                slot.pending = 0;
            }
        }
        final List<Slot> pending = new ObjectArrayList<>();
        final long[] times = new long[this.recipes.size()];
        // Keys closer to the requested output first, so a key is only raised once all of its consumers were, with
        // the whole shortfall known: raising it in pieces would round up every piece. Byproducts of crafts raised
        // later may still cover a key raised before, which the trim afterwards takes back.
        final IntHeapPriorityQueue queue = new IntHeapPriorityQueue((a, b) -> Integer.compare(rankOf(a), rankOf(b)));
        final BitSet queued = new BitSet();
        final IntArrayList touched = new IntArrayList();
        for (int key = 0; key < initial.length; key++) {
            if (initial[key] < 0) {
                queue.enqueue(key);
                queued.set(key);
            }
        }
        while (true) {
            if (queue.isEmpty()) {
                if (pending.isEmpty()) break;
                // Inputs with variants are given their variants once nothing else is short, all at once and those
                // taking the most per craft first, like packing the largest items first: given one at a time, an
                // early one may break up the variant a later one needed whole
                pending.sort((a, b) -> Long.compare(b.amount, a.amount));
                touched.clear();
                for (Slot slot : pending) {
                    final long crafts = slot.pending;
                    slot.pending = 0;
                    if (!allocate(slot, crafts, touched)) return null;
                }
                pending.clear();
                for (int i = 0; i < touched.size(); i++) {
                    final int other = touched.getInt(i);
                    if (this.balance[other] < 0 && !queued.get(other)) {
                        queue.enqueue(other);
                        queued.set(other);
                    }
                }
                continue;
            }
            final int key = queue.dequeueInt();
            queued.clear(key);

            for (Recipe recipe : this.producers.get(key)) {
                if (this.balance[key] >= 0) break;
                final long room = limits[recipe.index] - times[recipe.index];
                final long net = recipe.net(key);
                if (room <= 0 || net <= 0) continue;
                final long crafts = Math.min(room, ceilDiv(-this.balance[key], net));
                times[recipe.index] += crafts;
                recipe.apply(this.balance, crafts);
                touched.clear();
                for (int other : recipe.keys) {
                    touched.add(other);
                }
                for (Slot slot : recipe.slots) {
                    if (slot.pending == 0) pending.add(slot);
                    slot.pending = Math.addExact(slot.pending, crafts);
                }
                for (int i = 0; i < touched.size(); i++) {
                    final int other = touched.getInt(i);
                    if (this.balance[other] < 0 && !queued.get(other)) {
                        queue.enqueue(other);
                        queued.set(other);
                    }
                }
                if (!step()) return null;
            }
            if (this.balance[key] < 0) {
                if (!this.producers.get(key)
                    .isEmpty()) {
                    return null;
                }
                // Nothing in the plan makes it, so the rest is missing
                this.shortfall[key] = Math.subtractExact(this.shortfall[key], this.balance[key]);
                this.balance[key] = 0;
            }
        }
        trim(times);
        return times;
    }

    private void trim(long[] times) throws InterruptedException {
        final List<Recipe> order = new ObjectArrayList<>(this.recipes);
        order.sort((a, b) -> Integer.compare(rankOf(a.firstOutput()), rankOf(b.firstOutput())));
        boolean changed = true;
        for (int pass = 0; changed && pass < 64; pass++) {
            changed = false;
            for (Recipe recipe : order) {
                if (recipe.slots.length > 0 || times[recipe.index] <= 0) continue;
                long removable = times[recipe.index];
                for (int i = 0; i < recipe.keys.length && removable > 0; i++) {
                    final long net = recipe.out[i] - recipe.in[i];
                    if (net > 0) {
                        removable = Math.min(removable, Math.max(0, this.balance[recipe.keys[i]]) / net);
                    }
                }
                if (removable > 0) {
                    times[recipe.index] -= removable;
                    recipe.apply(this.balance, -removable);
                    changed = true;
                }
                if (!step()) return;
            }
        }
    }

    private int rankOf(int key) {
        return key < this.rank.length ? this.rank[key] : Integer.MAX_VALUE;
    }

    private int[] rankFrom(int root) {
        final int[] result = new int[this.keys.size()];
        Arrays.fill(result, Integer.MAX_VALUE);
        final BitSet visited = new BitSet();
        final IntArrayList postOrder = new IntArrayList();
        final IntArrayList stack = new IntArrayList();
        final IntArrayList next = new IntArrayList();
        stack.add(root);
        next.add(0);
        visited.set(root);
        while (!stack.isEmpty()) {
            final int top = stack.size() - 1;
            final int key = stack.getInt(top);
            final IntArrayList inputs = inputsOfProducers(key);
            final int index = next.getInt(top);
            if (index < inputs.size()) {
                next.set(top, index + 1);
                final int input = inputs.getInt(index);
                if (!visited.get(input)) {
                    visited.set(input);
                    stack.add(input);
                    next.add(0);
                }
            } else {
                postOrder.add(key);
                stack.removeInt(top);
                next.removeInt(top);
            }
        }
        for (int i = 0; i < postOrder.size(); i++) {
            result[postOrder.getInt(i)] = postOrder.size() - 1 - i;
        }
        return result;
    }

    private IntArrayList inputsOfProducers(int key) {
        final IntArrayList result = new IntArrayList();
        for (Recipe recipe : this.producers.get(key)) {
            for (int i = 0; i < recipe.keys.length; i++) {
                if (recipe.in[i] > 0) result.add(recipe.keys[i]);
            }
            for (Slot slot : recipe.slots) {
                for (int variant : slot.fixed) result.add(variant);
            }
        }
        return result;
    }

    private boolean allocate(Slot slot, long crafts, IntArrayList touched) throws InterruptedException {
        long rest = crafts;
        boolean freshTaken = false;
        for (int round = 0; rest > 0 && round < MAX_ALLOCATION_ROUNDS; round++) {
            boolean progress = false;
            final IntArrayList candidates = candidates(slot);
            // The largest leftovers first: taking whole crafts from small ones leaves remainders no craft can use
            candidates.sort((a, b) -> Long.compare(this.balance[b], this.balance[a]));
            for (int i = 0; i < candidates.size() && rest > 0; i++) {
                final int variant = candidates.getInt(i);
                final int remaining = remaining(slot, variant);
                final long available = this.balance[variant];
                final long taken = remaining == variant ? available >= slot.amount ? rest : 0
                    : Math.min(rest, Math.max(0, available) / slot.amount);
                if (taken > 0) {
                    take(slot, variant, taken, touched);
                    rest -= taken;
                    progress = true;
                }
            }
            if (!progress) {
                if (freshTaken) break;
                // A tool that wears down serves several crafts, each turning it into the next variant of its chain:
                // only as many fresh ones as needed, the rounds after this one use them up
                final int fresh = defaultVariant(slot);
                final long uses = chainLength(slot, fresh);
                if (uses <= 1) break;
                final long freshCrafts = Math.min(rest, ceilDiv(rest, uses));
                take(slot, fresh, freshCrafts, touched);
                rest -= freshCrafts;
                freshTaken = true;
            }
            if (!step()) return false;
        }
        if (rest > 0) {
            take(slot, defaultVariant(slot), rest, touched);
        }
        return true;
    }

    private long chainLength(Slot slot, int variant) {
        long uses = 1;
        int current = variant;
        final BitSet seen = new BitSet();
        seen.set(current);
        while (uses < MAX_CHAIN) {
            final int next = remaining(slot, current);
            if (next < 0 || next == current || seen.get(next) || !valid(slot, next)) break;
            seen.set(next);
            current = next;
            uses++;
        }
        return uses;
    }

    private void take(Slot slot, int variant, long crafts, IntArrayList touched) {
        slot.allocation.addTo(variant, crafts);
        final long amount = Math.multiplyExact(slot.amount, crafts);
        final int remaining = remaining(slot, variant);
        if (remaining == variant) return;
        this.balance[variant] = Math.subtractExact(this.balance[variant], amount);
        touched.add(variant);
        if (remaining >= 0) {
            this.balance[remaining] = Math.addExact(this.balance[remaining], amount);
            touched.add(remaining);
        }
    }

    private int defaultVariant(Slot slot) {
        for (int variant : slot.fixed) {
            if (!this.producers.get(variant)
                .isEmpty() && valid(slot, variant)) {
                return variant;
            }
        }
        return slot.fixed[0];
    }

    private IntArrayList candidates(Slot slot) {
        final IntArrayList result = new IntArrayList(slot.fixed.length);
        for (int variant : slot.fixed) {
            if (valid(slot, variant)) result.add(variant);
        }
        if (slot.dynamic) {
            for (Object group : slot.groupKeys) {
                final IntArrayList members = this.groups.get(group);
                if (members == null) continue;
                for (int i = 0; i < members.size(); i++) {
                    final int variant = members.getInt(i);
                    if (!result.contains(variant) && valid(slot, variant)) result.add(variant);
                }
            }
        }
        return result;
    }

    private boolean valid(Slot slot, int variant) {
        if (slot.valid.containsKey(variant)) {
            return slot.valid.get(variant);
        }
        final boolean valid = slot.isFixed(variant) && !slot.dynamic
            || slot.input.isValid(this.keys.get(variant), this.level);
        slot.valid.put(variant, valid);
        return valid;
    }

    private int remaining(Slot slot, int variant) {
        int remaining = slot.remaining.get(variant);
        if (remaining == UNKNOWN) {
            final AEKey container = slot.input.getRemainingKey(this.keys.get(variant));
            remaining = container == null ? -1 : indexOf(container);
            slot.remaining.put(variant, remaining);
        }
        return remaining;
    }

    @Nullable
    private long[] replay(long[] times, long[] supply, long[] demand) throws InterruptedException {
        return replay(times, supply, demand, null);
    }

    @Nullable
    private long[] replay(long[] times, long[] supply, long[] demand, @Nullable long[] borrowed)
        throws InterruptedException {
        this.steps = 0;
        final long[] inventory = supply.clone();
        final long[] lowest = supply.clone();
        int borrows = 0;
        final long[] left = times.clone();
        final List<Int2LongLinkedOpenHashMap[]> allocations = new ObjectArrayList<>(this.recipes.size());
        long remaining = 0;
        for (Recipe recipe : this.recipes) {
            remaining = Math.addExact(remaining, left[recipe.index]);
            final Int2LongLinkedOpenHashMap[] copies = new Int2LongLinkedOpenHashMap[recipe.slots.length];
            for (int s = 0; s < copies.length; s++) {
                copies[s] = new Int2LongLinkedOpenHashMap(recipe.slots[s].allocation);
            }
            allocations.add(copies);
        }
        final Batch batch = new Batch();
        int rounds = 0;
        while (remaining > 0) {
            if (++rounds > MAX_REPLAY_ROUNDS) return null;
            boolean progress = false;
            for (Recipe recipe : this.recipes) {
                final Int2LongLinkedOpenHashMap[] allocation = allocations.get(recipe.index);
                while (left[recipe.index] > 0) {
                    final long crafts = recipe.slots.length == 0 ? recipe.runnable(inventory, left[recipe.index])
                        : prepareBatch(recipe, allocation, inventory, left[recipe.index], batch);
                    if (crafts <= 0) break;
                    if (recipe.slots.length == 0) {
                        recipe.run(inventory, lowest, crafts);
                    } else {
                        batch.run(inventory, lowest, crafts);
                        for (int s = 0; s < recipe.slots.length; s++) {
                            allocation[s].addTo(batch.variants[s], -crafts);
                        }
                    }
                    left[recipe.index] -= crafts;
                    remaining -= crafts;
                    progress = true;
                    if (!step()) return null;
                }
            }
            if (!progress) {
                if (borrowed == null || ++borrows > MAX_BORROWS || !borrowFor(allocations, left, inventory, borrowed)) {
                    return null;
                }
            }
        }
        for (int key = 0; key < demand.length; key++) {
            if (inventory[key] < demand[key]) return null;
        }
        return lowest;
    }

    private boolean borrowFor(List<Int2LongLinkedOpenHashMap[]> allocations, long[] left, long[] inventory,
        long[] borrowed) {
        for (Recipe recipe : this.recipes) {
            if (left[recipe.index] <= 0) continue;
            boolean any = false;
            for (int i = 0; i < recipe.keys.length; i++) {
                final long lacking = recipe.in[i] - inventory[recipe.keys[i]];
                if (lacking > 0) {
                    borrow(recipe.keys[i], lacking, inventory, borrowed);
                    any = true;
                }
            }
            final Int2LongLinkedOpenHashMap[] allocation = allocations.get(recipe.index);
            for (int s = 0; s < recipe.slots.length; s++) {
                final Slot slot = recipe.slots[s];
                for (Int2LongMap.Entry entry : allocation[s].int2LongEntrySet()) {
                    if (entry.getLongValue() <= 0) continue;
                    final long lacking = slot.amount - inventory[entry.getIntKey()];
                    if (lacking > 0) {
                        borrow(entry.getIntKey(), lacking, inventory, borrowed);
                        any = true;
                    }
                    break;
                }
            }
            if (any) return true;
        }
        return false;
    }

    private static void borrow(int key, long amount, long[] inventory, long[] borrowed) {
        inventory[key] = Math.addExact(inventory[key], amount);
        borrowed[key] = Math.addExact(borrowed[key], amount);
    }

    private long prepareBatch(Recipe recipe, Int2LongLinkedOpenHashMap[] allocation, long[] inventory, long wanted,
        Batch batch) {
        batch.reset(recipe);
        long crafts = wanted;
        for (int s = 0; s < recipe.slots.length; s++) {
            final Slot slot = recipe.slots[s];
            int chosen = -1;
            for (Int2LongMap.Entry entry : allocation[s].int2LongEntrySet()) {
                if (entry.getLongValue() > 0 && inventory[entry.getIntKey()] >= slot.amount) {
                    chosen = entry.getIntKey();
                    crafts = Math.min(crafts, entry.getLongValue());
                    break;
                }
            }
            if (chosen < 0) return 0;
            batch.variants[s] = chosen;
            batch.add(chosen, slot.amount, 0);
            final int remaining = remaining(slot, chosen);
            if (remaining >= 0) {
                batch.add(remaining, 0, slot.amount);
            }
        }
        return batch.runnable(inventory, crafts);
    }

    private boolean step() throws InterruptedException {
        if (++this.steps % PAUSE_INTERVAL == 0) {
            this.calculation.handlePausing();
        }
        return this.steps <= MAX_STEPS;
    }

    private CraftingPlan rebuild(CraftingPlan plan, long[] times, long[] taken) {
        final Object2LongLinkedOpenHashMap<IPatternDetails> patternTimes = new Object2LongLinkedOpenHashMap<>();
        final Map<IPatternDetails, Map<AEKey, KeyCounter>> budgets = new Reference2ObjectLinkedOpenHashMap<>();
        for (Recipe recipe : this.recipes) {
            if (times[recipe.index] <= 0) continue;
            patternTimes.put(recipe.pattern, times[recipe.index]);
            if (recipe.slots.length == 0) continue;
            // Grouped by the encoded stack of each input, which is what the CPU extracts an input by
            final Map<AEKey, KeyCounter> groups = new Object2ObjectOpenHashMap<>();
            for (Slot slot : recipe.slots) {
                final KeyCounter budget = groups.computeIfAbsent(this.keys.get(slot.fixed[0]), k -> new KeyCounter());
                for (Int2LongMap.Entry entry : slot.allocation.int2LongEntrySet()) {
                    if (entry.getLongValue() > 0) {
                        budget.add(
                            this.keys.get(entry.getIntKey()),
                            Math.multiplyExact(slot.amount, entry.getLongValue()));
                    }
                }
            }
            budgets.put(recipe.pattern, groups);
        }
        // Take from the network first, then from emitters, and only the rest is missing
        final long[] left = taken.clone();
        final KeyCounter used = new KeyCounter();
        for (int key = 0; key < left.length; key++) {
            final long amount = Math.min(left[key], key < this.stock.length ? this.stock[key] : 0);
            if (amount > 0) {
                used.add(this.keys.get(key), amount);
                left[key] -= amount;
            }
        }
        for (Object2LongMap.Entry<AEKey> entry : plan.usedItems()) {
            if (this.keyIndex.getInt(entry.getKey()) < 0) {
                // No craft touches it, so the replay knows nothing about it
                used.add(entry.getKey(), entry.getLongValue());
            }
        }
        final KeyCounter emitted = take(plan.emittedItems(), left);
        final KeyCounter missing = new KeyCounter();
        for (Object2LongMap.Entry<AEKey> entry : plan.missingItems()) {
            if (this.keyIndex.getInt(entry.getKey()) < 0) {
                missing.add(entry.getKey(), entry.getLongValue());
            }
        }
        for (int key = 0; key < left.length; key++) {
            if (left[key] > 0) {
                missing.add(this.keys.get(key), left[key]);
            }
        }
        return new CraftingPlan(
            plan.finalOutput(),
            plan.bytes(),
            plan.simulation(),
            used,
            emitted,
            missing,
            patternTimes,
            budgets.isEmpty() ? null : budgets);
    }

    private KeyCounter take(KeyCounter source, long[] left) {
        final KeyCounter result = new KeyCounter();
        for (Object2LongMap.Entry<AEKey> entry : source) {
            final int key = this.keyIndex.getInt(entry.getKey());
            if (key < 0 || key >= left.length) {
                // No craft touches it, so the replay knows nothing about it
                result.add(entry.getKey(), entry.getLongValue());
                continue;
            }
            final long amount = Math.min(entry.getLongValue(), left[key]);
            if (amount > 0) {
                result.add(entry.getKey(), amount);
                left[key] -= amount;
            }
        }
        return result;
    }

    private static long ceilDiv(long dividend, long divisor) {
        final long quotient = dividend / divisor;
        return dividend % divisor == 0 ? quotient : quotient + 1;
    }

    private static class Slot {

        private final IPatternDetails.IInput input;
        private final long amount;
        private final int[] fixed;
        private final boolean dynamic;
        private final Object[] groupKeys;
        private final Int2IntOpenHashMap remaining = new Int2IntOpenHashMap();
        private final Int2BooleanOpenHashMap valid = new Int2BooleanOpenHashMap();
        private final Int2LongLinkedOpenHashMap allocation = new Int2LongLinkedOpenHashMap();
        private long pending;

        private Slot(IPatternDetails.IInput input, long amount, int[] fixed, boolean dynamic, Object[] groupKeys) {
            this.input = input;
            this.amount = amount;
            this.fixed = fixed;
            this.dynamic = dynamic;
            this.groupKeys = groupKeys;
            this.remaining.defaultReturnValue(UNKNOWN);
        }

        boolean isFixed(int variant) {
            for (int key : this.fixed) {
                if (key == variant) return true;
            }
            return false;
        }
    }

    private static class Batch {

        private int[] keys = new int[8];
        private long[] in = new long[8];
        private long[] out = new long[8];
        private int size;
        private int[] variants = new int[0];

        void reset(Recipe recipe) {
            this.size = 0;
            if (this.variants.length < recipe.slots.length) {
                this.variants = new int[recipe.slots.length];
            }
            for (int i = 0; i < recipe.keys.length; i++) {
                add(recipe.keys[i], recipe.in[i], recipe.out[i]);
            }
        }

        void add(int key, long in, long out) {
            for (int i = 0; i < this.size; i++) {
                if (this.keys[i] == key) {
                    this.in[i] = Math.addExact(this.in[i], in);
                    this.out[i] = Math.addExact(this.out[i], out);
                    return;
                }
            }
            if (this.size == this.keys.length) {
                this.keys = Arrays.copyOf(this.keys, this.size * 2);
                this.in = Arrays.copyOf(this.in, this.size * 2);
                this.out = Arrays.copyOf(this.out, this.size * 2);
            }
            this.keys[this.size] = key;
            this.in[this.size] = in;
            this.out[this.size] = out;
            this.size++;
        }

        long inputOf(int key) {
            for (int i = 0; i < this.size; i++) {
                if (this.keys[i] == key) return this.in[i];
            }
            return 0;
        }

        long runnable(long[] inventory, long wanted) {
            return Recipe.runnable(this.keys, this.in, this.out, this.size, inventory, wanted);
        }

        void run(long[] inventory, long[] lowest, long crafts) {
            Recipe.run(this.keys, this.in, this.out, this.size, inventory, lowest, crafts);
        }
    }

    private static class Recipe {

        private final IPatternDetails pattern;
        private final int index;
        private final long limit;
        private final int[] keys;
        private final long[] in;
        private final long[] out;
        private final Slot[] slots;

        private Recipe(IPatternDetails pattern, int index, long limit, int[] keys, long[] in, long[] out,
            Slot[] slots) {
            this.pattern = pattern;
            this.index = index;
            this.limit = limit;
            this.keys = keys;
            this.in = in;
            this.out = out;
            this.slots = slots;
        }

        @Nullable
        static Recipe of(IPatternDetails pattern, long limit, CraftingPlanCompactor compactor) {
            final boolean substitutes = pattern.canSubstitute();
            final Object2LongOpenHashMap<AEKey> inputs = new Object2LongOpenHashMap<>();
            final Object2LongOpenHashMap<AEKey> outputs = new Object2LongOpenHashMap<>();
            final List<Slot> slots = new ObjectArrayList<>();
            for (IPatternDetails.IInput input : pattern.getInputs()) {
                final GenericStack[] possible = input.possibleInputs();
                if (possible.length == 0 || input.getMultiplier() <= 0) {
                    return null;
                }
                final long amount = Math.multiplyExact(possible[0].amount(), input.getMultiplier());
                for (GenericStack variant : possible) {
                    if (variant.amount() != possible[0].amount()) return null;
                }
                if (amount <= 0) {
                    return null;
                }
                if (possible.length == 1 && !substitutes) {
                    final AEKey key = possible[0].what();
                    inputs.addTo(key, amount);
                    final AEKey container = input.getRemainingKey(key);
                    if (container != null) {
                        outputs.addTo(container, amount);
                    }
                    continue;
                }
                final int[] fixed = new int[possible.length];
                final Object[] groupKeys = new Object[possible.length];
                for (int v = 0; v < possible.length; v++) {
                    fixed[v] = compactor.indexOf(possible[v].what());
                    groupKeys[v] = possible[v].what()
                        .getPrimaryKey();
                }
                slots.add(new Slot(input, amount, fixed, substitutes, groupKeys));
            }
            for (GenericStack output : pattern.getOutputs()) {
                if (output.amount() <= 0) {
                    return null;
                }
                outputs.addTo(output.what(), output.amount());
            }

            final List<AEKey> order = new ObjectArrayList<>(inputs.keySet());
            for (AEKey key : outputs.keySet()) {
                if (!inputs.containsKey(key)) {
                    order.add(key);
                }
            }
            final int[] keys = new int[order.size()];
            final long[] in = new long[order.size()];
            final long[] out = new long[order.size()];
            for (int i = 0; i < keys.length; i++) {
                final AEKey key = order.get(i);
                keys[i] = compactor.indexOf(key);
                in[i] = inputs.getLong(key);
                out[i] = outputs.getLong(key);
            }
            final Recipe recipe = new Recipe(
                pattern,
                compactor.recipes.size(),
                limit,
                keys,
                in,
                out,
                slots.toArray(new Slot[0]));
            for (int i = 0; i < keys.length; i++) {
                if (out[i] > in[i]) {
                    compactor.producers.get(keys[i])
                        .add(recipe);
                }
            }
            return recipe;
        }

        int firstOutput() {
            for (int i = 0; i < this.keys.length; i++) {
                if (this.out[i] > this.in[i]) return this.keys[i];
            }
            return this.keys.length == 0 ? 0 : this.keys[0];
        }

        boolean makesFirst(AEKey key) {
            final List<GenericStack> outputs = this.pattern.getOutputs();
            return !outputs.isEmpty() && outputs.get(0)
                .what()
                .equals(key);
        }

        long net(int key) {
            for (int i = 0; i < this.keys.length; i++) {
                if (this.keys[i] == key) return this.out[i] - this.in[i];
            }
            return 0;
        }

        void apply(long[] balance, long crafts) {
            for (int i = 0; i < this.keys.length; i++) {
                final long delta = Math.multiplyExact(this.out[i] - this.in[i], crafts);
                balance[this.keys[i]] = Math.addExact(balance[this.keys[i]], delta);
            }
        }

        long runnable(long[] inventory, long wanted) {
            return runnable(this.keys, this.in, this.out, this.keys.length, inventory, wanted);
        }

        void run(long[] inventory, long[] lowest, long crafts) {
            run(this.keys, this.in, this.out, this.keys.length, inventory, lowest, crafts);
        }

        static long runnable(int[] keys, long[] in, long[] out, int size, long[] inventory, long wanted) {
            long crafts = wanted;
            for (int i = 0; i < size && crafts > 0; i++) {
                if (in[i] == 0) continue;
                final long available = inventory[keys[i]];
                if (available < in[i]) return 0;
                final long loss = in[i] - out[i];
                if (loss > 0) {
                    crafts = Math.min(crafts, (available - in[i]) / loss + 1);
                }
            }
            return crafts;
        }

        static void run(int[] keys, long[] in, long[] out, int size, long[] inventory, long[] lowest, long crafts) {
            for (int i = 0; i < size; i++) {
                final int key = keys[i];
                if (in[i] > 0) {
                    // The inputs of the last craft are out while its outputs are not back yet
                    final long loss = Math.max(0, in[i] - out[i]);
                    final long low = inventory[key] - in[i] - Math.multiplyExact(loss, crafts - 1);
                    lowest[key] = Math.min(lowest[key], low);
                }
                inventory[key] = Math.addExact(inventory[key], Math.multiplyExact(out[i] - in[i], crafts));
            }
        }
    }
}
