package com.science.gtnl.common.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.science.gtnl.CommonProxy;
import com.science.gtnl.common.packet.base.ClientboundPacket;
import com.science.gtnl.utils.detrav.DetravMapTexture;
import com.science.gtnl.utils.detrav.DetravScannerGUI;
import com.science.gtnl.utils.enums.GuiType;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import detrav.utils.FluidColors;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ShortMap;
import it.unimi.dsi.fastutil.longs.Long2ShortOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;

public class ProspectingPacket extends ClientboundPacket {

    public static final int MODE_BIG_ORES = 0;
    public static final int MODE_ALL_ORES = 1;
    public static final int MODE_FLUIDS = 2;
    public static final int MODE_POLLUTION = 3;
    private static final int DEFAULT_COLOR = 0xFF7D7D7D;
    public static final int MAX_COMPRESSED_PAYLOAD_SIZE = 2_000_000;

    public int chunkX;
    public int chunkZ;
    public int posX;
    public int posZ;
    public int size;
    public int ptype;
    public final Long2ShortOpenHashMap map = new Long2ShortOpenHashMap();
    public final Short2ObjectOpenHashMap<ObjectIntPair<String>> objects = new Short2ObjectOpenHashMap<>();
    public final Short2ObjectOpenHashMap<String> oreMaterialNames = new Short2ObjectOpenHashMap<>();
    private final Object2ShortOpenHashMap<String> nameLookup = new Object2ShortOpenHashMap<>();
    private short nextId;

    public ProspectingPacket() {}

    public ProspectingPacket(int chunkX, int chunkZ, int posX, int posZ, int size, int ptype) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.posX = posX;
        this.posZ = posZ;
        this.size = size;
        this.ptype = ptype;
    }

    @Override
    protected void read(ByteBuf buf) {
        int compressedLength = buf.readInt();
        byte[] compressed = new byte[compressedLength];
        buf.readBytes(compressed);

        ByteBuf rawBuffer = Unpooled.wrappedBuffer(decompress(compressed));
        try {
            readRaw(rawBuffer);
        } finally {
            rawBuffer.release();
        }
    }

    @Override
    protected void write(ByteBuf buf) {
        byte[] compressed = compress();
        buf.writeInt(compressed.length);
        buf.writeBytes(compressed);
    }

    public int getPayloadSize() {
        return Integer.BYTES + compress().length;
    }

    public boolean trimToPayloadLimit(int maxPayloadSize) {
        if (getPayloadSize() <= maxPayloadSize) {
            return true;
        }
        if (ptype == MODE_BIG_ORES || ptype == MODE_ALL_ORES) {
            collapseToTopVisibleLayer();
            pruneUnusedObjects();
            if (getPayloadSize() <= maxPayloadSize) {
                return true;
            }
            for (int sampleStep = 2; sampleStep <= 64; sampleStep <<= 1) {
                downsampleColumns(sampleStep);
                pruneUnusedObjects();
                if (getPayloadSize() <= maxPayloadSize) {
                    return true;
                }
            }
        }
        return getPayloadSize() <= maxPayloadSize;
    }

    private void readRaw(ByteBuf buf) {
        chunkX = buf.readInt();
        chunkZ = buf.readInt();
        posX = buf.readInt();
        posZ = buf.readInt();
        size = buf.readInt();
        ptype = buf.readInt();

        objects.clear();
        oreMaterialNames.clear();
        nameLookup.clear();
        nextId = 0;
        int objectCount = buf.readInt();
        objects.ensureCapacity(objectCount);
        for (int i = 0; i < objectCount; i++) {
            short objectId = buf.readShort();
            String name = ByteBufUtils.readUTF8String(buf);
            int color = buf.readInt();
            String oreMaterialName = ByteBufUtils.readUTF8String(buf);
            objects.put(objectId, ObjectIntImmutablePair.of(name, color));
            oreMaterialNames.put(objectId, oreMaterialName);
            nameLookup.put(name, objectId);
            nextId = (short) Math.max(nextId, objectId + 1);
        }

        map.clear();
        int mapCount = buf.readInt();
        map.ensureCapacity(mapCount);
        for (int i = 0; i < mapCount; i++) {
            map.put(buf.readLong(), buf.readShort());
        }
    }

    private byte[] compress() {
        ByteBuf rawBuffer = Unpooled.buffer();
        try {
            writeRaw(rawBuffer);
            byte[] raw = new byte[rawBuffer.readableBytes()];
            rawBuffer.readBytes(raw);
            ByteArrayOutputStream output = new ByteArrayOutputStream(raw.length);
            try (GZIPOutputStream gzip = new GZIPOutputStream(output)) {
                gzip.write(raw);
            }
            return output.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to compress prospecting packet", e);
        } finally {
            rawBuffer.release();
        }
    }

    private static byte[] decompress(byte[] compressed) {
        try (ByteArrayInputStream input = new ByteArrayInputStream(compressed);
            GZIPInputStream gzip = new GZIPInputStream(input);
            ByteArrayOutputStream output = new ByteArrayOutputStream(compressed.length * 2)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = gzip.read(buffer)) >= 0) {
                output.write(buffer, 0, read);
            }
            return output.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to decompress prospecting packet", e);
        }
    }

    private void writeRaw(ByteBuf buf) {
        buf.writeInt(chunkX);
        buf.writeInt(chunkZ);
        buf.writeInt(posX);
        buf.writeInt(posZ);
        buf.writeInt(size);
        buf.writeInt(ptype);

        buf.writeInt(objects.size());
        for (Short2ObjectMap.Entry<ObjectIntPair<String>> entry : objects.short2ObjectEntrySet()) {
            short objectId = entry.getShortKey();
            buf.writeShort(objectId);
            ByteBufUtils.writeUTF8String(
                buf,
                entry.getValue()
                    .left());
            buf.writeInt(
                entry.getValue()
                    .rightInt());
            ByteBufUtils.writeUTF8String(buf, oreMaterialNames.getOrDefault(objectId, ""));
        }

        buf.writeInt(map.size());
        for (Long2ShortMap.Entry entry : map.long2ShortEntrySet()) {
            buf.writeLong(entry.getLongKey());
            buf.writeShort(entry.getShortValue());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        DetravScannerGUI.newMap(new DetravMapTexture(this));
        openProspectorGUI();
    }

    @SideOnly(Side.CLIENT)
    public void openProspectorGUI() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        CommonProxy.openGui(
            player,
            GuiType.DetravScannerGUI,
            null,
            player.worldObj,
            (int) player.posX,
            (int) player.posY,
            (int) player.posZ);
    }

    public void addBlock(int worldX, int worldY, int worldZ, Block block, int meta) {
        int relativeX = worldX - (chunkX - size) * 16;
        int relativeZ = worldZ - (chunkZ - size) * 16;
        ItemStack stack = new ItemStack(block, 1, meta);
        String name = stack.getDisplayName();

        short objectId;
        if (nameLookup.containsKey(name)) {
            objectId = nameLookup.getShort(name);
        } else {
            objectId = nextId++;
            nameLookup.put(name, objectId);
            objects.put(objectId, ObjectIntImmutablePair.of(name, DEFAULT_COLOR));
            oreMaterialNames.put(objectId, "");
        }

        map.put(CoordinatePacker.pack(relativeX, worldY, relativeZ), objectId);
    }

    public void addFluid(int chunkX, int chunkZ, @Nullable FluidStack fluid) {
        if (fluid == null || fluid.getFluid() == null) {
            return;
        }
        int relativeChunkX = chunkX - (this.chunkX - size);
        int relativeChunkZ = chunkZ - (this.chunkZ - size);
        String name = fluid.getLocalizedName();
        short objectId = getOrCreateFluidObject(name, FluidColors.getColor(fluid.getFluidID()));

        int lower = fluid.amount & 0xFFFF;
        int upper = fluid.amount >>> 16;
        map.put(CoordinatePacker.pack(relativeChunkX, 0, relativeChunkZ), objectId);
        map.put(CoordinatePacker.pack(relativeChunkX, 1, relativeChunkZ), (short) lower);
        map.put(CoordinatePacker.pack(relativeChunkX, 2, relativeChunkZ), (short) upper);
    }

    public void addPollution(int chunkX, int chunkZ, int amount) {
        int relativeChunkX = chunkX - (this.chunkX - size);
        int relativeChunkZ = chunkZ - (this.chunkZ - size);

        int lower = amount & 0xFFFF;
        int upper = amount >>> 16;
        map.put(CoordinatePacker.pack(relativeChunkX, 1, relativeChunkZ), (short) lower);
        map.put(CoordinatePacker.pack(relativeChunkX, 2, relativeChunkZ), (short) upper);
    }

    public int getAmount(int relativeChunkX, int relativeChunkZ) {
        int lower = Short.toUnsignedInt(map.get(CoordinatePacker.pack(relativeChunkX, 1, relativeChunkZ)));
        int upper = Short.toUnsignedInt(map.get(CoordinatePacker.pack(relativeChunkX, 2, relativeChunkZ)));
        return (upper << 16) | lower;
    }

    public int getSize() {
        return (size * 2 + 1) * 16;
    }

    public String getOreMaterialName(short objectId) {
        return oreMaterialNames.getOrDefault(objectId, "");
    }

    public int getObjectColor(short objectId) {
        ObjectIntPair<String> object = objects.get(objectId);
        return object == null ? DEFAULT_COLOR : object.rightInt();
    }

    private short getOrCreateFluidObject(String name, short[] fluidColor) {
        if (nameLookup.containsKey(name)) {
            return nameLookup.getShort(name);
        }
        short objectId = nextId++;
        nameLookup.put(name, objectId);
        objects.put(objectId, ObjectIntImmutablePair.of(name, rgbaToColor(fluidColor)));
        oreMaterialNames.put(objectId, "");
        return objectId;
    }

    private void collapseToTopVisibleLayer() {
        Long2ShortOpenHashMap collapsedMap = new Long2ShortOpenHashMap();
        Long2LongOpenHashMap topByColumn = new Long2LongOpenHashMap();

        for (Long2ShortMap.Entry entry : map.long2ShortEntrySet()) {
            long coordinate = entry.getLongKey();
            long columnKey = getColumnKey(CoordinatePacker.unpackX(coordinate), CoordinatePacker.unpackZ(coordinate));
            if (topByColumn.containsKey(columnKey)) {
                long previousCoordinate = topByColumn.get(columnKey);
                if (CoordinatePacker.unpackY(previousCoordinate) >= CoordinatePacker.unpackY(coordinate)) {
                    continue;
                }
            }
            topByColumn.put(columnKey, coordinate);
        }

        collapsedMap.ensureCapacity(topByColumn.size());
        for (Long2LongMap.Entry entry : topByColumn.long2LongEntrySet()) {
            long coordinate = entry.getLongKey();
            collapsedMap.put(coordinate, map.get(coordinate));
        }

        map.clear();
        map.putAll(collapsedMap);
    }

    private void pruneUnusedObjects() {
        Short2ObjectOpenHashMap<ObjectIntPair<String>> usedObjects = new Short2ObjectOpenHashMap<>();
        Short2ObjectOpenHashMap<String> usedMaterialNames = new Short2ObjectOpenHashMap<>();
        Object2ShortOpenHashMap<String> usedLookup = new Object2ShortOpenHashMap<>();
        short maxObjectId = -1;

        for (Long2ShortMap.Entry entry : map.long2ShortEntrySet()) {
            short objectId = entry.getShortValue();
            ObjectIntPair<String> object = objects.get(objectId);
            if (object == null) {
                continue;
            }
            usedObjects.put(objectId, object);
            usedMaterialNames.put(objectId, oreMaterialNames.getOrDefault(objectId, ""));
            usedLookup.put(object.left(), objectId);
            maxObjectId = (short) Math.max(maxObjectId, objectId);
        }

        objects.clear();
        objects.putAll(usedObjects);
        oreMaterialNames.clear();
        oreMaterialNames.putAll(usedMaterialNames);
        nameLookup.clear();
        nameLookup.putAll(usedLookup);
        nextId = (short) (maxObjectId + 1);
    }

    private void downsampleColumns(int sampleStep) {
        Long2ShortOpenHashMap sampledMap = new Long2ShortOpenHashMap();
        sampledMap.ensureCapacity(Math.max(1, map.size() / (sampleStep * sampleStep)));

        for (Long2ShortMap.Entry entry : map.long2ShortEntrySet()) {
            long coordinate = entry.getLongKey();
            if (CoordinatePacker.unpackX(coordinate) % sampleStep != 0
                || CoordinatePacker.unpackZ(coordinate) % sampleStep != 0) {
                continue;
            }
            sampledMap.put(coordinate, entry.getShortValue());
        }

        map.clear();
        map.putAll(sampledMap);
    }

    private long getColumnKey(int relativeX, int relativeZ) {
        return ((long) relativeX & 0xFFFFFFFFL) << 32 | ((long) relativeZ & 0xFFFFFFFFL);
    }

    private static int rgbaToColor(short[] rgba) {
        if (rgba == null || rgba.length < 3) {
            return DEFAULT_COLOR;
        }
        return (0xFF << 24) | ((rgba[0] & 0xFF) << 16) | ((rgba[1] & 0xFF) << 8) | (rgba[2] & 0xFF);
    }
}
