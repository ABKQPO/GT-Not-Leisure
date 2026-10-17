package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;

import com.science.gtnl.client.railgun.RailgunCharge;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class RailgunChargeCapPacket extends ClientboundPacket {

    private int units;

    public RailgunChargeCapPacket() {}

    public RailgunChargeCapPacket(int units) {
        this.units = units;
    }

    @Override
    protected void read(ByteBuf buffer) {
        units = buffer.readInt();
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeInt(units);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        if (units < 0) return;
        var world = minecraft.theWorld;
        minecraft.func_152344_a(() -> { if (world != null && minecraft.theWorld == world) RailgunCharge.cap(units); });
    }
}
