package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import com.science.gtnl.common.item.items.ElectromagneticRailgun;
import com.science.gtnl.common.railgun.RailgunData;
import com.science.gtnl.common.railgun.RailgunModuleType;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class RailgunHudRenderer {

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS) return;
        var mc = Minecraft.getMinecraft();
        var player = mc.thePlayer;
        if (player == null || !player.isUsingItem()) return;
        var stack = player.getItemInUse();
        if (stack == null || !(stack.getItem() instanceof ElectromagneticRailgun)) return;
        int units = player.getItemInUseDuration() * (1 + RailgunData.modules(stack)
            .count(RailgunModuleType.ACCELERATION));
        int x = event.resolution.getScaledWidth() / 2 - 40, y = event.resolution.getScaledHeight() / 2 + 15;
        Gui.drawRect(x - 1, y - 1, x + 81, y + 5, 0xAA101020);
        Gui.drawRect(x, y, x + Math.min(80, units * 2), y + 4, units >= 40 ? 0xFFFF509A : 0xFF50CFFF);
        for (int threshold : new int[] { 10, 24, 40 })
            Gui.drawRect(x + threshold * 2 - 1, y - 2, x + threshold * 2, y + 6, 0xFFFFFFFF);
    }
}
