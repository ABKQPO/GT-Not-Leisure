package com.science.gtnl.common.railgun;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public interface RailgunAmmo {

    boolean canFire(EntityPlayerMP player, ItemStack railgun, RailgunChargeTier tier);

    boolean consume(EntityPlayerMP player, ItemStack railgun, RailgunChargeTier tier);
}
