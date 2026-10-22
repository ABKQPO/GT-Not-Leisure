package com.science.gtnl.mixins.late.appliedEnergistics.quamtumComputer;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.common.crafting.quantum.ae2.QuantumCraftingJob;
import com.science.gtnl.utils.ECraftingCPUCluster;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.data.IAEStack;
import appeng.me.cluster.implementations.CraftingCPUCluster;

@Mixin(value = CraftingCPUCluster.class, remap = false)
public abstract class MixinCraftingCPUClusterQuantumJob {

    @Inject(
        method = "submitJob(Lappeng/api/networking/IGrid;Lappeng/api/networking/crafting/ICraftingJob;Lappeng/api/networking/security/BaseActionSource;Lappeng/api/networking/crafting/ICraftingRequester;)Lappeng/api/networking/crafting/ICraftingLink;",
        at = @At("HEAD"),
        cancellable = true,
        require = 1)
    private void gtnl$rejectQuantumJobOnRegularCPU(IGrid g, ICraftingJob job, BaseActionSource src,
        ICraftingRequester requestingMachine, CallbackInfoReturnable<ICraftingLink> cir) {
        if (job instanceof QuantumCraftingJob && !job.supportsCPUCluster((ICraftingCPU) this)) {
            cir.setReturnValue(null);
        }
    }

    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/networking/crafting/ICraftingMedium;pushPattern(Lappeng/api/networking/crafting/ICraftingPatternDetails;Lnet/minecraft/inventory/InventoryCrafting;)Z"),
        require = 1)
    private boolean gtnl$countVariantBudget(ICraftingMedium medium, ICraftingPatternDetails details,
        InventoryCrafting table, Operation<Boolean> original,
        @Local(name = "expandedInputs") List<IAEStack<?>> expandedInputs) {
        final boolean pushed = original.call(medium, details, table);
        if (pushed && (Object) this instanceof ECraftingCPUCluster cluster) {
            cluster.consumeVariantBudget(details, table, expandedInputs);
        }
        return pushed;
    }
}
