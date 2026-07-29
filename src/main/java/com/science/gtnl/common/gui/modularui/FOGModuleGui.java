package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static net.minecraft.util.StatCollector.translateToLocal;
import static net.minecraft.util.StatCollector.translateToLocalFormatted;

import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.IntValue;
import com.cleanroommc.modularui.value.LongValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.gui.GTNLMui2Textures;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.multiblock.godforge.ForgeOfGodsGuiUtil;
import gregtech.common.gui.modularui.multiblock.godforge.MTEBaseModuleGui;
import gregtech.common.gui.modularui.multiblock.godforge.sync.Modules;
import gregtech.common.gui.modularui.multiblock.godforge.sync.Panels;
import tectech.thing.metaTileEntity.multi.godforge.MTEBaseModule;

public class FOGModuleGui extends MTEBaseModuleGui<MTEBaseModule> {

    private static final String VOLTAGE_CONFIG_PANEL_KEY = "fogVoltageConfig";
    private static final int VOLTAGE_PANEL_WIDTH = 138;
    private static final int VOLTAGE_PANEL_HEIGHT = 98;

    public FOGModuleGui(@NotNull MTEBaseModule multiblock) {
        super(multiblock);
    }

    @Override
    public Modules<MTEBaseModule> getModuleType() {
        return Modules.ANY;
    }

    @Override
    public Panels getMainPanel() {
        return Panels.MAIN;
    }

    @Override
    protected ButtonWidget<?> createGeneralInfoPanelButton() {
        return super.createGeneralInfoPanelButton().background(GTNLMui2Textures.PICTURE_GTNL_LOGO);
    }

    @Override
    protected ButtonWidget<?> createVoltageConfigButton() {
        IPanelHandler voltageConfigPanel = hypervisor.getSyncManager(getMainPanel())
            .syncedPanel(
                VOLTAGE_CONFIG_PANEL_KEY,
                true,
                (panelSyncManager, syncHandler) -> createVoltageConfigPanel(panelSyncManager));
        return new ButtonWidget<>().size(16)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .overlay(GTGuiTextures.TT_OVERLAY_BUTTON_POWER_PANEL)
            .onMousePressed(mouseButton -> {
                if (voltageConfigPanel.isPanelOpen()) {
                    voltageConfigPanel.closePanel();
                } else {
                    voltageConfigPanel.openPanel();
                }
                return true;
            })
            .tooltip(tooltip -> tooltip.addLine(translateToLocal("GT5U.gui.button.power_panel")))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound());
    }

    private ModularPanel createVoltageConfigPanel(PanelSyncManager syncManager) {
        IntSyncValue maxParallelSyncer = new IntSyncValue(
            multiblock::getCalculatedMaxParallel,
            multiblock::setCalculatedMaxParallel);
        IntSyncValue setMaxParallelSyncer = new IntSyncValue(
            multiblock::getPowerPanelMaxParallel,
            multiblock::setPowerPanelMaxParallel).allowC2S();
        BooleanSyncValue alwaysMaxParallelSyncer = new BooleanSyncValue(
            multiblock::isAlwaysMaxParallel,
            multiblock::setAlwaysMaxParallel).allowC2S();
        LongSyncValue processingVoltageSyncer = new LongSyncValue(
            multiblock::getProcessingVoltage,
            multiblock::setProcessingVoltage).allowC2S();
        BooleanSyncValue voltageConfigSyncer = new BooleanSyncValue(
            multiblock::getVoltageConfig,
            multiblock::setVoltageConfig);

        syncManager.syncValue("fogMaxParallel", maxParallelSyncer);
        syncManager.syncValue("fogSetMaxParallel", setMaxParallelSyncer);
        syncManager.syncValue("fogAlwaysMaxParallel", alwaysMaxParallelSyncer);
        syncManager.syncValue("fogProcessingVoltage", processingVoltageSyncer);
        syncManager.syncValue("fogVoltageConfigUnlocked", voltageConfigSyncer);

        Flow column = Flow.column()
            .full()
            .child(
                IKey.lang("GT5U.gui.text.power_panel")
                    .style(EnumChatFormatting.UNDERLINE, EnumChatFormatting.BLACK)
                    .alignment(Alignment.CENTER)
                    .asWidget()
                    .marginTop(7)
                    .marginBottom(5))
            .child(
                createMaxParallelGroup(maxParallelSyncer, setMaxParallelSyncer, alwaysMaxParallelSyncer)
                    .marginBottom(5))
            .child(createVoltageGroup(processingVoltageSyncer, voltageConfigSyncer));

        return new ModularPanel(VOLTAGE_CONFIG_PANEL_KEY).relative(hypervisor.getModularPanel(getMainPanel()))
            .topRel(0)
            .leftRel(1)
            .size(VOLTAGE_PANEL_WIDTH, VOLTAGE_PANEL_HEIGHT)
            .child(column);
    }

    private Flow createMaxParallelGroup(IntSyncValue maxParallelSyncer, IntSyncValue setMaxParallelSyncer,
        BooleanSyncValue alwaysMaxParallelSyncer) {
        Flow row = Flow.row()
            .coverChildren()
            .childPadding(4)
            .child(
                new TextFieldWidget().formatAsInteger(true)
                    .numbersInt(
                        () -> alwaysMaxParallelSyncer.getBoolValue() ? maxParallelSyncer.getIntValue() : 1,
                        maxParallelSyncer::getIntValue)
                    .value(new IntValue.Dynamic(setMaxParallelSyncer::getIntValue, setMaxParallelSyncer::setIntValue))
                    .scrollValues(1, 64, 4, 16)
                    .setTextAlignment(Alignment.CENTER)
                    .tooltipDynamic(tooltip -> {
                        int maxParallel = maxParallelSyncer.getIntValue();
                        if (alwaysMaxParallelSyncer.getBoolValue()) {
                            tooltip.addLine(translateToLocalFormatted("GT5U.gui.text.lockedvalue", maxParallel));
                        } else {
                            tooltip.addLine(translateToLocalFormatted("GT5U.gui.text.rangedvalue", 1, maxParallel));
                        }
                    })
                    .tooltipAutoUpdate(true)
                    .tooltipShowUpTimer(TOOLTIP_DELAY)
                    .size(70, 18))
            .child(
                new ButtonWidget<>().size(16)
                    .margin(1)
                    .overlay(
                        new DynamicDrawable(
                            () -> alwaysMaxParallelSyncer.getBoolValue() ? GTGuiTextures.OVERLAY_BUTTON_CHECKMARK
                                : GTGuiTextures.OVERLAY_BUTTON_CROSS))
                    .onMousePressed(mouseButton -> {
                        alwaysMaxParallelSyncer.setValue(!alwaysMaxParallelSyncer.getBoolValue());
                        setMaxParallelSyncer.setValue(maxParallelSyncer.getIntValue());
                        return true;
                    })
                    .tooltip(tooltip -> tooltip.addLine(translateToLocal("GT5U.gui.button.max_parallel")))
                    .tooltipShowUpTimer(TOOLTIP_DELAY)
                    .clickSound(ForgeOfGodsGuiUtil.getButtonSound()));

        return Flow.column()
            .coverChildren()
            .child(
                IKey.lang("GTPP.CC.parallel")
                    .style(EnumChatFormatting.BLACK)
                    .alignment(Alignment.CENTER)
                    .asWidget()
                    .height(14))
            .child(row);
    }

    private ParentWidget<?> createVoltageGroup(LongSyncValue processingVoltageSyncer,
        BooleanSyncValue voltageConfigSyncer) {
        Flow column = Flow.column()
            .coverChildren()
            .collapseDisabledChild()
            .child(
                IKey.lang("gt.blockmachines.multimachine.FOG.voltageinfo")
                    .style(EnumChatFormatting.BLACK)
                    .alignment(Alignment.CENTER)
                    .asWidget()
                    .height(14))
            .child(
                new TextFieldWidget().formatAsInteger(true)
                    .numbersLong(() -> 2_000_000_000L, () -> Long.MAX_VALUE)
                    .value(
                        new LongValue.Dynamic(
                            processingVoltageSyncer::getLongValue,
                            processingVoltageSyncer::setLongValue))
                    .scrollValues(1, 64, 4, 16)
                    .setTextAlignment(Alignment.CENTER)
                    .size(130, 18)
                    .setTooltipOverride(true)
                    .setEnabledIf(widget -> voltageConfigSyncer.getBoolValue()))
            .child(
                GTGuiTextures.OVERLAY_BUTTON_CROSS.asWidget()
                    .size(20)
                    .tooltip(tooltip -> tooltip.addLine(translateToLocal("fog.button.voltageconfig.tooltip.02")))
                    .tooltipShowUpTimer(TOOLTIP_DELAY)
                    .setEnabledIf(widget -> !voltageConfigSyncer.getBoolValue()));

        return new ParentWidget<>().coverChildren()
            .child(
                GTGuiTextures.PICTURE_INFO.asWidget()
                    .size(8)
                    .topRel(0)
                    .rightRel(0)
                    .tooltip(tooltip -> {
                        tooltip.addLine(translateToLocal("fog.text.tooltip.voltageadjustment"));
                        tooltip.addLine(translateToLocal("fog.text.tooltip.voltageadjustment.1"));
                    })
                    .tooltipShowUpTimer(TOOLTIP_DELAY)
                    .setEnabledIf(widget -> voltageConfigSyncer.getBoolValue()))
            .child(column);
    }
}
