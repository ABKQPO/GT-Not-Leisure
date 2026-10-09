package com.science.gtnl.common.packet;

import java.nio.charset.StandardCharsets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.packet.base.ClientboundPacket;
import com.science.gtnl.common.packet.base.ServerboundPacket;
import com.science.gtnl.common.wireless.WirelessCardContainer;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public final class WirelessCardGuiPacket extends ServerboundPacket {

    private int window, action;
    private long session, revision;
    private String text = "", dimension = "";
    private int filter;

    public WirelessCardGuiPacket() {}

    public WirelessCardGuiPacket(int window, NBTTagCompound display, int action) {
        this.window = window;
        this.session = display.getLong("session");
        this.revision = display.getLong("revision");
        this.action = action;
    }

    public WirelessCardGuiPacket(int window, NBTTagCompound display, int action, String text, String dimension,
        int filter) {
        this(window, display, action);
        this.text = text;
        this.dimension = dimension;
        this.filter = filter;
    }

    private static String readText(ByteBuf buf) {
        int length = buf.readUnsignedShort();
        if (length > 256 || length > buf.readableBytes()) throw new IllegalArgumentException("Oversized GUI text");
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static void writeText(ByteBuf buf, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 256) throw new IllegalArgumentException("Oversized GUI text");
        buf.writeShort(bytes.length);
        buf.writeBytes(bytes);
    }

    @Override
    protected void read(ByteBuf buf) {
        window = buf.readInt();
        session = buf.readLong();
        revision = buf.readLong();
        action = buf.readInt();
        text = readText(buf);
        dimension = readText(buf);
        filter = buf.readUnsignedByte();
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeInt(window);
        buf.writeLong(session);
        buf.writeLong(revision);
        buf.writeInt(action);
        writeText(buf, text);
        writeText(buf, dimension);
        buf.writeByte(filter);
    }

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            if (player.openContainer instanceof WirelessCardContainer container && container.windowId == window) {
                container.action(session, revision, action, text, dimension, filter);
            }
        });
    }

    public static final class Snapshot extends ClientboundPacket {

        private int window;
        private NBTTagCompound data;

        public Snapshot() {}

        public Snapshot(int window, NBTTagCompound data) {
            this.window = window;
            this.data = data;
        }

        @Override
        protected void read(ByteBuf buf) {
            window = buf.readInt();
            data = ByteBufUtils.readTag(buf);
        }

        @Override
        protected void write(ByteBuf buf) {
            buf.writeInt(window);
            ByteBufUtils.writeTag(buf, data);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void handleClient(Minecraft minecraft) {
            var connection = minecraft.getNetHandler();
            minecraft.func_152344_a(() -> { if (minecraft.getNetHandler() == connection) apply(minecraft); });
        }

        @SideOnly(Side.CLIENT)
        private void apply(Minecraft minecraft) {
            if (data != null && minecraft.thePlayer != null
                && minecraft.thePlayer.openContainer instanceof WirelessCardContainer container
                && container.windowId == window) {
                container.display = data;
                if (data.hasKey("highlight", 9))
                    com.science.gtnl.client.WirelessWorldHighlight.show(minecraft, data.getTagList("highlight", 10));
            }
        }
    }
}
