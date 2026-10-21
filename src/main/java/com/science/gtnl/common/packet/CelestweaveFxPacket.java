package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;

import com.gtnllib.utils.Vec3d;
import com.science.gtnl.client.celestweave.CelestweaveClientEvents;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class CelestweaveFxPacket extends ClientboundPacket {

    public enum Type {

        SHIELD_HIT,
        DASH,
        LAND,
        REFLECT,
        SAVE,
        LIGHTNING,
        SHIELD_AMBIENT;

        private static final Type[] VALUES = values();

        public static Type byOrdinal(int ordinal) {
            return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : null;
        }
    }

    private int entityId;
    private Type type;
    private Vec3d position;

    public CelestweaveFxPacket() {}

    public CelestweaveFxPacket(int entityId, Type type, double x, double y, double z) {
        this.entityId = entityId;
        this.type = type;
        this.position = new Vec3d(x, y, z);
    }

    @Override
    protected void read(ByteBuf buffer) {
        entityId = buffer.readInt();
        type = Type.byOrdinal(buffer.readByte());
        try {
            position = Vec3d.read(buffer);
        } catch (IllegalArgumentException invalid) {
            position = null;
        }
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeByte(type.ordinal());
        position.write(buffer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        if (type == null || position == null) return;
        runInWorld(
            minecraft,
            () -> CelestweaveClientEvents
                .onFx(entityId, type, (float) position.x(), (float) position.y(), (float) position.z()));
    }
}
