package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.machine.hatch.TypeFilteredInputBusME;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.util.GTRecipeBuilder;

public class TypeFilteredInputBusMEGui extends OredictInputBusMEGui {

    private static final String MOD_ID_SYNC_KEY = "typeFilterModId";
    private static final String ITEM_NAME_SYNC_KEY = "typeFilterItemName";
    private static final String ITEM_META_SYNC_KEY = "typeFilterItemMeta";

    private final TypeFilteredInputBusME typeFilteredHatch;

    public TypeFilteredInputBusMEGui(TypeFilteredInputBusME hatch) {
        super(hatch);
        this.typeFilteredHatch = hatch;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            MOD_ID_SYNC_KEY,
            new StringSyncValue(typeFilteredHatch::getModidForGui, typeFilteredHatch::setModid).allowC2S());
        syncManager.syncValue(
            ITEM_NAME_SYNC_KEY,
            new StringSyncValue(typeFilteredHatch::getNameFilterForGui, typeFilteredHatch::setNameFilter).allowC2S());
        syncManager.syncValue(
            ITEM_META_SYNC_KEY,
            new IntSyncValue(typeFilteredHatch::getMetaFilter, typeFilteredHatch::setMetaFilter).allowC2S());
    }

    @Override
    protected ModularPanel createStackSizeConfigurationPanel(ModularPanel parent, PanelSyncManager syncManager) {
        IntSyncValue minStackSyncer = syncManager.findSyncHandler(MIN_AUTO_PULL_SYNC_KEY, IntSyncValue.class);
        IntSyncValue refreshSyncer = syncManager.findSyncHandler(AUTO_PULL_REFRESH_SYNC_KEY, IntSyncValue.class);
        BooleanSyncValue recipeCheckSyncer = syncManager
            .findSyncHandler(EXPEDITE_RECIPE_SYNC_KEY, BooleanSyncValue.class);
        StringSyncValue modIdSyncer = syncManager.findSyncHandler(MOD_ID_SYNC_KEY, StringSyncValue.class);
        StringSyncValue itemNameSyncer = syncManager.findSyncHandler(ITEM_NAME_SYNC_KEY, StringSyncValue.class);
        IntSyncValue itemMetaSyncer = syncManager.findSyncHandler(ITEM_META_SYNC_KEY, IntSyncValue.class);

        Flow mainColumn = Flow.column()
            .coverChildren()
            .marginTop(15)
            .childPadding(3)
            .child(createLabelledIntegerField("GT5U.machines.stocking_bus.min_stack_size", minStackSyncer))
            .child(createLabelledIntegerField("GT5U.machines.stocking_bus.refresh_time", refreshSyncer))
            .child(createRecipeCheckRow(recipeCheckSyncer))
            .child(createLabelledStringField("Info_TypeFilteredInputBusME_ModID", modIdSyncer))
            .child(createLabelledStringField("Info_TypeFilteredInputBusME_ItemName", itemNameSyncer))
            .child(createLabelledMetaField(itemMetaSyncer));

        Dialog<?> panel = createDialog(CONFIG_PANEL_KEY, parent);
        panel.coverChildren()
            .padding(5)
            .leftRel(1)
            .topRel(0);
        panel.child(ButtonWidget.panelCloseButton());
        panel.child(mainColumn);
        return panel;
    }

    private Flow createLabelledIntegerField(String labelKey, IntSyncValue syncer) {
        return Flow.column()
            .coverChildren()
            .child(
                IKey.lang(labelKey)
                    .asWidget()
                    .maxWidth(72)
                    .textAlign(Alignment.Center))
            .child(createIntegerField(syncer));
    }

    private Flow createLabelledStringField(String labelKey, StringSyncValue syncer) {
        return Flow.column()
            .coverChildren()
            .child(
                IKey.lang(labelKey)
                    .asWidget()
                    .maxWidth(72)
                    .textAlign(Alignment.Center))
            .child(createStringField(syncer));
    }

    private Flow createLabelledMetaField(IntSyncValue syncer) {
        return Flow.column()
            .coverChildren()
            .child(
                IKey.lang("Info_TypeFilteredInputBusME_ItemMeta")
                    .asWidget()
                    .maxWidth(72)
                    .textAlign(Alignment.Center)
                    .addTooltipLine(IKey.str("* = " + GTRecipeBuilder.WILDCARD)))
            .child(createMetaField(syncer));
    }

    private TextFieldWidget createStringField(StringSyncValue syncer) {
        return new TextFieldWidget().value(syncer)
            .setTextAlignment(Alignment.Center)
            .setTextColor(Color.WHITE.main)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
            .size(72, 18);
    }

    private TextFieldWidget createMetaField(IntSyncValue syncer) {
        return new TextFieldWidget().value(syncer)
            .numbersInt(0, GTRecipeBuilder.WILDCARD)
            .formatAsInteger(true)
            .scrollValues(1, 4, 64, 256)
            .setTextAlignment(Alignment.Center)
            .setTextColor(Color.WHITE.main)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
            .size(72, 18);
    }
}
