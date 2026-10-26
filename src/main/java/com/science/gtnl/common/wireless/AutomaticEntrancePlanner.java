package com.science.gtnl.common.wireless;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public class AutomaticEntrancePlanner<N> {

    private static final int TOPOLOGY_DELAY = 10;
    private static final int NEXT_TRIAL_DELAY = 1;
    private static final int FAILED_CONNECTION_DELAY = 20;
    private static final int CHANNELS_PER_ENTRANCE = ChannelBudget.CHANNELS_PER_FACE;
    private static final int MAX_BATCH = 8;

    public enum Status {
        WAITING,
        DISABLED,
        UNPOWERED,
        SATISFIED,
        BUDGET_FULL,
        ENTRANCE_LIMIT,
        EXPANDING,
        ROLLING_BACK,
        NO_CANDIDATE
    }

    public enum Action {
        NONE,
        ADD,
        KEEP,
        REMOVE
    }

    record Observation(long context, long revision, long tick, boolean channelsEnabled, boolean powered,
        boolean settled, int capacity, int used, int served, int missing, int remainingEntrances) {}

    record Decision<N> (Action action, List<N> targets, IntList entranceIds) {}

    private record Trial<N> (List<N> targets, IntList ids, long revision, int served, int used) {}

    private final Set<N> rejected = new ReferenceOpenHashSet<>();
    private Trial<N> trial;
    private long context;
    private boolean initialized;
    private boolean exhausted;
    private long waitForRevision = -1;
    private long nextActionTick;
    private Status status = Status.WAITING;

    Status status() {
        return status;
    }

    boolean hasTrial() {
        return trial != null;
    }

    IntList pendingEntrances() {
        return trial == null ? IntList.of() : trial.ids();
    }

    boolean isPending(int entranceId) {
        return trial != null && trial.ids()
            .contains(entranceId);
    }

    void entranceLost() {
        trial = null;
        waitForRevision = -1;
        status = Status.WAITING;
    }

    Decision<N> decide(Observation observation, Function<Predicate<N>, N> selectCandidate) {
        if (!initialized || context != observation.context()) {
            context = observation.context();
            initialized = true;
            rejected.clear();
            exhausted = false;
            trial = null;
            waitForRevision = -1;
            nextActionTick = observation.tick() + TOPOLOGY_DELAY;
        }
        if (!observation.channelsEnabled()) return idle(Status.DISABLED);
        if (!observation.powered()) {
            if (trial != null) return rollback(observation);
            return idle(Status.UNPOWERED);
        }
        if (!observation.settled()) return idle(Status.WAITING);
        if (trial != null) {
            if (observation.revision() <= trial.revision()) return idle(Status.WAITING);
            if (observation.served() > trial.served() && observation.used() > trial.used()) {
                Decision<N> result = new Decision<>(Action.KEEP, trial.targets(), trial.ids());
                trial = null;
                nextActionTick = observation.tick() + NEXT_TRIAL_DELAY;
                status = Status.EXPANDING;
                return result;
            }
            return rollback(observation);
        }
        if (observation.revision() <= waitForRevision) return idle(Status.WAITING);
        if (observation.missing() == 0) return idle(Status.SATISFIED);
        if (observation.used() >= observation.capacity()) return idle(Status.BUDGET_FULL);
        if (observation.remainingEntrances() <= 0) return idle(Status.ENTRANCE_LIMIT);
        if (observation.tick() < nextActionTick) return idle(Status.WAITING);
        if (exhausted) return idle(Status.NO_CANDIDATE);
        Set<N> picked = new ReferenceOpenHashSet<>();
        List<N> chosen = new ObjectArrayList<>();
        for (int index = batchSize(observation); index > 0; index--) {
            N candidate = selectCandidate.apply(node -> !rejected.contains(node) && !picked.contains(node));
            if (candidate == null) break;
            picked.add(candidate);
            chosen.add(candidate);
        }
        if (chosen.isEmpty()) {
            exhausted = true;
            return idle(Status.NO_CANDIDATE);
        }
        status = Status.EXPANDING;
        return new Decision<>(Action.ADD, List.copyOf(chosen), IntList.of());
    }

    static int batchSize(Observation observation) {
        int demand = ceilDiv(observation.missing(), CHANNELS_PER_ENTRANCE);
        int budget = ceilDiv(observation.capacity() - observation.used(), CHANNELS_PER_ENTRANCE);
        int batch = Math.min(MAX_BATCH, Math.min(observation.remainingEntrances(), Math.min(demand, budget)));
        return Math.max(1, batch);
    }

    private static int ceilDiv(int value, int divisor) {
        return value <= 0 ? 0 : (value + divisor - 1) / divisor;
    }

    void created(List<N> targets, IntList entranceIds, Observation before) {
        trial = new Trial<>(
            List.copyOf(targets),
            IntList.of(entranceIds.toIntArray()),
            before.revision(),
            before.served(),
            before.used());
        status = Status.EXPANDING;
    }

    void failed(N target, long tick) {
        rejected.add(target);
        nextActionTick = tick + FAILED_CONNECTION_DELAY;
        status = Status.WAITING;
    }

    void reject(N target) {
        rejected.add(target);
    }

    private Decision<N> rollback(Observation observation) {
        Decision<N> result = new Decision<>(Action.REMOVE, trial.targets(), trial.ids());
        rejected.addAll(trial.targets());
        trial = null;
        waitForRevision = observation.revision();
        nextActionTick = observation.tick() + NEXT_TRIAL_DELAY;
        status = Status.ROLLING_BACK;
        return result;
    }

    private Decision<N> idle(Status newStatus) {
        status = newStatus;
        return new Decision<>(Action.NONE, List.of(), IntList.of());
    }
}
