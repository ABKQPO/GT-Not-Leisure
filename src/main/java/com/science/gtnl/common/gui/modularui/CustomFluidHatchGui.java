package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;

import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;

public class CustomFluidHatchGui extends MTEHatchBaseGui<CustomFluidHatch> {

    public CustomFluidHatchGui(CustomFluidHatch hatch) {
        super(hatch);
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        Flow mainRow = Flow.row()
            .coverChildren()
            .childPadding(1)
            .crossAxisAlignment(Alignment.CrossAxis.START);

        mainRow.child(createScreen(panel, syncManager, machine.getFluidTank()));
        mainRow.child(createIO(panel, syncManager, machine.getInputSlot(), machine.getOutputSlot()));
        mainRow.childIf(supportsFluidFilterScreen(), () -> createFilterScreen(panel, syncManager));

        return super.createContentSection(panel, syncManager).child(mainRow);
    }

    @Override
    protected UITexture getLogoTexture() {
        return machine.usesSteamLogoForMui2() ? GTNLMui2Textures.PICTURE_GTNL_STEAM_LOGO
            : GTNLMui2Textures.PICTURE_GTNL_LOGO;
    }
}
