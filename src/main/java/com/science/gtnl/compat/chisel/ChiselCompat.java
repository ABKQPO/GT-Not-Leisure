package com.science.gtnl.compat.chisel;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.cricketcraft.chisel.api.carving.CarvingUtils;
import com.cricketcraft.chisel.api.carving.ICarvingRegistry;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;

public class ChiselCompat {

    private ChiselCompat() {}

    public static boolean loaded() {
        return Mods.Chisel.isModLoaded();
    }

    public static List<ItemStack> variants(ItemStack stack) {
        return loaded() ? chiselVariants(stack) : null;
    }

    @Optional.Method(modid = Mods.ModIDs.CHISEL)
    private static List<ItemStack> chiselVariants(ItemStack stack) {
        ICarvingRegistry registry = CarvingUtils.getChiselRegistry();
        return registry == null ? null : registry.getItemsForChiseling(stack);
    }
}
