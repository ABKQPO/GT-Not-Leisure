package com.science.gtnl.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ConfigRegistry {

    public static final String DIRECTORY = "GTNotLeisure";
    public static final String LANG_PATTERN = "gtnl.gui.config.%cat.%field";

    public static final String GENERAL_FILE = "general";
    public static final String ITEM_FILE = "item";
    public static final String MACHINE_FILE = "machine";
    public static final String RECIPE_FILE = "recipe";
    public static final String RE_AVARITIA_FILE = "re_avaritia";
    public static final String CLIENT_FILE = "client";

    private static final Logger LOG = LogManager.getLogger("GTNL Config");

    public static Class<?>[] common() {
        return new Class<?>[] { ItemConfig.class, MachineConfig.class, RecipeConfig.class, ReAvaritiaConfig.class,
            EffectConfig.class, MessageConfig.class, MinecraftConfig.class, OtherConfig.class, TickrateConfig.class,
            WirelessConfig.class, DebugConfig.class };
    }

    @SideOnly(Side.CLIENT)
    public static Class<?>[] client() {
        return new Class<?>[] { ClientConfig.class, EquipmentSoundConfig.class, MiniTerminalClientConfig.class,
            QuantumCraftingClientConfig.class, YogTerminalClientConfig.class };
    }

    @SideOnly(Side.CLIENT)
    public static Class<?>[] gui() {
        Class<?>[] common = common();
        Class<?>[] gui = new Class<?>[common.length + 2];
        gui[0] = ClientConfig.class;
        gui[1] = EquipmentSoundConfig.class;
        System.arraycopy(common, 0, gui, 2, common.length);
        return gui;
    }

    public static void registerCommon() {
        register(common());
    }

    @SideOnly(Side.CLIENT)
    public static void registerClient() {
        register(client());
    }

    public static void save(Class<?>... types) {
        for (Class<?> type : types) if (ConfigurationManager.getConfig(type) == null) return;
        ConfigurationManager.save(types);
    }

    private static void register(Class<?>... types) {
        for (Class<?> type : types) {
            try {
                ConfigurationManager.registerConfig(type);
            } catch (ConfigException e) {
                LOG.error("Failed to register config {}", type.getSimpleName(), e);
            }
        }
    }
}
