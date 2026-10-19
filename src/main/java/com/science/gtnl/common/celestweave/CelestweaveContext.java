package com.science.gtnl.common.celestweave;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public record CelestweaveContext(EntityPlayer player, int type, ItemStack stack, double moved) {

    public boolean moving() {
        return moved > 0.01;
    }

    public boolean server() {
        return !player.worldObj.isRemote;
    }

    public boolean affords(double eu) {
        return CelestweaveEnergy.affords(player, type, stack, eu);
    }

    public boolean drain(double eu) {
        return CelestweaveEnergy.drain(player, type, stack, eu);
    }

    public int cfgInt(CelestweaveModule module, String key, int fallback, int min, int max) {
        return CelestweaveData.cfgInt(stack, module, key, fallback, min, max);
    }

    public boolean cfgBool(CelestweaveModule module, String key, boolean fallback) {
        return CelestweaveData.cfgBool(stack, module, key, fallback);
    }
}
