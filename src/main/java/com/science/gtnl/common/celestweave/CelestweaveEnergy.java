package com.science.gtnl.common.celestweave;

import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.common.item.items.equipment.EnergyModuleItem;
import com.science.gtnl.config.ItemConfig;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class CelestweaveEnergy {

    private static final double FLUSH_SHARE = 0.01;
    private static final Map<UUID, double[]> PENDING = new Object2ObjectOpenHashMap<>();

    public static double capacity(ItemStack stack) {
        return EnergyModuleItem.capacity(CelestweaveData.energyTier(stack), ItemConfig.celestweave.baseCapacity);
    }

    public static void write(ItemStack stack, double stored) {
        EquipmentEnergy.write(stack, stored, capacity(stack));
    }

    public static double fraction(ItemStack stack) {
        return EquipmentEnergy.fraction(stack, capacity(stack));
    }

    public static void clamp(ItemStack stack) {
        EquipmentEnergy.clamp(stack, capacity(stack));
    }

    private static double pending(EntityPlayer player, int piece) {
        double[] pending = PENDING.get(player.getUniqueID());
        return pending == null ? 0 : pending[piece];
    }

    public static double available(EntityPlayer player, int piece, ItemStack stack) {
        if (player.worldObj.isRemote) return EquipmentEnergy.read(stack);
        return Math.max(0, EquipmentEnergy.read(stack) - pending(player, piece));
    }

    public static boolean hasEnergy(EntityPlayer player, int piece, ItemStack stack) {
        return EquipmentEnergy.free(player) || available(player, piece, stack) > 0;
    }

    public static boolean affords(EntityPlayer player, int piece, ItemStack stack, double eu) {
        return hasEnergy(player, piece, stack)
            && (EquipmentEnergy.free(player) || available(player, piece, stack) >= eu);
    }

    public static boolean activeAndHasEnergy(EntityPlayer player, int piece, CelestweaveModule module) {
        ItemStack stack = CelestweaveData.piece(player, piece);
        return stack != null && CelestweaveData.active(stack, module) && hasEnergy(player, piece, stack);
    }

    public static boolean activeAndAffords(EntityPlayer player, int piece, CelestweaveModule module, double eu) {
        ItemStack stack = CelestweaveData.piece(player, piece);
        return stack != null && CelestweaveData.active(stack, module) && affords(player, piece, stack, eu);
    }

    public static boolean drain(EntityPlayer player, int piece, ItemStack stack, double eu) {
        if (eu <= 0 || EquipmentEnergy.free(player)) return true;
        if (player.worldObj.isRemote) return EquipmentEnergy.read(stack) >= eu;
        if (available(player, piece, stack) < eu) return false;
        add(player, piece, eu);
        return true;
    }

    public static double drainUpTo(EntityPlayer player, int piece, ItemStack stack, double eu) {
        if (eu <= 0 || player.worldObj.isRemote) return 0;
        double paid = Math.min(eu, available(player, piece, stack));
        if (paid > 0) add(player, piece, paid);
        return paid;
    }

    private static void add(EntityPlayer player, int piece, double eu) {
        PENDING.computeIfAbsent(player.getUniqueID(), k -> new double[CelestweaveArmor.PIECES])[piece] += eu;
    }

    public static double availableSet(EntityPlayer player) {
        double total = 0;
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) {
            ItemStack stack = CelestweaveData.piece(player, piece);
            if (stack != null) total += available(player, piece, stack);
        }
        return total;
    }

    public static double drainSetUpTo(EntityPlayer player, double eu) {
        if (eu <= 0) return 0;
        double total = availableSet(player);
        if (total <= 0) return 0;
        double share = Math.min(1, eu / total), paid = 0;
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) {
            ItemStack stack = CelestweaveData.piece(player, piece);
            if (stack == null) continue;
            paid += drainUpTo(player, piece, stack, available(player, piece, stack) * share);
        }
        return paid;
    }

    public static void flush(EntityPlayer player, int piece, ItemStack stack) {
        double[] pending = PENDING.get(player.getUniqueID());
        if (pending == null || pending[piece] <= 0) return;
        if (stack != null) EquipmentEnergy.settle(stack, pending[piece]);
        pending[piece] = 0;
    }

    public static void flushLarge(EntityPlayer player) {
        double[] pending = PENDING.get(player.getUniqueID());
        if (pending == null) return;
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) {
            ItemStack stack = CelestweaveData.piece(player, piece);
            if (stack != null && pending[piece] > capacity(stack) * FLUSH_SHARE) flush(player, piece, stack);
        }
    }

    public static void flush(EntityPlayer player) {
        double[] pending = PENDING.remove(player.getUniqueID());
        if (pending == null) return;
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) {
            ItemStack stack = CelestweaveData.piece(player, piece);
            if (stack != null && pending[piece] > 0) EquipmentEnergy.settle(stack, pending[piece]);
        }
    }

    public static void discard(EntityPlayer player) {
        PENDING.remove(player.getUniqueID());
    }

    public static boolean drainWithWireless(EntityPlayer player, double eu) {
        if (eu <= 0 || EquipmentEnergy.free(player)) return true;
        boolean wireless = ItemConfig.celestweave.enableWirelessFallback;
        double set = availableSet(player);
        if (set + (wireless && set < eu ? EquipmentEnergy.wirelessAvailable(player) : 0) < eu) return false;
        double paid = drainSetUpTo(player, eu);
        if (paid < eu && wireless) paid += EquipmentEnergy.takeWireless(player, eu - paid);
        return eu - paid < 1;
    }

    public static void refillFromWireless(ItemStack stack, EntityPlayerMP player) {
        if (ItemConfig.celestweave.enableWirelessRefill)
            EquipmentEnergy.refillFromWireless(stack, player, capacity(stack));
    }
}
