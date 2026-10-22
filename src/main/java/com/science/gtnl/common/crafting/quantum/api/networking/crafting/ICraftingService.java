package com.science.gtnl.common.crafting.quantum.api.networking.crafting;

import java.util.Collection;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.crafting.graph.CraftingGraph;

public interface ICraftingService {

    Collection<IPatternDetails> getCraftingFor(AEKey whatToCraft);

    @Nullable
    AEKey getFuzzyCraftable(AEKey whatToCraft, Predicate<AEKey> filter);

    boolean canEmitFor(AEKey what);

    default long getRecursiveIngredientReserveAmount() {
        return 1;
    }

    default long getGraphCacheRevision() {
        return Long.MIN_VALUE;
    }

    @Nullable
    default CraftingGraph takeCachedGraph(AEKey output) {
        return null;
    }

    @Nullable
    default CraftingGraph peekCachedGraph(AEKey output) {
        return null;
    }

    default void putCachedGraph(AEKey output, CraftingGraph graph) {}
}
