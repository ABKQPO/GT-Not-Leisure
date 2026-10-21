package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class EssentiaSyncThrottle {

    private static final int SYNC_INTERVAL = 10;

    private long lastSyncTick = -SYNC_INTERVAL;
    private boolean syncPending;

    public void request(TileEntity tile) {
        World world = tile.getWorldObj();
        if (world == null) return;

        if (world.isRemote || world.getTotalWorldTime() - lastSyncTick >= SYNC_INTERVAL) {
            sync(tile, world);
        } else {
            syncPending = true;
        }
    }

    public void flush(TileEntity tile) {
        World world = tile.getWorldObj();
        if (!syncPending || world == null || world.getTotalWorldTime() - lastSyncTick < SYNC_INTERVAL) return;

        sync(tile, world);
    }

    private void sync(TileEntity tile, World world) {
        lastSyncTick = world.getTotalWorldTime();
        syncPending = false;
        world.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
    }
}
