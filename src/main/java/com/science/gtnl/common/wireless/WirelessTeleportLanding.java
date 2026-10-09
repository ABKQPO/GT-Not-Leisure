package com.science.gtnl.common.wireless;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraftforge.common.util.ForgeDirection;

/** Bounded search near a displayed device; no candidate is accepted without the world's safety check. */
final class WirelessTeleportLanding {

    record Position(int x, int y, int z) {}

    private WirelessTeleportLanding() {}

    static Position find(int x, int y, int z, ForgeDirection front, Predicate<Position> safe) {
        Set<Position> candidates = new LinkedHashSet<>();
        if (front != null && front != ForgeDirection.UNKNOWN && front.offsetY == 0)
            candidates.add(new Position(x + front.offsetX, y, z + front.offsetZ));
        for (int dy : new int[] { 0, 1, -1, 2, -2 }) {
            if (dy > 0) candidates.add(new Position(x, y + dy, z));
            for (int radius = 1; radius <= 3; radius++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz)) == radius)
                            candidates.add(new Position(x + dx, y + dy, z + dz));
                    }
                }
            }
        }
        return candidates.stream()
            .filter(safe)
            .findFirst()
            .orElse(null);
    }
}
