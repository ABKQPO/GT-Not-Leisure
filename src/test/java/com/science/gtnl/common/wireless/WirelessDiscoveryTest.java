package com.science.gtnl.common.wireless;

import java.util.List;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.chunk.Chunk;

/** Runs inside the real Mixin harness; reproduces a saved non-ticking AE-style host absent from the world tick list. */
public final class WirelessDiscoveryTest {

    public static final class NonTickingTile extends TileEntity {

        @Override
        public boolean canUpdate() {
            return false;
        }
    }

    public static void run() throws Exception {
        TileEntity.addMapping(NonTickingTile.class, "GTNLWirelessNonTickingTest");
        NonTickingTile original = new NonTickingTile();
        NBTTagCompound source = new NBTTagCompound();
        source.setInteger("dimension", 0);
        source.setInteger("x", 100);
        source.setInteger("y", 64);
        source.setInteger("z", 100);
        source.setInteger("side", 6);
        source.setBoolean("paused", true);
        source.setBoolean("entrance", true);
        NBTTagList frequencies = new NBTTagList();
        frequencies.appendTag(source);
        NBTTagCompound node = new NBTTagCompound();
        node.setInteger("side", 6);
        node.setBoolean("external", false);
        node.setString("kind", "test-host");
        node.setTag("frequencies", frequencies);
        NBTTagList nodes = new NBTTagList();
        nodes.appendTag(node);
        NBTTagCompound payload = new NBTTagCompound();
        payload.setInteger("version", 1);
        payload.setTag("nodes", nodes);
        WirelessTileData.of(original)
            .setTag(WirelessTileData.KEY, payload);
        NBTTagCompound saved = new NBTTagCompound();
        original.writeToNBT(saved);
        NonTickingTile restored = new NonTickingTile();
        restored.readFromNBT(saved);
        NonTickingTile unrelated = new NonTickingTile();
        Chunk chunk = new Chunk(null, 0, 0);
        chunk.chunkTileEntityMap.put(new ChunkPosition(0, 64, 0), restored);
        chunk.chunkTileEntityMap.put(new ChunkPosition(1, 64, 0), unrelated);
        if (restored.canUpdate()) throw new AssertionError("Regression target must be absent from the world tick list");
        var field = WirelessLinkPersistence.class.getDeclaredField("TILES");
        field.setAccessible(true);
        Set<?> discovered = (Set<?>) field.get(null);
        WirelessLinkPersistence.clear();
        WirelessLinkPersistence.discover(chunk);
        check(discovered.contains(restored), "Chunk-load discovery must include a non-ticking saved host");
        // Reproduce losing the startup discovery queue, then bootstrap from chunks already in memory.
        WirelessLinkPersistence.clear();
        check(discovered.isEmpty(), "Runtime reset clears old object identities");
        WirelessLinkPersistence.discoverLoaded(List.of(chunk));
        check(
            discovered.contains(restored),
            "Initial loaded-chunk scan must recover non-ticking hosts without player clicks");
        check(!discovered.contains(unrelated), "Unbound tiles are not scheduled for recovery");
        WirelessLinkPersistence.discoverLoaded(List.of(chunk));
        check(discovered.size() == 1, "Event discovery and bootstrap must not duplicate a host");
        WirelessLinkPersistence.clear();
        WirelessLinkPersistence.discoverLoaded(List.of());
        check(discovered.isEmpty(), "A new world must not inherit the previous world's discovery queue");
        check(
            WirelessTileData.of(restored)
                .getCompoundTag(WirelessTileData.KEY)
                .equals(payload),
            "Runtime reset preserves saved provenance");
        System.out.println("WirelessDiscoveryTest: non-ticking tile discovery and startup rescan passed.");
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
