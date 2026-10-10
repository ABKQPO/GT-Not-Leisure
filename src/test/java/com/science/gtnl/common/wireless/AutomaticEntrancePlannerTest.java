package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.science.gtnl.common.wireless.AutomaticEntrancePlanner.Action;
import com.science.gtnl.common.wireless.AutomaticEntrancePlanner.Observation;
import com.science.gtnl.common.wireless.AutomaticEntrancePlanner.Status;

/** Feedback protocol and candidate placement. AE2 remains the actual routing/allocation authority. */
public final class AutomaticEntrancePlannerTest {

    private static final class Node {

        final int order;
        final int rank;
        final List<Node> neighbours = new ArrayList<>();

        Node(int order, int rank) {
            this.order = order;
            this.rank = rank;
        }
    }

    public static void main(String[] args) {
        freshAllocationAndGain();
        restoreElevenEntrances(1);
        restoreElevenEntrances(7);
        rollbackWaitsForAllocationInsteadOfHalfASecond();
        incrementalExpansionSharesWiredBudget();
        noGainRollsBackOnce();
        noBudgetOrPowerMeansNoExpansion();
        powerLossDuringTrial();
        changedTopologyInvalidatesBaseline();
        deviceCountsDoNotOverrideChannelAccounting();
        failedConnectionsTryOtherCandidates();
        cableCandidatesNearMissingDevices();
        System.out.println(
            "AutomaticEntrancePlannerTest: fresh-result gating, budget/power stops, rollback and candidate checks passed.");
    }

    /** Simulates allocation latency, not AE's route selection or real-world loading time. */
    private static void restoreElevenEntrances(int allocationDelay) {
        var planner = new AutomaticEntrancePlanner<Node>();
        int served = 4, entrances = 1;
        long revision = 1;
        int allocationAt = -1;
        for (int tick = 0; tick < 120; tick++) {
            if (tick == allocationAt) {
                served += 6;
                revision++;
            }
            boolean waiting = allocationAt >= 0 && tick < allocationAt;
            var before = new Observation(
                1,
                revision,
                tick,
                true,
                true,
                !waiting,
                192,
                served,
                served,
                64 - served,
                false);
            int candidateOrder = entrances;
            var decision = planner.decide(before, allowed -> {
                check(planner.pendingEntrance() < 0, "Only one unconfirmed entrance per source grid");
                return new Node(candidateOrder, 0);
            });
            if (waiting) check(decision.action() == Action.NONE, "Even the faster planner must wait for AE allocation");
            if (decision.action() == Action.ADD) {
                planner.created(decision.target(), entrances, before);
                allocationAt = tick + allocationDelay;
            } else if (decision.action() == Action.KEEP) {
                entrances++;
                allocationAt = -1;
                if (entrances == 11) {
                    check(
                        tick <= 10 + 10 * (allocationDelay + 1),
                        "Eleven useful entrances must not accumulate ten half-second waits");
                    return;
                }
            } else check(decision.action() == Action.NONE, "Every simulated trial has a real channel gain");
        }
        throw new AssertionError("Eleven-entrance restoration exceeded the simulation deadline");
    }

    private static void rollbackWaitsForAllocationInsteadOfHalfASecond() {
        var planner = new AutomaticEntrancePlanner<Node>();
        Node rejected = new Node(1, 0), alternative = new Node(2, 0);
        begin(planner, rejected);
        check(
            planner.decide(ready(2, 30, 32, 32, 32), allowed -> rejected)
                .action() == Action.REMOVE,
            "A failed route is removed");
        check(
            planner.decide(
                ready(2, 31, 32, 32, 32),
                allowed -> { throw new AssertionError("Old allocation after rollback must never select an entrance"); })
                .action() == Action.NONE,
            "Rollback still needs a fresh allocation");
        check(planner.decide(ready(3, 32, 32, 32, 32), allowed -> {
            check(!allowed.test(rejected), "Failed route remains rejected");
            return alternative;
        })
            .action() == Action.ADD, "A fresh rollback allocation allows trying the next route promptly");
    }

    private static void freshAllocationAndGain() {
        var planner = new AutomaticEntrancePlanner<Node>();
        Node target = new Node(1, 0);
        Observation before = ready(1, 10, 32, 32, 32);
        planner.decide(before, allowed -> target);
        before = ready(1, 20, 32, 32, 32);
        var add = planner.decide(before, allowed -> target);
        check(add.action() == Action.ADD, "32 served and 32 missing should try an additional entrance");
        planner.created(target, 42, before);
        check(
            planner.decide(ready(1, 30, 64, 64, 0), allowed -> target)
                .action() == Action.NONE,
            "Even apparently improved counters cannot justify a trial without a newer allocation generation");
        Observation queued = new Observation(1, 2, 31, true, true, false, 192, 64, 64, 0, false);
        check(
            planner.decide(queued, allowed -> target)
                .action() == Action.NONE,
            "Queued pathing is not settled");
        check(
            planner.decide(ready(2, 32, 64, 64, 0), allowed -> target)
                .action() == Action.KEEP,
            "A real 32-to-64 channel increase keeps the trial entrance");
        check(
            planner.decide(ready(2, 50, 64, 64, 0), allowed -> target)
                .action() == Action.NONE && planner.status() == Status.SATISFIED,
            "Satisfied demand does not add more entrances");
    }

    private static void incrementalExpansionSharesWiredBudget() {
        var planner = new AutomaticEntrancePlanner<Node>();
        int remoteServed = 32;
        int wired = 32;
        long revision = 1;
        long tick = 0;
        planner.decide(ready(revision, tick, wired + remoteServed, remoteServed, 192 - remoteServed), allowed -> null);
        for (int entrance = 0; entrance < 4; entrance++) {
            tick += 20;
            Node candidate = new Node(entrance, 0);
            Observation before = ready(revision, tick, wired + remoteServed, remoteServed, 192 - remoteServed);
            check(
                planner.decide(before, allowed -> candidate)
                    .action() == Action.ADD,
                "Unmet demand continues after a useful entrance");
            planner.created(candidate, entrance + 10, before);
            remoteServed += 32;
            revision++;
            tick++;
            check(
                planner
                    .decide(
                        ready(revision, tick, wired + remoteServed, remoteServed, 192 - remoteServed),
                        allowed -> candidate)
                    .action() == Action.KEEP,
                "Each additional entrance is independently validated against the next allocation");
        }
        planner.decide(
            ready(revision, tick + 20, wired + remoteServed, remoteServed, 192 - remoteServed),
            allowed -> { throw new AssertionError("Wireless planning ignored existing wired consumption"); });
        check(
            remoteServed == 160 && planner.status() == Status.BUDGET_FULL,
            "32 wired plus 160 wireless exhaust 192, even with another 32 wireless consumers waiting");
    }

    private static void noGainRollsBackOnce() {
        var planner = new AutomaticEntrancePlanner<Node>();
        Node target = new Node(1, 0);
        begin(planner, target);
        var removed = planner.decide(ready(2, 30, 32, 32, 32), allowed -> target);
        check(removed.action() == Action.REMOVE && removed.entranceId() == 42, "No gain removes only the trial's ID");
        check(
            planner
                .decide(
                    ready(2, 60, 32, 32, 32),
                    allowed -> { throw new AssertionError("Stale rollback result used"); })
                .action() == Action.NONE,
            "Rollback itself must get a fresh allocation before trying anything else");
        planner.decide(ready(3, 70, 32, 32, 32), allowed -> allowed.test(target) ? target : null);
        check(planner.status() == Status.NO_CANDIDATE, "Failed candidate is remembered after rollback");
        planner.decide(
            ready(4, 100, 32, 32, 32),
            allowed -> { throw new AssertionError("Exhausted layout retried indefinitely"); });
        check(
            planner.status() == Status.NO_CANDIDATE,
            "Additional allocation generations alone do not reset rejection history");
    }

    private static void noBudgetOrPowerMeansNoExpansion() {
        for (int scenario = 0; scenario < 4; scenario++) {
            var planner = new AutomaticEntrancePlanner<Node>();
            int used = scenario == 0 ? 192 : 32;
            Observation observation = new Observation(
                1,
                1,
                0,
                scenario != 2,
                scenario != 1,
                true,
                192,
                used,
                used,
                32,
                scenario == 3);
            planner.decide(observation, allowed -> { throw new AssertionError("Gated planner selected a candidate"); });
            Status expected = switch (scenario) {
                case 0 -> Status.BUDGET_FULL;
                case 1 -> Status.UNPOWERED;
                case 2 -> Status.DISABLED;
                default -> Status.ENTRANCE_LIMIT;
            };
            check(
                planner.status() == expected,
                "224 demand against 192 budget, power loss, disabled channels and global limit are gates");
        }
    }

    private static void powerLossDuringTrial() {
        var planner = new AutomaticEntrancePlanner<Node>();
        Node target = new Node(1, 0);
        begin(planner, target);
        Observation noPower = new Observation(1, 2, 30, true, false, true, 192, 64, 64, 0, false);
        check(
            planner.decide(noPower, allowed -> target)
                .action() == Action.REMOVE,
            "A speculative entrance is rolled back if the network loses power");
    }

    private static void changedTopologyInvalidatesBaseline() {
        var planner = new AutomaticEntrancePlanner<Node>();
        Node target = new Node(1, 0);
        begin(planner, target);
        Observation changed = new Observation(2, 2, 30, true, true, true, 192, 32, 32, 40, false);
        check(
            planner.decide(changed, allowed -> target)
                .action() == Action.NONE && planner.pendingEntrance() == -1,
            "Split/merge/demand changes invalidate comparison; do not delete an entry using an obsolete baseline");
        changed = new Observation(2, 3, 50, true, true, true, 192, 32, 32, 40, false);
        check(
            planner.decide(changed, allowed -> target)
                .action() == Action.ADD,
            "New physical layout is eligible for planning");
        planner.created(target, 43, changed);
        planner.entranceLost();
        check(planner.pendingEntrance() == -1, "Removed or unloaded entrances cannot affect a later replacement ID");
    }

    private static void deviceCountsDoNotOverrideChannelAccounting() {
        var planner = new AutomaticEntrancePlanner<Node>();
        Node target = new Node(1, 0);
        begin(planner, target);
        check(
            planner.decide(ready(2, 30, 32, 64, 0), allowed -> target)
                .action() == Action.REMOVE,
            "Multi-block or rerouting status gains alone cannot substitute for increased native allocated channels");
    }

    private static void failedConnectionsTryOtherCandidates() {
        var planner = new AutomaticEntrancePlanner<Node>();
        Node rejected = new Node(1, 0), alternative = new Node(2, 1);
        planner.decide(ready(1, 0, 32, 32, 32), allowed -> rejected);
        planner.decide(ready(1, 20, 32, 32, 32), allowed -> rejected);
        planner.failed(rejected, 20);
        var decision = planner.decide(ready(1, 45, 32, 32, 32), allowed -> {
            check(
                !allowed.test(rejected),
                "Native connection failures blacklist that candidate for the unchanged layout");
            return allowed.test(alternative) ? alternative : null;
        });
        check(
            decision.action() == Action.ADD && decision.target() == alternative,
            "Failure does not prevent trying another viable node");
    }

    private static void cableCandidatesNearMissingDevices() {
        Node denseA = new Node(0, 0), denseB = new Node(1, 0), consumer = new Node(2, 2);
        join(denseA, denseB);
        join(denseB, consumer);
        Set<Node> members = members(denseA, denseB, consumer);
        Predicate<Node> eligible = node -> node != denseA;
        Node chosen = EntranceCandidates.select(
            members,
            List.of(consumer),
            List.of(denseA),
            node -> node.neighbours,
            eligible,
            node -> node.rank,
            Comparator.comparingInt(node -> node.order));
        check(chosen == denseB, "Missing consumers prefer the nearby second dense cable, not the existing entrance");
        chosen = EntranceCandidates.select(
            members,
            List.of(consumer),
            List.of(denseA),
            node -> node.neighbours,
            node -> node == consumer,
            node -> node.rank,
            Comparator.comparingInt(node -> node.order));
        check(chosen == consumer, "When nearby cable attempts fail, direct device entry is a valid fallback");
        Node normalA = new Node(3, 1), normalB = new Node(4, 1), ninthDevice = new Node(5, 2), foreign = new Node(6, 0);
        join(normalA, normalB);
        join(normalB, ninthDevice);
        join(ninthDevice, foreign); // Even an adapter exposing this neighbour cannot escape the physical member set.
        chosen = EntranceCandidates.select(
            members(normalA, normalB, ninthDevice),
            List.of(ninthDevice),
            List.of(normalA),
            node -> node.neighbours,
            node -> node != normalA,
            node -> node.rank,
            Comparator.comparingInt(node -> node.order));
        check(
            chosen == normalB,
            "Choose an additional inlet near the ninth device without changing normal cable capacity or leaving the cluster");
    }

    private static Observation ready(long revision, long tick, int used, int served, int missing) {
        return new Observation(1, revision, tick, true, true, true, 192, used, served, missing, false);
    }

    private static void begin(AutomaticEntrancePlanner<Node> planner, Node target) {
        planner.decide(ready(1, 0, 32, 32, 32), allowed -> target);
        Observation before = ready(1, 20, 32, 32, 32);
        check(
            planner.decide(before, allowed -> target)
                .action() == Action.ADD,
            "Trial setup");
        planner.created(target, 42, before);
    }

    private static void join(Node a, Node b) {
        a.neighbours.add(b);
        b.neighbours.add(a);
    }

    private static Set<Node> members(Node... nodes) {
        Set<Node> result = Collections.newSetFromMap(new IdentityHashMap<>());
        Collections.addAll(result, nodes);
        return result;
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
