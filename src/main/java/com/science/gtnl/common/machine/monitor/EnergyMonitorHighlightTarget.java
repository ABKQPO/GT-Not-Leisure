package com.science.gtnl.common.machine.monitor;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EnergyMonitorHighlightTarget {

    private int dimensionId;
    private int x;
    private int y;
    private int z;

    public EnergyMonitorHighlightTarget() {}

    public EnergyMonitorHighlightTarget(int dimensionId, int x, int y, int z) {
        this.dimensionId = dimensionId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
