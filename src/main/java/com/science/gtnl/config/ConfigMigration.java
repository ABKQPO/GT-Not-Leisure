package com.science.gtnl.config;

import java.io.File;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import com.gtnllib.GTNLLib;
import com.gtnllib.config.GTNLLibConfig;

public class ConfigMigration {

    public static final String MAIN_CONFIG_FILE = "GTNotLeisure.cfg";
    public static final String OLD_CLIENT_CATEGORY = "general.client";
    public static final String TEXT_EFFECTS = "enableTextEffects";

    // Must run before MainConfig is registered: registering removes the keys MainConfig no longer declares.
    public static void moveTextEffectsSetting(File configDir) {
        File directory = new File(configDir, GTNLLib.CONFIG_DIRECTORY);
        File oldFile = new File(directory, MAIN_CONFIG_FILE);
        if (!oldFile.isFile()) return;
        Configuration oldConfig = new Configuration(oldFile);
        if (!oldConfig.hasCategory(OLD_CLIENT_CATEGORY)) return;
        ConfigCategory oldClient = oldConfig.getCategory(OLD_CLIENT_CATEGORY);
        if (!oldClient.containsKey(TEXT_EFFECTS)) return;

        Configuration newConfig = new Configuration(new File(directory, GTNLLib.MODID + ".cfg"));
        if (newConfig.hasCategory(GTNLLibConfig.CATEGORY) && newConfig.getCategory(GTNLLibConfig.CATEGORY)
            .containsKey(TEXT_EFFECTS)) return;
        newConfig.get(GTNLLibConfig.CATEGORY, TEXT_EFFECTS, true)
            .set(
                oldClient.get(TEXT_EFFECTS)
                    .getBoolean(true));
        newConfig.save();
    }
}
