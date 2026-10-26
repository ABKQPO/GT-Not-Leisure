package com.science.gtnl.common.machine.cover;

import static java.lang.Long.min;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;

import org.jetbrains.annotations.NotNull;

import com.google.common.io.ByteArrayDataInput;
import com.science.gtnl.utils.machine.MachineUtils;

import gregtech.api.covers.CoverContext;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.common.covers.Cover;
import gregtech.common.misc.WirelessNetworkManager;
import io.netty.buffer.ByteBuf;

public class WirelessMultiEnergyCover extends Cover {

    // Same int and save format as the old GT CoverLegacyData base, so covers placed before keep their settings.
    public int coverData;

    public long transferredEnergyPerOperation;
    public int maxAmp;

    public WirelessMultiEnergyCover(CoverContext context, int voltage, int amp) {
        super(context, null);
        this.maxAmp = amp;
        this.transferredEnergyPerOperation = voltage * amp * WirelessNetworkManager.ticks_between_energy_addition;
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
        if (coverData == 0 || aTimer % WirelessNetworkManager.ticks_between_energy_addition / 2 == 0) {
            tryFetchingEnergy(coveredTile.get());
        }
        coverData = 1;
    }

    public void tryFetchingEnergy(ICoverable tileEntity) {
        if (tileEntity instanceof BaseMetaTileEntity bmte) {
            long currentEU = bmte.getStoredEUuncapped();
            long euToTransfer = min(transferredEnergyPerOperation - currentEU, transferredEnergyPerOperation);
            if (euToTransfer <= 0) return;
            if (!WirelessNetworkManager.addEUToGlobalEnergyMap(MachineUtils.getOwner(tileEntity), -euToTransfer))
                return;
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

    @Override
    protected void readDataFromPacket(ByteArrayDataInput byteData) {
        coverData = byteData.readInt();
    }

    @Override
    protected void writeDataToByteBuf(ByteBuf byteBuf) {
        byteBuf.writeInt(coverData);
    }

    @Override
    public boolean isRedstoneSensitive(long aTimer) {
        return true;
    }

    @Override
    protected void readDataFromNbt(NBTBase nbt) {
        coverData = nbt instanceof NBTTagInt tagInt ? tagInt.func_150287_d() : 0;
    }

    @Override
    protected @NotNull NBTBase saveDataToNbt() {
        return new NBTTagInt(coverData);
    }
}
