package com.science.gtnl.common.celestweave;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;

public final class CelestweavePresets {

    private CelestweavePresets() {}

    public static ItemStack revealingItem() {
        Item goggles = GameRegistry.findItem("Thaumcraft", "ItemGoggles");
        return new ItemStack(goggles != null ? goggles : Items.ender_eye);
    }

    public static ItemStack holoItem() {
        Item glasses = GameRegistry.findItem("holoinventory", "Hologlasses");
        return glasses != null ? new ItemStack(glasses) : revealingItem();
    }

    public static ItemStack backpackItem() {
        return GTNLItemList.PortableDiamondChest.hasBeenSet() ? GTNLItemList.PortableDiamondChest.get(1)
            : new ItemStack(Items.diamond);
    }
}
