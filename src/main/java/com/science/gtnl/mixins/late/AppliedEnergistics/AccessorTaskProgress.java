package com.science.gtnl.mixins.late.AppliedEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import appeng.me.diagnostics.CraftingDiagnosticSessionId;

@Mixin(targets = "appeng.me.cluster.implementations.CraftingCPUCluster$TaskProgress", remap = false)
public interface AccessorTaskProgress {

    @Accessor
    long getValue();

    @Accessor
    void setValue(long value);

    @Invoker("consumeCraftSession")
    CraftingDiagnosticSessionId consumeCraftSession();
}
