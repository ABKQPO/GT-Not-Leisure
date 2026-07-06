package com.science.gtnl.common.machine.monitor;

import lombok.Getter;
import lombok.Setter;

@Getter
public class EnergyMonitorSummarySnapshot {

    private String totalEnergyText = "0";
    private String averageEuText = "0";
    private String ampText = "0.0";
    @Setter
    private int voltageTier;
    @Setter
    private boolean outputMode;
    @Setter
    private boolean estimatedEmpty;
    private String estimatedTimeText = "";

    public static EnergyMonitorSummarySnapshot empty() {
        EnergyMonitorSummarySnapshot snapshot = new EnergyMonitorSummarySnapshot();
        snapshot.totalEnergyText = "0";
        snapshot.averageEuText = "0";
        snapshot.ampText = "0.0";
        snapshot.voltageTier = 0;
        snapshot.outputMode = false;
        snapshot.estimatedEmpty = false;
        snapshot.estimatedTimeText = "";
        return snapshot;
    }

    public void setTotalEnergyText(String totalEnergyText) {
        this.totalEnergyText = totalEnergyText == null ? "0" : totalEnergyText;
    }

    public void setAverageEuText(String averageEuText) {
        this.averageEuText = averageEuText == null ? "0" : averageEuText;
    }

    public void setAmpText(String ampText) {
        this.ampText = ampText == null ? "0.0" : ampText;
    }

    public void setEstimatedTimeText(String estimatedTimeText) {
        this.estimatedTimeText = estimatedTimeText == null ? "" : estimatedTimeText;
    }
}
