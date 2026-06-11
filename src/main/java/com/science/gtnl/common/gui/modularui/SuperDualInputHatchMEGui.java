package com.science.gtnl.common.gui.modularui;

import static gregtech.api.util.GTUtility.translate;

import java.text.DecimalFormat;
import java.text.MessageFormat;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.utils.MouseData;
import com.cleanroommc.modularui.utils.fluid.FluidInteractions;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.PageButton;
import com.cleanroommc.modularui.widgets.PagedWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.SuperDualInputHatchME;

import appeng.core.localization.WailaText;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.util.StockingSlot;

public class SuperDualInputHatchMEGui {

    private static final String ITEM_FILTER_INV_NAME = "gtnl_super_dual_input_item_filter_inv";
    private static final String ITEM_STOCK_INV_NAME = "gtnl_super_dual_input_item_stock_inv";
    private static final String FLUID_FILTER_INV_NAME = "gtnl_super_dual_input_fluid_filter_inv";
    private static final String FLUID_STOCK_INV_NAME = "gtnl_super_dual_input_fluid_stock_inv";
    private static final String ITEM_SLOT_SIZE_PANEL_KEY_PREFIX = "gtnl_super_dual_input_item_slot_size_panel_";
    private static final String FLUID_SLOT_SIZE_PANEL_KEY_PREFIX = "gtnl_super_dual_input_fluid_slot_size_panel_";
    private static final String AUTO_PULL_SYNC_KEY = "autoPullItemList";
    private static final String MIN_ITEM_SYNC_KEY = "minAutoPullItemAmount";
    private static final String MIN_FLUID_SYNC_KEY = "minAutoPullFluidAmount";
    private static final String AUTO_PULL_REFRESH_SYNC_KEY = "autoPullRefreshTime";
    private static final String INT_MAX_SCALE_SYNC_KEY = "intMaxScale";
    private static final String EXPEDITE_RECIPE_SYNC_KEY = "expediteRecipeCheck";
    private static final String ACTIVE_SYNC_KEY = "isActive";
    private static final String POWERED_SYNC_KEY = "isPowered";
    private static final String BOOTING_SYNC_KEY = "isBooting";
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_COLUMNS = 10;
    private static final int VISIBLE_SLOT_ROWS = 4;
    private static final int FILTER_GRID_X = 7;
    private static final int GRID_Y = 9;
    private static final int STOCK_GRID_X = 205;
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###");
    private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#,###.00");

    private final SuperDualInputHatchME hatch;

    public SuperDualInputHatchMEGui(SuperDualInputHatchME hatch) {
        this.hatch = hatch;
    }

    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        hatch.updateAllInformationSlots();
        registerSyncValues(syncManager);

        ModularPanel panel = GTGuis.mteTemplatePanelBuilder(hatch, guiData, syncManager, uiSettings)
            .setWidth(hatch.getGUIWidth())
            .setHeight(hatch.getGUIHeight())
            .doesBindPlayerInventory(false)
            .doesAddGregTechLogo(false)
            .build();

        PagedWidget.Controller controller = new PagedWidget.Controller();
        panel.child(createPages(panel, syncManager, controller))
            .child(createPageButtons(controller))
            .child(createStatusText(syncManager))
            .child(createLogo());
        return panel;
    }

    private void registerSyncValues(PanelSyncManager syncManager) {
        syncManager.syncValue(
            AUTO_PULL_SYNC_KEY,
            new BooleanSyncValue(hatch::isAutoPullItemListForGui, hatch::setAutoPullItemList).allowC2S());
        syncManager.syncValue(
            MIN_ITEM_SYNC_KEY,
            new LongSyncValue(hatch::getMinAutoPullItemAmountForGui, hatch::setMinAutoPullItemAmountForGui).allowC2S());
        syncManager.syncValue(
            MIN_FLUID_SYNC_KEY,
            new LongSyncValue(hatch::getMinAutoPullFluidAmountForGui, hatch::setMinAutoPullFluidAmountForGui)
                .allowC2S());
        syncManager.syncValue(
            AUTO_PULL_REFRESH_SYNC_KEY,
            new IntSyncValue(hatch::getAutoPullRefreshTimeForGui, hatch::setAutoPullRefreshTimeForGui).allowC2S());
        syncManager.syncValue(
            INT_MAX_SCALE_SYNC_KEY,
            new IntSyncValue(hatch::getIntMaxScaleForGui, hatch::setIntMaxScaleForGui).allowC2S());
        syncManager.syncValue(
            EXPEDITE_RECIPE_SYNC_KEY,
            new BooleanSyncValue(hatch::doFastRecipeCheck, hatch::setRecipeCheck).allowC2S());
        syncManager.syncValue(ACTIVE_SYNC_KEY, new BooleanSyncValue(hatch::isActive));
        syncManager.syncValue(POWERED_SYNC_KEY, new BooleanSyncValue(hatch::isPowered));
        syncManager.syncValue(BOOTING_SYNC_KEY, new BooleanSyncValue(hatch::isBooting));
    }

    private PagedWidget<?> createPages(ModularPanel panel, PanelSyncManager syncManager,
        PagedWidget.Controller controller) {
        return new PagedWidget<>().controller(controller)
            .addPage(createItemPage(panel, syncManager))
            .addPage(createFluidPage(panel, syncManager))
            .addPage(createConfigPage(syncManager))
            .size(18 * 21 + 10, 72)
            .pos(0, 9);
    }

    private Widget<?> createPageButtons(PagedWidget.Controller controller) {
        ItemStack itemTab = ItemList.Hatch_Input_Bus_ME_Advanced.get(1);
        ItemStack fluidTab = ItemList.Hatch_Input_ME_Advanced.get(1);
        ItemStack configTab = GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Iron, 1);

        return Flow.row()
            .coverChildren()
            .child(
                new PageButton(0, controller).background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED)
                    .background(false, GTGuiTextures.BUTTON_STANDARD)
                    .overlay(new ItemDrawable(itemTab).asIcon())
                    .pos(hatch.getGUIWidth() - 4, 0)
                    .size(28, 28))
            .child(
                new PageButton(1, controller).background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED)
                    .background(false, GTGuiTextures.BUTTON_STANDARD)
                    .overlay(new ItemDrawable(fluidTab).asIcon())
                    .pos(hatch.getGUIWidth() - 4, 28)
                    .size(28, 28))
            .child(
                new PageButton(2, controller).background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED)
                    .background(false, GTGuiTextures.BUTTON_STANDARD)
                    .overlay(new ItemDrawable(configTab).asIcon())
                    .pos(hatch.getGUIWidth() - 4, 56)
                    .size(28, 28));
    }

    private Widget<?> createItemPage(ModularPanel parent, PanelSyncManager syncManager) {
        return new Grid().scrollable(new VerticalScrollData())
            .minColWidth(SLOT_SIZE)
            .minRowHeight(SLOT_SIZE)
            .size(SLOT_SIZE * 21 + 10, SLOT_SIZE * VISIBLE_SLOT_ROWS)
            .child(createItemFilterGrid(parent, syncManager))
            .child(createItemStockGrid())
            .child(
                GTGuiTextures.PICTURE_ARROW_DOUBLE.asWidget()
                    .size(12)
                    .pos(190, 30));
    }

    private Grid createItemFilterGrid(ModularPanel parent, PanelSyncManager syncManager) {
        BooleanSyncValue autoPullSyncer = syncManager.findSyncHandler(AUTO_PULL_SYNC_KEY, BooleanSyncValue.class);
        syncManager.registerSlotGroup(ITEM_FILTER_INV_NAME, getSlotRows());

        return createGridShell(FILTER_GRID_X).child(
            new Grid().coverChildren()
                .gridOfWidthHeight(
                    SLOT_COLUMNS,
                    getSlotRows(),
                    (x, y, index) -> createItemFilterSlot(parent, syncManager, autoPullSyncer, index)));
    }

    private ItemSlot createItemFilterSlot(ModularPanel parent, PanelSyncManager syncManager,
        BooleanSyncValue autoPullSyncer, int index) {
        IPanelHandler slotSizePanel = syncManager.syncedPanel(
            ITEM_SLOT_SIZE_PANEL_KEY_PREFIX + index,
            true,
            (manager, handler) -> createStoredStackSizePanel(
                ITEM_SLOT_SIZE_PANEL_KEY_PREFIX + index,
                parent,
                manager,
                index,
                "Info_SuperDualInputHatchME_00",
                () -> hatch.getStoredItemStackSizeForGui(index),
                value -> hatch.setStoredItemStackSizeForGui(index, value)));

        ModularSlot slot = new ModularSlot(hatch.getMui2FilterItemHandler(), index).slotGroup(ITEM_FILTER_INV_NAME)
            .filter(stack -> !autoPullSyncer.getBoolValue() && !hatch.containsFilterItemForGui(stack))
            .changeListener((newStack, onlyAmountChanged, client, init) -> {
                if (!client && !init) {
                    ItemStack stack = newStack == null ? null : GTUtility.copyAmount(1, newStack);
                    hatch.setFilterItemForGui(index, stack);
                }
            });

        return new StoredStackSizeSlot(autoPullSyncer, slotSizePanel).slot(slot);
    }

    private Grid createItemStockGrid() {
        return createGridShell(STOCK_GRID_X).child(
            new Grid().coverChildren()
                .gridOfWidthHeight(SLOT_COLUMNS, getSlotRows(), (x, y, index) -> new ItemSlot() {

                    @Override
                    public void buildTooltip(ItemStack stack, RichTooltip tooltip) {
                        super.buildTooltip(stack, tooltip);
                        long amount = hatch.getInformationItemAmountForGui(index);
                        if (amount >= 1000) {
                            tooltip.addLine(IKey.lang("modularui.amount", NUMBER_FORMAT.format(amount)));
                        }
                        if (amount > Integer.MAX_VALUE) {
                            tooltip.addLine(IKey.lang("Info_AdvancedSuperDualInputHatchME_ExceedIntMax"));
                            tooltip
                                .addLine(IKey.str(DOUBLE_FORMAT.format(amount * 1d / Integer.MAX_VALUE) + "*int.max"));
                        }
                    }
                }.background(GTGuiTextures.SLOT_ITEM_DARK)
                    .slot(
                        new ModularSlot(hatch.getMui2InformationItemHandler(), index).accessibility(false, false)
                            .slotGroup(ITEM_STOCK_INV_NAME))));
    }

    private Widget<?> createFluidPage(ModularPanel parent, PanelSyncManager syncManager) {
        return new Grid().scrollable(new VerticalScrollData())
            .minColWidth(SLOT_SIZE)
            .minRowHeight(SLOT_SIZE)
            .size(SLOT_SIZE * 21 + 10, SLOT_SIZE * VISIBLE_SLOT_ROWS)
            .child(createFluidFilterGrid(parent, syncManager))
            .child(createFluidStockGrid(syncManager))
            .child(
                GTGuiTextures.PICTURE_ARROW_DOUBLE.asWidget()
                    .size(12)
                    .pos(190, 30));
    }

    private Grid createFluidFilterGrid(ModularPanel parent, PanelSyncManager syncManager) {
        BooleanSyncValue autoPullSyncer = syncManager.findSyncHandler(AUTO_PULL_SYNC_KEY, BooleanSyncValue.class);
        syncManager.registerSlotGroup(FLUID_FILTER_INV_NAME, getSlotRows());

        return createGridShell(FILTER_GRID_X).child(
            new Grid().coverChildren()
                .gridOfWidthHeight(
                    SLOT_COLUMNS,
                    getSlotRows(),
                    (x, y, index) -> createFluidFilterSlot(parent, syncManager, autoPullSyncer, index)));
    }

    private FluidSlot createFluidFilterSlot(ModularPanel parent, PanelSyncManager syncManager,
        BooleanSyncValue autoPullSyncer, int index) {
        IPanelHandler slotSizePanel = syncManager.syncedPanel(
            FLUID_SLOT_SIZE_PANEL_KEY_PREFIX + index,
            true,
            (manager, handler) -> createStoredStackSizePanel(
                FLUID_SLOT_SIZE_PANEL_KEY_PREFIX + index,
                parent,
                manager,
                index,
                "Info_SuperDualInputHatchME_01",
                () -> hatch.getStoredFluidStackSizeForGui(index),
                value -> hatch.setStoredFluidStackSizeForGui(index, value)));

        return new StoredStackSizeFluidSlot(slotSizePanel, false) {

            @Override
            protected void addToolTip(RichTooltip tooltip) {
                FluidStack fluid = getFluidStack();
                if (fluid != null) {
                    tooltip.addFromFluid(fluid);
                    if (!autoPullSyncer.getBoolValue()) {
                        tooltip.addLine(IKey.lang("modularui2.fluid.phantom.clear"));
                    }
                } else {
                    tooltip.addLine(IKey.lang("modularui2.fluid.empty"));
                }

                if (autoPullSyncer.getBoolValue()) {
                    tooltip.addLine(IKey.lang("GT5U.machines.stocking_bus.cannot_set_slot"));
                }
            }
        }.syncHandler(new FluidSlotSyncHandler(new ConfigFluidTank(index)) {

            @Override
            public void tryScrollPhantom(MouseData mouseData) {}

            @Override
            protected void tryClickPhantom(MouseData mouseData, ItemStack cursorStack) {
                if (mouseData.mouseButton != 0 || autoPullSyncer.getBoolValue()) return;

                FluidStack heldFluid = FluidInteractions.getFluidForItem(cursorStack);
                if (heldFluid != null && hatch.containsFilterFluidForGui(heldFluid)) return;

                hatch.setFilterFluidForGui(index, heldFluid == null ? null : GTUtility.copyAmount(1, heldFluid));
            }
        }.phantom(true)
            .controlsAmount(false))
            .backgroundOverlay(
                new DynamicDrawable(
                    () -> autoPullSyncer.getBoolValue() ? GTGuiTextures.SLOT_FLUID_DARK
                        : GTGuiTextures.SLOT_FLUID_STANDARD),
                GTGuiTextures.OVERLAY_SLOT_ARROW_ME);
    }

    private Grid createFluidStockGrid(PanelSyncManager syncManager) {
        syncManager.registerSlotGroup(FLUID_STOCK_INV_NAME, getSlotRows());

        return createGridShell(STOCK_GRID_X).child(
            new Grid().coverChildren()
                .gridOfWidthHeight(SLOT_COLUMNS, getSlotRows(), (x, y, index) -> new FluidSlot() {

                    @Override
                    protected void addToolTip(RichTooltip tooltip) {
                        FluidStack fluid = getFluidStack();
                        if (fluid != null) {
                            tooltip.addFromFluid(fluid);
                            long amount = hatch.getInformationFluidAmountForGui(index);
                            tooltip.addLine(
                                IKey.lang(
                                    "modularui2.fluid.phantom.amount",
                                    NUMBER_FORMAT.format(amount),
                                    getBaseUnit()));
                            if (amount > Integer.MAX_VALUE) {
                                tooltip.addLine(IKey.lang("Info_AdvancedSuperDualInputHatchME_ExceedIntMax"));
                                tooltip.addLine(
                                    IKey.str(DOUBLE_FORMAT.format(amount * 1d / Integer.MAX_VALUE) + "*int.max"));
                            }
                            addAdditionalFluidInfo(tooltip, fluid);
                            if (!Interactable.hasShiftDown()) {
                                tooltip.addLine(IKey.lang("modularui2.tooltip.shift"));
                            }
                        } else {
                            tooltip.addLine(IKey.lang("modularui2.fluid.empty"));
                        }
                    }
                }.syncHandler(new FluidSlotSyncHandler(new InformationFluidTank(index)) {

                    @Override
                    protected void tryClickPhantom(MouseData mouseData, ItemStack cursorStack) {}

                    @Override
                    public void tryScrollPhantom(MouseData mouseData) {}
                }.phantom(true))
                    .background(GTGuiTextures.SLOT_FLUID_DARK)));
    }

    private Grid createGridShell(int x) {
        return new Grid().scrollable(new VerticalScrollData())
            .minColWidth(SLOT_SIZE)
            .minRowHeight(SLOT_SIZE)
            .size(SLOT_SIZE * SLOT_COLUMNS + 4, SLOT_SIZE * VISIBLE_SLOT_ROWS)
            .pos(x, GRID_Y);
    }

    private int getSlotRows() {
        return hatch.getDualSlotCountForGui() / SLOT_COLUMNS;
    }

    private Widget<?> createConfigPage(PanelSyncManager syncManager) {
        LongSyncValue minItemSyncer = syncManager.findSyncHandler(MIN_ITEM_SYNC_KEY, LongSyncValue.class);
        LongSyncValue minFluidSyncer = syncManager.findSyncHandler(MIN_FLUID_SYNC_KEY, LongSyncValue.class);
        IntSyncValue refreshSyncer = syncManager.findSyncHandler(AUTO_PULL_REFRESH_SYNC_KEY, IntSyncValue.class);
        IntSyncValue intMaxSyncer = syncManager.findSyncHandler(INT_MAX_SCALE_SYNC_KEY, IntSyncValue.class);
        BooleanSyncValue autoPullSyncer = syncManager.findSyncHandler(AUTO_PULL_SYNC_KEY, BooleanSyncValue.class);
        BooleanSyncValue recipeCheckSyncer = syncManager
            .findSyncHandler(EXPEDITE_RECIPE_SYNC_KEY, BooleanSyncValue.class);

        return new Grid().coverChildren()
            .pos(3, 3)
            .child(
                createConfigField("GT5U.machines.stocking_bus.refresh_time", createIntegerField(refreshSyncer), 0, 0))
            .child(
                createConfigField("Info_AdvancedSuperDualInputHatchME_IntMax", createIntegerField(intMaxSyncer), 0, 40))
            .child(createConfigField("Info_SuperDualInputHatchME_03", createLongField(minItemSyncer), 77, 0))
            .child(createConfigField("Info_SuperDualInputHatchME_04", createLongField(minFluidSyncer), 77, 40))
            .child(createAutoPullButton(autoPullSyncer))
            .child(createRecipeCheckRow(recipeCheckSyncer));
    }

    private Widget<?> createConfigField(String labelKey, TextFieldWidget field, int x, int y) {
        return Flow.row()
            .coverChildren()
            .pos(x, y)
            .child(field.size(74, 18))
            .child(
                IKey.lang(labelKey)
                    .asWidget()
                    .pos(0, 19)
                    .size(74, 14)
                    .addTooltipLine(
                        labelKey.equals("Info_AdvancedSuperDualInputHatchME_IntMax")
                            ? translate("Info_AdvancedSuperDualInputHatchME_IntMaxTooltip")
                            : ""));
    }

    private Widget<?> createAutoPullButton(BooleanSyncValue autoPullSyncer) {
        return new ToggleButton().value(autoPullSyncer)
            .size(16, 16)
            .pos(157, 4)
            .background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED)
            .background(false, GTGuiTextures.BUTTON_STANDARD)
            .overlay(true, GTGuiTextures.OVERLAY_BUTTON_AUTOPULL_ME)
            .overlay(false, GTGuiTextures.OVERLAY_BUTTON_AUTOPULL_ME_DISABLED)
            .setEnabledIf(button -> hatch.allowAuto)
            .addTooltipLine(translate("GT5U.machines.stocking_bus.auto_pull.tooltip.1"));
    }

    private Flow createRecipeCheckRow(BooleanSyncValue recipeCheckSyncer) {
        return Flow.row()
            .coverChildren()
            .childPadding(4)
            .pos(157, 44)
            .child(
                new ToggleButton().value(recipeCheckSyncer)
                    .size(16)
                    .background(true, GTGuiTextures.BUTTON_STANDARD)
                    .background(false, GTGuiTextures.BUTTON_STANDARD)
                    .overlay(true, GTGuiTextures.OVERLAY_BUTTON_CHECKMARK)
                    .overlay(false, GTGuiTextures.OVERLAY_BUTTON_CROSS)
                    .addTooltipLine(translate("GT5U.machines.stocking_bus.hatch_warning")))
            .child(
                IKey.lang("GT5U.machines.stocking_bus.force_check")
                    .asWidget()
                    .maxWidth(50));
    }

    private ModularPanel createStoredStackSizePanel(String key, ModularPanel parent, PanelSyncManager syncManager,
        int slot, String titleKey, LongGetter getter, LongSetter setter) {
        LongSyncValue stackSizeSyncer = new LongSyncValue(getter::get, setter::set).allowC2S();
        syncManager.syncValue(key + "_value", stackSizeSyncer);

        Flow mainColumn = Flow.column()
            .coverChildren()
            .marginTop(15)
            .childPadding(3)
            .child(
                IKey.lang(titleKey)
                    .asWidget())
            .child(
                IKey.str(translate("Info_SuperDualInputHatchME_02") + slot)
                    .asWidget()
                    .maxWidth(106))
            .child(
                createLongField(stackSizeSyncer).setScrollValues(1, 100_000, 1_000_000)
                    .size(106, 18));

        Dialog<?> panel = createDialog(key, parent);
        panel.size(110, 66)
            .padding(3)
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
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD);
    }

    private TextFieldWidget createLongField(LongSyncValue syncer) {
        return new TextFieldWidget().value(syncer)
            .numbersLong(() -> 1L, () -> Long.MAX_VALUE)
            .formatAsInteger(true)
            .setScrollValues(1, 4, 64)
            .setTextAlignment(Alignment.Center)
            .setTextColor(Color.WHITE.main)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD);
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
                String workState = translate(
                    hatch.isAllowedToWork() ? "GT5U.gui.text.enabled" : "GT5U.gui.text.disabled");
                return MessageFormat.format("{0}{1}§f ({2})", EnumChatFormatting.GREEN, state, workState);
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

    private class ConfigFluidTank implements IFluidTank {

        private final int slot;

        private ConfigFluidTank(int slot) {
            this.slot = slot;
        }

        @Override
        public FluidStack getFluid() {
            return hatch.getFilterFluidForGui(slot);
        }

        @Override
        public int getFluidAmount() {
            FluidStack fluid = getFluid();
            return fluid == null ? 0 : fluid.amount;
        }

        @Override
        public int getCapacity() {
            return 1;
        }

        @Override
        public FluidTankInfo getInfo() {
            return new FluidTankInfo(this);
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (resource == null) return 0;
            if (doFill) {
                hatch.setFilterFluidForGui(slot, GTUtility.copyAmount(1, resource));
            }
            return 1;
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            FluidStack fluid = getFluid();
            if (fluid != null && doDrain) {
                hatch.setFilterFluidForGui(slot, null);
            }
            return fluid;
        }
    }

    private class InformationFluidTank implements IFluidTank {

        private final int slot;

        private InformationFluidTank(int slot) {
            this.slot = slot;
        }

        @Override
        public FluidStack getFluid() {
            FluidStack fluid = hatch.getInformationFluidForGui(slot);
            if (fluid == null) {
                return null;
            }
            return GTUtility
                .copyAmount((int) Math.min(Integer.MAX_VALUE, hatch.getInformationFluidAmountForGui(slot)), fluid);
        }

        @Override
        public int getFluidAmount() {
            FluidStack fluid = getFluid();
            return fluid == null ? 0 : fluid.amount;
        }

        @Override
        public int getCapacity() {
            return Integer.MAX_VALUE;
        }

        @Override
        public FluidTankInfo getInfo() {
            return new FluidTankInfo(this);
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            return 0;
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return null;
        }
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

    public static class StoredStackSizeFluidSlot extends FluidSlot {

        private final IPanelHandler stackSizePanel;
        private final boolean displayMiddleClickTooltip;

        public StoredStackSizeFluidSlot(IPanelHandler stackSizePanel, boolean displayMiddleClickTooltip) {
            this.stackSizePanel = stackSizePanel;
            this.displayMiddleClickTooltip = displayMiddleClickTooltip;
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

        @Override
        protected void addToolTip(RichTooltip tooltip) {
            super.addToolTip(tooltip);
            if (displayMiddleClickTooltip) {
                tooltip.addLine(IKey.lang("Info_SuperInputHatchME_00"));
            }
        }
    }

    private interface LongGetter {

        long get();
    }

    private interface LongSetter {

        void set(long value);
    }
}
