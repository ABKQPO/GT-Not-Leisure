package com.science.gtnl.config;

import static com.gtnltest.ContractAssertions.check;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import net.minecraftforge.common.config.Configuration;

import com.gtnllib.GTNLLib;
import com.gtnllib.config.GTNLLibConfig;

import cpw.mods.fml.relauncher.FMLInjectionData;

public class ConfigMigrationContracts {

    public static void run() throws IOException, ReflectiveOperationException {
        Path home = Files.createTempDirectory("gtnl-config");
        Field minecraftHome = FMLInjectionData.class.getDeclaredField("minecraftHome");
        minecraftHome.setAccessible(true);
        Object previous = minecraftHome.get(null);
        minecraftHome.set(null, home.toFile());
        try {
            Path configDir = home.resolve("config");
            Path dir = configDir.resolve(GTNLLib.CONFIG_DIRECTORY);
            Files.createDirectories(dir);
            Files.write(
                dir.resolve(ConfigMigration.MAIN_CONFIG_FILE),
                List.of("general {", "    client {", "        B:enableTextEffects=false", "    }", "}"));
            ConfigMigration.moveTextEffectsSetting(configDir.toFile());
            check(!textEffects(dir), "a player who turned text effects off keeps them off after the setting moved");

            Files.write(dir.resolve(GTNLLib.MODID + ".cfg"), List.of("client {", "    B:enableTextEffects=true", "}"));
            ConfigMigration.moveTextEffectsSetting(configDir.toFile());
            check(textEffects(dir), "the old text effects setting never overwrites one saved in the GTNLLib config");
        } finally {
            minecraftHome.set(null, previous);
        }
    }

    private static boolean textEffects(Path dir) {
        return new Configuration(
            dir.resolve(GTNLLib.MODID + ".cfg")
                .toFile()).get(GTNLLibConfig.CATEGORY, ConfigMigration.TEXT_EFFECTS, true)
                    .getBoolean(true);
    }
}
