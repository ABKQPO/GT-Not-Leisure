package com.science.gtnl.common.packet;

import static com.science.gtnl.utils.SubscribeEventUtils.*;

import com.science.gtnl.loader.RecipeLoader;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class SyncCircuitNanitesPacket implements IMessage {

    public long worldSeed;

    public SyncCircuitNanitesPacket() {}

    public SyncCircuitNanitesPacket(long worldSeed) {
        this.worldSeed = worldSeed;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        worldSeed = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(worldSeed);
    }

    public static class Handler implements IMessageHandler<SyncCircuitNanitesPacket, IMessage> {

        @Override
        public IMessage onMessage(SyncCircuitNanitesPacket message, MessageContext ctx) {
            if (!circuitNanitesDataLoad) {
                RecipeLoader.loadCircuitNanitesData(message.worldSeed);
            }
            circuitNanitesDataLoad = true;
            return null;
        }
    }

}
