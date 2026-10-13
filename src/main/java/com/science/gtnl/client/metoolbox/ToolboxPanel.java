package com.science.gtnl.client.metoolbox;

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

final class ToolboxPanel extends ModularPanel implements IDraggable {

    private final ToolboxLayout layout;
    private final Matrix4f dragTransformInverse = new Matrix4f();
    private DraggablePanelWrapper drag;
    private boolean nativeGeometryPending;
    private ResizeDragArea resizeEdge;
    private Rectangle resizeStart;
    private ResizeDragArea pendingResizeEdge;
    private Rectangle pendingResizeStart;
    private boolean pendingCentered;

    ToolboxPanel(ToolboxLayout layout) {
        super("me_toolbox_mini");
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
        return ToolboxLayout.MIN_WIDTH;
    }

    @Override
    public int getMinDragHeight() {
        return ToolboxLayout.MIN_HEIGHT;
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
        }
        return super.onMousePressed(button);
    }

    @Override
    public void onDragResize() {
        nativeGeometryPending = true;

        pendingResizeEdge = resizeEdge;
        pendingResizeStart = resizeStart;
        pendingCentered = keepPosOnDragResize() || GuiScreen.isShiftKeyDown();
        scheduleResize();
    }

    @Override
    public void postResize() {
        super.postResize();
        if (!nativeGeometryPending) return;
        nativeGeometryPending = false;
        Area area = getArea();
        Rectangle next = new Rectangle(area.x, area.y, area.width, area.height);
        ResizeDragArea edge = pendingResizeEdge;
        Rectangle start = pendingResizeStart;
        pendingResizeEdge = null;
        pendingResizeStart = null;

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
        if (drag == null) return;
        drag.onDragEnd(successful);
        if (successful) {
            nativeGeometryPending = true;
            pendingResizeEdge = null;
            pendingResizeStart = null;
        }
    }

    @Override
    public void onDrag(int mouseButton, long timeSinceLastClick) {
        if (drag != null) drag.onDrag(mouseButton, timeSinceLastClick);
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
