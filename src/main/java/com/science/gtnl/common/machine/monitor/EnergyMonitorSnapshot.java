package com.science.gtnl.common.machine.monitor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EnergyMonitorSnapshot {

    private EnergyMonitorSummarySnapshot summary;
    private List<EnergyMonitorRowSnapshot> rows;
    private boolean moreRows;

    public EnergyMonitorSnapshot(EnergyMonitorSummarySnapshot summary, List<EnergyMonitorRowSnapshot> rows,
        boolean moreRows) {
        this.summary = summary == null ? EnergyMonitorSummarySnapshot.empty() : summary;
        this.rows = rows == null ? Collections.emptyList()
            : rows.stream()
                .map(EnergyMonitorRowSnapshot::copy)
                .collect(Collectors.toList());
        this.moreRows = moreRows;
    }

    public static EnergyMonitorSnapshot empty() {
        return new EnergyMonitorSnapshot(EnergyMonitorSummarySnapshot.empty(), Collections.emptyList(), false);
    }

    public EnergyMonitorSummarySnapshot getSummary() {
        return summary;
    }

    public void setSummary(EnergyMonitorSummarySnapshot summary) {
        this.summary = summary == null ? EnergyMonitorSummarySnapshot.empty() : summary;
    }

    public List<EnergyMonitorRowSnapshot> getRows() {
        return rows;
    }

    public void setRows(List<EnergyMonitorRowSnapshot> rows) {
        this.rows = rows == null ? Collections.emptyList() : rows;
    }

    public boolean hasMoreRows() {
        return moreRows;
    }

    public void setMoreRows(boolean moreRows) {
        this.moreRows = moreRows;
    }
}
