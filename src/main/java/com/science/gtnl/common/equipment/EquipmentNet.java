package com.science.gtnl.common.equipment;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;

import com.gtnewhorizon.gtnhlib.GTNHLib;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class EquipmentNet {

    private static final int HOTBAR_TICKS = 40, GUI_THROTTLE_TICKS = 5;
    private static final Map<EntityPlayer, Long> LAST_GUI = Collections.synchronizedMap(new WeakHashMap<>());

    private EquipmentNet() {}

    public static void sendAround(Entity entity, ClientboundPacket packet, double range) {
        ScienceNotLeisure.network.sendToAllAround(
            packet,
            new TargetPoint(entity.worldObj.provider.dimensionId, entity.posX, entity.posY, entity.posZ, range));
    }

    public static void hotbar(EntityPlayerMP player, String key, Object... args) {
        GTNHLib.proxy.sendMessageAboveHotbar(player, new ChatComponentTranslation(key, args), HOTBAR_TICKS, true, true);
    }

    public static boolean guiThrottled(EntityPlayer player) {
        long now = player.worldObj.getTotalWorldTime();
        Long last = LAST_GUI.put(player, now);
        return last != null && now - last >= 0 && now - last < GUI_THROTTLE_TICKS;
    }
}
