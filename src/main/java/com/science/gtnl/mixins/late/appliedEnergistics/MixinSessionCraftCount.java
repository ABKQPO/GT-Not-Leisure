package com.science.gtnl.mixins.late.appliedEnergistics;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.science.gtnl.utils.crafting.CraftingBatchPlanner.SessionSegment;

import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.diagnostics.CraftingDiagnosticSessionId;

// A class mixin rather than an accessor: javac copies @Accessor onto the generic bridge method, which Mixin rejects.
@Mixin(value = CraftingCPUCluster.TaskProgress.SessionCraftCount.class, remap = false)
public abstract class MixinSessionCraftCount implements SessionSegment<CraftingDiagnosticSessionId> {

    @Shadow
    @Final
    private CraftingDiagnosticSessionId sessionId;

    @Shadow
    private long remaining;

    @Override
    public CraftingDiagnosticSessionId getSessionId() {
        return this.sessionId;
    }

    @Override
    public long getRemaining() {
        return this.remaining;
    }

    @Override
    public void setRemaining(long remaining) {
        this.remaining = remaining;
    }
}
