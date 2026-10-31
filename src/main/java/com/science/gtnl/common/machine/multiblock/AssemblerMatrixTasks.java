package com.science.gtnl.common.machine.multiblock;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import appeng.api.storage.data.IAEItemStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

// Work accepted from AE: the CPU already took the inputs, so every stack here must reach AE storage.
public class AssemblerMatrixTasks {

    public static final String BATCH_TAG = "CACHED_OUTPUT_ITEMS";
    public static final String PENDING_TAG = "OUTPUT_ITEMS";
    public static final String RETURNS_TAG = "INPUT_ITEMS";

    private final Deque<IAEItemStack> pending = new ArrayDeque<>();
    private final Deque<IAEItemStack> returns = new ArrayDeque<>();
    private final List<IAEItemStack> batch = new ObjectArrayList<>();

    public void accept(Collection<IAEItemStack> containerReturns, Collection<IAEItemStack> outputs) {
        addAll(this.returns, containerReturns);
        addAll(this.pending, outputs);
    }

    public boolean hasPending() {
        return !this.pending.isEmpty();
    }

    public boolean hasReturns() {
        return !this.returns.isEmpty();
    }

    public void sendReturns(UnaryOperator<IAEItemStack> insert) {
        for (int i = this.returns.size(); i > 0; i--) {
            IAEItemStack stack = this.returns.poll();
            if (stack == null) continue;
            IAEItemStack leftover;
            try {
                leftover = insert.apply(stack);
            } catch (RuntimeException exception) {
                this.returns.addFirst(stack);
                throw exception;
            }
            if (leftover != null && leftover.getStackSize() > 0) this.returns.add(leftover);
        }
    }

    public long takeBatch(long parallel) {
        requeueBatch();
        long taken = 0;
        for (int i = this.pending.size(); i > 0 && parallel > 0; i--) {
            IAEItemStack stack = this.pending.poll();
            if (stack == null || stack.getStackSize() <= 0) continue;
            long size = stack.getStackSize();
            if (size <= parallel) {
                this.batch.add(stack);
                taken += size;
                parallel -= size;
            } else {
                this.batch.add(
                    stack.copy()
                        .setStackSize(parallel));
                this.pending.add(
                    stack.copy()
                        .setStackSize(size - parallel));
                taken += parallel;
                parallel = 0;
            }
        }
        return taken;
    }

    public boolean hasBatch() {
        return !this.batch.isEmpty();
    }

    public void deliverBatch(UnaryOperator<IAEItemStack> insert) {
        List<IAEItemStack> delivering = new ObjectArrayList<>(this.batch);
        this.batch.clear();
        for (int i = 0; i < delivering.size(); i++) {
            IAEItemStack leftover;
            try {
                leftover = insert.apply(delivering.get(i));
            } catch (RuntimeException exception) {
                addAll(this.pending, delivering.subList(i, delivering.size()));
                throw exception;
            }
            if (leftover != null && leftover.getStackSize() > 0) this.pending.add(leftover);
        }
    }

    public void requeueBatch() {
        addAll(this.pending, this.batch);
        this.batch.clear();
    }

    public List<IAEItemStack> batchView() {
        return Collections.unmodifiableList(this.batch);
    }

    // Client-side display copy of the server batch; the server never calls this.
    public void mirrorBatch(List<IAEItemStack> stacks) {
        this.batch.clear();
        addAll(this.batch, stacks);
    }

    public void writeToNBT(NBTTagCompound root) {
        root.setTag(BATCH_TAG, write(this.batch));
        root.setTag(PENDING_TAG, write(this.pending));
        root.setTag(RETURNS_TAG, write(this.returns));
    }

    public void readFromNBT(NBTTagCompound root, Function<NBTTagCompound, IAEItemStack> reader) {
        this.batch.clear();
        this.pending.clear();
        this.returns.clear();
        read(root, BATCH_TAG, reader, this.batch);
        read(root, PENDING_TAG, reader, this.pending);
        read(root, RETURNS_TAG, reader, this.returns);
    }

    private static void addAll(Collection<IAEItemStack> target, Collection<IAEItemStack> stacks) {
        for (IAEItemStack stack : stacks) {
            if (stack != null && stack.getStackSize() > 0) target.add(stack);
        }
    }

    private static NBTTagList write(Collection<IAEItemStack> stacks) {
        NBTTagList list = new NBTTagList();
        for (IAEItemStack stack : stacks) {
            NBTTagCompound tag = new NBTTagCompound();
            stack.writeToNBT(tag);
            list.appendTag(tag);
        }
        return list;
    }

    private static void read(NBTTagCompound root, String key, Function<NBTTagCompound, IAEItemStack> reader,
        Collection<IAEItemStack> target) {
        NBTTagList list = root.getTagList(key, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            IAEItemStack stack = reader.apply(list.getCompoundTagAt(i));
            if (stack != null && stack.getStackSize() > 0) target.add(stack);
        }
    }
}
