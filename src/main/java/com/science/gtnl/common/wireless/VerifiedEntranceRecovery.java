package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/** Restores validated saved locations in bounded batches, without serial speculative allocation trials. */
final class VerifiedEntranceRecovery<N, S> {

    record Candidate<N, S> (N node, S source) {}

    enum Result {
        CONNECTED,
        WAITING,
        REJECTED
    }

    private static final int BATCH_SIZE = 16;
    private static final int RETRY_DELAY = 20;
    private final Map<Candidate<N, S>, Long> retryAfter = new HashMap<>();
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

    /** Returns sources whose saved entries must take priority over speculative expansion on the next tick. */
    Set<S> restore(List<Candidate<N, S>> candidates, Predicate<Candidate<N, S>> valid,
        Predicate<Candidate<N, S>> connected, Predicate<S> ready, Function<Candidate<N, S>, Result> connect) {
        retryAfter.keySet()
            .retainAll(new HashSet<>(candidates));
        List<Candidate<N, S>> eligible = new ArrayList<>();
        Map<S, Boolean> readiness = new HashMap<>();
        for (Candidate<N, S> candidate : candidates) {
            if (!valid.test(candidate) || connected.test(candidate)) continue;
            eligible.add(candidate);
            // Snapshot every source before creating edges: the first edge queues native repathing.
            readiness.computeIfAbsent(candidate.source(), ready::test);
        }
        Set<S> pending = new HashSet<>();
        for (Candidate<N, S> candidate : eligible) {
            if (!readiness.get(candidate.source()) || tick < retryAfter.getOrDefault(candidate, Long.MIN_VALUE))
                continue;
            if (remaining == 0) {
                pending.add(candidate.source());
                continue;
            }
            remaining--;
            switch (connect.apply(candidate)) {
                case CONNECTED -> retryAfter.remove(candidate);
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
