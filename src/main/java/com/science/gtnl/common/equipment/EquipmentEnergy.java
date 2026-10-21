package com.science.gtnl.common.equipment;

import java.math.BigInteger;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import gregtech.api.enums.GTValues;
import gregtech.common.misc.WirelessNetworkManager;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;

public class EquipmentEnergy {

    public static final String CHARGE = "charge";
    private static final int[] VOLTAGE_TIERS = { 3, 5, 6, 7 };
    private static final double REFILL_SHARE = 0.01;

    private EquipmentEnergy() {}

    public static int voltageTier(int energyTier) {
        return VOLTAGE_TIERS[Math.max(0, Math.min(VOLTAGE_TIERS.length - 1, energyTier))];
    }

    public static double transferLimit(int energyTier) {
        return GTValues.V[voltageTier(energyTier)];
    }

    public static double read(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()
            || !stack.getTagCompound()
                .hasKey(CHARGE, Constants.NBT.TAG_ANY_NUMERIC))
            return 0;
        double charge = stack.getTagCompound()
            .getDouble(CHARGE);
        return Double.isFinite(charge) ? Math.max(0, charge) : 0;
    }

    public static void write(ItemStack stack, double stored, double capacity) {
        store(stack, Math.min(capacity, stored));
    }

    public static void settle(ItemStack stack, double eu) {
        store(stack, read(stack) - eu);
    }

    private static void store(ItemStack stack, double stored) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound()
            .setDouble(CHARGE, Double.isFinite(stored) ? Math.max(0, stored) : 0);
    }

    public static void clamp(ItemStack stack, double capacity) {
        if (read(stack) > capacity) write(stack, capacity, capacity);
    }

    public static double fraction(ItemStack stack, double capacity) {
        return capacity <= 0 ? 0 : Math.min(1, read(stack) / capacity);
    }

    public static boolean free(EntityPlayer player) {
        return player != null && player.capabilities.isCreativeMode;
    }

    public static boolean drain(EntityPlayer player, ItemStack stack, double eu) {
        if (!Double.isFinite(eu) || eu < 0) return false;
        if (eu == 0 || free(player)) return true;
        if (read(stack) < eu) return false;
        settle(stack, eu);
        return true;
    }

    public static String format(double eu) {
        return String.format("%,.0f", eu);
    }

    public static UUID wirelessOwner(EntityPlayer player) {
        return SpaceProjectManager.getLeader(player.getUniqueID());
    }

    public static double wirelessAvailable(EntityPlayer player) {
        return WirelessNetworkManager.getUserEU(wirelessOwner(player))
            .min(BigInteger.valueOf(Long.MAX_VALUE))
            .doubleValue();
    }

    public static double takeWireless(EntityPlayer player, double eu) {
        if (eu < 1 || player.worldObj.isRemote) return 0;
        UUID owner = wirelessOwner(player);
        WirelessNetworkManager.strongCheckOrAddUser(owner);
        long take = (long) Math.min(
            Math.min(eu, Long.MAX_VALUE),
            WirelessNetworkManager.getUserEU(owner)
                .min(BigInteger.valueOf(Long.MAX_VALUE))
                .longValue());
        return take > 0 && WirelessNetworkManager.addEUToGlobalEnergyMap(owner, -take) ? take : 0;
    }

    public static void refillFromWireless(ItemStack stack, EntityPlayer player, double capacity) {
        double room = capacity - read(stack);
        if (room < Math.max(1, capacity * REFILL_SHARE)) return;
        double taken = takeWireless(player, room);
        if (taken > 0) write(stack, read(stack) + taken, capacity);
    }
}
