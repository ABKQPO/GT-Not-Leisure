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

@SideOnly(Side.CLIENT)
public class TopologyTopBarWidget extends Widget<TopologyTopBarWidget> implements Interactable {

    public static final int HEIGHT = 26;
    private static final int PADDING = 2;

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
        int buttonWidth = buttonWidth();
        boolean hovered = isOverButton();
        draw.begin();
        draw.roundedRect(
            width - buttonWidth,
            PADDING,
            width,
            HEIGHT - PADDING,
            (HEIGHT - PADDING * 2) * 0.5F,
            TopologyPalette.withAlpha(hovered ? TopologyPalette.PANEL_SELECTED : TopologyPalette.PANEL_RAISED, 0.92F));
        draw.roundedOutline(
            width - buttonWidth,
            PADDING,
            width,
            HEIGHT - PADDING,
            (HEIGHT - PADDING * 2) * 0.5F,
            1,
            TopologyPalette.withAlpha(hovered ? TopologyPalette.ACCENT_DEEP : TopologyPalette.STROKE, 1.0F));
        draw.end();

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        try {
            String title = TopologyViewState.translate("title");
            font.drawString(title, 0, (HEIGHT - font.FONT_HEIGHT) / 2, TopologyPalette.opaque(TopologyPalette.TEXT));
            String label = TopologyViewState.translate("rescan");
            font.drawString(
                label,
                width - buttonWidth + (buttonWidth - font.getStringWidth(label)) / 2,
                (HEIGHT - font.FONT_HEIGHT) / 2,
                TopologyPalette.opaque(hovered ? TopologyPalette.TEXT : TopologyPalette.TEXT_MUTED));
            // Counts first, then the shortcut hint, and only while each still fits beside the title.
            int cursor = width - buttonWidth - 10;
            String counts = TopologyViewState.translate(
                "counts",
                state.filteredNetworks()
                    .size(),
                state.snapshot()
                    .networks()
                    .size(),
                state.filteredBridges()
                    .size());
            int titleEnd = font.getStringWidth(title) + 16;
            if (cursor - font.getStringWidth(counts) > titleEnd) {
                cursor -= font.getStringWidth(counts);
                font.drawString(
                    counts,
                    cursor,
                    (HEIGHT - font.FONT_HEIGHT) / 2,
                    TopologyPalette.opaque(TopologyPalette.TEXT_DIM));
                cursor -= 14;
            }
            String shortcuts = TopologyViewState.translate("shortcuts");
            if (cursor - font.getStringWidth(shortcuts) > titleEnd) font.drawString(
                shortcuts,
                cursor - font.getStringWidth(shortcuts),
                (HEIGHT - font.FONT_HEIGHT) / 2,
                TopologyPalette.withAlpha(TopologyPalette.TEXT_DIM, 0.65F));
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

    public int buttonWidth() {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(TopologyViewState.translate("rescan")) + 24;
    }

    private boolean isOverButton() {
        if (!isBelowMouse()) return false;
        int mouseX = getContext().getAbsMouseX() - getArea().x;
        return mouseX >= getArea().width - buttonWidth();
    }
}
