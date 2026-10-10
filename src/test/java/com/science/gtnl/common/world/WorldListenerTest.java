package com.science.gtnl.common.world;

import java.util.Set;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.science.gtnl.api.IBlockStateListener;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

public final class WorldListenerTest {

    public static void run() throws Exception {
        WorldListener access = new WorldListener();
        long a = CoordinatePacker.pack(-1, 64, -1);
        long b = CoordinatePacker.pack(32, 64, 32);
        long c = CoordinatePacker.pack(-2, 65, -2);
        int[] calls = new int[2];
        IBlockStateListener first = pos -> calls[0]++;
        IBlockStateListener second = pos -> calls[1]++;
        LongOpenHashSet positions = new LongOpenHashSet(new long[] { a, b, c, a });
        access.replacePositions(first, positions);
        positions.clear();
        access.replacePositions(second, new LongOpenHashSet(new long[] { a }));
        access.markBlockForUpdate(-1, 64, -1);
        check(
            calls[0] == 1 && calls[1] == 1,
            "Duplicate coordinates notify once and input mutation cannot erase watches");
        access.markBlockForUpdate(100, 64, 100);
        check(calls[0] == 1 && calls[1] == 1, "Unrelated positions do not notify");
        access.replacePositions(first, new LongOpenHashSet(new long[] { b }));
        access.markBlockForUpdate(-1, 64, -1);
        check(calls[0] == 1 && calls[1] == 2, "Replacing one listener keeps overlapping listeners intact");
        access.replacePositions(first, new LongOpenHashSet());
        access.replacePositions(second, new LongOpenHashSet());
        assertEmptyIndex(access, "blockStateListeners");
        assertEmptyIndex(access, "chunkListeners");
        // Arbitrary coordinates revisit the same chunk non-consecutively; unregister must remove it only once.
        access.replacePositions(first, new LongOpenHashSet(new long[] { a, b, c }));
        access.replacePositions(first, new LongOpenHashSet());
        assertEmptyIndex(access, "chunkListeners");
        IBlockStateListener selfRemoving = new IBlockStateListener() {

            public void onBlockChanged(com.gtnewhorizon.gtnhlib.blockpos.BlockPos pos) {
                access.replacePositions(this, new LongOpenHashSet());
            }
        };
        access.replacePositions(selfRemoving, new LongOpenHashSet(new long[] { a }));
        access.replacePositions(second, new LongOpenHashSet(new long[] { a }));
        access.markBlockForUpdate(-1, 64, -1);
        access.markBlockForUpdate(-1, 64, -1);
        check(calls[1] == 4, "Callbacks may unregister while other listeners continue receiving updates");
        access.replacePositions(second, new LongOpenHashSet());
        assertEmptyIndex(access, "blockStateListeners");
        assertEmptyIndex(access, "chunkListeners");
    }

    private static void assertEmptyIndex(WorldListener access, String name) throws Exception {
        var field = WorldListener.class.getDeclaredField(name);
        field.setAccessible(true);
        check(
            ((Long2ObjectOpenHashMap<Set<IBlockStateListener>>) field.get(access)).isEmpty(),
            name + " leaked watches");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
