package com.science.gtnl.common.packet;

import com.science.gtnl.utils.gui.ContainerDirePatternEncoder;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class EncodeDirePattern implements IMessage, IMessageHandler<EncodeDirePattern, IMessage> {

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    @Override
    public IMessage onMessage(EncodeDirePattern message, MessageContext ctx) {
        if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerDirePatternEncoder c) c.encode();
        return null;
    }
}
