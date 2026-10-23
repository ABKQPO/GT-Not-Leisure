package com.science.gtnl.client.floatingwindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import codechicken.nei.api.API;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class FloatingWindows {

    private static final FloatingWindows INSTANCE = new FloatingWindows();
    private static final List<Function<GuiScreen, ? extends FloatingWindowPanel>> PROVIDERS = new ArrayList<>();
    private static boolean initialized;

    private FloatingWindows() {}

    public static void register(Function<GuiScreen, ? extends FloatingWindowPanel> provider) {
        init();
        PROVIDERS.add(provider);
    }

    private static void init() {
        if (initialized) return;
        initialized = true;
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FloatingWindowNEIHandler nei = new FloatingWindowNEIHandler();
        API.registerNEIGuiHandler(nei);
        GuiContainerManager.objectHandlers.addFirst(nei);
        GuiContainerManager.addDrawHandler(nei);
        GuiContainerManager.addTooltipHandler(nei);
    }

    public static List<FloatingWindowPanel> windows(GuiScreen gui) {
        if (gui == null || PROVIDERS.isEmpty()) return Collections.emptyList();
        List<FloatingWindowPanel> windows = null;
        for (Function<GuiScreen, ? extends FloatingWindowPanel> provider : PROVIDERS) {
            FloatingWindowPanel window = provider.apply(gui);
            if (window == null || !window.isValid() || window.inventory() != gui) continue;
            if (windows == null) windows = new ArrayList<>(PROVIDERS.size());
            windows.add(window);
        }
        return windows == null ? Collections.emptyList() : windows;
    }

    public static boolean hasWindows(GuiScreen gui) {
        return !windows(gui).isEmpty();
    }

    public static FloatingWindowPanel windowAt(GuiScreen gui, int mouseX, int mouseY) {
        for (FloatingWindowPanel window : windows(gui)) {
            if (window.isMouseOver(mouseX, mouseY)) return window;
        }
        return null;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void prepareFrame(DrawScreenEvent.Pre event) {
        FloatingWindowLayer.beginFrame(event.gui);
        for (FloatingWindowPanel window : windows(event.gui)) window.prepareFrame();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void finishFrame(DrawScreenEvent.Post event) {
        FloatingWindowLayer.endFrame(event.gui);
    }
}
