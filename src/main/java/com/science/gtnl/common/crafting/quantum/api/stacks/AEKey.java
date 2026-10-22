package com.science.gtnl.common.crafting.quantum.api.stacks;

import org.jetbrains.annotations.Nullable;

import appeng.api.config.FuzzyMode;

public abstract class AEKey {

    public abstract AEKeyType getType();

    public final int getAmountPerUnit() {
        return getType().getAmountPerUnit();
    }

    public final int getAmountPerOperation() {
        return getType().getAmountPerOperation();
    }

    public final int getAmountPerByte() {
        return getType().getAmountPerByte();
    }

    public abstract Object getPrimaryKey();

    public int getFuzzySearchValue() {
        return 0;
    }

    public int getFuzzySearchMaxValue() {
        return 0;
    }

    public final boolean fuzzyEquals(AEKey other, FuzzyMode fuzzyMode) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        // For any fuzzy mode, the primary key (item, fluid) must still match
        if (getPrimaryKey() != other.getPrimaryKey()) {
            return false;
        }

        // If the type doesn't support fuzzy range search, it always behaves like IGNORE_ALL, which just ignores NBT
        if (!supportsFuzzyRangeSearch()) {
            return true;
        } else if (fuzzyMode == FuzzyMode.IGNORE_ALL) {
            return true;
        } else if (fuzzyMode == FuzzyMode.PERCENT_99) {
            return getFuzzySearchValue() > 0 == other.getFuzzySearchValue() > 0;
        } else {
            final float percentA = (float) getFuzzySearchValue() / getFuzzySearchMaxValue();
            final float percentB = (float) other.getFuzzySearchValue() / other.getFuzzySearchMaxValue();

            return percentA > fuzzyMode.breakPoint == percentB > fuzzyMode.breakPoint;
        }
    }

    public final boolean matches(@Nullable GenericStack stack) {
        return stack != null && stack.what()
            .equals(this);
    }

    public abstract Object toTag();

    public final boolean supportsFuzzyRangeSearch() {
        return getType().supportsFuzzyRangeSearch() && getFuzzySearchMaxValue() > 0;
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
