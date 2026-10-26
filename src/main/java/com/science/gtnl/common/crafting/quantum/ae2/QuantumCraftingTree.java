package com.science.gtnl.common.crafting.quantum.ae2;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.GenericStack;
import com.science.gtnl.common.crafting.quantum.crafting.CraftingPlan;
import com.science.gtnl.mixins.late.appliedEnergistics.quamtumComputer.AccessorPacketCraftingTreeData;

import appeng.api.config.CraftingMode;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.storage.data.IAEStack;
import appeng.core.sync.packets.PacketCraftingTreeData;
import appeng.crafting.v2.CraftingCalculations;
import appeng.crafting.v2.CraftingRequest.SubstitutionMode;
import appeng.crafting.v2.CraftingTreeSerializer;
import appeng.crafting.v2.resolvers.CraftingTask;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2LongOpenHashMap;

public final class QuantumCraftingTree {

    private static final int MAX_NODES = 100_000;
    private static final int MAX_LOOP_DEPTH = 64;
    private static final int CHUNK_SIZE = 1024 * 1024;
    private static final byte JOB_STATE_FINISHED = 1;

    private final World world;
    private final CraftingMode craftingMode;
    private final long byteTotal;
    private final Object2LongOpenHashMap<AEKey> stored = new Object2LongOpenHashMap<>();
    private final Object2LongOpenHashMap<AEKey> emitted = new Object2LongOpenHashMap<>();
    private final Object2LongOpenHashMap<AEKey> missing = new Object2LongOpenHashMap<>();
    private final Object2LongOpenHashMap<AEKey> byproducts = new Object2LongOpenHashMap<>();
    private final Reference2LongOpenHashMap<IPatternDetails> crafts = new Reference2LongOpenHashMap<>();
    private final Map<AEKey, List<IPatternDetails>> producers = new Object2ObjectOpenHashMap<>();
    private final Map<Object, List<AEKey>> poolKeysByGroup = new Object2ObjectOpenHashMap<>();
    private final Map<IPatternDetails, Set<AEKey>> producedKeys = new Object2ObjectOpenHashMap<>();
    private int nodes;

    private QuantumCraftingTree(World world, CraftingMode craftingMode, long byteTotal, CraftingPlan plan) {
        this.world = world;
        this.craftingMode = craftingMode;
        this.byteTotal = byteTotal;
        fill(this.stored, plan.usedItems());
        fill(this.emitted, plan.emittedItems());
        fill(this.missing, plan.missingItems());
        for (Object2LongMap.Entry<IPatternDetails> entry : plan.patternTimes()
            .object2LongEntrySet()) {
            final IPatternDetails pattern = entry.getKey();
            if (entry.getLongValue() <= 0) continue;
            this.crafts.put(pattern, entry.getLongValue());
            for (GenericStack output : pattern.getOutputs()) {
                this.producers.computeIfAbsent(output.what(), k -> new ObjectArrayList<>(1))
                    .add(pattern);
            }
        }
    }

    private void fill(Object2LongOpenHashMap<AEKey> pool, Iterable<Object2LongMap.Entry<AEKey>> source) {
        for (Object2LongMap.Entry<AEKey> entry : source) {
            addToPool(pool, entry.getKey(), entry.getLongValue());
        }
    }

    private void addToPool(Object2LongOpenHashMap<AEKey> pool, AEKey key, long amount) {
        if (amount <= 0) return;
        pool.put(key, addSaturated(pool.getLong(key), amount));
        final List<AEKey> group = this.poolKeysByGroup
            .computeIfAbsent(key.getPrimaryKey(), k -> new ObjectArrayList<>(1));
        if (!group.contains(key)) {
            group.add(key);
        }
    }

    public static List<PacketCraftingTreeData> createChunks(World world, QuantumCraftingJob<?> job) {
        final CraftingPlan plan = job.getPlan();
        if (plan == null) {
            return List.of();
        }
        try {
            final QuantumCraftingTree tree = new QuantumCraftingTree(
                world,
                job.getCraftingMode(),
                job.getByteTotal(),
                plan);
            final Request root = tree.build(plan.finalOutput());
            return chunk(tree.serialize(root));
        } catch (Exception e) {
            ScienceNotLeisure.LOG.error("Could not lay out the quantum crafting tree", e);
            return List.of();
        }
    }

    private Request build(GenericStack finalOutput) {
        final Request root = new Request(
            finalOutput.what(),
            finalOutput.amount(),
            0,
            SubstitutionMode.PRECISE_FRESH,
            null,
            null);
        final ObjectArrayFIFOQueue<Request> pending = new ObjectArrayFIFOQueue<>();
        pending.enqueue(root);
        this.nodes = 1;
        while (!pending.isEmpty()) {
            final Request request = pending.dequeue();
            resolve(request, pending);
        }
        foldLoops(root);
        return root;
    }

    // What comes around a loop is crafted again by the pattern above it, repeating the loop under itself once per
    // round. Those crafts are the ones above done more times, so they are merged into them, and what comes around is
    // left with only what starts the loop under it.
    private static void foldLoops(Request root) {
        boolean folded = true;
        while (folded) {
            folded = false;
            final ObjectArrayList<Request> pending = new ObjectArrayList<>();
            pending.add(root);
            while (!pending.isEmpty()) {
                final Request request = pending.pop();
                folded |= foldRounds(request);
                for (Resolver resolver : request.resolvers) {
                    pending.addAll(resolver.children);
                }
            }
        }
    }

    private static boolean foldRounds(Request request) {
        boolean folded = false;
        for (int i = 0; i < request.resolvers.size();) {
            final Resolver resolver = request.resolvers.get(i);
            final Resolver above = resolver.kind == Kind.CRAFT ? craftAbove(request, resolver.pattern) : null;
            if (above == null) {
                i++;
                continue;
            }
            request.resolvers.remove(i);
            above.times = addSaturated(above.times, resolver.times);
            mergeChildren(above, resolver);
            folded = true;
        }
        return folded;
    }

    private static @Nullable Resolver craftAbove(Request request, IPatternDetails pattern) {
        for (Resolver resolver = request.parent; resolver != null; resolver = resolver.owner.parent) {
            if (resolver.pattern == pattern) return resolver;
        }
        return null;
    }

    private static void mergeChildren(Resolver into, Resolver from) {
        for (Request child : from.children) {
            Request same = null;
            for (Request existing : into.children) {
                if (existing.key.equals(child.key)) {
                    same = existing;
                    break;
                }
            }
            if (same == null) {
                child.parent = into;
                into.children.add(child);
            } else {
                merge(same, child);
            }
        }
    }

    private static void merge(Request into, Request from) {
        into.amount = addSaturated(into.amount, from.amount);
        into.remaining = addSaturated(into.remaining, from.remaining);
        into.returnsAfter = addSaturated(into.returnsAfter, from.returnsAfter);
        into.simulated |= from.simulated;
        for (Resolver resolver : from.resolvers) {
            Resolver same = null;
            for (Resolver existing : into.resolvers) {
                if (existing.kind == resolver.kind && existing.what.equals(resolver.what)
                    && existing.pattern == resolver.pattern
                    && existing.fromByproducts == resolver.fromByproducts) {
                    same = existing;
                    break;
                }
            }
            if (same == null) {
                resolver.owner = into;
                into.resolvers.add(resolver);
            } else {
                same.amount = addSaturated(same.amount, resolver.amount);
                same.times = addSaturated(same.times, resolver.times);
                mergeChildren(same, resolver);
            }
        }
    }

    private void resolve(Request request, ObjectArrayFIFOQueue<Request> pending) {
        long rest = request.amount;
        final Set<AEKey> candidates = candidates(request);

        // The requested output itself is never taken from storage. What a loop gets back takes the stored items first:
        // they are what starts the loop, and the rest comes around from the crafts above.
        if (request.mode != SubstitutionMode.PRECISE_FRESH) {
            if (request.loop) {
                rest = extract(request, candidates, rest, false);
                rest = extract(request, candidates, rest, true);
            } else {
                rest = extract(request, candidates, rest, true);
                rest = extract(request, candidates, rest, false);
            }
        }

        for (AEKey candidate : candidates) {
            final List<IPatternDetails> patterns = this.producers.get(candidate);
            if (patterns == null) continue;
            for (IPatternDetails pattern : patterns) {
                if (rest <= 0) break;
                final long available = this.crafts.getLong(pattern);
                final long perCraft = outputAmount(pattern, candidate);
                if (available <= 0 || perCraft <= 0) continue;

                final long times = Math.min(available, ceilDiv(rest, perCraft));
                this.crafts.put(pattern, available - times);
                final long fulfilled = Math.min(rest, multiplySaturated(times, perCraft));
                rest -= fulfilled;
                for (GenericStack output : pattern.getOutputs()) {
                    final long made = multiplySaturated(times, output.amount());
                    if (output.what()
                        .equals(candidate)) {
                        addToPool(this.byproducts, candidate, made - fulfilled);
                    } else if (inputAmount(pattern, output.what()) <= 0) {
                        addToPool(this.byproducts, output.what(), made);
                    }
                }
                addReturns(pattern, candidate, times);

                final Resolver resolver = new Resolver(Kind.CRAFT, candidate, fulfilled);
                resolver.owner = request;
                resolver.pattern = pattern;
                resolver.times = times;
                resolver.perCraftOutput = perCraft;
                request.resolvers.add(resolver);
                if (this.nodes < MAX_NODES) {
                    addInputs(resolver, pending);
                }
            }
        }

        for (AEKey candidate : candidates) {
            if (rest <= 0) break;
            final long taken = take(this.emitted, candidate, rest);
            if (taken > 0) {
                rest -= taken;
                request.resolvers.add(new Resolver(Kind.EMIT, candidate, taken));
            }
        }

        for (AEKey candidate : candidates) {
            if (rest <= 0) break;
            final long taken = take(this.missing, candidate, rest);
            if (taken > 0) {
                rest -= taken;
                request.simulated = true;
                request.resolvers.add(new Resolver(Kind.MISSING, candidate, taken));
            }
        }
        request.remaining = rest;
        addToPool(this.byproducts, request.key, request.returnsAfter);
    }

    private long extract(Request request, Set<AEKey> candidates, long rest, boolean fromByproducts) {
        final Object2LongOpenHashMap<AEKey> pool = fromByproducts ? this.byproducts : this.stored;
        for (AEKey candidate : candidates) {
            if (rest <= 0) break;
            final long taken = take(pool, candidate, rest);
            if (taken > 0) {
                rest -= taken;
                final Resolver resolver = new Resolver(Kind.EXTRACT, candidate, taken);
                resolver.fromByproducts = fromByproducts;
                request.resolvers.add(resolver);
            }
        }
        return rest;
    }

    // What a batch of crafts gives back of its own inputs for the crafts below it to take: what the catalysts gain,
    // and the containers of all crafts but the last. The catalysts themselves only come back once they are there, see
    // Request#returnsAfter.
    private void addReturns(IPatternDetails pattern, AEKey candidate, long times) {
        final Set<AEKey> seen = new ObjectLinkedOpenHashSet<>();
        for (GenericStack output : pattern.getOutputs()) {
            final AEKey key = output.what();
            if (key.equals(candidate) || !seen.add(key)) continue;
            final long perCraft = inputAmount(pattern, key);
            if (perCraft > 0) {
                final long returned = outputAmount(pattern, key);
                final long after = catalystReturned(perCraft, returned, times);
                addToPool(this.byproducts, key, after - Math.min(after, catalystRequired(perCraft, returned, times)));
            }
        }
        for (IPatternDetails.IInput input : pattern.getInputs()) {
            final GenericStack[] possible = input.possibleInputs();
            if (possible.length == 0) continue;
            final AEKey container = input.getRemainingKey(possible[0].what());
            if (container != null) {
                addToPool(this.byproducts, container, multiplySaturated(input.getMultiplier(), times - 1));
            }
        }
    }

    // A catalyst only has to be there for the first craft, each later one needs what the earlier ones did not give back
    private static long catalystRequired(long perCraft, long returned, long times) {
        final long lost = Math.max(0, perCraft - returned);
        return addSaturated(perCraft, multiplySaturated(lost, times - 1));
    }

    private static long catalystReturned(long perCraft, long returned, long times) {
        return returned < perCraft ? returned : addSaturated(perCraft, multiplySaturated(returned - perCraft, times));
    }

    private Set<AEKey> producedKeys(IPatternDetails pattern) {
        Set<AEKey> keys = this.producedKeys.get(pattern);
        if (keys == null) {
            keys = new ObjectLinkedOpenHashSet<>();
            for (GenericStack output : pattern.getOutputs()) {
                keys.add(output.what());
            }
            for (IPatternDetails.IInput input : pattern.getInputs()) {
                final GenericStack[] possible = input.possibleInputs();
                if (possible.length == 0) continue;
                final AEKey container = input.getRemainingKey(possible[0].what());
                if (container != null) {
                    keys.add(container);
                }
            }
            this.producedKeys.put(pattern, keys);
        }
        return keys;
    }

    // A request for what a craft above it makes is the way back around a loop
    private boolean isLoop(AEKey key, Resolver parent) {
        Resolver resolver = parent;
        for (int depth = 0; resolver != null && depth < MAX_LOOP_DEPTH; depth++) {
            if (producedKeys(resolver.pattern).contains(key)) {
                return true;
            }
            resolver = resolver.owner.parent;
        }
        return false;
    }

    private Set<AEKey> candidates(Request request) {
        final Set<AEKey> candidates = new ObjectLinkedOpenHashSet<>();
        candidates.add(request.key);
        final IPatternDetails.IInput input = request.input;
        if (input == null || request.mode != SubstitutionMode.ACCEPT_FUZZY) {
            return candidates;
        }

        final List<AEKey> alternatives = new ObjectArrayList<>();
        for (GenericStack possible : input.possibleInputs()) {
            alternatives.add(possible.what());
        }
        for (AEKey alternative : alternatives) {
            addValid(candidates, input, alternative);
            final List<AEKey> group = this.poolKeysByGroup.get(alternative.getPrimaryKey());
            if (group != null) {
                for (AEKey variant : group) {
                    addValid(candidates, input, variant);
                }
            }
        }
        return candidates;
    }

    private void addValid(Set<AEKey> candidates, IPatternDetails.IInput input, AEKey key) {
        if (!candidates.contains(key) && input.isValid(key, this.world)) {
            candidates.add(key);
        }
    }

    private void addInputs(Resolver resolver, ObjectArrayFIFOQueue<Request> pending) {
        final IPatternDetails pattern = resolver.pattern;
        final SubstitutionMode mode = pattern.canSubstitute() ? SubstitutionMode.ACCEPT_FUZZY
            : SubstitutionMode.PRECISE;

        // Condense the input slots by their encoded stack, like AE2 shows them
        final Map<AEKey, Request> byKey = new Object2ObjectOpenHashMap<>();
        for (IPatternDetails.IInput input : pattern.getInputs()) {
            final GenericStack[] possible = input.possibleInputs();
            if (possible.length == 0) continue;
            final long perCraft = multiplySaturated(possible[0].amount(), input.getMultiplier());
            final Request existing = byKey.get(possible[0].what());
            if (existing != null) {
                existing.perCraft = addSaturated(existing.perCraft, perCraft);
                continue;
            }
            final Request child = new Request(possible[0].what(), 0, perCraft, mode, input, resolver);
            byKey.put(child.key, child);
            resolver.children.add(child);
        }
        for (Request child : resolver.children) {
            final long returned = child.key.equals(resolver.what) ? 0 : outputAmount(pattern, child.key);
            if (returned > 0) {
                child.amount = catalystRequired(child.perCraft, returned, resolver.times);
                child.returnsAfter = Math.min(child.amount, catalystReturned(child.perCraft, returned, resolver.times));
            } else {
                child.amount = multiplySaturated(child.perCraft, resolver.times);
            }
            child.loop = isLoop(child.key, resolver);
        }
        // Inputs from outside the loop first, the way back around it last
        resolver.children.sort((a, b) -> Boolean.compare(a.loop, b.loop));
        for (Request child : resolver.children) {
            pending.enqueue(child);
            this.nodes++;
        }
    }

    private static long inputAmount(IPatternDetails pattern, AEKey what) {
        long amount = 0;
        for (IPatternDetails.IInput input : pattern.getInputs()) {
            final GenericStack[] possible = input.possibleInputs();
            if (possible.length > 0 && possible[0].what()
                .equals(what)) {
                amount = addSaturated(amount, multiplySaturated(possible[0].amount(), input.getMultiplier()));
            }
        }
        return amount;
    }

    private static long outputAmount(IPatternDetails pattern, AEKey what) {
        long amount = 0;
        for (GenericStack output : pattern.getOutputs()) {
            if (output.what()
                .equals(what)) {
                amount = addSaturated(amount, output.amount());
            }
        }
        return amount;
    }

    private static long take(Object2LongOpenHashMap<AEKey> pool, AEKey key, long wanted) {
        final long available = pool.getLong(key);
        if (available <= 0) return 0;
        final long taken = Math.min(available, wanted);
        pool.put(key, available - taken);
        return taken;
    }

    private ByteBuf serialize(Request root) throws IOException {
        final CraftingTreeSerializer serializer = new CraftingTreeSerializer(this.world);
        final ByteBuf buffer = serializer.getBuffer();
        final ObjectArrayList<Runnable> work = new ObjectArrayList<>();

        ByteBufUtils.writeUTF8String(buffer, ":j");
        buffer.writeLong(this.byteTotal);
        buffer.writeByte(JOB_STATE_FINISHED);
        ByteBufUtils.writeUTF8String(buffer, "");
        writeRequestFields(serializer, root);
        queueResolvers(serializer, root, work);

        try {
            while (!work.isEmpty()) {
                work.pop()
                    .run();
            }
        } catch (IndexOutOfBoundsException e) {
            // Past AE2's tree size limit: the client shows the part it received, like AE2's own trees
            ScienceNotLeisure.LOG.warn("Quantum crafting tree exceeds the crafting tree visualization size");
        }
        return serializer.finalizeSerializer();
    }

    private void queueResolvers(CraftingTreeSerializer serializer, Request request, ObjectArrayList<Runnable> work) {
        final List<Resolver> resolvers = request.resolvers;
        ByteBufUtils.writeVarInt(serializer.getBuffer(), resolvers.size(), 5);
        for (int i = resolvers.size() - 1; i >= 0; i--) {
            final Resolver resolver = resolvers.get(i);
            work.push(() -> writeResolver(serializer, resolver, work));
        }
    }

    private void writeResolver(CraftingTreeSerializer serializer, Resolver resolver, ObjectArrayList<Runnable> work) {
        final ByteBuf buffer = serializer.getBuffer();
        ByteBufUtils.writeUTF8String(buffer, ":re");
        serializer.writeStackWithSize(stack(resolver.what, resolver.amount));
        ByteBufUtils.writeVarInt(buffer, 1, 5);
        work.push(() -> writeTask(serializer, resolver, work));
    }

    private void writeTask(CraftingTreeSerializer serializer, Resolver resolver, ObjectArrayList<Runnable> work) {
        final ByteBuf buffer = serializer.getBuffer();
        final ItemStack patternItem = resolver.kind == Kind.CRAFT ? ((QuantumPattern) resolver.pattern).getDetails()
            .getPattern() : null;
        // AE2 rebuilds crafting tasks from the pattern item, so other patterns can only be shown as their output
        final Kind kind = resolver.kind == Kind.CRAFT
            && (patternItem == null || !(patternItem.getItem() instanceof ICraftingPatternItem)) ? Kind.EXTRACT
                : resolver.kind;

        switch (kind) {
            case EXTRACT -> {
                ByteBufUtils.writeUTF8String(buffer, ":tx");
                writeTaskFields(serializer);
                final List<IAEStack<?>> removed = resolver.kind == Kind.EXTRACT
                    ? Collections.singletonList(stack(resolver.what, resolver.amount))
                    : Collections.emptyList();
                writeStacks(serializer, resolver.fromByproducts ? Collections.emptyList() : removed);
                writeStacks(serializer, resolver.fromByproducts ? removed : Collections.emptyList());
                ByteBufUtils.writeVarInt(buffer, 0, 5);
            }
            case CRAFT -> {
                ByteBufUtils.writeUTF8String(buffer, ":tc");
                writeTaskFields(serializer);
                serializer.writeStack(AEItemStack.create(patternItem));
                buffer.writeBoolean(true);
                buffer.writeBoolean(false);
                serializer.writeStackWithSize(stack(resolver.what, resolver.perCraftOutput));
                serializer.writeStack(null);
                buffer.writeLong(resolver.times);
                final List<Request> children = resolver.children;
                ByteBufUtils.writeVarInt(buffer, children.size(), 5);
                for (int i = children.size() - 1; i >= 0; i--) {
                    final Request child = children.get(i);
                    work.push(() -> writeChildRequest(serializer, child, work));
                }
            }
            case EMIT, MISSING -> {
                final String key = kind == Kind.EMIT ? ":te"
                    : this.craftingMode == CraftingMode.IGNORE_MISSING ? ":tp" : ":ts";
                ByteBufUtils.writeUTF8String(buffer, key);
                writeTaskFields(serializer);
                buffer.writeLong(resolver.amount);
                ByteBufUtils.writeVarInt(buffer, 0, 5);
            }
        }
    }

    private void writeChildRequest(CraftingTreeSerializer serializer, Request request, ObjectArrayList<Runnable> work) {
        final ByteBuf buffer = serializer.getBuffer();
        ByteBufUtils.writeUTF8String(buffer, ":tcr");
        buffer.writeLong(request.perCraft);
        writeRequestFields(serializer, request);
        queueResolvers(serializer, request, work);
    }

    private void writeRequestFields(CraftingTreeSerializer serializer, Request request) {
        final ByteBuf buffer = serializer.getBuffer();
        final IAEStack<?> stack = stack(request.key, request.amount);
        serializer.writeStackWithSize(stack);
        writeEnum(serializer, request.mode);
        buffer.writeBoolean(true);
        buffer.writeLong(request.remaining);
        final long bytes = request.amount > 0 ? CraftingCalculations.adjustByteCost(stack, request.amount) : 0;
        buffer.writeLong(bytes);
        buffer.writeLong(request.amount);
        buffer.writeBoolean(request.simulated);
        buffer.writeBoolean(request.remaining > 0);
        buffer.writeInt(this.craftingMode.ordinal());
        buffer.writeLong(1);
    }

    private static void writeTaskFields(CraftingTreeSerializer serializer) {
        serializer.getBuffer()
            .writeInt(0);
        writeEnum(serializer, CraftingTask.State.SUCCESS);
    }

    private static void writeEnum(CraftingTreeSerializer serializer, Enum<?> value) {
        try {
            serializer.writeEnum(value);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void writeStacks(CraftingTreeSerializer serializer, List<IAEStack<?>> stacks) {
        try {
            serializer.writeList(stacks, serializer::writeStackWithSize);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static IAEStack<?> stack(AEKey key, long amount) {
        return ((StackKey) key).toStack(amount);
    }

    private static List<PacketCraftingTreeData> chunk(ByteBuf data) throws IOException {
        final ByteBuf output = Unpooled.buffer(data.readableBytes() / 4 + 64);
        try (ByteBufOutputStream bbos = new ByteBufOutputStream(output);
            GZIPOutputStream gzos = new GZIPOutputStream(bbos);
            ByteBufInputStream bbis = new ByteBufInputStream(data)) {
            IOUtils.copy(bbis, gzos);
            gzos.finish();
        }
        final int chunkCount = (int) ceilDiv(output.readableBytes(), CHUNK_SIZE);
        final List<PacketCraftingTreeData> chunks = new ObjectArrayList<>(chunkCount);
        for (int chunk = 0; chunk < chunkCount; chunk++) {
            final int start = CHUNK_SIZE * chunk;
            final int length = Math.min(start + CHUNK_SIZE, output.readableBytes()) - start;
            chunks.add(AccessorPacketCraftingTreeData.gtnl$create(output.slice(start, length), chunk, chunkCount));
        }
        return chunks;
    }

    private static long ceilDiv(long dividend, long divisor) {
        return -Math.floorDiv(-dividend, divisor);
    }

    private static long multiplySaturated(long a, long b) {
        final long high = Math.multiplyHigh(a, b);
        final long low = a * b;
        return high == 0 && low >= 0 || high == -1 && low < 0 ? low : Long.MAX_VALUE;
    }

    private static long addSaturated(long a, long b) {
        final long sum = a + b;
        return ((a ^ sum) & (b ^ sum)) < 0 ? Long.MAX_VALUE : sum;
    }

    private enum Kind {
        EXTRACT,
        CRAFT,
        EMIT,
        MISSING
    }

    private static final class Request {

        private final AEKey key;
        private long amount;
        private long perCraft;
        private final SubstitutionMode mode;
        @Nullable
        private final IPatternDetails.IInput input;
        @Nullable
        private Resolver parent;
        private final List<Resolver> resolvers = new ObjectArrayList<>(1);
        private long remaining;
        private boolean simulated;
        private boolean loop;
        // The catalyst the crafts of the parent give back once this request got it to them
        private long returnsAfter;

        private Request(AEKey key, long amount, long perCraft, SubstitutionMode mode,
            @Nullable IPatternDetails.IInput input, @Nullable Resolver parent) {
            this.key = key;
            this.amount = amount;
            this.perCraft = perCraft;
            this.mode = mode;
            this.input = input;
            this.parent = parent;
        }
    }

    private static final class Resolver {

        private final Kind kind;
        private final AEKey what;
        private long amount;
        private Request owner;
        private IPatternDetails pattern;
        private long times;
        private long perCraftOutput;
        private boolean fromByproducts;
        private final List<Request> children = new ObjectArrayList<>();

        private Resolver(Kind kind, AEKey what, long amount) {
            this.kind = kind;
            this.what = what;
            this.amount = amount;
        }
    }
}
