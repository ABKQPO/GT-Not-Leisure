package com.science.gtnl.common.world;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.science.gtnl.api.IBlockStateListener;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public class WorldListener implements IWorldAccess {

    public static WorldListener INSTANCE;

    private final Map<World, WorldListener> worlds = new IdentityHashMap<>();
    private final Long2ObjectOpenHashMap<Set<IBlockStateListener>> blockStateListeners = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectOpenHashMap<Set<IBlockStateListener>> chunkListeners = new Long2ObjectOpenHashMap<>();
    private final Map<IBlockStateListener, LongSet> listenerPositions = new IdentityHashMap<>();

    public WorldListener() {
        INSTANCE = this;
    }

    private WorldListener(World world) {
        world.addWorldAccess(this);
    }

    public void registerBlockStateListener(World world, IBlockStateListener listener, BlockPos origin,
        ForgeDirection direction, int distance) {
        if (world.isRemote) return;
        WorldListener access = worlds.computeIfAbsent(world, WorldListener::new);
        LongSet positions = new LongOpenHashSet();
        int x = origin.x;
        int y = origin.y;
        int z = origin.z;
        for (int i = 0; i < distance; i++) {
            x += direction.offsetX;
            y += direction.offsetY;
            z += direction.offsetZ;
            positions.add(CoordinatePacker.pack(x, y, z));
        }
        access.replacePositions(listener, positions);
    }

    /** Replaces one listener's arbitrary coordinate set in this world. Duplicate coordinates are harmless. */
    public void registerBlockStateListener(World world, IBlockStateListener listener, Iterable<BlockPos> coordinates) {
        if (world.isRemote) return;
        LongSet positions = new LongOpenHashSet();
        for (BlockPos pos : coordinates) positions.add(CoordinatePacker.pack(pos.x, pos.y, pos.z));
        worlds.computeIfAbsent(world, WorldListener::new)
            .replacePositions(listener, positions);
    }

    void replacePositions(IBlockStateListener listener, LongSet positions) {
        if (positions.equals(listenerPositions.get(listener))) return;
        removeListener(listener);
        if (positions.isEmpty()) return;
        positions = new LongOpenHashSet(positions);
        for (long position : positions) {
            blockStateListeners.computeIfAbsent(position, key -> new ReferenceOpenHashSet<>(2))
                .add(listener);
            long chunk = CoordinatePacker
                .pack(CoordinatePacker.unpackX(position) >> 4, 0, CoordinatePacker.unpackZ(position) >> 4);
            chunkListeners.computeIfAbsent(chunk, key -> new ReferenceOpenHashSet<>(2))
                .add(listener);
        }
        listenerPositions.put(listener, positions);
    }

    public void unregisterBlockStateListener(World world, IBlockStateListener listener) {
        WorldListener access = worlds.get(world);
        if (access != null) access.removeListener(listener);
    }

    private void removeListener(IBlockStateListener listener) {
        LongSet positions = listenerPositions.remove(listener);
        if (positions == null) return;
        LongSet removedChunks = new LongOpenHashSet();
        for (long position : positions) {
            Set<IBlockStateListener> listeners = blockStateListeners.get(position);
            listeners.remove(listener);
            if (listeners.isEmpty()) blockStateListeners.remove(position);
            long chunk = CoordinatePacker
                .pack(CoordinatePacker.unpackX(position) >> 4, 0, CoordinatePacker.unpackZ(position) >> 4);
            if (removedChunks.add(chunk)) {
                Set<IBlockStateListener> chunkSet = chunkListeners.get(chunk);
                chunkSet.remove(listener);
                if (chunkSet.isEmpty()) chunkListeners.remove(chunk);
            }
        }
    }

    @Override
    public void markBlockForUpdate(int x, int y, int z) {
        Set<IBlockStateListener> listeners = blockStateListeners.get(CoordinatePacker.pack(x, y, z));
        if (listeners == null) return;
        BlockPos pos = new BlockPos(x, y, z);
        // A callback may unregister itself or another listener.
        for (IBlockStateListener listener : listeners.toArray(new IBlockStateListener[listeners.size()]))
            listener.onBlockChanged(pos);
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
        if (!event.world.isRemote) worlds.computeIfAbsent(event.world, WorldListener::new);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        WorldListener access = worlds.remove(event.world);
        if (access != null) {
            event.world.removeWorldAccess(access);
            access.blockStateListeners.clear();
            access.chunkListeners.clear();
            access.listenerPositions.clear();
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        notifyChunk(event);
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        notifyChunk(event);
    }

    private void notifyChunk(ChunkEvent event) {
        WorldListener access = worlds.get(event.world);
        if (access == null) return;
        int chunkX = event.getChunk().xPosition;
        int chunkZ = event.getChunk().zPosition;
        Set<IBlockStateListener> affected = access.chunkListeners.get(CoordinatePacker.pack(chunkX, 0, chunkZ));
        if (affected == null) return;
        BlockPos pos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
        for (IBlockStateListener listener : affected.toArray(new IBlockStateListener[affected.size()]))
            listener.onBlockChanged(pos);
    }

    @Override
    public void onStaticEntitiesChanged() {}
}
