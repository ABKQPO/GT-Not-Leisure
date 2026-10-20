package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.equipment.IEquipmentItem;
import com.science.gtnl.common.packet.base.ServerboundPacket;

public class OpenEquipmentGuiPacket extends ServerboundPacket {

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            var stack = player.getHeldItem();
            if (player.isDead || stack == null
                || player.isUsingItem()
                || player.openContainer != player.inventoryContainer) return;
            if (!(stack.getItem() instanceof IEquipmentItem equipment)) return;
            equipment.beforeSettingsOpen(player, stack);
            GuiFactories.playerInventory()
                .openFromMainHand(player);
        });
    }
}
