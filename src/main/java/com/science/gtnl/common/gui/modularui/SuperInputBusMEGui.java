package com.science.gtnl.common.gui.modularui;

import static gregtech.api.util.GTUtility.translate;

import java.text.MessageFormat;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.SuperInputBusME;

import appeng.core.localization.WailaText;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.util.StockingSlot;
import gregtech.common.modularui2.widget.builder.ItemSlotGridBuilder;

public class SuperInputBusMEGui {

    private static final String FILTER_INV_NAME = "gtnl_super_input_bus_filter_inv";
    private static final String STOCK_INV_NAME = "gtnl_super_input_bus_stock_inv";
    private static final String MANUAL_INV_NAME = "gtnl_super_input_bus_manual_inv";
    private static final String CONFIG_PANEL_KEY = "gtnl_super_input_bus_config_panel";
    private static final String MANUAL_PANEL_KEY = "gtnl_super_input_bus_manual_panel";
    private static final String SLOT_SIZE_PANEL_KEY_PREFIX = "gtnl_super_input_bus_slot_size_panel_";
    private static final String AUTO_PULL_SYNC_KEY = "autoPullItemList";
    private static final String MIN_AUTO_PULL_SYNC_KEY = "minAutoPullStackSize";
    private static final String AUTO_PULL_REFRESH_SYNC_KEY = "autoPullRefreshTime";
    private static final String EXPEDITE_RECIPE_SYNC_KEY = "expediteRecipeCheck";
    private static final String ACTIVE_SYNC_KEY = "isActive";
    private static final String POWERED_SYNC_KEY = "isPowered";
    private static final String BOOTING_SYNC_KEY = "isBooting";
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_COLUMNS = 10;
    private static final int VISIBLE_SLOT_ROWS = 4;
    private static final int FILTER_GRID_X = 7;
    private static final int FILTER_GRID_Y = 9;
    private static final int STOCK_GRID_X = 205;

    private final SuperInputBusME hatch;

    public SuperInputBusMEGui(SuperInputBusME hatch) {
        this.hatch = hatch;
    }

    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        registerSyncValues(syncManager);

        ModularPanel panel = GTGuis.mteTemplatePanelBuilder(hatch, guiData, syncManager, uiSettings)
            .setWidth(hatch.getGUIWidth())
            .setHeight(hatch.getGUIHeight())
            .doesBindPlayerInventory(false)
            .doesAddGregTechLogo(false)
            .build();

        panel.child(createFilterGrid(panel, syncManager))
            .child(createStockGrid(syncManager))
            .child(
                GTGuiTextures.PICTURE_ARROW_DOUBLE.asWidget()
                    .size(12)
                    .pos(190, 30))
            .child(createAutoPullButton(panel, syncManager))
            .child(createManualSlotButton(panel, syncManager))
            .child(createStatusText(syncManager))
            .child(createLogo());
        return panel;
    }

    private void registerSyncValues(PanelSyncManager syncManager) {
        syncManager.syncValue(
            AUTO_PULL_SYNC_KEY,
            new BooleanSyncValue(hatch::isAutoPullItemList, hatch::setAutoPullItemList).allowC2S());
        syncManager.syncValue(
            MIN_AUTO_PULL_SYNC_KEY,
            new IntSyncValue(hatch::getMinAutoPullStackSize, hatch::setMinAutoPullStackSize).allowC2S());
        syncManager.syncValue(
            AUTO_PULL_REFRESH_SYNC_KEY,
            new IntSyncValue(hatch::getAutoPullRefreshTime, hatch::setAutoPullRefreshTime).allowC2S());
        syncManager.syncValue(
            EXPEDITE_RECIPE_SYNC_KEY,
            new BooleanSyncValue(hatch::doFastRecipeCheck, hatch::setRecipeCheck).allowC2S());
        syncManager.syncValue(ACTIVE_SYNC_KEY, new BooleanSyncValue(hatch::isActive));
        syncManager.syncValue(POWERED_SYNC_KEY, new BooleanSyncValue(hatch::isPowered));
        syncManager.syncValue(BOOTING_SYNC_KEY, new BooleanSyncValue(hatch::isBooting));
    }

    private Grid createFilterGrid(ModularPanel parent, PanelSyncManager syncManager) {
        BooleanSyncValue autoPullSyncer = syncManager.findSyncHandler(AUTO_PULL_SYNC_KEY, BooleanSyncValue.class);
        syncManager.registerSlotGroup(FILTER_INV_NAME, getSlotRows());

        return createGridShell(FILTER_GRID_X).child(
            new Grid().coverChildren()
                .gridOfWidthHeight(
                    SLOT_COLUMNS,
                    getSlotRows(),
                    (x, y, index) -> createFilterSlot(parent, syncManager, autoPullSyncer, index)));
    }

    private ItemSlot createFilterSlot(ModularPanel parent, PanelSyncManager syncManager,
        BooleanSyncValue autoPullSyncer, int index) {
        IPanelHandler slotSizePanel = syncManager.syncedPanel(
            SLOT_SIZE_PANEL_KEY_PREFIX + index,
            true,
            (manager, handler) -> createStoredStackSizePanel(parent, manager, index));

        ModularSlot slot = new ModularSlot(hatch.inventoryHandler, index).slotGroup(FILTER_INV_NAME)
            .filter(stack -> !autoPullSyncer.getBoolValue() && !hatch.containsFilterStackForGui(stack))
            .changeListener((newStack, onlyAmountChanged, client, init) -> {
                if (!client && !init) {
                    ItemStack stack = newStack == null ? null : GTUtility.copyAmount(1, newStack);
                    hatch.updateInformationSlotForGui(index, stack);
                }
            });

        return new StoredStackSizeSlot(autoPullSyncer, slotSizePanel).slot(slot);
    }

    private Grid createStockGrid(PanelSyncManager syncManager) {
        return createGridShell(STOCK_GRID_X).child(
            new ItemSlotGridBuilder(hatch.inventoryHandler, syncManager).size(SLOT_COLUMNS, getSlotRows())
                .slotGroupKey(STOCK_INV_NAME)
                .indexOffset(hatch.getStockSlotOffsetForGui())
                .accessibility(false, false)
                .itemSlotSupplier(() -> new ItemSlot().background(GTGuiTextures.SLOT_ITEM_DARK))
                .build());
    }

    private Grid createGridShell(int x) {
        return new Grid().scrollable(new VerticalScrollData())
            .minColWidth(SLOT_SIZE)
            .minRowHeight(SLOT_SIZE)
            .size(SLOT_SIZE * SLOT_COLUMNS + 4, SLOT_SIZE * VISIBLE_SLOT_ROWS)
            .pos(x, FILTER_GRID_Y);
    }

    private int getSlotRows() {
        return hatch.getFilterSlotCountForGui() / SLOT_COLUMNS;
    }

    private Widget<?> createAutoPullButton(ModularPanel parent, PanelSyncManager syncManager) {
        BooleanSyncValue autoPullSyncer = syncManager.findSyncHandler(AUTO_PULL_SYNC_KEY, BooleanSyncValue.class);
        IPanelHandler configPanel = syncManager.syncedPanel(
            CONFIG_PANEL_KEY,
            true,
            (manager, handler) -> createStackSizeConfigurationPanel(parent, manager));

        return new ToggleButton() {

            @Override
            public @NotNull Result onMousePressed(int mouseButton) {
                if (mouseButton == 0) {
                    next();
                    playClickSound();
                    return Result.SUCCESS;
                }
                if (mouseButton == 1) {
                    configPanel.togglePanel();
                    playClickSound();
                    return Result.SUCCESS;
                }
                return Result.IGNORE;
            }
        }.value(autoPullSyncer)
            .size(16, 16)
            .pos(188, 10)
            .background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED)
            .background(false, GTGuiTextures.BUTTON_STANDARD)
            .overlay(true, GTGuiTextures.OVERLAY_BUTTON_AUTOPULL_ME)
            .overlay(false, GTGuiTextures.OVERLAY_BUTTON_AUTOPULL_ME_DISABLED)
            .setEnabledIf(button -> hatch.autoPullAvailable)
            .addTooltipLine(translate("GT5U.machines.stocking_bus.auto_pull.tooltip.1"))
            .addTooltipLine(translate("GT5U.machines.stocking_bus.auto_pull.tooltip.2"));
    }

    private Widget<?> createManualSlotButton(ModularPanel parent, PanelSyncManager syncManager) {
        IPanelHandler manualPanel = syncManager
            .syncedPanel(MANUAL_PANEL_KEY, true, (manager, handler) -> createManualSlotPanel(parent, manager));

        return new ButtonWidget<>().background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_PLUS_LARGE)
            .size(16, 16)
            .pos(188, 46)
            .onMousePressed(mouseButton -> {
                manualPanel.togglePanel();
                return true;
            });
    }

    private ModularPanel createManualSlotPanel(ModularPanel parent, PanelSyncManager syncManager) {
        Dialog<?> panel = createDialog(MANUAL_PANEL_KEY, parent);
        panel.size(176, 86)
            .leftRel(1)
            .topRel(0);
        panel.child(ButtonWidget.panelCloseButton());
        panel.child(
            new Grid().scrollable(new VerticalScrollData())
                .minColWidth(SLOT_SIZE)
                .minRowHeight(SLOT_SIZE)
                .size(SLOT_SIZE * 9 + 4, SLOT_SIZE * 4)
                .pos(7, 7)
                .child(
                    new ItemSlotGridBuilder(hatch.inventoryHandler, syncManager).size(9, 9)
                        .slotGroupKey(MANUAL_INV_NAME)
                        .indexOffset(hatch.getManualSlotStartForGui())
                        .build()));
        return panel;
    }

    private ModularPanel createStoredStackSizePanel(ModularPanel parent, PanelSyncManager syncManager, int slot) {
        IntSyncValue stackSizeSyncer = new IntSyncValue(
            () -> hatch.getStoredStackSizeForGui(slot),
            value -> hatch.setStoredStackSizeForGui(slot, value)).allowC2S();
        syncManager.syncValue("storedStackSize" + slot, stackSizeSyncer);

        Flow mainColumn = Flow.column()
            .coverChildren()
            .marginTop(15)
            .childPadding(3)
            .child(
                IKey.lang("Info_SuperInputHatchME_00")
                    .asWidget())
            .child(
                IKey.str(translate("Info_SuperInputHatchME_01") + slot)
                    .asWidget()
                    .maxWidth(106))
            .child(createIntegerField(stackSizeSyncer).size(106, 18));

        Dialog<?> panel = createDialog(SLOT_SIZE_PANEL_KEY_PREFIX + slot, parent);
        panel.size(110, 66)
            .padding(3)
            .leftRel(1)
            .topRel(0);
        panel.child(ButtonWidget.panelCloseButton());
        panel.child(mainColumn);
        return panel;
    }

    private ModularPanel createStackSizeConfigurationPanel(ModularPanel parent, PanelSyncManager syncManager) {
        IntSyncValue minStackSyncer = syncManager.findSyncHandler(MIN_AUTO_PULL_SYNC_KEY, IntSyncValue.class);
        IntSyncValue refreshSyncer = syncManager.findSyncHandler(AUTO_PULL_REFRESH_SYNC_KEY, IntSyncValue.class);
        BooleanSyncValue recipeCheckSyncer = syncManager
            .findSyncHandler(EXPEDITE_RECIPE_SYNC_KEY, BooleanSyncValue.class);

        Flow mainColumn = Flow.column()
            .coverChildren()
            .marginTop(15)
            .childPadding(3)
            .child(
                IKey.lang("GT5U.machines.stocking_bus.min_stack_size")
                    .asWidget())
            .child(createIntegerField(minStackSyncer))
            .child(
                IKey.lang("GT5U.machines.stocking_bus.refresh_time")
                    .asWidget()
                    .maxWidth(72)
                    .textAlign(Alignment.Center))
            .child(createIntegerField(refreshSyncer))
            .child(createRecipeCheckRow(recipeCheckSyncer));

        Dialog<?> panel = createDialog(CONFIG_PANEL_KEY, parent);
        panel.coverChildren()
            .padding(5)
            .leftRel(1)
            .topRel(0);
        panel.child(ButtonWidget.panelCloseButton());
        panel.child(mainColumn);
        return panel;
    }

    private Dialog<?> createDialog(String key, ModularPanel parent) {
        Dialog<?> panel = new Dialog<>(key, null);
        panel.relative(parent)
            .background(GTGuiTextures.BACKGROUND_POPUP_STANDARD);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true);
        return panel;
    }

    private TextFieldWidget createIntegerField(IntSyncValue syncer) {
        return new TextFieldWidget().value(syncer)
            .numbersInt(1, Integer.MAX_VALUE)
            .formatAsInteger(true)
            .setScrollValues(1, 4, 64)
            .setTextAlignment(Alignment.Center)
            .setTextColor(Color.WHITE.main)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
            .size(72, 18);
    }

    private Flow createRecipeCheckRow(BooleanSyncValue recipeCheckSyncer) {
        return Flow.row()
            .coverChildren()
            .childPadding(4)
            .child(
                IKey.lang("GT5U.machines.stocking_bus.force_check")
                    .asWidget()
                    .maxWidth(50))
            .child(
                new ToggleButton().value(recipeCheckSyncer)
                    .size(16)
                    .background(true, GTGuiTextures.BUTTON_STANDARD)
                    .background(false, GTGuiTextures.BUTTON_STANDARD)
                    .overlay(true, GTGuiTextures.OVERLAY_BUTTON_CHECKMARK)
                    .overlay(false, GTGuiTextures.OVERLAY_BUTTON_CROSS)
                    .addTooltipLine(translate("GT5U.machines.stocking_bus.hatch_warning")));
    }

    private TextWidget<?> createStatusText(PanelSyncManager syncManager) {
        BooleanSyncValue activeSyncer = syncManager.findSyncHandler(ACTIVE_SYNC_KEY, BooleanSyncValue.class);
        BooleanSyncValue poweredSyncer = syncManager.findSyncHandler(POWERED_SYNC_KEY, BooleanSyncValue.class);
        BooleanSyncValue bootingSyncer = syncManager.findSyncHandler(BOOTING_SYNC_KEY, BooleanSyncValue.class);

        return IKey.dynamic(() -> {
            boolean active = activeSyncer.getBoolValue();
            boolean powered = poweredSyncer.getBoolValue();
            String state = WailaText.getPowerState(active, powered, bootingSyncer.getBoolValue());
            if (active && powered) {
                return MessageFormat.format("{0}{1}§f", EnumChatFormatting.GREEN, state);
            }
            return EnumChatFormatting.DARK_RED + state;
        })
            .asWidget()
            .pos(131, 84)
            .size(130, 9)
            .textAlign(Alignment.Center)
            .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE);
    }

    private Widget<?> createLogo() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO.asWidget()
            .size(18)
            .pos(367, 81);
    }

    public static class StoredStackSizeSlot extends StockingSlot {

        private final IPanelHandler stackSizePanel;

        public StoredStackSizeSlot(BooleanSyncValue isLocked, IPanelHandler stackSizePanel) {
            super(isLocked);
            this.stackSizePanel = stackSizePanel;
        }

        @Override
        public @NotNull Interactable.Result onMousePressed(int mouseButton) {
            if (mouseButton == 2) {
                stackSizePanel.togglePanel();
                return Interactable.Result.SUCCESS;
            }
            return super.onMousePressed(mouseButton);
        }

        @Override
        public boolean onMouseScroll(UpOrDown scrollDirection, int amount) {
            return false;
        }

    }
}
