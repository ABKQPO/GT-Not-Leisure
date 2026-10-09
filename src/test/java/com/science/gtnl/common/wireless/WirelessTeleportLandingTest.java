package com.science.gtnl.common.wireless;

import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.WirelessTeleportLanding.Position;

/** Search policy regression; world collision and cross-dimension movement also require game validation. */
public final class WirelessTeleportLandingTest {

    public static void main(String[] args) {
        Position front = new Position(10, 64, 9);
        Position other = new Position(9, 64, 9);
        check(
            front.equals(
                WirelessTeleportLanding.find(
                    10,
                    64,
                    10,
                    ForgeDirection.NORTH,
                    pos -> Set.of(front, other)
                        .contains(pos))),
            "A safe device front takes priority over another free space");
        check(
            other.equals(WirelessTeleportLanding.find(10, 64, 10, ForgeDirection.NORTH, other::equals)),
            "An obstructed or dangerous front falls back to a checked neighbouring position");
        Position above = new Position(10, 65, 10);
        check(
            above.equals(WirelessTeleportLanding.find(10, 64, 10, ForgeDirection.DOWN, above::equals)),
            "A device with no walkable neighbours can use a clear supported position above it");
        Position raised = new Position(-18, 65, -18);
        check(
            raised.equals(WirelessTeleportLanding.find(-16, 64, -16, ForgeDirection.UNKNOWN, raised::equals)),
            "Negative coordinates and a nearby raised platform are supported");
        Set<Position> checked = new HashSet<>();
        check(WirelessTeleportLanding.find(0, 64, 0, ForgeDirection.NORTH, pos -> {
            check(checked.add(pos), "Every candidate is checked once, including the preferred front");
            check(
                Math.abs(pos.x()) <= 3 && Math.abs(pos.z()) <= 3 && Math.abs(pos.y() - 64) <= 2,
                "Search cannot wander beyond the target's bounded neighbourhood");
            check(!pos.equals(new Position(0, 64, 0)), "Never fall back inside the target device");
            return false;
        }) == null, "No usable floor/headroom must fail rather than teleport to an unchecked fallback");
        check(checked.size() <= 243, "One request has bounded world queries");
        check(
            WirelessTeleportLanding.find(0, 64, 0, ForgeDirection.UNKNOWN, new Position(4, 64, 0)::equals) == null,
            "Free space beyond the search radius is not silently selected");
        System.out
            .println("WirelessTeleportLandingTest: preferred front, safe fallback, vertical search and bounds passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
