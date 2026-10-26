package com.gtnllib.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;

import org.jetbrains.annotations.Nullable;

import com.gtnllib.GTNLLib;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PayloadHandler {

    public static class Client<T extends ClientboundPacket> implements IMessageHandler<T, IMessage> {

        @Override
        public @Nullable IMessage onMessage(T message, MessageContext ctx) {
            try {
                runClient(message);
            } catch (RuntimeException exception) {
                GTNLLib.LOG.error(
                    "Unhandled clientbound packet {}",
                    message.getClass()
                        .getName(),
                    exception);
                throw exception;
            }
            return null;
        }

        @SideOnly(Side.CLIENT)
        private void runClient(T message) {
            message.handleClient(Minecraft.getMinecraft());
        }
    }

    public static class Server<T extends ServerboundPacket> implements IMessageHandler<T, IMessage> {

        @Override
        public @Nullable IMessage onMessage(T message, MessageContext ctx) {
            if (message.isInvalid()) {
                return null;
            }
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            try {
                message.handleServer(player);
            } catch (RuntimeException exception) {
                GTNLLib.LOG.error(
                    "Unhandled serverbound packet {}",
                    message.getClass()
                        .getName(),
                    exception);
                throw exception;
            }
            return null;
        }
    }
}
