package com.science.gtnl.common.gui.modularui;

import static gregtech.api.util.GTUtility.translate;

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
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.SuperInputHatchME;

import appeng.core.localization.WailaText;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;

public class SuperInputHatchMEGui extends MTEHatchBaseGui<SuperInputHatchME> {

    public static final String FILTER_INV_NAME = "gtnl_super_input_hatch_filter_inv";
    public static final String STOCK_INV_NAME = "gtnl_super_input_hatch_stock_inv";
    public static final String CONFIG_PANEL_KEY = "gtnl_super_input_hatch_config_panel";
    public static final String SLOT_SIZE_PANEL_KEY_PREFIX = "gtnl_super_input_hatch_slot_size_panel_";
    public static final String AUTO_PULL_SYNC_KEY = "autoPullFluidList";
    public static final String MIN_AUTO_PULL_SYNC_KEY = "minAutoPullAmount";
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

    public SuperInputHatchMEGui(SuperInputHatchME hatch) {
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
            .child(createStatusText(syncManager))
            .child(createLogo());
        return panel;
    }

    @Override
    public void registerSyncValues(PanelSyncManager syncManager) {
        syncManager.syncValue(
            AUTO_PULL_SYNC_KEY,
            new BooleanSyncValue(machine::isAutoPullFluidListForGui, machine::setAutoPullFluidList).allowC2S());
        syncManager.syncValue(
            MIN_AUTO_PULL_SYNC_KEY,
            new IntSyncValue(machine::getMinAutoPullAmountForGui, machine::setMinAutoPullAmountForGui).allowC2S());
        syncManager.syncValue(
            AUTO_PULL_REFRESH_SYNC_KEY,
            new IntSyncValue(machine::getAutoPullRefreshTimeForGui, machine::setAutoPullRefreshTimeForGui).allowC2S());
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

    public FluidSlot createFilterSlot(ModularPanel parent, PanelSyncManager syncManager,
        BooleanSyncValue autoPullSyncer, int index) {
        IPanelHandler slotSizePanel = syncManager.syncedPanel(
            SLOT_SIZE_PANEL_KEY_PREFIX + index,
            true,
            (manager, handler) -> createStoredStackSizePanel(parent, manager, index));

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
                if (heldFluid != null && machine.containsFluidForGui(heldFluid)) return;

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

    public Grid createStockGrid(PanelSyncManager syncManager) {
        syncManager.registerSlotGroup(STOCK_INV_NAME, getSlotRows());

        return createGridShell(STOCK_GRID_X).child(
            new Grid().coverChildren()
                .gridOfWidthHeight(SLOT_COLUMNS, getSlotRows(), (x, y, index) -> new FluidSlot() {

                    @Override
                    protected void addToolTip(RichTooltip tooltip) {
                        FluidStack fluid = getFluidStack();
                        if (fluid != null) {
                            tooltip.addFromFluid(fluid);
                            tooltip.addLine(
                                IKey.lang(
                                    "modularui2.fluid.phantom.amount",
                                    formatFluidTooltipAmount(fluid.amount),
                                    getBaseUnit()));
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
            .pos(x, FILTER_GRID_Y);
    }

    public int getSlotRows() {
        return machine.getFluidSlotCountForGui() / SLOT_COLUMNS;
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
            .addTooltipLine(translate("GT5U.machines.stocking_hatch.auto_pull.tooltip.1"))
            .addTooltipLine(translate("GT5U.machines.stocking_hatch.auto_pull.tooltip.2"));
    }

    public ModularPanel createStoredStackSizePanel(ModularPanel parent, PanelSyncManager syncManager, int slot) {
        IntSyncValue stackSizeSyncer = new IntSyncValue(
            () -> machine.getStoredStackSizeForGui(slot),
            value -> machine.setStoredStackSizeForGui(slot, value)).allowC2S();
        syncManager.syncValue("storedFluidStackSize" + slot, stackSizeSyncer);

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
            .child(
                createIntegerField(stackSizeSyncer).scrollValues(1, 1000, 10000, 1000000)
                    .size(106, 18));

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
        IntSyncValue minAmountSyncer = new IntSyncValue(
            machine::getMinAutoPullAmountForGui,
            machine::setMinAutoPullAmountForGui).allowC2S();
        IntSyncValue refreshSyncer = new IntSyncValue(
            machine::getAutoPullRefreshTimeForGui,
            machine::setAutoPullRefreshTimeForGui).allowC2S();
        BooleanSyncValue recipeCheckSyncer = new BooleanSyncValue(machine::doFastRecipeCheck, machine::setRecipeCheck)
            .allowC2S();

        Flow mainColumn = Flow.column()
            .coverChildren()
            .marginTop(15)
            .childPadding(3)
            .child(
                IKey.lang("GT5U.machines.stocking_hatch.min_amount")
                    .asWidget())
            .child(createIntegerField(minAmountSyncer))
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
            return machine.getInformationFluidForGui(slot);
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
}
