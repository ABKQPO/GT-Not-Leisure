package com.science.gtnl.common.celestweave;

import net.minecraft.item.ItemStack;

import gregtech.api.enums.Mods;

public final class CelestweaveCompat {

    private CelestweaveCompat() {}

    public static boolean isHoloGlasses(ItemStack stack) {
        return Mods.HoloInventory.isModLoaded() && CelestweaveHoloCompat.isGlasses(stack);
    }

    public static boolean holoShouldRender(ItemStack held) {
        return Mods.HoloInventory.isModLoaded() && CelestweaveHoloCompat.shouldRender(held);
    }
}
