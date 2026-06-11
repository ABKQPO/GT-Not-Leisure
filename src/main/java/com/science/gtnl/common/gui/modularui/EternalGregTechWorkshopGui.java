package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.math.BigInteger;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import com.cleanroommc.modularui.api.GuiAxis;
import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Alignment.MainAxis;
import com.cleanroommc.modularui.value.sync.BigIntSyncValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.FloatSyncValue;
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.FluidDisplayWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget.Direction;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.google.common.math.LongMath;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.EternalGregTechWorkshop;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util.EGTWUpgradeStorage;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util.EternalGregTechWorkshopUpgrade;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util.MilestoneFormatter;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util.MilestoneIcon;
import com.science.gtnl.config.MainConfig;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.api.modularui2.common.CommonButtons;
import gregtech.common.gui.modularui.multiblock.godforge.ForgeOfGodsGuiUtil;
import gregtech.common.gui.modularui.widget.RotatedDrawable;
import gregtech.common.modularui2.sync.LinkedBoolValue;
import gregtech.common.modularui2.widget.SelectButton;
import gregtech.common.modularui2.widget.SlotLikeButtonWidget;

public class EternalGregTechWorkshopGui extends GTNLMultiBlockBaseGui<EternalGregTechWorkshop> {

    private static final String MACHINE_TIER_SYNC_KEY = "egtwMachineTier";
    private static final String EXTRA_MODULE_ENABLED_SYNC_KEY = "egtwExtraModuleEnabled";
    private static final String EXTRA_MODULE_BUILT_SYNC_KEY = "egtwExtraModuleBuilt";
    private static final String FUEL_FACTOR_SYNC_KEY = "egtwFuelFactor";
    private static final String FUEL_TYPE_RESIDUE_SYNC_KEY = "egtwFuelTypeResidue";
    private static final String FUEL_TYPE_STELLAR_SYNC_KEY = "egtwFuelTypeStellar";
    private static final String FUEL_TYPE_MHDCSM_SYNC_KEY = "egtwFuelTypeMhdcsm";
    private static final String FUEL_CONSUMPTION_SYNC_KEY = "egtwFuelConsumption";
    private static final String CURRENT_MILESTONE_SYNC_KEY = "egtwCurrentMilestone";
    private static final String FORMATTER_SYNC_KEY = "egtwFormatter";
    private static final String TOTAL_POWER_SYNC_KEY = "egtwTotalPower";
    private static final String TOTAL_RECIPES_SYNC_KEY = "egtwTotalRecipes";
    private static final String TOTAL_FUEL_SYNC_KEY = "egtwTotalFuel";
    private static final String MILESTONE_LEVEL_SYNC_KEY_PREFIX = "egtwMilestoneLevel";
    private static final String MILESTONE_PROGRESS_SYNC_KEY_PREFIX = "egtwMilestoneProgress";
    private static final String CURRENT_UPGRADE_SYNC_KEY = "egtwCurrentUpgrade";
    private static final String UPGRADE_DATA_SYNC_KEY = "egtwUpgradeData";
    private static final String SECRET_UPGRADE_SYNC_KEY = "egtwSecretUpgrade";
    private static final String GRAVITON_SHARDS_SYNC_KEY = "egtwGravitonShards";
    private static final String GRAVITON_SHARD_EJECTION_SYNC_KEY = "egtwGravitonShardEjection";

    private static final String GENERAL_INFO_PANEL_KEY = "egtwGeneralInfo";
    private static final String FUEL_CONFIG_PANEL_KEY = "egtwFuelConfig";
    private static final String MILESTONE_PANEL_KEY = "egtwMilestone";
    private static final String INDIVIDUAL_MILESTONE_PANEL_KEY = "egtwIndividualMilestone";
    private static final String UPGRADE_TREE_PANEL_KEY = "egtwUpgradeTree";
    private static final String INDIVIDUAL_UPGRADE_PANEL_KEY = "egtwIndividualUpgrade";
    private static final String MANUAL_INSERTION_PANEL_KEY = "egtwManualInsertion";
    private static final String STATISTICS_PANEL_KEY = "egtwStatistics";

    private static final int GENERAL_INFO_SIZE = 300;
    private static final int STATISTICS_PANEL_WIDTH = 180;
    private static final int STATISTICS_PANEL_HEIGHT = 96;
    private static final int FUEL_PANEL_WIDTH = 78;
    private static final int FUEL_PANEL_HEIGHT = 138;
    private static final int MILESTONE_PANEL_WIDTH = 400;
    private static final int MILESTONE_PANEL_HEIGHT = 300;
    private static final int INDIVIDUAL_MILESTONE_SIZE = 150;
    private static final int MILESTONE_PROGRESS_BAR_WIDTH = 130;
    private static final int MILESTONE_PROGRESS_BAR_HEIGHT = 7;
    private static final int UPGRADE_TREE_SIZE = 300;
    private static final int UPGRADE_TREE_SCROLL_SIZE = 957;
    private static final int UPGRADE_BUTTON_WIDTH = 40;
    private static final int UPGRADE_BUTTON_HEIGHT = 15;
    private static final int MANUAL_INSERTION_PANEL_WIDTH = 261;
    private static final int MANUAL_INSERTION_PANEL_HEIGHT = 115;
    private static final int EXTRA_COST_COLUMNS = 5;
    private static final int EXTRA_COST_ROWS = 4;
    private static final int EXTRA_COST_ROW_WIDTH = 36;
    private static final int EXTRA_COST_ROW_HEIGHT = 18;
    private static final String UPGRADE_INPUT_SLOT_GROUP = "egtwUpgradeInput";

    public EternalGregTechWorkshopGui(EternalGregTechWorkshop multiblock) {
        super(multiblock);
    }

    @Override
    protected ModularPanel getBasePanel(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        ModularPanel panel = super.getBasePanel(guiData, syncManager, uiSettings);
        panel.child(
            GTGuiTextures.PICTURE_HEAT_SINK_16x8.asWidget()
                .size(16, 8)
                .bottomRel(0)
                .rightRel(0)
                .marginRight(8)
                .marginBottom(1));
        return panel;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(MACHINE_TIER_SYNC_KEY, new IntSyncValue(multiblock::getMachineTierForGui));
        syncManager.syncValue(
            EXTRA_MODULE_ENABLED_SYNC_KEY,
            new BooleanSyncValue(multiblock::isExtraModuleEnabledForGui, multiblock::setExtraModuleEnabledFromGui)
                .allowC2S());
        syncManager.syncValue(EXTRA_MODULE_BUILT_SYNC_KEY, new BooleanSyncValue(multiblock::isExtraModuleBuiltForGui));
        syncManager.syncValue(
            FUEL_FACTOR_SYNC_KEY,
            new IntSyncValue(multiblock::getFuelFactor, multiblock::setFuelFactorFromGui).allowC2S());
        syncManager.syncValue(
            FUEL_TYPE_RESIDUE_SYNC_KEY,
            new BooleanSyncValue(
                () -> multiblock.getFuelType() == 0,
                value -> { if (value) multiblock.setFuelType(0); }).allowC2S());
        syncManager.syncValue(
            FUEL_TYPE_STELLAR_SYNC_KEY,
            new BooleanSyncValue(
                () -> multiblock.getFuelType() == 1,
                value -> { if (value) multiblock.setFuelType(1); }).allowC2S());
        syncManager.syncValue(
            FUEL_TYPE_MHDCSM_SYNC_KEY,
            new BooleanSyncValue(
                () -> multiblock.getFuelType() == 2,
                value -> { if (value) multiblock.setFuelType(2); }).allowC2S());
        syncManager.syncValue(FUEL_CONSUMPTION_SYNC_KEY, new LongSyncValue(multiblock::getFuelConsumptionForGui));
        syncManager.syncValue(
            CURRENT_MILESTONE_SYNC_KEY,
            new IntSyncValue(multiblock::getCurrentMilestoneIdForGui, multiblock::setCurrentMilestoneIdFromGui)
                .allowC2S());
        syncManager.syncValue(
            FORMATTER_SYNC_KEY,
            new EnumSyncValue<>(
                MilestoneFormatter.class,
                multiblock::getFormattingModeForGui,
                multiblock::setFormattingModeFromGui).allowC2S());
        syncManager.syncValue(TOTAL_POWER_SYNC_KEY, new BigIntSyncValue(multiblock::getTotalPowerConsumedForGui, null));
        syncManager.syncValue(TOTAL_RECIPES_SYNC_KEY, new LongSyncValue(multiblock::getTotalRecipesProcessedForGui));
        syncManager.syncValue(TOTAL_FUEL_SYNC_KEY, new LongSyncValue(multiblock::getTotalFuelConsumedForGui));
        syncManager.syncValue(
            CURRENT_UPGRADE_SYNC_KEY,
            new EnumSyncValue<>(
                EternalGregTechWorkshopUpgrade.class,
                multiblock::getCurrentUpgradeForGui,
                multiblock::setCurrentUpgradeFromGui).allowC2S());
        syncManager.syncValue(
            UPGRADE_DATA_SYNC_KEY,
            GenericListSyncHandler.<EGTWUpgradeStorage.UpgradeData>builder()
                .getter(multiblock::getUpgradeDataForGui)
                .setter(multiblock::setUpgradeDataFromGui)
                .serializer(EGTWUpgradeStorage.UpgradeData::writeToBuffer)
                .deserializer(EGTWUpgradeStorage.UpgradeData::readFromBuffer)
                .build());
        syncManager.syncValue(
            SECRET_UPGRADE_SYNC_KEY,
            new BooleanSyncValue(multiblock::isSecretUpgradeForGui, multiblock::setSecretUpgradeFromGui).allowC2S());
        syncManager.syncValue(
            GRAVITON_SHARDS_SYNC_KEY,
            new IntSyncValue(
                multiblock::getGravitonShardsAvailableForGui,
                multiblock::setGravitonShardsAvailableFromGui).allowC2S());
        syncManager.syncValue(
            GRAVITON_SHARD_EJECTION_SYNC_KEY,
            new BooleanSyncValue(multiblock::isGravitonShardEjectionForGui, multiblock::setGravitonShardEjectionFromGui)
                .allowC2S());
        for (int milestoneId = 0; milestoneId < MilestoneIcon.VALUES.length; milestoneId++) {
            int id = milestoneId;
            syncManager.syncValue(
                getMilestoneLevelSyncKey(id),
                new IntSyncValue(() -> multiblock.getMilestoneProgressLevelForGui(id)));
            syncManager.syncValue(
                getMilestoneProgressSyncKey(id),
                new FloatSyncValue(() -> multiblock.getMilestonePercentageForGui(id)));
        }
    }

    @Override
    protected void initPanelMap(ModularPanel parent, PanelSyncManager syncManager) {
        super.initPanelMap(parent, syncManager);
        panelMap.put(
            GENERAL_INFO_PANEL_KEY,
            syncManager.syncedPanel(
                GENERAL_INFO_PANEL_KEY,
                true,
                (panelSyncManager, panelHandler) -> createGeneralInfoPanel(parent)));
        panelMap.put(
            FUEL_CONFIG_PANEL_KEY,
            syncManager.syncedPanel(
                FUEL_CONFIG_PANEL_KEY,
                true,
                (panelSyncManager, panelHandler) -> createFuelConfigPanel(parent, panelSyncManager)));
        panelMap.put(
            MILESTONE_PANEL_KEY,
            syncManager.syncedPanel(
                MILESTONE_PANEL_KEY,
                true,
                (panelSyncManager, panelHandler) -> createMilestonePanel(parent, panelSyncManager)));
        panelMap.put(
            INDIVIDUAL_MILESTONE_PANEL_KEY,
            syncManager.syncedPanel(
                INDIVIDUAL_MILESTONE_PANEL_KEY,
                true,
                (panelSyncManager, panelHandler) -> createIndividualMilestonePanel(parent, panelSyncManager)));
        panelMap.put(
            UPGRADE_TREE_PANEL_KEY,
            syncManager.syncedPanel(
                UPGRADE_TREE_PANEL_KEY,
                true,
                (panelSyncManager, panelHandler) -> createUpgradeTreePanel(parent, panelSyncManager)));
        panelMap.put(
            INDIVIDUAL_UPGRADE_PANEL_KEY,
            syncManager.syncedPanel(
                INDIVIDUAL_UPGRADE_PANEL_KEY,
                true,
                (panelSyncManager, panelHandler) -> createIndividualUpgradePanel(parent, panelSyncManager)));
        panelMap.put(
            MANUAL_INSERTION_PANEL_KEY,
            syncManager.syncedPanel(
                MANUAL_INSERTION_PANEL_KEY,
                true,
                (panelSyncManager, panelHandler) -> createManualInsertionPanel(parent, panelSyncManager)));
        panelMap.put(
            STATISTICS_PANEL_KEY,
            syncManager.syncedPanel(
                STATISTICS_PANEL_KEY,
                true,
                (panelSyncManager, panelHandler) -> createStatisticsPanel(parent, panelSyncManager)));
    }

    @Override
    protected Flow createButtonColumn(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.column()
            .width(18)
            .leftRel(1, -3, 1)
            .childPadding(3)
            .mainAxisAlignment(MainAxis.END)
            .child(createMilestoneButton())
            .child(createFuelConfigButton())
            .child(createUpgradeTreeButton())
            .child(createExtraModuleButton(syncManager))
            .child(createPowerSwitchButton())
            .child(createStructureUpdateButton(syncManager));
    }

    @Override
    protected Flow createTerminalRightCornerColumn(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.column()
            .coverChildren()
            .rightRel(0, 6, 0)
            .bottomRel(0, 6, 0)
            .child(createGeneralInfoButton());
    }

    @Override
    protected Flow createPanelGap(ModularPanel parent, PanelSyncManager syncManager) {
        return Flow.row()
            .collapseDisabledChild()
            .fullWidth()
            .paddingRight(6)
            .paddingLeft(5)
            .childPadding(2)
            .height(getTextBoxToInventoryGap())
            .child(createModuleRefreshButton(syncManager))
            .child(createStatisticsButton())
            .child(createShardEjectionButton(syncManager));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        IntSyncValue machineTierSyncer = syncManager.findSyncHandler(MACHINE_TIER_SYNC_KEY, IntSyncValue.class);
        BooleanSyncValue extraEnabledSyncer = syncManager
            .findSyncHandler(EXTRA_MODULE_ENABLED_SYNC_KEY, BooleanSyncValue.class);
        BooleanSyncValue extraBuiltSyncer = syncManager
            .findSyncHandler(EXTRA_MODULE_BUILT_SYNC_KEY, BooleanSyncValue.class);

        return super.createTerminalTextWidget(syncManager, parent).child(
            IKey.lang("EGTW_MachineTier")
                .style(EnumChatFormatting.WHITE)
                .asWidget()
                .fullWidth()
                .marginTop(4))
            .child(
                IKey.dynamic(() -> Integer.toString(machineTierSyncer.getIntValue()))
                    .style(EnumChatFormatting.WHITE)
                    .asWidget()
                    .fullWidth()
                    .marginBottom(2))
            .child(
                IKey.dynamic(() -> extraEnabledSyncer.getBoolValue() ? translateToLocal("EGTW_ExtraModule") : "")
                    .style(EnumChatFormatting.WHITE)
                    .asWidget()
                    .fullWidth()
                    .marginBottom(2))
            .child(
                IKey.dynamic(() -> getExtraModuleState(extraEnabledSyncer, extraBuiltSyncer))
                    .style(EnumChatFormatting.WHITE)
                    .asWidget()
                    .fullWidth()
                    .marginBottom(2))
            .child(
                IKey.dynamic(multiblock::getMachineStateTextForGui)
                    .style(EnumChatFormatting.WHITE)
                    .asWidget()
                    .fullWidth()
                    .marginBottom(2));
    }

    @Override
    protected int getTextBoxToInventoryGap() {
        return 20;
    }

    @Override
    protected ToggleButton createMuffleButton() {
        return CommonButtons.createMuffleButton("mufflerSyncer")
            .size(7)
            .disableThemeBackground(true)
            .disableHoverThemeBackground(true)
            .overlay(true, GTGuiTextures.GODFORGE_SOUND_OFF)
            .overlay(false, GTGuiTextures.GODFORGE_SOUND_ON)
            .top(8)
            .right(8)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound());
    }

    @Override
    protected IWidget createStructureUpdateButton(PanelSyncManager syncManager) {
        return ((ToggleButton) super.createStructureUpdateButton(syncManager)).size(16)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .selectedBackground(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .overlay(
                new DynamicDrawable(
                    () -> multiblock.getStructureUpdateTime() > -20 ? GTGuiTextures.TT_OVERLAY_BUTTON_STRUCTURE_CHECK
                        : GTGuiTextures.TT_OVERLAY_BUTTON_STRUCTURE_CHECK_OFF))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound());
    }

    private String getExtraModuleState(BooleanSyncValue extraEnabledSyncer, BooleanSyncValue extraBuiltSyncer) {
        if (!extraEnabledSyncer.getBoolValue()) return "";
        return translateToLocal(extraBuiltSyncer.getBoolValue() ? "EGTW_ExtraModule_On" : "EGTW_ExtraModule_Off");
    }

    private IWidget createGeneralInfoButton() {
        IPanelHandler generalInfoPanel = panelMap.get(GENERAL_INFO_PANEL_KEY);
        return new ButtonWidget<>().overlay(IDrawable.EMPTY)
            .background(GTNLMui2Textures.PICTURE_GODFORGE_LOGO)
            .disableHoverBackground()
            .onMousePressed(mouseButton -> {
                togglePanel(generalInfoPanel);
                return true;
            })
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound())
            .tooltip(tooltip -> tooltip.addLine(translateToLocal("gt.blockmachines.multimachine.FOG.clickhere")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private IWidget createFuelConfigButton() {
        IPanelHandler fuelConfigPanel = panelMap.get(FUEL_CONFIG_PANEL_KEY);
        return new ButtonWidget<>().size(16)
            .overlay(GTGuiTextures.TT_OVERLAY_BUTTON_HEAT_ON)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .disableHoverBackground()
            .onMousePressed(mouseButton -> {
                togglePanel(fuelConfigPanel);
                return true;
            })
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound())
            .tooltip(tooltip -> tooltip.addLine(translateToLocal("fog.button.fuelconfig.tooltip")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private IWidget createMilestoneButton() {
        IPanelHandler milestonePanel = panelMap.get(MILESTONE_PANEL_KEY);
        return new ButtonWidget<>().size(16)
            .overlay(GTGuiTextures.TT_OVERLAY_BUTTON_FLAG)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .disableHoverBackground()
            .onMousePressed(mouseButton -> {
                togglePanel(milestonePanel);
                return true;
            })
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound())
            .tooltip(tooltip -> tooltip.addLine(translateToLocal("fog.button.milestones.tooltip")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private IWidget createUpgradeTreeButton() {
        IPanelHandler upgradeTreePanel = panelMap.get(UPGRADE_TREE_PANEL_KEY);
        return new ButtonWidget<>().size(16)
            .overlay(GTGuiTextures.TT_OVERLAY_BUTTON_ARROW_BLUE_UP)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .disableHoverBackground()
            .onMousePressed(mouseButton -> {
                togglePanel(upgradeTreePanel);
                return true;
            })
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound())
            .tooltip(tooltip -> tooltip.addLine(translateToLocal("fog.button.upgradetree.tooltip")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private IWidget createModuleRefreshButton(PanelSyncManager syncManager) {
        BooleanSyncValue structureUpdateSyncer = syncManager
            .findSyncHandler("structureUpdateButton", BooleanSyncValue.class);
        return new ButtonWidget<>().size(16)
            .overlay(GTGuiTextures.TT_OVERLAY_CYCLIC_BLUE)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .disableHoverBackground()
            .onMousePressed(mouseButton -> {
                structureUpdateSyncer.setBoolValue(true, true, true);
                return true;
            })
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound())
            .tooltip(tooltip -> tooltip.addLine(translateToLocal("EGTW_UpdateStructureCheck")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private IWidget createStatisticsButton() {
        IPanelHandler statisticsPanel = panelMap.get(STATISTICS_PANEL_KEY);
        return new ButtonWidget<>().size(16)
            .overlay(GTGuiTextures.TT_OVERLAY_BUTTON_STATISTICS)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .disableHoverBackground()
            .onMousePressed(mouseButton -> {
                togglePanel(statisticsPanel);
                return true;
            })
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound())
            .tooltip(tooltip -> tooltip.addLine(translateToLocal("fog.button.statistics.tooltip")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private IWidget createShardEjectionButton(PanelSyncManager syncManager) {
        BooleanSyncValue shardEjectionSyncer = syncManager
            .findSyncHandler(GRAVITON_SHARD_EJECTION_SYNC_KEY, BooleanSyncValue.class);
        return new ButtonWidget<>().size(16)
            .setEnabledIf(widget -> isUpgradeActive(syncManager, EternalGregTechWorkshopUpgrade.END))
            .overlay(
                new DynamicDrawable(
                    () -> shardEjectionSyncer.getBoolValue() ? GTGuiTextures.TT_OVERLAY_EJECTION_ON
                        : GTGuiTextures.TT_OVERLAY_EJECTION_LOCKED))
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .disableHoverBackground()
            .onMousePressed(mouseButton -> {
                shardEjectionSyncer.setBoolValue(!shardEjectionSyncer.getBoolValue(), true, true);
                return true;
            })
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound())
            .tooltip(tooltip -> tooltip.addLine(translateToLocal("fog.button.ejection.tooltip")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private IWidget createExtraModuleButton(PanelSyncManager syncManager) {
        BooleanSyncValue extraEnabledSyncer = syncManager
            .findSyncHandler(EXTRA_MODULE_ENABLED_SYNC_KEY, BooleanSyncValue.class);
        return new ButtonWidget<>().size(16)
            .overlay(GTGuiTextures.TT_OVERLAY_BUTTON_ARROW_BLUE_UP)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .disableHoverBackground()
            .onMousePressed(mouseButton -> {
                extraEnabledSyncer.setBoolValue(!extraEnabledSyncer.getBoolValue(), true, true);
                return true;
            })
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound())
            .tooltip(tooltip -> tooltip.addLine(translateToLocal("EGTW_EnableExtraModule")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ModularPanel createGeneralInfoPanel(ModularPanel parent) {
        Dialog<?> panel = new Dialog<>(GENERAL_INFO_PANEL_KEY, null);
        panel.relative(parent)
            .size(GENERAL_INFO_SIZE)
            .padding(10, 0, 10, 0)
            .background(GTNLMui2Textures.BACKGROUND_GLOW_WHITE);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true)
            .disableHoverBackground();

        panel.child(ForgeOfGodsGuiUtil.panelCloseButton());
        panel.child(createGeneralInfoScroll());
        return panel;
    }

    private IWidget createGeneralInfoScroll() {
        ListWidget<IWidget, ?> textList = new ListWidget<>().size(GENERAL_INFO_SIZE - 20);
        TextWidget<?> fuelHeader = createInfoHeader("gt.blockmachines.multimachine.FOG.fuel");
        TextWidget<?> moduleHeader = createInfoHeader("gt.blockmachines.multimachine.FOG.modules");
        TextWidget<?> upgradeHeader = createInfoHeader("gt.blockmachines.multimachine.FOG.upgrades");
        TextWidget<?> milestoneHeader = createInfoHeader("gt.blockmachines.multimachine.FOG.milestones");

        textList.child(createInfoHeader("gt.blockmachines.multimachine.FOG.introduction"));
        textList.child(createInfoText("gt.blockmachines.multimachine.FOG.introductioninfotext"));
        textList.child(createTableOfContentsHeader());
        textList.child(createToCEntry(textList, "gt.blockmachines.multimachine.FOG.fuel", fuelHeader));
        textList.child(createToCEntry(textList, "gt.blockmachines.multimachine.FOG.modules", moduleHeader));
        textList.child(createToCEntry(textList, "gt.blockmachines.multimachine.FOG.upgrades", upgradeHeader));
        textList.child(createToCEntry(textList, "gt.blockmachines.multimachine.FOG.milestones", milestoneHeader));
        textList.child(fuelHeader);
        textList.child(createInfoText("gt.blockmachines.multimachine.FOG.fuelinfotext"));
        textList.child(moduleHeader);
        textList.child(createInfoText("gt.blockmachines.multimachine.FOG.moduleinfotext"));
        textList.child(upgradeHeader);
        textList.child(createInfoText("gt.blockmachines.multimachine.FOG.upgradeinfotext"));
        textList.child(milestoneHeader);
        textList.child(createInfoText("gt.blockmachines.multimachine.FOG.milestoneinfotext"));

        return new ScrollWidget<>().size(GENERAL_INFO_SIZE - 8)
            .pos(4, 4)
            .child(textList);
    }

    private TextWidget<?> createInfoHeader(String langKey) {
        return IKey.lang(langKey)
            .style(EnumChatFormatting.DARK_PURPLE, EnumChatFormatting.BOLD, EnumChatFormatting.UNDERLINE)
            .asWidget()
            .horizontalCenter()
            .marginBottom(8);
    }

    private TextWidget<?> createInfoText(String langKey) {
        return IKey.lang(langKey)
            .style(EnumChatFormatting.GOLD)
            .alignment(Alignment.CenterLeft)
            .asWidget()
            .width(GENERAL_INFO_SIZE - 20)
            .marginBottom(8);
    }

    private TextWidget<?> createTableOfContentsHeader() {
        return IKey.lang("gt.blockmachines.multimachine.FOG.tableofcontents")
            .style(EnumChatFormatting.AQUA, EnumChatFormatting.BOLD)
            .alignment(Alignment.CenterLeft)
            .asWidget()
            .width(GENERAL_INFO_SIZE - 20)
            .marginBottom(8);
    }

    private ButtonWidget<?> createToCEntry(ListWidget<IWidget, ?> textList, String langKey, TextWidget<?> jumpPoint) {
        return new ButtonWidget<>().width(GENERAL_INFO_SIZE - 20)
            .background(IDrawable.EMPTY)
            .overlay(
                IKey.lang(langKey)
                    .style(EnumChatFormatting.AQUA, EnumChatFormatting.BOLD)
                    .alignment(Alignment.CenterLeft))
            .disableHoverBackground()
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound())
            .onMousePressed(mouseButton -> {
                textList.getScrollData()
                    .animateTo(
                        textList.getScrollArea(),
                        jumpPoint.getArea()
                            .getRelativePoint(GuiAxis.Y));
                return true;
            });
    }

    private ModularPanel createFuelConfigPanel(ModularPanel parent, PanelSyncManager syncManager) {
        Dialog<?> panel = new Dialog<>(FUEL_CONFIG_PANEL_KEY, null);
        panel.relative(parent)
            .size(FUEL_PANEL_WIDTH, FUEL_PANEL_HEIGHT)
            .topRel(0)
            .leftRelOffset(1, -3)
            .background(GTGuiTextures.BACKGROUND_POPUP_STANDARD);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true);

        panel.child(ForgeOfGodsGuiUtil.panelCloseButtonStandard());
        panel.child(createFuelConfigContent(syncManager));
        panel.child(createFuelInfoIcon());
        return panel;
    }

    private IWidget createFuelConfigContent(PanelSyncManager syncManager) {
        IntSyncValue fuelFactorSyncer = syncManager.findSyncHandler(FUEL_FACTOR_SYNC_KEY, IntSyncValue.class);
        LongSyncValue fuelConsumptionSyncer = syncManager
            .findSyncHandler(FUEL_CONSUMPTION_SYNC_KEY, LongSyncValue.class);
        Flow fuelRow = Flow.row()
            .coverChildren()
            .marginTop(5)
            .childPadding(7)
            .child(createFuelSelection(syncManager, FUEL_TYPE_RESIDUE_SYNC_KEY, 0))
            .child(createFuelSelection(syncManager, FUEL_TYPE_STELLAR_SYNC_KEY, 1))
            .child(createFuelSelection(syncManager, FUEL_TYPE_MHDCSM_SYNC_KEY, 2));

        return Flow.column()
            .size(FUEL_PANEL_WIDTH, FUEL_PANEL_HEIGHT)
            .child(
                IKey.lang("gt.blockmachines.multimachine.FOG.fuelconsumption")
                    .alignment(Alignment.CENTER)
                    .asWidget()
                    .width(FUEL_PANEL_WIDTH - 4)
                    .marginTop(5))
            .child(
                new TextFieldWidget().formatAsInteger(true)
                    .numbersInt(
                        raw -> MathHelper.clamp_int(raw, 1, EternalGregTechWorkshop.calculateMaxFuelFactor(multiblock)))
                    .setTextAlignment(Alignment.CENTER)
                    .value(fuelFactorSyncer)
                    .setScrollValues(1, 4, 64)
                    .size(70, 18)
                    .marginLeft(4)
                    .marginTop(3))
            .child(
                IKey.lang("gt.blockmachines.multimachine.FOG.fueltype")
                    .alignment(Alignment.CENTER)
                    .asWidget()
                    .width(FUEL_PANEL_WIDTH - 4)
                    .marginTop(5))
            .child(fuelRow)
            .child(
                IKey.lang("gt.blockmachines.multimachine.FOG.fuelusage")
                    .alignment(Alignment.CENTER)
                    .asWidget()
                    .width(FUEL_PANEL_WIDTH - 4)
                    .marginTop(5))
            .child(
                IKey.dynamic(() -> fuelConsumptionSyncer.getLongValue() + " L/5s")
                    .alignment(Alignment.CENTER)
                    .asWidget()
                    .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_GRAY)
                    .width(FUEL_PANEL_WIDTH - 4)
                    .marginTop(3));
    }

    private IWidget createFuelInfoIcon() {
        return GTGuiTextures.PICTURE_INFO.asWidget()
            .size(10)
            .pos(FUEL_PANEL_WIDTH - 14, 24)
            .tooltip(tooltip -> {
                for (int i = 0; i <= 5; i++) {
                    tooltip.addLine(translateToLocal("gt.blockmachines.multimachine.FOG.fuelinfo." + i));
                }
            })
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ParentWidget<?> createFuelSelection(PanelSyncManager syncManager, String syncKey, int fuelType) {
        BooleanSyncValue fuelSyncer = syncManager.findSyncHandler(syncKey, BooleanSyncValue.class);
        return new ParentWidget<>().coverChildrenWidth()
            .size(18)
            .child(
                new FluidDisplayWidget().background(IDrawable.EMPTY)
                    .value(multiblock.getFuelStackForGui(fuelType))
                    .displayAmount(false)
                    .topRel(0)
                    .leftRel(0)
                    .size(18))
            .child(
                new SelectButton().value(LinkedBoolValue.of(fuelSyncer, true))
                    .disableThemeBackground(true)
                    .disableHoverThemeBackground(true)
                    .selectedBackground(GTGuiTextures.SLOT_OUTLINE_GREEN)
                    .clickSound(ForgeOfGodsGuiUtil.getButtonSound())
                    .tooltip(tooltip -> tooltip.addFromFluid(multiblock.getFuelStackForGui(fuelType)))
                    .tooltipShowUpTimer(TOOLTIP_DELAY));
    }

    private ModularPanel createStatisticsPanel(ModularPanel parent, PanelSyncManager syncManager) {
        Dialog<?> panel = new Dialog<>(STATISTICS_PANEL_KEY, null);
        panel.relative(parent)
            .size(STATISTICS_PANEL_WIDTH, STATISTICS_PANEL_HEIGHT)
            .leftRelOffset(0, 4)
            .topRelOffset(0, 3)
            .background(GTGuiTextures.BACKGROUND_POPUP_STANDARD);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true);

        panel.child(ForgeOfGodsGuiUtil.panelCloseButtonStandard());
        panel.child(
            Flow.column()
                .size(STATISTICS_PANEL_WIDTH - 12, STATISTICS_PANEL_HEIGHT - 10)
                .marginLeft(6)
                .marginTop(6)
                .childPadding(5)
                .child(createStatisticsHeader())
                .child(createStatisticsRow("gt.blockmachines.multimachine.FOG.power", getTotalPowerText(syncManager)))
                .child(
                    createStatisticsRow("gt.blockmachines.multimachine.FOG.recipes", getTotalRecipesText(syncManager)))
                .child(
                    createStatisticsRow(
                        "gt.blockmachines.multimachine.FOG.fuelconsumed",
                        getTotalFuelText(syncManager)))
                .child(
                    createStatisticsRow(
                        "gt.blockmachines.multimachine.FOG.availableshards",
                        getAvailableShardsText(syncManager))));
        return panel;
    }

    private TextWidget<?> createStatisticsHeader() {
        return IKey.lang("fog.button.statistics.tooltip")
            .style(EnumChatFormatting.DARK_GRAY, EnumChatFormatting.BOLD)
            .alignment(Alignment.CENTER)
            .asWidget()
            .fullWidth();
    }

    private TextWidget<?> createStatisticsRow(String labelKey, Supplier<String> valueSupplier) {
        return IKey.dynamic(() -> translateToLocal(labelKey) + ": " + EnumChatFormatting.GRAY + valueSupplier.get())
            .style(EnumChatFormatting.DARK_GRAY)
            .alignment(Alignment.CenterLeft)
            .asWidget()
            .fullWidth();
    }

    private Supplier<String> getTotalPowerText(PanelSyncManager syncManager) {
        return () -> getFormatter(syncManager).format(
            syncManager.findSyncHandler(TOTAL_POWER_SYNC_KEY, BigIntSyncValue.class)
                .getValue());
    }

    private Supplier<String> getTotalRecipesText(PanelSyncManager syncManager) {
        return () -> getFormatter(syncManager).format(
            syncManager.findSyncHandler(TOTAL_RECIPES_SYNC_KEY, LongSyncValue.class)
                .getLongValue());
    }

    private Supplier<String> getTotalFuelText(PanelSyncManager syncManager) {
        return () -> getFormatter(syncManager).format(
            syncManager.findSyncHandler(TOTAL_FUEL_SYNC_KEY, LongSyncValue.class)
                .getLongValue());
    }

    private Supplier<String> getAvailableShardsText(PanelSyncManager syncManager) {
        return () -> getFormatter(syncManager).format(
            syncManager.findSyncHandler(GRAVITON_SHARDS_SYNC_KEY, IntSyncValue.class)
                .getIntValue());
    }

    private ModularPanel createMilestonePanel(ModularPanel parent, PanelSyncManager syncManager) {
        Dialog<?> panel = new Dialog<>(MILESTONE_PANEL_KEY, null);
        panel.relative(parent)
            .size(MILESTONE_PANEL_WIDTH, MILESTONE_PANEL_HEIGHT)
            .background(GTGuiTextures.BACKGROUND_SPACE);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true)
            .disableHoverBackground();

        panel.child(ForgeOfGodsGuiUtil.panelCloseButton());
        panel.child(createMilestoneSummary(syncManager, 0, 80, 100, 62, 24, 77, 45));
        panel.child(createMilestoneSummary(syncManager, 1, 70, 98, 263, 25, 268, 45));
        panel.child(createMilestoneSummary(syncManager, 2, 100, 100, 52, 169, 77, 190));
        panel.child(createMilestoneSummary(syncManager, 3, 100, 100, 248, 169, 268, 190));
        panel.child(createMilestoneProgress(syncManager, 0, 37, 70, GTGuiTextures.PROGRESSBAR_GODFORGE_MILESTONE_RED));
        panel.child(
            createMilestoneProgress(syncManager, 1, 233, 70, GTGuiTextures.PROGRESSBAR_GODFORGE_MILESTONE_PURPLE));
        panel
            .child(createMilestoneProgress(syncManager, 2, 37, 215, GTGuiTextures.PROGRESSBAR_GODFORGE_MILESTONE_BLUE));
        panel.child(
            createMilestoneProgress(syncManager, 3, 233, 215, GTGuiTextures.PROGRESSBAR_GODFORGE_MILESTONE_RAINBOW));
        return panel;
    }

    private ParentWidget<?> createMilestoneSummary(PanelSyncManager syncManager, int milestoneId, int buttonWidth,
        int buttonHeight, int buttonX, int buttonY, int titleX, int titleY) {
        IntSyncValue currentMilestoneSyncer = syncManager
            .findSyncHandler(CURRENT_MILESTONE_SYNC_KEY, IntSyncValue.class);
        IPanelHandler individualMilestonePanel = panelMap.get(INDIVIDUAL_MILESTONE_PANEL_KEY);
        return new ParentWidget<>().size(MILESTONE_PANEL_WIDTH, MILESTONE_PANEL_HEIGHT)
            .child(
                new ButtonWidget<>().size(buttonWidth, buttonHeight)
                    .pos(buttonX, buttonY)
                    .background(getMilestoneGlow(milestoneId))
                    .disableHoverBackground()
                    .onMousePressed(mouseButton -> {
                        currentMilestoneSyncer.setIntValue(milestoneId, true, true);
                        if (individualMilestonePanel != null && !individualMilestonePanel.isPanelOpen()) {
                            individualMilestonePanel.openPanel();
                        }
                        return true;
                    })
                    .tooltip(
                        tooltip -> tooltip.addLine(translateToLocal("gt.blockmachines.multimachine.FOG.milestoneinfo")))
                    .tooltipShowUpTimer(TOOLTIP_DELAY)
                    .clickSound(ForgeOfGodsGuiUtil.getButtonSound()))
            .child(
                IKey.lang(getMilestoneTitleKey(milestoneId))
                    .style(EnumChatFormatting.GOLD)
                    .alignment(Alignment.CENTER)
                    .asWidget()
                    .pos(titleX, titleY)
                    .size(milestoneId == 1 || milestoneId == 3 ? 60 : 50, 30));
    }

    private IDrawable getMilestoneGlow(int milestoneId) {
        return switch (milestoneId) {
            case 1 -> GTGuiTextures.PICTURE_GODFORGE_MILESTONE_CONVERSION_GLOW;
            case 2 -> GTGuiTextures.PICTURE_GODFORGE_MILESTONE_CATALYST_GLOW;
            case 3 -> GTGuiTextures.PICTURE_GODFORGE_MILESTONE_COMPOSITION_GLOW;
            default -> GTGuiTextures.PICTURE_GODFORGE_MILESTONE_CHARGE_GLOW;
        };
    }

    private String getMilestoneTitleKey(int milestoneId) {
        return switch (milestoneId) {
            case 1 -> "gt.blockmachines.multimachine.FOG.recipemilestone";
            case 2 -> "gt.blockmachines.multimachine.FOG.fuelmilestone";
            case 3 -> "gt.blockmachines.multimachine.FOG.purchasablemilestone";
            default -> "gt.blockmachines.multimachine.FOG.powermilestone";
        };
    }

    private ProgressWidget createMilestoneProgress(PanelSyncManager syncManager, int milestoneId, int x, int y,
        UITexture overlay) {
        FloatSyncValue progressSyncer = syncManager
            .findSyncHandler(getMilestoneProgressSyncKey(milestoneId), FloatSyncValue.class);
        return new ProgressWidget().value(new DoubleSyncValue(progressSyncer::getDoubleValue))
            .texture(GTGuiTextures.PROGRESSBAR_GODFORGE_MILESTONE_BACKGROUND, overlay, -1)
            .direction(Direction.RIGHT)
            .size(MILESTONE_PROGRESS_BAR_WIDTH, MILESTONE_PROGRESS_BAR_HEIGHT)
            .pos(x, y);
    }

    private ModularPanel createIndividualMilestonePanel(ModularPanel parent, PanelSyncManager syncManager) {
        Dialog<?> panel = new Dialog<>(INDIVIDUAL_MILESTONE_PANEL_KEY, null);
        panel.relative(parent)
            .size(INDIVIDUAL_MILESTONE_SIZE)
            .background(GTGuiTextures.BACKGROUND_GLOW_WHITE);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true)
            .disableHoverBackground();

        panel.child(ForgeOfGodsGuiUtil.panelCloseButton());
        for (int milestoneId = 0; milestoneId < MilestoneIcon.VALUES.length; milestoneId++) {
            panel.child(createIndividualMilestoneSymbol(syncManager, milestoneId));
        }
        panel.child(createFormatterButton(syncManager));
        panel.child(createIndividualMilestoneTexts(syncManager));
        return panel;
    }

    private Widget<?> createIndividualMilestoneSymbol(PanelSyncManager syncManager, int milestoneId) {
        IntSyncValue currentMilestoneSyncer = syncManager
            .findSyncHandler(CURRENT_MILESTONE_SYNC_KEY, IntSyncValue.class);
        MilestoneIcon icon = MilestoneIcon.VALUES[milestoneId];
        int iconWidth = (int) (INDIVIDUAL_MILESTONE_SIZE / 2.0f * icon.getWidthRatio());
        int iconHeight = INDIVIDUAL_MILESTONE_SIZE / 2;
        return getMilestoneSymbol(milestoneId).asWidget()
            .size(iconWidth, iconHeight)
            .center()
            .setEnabledIf(widget -> currentMilestoneSyncer.getIntValue() == milestoneId);
    }

    private UITexture getMilestoneSymbol(int milestoneId) {
        return switch (milestoneId) {
            case 1 -> GTGuiTextures.PICTURE_GODFORGE_MILESTONE_CONVERSION;
            case 2 -> GTGuiTextures.PICTURE_GODFORGE_MILESTONE_CATALYST;
            case 3 -> GTGuiTextures.PICTURE_GODFORGE_MILESTONE_COMPOSITION;
            default -> GTGuiTextures.PICTURE_GODFORGE_MILESTONE_CHARGE;
        };
    }

    private ButtonWidget<?> createFormatterButton(PanelSyncManager syncManager) {
        EnumSyncValue<MilestoneFormatter, ?> formatterSyncer = syncManager
            .findSyncHandler(FORMATTER_SYNC_KEY, EnumSyncValue.class);
        return new ButtonWidget<>().background(GTGuiTextures.TT_OVERLAY_CYCLIC_BLUE)
            .disableHoverBackground()
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound())
            .onMousePressed(mouseButton -> {
                formatterSyncer.setValue(
                    formatterSyncer.getValue()
                        .cycle(),
                    true,
                    true);
                return true;
            })
            .size(10)
            .marginLeft(5)
            .marginBottom(5)
            .bottomRel(0)
            .leftRel(0)
            .tooltip(tooltip -> tooltip.addLine(translateToLocal("fog.button.formatting.tooltip")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private Flow createIndividualMilestoneTexts(PanelSyncManager syncManager) {
        return Flow.column()
            .coverChildren()
            .childPadding(10)
            .marginTop(12)
            .child(createIndividualMilestoneHeader(syncManager))
            .child(createIndividualMilestoneText(() -> getTotalMilestoneProgress(syncManager)))
            .child(createIndividualMilestoneText(() -> getCurrentMilestoneLevel(syncManager)))
            .child(createIndividualMilestoneText(() -> getMilestoneProgressText(syncManager)))
            .child(createIndividualMilestoneText(() -> getGravitonShardAmountText(syncManager)));
    }

    private TextWidget<?> createIndividualMilestoneHeader(PanelSyncManager syncManager) {
        IntSyncValue currentMilestoneSyncer = syncManager
            .findSyncHandler(CURRENT_MILESTONE_SYNC_KEY, IntSyncValue.class);
        return IKey.dynamic(() -> translateToLocal(getMilestoneNameKey(currentMilestoneSyncer.getIntValue())))
            .style(EnumChatFormatting.GOLD)
            .alignment(Alignment.CENTER)
            .asWidget()
            .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE)
            .marginBottom(6);
    }

    private TextWidget<?> createIndividualMilestoneText(Supplier<String> textSupplier) {
        return IKey.dynamic(textSupplier)
            .alignment(Alignment.CENTER)
            .scale(0.7f)
            .asWidget()
            .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE)
            .width(140);
    }

    private String getMilestoneNameKey(int milestoneId) {
        return switch (milestoneId) {
            case 1 -> "gt.blockmachines.multimachine.FOG.recipemilestone";
            case 2 -> "gt.blockmachines.multimachine.FOG.fuelmilestone";
            case 3 -> "gt.blockmachines.multimachine.FOG.purchasablemilestone";
            default -> "gt.blockmachines.multimachine.FOG.powermilestone";
        };
    }

    private String getTotalMilestoneProgress(PanelSyncManager syncManager) {
        int milestoneId = getCurrentMilestoneId(syncManager);
        MilestoneFormatter formatter = getFormatter(syncManager);
        Number progress = switch (milestoneId) {
            case 1 -> syncManager.findSyncHandler(TOTAL_RECIPES_SYNC_KEY, LongSyncValue.class)
                .getLongValue();
            case 2 -> syncManager.findSyncHandler(TOTAL_FUEL_SYNC_KEY, LongSyncValue.class)
                .getLongValue();
            case 3 -> syncManager.findSyncHandler(getMilestoneLevelSyncKey(3), IntSyncValue.class)
                .getIntValue();
            default -> syncManager.findSyncHandler(TOTAL_POWER_SYNC_KEY, BigIntSyncValue.class)
                .getValue();
        };
        return translateToLocal("gt.blockmachines.multimachine.FOG.totalprogress") + ": "
            + EnumChatFormatting.GRAY
            + formatter.format(progress)
            + " "
            + translateToLocal(getMilestoneProgressUnitKey(milestoneId));
    }

    private String getCurrentMilestoneLevel(PanelSyncManager syncManager) {
        int level = Math.min(getCurrentMilestoneLevelValue(syncManager), 7);
        return translateToLocal("gt.blockmachines.multimachine.FOG.milestoneprogress") + ": "
            + EnumChatFormatting.GRAY
            + level;
    }

    private String getMilestoneProgressText(PanelSyncManager syncManager) {
        int milestoneId = getCurrentMilestoneId(syncManager);
        int level = getCurrentMilestoneLevelValue(syncManager);
        MilestoneFormatter formatter = getFormatter(syncManager);
        if (level >= 7) {
            return translateToLocal("gt.blockmachines.multimachine.FOG.milestonecomplete")
                + (formatter != EternalGregTechWorkshop.DEFAULT_FORMATTING_MODE ? EnumChatFormatting.DARK_RED + "?"
                    : "");
        }

        Number max = switch (milestoneId) {
            case 1 -> LongMath.pow(4, level) * LongMath.pow(10, 7);
            case 2 -> LongMath.pow(3, level) * LongMath.pow(10, 4);
            case 3 -> level + 1;
            default -> BigInteger.valueOf(LongMath.pow(9, level))
                .multiply(BigInteger.valueOf(EternalGregTechWorkshop.POWER_MILESTONE_CONSTANT));
        };
        return translateToLocal("gt.blockmachines.multimachine.FOG.progress") + ": "
            + EnumChatFormatting.GRAY
            + formatter.format(max)
            + " "
            + translateToLocal(getMilestoneProgressUnitKey(milestoneId));
    }

    private String getGravitonShardAmountText(PanelSyncManager syncManager) {
        int level = getCurrentMilestoneLevelValue(syncManager);
        int shardSum = level * (level + 1) / 2;
        return translateToLocal("gt.blockmachines.multimachine.FOG.shardgain") + ": "
            + EnumChatFormatting.GRAY
            + getFormatter(syncManager).format(shardSum);
    }

    private int getCurrentMilestoneId(PanelSyncManager syncManager) {
        return syncManager.findSyncHandler(CURRENT_MILESTONE_SYNC_KEY, IntSyncValue.class)
            .getIntValue();
    }

    private int getCurrentMilestoneLevelValue(PanelSyncManager syncManager) {
        return syncManager
            .findSyncHandler(getMilestoneLevelSyncKey(getCurrentMilestoneId(syncManager)), IntSyncValue.class)
            .getIntValue();
    }

    private MilestoneFormatter getFormatter(PanelSyncManager syncManager) {
        EnumSyncValue<MilestoneFormatter, ?> formatterSyncer = syncManager
            .findSyncHandler(FORMATTER_SYNC_KEY, EnumSyncValue.class);
        return formatterSyncer.getValue();
    }

    private String getMilestoneProgressUnitKey(int milestoneId) {
        return switch (milestoneId) {
            case 1 -> "gt.blockmachines.multimachine.FOG.recipes";
            case 2 -> "gt.blockmachines.multimachine.FOG.fuelconsumed";
            case 3 -> "gt.blockmachines.multimachine.FOG.extensions";
            default -> "gt.blockmachines.multimachine.FOG.power";
        };
    }

    private ModularPanel createUpgradeTreePanel(ModularPanel parent, PanelSyncManager syncManager) {
        Dialog<?> panel = new Dialog<>(UPGRADE_TREE_PANEL_KEY, null);
        panel.relative(parent)
            .size(UPGRADE_TREE_SIZE)
            .padding(4, 0, 4, 0)
            .background(GTGuiTextures.BACKGROUND_STAR);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true)
            .disableHoverBackground();

        VerticalScrollData scrollData = new VerticalScrollData();
        scrollData.setScrollSize(UPGRADE_TREE_SCROLL_SIZE);
        ScrollWidget<?> tree = new ScrollWidget<>(scrollData).size(292);
        addUpgradeConnectors(tree, syncManager);
        for (EternalGregTechWorkshopUpgrade upgrade : EternalGregTechWorkshopUpgrade.VALUES) {
            tree.child(createUpgradeButton(upgrade, syncManager));
        }
        tree.child(createSecretUpgrade(syncManager));
        tree.child(
            new Widget<>().size(1, 1)
                .pos(0, 945));

        panel.child(ForgeOfGodsGuiUtil.panelCloseButton());
        panel.child(tree);
        panel.child(createUpgradeDebugWidgets(syncManager));
        return panel;
    }

    private void addUpgradeConnectors(ScrollWidget<?> tree, PanelSyncManager syncManager) {
        tree.child(
            createUpgradeConnector(
                syncManager,
                EternalGregTechWorkshopUpgrade.START,
                EternalGregTechWorkshopUpgrade.IGCC))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.IGCC,
                    EternalGregTechWorkshopUpgrade.STEM))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.IGCC,
                    EternalGregTechWorkshopUpgrade.CFCE))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.STEM,
                    EternalGregTechWorkshopUpgrade.GISS))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.STEM,
                    EternalGregTechWorkshopUpgrade.FDIM))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.CFCE,
                    EternalGregTechWorkshopUpgrade.FDIM))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.CFCE,
                    EternalGregTechWorkshopUpgrade.SA))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.FDIM,
                    EternalGregTechWorkshopUpgrade.GPCI))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.GPCI,
                    EternalGregTechWorkshopUpgrade.GEM))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.GISS,
                    EternalGregTechWorkshopUpgrade.REC))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.GPCI,
                    EternalGregTechWorkshopUpgrade.REC))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.SA,
                    EternalGregTechWorkshopUpgrade.CTCDD))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.GPCI,
                    EternalGregTechWorkshopUpgrade.CTCDD))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.REC,
                    EternalGregTechWorkshopUpgrade.QGPIU))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.CTCDD,
                    EternalGregTechWorkshopUpgrade.QGPIU))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.QGPIU,
                    EternalGregTechWorkshopUpgrade.TCT))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.TCT,
                    EternalGregTechWorkshopUpgrade.EPEC))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.EPEC,
                    EternalGregTechWorkshopUpgrade.POS))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.POS,
                    EternalGregTechWorkshopUpgrade.NGMS))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.QGPIU,
                    EternalGregTechWorkshopUpgrade.SEFCP))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.SEFCP,
                    EternalGregTechWorkshopUpgrade.CNTI))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.CNTI,
                    EternalGregTechWorkshopUpgrade.NDPE))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.NDPE,
                    EternalGregTechWorkshopUpgrade.NGMS))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.CNTI,
                    EternalGregTechWorkshopUpgrade.DOP))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.QGPIU,
                    EternalGregTechWorkshopUpgrade.GGEBE))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.GGEBE,
                    EternalGregTechWorkshopUpgrade.IMKG))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.IMKG,
                    EternalGregTechWorkshopUpgrade.DOR))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.DOR,
                    EternalGregTechWorkshopUpgrade.NGMS))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.GGEBE,
                    EternalGregTechWorkshopUpgrade.TPTP))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.NGMS,
                    EternalGregTechWorkshopUpgrade.SEDS))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.SEDS,
                    EternalGregTechWorkshopUpgrade.PA))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.PA,
                    EternalGregTechWorkshopUpgrade.CD))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.CD,
                    EternalGregTechWorkshopUpgrade.TSE))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.TSE,
                    EternalGregTechWorkshopUpgrade.TBF))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.TBF,
                    EternalGregTechWorkshopUpgrade.EE))
            .child(
                createUpgradeConnector(
                    syncManager,
                    EternalGregTechWorkshopUpgrade.EE,
                    EternalGregTechWorkshopUpgrade.END));
    }

    private Widget<?> createUpgradeConnector(PanelSyncManager syncManager, EternalGregTechWorkshopUpgrade fromUpgrade,
        EternalGregTechWorkshopUpgrade toUpgrade) {
        int fromCenterX = fromUpgrade.getTreeXForGui() + UPGRADE_BUTTON_WIDTH / 2;
        int fromCenterY = fromUpgrade.getTreeYForGui() + UPGRADE_BUTTON_HEIGHT / 2;
        int toCenterX = toUpgrade.getTreeXForGui() + UPGRADE_BUTTON_WIDTH / 2;
        int toCenterY = toUpgrade.getTreeYForGui() + UPGRADE_BUTTON_HEIGHT / 2;
        int width = 6;
        int height = (int) Math.sqrt(Math.pow(toCenterX - fromCenterX, 2) + Math.pow(toCenterY - fromCenterY, 2));
        float rotation = (float) (Math.atan2(toCenterY - fromCenterY, toCenterX - fromCenterX) - Math.PI / 2);
        int x = (fromCenterX + toCenterX) / 2 - width / 2;
        int y = (fromCenterY + toCenterY) / 2 - height / 2;

        return new DynamicDrawable(() -> {
            IDrawable texture = fromUpgrade.getColorForGui()
                .getMui2Connector();
            if (isUpgradeActive(syncManager, fromUpgrade) && isUpgradeActive(syncManager, toUpgrade)) {
                texture = fromUpgrade.getColorForGui()
                    .getMui2OpaqueConnector();
            }
            return new RotatedDrawable(texture).rotationRadian(rotation);
        }).asWidget()
            .pos(x, y)
            .size(width, height);
    }

    private ButtonWidget<?> createUpgradeButton(EternalGregTechWorkshopUpgrade upgrade, PanelSyncManager syncManager) {
        EnumSyncValue<EternalGregTechWorkshopUpgrade, ?> upgradeSyncer = getCurrentUpgradeSyncer(syncManager);
        IPanelHandler individualPanel = panelMap.get(INDIVIDUAL_UPGRADE_PANEL_KEY);
        IPanelHandler manualInsertionPanel = panelMap.get(MANUAL_INSERTION_PANEL_KEY);
        IPanelHandler treePanel = panelMap.get(UPGRADE_TREE_PANEL_KEY);

        return new ButtonWidget<>().size(UPGRADE_BUTTON_WIDTH, UPGRADE_BUTTON_HEIGHT)
            .pos(upgrade.getTreeXForGui(), upgrade.getTreeYForGui())
            .disableThemeBackground(true)
            .disableHoverThemeBackground(true)
            .overlay(
                new DynamicDrawable(
                    () -> isUpgradeActive(syncManager, upgrade) ? GTGuiTextures.BUTTON_SPACE_PRESSED_32x16
                        : GTGuiTextures.BUTTON_SPACE_32x16),
                IKey.str(upgrade.getShortNameText())
                    .style(EnumChatFormatting.GOLD)
                    .scale(0.8f)
                    .alignment(Alignment.CENTER))
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
                if (mouseData.mouseButton == 0) {
                    upgradeSyncer.setValue(upgrade, true, true);
                    if (mouseData.shift) {
                        if (!upgrade.hasExtraCost() || multiblock.isUpgradeCostPaidForGui(upgrade)) {
                            multiblock.completeUpgradeFromGui(upgrade);
                            notifyUpgradeState(syncManager);
                        } else {
                            if (mouseData.isClient()) {
                                if (manualInsertionPanel != null) manualInsertionPanel.openPanel();
                                if (individualPanel != null && individualPanel.isPanelOpen())
                                    individualPanel.closePanel();
                                if (treePanel != null && treePanel.isPanelOpen()) treePanel.closePanel();
                            }
                        }
                    } else if (mouseData.isClient() && individualPanel != null) {
                        individualPanel.openPanel();
                    }
                } else if (mouseData.mouseButton == 1) {
                    multiblock.respecUpgradeFromGui(upgrade);
                    notifyUpgradeState(syncManager);
                }
            }))
            .tooltip(tooltip -> tooltip.addLine(upgrade.getNameText()))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound());
    }

    private Flow createSecretUpgrade(PanelSyncManager syncManager) {
        BooleanSyncValue secretSyncer = syncManager.findSyncHandler(SECRET_UPGRADE_SYNC_KEY, BooleanSyncValue.class);
        return Flow.row()
            .size(60, 15)
            .pos(
                EternalGregTechWorkshopUpgrade.START.getTreeXForGui() - 60,
                EternalGregTechWorkshopUpgrade.START.getTreeYForGui())
            .child(
                new ButtonWidget<>().size(40, 15)
                    .background(
                        new DynamicDrawable(
                            () -> secretSyncer.getBoolValue() ? GTGuiTextures.BUTTON_SPACE_PRESSED_32x16
                                : IDrawable.EMPTY))
                    .overlay(
                        new DynamicDrawable(
                            () -> secretSyncer.getBoolValue() ? IKey.lang("fog.upgrade.tt.short.secret")
                                .style(EnumChatFormatting.GOLD)
                                .scale(0.8f)
                                .alignment(Alignment.CENTER) : IDrawable.EMPTY))
                    .onMousePressed(mouseButton -> {
                        secretSyncer.setBoolValue(!secretSyncer.getBoolValue(), true, true);
                        return true;
                    })
                    .tooltip(tooltip -> tooltip.addLine(translateToLocal("fog.upgrade.tt.secret")))
                    .tooltipShowUpTimer(20)
                    .clickSound(ForgeOfGodsGuiUtil.getButtonSound()))
            .child(
                GTGuiTextures.PICTURE_UPGRADE_CONNECTOR_BLUE_OPAQUE.asWidget()
                    .size(20, 6)
                    .setEnabledIf(widget -> secretSyncer.getBoolValue()));
    }

    private Flow createUpgradeDebugWidgets(PanelSyncManager syncManager) {
        IntSyncValue shardSyncer = syncManager.findSyncHandler(GRAVITON_SHARDS_SYNC_KEY, IntSyncValue.class);
        return Flow.column()
            .coverChildren()
            .topRel(0)
            .leftRel(0)
            .setEnabledIf(widget -> MainConfig.debug.enableDebugMode)
            .child(new ButtonWidget<>().syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
                multiblock.resetUpgradesFromGui();
                notifyUpgradeState(syncManager);
            }))
                .overlay(
                    IKey.lang("fog.debug.resetbutton.text")
                        .alignment(Alignment.CENTER)
                        .scale(0.57f))
                .size(40, 15)
                .tooltip(tooltip -> tooltip.addLine(translateToLocal("fog.debug.resetbutton.tooltip")))
                .tooltipShowUpTimer(TOOLTIP_DELAY))
            .child(
                new TextFieldWidget().formatAsInteger(true)
                    .numbersInt(0, 112)
                    .setTextAlignment(Alignment.CENTER)
                    .value(shardSyncer)
                    .setScrollValues(1, 4, 64)
                    .size(25, 18)
                    .tooltip(tooltip -> tooltip.addLine(translateToLocal("fog.debug.gravitonshardsetter.tooltip")))
                    .tooltipShowUpTimer(TOOLTIP_DELAY))
            .child(new ButtonWidget<>().syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
                multiblock.unlockAllUpgradesFromGui();
                notifyUpgradeState(syncManager);
            }))
                .overlay(
                    IKey.lang("fog.debug.unlockall.text")
                        .alignment(Alignment.CENTER)
                        .scale(0.57f))
                .size(40, 15)
                .tooltip(tooltip -> tooltip.addLine(translateToLocal("fog.debug.unlockall.text")))
                .tooltipShowUpTimer(TOOLTIP_DELAY));
    }

    private ModularPanel createIndividualUpgradePanel(ModularPanel parent, PanelSyncManager syncManager) {
        Dialog<?> panel = new Dialog<>(INDIVIDUAL_UPGRADE_PANEL_KEY, null);
        panel.relative(parent)
            .size(300)
            .background(IDrawable.EMPTY);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true)
            .disableHoverBackground();

        for (EternalGregTechWorkshopUpgrade upgrade : EternalGregTechWorkshopUpgrade.VALUES) {
            panel.child(createIndividualUpgradeContent(upgrade, syncManager));
        }
        return panel;
    }

    private ParentWidget<?> createIndividualUpgradeContent(EternalGregTechWorkshopUpgrade upgrade,
        PanelSyncManager syncManager) {
        EnumSyncValue<EternalGregTechWorkshopUpgrade, ?> upgradeSyncer = getCurrentUpgradeSyncer(syncManager);
        int width = upgrade.getWindowWidthForGui();
        int height = upgrade.getWindowHeightForGui();
        IPanelHandler manualInsertionPanel = panelMap.get(MANUAL_INSERTION_PANEL_KEY);
        IPanelHandler treePanel = panelMap.get(UPGRADE_TREE_PANEL_KEY);

        ParentWidget<?> content = new ParentWidget<>().size(width, height)
            .center()
            .setEnabledIf(widget -> upgradeSyncer.getValue() == upgrade)
            .child(
                upgrade.getMui2Background()
                    .asWidget()
                    .size(width, height))
            .child(ForgeOfGodsGuiUtil.panelCloseButton());

        content.child(
            upgrade.getMui2Symbol()
                .asWidget()
                .size((int) (width / 2.0f * upgrade.getSymbolWidthRatio()), height / 2)
                .center());
        content.child(
            upgrade.getMui2Overlay()
                .asWidget()
                .size(width / 2, height / 2)
                .center());
        content.child(
            Flow.column()
                .size(width - 16, height - 26)
                .marginTop(15)
                .horizontalCenter()
                .child(
                    IKey.str(upgrade.getNameText())
                        .style(EnumChatFormatting.GOLD)
                        .alignment(Alignment.CENTER)
                        .asWidget())
                .child(
                    IKey.str(upgrade.getBodyText())
                        .style(EnumChatFormatting.WHITE)
                        .alignment(Alignment.CENTER)
                        .asWidget()
                        .height(upgrade.getLoreYPos() - 30)
                        .marginTop(7))
                .child(
                    IKey.str(upgrade.getLoreText())
                        .style(EnumChatFormatting.ITALIC)
                        .color(0xFFBBBDBD)
                        .alignment(Alignment.CENTER)
                        .asWidget()
                        .height((int) (height * 0.9) - upgrade.getLoreYPos())
                        .marginTop(5))
                .child(createIndividualUpgradeBottomRow(upgrade, syncManager, manualInsertionPanel, treePanel)));
        return content;
    }

    private ParentWidget<?> createIndividualUpgradeBottomRow(EternalGregTechWorkshopUpgrade upgrade,
        PanelSyncManager syncManager, IPanelHandler manualInsertionPanel, IPanelHandler treePanel) {
        return new ParentWidget<>().fullWidth()
            .height(15)
            .bottomRel(0)
            .horizontalCenter()
            .child(
                IKey.dynamic(
                    () -> translateToLocal("gt.blockmachines.multimachine.FOG.shardcost") + " "
                        + EnumChatFormatting.BLUE
                        + upgrade.getShardCost())
                    .alignment(Alignment.CENTER)
                    .scale(0.7f)
                    .color(0xFF9C9C9C)
                    .asWidget()
                    .size(70, 15)
                    .leftRel(0))
            .child(IKey.dynamic(() -> {
                int shards = getShardSyncer(syncManager).getIntValue();
                EnumChatFormatting color = shards >= upgrade.getShardCost() ? EnumChatFormatting.GREEN
                    : EnumChatFormatting.RED;
                return translateToLocal("gt.blockmachines.multimachine.FOG.availableshards") + " "
                    + color
                    + getFormatter(syncManager).format(shards);
            })
                .alignment(Alignment.CENTER)
                .scale(0.7f)
                .color(0xFF9C9C9C)
                .asWidget()
                .size(70, 15)
                .rightRel(0))
            .child(
                Flow.row()
                    .size(78, 15)
                    .horizontalCenter()
                    .childPadding(4)
                    .child(createExtraCostButton(upgrade, manualInsertionPanel, treePanel))
                    .child(createConfirmOrRespecButton(upgrade, syncManager)));
    }

    private ButtonWidget<?> createExtraCostButton(EternalGregTechWorkshopUpgrade upgrade,
        IPanelHandler manualInsertionPanel, IPanelHandler treePanel) {
        return new ButtonWidget<>().size(15)
            .disableHoverThemeBackground(true)
            .background(
                new DynamicDrawable(
                    () -> multiblock.isUpgradeCostPaidForGui(upgrade) ? GTGuiTextures.BUTTON_BOXED_CHECKMARK_18x18
                        : GTGuiTextures.BUTTON_BOXED_EXCLAMATION_POINT_18x18))
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
                if (mouseData.isClient()) {
                    if (treePanel != null && treePanel.isPanelOpen()) treePanel.closePanel();
                    if (manualInsertionPanel != null && manualInsertionPanel.isPanelOpen())
                        manualInsertionPanel.closePanel();
                    if (manualInsertionPanel != null) manualInsertionPanel.openPanel();
                }
            }))
            .tooltipDynamic(tooltip -> {
                if (multiblock.isUpgradeCostPaidForGui(upgrade)) {
                    tooltip.addLine(translateToLocal("fog.button.materialrequirementsmet.tooltip"));
                } else {
                    tooltip.addLine(translateToLocal("fog.button.materialrequirements.tooltip"));
                }
                tooltip.addLine(
                    EnumChatFormatting.GRAY + translateToLocal("fog.button.materialrequirements.tooltip.clickhere"));
            })
            .tooltipAutoUpdate(true)
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound())
            .setEnabledIf(widget -> upgrade.hasExtraCost());
    }

    private ButtonWidget<?> createConfirmOrRespecButton(EternalGregTechWorkshopUpgrade upgrade,
        PanelSyncManager syncManager) {
        return new ButtonWidget<>().size(40, 15)
            .background(
                new DynamicDrawable(
                    () -> isUpgradeActive(syncManager, upgrade) ? GTGuiTextures.BUTTON_OUTLINE_HOLLOW_PRESSED
                        : GTGuiTextures.BUTTON_OUTLINE_HOLLOW))
            .overlay(
                new DynamicDrawable(
                    () -> isUpgradeActive(syncManager, upgrade) ? IKey.lang("fog.upgrade.respec")
                        .alignment(Alignment.CENTER)
                        .scale(0.7f)
                        : IKey.lang("fog.upgrade.confirm")
                            .alignment(Alignment.CENTER)
                            .scale(0.7f)))
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
                if (multiblock.isUpgradeActiveForGui(upgrade)) {
                    multiblock.respecUpgradeFromGui(upgrade);
                } else {
                    multiblock.completeUpgradeFromGui(upgrade);
                }
                notifyUpgradeState(syncManager);
            }))
            .tooltipDynamic(
                tooltip -> tooltip.addLine(
                    translateToLocal(
                        isUpgradeActive(syncManager, upgrade) ? "fog.upgrade.respec" : "fog.upgrade.confirm")))
            .tooltipAutoUpdate(true)
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound());
    }

    private ModularPanel createManualInsertionPanel(ModularPanel parent, PanelSyncManager syncManager) {
        syncManager.registerSlotGroup(UPGRADE_INPUT_SLOT_GROUP, 4);
        Dialog<?> panel = new Dialog<>(MANUAL_INSERTION_PANEL_KEY, null);
        panel.relative(parent)
            .leftRelOffset(0, 4)
            .topRelOffset(0, 3)
            .size(MANUAL_INSERTION_PANEL_WIDTH, MANUAL_INSERTION_PANEL_HEIGHT)
            .background(GTGuiTextures.BACKGROUND_STANDARD)
            .onCloseAction(() -> {
                IPanelHandler treePanel = panelMap.get(UPGRADE_TREE_PANEL_KEY);
                IPanelHandler individualPanel = panelMap.get(INDIVIDUAL_UPGRADE_PANEL_KEY);
                if (treePanel != null && !treePanel.isPanelOpen()) treePanel.openPanel();
                if (individualPanel != null && !individualPanel.isPanelOpen()) individualPanel.openPanel();
            });
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true)
            .disableHoverBackground();

        panel.child(ForgeOfGodsGuiUtil.panelCloseButtonStandard());
        panel.child(
            IKey.lang("gt.blockmachines.multimachine.FOG.payUpgradeCosts")
                .style(EnumChatFormatting.DARK_GRAY)
                .alignment(Alignment.CENTER)
                .asWidget()
                .horizontalCenter()
                .marginTop(5));
        panel.child(
            Flow.row()
                .size(MANUAL_INSERTION_PANEL_WIDTH - 10, 72)
                .topRel(0)
                .leftRel(0)
                .marginLeft(5)
                .marginTop(16)
                .child(createCostGrid(syncManager))
                .child(createUpgradeInputSlots(syncManager)));
        panel.child(createConsumeUpgradeMaterialsButton(syncManager));
        return panel;
    }

    private Flow createCostGrid(PanelSyncManager syncManager) {
        Flow grid = Flow.row()
            .size(EXTRA_COST_COLUMNS * EXTRA_COST_ROW_WIDTH, EXTRA_COST_ROWS * EXTRA_COST_ROW_HEIGHT);
        for (int column = 0; column < EXTRA_COST_COLUMNS; column++) {
            grid.child(createCostColumn(syncManager, column));
        }
        return grid;
    }

    private Flow createCostColumn(PanelSyncManager syncManager, int columnIndex) {
        Flow column = Flow.column()
            .size(EXTRA_COST_ROW_WIDTH, EXTRA_COST_ROWS * EXTRA_COST_ROW_HEIGHT);
        for (int row = 0; row < EXTRA_COST_ROWS; row++) {
            column.child(createCostRow(syncManager, columnIndex + row * EXTRA_COST_COLUMNS));
        }
        return column;
    }

    private Flow createCostRow(PanelSyncManager syncManager, int index) {
        return Flow.row()
            .size(EXTRA_COST_ROW_WIDTH, EXTRA_COST_ROW_HEIGHT)
            .collapseDisabledChild()
            .child(
                GTGuiTextures.BUTTON_STANDARD_DISABLED.asWidget()
                    .size(18)
                    .setEnabledIf(widget -> !hasExtraCost(syncManager, index)))
            .child(new SlotLikeButtonWidget(() -> getExtraCost(syncManager, index)).onMousePressed(mouseButton -> {
                ItemStack stack = getExtraCost(syncManager, index);
                if (stack == null) return false;
                if (mouseButton == 0) {
                    GuiCraftingRecipe.openRecipeGui("item", stack);
                } else if (mouseButton == 1) {
                    GuiUsageRecipe.openRecipeGui("item", stack);
                }
                return true;
            })
                .tooltipDynamic(tooltip -> {
                    ItemStack stack = getExtraCost(syncManager, index);
                    if (stack != null) tooltip.addFromItem(stack);
                })
                .tooltipAutoUpdate(true)
                .setEnabledIf(widget -> hasExtraCost(syncManager, index)))
            .child(
                IKey.dynamic(() -> getRemainingExtraCostText(syncManager, index))
                    .alignment(Alignment.CENTER)
                    .scale(0.8f)
                    .asWidget()
                    .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE)
                    .size(18)
                    .setEnabledIf(widget -> hasExtraCost(syncManager, index) && !isExtraCostPaid(syncManager, index)))
            .child(
                GTGuiTextures.GREEN_CHECKMARK_11x9.asWidget()
                    .size(11, 9)
                    .marginRight(4)
                    .marginTop(5)
                    .setEnabledIf(widget -> isExtraCostPaid(syncManager, index)));
    }

    private SlotGroupWidget createUpgradeInputSlots(PanelSyncManager syncManager) {
        GTNLMui2ItemHandlerAdapter adapter = new GTNLMui2ItemHandlerAdapter(multiblock.getUpgradeInputHandlerForGui());
        String[] matrix = { "ssss", "ssss", "ssss", "ssss" };
        return SlotGroupWidget.builder()
            .matrix(matrix)
            .key('s', index -> new ItemSlot().slot(new ModularSlot(adapter, index).slotGroup(UPGRADE_INPUT_SLOT_GROUP)))
            .build()
            .rightRel(0);
    }

    private ButtonWidget<?> createConsumeUpgradeMaterialsButton(PanelSyncManager syncManager) {
        return new ButtonWidget<>().overlay(
            IKey.lang("gt.blockmachines.multimachine.FOG.consumeUpgradeMats")
                .style(EnumChatFormatting.DARK_GRAY)
                .alignment(Alignment.CENTER)
                .scale(0.75f))
            .disableHoverBackground()
            .disableHoverOverlay()
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
                multiblock.payUpgradeCostFromGui(getCurrentUpgradeSyncer(syncManager).getValue());
                notifyUpgradeState(syncManager);
            }))
            .size(MANUAL_INSERTION_PANEL_WIDTH - 10, 18)
            .bottomRel(0)
            .leftRel(0)
            .marginBottom(5)
            .marginLeft(5)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound());
    }

    private boolean hasExtraCost(PanelSyncManager syncManager, int index) {
        return getExtraCost(syncManager, index) != null;
    }

    private ItemStack getExtraCost(PanelSyncManager syncManager, int index) {
        ItemStack[] costs = getCurrentUpgradeSyncer(syncManager).getValue()
            .getExtraCost();
        if (index < 0 || index >= costs.length) return null;
        return costs[index];
    }

    private boolean isExtraCostPaid(PanelSyncManager syncManager, int index) {
        ItemStack stack = getExtraCost(syncManager, index);
        if (stack == null) return false;
        return getPaidCosts(syncManager, getCurrentUpgradeSyncer(syncManager).getValue())[index] >= stack.stackSize;
    }

    private String getRemainingExtraCostText(PanelSyncManager syncManager, int index) {
        ItemStack stack = getExtraCost(syncManager, index);
        if (stack == null) return "";
        int paid = getPaidCosts(syncManager, getCurrentUpgradeSyncer(syncManager).getValue())[index];
        int remaining = Math.max(0, stack.stackSize - paid);
        EnumChatFormatting color = EnumChatFormatting.YELLOW;
        if (paid == 0) {
            color = EnumChatFormatting.RED;
        } else if (remaining == 0) {
            color = EnumChatFormatting.GREEN;
        }
        return color + "x" + remaining;
    }

    private boolean isUpgradeActive(PanelSyncManager syncManager, EternalGregTechWorkshopUpgrade upgrade) {
        return getUpgradeData(syncManager, upgrade).active;
    }

    private int[] getPaidCosts(PanelSyncManager syncManager, EternalGregTechWorkshopUpgrade upgrade) {
        return getUpgradeData(syncManager, upgrade).amountsPaid;
    }

    private EGTWUpgradeStorage.UpgradeData getUpgradeData(PanelSyncManager syncManager,
        EternalGregTechWorkshopUpgrade upgrade) {
        GenericListSyncHandler<EGTWUpgradeStorage.UpgradeData> syncer = syncManager
            .findSyncHandler(UPGRADE_DATA_SYNC_KEY, GenericListSyncHandler.class);
        return syncer.getValue()
            .get(upgrade.ordinal());
    }

    private EnumSyncValue<EternalGregTechWorkshopUpgrade, ?> getCurrentUpgradeSyncer(PanelSyncManager syncManager) {
        return syncManager.findSyncHandler(CURRENT_UPGRADE_SYNC_KEY, EnumSyncValue.class);
    }

    private IntSyncValue getShardSyncer(PanelSyncManager syncManager) {
        return syncManager.findSyncHandler(GRAVITON_SHARDS_SYNC_KEY, IntSyncValue.class);
    }

    private void notifyUpgradeState(PanelSyncManager syncManager) {
        syncManager.findSyncHandler(UPGRADE_DATA_SYNC_KEY, GenericListSyncHandler.class)
            .notifyUpdate();
        syncManager.findSyncHandler(GRAVITON_SHARDS_SYNC_KEY, IntSyncValue.class)
            .notifyUpdate();
    }

    private void togglePanel(IPanelHandler panelHandler) {
        if (panelHandler == null) return;
        if (panelHandler.isPanelOpen()) {
            panelHandler.closePanel();
        } else {
            panelHandler.openPanel();
        }
    }

    private static String getMilestoneLevelSyncKey(int milestoneId) {
        return MILESTONE_LEVEL_SYNC_KEY_PREFIX + milestoneId;
    }

    private static String getMilestoneProgressSyncKey(int milestoneId) {
        return MILESTONE_PROGRESS_SYNC_KEY_PREFIX + milestoneId;
    }
}
