package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionHitEffectState;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class DraconicArmorProjectionHitEffectPacket
    implements IMessage, IMessageHandler<DraconicArmorProjectionHitEffectPacket, IMessage> {

    private int playerId;
    private byte shieldPowerByte;

    public DraconicArmorProjectionHitEffectPacket() {}

    public DraconicArmorProjectionHitEffectPacket(EntityPlayer player, float shieldPower) {
        playerId = player.getEntityId();
        shieldPowerByte = (byte) (Math.max(0.0F, Math.min(1.0F, shieldPower)) * Byte.MAX_VALUE);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerId = buf.readInt();
        shieldPowerByte = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(playerId);
        buf.writeByte(shieldPowerByte);
    }

    public float getShieldPower() {
        return (float) shieldPowerByte / (float) Byte.MAX_VALUE;
    }

    @Override
    public IMessage onMessage(DraconicArmorProjectionHitEffectPacket message, MessageContext ctx) {
        if (ctx.side == Side.CLIENT) {
            onClient(message);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void onClient(DraconicArmorProjectionHitEffectPacket message) {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null) {
            return;
        }

        Entity entity = world.getEntityByID(message.playerId);
        if (!(entity instanceof EntityPlayer player)) {
            return;
        }

        DraconicArmorProjectionHitEffectState.trigger(player, message.getShieldPower());
    }
}
