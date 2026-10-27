package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;

import com.science.gtnl.client.celestweave.CelestweaveClientEvents;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class CelestweaveConcealPacket extends ClientboundPacket {

    private int entityId;
    private boolean concealed;

    public CelestweaveConcealPacket() {}

    public CelestweaveConcealPacket(int entityId, boolean concealed) {
        this.entityId = entityId;
        this.concealed = concealed;
    }

    @Override
    protected void read(ByteBuf buffer) {
        entityId = buffer.readInt();
        concealed = buffer.readBoolean();
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeBoolean(concealed);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        // The login sync arrives before the client has a world, so runInWorld would drop it.
        minecraft.func_152344_a(() -> CelestweaveClientEvents.setConcealed(entityId, concealed));
    }
}
