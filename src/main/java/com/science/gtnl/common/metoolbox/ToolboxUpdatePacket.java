package com.science.gtnl.common.metoolbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.science.gtnl.client.metoolbox.ToolboxClient;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class ToolboxUpdatePacket extends ClientboundPacket {

    private static final int MAX_ENTRIES = 64;
    private static final int MAX_COMPRESSED_BYTES = 24_000;

    private static final long MAX_TRACKED_BYTES = 2L * 1024 * 1024 - 8 * 1024;

    private NBTTagCompound data;

    public ToolboxUpdatePacket() {}

    public ToolboxUpdatePacket(NBTTagCompound data) {
        this.data = data;
    }

    @Override
    protected void read(ByteBuf buf) {
        data = ByteBufUtils.readTag(buf);
    }

    @Override
    protected void write(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, data);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        minecraft.func_152344_a(() -> ToolboxClient.receive(data));
    }

    static List<NBTTagList> split(List<NBTTagCompound> entries) throws IOException {
        List<NBTTagList> parts = new ArrayList<>();
        NBTTagList part = new NBTTagList();
        int compressedBytes = 0;
        long trackedBytes = 0;
        for (NBTTagCompound entry : entries) {
            if (entry == null) throw new IOException("Null ME toolbox entry");
            NBTTagList singleton = new NBTTagList();
            singleton.appendTag(entry);
            EncodedSize size = measure(singleton);
            if (part.tagCount() >= MAX_ENTRIES || compressedBytes + size.compressedBytes() > MAX_COMPRESSED_BYTES
                || trackedBytes + size.trackedBytes() > MAX_TRACKED_BYTES) {
                addPart(parts, part);
                part = new NBTTagList();
                compressedBytes = 0;
                trackedBytes = 0;
            }
            part.appendTag(entry);
            compressedBytes += size.compressedBytes();

            trackedBytes += size.trackedBytes();
        }
        addPart(parts, part);
        return parts;
    }

    private static void addPart(List<NBTTagList> parts, NBTTagList part) throws IOException {

        measure(part);
        parts.add(part);
    }

    private static EncodedSize measure(NBTTagList entries) throws IOException {
        NBTTagCompound envelope = new NBTTagCompound();

        envelope.setTag("entries", entries);
        byte[] compressed = CompressedStreamTools.compress(envelope);
        if (compressed.length > MAX_COMPRESSED_BYTES) throw new IOException("Oversize ME toolbox compressed NBT");
        MeasuringTracker tracker = new MeasuringTracker();
        try {
            CompressedStreamTools.func_152457_a(compressed, tracker);
        } catch (RuntimeException exception) {
            throw new IOException("ME toolbox NBT exceeds the native decoder budget or nesting limit", exception);
        }
        return new EncodedSize(compressed.length, tracker.bytes);
    }

    private record EncodedSize(int compressedBytes, long trackedBytes) {}

    private static final class MeasuringTracker extends NBTSizeTracker {

        private long bytes;

        private MeasuringTracker() {
            super(MAX_TRACKED_BYTES);
        }

        @Override
        public void func_152450_a(long bits) {
            super.func_152450_a(bits);
            bytes += bits / 8L;
        }
    }
}
