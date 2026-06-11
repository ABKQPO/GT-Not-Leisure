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
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
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

public class SuperInputHatchMEGui {

    private static final String FILTER_INV_NAME = "gtnl_super_input_hatch_filter_inv";
    private static final String STOCK_INV_NAME = "gtnl_super_input_hatch_stock_inv";
    private static final String CONFIG_PANEL_KEY = "gtnl_super_input_hatch_config_panel";
    private static final String SLOT_SIZE_PANEL_KEY_PREFIX = "gtnl_super_input_hatch_slot_size_panel_";
    private static final String AUTO_PULL_SYNC_KEY = "autoPullFluidList";
    private static final String MIN_AUTO_PULL_SYNC_KEY = "minAutoPullAmount";
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

    private final SuperInputHatchME hatch;

    public SuperInputHatchMEGui(SuperInputHatchME hatch) {
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
            .child(createStatusText(syncManager))
            .child(createLogo());
        return panel;
    }

    private void registerSyncValues(PanelSyncManager syncManager) {
        syncManager.syncValue(
            AUTO_PULL_SYNC_KEY,
            new BooleanSyncValue(hatch::isAutoPullFluidListForGui, hatch::setAutoPullFluidList).allowC2S());
        syncManager.syncValue(
            MIN_AUTO_PULL_SYNC_KEY,
            new IntSyncValue(hatch::getMinAutoPullAmountForGui, hatch::setMinAutoPullAmountForGui).allowC2S());
        syncManager.syncValue(
            AUTO_PULL_REFRESH_SYNC_KEY,
            new IntSyncValue(hatch::getAutoPullRefreshTimeForGui, hatch::setAutoPullRefreshTimeForGui).allowC2S());
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

    private FluidSlot createFilterSlot(ModularPanel parent, PanelSyncManager syncManager,
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
                if (heldFluid != null && hatch.containsFluidForGui(heldFluid)) return;

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

    private Grid createStockGrid(PanelSyncManager syncManager) {
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
            .pos(x, FILTER_GRID_Y);
    }

    private int getSlotRows() {
        return hatch.getFluidSlotCountForGui() / SLOT_COLUMNS;
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
            .addTooltipLine(translate("GT5U.machines.stocking_hatch.auto_pull.tooltip.1"))
            .addTooltipLine(translate("GT5U.machines.stocking_hatch.auto_pull.tooltip.2"));
    }

    private ModularPanel createStoredStackSizePanel(ModularPanel parent, PanelSyncManager syncManager, int slot) {
        IntSyncValue stackSizeSyncer = new IntSyncValue(
            () -> hatch.getStoredStackSizeForGui(slot),
            value -> hatch.setStoredStackSizeForGui(slot, value)).allowC2S();
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
                createIntegerField(stackSizeSyncer).setScrollValues(1, 1000, 10000)
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

    private ModularPanel createStackSizeConfigurationPanel(ModularPanel parent, PanelSyncManager syncManager) {
        IntSyncValue minAmountSyncer = syncManager.findSyncHandler(MIN_AUTO_PULL_SYNC_KEY, IntSyncValue.class);
        IntSyncValue refreshSyncer = syncManager.findSyncHandler(AUTO_PULL_REFRESH_SYNC_KEY, IntSyncValue.class);
        BooleanSyncValue recipeCheckSyncer = syncManager
            .findSyncHandler(EXPEDITE_RECIPE_SYNC_KEY, BooleanSyncValue.class);

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
            return hatch.getInformationFluidForGui(slot);
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
}
