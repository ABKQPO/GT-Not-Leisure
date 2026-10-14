package com.science.gtnl.client.metoolbox;

import java.awt.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;

import org.lwjgl.opengl.GL11;

import appeng.client.gui.slots.VirtualMESlot;
import appeng.integration.modules.NEI;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Keeps cursor items and tooltips above the toolbox, in the same order as a normal container GUI. */
@SideOnly(Side.CLIENT)
final class ToolboxForeground {

    private final ToolboxInventoryOverlay overlay;
    private boolean drawingNativeTooltip;
    private boolean nativeTooltipRequested;
    private DrawScreenEvent.Post chromaticEvent;
    private Runnable chromaticTooltip;

    ToolboxForeground(ToolboxInventoryOverlay overlay) {
        this.overlay = overlay;
    }

    void queueCursor() {
        // Queue before MUI widgets add their tooltips; the queue runs after all overlay panels are drawn.
        overlay.getContext().queueForegroundRender(this::drawCursor);
    }

    void queueTooltip() {
        overlay.getContext().queueForegroundRender(this::drawNativeTooltip);
    }

    boolean deferNativeTooltip() {
        if (!active() || drawingNativeTooltip) return false;
        nativeTooltipRequested = true;
        return true;
    }

    boolean deferChromaticTooltip(DrawScreenEvent.Post event, Runnable renderTooltip) {
        if (!active() || event.gui != overlay.inventory()) return false;
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
        return ToolboxClient.overlay() == overlay && Minecraft.getMinecraft().currentScreen == overlay.inventory();
    }

    private void drawCursor() {
        if (!active()) return;
        int x = overlay.getContext().getAbsMouseX();
        int y = overlay.getContext().getAbsMouseY();
        Rectangle bounds = overlay.bounds();
        // Include icons whose centre is outside the panel but whose edge is covered by it.
        if (!bounds.intersects(x - 8, y - 8, 16, 16)) return;
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack cursor = NEI.instance != null && NEIClientConfig.isEnabled() && !NEIClientConfig.isHidden()
            ? NEI.instance.getDraggingPhantomItem()
            : null;
        if (cursor == null) cursor = mc.thePlayer.inventory.getItemStack();
        if (cursor == null) return;

        int scale = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight).getScaleFactor();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            // Only redraw the covered portion, avoiding doubled glint/transparent pixels outside the panel.
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(
                bounds.x * scale,
                mc.displayHeight - (bounds.y + bounds.height) * scale,
                bounds.width * scale,
                bounds.height * scale);
            GL11.glTranslatef(0, 0, 200);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            RenderHelper.enableGUIStandardItemLighting();
            GuiContainerManager.drawItem(x - 8, y - 8, cursor);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private void drawNativeTooltip() {
        boolean requested = nativeTooltipRequested;
        nativeTooltipRequested = false;
        if (!active()) return;
        int x = overlay.getContext().getAbsMouseX();
        int y = overlay.getContext().getAbsMouseY();
        if (overlay.bounds().contains(x, y)) {
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
