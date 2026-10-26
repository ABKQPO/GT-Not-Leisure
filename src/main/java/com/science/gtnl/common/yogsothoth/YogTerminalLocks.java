package com.science.gtnl.common.yogsothoth;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;

public final class YogTerminalLocks {

    private static volatile IntSet client = IntSets.emptySet();

    public static IntSet client() {
        return client;
    }

    static void setClient(IntSet locked) {
        client = locked.isEmpty() ? IntSets.emptySet() : IntSets.unmodifiable(new IntOpenHashSet(locked));
    }

    public static void clearClient() {
        client = IntSets.emptySet();
    }
}
