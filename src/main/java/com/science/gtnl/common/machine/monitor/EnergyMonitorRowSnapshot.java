package com.science.gtnl.common.machine.monitor;

import java.math.BigInteger;
import java.util.Objects;

import net.minecraft.item.ItemStack;

public class EnergyMonitorRowSnapshot {

    private ItemStack iconStack;
    private String displayName = "";
    private String ownerName = "";
    private BigInteger eut = BigInteger.ZERO;
    private String formattedEut = "0";
    private int voltageTier;
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
    }

    public String getFormattedEut() {
        return formattedEut;
    }

    public void setFormattedEut(String formattedEut) {
        this.formattedEut = formattedEut == null ? "0" : formattedEut;
    }

    public int getVoltageTier() {
        return voltageTier;
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
            && Objects.equals(formattedEut, other.formattedEut)
            && voltageTier == other.voltageTier
            && category == other.category
            && wireless == other.wireless
            && highlightTarget.getDimensionId() == other.highlightTarget.getDimensionId()
            && highlightTarget.getX() == other.highlightTarget.getX()
            && highlightTarget.getY() == other.highlightTarget.getY()
            && highlightTarget.getZ() == other.highlightTarget.getZ();
    }
}
