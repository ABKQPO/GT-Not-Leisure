package com.science.gtnl.client.metoolbox;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IMuiScreen;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.RichTooltipEvent;

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
    private boolean collectingHostTooltips;
    private MuiTooltip pendingHostTooltip;
    private final List<MuiTooltip> hostTooltips = new ArrayList<>();
    private DrawScreenEvent.Post chromaticEvent;
    private Runnable chromaticTooltip;

    ToolboxForeground(ToolboxInventoryOverlay overlay) {
        this.overlay = overlay;
    }

    void beginFrame() {
        pendingHostTooltip = null;
        hostTooltips.clear();
        collectingHostTooltips = active() && overlay.inventory() instanceof IMuiScreen;
    }

    void captureHostTooltip(RichTooltipEvent.Color event) {
        if (!collectingHostTooltips || !active() || !(event.getTooltip() instanceof RichTooltip tooltip)) return;
        // Color exposes the original RichTooltip; PostText supplies its final bounds after wrapping/positioning.
        pendingHostTooltip = new MuiTooltip(tooltip, event.getItemStack(), event.getX(), event.getY(), 0, 0);
    }

    void captureHostTooltipBounds(RichTooltipEvent.PostText event) {
        MuiTooltip pending = pendingHostTooltip;
        pendingHostTooltip = null;
        if (!collectingHostTooltips || !active() || pending == null
            || pending.stack() != event.getItemStack()
            || pending.x() != event.getX()
            || pending.y() != event.getY()) return;
        hostTooltips.add(
            new MuiTooltip(
                pending.tooltip(),
                pending.stack(),
                pending.x(),
                pending.y(),
                event.getWidth(),
                event.getHeight()));
    }

    void queueCursor() {
        // Host RichTooltips were drawn before this overlay; don't collect our own or replayed tooltips.
        collectingHostTooltips = false;
        pendingHostTooltip = null;
        // Queue before MUI widgets add their tooltips; the queue runs after all overlay panels are drawn.
        overlay.getContext().queueForegroundRender(this::drawCursor);
    }

    void queueTooltip() {
        overlay.getContext().queueForegroundRender(this::drawHostTooltips);
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

        ItemStack renderedCursor = cursor;
        drawOverPanel(() -> {
            GL11.glTranslatef(0, 0, 200);
            RenderHelper.enableGUIStandardItemLighting();
            GuiContainerManager.drawItem(x - 8, y - 8, renderedCursor);
        });
    }

    private void drawHostTooltips() {
        try {
            if (!active() || hostTooltips.isEmpty() || !(overlay.inventory() instanceof IMuiScreen host)) return;
            Rectangle bounds = overlay.bounds();
            if (bounds.contains(overlay.getContext().getAbsMouseX(), overlay.getContext().getAbsMouseY())) return;
            for (MuiTooltip tooltip : hostTooltips) {
                // Include MUI's four-pixel background/border around the text bounds.
                if (!bounds.intersects(tooltip.x() - 4, tooltip.y() - 4, tooltip.width() + 8, tooltip.height() + 8))
                    continue;
                drawOverPanel(
                    () -> tooltip.tooltip()
                        .draw(
                            host.getScreen()
                                .getContext(),
                            tooltip.stack()));
            }
        } finally {
            hostTooltips.clear();
        }
    }

    private void drawOverPanel(Runnable render) {
        Minecraft mc = Minecraft.getMinecraft();
        Rectangle bounds = overlay.bounds();
        int scale = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight).getScaleFactor();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            // Redraw only the covered portion, leaving existing text, transparency and glint outside unchanged.
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(
                bounds.x * scale,
                mc.displayHeight - (bounds.y + bounds.height) * scale,
                bounds.width * scale,
                bounds.height * scale);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            render.run();
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

    private record MuiTooltip(RichTooltip tooltip, ItemStack stack, int x, int y, int width, int height) {}
}
