package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DrawableStack;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.FluidDisplayWidget;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.multiblock.ElementCopying;
import com.science.gtnl.common.machine.multiblock.ElementCopying.ElementCopyingEntry;
import com.science.gtnl.common.machine.multiblock.ElementCopying.FluidCopyingEntry;
import com.science.gtnl.common.machine.multiblock.ElementCopying.ItemCopyingEntry;
import com.science.gtnl.common.recipe.gtnl.ElementCopyingRecipes;

import gregtech.api.modularui2.GTGuiTextures;

public class ElementCopyingGui extends GTNLMultiBlockBaseGui<ElementCopying> {

    private static final String ITEM_SELECTION_SYNC_KEY = "elementCopyingItemSelection";
    private static final String FLUID_SELECTION_SYNC_KEY = "elementCopyingFluidSelection";
    private static final String SELECTION_PANEL_KEY = "elementCopyingSelectionPanel";
    private static final int PANEL_WIDTH = 198;
    private static final int ENTRY_COLUMNS = 10;
    private static final int ENTRY_BUTTON_SIZE = 16;
    private static final int ENTRY_STEP = 18;
    private static final int ENTRY_START_X = 9;
    private static final int ENTRY_START_Y = 10;

    public ElementCopyingGui(ElementCopying multiblock) {
        super(multiblock);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            ITEM_SELECTION_SYNC_KEY,
            GenericListSyncHandler.<ItemCopyingEntry>builder()
                .getter(multiblock::getSelectedItemEntriesForGui)
                .setter(multiblock::setSelectedItemEntriesFromGui)
                .serializer((buffer, entry) -> buffer.writeNBTTagCompoundToBuffer(entry.serialize()))
                .deserializer(buffer -> ItemCopyingEntry.deserialize(buffer.readNBTTagCompoundFromBuffer()))
                .immutableCopy()
                .build()
                .allowC2S());
        syncManager.syncValue(
            FLUID_SELECTION_SYNC_KEY,
            GenericListSyncHandler.<FluidCopyingEntry>builder()
                .getter(multiblock::getSelectedFluidEntriesForGui)
                .setter(multiblock::setSelectedFluidEntriesFromGui)
                .serializer((buffer, entry) -> buffer.writeNBTTagCompoundToBuffer(entry.serialize()))
                .deserializer(buffer -> FluidCopyingEntry.deserialize(buffer.readNBTTagCompoundFromBuffer()))
                .immutableCopy()
                .build()
                .allowC2S());
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        Flow row = super.createRightPanelGapRow(parent, syncManager);
        row.child(createSelectionPanelButton(parent, syncManager));
        return row;
    }

    private IWidget createSelectionPanelButton(ModularPanel parent, PanelSyncManager syncManager) {
        IPanelHandler selectionPanel = syncManager.syncedPanel(
            SELECTION_PANEL_KEY,
            true,
            (panelSyncManager, panelHandler) -> createSelectionPanel(parent, syncManager));
        return new ButtonWidget<>().size(18, 18)
            .overlay(new DrawableStack(GTGuiTextures.BUTTON_STANDARD, GTNLMui2Textures.OVERLAY_BUTTON_ARROW_GREEN_UP))
            .onMousePressed(mouseButton -> {
                if (selectionPanel.isPanelOpen()) {
                    selectionPanel.closePanel();
                } else {
                    selectionPanel.openPanel();
                }
                return true;
            })
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ModularPanel createSelectionPanel(ModularPanel parent, PanelSyncManager syncManager) {
        Dialog<?> panel = new Dialog<>(SELECTION_PANEL_KEY);
        panel.relative(parent)
            .leftRel(1)
            .topRel(0)
            .size(PANEL_WIDTH, getSelectionPanelHeight())
            .background(GTGuiTextures.BACKGROUND_POPUP_STANDARD);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true);
        panel.child(ButtonWidget.panelCloseButton());
        panel.child(createEntryGrid(syncManager));
        panel.child(createSummaryText(syncManager));
        return panel;
    }

    private IWidget createEntryGrid(PanelSyncManager syncManager) {
        ParentWidget<?> grid = new ParentWidget<>().size(PANEL_WIDTH, getEntryGridHeight());
        List<ElementCopyingEntry> entries = ElementCopyingRecipes.ENTRIES;
        for (int i = 0; i < entries.size(); i++) {
            int col = i % ENTRY_COLUMNS;
            int row = i / ENTRY_COLUMNS;
            grid.child(
                createEntryButton(entries.get(i), syncManager)
                    .pos(ENTRY_START_X + col * ENTRY_STEP, ENTRY_START_Y + row * ENTRY_STEP));
        }
        return grid;
    }

    private Widget<?> createEntryButton(ElementCopyingEntry entry, PanelSyncManager syncManager) {
        if (entry instanceof ItemCopyingEntry itemEntry) {
            return createItemEntryButton(itemEntry, getItemSelectionSyncer(syncManager));
        }
        if (entry instanceof FluidCopyingEntry fluidEntry) {
            return createFluidEntryButton(fluidEntry, getFluidSelectionSyncer(syncManager));
        }
        return new ParentWidget<>().size(ENTRY_BUTTON_SIZE);
    }

    private Widget<?> createItemEntryButton(ItemCopyingEntry entry, GenericListSyncHandler<ItemCopyingEntry> syncer) {
        ItemStack stack = entry.itemId()
            .toStack(1);
        return createSelectionButton(
            new ItemDisplayWidget().item(stack)
                .displayAmount(false)
                .disableThemeBackground(true)
                .disableHoverThemeBackground(true)
                .size(ENTRY_BUTTON_SIZE),
            () -> syncer.getValue()
                .contains(entry),
            () -> toggleEntry(syncer, entry)).tooltipBuilder(tooltip -> tooltip.addLine(stack.getDisplayName()));
    }

    private Widget<?> createFluidEntryButton(FluidCopyingEntry entry,
        GenericListSyncHandler<FluidCopyingEntry> syncer) {
        FluidStack stack = entry.fluidId()
            .getFluidStack();
        return createSelectionButton(
            new FluidDisplayWidget().value(stack)
                .displayAmount(false)
                .disableThemeBackground(true)
                .disableHoverThemeBackground(true)
                .size(ENTRY_BUTTON_SIZE),
            () -> syncer.getValue()
                .contains(entry),
            () -> toggleEntry(syncer, entry)).tooltipBuilder(tooltip -> tooltip.addLine(stack.getLocalizedName()));
    }

    private ButtonWidget<?> createSelectionButton(IWidget icon, SelectedState selectedState, Runnable toggleSelection) {
        return new ButtonWidget<>().size(ENTRY_BUTTON_SIZE)
            .background(
                new DynamicDrawable(
                    () -> selectedState.isSelected() ? GTGuiTextures.BUTTON_STANDARD_PRESSED
                        : GTGuiTextures.BUTTON_STANDARD))
            .child(icon)
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> toggleSelection.run()))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private <T> void toggleEntry(GenericListSyncHandler<T> syncer, T entry) {
        syncer.modifyValue(entries -> {
            if (!entries.remove(entry)) {
                entries.add(entry);
            }
        });
    }

    private IWidget createSummaryText(PanelSyncManager syncManager) {
        GenericListSyncHandler<ItemCopyingEntry> itemSyncer = getItemSelectionSyncer(syncManager);
        GenericListSyncHandler<FluidCopyingEntry> fluidSyncer = getFluidSelectionSyncer(syncManager);
        return IKey
            .dynamic(
                () -> StatCollector.translateToLocalFormatted(
                    "Info_ElementCopying_00",
                    itemSyncer.getValue()
                        .size()
                        + fluidSyncer.getValue()
                            .size(),
                    NumberFormatUtil.formatNumber(
                        getTotalCost(itemSyncer.getValue(), ElementCopyingEntry::costUUM)
                            + getTotalCost(fluidSyncer.getValue(), ElementCopyingEntry::costUUM)),
                    NumberFormatUtil.formatNumber(
                        getTotalCost(itemSyncer.getValue(), ElementCopyingEntry::costEU)
                            + getTotalCost(fluidSyncer.getValue(), ElementCopyingEntry::costEU))))
            .asWidget()
            .textAlign(Alignment.Center)
            .color(Color.WHITE.main)
            .pos(9, getSelectionPanelHeight() - 28)
            .size(180, 12);
    }

    private long getTotalCost(List<? extends ElementCopyingEntry> entries, CostGetter costGetter) {
        long total = 0;
        for (ElementCopyingEntry entry : entries) {
            total += costGetter.getCost(entry);
        }
        return total;
    }

    private int getSelectionPanelHeight() {
        return 42 + (ElementCopyingRecipes.ENTRIES.size() + 8) / ENTRY_COLUMNS * ENTRY_STEP;
    }

    private int getEntryGridHeight() {
        return Math.max(ENTRY_START_Y + ENTRY_BUTTON_SIZE, getSelectionPanelHeight() - 30);
    }

    @SuppressWarnings("unchecked")
    private GenericListSyncHandler<ItemCopyingEntry> getItemSelectionSyncer(PanelSyncManager syncManager) {
        return syncManager.findSyncHandler(ITEM_SELECTION_SYNC_KEY, GenericListSyncHandler.class);
    }

    @SuppressWarnings("unchecked")
    private GenericListSyncHandler<FluidCopyingEntry> getFluidSelectionSyncer(PanelSyncManager syncManager) {
        return syncManager.findSyncHandler(FLUID_SELECTION_SYNC_KEY, GenericListSyncHandler.class);
    }

    private interface SelectedState {

        boolean isSelected();
    }

    private interface CostGetter {

        long getCost(ElementCopyingEntry entry);
    }
}
