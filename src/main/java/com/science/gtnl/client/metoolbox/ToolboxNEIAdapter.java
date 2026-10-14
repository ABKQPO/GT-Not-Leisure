package com.science.gtnl.client.metoolbox;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import appeng.api.storage.data.IAEStack;
import appeng.client.gui.slots.VirtualMEPinSlot;
import appeng.client.gui.slots.VirtualMESlot;
import appeng.integration.modules.NEI;
import appeng.integration.modules.NEIHelpers.NEIGuiHandler;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.api.ShortcutInputHandler;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerDrawHandler;
import codechicken.nei.guihook.IContainerObjectHandler;
import codechicken.nei.guihook.IContainerSlotClickHandler;
import codechicken.nei.guihook.IContainerTooltipHandler;

public final class ToolboxNEIAdapter extends NEIGuiHandler
    implements IContainerObjectHandler, IContainerTooltipHandler, IContainerSlotClickHandler, IContainerDrawHandler {

    private static ToolboxInventoryOverlay overlay(GuiContainer gui) {
        ToolboxInventoryOverlay overlay = ToolboxClient.overlay();
        return overlay != null && gui == overlay.inventory() ? overlay : null;
    }

    static void refreshHoveredSlot(GuiContainer gui) {
        if (overlay(gui) != null) {

            gui.theSlot = GuiContainerManager.getSlotMouseOver(gui);
        }
    }

    static boolean handleKeyTyped(GuiContainer gui, char character, int keyCode) {
        ToolboxInventoryOverlay overlay = overlay(gui);
        if (overlay == null || Minecraft.getMinecraft().currentScreen != gui) return false;
        Point mouse = GuiDraw.getMousePosition();
        if (overlay.bounds()
            .contains(mouse.x, mouse.y)) {
            VirtualMESlot slot = overlay.hoveredSlot(mouse.x, mouse.y);

            if (slot == null || slot.getAEStack() == null) return false;
        }
        refreshHoveredSlot(gui);

        boolean handled = GuiContainerManager.getManager(gui)
            .lastKeyTyped(keyCode, character);
        return handled || Minecraft.getMinecraft().currentScreen != gui;
    }

    static boolean handleSearchDrop(GuiContainer gui) {
        if (!NEIClientConfig.isEnabled() || NEIClientConfig.isHidden()) return false;
        Point mouse = GuiDraw.getMousePosition();
        ItemStack stack = NEI.instance == null ? null : NEI.instance.getDraggingPhantomItem();
        if (stack == null) stack = NEIClientUtils.getHeldItem();
        return fillSearch(gui, mouse.x, mouse.y, stack);
    }

    @Override
    public boolean handleDragNDrop(GuiContainer gui, int mouseX, int mouseY, ItemStack draggedStack, int button) {
        return fillSearch(gui, mouseX, mouseY, draggedStack);
    }

    private static boolean fillSearch(GuiContainer gui, int mouseX, int mouseY, ItemStack stack) {
        ToolboxInventoryOverlay overlay = overlay(gui);
        if (overlay == null || stack == null || stack.getItem() == null) return false;
        overlay.syncLayout();
        if (!overlay.layout()
            .expanded() || overlay.getContext()
                .hasDraggable())
            return false;
        Rectangle bounds = overlay.bounds();
        if (!overlay.nativeSearchField()
            .isMouseIn(mouseX - bounds.x - ToolboxLayout.SEARCH_X, mouseY - bounds.y - ToolboxLayout.HEADER_Y))
            return false;

        overlay.applySearch(NEI.searchField.getEscapedSearchText(getItemStackText(stack)));
        return true;
    }

    @Override
    public void renderObjects(GuiContainer gui, int mouseX, int mouseY) {

        ToolboxInventoryOverlay overlay = overlay(gui);
        if (overlay != null) overlay.syncLayout();
    }

    @Override
    public void onPreDraw(GuiContainer gui) {}

    @Override
    public void postRenderObjects(GuiContainer gui, int mouseX, int mouseY) {}

    @Override
    public void renderSlotUnderlay(GuiContainer gui, Slot slot) {}

    @Override
    public void renderSlotOverlay(GuiContainer gui, Slot slot) {}

    @Override
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int width, int height) {
        ToolboxInventoryOverlay overlay = overlay(gui);
        if (overlay == null) return false;
        overlay.syncLayout();
        return overlay.bounds()
            .intersects(x, y, width, height);
    }

    @Override
    public ItemStack getStackUnderMouse(GuiContainer gui, int mouseX, int mouseY) {
        ToolboxInventoryOverlay overlay = overlay(gui);
        VirtualMESlot slot = overlay == null ? null : overlay.hoveredSlot(mouseX, mouseY);
        IAEStack<?> stack = slot == null ? null : slot.getAEStack();

        return stack == null ? null : stack.getItemStackForNEI();
    }

    @Override
    public boolean objectUnderMouse(GuiContainer gui, int mouseX, int mouseY) {
        ToolboxInventoryOverlay overlay = overlay(gui);
        if (overlay == null) return false;
        overlay.syncLayout();
        return overlay.bounds()
            .contains(mouseX, mouseY);
    }

    @Override
    public List<String> handleTooltip(GuiContainer gui, int mouseX, int mouseY, List<String> currentTooltip) {
        ToolboxInventoryOverlay overlay = overlay(gui);
        if (overlay == null || !overlay.isOnline()) return currentTooltip;
        VirtualMESlot slot = overlay.hoveredSlot(mouseX, mouseY);
        // NEI skips item tooltips while carrying a stack; AE's container interaction hints must still be shown.
        if (slot != null) slot.addTooltip(currentTooltip);
        return currentTooltip;
    }

    @Override
    public List<String> handleItemTooltip(GuiContainer gui, ItemStack stack, int mouseX, int mouseY,
        List<String> currentTooltip) {
        ToolboxInventoryOverlay overlay = overlay(gui);
        if (overlay == null || stack == null) return currentTooltip;
        VirtualMESlot slot = overlay.hoveredSlot(mouseX, mouseY);
        if (slot == null || slot.getAEStack() == null) return currentTooltip;
        if (!overlay.isOnline()) currentTooltip.add(StatCollector.translateToLocal("gtnl.metoolbox.unknown"));
        if (slot instanceof VirtualMEPinSlot)
            currentTooltip.add(StatCollector.translateToLocal("gtnl.metoolbox.unpin"));
        return currentTooltip;
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
    public void beforeSlotClick(GuiContainer gui, int slotIndex, int button, Slot slot, int modifier) {}

    @Override
    public boolean handleSlotClick(GuiContainer gui, int slotIndex, int button, Slot slot, int modifier,
        boolean eventconsumed) {

        return eventconsumed || overlay(gui) != null && ToolboxClient.shiftClickToME(slot, button, modifier);
    }

    @Override
    public void afterSlotClick(GuiContainer gui, int slotIndex, int button, Slot slot, int modifier) {}

    @Override
    public void guiTick(GuiContainer gui) {

        refreshHoveredSlot(gui);
    }

    @Override
    public void refresh(GuiContainer gui) {}

    @Override
    public void load(GuiContainer gui) {}
}
