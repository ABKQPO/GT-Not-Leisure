package com.science.gtnl.common.machine.monitor;

import lombok.Getter;

@Getter
public enum EnergyMonitorMode {

    WIRED("gtnl.energy_monitor.mode.wired"),
    WIRELESS("gtnl.energy_monitor.mode.wireless"),
    ALL("gtnl.energy_monitor.mode.all");

    private final String translationKey;

    EnergyMonitorMode(String translationKey) {
        this.translationKey = translationKey;
    }

    public EnergyMonitorMode next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public EnergyMonitorMode previous() {
        return values()[(ordinal() + values().length - 1) % values().length];
    }
}
