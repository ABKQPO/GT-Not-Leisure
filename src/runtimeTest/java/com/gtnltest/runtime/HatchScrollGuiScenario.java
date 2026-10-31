package com.gtnltest.runtime;

import static com.gtnltest.runtime.RuntimeReport.require;

import java.util.function.IntSupplier;

import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Uses the real scroll area and slot widgets; physical mouse-wheel input is not simulated. */
@SideOnly(Side.CLIENT)
final class HatchScrollGuiScenario implements GuiInteractionScenario {

    private final ScreenshotCapture screenshot;
    private final IntSupplier frames;
    private int step, firstFrame, ticks;
    private ScrollWidget<?> viewport;

    HatchScrollGuiScenario(ScreenshotCapture screenshot, IntSupplier frames) {
        this.screenshot = screenshot;
        this.frames = frames;
    }

    @Override
    public boolean tick(ModularScreen screen, RuntimeReport.Result result) throws Exception {
        require(screen != null, "scrollable hatch GUI disappeared");
        ticks++;
        if (step == 0) {
            viewport = (ScrollWidget<?>) GuiTestSupport.find(screen.getMainPanel(), "gtnlDualInputSlots");
            require(viewport != null, "high-tier hatch is missing its scroll viewport");
            int rows = result.id.endsWith("_max.gui") ? 14 : 13;
            require(GuiTestSupport.count(viewport, ItemSlot.class) == rows * rows, "hatch lost item slots");
            require(GuiTestSupport.count(viewport, FluidSlot.class) == rows, "hatch lost fluid slots");
            require(
                screen.getMainPanel()
                    .getArea().y >= 15,
                "hatch title has no space above its panel");
            var area = viewport.getScrollArea();
            area.getScrollY()
                .scrollTo(area, Integer.MAX_VALUE);
            require(viewport.getScrollY() >= (rows - 12) * 18, "cannot scroll to the last slot row");
            firstFrame = frames.getAsInt();
            ticks = 0;
            step = 1;
        } else if (ticks >= 6 && frames.getAsInt() - firstFrame >= 4) {
            var grid = GuiTestSupport.find(viewport, "gtnlDualInputSlotGrid")
                .getArea();
            var area = viewport.getArea();
            require(
                grid.y + grid.height - viewport.getScrollY() <= area.y + area.height,
                "last slot row remains outside the viewport after scrolling");
            result.additionalScreenshots.add(screenshot.capture("last-slot-row"));
            result.checks.add("hatch.scroll.all-slots");
            result.checks.add("hatch.scroll.last-row");
            viewport.getScrollArea()
                .getScrollY()
                .scrollTo(viewport.getScrollArea(), 0);
            return true;
        }
        return false;
    }

    @Override
    public String progress() {
        return "scroll step=" + step + ", ticks=" + ticks;
    }
}
