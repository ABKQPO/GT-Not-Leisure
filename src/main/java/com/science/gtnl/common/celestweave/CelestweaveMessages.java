package com.science.gtnl.common.celestweave;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;

import com.gtnewhorizon.gtnhlib.GTNHLib;

public final class CelestweaveMessages {

    private CelestweaveMessages() {}

    public static void hotbar(EntityPlayerMP player, String key, Object... args) {
        GTNHLib.proxy.sendMessageAboveHotbar(player, new ChatComponentTranslation(key, args), 40, true, true);
    }
}
