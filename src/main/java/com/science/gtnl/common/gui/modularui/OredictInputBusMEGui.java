package com.science.gtnl.common.gui.modularui;

import static gregtech.api.util.GTUtility.translate;

import java.util.Objects;
import java.util.stream.Stream;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.OredictInputBusME;

import appeng.me.GridAccessException;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.util.GTDataUtils;
import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.hatch.MTEHatchInputBusMEGui;
import gregtech.common.gui.modularui.util.StockingSlot;
import gregtech.common.modularui2.widget.builder.ItemSlotGridBuilder;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gregtech.common.tileentities.machines.MTEHatchInputBusME.Slot;

public class OredictInputBusMEGui extends MTEHatchInputBusMEGui {

    protected static final String FILTER_INV_NAME = "gtnl_oredict_filter_inv";
    protected static final String STOCK_INV_NAME = "gtnl_oredict_stock_inv";
    protected static final String MANUAL_INV_NAME = "gtnl_oredict_manual_inv";
    protected static final String CONFIG_PANEL_KEY = "gtnl_oredict_config_panel";
    protected static final String MANUAL_PANEL_KEY = "gtnl_oredict_manual_panel";
    protected static final String AUTO_PULL_SYNC_KEY = "autoPullItemList";
    protected static final String MIN_AUTO_PULL_SYNC_KEY = "minAutoPullStackSize";
    protected static final String AUTO_PULL_REFRESH_SYNC_KEY = "autoPullRefreshTime";
    protected static final String EXPEDITE_RECIPE_SYNC_KEY = "expediteRecipeCheck";
    protected static final String ORE_DICT_SYNC_KEY = "oreDict";
    protected static final String ACTIVE_SYNC_KEY = "isActive";
    protected static final String POWERED_SYNC_KEY = "isPowered";
    protected static final String BOOTING_SYNC_KEY = "isBooting";
    protected static final int SLOT_SIZE = 18;
    protected static final int SLOT_COLUMNS = 10;
    protected static final int VISIBLE_SLOT_ROWS = 4;
    protected static final int FILTER_GRID_X = 7;
    protected static final int FILTER_GRID_Y = 9;
    protected static final int STOCK_GRID_X = 205;
    protected final OredictInputBusME oredictHatch;
    protected final Slot[] guiSlots;

    public OredictInputBusMEGui(OredictInputBusME hatch) {
        super(hatch, hatch.getSlotsForGui());
        this.oredictHatch = hatch;
        this.guiSlots = hatch.getSlotsForGui();
    }

    @Override
    protected int getBasePanelWidth() {
        return oredictHatch.isSuper ? 392 : super.getBasePanelWidth();
    }

    @Override
    protected int getBasePanelHeight() {
        return oredictHatch.isSuper ? 179 : super.getBasePanelHeight();
    }

    @Override
    public void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
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
        syncManager.syncValue(
            ORE_DICT_SYNC_KEY,
            new StringSyncValue(oredictHatch::getOreDictForGui, oredictHatch::setOreDict).allowC2S());
        syncManager.syncValue(ACTIVE_SYNC_KEY, new BooleanSyncValue(oredictHatch::isActive));
        syncManager.syncValue(POWERED_SYNC_KEY, new BooleanSyncValue(oredictHatch::isPowered));
        syncManager.syncValue(BOOTING_SYNC_KEY, new BooleanSyncValue(oredictHatch::isBooting));
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        if (oredictHatch.isSuper) {
            Flow mainRow = Flow.row()
                .coverChildren();
            mainRow.child(createFilterGrid(syncManager));
            mainRow.child(createMiddleColumn(syncManager, panel));
            mainRow.child(createStockGrid(syncManager));
            return getEmptyContent().child(mainRow);
        }

        BooleanSyncValue autoPullSyncer = syncManager.findSyncHandler(AUTO_PULL_SYNC_KEY, BooleanSyncValue.class);
        Flow mainRow = Flow.row()
            .coverChildren();
        mainRow.child(createLegacyFilterGrid(syncManager, autoPullSyncer));
        mainRow.child(createLegacyMiddleColumn(syncManager, panel, autoPullSyncer));
        mainRow.child(createLegacyStockGrid(syncManager));
        return getEmptyContent().child(mainRow);
    }

    protected Flow createMiddleColumn(PanelSyncManager syncManager, ModularPanel panel) {
        return Flow.column()
            .coverChildren()
            .childPadding(3)
            .mainAxisAlignment(Alignment.MainAxis.CENTER)
            .crossAxisAlignment(Alignment.CrossAxis.CENTER)
            .child(createAutoPullButton(panel, syncManager))
            .child(
                GTGuiTextures.PICTURE_ARROW_DOUBLE.asWidget()
                    .size(12))
            .child(createManualSlotButton(panel, syncManager))
            .child(createCircuitSlot(syncManager));
    }

    protected Grid createFilterGrid(PanelSyncManager syncManager) {
        BooleanSyncValue autoPullSyncer = syncManager.findSyncHandler(AUTO_PULL_SYNC_KEY, BooleanSyncValue.class);
        return createGridShell().child(
            new ItemSlotGridBuilder(machine.inventoryHandler, syncManager).size(SLOT_COLUMNS, getSlotRows())
                .slotGroupKey(FILTER_INV_NAME)
                .filter(stack -> !autoPullSyncer.getBoolValue() && !oredictHatch.containsFilterStackForGui(stack))
                .itemSlotSupplier(() -> new StockingSlot(autoPullSyncer))
                .modularSlotSupplier(
                    (handler, index) -> new ModularSlot(handler, index)
                        .changeListener((newStack, onlyAmountChanged, client, init) -> {
                            if (!client && !init) {
                                ItemStack stack = newStack == null ? null : GTUtility.copyAmount(1, newStack);
                                oredictHatch.updateInformationSlotForGui(index, stack);
                            }
                        }))
                .build());
    }

    protected Grid createLegacyFilterGrid(PanelSyncManager syncManager, BooleanSyncValue autoPullSyncer) {
        return new ItemSlotGridBuilder(createConfigItemHandler(), syncManager).size(4, 4)
            .slotGroupKey(FILTER_INV_NAME)
            .filter(stack -> doesNotContainStack(stack) && !autoPullSyncer.getBoolValue())
            .itemSlotSupplier(() -> new StockingSlot(autoPullSyncer))
            .build();
    }

    protected Grid createStockGrid(PanelSyncManager syncManager) {
        return createGridShell().child(
            new ItemSlotGridBuilder(machine.inventoryHandler, syncManager).size(SLOT_COLUMNS, getSlotRows())
                .slotGroupKey(STOCK_INV_NAME)
                .indexOffset(oredictHatch.getStockSlotOffsetForGui())
                .accessibility(false, false)
                .itemSlotSupplier(() -> new PhantomItemSlot() {

                    @Override
                    public @NotNull Result onMousePressed(int mouseButton) {
                        return Result.IGNORE;
                    }

                    @Override
                    public boolean onMouseScroll(com.cleanroommc.modularui.api.UpOrDown scrollDirection, int amount) {
                        return false;
                    }

                    @Override
                    public boolean handleDragAndDrop(@NotNull ItemStack draggedStack, int button) {
                        return false;
                    }
                }.backgroundOverlay(GTGuiTextures.SLOT_ITEM_DARK))
                .modularSlotSupplier((handler, index) -> new ModularSlot(handler, index))
                .build());
    }

    protected Grid createLegacyStockGrid(PanelSyncManager syncManager) {
        return new ItemSlotGridBuilder(createConfigItemHandler(), syncManager).size(4, 4)
            .slotGroupKey(STOCK_INV_NAME)
            .indexOffset(MTEHatchInputBusME.SLOT_COUNT)
            .accessibility(false, false)
            .itemSlotSupplier(() -> new ItemSlot().backgroundOverlay(GTGuiTextures.SLOT_ITEM_DARK))
            .build();
    }

    protected Grid createGridShell() {
        return new Grid().scrollable(new VerticalScrollData())
            .showScrollShadows(false)
            .minColWidth(SLOT_SIZE)
            .minRowHeight(SLOT_SIZE)
            .size(SLOT_SIZE * SLOT_COLUMNS + 4, SLOT_SIZE * VISIBLE_SLOT_ROWS);
    }

    protected int getSlotRows() {
        return oredictHatch.getFilterSlotCountForGui() / SLOT_COLUMNS;
    }

    protected Flow createLegacyMiddleColumn(PanelSyncManager syncManager, ModularPanel parent,
        BooleanSyncValue autoPullSyncer) {
        IPanelHandler configPanel = syncManager.syncedPanel(
            CONFIG_PANEL_KEY,
            true,
            (manager, handler) -> createStackSizeConfigurationPanel(parent, syncManager));

        return Flow.column()
            .coverChildren()
            .reverseLayout()
            .mainAxisAlignment(Alignment.MainAxis.END)
            .child(createCircuitSlot(syncManager))
            .child(
                new ItemSlot()
                    .slot(new ModularSlot(machine.inventoryHandler, oredictHatch.getManualSlot()).slotGroup("item_inv"))
                    .tooltip(t -> {
                        t.addLine(GTUtility.translate("GT5U.machines.stocking_bus.manual_slot.tooltip.1"));
                        t.addLine(
                            EnumChatFormatting.GRAY
                                + GTUtility.translate("GT5U.machines.stocking_bus.manual_slot.tooltip.2")
                                + EnumChatFormatting.RESET);
                    }))
            .child(
                GTGuiTextures.PICTURE_ARROW_DOUBLE.asWidget()
                    .size(12)
                    .margin(3))
            .child(new ToggleButton() {

                @Override
                public @NotNull Result onMousePressed(int mouseButton) {
                    switch (mouseButton) {
                        case 0:
                            if (oredictHatch.autoPullAvailable) {
                                next();
                                playClickSound();
                                return Result.SUCCESS;
                            }
                            return Result.IGNORE;
                        case 1:
                            if (configPanel.isPanelOpen()) {
                                configPanel.closePanel();
                            } else {
                                configPanel.openPanel();
                            }
                            playClickSound();
                            return Result.SUCCESS;
                        default:
                            return Result.IGNORE;
                    }
                }
            }.value(autoPullSyncer)
                .size(16)
                .margin(1)
                .overlay(true, GTGuiTextures.OVERLAY_BUTTON_AUTOPULL_ME)
                .overlay(false, GTGuiTextures.OVERLAY_BUTTON_AUTOPULL_ME_DISABLED)
                .addTooltipLine(translate("GT5U.machines.stocking_bus.auto_pull.tooltip.1"))
                .addTooltipLine(translate("GT5U.machines.stocking_bus.auto_pull.tooltip.2")));
    }

    protected IWidget createAutoPullButton(ModularPanel parent, PanelSyncManager syncManager) {
        BooleanSyncValue autoPullSyncer = syncManager.findSyncHandler(AUTO_PULL_SYNC_KEY, BooleanSyncValue.class);
        IPanelHandler configPanel = syncManager.syncedPanel(
            CONFIG_PANEL_KEY,
            true,
            (manager, handler) -> createStackSizeConfigurationPanel(parent, syncManager));

        return new ToggleButton() {

            @Override
            public @NotNull Result onMousePressed(int mouseButton) {
                if (mouseButton == 0) {
                    next();
                    playClickSound();
                    return Result.SUCCESS;
                }
                if (mouseButton == 1) {
                    if (configPanel.isPanelOpen()) {
                        configPanel.closePanel();
                    } else {
                        configPanel.openPanel();
                    }
                    playClickSound();
                    return Result.SUCCESS;
                }
                return Result.IGNORE;
            }
        }.value(autoPullSyncer)
            .size(16, 16)
            .background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED)
            .background(false, GTGuiTextures.BUTTON_STANDARD)
            .overlay(true, GTGuiTextures.OVERLAY_BUTTON_AUTOPULL_ME)
            .overlay(false, GTGuiTextures.OVERLAY_BUTTON_AUTOPULL_ME_DISABLED)
            .addTooltipLine(translate("GT5U.machines.stocking_bus.auto_pull.tooltip.1"))
            .addTooltipLine(translate("GT5U.machines.stocking_bus.auto_pull.tooltip.2"));
    }

    protected IWidget createManualSlotButton(ModularPanel parent, PanelSyncManager syncManager) {
        IPanelHandler manualPanel = syncManager
            .syncedPanel(MANUAL_PANEL_KEY, true, (manager, handler) -> createManualSlotPanel(parent, manager));

        return new ButtonWidget<>().background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_PLUS_LARGE)
            .size(16, 16)
            .onMousePressed(mouseButton -> {
                if (manualPanel.isPanelOpen()) {
                    manualPanel.closePanel();
                } else {
                    manualPanel.openPanel();
                }
                return true;
            });
    }

    protected ModularPanel createManualSlotPanel(ModularPanel parent, PanelSyncManager syncManager) {
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
                        .indexOffset(oredictHatch.getManualSlotStartForGui())
                        .build()));
        return panel;
    }

    protected ModularPanel createStackSizeConfigurationPanel(ModularPanel parent, PanelSyncManager syncManager) {
        IntSyncValue minStackSyncer = syncManager.findSyncHandler(MIN_AUTO_PULL_SYNC_KEY, IntSyncValue.class);
        IntSyncValue refreshSyncer = syncManager.findSyncHandler(AUTO_PULL_REFRESH_SYNC_KEY, IntSyncValue.class);
        BooleanSyncValue recipeCheckSyncer = syncManager
            .findSyncHandler(EXPEDITE_RECIPE_SYNC_KEY, BooleanSyncValue.class);
        StringSyncValue oreDictSyncer = syncManager.findSyncHandler(ORE_DICT_SYNC_KEY, StringSyncValue.class);

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
            .child(createRecipeCheckRow(recipeCheckSyncer))
            .child(
                IKey.lang("Info_OredictInputBusME_Oredict")
                    .asWidget()
                    .maxWidth(72)
                    .textAlign(Alignment.Center))
            .child(
                new TextFieldWidget().value(oreDictSyncer)
                    .setTextAlignment(Alignment.Center)
                    .setTextColor(Color.WHITE.main)
                    .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
                    .size(72, 18));

        Dialog<?> panel = createDialog(CONFIG_PANEL_KEY, parent);
        panel.coverChildren()
            .padding(5)
            .leftRel(1)
            .topRel(0);
        panel.child(ButtonWidget.panelCloseButton());
        panel.child(mainColumn);
        return panel;
    }

    protected Dialog<?> createDialog(String key, ModularPanel parent) {
        Dialog<?> panel = new Dialog<>(key, null);
        panel.relative(parent)
            .background(GTGuiTextures.BACKGROUND_POPUP_STANDARD);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true);
        return panel;
    }

    protected TextFieldWidget createIntegerField(IntSyncValue syncer) {
        return new TextFieldWidget().value(syncer)
            .numbersInt(1, Integer.MAX_VALUE)
            .formatAsInteger(true)
            .scrollValues(1, 4, 64, 256)
            .setTextAlignment(Alignment.Center)
            .setTextColor(Color.WHITE.main)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
            .size(72, 18);
    }

    protected Flow createRecipeCheckRow(BooleanSyncValue recipeCheckSyncer) {
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

    protected IItemHandlerModifiable createConfigItemHandler() {
        return new IItemHandlerModifiable() {

            @Override
            public int getSlots() {
                return MTEHatchInputBusME.SLOT_COUNT * 2;
            }

            @Override
            public @Nullable ItemStack getStackInSlot(int slotIndex) {
                boolean configSlot = slotIndex < MTEHatchInputBusME.SLOT_COUNT;
                int index = slotIndex % MTEHatchInputBusME.SLOT_COUNT;
                Slot slot = GTDataUtils.getIndexSafe(guiSlots, index);
                if (slot == null) {
                    return null;
                }
                return configSlot ? slot.config : GTUtility.copyAmountUnsafe(slot.extractedAmount, slot.extracted);
            }

            @Override
            public @Nullable ItemStack insertItem(int slot, @Nullable ItemStack stack, boolean simulate) {
                return null;
            }

            @Override
            public @Nullable ItemStack extractItem(int slot, int amount, boolean simulate) {
                return null;
            }

            @Override
            public int getSlotLimit(int slot) {
                return Integer.MAX_VALUE;
            }

            @Override
            public void setStackInSlot(int slotIndex, @Nullable ItemStack stack) {
                if (slotIndex >= MTEHatchInputBusME.SLOT_COUNT) {
                    return;
                }
                machine.setSlotConfig(slotIndex, GTUtility.copyAmount(1, stack));
                if (baseMetaTileEntity.isServerSide()) {
                    try {
                        machine.updateInformationSlot(slotIndex);
                    } catch (GridAccessException ignored) {}
                }
            }
        };
    }

    protected boolean doesNotContainStack(@Nullable ItemStack stack) {
        return Stream.of(guiSlots)
            .filter(Objects::nonNull)
            .noneMatch(slot -> GTUtility.areStacksEqual(slot.config, stack));
    }

    @Override
    protected IDrawable.DrawableWidget createLogo() {
        return new IDrawable.DrawableWidget(getLogoTexture()).size(SLOT_SIZE);
    }

    @Override
    protected UITexture getLogoTexture() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO;
    }
}
