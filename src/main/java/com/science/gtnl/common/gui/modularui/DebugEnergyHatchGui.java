package com.science.gtnl.common.gui.modularui;

import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.DebugEnergyHatch;

import gregtech.api.enums.GTValues;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;

public class DebugEnergyHatchGui extends MTEHatchBaseGui<DebugEnergyHatch> {

    private static final String EUT_SYNC_KEY = "eut";
    private static final String AMP_SYNC_KEY = "amp";
    private static final int CONTENT_OFFSET_X = -7;
    private static final int CONTENT_OFFSET_Y = -4;

    public DebugEnergyHatchGui(DebugEnergyHatch hatch) {
        super(hatch);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(EUT_SYNC_KEY, new LongSyncValue(machine::getMEUT, machine::setMEUTFromGui).allowC2S());
        syncManager.syncValue(AMP_SYNC_KEY, new LongSyncValue(machine::getMAMP, machine::setMAMPFromGui).allowC2S());
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        LongSyncValue eutSyncer = syncManager.findSyncHandler(EUT_SYNC_KEY, LongSyncValue.class);
        LongSyncValue ampSyncer = syncManager.findSyncHandler(AMP_SYNC_KEY, LongSyncValue.class);

        return super.createContentSection(panel, syncManager).child(createDebugControls(eutSyncer, ampSyncer));
    }

    private ParentWidget<?> createDebugControls(LongSyncValue eutSyncer, LongSyncValue ampSyncer) {
        return new ParentWidget<>().pos(CONTENT_OFFSET_X, CONTENT_OFFSET_Y)
            .size(176, 76)
            .child(
                GTGuiTextures.PICTURE_SCREEN_BLACK.asWidget()
                    .pos(43, 4)
                    .size(90, 72))
            .child(
                IKey.dynamic(() -> "TIER: " + getTierName(eutSyncer))
                    .asWidget()
                    .color(0xFFFFFFFF)
                    .pos(46, 22)
                    .size(84, 12))
            .child(
                IKey.dynamic(() -> "SUM: " + getPowerSum(eutSyncer, ampSyncer))
                    .asWidget()
                    .color(0xFFFFFFFF)
                    .pos(46, 46)
                    .size(84, 12))
            .child(createLabelledField("EUT: ", eutSyncer, 8))
            .child(createLabelledField("AMP: ", ampSyncer, 34))
            .child(
                createChangeButton(GTNLMui2Textures.OVERLAY_BUTTON_MINUS_LARGE, eutSyncer, value -> value - 512, 7, 4))
            .child(
                createChangeButton(GTNLMui2Textures.OVERLAY_BUTTON_MINUS_LARGE, eutSyncer, value -> value / 512, 7, 22))
            .child(
                createChangeButton(GTNLMui2Textures.OVERLAY_BUTTON_MINUS_LARGE, ampSyncer, value -> value - 512, 7, 40))
            .child(
                createChangeButton(GTNLMui2Textures.OVERLAY_BUTTON_MINUS_LARGE, ampSyncer, value -> value / 512, 7, 58))
            .child(
                createChangeButton(GTNLMui2Textures.OVERLAY_BUTTON_MINUS_SMALL, eutSyncer, value -> value - 16, 25, 4))
            .child(
                createChangeButton(GTNLMui2Textures.OVERLAY_BUTTON_MINUS_SMALL, eutSyncer, value -> value / 16, 25, 22))
            .child(
                createChangeButton(GTNLMui2Textures.OVERLAY_BUTTON_MINUS_SMALL, ampSyncer, value -> value - 16, 25, 40))
            .child(
                createChangeButton(GTNLMui2Textures.OVERLAY_BUTTON_MINUS_SMALL, ampSyncer, value -> value / 16, 25, 58))
            .child(createChangeButton(GTGuiTextures.OVERLAY_BUTTON_PLUS_SMALL, eutSyncer, value -> value + 16, 133, 4))
            .child(createChangeButton(GTGuiTextures.OVERLAY_BUTTON_PLUS_SMALL, eutSyncer, value -> value * 16, 133, 22))
            .child(createChangeButton(GTGuiTextures.OVERLAY_BUTTON_PLUS_SMALL, ampSyncer, value -> value + 16, 133, 40))
            .child(createChangeButton(GTGuiTextures.OVERLAY_BUTTON_PLUS_SMALL, ampSyncer, value -> value * 16, 133, 58))
            .child(createChangeButton(GTGuiTextures.OVERLAY_BUTTON_PLUS_LARGE, eutSyncer, value -> value + 512, 151, 4))
            .child(
                createChangeButton(GTGuiTextures.OVERLAY_BUTTON_PLUS_LARGE, eutSyncer, value -> value * 512, 151, 22))
            .child(
                createChangeButton(GTGuiTextures.OVERLAY_BUTTON_PLUS_LARGE, ampSyncer, value -> value + 512, 151, 40))
            .child(
                createChangeButton(GTGuiTextures.OVERLAY_BUTTON_PLUS_LARGE, ampSyncer, value -> value * 512, 151, 58));
    }

    private String getTierName(LongSyncValue eutSyncer) {
        int tier = GTUtility.getTier(Math.abs(eutSyncer.getLongValue()));
        int boundedTier = Math.min(tier, GTValues.VN.length - 1);
        String tierColor = boundedTier < GTValues.TIER_COLORS.length ? GTValues.TIER_COLORS[boundedTier] : "";
        return tierColor + GTValues.VN[boundedTier] + EnumChatFormatting.RESET;
    }

    private String getPowerSum(LongSyncValue eutSyncer, LongSyncValue ampSyncer) {
        return DebugEnergyHatch.formatPowerSum(eutSyncer.getLongValue(), ampSyncer.getLongValue());
    }

    private ParentWidget<?> createLabelledField(String label, LongSyncValue syncer, int yPos) {
        return new ParentWidget<>().pos(46, yPos - 1)
            .size(80, 14)
            .child(
                IKey.str(label)
                    .asWidget()
                    .color(0xFFFFFFFF)
                    .pos(0, 1)
                    .size(24, 12))
            .child(
                createNumberField(syncer).pos(24, 0)
                    .size(56, 10));
    }

    private TextFieldWidget createNumberField(LongSyncValue syncer) {
        return new TextFieldWidget().value(syncer)
            .setNumbersLong(value -> value)
            .setFormatAsInteger(true)
            .setTextColor(0xFFFFFFFF)
            .setTextAlignment(Alignment.Center)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD);
    }

    private ButtonWidget<?> createChangeButton(UITexture overlay, LongSyncValue syncer, LongTransform transform,
        int xPos, int yPos) {
        return new ButtonWidget<>().background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(overlay)
            .onMousePressed(mouseButton -> {
                long changedValue = transform.apply(syncer.getLongValue());
                syncer.setLongValue(changedValue, true, true);
                return true;
            })
            .pos(xPos, yPos)
            .size(18, 18);
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

    private interface LongTransform {

        long apply(long value);
    }
}
