package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.packet.base.ServerboundPacket;
import com.science.gtnl.utils.CreativeCursor;

import io.netty.buffer.ByteBuf;

public class CreativeScreenPacket extends ServerboundPacket {

    private boolean open;

    public CreativeScreenPacket() {}

    public CreativeScreenPacket(boolean open) {
        this.open = open;
    }

    @Override
    protected void read(ByteBuf buf) {
        open = buf.readBoolean();
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeBoolean(open);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> CreativeCursor.setCreativeScreen(player, open));
    }
}
