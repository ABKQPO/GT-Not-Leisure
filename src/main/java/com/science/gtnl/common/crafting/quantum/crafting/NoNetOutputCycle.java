package com.science.gtnl.common.crafting.quantum.crafting;

import java.util.List;

import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;

/**
 * The crafting cycle a request ran into that makes no more of what it was asked for than it takes: the items around
 * it, and the patterns making them.
 */
public record NoNetOutputCycle(List<AEKey> items, List<IPatternDetails> patterns) {

    public static NoNetOutputCycle of(List<AEKey> items, List<IPatternDetails> patterns) {
        return new NoNetOutputCycle(items, patterns);
    }
}
