package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;

import com.gtnllib.network.ClientboundPacket;
import com.science.gtnl.client.wireless.WirelessWorldHighlight;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class WirelessHighlightPacket extends ClientboundPacket {

    private int dimension, x, y, z;

    public WirelessHighlightPacket() {}

    public WirelessHighlightPacket(int dimension, int x, int y, int z) {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    protected void read(ByteBuf buf) {
        dimension = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    protected void write(ByteBuf buf) {
        buf.writeInt(dimension);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        var connection = minecraft.getNetHandler();
        minecraft.func_152344_a(
            () -> {
                if (minecraft.getNetHandler() == connection)
                    WirelessWorldHighlight.afterTeleport(minecraft, dimension, x, y, z);
            });
    }
}
