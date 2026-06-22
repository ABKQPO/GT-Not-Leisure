package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.IPacketWriter;
import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.DynamicSyncHandler;
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.EmptyWidget;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.basicMachine.EnergyMonitor;
import com.science.gtnl.common.machine.monitor.EnergyMonitorCategory;
import com.science.gtnl.common.machine.monitor.EnergyMonitorHighlightTarget;
import com.science.gtnl.common.machine.monitor.EnergyMonitorMode;
import com.science.gtnl.common.machine.monitor.EnergyMonitorRowSnapshot;

import appeng.api.util.DimensionalCoord;
import appeng.client.render.highlighter.BlockPosHighlighter;
import appeng.core.localization.PlayerMessages;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.singleblock.base.MTETieredMachineBlockBaseGui;

public class EnergyMonitorGui extends MTETieredMachineBlockBaseGui<EnergyMonitor> {

    private static final String OWNER_SYNC_KEY = "energyMonitorOwner";
    private static final String TOTAL_MODE_SYNC_KEY = "energyMonitorTotalMode";
    private static final String TOTAL_ENERGY_SYNC_KEY = "energyMonitorTotalEnergy";
    private static final String STATISTICS_MODE_SYNC_KEY = "energyMonitorStatisticsMode";
    private static final String AVERAGE_EU_SYNC_KEY = "energyMonitorAverageEu";
    private static final String AMP_SYNC_KEY = "energyMonitorAmp";
    private static final String VOLTAGE_TIER_SYNC_KEY = "energyMonitorVoltageTier";
    private static final String OUTPUT_MODE_SYNC_KEY = "energyMonitorOutputMode";
    private static final String ESTIMATED_TIME_SYNC_KEY = "energyMonitorEstimatedTime";
    private static final String VISIBLE_ROW_COUNT_SYNC_KEY = "energyMonitorVisibleRowCount";
    private static final String HAS_MORE_ROWS_SYNC_KEY = "energyMonitorHasMoreRows";
    private static final String ROWS_SYNC_KEY = "energyMonitorRows";
    private static final String TERMINAL_WIDGET_SYNC_KEY = "energyMonitorTerminalWidget";

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

    private int terminalScrollY;
    private DynamicSyncedWidget<?> terminalWidget;

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
        syncManager.syncValue(OWNER_SYNC_KEY, new StringSyncValue(machine::getOwnerNameForGui));
        syncManager.syncValue(
            TOTAL_MODE_SYNC_KEY,
            new IntSyncValue(
                () -> machine.getTotalEnergyMode()
                    .ordinal(),
                value -> machine.setTotalEnergyMode(resolveMode(value))).allowC2S());
        syncManager.syncValue(TOTAL_ENERGY_SYNC_KEY, new StringSyncValue(machine::getTotalEnergyTextForGui));
        syncManager.syncValue(
            STATISTICS_MODE_SYNC_KEY,
            new IntSyncValue(
                () -> machine.getStatisticsMode()
                    .ordinal(),
                value -> machine.setStatisticsMode(resolveMode(value))).allowC2S());
        syncManager.syncValue(AVERAGE_EU_SYNC_KEY, new StringSyncValue(machine::getAverageEuTextForGui));
        syncManager.syncValue(AMP_SYNC_KEY, new StringSyncValue(machine::getAmpTextForGui));
        syncManager.syncValue(VOLTAGE_TIER_SYNC_KEY, new IntSyncValue(machine::getVoltageTierForGui));
        syncManager.syncValue(OUTPUT_MODE_SYNC_KEY, new BooleanSyncValue(machine::isOutputModeForGui));
        syncManager.syncValue(ESTIMATED_TIME_SYNC_KEY, new StringSyncValue(machine::getEstimatedTimeTextForGui));
        syncManager.syncValue(
            VISIBLE_ROW_COUNT_SYNC_KEY,
            new IntSyncValue(machine::getVisibleRowCount, machine::setVisibleRowCount).allowC2S());

        BooleanSyncValue hasMoreRowsSyncer = new BooleanSyncValue(machine::hasMoreRowsForGui);
        syncManager.syncValue(HAS_MORE_ROWS_SYNC_KEY, hasMoreRowsSyncer);

        GenericListSyncHandler<EnergyMonitorRowSnapshot> rowsSyncer = new GenericListSyncHandler<>(
            machine::getVisibleRowsForGui,
            machine::setVisibleRowsFromGui,
            EnergyMonitorGui::readRowSnapshot,
            EnergyMonitorGui::writeRowSnapshot,
            EnergyMonitorGui::areRowsEqual,
            EnergyMonitorRowSnapshot::copy);
        syncManager.syncValue(ROWS_SYNC_KEY, rowsSyncer);

        DynamicSyncHandler terminalWidgetSyncer = new DynamicSyncHandler() {

            @Override
            public void notifyUpdate(IPacketWriter packetWriter) {
                saveTerminalScroll();
                super.notifyUpdate(packetWriter);
            }
        }.widgetProvider(
            (panelSyncManager, packet) -> packet == null ? new EmptyWidget() : createTerminalList(panelSyncManager));
        syncManager.syncValue(TERMINAL_WIDGET_SYNC_KEY, terminalWidgetSyncer);

        if (!syncManager.isClient()) {
            rowsSyncer.setChangeListener(() -> terminalWidgetSyncer.notifyUpdate(packet -> {}));
            hasMoreRowsSyncer.setChangeListener(() -> terminalWidgetSyncer.notifyUpdate(packet -> {}));
        }
    }

    private IWidget createTerminal(PanelSyncManager syncManager) {
        DynamicSyncHandler terminalWidgetSyncer = syncManager
            .findSyncHandler(TERMINAL_WIDGET_SYNC_KEY, DynamicSyncHandler.class);
        terminalWidget = new DynamicSyncedWidget<>().syncHandler(terminalWidgetSyncer)
            .initialChild(createTerminalList(syncManager))
            .pos(TERMINAL_TEXT_X, TERMINAL_TEXT_Y)
            .size(TERMINAL_TEXT_WIDTH, TERMINAL_TEXT_HEIGHT);
        return new ParentWidget<>().pos(TERMINAL_X, TERMINAL_Y)
            .size(TERMINAL_WIDTH, TERMINAL_HEIGHT)
            .child(
                GTGuiTextures.PICTURE_SCREEN_BLACK.asWidget()
                    .size(TERMINAL_WIDTH, TERMINAL_HEIGHT))
            .child(terminalWidget);
    }

    private IWidget createPlayerInventory() {
        return Flow.row()
            .pos(INVENTORY_X, INVENTORY_Y)
            .size(INVENTORY_WIDTH, INVENTORY_HEIGHT)
            .mainAxisAlignment(Alignment.MainAxis.CENTER)
            .child(SlotGroupWidget.playerInventory(false));
    }

    private IWidget createTerminalList(PanelSyncManager syncManager) {
        GenericListSyncHandler<EnergyMonitorRowSnapshot> rowsSyncer = getRowsSyncer(syncManager);
        IntSyncValue visibleRowCountSyncer = syncManager
            .findSyncHandler(VISIBLE_ROW_COUNT_SYNC_KEY, IntSyncValue.class);
        BooleanSyncValue hasMoreRowsSyncer = syncManager
            .findSyncHandler(HAS_MORE_ROWS_SYNC_KEY, BooleanSyncValue.class);

        MonitoringListWidget listWidget = new MonitoringListWidget(
            terminalScrollY,
            visibleRowCountSyncer,
            hasMoreRowsSyncer).size(TERMINAL_TEXT_WIDTH, TERMINAL_TEXT_HEIGHT)
                .scrollDirection(new VerticalScrollData())
                .showScrollShadows(false)
                .crossAxisAlignment(Alignment.CrossAxis.START);
        terminalScrollY = 0;

        listWidget.child(createOwnerLine(syncManager));
        listWidget.child(createTotalEnergyLine(syncManager));
        listWidget.child(createAverageLine(syncManager));
        listWidget.child(createEstimatedTimeLine(syncManager));
        listWidget.child(createStatisticsLine(syncManager));
        for (EnergyMonitorRowSnapshot row : rowsSyncer.getValue()) {
            listWidget.child(createRowWidget(syncManager, row));
        }
        listWidget.childIf(hasMoreRowsSyncer.getBoolValue(), this::createLoadMoreHint);
        return listWidget;
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
        StringSyncValue totalEnergySyncer = syncManager.findSyncHandler(TOTAL_ENERGY_SYNC_KEY, StringSyncValue.class);
        IntSyncValue totalModeSyncer = syncManager.findSyncHandler(TOTAL_MODE_SYNC_KEY, IntSyncValue.class);
        return Flow.row()
            .width(TERMINAL_TEXT_WIDTH)
            .height(INLINE_BUTTON_HEIGHT)
            .crossAxisAlignment(Alignment.CrossAxis.CENTER)
            .child(
                IKey.dynamic(
                    () -> buildTranslatedLine(
                        "gtnl.energy_monitor.total_energy",
                        EnumChatFormatting.GRAY + totalEnergySyncer.getValue()))
                    .asWidget()
                    .textAlign(Alignment.CenterLeft)
                    .maxWidth(TERMINAL_TEXT_WIDTH - getModeButtonWidth(true) - INLINE_BUTTON_SPACING))
            .child(createModeButton(totalModeSyncer, true));
    }

    private IWidget createAverageLine(PanelSyncManager syncManager) {
        BooleanSyncValue outputModeSyncer = syncManager.findSyncHandler(OUTPUT_MODE_SYNC_KEY, BooleanSyncValue.class);
        StringSyncValue averageEuSyncer = syncManager.findSyncHandler(AVERAGE_EU_SYNC_KEY, StringSyncValue.class);
        StringSyncValue ampSyncer = syncManager.findSyncHandler(AMP_SYNC_KEY, StringSyncValue.class);
        IntSyncValue voltageTierSyncer = syncManager.findSyncHandler(VOLTAGE_TIER_SYNC_KEY, IntSyncValue.class);
        return IKey.dynamic(() -> {
            String key = outputModeSyncer.getBoolValue() ? "gtnl.energy_monitor.average_output"
                : "gtnl.energy_monitor.average_input";
            String tierName = GTUtility.getColoredTierNameFromTier((byte) voltageTierSyncer.getIntValue());
            return EnumChatFormatting.WHITE + String.format(
                StatCollector.translateToLocal(key),
                EnumChatFormatting.GRAY + averageEuSyncer.getValue() + EnumChatFormatting.WHITE,
                EnumChatFormatting.GRAY + ampSyncer.getValue() + EnumChatFormatting.WHITE,
                tierName + EnumChatFormatting.WHITE);
        })
            .asWidget()
            .textAlign(Alignment.CenterLeft)
            .maxWidth(TERMINAL_TEXT_WIDTH)
            .fullWidth();
    }

    private IWidget createEstimatedTimeLine(PanelSyncManager syncManager) {
        BooleanSyncValue outputModeSyncer = syncManager.findSyncHandler(OUTPUT_MODE_SYNC_KEY, BooleanSyncValue.class);
        StringSyncValue estimatedTimeSyncer = syncManager
            .findSyncHandler(ESTIMATED_TIME_SYNC_KEY, StringSyncValue.class);
        return IKey.dynamic(() -> {
            String key = outputModeSyncer.getBoolValue() ? "gtnl.energy_monitor.estimated_empty"
                : "gtnl.energy_monitor.estimated_full";
            return buildTranslatedLine(
                key,
                EnumChatFormatting.GRAY + translateIfNeeded(estimatedTimeSyncer.getValue()));
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
            .height(INLINE_BUTTON_HEIGHT)
            .crossAxisAlignment(Alignment.CrossAxis.CENTER)
            .child(
                IKey.str(StatCollector.translateToLocal("gtnl.energy_monitor.statistics"))
                    .asWidget()
                    .textAlign(Alignment.CenterLeft)
                    .maxWidth(TERMINAL_TEXT_WIDTH - getModeButtonWidth(false) - INLINE_BUTTON_SPACING))
            .child(createModeButton(statisticsModeSyncer, false));
    }

    private IWidget createRowWidget(PanelSyncManager syncManager, EnergyMonitorRowSnapshot row) {
        boolean hasIcon = row.getIconStack() != null;
        Flow rowWidget = Flow.row()
            .fullWidth()
            .height(18)
            .crossAxisAlignment(Alignment.CrossAxis.CENTER);
        if (hasIcon) {
            rowWidget.child(createIconHolder(row.getIconStack()));
        }
        return rowWidget.child(createRowText(row, hasIcon))
            .child(createHighlightButton(syncManager, row));
    }

    private IWidget createIconHolder(ItemStack iconStack) {
        ParentWidget<?> holder = new ParentWidget<>().size(16, 16);
        if (iconStack != null) {
            holder.child(
                new ItemDisplayWidget().item(iconStack)
                    .size(16)
                    .disableThemeBackground(true)
                    .disableHoverThemeBackground(true));
        }
        return holder;
    }

    private IWidget createRowText(EnergyMonitorRowSnapshot row, boolean hasIcon) {
        return IKey.dynamic(() -> buildRowText(row))
            .asWidget()
            .textAlign(Alignment.CenterLeft)
            .marginLeft(2)
            .maxWidth(hasIcon ? TERMINAL_TEXT_WIDTH - 30 : TERMINAL_TEXT_WIDTH - 14)
            .tooltipBuilder(tooltip -> tooltip.addLine(buildRowTooltip(row)))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .fullWidth();
    }

    private IWidget createHighlightButton(PanelSyncManager syncManager, EnergyMonitorRowSnapshot row) {
        return new ButtonWidget<>().background(IDrawable.EMPTY)
            .size(16, 10)
            .disableThemeBackground(true)
            .disableHoverThemeBackground(true)
            .disableHoverBackground()
            .disableHoverOverlay()
            .child(
                IKey.str(EnumChatFormatting.YELLOW + "[]")
                    .asWidget()
                    .textAlign(Alignment.Center)
                    .size(16, 10))
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
            int buttonWidth = getTextWidth(buttonText);
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

    private void saveTerminalScroll() {
        if (terminalWidget == null || !terminalWidget.hasChildren()) {
            return;
        }
        IWidget widget = terminalWidget.getChildren()
            .get(0);
        if (widget instanceof MonitoringListWidget listWidget && listWidget.getScrollData()
            .getScrollSize() != 0) {
            terminalScrollY = listWidget.getScrollY();
        }
    }

    @SuppressWarnings("unchecked")
    private GenericListSyncHandler<EnergyMonitorRowSnapshot> getRowsSyncer(PanelSyncManager syncManager) {
        return syncManager.findSyncHandler(ROWS_SYNC_KEY, GenericListSyncHandler.class);
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

    private static void writeRowSnapshot(PacketBuffer buffer, EnergyMonitorRowSnapshot row) throws IOException {
        buffer.writeBoolean(row.getIconStack() != null);
        if (row.getIconStack() != null) {
            buffer.writeItemStackToBuffer(row.getIconStack());
        }
        buffer.writeStringToBuffer(row.getDisplayName());
        buffer.writeStringToBuffer(row.getOwnerName());
        buffer.writeStringToBuffer(
            row.getEut()
                .toString());
        buffer.writeStringToBuffer(row.getFormattedEut());
        buffer.writeVarIntToBuffer(row.getVoltageTier());
        buffer.writeVarIntToBuffer(
            row.getCategory()
                .ordinal());
        buffer.writeBoolean(row.isWireless());
        buffer.writeInt(
            row.getHighlightTarget()
                .getDimensionId());
        buffer.writeInt(
            row.getHighlightTarget()
                .getX());
        buffer.writeInt(
            row.getHighlightTarget()
                .getY());
        buffer.writeInt(
            row.getHighlightTarget()
                .getZ());
    }

    private static EnergyMonitorRowSnapshot readRowSnapshot(PacketBuffer buffer) throws IOException {
        EnergyMonitorRowSnapshot row = new EnergyMonitorRowSnapshot();
        row.setIconStack(buffer.readBoolean() ? buffer.readItemStackFromBuffer() : null);
        row.setDisplayName(buffer.readStringFromBuffer(32767));
        row.setOwnerName(buffer.readStringFromBuffer(32767));
        row.setEut(new BigInteger(buffer.readStringFromBuffer(32767)));
        row.setFormattedEut(buffer.readStringFromBuffer(32767));
        row.setVoltageTier(buffer.readVarIntFromBuffer());
        row.setCategory(EnergyMonitorCategory.values()[buffer.readVarIntFromBuffer()]);
        row.setWireless(buffer.readBoolean());
        row.setHighlightTarget(
            new EnergyMonitorHighlightTarget(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));
        return row;
    }

    private static boolean areRowsEqual(EnergyMonitorRowSnapshot left, EnergyMonitorRowSnapshot right) {
        return left == null ? right == null : left.sameAs(right);
    }

    private static int getModeButtonWidth(boolean wrapWithParentheses) {
        int maxWidth = 0;
        for (EnergyMonitorMode mode : EnergyMonitorMode.values()) {
            maxWidth = Math.max(maxWidth, getTextWidth(formatModeText(mode, wrapWithParentheses)));
        }
        return (int) Math.ceil((maxWidth + MODE_BUTTON_PADDING) * 1.5D);
    }

    private static int getTextWidth(String text) {
        String plainText = EnumChatFormatting.getTextWithoutFormattingCodes(text);
        return Math.max(1, plainText.length() * 6 + 2);
    }

    public static class MonitoringListWidget extends GTNLListWidget<IWidget, MonitoringListWidget> {

        private final IntSyncValue visibleRowCountSyncer;
        private final BooleanSyncValue hasMoreRowsSyncer;

        public MonitoringListWidget(int initialScrollY, IntSyncValue visibleRowCountSyncer,
            BooleanSyncValue hasMoreRowsSyncer) {
            super(initialScrollY);
            this.visibleRowCountSyncer = visibleRowCountSyncer;
            this.hasMoreRowsSyncer = hasMoreRowsSyncer;
        }

        @Override
        public boolean onMouseScroll(UpOrDown scrollDirection, int amount) {
            boolean handled = super.onMouseScroll(scrollDirection, amount);
            if (scrollDirection.isDown() && hasMoreRowsSyncer.getBoolValue() && isAtBottom()) {
                visibleRowCountSyncer
                    .setIntValue(visibleRowCountSyncer.getIntValue() + EnergyMonitor.LOAD_MORE_ROWS, true, true);
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
