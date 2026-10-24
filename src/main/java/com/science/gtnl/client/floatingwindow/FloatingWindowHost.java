package com.science.gtnl.client.floatingwindow;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.gui.inventory.GuiContainer;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IMuiScreen;
import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.overlay.OverlayStack;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.science.gtnl.ScienceNotLeisure;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class FloatingWindowHost<P extends FloatingWindowPanel> {

    private static final Map<ModularScreen, Map<String, WeakReference<FloatingWindowHost<?>>>> NATIVE_HOSTS = new WeakHashMap<>();

    private final P panel;
    private final ModularScreen screen;
    private final IPanelHandler handler;

    @SuppressWarnings("unchecked")
    public static <P extends FloatingWindowPanel> FloatingWindowHost<P> create(GuiContainer inventory, String key,
        Function<GuiContainer, P> factory, Consumer<P> reuse) {
        if (!(inventory instanceof IMuiScreen host)) return new FloatingWindowHost<>(factory.apply(inventory), null);
        ModularScreen screen = host.getScreen();
        Map<String, WeakReference<FloatingWindowHost<?>>> hosts = NATIVE_HOSTS
            .computeIfAbsent(screen, ignored -> new HashMap<>());
        WeakReference<FloatingWindowHost<?>> reference = hosts.get(key);
        FloatingWindowHost<P> attachment = reference == null ? null : (FloatingWindowHost<P>) reference.get();
        if (attachment == null) {
            attachment = new FloatingWindowHost<>(factory.apply(inventory), screen);
            hosts.put(key, new WeakReference<>(attachment));
        } else {
            reuse.accept(attachment.panel);
        }
        return attachment;
    }

    private FloatingWindowHost(P panel, ModularScreen host) {
        this.panel = panel;
        if (host == null) {
            screen = new Overlay(panel);
            handler = null;
        } else {
            screen = host;
            handler = IPanelHandler.simple(host.getMainPanel(), (parent, player) -> panel, true);
        }
    }

    static void release(FloatingWindowPanel panel) {
        for (Map<String, WeakReference<FloatingWindowHost<?>>> hosts : NATIVE_HOSTS.values()) {
            hosts.values()
                .removeIf(reference -> {
                    FloatingWindowHost<?> host = reference.get();
                    if (host == null || host.panel != panel) return host == null;
                    host.handler.deleteCachedPanel();
                    return true;
                });
        }
    }

    public P panel() {
        return panel;
    }

    public void open() {
        if (handler == null) {
            OverlayStack.open(screen);
            // Overlay panels initialise on resize, which MUI2 only does for itself on GUI init.
            screen.onResize(panel.inventory().width, panel.inventory().height);
        } else if (!screen.isPanelOpen(panel)) handler.openPanel();
        panel.resetInteraction();
    }

    static final class Overlay extends ModularScreen {

        private final FloatingWindowPanel panel;

        Overlay(FloatingWindowPanel panel) {
            super(ScienceNotLeisure.MODID, panel);
            this.panel = panel;
            constructOverlay(panel.inventory());
        }

        void drawInContainer(int mouseX, int mouseY) {
            if (!panel.isActive()) return;
            getContext().updateState(
                mouseX,
                mouseY,
                GuiContext.getDefault()
                    .getPartialTicks());
            panel.prepareFrame();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();
            try {
                FloatingWindowLayer.loadWindowMatrix(panel.inventory());
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glColor4f(1, 1, 1, 1);
                GL11.glDepthMask(true);
                GL11.glClearDepth(1);
                GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
                super.drawScreen();
                super.drawForeground();
                GL11.glDepthMask(true);
                GL11.glClearDepth(1);
                GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            } finally {
                GL11.glPopMatrix();
                GL11.glPopAttrib();
            }
        }

        @Override
        public void drawScreen() {}

        @Override
        public void drawForeground() {}
    }
}
