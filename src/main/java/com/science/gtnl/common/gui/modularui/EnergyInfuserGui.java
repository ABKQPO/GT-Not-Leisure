package com.science.gtnl.common.gui.modularui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.network.NetworkUtils;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.DynamicLinkedSyncHandler;
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.EnergyInfuser;

import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

public class EnergyInfuserGui extends GTNLTTMultiBlockBaseGui<EnergyInfuser> {

    private static final String STORED_ITEMS_SYNC_KEY = "energyInfuserStoredItems";
    private static final String STORED_ITEMS_WIDGET_SYNC_KEY = "energyInfuserStoredItemsWidget";
    private static final String PROGRESS_TIME_SYNC_KEY = "energyInfuserProgressTime";
    private static final String MAX_PROGRESS_TIME_SYNC_KEY = "energyInfuserMaxProgressTime";

    public EnergyInfuserGui(EnergyInfuser multiblock) {
        super(multiblock);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        GenericListSyncHandler<ItemStack> storedItemsSyncer = new GenericListSyncHandler<>(
            multiblock::getStoredItemsForGui,
            multiblock::setStoredItemsFromGui,
            NetworkUtils::readItemStack,
            NetworkUtils::writeItemStack,
            EnergyInfuserGui::areItemStacksEqual,
            EnergyInfuserGui::copyItemStack);
        syncManager.syncValue(STORED_ITEMS_SYNC_KEY, storedItemsSyncer);
        syncManager.syncValue(
            STORED_ITEMS_WIDGET_SYNC_KEY,
            new DynamicLinkedSyncHandler<>(storedItemsSyncer)
                .widgetProvider((panelSyncManager, syncValue) -> createStoredItemsWidget(syncValue.getValue())));
        syncManager.syncValue(
            PROGRESS_TIME_SYNC_KEY,
            new IntSyncValue(multiblock::getProgressTimeForGui, multiblock::setProgressTimeFromGui));
        syncManager.syncValue(
            MAX_PROGRESS_TIME_SYNC_KEY,
            new IntSyncValue(multiblock::getMaxProgressTimeForGui, multiblock::setMaxProgressTimeFromGui));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        IntSyncValue maxProgressTimeSyncer = syncManager
            .findSyncHandler(MAX_PROGRESS_TIME_SYNC_KEY, IntSyncValue.class);
        return super.createTerminalTextWidget(syncManager, parent)
            .child(
                IKey.dynamic(multiblock::generateCurrentProgressForGui)
                    .asWidget()
                    .textAlign(Alignment.TopLeft)
                    .size(180, 12)
                    .setEnabledIf(
                        widget -> !getStoredItemsSyncer(syncManager).getValue()
                            .isEmpty() || maxProgressTimeSyncer.getIntValue() > 0))
            .child(createStoredItemsWidget(syncManager));
    }

    private IWidget createStoredItemsWidget(PanelSyncManager syncManager) {
        DynamicLinkedSyncHandler<?> storedItemsWidgetSyncer = syncManager
            .findSyncHandler(STORED_ITEMS_WIDGET_SYNC_KEY, DynamicLinkedSyncHandler.class);
        return new DynamicSyncedWidget<>().syncHandler(storedItemsWidgetSyncer)
            .initialChild(createStoredItemsWidget(Collections.emptyList()))
            .fullWidth();
    }

    private IWidget createStoredItemsWidget(List<ItemStack> storedItems) {
        return new ListWidget<>().fullWidth()
            .crossAxisAlignment(Alignment.CrossAxis.START)
            .children(createStoredItemRows(storedItems));
    }

    private List<IWidget> createStoredItemRows(List<ItemStack> storedItems) {
        if (storedItems == null || storedItems.isEmpty()) {
            return Collections.emptyList();
        }

        Object2LongOpenHashMap<ItemStack> nameToAmount = new Object2LongOpenHashMap<>();
        for (ItemStack item : storedItems) {
            if (item == null || item.stackSize <= 0) continue;
            nameToAmount.addTo(item, item.stackSize);
        }

        List<Object2LongMap.Entry<ItemStack>> sortedList = new ArrayList<>(nameToAmount.object2LongEntrySet());
        sortedList.sort((left, right) -> Long.compare(right.getLongValue(), left.getLongValue()));

        List<IWidget> rows = new ArrayList<>();
        for (Object2LongMap.Entry<ItemStack> entry : sortedList) {
            rows.add(createStoredItemRow(entry));
        }
        return rows;
    }

    private IWidget createStoredItemRow(Object2LongMap.Entry<ItemStack> entry) {
        ItemStack stack = entry.getKey()
            .copy();
        long itemCount = entry.getLongValue();
        String itemName = stack.getDisplayName();
        String itemAmountString = EnumChatFormatting.WHITE + " x "
            + EnumChatFormatting.GOLD
            + GTUtility.formatShortenedLong(itemCount)
            + EnumChatFormatting.WHITE
            + multiblock.appendRateForGui(false, itemCount, true);
        String lineText = EnumChatFormatting.AQUA + GTUtility.truncateText(itemName, 40 - itemAmountString.length())
            + itemAmountString;
        String lineTooltip = EnumChatFormatting.AQUA + itemName
            + "\n"
            + multiblock.appendRateForGui(false, itemCount, false);

        return Flow.row()
            .height(10)
            .fullWidth()
            .child(
                new ItemDisplayWidget().item(stack)
                    .size(8, 8)
                    .disableThemeBackground(true))
            .child(
                IKey.str(lineText)
                    .asWidget()
                    .textAlign(Alignment.CenterLeft)
                    .tooltipBuilder(tooltip -> tooltip.addLine(lineTooltip))
                    .marginLeft(2)
                    .fullWidth());
    }

    @SuppressWarnings("unchecked")
    private GenericListSyncHandler<ItemStack> getStoredItemsSyncer(PanelSyncManager syncManager) {
        return syncManager.findSyncHandler(STORED_ITEMS_SYNC_KEY, GenericListSyncHandler.class);
    }

    private static boolean areItemStacksEqual(ItemStack left, ItemStack right) {
        if (left == null || right == null) {
            return left == right;
        }
        return left.isItemEqual(right) && left.stackSize == right.stackSize;
    }

    private static ItemStack copyItemStack(ItemStack stack) {
        return stack == null ? null : stack.copy();
    }
}
