package com.science.gtnl.common.gui.modularui;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.multiblock.QuantumComputer;
import com.science.gtnl.utils.Utils;

import gregtech.api.modularui2.GTGuiTextures;

public class QuantumComputerGui extends GTNLMultiBlockBaseGui<QuantumComputer> {

    private static final String WIDTH_SYNC_KEY = "quantumComputerWidth";
    private static final String HEIGHT_SYNC_KEY = "quantumComputerHeight";
    private static final String DEPTH_SYNC_KEY = "quantumComputerDepth";
    private static final String MAXIMUM_PARALLEL_SYNC_KEY = "quantumComputerMaximumParallel";
    private static final String USED_PARALLEL_SYNC_KEY = "quantumComputerUsedParallel";
    private static final String MAXIMUM_STORAGE_SYNC_KEY = "quantumComputerMaximumStorage";
    private static final String USED_STORAGE_SYNC_KEY = "quantumComputerUsedStorage";
    private static final String CUSTOM_NAME_SYNC_KEY = "quantumComputerCustomName";

    public QuantumComputerGui(QuantumComputer multiblock) {
        super(multiblock);
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        ModularPanel panel = super.build(guiData, syncManager, uiSettings);
        panel.child(createCirculationLogo())
            .child(createGtnlLogo());
        return panel;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager
            .syncValue(WIDTH_SYNC_KEY, new IntSyncValue(multiblock::getWidthForGui, multiblock::setWidthFromGui));
        syncManager
            .syncValue(HEIGHT_SYNC_KEY, new IntSyncValue(multiblock::getHeightForGui, multiblock::setHeightFromGui));
        syncManager
            .syncValue(DEPTH_SYNC_KEY, new IntSyncValue(multiblock::getDepthForGui, multiblock::setDepthFromGui));
        syncManager.syncValue(
            MAXIMUM_PARALLEL_SYNC_KEY,
            new IntSyncValue(multiblock::getMaximumParallelForGui, multiblock::setMaximumParallelFromGui));
        syncManager.syncValue(
            USED_PARALLEL_SYNC_KEY,
            new IntSyncValue(multiblock::getUsedParallelForGui, multiblock::setUsedParallelFromGui));
        syncManager.syncValue(
            MAXIMUM_STORAGE_SYNC_KEY,
            new LongSyncValue(multiblock::getMaximumStorageForGui, multiblock::setMaximumStorageFromGui));
        syncManager.syncValue(
            USED_STORAGE_SYNC_KEY,
            new LongSyncValue(multiblock::getUsedStorageForGui, multiblock::setUsedStorageFromGui));
        syncManager.syncValue(
            CUSTOM_NAME_SYNC_KEY,
            new StringSyncValue(multiblock::getDisplayNameForGui, multiblock::setCustomName).allowC2S());
    }

    @Override
    protected ParentWidget<?> createTerminalParentWidget(ModularPanel panel, PanelSyncManager syncManager) {
        return new ParentWidget<>().size(getTerminalWidgetWidth(), getTerminalWidgetHeight())
            .paddingTop(3)
            .paddingBottom(3)
            .paddingLeft(6)
            .paddingRight(0)
            .background(GTGuiTextures.PICTURE_SCREEN_BLACK)
            .child(
                createTerminalTextWidget(syncManager, panel)
                    .size(getTerminalWidgetWidth() - 8, getTerminalWidgetHeight() - 6)
                    .collapseDisabledChild());
    }

    @Override
    protected int getTerminalRowHeight() {
        return 85;
    }

    @Override
    protected int getTextBoxToInventoryGap() {
        return 22;
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        IntSyncValue widthSyncer = syncManager.findSyncHandler(WIDTH_SYNC_KEY, IntSyncValue.class);
        IntSyncValue heightSyncer = syncManager.findSyncHandler(HEIGHT_SYNC_KEY, IntSyncValue.class);
        IntSyncValue depthSyncer = syncManager.findSyncHandler(DEPTH_SYNC_KEY, IntSyncValue.class);
        IntSyncValue maximumParallelSyncer = syncManager.findSyncHandler(MAXIMUM_PARALLEL_SYNC_KEY, IntSyncValue.class);
        IntSyncValue usedParallelSyncer = syncManager.findSyncHandler(USED_PARALLEL_SYNC_KEY, IntSyncValue.class);
        LongSyncValue maximumStorageSyncer = syncManager.findSyncHandler(MAXIMUM_STORAGE_SYNC_KEY, LongSyncValue.class);
        LongSyncValue usedStorageSyncer = syncManager.findSyncHandler(USED_STORAGE_SYNC_KEY, LongSyncValue.class);

        return super.createTerminalTextWidget(syncManager, parent)
            .child(
                IKey.dynamic(
                    () -> StatCollector.translateToLocalFormatted(
                        "Info_QuantumComputer_00",
                        widthSyncer.getIntValue(),
                        heightSyncer.getIntValue(),
                        depthSyncer.getIntValue()))
                    .asWidget()
                    .textAlign(Alignment.CenterLeft)
                    .color(Color.WHITE.main)
                    .fullWidth())
            .child(
                IKey.dynamic(
                    () -> StatCollector.translateToLocalFormatted(
                        "Info_QuantumComputer_01",
                        NumberFormatUtil.formatNumber(maximumParallelSyncer.getIntValue()),
                        NumberFormatUtil.formatNumber(usedParallelSyncer.getIntValue()),
                        formatPercent(usedParallelSyncer.getIntValue(), maximumParallelSyncer.getIntValue())))
                    .asWidget()
                    .textAlign(Alignment.CenterLeft)
                    .color(Color.WHITE.main)
                    .fullWidth())
            .child(
                IKey.dynamic(
                    () -> StatCollector.translateToLocalFormatted(
                        "Info_QuantumComputer_02",
                        Utils.shortFormat(maximumStorageSyncer.getLongValue()),
                        usedStorageSyncer.getLongValue(),
                        formatPercent(usedParallelSyncer.getIntValue(), maximumStorageSyncer.getLongValue())))
                    .asWidget()
                    .textAlign(Alignment.CenterLeft)
                    .color(Color.WHITE.main)
                    .fullWidth());
    }

    @Override
    protected Flow createPanelGap(ModularPanel parent, PanelSyncManager syncManager) {
        return Flow.row()
            .fullWidth()
            .height(getTextBoxToInventoryGap())
            .paddingLeft(4)
            .paddingRight(25)
            .child(createCustomNameField(syncManager));
    }

    @Override
    protected IWidget createInventoryRow(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.row()
            .fullWidth()
            .height(76)
            .childIf(
                multiblock.doesBindPlayerInventory(),
                () -> SlotGroupWidget.playerInventory(false)
                    .marginLeft(4))
            .child(createButtonColumn(panel, syncManager));
    }

    @Override
    protected Flow createButtonColumn(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.column()
            .width(18)
            .leftRel(1, -2, 1)
            .top(36)
            .child(createStructureUpdateButton(syncManager))
            .childIf(
                multiblock.doesBindPlayerInventory(),
                () -> new ItemSlot()
                    .slot(new ModularSlot(multiblock.inventoryHandler, multiblock.getControllerSlotIndex()) {

                        @Override
                        public int getSlotStackLimit() {
                            return multiblock.getInventoryStackLimit();
                        }
                    }.singletonSlotGroup())
                    .backgroundOverlay(GTGuiTextures.SLOT_ITEM_DARK)
                    .marginTop(4));
    }

    @Override
    protected Widget<? extends Widget<?>> makeLogoWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return new Widget<>().size(0);
    }

    private IWidget createCustomNameField(PanelSyncManager syncManager) {
        StringSyncValue customNameSyncer = syncManager.findSyncHandler(CUSTOM_NAME_SYNC_KEY, StringSyncValue.class);
        return new TextFieldWidget().value(customNameSyncer)
            .setTextAlignment(Alignment.Center)
            .setTextColor(Color.WHITE.main)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
            .tooltipBuilder(tooltip -> tooltip.addLine(StatCollector.translateToLocal("Info_QuantumComputer_03")))
            .size(162, 18);
    }

    private IWidget createCirculationLogo() {
        return new IDrawable.DrawableWidget(new DynamicDrawable(() -> GTNLMui2Textures.PICTURE_CIRCULATION))
            .size(18, 18)
            .pos(172, 49);
    }

    private IWidget createGtnlLogo() {
        return new IDrawable.DrawableWidget(GTNLMui2Textures.PICTURE_GTNL_LOGO).size(18, 18)
            .pos(172, 67);
    }

    private static String formatPercent(long used, long maximum) {
        return String.format("%.1f%%", maximum > 0 ? (double) used / maximum * 100.0 : 0.0);
    }

    private static String formatPercent(int used, int maximum) {
        return String.format("%.1f%%", maximum > 0 ? (double) used / maximum * 100.0 : 0.0);
    }
}
