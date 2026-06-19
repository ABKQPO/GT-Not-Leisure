package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.DynamicLinkedSyncHandler;
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.CycleButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;

import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.Reference2LongOpenHashMap;

public class AssemblerMatrixGui extends GTNLMultiBlockBaseGui<AssemblerMatrix> {

    private static final String SHOW_PATTERN_SYNC_KEY = "showPattern";
    private static final String CUSTOM_NAME_SYNC_KEY = "customName";
    private static final String PATTERN_MULTIPLY_SYNC_KEY = "patternMultiply";
    private static final String PATTERN_OUTPUTS_SYNC_KEY = "patternOutputs";
    private static final String PATTERN_OUTPUTS_WIDGET_SYNC_KEY = "patternOutputsWidget";

    public AssemblerMatrixGui(AssemblerMatrix multiblock) {
        super(multiblock);
        withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_PACKAGER,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_UNPACKAGER,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            SHOW_PATTERN_SYNC_KEY,
            new BooleanSyncValue(multiblock::isShowPattern, multiblock::setShowPattern).allowC2S());
        syncManager.syncValue(
            CUSTOM_NAME_SYNC_KEY,
            new StringSyncValue(multiblock::getGuiCustomName, multiblock::setCustomName).allowC2S());
        syncManager.syncValue(
            PATTERN_MULTIPLY_SYNC_KEY,
            new IntSyncValue(multiblock::getPatternMultiply, multiblock::setPatternMultiply).allowC2S());
        GenericListSyncHandler<IAEItemStack> patternOutputsSyncer = new GenericListSyncHandler<>(
            multiblock::getCachedPatternOutputsForGui,
            multiblock::setCachedPatternOutputsFromGui,
            AssemblerMatrix::loadAEItemStackForGui,
            AssemblerMatrix::writeAEItemStackForGui,
            AssemblerMatrixGui::areAEItemStacksEqual,
            AssemblerMatrixGui::copyAEItemStack);
        syncManager.syncValue(PATTERN_OUTPUTS_SYNC_KEY, patternOutputsSyncer);
        syncManager.syncValue(
            PATTERN_OUTPUTS_WIDGET_SYNC_KEY,
            new DynamicLinkedSyncHandler<>(patternOutputsSyncer)
                .widgetProvider((panelSyncManager, syncValue) -> createPatternOutputsWidget(syncValue.getValue())));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return super.createTerminalTextWidget(syncManager, parent).child(createPatternOutputsWidget(syncManager));
    }

    @Override
    protected Flow createLeftPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        Flow row = super.createLeftPanelGapRow(parent, syncManager);
        row.child(createShowPatternButton(syncManager))
            .child(createCustomNameField(syncManager));
        return row;
    }

    @Override
    protected IWidget createModeSwitchButton(PanelSyncManager syncManager) {
        IntSyncValue machineModeSyncer = syncManager.findSyncHandler("machineMode", IntSyncValue.class);
        return new CycleButtonWidget().size(18, 18)
            .syncHandler("machineMode")
            .length(machineModeIcons.size())
            .overlay(new DynamicDrawable(() -> getMachineModeIcon(machineModeSyncer.getIntValue())))
            .tooltipBuilder(this::createModeSwitchTooltip)
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    @Override
    protected IWidget createPowerPanelButton(PanelSyncManager syncManager, ModularPanel parent) {
        IPanelHandler powerPanel = syncManager.syncedPanel(
            "assemblerMatrixPowerPanel",
            true,
            (panelSyncManager, panelHandler) -> createPatternPowerPanel(parent, syncManager));
        return new ButtonWidget<>().size(18, 18)
            .overlay(GTGuiTextures.TT_OVERLAY_BUTTON_POWER_PANEL)
            .onMousePressed(mouseButton -> {
                if (powerPanel.isPanelOpen()) {
                    powerPanel.closePanel();
                } else {
                    powerPanel.openPanel();
                }
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("Info_AssemblerMatrix_01")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    @Override
    protected Widget<? extends Widget<?>> makeLogoWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return new ParentWidget<>().size(18, 40)
            .marginTop(4)
            .child(createCirculationLogo())
            .child(createGtnlLogo());
    }

    private IWidget createCirculationLogo() {
        return new IDrawable.DrawableWidget(GTNLMui2Textures.PICTURE_CIRCULATION).size(18, 18)
            .pos(0, 0);
    }

    private IWidget createGtnlLogo() {
        return new IDrawable.DrawableWidget(GTNLMui2Textures.PICTURE_GTNL_LOGO).size(18, 18)
            .pos(0, 22);
    }

    private IWidget createShowPatternButton(PanelSyncManager syncManager) {
        BooleanSyncValue showPatternSyncer = syncManager.findSyncHandler(SHOW_PATTERN_SYNC_KEY, BooleanSyncValue.class);
        return new ButtonWidget<>().size(18, 18)
            .background(
                new DynamicDrawable(
                    () -> showPatternSyncer.getBoolValue() ? GTGuiTextures.BUTTON_STANDARD_PRESSED
                        : GTGuiTextures.BUTTON_STANDARD))
            .overlay(
                new DynamicDrawable(
                    () -> showPatternSyncer.getBoolValue() ? GTGuiTextures.OVERLAY_BUTTON_WHITELIST
                        : GTGuiTextures.OVERLAY_BUTTON_BLACKLIST))
            .syncHandler(
                new InteractionSyncHandler().setOnMousePressed(
                    mouseData -> showPatternSyncer.setBoolValue(!showPatternSyncer.getBoolValue(), true, true)))
            .tooltipDynamic(
                tooltip -> tooltip.addLine(
                    IKey.dynamic(
                        () -> StatCollector.translateToLocal(
                            "Info_ShowPattern_" + (showPatternSyncer.getBoolValue() ? "Enabled" : "Disabled")))))
            .tooltipAutoUpdate(true)
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private IWidget createCustomNameField(PanelSyncManager syncManager) {
        StringSyncValue customNameSyncer = syncManager.findSyncHandler(CUSTOM_NAME_SYNC_KEY, StringSyncValue.class);
        return new TextFieldWidget().value(customNameSyncer)
            .setTextAlignment(Alignment.Center)
            .setTextColor(Color.WHITE.main)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("Info_AssemblerMatrix_03")))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .size(126, 18);
    }

    private ModularPanel createPatternPowerPanel(ModularPanel parent, PanelSyncManager syncManager) {
        IntSyncValue patternMultiplySyncer = syncManager.findSyncHandler(PATTERN_MULTIPLY_SYNC_KEY, IntSyncValue.class);
        Dialog<?> panel = new Dialog<>("assemblerMatrixPowerPanel", null);
        panel.relative(parent)
            .leftRel(1)
            .topRel(0)
            .size(100, 80)
            .background(GTGuiTextures.BACKGROUND_POPUP_STANDARD);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true);

        panel.child(ButtonWidget.panelCloseButton());
        panel.child(
            IKey.str(EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("Info_AssemblerMatrix_01"))
                .asWidget()
                .pos(0, 2)
                .size(100, 18)
                .textAlign(Alignment.Center));
        panel.child(
            IKey.lang("Info_AssemblerMatrix_02")
                .asWidget()
                .pos(0, 24)
                .size(100, 18)
                .textAlign(Alignment.Center));
        panel.child(
            new TextFieldWidget().value(patternMultiplySyncer)
                .numbersInt(1, Integer.MAX_VALUE)
                .scrollValues(1, 4, 64, 256)
                .setTextAlignment(Alignment.Center)
                .setTextColor(Color.WHITE.main)
                .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
                .tooltipBuilder(
                    tooltip -> tooltip.addLine(
                        IKey.dynamic(
                            () -> StatCollector
                                .translateToLocalFormatted("GT5U.gui.text.rangedvalue", 1, Integer.MAX_VALUE))))
                .tooltipShowUpTimer(TOOLTIP_DELAY)
                .pos(15, 40)
                .size(70, 18));
        return panel;
    }

    private IWidget createPatternOutputsWidget(PanelSyncManager syncManager) {
        DynamicLinkedSyncHandler<?> outputsWidgetSyncer = syncManager
            .findSyncHandler(PATTERN_OUTPUTS_WIDGET_SYNC_KEY, DynamicLinkedSyncHandler.class);
        return new DynamicSyncedWidget<>().syncHandler(outputsWidgetSyncer)
            .initialChild(createPatternOutputsWidget(List.of()))
            .fullWidth();
    }

    private IWidget createPatternOutputsWidget(List<IAEItemStack> cachedOutputItems) {
        return new ListWidget<>().fullWidth()
            .crossAxisAlignment(Alignment.CrossAxis.START)
            .children(createPatternOutputRows(cachedOutputItems));
    }

    private List<IWidget> createPatternOutputRows(List<IAEItemStack> cachedOutputItems) {
        if (cachedOutputItems == null || cachedOutputItems.isEmpty()) {
            return List.of();
        }

        Reference2LongMap<IAEItemStack> nameToAmount = new Reference2LongOpenHashMap<>();
        for (IAEItemStack item : cachedOutputItems) {
            if (item == null || item.getStackSize() <= 0) continue;
            nameToAmount.merge(item, item.getStackSize(), Long::sum);
        }

        List<Reference2LongMap.Entry<IAEItemStack>> sortedMap = new ObjectArrayList<>(
            nameToAmount.reference2LongEntrySet());
        sortedMap.sort(
            ((Comparator<Reference2LongMap.Entry<IAEItemStack>> & Serializable) (left, right) -> Long
                .compare(right.getLongValue(), left.getLongValue())));

        return sortedMap.stream()
            .map(this::createPatternOutputRow)
            .toList();
    }

    private IWidget createPatternOutputRow(Reference2LongMap.Entry<IAEItemStack> entry) {
        ItemStack stack = entry.getKey()
            .getItemStack()
            .copy();
        long itemCount = entry.getLongValue();
        String itemName = stack.getDisplayName();
        String itemAmountString = EnumChatFormatting.WHITE + " x "
            + EnumChatFormatting.GOLD
            + GTUtility.formatShortenedLong(itemCount)
            + EnumChatFormatting.WHITE
            + GTUtility.appendRate(false, itemCount, true, multiblock.mMaxProgresstime);
        String lineText = EnumChatFormatting.AQUA + GTUtility.truncateText(itemName, 40 - itemAmountString.length())
            + itemAmountString;
        String lineTooltip = EnumChatFormatting.AQUA + itemName
            + "\n"
            + GTUtility.appendRate(false, itemCount, false, multiblock.mMaxProgresstime);

        return Flow.row()
            .height(10)
            .fullWidth()
            .crossAxisAlignment(Alignment.CrossAxis.CENTER)
            .child(
                new ItemDisplayWidget().item(stack)
                    .displayAmount(false)
                    .size(8, 8))
            .child(
                new TextWidget<>(IKey.str(lineText)).textAlign(Alignment.CenterLeft)
                    .size(170, 10)
                    .marginLeft(2)
                    .tooltipBuilder(tooltip -> tooltip.addLine(lineTooltip)));
    }

    @Override
    protected void createModeSwitchTooltip(RichTooltip tooltip) {
        super.createModeSwitchTooltip(tooltip);
        tooltip.addLine(IKey.dynamic(() -> GTUtility.getColoredSecondaryTooltip(multiblock.getMachineModeName())));
    }

    @Override
    protected boolean shouldDisplayVoidExcess() {
        return false;
    }

    @Override
    protected boolean shouldDisplayInputSeparation() {
        return false;
    }

    @Override
    protected boolean shouldDisplayBatchMode() {
        return false;
    }

    @Override
    protected boolean shouldDisplayRecipeLock() {
        return false;
    }

    private static boolean areAEItemStacksEqual(IAEItemStack left, IAEItemStack right) {
        if (left == right) return true;
        if (left == null || right == null) return false;
        return left.equals(right) && left.getStackSize() == right.getStackSize();
    }

    private static IAEItemStack copyAEItemStack(IAEItemStack stack) {
        return stack == null ? AEItemStack.create(new ItemStack(Blocks.fire)) : stack.copy();
    }
}
