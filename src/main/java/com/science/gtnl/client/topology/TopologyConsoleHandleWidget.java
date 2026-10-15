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
public class TopologyConsoleHandleWidget extends Widget<TopologyConsoleHandleWidget> implements Interactable {

    public static final int WIDTH = 9;
    private static final int MIN_HEIGHT = 22;
    private static final int MAX_HEIGHT = 40;

    private final TopologyViewState state;

    public static int grabHeight(int screenHeight) {
        return Math.max(MIN_HEIGHT, Math.min(MAX_HEIGHT, screenHeight / 6));
    }

    public TopologyConsoleHandleWidget(TopologyViewState state) {
        this.state = state;
        transform((widget, stack) -> stack.translate(-state.consoleVisibleWidth(), 0));
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        boolean hovered = isBelowMouse();
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        String glyph = state.consoleOpen() ? ">" : "<";
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        try {
            font.drawString(
                glyph,
                (WIDTH - font.getStringWidth(glyph)) / 2,
                (getArea().height - font.FONT_HEIGHT) / 2,
                TopologyPalette.opaque(hovered ? TopologyPalette.TEXT : TopologyPalette.TEXT_MUTED));
        } finally {
            GL11.glPopAttrib();
        }
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        state.toggleConsole();
        Interactable.playButtonClickSound();
        return Result.SUCCESS;
    }
}
