package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.multiMachineBase.SteamMultiMachineBase;
import com.science.gtnl.utils.enums.GTNLItemList;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.gui.modularui.widget.CircularGaugeDrawable;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTESteamMultiBlockBase;

public class GTNLSteamMultiBlockBaseGui extends MTEMultiBlockBaseGui<MTESteamMultiBlockBase<?>> {

    private static final String STEAM_STORED_SYNC_KEY = "gtnlSteamStored";
    private static final String STEAM_CAPACITY_SYNC_KEY = "gtnlSteamCapacity";
    private static final String STEAM_ANY_TYPE_SYNC_KEY = "gtnlSteamAnyTypeStored";
    private static final String STEAM_OC_SYNC_KEY = "gtnlSteamRecipeOcCount";
    private static final String STEAM_OC_PANEL_KEY = "gtnl_steam_recipe_oc";
    private static final int STEAM_OC_PANEL_WIDTH = 120;
    private static final int STEAM_OC_PANEL_HEIGHT = 54;

    public GTNLSteamMultiBlockBaseGui(MTESteamMultiBlockBase<?> multiblock) {
        super(multiblock);
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        ModularPanel panel = super.build(guiData, syncManager, uiSettings);
        SteamMultiMachineBase<?> steamMachine = getGTNLSteamMachine();
        if (steamMachine != null && steamMachine.supportsSteamCapacityUI()) {
            panel.child(createSteamGauge(syncManager));
            panel.child(createSteamGaugeNeedle(syncManager));
            panel.child(createWrongSteamWarning(syncManager));
        }
        return panel;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        SteamMultiMachineBase<?> steamMachine = getGTNLSteamMachine();
        if (steamMachine == null) return;
        syncManager.syncValue(STEAM_STORED_SYNC_KEY, new LongSyncValue(steamMachine::getLongTotalSteamStored));
        syncManager.syncValue(STEAM_CAPACITY_SYNC_KEY, new LongSyncValue(steamMachine::getTotalSteamCapacityLong));
        syncManager.syncValue(STEAM_ANY_TYPE_SYNC_KEY, new IntSyncValue(steamMachine::getTotalSteamStoredOfAnyType));
        syncManager.syncValue(
            STEAM_OC_SYNC_KEY,
            new IntSyncValue(
                () -> steamMachine.clampRecipeOcCount(steamMachine.recipeOcCount),
                value -> steamMachine.recipeOcCount = steamMachine.clampRecipeOcCount(value)).allowC2S());
    }

    @Override
    protected void initPanelMap(ModularPanel parent, PanelSyncManager syncManager) {
        super.initPanelMap(parent, syncManager);
        SteamMultiMachineBase<?> steamMachine = getGTNLSteamMachine();
        if (steamMachine == null || !steamMachine.supportsSteamOC()) return;
        panelMap.put(
            STEAM_OC_PANEL_KEY,
            syncManager.syncedPanel(
                STEAM_OC_PANEL_KEY,
                true,
                (panelSyncManager, panelHandler) -> createRecipeOcPanel(parent, syncManager)));
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        Flow row = super.createRightPanelGapRow(parent, syncManager);
        SteamMultiMachineBase<?> steamMachine = getGTNLSteamMachine();
        if (steamMachine != null && steamMachine.supportsSteamOC()) {
            row.child(createRecipeOcButton());
        }
        return row;
    }

    @Override
    protected Widget<? extends Widget<?>> makeLogoWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return new IDrawable.DrawableWidget(GTNLMui2Textures.PICTURE_GTNL_STEAM_LOGO).size(18)
            .marginTop(4);
    }

    private IWidget createSteamGauge(PanelSyncManager syncManager) {
        SteamMultiMachineBase<?> steamMachine = getGTNLSteamMachine();
        LongSyncValue steamStoredSyncer = syncManager.findSyncHandler(STEAM_STORED_SYNC_KEY, LongSyncValue.class);
        LongSyncValue steamCapacitySyncer = syncManager.findSyncHandler(STEAM_CAPACITY_SYNC_KEY, LongSyncValue.class);
        IntSyncValue anySteamSyncer = syncManager.findSyncHandler(STEAM_ANY_TYPE_SYNC_KEY, IntSyncValue.class);
        boolean steelGauge = steamMachine != null && steamMachine.tierMachine >= 2;
        return (steelGauge ? GTGuiTextures.STEAM_GAUGE_BG_STEEL : GTGuiTextures.STEAM_GAUGE_BG).asWidget()
            .size(48, 42)
            .left(-48)
            .top(8)
            .tooltipDynamic(tooltip -> {
                tooltip.addLine(
                    StatCollector.translateToLocal("AllSteamCapacity") + steamStoredSyncer.getLongValue()
                        + "/"
                        + steamCapacitySyncer.getLongValue()
                        + "L");
                if (steamStoredSyncer.getLongValue() == 0 && anySteamSyncer.getIntValue() != 0) {
                    tooltip.addLine(EnumChatFormatting.RED + "Found steam of wrong type!");
                }
            })
            .tooltipAutoUpdate(true);
    }

    private IWidget createSteamGaugeNeedle(PanelSyncManager syncManager) {
        LongSyncValue steamStoredSyncer = syncManager.findSyncHandler(STEAM_STORED_SYNC_KEY, LongSyncValue.class);
        LongSyncValue steamCapacitySyncer = syncManager.findSyncHandler(STEAM_CAPACITY_SYNC_KEY, LongSyncValue.class);
        return new CircularGaugeDrawable(
            () -> steamCapacitySyncer.getLongValue() <= 0 ? 0.0F
                : (float) steamStoredSyncer.getLongValue() / steamCapacitySyncer.getLongValue()).asWidget()
                    .size(18, 4)
                    .left(-48 + 21)
                    .top(8 + 21);
    }

    private IWidget createWrongSteamWarning(PanelSyncManager syncManager) {
        LongSyncValue steamStoredSyncer = syncManager.findSyncHandler(STEAM_STORED_SYNC_KEY, LongSyncValue.class);
        return new ItemDrawable(GTNLItemList.FakeItemSiren.get(1)).asWidget()
            .pos(-34, -12)
            .size(16, 16)
            .setEnabledIf(widget -> steamStoredSyncer.getLongValue() == 0);
    }

    private IWidget createRecipeOcButton() {
        return new ButtonWidget<>().size(18, 18)
            .marginLeft(4)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_BATCH_MODE_ON)
            .onMousePressed(mouseButton -> {
                var panel = panelMap.get(STEAM_OC_PANEL_KEY);
                if (panel == null) return false;
                if (!panel.isPanelOpen()) {
                    panel.openPanel();
                } else {
                    panel.closePanel();
                }
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("Info_SteamMachine_00")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ModularPanel createRecipeOcPanel(ModularPanel parent, PanelSyncManager syncManager) {
        IntSyncValue recipeOcSyncer = syncManager.findSyncHandler(STEAM_OC_SYNC_KEY, IntSyncValue.class);
        return new ModularPanel(STEAM_OC_PANEL_KEY).relative(parent)
            .leftRel(1)
            .topRel(0)
            .size(STEAM_OC_PANEL_WIDTH, STEAM_OC_PANEL_HEIGHT)
            .child(
                Flow.column()
                    .full()
                    .padding(3)
                    .child(makeRecipeOcTitleWidget())
                    .child(
                        new TextFieldWidget().value(recipeOcSyncer)
                            .setFormatAsInteger(true)
                            .numbersInt(0, 4)
                            .scrollValues(1, 4, 64, 256)
                            .setTextAlignment(Alignment.Center)
                            .size(STEAM_OC_PANEL_WIDTH - 6, 18)
                            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)));
    }

    private IWidget makeRecipeOcTitleWidget() {
        return IKey.str(EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("Info_SteamMachine_00"))
            .asWidget()
            .textAlign(Alignment.Center)
            .size(STEAM_OC_PANEL_WIDTH, 18)
            .marginBottom(4);
    }

    private SteamMultiMachineBase<?> getGTNLSteamMachine() {
        if (multiblock instanceof SteamMultiMachineBase<?>steamMachine) {
            return steamMachine;
        }
        return null;
    }
}
