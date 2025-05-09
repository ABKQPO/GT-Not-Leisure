package com.reavaritia.common.block.ExtremeAnvil;

import java.nio.charset.Charset;

import net.minecraft.util.ChatAllowedCharacters;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class ExtremeAnvilPacket implements IMessage {

    private String payloadIdentifier;
    private byte[] payloadData;

    public ExtremeAnvilPacket() {}

    public ExtremeAnvilPacket(String identifier, byte[] data) {
        this.payloadIdentifier = identifier;
        this.payloadData = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.payloadIdentifier = ByteBufUtils.readUTF8String(buf);
        int length = buf.readShort();
        if (length > 0) {
            this.payloadData = new byte[length];
            buf.readBytes(this.payloadData);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.payloadIdentifier);
        buf.writeShort(this.payloadData.length);
        buf.writeBytes(this.payloadData);
    }

    public String getPayloadIdentifier() {
        return this.payloadIdentifier;
    }

    public byte[] getPayloadData() {
        return this.payloadData;
    }

    public static class Handler implements IMessageHandler<ExtremeAnvilPacket, IMessage> {

        @Override
        public IMessage onMessage(ExtremeAnvilPacket message, MessageContext ctx) {
            if ("ExtremeAnvil|ItemName".equals(message.getPayloadIdentifier())) {
                if (ctx
                    .getServerHandler().playerEntity.openContainer instanceof ContainerExtremeAnvil ContainerExtremeAnvil) {

                    byte[] data = message.getPayloadData();
                    if (data != null && data.length >= 1) {
                        String itemName = ChatAllowedCharacters
                            .filerAllowedCharacters(new String(data, Charset.forName("UTF-8")));

                        if (itemName.length() <= 30) {
                            ContainerExtremeAnvil.updateItemName(itemName);
                        }
                    } else {
                        ContainerExtremeAnvil.updateItemName("");
                    }
                }
            }
            return null;
        }
    }
}
