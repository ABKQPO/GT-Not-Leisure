package com.science.gtnl.common.gui.modularui;

import static gregtech.api.util.GTUtility.translate;

import java.text.MessageFormat;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.UITexture;
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
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;
import gregtech.common.gui.modularui.util.StockingSlot;
import gregtech.common.modularui2.widget.builder.ItemSlotGridBuilder;

public class SuperInputBusMEGui extends MTEHatchBaseGui<SuperInputBusME> {

    public static final String FILTER_INV_NAME = "gtnl_super_input_bus_filter_inv";
    public static final String STOCK_INV_NAME = "gtnl_super_input_bus_stock_inv";
    public static final String MANUAL_INV_NAME = "gtnl_super_input_bus_manual_inv";
    public static final String CONFIG_PANEL_KEY = "gtnl_super_input_bus_config_panel";
    public static final String MANUAL_PANEL_KEY = "gtnl_super_input_bus_manual_panel";
    public static final String SLOT_SIZE_PANEL_KEY_PREFIX = "gtnl_super_input_bus_slot_size_panel_";
    public static final String AUTO_PULL_SYNC_KEY = "autoPullItemList";
    public static final String MIN_AUTO_PULL_SYNC_KEY = "minAutoPullStackSize";
    public static final String AUTO_PULL_REFRESH_SYNC_KEY = "autoPullRefreshTime";
    public static final String EXPEDITE_RECIPE_SYNC_KEY = "expediteRecipeCheck";
    public static final String ACTIVE_SYNC_KEY = "isActive";
    public static final String POWERED_SYNC_KEY = "isPowered";
    public static final String BOOTING_SYNC_KEY = "isBooting";
    public static final int SLOT_SIZE = 18;
    public static final int SLOT_COLUMNS = 10;
    public static final int VISIBLE_SLOT_ROWS = 4;
    public static final int FILTER_GRID_X = 7;
    public static final int FILTER_GRID_Y = 9;
    public static final int STOCK_GRID_X = 205;

    public SuperInputBusMEGui(SuperInputBusME hatch) {
        super(hatch);
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        registerSyncValues(syncManager);

        ModularPanel panel = GTGuis.mteTemplatePanelBuilder(machine, guiData, syncManager, uiSettings)
            .setWidth(machine.getGUIWidth())
            .setHeight(machine.getGUIHeight())
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

    @Override
    public void registerSyncValues(PanelSyncManager syncManager) {
        syncManager.syncValue(
            AUTO_PULL_SYNC_KEY,
            new BooleanSyncValue(machine::isAutoPullItemList, machine::setAutoPullItemList).allowC2S());
        syncManager.syncValue(
            MIN_AUTO_PULL_SYNC_KEY,
            new IntSyncValue(machine::getMinAutoPullStackSize, machine::setMinAutoPullStackSize).allowC2S());
        syncManager.syncValue(
            AUTO_PULL_REFRESH_SYNC_KEY,
            new IntSyncValue(machine::getAutoPullRefreshTime, machine::setAutoPullRefreshTime).allowC2S());
        syncManager.syncValue(
            EXPEDITE_RECIPE_SYNC_KEY,
            new BooleanSyncValue(machine::doFastRecipeCheck, machine::setRecipeCheck).allowC2S());
        syncManager.syncValue(ACTIVE_SYNC_KEY, new BooleanSyncValue(machine::isActive));
        syncManager.syncValue(POWERED_SYNC_KEY, new BooleanSyncValue(machine::isPowered));
        syncManager.syncValue(BOOTING_SYNC_KEY, new BooleanSyncValue(machine::isBooting));
    }

    public Grid createFilterGrid(ModularPanel parent, PanelSyncManager syncManager) {
        BooleanSyncValue autoPullSyncer = syncManager.findSyncHandler(AUTO_PULL_SYNC_KEY, BooleanSyncValue.class);
        syncManager.registerSlotGroup(FILTER_INV_NAME, getSlotRows());

        return createGridShell(FILTER_GRID_X).child(
            new Grid().coverChildren()
                .gridOfWidthHeight(
                    SLOT_COLUMNS,
                    getSlotRows(),
                    (x, y, index) -> createFilterSlot(parent, syncManager, autoPullSyncer, index)));
    }

    public ItemSlot createFilterSlot(ModularPanel parent, PanelSyncManager syncManager, BooleanSyncValue autoPullSyncer,
        int index) {
        IPanelHandler slotSizePanel = syncManager.syncedPanel(
            SLOT_SIZE_PANEL_KEY_PREFIX + index,
            true,
            (manager, handler) -> createStoredStackSizePanel(parent, manager, index));

        ModularSlot slot = new ModularSlot(machine.inventoryHandler, index).slotGroup(FILTER_INV_NAME)
            .filter(stack -> !autoPullSyncer.getBoolValue() && !machine.containsFilterStackForGui(stack))
            .changeListener((newStack, onlyAmountChanged, client, init) -> {
                if (!client && !init) {
                    ItemStack stack = newStack == null ? null : GTUtility.copyAmount(1, newStack);
                    machine.updateInformationSlotForGui(index, stack);
                }
            });

        return new StoredStackSizeSlot(autoPullSyncer, slotSizePanel).slot(slot);
    }

    public Grid createStockGrid(PanelSyncManager syncManager) {
        return createGridShell(STOCK_GRID_X).child(
            new ItemSlotGridBuilder(machine.inventoryHandler, syncManager).size(SLOT_COLUMNS, getSlotRows())
                .slotGroupKey(STOCK_INV_NAME)
                .indexOffset(machine.getStockSlotOffsetForGui())
                .accessibility(false, false)
                .itemSlotSupplier(() -> new ItemSlot().background(GTGuiTextures.SLOT_ITEM_DARK))
                .build());
    }

    public Grid createGridShell(int x) {
        return new Grid().scrollable(new VerticalScrollData())
            .showScrollShadows(false)
            .minColWidth(SLOT_SIZE)
            .minRowHeight(SLOT_SIZE)
            .size(SLOT_SIZE * SLOT_COLUMNS + 4, SLOT_SIZE * VISIBLE_SLOT_ROWS)
            .pos(x, FILTER_GRID_Y);
    }

    public int getSlotRows() {
        return machine.getFilterSlotCountForGui() / SLOT_COLUMNS;
    }

    public Widget<?> createAutoPullButton(ModularPanel parent, PanelSyncManager syncManager) {
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
            .setEnabledIf(button -> machine.autoPullAvailable)
            .addTooltipLine(translate("GT5U.machines.stocking_bus.auto_pull.tooltip.1"))
            .addTooltipLine(translate("GT5U.machines.stocking_bus.auto_pull.tooltip.2"));
    }

    public Widget<?> createManualSlotButton(ModularPanel parent, PanelSyncManager syncManager) {
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

    public ModularPanel createManualSlotPanel(ModularPanel parent, PanelSyncManager syncManager) {
        Dialog<?> panel = createDialog(MANUAL_PANEL_KEY, parent);
        panel.size(176, 86)
            .leftRel(1)
            .topRel(0);
        panel.child(ButtonWidget.panelCloseButton());
        panel.child(
            new Grid().scrollable(new VerticalScrollData())
                .showScrollShadows(false)
                .minColWidth(SLOT_SIZE)
                .minRowHeight(SLOT_SIZE)
                .size(SLOT_SIZE * 9 + 4, SLOT_SIZE * 4)
                .pos(7, 7)
                .child(
                    new ItemSlotGridBuilder(machine.inventoryHandler, syncManager).size(9, 9)
                        .slotGroupKey(MANUAL_INV_NAME)
                        .indexOffset(machine.getManualSlotStartForGui())
                        .build()));
        return panel;
    }

    public ModularPanel createStoredStackSizePanel(ModularPanel parent, PanelSyncManager syncManager, int slot) {
        IntSyncValue stackSizeSyncer = new IntSyncValue(
            () -> machine.getStoredStackSizeForGui(slot),
            value -> machine.setStoredStackSizeForGui(slot, value)).allowC2S();
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

    public ModularPanel createStackSizeConfigurationPanel(ModularPanel parent, PanelSyncManager syncManager) {
        IntSyncValue minStackSyncer = new IntSyncValue(
            machine::getMinAutoPullStackSize,
            machine::setMinAutoPullStackSize).allowC2S();
        IntSyncValue refreshSyncer = new IntSyncValue(machine::getAutoPullRefreshTime, machine::setAutoPullRefreshTime)
            .allowC2S();
        BooleanSyncValue recipeCheckSyncer = new BooleanSyncValue(machine::doFastRecipeCheck, machine::setRecipeCheck)
            .allowC2S();

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

    public Dialog<?> createDialog(String key, ModularPanel parent) {
        Dialog<?> panel = new Dialog<>(key, null);
        panel.relative(parent)
            .background(GTGuiTextures.BACKGROUND_POPUP_STANDARD);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true);
        return panel;
    }

    public TextFieldWidget createIntegerField(IntSyncValue syncer) {
        return new TextFieldWidget().value(syncer)
            .numbersInt(1, Integer.MAX_VALUE)
            .formatAsInteger(true)
            .scrollValues(1, 4, 64, 256)
            .setTextAlignment(Alignment.Center)
            .setTextColor(Color.WHITE.main)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
            .size(72, 18);
    }

    public Flow createRecipeCheckRow(BooleanSyncValue recipeCheckSyncer) {
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

    public TextWidget<?> createStatusText(PanelSyncManager syncManager) {
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

    @Override
    protected IDrawable.DrawableWidget createLogo() {
        return new IDrawable.DrawableWidget(getLogoTexture()).size(SLOT_SIZE)
            .pos(367, 81);
    }

    @Override
    protected UITexture getLogoTexture() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO;
    }

    public static class StoredStackSizeSlot extends StockingSlot {

        public final IPanelHandler stackSizePanel;

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
