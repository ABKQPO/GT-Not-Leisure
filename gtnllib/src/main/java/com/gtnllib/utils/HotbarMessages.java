package com.gtnllib.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import com.gtnewhorizon.gtnhlib.GTNHLib;

public class HotbarMessages {

    public static final int DEFAULT_TICKS = 40;
    public static final EnumChatFormatting DEFAULT_COLOR = EnumChatFormatting.WHITE;

    public static void send(EntityPlayer player, IChatComponent message) {
        send(player, message, DEFAULT_TICKS);
    }

    public static void send(EntityPlayer player, IChatComponent message, int ticks) {
        if (player instanceof EntityPlayerMP serverPlayer) GTNHLib.proxy
            .sendMessageAboveHotbar(serverPlayer, withDefaultColor(message, DEFAULT_COLOR), ticks, true, true);
    }

    public static IChatComponent withDefaultColor(IChatComponent message, EnumChatFormatting color) {
        IChatComponent shown = message.createCopy();
        ChatStyle style = shown.getChatStyle();
        // GTNHLib draws text that carries no colour of its own in black.
        if (style.getColor() == null) shown.setChatStyle(
            style.createShallowCopy()
                .setColor(color));
        return shown;
    }
}
