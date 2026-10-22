package com.science.gtnl.client.quantum;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.QuantumCraftingModePacket;

@Config(modid = ScienceNotLeisure.MODID, configSubDirectory = "GTNotLeisure", filename = "QuantumCraftingClient")
@Config.ExcludeFromAutoGui
public final class QuantumCraftingClientConfig {

    @Config.Comment("Plan player crafting requests with the quantum calculation when the network has a quantum computer.")
    @Config.DefaultBoolean(false)
    public static boolean useQuantumCraftingMode;

    @Config.Ignore
    private static boolean registered;

    @Config.Ignore
    private static boolean loadFailed;

    @Config.Ignore
    private static volatile boolean syncScheduled;

    public static boolean isQuantumCraftingMode() {
        ensureLoaded();
        return useQuantumCraftingMode;
    }

    public static void setQuantumCraftingMode(boolean enabled) {
        ensureLoaded();
        if (useQuantumCraftingMode == enabled) return;
        useQuantumCraftingMode = enabled;
        if (registered) ConfigurationManager.save(QuantumCraftingClientConfig.class);
    }

    public static void scheduleSync() {
        syncScheduled = true;
    }

    public static void syncIfScheduled() {
        if (!syncScheduled) return;
        syncScheduled = false;
        // The server starts every session with the mode off
        if (isQuantumCraftingMode()) {
            ScienceNotLeisure.network.sendToServer(new QuantumCraftingModePacket(true));
        }
    }

    private static void ensureLoaded() {
        if (registered || loadFailed) return;
        try {
            ConfigurationManager.registerConfig(QuantumCraftingClientConfig.class);
            registered = true;
        } catch (ConfigException e) {
            loadFailed = true;
            ScienceNotLeisure.LOG.error("Failed to load quantum crafting client preferences", e);
        }
    }
}
