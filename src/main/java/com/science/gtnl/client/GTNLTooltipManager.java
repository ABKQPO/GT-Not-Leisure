package com.science.gtnl.client;

import static codechicken.nei.NEIClientConfig.isEnabled;
import static codechicken.nei.NEIClientConfig.isHidden;
import static com.gtnewhorizons.modularui.api.KeyboardUtil.isAltKeyDown;

import java.util.Map;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;

import codechicken.nei.BookmarkPanel;
import codechicken.nei.LayoutManager;
import codechicken.nei.Widget;
import codechicken.nei.bookmark.BookmarksGridSlot;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerTooltipHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GTNLTooltipManager implements IContainerTooltipHandler {

    @Override
    public Map<String, String> handleHotkeys(GuiContainer gui, int mousex, int mousey, Map<String, String> hotkeys) {
        if (!isHidden() && isEnabled() && GuiContainerManager.shouldShowTooltip(gui) && isAltKeyDown()) {
            final Widget focused = LayoutManager.instance()
                .getWidgetUnderMouse(mousex, mousey);

            if (focused instanceof BookmarkPanel tip) {
                final var grid = tip.getGrid();
                final int overRowIndex = grid.getHoveredRowIndex(true);
                final BookmarksGridSlot slot = tip.getSlotMouseOver(mousex, mousey);

                if (grid.getPerPage() == 0 || grid.isEmpty() || slot == null || slot.getItemStack() == null) {
                    return hotkeys;
                }

                hotkeys.put(
                    StatCollector.translateToLocal("nei.key.ctrl") + " + "
                        + StatCollector.translateToLocal("nei.mouse.middle"),
                    StatCollector.translateToLocal("nei.bookmark.ae_retrieve_item"));
                hotkeys.put(
                    StatCollector.translateToLocal("nei.key.alt") + " + "
                        + StatCollector.translateToLocal("nei.mouse.middle"),
                    StatCollector.translateToLocal("nei.bookmark.ae_start_craft"));
            }
        }

        return hotkeys;
    }

}
