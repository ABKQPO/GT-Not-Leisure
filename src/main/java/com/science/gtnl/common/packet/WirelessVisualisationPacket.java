package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.client.WirelessCardInput;
import com.science.gtnl.common.packet.base.ClientboundPacket;
import com.science.gtnl.common.wireless.WirelessCardBinding;
import com.science.gtnl.common.wireless.WirelessCardVisualisation;

import appeng.core.sync.AppEngPacket;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

/** Native AE visualisation payload, qualified by source/owner and viewer dimension to reject stale replies. */
public final class WirelessVisualisationPacket extends ClientboundPacket {

    private NBTTagCompound bindingTag;
    private int dimension;
    private byte[] payload;

    public WirelessVisualisationPacket() {}

    public WirelessVisualisationPacket(NBTTagCompound tag, int dimension, AppEngPacket packet) {
        bindingTag = new NBTTagCompound();
        WirelessCardBinding.read(tag)
            .write(bindingTag);
        this.dimension = dimension;
        ByteBuf nativePayload = packet.getProxy()
            .payload();
        try {
            int size = nativePayload.readableBytes() - 4; // AE's packet ID is not part of its data constructor.
            if (size < 0 || size > WirelessCardVisualisation.MAX_PACKET_BYTES) {
                payload = new byte[0]; // Clear the old view rather than send an oversized snapshot.
            } else {
                payload = new byte[size];
                nativePayload.getBytes(nativePayload.readerIndex() + 4, payload);
            }
        } finally {
            nativePayload.release();
        }
    }

    @Override
    protected void read(ByteBuf buf) {
        bindingTag = ByteBufUtils.readTag(buf);
        dimension = buf.readInt();
        int size = buf.readInt();
        if (WirelessCardBinding.read(bindingTag) == null || size < 0
            || size > WirelessCardVisualisation.MAX_PACKET_BYTES
            || size > buf.readableBytes())
            throw new IllegalArgumentException("Invalid wireless visualisation snapshot");
        payload = new byte[size];
        buf.readBytes(payload);
    }

    @Override
    protected void write(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, bindingTag);
        buf.writeInt(dimension);
        buf.writeInt(payload.length);
        buf.writeBytes(payload);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        var connection = minecraft.getNetHandler();
        minecraft.func_152344_a(() -> {
            if (minecraft.getNetHandler() == connection) WirelessCardInput
                .receiveVisualisation(minecraft, WirelessCardBinding.read(bindingTag), dimension, payload);
        });
    }
}
