package com.science.gtnl.common.yogsothoth;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.science.gtnl.client.yogsothoth.YogTerminalClient;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class YogTerminalSyncPacket extends ClientboundPacket {

    private static final int PART_BUDGET = 768 * 1024;
    private static final long READ_LIMIT = 8L * 1024 * 1024;

    private NBTTagCompound data;

    public YogTerminalSyncPacket() {}

    private YogTerminalSyncPacket(NBTTagCompound data) {
        this.data = data;
    }

    // since: revision the client has within the current structure, -1 for everything; session: open entry or -1.
    static List<YogTerminalSyncPacket> of(YogRecord record, long since, int session) {
        List<NBTTagList> parts = new ArrayList<>();
        NBTTagList all = since < 0 ? record.writeTerminals() : record.writeChangedSince(since);
        NBTTagList part = new NBTTagList();
        int size = 0;
        for (int index = 0; index < all.tagCount(); index++) {
            NBTTagCompound entry = all.getCompoundTagAt(index);
            int entrySize = estimate(entry);
            if (part.tagCount() > 0 && size + entrySize > PART_BUDGET) {
                parts.add(part);
                part = new NBTTagList();
                size = 0;
            }
            part.appendTag(entry);
            size += entrySize;
        }
        parts.add(part);
        List<YogTerminalSyncPacket> packets = new ArrayList<>(parts.size());
        for (int index = 0; index < parts.size(); index++) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("id", record.id.toString());
            tag.setInteger("gen", record.generation);
            tag.setLong("rev", record.revision);
            tag.setInteger("active", record.active);
            tag.setBoolean("hidden", record.hidden);
            tag.setInteger("size", record.size());
            tag.setLong("structure", record.structure);
            tag.setBoolean("patch", since >= 0);
            tag.setInteger("session", session);
            tag.setInteger("part", index);
            tag.setInteger("parts", parts.size());
            tag.setTag("terminals", parts.get(index));
            packets.add(new YogTerminalSyncPacket(tag));
        }
        return packets;
    }

    private static int estimate(NBTTagCompound tag) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            CompressedStreamTools.write(tag, out);
        } catch (IOException ignored) {
            return PART_BUDGET;
        }
        return bytes.size();
    }

    @Override
    protected void read(ByteBuf buf) {
        byte[] bytes = new byte[buf.readInt()];
        buf.readBytes(bytes);
        try {
            data = CompressedStreamTools.func_152457_a(bytes, new NBTSizeTracker(READ_LIMIT));
        } catch (IOException exception) {
            throw new IllegalStateException("Malformed Yog-Sothoth terminal sync", exception);
        }
    }

    @Override
    protected void write(ByteBuf buf) {
        try {
            byte[] bytes = CompressedStreamTools.compress(data);
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to encode Yog-Sothoth terminal sync", exception);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        minecraft.func_152344_a(() -> YogTerminalClient.receive(data));
    }
}
