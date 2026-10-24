package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;
import com.science.gtnl.common.railgun.RailgunChargeTier;
import com.science.gtnl.common.railgun.RailgunData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

@SideOnly(Side.CLIENT)
public class RailgunCharge {

    private static final Int2ObjectOpenHashMap<RailgunChargeState> STATES = new Int2ObjectOpenHashMap<>();

    private RailgunCharge() {}

    public static boolean charging(EntityPlayer player) {
        RailgunChargeState state = refresh(player);
        return state != null && state.live();
    }

    public static int units(EntityPlayer player) {
        RailgunChargeState state = refresh(player);
        return state == null ? 0 : state.units(tick(player));
    }

    public static float units(EntityPlayer player, float partialTicks) {
        RailgunChargeState state = refresh(player);
        return state == null ? 0F : state.units(tick(player), partialTicks);
    }

    public static double age(EntityPlayer player, float partialTicks) {
        RailgunChargeState state = refresh(player);
        return state == null ? 0 : state.age(tick(player), partialTicks);
    }

    public static RailgunChargeTier tier(EntityPlayer player) {
        return RailgunChargeTier.fromUnits(units(player));
    }

    public static float progress(EntityPlayer player) {
        return Math.min(1F, units(player) / (float) RailgunData.FULL_CHARGE_UNITS);
    }

    public static void cap(int shooterId, int units) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        STATES.computeIfAbsent(shooterId, id -> new RailgunChargeState())
            .cap(units, world.getTotalWorldTime());
    }

    public static void finish(int shooterId) {
        RailgunChargeState state = STATES.get(shooterId);
        EntityPlayer self = Minecraft.getMinecraft().thePlayer;
        if (state == null || self == null) return;
        if (self.getEntityId() != shooterId) state.endUntilReleased(tick(self));
        else if (inUse(self) == null) state.end();
    }

    public static void clear() {
        STATES.clear();
    }

    private static RailgunChargeState refresh(EntityPlayer player) {
        if (player == null || player.worldObj == null) return null;
        boolean self = player == Minecraft.getMinecraft().thePlayer;
        ItemStack charging = self ? inUse(player) : heldWhileUsing(player);
        RailgunChargeState state = STATES.get(player.getEntityId());
        if (state == null) {
            if (charging == null) return null;
            state = new RailgunChargeState();
            STATES.put(player.getEntityId(), state);
        }
        state.update(
            tick(player),
            charging != null,
            self ? holdingKey(player) : holding(player),
            () -> RailgunData.chargeRate(charging));
        if (state.idle(tick(player))) STATES.remove(player.getEntityId());
        return state;
    }

    private static ItemStack inUse(EntityPlayer player) {
        if (player.isDead || !player.isUsingItem()) return null;
        return ElectromagneticRailgun.is(player.getItemInUse()) ? player.getItemInUse() : null;
    }

    private static ItemStack heldWhileUsing(EntityPlayer player) {
        return !player.isDead && player.isEating() && holding(player) ? player.getHeldItem() : null;
    }

    private static boolean holding(EntityPlayer player) {
        return !player.isDead && ElectromagneticRailgun.is(player.getHeldItem());
    }

    private static boolean holdingKey(EntityPlayer player) {
        return holding(player) && Minecraft.getMinecraft().gameSettings.keyBindUseItem.getIsKeyPressed();
    }

    private static long tick(EntityPlayer player) {
        return player == null || player.worldObj == null ? 0L : player.worldObj.getTotalWorldTime();
    }
}
