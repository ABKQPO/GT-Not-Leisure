package com.science.gtnl.mixins.late.randomComplement;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.utils.RCQuantumCluster;

import appeng.api.AEApi;
import appeng.me.cache.helpers.ConnectionWrapper;
import appeng.me.cluster.implementations.QuantumCluster;
import appeng.tile.qnb.TileQuantumBridge;

@Mixin(value = QuantumCluster.class, remap = false)
public abstract class MixinQuantumCluster implements RCQuantumCluster {

    @Shadow
    private long otherSide;

    @Shadow
    private ConnectionWrapper connection;

    @Shadow
    private TileQuantumBridge[] Ring;

    @Unique
    @Override
    public TileQuantumBridge[] r$getRing() {
        return this.Ring;
    }

    @Shadow
    @Override
    public abstract TileQuantumBridge getCenter();

    @Shadow
    private boolean isDestroyed;

    @Inject(method = "destroy", at = @At("HEAD"))
    public void destroy(CallbackInfo ci) {
        if (!this.isDestroyed) {
            r$setIdlePowerUsage(0);
        }
    }

    @Inject(
        method = "updateStatus",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/IAppEngApi;createGridConnection(Lappeng/api/networking/IGridNode;Lappeng/api/networking/IGridNode;)Lappeng/api/networking/IGridConnection;",
            shift = At.Shift.AFTER))
    public void updateStatusI(boolean updateGrid, CallbackInfo ci) {
        r$setIdlePowerUsage(22);
    }

    @Inject(
        method = "updateStatus",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/networking/IGridConnection;destroy()V",
            shift = At.Shift.AFTER))
    public void updateStatusD(boolean updateGrid, CallbackInfo ci) {
        r$setIdlePowerUsage(0);
    }

    @Unique
    @Override
    public boolean r$noWork() {
        return this.connection == null || this.connection.getConnection() == null;
    }

    @Unique
    private void r$setIdlePowerUsage(double power) {
        r$setIdlePowerUsage(this, power);
        r$setIdlePowerUsage(
            this.otherSide == 0L ? null
                : AEApi.instance()
                    .registries()
                    .locatable()
                    .getLocatableBy(this.otherSide),
            power);
    }

    @Unique
    @Override
    public void r$setIdlePowerUsage(Object obj, double power) {
        if (obj instanceof RCQuantumCluster qc) {
            for (TileQuantumBridge q : qc.r$getRing()) {
                q.getProxy()
                    .setIdlePowerUsage(power);
            }
            if (qc.getCenter() != null) qc.getCenter()
                .getProxy()
                .setIdlePowerUsage(power);
        }
    }

}
