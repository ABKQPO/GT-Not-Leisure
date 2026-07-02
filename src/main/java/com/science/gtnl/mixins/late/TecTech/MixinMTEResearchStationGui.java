package com.science.gtnl.mixins.late.TecTech;

import org.spongepowered.asm.mixin.Mixin;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;
import com.science.gtnl.api.mixinHelper.IResearchStationMarker;

import gregtech.common.gui.modularui.multiblock.MTEResearchStationGui;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import tectech.thing.metaTileEntity.multi.MTEResearchStation;

@Mixin(value = MTEResearchStationGui.class, remap = false)
public abstract class MixinMTEResearchStationGui extends MTEMultiBlockBaseGui<MTEResearchStation> {

    public MixinMTEResearchStationGui(MTEResearchStation multiblock) {
        super(multiblock);
    }

    @Override
    protected IWidget createPowerPanelButton(PanelSyncManager syncManager, ModularPanel parent) {
        IResearchStationMarker marker = (IResearchStationMarker) multiblock;
        return new PhantomItemSlot().slot(new ModularSlot(marker.gtnl$getResearchMarkerInventoryHandler(), 0))
            .size(18, 18)
            .marginLeft(5);
    }
}
