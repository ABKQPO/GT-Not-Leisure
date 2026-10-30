package com.science.gtnl.common.crafting.quantum.hooks;

import java.lang.reflect.Constructor;

import com.science.gtnl.mixins.late.appliedEnergistics.quantum.AccessorOptimizePattern;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEStack;

// The only code that touches AE's private ContainerOptimizePatterns.Pattern; update it when AE changes that class.
public class OptimizePatternAccess {

    private static Constructor<?> patternConstructor;

    public static Object newPattern() {
        try {
            if (patternConstructor == null) {
                final Constructor<?> constructor = Class
                    .forName("appeng.container.implementations.ContainerOptimizePatterns$Pattern")
                    .getDeclaredConstructor();
                constructor.setAccessible(true);
                patternConstructor = constructor;
            }
            return patternConstructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot create a pattern entry of the pattern optimizer", e);
        }
    }

    public static void addCraftingTask(Object pattern, ICraftingPatternDetails details, long crafts) {
        ((AccessorOptimizePattern) pattern).gtnl$addCraftingTask(details, crafts);
    }

    public static boolean isSingleProcessingPattern(Object pattern) {
        final AccessorOptimizePattern access = (AccessorOptimizePattern) pattern;
        return access.gtnl$getPatternDetails()
            .size() == 1
            && !access.gtnl$getPattern()
                .isCraftable();
    }

    public static long requestedCrafts(Object pattern) {
        return ((AccessorOptimizePattern) pattern).gtnl$getRequestedCrafts();
    }

    public static long craftAmountFor(Object pattern, IAEStack<?> stack) {
        return ((AccessorOptimizePattern) pattern).gtnl$getCraftAmountForItem(stack);
    }

    public static int maxBitMultiplier(Object pattern) {
        return ((AccessorOptimizePattern) pattern).gtnl$getMaxBitMultiplier();
    }
}
