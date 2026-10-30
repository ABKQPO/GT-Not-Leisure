package com.science.gtnl.compat.bogosorter;

import java.awt.Point;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import com.cleanroommc.bogosorter.ClientEventHandler;
import com.cleanroommc.bogosorter.api.BeforeSortEvent;
import com.cleanroommc.bogosorter.client.keybinds.KeyBind;
import com.cleanroommc.bogosorter.client.keybinds.control.BSKeybinds;
import com.cleanroommc.bogosorter.client.keybinds.control.BSKeybinds.KeybindDefinition;
import com.cleanroommc.bogosorter.common.config.ae2.TooltipFeatureConfig;
import com.cleanroommc.bogosorter.compat.ae2.Ae2TerminalGuiDetector;
import com.cleanroommc.modularui.api.event.KeyboardInputEvent;
import com.cleanroommc.modularui.api.event.MouseInputEvent;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;
import com.gtnllib.mod.ModList;
import com.science.gtnl.client.meminiterminal.MiniTerminalClient;
import com.science.gtnl.client.meminiterminal.MiniTerminalInventoryPanel;
import com.science.gtnl.client.meminiterminal.MiniTerminalNEIAdapter;
import com.science.gtnl.common.meminiterminal.MiniTerminalStackTypes;
import com.science.gtnl.common.packet.MiniTerminalRequestPacket.DepositMode;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.slots.VirtualMEPinSlot;
import appeng.client.gui.slots.VirtualMESlot;
import appeng.integration.modules.NEI;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.SearchField;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

public class MiniTerminalBogoCompat {

    public static boolean isTerminalScreen(GuiContainer gui) {
        return ModList.InventoryBogoSorter.isModLoaded() && Native.isTerminalScreen(gui);
    }

    private static MiniTerminalInventoryPanel searchTarget(GuiContainer gui) {
        MiniTerminalInventoryPanel overlay = MiniTerminalClient.overlay(gui);
        if (overlay == null || Minecraft.getMinecraft().currentScreen != gui
            || !overlay.layout()
                .expanded())
            return null;
        return overlay;
    }

    public static boolean handleSearchKey(GuiContainer gui) {
        if (!ModList.InventoryBogoSorter.isModLoaded()) return false;
        MiniTerminalInventoryPanel overlay = searchTarget(gui);
        return overlay != null && Native.handleSearchKey(gui, overlay);
    }

    public static boolean handleMiniInput(GuiContainer gui, boolean fromMouse) {
        return ModList.InventoryBogoSorter.isModLoaded() && !shouldPrioritizeContainerInteraction(gui)
            && Native.handleInput(gui, fromMouse);
    }

    public static boolean shouldPrioritizeContainerInteraction(GuiContainer gui) {
        MiniTerminalInventoryPanel overlay = searchTarget(gui);
        Minecraft mc = Minecraft.getMinecraft();
        if (overlay == null || mc.thePlayer == null) return false;
        ItemStack held = mc.thePlayer.inventory.getItemStack();
        if (!MiniTerminalInventoryPanel.Input.preferContainerInteraction(
            AEBaseGui.isCtrlKeyDown(),
            MiniTerminalStackTypes.containerType(held) != null,
            Keyboard.isKeyDown(Keyboard.KEY_SPACE))) return false;
        Point mouse = GuiDraw.getMousePosition();
        return overlay.hoveredSlot(mouse.x, mouse.y) != null;
    }

    public static boolean isOverMini(GuiScreen gui) {
        MiniTerminalInventoryPanel overlay = MiniTerminalClient.overlay(gui);
        Minecraft mc = Minecraft.getMinecraft();
        if (overlay == null || mc.currentScreen != gui || mc.displayWidth <= 0 || mc.displayHeight <= 0) return false;
        Point mouse = GuiDraw.getMousePosition();
        return overlay.isMouseOver(mouse.x, mouse.y);
    }

    public static boolean moveSingleItem(GuiContainer gui, boolean emptyOnly) {
        if (extractFromMini(gui, emptyOnly, true)) return true;
        return MiniTerminalClient.sendBogoInventory(gui.theSlot, DepositMode.ONE);
    }

    public static boolean moveAllItems(GuiContainer gui, boolean sameItemOnly) {
        if (extractFromMini(gui, false, false)) return true;
        return MiniTerminalClient
            .sendBogoInventory(gui.theSlot, sameItemOnly ? DepositMode.MATCHING : DepositMode.REGION);
    }

    private static boolean extractFromMini(GuiContainer gui, boolean emptyOnly, boolean single) {
        if (!isOverMini(gui)) return false;
        Point mouse = GuiDraw.getMousePosition();
        VirtualMESlot slot = MiniTerminalClient.overlay()
            .hoveredSlot(mouse.x, mouse.y);
        IAEStack<?> stack = slot == null ? null : slot.getAEStack();
        if (stack instanceof IAEItemStack) {
            int nativePin = slot instanceof VirtualMEPinSlot ? slot.getSlotIndex() : -1;
            MiniTerminalClient.sendBogoExtract(stack, nativePin, emptyOnly, single);
        }
        return true;
    }

    @EventBusSubscriber(side = Side.CLIENT, phase = Phase.PRE)
    public static class Native {

        @EventBusSubscriber.Condition
        public static boolean bogoSorterLoaded() {
            return ModList.InventoryBogoSorter.isModLoaded();
        }

        private static boolean isTerminalScreen(GuiContainer gui) {
            return Ae2TerminalGuiDetector.resolveSearchTarget(gui) != null;
        }

        private static boolean handleSearchKey(GuiContainer gui, MiniTerminalInventoryPanel overlay) {
            if (!TooltipFeatureConfig.isTerminalHoverSearchEnabled() || !Keyboard.getEventKeyState()
                || Keyboard.getEventKey() != BSKeybinds.ae2TerminalSearchKey.getKeyCode()
                || overlay.nativeSearchField()
                    .isFocused()
                || NEI.searchField != null && NEI.searchField.existsSearchField() && NEI.searchField.focused())
                return false;

            Point mouse = GuiDraw.getMousePosition();
            ItemStack hovered;
            if (overlay.isMouseOver(mouse.x, mouse.y)) {
                VirtualMESlot slot = overlay.hoveredSlot(mouse.x, mouse.y);

                if (slot == null || slot.getAEStack() == null) return true;
                hovered = slot.getAEStack()
                    .getItemStackForNEI();
            } else {
                hovered = GuiContainerManager.getStackMouseOver(gui);
            }
            if (hovered == null) return false;
            String displayName = hovered.getDisplayName();
            if (displayName == null || displayName.trim()
                .isEmpty()) return false;

            overlay.applySearch(SearchField.getEscapedSearchText(displayName));
            return true;
        }

        private static boolean handleInput(GuiContainer gui, boolean fromMouse) {
            MiniTerminalNEIAdapter.refreshHoveredSlot(gui);
            KeyBind.checkKeys(ClientEventHandler.getTicks());
            boolean handled = ClientEventHandler.handleInput(gui, fromMouse);

            return handled || pressed(BSKeybinds.MOVE_ALL)
                || pressed(BSKeybinds.MOVE_ALL_SAME)
                || pressed(BSKeybinds.MOVE_SINGLE)
                || pressed(BSKeybinds.MOVE_SINGLE_EMPTY)
                || pressed(BSKeybinds.THROW_ALL)
                || pressed(BSKeybinds.THROW_ALL_SAME);
        }

        private static boolean pressed(KeybindDefinition definition) {
            KeyBind key = BSKeybinds.getActiveKeyBind(definition);
            return key != null && key.isPressed();
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void mouseInput(MouseInputEvent.Pre event) {
            updateKeys(event.gui);
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void keyboardInput(KeyboardInputEvent.Pre event) {
            updateKeys(event.gui);
        }

        private static void updateKeys(GuiScreen gui) {
            MiniTerminalInventoryPanel overlay = MiniTerminalClient.overlay(gui);
            if (overlay != null) {
                MiniTerminalNEIAdapter.refreshHoveredSlot(overlay.inventory());
                KeyBind.checkKeys(ClientEventHandler.getTicks());
            }
        }

        @SubscribeEvent
        public static void beforeSort(BeforeSortEvent event) {
            Minecraft mc = Minecraft.getMinecraft();
            if (event.isFromKeybind() && isOverMini(mc.currentScreen)) event.setCanceled(true);
        }
    }
}
