package com.science.gtnl.common.wireless;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/** One speculative entrance per source grid; only a fresh, successful AE2 allocation can justify keeping it. */
public final class AutomaticEntrancePlanner<N> {

    private static final int TOPOLOGY_DELAY = 10;
    private static final int NEXT_TRIAL_DELAY = 1;
    private static final int FAILED_CONNECTION_DELAY = 20;

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

    public record Observation(long context, long revision, long tick, boolean channelsEnabled, boolean powered,
        boolean settled, int capacity, int used, int served, int missing, boolean entranceLimit) {}

    public record Decision<N> (Action action, N target, int entranceId) {}

    private record Trial<N> (N target, int id, long revision, int served, int used) {}

    private final Set<N> rejected = Collections.newSetFromMap(new IdentityHashMap<>());
    private Trial<N> trial;
    private long context;
    private boolean initialized;
    private boolean exhausted;
    private long waitForRevision = -1;
    private long nextActionTick;
    private Status status = Status.WAITING;

    public Status status() {
        return status;
    }

    public int pendingEntrance() {
        return trial == null ? -1 : trial.id();
    }

    /** A removed/unloaded entrance must never be evaluated against an unrelated replacement. */
    public void entranceLost() {
        trial = null;
        waitForRevision = -1;
        status = Status.WAITING;
    }

    public Decision<N> decide(Observation observation, Function<Predicate<N>, N> selectCandidate) {
        if (!initialized || context != observation.context()) {
            context = observation.context();
            initialized = true;
            rejected.clear();
            exhausted = false;
            // The physical layout/demand changed during the trial. Keep any surviving entry; the old baseline is
            // invalid.
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
                Decision<N> result = new Decision<>(Action.KEEP, trial.target(), trial.id());
                trial = null;
                // A fresh, settled allocation already confirmed this entrance. Do not wait another half-second.
                nextActionTick = observation.tick() + NEXT_TRIAL_DELAY;
                status = Status.EXPANDING;
                return result;
            }
            return rollback(observation);
        }
        if (observation.revision() <= waitForRevision) return idle(Status.WAITING);
        if (observation.missing() == 0) return idle(Status.SATISFIED);
        if (observation.used() >= observation.capacity()) return idle(Status.BUDGET_FULL);
        if (observation.entranceLimit()) return idle(Status.ENTRANCE_LIMIT);
        if (observation.tick() < nextActionTick) return idle(Status.WAITING);
        if (exhausted) return idle(Status.NO_CANDIDATE);
        N candidate = selectCandidate.apply(node -> !rejected.contains(node));
        if (candidate == null) {
            exhausted = true;
            return idle(Status.NO_CANDIDATE);
        }
        status = Status.EXPANDING;
        return new Decision<>(Action.ADD, candidate, -1);
    }

    public void created(N target, int entranceId, Observation before) {
        trial = new Trial<>(target, entranceId, before.revision(), before.served(), before.used());
        status = Status.EXPANDING;
    }

    public void failed(N target, long tick) {
        rejected.add(target);
        nextActionTick = tick + FAILED_CONNECTION_DELAY;
        status = Status.WAITING;
    }

    private Decision<N> rollback(Observation observation) {
        Decision<N> result = new Decision<>(Action.REMOVE, trial.target(), trial.id());
        rejected.add(trial.target());
        trial = null;
        waitForRevision = observation.revision();
        // waitForRevision still prevents another trial until AE has finished allocating after removal.
        nextActionTick = observation.tick() + NEXT_TRIAL_DELAY;
        status = Status.ROLLING_BACK;
        return result;
    }

    private Decision<N> idle(Status newStatus) {
        status = newStatus;
        return new Decision<>(Action.NONE, null, -1);
    }
}
