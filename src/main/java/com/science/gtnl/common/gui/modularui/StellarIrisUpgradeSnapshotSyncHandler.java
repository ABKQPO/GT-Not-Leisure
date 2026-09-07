package com.science.gtnl.common.gui.modularui;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.science.gtnl.api.stellar.StellarIrisUpgradeDefinition;
import com.science.gtnl.api.stellar.StellarIrisUpgradeRegistry;
import com.science.gtnl.utils.world.stellar.StellarIrisTeamSnapshot;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class StellarIrisUpgradeSnapshotSyncHandler extends SyncHandler<StellarIrisUpgradeSnapshotSyncHandler> {

    private static final int SNAPSHOT_PACKET = 0;

    private final Supplier<StellarIrisTeamSnapshot> snapshotSupplier;
    private final Consumer<StellarIrisTeamSnapshot> snapshotConsumer;
    private StellarIrisTeamSnapshot cachedSnapshot;

    public StellarIrisUpgradeSnapshotSyncHandler(Supplier<StellarIrisTeamSnapshot> snapshotSupplier,
        Consumer<StellarIrisTeamSnapshot> snapshotConsumer) {
        this.snapshotSupplier = snapshotSupplier;
        this.snapshotConsumer = snapshotConsumer;
    }

    @Override
    public void readOnClient(int packetId, PacketBuffer buffer) throws IOException {
        if (packetId == SNAPSHOT_PACKET) {
            snapshotConsumer.accept(readSnapshot(buffer));
        }
    }

    @Override
    public void readOnServer(int packetId, PacketBuffer buffer) {}

    @Override
    public void detectAndSendChanges(boolean init) {
        StellarIrisTeamSnapshot snapshot = snapshotSupplier.get();
        if (init || cachedSnapshot == null || !snapshot.sameAs(cachedSnapshot)) {
            cachedSnapshot = snapshot;
            syncToClient(SNAPSHOT_PACKET, buffer -> writeSnapshot(buffer, snapshot));
        }
    }

    private static void writeSnapshot(PacketBuffer buffer, StellarIrisTeamSnapshot snapshot) {
        buffer.writeVarIntToBuffer(snapshot.getSpendablePoints());
        buffer.writeVarIntToBuffer(snapshot.getLifetimePoints());
        buffer.writeVarIntToBuffer(snapshot.getTier());
        buffer.writeVarIntToBuffer(
            snapshot.getNetworkLevels()
                .size());
        for (Int2IntMap.Entry entry : snapshot.getNetworkLevels()
            .int2IntEntrySet()) {
            buffer.writeVarIntToBuffer(entry.getIntKey());
            buffer.writeVarIntToBuffer(entry.getIntValue());
        }
    }

    private static StellarIrisTeamSnapshot readSnapshot(PacketBuffer buffer) throws IOException {
        int spendablePoints = buffer.readVarIntFromBuffer();
        int lifetimePoints = buffer.readVarIntFromBuffer();
        int tier = buffer.readVarIntFromBuffer();
        int levelCount = buffer.readVarIntFromBuffer();
        int maxLevelCount = StellarIrisUpgradeRegistry.getUpgradeCount();
        if (spendablePoints < 0 || lifetimePoints < 0 || tier < 0 || levelCount < 0 || levelCount > maxLevelCount) {
            throw new IOException("Invalid Stellar Iris snapshot");
        }
        Int2IntMap levels = new Int2IntOpenHashMap(levelCount);
        for (int index = 0; index < levelCount; index++) {
            int networkId = buffer.readVarIntFromBuffer();
            StellarIrisUpgradeDefinition definition = StellarIrisUpgradeRegistry.getUpgrade(networkId);
            int level = buffer.readVarIntFromBuffer();
            if (definition == null || level <= 0 || level > definition.getMaxLevel()) {
                throw new IOException("Invalid Stellar Iris upgrade level");
            }
            levels.put(networkId, level);
        }
        return StellarIrisTeamSnapshot.fromNetworkLevels(spendablePoints, lifetimePoints, tier, levels);
    }
}
