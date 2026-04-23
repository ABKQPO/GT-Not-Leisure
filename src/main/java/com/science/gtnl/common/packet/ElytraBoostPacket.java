package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.reavaritia.common.items.InfinityElytra;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ganymedes01.etfuturum.api.elytra.IElytraPlayer;
import ganymedes01.etfuturum.entities.EntityBoostingFireworkRocket;
import io.netty.buffer.ByteBuf;

public class ElytraBoostPacket implements IMessage, IMessageHandler<ElytraBoostPacket, IMessage> {

    private static final int COOLDOWN_TICKS = 20;

    public ElytraBoostPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(ElytraBoostPacket message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        if (player == null) return null;
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
        return null;
    }
}
