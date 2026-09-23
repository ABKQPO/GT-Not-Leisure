package com.science.gtnl.mixins.late.tecTech;

import org.spongepowered.asm.mixin.Mixin;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment.MainAxis;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.science.gtnl.api.mixinHelper.IResearchStationMarker;
import com.science.gtnl.common.gui.modularui.PersistentFilterSlot;

import gregtech.common.gui.modularui.multiblock.MTEResearchStationGui;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import tectech.thing.metaTileEntity.multi.MTEResearchStation;

@Mixin(value = MTEResearchStationGui.class, remap = false)
public abstract class MixinMTEResearchStationGui extends MTEMultiBlockBaseGui<MTEResearchStation> {

    public MixinMTEResearchStationGui(MTEResearchStation multiblock) {
        super(multiblock);
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        IResearchStationMarker marker = (IResearchStationMarker) multiblock;
        return Flow.row()
            .mainAxisAlignment(MainAxis.END)
            .reverseLayout(true)
            .verticalCenter()
            .rightRel(0)
            .coverChildrenWidth()
            .fullHeight()
            .child(
                new PersistentFilterSlot().slot(new ModularSlot(marker.gtnl$getResearchMarkerInventoryHandler(), 0))
                    .size(18, 18)
                    .marginLeft(5));
    }
}
