package com.science.gtnl.client.topology;

import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A bare themed inset that backs a widget which cannot paint one itself, such as the search field.
 * <p>
 * It paints in {@code draw} rather than through the theme background, because ModularUI runs every sibling's
 * background pass before any sibling's content: a themed background here would end up underneath the full screen
 * canvas. It is deliberately not interactive, so clicks reach whatever sits behind it.
 */
@SideOnly(Side.CLIENT)
public class TopologySurfaceWidget extends Widget<TopologySurfaceWidget> {

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        TopologyTheme.drawInset(context, 0, 0, getArea().width, getArea().height);
    }
}
