package com.science.gtnl.api.armorflight;

import java.util.Random;

public enum ArmorFlightApproach {

    FRONT,
    BEHIND,
    RANDOM;

    public boolean pickBehind(Random random) {
        return switch (this) {
            case FRONT -> false;
            case BEHIND -> true;
            case RANDOM -> random.nextBoolean();
        };
    }

    public static ArmorFlightApproach byName(String name) {
        for (ArmorFlightApproach a : values()) if (a.name()
            .equalsIgnoreCase(name)) return a;
        return null;
    }
}
