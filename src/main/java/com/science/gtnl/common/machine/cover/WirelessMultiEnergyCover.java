package com.science.gtnl.common.machine.cover;

import static gregtech.common.misc.WirelessNetworkManager.*;
import static java.lang.Long.*;

import java.util.UUID;

import gregtech.api.covers.CoverContext;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.common.covers.CoverLegacyData;

public class WirelessMultiEnergyCover extends CoverLegacyData {

    public long transferredEnergyPerOperation;
    public int maxAmp;

    public WirelessMultiEnergyCover(CoverContext context, int voltage, int amp) {
        super(context);
        this.maxAmp = amp;
        this.transferredEnergyPerOperation = voltage * amp * ticks_between_energy_addition;
    }

    @Override
    public boolean isRedstoneSensitive(long aTimer) {
        return false;
    }

    @Override
    public boolean allowsCopyPasteTool() {
        return false;
    }

    @Override
    public boolean allowsTickRateAddition() {
        return false;
    }

    @Override
    public void doCoverThings(byte aInputRedstone, long aTimer) {
        if (coverData == 0 || aTimer % ticks_between_energy_addition / 2 == 0) {
            tryFetchingEnergy(coveredTile.get());
        }
        coverData = 1;
    }

    public static UUID getOwner(Object te) {
        if (te instanceof BaseMetaTileEntity igte) {
            return igte.getOwnerUuid();
        } else {
            return null;
        }
    }

    public void tryFetchingEnergy(ICoverable tileEntity) {
        if (tileEntity instanceof BaseMetaTileEntity bmte) {
            long currentEU = bmte.getStoredEUuncapped();
            long euToTransfer = min(transferredEnergyPerOperation - currentEU, transferredEnergyPerOperation);
            if (euToTransfer <= 0) return; // nothing to transfer
            if (!addEUToGlobalEnergyMap(getOwner(tileEntity), -euToTransfer)) return;
            bmte.increaseStoredEnergyUnits(euToTransfer, true);
        }
    }

    @Override
    public boolean alwaysLookConnected() {
        return true;
    }

    @Override
    public int getMinimumTickRate() {
        return 20;
    }
}
