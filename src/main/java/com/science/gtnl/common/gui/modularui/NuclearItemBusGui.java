package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.science.gtnl.common.machine.hatch.NuclearItemBus;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;
import gregtech.common.modularui2.widget.builder.ItemSlotGridBuilder;

public class NuclearItemBusGui extends MTEHatchBaseGui<NuclearItemBus> {

    public static final String ITEM_SLOT_GROUP = "gtnl_nuclear_item_bus_inv";
    private static final int COLOR_INPUT_BAR = 0xFF4D7BFF;
    private static final int COLOR_OUTPUT_BAR = 0xFFFF8A1E;

    public NuclearItemBusGui(NuclearItemBus hatch) {
        super(hatch);
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        Flow inputArea = Flow.row()
            .coverChildren()
            .childPadding(1)
            .child(bar(COLOR_INPUT_BAR))
            .child(itemGrid(syncManager, 0, 1, 1, true, true));

        Flow outputArea = Flow.row()
            .coverChildren()
            .childPadding(1)
            .child(bar(COLOR_OUTPUT_BAR))
            .child(itemGrid(syncManager, 1, 2, 1, false, true));

        Flow main = Flow.row()
            .coverChildren()
            .childPadding(2)
            .child(inputArea)
            .child(outputArea);

        return super.createContentSection(panel, syncManager).child(main.center());
    }

    private Grid itemGrid(PanelSyncManager syncManager, int indexOffset, int cols, int rows, boolean canPut,
        boolean canTake) {
        return new ItemSlotGridBuilder(machine.inventoryHandler, syncManager).indexOffset(indexOffset)
            .size(cols, rows)
            .accessibility(canPut, canTake)
            .slotGroupKey(ITEM_SLOT_GROUP)
            .itemSlotSupplier(() -> new ItemSlot().background(GTGuiTextures.SLOT_ITEM_STANDARD))
            .build();
    }

    private Widget<?> bar(int color) {
        return new IDrawable.DrawableWidget(new Rectangle().color(color)).size(2, SLOT_SIZE);
    }
}
