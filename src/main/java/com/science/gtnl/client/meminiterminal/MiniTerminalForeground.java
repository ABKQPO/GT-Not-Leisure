package com.science.gtnl.client.meminiterminal;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;

import appeng.client.gui.slots.VirtualMESlot;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Supplies AE slot tooltips, plus foreground compatibility for non-MUI overlay hosts. */
@SideOnly(Side.CLIENT)
final class MiniTerminalForeground {

    private final MiniTerminalInventoryPanel overlay;
    private boolean drawingNativeTooltip;
    private boolean nativeTooltipRequested;
    private DrawScreenEvent.Post chromaticEvent;
    private Runnable chromaticTooltip;

    MiniTerminalForeground(MiniTerminalInventoryPanel overlay) {
        this.overlay = overlay;
    }

    void reset() {
        nativeTooltipRequested = false;
        chromaticEvent = null;
        chromaticTooltip = null;
    }

    void queueTooltip() {
        overlay.getContext().queueForegroundRender(this::drawNativeTooltip);
    }

    boolean deferNativeTooltip() {
        if (!active() || !overlay.getScreen().isOverlay() || drawingNativeTooltip) return false;
        nativeTooltipRequested = true;
        return true;
    }

    boolean deferChromaticTooltip(DrawScreenEvent.Post event, Runnable renderTooltip) {
        if (!active() || !overlay.getScreen().isOverlay() || event.gui != overlay.inventory()) return false;
        chromaticEvent = event;
        chromaticTooltip = renderTooltip;
        return true;
    }

    void drawChromaticTooltip(DrawScreenEvent.Post event) {
        if (chromaticEvent != event) return;
        Runnable renderTooltip = chromaticTooltip;
        chromaticEvent = null;
        chromaticTooltip = null;
        if (active() && renderTooltip != null) renderTooltip.run();
    }

    private boolean active() {
        return MiniTerminalClient.overlay() == overlay && overlay.isValid() && overlay.isOpen()
            && Minecraft.getMinecraft().currentScreen == overlay.inventory();
    }

    private void drawNativeTooltip() {
        boolean requested = nativeTooltipRequested;
        nativeTooltipRequested = false;
        if (!active()) return;
        int x = overlay.getContext().getAbsMouseX();
        int y = overlay.getContext().getAbsMouseY();
        if (overlay.isMouseOver(x, y)) {
            VirtualMESlot slot = overlay.hoveredSlot(x, y);
            if (slot == null
                || slot.getAEStack() == null && Minecraft.getMinecraft().thePlayer.inventory.getItemStack() == null)
                return;
        } else if (!requested) {
            // Respect host GUIs that suppress NEI tooltips over their own widgets.
            return;
        }
        drawingNativeTooltip = true;
        try {
            GuiContainerManager manager = GuiContainerManager.getManager(overlay.inventory());
            NEIClientUtils.gl2DRenderContext(() -> manager.renderToolTips(x, y));
        } finally {
            drawingNativeTooltip = false;
        }
    }
}
