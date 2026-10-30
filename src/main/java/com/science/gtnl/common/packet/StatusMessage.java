package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

import com.gtnllib.network.ClientboundPacket;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class StatusMessage extends ClientboundPacket {

    private IChatComponent chat;

    public StatusMessage() {

    }

    public StatusMessage(IChatComponent chat) {
        this.chat = chat;
    }

    @Override
    protected void read(ByteBuf buf) {
        this.chat = IChatComponent.Serializer.func_150699_a(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    protected void write(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, IChatComponent.Serializer.func_150696_a(this.chat));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClient(Minecraft minecraft) {
        sendStatusMessage(chat);
    }

    @SideOnly(Side.CLIENT)
    public void sendStatusMessage(IChatComponent chat) {
        Minecraft.getMinecraft().ingameGUI.func_110326_a(chat.getFormattedText(), true);
    }
}
