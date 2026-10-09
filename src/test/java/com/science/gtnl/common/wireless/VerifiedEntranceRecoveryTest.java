package com.science.gtnl.common.wireless;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.VerifiedEntranceRecovery.Candidate;
import com.science.gtnl.common.wireless.VerifiedEntranceRecovery.Result;
import com.science.gtnl.common.wireless.WirelessChannelPrototype.Address;
import com.science.gtnl.common.wireless.WirelessLinkData.Claim;
import com.science.gtnl.common.wireless.WirelessLinkData.Slot;

/** Exercises the production recovery policy; native AE routing and real chunk timing remain in-game checks. */
final class VerifiedEntranceRecoveryTest {

    static void run() throws Exception {
        savedElevenRestoreInOneBatch();
        batchBudgetAndRetry();
        unavailableAndInvalidTargets();
        replacedGridWaitsWithoutFailureBackoff();
        verificationGate();
        System.out.println("VerifiedEntranceRecoveryTest: saved batch restore, validation, retry and evidence passed.");
    }

    private static void savedElevenRestoreInOneBatch() throws Exception {
        Address source = new Address(0, 1, 64, 1, ForgeDirection.UNKNOWN);
        Slot slot = new Slot(ForgeDirection.UNKNOWN, false);
        List<Candidate<Integer, Address>> recovered = new ArrayList<>();
        for (int position = 0; position < 64; position++) {
            NBTTagCompound tile = new NBTTagCompound();
            WirelessLinkData.write(
                tile,
                Map.of(
                    slot,
                    new Claim(
                        "interface",
                        Set.of(source),
                        Set.of(),
                        Map.of(),
                        position < 11 ? Set.of(source) : Set.of())));
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            CompressedStreamTools.writeCompressed(tile, bytes);
            Claim loaded = WirelessLinkData
                .read(CompressedStreamTools.readCompressed(new ByteArrayInputStream(bytes.toByteArray())))
                .get(slot);
            if (loaded.entrances()
                .contains(source)) recovered.add(new Candidate<>(position, source));
        }
        check(recovered.size() == 11, "64 bound devices retain exactly 11 saved entry locations");
        var recovery = new VerifiedEntranceRecovery<Integer, Address>();
        Set<Integer> connected = new HashSet<>();
        boolean[] repathPending = { false };
        int[] readyReads = { 0 };
        recovery.restore(recovered, candidate -> true, candidate -> connected.contains(candidate.node()), frequency -> {
            readyReads[0]++;
            return !repathPending[0];
        }, candidate -> {
            repathPending[0] = true;
            return connected.add(candidate.node()) ? Result.CONNECTED : Result.REJECTED;
        });
        check(
            connected.size() == 11 && readyReads[0] == 1,
            "First reconnection queues repath but cannot serialize the remaining ten saved entries");
        recovery.restore(
            recovered,
            candidate -> true,
            candidate -> connected.contains(candidate.node()),
            frequency -> true,
            candidate -> { throw new AssertionError("Existing entries must not be duplicated"); });
    }

    private static void batchBudgetAndRetry() {
        var recovery = new VerifiedEntranceRecovery<Integer, String>();
        List<Candidate<Integer, String>> candidates = new ArrayList<>();
        for (int i = 0; i < 35; i++) candidates.add(new Candidate<>(i, i < 20 ? "a" : "b"));
        Set<Integer> connected = new HashSet<>();
        int[] rejected = { 0 };
        java.util.function.Function<Candidate<Integer, String>, Result> connect = candidate -> {
            if (candidate.node() == 0) {
                rejected[0]++;
                return Result.REJECTED;
            }
            return connected.add(candidate.node()) ? Result.CONNECTED : Result.REJECTED;
        };
        var pending = recovery.restore(candidates, c -> true, c -> connected.contains(c.node()), s -> true, connect);
        check(
            connected.size() == 15 && pending.equals(Set.of("a", "b")),
            "Failed native connections count against global budget");
        recovery.restore(candidates, c -> true, c -> connected.contains(c.node()), s -> true, connect);
        check(connected.size() == 15 && rejected[0] == 1, "Repeated GUI reconciles cannot reset the per-tick budget");
        recovery.nextTick();
        recovery.restore(candidates, c -> true, c -> connected.contains(c.node()), s -> true, connect);
        check(connected.size() == 31, "Restoration makes progress across multiple sources");
        recovery.nextTick();
        recovery.restore(candidates, c -> true, c -> connected.contains(c.node()), s -> true, connect);
        check(connected.size() == 34 && rejected[0] == 1, "A rejected target cannot block later valid entries");
        for (int tick = 2; tick < 20; tick++) recovery.nextTick();
        recovery.restore(candidates, c -> true, c -> connected.contains(c.node()), s -> true, connect);
        check(rejected[0] == 2, "Native security/availability rejection retries only after the cooldown");
        recovery.clear();
        connected.clear();
        recovery.restore(candidates, c -> true, c -> false, s -> true, connect);
        check(rejected[0] == 3 && connected.size() == 15, "World reset clears runtime budget and cooldown state");
    }

    private static void unavailableAndInvalidTargets() {
        var recovery = new VerifiedEntranceRecovery<Integer, String>();
        var candidates = List
            .of(new Candidate<>(1, "delayed"), new Candidate<>(2, "ready"), new Candidate<>(3, "ready"));
        Set<Integer> connected = new HashSet<>();
        recovery.restore(
            candidates,
            c -> c.node() != 2,
            c -> false,
            s -> s.equals("ready"),
            c -> connected.add(c.node()) ? Result.CONNECTED : Result.REJECTED);
        check(
            connected.equals(Set.of(3)),
            "Invalid nodes and unloaded sources are skipped without blocking valid nodes");
        recovery.nextTick();
        recovery.restore(
            candidates,
            c -> c.node() != 2,
            c -> connected.contains(c.node()),
            s -> true,
            c -> connected.add(c.node()) ? Result.CONNECTED : Result.REJECTED);
        check(connected.equals(Set.of(1, 3)), "A late-loaded source can recover its retained hint");
        recovery.nextTick();
        // A paused/conflicting/blocked cluster supplies no candidates; its persisted hints are not changed.
        recovery.restore(
            List.of(),
            c -> true,
            c -> false,
            s -> true,
            c -> { throw new AssertionError("Ineligible cluster restored"); });
    }

    private static void verificationGate() {
        check(
            VerifiedEntranceRecovery.verified(true, true, false, true, 8, 64),
            "Settled channel-carrying entries are saved");
        check(
            !VerifiedEntranceRecovery.verified(false, true, false, true, 8, 64),
            "Stale channels during repath are not proof");
        check(!VerifiedEntranceRecovery.verified(true, false, false, true, 8, 64), "Power-off snapshots are not proof");
        check(
            !VerifiedEntranceRecovery.verified(true, true, true, true, 8, 64),
            "A pending trial is not persisted before planner approval");
        check(
            !VerifiedEntranceRecovery.verified(true, true, false, true, 0, 64),
            "An unused speculative survivor is not saved");
        check(
            VerifiedEntranceRecovery.verified(true, true, false, true, 0, 0),
            "An empty but connected base can retain its anchor");
        check(
            VerifiedEntranceRecovery.verified(true, true, false, false, 0, 64),
            "Channel-disabled networks can retain their anchor");
    }

    private static void replacedGridWaitsWithoutFailureBackoff() {
        var recovery = new VerifiedEntranceRecovery<Integer, String>();
        List<Candidate<Integer, String>> candidates = new ArrayList<>();
        for (int i = 0; i < 11; i++) candidates.add(new Candidate<>(i, "source"));
        Set<Integer> connected = new HashSet<>();
        var pending = recovery.restore(candidates, c -> true, c -> false, s -> true, c -> {
            if (!connected.isEmpty()) return Result.WAITING;
            connected.add(c.node());
            return Result.CONNECTED;
        });
        check(
            connected.size() == 1 && pending.contains("source"),
            "Grid replacement waits for controller state before restoring the remaining entries");
        recovery.nextTick();
        recovery.restore(candidates, c -> true, c -> connected.contains(c.node()), s -> true, c -> {
            connected.add(c.node());
            return Result.CONNECTED;
        });
        check(connected.size() == 11, "Ready replacement grid restores next tick without a 20-tick failure penalty");
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
