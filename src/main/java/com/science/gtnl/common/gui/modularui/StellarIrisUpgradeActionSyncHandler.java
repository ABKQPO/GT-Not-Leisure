package com.science.gtnl.common.gui.modularui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.science.gtnl.api.stellar.StellarIrisUpgradeDefinition;
import com.science.gtnl.api.stellar.StellarIrisUpgradeRegistry;
import com.science.gtnl.utils.world.stellar.StellarIrisUpgradeManager;

public class StellarIrisUpgradeActionSyncHandler extends SyncHandler<StellarIrisUpgradeActionSyncHandler> {

    private static final int UNLOCK_PACKET = 0;

    private final EntityPlayer player;

    public StellarIrisUpgradeActionSyncHandler(EntityPlayer player) {
        this.player = player;
        allowC2S();
    }

    public void requestUnlock(int upgradeNetworkId) {
        if (upgradeNetworkId < 0) {
            return;
        }
        syncToServer(UNLOCK_PACKET, buffer -> buffer.writeVarIntToBuffer(upgradeNetworkId));
    }

    @Override
    public void readOnClient(int packetId, PacketBuffer buffer) {}

    @Override
    public void readOnServer(int packetId, PacketBuffer buffer) {
        if (packetId != UNLOCK_PACKET) {
            return;
        }
        StellarIrisUpgradeDefinition definition = StellarIrisUpgradeRegistry.getUpgrade(buffer.readVarIntFromBuffer());
        if (definition != null) {
            StellarIrisUpgradeManager.tryUnlock(player, definition);
        }
    }
}
