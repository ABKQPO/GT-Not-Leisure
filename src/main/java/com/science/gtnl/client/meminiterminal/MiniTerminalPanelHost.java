package com.science.gtnl.client.meminiterminal;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.client.gui.inventory.GuiContainer;

import com.cleanroommc.modularui.api.IMuiScreen;
import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.overlay.OverlayStack;
import com.cleanroommc.modularui.screen.ModularScreen;

/** Chooses the native panel lifecycle for MUI hosts and an overlay for other containers. */
final class MiniTerminalPanelHost {

    // MUI keeps screens while visiting recipe pages. Reuse their handler, without retaining closed screens here.
    // Values must also be weak: the panel's context refers back to the screen used as the key.
    private static final Map<ModularScreen, WeakReference<MiniTerminalPanelHost>> NATIVE_HOSTS = new WeakHashMap<>();

    private final MiniTerminalInventoryPanel panel;
    private final ModularScreen screen;
    private final IPanelHandler handler;

    static MiniTerminalPanelHost create(GuiContainer inventory, MiniTerminalModel model) {
        if (!(inventory instanceof IMuiScreen host)) return new MiniTerminalPanelHost(inventory, model, null);
        ModularScreen screen = host.getScreen();
        WeakReference<MiniTerminalPanelHost> reference = NATIVE_HOSTS.get(screen);
        MiniTerminalPanelHost attachment = reference == null ? null : reference.get();
        if (attachment == null) {
            attachment = new MiniTerminalPanelHost(inventory, model, screen);
            NATIVE_HOSTS.put(screen, new WeakReference<>(attachment));
        } else {
            attachment.panel.setModel(model);
        }
        return attachment;
    }

    private MiniTerminalPanelHost(GuiContainer inventory, MiniTerminalModel model, ModularScreen host) {
        panel = new MiniTerminalInventoryPanel(inventory, model);
        if (host == null) {
            screen = new MiniTerminalInventoryOverlay(panel);
            handler = null;
        } else {
            screen = host;
            handler = IPanelHandler.simple(host.getMainPanel(), (parent, player) -> panel, true);
        }
    }

    MiniTerminalInventoryPanel panel() {
        return panel;
    }

    void open() {
        if (handler == null) OverlayStack.open(screen);
        // The same host/handler can be opened again after a visit to a recipe screen.
        else if (!screen.isPanelOpen(panel)) handler.openPanel();
        panel.resetInteraction();
    }
}
