package com.science.gtnl.common.crafting.quantum.ae2;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.KeyCounter;
import com.science.gtnl.common.crafting.quantum.crafting.CraftingCalculation;
import com.science.gtnl.common.crafting.quantum.crafting.CraftingCalculationFailure;
import com.science.gtnl.common.crafting.quantum.crafting.CraftingPlan;
import com.science.gtnl.utils.ECraftingCPUCluster;

import appeng.api.config.Actionable;
import appeng.api.config.CraftingMode;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingCallback;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.security.PlayerSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.crafting.CraftBranchFailure;
import appeng.crafting.MECraftingInventory;
import appeng.crafting.v2.CraftingCalculations;
import appeng.hooks.TickHandler;
import appeng.me.cache.CraftingGridCache;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class QuantumCraftingJob<StackType extends IAEStack<StackType>>
    implements ICraftingJob<StackType>, Future<ICraftingJob<StackType>> {

    private final World world;
    private final StackType output;
    private final CraftingMode craftingMode;
    @Nullable
    private final ICraftingCallback callback;
    private final QuantumNetworkSnapshot snapshot;
    private final CraftingCalculation calculation;
    private final CountDownLatch completion = new CountDownLatch(1);

    private volatile boolean started;
    private volatile boolean cancelled;
    @Nullable
    private volatile CraftingPlan plan;
    private volatile String errorMessage = "";
    private long byteTotal;
    @Nullable
    private MECraftingInventory storageAtBeginning;
    private boolean callbackFired;

    public QuantumCraftingJob(World world, IGrid grid, BaseActionSource actionSource, StackType what,
        CraftingMode craftingMode, @Nullable ICraftingCallback callback) {
        this.world = world;
        this.output = what.copy();
        this.craftingMode = craftingMode;
        this.callback = callback;
        this.snapshot = new QuantumNetworkSnapshot(
            grid.getCache(IStorageGrid.class),
            (CraftingGridCache) grid.<ICraftingGrid>getCache(ICraftingGrid.class),
            world);

        final StackKey key = this.snapshot.getKeyFactory()
            .of(what);
        if (key == null) {
            throw new IllegalArgumentException("Invalid Crafting Job Request");
        }
        this.calculation = new CraftingCalculation(
            world,
            this.snapshot,
            this.snapshot,
            describe(actionSource),
            key.toGeneric(what.getStackSize()));
    }

    private static String describe(BaseActionSource source) {
        if (source instanceof PlayerSource playerSource && playerSource.player != null) {
            return playerSource.player.getCommandSenderName();
        }
        if (source instanceof MachineSource machineSource && machineSource.via != null) {
            return machineSource.via.getClass()
                .getSimpleName();
        }
        return String.valueOf(source);
    }

    private void runCalculation() {
        this.started = true;
        try {
            final CraftingPlan result = this.calculation.run();
            this.byteTotal = computeByteTotal(result);
            this.plan = result;
        } catch (InterruptedException e) {
            this.cancelled = true;
        } catch (Throwable t) {
            // Already logged by the calculation
            this.errorMessage = describe(t);
        } finally {
            this.completion.countDown();
        }
    }

    private static String describe(Throwable t) {
        for (Throwable cause = t; cause != null; cause = cause.getCause()) {
            if (cause instanceof CraftingCalculationFailure failure) {
                return failure.getMessageKey()
                    .getTranslationKey();
            }
            if (cause instanceof ArithmeticException) {
                return cause.toString();
            }
        }
        return t.toString();
    }

    private static long computeByteTotal(CraftingPlan plan) {
        long bytes = 0;
        for (Object2LongMap.Entry<IPatternDetails> entry : plan.patternTimes()
            .object2LongEntrySet()) {
            for (var output : entry.getKey()
                .getOutputs()) {
                bytes = Math.addExact(
                    bytes,
                    byteCost(output.what(), Math.multiplyExact(output.amount(), entry.getLongValue())));
            }
        }
        bytes = Math.addExact(bytes, byteCost(plan.usedItems()));
        bytes = Math.addExact(bytes, byteCost(plan.emittedItems()));
        return Math.addExact(bytes, byteCost(plan.missingItems()));
    }

    private static long byteCost(KeyCounter counter) {
        long bytes = 0;
        for (Object2LongMap.Entry<AEKey> entry : counter) {
            bytes = Math.addExact(bytes, byteCost(entry.getKey(), entry.getLongValue()));
        }
        return bytes;
    }

    private static long byteCost(AEKey key, long amount) {
        if (amount <= 0) {
            return 0;
        }
        return CraftingCalculations.adjustByteCost(((StackKey) key).getStack(), amount);
    }

    @Nullable
    public CraftingPlan getPlan() {
        return this.plan;
    }

    @Override
    public boolean isSimulation() {
        final CraftingPlan result = this.plan;
        return result == null || result.simulation()
            || this.craftingMode != CraftingMode.IGNORE_MISSING && !result.missingItems()
                .isEmpty();
    }

    @Override
    public long getByteTotal() {
        return this.byteTotal;
    }

    @Override
    public StackType getOutput() {
        return this.output;
    }

    @Override
    public boolean simulateFor(int milli) {
        if (this.completion.getCount() > 0) {
            // The calculation only gets time once its thread is waiting for it
            if (!this.started) {
                return true;
            }
            if (this.calculation.simulateFor((int) Math.min(Integer.MAX_VALUE, milli * 1000L))) {
                return true;
            }
            awaitCompletion();
        }

        if (!this.callbackFired) {
            this.callbackFired = true;
            if (this.callback != null) {
                this.callback.calculationComplete(this);
            }
        }
        return false;
    }

    private void awaitCompletion() {
        // The calculation reports itself done just before its thread publishes the plan
        try {
            this.completion.await();
        } catch (InterruptedException e) {
            Thread.currentThread()
                .interrupt();
        }
    }

    @Override
    public Future<ICraftingJob<StackType>> schedule() {
        CraftingGridCache.getCraftingPool()
            .execute(this::runCalculation);
        TickHandler.INSTANCE.registerCraftingSimulation(this.world, this);
        return this;
    }

    @Override
    public boolean supportsCPUCluster(ICraftingCPU cluster) {
        return isQuantumCPU(cluster);
    }

    public static boolean isQuantumCPU(ICraftingCPU cluster) {
        return cluster instanceof ECraftingCPUCluster quantumCluster && quantumCluster.getVirtualCPUOwner() != null;
    }

    @Override
    public CraftingMode getCraftingMode() {
        return this.craftingMode;
    }

    @Override
    public void startCrafting(MECraftingInventory storage, ICraftingCPU craftingCPUCluster, BaseActionSource src) {
        final CraftingPlan result = this.plan;
        if (result == null) {
            throw new IllegalStateException("Quantum crafting job started before its calculation finished");
        }

        final CraftingCPUCluster cluster = (CraftingCPUCluster) craftingCPUCluster;
        for (Object2LongMap.Entry<IPatternDetails> entry : result.patternTimes()
            .object2LongEntrySet()) {
            cluster.addCrafting(((QuantumPattern) entry.getKey()).getDetails(), entry.getLongValue());
        }
        for (Object2LongMap.Entry<AEKey> entry : result.usedItems()) {
            pullStack(storage, cluster, (StackKey) entry.getKey(), entry.getLongValue());
        }
        for (Object2LongMap.Entry<AEKey> entry : result.missingItems()) {
            pullStack(storage, cluster, (StackKey) entry.getKey(), entry.getLongValue());
        }
        for (Object2LongMap.Entry<AEKey> entry : result.emittedItems()) {
            if (entry.getLongValue() > 0) {
                cluster.addEmitable(((StackKey) entry.getKey()).toStack(entry.getLongValue()));
            }
        }
        if (cluster instanceof ECraftingCPUCluster quantumCluster && result.variantBudgets() != null) {
            for (Map.Entry<IPatternDetails, Map<AEKey, KeyCounter>> pattern : result.variantBudgets()
                .entrySet()) {
                for (Map.Entry<AEKey, KeyCounter> input : pattern.getValue()
                    .entrySet()) {
                    final List<IAEStack<?>> variants = new ObjectArrayList<>();
                    for (Object2LongMap.Entry<AEKey> entry : input.getValue()) {
                        variants.add(((StackKey) entry.getKey()).toStack(entry.getLongValue()));
                    }
                    quantumCluster.stageVariantBudget(
                        ((QuantumPattern) pattern.getKey()).getDetails(),
                        ((StackKey) input.getKey()).getStack(),
                        variants);
                }
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void pullStack(MECraftingInventory storage, CraftingCPUCluster cluster, StackKey key, long count) {
        if (count <= 0) {
            return;
        }
        final IAEStack<?> extracted = storage.extractItems((IAEStack) key.toStack(count), Actionable.MODULATE);
        final long extractedCount = extracted == null ? 0 : extracted.getStackSize();
        if (extractedCount != count) {
            if (!cluster.isMissingMode()) {
                throw new CraftBranchFailure(key.toStack(count), count - extractedCount);
            }
            cluster.addEmitable(key.toStack(count - extractedCount));
        }
        if (extracted != null) {
            cluster.addStorage(extracted);
        }
    }

    @Override
    public MECraftingInventory getStorageAtBeginning() {
        MECraftingInventory result = this.storageAtBeginning;
        if (result == null) {
            final CraftingPlan plan = this.plan;
            final List<AEKey> keys = new ObjectArrayList<>();
            if (plan != null) {
                for (Object2LongMap.Entry<AEKey> entry : plan.usedItems()) keys.add(entry.getKey());
                for (Object2LongMap.Entry<AEKey> entry : plan.missingItems()) keys.add(entry.getKey());
            }
            result = this.snapshot.createStorageView(keys);
            if (plan != null) {
                this.storageAtBeginning = result;
            }
        }
        return result;
    }

    @Override
    public void populatePlan(IItemList<IAEStack<?>> list) {
        final CraftingPlan result = this.plan;
        if (result == null) {
            return;
        }

        for (Object2LongMap.Entry<IPatternDetails> entry : result.patternTimes()
            .object2LongEntrySet()) {
            final long times = entry.getLongValue();
            for (IAEStack<?> output : ((QuantumPattern) entry.getKey()).getDetails()
                .getCondensedAEOutputs()) {
                list.addRequestable(
                    output.copy()
                        .setStackSize(0)
                        .setCountRequestable(Math.multiplyExact(times, output.getStackSize()))
                        .setCountRequestableCrafts(times));
            }
        }
        for (Object2LongMap.Entry<AEKey> entry : result.usedItems()) {
            list.add(((StackKey) entry.getKey()).toStack(entry.getLongValue()));
        }
        for (Object2LongMap.Entry<AEKey> entry : result.emittedItems()) {
            list.addRequestable(
                ((StackKey) entry.getKey()).toStack(0)
                    .setCountRequestable(entry.getLongValue()));
        }
        for (Object2LongMap.Entry<AEKey> entry : result.missingItems()) {
            if (this.craftingMode == CraftingMode.IGNORE_MISSING) {
                list.addRequestable(
                    ((StackKey) entry.getKey()).toStack(0)
                        .setCountRequestable(entry.getLongValue()));
            } else {
                list.add(((StackKey) entry.getKey()).toStack(entry.getLongValue()));
            }
        }
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (this.completion.getCount() == 0) {
            return false;
        }
        this.cancelled = true;
        this.calculation.cancel();
        return true;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public boolean isDone() {
        return this.completion.getCount() == 0;
    }

    @Override
    public ICraftingJob<StackType> get() throws InterruptedException, ExecutionException {
        while (!isDone() && !this.cancelled) {
            if (!simulateFor(Integer.MAX_VALUE / 1000)) {
                break;
            }
        }
        this.completion.await();
        return this;
    }

    @Override
    public ICraftingJob<StackType> get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        final long deadline = System.nanoTime() + unit.toNanos(timeout);
        while (!isDone() && !this.cancelled) {
            final long remaining = TimeUnit.NANOSECONDS.toMillis(deadline - System.nanoTime());
            if (remaining <= 0) {
                throw new TimeoutException();
            }
            if (!simulateFor((int) Math.min(remaining, Integer.MAX_VALUE / 1000))) {
                break;
            }
        }
        if (!this.completion.await(Math.max(0, deadline - System.nanoTime()), TimeUnit.NANOSECONDS)) {
            throw new TimeoutException();
        }
        return this;
    }
}
