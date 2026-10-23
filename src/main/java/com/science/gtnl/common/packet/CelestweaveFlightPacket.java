package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;

import com.science.gtnl.client.celestweave.CelestweaveClientEvents;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class CelestweaveFlightPacket extends ClientboundPacket {

    private int entityId;
    private boolean flying;

    public CelestweaveFlightPacket() {}

    public CelestweaveFlightPacket(int entityId, boolean flying) {
        this.entityId = entityId;
        this.flying = flying;
    }

    @Override
    protected void read(ByteBuf buffer) {
        entityId = buffer.readInt();
        flying = buffer.readBoolean();
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeBoolean(flying);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        runInWorld(minecraft, () -> CelestweaveClientEvents.setRemoteFlying(entityId, flying));
    }
}
