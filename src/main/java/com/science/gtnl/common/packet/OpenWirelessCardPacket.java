package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.packet.base.ServerboundPacket;
import com.science.gtnl.common.wireless.WirelessAutoConnect;

/** The server selects and validates the carried card; the client supplies no inventory address. */
public final class OpenWirelessCardPacket extends ServerboundPacket {

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> WirelessAutoConnect.openGui(player));
    }
}
