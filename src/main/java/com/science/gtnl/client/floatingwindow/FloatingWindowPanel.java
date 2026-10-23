package com.science.gtnl.client.floatingwindow;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import org.joml.Matrix4f;

import com.cleanroommc.modularui.api.widget.IDraggable;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.api.widget.ResizeDragArea;
import com.cleanroommc.modularui.overlay.OverlayStack;
import com.cleanroommc.modularui.screen.DraggablePanelWrapper;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.LocatedWidget;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.widget.sizer.Area;

import codechicken.nei.NEIClientUtils;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class FloatingWindowPanel extends ModularPanel implements IDraggable {

    private final GuiContainer inventory;
    private final FloatingWindowGeometry geometry;
    private final Matrix4f dragTransformInverse = new Matrix4f();
    private DraggablePanelWrapper drag;
    private boolean nativeGeometryPending;
    private ResizeDragArea resizeEdge;
    private Rectangle resizeStart;
    private int resizeMouseX;
    private int resizeMouseY;
    private ResizeDragArea pendingResizeEdge;
    private Rectangle pendingResizeStart;
    private Rectangle pendingResizeBounds;
    private boolean pendingCentered;

    protected FloatingWindowPanel(String name, GuiContainer inventory, FloatingWindowGeometry geometry) {
        super(name);
        this.inventory = inventory;
        this.geometry = geometry;
        invisible();
        resizeableOnDrag(true);
    }

    public final GuiContainer inventory() {
        return inventory;
    }

    public FloatingWindowGeometry geometry() {
        return geometry;
    }

    public boolean isActive() {
        return FloatingWindows.windows(inventory)
            .contains(this) && isValid()
            && isOpen()
            && Minecraft.getMinecraft().currentScreen == inventory;
    }

    public void prepareFrame() {}

    public void resetInteraction() {
        if (isValid() && isMoving() && getContext().hasDraggable()) getContext().dropDraggable(true);
    }

    public void closeWindow() {
        resetInteraction();
        if (!isValid()) return;
        if (getScreen().isOverlay()) OverlayStack.close(getScreen());
        else {
            closeIfOpen();
            FloatingWindowHost.release(this);
        }
    }

    public void detach() {
        resetInteraction();
        if (isValid() && !getScreen().isOverlay()) closeIfOpen();
    }

    public Rectangle bounds() {
        Rectangle moving = movingBounds();
        return moving == null ? geometry.bounds() : moving;
    }

    public boolean intersects(int x, int y, int width, int height) {
        Rectangle moving = movingBounds();
        return moving == null ? geometry.intersects(x, y, width, height) : moving.intersects(x, y, width, height);
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        if (!isValid() || !isOpen() || !isEnabled()) return false;
        Rectangle moving = movingBounds();
        if (moving == null ? !geometry.contains(mouseX, mouseY) : !moving.contains(mouseX, mouseY)) return false;
        for (ModularPanel panel : getScreen().getPanelManager()
            .getOpenPanels()) {
            if (panel == this) return true;
            if (panel.disablePanelsBelow() || panel.isAnyHovered()) return false;
        }
        return false;
    }

    public ItemStack stackUnderMouse(int mouseX, int mouseY) {
        return null;
    }

    public List<String> handleTooltip(int mouseX, int mouseY, List<String> tooltip) {
        return tooltip;
    }

    public List<String> handleItemTooltip(ItemStack stack, int mouseX, int mouseY, List<String> tooltip) {
        return tooltip;
    }

    public boolean handleDragNDrop(int mouseX, int mouseY, ItemStack stack, int button) {
        return false;
    }

    protected void queueNEITooltip(ModularGuiContext context) {
        if (getScreen().isOverlay()) return;
        context.queueForegroundRender(() -> {
            if (!isActive()) return;
            int mouseX = getContext().getAbsMouseX();
            int mouseY = getContext().getAbsMouseY();
            if (stackUnderMouse(mouseX, mouseY) == null) return;
            GuiContainerManager manager = GuiContainerManager.getManager(inventory);
            NEIClientUtils.gl2DRenderContext(() -> manager.renderToolTips(mouseX, mouseY));
        });
    }

    @Override
    public boolean canHover() {
        return true;
    }

    @Override
    public boolean isDraggable() {
        return true;
    }

    @Override
    public boolean keepPosOnDragResize() {
        return false;
    }

    @Override
    public int getDragAreaSize() {
        return 4;
    }

    @Override
    public int getMinDragWidth() {
        return geometry.minWidth();
    }

    @Override
    public int getMinDragHeight() {
        return geometry.minHeight();
    }

    @Override
    public boolean onMousePressed(int button) {
        resizeEdge = null;
        resizeStart = null;
        LocatedWidget hovered = getTopHoveringLocated(false);
        if (hovered != null && hovered.getElement() == this
            && hovered.getAdditionalHoverInfo() instanceof ResizeDragArea edge) {
            resizeEdge = edge;
            resizeStart = new Rectangle(getArea());
            resizeMouseX = getContext().getAbsMouseX();
            resizeMouseY = getContext().getAbsMouseY();
        }
        return super.onMousePressed(button);
    }

    @Override
    public void onDragResize() {
        nativeGeometryPending = true;
        pendingResizeEdge = resizeEdge;
        pendingResizeStart = resizeStart;
        pendingCentered = keepPosOnDragResize() || GuiScreen.isShiftKeyDown();
        pendingResizeBounds = null;
        if (resizeEdge != null && resizeStart != null) {
            int factor = pendingCentered ? 2 : 1;
            int dx = (getContext().getAbsMouseX() - resizeMouseX) * factor;
            int dy = (getContext().getAbsMouseY() - resizeMouseY) * factor;
            pendingResizeBounds = new Rectangle(resizeStart);
            if (resizeEdge.left || resizeEdge.right) pendingResizeBounds.width += resizeEdge.left ? -dx : dx;
            if (resizeEdge.top || resizeEdge.bottom) pendingResizeBounds.height += resizeEdge.top ? -dy : dy;
        }
        scheduleResize();
    }

    @Override
    public void postResize() {
        super.postResize();
        if (!nativeGeometryPending) return;
        nativeGeometryPending = false;
        Area area = getArea();
        Rectangle next = pendingResizeBounds == null ? new Rectangle(area.x, area.y, area.width, area.height)
            : pendingResizeBounds;
        ResizeDragArea edge = pendingResizeEdge;
        Rectangle start = pendingResizeStart;
        pendingResizeEdge = null;
        pendingResizeStart = null;
        pendingResizeBounds = null;

        if (edge != null && start != null) geometry.acceptResizedBounds(next, start, edge, pendingCentered);
        else geometry.acceptPanelBounds(next);
    }

    @Override
    public boolean onDragStart(int button) {
        drag = new DraggablePanelWrapper(this);
        if (!drag.onDragStart(button)) return false;
        dragTransformInverse.set(
            getContext().peek()
                .getInvertedMatrix());
        return true;
    }

    @Override
    public void onDragEnd(boolean successful) {
        if (drag == null || !successful) return;
        onDrag(0, 0);
        Area next = drag.getMovingArea();
        resizer().resetPosition();
        resizer().relativeToScreen();
        pos(next.x, next.y);
        nativeGeometryPending = true;
        pendingResizeEdge = null;
        pendingResizeStart = null;
        pendingResizeBounds = null;
        scheduleResize();
    }

    @Override
    public void onDrag(int mouseButton, long timeSinceLastClick) {
        if (drag == null) return;
        drag.onDrag(mouseButton, timeSinceLastClick);
        Area screen = getScreen().getScreenArea();
        geometry.keepDragHandleVisible(drag.getMovingArea(), geometry.expanded(), screen.width, screen.height);
    }

    @Override
    public boolean canDropHere(int x, int y, IWidget widget) {
        return drag != null && drag.canDropHere(x, y, widget);
    }

    @Override
    public Area getMovingArea() {
        return drag == null ? null : drag.getMovingArea();
    }

    @Override
    public boolean isMoving() {
        return drag != null && drag.isMoving();
    }

    @Override
    public void setMoving(boolean moving) {
        if (drag != null) drag.setMoving(moving);
    }

    protected Rectangle movingBounds() {
        if (!isMoving()) return null;
        Area area = drag.getMovingArea();
        return new Rectangle(area.x, area.y, area.width, area.height);
    }

    @Override
    public void drawMovingState(ModularGuiContext context, float partialTicks) {
        if (drag == null) return;
        context.pushMatrix();
        try {
            context.multiply(dragTransformInverse);
            drag.drawMovingState(context, partialTicks);
        } finally {
            context.popMatrix();
        }
    }
}
