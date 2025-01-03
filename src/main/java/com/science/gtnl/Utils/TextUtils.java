package com.science.gtnl.Utils;

import static com.mojang.realmsclient.gui.ChatFormatting.*;

import net.minecraft.util.EnumChatFormatting;

import com.mojang.realmsclient.gui.ChatFormatting;

public class TextUtils {

    public static final String SCIENCE_NOT_LEISURE = TextUtils.full_color("Science Not Leisure");
    public static final String STERILE = SCIENCE_NOT_LEISURE + EnumChatFormatting.DARK_PURPLE + ": Sterile";
    public static final String AE = SCIENCE_NOT_LEISURE + EnumChatFormatting.BLUE + ": Applied Energistics 2";
    public static final String SRP = SCIENCE_NOT_LEISURE + EnumChatFormatting.BLUE + ": Structural Reconstruction Plan";
    public static final String SQY = SCIENCE_NOT_LEISURE + EnumChatFormatting.BLUE + "§r X §6§l§o犰狳重工";

    public static String formatting(String input, ChatFormatting[] colours, double delay) {
        StringBuilder sb = new StringBuilder(input.length() * 3);
        if (delay <= 0.0D) delay = 0.001D;
        int offset = (int) Math.floor((System.currentTimeMillis() & 0x3FFFL) / delay) % colours.length;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            sb.append(colours[(colours.length + i - offset) % colours.length].toString());
            sb.append(c);
        }
        return sb.toString();
    }

    public static String full_color(String input) {
        return formatting(input, new ChatFormatting[] { RED, GOLD, YELLOW, GREEN, AQUA, BLUE, LIGHT_PURPLE }, 80.0D);
    }
}
