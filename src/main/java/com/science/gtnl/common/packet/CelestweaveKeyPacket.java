package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.celestweave.CelestweaveBackpack;
import com.science.gtnl.common.celestweave.CelestweaveDash;
import com.science.gtnl.common.celestweave.CelestweaveData;
import com.science.gtnl.common.celestweave.CelestweaveEnergy;
import com.science.gtnl.common.celestweave.CelestweaveServerEvents;
import com.science.gtnl.common.packet.base.ServerboundPacket;

import io.netty.buffer.ByteBuf;

public class CelestweaveKeyPacket extends ServerboundPacket {

    public static final int DASH = 0, BACKPACK = 1, OPEN_PIECE = 2;

    private byte action;
    private byte piece;

    public CelestweaveKeyPacket() {}

    public CelestweaveKeyPacket(int action) {
        this(action, 0);
    }

    public CelestweaveKeyPacket(int action, int piece) {
        this.action = (byte) action;
        this.piece = (byte) piece;
    }

    @Override
    protected void read(ByteBuf buffer) {
        action = buffer.readByte();
        piece = buffer.readByte();
    }

    @Override
    protected void write(ByteBuf buffer) {
        buffer.writeByte(action);
        buffer.writeByte(piece);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            if (player.isDead || player.playerNetServerHandler == null) return;
            switch (action) {
                case DASH -> CelestweaveDash.dash(player);
                case BACKPACK -> {
                    if (!CelestweaveServerEvents.guiThrottled(player)) CelestweaveBackpack.open(player);
                }
                case OPEN_PIECE -> {
                    if (!CelestweaveServerEvents.guiThrottled(player)) openPiece(player);
                }
                default -> {}
            }
        });
    }

    private void openPiece(EntityPlayerMP player) {
        if (piece < 0 || piece > 3 || CelestweaveData.piece(player, piece) == null) return;
        CelestweaveEnergy.flush(player);
        if (player.openContainer != player.inventoryContainer) player.closeScreen();
        GuiFactories.playerInventory()
            .openFromPlayerInventory(player, CelestweaveData.armorSlot(player, piece));
    }
}
