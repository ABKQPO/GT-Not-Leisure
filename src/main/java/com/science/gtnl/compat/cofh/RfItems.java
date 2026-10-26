package com.science.gtnl.compat.cofh;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;

// stored, capacity and receive may only be called for items isRfItem accepted.
public class RfItems {

    private RfItems() {}

    public static boolean isRfItem(Item item) {
        return Mods.COFHCore.isModLoaded() && energyContainer(item);
    }

    @Optional.Method(modid = "CoFHCore")
    public static int stored(ItemStack stack) {
        return ((IEnergyContainerItem) stack.getItem()).getEnergyStored(stack);
    }

    @Optional.Method(modid = "CoFHCore")
    public static int capacity(ItemStack stack) {
        return ((IEnergyContainerItem) stack.getItem()).getMaxEnergyStored(stack);
    }

    @Optional.Method(modid = "CoFHCore")
    public static int receive(ItemStack stack, int amount) {
        return ((IEnergyContainerItem) stack.getItem()).receiveEnergy(stack, amount, false);
    }

    @Optional.Method(modid = "CoFHCore")
    private static boolean energyContainer(Item item) {
        return item instanceof IEnergyContainerItem;
    }
}
