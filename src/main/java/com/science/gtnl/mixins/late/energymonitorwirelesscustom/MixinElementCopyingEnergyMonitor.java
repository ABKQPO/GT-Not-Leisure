package com.science.gtnl.mixins.late.energymonitorwirelesscustom;

import java.math.BigInteger;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.machine.monitor.EnergyMonitorCustomWirelessEutProvider;
import com.science.gtnl.common.machine.multiblock.ElementCopying;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.common.misc.WirelessNetworkManager;

@Mixin(value = ElementCopying.class, remap = false)
public class MixinElementCopyingEnergyMonitor implements EnergyMonitorCustomWirelessEutProvider {

    @Unique
    private BigInteger gtnl$energyMonitorWirelessEut = BigInteger.ZERO;

    @Inject(method = "checkProcessing", at = @At("HEAD"))
    private void gtnl$resetEnergyMonitorWirelessCost(CallbackInfoReturnable<CheckRecipeResult> callbackInfo) {
        gtnl$energyMonitorWirelessEut = BigInteger.ZERO;
    }

    @Redirect(
        method = "checkProcessing",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/common/misc/WirelessNetworkManager;addEUToGlobalEnergyMap(Ljava/util/UUID;J)Z"))
    private boolean gtnl$captureEnergyMonitorWirelessCost(UUID ownerUuid, long amount) {
        gtnl$energyMonitorWirelessEut = BigInteger.valueOf(amount);
        return WirelessNetworkManager.addEUToGlobalEnergyMap(ownerUuid, amount);
    }

    @Override
    public BigInteger getEnergyMonitorWirelessEut() {
        ElementCopying machine = (ElementCopying) (Object) this;
        IGregTechTileEntity baseMetaTileEntity = machine.getBaseMetaTileEntity();
        if (!machine.wirelessMode || baseMetaTileEntity == null || !baseMetaTileEntity.isActive()) {
            return BigInteger.ZERO;
        }
        return gtnl$energyMonitorWirelessEut;
    }
}
