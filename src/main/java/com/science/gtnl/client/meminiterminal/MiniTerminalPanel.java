package com.science.gtnl.client.meminiterminal;

import java.awt.Rectangle;

import net.minecraft.client.gui.GuiScreen;

import org.joml.Matrix4f;

import com.cleanroommc.modularui.api.widget.IDraggable;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.api.widget.ResizeDragArea;
import com.cleanroommc.modularui.screen.DraggablePanelWrapper;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.LocatedWidget;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.widget.sizer.Area;

class MiniTerminalPanel extends ModularPanel implements IDraggable {

    private final MiniTerminalLayout layout;
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

    MiniTerminalPanel(MiniTerminalLayout layout) {
        super("me_mini_portable_terminal");
        this.layout = layout;
        invisible();
        resizeableOnDrag(true);
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
        return MiniTerminalLayout.MIN_WIDTH;
    }

    @Override
    public int getMinDragHeight() {
        return MiniTerminalLayout.MIN_HEIGHT;
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
            // MUI skips dimensions below its minimum. Pass the requested size to our layout so it clamps instead.
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

        if (edge != null && start != null) layout.acceptResizedBounds(next, start, edge, pendingCentered);
        else layout.acceptPanelBounds(next);
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
        // Persist the constrained preview directly, including positions outside the screen.
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
        MiniTerminalLayout.keepDragHandleVisible(drag.getMovingArea(), layout.expanded(), screen.width, screen.height);
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

    Rectangle movingBounds() {
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
