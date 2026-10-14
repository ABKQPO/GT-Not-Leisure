package com.science.gtnl.common.meminiterminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.science.gtnl.client.meminiterminal.MiniTerminalClient;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class MiniTerminalUpdatePacket extends ClientboundPacket {

    // Retain the existing wire key while Java fields use terminal terminology.
    public static final String TERMINAL_ID_KEY = "toolbox";

    private static final int MAX_ENTRIES = 64;
    private static final int MAX_COMPRESSED_BYTES = 24_000;
    // Bound the whole update before sending any parts; per-packet limits alone still allow bursts.
    private static final int MAX_UPDATE_PARTS = 128;
    static final int MAX_UPDATE_ENTRIES = MAX_ENTRIES * MAX_UPDATE_PARTS;
    private static final long MAX_UPDATE_COMPRESSED_BYTES = 1024 * 1024;
    private static final long MAX_UPDATE_TRACKED_BYTES = 16L * 1024 * 1024;

    private static final long MAX_TRACKED_BYTES = 2L * 1024 * 1024 - 8 * 1024;

    private NBTTagCompound data;

    public MiniTerminalUpdatePacket() {}

    public MiniTerminalUpdatePacket(NBTTagCompound data) {
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
        minecraft.func_152344_a(() -> MiniTerminalClient.receive(data));
    }

    static List<NBTTagList> split(List<NBTTagCompound> entries) throws IOException {
        if (entries.size() > MAX_UPDATE_ENTRIES) throw new IOException("Too many ME mini portable terminal update entries");
        List<NBTTagList> parts = new ArrayList<>();
        UpdateBudget budget = new UpdateBudget();
        NBTTagList part = new NBTTagList();
        EncodedSize singleEntrySize = null;
        int compressedBytes = 0;
        long trackedBytes = 0;
        for (NBTTagCompound entry : entries) {
            if (entry == null) throw new IOException("Null ME mini portable terminal entry");
            NBTTagList singleton = new NBTTagList();
            singleton.appendTag(entry);
            EncodedSize size = measure(singleton);
            if (part.tagCount() >= MAX_ENTRIES || compressedBytes + size.compressedBytes() > MAX_COMPRESSED_BYTES
                || trackedBytes + size.trackedBytes() > MAX_TRACKED_BYTES) {
                addPart(parts, part, singleEntrySize, budget);
                part = new NBTTagList();
                compressedBytes = 0;
                trackedBytes = 0;
            }
            part.appendTag(entry);
            if (part.tagCount() == 1) singleEntrySize = size;
            compressedBytes += size.compressedBytes();

            trackedBytes += size.trackedBytes();
        }
        addPart(parts, part, singleEntrySize, budget);
        return parts;
    }

    private static void addPart(List<NBTTagList> parts, NBTTagList part, EncodedSize singleEntrySize,
        UpdateBudget budget) throws IOException {
        if (parts.size() >= MAX_UPDATE_PARTS) throw new IOException("Too many ME mini portable terminal update packets");
        // A one-entry part is identical to the singleton envelope already validated by the native decoder.
        EncodedSize size = part.tagCount() == 1 ? singleEntrySize : measure(part);
        budget.compressedBytes += size.compressedBytes();
        budget.trackedBytes += size.trackedBytes();
        if (budget.compressedBytes > MAX_UPDATE_COMPRESSED_BYTES || budget.trackedBytes > MAX_UPDATE_TRACKED_BYTES) {
            throw new IOException("ME mini portable terminal update exceeds the total NBT budget");
        }
        parts.add(part);
    }

    private static final class UpdateBudget {

        private long compressedBytes;
        private long trackedBytes;
    }

    private static EncodedSize measure(NBTTagList entries) throws IOException {
        NBTTagCompound envelope = new NBTTagCompound();

        envelope.setTag("entries", entries);
        byte[] compressed = CompressedStreamTools.compress(envelope);
        if (compressed.length > MAX_COMPRESSED_BYTES) throw new IOException("Oversize ME mini portable terminal compressed NBT");
        MeasuringTracker tracker = new MeasuringTracker();
        try {
            CompressedStreamTools.func_152457_a(compressed, tracker);
        } catch (RuntimeException exception) {
            throw new IOException("ME mini portable terminal NBT exceeds the native decoder budget or nesting limit", exception);
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
