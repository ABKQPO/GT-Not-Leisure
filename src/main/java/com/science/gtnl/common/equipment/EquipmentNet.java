package com.science.gtnl.common.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;

import com.gtnewhorizon.gtnhlib.GTNHLib;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.base.ClientboundPacket;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class EquipmentNet {

    private EquipmentNet() {}

    public static void sendAround(Entity entity, ClientboundPacket packet, double range) {
        ScienceNotLeisure.network.sendToAllAround(
            packet,
            new TargetPoint(entity.worldObj.provider.dimensionId, entity.posX, entity.posY, entity.posZ, range));
    }

    public static void hotbar(EntityPlayerMP player, String key, Object... args) {
        GTNHLib.proxy.sendMessageAboveHotbar(player, new ChatComponentTranslation(key, args), 40, true, true);
    }
}
