package com.science.gtnl.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

import com.science.gtnl.client.hud.HudLayout.Placement;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HudOverlay {

    private HudOverlay() {}

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new HudOverlay());
    }

    @SubscribeEvent
    public void overlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || Minecraft.getMinecraft().thePlayer == null
            || HudEditor.isOpen()) return;
        int width = event.resolution.getScaledWidth(), height = event.resolution.getScaledHeight();
        for (HudElement element : HudRegistry.elements()) {
            if (!element.hasRenderer()) continue;
            Placement place = HudLayout.place(element, width, height);
            if (place == null) continue;
            element.render(place, event.partialTicks);
        }
    }
}
