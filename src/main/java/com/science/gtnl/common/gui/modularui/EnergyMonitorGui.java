package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.basicMachine.EnergyMonitor;
import com.science.gtnl.common.machine.monitor.EnergyMonitorHighlightTarget;
import com.science.gtnl.common.machine.monitor.EnergyMonitorMode;
import com.science.gtnl.common.machine.monitor.EnergyMonitorRowSnapshot;
import com.science.gtnl.common.machine.monitor.EnergyMonitorSnapshot;
import com.science.gtnl.common.machine.monitor.EnergyMonitorSummarySnapshot;

import appeng.api.util.DimensionalCoord;
import appeng.client.render.highlighter.BlockPosHighlighter;
import appeng.core.localization.PlayerMessages;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.singleblock.base.MTETieredMachineBlockBaseGui;
import gregtech.common.gui.modularui.synchandler.NBTSerializableSyncHandler;

public class EnergyMonitorGui extends MTETieredMachineBlockBaseGui<EnergyMonitor> {

    private static final String OWNER_SYNC_KEY = "energyMonitorOwner";
    private static final String TOTAL_MODE_SYNC_KEY = "energyMonitorTotalMode";
    private static final String STATISTICS_MODE_SYNC_KEY = "energyMonitorStatisticsMode";
    private static final String VISIBLE_ROW_COUNT_SYNC_KEY = "energyMonitorVisibleRowCount";
    private static final String SNAPSHOT_SYNC_KEY = "energyMonitorSnapshot";

    private static final int PANEL_WIDTH = 222;
    private static final int PANEL_HEIGHT = 205;
    private static final int TERMINAL_X = 12;
    private static final int TERMINAL_Y = 4;
    private static final int TERMINAL_WIDTH = 198;
    private static final int TERMINAL_HEIGHT = 118;
    private static final int TERMINAL_TEXT_X = 6;
    private static final int TERMINAL_TEXT_Y = 5;
    private static final int TERMINAL_TEXT_WIDTH = 186;
    private static final int TERMINAL_TEXT_HEIGHT = 108;
    private static final int INVENTORY_X = 30;
    private static final int INVENTORY_Y = 126;
    private static final int INVENTORY_WIDTH = 162;
    private static final int INVENTORY_HEIGHT = 76;
    private static final int LOGO_SIZE = 18;
    private static final int INLINE_BUTTON_HEIGHT = 10;
    private static final int INLINE_BUTTON_SPACING = 2;
    private static final int MODE_BUTTON_PADDING = 8;
    private static final int ROW_MIN_HEIGHT = 18;

    private MonitoringListWidget terminalListWidget;

    public EnergyMonitorGui(EnergyMonitor machine) {
        super(machine);
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        registerSyncValues(syncManager);
        return GTGuis.mteTemplatePanelBuilder(machine, guiData, syncManager, uiSettings)
            .setWidth(PANEL_WIDTH)
            .setHeight(PANEL_HEIGHT)
            .doesAddGregTechLogo(false)
            .doesBindPlayerInventory(false)
            .build()
            .child(createTerminal(syncManager))
            .child(createPlayerInventory())
            .child(createLogo());
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        syncManager.syncValue(
            OWNER_SYNC_KEY,
            new StringSyncValue(machine::getOwnerNameForGui, null, machine::getOwnerNameForGui, null));
        syncManager.syncValue(
            TOTAL_MODE_SYNC_KEY,
            new IntSyncValue(
                () -> machine.getTotalEnergyMode()
                    .ordinal(),
                value -> machine.setTotalEnergyMode(resolveMode(value))).allowC2S());
        syncManager.syncValue(
            STATISTICS_MODE_SYNC_KEY,
            new IntSyncValue(
                () -> machine.getStatisticsMode()
                    .ordinal(),
                value -> machine.setStatisticsMode(resolveMode(value))).allowC2S());
        syncManager.syncValue(
            VISIBLE_ROW_COUNT_SYNC_KEY,
            new IntSyncValue(machine::getVisibleRowCount, machine::setVisibleRowCount).allowC2S());
        syncManager.syncValue(
            SNAPSHOT_SYNC_KEY,
            new NBTSerializableSyncHandler<>(
                EnergyMonitorSnapshot::empty,
                machine::getSnapshotForSync,
                machine::setSnapshotFromSync).withEqualityFunc((left, right) -> {
                    if (left == right) {
                        return true;
                    }
                    if (left == null || right == null) {
                        return false;
                    }
                    EnergyMonitorSnapshot leftSnapshot = EnergyMonitorSnapshot.empty();
                    leftSnapshot.deserializeNBT(left);
                    EnergyMonitorSnapshot rightSnapshot = EnergyMonitorSnapshot.empty();
                    rightSnapshot.deserializeNBT(right);
                    return leftSnapshot.sameAs(rightSnapshot);
                }));
    }

    private IWidget createTerminal(PanelSyncManager syncManager) {
        terminalListWidget = new MonitoringListWidget(syncManager).pos(TERMINAL_TEXT_X, TERMINAL_TEXT_Y)
            .size(TERMINAL_TEXT_WIDTH, TERMINAL_TEXT_HEIGHT)
            .scrollDirection(new VerticalScrollData())
            .showScrollShadows(false)
            .crossAxisAlignment(Alignment.CrossAxis.START);
        terminalListWidget.buildStaticContent();
        return new ParentWidget<>().pos(TERMINAL_X, TERMINAL_Y)
            .size(TERMINAL_WIDTH, TERMINAL_HEIGHT)
            .child(
                GTGuiTextures.PICTURE_SCREEN_BLACK.asWidget()
                    .size(TERMINAL_WIDTH, TERMINAL_HEIGHT))
            .child(terminalListWidget);
    }

    private IWidget createPlayerInventory() {
        return Flow.row()
            .pos(INVENTORY_X, INVENTORY_Y)
            .size(INVENTORY_WIDTH, INVENTORY_HEIGHT)
            .mainAxisAlignment(Alignment.MainAxis.CENTER)
            .child(SlotGroupWidget.playerInventory(false));
    }

    private IWidget createOwnerLine(PanelSyncManager syncManager) {
        StringSyncValue ownerSyncer = syncManager.findSyncHandler(OWNER_SYNC_KEY, StringSyncValue.class);
        return IKey.dynamic(
            () -> buildTranslatedLine("gtnl.energy_monitor.owner", EnumChatFormatting.AQUA + ownerSyncer.getValue()))
            .asWidget()
            .textAlign(Alignment.CenterLeft)
            .maxWidth(TERMINAL_TEXT_WIDTH)
            .fullWidth();
    }

    private IWidget createTotalEnergyLine(PanelSyncManager syncManager) {
        IntSyncValue totalModeSyncer = syncManager.findSyncHandler(TOTAL_MODE_SYNC_KEY, IntSyncValue.class);
        return Flow.row()
            .width(TERMINAL_TEXT_WIDTH)
            .coverChildrenHeight(INLINE_BUTTON_HEIGHT)
            .wrap()
            .crossAxisAlignment(Alignment.CrossAxis.START)
            .crossAxisChildPadding(1)
            .child(IKey.dynamic(() -> {
                EnergyMonitorSummarySnapshot summary = machine.getSummarySnapshot();
                return buildTranslatedLine(
                    "gtnl.energy_monitor.total_energy",
                    EnumChatFormatting.GRAY + summary.getTotalEnergyText());
            })
                .asWidget()
                .textAlign(Alignment.CenterLeft)
                .maxWidth(TERMINAL_TEXT_WIDTH))
            .child(createModeButton(totalModeSyncer, true));
    }

    private IWidget createAverageLine() {
        return IKey.dynamic(() -> {
            EnergyMonitorSummarySnapshot summary = machine.getSummarySnapshot();
            String key = summary.isOutputMode() ? "gtnl.energy_monitor.average_output"
                : "gtnl.energy_monitor.average_input";
            String tierName = GTUtility.getColoredTierNameFromTier((byte) summary.getVoltageTier());
            return EnumChatFormatting.WHITE + String.format(
                StatCollector.translateToLocal(key),
                EnumChatFormatting.GRAY + summary.getAverageEuText() + EnumChatFormatting.WHITE,
                EnumChatFormatting.GRAY + summary.getAmpText() + EnumChatFormatting.WHITE,
                tierName + EnumChatFormatting.WHITE);
        })
            .asWidget()
            .textAlign(Alignment.CenterLeft)
            .maxWidth(TERMINAL_TEXT_WIDTH)
            .fullWidth();
    }

    private IWidget createEstimatedTimeLine() {
        return IKey.dynamic(() -> {
            EnergyMonitorSummarySnapshot summary = machine.getSummarySnapshot();
            String key = summary.isOutputMode() ? "gtnl.energy_monitor.estimated_empty"
                : "gtnl.energy_monitor.estimated_full";
            return buildTranslatedLine(
                key,
                EnumChatFormatting.GRAY + translateIfNeeded(summary.getEstimatedTimeText()));
        })
            .asWidget()
            .textAlign(Alignment.CenterLeft)
            .maxWidth(TERMINAL_TEXT_WIDTH)
            .fullWidth();
    }

    private IWidget createStatisticsLine(PanelSyncManager syncManager) {
        IntSyncValue statisticsModeSyncer = syncManager.findSyncHandler(STATISTICS_MODE_SYNC_KEY, IntSyncValue.class);
        return Flow.row()
            .width(TERMINAL_TEXT_WIDTH)
            .coverChildrenHeight(INLINE_BUTTON_HEIGHT)
            .wrap()
            .crossAxisAlignment(Alignment.CrossAxis.START)
            .crossAxisChildPadding(1)
            .child(
                IKey.str(StatCollector.translateToLocal("gtnl.energy_monitor.statistics"))
                    .asWidget()
                    .textAlign(Alignment.CenterLeft)
                    .maxWidth(TERMINAL_TEXT_WIDTH))
            .child(createModeButton(statisticsModeSyncer, false));
    }

    private IWidget createRowWidget(PanelSyncManager syncManager, EnergyMonitorRowSnapshot row) {
        int highlightButtonWidth = getHighlightButtonWidth();
        int contentWidth = getRowContentWidth(row, TERMINAL_TEXT_WIDTH);
        ButtonWidget<?> contentButton = new ButtonWidget<>().background(IDrawable.EMPTY)
            .disableThemeBackground(true)
            .disableHoverThemeBackground(true)
            .disableHoverBackground()
            .disableHoverOverlay()
            .width(contentWidth)
            .coverChildrenHeight(ROW_MIN_HEIGHT)
            .tooltipBuilder(tooltip -> tooltip.addLine(buildRowTooltip(row)))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .onMousePressed(mouseButton -> {
                if (!Interactable.hasShiftDown() || mouseButton != 0 && mouseButton != 1) {
                    return false;
                }
                highlightRow(syncManager, row);
                return true;
            })
            .child(createRowContent(row, contentWidth));
        return Flow.row()
            .width(TERMINAL_TEXT_WIDTH)
            .coverChildrenHeight(ROW_MIN_HEIGHT)
            .wrap()
            .crossAxisAlignment(Alignment.CrossAxis.START)
            .crossAxisChildPadding(1)
            .child(contentButton)
            .child(createHighlightButton(syncManager, row, highlightButtonWidth));
    }

    private IWidget createRowContent(EnergyMonitorRowSnapshot row, int contentWidth) {
        ParentWidget<?> content = new ParentWidget<>().size(contentWidth, ROW_MIN_HEIGHT);
        int textX = 0;
        ItemStack iconStack = row.getIconStack();
        if (iconStack != null && iconStack.getItem() != null) {
            content.child(
                new ItemDrawable(iconStack.copy()).asWidget()
                    .size(16, 16)
                    .pos(0, 1));
            textX = 18;
        }
        content.child(
            IKey.str(buildRowText(row))
                .asWidget()
                .textAlign(Alignment.CenterLeft)
                .size(Math.max(1, contentWidth - textX), ROW_MIN_HEIGHT)
                .maxWidth(Math.max(1, contentWidth - textX))
                .pos(textX, 0));
        return content;
    }

    private IWidget createHighlightButton(PanelSyncManager syncManager, EnergyMonitorRowSnapshot row, int buttonWidth) {
        return new ButtonWidget<>().background(IDrawable.EMPTY)
            .size(buttonWidth, INLINE_BUTTON_HEIGHT)
            .disableThemeBackground(true)
            .disableHoverThemeBackground(true)
            .disableHoverBackground()
            .disableHoverOverlay()
            .child(
                IKey.str(EnumChatFormatting.YELLOW + "[]")
                    .asWidget()
                    .textAlign(Alignment.CenterLeft)
                    .size(buttonWidth, INLINE_BUTTON_HEIGHT))
            .onMousePressed(mouseButton -> {
                if (mouseButton != 0 && mouseButton != 1) {
                    return false;
                }
                highlightRow(syncManager, row);
                return true;
            });
    }

    private IWidget createLoadMoreHint() {
        return IKey.str(EnumChatFormatting.YELLOW + StatCollector.translateToLocal("gtnl.energy_monitor.scroll_more"))
            .asWidget()
            .textAlign(Alignment.CenterLeft)
            .maxWidth(TERMINAL_TEXT_WIDTH)
            .fullWidth();
    }

    @Override
    protected IDrawable.DrawableWidget createLogo() {
        return new IDrawable.DrawableWidget(getLogoTexture()).size(LOGO_SIZE)
            .pos(TERMINAL_X + TERMINAL_WIDTH - LOGO_SIZE + 8, TERMINAL_Y + TERMINAL_HEIGHT + 4);
    }

    @Override
    protected UITexture getLogoTexture() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO;
    }

    private IWidget createModeButton(IntSyncValue modeSyncer, boolean wrapWithParentheses) {
        EnergyMonitorMode[] modes = EnergyMonitorMode.values();
        ParentWidget<?> holder = new ParentWidget<>().height(INLINE_BUTTON_HEIGHT);
        for (EnergyMonitorMode mode : modes) {
            String buttonText = formatModeText(mode, wrapWithParentheses);
            int buttonWidth = getModeButtonWidth(mode, wrapWithParentheses);
            holder.child(
                new ButtonWidget<>().background(IDrawable.EMPTY)
                    .size(buttonWidth, INLINE_BUTTON_HEIGHT)
                    .disableThemeBackground(true)
                    .disableHoverThemeBackground(true)
                    .disableHoverBackground()
                    .disableHoverOverlay()
                    .child(
                        IKey.str(buttonText)
                            .asWidget()
                            .textAlign(Alignment.CenterLeft)
                            .size(buttonWidth, INLINE_BUTTON_HEIGHT))
                    .onMousePressed(mouseButton -> {
                        if (mouseButton != 0 && mouseButton != 1) {
                            return false;
                        }
                        cycleMode(modeSyncer, mouseButton);
                        if (terminalListWidget != null) {
                            terminalListWidget.rebuildDynamicRows();
                        }
                        return true;
                    })
                    .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("gtnl.energy_monitor.mode_hint")))
                    .tooltipShowUpTimer(TOOLTIP_DELAY)
                    .setEnabledIf(widget -> resolveMode(modeSyncer.getIntValue()) == mode));
        }
        return holder.size(getModeButtonWidth(wrapWithParentheses), INLINE_BUTTON_HEIGHT)
            .marginLeft(INLINE_BUTTON_SPACING);
    }

    private void highlightRow(PanelSyncManager syncManager, EnergyMonitorRowSnapshot row) {
        EnergyMonitorHighlightTarget target = row.getHighlightTarget();
        BlockPosHighlighter.highlightBlocks(
            syncManager.getPlayer(),
            Collections.singletonList(
                new DimensionalCoord(target.getX(), target.getY(), target.getZ(), target.getDimensionId())),
            row.getDisplayName(),
            PlayerMessages.MachineHighlighted.getUnlocalized(),
            PlayerMessages.MachineInOtherDim.getUnlocalized());
    }

    private static void cycleMode(IntSyncValue syncer, int mouseButton) {
        EnergyMonitorMode currentMode = resolveMode(syncer.getIntValue());
        EnergyMonitorMode nextMode = mouseButton == 1 ? currentMode.previous() : currentMode.next();
        syncer.setIntValue(nextMode.ordinal(), true, true);
    }

    private static EnergyMonitorMode resolveMode(int ordinal) {
        EnergyMonitorMode[] modes = EnergyMonitorMode.values();
        return modes[Math.max(0, Math.min(modes.length - 1, ordinal))];
    }

    private static String buildTranslatedLine(String translationKey, String valueText) {
        return EnumChatFormatting.WHITE + String.format(StatCollector.translateToLocal(translationKey), valueText);
    }

    private static String formatModeText(EnergyMonitorMode mode, boolean wrapWithParentheses) {
        String translatedMode = EnumChatFormatting.YELLOW + translateMode(mode);
        return wrapWithParentheses ? EnumChatFormatting.YELLOW + "(" + translatedMode + EnumChatFormatting.YELLOW + ")"
            : translatedMode;
    }

    private static String buildRowText(EnergyMonitorRowSnapshot row) {
        return EnumChatFormatting.WHITE + row.getDisplayName()
            + " "
            + EnumChatFormatting.GRAY
            + row.getFormattedEut()
            + " EU/t "
            + EnumChatFormatting.WHITE
            + "("
            + GTUtility.getColoredTierNameFromTier((byte) row.getVoltageTier())
            + EnumChatFormatting.WHITE
            + ")";
    }

    private static IKey buildRowTooltip(EnergyMonitorRowSnapshot row) {
        EnergyMonitorHighlightTarget target = row.getHighlightTarget();
        return IKey.lang(
            "gtnl.energy_monitor.tooltip",
            () -> new Object[] { target.getDimensionId(), target.getX(), target.getY(), target.getZ(),
                row.getOwnerName() });
    }

    private static String translateMode(EnergyMonitorMode mode) {
        return StatCollector.translateToLocal(mode.getTranslationKey());
    }

    private static String translateIfNeeded(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.startsWith("gtnl.energy_monitor.") ? StatCollector.translateToLocal(value) : value;
    }

    private static int getModeButtonWidth(boolean wrapWithParentheses) {
        int maxWidth = 0;
        for (EnergyMonitorMode mode : EnergyMonitorMode.values()) {
            maxWidth = Math.max(maxWidth, getModeButtonWidth(mode, wrapWithParentheses));
        }
        return maxWidth;
    }

    private static int getModeButtonWidth(EnergyMonitorMode mode, boolean wrapWithParentheses) {
        return Math.max(
            1,
            (int) Math.ceil(
                (IKey.str(formatModeText(mode, wrapWithParentheses))
                    .getDefaultWidth() + MODE_BUTTON_PADDING) * 1.5D));
    }

    private static int getHighlightButtonWidth() {
        return Math.max(
            1,
            IKey.str(EnumChatFormatting.YELLOW + "[]")
                .getDefaultWidth() + 2);
    }

    private static int getRowContentWidth(EnergyMonitorRowSnapshot row, int totalWidth) {
        int textWidth = Math.max(
            1,
            IKey.str(buildRowText(row))
                .getDefaultWidth() + 2);
        int iconWidth = row.getIconStack() != null && row.getIconStack()
            .getItem() != null ? 18 : 0;
        return Math.max(1, Math.min(totalWidth, textWidth + iconWidth));
    }

    public class MonitoringListWidget extends GTNLListWidget<IWidget, MonitoringListWidget> {

        private final PanelSyncManager syncManager;
        private final Flow dynamicRows = Flow.column()
            .width(TERMINAL_TEXT_WIDTH)
            .coverChildrenHeight(1)
            .crossAxisAlignment(Alignment.CrossAxis.START);
        private long lastVisibleRowsRevision = Long.MIN_VALUE;

        public MonitoringListWidget(PanelSyncManager syncManager) {
            this.syncManager = syncManager;
        }

        public void buildStaticContent() {
            child(createOwnerLine(syncManager));
            child(createTotalEnergyLine(syncManager));
            child(createAverageLine());
            child(createEstimatedTimeLine());
            child(createStatisticsLine(syncManager));
            child(dynamicRows);
            rebuildDynamicRows();
        }

        public void rebuildDynamicRows() {
            List<EnergyMonitorRowSnapshot> visibleRows = machine.getVisibleRowsForGui();
            boolean hasMoreRows = machine.hasMoreRowsForGui();
            dynamicRows.removeAll();
            for (EnergyMonitorRowSnapshot row : visibleRows) {
                dynamicRows.child(createRowWidget(syncManager, row));
            }
            if (hasMoreRows) {
                dynamicRows.child(createLoadMoreHint());
            }
            dynamicRows.scheduleResize();
            scheduleResize();
            lastVisibleRowsRevision = machine.getVisibleRowsRevision();
        }

        @Override
        public void onUpdate() {
            super.onUpdate();
            if (lastVisibleRowsRevision != machine.getVisibleRowsRevision()) {
                rebuildDynamicRows();
            }
        }

        @Override
        public boolean onMouseScroll(UpOrDown scrollDirection, int amount) {
            boolean handled = super.onMouseScroll(scrollDirection, amount);
            if (scrollDirection.isDown() && machine.hasMoreRowsForGui() && isAtBottom()) {
                machine.loadMoreRows();
                rebuildDynamicRows();
                return true;
            }
            return handled;
        }

        private boolean isAtBottom() {
            VerticalScrollData scrollData = getScrollArea().getScrollY();
            if (scrollData == null) {
                return true;
            }
            int visibleBottom = getScrollY() + scrollData.getFullVisibleSize(getScrollArea());
            return visibleBottom >= scrollData.getScrollSize() - 1;
        }
    }
}
