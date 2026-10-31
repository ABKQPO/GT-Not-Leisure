package com.science.gtnl.common.celestweave;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.equipment.EquipmentNet;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.common.packet.CelestweaveFlightPacket;
import com.science.gtnl.config.ItemConfig;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class CelestweaveFlight implements ICelestweaveBehavior {

    private static final String TAG = "GTNLCelestweaveFlight", WAS_FLYING = "GTNLCelestweaveWasFlying";
    private static final double SYNC_RANGE = 256;
    private static final Set<UUID> FLYING = Collections.synchronizedSet(new ObjectOpenHashSet<>());

    CelestweaveFlight() {}

    private static double cost() {
        return ItemConfig.celestweave.flightEuPerTick;
    }

    public static boolean canFly(EntityPlayer player, ItemStack legs) {
        return legs != null && CelestweaveData.active(legs, CelestweaveModule.FLIGHT)
            && CelestweaveEnergy.affords(player, CelestweaveArmor.LEGS, legs, cost());
    }

    public static boolean cancelsFall(EntityPlayer player) {
        return canFly(player, CelestweaveData.piece(player, CelestweaveArmor.LEGS));
    }

    @Override
    public void tick(CelestweaveContext ctx) {
        EntityPlayer player = ctx.player();
        boolean affordable = ctx.affords(cost());
        boolean levitate = CelestweaveModuleSetting.FLIGHT_LEVITATE.on(ctx.stack());
        if (affordable && levitate && player.capabilities.allowFlying && !player.isRiding())
            player.capabilities.isFlying = true;
        if (!ctx.server()) return;
        if (affordable && player.capabilities.isFlying && ctx.moving()) affordable = ctx.drain(cost());
        boolean changed = false;
        if (affordable && !player.capabilities.allowFlying) {
            player.capabilities.allowFlying = true;
            player.getEntityData()
                .setBoolean(TAG, true);
            changed = true;
        } else if (!affordable && revoke(player)) changed = true;
        if (player.capabilities.isFlying) player.fallDistance = 0;
        if (changed) player.sendPlayerAbilities();
    }

    @Override
    public void unequip(CelestweaveContext ctx) {
        if (ctx.server() && revoke(ctx.player())) ctx.player()
            .sendPlayerAbilities();
    }

    private static boolean revoke(EntityPlayer player) {
        if (!player.getEntityData()
            .getBoolean(TAG)) return false;
        player.getEntityData()
            .removeTag(TAG);
        if (player.capabilities.isCreativeMode) return false;
        player.capabilities.allowFlying = false;
        player.capabilities.isFlying = false;
        return true;
    }

    public static void update(EntityPlayerMP player) {
        ItemStack legs = CelestweaveData.piece(player, CelestweaveArmor.LEGS);
        boolean active = legs != null && CelestweaveData.active(legs, CelestweaveModule.FLIGHT);
        if (!active && revoke(player)) player.sendPlayerAbilities();
        boolean flying = active && player.capabilities.isFlying;
        remember(player, flying);
        UUID id = player.getUniqueID();
        if (flying == FLYING.contains(id)) return;
        if (flying) FLYING.add(id);
        else FLYING.remove(id);
        EquipmentNet.sendAround(player, new CelestweaveFlightPacket(player.getEntityId(), flying), SYNC_RANGE);
    }

    public static void startTracking(EntityPlayer target, EntityPlayerMP viewer) {
        if (FLYING.contains(target.getUniqueID()))
            ScienceNotLeisure.network.sendTo(new CelestweaveFlightPacket(target.getEntityId(), true), viewer);
    }

    public static void changedDimension(EntityPlayer player) {
        FLYING.remove(player.getUniqueID());
        if (player instanceof EntityPlayerMP serverPlayer && serverPlayer.capabilities.allowFlying)
            serverPlayer.sendPlayerAbilities();
    }

    public static void forget(EntityPlayer player) {
        FLYING.remove(player.getUniqueID());
    }

    private static void remember(EntityPlayer player, boolean flying) {
        NBTTagCompound data = player.getEntityData();
        boolean wasFlying = flying && data.getBoolean(TAG);
        if (wasFlying == data.getBoolean(WAS_FLYING)) return;
        if (wasFlying) data.setBoolean(WAS_FLYING, true);
        else data.removeTag(WAS_FLYING);
    }

    public static void restore(EntityPlayerMP player) {
        if (!player.getEntityData()
            .getBoolean(WAS_FLYING)) return;
        player.getEntityData()
            .removeTag(WAS_FLYING);
        if (player.isRiding() || !canFly(player, CelestweaveData.piece(player, CelestweaveArmor.LEGS))) return;
        player.capabilities.allowFlying = true;
        player.capabilities.isFlying = true;
        player.getEntityData()
            .setBoolean(TAG, true);
        player.sendPlayerAbilities();
    }
}
