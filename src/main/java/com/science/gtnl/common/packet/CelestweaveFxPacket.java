package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;

import com.science.gtnl.client.celestweave.CelestweaveClientEvents;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class CelestweaveFxPacket extends ClientboundPacket {

    public static final int SHIELD_HIT = 0, DASH = 1, LAND = 2, REFLECT = 3, SAVE = 4, LIGHTNING = 5,
        SHIELD_AMBIENT = 6;

    private int entityId;
    private byte type;
    private float x, y, z;

    public CelestweaveFxPacket() {}

    public CelestweaveFxPacket(int entityId, int type, double x, double y, double z) {
        this.entityId = entityId;
        this.type = (byte) type;
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    @Override
    protected void read(ByteBuf buffer) {
        entityId = buffer.readInt();
        type = buffer.readByte();
        x = buffer.readFloat();
        y = buffer.readFloat();
        z = buffer.readFloat();
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeByte(type);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
        buffer.writeFloat(z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        if (type < SHIELD_HIT || type > SHIELD_AMBIENT
            || !Float.isFinite(x)
            || !Float.isFinite(y)
            || !Float.isFinite(z)) return;
        var world = minecraft.theWorld;
        minecraft.func_152344_a(
            () -> {
                if (world != null && minecraft.theWorld == world) CelestweaveClientEvents.onFx(entityId, type, x, y, z);
            });
    }
}
