package com.science.gtnl.client.yogsothoth;

import java.awt.Rectangle;
import java.util.List;
import java.util.function.BooleanSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.layout.IViewport;
import com.cleanroommc.modularui.api.layout.IViewportStack;
import com.cleanroommc.modularui.api.widget.IDragResizeable;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.HoveredWidgetList;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.WidgetTree;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.science.gtnl.client.floatingwindow.FloatingWindowPanel;

import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

@SideOnly(Side.CLIENT)
public final class YogTerminalPanel extends FloatingWindowPanel {

    private static final int TITLE_COLOR = 0xE6DAFF;

    private final YogTerminalLayout layout = YogTerminalClient.layout();
    private final IntOpenHashSet consumedMouseButtons = new IntOpenHashSet();
    private final HeaderButton[] buttons;
    private int scroll;
    private boolean resizing;

    public YogTerminalPanel(GuiContainer inventory) {
        super("yog_sothoth_terminal", inventory, YogTerminalClient.layout());
        pos(0, 0).size(YogTerminalLayout.MIN_WIDTH, YogTerminalLayout.MIN_HEIGHT);
        buttons = new HeaderButton[] {
            new HeaderButton(
                0,
                YogTerminalTextures.ICON_WINDOW_HIDDEN,
                "hide_window",
                () -> YogTerminalClient.toggleWindow(YogTerminalClient.selectedToken()),
                () -> true),
            new HeaderButton(
                1,
                YogTerminalTextures.ICON_COLLAPSE,
                "collapse",
                YogTerminalClient::toggleCollapsed,
                () -> true),
            new HeaderButton(2, YogTerminalTextures.ICON_RESET, "reset_window", () -> {
                layout.reset();
                YogTerminalClient.saveWindow();
            }, () -> true),
            new HeaderButton(
                3,
                YogTerminalTextures.ICON_NEXT,
                "next",
                YogTerminalClient::selectNext,
                () -> YogTerminalClient.windowTokens()
                    .size() > 1) };
        child(
            new GridWidget().full()
                .pos(0, 0));
        for (HeaderButton button : buttons) child(button);
    }

    private static String tr(String key) {
        return StatCollector.translateToLocal("gtnl.yog_sothoth_terminal." + key);
    }

    private static YogTerminalClient.View view() {
        YogTerminalClient.Token token = YogTerminalClient.selectedToken();
        return token == null ? null : token.view();
    }

    private static int cellCount() {
        YogTerminalClient.View view = view();
        return (view == null ? 0 : view.terminals.size()) + 1;
    }

    private static ItemStack entry(int index) {
        YogTerminalClient.View view = view();
        return view == null ? null : view.get(index);
    }

    @Override
    public void prepareFrame() {
        syncLayout();
    }

    private void syncLayout() {
        if (resizing || !isValid() || getContext().getStackSize() != 0 || getContext().hasDraggable()) return;
        resizing = true;
        try {
            boolean changed = requiresResize();
            if (changed) WidgetTree.resizeInternal(resizer(), false);
            layout.setCells(cellCount());
            changed |= layout.update(inventory(), YogTerminalClient.collapsed());
            Rectangle bounds = layout.bounds();
            if (getArea().x != bounds.x || getArea().y != bounds.y
                || getArea().width != bounds.width
                || getArea().height != bounds.height) {
                resizer().resetPosition();
                pos(bounds.x, bounds.y).size(bounds.width, bounds.height);
                changed = true;
            }
            resizeableOnDrag(layout.expanded());
            for (HeaderButton button : buttons) button.setEnabled(layout.expanded() && button.visible.getAsBoolean());
            scroll = Math.max(0, Math.min(scroll, layout.maxScroll()));
            if (changed) {
                WidgetTree.resizeInternal(resizer(), false);
                getScreen().onFrameUpdate();
            }
        } finally {
            resizing = false;
        }
    }

    private int indexAt(int x, int y) {
        int cell = layout.cellAt(x, y);
        if (cell < 0) return -1;
        int index = scroll * layout.columns() + cell;
        return index < cellCount() ? index : -1;
    }

    @Override
    public void onClose() {
        resetInteraction();
        super.onClose();
        YogTerminalClient.closeWindow(this);
    }

    @Override
    public void resetInteraction() {
        super.resetInteraction();
        consumedMouseButtons.clear();
    }

    @Override
    public boolean onMousePressed(int button) {
        consumedMouseButtons.remove(button);
        syncLayout();
        if (!isMouseOver(getContext().getAbsMouseX(), getContext().getAbsMouseY())) return false;
        super.onMousePressed(button);
        consumedMouseButtons.add(button);
        return true;
    }

    @Override
    public boolean onMouseRelease(int button) {
        boolean consumed = consumedMouseButtons.remove(button);
        boolean handled = super.onMouseRelease(button);
        syncLayout();
        YogTerminalClient.saveWindow();
        return handled || consumed;
    }

    @Override
    public boolean onMouseScroll(UpOrDown direction, int amount) {
        syncLayout();
        return isMouseOver(getContext().getAbsMouseX(), getContext().getAbsMouseY())
            && super.onMouseScroll(direction, amount);
    }

    @Override
    public boolean onKeyPressed(char character, int keyCode) {
        int mouseX = getContext().getAbsMouseX();
        int mouseY = getContext().getAbsMouseY();
        boolean closeKey = keyCode == Keyboard.KEY_ESCAPE
            || keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode();
        if (closeKey || !isMouseOver(mouseX, mouseY)) return super.onKeyPressed(character, keyCode);
        if (stackUnderMouse(mouseX, mouseY) != null) GuiContainerManager.getManager(inventory())
            .lastKeyTyped(keyCode, character);
        return true;
    }

    @Override
    public ItemStack stackUnderMouse(int mouseX, int mouseY) {
        if (!layout.expanded() || getContext().hasDraggable()) return null;
        Rectangle bounds = layout.bounds();
        int index = indexAt(mouseX - bounds.x, mouseY - bounds.y);
        return index < 0 ? null : entry(index);
    }

    @Override
    public List<String> handleItemTooltip(ItemStack stack, int mouseX, int mouseY, List<String> tooltip) {
        Rectangle bounds = layout.bounds();
        int index = indexAt(mouseX - bounds.x, mouseY - bounds.y);
        YogTerminalClient.View view = view();
        if (index < 0 || view == null) return tooltip;
        if (YogTerminalClient.isSessionEntry(view, index)) tooltip.add(EnumChatFormatting.RED + tr("hint.locked"));
        tooltip.add(EnumChatFormatting.DARK_GRAY + tr("hint.use"));
        return tooltip;
    }

    @Override
    public void drawForeground(ModularGuiContext context) {
        super.drawForeground(context);
        queueNEITooltip(context);
    }

    private void click(int index, int button) {
        YogTerminalClient.clickEntry(inventory(), YogTerminalClient.selectedToken(), index, button);
    }

    private final class HeaderButton extends ButtonWidget<HeaderButton> {

        private final UITexture icon;
        private final BooleanSupplier visible;

        HeaderButton(int slot, UITexture icon, String key, Runnable action, BooleanSupplier visible) {
            this.icon = icon;
            this.visible = visible;
            right(YogTerminalLayout.buttonRight(slot)).top(YogTerminalLayout.HEADER_Y)
                .size(YogTerminalLayout.BUTTON_SIZE, YogTerminalLayout.BUTTON_SIZE)
                .invisible()
                .playClickSound(true)
                .onMousePressed(mouseButton -> {
                    if (mouseButton == 0) action.run();
                    return true;
                })
                .tooltipBuilder(tooltip -> {
                    tooltip.addLine(tr(key));
                    tooltip.addLine(EnumChatFormatting.GRAY + tr(key + ".desc"));
                });
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            GL11.glColor4f(1, 1, 1, 1);
            (isHovering() ? YogTerminalTextures.BUTTON_HOVER : YogTerminalTextures.BUTTON)
                .draw(0, 0, YogTerminalLayout.BUTTON_SIZE, YogTerminalLayout.BUTTON_SIZE);
            icon.draw(1, 1, 8, 8);
        }
    }

    private final class GridWidget extends Widget<GridWidget> implements Interactable, IViewport {

        GridWidget() {
            tooltipPos(RichTooltip.Pos.NEXT_TO_MOUSE).tooltipAutoUpdate(true)
                .tooltipBuilder(this::describe);
        }

        private void describe(RichTooltip tooltip) {
            if (!layout.expanded()) {
                tooltip.addLine(tr("title"));
                tooltip.addLine(EnumChatFormatting.GRAY + tr(layout.hasRoom() ? "expand" : "no_room"));
                return;
            }
            int index = indexAt(getContext().getMouseX(), getContext().getMouseY());
        }

        @Override
        public void getSelfAt(IViewportStack stack, HoveredWidgetList widgets, int x, int y) {
            int localX = stack.unTransformX(x, y);
            int localY = stack.unTransformY(x, y);
            if (layout.dragHandleAt(localX, localY)
                || IDragResizeable.getDragResizeCorner(YogTerminalPanel.this, getArea(), stack, x, y) != null) return;
            IViewport.super.getSelfAt(stack, widgets, x, y);
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            int width = getArea().width;
            int height = getArea().height;
            if (width <= 0 || height <= 0) return;
            GL11.glColor4f(1, 1, 1, 1);
            YogTerminalClient.Token token = YogTerminalClient.selectedToken();
            if (!layout.expanded()) {
                YogTerminalTextures.COMPACT.draw(0, 0, width, height);
                if (token != null)
                    YogTerminalTextures.drawItem(token.stack(), (width - 16) / 2, (height - 16) / 2, false);
                return;
            }
            YogTerminalTextures.BACKGROUND.draw(0, 0, width, height);
            drawTitle(token);
            YogTerminalClient.View view = token == null ? null : token.view();
            int cells = cellCount();
            int first = scroll * layout.columns();
            int hovered = indexAt(context.getMouseX(), context.getMouseY());
            int visibleCells = layout.columns() * layout.visibleRows();
            for (int cell = 0; cell < visibleCells && first + cell < cells; cell++) {
                int index = first + cell;
                int x = layout.cellX(cell);
                int y = layout.cellY(cell);
                ItemStack stack = view == null ? null : view.get(index);
                UITexture frame = stack == null ? YogTerminalTextures.SLOT_INSERT : YogTerminalTextures.SLOT;
                if (view != null && YogTerminalClient.isSessionEntry(view, index))
                    frame = YogTerminalTextures.SLOT_LOCKED;
                else if (view != null && index == view.active) frame = YogTerminalTextures.SLOT_ACTIVE;
                frame.draw(x, y, YogTerminalLayout.CELL, YogTerminalLayout.CELL);
                if (stack != null) YogTerminalTextures.drawItem(stack, x + 1, y + 1, true);
                if (index == hovered) {
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    Gui.drawRect(x + 1, y + 1, x + 17, y + 17, 0x80FFFFFF);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                }
            }
            if (layout.scrolls()) drawScrollbar();
            GL11.glColor4f(1, 1, 1, 1);
        }

        private void drawTitle(YogTerminalClient.Token token) {
            Minecraft mc = Minecraft.getMinecraft();
            String title = tr("title");
            List<YogTerminalClient.Token> tokens = YogTerminalClient.windowTokens();
            if (tokens.size() > 1) title += " " + (tokens.indexOf(token) + 1) + "/" + tokens.size();
            title = mc.fontRenderer.trimStringToWidth(title, Math.max(0, layout.titleWidth()));
            mc.fontRenderer
                .drawString(title, YogTerminalLayout.BORDER, YogTerminalLayout.HEADER_Y + 1, TITLE_COLOR, false);
        }

        private void drawScrollbar() {
            int x = layout.scrollX();
            int top = YogTerminalLayout.GRID_Y;
            int height = layout.visibleRows() * YogTerminalLayout.CELL;
            YogTerminalTextures.SCROLL_TRACK.draw(x - 1, top, YogTerminalLayout.SCROLL_WIDTH + 2, height);
            int thumb = 15;
            int maximum = layout.maxScroll();
            int offset = maximum <= 0 ? 0 : scroll * (height - 2 - thumb) / maximum;
            YogTerminalTextures.SCROLL_THUMB.draw(x, top + 1 + offset, YogTerminalLayout.SCROLL_WIDTH, thumb);
        }

        @Override
        public Result onMousePressed(int button) {
            if (!layout.expanded()) {
                if (button == 0 && layout.hasRoom()) YogTerminalClient.toggleCollapsed();
                return Result.SUCCESS;
            }
            int index = indexAt(getContext().getMouseX(), getContext().getMouseY());
            if (index >= 0) click(index, button);
            return Result.SUCCESS;
        }

        @Override
        public boolean onMouseScroll(UpOrDown direction, int amount) {
            if (!layout.expanded()) return true;
            scroll = Math.max(0, Math.min(scroll + (direction == UpOrDown.UP ? -1 : 1), layout.maxScroll()));
            return true;
        }
    }
}
