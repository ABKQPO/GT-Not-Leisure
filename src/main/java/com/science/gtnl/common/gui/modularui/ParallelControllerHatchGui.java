package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;

public class ParallelControllerHatchGui extends MTEHatchBaseGui<ParallelControllerHatch> {

    private static final String PARALLEL_SYNC_KEY = "parallel";

    public ParallelControllerHatchGui(ParallelControllerHatch hatch) {
        super(hatch);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            PARALLEL_SYNC_KEY,
            new IntSyncValue(machine::getParallel, machine::setParallelFromGui).allowC2S());
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        IntSyncValue parallelSyncer = syncManager.findSyncHandler(PARALLEL_SYNC_KEY, IntSyncValue.class);
        return super.createContentSection(panel, syncManager).child(
            IKey.lang("Info_ParallelControllerHatch_00")
                .asWidget()
                .textAlign(Alignment.Center)
                .pos(49, 18)
                .size(81, 14))
            .child(
                new TextFieldWidget().value(parallelSyncer)
                    .numbersInt(1, machine.getMaxParallel())
                    .scrollValues(1, 4, 64, 256)
                    .setTextAlignment(Alignment.Center)
                    .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
                    .pos(54, 36)
                    .size(70, 18));
    }

    @Override
    protected UITexture getLogoTexture() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO;
    }

    @Override
    protected boolean supportsFluidScreen() {
        return false;
    }

    @Override
    protected boolean supportsFluidIOColumn() {
        return false;
    }

    @Override
    protected boolean supportsMuffler() {
        return false;
    }

    @Override
    protected boolean supportsPowerSwitch() {
        return false;
    }
}
