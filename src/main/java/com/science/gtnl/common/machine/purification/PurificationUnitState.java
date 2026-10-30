package com.science.gtnl.common.machine.purification;

import java.math.BigInteger;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;

public class PurificationUnitState {

    private long maxParallel = 1;
    private long effectiveParallel = 1;
    private BigInteger costingEU = BigInteger.ZERO;
    private boolean wirelessMode;
    private UUID ownerUUID;

    public long getMaxParallel() {
        return maxParallel;
    }

    public long getEffectiveParallel() {
        return effectiveParallel;
    }

    public BigInteger getCostingEU() {
        return costingEU;
    }

    public boolean isWirelessMode() {
        return wirelessMode;
    }

    public void setWirelessMode(boolean enabled) {
        wirelessMode = enabled;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void bindOwner(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public void setMaxParallel(long maxParallel) {
        this.maxParallel = Math.max(1, maxParallel);
    }

    public long limitParallel(long available) {
        this.effectiveParallel = Math.min(this.maxParallel, available);
        return this.effectiveParallel;
    }

    public void resetParallel() {
        this.effectiveParallel = 1;
    }

    public String getCostingEUText() {
        return NumberFormatUtil.formatNumber(costingEU);
    }

    public void resetCost() {
        this.costingEU = BigInteger.ZERO;
    }

    public void addCost(BigInteger cost) {
        this.costingEU = this.costingEU.add(cost);
    }

    public void readFromNBT(NBTTagCompound aNBT) {
        if (aNBT.hasKey("wirelessMode")) this.wirelessMode = aNBT.getBoolean("wirelessMode");
        if (aNBT.hasKey("configuredParallelLong")) setMaxParallel(aNBT.getLong("configuredParallelLong"));
        if (aNBT.hasKey("effectiveParallelLong")) this.effectiveParallel = aNBT.getLong("effectiveParallelLong");
    }

    public void writeToNBT(NBTTagCompound aNBT) {
        aNBT.setBoolean("wirelessMode", this.wirelessMode);
        aNBT.setLong("configuredParallelLong", this.maxParallel);
        aNBT.setLong("effectiveParallelLong", this.effectiveParallel);
    }
}
