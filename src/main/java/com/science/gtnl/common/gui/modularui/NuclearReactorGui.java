package com.science.gtnl.common.gui.modularui;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.DynamicLinkedSyncHandler;
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.FluidDisplayWidget;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.machine.multiblock.NuclearReactor;
import com.science.gtnl.common.machine.multiblock.NuclearReactor.CoreCellDisplay;

import gregtech.api.casing.Casings;
import gregtech.api.modularui2.GTGuiTextures;

public class NuclearReactorGui extends GTNLMultiBlockBaseGui<NuclearReactor> {

    private static final String STEAM_RATE_SYNC_KEY = "nuclearReactorSteamRate";
    private static final String TIER_SYNC_KEY = "nuclearReactorTier";
    private static final String CELLS_SYNC_KEY = "nuclearReactorCoreCells";
    private static final String CELLS_WIDGET_SYNC_KEY = "nuclearReactorCoreCellsWidget";
    private static final int CELL_SIZE = 19;
    private static final int GRID_PADDING = 1;
    private static final int FRAME_BORDER_THICKNESS = 2;
    private static final int GRID_TOP_MARGIN = 2;
    private static final int STEAM_LINE_HEIGHT = 12;
    private static final int EXTRA_TERMINAL_HEIGHT = 34;
    private static final int TITLE_CLEARANCE = 12;
    private static final int PANEL_TOP_OFFSET = 0;
    private static final int FRAME_BORDER_COLOR = 0xFFC8C8C8;
    private static final int STRUCTURE_FILL_COLOR = 0xFF2E2E2E;
    private static final int STRUCTURE_EDGE_COLOR = 0xFF707070;
    private IntSyncValue steamRateSyncer;
    private IntSyncValue tierSyncer;

    public NuclearReactorGui(NuclearReactor multiblock) {
        super(multiblock);
    }

    @Override
    protected ModularPanel getBasePanel(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        return super.getBasePanel(guiData, syncManager, uiSettings)
            .topRel(0.5f, -super.getBasePanelHeight() / 2 + PANEL_TOP_OFFSET, 0f);
    }

    @Override
    protected boolean shouldDisplayVoidExcess() {
        return false;
    }

    @Override
    protected boolean shouldDisplayInputSeparation() {
        return false;
    }

    @Override
    protected boolean shouldDisplayBatchMode() {
        return false;
    }

    @Override
    protected boolean shouldDisplayRecipeLock() {
        return false;
    }

    @Override
    protected Flow createTerminalRow(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createTerminalRow(panel, syncManager).marginTop(TITLE_CLEARANCE);
    }

    @Override
    protected int getTerminalRowHeight() {
        return super.getTerminalRowHeight() + terminalExtra();
    }

    @Override
    protected int getTerminalWidgetHeight() {
        return super.getTerminalWidgetHeight() + terminalExtra();
    }

    @Override
    protected int getBasePanelHeight() {
        return super.getBasePanelHeight() + terminalExtra();
    }

    private int terminalExtra() {
        return EXTRA_TERMINAL_HEIGHT;
    }

    private int availableTerminalContentHeight() {
        return super.getTerminalRowHeight() - 8 + terminalExtra();
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);

        steamRateSyncer = new IntSyncValue(
            multiblock::getSteamOutputPerSecondForGui,
            multiblock::setSteamOutputPerSecondFromGui);
        syncManager.syncValue(STEAM_RATE_SYNC_KEY, steamRateSyncer);

        tierSyncer = new IntSyncValue(multiblock::getReactorTierForGui, multiblock::setReactorTierFromGui);
        syncManager.syncValue(TIER_SYNC_KEY, tierSyncer);

        GenericListSyncHandler<CoreCellDisplay> cellsSyncer = new GenericListSyncHandler<>(
            multiblock::getCoreCellDisplaysForGui,
            multiblock::setCoreCellDisplaysFromGui,
            buffer -> CoreCellDisplay.deserialize(buffer.readNBTTagCompoundFromBuffer()),
            (buffer, entry) -> buffer.writeNBTTagCompoundToBuffer(entry.serialize()),
            CoreCellDisplay::equals,
            CoreCellDisplay::copy);
        syncManager.syncValue(CELLS_SYNC_KEY, cellsSyncer);
        syncManager.syncValue(
            CELLS_WIDGET_SYNC_KEY,
            new DynamicLinkedSyncHandler<>(cellsSyncer).widgetProvider(this::buildCoreGridWidget));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        super.createTerminalTextWidget(syncManager, parent);

        IntSyncValue rateSyncer = syncManager.findSyncHandler(STEAM_RATE_SYNC_KEY, IntSyncValue.class);

        ListWidget<IWidget, ?> list = new ListWidget<>().fullWidth()
            .crossAxisAlignment(Alignment.CrossAxis.CENTER);

        list.child(
            IKey.dynamic(
                () -> StatCollector.translateToLocal("gtnl.machine.nuclear_reactor.gui.steam_rate") + ": "
                    + rateSyncer.getIntValue()
                    + " L/s")
                .asWidget()
                .textAlign(Alignment.TopLeft)
                .fullWidth()
                .height(STEAM_LINE_HEIGHT));

        list.child(createShutdownReasonWidget(syncManager));
        list.child(createStructureErrorWidget(syncManager));
        list.child(createDynamicCoreGrid(syncManager));
        return list;
    }

    private IWidget createDynamicCoreGrid(PanelSyncManager syncManager) {
        DynamicLinkedSyncHandler<?> syncer = syncManager
            .findSyncHandler(CELLS_WIDGET_SYNC_KEY, DynamicLinkedSyncHandler.class);
        return new DynamicSyncedWidget<>().syncHandler(syncer)
            .initialChild(createCoreGrid(Collections.emptyList(), getSyncedTier(syncManager)))
            .fullWidth();
    }

    private IWidget buildCoreGridWidget(PanelSyncManager panelSyncManager,
        GenericListSyncHandler<CoreCellDisplay> syncValue) {
        return createCoreGrid(syncValue.getValue(), getSyncedTier(panelSyncManager));
    }

    private int getSyncedTier(PanelSyncManager syncManager) {
        IntSyncValue syncer = syncManager.findSyncHandler(TIER_SYNC_KEY, IntSyncValue.class);
        if (syncer != null) return syncer.getIntValue();
        return multiblock.getReactorTierForGui();
    }

    private IWidget createCoreGrid(List<CoreCellDisplay> displays, int tier) {
        int n = NuclearReactor.getCoreSizeForTier(tier);
        int cell = CELL_SIZE;
        Flow column = Flow.column()
            .coverChildren();
        for (int row = n - 1; row >= 0; row--) {
            column.child(createCoreRow(displays, tier, n, row, cell));
        }

        int gridSize = n * cell;
        int frameSize = gridSize + 2 * GRID_PADDING;
        int area = Math.max(frameSize, availableGridArea());
        int width = Math.max(frameSize, getTerminalWidgetWidth() - 4);
        int left = (width - frameSize) / 2;
        int top = GRID_TOP_MARGIN + (area - frameSize) / 2;

        ParentWidget<?> frame = new ParentWidget<>().size(frameSize, frameSize)
            .background(GTGuiTextures.BACKGROUND_GRAY_BORDER)
            .overlay(
                new Rectangle().color(FRAME_BORDER_COLOR)
                    .hollow(FRAME_BORDER_THICKNESS))
            .child(column.pos(GRID_PADDING, GRID_PADDING));

        return new ParentWidget<>().size(width, GRID_TOP_MARGIN + area)
            .child(
                frame.left(left)
                    .top(top));
    }

    private int availableGridArea() {
        int above = STEAM_LINE_HEIGHT + GRID_TOP_MARGIN;
        return Math.max(7 * CELL_SIZE + 2 * GRID_PADDING, availableTerminalContentHeight() - above);
    }

    private IWidget createCoreRow(List<CoreCellDisplay> displays, int tier, int n, int row, int cell) {
        int lead = 0;
        while (lead < n && !NuclearReactor.isValidCoreCellForTier(tier, row, lead)) lead++;
        int trail = 0;
        while (trail < n - lead && !NuclearReactor.isValidCoreCellForTier(tier, row, n - 1 - trail)) trail++;

        Flow rowFlow = Flow.row()
            .coverChildren();
        if (lead > 0) rowFlow.child(new Widget<>().size(lead * cell, cell));
        for (int col = lead; col < n - trail; col++) {
            rowFlow.child(createCoreCellWidget(displays, NuclearReactor.getCoreSlotIndexForTier(tier, row, col), cell));
        }
        if (trail > 0) rowFlow.child(new Widget<>().size(trail * cell, cell));
        return rowFlow;
    }

    private IWidget createCoreCellWidget(List<CoreCellDisplay> displays, int slot, int cell) {
        CoreCellDisplay display = (displays == null || slot < 0 || slot >= displays.size()) ? null : displays.get(slot);
        int kind = display == null ? CoreCellDisplay.KIND_NONE : display.kind;

        if (kind == CoreCellDisplay.KIND_ITEM_BUS) {
            ItemStack stack = display.item;

            if (stack == null) {
                return new Widget<>().size(cell)
                    .background(GTGuiTextures.SLOT_ITEM_STANDARD);
            }
            ItemDisplayWidget widget = new ItemDisplayWidget().size(cell)
                .background(GTGuiTextures.SLOT_ITEM_STANDARD);
            widget.item(stack.copy());

            widget.tooltip()
                .setAutoUpdate(true)
                .tooltipBuilder(t -> t.addFromItem(stack));
            return widget;
        }

        if (kind == CoreCellDisplay.KIND_FLUID_HATCH) {
            FluidStack fluid = display.fluid;
            if (fluid == null) {
                return new Widget<>().size(cell)
                    .background(GTGuiTextures.SLOT_FLUID_STANDARD);
            }
            FluidDisplayWidget widget = new FluidDisplayWidget().size(cell)
                .background(GTGuiTextures.SLOT_FLUID_STANDARD);
            widget.value(fluid.copy())
                .displayAmount(true);
            widget.tooltip()
                .setAutoUpdate(true)
                .tooltipBuilder(t -> t.addFromFluid(fluid));
            return widget;
        }

        ItemStack casing = Casings.HastelloyNSealantBlock.toStack(1);
        ItemDisplayWidget widget = new ItemDisplayWidget().size(cell)
            .background(new Rectangle().color(STRUCTURE_FILL_COLOR))
            .overlay(
                new Rectangle().color(STRUCTURE_EDGE_COLOR)
                    .hollow(1));
        widget.item(casing);
        widget.tooltip()
            .setAutoUpdate(true)
            .tooltipBuilder(t -> t.addFromItem(casing));
        return widget;
    }
}
