package com.science.gtnl.common.gui;

import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.factory.inventory.InventoryTypes;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.science.gtnl.client.yogsothoth.YogTerminalScreenWidgets;

public class YogSothothTerminalGui {

    public static final String PANEL = "yog_sothoth_terminal_screen";
    public static final int COLUMNS = 9;
    public static final int ROWS = 4;
    public static final int CELL = 18;
    public static final int BORDER = 7;
    public static final int GRID_Y = 17;
    public static final int SCROLL_X = BORDER + COLUMNS * CELL + 3;
    public static final int INVENTORY_LABEL_Y = GRID_Y + ROWS * CELL + 4;
    public static final int INVENTORY_Y = INVENTORY_LABEL_Y + 11;
    public static final int WIDTH = SCROLL_X + 8 + BORDER;
    public static final int HEIGHT = INVENTORY_Y + 3 * CELL + 4 + CELL + BORDER;

    private final PlayerInventoryGuiData data;
    private final PanelSyncManager syncManager;

    public YogSothothTerminalGui(PlayerInventoryGuiData data, PanelSyncManager syncManager) {
        this.data = data;
        this.syncManager = syncManager;
    }

    public ModularPanel build() {
        if (data.getInventoryType() == InventoryTypes.PLAYER) syncManager.bindPlayerInventory(
            data.getPlayer(),
            (inventory, index) -> index == data.getSlotIndex()
                ? new ModularSlot(inventory, index).accessibility(false, false)
                : new ModularSlot(inventory, index));
        ModularPanel panel = new ModularPanel(PANEL).size(WIDTH, HEIGHT);
        boolean client = data.isClient();
        if (client) YogTerminalScreenWidgets.decorate(panel, data.getPlayer().inventory.currentItem);
        panel.child(
            SlotGroupWidget.playerInventory((index, slot) -> client ? YogTerminalScreenWidgets.slot(slot) : slot)
                .pos(BORDER, INVENTORY_Y));
        return panel;
    }
}
