package com.science.gtnl.common.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IWorldAccess;
import net.minecraftforge.event.world.WorldEvent;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.science.gtnl.api.IBlockStateListener;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class WorldListener implements IWorldAccess {

    public static WorldListener instance;

    private final Long2ObjectOpenHashMap<IBlockStateListener> blockStateListeners = new Long2ObjectOpenHashMap<>();

    public WorldListener() {
        instance = this;
    }

    public void registerBlockStateListener(IBlockStateListener listener, Iterable<BlockPos> blocks) {
        for (BlockPos bp : blocks) {
            this.blockStateListeners.put(bp.asLong(), listener);
        }
    }

    public void unregisterBlockStateListener(IBlockStateListener listener) {
        this.blockStateListeners.values()
            .removeIf(v -> v == listener);
    }

    @Override
    public void markBlockForUpdate(int x, int y, int z) {
        IBlockStateListener listener = this.blockStateListeners.get(CoordinatePacker.pack(x, y, z));
        if (listener != null) {
            listener.onBlockChanged(new BlockPos(x, y, z));
        }
    }

    @Override
    public void markBlockForRenderUpdate(int x, int y, int z) {}

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {}

    @Override
    public void playSound(String name, double x, double y, double z, float volume, float pitch) {}

    @Override
    public void playSoundToNearExcept(EntityPlayer player, String name, double x, double y, double z, float volume,
        float pitch) {}

    @Override
    public void spawnParticle(String particle, double x, double y, double z, double mx, double my, double mz) {}

    @Override
    public void onEntityCreate(Entity entity) {}

    @Override
    public void onEntityDestroy(Entity entity) {}

    @Override
    public void playRecord(String name, int x, int y, int z) {}

    @Override
    public void broadcastSound(int soundID, int x, int y, int z, int data) {}

    @Override
    public void playAuxSFX(EntityPlayer player, int type, int x, int y, int z, int data) {}

    @Override
    public void destroyBlockPartially(int breakerId, int x, int y, int z, int progress) {}

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        event.world.addWorldAccess(this);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        this.blockStateListeners.clear();
        event.world.removeWorldAccess(this);
    }

    @Override
    public void onStaticEntitiesChanged() {}
}
