package com.science.gtnl.api;

import java.util.Collections;

import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.api.storage.data.IItemList;

public interface IStackVault {

    boolean isValid();

    default boolean supportsStackType(IAEStackType<?> type) {
        return false;
    }

    default Iterable<IAEStackType<?>> getSupportedStackTypes() {
        return Collections.emptyList();
    }

    default <T extends IAEStack<T>> IItemList<T> getStoredStacks(IAEStackType<T> type) {
        return type.createList();
    }

    default <T extends IAEStack<T>> T getStoredStack(T stack) {
        return null;
    }

    default <T extends IAEStack<T>> long injectStack(T stack, boolean doInput) {
        return 0;
    }

    default <T extends IAEStack<T>> long extractStack(T stack, boolean doOutput) {
        return 0;
    }

    default long stackTypesCount(IAEStackType<?> type) {
        return 0;
    }

    default long maxStackTypes(IAEStackType<?> type) {
        return 0;
    }

    default long capacityPerStack(IAEStackType<?> type) {
        return 0;
    }
}
