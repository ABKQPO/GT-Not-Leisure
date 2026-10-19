package com.science.gtnl.common.celestweave;

import net.minecraft.item.ItemStack;

public final class CelestweaveSettings {

    public static final int ENABLE = 0, SHOW = 1, VALUE = 2, COSMETIC = 3;

    private CelestweaveSettings() {}

    public static boolean apply(ItemStack stack, CelestweaveModule module, int op, String key, float value) {
        if (stack == null || !Float.isFinite(value)) return false;
        switch (op) {
            case ENABLE -> {
                if (module == null || !CelestweaveData.installed(stack)
                    .contains(module)) return false;
                CelestweaveData.writeFlag(stack, CelestweaveData.DISABLED, module, value < 0.5F);
                return true;
            }
            case SHOW -> {
                if (module == null || !module.hideable()
                    || !CelestweaveData.installed(stack)
                        .contains(module))
                    return false;
                CelestweaveData.writeFlag(stack, CelestweaveData.HIDDEN, module, value < 0.5F);
                return true;
            }
            case VALUE -> {
                CelestweaveModuleSetting setting = CelestweaveModuleSetting.byKey(module, key);
                if (setting == null || !CelestweaveData.installed(stack)
                    .contains(module)) return false;
                setting.write(stack, value);
                return true;
            }
            case COSMETIC -> {
                CelestweaveCosmeticOption option = CelestweaveCosmeticOption.byKey(key);
                if (option == null) return false;
                CelestweaveData.writeCosmetic(
                    stack,
                    CelestweaveData.cosmetic(stack)
                        .with(option, value));
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
