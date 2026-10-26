package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.gtnllib.network.ServerboundPacket;
import com.reavaritia.common.items.InfinityElytra;

import ganymedes01.etfuturum.api.elytra.IElytraPlayer;
import ganymedes01.etfuturum.entities.EntityBoostingFireworkRocket;

public class ElytraBoostPacket extends ServerboundPacket {

    private static final int COOLDOWN_TICKS = 20;

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            if (!InfinityElytra.isWearingInfinityElytra(player)) return;
            if (!(player instanceof IElytraPlayer)) return;
            if (!((IElytraPlayer) player).etfu$isElytraFlying()) return;

            NBTTagCompound data = player.getEntityData();
            int lastBoost = data.getInteger("InfinityElytraLastBoost");
            if (player.ticksExisted - lastBoost < COOLDOWN_TICKS) return;
            data.setInteger("InfinityElytraLastBoost", player.ticksExisted);

            ItemStack firework = new ItemStack(Items.fireworks);
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagCompound fireworksTag = new NBTTagCompound();
            fireworksTag.setByte("Flight", (byte) 1);
            fireworksTag.setTag("Explosions", new NBTTagList());
            tag.setTag("Fireworks", fireworksTag);
            firework.setTagCompound(tag);

            EntityBoostingFireworkRocket rocket = new EntityBoostingFireworkRocket(player.worldObj, firework, player);
            player.worldObj.spawnEntityInWorld(rocket);
        });
    }
}
