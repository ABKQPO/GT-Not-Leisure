package com.science.gtnl.common.meminiterminal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.client.meminiterminal.MiniTerminalClient;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import appeng.api.storage.data.IAEStack;
import appeng.util.Platform;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;

public class MiniTerminalUpdatePacket extends ClientboundPacket {

    public static final String TERMINAL_ID_KEY = "toolbox";

    private static final int PART_BYTES = 512 * 1024;
    private static final int MAX_PART_BYTES = 16 * 1024 * 1024;

    public record Entry(int key, IAEStack<?> stack) {}

    private NBTTagCompound data;
    private byte[] compressed = new byte[0];
    private List<Entry> entries = List.of();

    public MiniTerminalUpdatePacket() {}

    public MiniTerminalUpdatePacket(NBTTagCompound data) {
        this.data = data;
    }

    MiniTerminalUpdatePacket(NBTTagCompound data, byte[] compressed) {
        this.data = data;
        this.compressed = compressed;
    }

    @Override
    protected void read(ByteBuf buf) {
        data = ByteBufUtils.readTag(buf);
        int length = buf.readInt();
        if (length < 0 || length > buf.readableBytes()) throw new IllegalArgumentException("Mini terminal entries");
        if (length == 0) return;
        ByteBuf raw = Unpooled.buffer();
        try (InputStream input = new GZIPInputStream(new ByteBufInputStream(buf.readSlice(length)))) {
            raw.writeBytes(input.readNBytes(MAX_PART_BYTES + 1));
        } catch (IOException exception) {
            raw.release();
            throw new IllegalArgumentException("Mini terminal entries", exception);
        } catch (RuntimeException exception) {
            raw.release();
            throw exception;
        }
        try {
            if (raw.readableBytes() > MAX_PART_BYTES) throw new IllegalArgumentException("Mini terminal entries");
            List<Entry> decoded = new ArrayList<>();
            while (raw.isReadable()) decoded.add(new Entry(raw.readInt(), Platform.readStackByte(raw)));
            entries = decoded;
        } finally {
            raw.release();
        }
    }

    @Override
    protected void write(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, data);
        buf.writeInt(compressed.length);
        buf.writeBytes(compressed);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        minecraft.func_152344_a(() -> MiniTerminalClient.receive(data, entries));
    }

    static List<byte[]> encode(List<Entry> entries) throws IOException {
        List<byte[]> parts = new ArrayList<>();
        ByteBuf part = Unpooled.buffer(Math.min(PART_BYTES, 64 + entries.size() * 64));
        try {
            for (Entry entry : entries) {
                int start = part.writerIndex();
                part.writeInt(entry.key());
                Platform.writeStackByte(entry.stack(), part);
                if (part.readableBytes() > PART_BYTES) {
                    if (start == 0) throw new IOException("Oversize ME mini portable terminal entry");
                    ByteBuf next = Unpooled.buffer(PART_BYTES);
                    try {
                        next.writeBytes(part, start, part.writerIndex() - start);
                        part.writerIndex(start);
                        ByteBuf previous = part;
                        part = null;
                        parts.add(compress(previous));
                        part = next;
                        next = null;
                    } finally {
                        if (next != null) next.release();
                    }
                }
            }
            ByteBuf last = part;
            part = null;
            parts.add(compress(last));
            return parts;
        } finally {
            if (part != null) part.release();
        }
    }

    private static byte[] compress(ByteBuf raw) throws IOException {
        try {
            if (!raw.isReadable()) return new byte[0];
            if (raw.readableBytes() > MAX_PART_BYTES) throw new IOException("Oversize ME mini portable terminal entry");
            ByteArrayOutputStream output = new ByteArrayOutputStream(raw.readableBytes() / 4 + 64);
            try (GZIPOutputStream gzip = new GZIPOutputStream(output)) {
                raw.readBytes(gzip, raw.readableBytes());
            }
            return output.toByteArray();
        } finally {
            raw.release();
        }
    }
}
