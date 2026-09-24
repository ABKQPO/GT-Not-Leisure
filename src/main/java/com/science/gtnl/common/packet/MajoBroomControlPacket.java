package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.entity.EntityMajoBroom;
import com.science.gtnl.common.packet.base.ServerboundPacket;

import io.netty.buffer.ByteBuf;

public class MajoBroomControlPacket extends ServerboundPacket {

    private int broomId;
    private byte control;

    public MajoBroomControlPacket() {}

    public MajoBroomControlPacket(int broomId, byte forwardInput, byte strafeInput, byte verticalInput) {
        this.broomId = broomId;
        control = (byte) (encode(forwardInput, 0) | encode(strafeInput, 2) | encode(verticalInput, 4));
    }

    @Override
    protected void read(ByteBuf buf) {
        broomId = buf.readInt();
        control = buf.readByte();
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeInt(broomId);
        buf.writeByte(control);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            if (player.ridingEntity instanceof EntityMajoBroom broom && broom.getEntityId() == broomId)
                broom.setRiderInput(decode(control, 0), decode(control, 2), decode(control, 4));
        });
    }

    private static byte encode(byte input, int shift) {
        int value = input > 0 ? 1 : input < 0 ? 2 : 0;
        return (byte) (value << shift);
    }

    private static byte decode(byte packed, int shift) {
        int value = (packed >> shift) & 3;
        return (byte) (value == 1 ? 1 : value == 2 ? -1 : 0);
    }
}
