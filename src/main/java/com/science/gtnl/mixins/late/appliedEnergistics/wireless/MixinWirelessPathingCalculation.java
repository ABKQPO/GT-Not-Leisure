package com.science.gtnl.mixins.late.appliedEnergistics.wireless;

import java.util.Map;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.common.wireless.ChannelBudget;
import com.science.gtnl.common.wireless.WirelessChannelManager;
import com.science.gtnl.common.wireless.WirelessPathingAccess;

import appeng.api.networking.IGrid;
import appeng.me.GridConnection;
import appeng.me.GridNode;
import appeng.me.pathfinding.PathingCalculation;

@Mixin(value = PathingCalculation.class, remap = false)
public abstract class MixinWirelessPathingCalculation implements WirelessPathingAccess {

    @Unique
    private ChannelBudget gtnl$wirelessBudget;

    @Unique
    private IGrid gtnl$wirelessGrid;

    @WrapOperation(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"),
        require = 2,
        allow = 2)
    private Object gtnl$independentEntrance(Map<Object, Object> faces, Object direction,
        Function<Object, Object> factory, Operation<Object> original, @Local(name = "gc") GridConnection connection) {
        // Cover both ordinary and creative-energy controller loops. The factory supplies AE2's native
        // 32-channel face (or its unlimited-mode capacity); infinite energy does not imply infinite channels.
        // Do not change unowned UNKNOWN connections, part internals, P2P or quantum bridges.
        return WirelessChannelManager.isOwned(connection) ? factory.apply(direction)
            : original.call(faces, direction, factory);
    }

    @Inject(method = "<init>", at = @At("RETURN"), require = 1)
    private void gtnl$initializeBudget(IGrid grid, CallbackInfo ci) {
        gtnl$wirelessGrid = grid;
        WirelessChannelManager.invalidateAllocation(grid);
        if (WirelessChannelManager.channelsEnabled() && WirelessChannelManager.manages(grid)) {
            gtnl$wirelessBudget = new ChannelBudget(WirelessChannelManager.capacity(grid));
        }
    }

    @Inject(method = "tryUseChannel", at = @At("HEAD"), cancellable = true, require = 1)
    private void gtnl$checkBudget(GridNode start, CallbackInfoReturnable<Boolean> cir) {
        if (gtnl$wirelessBudget != null && !gtnl$wirelessBudget.hasRemaining()) cir.setReturnValue(false);
    }

    @Inject(method = "tryUseChannel", at = @At("RETURN"), require = 1)
    private void gtnl$countAcceptedChannel(GridNode start, CallbackInfoReturnable<Boolean> cir) {
        // Native multiblock de-duplication and all cable/P2P bottleneck checks run before this succeeds.
        // This counts wired and wireless consumers together; failed routes cost no budget.
        if (gtnl$wirelessBudget != null && cir.getReturnValueZ()) gtnl$wirelessBudget.recordAllocation();
    }

    @Inject(method = "compute", at = @At("RETURN"), require = 1)
    private void gtnl$publishAllocation(CallbackInfo ci) {
        WirelessChannelManager.recordAllocation(
            gtnl$wirelessGrid,
            gtnl$wirelessBudget == null ? WirelessChannelManager.capacity(gtnl$wirelessGrid)
                : gtnl$wirelessBudget.capacity(),
            gtnl$wirelessBudget == null ? ((PathingCalculation) (Object) this).getChannelsInUse()
                : gtnl$wirelessBudget.allocated());
    }
}
