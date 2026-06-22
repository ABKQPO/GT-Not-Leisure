package com.science.gtnl.common.machine.monitor;

import java.math.BigInteger;
import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class EnergyMonitorRowSnapshot {

    private ItemStack iconStack;
    private String displayName = "";
    private String ownerName = "";
    private BigInteger eut = BigInteger.ZERO;
    private String formattedEut;
    private int voltageTier = -1;
    private EnergyMonitorCategory category = EnergyMonitorCategory.BASIC_MACHINE;
    private boolean wireless;
    private EnergyMonitorHighlightTarget highlightTarget = new EnergyMonitorHighlightTarget();

    public EnergyMonitorRowSnapshot copy() {
        EnergyMonitorRowSnapshot copy = new EnergyMonitorRowSnapshot();
        copy.iconStack = iconStack == null ? null : iconStack.copy();
        copy.displayName = displayName;
        copy.ownerName = ownerName;
        copy.eut = eut;
        copy.formattedEut = formattedEut;
        copy.voltageTier = voltageTier;
        copy.category = category;
        copy.wireless = wireless;
        copy.highlightTarget = new EnergyMonitorHighlightTarget(
            highlightTarget.getDimensionId(),
            highlightTarget.getX(),
            highlightTarget.getY(),
            highlightTarget.getZ());
        return copy;
    }

    public ItemStack getIconStack() {
        return iconStack;
    }

    public void setIconStack(ItemStack iconStack) {
        this.iconStack = iconStack;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName == null ? "" : displayName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName == null ? "" : ownerName;
    }

    public BigInteger getEut() {
        return eut;
    }

    public void setEut(BigInteger eut) {
        this.eut = eut == null ? BigInteger.ZERO : eut;
        this.formattedEut = null;
        this.voltageTier = -1;
    }

    public String getFormattedEut() {
        return formattedEut == null ? EnergyMonitorFormatter.formatBigInteger(eut) : formattedEut;
    }

    public void setFormattedEut(String formattedEut) {
        this.formattedEut = formattedEut;
    }

    public int getVoltageTier() {
        return voltageTier >= 0 ? voltageTier : EnergyMonitorFormatter.getVoltageTier(eut.abs());
    }

    public void setVoltageTier(int voltageTier) {
        this.voltageTier = voltageTier;
    }

    public EnergyMonitorCategory getCategory() {
        return category;
    }

    public void setCategory(EnergyMonitorCategory category) {
        this.category = category == null ? EnergyMonitorCategory.BASIC_MACHINE : category;
    }

    public boolean isWireless() {
        return wireless;
    }

    public void setWireless(boolean wireless) {
        this.wireless = wireless;
    }

    public EnergyMonitorHighlightTarget getHighlightTarget() {
        return highlightTarget;
    }

    public void setHighlightTarget(EnergyMonitorHighlightTarget highlightTarget) {
        this.highlightTarget = highlightTarget == null ? new EnergyMonitorHighlightTarget() : highlightTarget;
    }

    public boolean sameAs(EnergyMonitorRowSnapshot other) {
        if (other == null) {
            return false;
        }
        return ItemStack.areItemStacksEqual(iconStack, other.iconStack)
            && Objects.equals(displayName, other.displayName)
            && Objects.equals(ownerName, other.ownerName)
            && Objects.equals(eut, other.eut)
            && wireless == other.wireless
            && highlightTarget.getDimensionId() == other.highlightTarget.getDimensionId()
            && highlightTarget.getX() == other.highlightTarget.getX()
            && highlightTarget.getY() == other.highlightTarget.getY()
            && highlightTarget.getZ() == other.highlightTarget.getZ();
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        if (iconStack != null) {
            tag.setTag("iconStack", iconStack.writeToNBT(new NBTTagCompound()));
        }
        tag.setString("displayName", displayName);
        tag.setString("ownerName", ownerName);
        tag.setString("eut", eut.toString());
        tag.setBoolean("wireless", wireless);
        tag.setInteger("dimensionId", highlightTarget.getDimensionId());
        tag.setInteger("x", highlightTarget.getX());
        tag.setInteger("y", highlightTarget.getY());
        tag.setInteger("z", highlightTarget.getZ());
        return tag;
    }

    public void deserializeNBT(NBTTagCompound tag) {
        iconStack = tag.hasKey("iconStack") ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag("iconStack")) : null;
        setDisplayName(tag.getString("displayName"));
        setOwnerName(tag.getString("ownerName"));
        setEut(parseBigInteger(tag.getString("eut")));
        if (tag.hasKey("formattedEut")) {
            setFormattedEut(tag.getString("formattedEut"));
        }
        if (tag.hasKey("voltageTier")) {
            setVoltageTier(tag.getInteger("voltageTier"));
        }
        if (tag.hasKey("category")) {
            int categoryOrdinal = tag.getInteger("category");
            EnergyMonitorCategory[] categories = EnergyMonitorCategory.values();
            setCategory(categories[Math.max(0, Math.min(categories.length - 1, categoryOrdinal))]);
        }
        setWireless(tag.getBoolean("wireless"));
        setHighlightTarget(
            new EnergyMonitorHighlightTarget(
                tag.getInteger("dimensionId"),
                tag.getInteger("x"),
                tag.getInteger("y"),
                tag.getInteger("z")));
    }

    public static EnergyMonitorRowSnapshot fromNBT(NBTTagCompound tag) {
        EnergyMonitorRowSnapshot row = new EnergyMonitorRowSnapshot();
        row.deserializeNBT(tag);
        return row;
    }

    private static BigInteger parseBigInteger(String value) {
        if (value == null || value.isEmpty()) {
            return BigInteger.ZERO;
        }
        try {
            return new BigInteger(value);
        } catch (NumberFormatException ignored) {
            return BigInteger.ZERO;
        }
    }
}
