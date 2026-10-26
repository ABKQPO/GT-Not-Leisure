package com.science.gtnl.client.meminiterminal;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.science.gtnl.common.meminiterminal.MiniTerminalStackTypes;

import appeng.api.storage.data.IAEStack;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.slots.VirtualMEPinSlot;
import appeng.client.gui.slots.VirtualMESlot;
import appeng.integration.modules.NEI;
import appeng.integration.modules.NEIHelpers.NEIGuiHandler;
import appeng.items.contents.PinList;
import appeng.util.item.AEItemStack;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerSlotClickHandler;

public final class MiniTerminalNEIAdapter extends NEIGuiHandler implements IContainerSlotClickHandler {

    public static void refreshHoveredSlot(GuiContainer gui) {
        if (MiniTerminalClient.overlay(gui) != null) {

            gui.theSlot = GuiContainerManager.getSlotMouseOver(gui);
        }
    }

    static boolean handleKeyTyped(GuiContainer gui, char character, int keyCode) {
        MiniTerminalInventoryPanel overlay = MiniTerminalClient.overlay(gui);
        if (overlay == null || Minecraft.getMinecraft().currentScreen != gui) return false;
        Point mouse = GuiDraw.getMousePosition();
        if (overlay.isMouseOver(mouse.x, mouse.y)) {
            VirtualMESlot slot = overlay.hoveredSlot(mouse.x, mouse.y);

            if (slot == null || slot.getAEStack() == null) return false;
        }
        refreshHoveredSlot(gui);

        boolean handled = GuiContainerManager.getManager(gui)
            .lastKeyTyped(keyCode, character);
        return handled || Minecraft.getMinecraft().currentScreen != gui;
    }

    static boolean handleDrop(GuiContainer gui, int button) {
        if (!NEIClientConfig.isEnabled() || NEIClientConfig.isHidden()) return false;
        Point mouse = GuiDraw.getMousePosition();
        ItemStack stack = NEI.instance == null ? null : NEI.instance.getDraggingPhantomItem();
        if (stack != null && pinDrop(gui, mouse.x, mouse.y, stack, button)) return true;
        if (stack == null) stack = NEIClientUtils.getHeldItem();
        return fillSearch(gui, mouse.x, mouse.y, stack);
    }

    static boolean dragNDrop(GuiContainer gui, int mouseX, int mouseY, ItemStack draggedStack, int button) {
        if (NEI.instance != null && NEI.instance.getDraggingPhantomItem() != null
            && pinDrop(gui, mouseX, mouseY, draggedStack, button)) return true;
        return fillSearch(gui, mouseX, mouseY, draggedStack);
    }

    private static boolean pinDrop(GuiContainer gui, int mouseX, int mouseY, ItemStack stack, int button) {
        MiniTerminalInventoryPanel overlay = MiniTerminalClient.overlay(gui);
        if (overlay == null || stack == null || stack.getItem() == null || button != 0 && button != 1) return false;
        VirtualMESlot slot = overlay.hoveredSlot(mouseX, mouseY);
        if (!(slot instanceof VirtualMEPinSlot)) return false;
        if (!overlay.isOnline() || slot.getSlotIndex() < PinList.PLAYER_OFFSET) return true;
        ItemStack dragged = stack.copy();
        dragged.stackSize = 1;
        IAEStack<?> target = MiniTerminalStackTypes.convertFromItem(dragged);
        if (target == null) {
            target = AEBaseGui.isCtrlKeyDown() ? MiniTerminalStackTypes.containerContents(dragged)
                : AEItemStack.create(dragged);
        }
        if (target != null) MiniTerminalClient.sendPin(slot.getSlotIndex(), target);
        return true;
    }

    private static boolean fillSearch(GuiContainer gui, int mouseX, int mouseY, ItemStack stack) {
        MiniTerminalInventoryPanel overlay = MiniTerminalClient.overlay(gui);
        if (overlay == null || stack == null || stack.getItem() == null) return false;
        if (!overlay.layout()
            .expanded() || overlay.getContext()
                .hasDraggable()
            || !overlay.isMouseOver(mouseX, mouseY)) return false;
        Rectangle bounds = overlay.bounds();
        if (!overlay.nativeSearchField()
            .isMouseIn(
                mouseX - bounds.x - MiniTerminalLayout.SEARCH_X,
                mouseY - bounds.y - MiniTerminalLayout.HEADER_Y))
            return false;

        overlay.applySearch(NEI.searchField.getEscapedSearchText(getItemStackText(stack)));
        return true;
    }

    static void drawMuiSlotTooltip(MiniTerminalInventoryPanel panel) {
        if (!panel.isActive()) return;
        int mouseX = panel.getContext()
            .getAbsMouseX();
        int mouseY = panel.getContext()
            .getAbsMouseY();
        VirtualMESlot slot = panel.hoveredSlot(mouseX, mouseY);
        if (slot == null
            || slot.getAEStack() == null && Minecraft.getMinecraft().thePlayer.inventory.getItemStack() == null) return;
        GuiContainerManager manager = GuiContainerManager.getManager(panel.inventory());
        NEIClientUtils.gl2DRenderContext(() -> manager.renderToolTips(mouseX, mouseY));
    }

    static ItemStack stackUnderMouse(MiniTerminalInventoryPanel overlay, int mouseX, int mouseY) {
        VirtualMESlot slot = overlay.hoveredSlot(mouseX, mouseY);
        IAEStack<?> stack = slot == null ? null : slot.getAEStack();

        return stack == null ? null : stack.getItemStackForNEI();
    }

    static List<String> handleTooltip(MiniTerminalInventoryPanel overlay, int mouseX, int mouseY,
        List<String> currentTooltip) {
        if (!overlay.isOnline()) return currentTooltip;
        VirtualMESlot slot = overlay.hoveredSlot(mouseX, mouseY);
        if (slot != null) slot.addTooltip(currentTooltip);
        return currentTooltip;
    }

    static List<String> handleItemTooltip(MiniTerminalInventoryPanel overlay, int mouseX, int mouseY,
        List<String> currentTooltip) {
        VirtualMESlot slot = overlay.hoveredSlot(mouseX, mouseY);
        if (slot == null || slot.getAEStack() == null) return currentTooltip;
        if (!overlay.isOnline()) currentTooltip.add(StatCollector.translateToLocal("gtnl.meminiterminal.unknown"));
        if (slot instanceof VirtualMEPinSlot)
            currentTooltip.add(StatCollector.translateToLocal("gtnl.meminiterminal.unpin"));
        return currentTooltip;
    }

    @Override
    public void beforeSlotClick(GuiContainer gui, int slotIndex, int button, Slot slot, int modifier) {}

    @Override
    public boolean handleSlotClick(GuiContainer gui, int slotIndex, int button, Slot slot, int modifier,
        boolean eventconsumed) {

        return eventconsumed
            || MiniTerminalClient.overlay(gui) != null && MiniTerminalClient.shiftClickToME(slot, button, modifier);
    }

    @Override
    public void afterSlotClick(GuiContainer gui, int slotIndex, int button, Slot slot, int modifier) {}
}
