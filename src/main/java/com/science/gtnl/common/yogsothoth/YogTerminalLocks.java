package com.science.gtnl.common.yogsothoth;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class YogTerminalLocks {

    private static volatile Set<Integer> client = Collections.emptySet();

    public static Set<Integer> client() {
        return client;
    }

    static void setClient(Set<Integer> locked) {
        client = locked.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(locked));
    }

    public static void clearClient() {
        client = Collections.emptySet();
    }
}
