package com.science.gtnl.common.equipment;

import net.minecraft.entity.Entity;

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
}
