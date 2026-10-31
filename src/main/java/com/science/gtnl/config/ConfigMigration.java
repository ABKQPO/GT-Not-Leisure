package com.science.gtnl.config;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnllib.GTNLLib;
import com.gtnllib.config.GTNLLibConfig;

// Moves the settings of the old single-file layout into the split files; must run before any config is registered.
public class ConfigMigration {

    private static final Logger LOG = LogManager.getLogger("GTNL Config");

    private static final String OLD_MAIN_FILE = "GTNotLeisure";
    private static final String OLD_CLIENT_CATEGORY = "general.client";
    private static final String TEXT_EFFECTS = "enableTextEffects";

    private record Move(String oldFile, String oldCategory, String newFile, String newCategory, boolean client) {}

    private static final Move[] MOVES = { main("debug", ConfigRegistry.GENERAL_FILE),
        main("effect", ConfigRegistry.GENERAL_FILE), main("message", ConfigRegistry.GENERAL_FILE),
        main("minecraft", ConfigRegistry.GENERAL_FILE), main("other", ConfigRegistry.GENERAL_FILE),
        main("tickrate", ConfigRegistry.GENERAL_FILE), main("wireless", ConfigRegistry.GENERAL_FILE),
        main("item", ConfigRegistry.ITEM_FILE), main("machine", ConfigRegistry.MACHINE_FILE),
        main("recipe", ConfigRegistry.RECIPE_FILE), main("re_avaritia", ConfigRegistry.RE_AVARITIA_FILE),
        new Move(OLD_MAIN_FILE, OLD_CLIENT_CATEGORY, ConfigRegistry.CLIENT_FILE, "client", true),
        new Move("GTNotLeisureSounds", "sound", ConfigRegistry.CLIENT_FILE, "sound", true),
        new Move("MEMiniTerminalClient", "general", ConfigRegistry.CLIENT_FILE, "mini_terminal", true),
        new Move("QuantumCraftingClient", "general", ConfigRegistry.CLIENT_FILE, "quantum_crafting", true),
        new Move("YogSothothTerminalClient", "general", ConfigRegistry.CLIENT_FILE, "yog_sothoth_terminal", true) };

    private static Move main(String category, String newFile) {
        return new Move(OLD_MAIN_FILE, "general." + category, newFile, category, false);
    }

    public static void migrate(File configDir, boolean client) {
        File directory = new File(configDir, ConfigRegistry.DIRECTORY);
        if (client) moveTextEffectsSetting(directory, new File(configDir, GTNLLib.CONFIG_DIRECTORY));

        Map<String, Configuration> sources = new LinkedHashMap<>();
        Map<String, Configuration> targets = new HashMap<>();
        for (Move move : MOVES) {
            if (move.client() && !client) continue;
            File oldFile = file(directory, move.oldFile());
            if (!oldFile.isFile()) continue;
            Configuration source = sources.computeIfAbsent(move.oldFile(), name -> new Configuration(oldFile));
            if (!source.hasCategory(move.oldCategory())) continue;
            Configuration target = targets
                .computeIfAbsent(move.newFile(), name -> new Configuration(file(directory, name)));
            if (target.hasCategory(move.newCategory())) continue;
            copy(source.getCategory(move.oldCategory()), target, move.newCategory());
        }
        targets.values()
            .forEach(Configuration::save);

        for (String name : sources.keySet()) {
            File oldFile = file(directory, name);
            File backup = new File(directory, name + ".cfg.bak");
            if (backup.exists() && !backup.delete() || !oldFile.renameTo(backup)) {
                LOG.warn("Could not move the migrated config {} aside", oldFile);
            }
        }
    }

    private static File file(File directory, String name) {
        return new File(directory, name + ".cfg");
    }

    private static void copy(ConfigCategory from, Configuration target, String to) {
        ConfigCategory category = target.getCategory(to);
        for (Map.Entry<String, Property> entry : from.entrySet()) category.put(entry.getKey(), entry.getValue());
        for (ConfigCategory child : from.getChildren())
            copy(child, target, to + Configuration.CATEGORY_SPLITTER + child.getName());
    }

    private static void moveTextEffectsSetting(File directory, File libraryDirectory) {
        File oldFile = file(directory, OLD_MAIN_FILE);
        if (!oldFile.isFile()) return;
        Configuration oldConfig = new Configuration(oldFile);
        if (!oldConfig.hasCategory(OLD_CLIENT_CATEGORY)) return;
        ConfigCategory oldClient = oldConfig.getCategory(OLD_CLIENT_CATEGORY);
        if (!oldClient.containsKey(TEXT_EFFECTS)) return;

        Configuration newConfig = new Configuration(new File(libraryDirectory, GTNLLib.MODID + ".cfg"));
        if (newConfig.hasCategory(GTNLLibConfig.CATEGORY) && newConfig.getCategory(GTNLLibConfig.CATEGORY)
            .containsKey(TEXT_EFFECTS)) return;
        newConfig.get(GTNLLibConfig.CATEGORY, TEXT_EFFECTS, true)
            .set(
                oldClient.get(TEXT_EFFECTS)
                    .getBoolean(true));
        newConfig.save();
    }
}
