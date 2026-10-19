package com.science.gtnl.common.celestweave;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

import com.science.gtnl.common.equipment.EquipmentMessages;
import com.science.gtnl.common.item.items.CelestweaveArmor;
import com.science.gtnl.common.packet.CelestweaveFxPacket;
import com.science.gtnl.config.MainConfig;

public final class CelestweaveDash {

    private static final Map<UUID, Long> LAST = new ConcurrentHashMap<>();

    private CelestweaveDash() {}

    public static void dash(EntityPlayerMP player) {
        ItemStack boots = CelestweaveData.piece(player, CelestweaveArmor.BOOTS);
        if (boots == null || !CelestweaveData.active(boots, CelestweaveModule.DASH) || player.isRiding()) return;
        var config = MainConfig.item.celestweave;
        long now = player.worldObj.getTotalWorldTime();
        Long last = LAST.get(player.getUniqueID());
        if (last != null && now - last >= 0 && now - last < config.dashCooldown) return;
        if (!CelestweaveEnergy.drain(player, CelestweaveArmor.BOOTS, boots, config.dashEu)) {
            EquipmentMessages.hotbar(player, "item.gtnl.celestweave.message.no_energy");
            return;
        }
        LAST.put(player.getUniqueID(), now);
        Vec3 look = player.getLookVec();
        double speed = config.dashSpeed;
        player.motionX = look.xCoord * speed;
        player.motionY = Math.max(0.1, look.yCoord * speed * 0.6 + 0.1);
        player.motionZ = look.zCoord * speed;
        player.fallDistance = 0;
        player.velocityChanged = true;
        CelestweaveServerEvents.sendFx(player, CelestweaveFxPacket.DASH, player.posX, player.posY, player.posZ);
    }

    public static void forget(EntityPlayerMP player) {
        LAST.remove(player.getUniqueID());
    }
}
