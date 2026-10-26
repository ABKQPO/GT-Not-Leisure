package com.science.gtnl.common.crafting.quantum.api.stacks;

import java.util.Comparator;
import java.util.function.ObjLongConsumer;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.Object2LongAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

/**
 * Custom extension to expose the increment function in a polymorphic way. We don't want to use
 * {@link Object2LongMap#mergeLong} because the fastutil maps don't override the naive implementation.
 */
@SuppressWarnings("CloneableClassInSecureContext")
public interface AEKey2LongMap extends Object2LongMap<AEKey> {

    /**
     * Adds an increment to Value currently associated with a key.
     *
     * @return the old Value, or the {@linkplain #defaultReturnValue() default return Value} if no Value was present for
     *         the given key.
     */
    long addTo(AEKey k, long incr);

    /**
     * Visits primitive amounts without boxed values. The callback must not add or remove keys from this map.
     */
    default void forEachEntry(ObjLongConsumer<? super AEKey> action) {
        var iterator = Object2LongMaps.fastIterator(this);
        while (iterator.hasNext()) {
            var entry = iterator.next();
            action.accept(entry.getKey(), entry.getLongValue());
        }
    }

    final class OpenHashMap extends Object2LongOpenHashMap<AEKey> implements AEKey2LongMap {

        public OpenHashMap() {
            super();
        }

        public OpenHashMap(int expected) {
            super(expected);
        }

        public OpenHashMap(Object2LongMap<AEKey> inventory) {
            super(inventory);
        }

        @Override
        public void forEachEntry(ObjLongConsumer<? super AEKey> action) {
            // Match the map iterator's order without allocating an iterator and a mutable entry per variant group.
            if (containsNullKey) action.accept(null, value[n]);
            Object[] keys = key;
            for (int position = n; position-- != 0;) {
                if (keys[position] != null) action.accept((AEKey) keys[position], value[position]);
            }
        }
    }

    /**
     * Open-addressed map whose primitive accumulation clamps signed-long overflow without doing a second lookup.
     */
    final class SaturatingOpenHashMap extends Object2LongOpenHashMap<AEKey> implements AEKey2LongMap {

        public SaturatingOpenHashMap() {
            super();
        }

        public SaturatingOpenHashMap(int expected) {
            super(expected);
        }

        private static long saturatedAdd(long left, long right) {
            long result = left + right;
            if (((left ^ result) & (right ^ result)) < 0) {
                return right < 0 ? Long.MIN_VALUE : Long.MAX_VALUE;
            }
            return result;
        }

        @Override
        public long addTo(AEKey key, long increment) {
            Object[] keys = this.key;
            int position;
            if (key == null) {
                if (containsNullKey) {
                    return addToExisting(n, increment);
                }
                position = n;
                containsNullKey = true;
            } else {
                AEKey current;
                if ((current = (AEKey) keys[position = HashCommon.mix(key.hashCode()) & mask]) != null) {
                    do {
                        if (current.equals(key)) {
                            return addToExisting(position, increment);
                        }
                    } while ((current = (AEKey) keys[position = (position + 1) & mask]) != null);
                }
            }

            keys[position] = key;
            value[position] = saturatedAdd(defaultReturnValue(), increment);
            if (size++ >= maxFill) {
                rehash(HashCommon.arraySize(size + 1, f));
            }
            return defaultReturnValue();
        }

        private long addToExisting(int position, long increment) {
            long previous = value[position];
            value[position] = saturatedAdd(previous, increment);
            return previous;
        }
    }

    final class LinkdOpenHashMap extends Object2LongLinkedOpenHashMap<AEKey> implements AEKey2LongMap {

    }

    /**
     * Read-only primitive map view that preserves the AE-key-specific map contract.
     */
    final class UnmodifiableMap extends Object2LongMaps.UnmodifiableMap<AEKey> implements AEKey2LongMap {

        public UnmodifiableMap(AEKey2LongMap map) {
            super(map);
        }

        @Override
        public long addTo(AEKey key, long increment) {
            throw new UnsupportedOperationException("This map is immutable");
        }
    }

    final class AVLTreeMap extends Object2LongAVLTreeMap<AEKey> implements AEKey2LongMap {

        public AVLTreeMap(Comparator<? super AEKey> c) {
            super(c);
        }
    }
}
