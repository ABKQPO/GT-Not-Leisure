package com.science.gtnl.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

@LateMixin
public class LateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.ScienceNotLeisure.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return Arrays.asList();
    }
}
