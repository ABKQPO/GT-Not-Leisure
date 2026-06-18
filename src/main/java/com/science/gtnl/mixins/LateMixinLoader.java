package com.science.gtnl.mixins;

import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;

@LateMixin
public class LateMixinLoader implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        int v = Runtime.version()
            .feature();
        if (v >= 21) return "mixins.sciencenotleisure.late.j21.json";
        if (v >= 17) return "mixins.sciencenotleisure.late.j17.json";
        return "mixins.sciencenotleisure.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return IMixins.getLateMixins(Mixins.class, loadedMods);
    }
}
