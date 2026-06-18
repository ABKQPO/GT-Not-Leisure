package com.science.gtnl.common.gui.modularui;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.EmptyWidget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.HumongousDualInputHatch;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;
import gregtech.common.gui.modularui.synchandler.NBTSerializableSyncHandler;
import gregtech.common.gui.modularui.util.AEItemSlot;
import gregtech.common.inventory.AEInventory;

public class HumongousDualInputHatchGui extends MTEHatchBaseGui<HumongousDualInputHatch> {

    public static final String ITEM_SLOT_GROUP = "humongous_item_inv";
    public static final String ITEM_INVENTORY_SYNC_KEY = "humongous_inventory";
    public static final String REFUND_ACTION_KEY = "refund_all";

    public HumongousDualInputHatchGui(HumongousDualInputHatch hatch) {
        super(hatch);
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        ModularPanel panel = GTGuis.mteTemplatePanelBuilder(machine, guiData, syncManager, uiSettings)
            .setWidth(machine.getGUIWidth())
            .setHeight(machine.getGUIHeight())
            .doesAddGregTechLogo(false)
            .build();

        SlotLayout layout = SlotLayout.of(machine);
        addItemSlots(panel, syncManager, layout);
        addFluidSlots(panel, layout);
        registerRefundAction(syncManager);
        panel.child(createRefundButton(syncManager))
            .child(createLogo());
        return panel;
    }

    public void addItemSlots(ModularPanel panel, PanelSyncManager syncManager, SlotLayout layout) {
        AEInventory inv = machine.getAEInventory();
        syncManager.registerSlotGroup(ITEM_SLOT_GROUP, layout.itemColumns);
        syncManager.syncValue(ITEM_INVENTORY_SYNC_KEY, new NBTSerializableSyncHandler<>(machine::getAEInventory));

        panel.child(
            new Grid().coverChildren()
                .gridOfWidthHeight(
                    layout.itemColumns,
                    layout.itemRows,
                    ($x, $y, index) -> index < machine.getItemStorageSlotCount()
                        ? new AEItemSlot(syncManager, ITEM_SLOT_GROUP, inv, index).setDumpable(true)
                            .background(GTGuiTextures.SLOT_ITEM_STANDARD)
                        : new EmptyWidget())
                .pos(layout.centerX + 5, layout.centerY));
    }

    public void addFluidSlots(ModularPanel panel, SlotLayout layout) {
        for (int i = 0; i < machine.getFluidTanksForGui().length; i++) {
            panel.child(
                new FluidSlot().syncHandler(new FluidSlotSyncHandler(machine.getFluidTanksForGui()[i]))
                    .background(GTGuiTextures.SLOT_FLUID_TANK)
                    .pos(layout.centerX + 18 * layout.itemColumns + 5, layout.centerY + i * 18));
        }
    }

    public void registerRefundAction(PanelSyncManager syncManager) {
        syncManager.registerSyncedAction(REFUND_ACTION_KEY, packet -> {
            if (!syncManager.isClient()) {
                machine.refundAll();
            }
        });
    }

    public ButtonWidget<?> createRefundButton(PanelSyncManager syncManager) {
        return new ButtonWidget<>().background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_EXPORT)
            .addTooltipLine(StatCollector.translateToLocal("Button_Tooltip_HumongousDualInputHatch_00"))
            .onMousePressed(mouseButton -> {
                syncManager.callSyncedAction(REFUND_ACTION_KEY, buffer -> {});
                return true;
            })
            .size(16, 16)
            .pos(170 + 4 * (machine.mTier - 1) + machine.mTier / 2, 102 + 14 * (machine.mTier - 1));
    }

    @Override
    protected IDrawable.DrawableWidget createLogo() {
        return new IDrawable.DrawableWidget(getLogoTexture()).size(SLOT_SIZE);
    }

    @Override
    protected UITexture getLogoTexture() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO;
    }

    public record SlotLayout(int itemColumns, int itemRows, int centerX, int centerY) {

        public static SlotLayout of(HumongousDualInputHatch hatch) {
            int itemColumns = Math.max(1, hatch.mTier);
            int itemRows = Math.max(1, hatch.mTier);
            int totalWidth = 9 * itemColumns + 36;
            int totalHeight = 5 * itemRows + 81;
            return new SlotLayout(itemColumns, itemRows, (176 - totalWidth) / 2, (166 - totalHeight) / 2);
        }
    }
}
