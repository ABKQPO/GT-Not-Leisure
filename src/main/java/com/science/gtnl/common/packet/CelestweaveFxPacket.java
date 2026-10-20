package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;

import com.science.gtnl.client.celestweave.CelestweaveClientEvents;
import com.science.gtnl.common.equipment.EquipmentVec;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class CelestweaveFxPacket extends ClientboundPacket {

    public static final int SHIELD_HIT = 0, DASH = 1, LAND = 2, REFLECT = 3, SAVE = 4, LIGHTNING = 5,
        SHIELD_AMBIENT = 6;

    private int entityId;
    private byte type;
    private EquipmentVec position;

    public CelestweaveFxPacket() {}

    public CelestweaveFxPacket(int entityId, int type, double x, double y, double z) {
        this.entityId = entityId;
        this.type = (byte) type;
        this.position = new EquipmentVec(x, y, z);
    }

    @Override
    protected void read(ByteBuf buffer) {
        entityId = buffer.readInt();
        type = buffer.readByte();
        try {
            position = EquipmentVec.read(buffer);
        } catch (IllegalArgumentException invalid) {
            position = null;
        }
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeByte(type);
        position.write(buffer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        if (type < SHIELD_HIT || type > SHIELD_AMBIENT || position == null) return;
        runInWorld(
            minecraft,
            () -> CelestweaveClientEvents
                .onFx(entityId, type, (float) position.x(), (float) position.y(), (float) position.z()));
    }
}
