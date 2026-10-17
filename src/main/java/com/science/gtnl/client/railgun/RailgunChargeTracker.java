package com.science.gtnl.client.railgun;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.item.items.ElectromagneticRailgun;
import com.science.gtnl.common.railgun.RailgunChargeTier;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunModuleType;

public class RailgunChargeTracker {

    public static final int FULL_CHARGE_UNITS = RailgunChargeTier.EHV3.chargeTicks;

    private RailgunChargeTracker() {}

    public static boolean charging(EntityPlayer player) {
        return player != null && !player.isDead && player.isUsingItem() && railgun(player.getItemInUse());
    }

    public static boolean holding(EntityPlayer player) {
        return player != null && railgun(player.getHeldItem());
    }

    public static int units(EntityPlayer player) {
        if (!charging(player)) return 0;
        return player.getItemInUseDuration() * accelerationStep(player.getItemInUse());
    }

    public static RailgunChargeTier tier(EntityPlayer player) {
        return RailgunChargeTier.fromUnits(units(player));
    }

    public static float progress(EntityPlayer player) {
        return Math.min(1F, units(player) / (float) FULL_CHARGE_UNITS);
    }

    public static boolean soundEnabled(EntityPlayer player) {
        ItemStack stack = player == null ? null : player.getHeldItem();
        return railgun(stack) && RailgunData.settings(stack)
            .sound();
    }

    public static boolean railgun(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ElectromagneticRailgun;
    }

    private static int accelerationStep(ItemStack stack) {
        return 1 + RailgunData.modules(stack)
            .count(RailgunModuleType.ACCELERATION);
    }
}
