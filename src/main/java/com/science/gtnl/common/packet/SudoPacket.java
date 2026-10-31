package com.science.gtnl.common.packet;

import java.nio.charset.StandardCharsets;

import net.minecraft.client.Minecraft;

import com.gtnllib.network.ClientboundPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class SudoPacket extends ClientboundPacket {

    public String message;

    public SudoPacket() {}

    public SudoPacket(String message) {
        this.message = message;
    }

    @Override
    protected void read(ByteBuf buf) {
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        this.message = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    protected void write(ByteBuf buf) {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        sendChatMessage(message);
    }

    @SideOnly(Side.CLIENT)
    public void sendChatMessage(String message) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.sendChatMessage(message);
    }
}
