package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.SuperVoidBus;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;

public class SuperVoidBusGui extends MTEHatchBaseGui<SuperVoidBus> {

    private static final int SLOT_COLUMNS = 10;
    private static final int SLOT_ROWS = 10;

    private final GTNLMui2ItemHandlerAdapter lockedInventoryHandler;

    public SuperVoidBusGui(SuperVoidBus hatch) {
        super(hatch);
        this.lockedInventoryHandler = new GTNLMui2ItemHandlerAdapter(hatch.lockedInventoryHandler);
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        return GTGuis.mteTemplatePanelBuilder(machine, guiData, syncManager, uiSettings)
            .setWidth(machine.getGUIWidth())
            .setHeight(machine.getGUIHeight())
            .doesAddGregTechLogo(false)
            .build()
            .child(createFilterScroll())
            .child(createLogo());
    }

    private IWidget createFilterScroll() {
        return new ListWidget<>().scrollDirection(new VerticalScrollData())
            .showScrollShadows(false)
            .size(18 * SLOT_COLUMNS + 4, 72)
            .pos(20, 9)
            .child(createFilterGrid());
    }

    private IWidget createFilterGrid() {
        return new Grid().coverChildren()
            .gridOfWidthHeight(
                SLOT_COLUMNS,
                SLOT_ROWS,
                ($x, $y, index) -> new PhantomItemSlot().slot(new ModularSlot(lockedInventoryHandler, index))
                    .background(GTGuiTextures.SLOT_ITEM_STANDARD)
                    .backgroundOverlay(GTGuiTextures.OVERLAY_SLOT_FILTER));
    }

    @Override
    protected IDrawable.DrawableWidget createLogo() {
        return new IDrawable.DrawableWidget(getLogoTexture()).size(SLOT_SIZE);
    }

    @Override
    protected UITexture getLogoTexture() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO;
    }
}
