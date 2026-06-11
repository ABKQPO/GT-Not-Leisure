package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.api.IPacketWriter;
import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.DynamicSyncHandler;
import com.cleanroommc.modularui.value.sync.GenericListSyncHandler;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.EmptyWidget;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.FluidDisplayWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.machine.multiblock.EyeOfHarmonyInjector;

import appeng.api.util.DimensionalCoord;
import appeng.client.render.highlighter.BlockPosHighlighter;
import appeng.core.localization.PlayerMessages;
import gregtech.api.modularui2.GTGuiTextures;

public class EyeOfHarmonyInjectorStatusPanel {

    private static final String LINKED_UNITS_SYNC_KEY = "eyeOfHarmonyInjectorLinkedUnits";
    private static final String LINKED_UNITS_WIDGET_SYNC_KEY = "eyeOfHarmonyInjectorLinkedUnitsWidget";
    public static final String STATUS_PANEL_KEY = "eyeOfHarmonyInjectorStatusPanel";
    private static final int PANEL_WIDTH = 235;
    private static final int PANEL_HEIGHT = 220;
    private static final int ROW_HEIGHT = 140;
    private static final int BUTTON_SIZE = 18;

    private final EyeOfHarmonyInjector multiblock;
    private IPanelHandler statusPanelHandler;
    private DynamicSyncedWidget<?> linkedUnitsWidget;
    private int linkedUnitsScrollY;

    public EyeOfHarmonyInjectorStatusPanel(EyeOfHarmonyInjector multiblock) {
        this.multiblock = multiblock;
    }

    public void registerSyncValues(PanelSyncManager syncManager) {
        GenericListSyncHandler<EyeOfHarmonyInjector.LinkedUnitGuiData> linkedUnitsSyncer = GenericListSyncHandler.<EyeOfHarmonyInjector.LinkedUnitGuiData>builder()
            .getter(multiblock::getLinkedUnitGuiData)
            .setter(multiblock::updateLinkedUnitGuiData)
            .serializer(EyeOfHarmonyInjectorStatusPanel::writeLinkedUnit)
            .deserializer(EyeOfHarmonyInjectorStatusPanel::readLinkedUnit)
            .equals(EyeOfHarmonyInjectorStatusPanel::areSameLinkedUnits)
            .build()
            .allowC2S();
        syncManager.syncValue(LINKED_UNITS_SYNC_KEY, linkedUnitsSyncer);
        DynamicSyncHandler linkedUnitsWidgetSyncer = new DynamicSyncHandler() {

            @Override
            public void notifyUpdate(IPacketWriter packetWriter) {
                saveLinkedUnitsScroll();
                super.notifyUpdate(packetWriter);
            }
        }.widgetProvider((panelSyncManager, packet) -> {
            if (packet == null) {
                return new EmptyWidget();
            }
            return createLinkedUnitsList(panelSyncManager, linkedUnitsSyncer);
        });
        linkedUnitsSyncer.setChangeListener(() -> linkedUnitsWidgetSyncer.notifyUpdate(packet -> {}));
        syncManager.syncValue(LINKED_UNITS_WIDGET_SYNC_KEY, linkedUnitsWidgetSyncer);
    }

    public void registerPanel(ModularPanel parent, PanelSyncManager syncManager, Map<String, IPanelHandler> panelMap) {
        panelMap.put(
            STATUS_PANEL_KEY,
            syncManager.syncedPanel(
                STATUS_PANEL_KEY,
                true,
                (panelSyncManager, panelHandler) -> createStatusPanel(parent, panelSyncManager, panelHandler)));
    }

    public IWidget createStatusPanelButton(IPanelHandler statusPanel) {
        statusPanelHandler = statusPanel;
        return new ButtonWidget<>().size(16, 16)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT)
            .onMousePressed(unused -> {
                if (statusPanel == null) {
                    return false;
                }
                if (statusPanel.isPanelOpen()) {
                    statusPanel.closePanel();
                } else {
                    statusPanel.openPanel();
                }
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("Info_EyeOfHarmonyInjector_03")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ModularPanel createStatusPanel(ModularPanel parent, PanelSyncManager syncManager,
        IPanelHandler panelHandler) {
        statusPanelHandler = panelHandler;
        Dialog<?> panel = new Dialog<>(STATUS_PANEL_KEY, null);
        panel.relative(parent)
            .leftRel(1)
            .topRel(0)
            .size(PANEL_WIDTH, PANEL_HEIGHT)
            .background(GTGuiTextures.BACKGROUND_POPUP_STANDARD);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true);
        panel.child(ButtonWidget.panelCloseButton());
        panel.child(
            IKey.lang("Info_EyeOfHarmonyInjector_Title")
                .asWidget()
                .textAlign(Alignment.Center)
                .pos(5, 10)
                .size(PANEL_WIDTH - 10, 8));
        panel.child(createDynamicLinkedUnitsWidget(syncManager, panelHandler));
        return panel;
    }

    private IWidget createDynamicLinkedUnitsWidget(PanelSyncManager syncManager, IPanelHandler panelHandler) {
        statusPanelHandler = panelHandler;
        DynamicSyncHandler linkedUnitsWidgetSyncer = syncManager
            .findSyncHandler(LINKED_UNITS_WIDGET_SYNC_KEY, DynamicSyncHandler.class);
        linkedUnitsWidget = new DynamicSyncedWidget<>().syncHandler(linkedUnitsWidgetSyncer)
            .initialChild(createLinkedUnitsList(syncManager, getLinkedUnitsSyncer(syncManager), panelHandler))
            .pos(5, 20)
            .size(PANEL_WIDTH - 10, PANEL_HEIGHT - 25);
        return linkedUnitsWidget;
    }

    private IWidget createLinkedUnitsList(PanelSyncManager syncManager,
        GenericListSyncHandler<EyeOfHarmonyInjector.LinkedUnitGuiData> linkedUnitsSyncer) {
        return createLinkedUnitsList(syncManager, linkedUnitsSyncer, statusPanelHandler);
    }

    private IWidget createLinkedUnitsList(PanelSyncManager syncManager,
        GenericListSyncHandler<EyeOfHarmonyInjector.LinkedUnitGuiData> linkedUnitsSyncer, IPanelHandler panelHandler) {
        List<EyeOfHarmonyInjector.LinkedUnitGuiData> units = linkedUnitsSyncer.getValue()
            .stream()
            .filter(unit -> unit != null)
            .collect(Collectors.toList());
        if (units.isEmpty()) {
            return IKey.lang("Info_EyeOfHarmonyInjector_05")
                .asWidget()
                .textAlign(Alignment.Center)
                .size(PANEL_WIDTH - 10, PANEL_HEIGHT - 25);
        }

        ListWidget<IWidget, ?> list = new GTNLListWidget<>(linkedUnitsScrollY).size(PANEL_WIDTH - 10, PANEL_HEIGHT - 25)
            .scrollDirection(new VerticalScrollData())
            .crossAxisAlignment(Alignment.CrossAxis.START);
        linkedUnitsScrollY = 0;
        for (EyeOfHarmonyInjector.LinkedUnitGuiData unit : units) {
            list.child(createLinkedUnitRow(unit, linkedUnitsSyncer, syncManager, panelHandler));
        }
        return list;
    }

    private IWidget createLinkedUnitRow(EyeOfHarmonyInjector.LinkedUnitGuiData unit,
        GenericListSyncHandler<EyeOfHarmonyInjector.LinkedUnitGuiData> linkedUnitsSyncer, PanelSyncManager syncManager,
        IPanelHandler panelHandler) {
        return new ParentWidget<>().size(215, ROW_HEIGHT)
            .child(createLocateButton(unit, syncManager, panelHandler))
            .child(createStoredFluidDisplay(unit))
            .child(
                IKey.dynamic(() -> getLinkedUnitDisplayText(unit))
                    .asWidget()
                    .textAlign(Alignment.CenterLeft)
                    .pos(75, 5)
                    .size(140, 10))
            .child(createAmountLabel("Tooltip_EyeOfHarmonyInjector_HeliumParametrization", unit, AmountType.HELIUM, 18))
            .child(createAmountField(unit, linkedUnitsSyncer, syncManager, AmountType.HELIUM, 36))
            .child(
                createAmountLabel(
                    "Tooltip_EyeOfHarmonyInjector_HydrogenParametrization",
                    unit,
                    AmountType.HYDROGEN,
                    54))
            .child(createAmountField(unit, linkedUnitsSyncer, syncManager, AmountType.HYDROGEN, 72))
            .child(
                createAmountLabel(
                    "Tooltip_EyeOfHarmonyInjector_RawStarMatterParametrization",
                    unit,
                    AmountType.RAW_STAR_MATTER,
                    90))
            .child(createAmountField(unit, linkedUnitsSyncer, syncManager, AmountType.RAW_STAR_MATTER, 108));
    }

    private IWidget createLocateButton(EyeOfHarmonyInjector.LinkedUnitGuiData unit, PanelSyncManager syncManager,
        IPanelHandler panelHandler) {
        return new ButtonWidget<>().size(BUTTON_SIZE, BUTTON_SIZE)
            .background(GTGuiTextures.BUTTON_STANDARD, new DynamicDrawable(() -> createDisplayStackDrawable(unit)))
            .onMousePressed(unused -> {
                BlockPosHighlighter.highlightBlocks(
                    syncManager.getPlayer(),
                    Collections.singletonList(new DimensionalCoord(unit.x, unit.y, unit.z, unit.dimensionId)),
                    getLinkedUnitDisplayName(unit),
                    PlayerMessages.MachineHighlighted.getUnlocalized(),
                    PlayerMessages.MachineInOtherDim.getUnlocalized());
                if (panelHandler != null) {
                    panelHandler.closePanel();
                }
                return true;
            })
            .tooltipBuilder(
                tooltip -> tooltip.addLine(IKey.lang("Info_EyeOfHarmonyInjector_00"))
                    .addLine(IKey.lang("Info_EyeOfHarmonyInjector_01"))
                    .addLine(IKey.str("Dim: %s", unit.dimensionId))
                    .addLine(IKey.str("X: %s, Y: %s, Z: %s", unit.x, unit.y, unit.z))
                    .addLine(IKey.lang("Info_EyeOfHarmonyInjector_02")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private String getLinkedUnitDisplayText(EyeOfHarmonyInjector.LinkedUnitGuiData unit) {
        return getLinkedUnitDisplayName(unit) + " - " + getLinkedUnitStatusText(unit);
    }

    private String getLinkedUnitDisplayName(EyeOfHarmonyInjector.LinkedUnitGuiData unit) {
        return unit.displayName == null || unit.displayName.isEmpty()
            ? StatCollector.translateToLocal("Info_EyeOfHarmonyInjector_FallbackName")
            : unit.displayName;
    }

    private String getLinkedUnitStatusText(EyeOfHarmonyInjector.LinkedUnitGuiData unit) {
        return unit.statusText == null ? "" : unit.statusText;
    }

    private ItemDrawable createDisplayStackDrawable(EyeOfHarmonyInjector.LinkedUnitGuiData unit) {
        return unit.displayStack == null ? new ItemDrawable(new ItemStack(Blocks.beacon))
            : new ItemDrawable(unit.displayStack);
    }

    private IWidget createStoredFluidDisplay(EyeOfHarmonyInjector.LinkedUnitGuiData unit) {
        return Flow.row()
            .pos(18, 0)
            .size(54, 18)
            .child(createStoredFluidWidget(EyeOfHarmonyInjector.heliumStack, unit.heliumAmount))
            .child(createStoredFluidWidget(EyeOfHarmonyInjector.hydrogenStack, unit.hydrogenAmount))
            .child(createStoredFluidWidget(EyeOfHarmonyInjector.rawStarMatterStack, unit.rawStarMatterAmount));
    }

    private IWidget createStoredFluidWidget(FluidStack stack, long amount) {
        FluidStack displayStack = stack.copy();
        return new FluidDisplayWidget().value(displayStack)
            .displayAmount(false)
            .disableThemeBackground(true)
            .disableHoverThemeBackground(true)
            .background(GTGuiTextures.SLOT_FLUID_DARK)
            .size(18, 18)
            .fluidTooltip((tooltip, fluid) -> buildStoredFluidTooltip(tooltip, fluid, amount));
    }

    private void buildStoredFluidTooltip(RichTooltip tooltip, FluidStack fluid, long amount) {
        if (fluid == null) {
            tooltip.addLine(IKey.lang("modularui2.fluid.empty"));
            return;
        }
        tooltip.addFromFluid(fluid);
        tooltip.addLine(IKey.lang("modularui2.fluid.phantom.amount", NumberFormatUtil.formatNumber(amount), "L"));
        tooltip.addAdditionalInfoFromFluid(fluid);
        if (!Interactable.hasShiftDown()) {
            tooltip.addLine(IKey.lang("modularui2.tooltip.shift"));
        }
    }

    private IWidget createAmountLabel(String langKey, EyeOfHarmonyInjector.LinkedUnitGuiData unit,
        AmountType amountType, int y) {
        return IKey
            .dynamic(
                () -> amountType.hasManualOverride(unit) ? StatCollector.translateToLocal(langKey)
                    : StatCollector.translateToLocal(langKey) + " - "
                        + StatCollector.translateToLocal("Info_EyeOfHarmonyInjector_04"))
            .asWidget()
            .textAlign(Alignment.CenterLeft)
            .pos(15, y)
            .size(200, 18);
    }

    private IWidget createAmountField(EyeOfHarmonyInjector.LinkedUnitGuiData unit,
        GenericListSyncHandler<EyeOfHarmonyInjector.LinkedUnitGuiData> linkedUnitsSyncer, PanelSyncManager syncManager,
        AmountType amountType, int y) {
        LongSyncValue amountSyncer = syncManager.getOrCreateSyncHandler(
            amountType.syncKey(unit),
            LongSyncValue.class,
            () -> new LongSyncValue(() -> amountType.getDisplayValue(unit), value -> {
                amountType.setOverride(unit, value);
                linkedUnitsSyncer.modifyValue(links -> updateSyncedUnit(links, unit, amountType, value));
                refreshLinkedUnitsWidget(syncManager);
            }).allowC2S());
        return new TextFieldWidget().value(amountSyncer)
            .setNumbersLong(value -> Math.min(Long.MAX_VALUE, Math.max(-1, value)))
            .setFormatAsInteger(true)
            .setScrollValues(1, 10000, 1000000)
            .setTextColor(Color.WHITE.main)
            .setTextAlignment(Alignment.Center)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
            .pos(15, y)
            .size(200, 18);
    }

    private void updateSyncedUnit(List<EyeOfHarmonyInjector.LinkedUnitGuiData> links,
        EyeOfHarmonyInjector.LinkedUnitGuiData target, AmountType amountType, long value) {
        for (EyeOfHarmonyInjector.LinkedUnitGuiData link : links) {
            if (link.dimensionId == target.dimensionId && link.x == target.x
                && link.y == target.y
                && link.z == target.z) {
                amountType.setOverride(link, value);
                return;
            }
        }
    }

    private void refreshLinkedUnitsWidget(PanelSyncManager syncManager) {
        DynamicSyncHandler linkedUnitsWidgetSyncer = syncManager
            .findSyncHandler(LINKED_UNITS_WIDGET_SYNC_KEY, DynamicSyncHandler.class);
        if (linkedUnitsWidgetSyncer != null) {
            linkedUnitsWidgetSyncer.notifyUpdate(packet -> {});
        }
    }

    @SuppressWarnings("unchecked")
    private GenericListSyncHandler<EyeOfHarmonyInjector.LinkedUnitGuiData> getLinkedUnitsSyncer(
        PanelSyncManager syncManager) {
        return syncManager.findSyncHandler(LINKED_UNITS_SYNC_KEY, GenericListSyncHandler.class);
    }

    private void saveLinkedUnitsScroll() {
        if (linkedUnitsWidget == null || !linkedUnitsWidget.hasChildren()) {
            return;
        }
        IWidget widget = linkedUnitsWidget.getChildren()
            .get(0);
        if (widget instanceof GTNLListWidget<?, ?>list && list.getScrollData()
            .getScrollSize() != 0) {
            linkedUnitsScrollY = list.getScrollY();
        }
    }

    private static void writeLinkedUnit(PacketBuffer buffer, EyeOfHarmonyInjector.LinkedUnitGuiData unit) {
        try {
            buffer.writeInt(unit.dimensionId);
            buffer.writeInt(unit.x);
            buffer.writeInt(unit.y);
            buffer.writeInt(unit.z);
            buffer.writeLong(unit.maxHeliumAmount);
            buffer.writeLong(unit.maxHydrogenAmount);
            buffer.writeLong(unit.maxRawStarMatterAmount);
            buffer.writeLong(unit.heliumAmount);
            buffer.writeLong(unit.hydrogenAmount);
            buffer.writeLong(unit.rawStarMatterAmount);
            buffer.writeLong(unit.displayHeliumMax);
            buffer.writeLong(unit.displayHydrogenMax);
            buffer.writeLong(unit.displayRawStarMatterMax);
            buffer.writeStringToBuffer(unit.displayName == null ? "" : unit.displayName);
            buffer.writeStringToBuffer(unit.statusText == null ? "" : unit.statusText);
            buffer.writeBoolean(unit.displayStack != null);
            if (unit.displayStack != null) {
                buffer.writeItemStackToBuffer(unit.displayStack);
            }
        } catch (IOException exception) {
            ScienceNotLeisure.LOG.error("Failed to sync EyeOfHarmonyInjector linked unit", exception);
        }
    }

    private static EyeOfHarmonyInjector.LinkedUnitGuiData readLinkedUnit(PacketBuffer buffer) {
        try {
            EyeOfHarmonyInjector.LinkedUnitGuiData unit = new EyeOfHarmonyInjector.LinkedUnitGuiData();
            unit.dimensionId = buffer.readInt();
            unit.x = buffer.readInt();
            unit.y = buffer.readInt();
            unit.z = buffer.readInt();
            unit.maxHeliumAmount = buffer.readLong();
            unit.maxHydrogenAmount = buffer.readLong();
            unit.maxRawStarMatterAmount = buffer.readLong();
            unit.heliumAmount = buffer.readLong();
            unit.hydrogenAmount = buffer.readLong();
            unit.rawStarMatterAmount = buffer.readLong();
            unit.displayHeliumMax = buffer.readLong();
            unit.displayHydrogenMax = buffer.readLong();
            unit.displayRawStarMatterMax = buffer.readLong();
            unit.displayName = buffer.readStringFromBuffer(32767);
            unit.statusText = buffer.readStringFromBuffer(32767);
            unit.displayStack = buffer.readBoolean() ? buffer.readItemStackFromBuffer() : null;
            return unit;
        } catch (IOException exception) {
            ScienceNotLeisure.LOG.error("Failed to read EyeOfHarmonyInjector linked unit", exception);
            return null;
        }
    }

    private static boolean areSameLinkedUnits(EyeOfHarmonyInjector.LinkedUnitGuiData left,
        EyeOfHarmonyInjector.LinkedUnitGuiData right) {
        if (left == null || right == null) {
            return left == right;
        }
        return left.dimensionId == right.dimensionId && left.x == right.x
            && left.y == right.y
            && left.z == right.z
            && left.maxHeliumAmount == right.maxHeliumAmount
            && left.maxHydrogenAmount == right.maxHydrogenAmount
            && left.maxRawStarMatterAmount == right.maxRawStarMatterAmount
            && left.heliumAmount == right.heliumAmount
            && left.hydrogenAmount == right.hydrogenAmount
            && left.rawStarMatterAmount == right.rawStarMatterAmount
            && left.displayHeliumMax == right.displayHeliumMax
            && left.displayHydrogenMax == right.displayHydrogenMax
            && left.displayRawStarMatterMax == right.displayRawStarMatterMax
            && ItemStack.areItemStacksEqual(left.displayStack, right.displayStack)
            && Objects.equals(left.displayName, right.displayName)
            && Objects.equals(left.statusText, right.statusText);
    }

    private enum AmountType {

        HELIUM("heliumMax", unit -> unit.maxHeliumAmount != -1 ? unit.maxHeliumAmount : unit.displayHeliumMax,
            unit -> unit.maxHeliumAmount, (unit, value) -> unit.maxHeliumAmount = value),
        HYDROGEN("hydrogenMax", unit -> unit.maxHydrogenAmount != -1 ? unit.maxHydrogenAmount : unit.displayHydrogenMax,
            unit -> unit.maxHydrogenAmount, (unit, value) -> unit.maxHydrogenAmount = value),
        RAW_STAR_MATTER("rawStarMatterMax",
            unit -> unit.maxRawStarMatterAmount != -1 ? unit.maxRawStarMatterAmount : unit.displayRawStarMatterMax,
            unit -> unit.maxRawStarMatterAmount, (unit, value) -> unit.maxRawStarMatterAmount = value);

        private final String syncName;
        private final ToLongFunction<EyeOfHarmonyInjector.LinkedUnitGuiData> getter;
        private final ToLongFunction<EyeOfHarmonyInjector.LinkedUnitGuiData> overrideGetter;
        private final UnitLongSetter setter;

        AmountType(String syncName, ToLongFunction<EyeOfHarmonyInjector.LinkedUnitGuiData> getter,
            ToLongFunction<EyeOfHarmonyInjector.LinkedUnitGuiData> overrideGetter, UnitLongSetter setter) {
            this.syncName = syncName;
            this.getter = getter;
            this.overrideGetter = overrideGetter;
            this.setter = setter;
        }

        private String syncKey(EyeOfHarmonyInjector.LinkedUnitGuiData unit) {
            return syncName + "_" + unit.dimensionId + "_" + unit.x + "_" + unit.y + "_" + unit.z;
        }

        private long getDisplayValue(EyeOfHarmonyInjector.LinkedUnitGuiData unit) {
            return getter.applyAsLong(unit);
        }

        private boolean hasManualOverride(EyeOfHarmonyInjector.LinkedUnitGuiData unit) {
            return overrideGetter.applyAsLong(unit) != -1;
        }

        private void setOverride(EyeOfHarmonyInjector.LinkedUnitGuiData unit, long value) {
            setter.accept(unit, value);
        }
    }

    private interface UnitLongSetter {

        void accept(EyeOfHarmonyInjector.LinkedUnitGuiData unit, long value);
    }
}
