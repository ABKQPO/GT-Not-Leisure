package com.science.gtnl.client.floatingwindow;

import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import codechicken.nei.api.INEIGuiAdapter;
import codechicken.nei.api.ShortcutInputHandler;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerDrawHandler;
import codechicken.nei.guihook.IContainerObjectHandler;
import codechicken.nei.guihook.IContainerTooltipHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FloatingWindowNEIHandler extends INEIGuiAdapter
    implements IContainerObjectHandler, IContainerTooltipHandler, IContainerDrawHandler {

    @Override
    public void onPreDraw(GuiContainer gui) {
        FloatingWindowLayer.openHostPass(gui);
    }

    @Override
    public void renderObjects(GuiContainer gui, int mouseX, int mouseY) {
        List<FloatingWindowPanel> windows = FloatingWindows.windows(gui);
        if (windows.isEmpty()) return;
        for (FloatingWindowPanel window : windows) {
            if (window.getScreen() instanceof FloatingWindowHost.Overlay overlay)
                overlay.drawInContainer(mouseX, mouseY);
        }
        FloatingWindowLayer.flushTooltips(gui);
    }

    @Override
    public void postRenderObjects(GuiContainer gui, int mouseX, int mouseY) {}

    @Override
    public void renderSlotUnderlay(GuiContainer gui, Slot slot) {}

    @Override
    public void renderSlotOverlay(GuiContainer gui, Slot slot) {}

    @Override
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int width, int height) {
        for (FloatingWindowPanel window : FloatingWindows.windows(gui)) {
            if (window.intersects(x, y, width, height)) return true;
        }
        return false;
    }

    @Override
    public boolean handleDragNDrop(GuiContainer gui, int mouseX, int mouseY, ItemStack draggedStack, int button) {
        FloatingWindowPanel window = FloatingWindows.windowAt(gui, mouseX, mouseY);
        return window != null && window.handleDragNDrop(mouseX, mouseY, draggedStack, button);
    }

    @Override
    public ItemStack getStackUnderMouse(GuiContainer gui, int mouseX, int mouseY) {
        FloatingWindowPanel window = FloatingWindows.windowAt(gui, mouseX, mouseY);
        return window == null ? null : window.stackUnderMouse(mouseX, mouseY);
    }

    @Override
    public boolean objectUnderMouse(GuiContainer gui, int mouseX, int mouseY) {
        return FloatingWindows.windowAt(gui, mouseX, mouseY) != null;
    }

    @Override
    public List<String> handleTooltip(GuiContainer gui, int mouseX, int mouseY, List<String> currentTooltip) {
        FloatingWindowPanel window = FloatingWindows.windowAt(gui, mouseX, mouseY);
        return window == null ? currentTooltip : window.handleTooltip(mouseX, mouseY, currentTooltip);
    }

    @Override
    public List<String> handleItemTooltip(GuiContainer gui, ItemStack stack, int mouseX, int mouseY,
        List<String> currentTooltip) {
        if (stack == null) return currentTooltip;
        FloatingWindowPanel window = FloatingWindows.windowAt(gui, mouseX, mouseY);
        return window == null ? currentTooltip : window.handleItemTooltip(stack, mouseX, mouseY, currentTooltip);
    }

    @Override
    public Map<String, String> handleHotkeys(GuiContainer gui, int mouseX, int mouseY, Map<String, String> hotkeys) {
        ItemStack stack = getStackUnderMouse(gui, mouseX, mouseY);
        if (stack != null) hotkeys.putAll(ShortcutInputHandler.handleHotkeys(mouseX, mouseY, stack));
        return hotkeys;
    }

    @Override
    public boolean shouldShowTooltip(GuiContainer gui) {
        return true;
    }

    @Override
    public void guiTick(GuiContainer gui) {
        if (FloatingWindows.hasWindows(gui)) gui.theSlot = GuiContainerManager.getSlotMouseOver(gui);
    }

    @Override
    public void refresh(GuiContainer gui) {}

    @Override
    public void load(GuiContainer gui) {}
}
