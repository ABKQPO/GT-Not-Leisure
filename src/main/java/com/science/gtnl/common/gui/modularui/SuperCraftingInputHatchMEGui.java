package com.science.gtnl.common.gui.modularui;

import static gregtech.api.util.GTUtility.translate;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputHatchME;

import appeng.api.implementations.ICraftingPatternItem;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;
import gregtech.common.gui.modularui.util.PatternSlot;
import gregtech.common.modularui2.widget.builder.ItemSlotGridBuilder;

public class SuperCraftingInputHatchMEGui extends MTEHatchBaseGui<SuperCraftingInputHatchME> {

    private static final String PATTERN_INV_NAME = "gtnl_super_crafting_pattern_inv";
    private static final String MANUAL_ITEM_INV_NAME = "gtnl_super_crafting_manual_item_inv";
    private static final String MANUAL_PANEL_KEY = "gtnl_super_crafting_manual_panel";
    private static final String PATTERN_MANUAL_PANEL_KEY_PREFIX = "gtnl_super_crafting_pattern_manual_panel_";
    private static final String OPTIMIZER_SYNC_KEY = "gtnl_super_crafting_pattern_optimizer";
    private static final String SHOW_PATTERN_SYNC_KEY = "gtnl_super_crafting_show_pattern";
    private static final int PATTERN_SLOT_PER_ROW = 9;
    private static final int VISIBLE_PATTERN_ROWS = 3;
    private static final int VISIBLE_MANUAL_ROWS = 4;
    private static final int MANUAL_SLOT_ROW = 9;
    private static final int MANUAL_SLOT_PER_ROW = 9;
    private static final int PATTERN_MANUAL_SLOT_ROW = 3;
    private static final int PATTERN_MANUAL_SLOT_PER_ROW = 3;

    public SuperCraftingInputHatchMEGui(SuperCraftingInputHatchME hatch) {
        super(hatch);
    }

    @Override
    protected int getBasePanelWidth() {
        return machine.getGUIWidth();
    }

    @Override
    protected UITexture getLogoTexture() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO;
    }

    @Override
    protected boolean supportsFluidScreen() {
        return false;
    }

    @Override
    protected boolean supportsFluidIOColumn() {
        return false;
    }

    @Override
    protected boolean supportsMuffler() {
        return false;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            OPTIMIZER_SYNC_KEY,
            new BooleanSyncValue(machine::isPatternOptimizationDisabledForGui, machine::setPatternOptimizationDisabled)
                .allowC2S());
        syncManager.syncValue(
            SHOW_PATTERN_SYNC_KEY,
            new BooleanSyncValue(machine::isShowPatternForGui, machine::setShowPattern).allowC2S());
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createContentSection(panel, syncManager).child(createPatternGrid(panel, syncManager));
    }

    private Grid createPatternGrid(ModularPanel panel, PanelSyncManager syncManager) {
        syncManager.registerSlotGroup(PATTERN_INV_NAME, machine.getPatternRowsForGui());

        return new Grid().scrollable(new VerticalScrollData())
            .minColWidth(SLOT_SIZE)
            .minRowHeight(SLOT_SIZE)
            .size(SLOT_SIZE * PATTERN_SLOT_PER_ROW + 4, SLOT_SIZE * VISIBLE_PATTERN_ROWS)
            .pos(0, 0)
            .child(
                new Grid().coverChildren()
                    .gridOfWidthHeight(
                        PATTERN_SLOT_PER_ROW,
                        machine.getPatternRowsForGui(),
                        ($x, $y, index) -> createPatternSlot(panel, syncManager, index)));
    }

    private PatternManualSlot createPatternSlot(ModularPanel panel, PanelSyncManager syncManager, int index) {
        IPanelHandler manualPanel = syncManager.syncedPanel(
            PATTERN_MANUAL_PANEL_KEY_PREFIX + index,
            true,
            (manager, handler) -> createPatternManualPanel(panel, manager, index));

        PatternManualSlot patternSlot = new PatternManualSlot(manualPanel);
        patternSlot.slot(
            new ModularSlot(machine.inventoryHandler, index)
                .filter(stack -> stack.getItem() instanceof ICraftingPatternItem)
                .changeListener((newStack, onlyAmountChanged, client, init) -> {
                    if (!client && !init) {
                        machine.onPatternChange(index, newStack);
                    }
                })
                .slotGroup(PATTERN_INV_NAME));
        return patternSlot;
    }

    @Override
    protected Flow createBottomLeftCornerFlow(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createBottomLeftCornerFlow(panel, syncManager).child(createOptimizerButton(syncManager))
            .child(createShowPatternButton(syncManager))
            .child(createExportButton())
            .child(createDoublePatternButton())
            .child(createManualItemsButton(panel, syncManager));
    }

    private ToggleButton createOptimizerButton(PanelSyncManager syncManager) {
        BooleanSyncValue optimizerSyncer = syncManager.findSyncHandler(OPTIMIZER_SYNC_KEY, BooleanSyncValue.class);

        return new ToggleButton().value(optimizerSyncer)
            .background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED)
            .background(false, GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_PATTERN_OPTIMIZE)
            .addTooltip(true, translate("Button_Tooltip_SuperCraftingInputHatchME_02_01"))
            .addTooltip(false, translate("Button_Tooltip_SuperCraftingInputHatchME_02_00"));
    }

    private ToggleButton createShowPatternButton(PanelSyncManager syncManager) {
        BooleanSyncValue showPatternSyncer = syncManager.findSyncHandler(SHOW_PATTERN_SYNC_KEY, BooleanSyncValue.class);

        return new ToggleButton().value(showPatternSyncer)
            .background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED)
            .background(false, GTGuiTextures.BUTTON_STANDARD)
            .overlay(true, GTGuiTextures.OVERLAY_BUTTON_WHITELIST)
            .overlay(false, GTGuiTextures.OVERLAY_BUTTON_BLACKLIST)
            .addTooltip(true, translate("Info_ShowPattern_Enabled"))
            .addTooltip(false, translate("Info_ShowPattern_Disabled"));
    }

    private ButtonWidget<?> createExportButton() {
        return new ButtonWidget<>().syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
            if (!mouseData.isClient() && mouseData.mouseButton == 0) {
                machine.refundAll(false);
            }
        }))
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_EXPORT)
            .addTooltipLine(translate("Button_Tooltip_SuperCraftingInputHatchME_01"));
    }

    private ButtonWidget<?> createDoublePatternButton() {
        return new ButtonWidget<>().syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
            if (!mouseData.isClient()) {
                int value = mouseData.shift ? 1 : 0;
                if (mouseData.mouseButton == 1) {
                    value |= 0b10;
                }
                machine.doublePatterns(value);
            }
        }))
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_X2)
            .addTooltipLine(translate("gui.tooltips.appliedenergistics2.DoublePatterns"));
    }

    private ButtonWidget<?> createManualItemsButton(ModularPanel panel, PanelSyncManager syncManager) {
        IPanelHandler manualPanel = syncManager
            .syncedPanel(MANUAL_PANEL_KEY, true, (manager, handler) -> createManualSlotPanel(panel, manager));

        return new ButtonWidget<>().background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_PLUS_LARGE)
            .addTooltipLine(translate("Button_Tooltip_SuperCraftingInputHatchME_00"))
            .onMousePressed(mouseButton -> {
                if (mouseButton == 0) {
                    manualPanel.togglePanel();
                    return true;
                }
                return false;
            });
    }

    private ModularPanel createManualSlotPanel(ModularPanel parent, PanelSyncManager syncManager) {
        ModularPanel panel = createDialog(MANUAL_PANEL_KEY, parent).size(176, 86)
            .leftRel(1)
            .topRel(0);
        panel.child(ButtonWidget.panelCloseButton());
        panel.child(
            new Grid().scrollable(new VerticalScrollData())
                .minColWidth(SLOT_SIZE)
                .minRowHeight(SLOT_SIZE)
                .size(SLOT_SIZE * MANUAL_SLOT_PER_ROW + 4, SLOT_SIZE * VISIBLE_MANUAL_ROWS)
                .pos(7, 7)
                .child(
                    new ItemSlotGridBuilder(machine.inventoryHandler, syncManager)
                        .size(MANUAL_SLOT_PER_ROW, MANUAL_SLOT_ROW)
                        .slotGroupKey(MANUAL_ITEM_INV_NAME)
                        .indexOffset(machine.getManualSlotStartForGui())
                        .modularSlotSupplier(
                            (handler, index) -> new ModularSlot(handler, index)
                                .changeListener((stack, onlyAmountChanged, client, init) -> {
                                    if (!client && !init) {
                                        machine.resetCraftingInputRecipeMap();
                                    }
                                }))
                        .build()));
        return panel;
    }

    private ModularPanel createPatternManualPanel(ModularPanel parent, PanelSyncManager syncManager, int patternSlot) {
        String key = PATTERN_MANUAL_PANEL_KEY_PREFIX + patternSlot;
        ModularPanel panel = createDialog(key, parent).size(68, 68)
            .leftRel(1)
            .topRel(0);
        panel.child(ButtonWidget.panelCloseButton());
        panel.child(
            new ItemSlotGridBuilder(machine.inventoryHandler, syncManager)
                .size(PATTERN_MANUAL_SLOT_PER_ROW, PATTERN_MANUAL_SLOT_ROW)
                .slotGroupKey(key + "_inv")
                .indexOffset(machine.getPatternManualSlotStartForGui(patternSlot))
                .build()
                .pos(7, 7));
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

    private static class PatternManualSlot extends PatternSlot {

        private final IPanelHandler manualPanel;

        private PatternManualSlot(IPanelHandler manualPanel) {
            this.manualPanel = manualPanel;
        }

        @Override
        public @NotNull Interactable.Result onMousePressed(int mouseButton) {
            if (mouseButton == 2) {
                manualPanel.togglePanel();
                return Interactable.Result.SUCCESS;
            }
            return super.onMousePressed(mouseButton);
        }
    }
}
