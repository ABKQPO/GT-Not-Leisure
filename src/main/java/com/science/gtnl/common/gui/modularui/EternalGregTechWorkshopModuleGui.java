package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static net.minecraft.util.StatCollector.translateToLocal;

import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.GuiAxis;
import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Alignment.MainAxis;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.EternalGregTechWorkshopModule;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.multiblock.godforge.ForgeOfGodsGuiUtil;

public class EternalGregTechWorkshopModuleGui extends GTNLMultiBlockBaseGui<EternalGregTechWorkshopModule> {

    private static final String CONNECTION_SYNC_KEY = "egtwModuleConnection";
    private static final String GENERAL_INFO_PANEL_KEY = "egtwModuleGeneralInfo";
    private static final int GENERAL_INFO_SIZE = 300;

    public EternalGregTechWorkshopModuleGui(EternalGregTechWorkshopModule multiblock) {
        super(multiblock);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(CONNECTION_SYNC_KEY, new BooleanSyncValue(multiblock::isConnectedForGui));
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
    }

    @Override
    protected Flow createButtonColumn(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.column()
            .width(18)
            .leftRel(1, -3, 1)
            .childPadding(2)
            .mainAxisAlignment(MainAxis.END)
            .reverseLayout(true)
            .child(
                new ItemSlot()
                    .slot(
                        new ModularSlot(multiblock.inventoryHandler, multiblock.getControllerSlotIndex())
                            .slotGroup("item_inv"))
                    .backgroundOverlay(GTGuiTextures.TT_OVERLAY_SLOT_MESH)
                    .overlay(
                        GTGuiTextures.TT_CONTROLLER_SLOT_HEAT_SINK.asIcon()
                            .size(18, 6)
                            .marginTop(22))
                    .marginTop(2))
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
    protected Flow createLeftPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        return super.createLeftPanelGapRow(parent, syncManager).childPadding(2)
            .marginLeft(1)
            .child(createConnectionStatus(syncManager));
    }

    @Override
    protected int getTextBoxToInventoryGap() {
        return 20;
    }

    @Override
    protected ToggleButton createPowerSwitchButton() {
        return super.createPowerSwitchButton().size(16)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .selectedBackground(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .overlay(
                new DynamicDrawable(
                    () -> multiblock.isAllowedToWork() ? GTGuiTextures.TT_OVERLAY_BUTTON_POWER_SWITCH_ON
                        : GTGuiTextures.TT_OVERLAY_BUTTON_POWER_SWITCH_DISABLED))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
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

    @Override
    protected IWidget createVoidExcessButton(PanelSyncManager syncManager) {
        return ((ButtonWidget<?>) super.createVoidExcessButton(syncManager)).size(16)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .overlay(new DynamicDrawable(() -> switch (multiblock.getVoidingMode()) {
            case VOID_NONE -> GTGuiTextures.TT_OVERLAY_BUTTON_VOIDING_OFF;
            case VOID_ITEM -> GTGuiTextures.TT_OVERLAY_BUTTON_VOIDING_ITEMS;
            case VOID_FLUID -> GTGuiTextures.TT_OVERLAY_BUTTON_VOIDING_FLUIDS;
            case VOID_ALL -> GTGuiTextures.TT_OVERLAY_BUTTON_VOIDING_BOTH;
            }))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound());
    }

    @Override
    protected IWidget createInputSeparationButton(PanelSyncManager syncManager) {
        return ((ToggleButton) super.createInputSeparationButton(syncManager)).size(16)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .selectedBackground(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .overlay(
                new DynamicDrawable(
                    () -> multiblock.isInputSeparationEnabled() ? GTGuiTextures.TT_OVERLAY_BUTTON_INPUT_SEPARATION
                        : GTGuiTextures.TT_OVERLAY_BUTTON_INPUT_SEPARATION_OFF))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound());
    }

    @Override
    protected IWidget createBatchModeButton(PanelSyncManager syncManager) {
        return ((ToggleButton) super.createBatchModeButton(syncManager)).size(16)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .selectedBackground(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .overlay(
                new DynamicDrawable(
                    () -> multiblock.isBatchModeEnabled() ? GTGuiTextures.TT_OVERLAY_BUTTON_BATCH_MODE
                        : GTGuiTextures.TT_OVERLAY_BUTTON_BATCH_MODE_OFF))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound());
    }

    @Override
    protected IWidget createLockToSingleRecipeButton(PanelSyncManager syncManager) {
        return ((ToggleButton) super.createLockToSingleRecipeButton(syncManager)).size(16)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .selectedBackground(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .overlay(
                new DynamicDrawable(
                    () -> multiblock.isRecipeLockingEnabled() ? GTGuiTextures.TT_OVERLAY_BUTTON_RECIPE_LOCKED
                        : GTGuiTextures.TT_OVERLAY_BUTTON_RECIPE_UNLOCKED))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound());
    }

    private IWidget createConnectionStatus(PanelSyncManager syncManager) {
        BooleanSyncValue connectionSyncer = syncManager.findSyncHandler(CONNECTION_SYNC_KEY, BooleanSyncValue.class);
        return IKey.dynamic(() -> {
            EnumChatFormatting color = connectionSyncer.getBoolValue() ? EnumChatFormatting.GREEN
                : EnumChatFormatting.RED;
            String status = translateToLocal(
                connectionSyncer.getBoolValue() ? "gt.blockmachines.multimachine.FOG.modulestatus.true"
                    : "gt.blockmachines.multimachine.FOG.modulestatus.false");
            return translateToLocal("gt.blockmachines.multimachine.FOG.modulestatus") + " " + color + status;
        })
            .style(EnumChatFormatting.BLACK)
            .alignment(Alignment.CENTER)
            .asWidget()
            .size(86, 16);
    }

    private IWidget createGeneralInfoButton() {
        IPanelHandler panel = panelMap.get(GENERAL_INFO_PANEL_KEY);
        return new ButtonWidget<>().overlay(IDrawable.EMPTY)
            .background(GTGuiTextures.PICTURE_GODFORGE_LOGO)
            .disableHoverBackground()
            .onMousePressed(mouseButton -> {
                if (panel == null) return false;
                if (panel.isPanelOpen()) {
                    panel.closePanel();
                } else {
                    panel.openPanel();
                }
                return true;
            })
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound())
            .tooltip(tooltip -> tooltip.addLine(translateToLocal("gt.blockmachines.multimachine.FOG.clickhere")))
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
}
