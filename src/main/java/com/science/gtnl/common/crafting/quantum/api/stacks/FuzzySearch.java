package com.science.gtnl.common.crafting.quantum.api.stacks;

import java.util.Comparator;
import java.util.SortedMap;

import net.minecraft.item.ItemStack;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import appeng.api.config.FuzzyMode;

public class FuzzySearch {

    @VisibleForTesting
    static final KeyComparator COMPARATOR = new KeyComparator();
    /**
     * Minecraft reverses the damage values. So anything with a damage Value of 0 is undamaged and increases the more
     * damaged
     * the item is.
     * <p>
     * Further the used subMap follows [MAX_DAMAGE, MIN_DAMAGE), so to include undamaged items, we have to start with a
     * lower damage Value than 0, while it is fine to use {@link ItemStack#getMaxDamage()} for the upper bound.
     */
    private static final int MIN_DAMAGE_VALUE = -1;

    /**
     * Creates a map that is searchable via {@link #findFuzzy}.
     */
    public static AEKey2LongMap.AVLTreeMap createMap2Long() {
        return new AEKey2LongMap.AVLTreeMap(COMPARATOR);
    }

    /**
     * Does a fuzzy search. The map must have been created using {@link #createMap2Long()} or an equivalent map using
     * the same comparator contract.
     */
    @SuppressWarnings({ "unchecked" })
    public static <T extends SortedMap<K, V>, K, V> T findFuzzy(T map, AEKey key, FuzzyMode fuzzy) {
        FuzzyBound lowerBound = makeLowerBound(key, fuzzy);
        FuzzyBound upperBound = makeUpperBound(key, fuzzy);
        Preconditions.checkState(lowerBound.itemDamage() > upperBound.itemDamage());

        // We can use lower/upper bound in this map for queries because our comparator (see below) specifically
        // supports dealing with it
        return (T) map.subMap((K) lowerBound, (K) upperBound);
    }

    /*
     * Keep in mind that the stack order is from most damaged to least damaged, so this lower bound will actually be a
     * higher number than the upper bound.
     */
    static FuzzyBound makeLowerBound(AEKey key, FuzzyMode fuzzy) {
        int maxValue = key.getFuzzySearchMaxValue();
        Preconditions
            .checkState(maxValue > 0, "Cannot use fuzzy search on keys that don't have a fuzzy max Value: %s", key);

        int damage;
        if (fuzzy == FuzzyMode.IGNORE_ALL) {
            damage = maxValue;
        } else {
            int breakpoint = fuzzy.calculateBreakPoint(maxValue);
            damage = key.getFuzzySearchValue() <= breakpoint ? breakpoint : maxValue;
        }

        return new FuzzyBound(damage);
    }

    /*
     * Keep in mind that the stack order is from most damaged to least damaged, so this upper bound will actually be a
     * lower number than the lower bound. It also is exclusive.
     */
    static FuzzyBound makeUpperBound(AEKey key, FuzzyMode fuzzy) {
        int maxValue = key.getFuzzySearchMaxValue();
        Preconditions
            .checkState(maxValue > 0, "Cannot use fuzzy search on keys that don't have a fuzzy max Value: %s", key);

        int damage;
        if (fuzzy == FuzzyMode.IGNORE_ALL) {
            damage = MIN_DAMAGE_VALUE;
        } else {
            final int breakpoint = fuzzy.calculateBreakPoint(maxValue);
            damage = key.getFuzzySearchValue() <= breakpoint ? MIN_DAMAGE_VALUE : breakpoint;
        }

        return new FuzzyBound(damage);
    }

    @VisibleForTesting
    record FuzzyBound(int itemDamage) {}

    /**
     * This comparator creates a strict and total ordering over all {@link AEKey} of the same item. To support selecting
     * ranges of durability, it is defined for type {@link Object} and also accepts {@link FuzzyBound} as an argument to
     * compare against.
     */
    private static class KeyComparator implements Comparator<Object> {

        @Override
        public int compare(Object a, Object b) {
            // Either argument can be a bounded damage Value or a shared item stack
            // Since we never put damage bounds into the map as keys, only one
            // of the two arguments can possibly be a bound
            FuzzyBound boundA = null;
            AEKey stackA = null;
            int fuzzyOrderB;
            if (a instanceof FuzzyBound fuzzyBound) {
                boundA = fuzzyBound;
                fuzzyOrderB = boundA.itemDamage();
            } else {
                stackA = (AEKey) a;
                fuzzyOrderB = stackA.getFuzzySearchValue();
            }
            FuzzyBound boundB = null;
            AEKey stackB = null;
            int fuzzyOrderA;
            if (b instanceof FuzzyBound fuzzyBound) {
                boundB = fuzzyBound;
                fuzzyOrderA = boundB.itemDamage();
            } else {
                stackB = (AEKey) b;
                fuzzyOrderA = stackB.getFuzzySearchValue();
            }

            // When either argument is a bounded damage Value, we compare the damage values because that path is used
            // only to get a certain damage range out of the map.
            if (boundA != null || boundB != null) {
                return Integer.compare(fuzzyOrderA, fuzzyOrderB);
            }

            if (stackA.equals(stackB)) {
                return 0;
            }

            // Damaged items are sorted before undamaged items
            final int fuzzyOrder = Integer.compare(fuzzyOrderA, fuzzyOrderB);
            if (fuzzyOrder != 0) {
                return fuzzyOrder;
            }

            // As a final tie-breaker, order by the hash code of the key
            // While this will order seemingly at random, we only need the order of
            // damage values to be predictable, while still having to satisfy the
            // complete order requirements of the sorted map
            // (We hope there won't be hash collisions... the probability is very low anyway)
            int hashOrder = Integer.compare(stackA.hashCode(), stackB.hashCode());
            if (hashOrder != 0) {
                return hashOrder;
            }
            int tagOrder = stackA.toTag()
                .toString()
                .compareTo(
                    stackB.toTag()
                        .toString());
            if (tagOrder != 0) {
                return tagOrder;
            }
            return Integer.compare(System.identityHashCode(stackA), System.identityHashCode(stackB));
        }
    }
}
