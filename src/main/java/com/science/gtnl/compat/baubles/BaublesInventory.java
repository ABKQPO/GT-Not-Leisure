package com.science.gtnl.compat.baubles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;

public class BaublesInventory {

    public static IInventory of(EntityPlayer player) {
        return Mods.Baubles.isModLoaded() ? baubles(player) : null;
    }

    @Optional.Method(modid = "Baubles")
    private static IInventory baubles(EntityPlayer player) {
        return BaublesApi.getBaubles(player);
    }
}
