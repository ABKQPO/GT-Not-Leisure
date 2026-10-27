package com.science.gtnl.common.meminiterminal;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.ScienceNotLeisure;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;

public class MiniTerminalOutbound {

    private static final int ASYNC_ENTRIES = 2048;
    private static final AtomicInteger THREAD_INDEX = new AtomicInteger();
    private static final ExecutorService ENCODER = Executors.newFixedThreadPool(
        Math.max(
            1,
            Math.min(
                4,
                Runtime.getRuntime()
                    .availableProcessors() / 2)),
        task -> {
            Thread thread = new Thread(task, "GTNL Mini Terminal Encoder #" + THREAD_INDEX.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        });
    private static final Map<UUID, ObjectArrayFIFOQueue<Pending>> QUEUES = new Object2ObjectOpenHashMap<>();

    public interface EncodeFailure {

        void failed(EntityPlayerMP player, long session, Throwable cause);
    }

    static void send(EntityPlayerMP player, NBTTagCompound data) {
        enqueue(player, new Pending(data, false, CompletableFuture.completedFuture(null), null));
    }

    static void sendParts(EntityPlayerMP player, NBTTagCompound header, List<MiniTerminalUpdatePacket.Entry> entries,
        boolean snapshot, EncodeFailure failure) {
        CompletableFuture<List<byte[]>> parts;
        if (entries.size() < ASYNC_ENTRIES) {
            parts = new CompletableFuture<>();
            try {
                parts.complete(MiniTerminalUpdatePacket.encode(entries));
            } catch (Exception exception) {
                parts.completeExceptionally(exception);
            }
        } else {
            parts = CompletableFuture.supplyAsync(() -> {
                try {
                    return MiniTerminalUpdatePacket.encode(entries);
                } catch (Exception exception) {
                    throw new CompletionException(exception);
                }
            }, ENCODER);
        }
        enqueue(player, new Pending(header, snapshot, parts, failure));
    }

    static void flush(EntityPlayerMP player) {
        ObjectArrayFIFOQueue<Pending> queue = QUEUES.get(player.getUniqueID());
        if (queue == null) return;
        while (!queue.isEmpty() && queue.first().parts.isDone()) {
            queue.dequeue()
                .deliver(player);
        }
        if (queue.isEmpty()) QUEUES.remove(player.getUniqueID());
    }

    static void forget(UUID player) {
        QUEUES.remove(player);
    }

    private static void enqueue(EntityPlayerMP player, Pending pending) {
        QUEUES.computeIfAbsent(player.getUniqueID(), key -> new ObjectArrayFIFOQueue<>())
            .enqueue(pending);
        flush(player);
    }

    private record Pending(NBTTagCompound header, boolean snapshot, CompletableFuture<List<byte[]>> parts,
        EncodeFailure failure) {

        void deliver(EntityPlayerMP player) {
            List<byte[]> encoded;
            try {
                encoded = parts.join();
            } catch (CompletionException exception) {
                Throwable cause = exception.getCause() == null ? exception : exception.getCause();
                if (failure != null) failure.failed(player, header.getLong("session"), cause);
                else ScienceNotLeisure.LOG.warn("Cannot encode ME mini portable terminal update", cause);
                return;
            }
            if (encoded == null) {
                ScienceNotLeisure.network.sendTo(new MiniTerminalUpdatePacket(header), player);
                return;
            }
            for (int index = 0; index < encoded.size(); index++) {
                NBTTagCompound tag = (NBTTagCompound) header.copy();
                tag.setBoolean("snapshot", snapshot);
                tag.setBoolean("reset", snapshot && index == 0);
                tag.setBoolean("last", index == encoded.size() - 1);
                tag.setInteger("part", index);
                tag.setInteger("parts", encoded.size());
                ScienceNotLeisure.network.sendTo(new MiniTerminalUpdatePacket(tag, encoded.get(index)), player);
            }
        }
    }
}
