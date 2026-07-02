package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.DynamicLinkedSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.science.gtnl.common.machine.multiblock.SingularityDataHub;

public class SingularityDataHubGui extends GTNLMultiBlockBaseGui<SingularityDataHub> {

    private static final String TYPE_COUNT_SYNC_KEY = "singularityDataHubTypeCounts";
    private static final String TYPE_COUNT_WIDGET_SYNC_KEY = "singularityDataHubTypeCountRows";
    private static final String TYPE_COUNT_TRANSLATION_KEY = "Info_SingularityDataHub_TypeCount";

    public SingularityDataHubGui(SingularityDataHub multiblock) {
        super(multiblock);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        StringSyncValue typeCountSyncer = new StringSyncValue(
            multiblock::getTypeCountPayloadForGui,
            multiblock::setTypeCountPayloadFromGui);
        syncManager.syncValue(TYPE_COUNT_SYNC_KEY, typeCountSyncer);
        syncManager.syncValue(
            TYPE_COUNT_WIDGET_SYNC_KEY,
            new DynamicLinkedSyncHandler<>(typeCountSyncer)
                .widgetProvider((panelSyncManager, syncValue) -> createTypeCountRows(syncValue.getValue())));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return super.createTerminalTextWidget(syncManager, parent).child(createTypeCountRows(syncManager));
    }

    private IWidget createTypeCountRows(PanelSyncManager syncManager) {
        DynamicLinkedSyncHandler<?> typeCountWidgetSyncer = syncManager
            .findSyncHandler(TYPE_COUNT_WIDGET_SYNC_KEY, DynamicLinkedSyncHandler.class);
        return new DynamicSyncedWidget<>().syncHandler(typeCountWidgetSyncer)
            .initialChild(createTypeCountRows(""))
            .fullWidth();
    }

    private IWidget createTypeCountRows(String payload) {
        return new ListWidget<>().fullWidth()
            .crossAxisAlignment(Alignment.CrossAxis.START)
            .children(VaultTypeCountFormatter.createTypeCountRows(payload, TYPE_COUNT_TRANSLATION_KEY));
    }
}
