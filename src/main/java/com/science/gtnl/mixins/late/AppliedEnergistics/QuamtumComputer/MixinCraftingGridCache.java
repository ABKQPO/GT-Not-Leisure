package com.science.gtnl.mixins.late.AppliedEnergistics.QuamtumComputer;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.machine.multiblock.QuantumComputer;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.crafting.CraftingLink;
import appeng.me.cache.CraftingGridCache;
import appeng.me.cluster.implementations.CraftingCPUCluster;

@Mixin(value = CraftingGridCache.class, remap = false)
public abstract class MixinCraftingGridCache {

    @Shadow
    @Final
    private IGrid grid;

    @Shadow
    @Final
    private Set<CraftingCPUCluster> craftingCPUClusters;

    @Shadow
    public abstract void addLink(final CraftingLink link);

    @Inject(method = "updateCPUClusters()V", at = @At("RETURN"),
        require = 1)
    private void injectUpdateCPUClusters(final CallbackInfo ci) {
        for (final IGridNode ecNode : grid.getMachines(QuantumComputer.class)) {
            final var ec = (QuantumComputer) ecNode.getMachine();
            final List<CraftingCPUCluster> cpus = ec.getCPUs();

            for (CraftingCPUCluster cpu : cpus) {
                this.craftingCPUClusters.add(cpu);

                if (cpu.getLastCraftingLink() != null) {
                    this.addLink((CraftingLink) cpu.getLastCraftingLink());
                }
            }
        }
    }

}
