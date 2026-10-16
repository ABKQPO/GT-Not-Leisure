package com.science.gtnl.common.railgun;

import java.math.BigInteger;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.science.gtnl.config.MainConfig;

import gregtech.common.misc.WirelessNetworkManager;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;
import ic2.api.item.ElectricItem;

public final class RailgunEnergy {

    private RailgunEnergy() {}

    public static double capacity(ItemStack stack) {
        var config = MainConfig.item.railgun;
        var modules = RailgunData.modules(stack);
        double capacity = modules.count(RailgunModuleType.ENERGY_T3) > 0 ? config.energyCapacityT3
            : modules.count(RailgunModuleType.ENERGY_T2) > 0 ? config.energyCapacityT2
                : modules.count(RailgunModuleType.ENERGY_T1) > 0 ? config.energyCapacityT1 : config.baseCapacity;
        return Double.isFinite(capacity) ? Math.max(0, capacity) : 0;
    }

    public static double read(ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound()
            .hasKey("charge", 99)) return 0;
        double charge = ElectricItem.manager.getCharge(stack);
        return Double.isFinite(charge) ? Math.max(0, charge) : 0;
    }

    public static boolean tryConsume(ItemStack stack, double eu) {
        if (!Double.isFinite(eu) || eu < 0 || read(stack) < eu) return false;
        ElectricItem.manager.discharge(stack, eu, Integer.MAX_VALUE, true, false, false);
        return true;
    }

    public static void refillFromWireless(ItemStack stack, EntityPlayerMP player) {
        if (!MainConfig.item.railgun.wirelessRefill || player.isUsingItem()) return;
        double room = ElectricItem.manager.charge(stack, capacity(stack), Integer.MAX_VALUE, true, true);
        if (room < 1) return;
        UUID owner = SpaceProjectManager.getLeader(player.getUniqueID());
        WirelessNetworkManager.strongCheckOrAddUser(owner);
        BigInteger available = WirelessNetworkManager.getUserEU(owner);
        long take = Math.min(
            (long) Math.min(room, Long.MAX_VALUE),
            available.min(BigInteger.valueOf(Long.MAX_VALUE))
                .longValue());
        if (take > 0 && WirelessNetworkManager.addEUToGlobalEnergyMap(owner, -take)) {
            double accepted = ElectricItem.manager.charge(stack, take, Integer.MAX_VALUE, true, false);
            long refund = (long) (take - accepted);
            if (refund > 0) WirelessNetworkManager.addEUToGlobalEnergyMap(owner, refund);
        }
    }
}
