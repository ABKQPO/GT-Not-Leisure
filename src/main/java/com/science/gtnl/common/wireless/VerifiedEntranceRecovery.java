package com.science.gtnl.common.wireless;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class VerifiedEntranceRecovery<N, S> {

    record Candidate<N, S> (N node, S source) {}

    public enum Result {
        CONNECTED,
        WAITING,
        REJECTED
    }

    private static final int BATCH_SIZE = 16;
    private static final int RETRY_DELAY = 20;
    private final Object2LongOpenHashMap<Candidate<N, S>> retryAfter = new Object2LongOpenHashMap<>();
    private long tick;
    private int remaining = BATCH_SIZE;

    static boolean verified(boolean settled, boolean powered, boolean trial, boolean channelsEnabled, int usedChannels,
        int demand) {
        return settled && powered && !trial && (usedChannels > 0 || demand == 0 || !channelsEnabled);
    }

    void nextTick() {
        tick++;
        remaining = BATCH_SIZE;
    }

    Set<S> restore(List<Candidate<N, S>> candidates, Predicate<Candidate<N, S>> valid,
        Predicate<Candidate<N, S>> connected, Predicate<S> ready, Function<Candidate<N, S>, Result> connect) {
        retryAfter.keySet()
            .retainAll(new ObjectOpenHashSet<>(candidates));
        List<Candidate<N, S>> eligible = new ObjectArrayList<>();
        Object2BooleanOpenHashMap<S> readiness = new Object2BooleanOpenHashMap<>();
        for (Candidate<N, S> candidate : candidates) {
            if (!valid.test(candidate) || connected.test(candidate)) continue;
            eligible.add(candidate);
            // Snapshot every source before creating edges: the first edge queues native repathing.
            readiness.computeIfAbsent(candidate.source(), ready::test);
        }
        Set<S> pending = new ObjectOpenHashSet<>();
        for (Candidate<N, S> candidate : eligible) {
            if (!readiness.getBoolean(candidate.source()) || tick < retryAfter.getOrDefault(candidate, Long.MIN_VALUE))
                continue;
            if (remaining == 0) {
                pending.add(candidate.source());
                continue;
            }
            remaining--;
            switch (connect.apply(candidate)) {
                case CONNECTED -> retryAfter.removeLong(candidate);
                case REJECTED -> retryAfter.put(candidate, tick + RETRY_DELAY);
                case WAITING -> {
                    // A grid merge can replace the source cache before AE updates controller state next tick.
                    // No native connection was attempted, so neither spend budget nor impose failure backoff.
                    remaining++;
                    pending.add(candidate.source());
                }
            }
        }
        return pending;
    }

    void clear() {
        retryAfter.clear();
        tick = 0;
        remaining = BATCH_SIZE;
    }
}
