package com.science.gtnl.compat.ae2things;

import net.minecraft.item.ItemStack;

import com.gtnllib.mod.ModList;

public class AE2ThingsCompat {

    private AE2ThingsCompat() {}

    // AE2Things infusion pattern terminals mimic standard patterns with the tc_crafting flag.
    public static boolean isInfusionPattern(ItemStack stack) {
        if (!ModList.AE2Thing.isModLoaded()) return false;
        return stack.stackTagCompound != null && stack.stackTagCompound.hasKey("tc_crafting");
    }
}
