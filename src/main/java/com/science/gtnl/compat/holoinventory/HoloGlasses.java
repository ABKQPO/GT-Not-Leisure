package com.science.gtnl.compat.holoinventory;

import net.dries007.holoInventory.api.IHoloGlasses;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;

// Callable without HoloInventory installed: its classes are only touched inside the optional methods.
public class HoloGlasses {

    private HoloGlasses() {}

    public static boolean isGlasses(ItemStack stack) {
        return Mods.HoloInventory.isModLoaded() && glasses(stack);
    }

    public static boolean shouldRender(ItemStack held) {
        return Mods.HoloInventory.isModLoaded() && renders(held);
    }

    @Optional.Method(modid = "holoinventory")
    private static boolean glasses(ItemStack stack) {
        return stack != null && stack.getItem() instanceof IHoloGlasses;
    }

    @Optional.Method(modid = "holoinventory")
    private static boolean renders(ItemStack held) {
        return held != null && held.getItem() instanceof IHoloGlasses glasses && glasses.shouldRender(held);
    }
}
