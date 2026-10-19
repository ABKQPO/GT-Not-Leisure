package com.science.gtnl.common.celestweave;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.config.MainConfig;

import gregtech.api.enums.GTValues;
import gregtech.common.misc.WirelessNetworkManager;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;
import ic2.api.item.ElectricItem;

public final class CelestweaveEnergy {

    private static final int[] VOLTAGE_TIERS = { 3, 5, 6, 7 };
    private static final Map<UUID, double[]> PENDING = new HashMap<>();

    private CelestweaveEnergy() {}

    public static double capacity(ItemStack stack) {
        var config = MainConfig.item.celestweave;
        double capacity = switch (CelestweaveData.energyTier(stack)) {
            case 3 -> config.energyCapacityT3;
            case 2 -> config.energyCapacityT2;
            case 1 -> config.energyCapacityT1;
            default -> config.baseCapacity;
        };
        return Double.isFinite(capacity) ? Math.max(0, capacity) : 0;
    }

    public static int voltageTier(ItemStack stack) {
        return VOLTAGE_TIERS[CelestweaveData.energyTier(stack)];
    }

    public static double transferLimit(ItemStack stack) {
        return GTValues.V[voltageTier(stack)];
    }

    public static double read(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()
            || !stack.getTagCompound()
                .hasKey("charge", 99))
            return 0;
        double charge = stack.getTagCompound()
            .getDouble("charge");
        return Double.isFinite(charge) ? Math.max(0, charge) : 0;
    }

    public static void write(ItemStack stack, double charge) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound()
            .setDouble("charge", Math.max(0, Math.min(capacity(stack), charge)));
    }

    private static void deduct(ItemStack stack, double eu) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound()
            .setDouble("charge", Math.max(0, read(stack) - eu));
    }

    public static double fraction(ItemStack stack) {
        double capacity = capacity(stack);
        return capacity <= 0 ? 0 : Math.min(1, read(stack) / capacity);
    }

    public static void fill(ItemStack stack) {
        write(stack, capacity(stack));
    }

    public static void clamp(ItemStack stack) {
        if (read(stack) > capacity(stack)) write(stack, capacity(stack));
    }

    public static double pending(EntityPlayer player, int type) {
        double[] pending = PENDING.get(player.getUniqueID());
        return pending == null ? 0 : pending[type];
    }

    public static double available(EntityPlayer player, int type, ItemStack stack) {
        if (player.worldObj.isRemote) return read(stack);
        return Math.max(0, read(stack) - pending(player, type));
    }

    public static boolean powered(EntityPlayer player, int type, ItemStack stack) {
        return player.capabilities.isCreativeMode || available(player, type, stack) > 0;
    }

    public static boolean affords(EntityPlayer player, int type, ItemStack stack, double eu) {
        return powered(player, type, stack)
            && (player.capabilities.isCreativeMode || available(player, type, stack) >= eu);
    }

    public static boolean drain(EntityPlayer player, int type, ItemStack stack, double eu) {
        if (eu <= 0 || player.capabilities.isCreativeMode) return true;
        if (player.worldObj.isRemote) return read(stack) >= eu;
        if (available(player, type, stack) < eu) return false;
        add(player, type, stack, eu);
        return true;
    }

    public static double drainUpTo(EntityPlayer player, int type, ItemStack stack, double eu) {
        if (eu <= 0 || player.worldObj.isRemote) return 0;
        double paid = Math.min(eu, available(player, type, stack));
        if (paid > 0) add(player, type, stack, paid);
        return paid;
    }

    private static void add(EntityPlayer player, int type, ItemStack stack, double eu) {
        double[] pending = PENDING.computeIfAbsent(player.getUniqueID(), k -> new double[4]);
        pending[type] += eu;
    }

    public static double availableSet(EntityPlayer player) {
        double total = 0;
        for (int type = 0; type < 4; type++) {
            ItemStack stack = CelestweaveServerEvents.piece(player, type);
            if (stack != null) total += available(player, type, stack);
        }
        return total;
    }

    public static double drainSetUpTo(EntityPlayer player, double eu) {
        if (eu <= 0) return 0;
        double total = availableSet(player);
        if (total <= 0) return 0;
        double share = Math.min(1, eu / total), paid = 0;
        for (int type = 0; type < 4; type++) {
            ItemStack stack = CelestweaveServerEvents.piece(player, type);
            if (stack == null) continue;
            paid += drainUpTo(player, type, stack, available(player, type, stack) * share);
        }
        return paid;
    }

    public static void flush(EntityPlayer player, int type, ItemStack stack) {
        double[] pending = PENDING.get(player.getUniqueID());
        if (pending == null || pending[type] <= 0) return;
        if (stack != null) deduct(stack, pending[type]);
        pending[type] = 0;
    }

    public static void flushLarge(EntityPlayer player) {
        double[] pending = PENDING.get(player.getUniqueID());
        if (pending == null) return;
        for (int type = 0; type < 4; type++) {
            if (pending[type] <= 0) continue;
            ItemStack stack = CelestweaveServerEvents.piece(player, type);
            if (stack != null && pending[type] > capacity(stack) * 0.01) flush(player, type, stack);
        }
    }

    public static void flush(EntityPlayer player) {
        double[] pending = PENDING.remove(player.getUniqueID());
        if (pending == null) return;
        for (int type = 0; type < 4; type++) {
            if (pending[type] <= 0) continue;
            ItemStack stack = CelestweaveServerEvents.piece(player, type);
            if (stack != null) deduct(stack, pending[type]);
        }
    }

    public static void discard(EntityPlayer player) {
        PENDING.remove(player.getUniqueID());
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

    public static boolean pay(EntityPlayer player, double eu) {
        if (eu <= 0 || player.capabilities.isCreativeMode) return true;
        boolean wireless = MainConfig.item.celestweave.wirelessFallback;
        double set = availableSet(player);
        if (set + (wireless && set < eu ? wirelessAvailable(player) : 0) < eu) return false;
        double paid = drainSetUpTo(player, eu);
        if (paid < eu && wireless) paid += takeWireless(player, eu - paid);
        return eu - paid < 1;
    }

    public static void refillFromWireless(ItemStack stack, EntityPlayerMP player) {
        if (!MainConfig.item.celestweave.wirelessRefill) return;
        double room = ElectricItem.manager.charge(stack, capacity(stack), Integer.MAX_VALUE, true, true);
        if (room < Math.max(1, capacity(stack) * 0.01)) return;
        double taken = takeWireless(player, room);
        if (taken <= 0) return;
        double accepted = ElectricItem.manager.charge(stack, taken, Integer.MAX_VALUE, true, false);
        long refund = (long) (taken - accepted);
        if (refund > 0) WirelessNetworkManager.addEUToGlobalEnergyMap(wirelessOwner(player), refund);
    }
}
