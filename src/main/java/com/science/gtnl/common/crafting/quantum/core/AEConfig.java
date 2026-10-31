package com.science.gtnl.common.crafting.quantum.core;

import java.util.concurrent.TimeUnit;

import net.minecraft.server.MinecraftServer;

import com.science.gtnl.config.MachineConfig;

public class AEConfig {

    public static final Crafting CRAFTING = new Crafting();
    private static final AEConfig INSTANCE = new AEConfig();

    public static AEConfig instance() {
        return INSTANCE;
    }

    public long getCraftingFallbackNanos() {
        return TimeUnit.SECONDS.toNanos(Math.max(0, MachineConfig.quantum_computer.calculationFallbackSeconds));
    }

    public long getCalculationLimitNanos() {
        var server = MinecraftServer.getServer();
        if (server == null || !server.isDedicatedServer()) {
            return 0;
        }
        return TimeUnit.SECONDS.toNanos(Math.max(0, MachineConfig.quantum_computer.calculationLimitSeconds));
    }

    public boolean isCraftingPerformanceLogEnabled() {
        return MachineConfig.quantum_computer.enableCalculationPerformanceLog;
    }

    public static class Crafting {

        public int craftingCalculationPausingInterval = 10000;
    }
}
