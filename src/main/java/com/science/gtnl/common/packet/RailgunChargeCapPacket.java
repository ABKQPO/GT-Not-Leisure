package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;

import com.gtnllib.network.ClientboundPacket;
import com.science.gtnl.client.railgun.RailgunCharge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class RailgunChargeCapPacket extends ClientboundPacket {

    private int shooterId, units;

    public RailgunChargeCapPacket() {}

    public RailgunChargeCapPacket(int shooterId, int units) {
        this.shooterId = shooterId;
        this.units = units;
    }

    @Override
    protected void read(ByteBuf buffer) {
        shooterId = buffer.readInt();
        units = buffer.readInt();
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeInt(shooterId);
        buffer.writeInt(units);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        if (units >= 0) runInWorld(minecraft, () -> RailgunCharge.cap(shooterId, units));
    }
}
