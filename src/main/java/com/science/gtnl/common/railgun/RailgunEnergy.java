package com.science.gtnl.common.railgun;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.item.items.equipment.EnergyModuleItem;
import com.science.gtnl.config.MainConfig;

public class RailgunEnergy {

    private RailgunEnergy() {}

    public static double capacity(ItemStack stack) {
        return EnergyModuleItem.capacity(
            RailgunData.modules(stack)
                .energyTier(),
            MainConfig.item.railgun.baseCapacity);
    }

    public static double available(EntityPlayer player, ItemStack stack) {
        return EquipmentEnergy.free(player) ? Double.POSITIVE_INFINITY : EquipmentEnergy.read(stack);
    }

    public static void clamp(ItemStack stack) {
        EquipmentEnergy.clamp(stack, capacity(stack));
    }

    public static void refillFromWireless(ItemStack stack, EntityPlayerMP player) {
        if (!MainConfig.item.railgun.wirelessRefill || player.isUsingItem()) return;
        EquipmentEnergy.refillFromWireless(stack, player, capacity(stack));
    }
}
