package com.science.gtnl.common.packet;

import static com.science.gtnl.ScienceNotLeisure.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.science.gtnl.common.packet.client.TitleDisplayHandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class TitlePacket implements IMessage, IMessageHandler<TitlePacket, IMessage> {

    public String message;
    public int durationTicks;
    public int colorText;
    public int fadeIn;
    public int fadeOut;
    public double scaleText;

    public TitlePacket() {
        this.durationTicks = 70;
        this.colorText = 0xFFFFFF;
        this.scaleText = 3;
        this.fadeIn = 10;
        this.fadeOut = 20;
    }

    public TitlePacket(String message, int durationTicks, int color, double scale, int fadeIn, int fadeOut) {
        this.message = message;
        this.durationTicks = durationTicks;
        this.colorText = color;
        this.scaleText = scale;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
    }

    public TitlePacket(String message) {
        this(message, 70, 0xFFFFFF, 3, 10, 20);
    }

    public TitlePacket(String message, int durationTicks) {
        this(message, durationTicks, 0xFFFFFF, 3, 10, 20);
    }

    public TitlePacket(String message, int durationTicks, int color) {
        this(message, durationTicks, color, 3, 10, 20);
    }

    public TitlePacket(String message, int durationTicks, int color, double scale) {
        this(message, durationTicks, color, scale, 10, 20);
    }

    public TitlePacket(String message, int durationTicks, int color, double scale, int fadeIn) {
        this(message, durationTicks, color, scale, fadeIn, 20);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.message = ByteBufUtils.readUTF8String(buf);
        this.durationTicks = buf.readInt();
        this.colorText = buf.readInt();
        this.scaleText = buf.readDouble();
        this.fadeIn = buf.readInt();
        this.fadeOut = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.message);
        buf.writeInt(this.durationTicks);
        buf.writeInt(this.colorText);
        buf.writeDouble(this.scaleText);
        buf.writeInt(this.fadeIn);
        buf.writeInt(this.fadeOut);
    }

    @Override
    public IMessage onMessage(final TitlePacket msg, final MessageContext ctx) {
        TitleDisplayHandler
            .displayTitle(msg.message, msg.durationTicks, msg.colorText, msg.scaleText, msg.fadeIn, msg.fadeOut);
        return null;
    }

    public static void sendTitleToPlayerName(String playerName, String message, int durationTicks, int color,
        double scale, int fadeIn, int fadeOut) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            EntityPlayerMP player = server.getConfigurationManager()
                .func_152612_a(playerName);
            if (player != null) {
                sendTitleToPlayer(player, message, durationTicks, color, scale, fadeIn, fadeOut);
            }
        }
    }

    public static void sendTitleToPlayer(EntityPlayerMP player, String message) {
        network.sendTo(new TitlePacket(message), player);
    }

    public static void sendTitleToPlayer(EntityPlayerMP player, String message, int durationTicks) {
        network.sendTo(new TitlePacket(message, durationTicks), player);
    }

    public static void sendTitleToPlayer(EntityPlayerMP player, String message, int durationTicks, int color) {
        network.sendTo(new TitlePacket(message, durationTicks, color), player);
    }

    public static void sendTitleToPlayer(EntityPlayerMP player, String message, int durationTicks, int color,
        double scale) {
        network.sendTo(new TitlePacket(message, durationTicks, color, scale), player);
    }

    public static void sendTitleToPlayer(EntityPlayerMP player, String message, int durationTicks, int color,
        double scale, int fadeIn) {
        network.sendTo(new TitlePacket(message, durationTicks, color, scale, fadeIn), player);
    }

    public static void sendTitleToPlayer(EntityPlayerMP player, String message, int durationTicks, int color,
        double scale, int fadeIn, int fadeOut) {
        network.sendTo(new TitlePacket(message, durationTicks, color, scale, fadeIn, fadeOut), player);
    }
}
