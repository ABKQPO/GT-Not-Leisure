package com.gtnllib.mixins;

import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
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

}
