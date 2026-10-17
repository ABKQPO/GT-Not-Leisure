package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.item.items.ElectromagneticRailgun;
import com.science.gtnl.common.railgun.RailgunChargeTier;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunDefaults;

public class RailgunCharge {

    private static boolean live;
    private static long startedTick;
    private static long lastLiveTick;
    private static double rate = 1;
    private static int cap = Integer.MAX_VALUE;

    private RailgunCharge() {}

    public static boolean charging(EntityPlayer player) {
        refresh(player);
        return live;
    }

    public static int units(EntityPlayer player) {
        refresh(player);
        return live ? Math.min(cap, RailgunData.units(tick(player) - startedTick, rate)) : 0;
    }

    public static float units(EntityPlayer player, float partialTicks) {
        refresh(player);
        return live ? Math.min(cap, (float) (Math.max(0F, tick(player) - startedTick + partialTicks) * rate)) : 0F;
    }

    public static RailgunChargeTier tier(EntityPlayer player) {
        return RailgunChargeTier.fromUnits(units(player));
    }

    public static float progress(EntityPlayer player) {
        return Math.min(1F, units(player) / (float) RailgunData.FULL_CHARGE_UNITS);
    }

    public static void cap(int units) {
        cap = Math.max(0, units);
    }

    public static void finish(int shooterId) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player != null && player.getEntityId() == shooterId) live = false;
    }

    public static void reset() {
        live = false;
        startedTick = 0L;
        lastLiveTick = 0L;
        rate = 1;
        cap = Integer.MAX_VALUE;
    }

    private static void refresh(EntityPlayer player) {
        long now = tick(player);
        if (using(player)) {
            if (!live || now - lastLiveTick > RailgunDefaults.CHARGE_RESUME_GRACE_TICKS) {
                startedTick = now;
                rate = Math.max(1, RailgunData.chargeRate(inUse(player)));
                cap = Integer.MAX_VALUE;
            }
            live = true;
            lastLiveTick = now;
            return;
        }
        if (live && (!held(player) || now - lastLiveTick > RailgunDefaults.CHARGE_RESUME_GRACE_TICKS)) live = false;
    }

    private static boolean using(EntityPlayer player) {
        return player != null && !player.isDead && player.isUsingItem() && inUse(player) != null;
    }

    private static ItemStack inUse(EntityPlayer player) {
        return railgun(player.getItemInUse()) ? player.getItemInUse() : null;
    }

    private static boolean held(EntityPlayer player) {
        if (player == null || player.isDead) return false;
        if (!railgun(player.getHeldItem())) return false;
        return Minecraft.getMinecraft().gameSettings.keyBindUseItem.getIsKeyPressed();
    }

    private static boolean railgun(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ElectromagneticRailgun;
    }

    private static long tick(EntityPlayer player) {
        return player == null || player.worldObj == null ? 0L : player.worldObj.getTotalWorldTime();
    }
}
