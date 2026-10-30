package com.gtnllib.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

import com.gtnllib.client.hud.HudLayout.Placement;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HudOverlay {

    public static void register() {
        HudOverlay overlay = new HudOverlay();
        MinecraftForge.EVENT_BUS.register(overlay);
        FMLCommonHandler.instance()
            .bus()
            .register(overlay);
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

    @SubscribeEvent
    public void afterFrame(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) HudEditor.openPending();
    }
}
