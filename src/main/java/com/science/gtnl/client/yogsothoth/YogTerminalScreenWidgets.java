package com.science.gtnl.client.yogsothoth;

import static com.science.gtnl.common.gui.YogSothothTerminalGui.BORDER;
import static com.science.gtnl.common.gui.YogSothothTerminalGui.CELL;
import static com.science.gtnl.common.gui.YogSothothTerminalGui.COLUMNS;
import static com.science.gtnl.common.gui.YogSothothTerminalGui.GRID_Y;
import static com.science.gtnl.common.gui.YogSothothTerminalGui.INVENTORY_LABEL_Y;
import static com.science.gtnl.common.gui.YogSothothTerminalGui.ROWS;
import static com.science.gtnl.common.gui.YogSothothTerminalGui.SCROLL_X;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IMuiScreen;
import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.science.gtnl.common.gui.YogSothothTerminalGui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class YogTerminalScreenWidgets {

    private static final int TITLE_COLOR = 0xE6DAFF;

    private static String tr(String key) {
        return StatCollector.translateToLocal("gtnl.yog_sothoth_terminal." + key);
    }

    public static boolean isScreen(GuiScreen gui) {
        return gui instanceof IMuiScreen screen && YogSothothTerminalGui.PANEL.equals(
            screen.getScreen()
                .getMainPanel()
                .getName());
    }

    public static ItemSlot slot(ItemSlot slot) {
        return slot.background(YogTerminalTextures.SLOT);
    }

    public static void decorate(ModularPanel panel, int location) {
        panel.background(YogTerminalTextures.BACKGROUND);
        panel.child(new Labels(location).full());
        panel.child(
            new Grid(location).pos(BORDER, GRID_Y)
                .size(SCROLL_X + 8 - BORDER, ROWS * CELL));
        panel.child(
            new WindowToggle(location).top(4)
                .right(6)
                .size(10, 10));
    }

    private static YogTerminalClient.Token token(int location) {
        for (YogTerminalClient.Token token : YogTerminalClient.tokens()) if (token.location() == location) return token;
        return null;
    }

    private static class Labels extends Widget<Labels> {

        private final int location;

        Labels(int location) {
            this.location = location;
        }

        @Override
        public boolean canHover() {
            return false;
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            Minecraft mc = Minecraft.getMinecraft();
            YogTerminalClient.Token token = token(location);
            YogTerminalClient.View view = token == null ? null : token.view();
            String title = tr("title") + " (" + (view == null ? 0 : view.terminals.size()) + ")";
            mc.fontRenderer.drawString(
                mc.fontRenderer.trimStringToWidth(title, getArea().width - BORDER - 20),
                BORDER,
                6,
                TITLE_COLOR,
                false);
            mc.fontRenderer.drawString(tr("screen.inventory"), BORDER, INVENTORY_LABEL_Y, TITLE_COLOR, false);
            GL11.glColor4f(1, 1, 1, 1);
        }
    }

    private static class WindowToggle extends ButtonWidget<WindowToggle> {

        private final int location;

        WindowToggle(int location) {
            this.location = location;
            invisible().playClickSound(true)
                .onMousePressed(button -> {
                    if (button == 0) YogTerminalClient.toggleWindow(token(location));
                    return true;
                })
                .tooltipAutoUpdate(true)
                .tooltipBuilder(tooltip -> {
                    tooltip.addLine(tr("show_window"));
                    tooltip.addLine(EnumChatFormatting.GRAY + tr(hidden() ? "show_window.off" : "show_window.on"));
                });
        }

        private boolean hidden() {
            YogTerminalClient.Token token = token(location);
            return token != null && token.view() != null && token.view().hidden;
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            GL11.glColor4f(1, 1, 1, 1);
            (isHovering() ? YogTerminalTextures.BUTTON_HOVER : YogTerminalTextures.BUTTON).draw(0, 0, 10, 10);
            (hidden() ? YogTerminalTextures.ICON_WINDOW_HIDDEN : YogTerminalTextures.ICON_WINDOW_SHOWN)
                .draw(1, 1, 8, 8);
        }
    }

    private static class Grid extends Widget<Grid> implements Interactable {

        private final int location;
        private int scroll;

        Grid(int location) {
            this.location = location;
            tooltipAutoUpdate(true).tooltipPos(RichTooltip.Pos.NEXT_TO_MOUSE)
                .tooltipBuilder(this::describe);
        }

        private int cells(YogTerminalClient.View view) {
            return (view == null ? 0 : view.terminals.size()) + 1;
        }

        private int maxScroll(int cells) {
            return Math.max(0, (cells + COLUMNS - 1) / COLUMNS - ROWS);
        }

        private int indexAt(int x, int y, int cells) {
            if (x < 0 || y < 0 || x >= COLUMNS * CELL || y >= ROWS * CELL) return -1;
            int index = (scroll + y / CELL) * COLUMNS + x / CELL;
            return index < cells ? index : -1;
        }

        private void describe(RichTooltip tooltip) {
            YogTerminalClient.Token token = token(location);
            YogTerminalClient.View view = token == null ? null : token.view();
            int index = indexAt(getContext().getMouseX(), getContext().getMouseY(), cells(view));
            if (index < 0) return;
            ItemStack stack = view == null ? null : view.get(index);
            if (stack == null) return;
            Minecraft mc = Minecraft.getMinecraft();
            for (Object line : stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips))
                tooltip.addLine(String.valueOf(line));
            if (YogTerminalClient.isSessionEntry(view, index))
                tooltip.addLine(EnumChatFormatting.RED + tr("hint.locked"));
            tooltip.addLine(EnumChatFormatting.DARK_GRAY + tr("hint.use"));
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            GL11.glColor4f(1, 1, 1, 1);
            YogTerminalClient.Token token = token(location);
            YogTerminalClient.View view = token == null ? null : token.view();
            int cells = cells(view);
            scroll = Math.max(0, Math.min(scroll, maxScroll(cells)));
            int first = scroll * COLUMNS;
            int hovered = indexAt(context.getMouseX(), context.getMouseY(), cells);
            for (int cell = 0; cell < COLUMNS * ROWS && first + cell < cells; cell++) {
                int index = first + cell;
                int x = cell % COLUMNS * CELL;
                int y = cell / COLUMNS * CELL;
                ItemStack stack = view == null ? null : view.get(index);
                YogTerminalTextures.slotFrame(view, index, stack)
                    .draw(x, y, CELL, CELL);
                if (stack != null) YogTerminalTextures.drawItem(stack, x + 1, y + 1, true);
                if (index == hovered) {
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    Gui.drawRect(x + 1, y + 1, x + 17, y + 17, 0x80FFFFFF);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                }
            }
            int maximum = maxScroll(cells);
            if (maximum > 0) {
                int scrollX = SCROLL_X - BORDER;
                int height = ROWS * CELL;
                YogTerminalTextures.SCROLL_TRACK.draw(scrollX, 0, 8, height);
                YogTerminalTextures.SCROLL_THUMB.draw(scrollX + 1, 1 + scroll * (height - 2 - 15) / maximum, 6, 15);
            }
            GL11.glColor4f(1, 1, 1, 1);
        }

        @Override
        public Result onMousePressed(int button) {
            YogTerminalClient.Token token = token(location);
            YogTerminalClient.View view = token == null ? null : token.view();
            int index = indexAt(getContext().getMouseX(), getContext().getMouseY(), cells(view));
            if (index >= 0 && Minecraft.getMinecraft().currentScreen instanceof GuiContainer gui)
                YogTerminalClient.clickEntry(gui, token, index, button);
            return Result.SUCCESS;
        }

        @Override
        public boolean onMouseScroll(UpOrDown direction, int amount) {
            YogTerminalClient.Token token = token(location);
            int maximum = maxScroll(cells(token == null ? null : token.view()));
            scroll = Math.max(0, Math.min(scroll + (direction == UpOrDown.UP ? -1 : 1), maximum));
            return true;
        }
    }
}
