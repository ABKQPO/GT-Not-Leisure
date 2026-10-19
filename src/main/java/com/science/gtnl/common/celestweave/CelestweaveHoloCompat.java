package com.science.gtnl.common.celestweave;

import net.dries007.holoInventory.api.IHoloGlasses;
import net.minecraft.item.ItemStack;

final class CelestweaveHoloCompat {

    private CelestweaveHoloCompat() {}

    static boolean isGlasses(ItemStack stack) {
        return stack != null && stack.getItem() instanceof IHoloGlasses;
    }

    static boolean shouldRender(ItemStack held) {
        return held != null && held.getItem() instanceof IHoloGlasses glasses && glasses.shouldRender(held);
    }
}
