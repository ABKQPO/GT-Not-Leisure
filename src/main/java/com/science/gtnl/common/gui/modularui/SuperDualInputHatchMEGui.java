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
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
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
import com.cleanroommc.modularui.widget.ParentWidget;
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
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;
import gregtech.common.gui.modularui.util.StockingSlot;

public class SuperDualInputHatchMEGui extends MTEHatchBaseGui<SuperDualInputHatchME> {

    public static final String ITEM_FILTER_INV_NAME = "gtnl_super_dual_input_item_filter_inv";
    public static final String ITEM_STOCK_INV_NAME = "gtnl_super_dual_input_item_stock_inv";
    public static final String FLUID_FILTER_INV_NAME = "gtnl_super_dual_input_fluid_filter_inv";
    public static final String FLUID_STOCK_INV_NAME = "gtnl_super_dual_input_fluid_stock_inv";
    public static final String ITEM_SLOT_SIZE_PANEL_KEY_PREFIX = "gtnl_super_dual_input_item_slot_size_panel_";
    public static final String FLUID_SLOT_SIZE_PANEL_KEY_PREFIX = "gtnl_super_dual_input_fluid_slot_size_panel_";
    public static final String AUTO_PULL_SYNC_KEY = "autoPullItemList";
    public static final String MIN_ITEM_SYNC_KEY = "minAutoPullItemAmount";
    public static final String MIN_FLUID_SYNC_KEY = "minAutoPullFluidAmount";
    public static final String AUTO_PULL_REFRESH_SYNC_KEY = "autoPullRefreshTime";
    public static final String INT_MAX_SCALE_SYNC_KEY = "intMaxScale";
    public static final String EXPEDITE_RECIPE_SYNC_KEY = "expediteRecipeCheck";
    public static final String ACTIVE_SYNC_KEY = "isActive";
    public static final String POWERED_SYNC_KEY = "isPowered";
    public static final String BOOTING_SYNC_KEY = "isBooting";
    public static final int SLOT_SIZE = 18;
    public static final int SLOT_COLUMNS = 10;
    public static final int VISIBLE_SLOT_ROWS = 4;
    public static final int FILTER_GRID_X = 7;
    public static final int GRID_Y = 0;
    public static final int STOCK_GRID_X = 205;
    public static final int PAGE_WIDTH = SLOT_SIZE * 21 + 10;
    public static final int PAGE_HEIGHT = SLOT_SIZE * VISIBLE_SLOT_ROWS;
    public static final int CENTER_CONTROL_X = 188;
    public static final int CENTER_CIRCUIT_X = 187;
    public static final int CENTER_CIRCUIT_Y = 54;
    public static final int CENTER_AUTOPULL_X = 1;
    public static final int CENTER_AUTOPULL_Y = 1;
    public static final int CENTER_ARROW_X = 190;
    public static final int CENTER_ARROW_Y = 21;
    public static final int CONFIG_FIELD_WIDTH = 84;
    public static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###");
    public static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#,###.00");

    public SuperDualInputHatchMEGui(SuperDualInputHatchME hatch) {
        super(hatch);
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        machine.updateAllInformationSlots();
        registerSyncValues(syncManager);

        ModularPanel panel = GTGuis.mteTemplatePanelBuilder(machine, guiData, syncManager, uiSettings)
            .setWidth(machine.getGUIWidth())
            .setHeight(machine.getGUIHeight())
            .doesAddGregTechLogo(false)
            .doesAddGhostCircuitSlot(false)
            .build();

        PagedWidget.Controller controller = new PagedWidget.Controller();
        panel.child(createPages(panel, syncManager, controller))
            .child(createPageButtons(controller))
            .child(createStatusText(syncManager))
            .child(createLogo());
        return panel;
    }

    @Override
    public void registerSyncValues(PanelSyncManager syncManager) {
        syncManager.syncValue(
            AUTO_PULL_SYNC_KEY,
            new BooleanSyncValue(machine::isAutoPullItemListForGui, machine::setAutoPullItemList).allowC2S());
        syncManager.syncValue(
            MIN_ITEM_SYNC_KEY,
            new LongSyncValue(machine::getMinAutoPullItemAmountForGui, machine::setMinAutoPullItemAmountForGui)
                .allowC2S());
        syncManager.syncValue(
            MIN_FLUID_SYNC_KEY,
            new LongSyncValue(machine::getMinAutoPullFluidAmountForGui, machine::setMinAutoPullFluidAmountForGui)
                .allowC2S());
        syncManager.syncValue(
            AUTO_PULL_REFRESH_SYNC_KEY,
            new IntSyncValue(machine::getAutoPullRefreshTimeForGui, machine::setAutoPullRefreshTimeForGui).allowC2S());
        syncManager.syncValue(
            INT_MAX_SCALE_SYNC_KEY,
            new IntSyncValue(machine::getIntMaxScaleForGui, machine::setIntMaxScaleForGui).allowC2S());
        syncManager.syncValue(
            EXPEDITE_RECIPE_SYNC_KEY,
            new BooleanSyncValue(machine::doFastRecipeCheck, machine::setRecipeCheck).allowC2S());
        syncManager.syncValue(ACTIVE_SYNC_KEY, new BooleanSyncValue(machine::isActive));
        syncManager.syncValue(POWERED_SYNC_KEY, new BooleanSyncValue(machine::isPowered));
        syncManager.syncValue(BOOTING_SYNC_KEY, new BooleanSyncValue(machine::isBooting));
    }

    public PagedWidget<?> createPages(ModularPanel panel, PanelSyncManager syncManager,
        PagedWidget.Controller controller) {
        return new PagedWidget<>().controller(controller)
            .addPage(createItemPage(panel, syncManager))
            .addPage(createFluidPage(panel, syncManager))
            .addPage(createConfigPage(syncManager))
            .size(PAGE_WIDTH, PAGE_HEIGHT)
            .pos(0, 9);
    }

    public Widget<?> createPageButtons(PagedWidget.Controller controller) {
        ItemStack itemTab = ItemList.Hatch_Input_Bus_ME_Advanced.get(1);
        ItemStack fluidTab = ItemList.Hatch_Input_ME_Advanced.get(1);
        ItemStack configTab = GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Iron, 1);

        return Flow.row()
            .coverChildren()
            .child(
                new PageButton(0, controller).background(true, createTabBackground(0, true))
                    .background(false, createTabBackground(0, false))
                    .overlay(new ItemDrawable(itemTab).asIcon())
                    .pos(machine.getGUIWidth() - 4, 0)
                    .size(28, 32))
            .child(
                new PageButton(1, controller).background(true, createTabBackground(1, true))
                    .background(false, createTabBackground(1, false))
                    .overlay(new ItemDrawable(fluidTab).asIcon())
                    .pos(machine.getGUIWidth() - 4, 28)
                    .size(28, 32))
            .child(
                new PageButton(2, controller).background(true, createTabBackground(2, true))
                    .background(false, createTabBackground(2, false))
                    .overlay(new ItemDrawable(configTab).asIcon())
                    .pos(machine.getGUIWidth() - 4, 56)
                    .size(28, 32));
    }

    public ParentWidget<?> createItemPage(ModularPanel parent, PanelSyncManager syncManager) {
        return new ParentWidget<>().size(PAGE_WIDTH, PAGE_HEIGHT)
            .child(createItemFilterGrid(parent, syncManager))
            .child(createItemStockGrid())
            .child(createCenterControls(syncManager));
    }

    public Grid createItemFilterGrid(ModularPanel parent, PanelSyncManager syncManager) {
        BooleanSyncValue autoPullSyncer = syncManager.findSyncHandler(AUTO_PULL_SYNC_KEY, BooleanSyncValue.class);
        syncManager.registerSlotGroup(ITEM_FILTER_INV_NAME, getSlotRows());

        return createGridShell(FILTER_GRID_X).child(
            new Grid().coverChildren()
                .gridOfWidthHeight(
                    SLOT_COLUMNS,
                    getSlotRows(),
                    (x, y, index) -> createItemFilterSlot(parent, syncManager, autoPullSyncer, index)));
    }

    public ItemSlot createItemFilterSlot(ModularPanel parent, PanelSyncManager syncManager,
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
                () -> machine.getStoredItemStackSizeForGui(index),
                value -> machine.setStoredItemStackSizeForGui(index, value)));

        ModularSlot slot = new ModularSlot(machine.getMui2FilterItemHandler(), index).slotGroup(ITEM_FILTER_INV_NAME)
            .filter(stack -> !autoPullSyncer.getBoolValue() && !machine.containsFilterItemForGui(stack))
            .changeListener((newStack, onlyAmountChanged, client, init) -> {
                if (!client && !init) {
                    ItemStack stack = newStack == null ? null : GTUtility.copyAmount(1, newStack);
                    machine.setFilterItemForGui(index, stack);
                }
            });

        return new StoredStackSizeSlot(autoPullSyncer, slotSizePanel).slot(slot);
    }

    public Grid createItemStockGrid() {
        return createGridShell(STOCK_GRID_X).child(
            new Grid().coverChildren()
                .gridOfWidthHeight(SLOT_COLUMNS, getSlotRows(), (x, y, index) -> new ItemSlot() {

                    @Override
                    public void buildTooltip(ItemStack stack, RichTooltip tooltip) {
                        super.buildTooltip(stack, tooltip);
                        long amount = machine.getInformationItemAmountForGui(index);
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
                        new ModularSlot(machine.getMui2InformationItemHandler(), index).accessibility(false, false)
                            .slotGroup(ITEM_STOCK_INV_NAME))));
    }

    public ParentWidget<?> createFluidPage(ModularPanel parent, PanelSyncManager syncManager) {
        return new ParentWidget<>().size(PAGE_WIDTH, PAGE_HEIGHT)
            .child(createFluidFilterGrid(parent, syncManager))
            .child(createFluidStockGrid(syncManager))
            .child(createCenterControls(syncManager));
    }

    public Grid createFluidFilterGrid(ModularPanel parent, PanelSyncManager syncManager) {
        BooleanSyncValue autoPullSyncer = syncManager.findSyncHandler(AUTO_PULL_SYNC_KEY, BooleanSyncValue.class);
        syncManager.registerSlotGroup(FLUID_FILTER_INV_NAME, getSlotRows());

        return createGridShell(FILTER_GRID_X).child(
            new Grid().coverChildren()
                .gridOfWidthHeight(
                    SLOT_COLUMNS,
                    getSlotRows(),
                    (x, y, index) -> createFluidFilterSlot(parent, syncManager, autoPullSyncer, index)));
    }

    public FluidSlot createFluidFilterSlot(ModularPanel parent, PanelSyncManager syncManager,
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
                () -> machine.getStoredFluidStackSizeForGui(index),
                value -> machine.setStoredFluidStackSizeForGui(index, value)));

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
                if (heldFluid != null && machine.containsFilterFluidForGui(heldFluid)) return;

                machine.setFilterFluidForGui(index, heldFluid == null ? null : GTUtility.copyAmount(1, heldFluid));
            }
        }.phantom(true)
            .controlsAmount(false))
            .backgroundOverlay(
                new DynamicDrawable(
                    () -> autoPullSyncer.getBoolValue() ? GTGuiTextures.SLOT_FLUID_DARK
                        : GTGuiTextures.SLOT_FLUID_STANDARD),
                GTGuiTextures.OVERLAY_SLOT_ARROW_ME);
    }

    public Grid createFluidStockGrid(PanelSyncManager syncManager) {
        syncManager.registerSlotGroup(FLUID_STOCK_INV_NAME, getSlotRows());

        return createGridShell(STOCK_GRID_X).child(
            new Grid().coverChildren()
                .gridOfWidthHeight(SLOT_COLUMNS, getSlotRows(), (x, y, index) -> new FluidSlot() {

                    @Override
                    protected void addToolTip(RichTooltip tooltip) {
                        FluidStack fluid = getFluidStack();
                        if (fluid != null) {
                            tooltip.addFromFluid(fluid);
                            long amount = machine.getInformationFluidAmountForGui(index);
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

                    @Override
                    public boolean onMouseScroll(UpOrDown scrollDirection, int amount) {
                        return false;
                    }
                }.syncHandler(new FluidSlotSyncHandler(new InformationFluidTank(index)) {

                    @Override
                    protected void tryClickPhantom(MouseData mouseData, ItemStack cursorStack) {}

                    @Override
                    public void tryScrollPhantom(MouseData mouseData) {}
                }.phantom(true))
                    .background(GTGuiTextures.SLOT_FLUID_DARK)));
    }

    public Grid createGridShell(int x) {
        return new Grid().scrollable(new VerticalScrollData())
            .showScrollShadows(false)
            .minColWidth(SLOT_SIZE)
            .minRowHeight(SLOT_SIZE)
            .size(SLOT_SIZE * SLOT_COLUMNS + 4, SLOT_SIZE * VISIBLE_SLOT_ROWS)
            .pos(x, GRID_Y);
    }

    public int getSlotRows() {
        return machine.getDualSlotCountForGui() / SLOT_COLUMNS;
    }

    public ParentWidget<?> createConfigPage(PanelSyncManager syncManager) {
        LongSyncValue minItemSyncer = syncManager.findSyncHandler(MIN_ITEM_SYNC_KEY, LongSyncValue.class);
        LongSyncValue minFluidSyncer = syncManager.findSyncHandler(MIN_FLUID_SYNC_KEY, LongSyncValue.class);
        IntSyncValue refreshSyncer = syncManager.findSyncHandler(AUTO_PULL_REFRESH_SYNC_KEY, IntSyncValue.class);
        IntSyncValue intMaxSyncer = syncManager.findSyncHandler(INT_MAX_SCALE_SYNC_KEY, IntSyncValue.class);
        BooleanSyncValue recipeCheckSyncer = syncManager
            .findSyncHandler(EXPEDITE_RECIPE_SYNC_KEY, BooleanSyncValue.class);

        return new ParentWidget<>().size(PAGE_WIDTH, PAGE_HEIGHT)
            .child(
                createConfigField("GT5U.machines.stocking_bus.refresh_time", createIntegerField(refreshSyncer), 10, 6))
            .child(
                createConfigField(
                    "Info_AdvancedSuperDualInputHatchME_IntMax",
                    createIntegerField(intMaxSyncer),
                    104,
                    6))
            .child(createConfigField("Info_SuperDualInputHatchME_03", createLongField(minItemSyncer), 198, 6))
            .child(createConfigField("Info_SuperDualInputHatchME_04", createLongField(minFluidSyncer), 292, 6))
            .child(createRecipeCheckRow(recipeCheckSyncer));
    }

    public Widget<?> createConfigField(String labelKey, TextFieldWidget field, int x, int y) {
        Flow column = Flow.column()
            .coverChildren()
            .pos(x, y)
            .child(
                IKey.lang(labelKey)
                    .asWidget()
                    .size(CONFIG_FIELD_WIDTH, 14)
                    .textAlign(Alignment.Center))
            .child(field.size(CONFIG_FIELD_WIDTH, 18));
        if ("Info_AdvancedSuperDualInputHatchME_IntMax".equals(labelKey)) {
            column.tooltip(tooltip -> tooltip.addLine(IKey.lang("Info_AdvancedSuperDualInputHatchME_IntMaxTooltip")));
        }
        return column;
    }

    public Widget<?> createPageAutoPullButton(PanelSyncManager syncManager) {
        BooleanSyncValue autoPullSyncer = syncManager.findSyncHandler(AUTO_PULL_SYNC_KEY, BooleanSyncValue.class);
        return new ToggleButton().value(autoPullSyncer)
            .size(16, 16)
            .pos(CENTER_AUTOPULL_X, CENTER_AUTOPULL_Y)
            .background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED)
            .background(false, GTGuiTextures.BUTTON_STANDARD)
            .overlay(true, GTGuiTextures.OVERLAY_BUTTON_AUTOPULL_ME)
            .overlay(false, GTGuiTextures.OVERLAY_BUTTON_AUTOPULL_ME_DISABLED)
            .setEnabledIf(button -> machine.allowAuto)
            .addTooltipLine(translate("GT5U.machines.stocking_bus.auto_pull.tooltip.1"));
    }

    public Widget<?> createCenterControls(PanelSyncManager syncManager) {
        return new ParentWidget<>().size(30, 72)
            .pos(CENTER_CONTROL_X, 0)
            .child(createPageAutoPullButton(syncManager))
            .child(
                GTGuiTextures.PICTURE_ARROW_DOUBLE.asWidget()
                    .size(12)
                    .pos(CENTER_ARROW_X - CENTER_CONTROL_X, CENTER_ARROW_Y))
            .child(createCircuitSlot(syncManager).pos(CENTER_CIRCUIT_X - CENTER_CONTROL_X, CENTER_CIRCUIT_Y));
    }

    public Flow createRecipeCheckRow(BooleanSyncValue recipeCheckSyncer) {
        return Flow.row()
            .coverChildren()
            .childPadding(4)
            .pos(10, 48)
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
                    .maxWidth(150));
    }

    public ModularPanel createStoredStackSizePanel(String key, ModularPanel parent, PanelSyncManager syncManager,
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
                createLongField(stackSizeSyncer).scrollValues(1, 100000, 1000000, 1000000000)
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
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD);
    }

    public TextFieldWidget createLongField(LongSyncValue syncer) {
        return new TextFieldWidget().value(syncer)
            .numbersLong(() -> 1L, () -> Long.MAX_VALUE)
            .formatAsInteger(true)
            .scrollValues(1, 4, 64, 256)
            .setTextAlignment(Alignment.Center)
            .setTextColor(Color.WHITE.main)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD);
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
                String workState = translate(
                    machine.isAllowedToWork() ? "GT5U.gui.text.enabled" : "GT5U.gui.text.disabled");
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

    @Override
    protected IDrawable.DrawableWidget createLogo() {
        return new IDrawable.DrawableWidget(getLogoTexture()).size(SLOT_SIZE)
            .pos(367, 81);
    }

    @Override
    protected UITexture getLogoTexture() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO;
    }

    public UITexture createTabBackground(int pageIndex, boolean selected) {
        UITexture texture = selected ? GTGuiTextures.BUTTON_COVER_TAB_HIGHLIGHT_STANDARD
            : GTGuiTextures.BUTTON_COVER_TAB_NORMAL_STANDARD;
        return new UITexture(
            texture.location,
            texture.u1,
            texture.v0,
            texture.u0,
            texture.v1,
            texture.colorType,
            texture.nonOpaque);
    }

    public class ConfigFluidTank implements IFluidTank {

        public final int slot;

        public ConfigFluidTank(int slot) {
            this.slot = slot;
        }

        @Override
        public FluidStack getFluid() {
            return machine.getFilterFluidForGui(slot);
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
                machine.setFilterFluidForGui(slot, GTUtility.copyAmount(1, resource));
            }
            return 1;
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            FluidStack fluid = getFluid();
            if (fluid != null && doDrain) {
                machine.setFilterFluidForGui(slot, null);
            }
            return fluid;
        }
    }

    public class InformationFluidTank implements IFluidTank {

        public final int slot;

        public InformationFluidTank(int slot) {
            this.slot = slot;
        }

        @Override
        public FluidStack getFluid() {
            FluidStack fluid = machine.getInformationFluidForGui(slot);
            if (fluid == null) {
                return null;
            }
            return GTUtility
                .copyAmount((int) Math.min(Integer.MAX_VALUE, machine.getInformationFluidAmountForGui(slot)), fluid);
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

    public static class StoredStackSizeFluidSlot extends FluidSlot {

        public final IPanelHandler stackSizePanel;
        public final boolean displayMiddleClickTooltip;

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

    public interface LongGetter {

        long get();
    }

    public interface LongSetter {

        void set(long value);
    }
}
