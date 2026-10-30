package com.gtnllib.mixins;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigSource;
import org.spongepowered.asm.mixin.transformer.Config;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.gtnewhorizon.gtnhmixins.Reflection;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;

public class GTNLLibEarlyMixins implements IEarlyMixinLoader {

    @Override
    public String getMixinConfig() {
        int v = Runtime.version()
            .feature();
        if (v >= 21) return "mixins.gtnllib.early.j21.json";
        if (v >= 17) return "mixins.gtnllib.early.j17.json";
        return "mixins.gtnllib.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return IMixins.getEarlyMixins(GTNLLibMixins.class, loadedCoreMods);
    }

    public static void queueFromHost(Set<String> loadedCoreMods) {
        GTNLLibEarlyMixins loader = new GTNLLibEarlyMixins();
        try {
            Config config = Config.create(loader.getMixinConfig(), (IMixinConfigSource) null);
            Reflection.mixinClassesField.set(Reflection.configField.get(config), loader.getMixins(loadedCoreMods));
            Reflection.registerConfigurationMethod.invoke(null, config);
        } catch (ReflectiveOperationException e) {
            LogManager.getLogger(GTNLLibEarlyMixins.class)
                .error("Failed to queue the GTNLLib early mixins", e);
        }
    }
}
