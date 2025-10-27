package com.science.gtnl.common.packet;

import com.science.gtnl.loader.RecipeLoader;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class SyncRecipePacket implements IMessage {

    public SyncRecipePacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<SyncRecipePacket, IMessage> {

        @Override
        public IMessage onMessage(SyncRecipePacket message, MessageContext ctx) {
            RecipeLoader.loadRecipesServerStart();
            return null;
        }
    }

}
