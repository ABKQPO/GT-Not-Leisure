package com.science.gtnl.common.machine.voidminer;

import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;

public class VoidMinerState {

    public static final String NO_OVERRIDE = "None";

    private long lEUt;
    private String warning = "";
    private int dimensionChangeVersion;
    private String activeDropMapDimension = "";
    private String lastDimensionOverride = NO_OVERRIDE;

    public long getEUt() {
        return lEUt;
    }

    public void setEUt(long lEUt) {
        this.lEUt = lEUt;
    }

    public String getWarning() {
        return warning;
    }

    public void warn(String translationKey) {
        this.warning = translationKey;
    }

    public void clearWarning() {
        this.warning = "";
    }

    public String getLastDimensionOverride() {
        return lastDimensionOverride;
    }

    public boolean hasOverride() {
        return !NO_OVERRIDE.equals(lastDimensionOverride);
    }

    public boolean isOverride(String dimensionKey) {
        return Objects.equals(dimensionKey, lastDimensionOverride);
    }

    public void setLastDimensionOverride(String dimensionKey) {
        this.lastDimensionOverride = dimensionKey == null || dimensionKey.isEmpty() ? NO_OVERRIDE : dimensionKey;
    }

    public String getActiveDropMapDimension() {
        return activeDropMapDimension;
    }

    public int getDimensionChangeVersion() {
        return dimensionChangeVersion;
    }

    public boolean switchDropMapDimension(String dimensionName) {
        boolean changed = !activeDropMapDimension.isEmpty() && !Objects.equals(activeDropMapDimension, dimensionName);
        this.activeDropMapDimension = dimensionName;
        if (changed) dimensionChangeVersion++;
        return changed;
    }

    public boolean clearDropMapDimension() {
        boolean hadDimension = !activeDropMapDimension.isEmpty();
        this.activeDropMapDimension = "";
        if (hadDimension) dimensionChangeVersion++;
        return hadDimension;
    }

    public void readDimensionFromNBT(NBTTagCompound aNBT) {
        setLastDimensionOverride(aNBT.getString("mLastDimensionOverride"));
        this.activeDropMapDimension = aNBT.getString("vmTweak$activeDropMapDimension");
    }

    public void writeDimensionToNBT(NBTTagCompound aNBT) {
        aNBT.setString("mLastDimensionOverride", lastDimensionOverride);
        aNBT.setString("vmTweak$activeDropMapDimension", activeDropMapDimension);
    }
}
