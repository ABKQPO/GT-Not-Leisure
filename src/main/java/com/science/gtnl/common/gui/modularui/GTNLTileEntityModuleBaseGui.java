package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.science.gtnl.common.gui.GTNLMui2Textures;

import gregtech.common.gui.modularui.multiblock.base.TileEntityModuleBaseGui;
import gtnhintergalactic.tile.multi.elevatormodules.TileEntityModuleBase;

public class GTNLTileEntityModuleBaseGui<T extends TileEntityModuleBase> extends TileEntityModuleBaseGui<T> {

    public GTNLTileEntityModuleBaseGui(T multiblock) {
        super(multiblock);
    }

    @Override
    protected Widget<? extends Widget<?>> makeLogoWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return new IDrawable.DrawableWidget(GTNLMui2Textures.PICTURE_GTNL_LOGO).size(18)
            .marginTop(4);
    }
}
