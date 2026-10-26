package com.science.gtnl.compat.avaritia;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.science.gtnl.utils.SimpleItem;

import cpw.mods.fml.common.Optional;
import fox.spiteful.avaritia.crafting.ExtremeCraftingManager;
import fox.spiteful.avaritia.tile.TileEntityDireCrafting;
import gregtech.api.enums.Mods;

// Callable without Avaritia installed: its classes are only touched inside the optional methods.
public class DireCrafting {

    private DireCrafting() {}

    public static ItemStack output(InventoryCrafting grid, World world) {
        return Mods.Avaritia.isModLoaded() ? findOutput(grid, world) : null;
    }

    public static SimpleItem[] inputs(TileEntity tile) {
        return Mods.Avaritia.isModLoaded() ? readInputs(tile) : null;
    }

    @Optional.Method(modid = "Avaritia")
    private static ItemStack findOutput(InventoryCrafting grid, World world) {
        return ExtremeCraftingManager.getInstance()
            .findMatchingRecipe(grid, world);
    }

    @Optional.Method(modid = "Avaritia")
    private static SimpleItem[] readInputs(TileEntity tile) {
        if (!(tile instanceof TileEntityDireCrafting table)) return null;
        SimpleItem[] inputs = new SimpleItem[81];
        for (int i = 1; i < table.getSizeInventory(); i++) {
            inputs[i - 1] = SimpleItem.getInstance(table.getStackInSlot(i));
        }
        return inputs;
    }
}
