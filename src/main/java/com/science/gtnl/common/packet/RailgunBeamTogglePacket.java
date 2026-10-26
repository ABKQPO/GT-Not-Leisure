package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.gtnllib.network.ServerboundPacket;
import com.science.gtnl.common.railgun.RailgunBeamService;

import io.netty.buffer.ByteBuf;

public class RailgunBeamTogglePacket extends ServerboundPacket {

    private boolean firing;

    public RailgunBeamTogglePacket() {}

    public RailgunBeamTogglePacket(boolean firing) {
        this.firing = firing;
    }

    @Override
    protected void read(ByteBuf buffer) {
        firing = buffer.readBoolean();
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeBoolean(firing);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> RailgunBeamService.setFiring(player, firing));
    }
}
