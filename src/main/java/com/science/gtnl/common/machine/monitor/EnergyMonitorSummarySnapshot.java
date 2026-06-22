package com.science.gtnl.common.machine.monitor;

public class EnergyMonitorSummarySnapshot {

    private String totalEnergyText = "0";
    private String averageEuText = "0";
    private String ampText = "0.0";
    private int voltageTier;
    private boolean outputMode;
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

    public String getTotalEnergyText() {
        return totalEnergyText;
    }

    public void setTotalEnergyText(String totalEnergyText) {
        this.totalEnergyText = totalEnergyText == null ? "0" : totalEnergyText;
    }

    public String getAverageEuText() {
        return averageEuText;
    }

    public void setAverageEuText(String averageEuText) {
        this.averageEuText = averageEuText == null ? "0" : averageEuText;
    }

    public String getAmpText() {
        return ampText;
    }

    public void setAmpText(String ampText) {
        this.ampText = ampText == null ? "0.0" : ampText;
    }

    public int getVoltageTier() {
        return voltageTier;
    }

    public void setVoltageTier(int voltageTier) {
        this.voltageTier = voltageTier;
    }

    public boolean isOutputMode() {
        return outputMode;
    }

    public void setOutputMode(boolean outputMode) {
        this.outputMode = outputMode;
    }

    public boolean isEstimatedEmpty() {
        return estimatedEmpty;
    }

    public void setEstimatedEmpty(boolean estimatedEmpty) {
        this.estimatedEmpty = estimatedEmpty;
    }

    public String getEstimatedTimeText() {
        return estimatedTimeText;
    }

    public void setEstimatedTimeText(String estimatedTimeText) {
        this.estimatedTimeText = estimatedTimeText == null ? "" : estimatedTimeText;
    }
}
