package com.science.gtnl.common.gui.modularui;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.science.gtnl.utils.world.stellar.StellarIrisUpgradeManager;

public class StellarIrisUpgradeActionSyncHandler extends SyncHandler<StellarIrisUpgradeActionSyncHandler> {

    private static final int UNLOCK_PACKET = 1;
    private static final int MAX_UPGRADE_ID_LENGTH = 128;

    private final EntityPlayer player;

    public StellarIrisUpgradeActionSyncHandler(EntityPlayer player) {
        this.player = player;
        allowC2S();
    }

    public void requestUnlock(String upgradeId) {
        if (upgradeId == null || upgradeId.isEmpty() || upgradeId.length() > MAX_UPGRADE_ID_LENGTH) {
            return;
        }
        syncToServer(UNLOCK_PACKET, buffer -> buffer.writeStringToBuffer(upgradeId));
    }

    @Override
    public void readOnClient(int packetId, PacketBuffer buffer) {}

    @Override
    public void readOnServer(int packetId, PacketBuffer buffer) throws IOException {
        if (packetId != UNLOCK_PACKET) {
            return;
        }
        String upgradeId = buffer.readStringFromBuffer(MAX_UPGRADE_ID_LENGTH);
        if (!upgradeId.isEmpty()) {
            StellarIrisUpgradeManager.tryUnlock(player, upgradeId);
        }
    }
}
