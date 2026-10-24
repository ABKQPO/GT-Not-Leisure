package com.science.gtnl.common.crafting.quantum.api.stacks;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.ObjLongConsumer;

import org.jetbrains.annotations.NotNull;

import appeng.api.config.FuzzyMode;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;

/**
 * Tallies a negative or positive amount for sub-variants of a {@link AEKey}.
 */
abstract class VariantCounter implements Iterable<Object2LongMap.Entry<AEKey>> {

    private final boolean saturating;

    protected VariantCounter(boolean saturating) {
        this.saturating = saturating;
    }

    public long get(AEKey key) {
        return this.getRecords()
            .getOrDefault(key, 0);
    }

    public void add(AEKey key, long amount) {
        var records = getRecords();
        if (saturating) {
            long newAmount;
            long left = records.getLong(key);
            long result = left + amount;
            if (((left ^ result) & (amount ^ result)) < 0) {
                newAmount = result < 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
            } else {
                newAmount = result;
            }
            records.put(key, newAmount);
        } else {
            records.addTo(key, amount);
        }
    }

    public void set(AEKey key, long amount) {
        getRecords().put(key, amount);
    }

    public long remove(AEKey key) {
        return getRecords().removeLong(key);
    }

    public void addAll(VariantCounter other) {
        other.forEachEntry(this::add);
    }

    public void removeAll(VariantCounter other) {
        other.forEachEntry((key, amount) -> {
            if (amount == Long.MIN_VALUE) {
                add(key, Long.MAX_VALUE);
            } else {
                add(key, -amount);
            }
        });
    }

    public abstract Collection<Object2LongMap.Entry<AEKey>> findFuzzy(AEKey filter, FuzzyMode fuzzy);

    @Override
    public void forEach(Consumer<? super Object2LongMap.Entry<AEKey>> action) {
        Objects.requireNonNull(action, "action");
        var records = getRecords();
        if (!records.isEmpty()) Object2LongMaps.fastForEach(records, action);
    }

    public void forEachEntry(ObjLongConsumer<? super AEKey> action) {
        Objects.requireNonNull(action, "action");
        var records = getRecords();
        if (!records.isEmpty()) records.forEachEntry(action);
    }

    public abstract void forEachFuzzy(AEKey filter, FuzzyMode fuzzy, ObjLongConsumer<? super AEKey> action);

    public int size() {
        return getRecords().size();
    }

    public boolean isEmpty() {
        return getRecords().isEmpty();
    }

    @Override
    public @NotNull Iterator<Object2LongMap.Entry<AEKey>> iterator() {
        return Object2LongMaps.fastIterator(getRecords());
    }

    abstract AEKey2LongMap getRecords();

    /**
     * Sets all amounts to zero.
     */
    public void reset() {
        forEach(entry -> entry.setValue(0L));
    }

    public void clear() {
        getRecords().clear();
    }

    public abstract VariantCounter copy(boolean saturating);

    public void invert() {
        forEach(entry -> {
            long amount = entry.getLongValue();
            entry.setValue(amount == Long.MIN_VALUE ? Long.MAX_VALUE : -amount);
        });
    }

    public void removeZeros() {
        var it = getRecords().values()
            .iterator();
        while (it.hasNext()) {
            var entry = it.nextLong();
            if (entry == 0) {
                it.remove();
            }
        }
    }

    /**
     * This variant list is optimized for items that cannot be damaged and thus do not support querying durability
     * ranges via {@link #findFuzzy}.
     */
    static class UnorderedVariantMap extends VariantCounter {

        // Most primary keys have only one variant. Do not allocate 32 buckets for each such counter.
        private final AEKey2LongMap records;

        UnorderedVariantMap(boolean saturating) {
            this(saturating, 1);
        }

        private UnorderedVariantMap(boolean saturating, int expected) {
            super(saturating);
            this.records = new AEKey2LongMap.OpenHashMap(expected);
        }

        /**
         * For keys whose primary key does not support fuzzy range lookups, we simply return all records, which amounts
         * to ignoring NBT.
         */
        @Override
        public Collection<Object2LongMap.Entry<AEKey>> findFuzzy(AEKey filter, FuzzyMode fuzzy) {
            return records.object2LongEntrySet();
        }

        @Override
        public void forEachFuzzy(AEKey filter, FuzzyMode fuzzy, ObjLongConsumer<? super AEKey> action) {
            records.forEachEntry(action);
        }

        @Override
        AEKey2LongMap getRecords() {
            return records;
        }

        @Override
        public VariantCounter copy(boolean saturating) {
            var result = new UnorderedVariantMap(saturating, records.size());
            result.records.putAll(records);
            return result;
        }
    }

    /**
     * This variant list is optimized for damageable items, and supports selecting durability ranges with
     * {@link #findFuzzy}.
     */
    static class FuzzyVariantMap extends VariantCounter {

        private final AEKey2LongMap.AVLTreeMap records = FuzzySearch.createMap2Long();

        FuzzyVariantMap(boolean saturating) {
            super(saturating);
        }

        @Override
        public void forEachFuzzy(AEKey filter, FuzzyMode fuzzy, ObjLongConsumer<? super AEKey> action) {
            records.forEachEntry((key, amount) -> { if (key.fuzzyEquals(filter, fuzzy)) action.accept(key, amount); });
        }

        @Override
        public Collection<Object2LongMap.Entry<AEKey>> findFuzzy(AEKey key, FuzzyMode fuzzy) {
            // The comparator's range bounds only order by damage. Filter the selected entries with the public fuzzy
            // contract as a final step so equal damage and hash-collision keys cannot be lost at a range boundary.
            return new AbstractCollection<>() {

                @Override
                public int size() {
                    int result = 0;
                    for (var entry : records.object2LongEntrySet()) {
                        if (entry.getKey()
                            .fuzzyEquals(key, fuzzy)) {
                            result++;
                        }
                    }
                    return result;
                }

                @Override
                public @NotNull Iterator<Object2LongMap.Entry<AEKey>> iterator() {
                    return new Iterator<>() {

                        private Iterator<Object2LongMap.Entry<AEKey>> source = records.object2LongEntrySet()
                            .iterator();
                        private Object2LongMap.Entry<AEKey> next;
                        private Object2LongMap.Entry<AEKey> current;
                        private boolean advanced;

                        @Override
                        public boolean hasNext() {
                            if (next != null) {
                                return true;
                            }
                            while (source.hasNext()) {
                                var candidate = source.next();
                                advanced = true;
                                if (candidate.getKey()
                                    .fuzzyEquals(key, fuzzy)) {
                                    next = candidate;
                                    return true;
                                }
                            }
                            return false;
                        }

                        @Override
                        public Object2LongMap.Entry<AEKey> next() {
                            if (!hasNext()) {
                                throw new NoSuchElementException();
                            }
                            current = next;
                            next = null;
                            advanced = false;
                            return current;
                        }

                        @Override
                        public void remove() {
                            if (current == null) {
                                throw new IllegalStateException();
                            }
                            if (advanced) {
                                // hasNext may have moved past current, including past rejected entries. Remove the
                                // returned key and seek strictly after it so the AVL iterator cannot retain a
                                // deleted predecessor, and a prefetched match is neither skipped nor removed.
                                records.removeLong(current.getKey());
                                source = records.object2LongEntrySet()
                                    .iterator(current);
                                next = null;
                            } else {
                                source.remove();
                            }
                            current = null;
                        }
                    };
                }
            };
        }

        @Override
        AEKey2LongMap getRecords() {
            return this.records;
        }

        @Override
        public VariantCounter copy(boolean saturating) {
            var result = new FuzzyVariantMap(saturating);
            result.records.putAll(records);
            return result;
        }
    }

}
