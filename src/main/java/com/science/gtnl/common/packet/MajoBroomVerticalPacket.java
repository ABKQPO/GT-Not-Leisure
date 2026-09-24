package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.entity.EntityMajoBroom;
import com.science.gtnl.common.packet.base.ServerboundPacket;

import io.netty.buffer.ByteBuf;

public class MajoBroomVerticalPacket extends ServerboundPacket {

    private int broomId;
    private byte verticalInput;

    public MajoBroomVerticalPacket() {}

    public MajoBroomVerticalPacket(int broomId, byte verticalInput) {
        this.broomId = broomId;
        this.verticalInput = verticalInput;
    }

    @Override
    protected void read(ByteBuf buf) {
        broomId = buf.readInt();
        verticalInput = buf.readByte();
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeInt(broomId);
        buf.writeByte(verticalInput);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            if (player.ridingEntity instanceof EntityMajoBroom broom && broom.getEntityId() == broomId)
                broom.setVerticalInput(verticalInput);
        });
    }
}
