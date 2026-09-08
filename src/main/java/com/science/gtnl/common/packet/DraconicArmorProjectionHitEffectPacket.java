package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.science.gtnl.common.item.items.bauble.DraconicArmorProjectionHitEffectState;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class DraconicArmorProjectionHitEffectPacket extends ClientboundPacket {

    private int playerId;
    private byte shieldPowerByte;

    public DraconicArmorProjectionHitEffectPacket() {}

    public DraconicArmorProjectionHitEffectPacket(EntityPlayer player, float shieldPower) {
        playerId = player.getEntityId();
        shieldPowerByte = (byte) (Math.clamp(shieldPower, 0.0F, 1.0F) * Byte.MAX_VALUE);
    }

    @Override
    protected void read(ByteBuf buf) {
        playerId = buf.readInt();
        shieldPowerByte = buf.readByte();
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeInt(playerId);
        buf.writeByte(shieldPowerByte);
    }

    public float getShieldPower() {
        return (float) shieldPowerByte / (float) Byte.MAX_VALUE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        World world = minecraft.theWorld;
        if (world == null) {
            return;
        }

        Entity entity = world.getEntityByID(playerId);
        if (!(entity instanceof EntityPlayer player)) {
            return;
        }

        DraconicArmorProjectionHitEffectState.trigger(player, getShieldPower());
    }
}
