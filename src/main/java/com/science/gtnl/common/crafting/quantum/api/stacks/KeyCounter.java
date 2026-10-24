/*
 * The MIT License (MIT)
 * Copyright (c) 2021 TeamAppliedEnergistics
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.science.gtnl.common.crafting.quantum.api.stacks;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.ObjLongConsumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Iterators;

import appeng.api.config.FuzzyMode;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

/**
 * Associates a generic Value of type T with AE keys and makes key/Value pairs searchable with fuzzy mode semantics.
 */
public final class KeyCounter implements Iterable<Object2LongMap.Entry<AEKey>> {

    private static final Reference2ObjectMap<Object, VariantCounter> EMPTY_LISTS = Reference2ObjectMaps.emptyMap();
    // First map contains a mapping from AEKey#primaryKey
    private Reference2ObjectMap<Object, VariantCounter> lists;
    private final boolean saturating;

    public KeyCounter() {
        this(false);
    }

    private KeyCounter(boolean saturating) {
        this.saturating = saturating;
        this.lists = EMPTY_LISTS;
    }

    private KeyCounter(boolean saturating, int expectedKeys) {
        this.saturating = saturating;
        this.lists = new Reference2ObjectOpenHashMap<>(expectedKeys);
    }

    /**
     * Creates a counter that clamps additions to the signed long range.
     */
    public static KeyCounter saturating() {
        return new KeyCounter(true);
    }

    /**
     * Creates a counter that clamps additions to the signed long range, pre-sized for the given key count so bulk
     * copies avoid rehashing while they fill.
     */
    public static KeyCounter saturating(int expectedKeys) {
        return new KeyCounter(true, expectedKeys);
    }

    public Collection<Object2LongMap.Entry<AEKey>> findFuzzy(AEKey key, FuzzyMode fuzzy) {
        Objects.requireNonNull(key, "key");
        var subIndex = getSubIndexOrNull(key);
        return subIndex == null ? List.of() : subIndex.findFuzzy(key, fuzzy);
    }

    /**
     * Visits entries in iterator order without constructing the counter's iterator chain. Like the fast iterator,
     * this traversal may reuse an entry object, so consume it inside the callback. Existing amounts may be updated,
     * but the callback must not add or remove keys.
     */
    @Override
    public void forEach(Consumer<? super Object2LongMap.Entry<AEKey>> action) {
        Objects.requireNonNull(action, "action");
        if (lists.isEmpty()) return;
        lists.values()
            .forEach(list -> list.forEach(action));
    }

    /**
     * Visits keys and primitive amounts in iterator order without entry wrappers or boxed amounts. Existing amounts
     * may be updated, but the callback must not add or remove keys.
     */
    public void forEach(ObjLongConsumer<? super AEKey> action) {
        Objects.requireNonNull(action, "action");
        if (lists.isEmpty()) return;
        lists.values()
            .forEach(list -> list.forEachEntry(action));
    }

    /**
     * Same keys, amounts and order as findFuzzy, without constructing an iterable pipeline.
     */
    public void forEachFuzzy(AEKey key, FuzzyMode fuzzy, ObjLongConsumer<? super AEKey> action) {
        Objects.requireNonNull(key, "key");
        var subIndex = getSubIndexOrNull(key);
        if (subIndex != null) subIndex.forEachFuzzy(key, fuzzy, action);
    }

    public void removeZeros() {
        if (lists.isEmpty()) return;
        var iterator = lists.values()
            .iterator();
        while (iterator.hasNext()) {
            var variantList = iterator.next();
            variantList.removeZeros();
            if (variantList.isEmpty()) {
                iterator.remove();
            }
        }
    }

    public void removeEmptySubmaps() {
        if (lists.isEmpty()) return;
        lists.values()
            .removeIf(VariantCounter::isEmpty);
    }

    public void addAll(KeyCounter other) {
        if (other.lists.isEmpty()) return;
        ensureLists(other.lists.size());
        other.lists.forEach((primaryKey, variants) -> {
            var ourSubIndex = lists.get(primaryKey);
            if (ourSubIndex == null) {
                lists.put(primaryKey, variants.copy(saturating));
            } else {
                ourSubIndex.addAll(variants);
            }
        });
    }

    public void removeAll(KeyCounter other) {
        if (other.lists.isEmpty()) return;
        ensureLists(other.lists.size());
        other.lists.forEach((primaryKey, variants) -> {
            var ourSubIndex = lists.get(primaryKey);
            if (ourSubIndex == null) {
                var copied = variants.copy(saturating);
                copied.invert();
                lists.put(primaryKey, copied);
            } else {
                ourSubIndex.removeAll(variants);
            }
        });
    }

    public void add(AEKey key, long amount) {
        Objects.requireNonNull(key, "key");
        getSubIndex(key).add(key, amount);
    }

    /**
     * Subtracts the given amount from the Value associated with the given key.
     */
    public void remove(AEKey key, long amount) {
        if (amount == Long.MIN_VALUE) {
            add(key, Long.MAX_VALUE);
        } else {
            add(key, -amount);
        }
    }

    /**
     * Removes the given key from this counter, and returns the old Value (or 0).
     */
    public void remove(AEKey key) {
        var subIndex = getSubIndexOrNull(key);
        if (subIndex == null) return;
        subIndex.remove(key);
        if (subIndex.isEmpty()) {
            lists.remove(key.getPrimaryKey());
        }
    }

    public void set(AEKey key, long amount) {
        getSubIndex(key).set(key, amount);
    }

    public long get(AEKey key) {
        Objects.requireNonNull(key);
        var subIndex = lists.get(key.getPrimaryKey());
        if (subIndex == null) {
            return 0;
        }
        return subIndex.get(key);
    }

    public void reset() {
        lists.values()
            .forEach(VariantCounter::reset);
    }

    public void clear() {
        lists.values()
            .forEach(VariantCounter::clear);
    }

    /**
     * Takes the prepared counter's contents without copying its variant indexes. The source becomes empty;
     * previously borrowed entries and fuzzy views must not be used across this replacement.
     */
    public void moveFrom(KeyCounter prepared) {
        Objects.requireNonNull(prepared, "prepared");
        if (prepared == this) {
            return;
        }
        if (this.saturating != prepared.saturating) {
            throw new IllegalArgumentException("Cannot move counters with different overflow policies");
        }
        var previous = this.lists;
        this.lists = prepared.lists;
        prepared.lists = previous;
        prepared.clear();
    }

    public boolean isEmpty() {
        for (var list : lists.values()) {
            if (!list.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        int tot = 0;
        for (var list : lists.values()) {
            tot += list.size();
        }
        return tot;
    }

    @Override
    public @NotNull Iterator<Object2LongMap.Entry<AEKey>> iterator() {
        if (lists.isEmpty()) return Collections.emptyIterator();
        return new Iterator<>() {

            private final Iterator<VariantCounter> groups = lists.values()
                .iterator();
            private Iterator<Object2LongMap.Entry<AEKey>> current = Collections.emptyIterator();
            private Iterator<Object2LongMap.Entry<AEKey>> lastReturned;

            @Override
            public boolean hasNext() {
                while (!current.hasNext() && groups.hasNext()) current = groups.next()
                    .iterator();
                return current.hasNext();
            }

            @Override
            public Object2LongMap.Entry<AEKey> next() {
                if (!hasNext()) throw new NoSuchElementException();
                var entry = current.next();
                lastReturned = current;
                return entry;
            }

            @Override
            public void remove() {
                if (lastReturned == null) throw new IllegalStateException();
                // hasNext() may already have advanced to another group.
                lastReturned.remove();
                lastReturned = null;
            }
        };
    }

    private VariantCounter getSubIndex(AEKey key) {
        var primaryKey = key.getPrimaryKey();
        var result = lists.get(primaryKey);
        if (result != null) return result;
        ensureLists(1);
        result = key.getFuzzySearchMaxValue() > 0 ? new VariantCounter.FuzzyVariantMap(saturating)
            : new VariantCounter.UnorderedVariantMap(saturating);
        lists.put(primaryKey, result);
        return result;
    }

    private void ensureLists(int expectedKeys) {
        if (lists == EMPTY_LISTS) lists = new Reference2ObjectOpenHashMap<>(expectedKeys);
    }

    @Nullable
    private VariantCounter getSubIndexOrNull(AEKey key) {
        return lists.get(key.getPrimaryKey());
    }

    @Nullable
    public AEKey getFirstKey() {
        var e = getFirstEntry();
        return e != null ? e.getKey() : null;
    }

    @Nullable
    public <T extends AEKey> T getFirstKey(Class<T> keyClass) {
        var e = getFirstEntry(keyClass);
        return e != null ? keyClass.cast(e.getKey()) : null;
    }

    @Nullable
    public Object2LongMap.Entry<AEKey> getFirstEntry() {
        for (var value : lists.values()) {
            var it = value.iterator();
            if (it.hasNext()) {
                return it.next();
            }
        }
        return null;
    }

    @Nullable
    public <T extends AEKey> Object2LongMap.Entry<AEKey> getFirstEntry(Class<T> keyClass) {
        for (var value : lists.values()) {
            for (var entry : value) {
                if (keyClass.isInstance(entry.getKey())) {
                    return entry;
                }
            }
        }
        return null;
    }

    public Set<AEKey> keySet() {
        // The exact entry count would require traversing every variant map before this traversal. The primary-map
        // count is a cheap lower bound and lets the set grow naturally for fuzzy variants.
        var keys = new ObjectOpenHashSet<AEKey>(lists.size());
        forEach((key, amount) -> keys.add(key));
        return keys;
    }
}
