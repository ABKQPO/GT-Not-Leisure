package com.science.gtnl.client.topology;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Floats directly on the star map, so it carries no panel plate: only a flat pill behind the single button and light
 * text that reads against the dark canvas.
 */
@SideOnly(Side.CLIENT)
public class TopologyTopBarWidget extends Widget<TopologyTopBarWidget> implements Interactable {

    public static final int HEIGHT = 16;
    private static final int BUTTON_PADDING = 14;
    private static final int TITLE_GAP = 10;
    private static final int COUNT_GAP = 8;
    private static final float CORNER = 3.0F;

    private final TopologyViewState state;
    private final TopologyDraw draw = new TopologyDraw();
    private final Runnable rescan;

    public TopologyTopBarWidget(TopologyViewState state, Runnable rescan) {
        this.state = state;
        this.rescan = rescan;
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        int width = getArea().width;
        int buttonWidth = buttonWidth(width);
        boolean hovered = isOverButton();
        draw.begin();
        TopologyChrome.pill(draw, width - buttonWidth, 0, buttonWidth, HEIGHT, CORNER, hovered, false);
        draw.end();

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        try {
            int baseline = TopologyTheme.captionTop(0, HEIGHT);
            String title = TopologyViewState.translate("title");
            TopologyTheme.label(title, 0, baseline, TopologyPalette.opaque(TopologyPalette.TEXT), false);
            String label = TopologyViewState.translate("rescan");
            if (buttonWidth >= font.getStringWidth(label) + 4) TopologyTheme.centeredLabel(
                label,
                width - buttonWidth / 2,
                baseline,
                TopologyPalette.opaque(hovered ? TopologyPalette.TEXT : TopologyPalette.TEXT_MUTED),
                false);

            // Counts first, then the shortcut hint, and only while each still fits beside the title.
            int titleEnd = font.getStringWidth(title) + TITLE_GAP;
            int cursor = width - buttonWidth - COUNT_GAP;
            String counts = TopologyViewState.translate(
                "counts",
                state.filteredNetworks()
                    .size(),
                state.snapshot()
                    .networks()
                    .size(),
                state.filteredBridges()
                    .size());
            if (cursor - font.getStringWidth(counts) > titleEnd) {
                cursor -= font.getStringWidth(counts);
                TopologyTheme
                    .label(counts, cursor, baseline, TopologyPalette.opaque(TopologyPalette.TEXT_MUTED), false);
                cursor -= COUNT_GAP * 2;
            }
            if (state.compact()) return;
            String shortcuts = TopologyViewState.translate("shortcuts");
            if (cursor - font.getStringWidth(shortcuts) > titleEnd) TopologyTheme.label(
                shortcuts,
                cursor - font.getStringWidth(shortcuts),
                baseline,
                TopologyPalette.opaque(TopologyPalette.TEXT_DIM),
                false);
        } finally {
            GL11.glPopAttrib();
        }
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        if (!isOverButton()) return Result.IGNORE;
        rescan.run();
        Interactable.playButtonClickSound();
        return Result.SUCCESS;
    }

    private int buttonWidth(int width) {
        int natural = Minecraft.getMinecraft().fontRenderer.getStringWidth(TopologyViewState.translate("rescan"))
            + BUTTON_PADDING;
        return Math.min(natural, Math.max(0, width - TITLE_GAP));
    }

    private boolean isOverButton() {
        if (!isBelowMouse()) return false;
        int mouseX = getContext().getAbsMouseX() - getArea().x;
        return mouseX >= getArea().width - buttonWidth(getArea().width);
    }
}
