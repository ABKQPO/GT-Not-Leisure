package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.science.gtnl.common.item.items.ElectromagneticRailgun;
import com.science.gtnl.common.packet.base.ServerboundPacket;
import com.science.gtnl.common.railgun.RailgunBeamService;

public class OpenRailgunGuiPacket extends ServerboundPacket {

    @Override
    public void handleServer(EntityPlayerMP player) {
        ServerThreadUtil.addScheduledTask(() -> {
            var stack = player.getHeldItem();
            if (!player.isDead && stack != null
                && stack.getItem() instanceof ElectromagneticRailgun
                && !player.isUsingItem()
                && player.openContainer == player.inventoryContainer) {
                RailgunBeamService.stop(player);
                GuiFactories.playerInventory()
                    .openFromMainHand(player);
            }
        });
    }
}
