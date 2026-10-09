package com.science.gtnl.common.wireless;

import java.util.HashSet;
import java.util.Set;

/** Dependency-free policy checks. Actual AE2 routing is covered by the in-game acceptance procedure. */
public final class ChannelBudgetTest {

    public static void main(String[] args) {
        var a = new ChannelBudget.Position(0, 0, 64, 0);
        check(ChannelBudget.capacityOf(Set.of()) == 0, "No controllers supply no channels");
        check(ChannelBudget.capacityOf(Set.of(a)) == 192, "Single controller exposes six faces");
        check(
            ChannelBudget.capacityOf(Set.of(a, new ChannelBudget.Position(0, 1, 64, 0))) == 320,
            "Two adjacent controllers share two internal faces");
        check(
            ChannelBudget.capacityOf(Set.of(a, new ChannelBudget.Position(1, 1, 64, 0))) == 384,
            "Coordinates in different dimensions are not neighbors");
        Set<ChannelBudget.Position> cube = new HashSet<>();
        for (int x = 0; x < 3; x++) for (int y = 0; y < 3; y++) for (int z = 0; z < 3; z++) {
            cube.add(new ChannelBudget.Position(0, x, y, z));
        }
        // Geometry only: AE2 remains responsible for validating whether a controller structure is legal.
        check(ChannelBudget.capacityOf(cube) == 54 * 32, "Internal faces cannot mint wireless capacity");
        ChannelBudget budget = new ChannelBudget(192);
        // A wired branch consumes 32; six wireless branches request another 192.
        for (int i = 0; i < 32; i++) budget.recordAllocation();
        int acceptedWireless = 0;
        for (int branch = 0; branch < 6; branch++) for (int i = 0; i < 32; i++) {
            if (budget.hasRemaining()) {
                budget.recordAllocation();
                acceptedWireless++;
            }
        }
        check(acceptedWireless == 160 && budget.allocated() == 192, "Wired and wireless share one total budget");
        boolean rejected = false;
        try {
            budget.recordAllocation();
        } catch (IllegalStateException expected) {
            rejected = true;
        }
        check(rejected, "Cannot overdraw the budget");
        check(!new ChannelBudget(0).hasRemaining(), "Unsupported structures fail closed");
        check(new ChannelBudget(192).allocated() == 0, "Repath starts a new allocation pass");
        System.out.println("ChannelBudgetTest: all policy checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
