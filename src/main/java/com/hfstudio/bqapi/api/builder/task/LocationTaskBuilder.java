package com.hfstudio.bqapi.api.builder.task;

import java.util.Objects;

import com.hfstudio.bqapi.api.builder.TaskBuilder;
import com.hfstudio.bqapi.api.definition.LocationTaskDefinition;

public final class LocationTaskBuilder extends TaskBuilder<LocationTaskDefinition, LocationTaskBuilder> {

    private String labelKey = "";
    private int x;
    private int y;
    private int z;
    private int dimension;
    private int range = -1;
    private boolean visible;
    private boolean hideInfo;
    private boolean invert;
    private boolean taxiCab;

    public LocationTaskBuilder(String id) {
        super(id);
    }

    public LocationTaskBuilder labelKey(String labelKey) {
        this.labelKey = Objects.requireNonNull(labelKey, "labelKey");
        return this;
    }

    public LocationTaskBuilder x(int x) {
        this.x = x;
        return this;
    }

    public LocationTaskBuilder y(int y) {
        this.y = y;
        return this;
    }

    public LocationTaskBuilder z(int z) {
        this.z = z;
        return this;
    }

    public LocationTaskBuilder dimension(int dimension) {
        this.dimension = dimension;
        return this;
    }

    public LocationTaskBuilder range(int range) {
        this.range = range;
        return this;
    }

    public LocationTaskBuilder visible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public LocationTaskBuilder hideInfo(boolean hideInfo) {
        this.hideInfo = hideInfo;
        return this;
    }

    public LocationTaskBuilder invert(boolean invert) {
        this.invert = invert;
        return this;
    }

    public LocationTaskBuilder taxiCab(boolean taxiCab) {
        this.taxiCab = taxiCab;
        return this;
    }

    @Override
    protected LocationTaskBuilder self() {
        return this;
    }

    @Override
    public LocationTaskDefinition build() {
        return new LocationTaskDefinition(
            id,
            slot,
            labelKey,
            x,
            y,
            z,
            dimension,
            range,
            visible,
            hideInfo,
            invert,
            taxiCab);
    }
}
