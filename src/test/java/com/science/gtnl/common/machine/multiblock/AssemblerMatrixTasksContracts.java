package com.science.gtnl.common.machine.multiblock;

import static com.gtnltest.ContractAssertions.check;

import java.lang.reflect.Proxy;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import appeng.api.storage.data.IAEItemStack;

public class AssemblerMatrixTasksContracts {

    private static final String ID_TAG = "id";
    private static final String COUNT_TAG = "Cnt";

    public static void run() {
        AssemblerMatrixTasks tasks = new AssemblerMatrixTasks();
        tasks.accept(List.of(stack("bucket", 3)), List.of(stack("stick", 10), stack("plank", 5)));
        check(total(tasks) == 18, "an accepted push owes every output and container item");

        long taken = tasks.takeBatch(12);
        check(taken == 12 && total(tasks) == 18, "taking a batch splits the last stack without losing items");

        AssemblerMatrixTasks reloaded = new AssemblerMatrixTasks();
        reloaded.readFromNBT(save(tasks), AssemblerMatrixTasksContracts::load);
        check(total(reloaded) == 18, "a batch in progress survives a save and reload");
        check(
            reloaded.takeBatch(100) == 15 && total(reloaded) == 18,
            "an undelivered batch goes back to the queue before the next batch is taken");

        reloaded.requeueBatch();
        check(!reloaded.hasBatch() && total(reloaded) == 18, "stopping the machine returns the batch to the queue");

        reloaded.takeBatch(15);
        reloaded.deliverBatch(
            stack -> stack.copy()
                .setStackSize(stack.getStackSize() / 2));
        check(total(reloaded) == 3 + 1 + 5 + 1, "outputs storage refuses are queued again, accepted ones leave");

        AssemblerMatrixTasks failing = new AssemblerMatrixTasks();
        failing.accept(List.of(), List.of(stack("stick", 4), stack("plank", 6)));
        failing.takeBatch(10);
        int[] calls = { 0 };
        try {
            failing.deliverBatch(stack -> {
                if (calls[0]++ > 0) throw new IllegalStateException("grid went away");
                return null;
            });
        } catch (IllegalStateException expected) {}
        check(total(failing) == 6, "items not yet delivered when storage fails stay queued");

        AssemblerMatrixTasks returns = new AssemblerMatrixTasks();
        returns.accept(List.of(stack("bucket", 4)), List.of());
        returns.sendReturns(
            stack -> stack.copy()
                .setStackSize(1));
        check(returns.hasReturns() && total(returns) == 1, "container items storage refuses stay queued");
    }

    private static NBTTagCompound save(AssemblerMatrixTasks tasks) {
        NBTTagCompound root = new NBTTagCompound();
        tasks.writeToNBT(root);
        return root;
    }

    private static long total(AssemblerMatrixTasks tasks) {
        NBTTagCompound root = save(tasks);
        long total = 0;
        for (String key : new String[] { AssemblerMatrixTasks.BATCH_TAG, AssemblerMatrixTasks.PENDING_TAG,
            AssemblerMatrixTasks.RETURNS_TAG }) {
            NBTTagList list = root.getTagList(key, 10);
            for (int i = 0; i < list.tagCount(); i++) {
                total += list.getCompoundTagAt(i)
                    .getLong(COUNT_TAG);
            }
        }
        return total;
    }

    private static IAEItemStack load(NBTTagCompound tag) {
        return stack(tag.getString(ID_TAG), tag.getLong(COUNT_TAG));
    }

    private static IAEItemStack stack(String id, long count) {
        long[] size = { count };
        return (IAEItemStack) Proxy.newProxyInstance(
            AssemblerMatrixTasksContracts.class.getClassLoader(),
            new Class<?>[] { IAEItemStack.class },
            (proxy, method, args) -> switch (method.getName()) {
            case "getStackSize" -> size[0];
            case "setStackSize" -> {
            size[0] = (Long) args[0];
            yield proxy;
            }
            case "copy" -> stack(id, size[0]);
            case "writeToNBT" -> {
            ((NBTTagCompound) args[0]).setString(ID_TAG, id);
            ((NBTTagCompound) args[0]).setLong(COUNT_TAG, size[0]);
            yield null;
            }
            case "hashCode" -> System.identityHashCode(proxy);
            case "equals" -> proxy == args[0];
            case "toString" -> id + "x" + size[0];
            default -> throw new UnsupportedOperationException(method.getName());
            });
    }
}
