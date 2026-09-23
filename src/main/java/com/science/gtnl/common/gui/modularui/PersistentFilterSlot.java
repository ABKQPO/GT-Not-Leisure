package com.science.gtnl.common.gui.modularui;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.util.FilterSlot;

public class PersistentFilterSlot extends FilterSlot {

    @Override
    public @NotNull IDrawable getCurrentBackground(WidgetThemeEntry<?> widgetTheme) {
        return IDrawable.of(GuiTextures.SLOT_ITEM, GTGuiTextures.OVERLAY_SLOT_FILTER);
    }
}
