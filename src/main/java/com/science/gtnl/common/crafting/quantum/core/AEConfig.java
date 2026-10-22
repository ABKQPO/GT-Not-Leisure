package com.science.gtnl.common.crafting.quantum.core;

import java.util.concurrent.TimeUnit;

import com.science.gtnl.config.MainConfig;

public final class AEConfig {

    public static final Crafting CRAFTING = new Crafting();
    private static final AEConfig INSTANCE = new AEConfig();

    public static AEConfig instance() {
        return INSTANCE;
    }

    public long getCraftingFallbackNanos() {
        return TimeUnit.SECONDS.toNanos(Math.max(0, MainConfig.machine.quantum_computer.calculationFallbackSeconds));
    }

    public boolean isCraftingPerformanceLogEnabled() {
        return MainConfig.machine.quantum_computer.enableCalculationPerformanceLog;
    }

    public static final class Crafting {

        public int craftingCalculationPausingInterval = 10000;
    }
}
