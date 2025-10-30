package com.science.gtnl.mixins;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.ModList;

import io.github.tox1cozz.mixinbooterlegacy.ILateMixinLoader;
import io.github.tox1cozz.mixinbooterlegacy.LateMixin;

@LateMixin
public class LateMixinLoader implements ILateMixinLoader {

    public static final String LOG_PREFIX = "[GTNL]" + ' ';
    private static final Map<String, BooleanSupplier> MIXIN_CONFIGS = new LinkedHashMap<>();

    static {
        addMixinCFG("mixins.sciencenotleisure.late.json");
        addMixinCFG("mixins.sciencenotleisure.late.rc.json");
        addMixinCFG("mixins.sciencenotleisure.late.Debug.json", () -> MainConfig.enableDebugMode);
        addMixinCFG(
            "mixins.sciencenotleisure.late.Overpowered.json",
            () -> !ModList.Overpowered.isModLoaded() && MainConfig.enableRecipeOutputChance);
        addMixinCFG("mixins.sciencenotleisure.late.NoNHU.json", () -> !ModList.NHUtilities.isModLoaded());
        addMixinCFG(
            "mixins.sciencenotleisure.late.TwistSpaceTechnology.json",
            ModList.TwistSpaceTechnology::isModLoaded);
        addMixinCFG("mixins.sciencenotleisure.late.PPChange.json", () -> MainConfig.enablePurificationPlantBuff);
        addMixinCFG("mixins.sciencenotleisure.late.OPChange.json", () -> MainConfig.enableIntegratedOreFactoryChange);
        addMixinCFG("mixins.sciencenotleisure.late.Stick.json", () -> MainConfig.enableStickItem);
    }

    @Override
    public List<String> getMixinConfigs() {
        return new ArrayList<>(MIXIN_CONFIGS.keySet());
    }

    @Override
    public boolean shouldMixinConfigQueue(final String mixinConfig) {
        var supplier = MIXIN_CONFIGS.get(mixinConfig);
        if (supplier == null) {
            ScienceNotLeisure.LOG
                .warn(LOG_PREFIX + "Mixin config {} is not found in config map! It will never be loaded.", mixinConfig);
            return false;
        }
        return supplier.getAsBoolean();
    }

    private static void addMixinCFG(final String mixinConfig) {
        MIXIN_CONFIGS.put(mixinConfig, () -> true);
    }

    private static void addMixinCFG(final String mixinConfig, final BooleanSupplier conditions) {
        MIXIN_CONFIGS.put(mixinConfig, conditions);
    }
}
