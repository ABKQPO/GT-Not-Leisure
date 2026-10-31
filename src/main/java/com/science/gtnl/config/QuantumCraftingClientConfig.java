package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.QuantumCraftingModePacket;

@Config(
    modid = ScienceNotLeisure.MODID,
    category = "quantum_crafting",
    configSubDirectory = ConfigRegistry.DIRECTORY,
    filename = ConfigRegistry.CLIENT_FILE)
@Config.ExcludeFromAutoGui
@Config.Comment("Quantum crafting preference of this client")
public class QuantumCraftingClientConfig {

    @Config.Comment("Plan player crafting requests with the quantum calculation when the network has a quantum computer.")
    @Config.DefaultBoolean(false)
    public static boolean useQuantumCraftingMode;

    @Config.Ignore
    private static volatile boolean syncScheduled;

    public static boolean isQuantumCraftingMode() {
        return useQuantumCraftingMode;
    }

    public static void setQuantumCraftingMode(boolean enabled) {
        if (useQuantumCraftingMode == enabled) return;
        useQuantumCraftingMode = enabled;
        ConfigRegistry.save(QuantumCraftingClientConfig.class);
    }

    public static void scheduleSync() {
        syncScheduled = true;
    }

    public static void syncIfScheduled() {
        if (!syncScheduled) return;
        syncScheduled = false;
        // The server starts every session with the mode off
        if (useQuantumCraftingMode) {
            ScienceNotLeisure.network.sendToServer(new QuantumCraftingModePacket(true));
        }
    }
}
