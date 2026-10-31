package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.gtnllib.network.ServerboundPacket;
import com.science.gtnl.common.celestweave.CelestweaveBackpack;
import com.science.gtnl.common.celestweave.CelestweaveDash;
import com.science.gtnl.common.celestweave.CelestweaveServerEvents;
import com.science.gtnl.common.equipment.EquipmentNet;

import io.netty.buffer.ByteBuf;

public class CelestweaveKeyPacket extends ServerboundPacket {

    public static final int DASH = 0, BACKPACK = 1, CONCEAL = 2;

    private byte action;

    public CelestweaveKeyPacket() {}

    public CelestweaveKeyPacket(int action) {
        this.action = (byte) action;
    }

    @Override
    protected void read(ByteBuf buffer) {
        action = buffer.readByte();
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeByte(action);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            if (player.isDead || player.playerNetServerHandler == null) return;
            switch (action) {
                case DASH -> CelestweaveDash.dash(player);
                case BACKPACK -> {
                    if (!EquipmentNet.guiThrottled(player)) CelestweaveBackpack.open(player);
                }
                case CONCEAL -> CelestweaveServerEvents.toggleConceal(player);
                default -> {}
            }
        });
    }
}
