package com.science.gtnl.common.celestweave;

import net.minecraft.item.ItemStack;

public class CelestweaveSettings {

    private CelestweaveSettings() {}

    public static boolean setEnabled(ItemStack stack, CelestweaveModule module, boolean enabled) {
        if (!installed(stack, module)) return false;
        CelestweaveData.writeFlag(stack, CelestweaveData.DISABLED, module, !enabled);
        return true;
    }

    public static boolean setShown(ItemStack stack, CelestweaveModule module, boolean shown) {
        if (!installed(stack, module) || !module.hideable()) return false;
        CelestweaveData.writeFlag(stack, CelestweaveData.HIDDEN, module, !shown);
        return true;
    }

    public static boolean setValue(ItemStack stack, CelestweaveModuleSetting setting, float value) {
        if (!Float.isFinite(value) || !installed(stack, setting.module)) return false;
        setting.write(stack, value);
        return true;
    }

    public static boolean setCosmetic(ItemStack stack, CelestweaveCosmeticOption option, float value) {
        if (stack == null || !Float.isFinite(value)) return false;
        CelestweaveData.writeCosmetic(
            stack,
            CelestweaveData.cosmetic(stack)
                .with(option, value));
        return true;
    }

    private static boolean installed(ItemStack stack, CelestweaveModule module) {
        return stack != null && module != null
            && CelestweaveData.installed(stack)
                .contains(module);
    }
}
