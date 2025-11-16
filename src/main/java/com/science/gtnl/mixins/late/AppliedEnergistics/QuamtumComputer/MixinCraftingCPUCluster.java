package com.science.gtnl.mixins.late.AppliedEnergistics.QuamtumComputer;

import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.science.gtnl.common.machine.multiblock.QuantumComputer;
import com.science.gtnl.utils.ECPUCluster;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.crafting.MECraftingInventory;
import appeng.me.cache.CraftingGridCache;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.tile.crafting.TileCraftingTile;

@Mixin(value = CraftingCPUCluster.class, remap = false)
public abstract class MixinCraftingCPUCluster implements ECPUCluster {

    @Unique
    private QuantumComputer ec$virtualCPUOwner = null;
    @Shadow
    private long availableStorage;

    @Shadow
    private boolean isDestroyed;

    @Shadow
    private int accelerator;

    @Shadow
    private MECraftingInventory inventory;

    @Shadow
    private boolean isComplete;

    @Shadow
    private ICraftingLink myLastLink;

    @Shadow
    private MachineSource machineSrc;
    @Final
    @Shadow
    private int[] usedOps;

    @Shadow
    public abstract void destroy();

    @Shadow
    public abstract void cancel();

    @Inject(
        method = "submitJob",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/networking/crafting/ICraftingJob;getOutput()Lappeng/api/storage/data/IAEItemStack;"),
        require = 1)
    private void injectSubmitJob(IGrid g, ICraftingJob job, BaseActionSource src, ICraftingRequester requestingMachine,
        CallbackInfoReturnable<ICraftingLink> cir) {
        if (this.ec$virtualCPUOwner == null) return;
        this.ec$virtualCPUOwner.onVirtualCPUSubmitJob(job.getByteTotal());
    }

    @Inject(method = "cancel", at = @At("RETURN"), require = 1)
    private void injectCancel(final CallbackInfo ci) {
        if (this.ec$virtualCPUOwner == null) return;
        if (this.inventory.getItemList()
            .isEmpty()) destroy();
    }

    @Inject(method = "updateCraftingLogic", at = @At("HEAD"), cancellable = true, require = 1)
    private void injectUpdateCraftingLogicStoreItems(final IGrid grid, final IEnergyGrid eg,
        final CraftingGridCache cgc, final CallbackInfo ci) {
        if (this.ec$virtualCPUOwner == null) return;
        if (this.myLastLink != null) {
            if (this.myLastLink.isCanceled()) {
                this.myLastLink = null;
                this.cancel();
            }
        }
        if (this.isComplete && !this.ec$virtualCPUOwner.isVirtualCPU(this)) {
            // Ensure inventory is empty
            if (this.inventory.getItemList()
                .isEmpty()) {
                destroy();
                ci.cancel();
            }
        }
    }

    @WrapOperation(
        method = "updateCraftingLogic",
        at = @At(value = "INVOKE", target = "Lappeng/tile/crafting/TileCraftingTile;isActive()Z"),
        require = 1)
    private boolean redirectUpdateCraftingLogicIsActive(final TileCraftingTile instance,
        final Operation<Boolean> original) {
        if (this.ec$virtualCPUOwner == null) return original.call(instance);
        return ec$virtualCPUOwner.getProxy()
            .isActive();

    }

    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true, require = 1)
    private void injectDestroy(final CallbackInfo ci) {
        if (this.ec$virtualCPUOwner == null) return;
        if (this.isDestroyed) {
            ci.cancel();
            return;
        }
        this.ec$virtualCPUOwner.onCPUDestroyed((CraftingCPUCluster) (Object) this);
    }

    @Inject(method = "isActive", at = @At("HEAD"), cancellable = true, require = 1)
    private void injectIsActive(final CallbackInfoReturnable<Boolean> cir) {
        if (this.ec$virtualCPUOwner == null) return;
        cir.setReturnValue(
            ec$virtualCPUOwner.getProxy()
                .isActive());
    }

    @Inject(method = "getGrid", at = @At("HEAD"), cancellable = true, require = 1)
    private void injectGetGrid(final CallbackInfoReturnable<IGrid> cir) {
        if (this.ec$virtualCPUOwner == null) return;
        IGridNode node = ec$virtualCPUOwner.getProxy()
            .getNode();
        cir.setReturnValue(node == null ? null : node.getGrid());

    }

    @Inject(method = "getCore", at = @At("HEAD"), cancellable = true, require = 1)
    private void injectGetCore(final CallbackInfoReturnable<TileCraftingTile> cir) {
        if (this.ec$virtualCPUOwner == null) return;
        cir.setReturnValue(null);
    }

    @Inject(method = "getWorld", at = @At("HEAD"), cancellable = true, require = 1)
    private void injectGetWorld(final CallbackInfoReturnable<World> cir) {
        if (this.ec$virtualCPUOwner == null) return;
        cir.setReturnValue(
            ec$virtualCPUOwner.getBaseMetaTileEntity()
                .getWorld());

    }

    @Inject(method = "markDirty", at = @At("HEAD"), cancellable = true, require = 1)
    private void injectMarkDirty(final CallbackInfo ci) {
        if (this.ec$virtualCPUOwner == null) return;
        this.ec$virtualCPUOwner.markDirty();
        ci.cancel();

    }

    @Override
    public void ec$setAvailableStorage(long availableStorage) {
        this.availableStorage = availableStorage;
    }

    @Override
    public void ec$setAccelerators(int accelerators) {
        this.accelerator = accelerators;
    }

    @Override
    public void ec$setVirtualCPUOwner(@Nullable QuantumComputer isVirtualCPUOwner) {
        this.ec$virtualCPUOwner = isVirtualCPUOwner;
        this.machineSrc = new MachineSource(isVirtualCPUOwner);
    }

    @Override
    public void ec$markDestroyed() {
        this.isDestroyed = true;
        this.isComplete = true;
    }

    @Override
    public QuantumComputer ec$getVirtualCPUOwner() {
        return ec$virtualCPUOwner;
    }
}
