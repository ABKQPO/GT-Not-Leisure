package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.item.items.CelestweaveArmor;
import com.science.gtnl.common.item.items.ElectromagneticRailgun;
import com.science.gtnl.common.packet.base.ServerboundPacket;
import com.science.gtnl.common.railgun.RailgunBeamService;

public class OpenEquipmentGuiPacket extends ServerboundPacket {

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            var stack = player.getHeldItem();
            if (player.isDead || stack == null
                || player.isUsingItem()
                || player.openContainer != player.inventoryContainer) return;
            if (ElectromagneticRailgun.is(stack)) RailgunBeamService.stop(player);
            else if (!(stack.getItem() instanceof CelestweaveArmor)) return;
            GuiFactories.playerInventory()
                .openFromMainHand(player);
        });
    }
}
