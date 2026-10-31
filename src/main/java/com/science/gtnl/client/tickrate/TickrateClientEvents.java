package com.science.gtnl.client.tickrate;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.science.gtnl.api.TickrateAPI;
import com.science.gtnl.config.TickrateConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;

@EventBusSubscriber(side = Side.CLIENT)
public class TickrateClientEvents {

    @SubscribeEvent
    public static void onChat(ClientChatReceivedEvent event) {
        if (event.message instanceof ChatComponentTranslation chat) {
            if (chat.getKey()
                .equals("GTNLEarlyCoreMod.show.clientside")) {
                event.message = new ChatComponentText("");
                ChatComponentTranslation currentTickrate = new ChatComponentTranslation(
                    "gtnl.command.tickrate.current_client");
                currentTickrate.getChatStyle()
                    .setColor(EnumChatFormatting.WHITE)
                    .setBold(true);
                event.message.appendSibling(currentTickrate);
                event.message.appendSibling(new ChatComponentText(" "));
                ChatComponentTranslation ticksPerSecond = new ChatComponentTranslation(
                    "gtnl.command.tickrate.ticks_per_second",
                    TickrateAPI.getClientTickrate());
                ticksPerSecond.getChatStyle()
                    .setColor(EnumChatFormatting.GREEN);
                event.message.appendSibling(ticksPerSecond);
            }
        }
    }

    @SubscribeEvent
    public static void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        TickrateAPI.changeServerTickrate(TickrateConfig.defaultTickrate);
        TickrateAPI.changeClientTickrate(null, TickrateConfig.defaultTickrate);
    }
}
