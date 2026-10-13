package com.science.gtnl.client.metoolbox;

import java.awt.Point;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

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
import com.science.gtnl.common.metoolbox.ToolboxRequestPacket.DepositMode;
import com.science.gtnl.utils.enums.ModList;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.client.gui.slots.VirtualMEPinSlot;
import appeng.client.gui.slots.VirtualMESlot;
import appeng.integration.modules.NEI;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.SearchField;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class ToolboxBogoCompat {

    private ToolboxBogoCompat() {}

    public static void init() {
        if (ModList.InventoryBogoSorter.isModLoaded()) Native.initialize();
    }

    static boolean isTerminalScreen(GuiContainer gui) {
        return ModList.InventoryBogoSorter.isModLoaded() && Native.isTerminalScreen(gui);
    }

    private static ToolboxInventoryOverlay searchTarget(GuiContainer gui) {
        ToolboxInventoryOverlay overlay = ToolboxClient.overlay();
        if (overlay == null || gui != overlay.inventory()
            || Minecraft.getMinecraft().currentScreen != gui
            || !overlay.layout()
                .expanded())
            return null;
        return overlay;
    }

    public static boolean handleSearchKey(GuiContainer gui) {
        if (!ModList.InventoryBogoSorter.isModLoaded()) return false;
        ToolboxInventoryOverlay overlay = searchTarget(gui);
        return overlay != null && Native.handleSearchKey(gui, overlay);
    }

    public static boolean handleMiniInput(GuiContainer gui, boolean fromMouse) {
        return ModList.InventoryBogoSorter.isModLoaded() && Native.handleInput(gui, fromMouse);
    }

    public static boolean isOverMini(GuiScreen gui) {
        ToolboxInventoryOverlay overlay = ToolboxClient.overlay();
        Minecraft mc = Minecraft.getMinecraft();
        if (overlay == null || gui != overlay.inventory()
            || mc.currentScreen != gui
            || mc.displayWidth <= 0
            || mc.displayHeight <= 0) return false;
        Point mouse = GuiDraw.getMousePosition();
        return overlay.bounds()
            .contains(mouse.x, mouse.y);
    }

    public static boolean moveSingleItem(GuiContainer gui, boolean emptyOnly) {
        if (extractFromMini(gui, emptyOnly, true)) return true;
        return ToolboxClient.sendBogoInventory(gui.theSlot, DepositMode.ONE);
    }

    public static boolean moveAllItems(GuiContainer gui, boolean sameItemOnly) {
        if (extractFromMini(gui, false, false)) return true;
        return ToolboxClient.sendBogoInventory(gui.theSlot, sameItemOnly ? DepositMode.MATCHING : DepositMode.REGION);
    }

    private static boolean extractFromMini(GuiContainer gui, boolean emptyOnly, boolean single) {
        if (!isOverMini(gui)) return false;
        Point mouse = GuiDraw.getMousePosition();
        VirtualMESlot slot = ToolboxClient.overlay()
            .hoveredSlot(mouse.x, mouse.y);
        IAEStack<?> stack = slot == null ? null : slot.getAEStack();
        if (stack instanceof IAEItemStack) {
            int nativePin = slot instanceof VirtualMEPinSlot ? slot.getSlotIndex() : -1;
            ToolboxClient.sendBogoExtract(stack, nativePin, emptyOnly, single);
        }
        return true;
    }

    public static final class Native {

        private static final Native INSTANCE = new Native();

        private Native() {
            MinecraftForge.EVENT_BUS.register(this);
        }

        private static void initialize() {}

        private static boolean isTerminalScreen(GuiContainer gui) {
            return Ae2TerminalGuiDetector.resolveSearchTarget(gui) != null;
        }

        private static boolean handleSearchKey(GuiContainer gui, ToolboxInventoryOverlay overlay) {
            if (!TooltipFeatureConfig.isTerminalHoverSearchEnabled() || !Keyboard.getEventKeyState()
                || Keyboard.getEventKey() != BSKeybinds.ae2TerminalSearchKey.getKeyCode()
                || overlay.nativeSearchField()
                    .isFocused()
                || NEI.searchField != null && NEI.searchField.existsSearchField() && NEI.searchField.focused())
                return false;

            Point mouse = GuiDraw.getMousePosition();
            ItemStack hovered;
            if (overlay.bounds()
                .contains(mouse.x, mouse.y)) {
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
            ToolboxNEIAdapter.refreshHoveredSlot(gui);
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
        public void mouseInput(MouseInputEvent.Pre event) {
            updateKeys(event.gui);
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void keyboardInput(KeyboardInputEvent.Pre event) {
            updateKeys(event.gui);
        }

        private static void updateKeys(GuiScreen gui) {
            ToolboxInventoryOverlay overlay = ToolboxClient.overlay();

            if (overlay != null && gui == overlay.inventory()) {
                ToolboxNEIAdapter.refreshHoveredSlot(overlay.inventory());
                KeyBind.checkKeys(ClientEventHandler.getTicks());
            }
        }

        @SubscribeEvent
        public void beforeSort(BeforeSortEvent event) {
            Minecraft mc = Minecraft.getMinecraft();
            if (event.isFromKeybind() && isOverMini(mc.currentScreen)) event.setCanceled(true);
        }
    }
}
