package com.science.gtnl.common.crafting.quantum.core;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MachineConfig;

public class AELog {

    public static boolean isCraftingLogEnabled() {
        return MachineConfig.quantum_computer.enableCalculationLog;
    }

    public static void crafting(String message) {
        ScienceNotLeisure.LOG.info("[QuantumCrafting] {}", message);
    }

    public static void craftingDebug(String message) {
        ScienceNotLeisure.LOG.debug("[QuantumCrafting] {}", message);
    }

    public static void craftingPerformance(String format, Object... params) {
        ScienceNotLeisure.LOG.info("[QuantumCrafting] {}", String.format(format, params));
    }

    public static void info(String format, Object... params) {
        ScienceNotLeisure.LOG.info("[QuantumCrafting] {}", String.format(format, params));
    }

    public static void info(Throwable throwable, String message) {
        ScienceNotLeisure.LOG.info("[QuantumCrafting] {}", message, throwable);
    }
}
