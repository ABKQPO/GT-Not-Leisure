package com.science.gtnl.asm;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.mixins.Mixins;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class GTNLEarlyCoreMod implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public static boolean enableAprilFool;

    public static Logger LOGGER = LogManager.getLogger("GTNL Asm Core Mod");

    public GTNLEarlyCoreMod() {}

    static {
        try {
            if (System.getProperty("java.version")
                .startsWith("1.8")) {
                LOGGER.info("Patching ObfuscationRun.theConstructor for Java 8 compatibility...");
                ObfuscationRunPatcher.patchConstructor();
            } else {
                LOGGER.warn("Skipping ObfuscationRun patch, not running Java 8.");
            }
        } catch (Throwable t) {
            LOGGER.error("Failed to patch ObfuscationRun", t);
        }

        try {
            ConfigurationManager.registerConfig(MainConfig.class);
        } catch (ConfigException e) {
            LOGGER.error("Failed to register config", e);
        }

        LocalDate today = LocalDate.now();
        enableAprilFool = today.getMonthValue() == 4 && today.getDayOfMonth() == 1;
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public String getMixinConfig() {
        return "mixins.sciencenotleisure.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return IMixins.getEarlyMixins(Mixins.class, loadedCoreMods);
    }
}
