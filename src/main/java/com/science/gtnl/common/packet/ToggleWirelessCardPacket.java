package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.packet.base.ServerboundPacket;
import com.science.gtnl.common.wireless.WirelessAutoConnect;

import io.netty.buffer.ByteBuf;

/** Contains an input request only: inventory, owner and feature checks happen on the server thread. */
public final class ToggleWirelessCardPacket extends ServerboundPacket {

    private boolean heldOnly;
    private int slot;

    public ToggleWirelessCardPacket() {}

    public ToggleWirelessCardPacket(boolean heldOnly, int slot) {
        this.heldOnly = heldOnly;
        this.slot = slot;
    }

    @Override
    protected void read(ByteBuf buf) {
        heldOnly = buf.readBoolean();
        slot = buf.readByte();
        if (heldOnly && (slot < 0 || slot > 8)) throw new IllegalArgumentException("Invalid hotbar slot");
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeBoolean(heldOnly);
        buf.writeByte(slot);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> WirelessAutoConnect.toggle(player, heldOnly, slot));
    }
}
