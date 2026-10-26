package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnllib.network.ServerboundPacket;
import com.science.gtnl.common.gui.container.ContainerUniversalStorageBus;

import io.netty.buffer.ByteBuf;

public class UniversalStorageBusActionPacket extends ServerboundPacket {

    public enum Action {
        PARTITION,
        PARTITION_CLEAR,
        CLEAR
    }

    private Action action;

    public UniversalStorageBusActionPacket() {}

    public UniversalStorageBusActionPacket(Action action) {
        this.action = action;
    }

    @Override
    public void read(ByteBuf buf) {
        final int ordinal = buf.readUnsignedByte();
        final Action[] values = Action.values();
        if (ordinal >= values.length) {
            throw new IllegalArgumentException("Unknown universal storage bus action");
        }
        this.action = values[ordinal];
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.action.ordinal());
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        if (!(player.openContainer instanceof ContainerUniversalStorageBus container)) return;
        switch (this.action) {
            case PARTITION -> container.partition(false);
            case PARTITION_CLEAR -> container.partition(true);
            case CLEAR -> container.clear();
        }
    }
}
