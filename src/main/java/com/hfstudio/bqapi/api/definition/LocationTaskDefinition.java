package com.hfstudio.bqapi.api.definition;

import java.util.Objects;

public final class LocationTaskDefinition extends TaskDefinition {

    private final String labelKey;
    private final int x;
    private final int y;
    private final int z;
    private final int dimension;
    private final int range;
    private final boolean visible;
    private final boolean hideInfo;
    private final boolean invert;
    private final boolean taxiCab;

    public LocationTaskDefinition(String id, int slot, String labelKey, int x, int y, int z, int dimension, int range,
        boolean visible, boolean hideInfo, boolean invert, boolean taxiCab) {
        super(id, slot);
        this.labelKey = Objects.requireNonNull(labelKey, "labelKey");
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.range = range;
        this.visible = visible;
        this.hideInfo = hideInfo;
        this.invert = invert;
        this.taxiCab = taxiCab;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getDimension() {
        return dimension;
    }

    public int getRange() {
        return range;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isHideInfo() {
        return hideInfo;
    }

    public boolean isInvert() {
        return invert;
    }

    public boolean isTaxiCab() {
        return taxiCab;
    }
}
