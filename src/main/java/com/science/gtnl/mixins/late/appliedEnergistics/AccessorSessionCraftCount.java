package com.science.gtnl.mixins.late.appliedEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.science.gtnl.utils.crafting.CraftingBatchPlanner.SessionSegment;

import appeng.me.diagnostics.CraftingDiagnosticSessionId;

/**
 * Adapts one private AE diagnostics count segment to the planner's bulk-consumption contract without reflection.
 */
@Mixin(targets = "appeng.me.cluster.implementations.CraftingCPUCluster$TaskProgress$SessionCraftCount", remap = false)
public interface AccessorSessionCraftCount extends SessionSegment<CraftingDiagnosticSessionId> {

    /**
     * Returns the diagnostics session owning this contiguous craft segment.
     *
     * @return owning session identifier
     */
    @Override
    @Accessor("sessionId")
    CraftingDiagnosticSessionId getSessionId();

    /**
     * Returns the unconsumed craft count in this segment.
     *
     * @return positive remaining count
     */
    @Override
    @Accessor("remaining")
    long getRemaining();

    /**
     * Stores the positive remainder after a batch consumes part of this segment.
     *
     * @param remaining positive unconsumed count
     */
    @Override
    @Accessor("remaining")
    void setRemaining(long remaining);
}
