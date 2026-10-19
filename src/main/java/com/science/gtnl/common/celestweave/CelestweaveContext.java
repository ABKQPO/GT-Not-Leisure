package com.science.gtnl.common.celestweave;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public record CelestweaveContext(EntityPlayer player, int piece, ItemStack stack, double moved) {

    public boolean moving() {
        return moved > 0.01;
    }

    public boolean server() {
        return !player.worldObj.isRemote;
    }

    public boolean affords(double eu) {
        return CelestweaveEnergy.affords(player, piece, stack, eu);
    }

    public boolean drain(double eu) {
        return CelestweaveEnergy.drain(player, piece, stack, eu);
    }
}
